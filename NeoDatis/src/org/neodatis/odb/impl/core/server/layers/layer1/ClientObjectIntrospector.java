/*
NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

"This file is part of the NeoDatis ODB open source object database".

NeoDatis ODB is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

NeoDatis ODB is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package org.neodatis.odb.impl.core.server.layers.layer1;

import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IIntrospectionCallback;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.core.server.layers.layer1.IClientObjectIntrospector;
import org.neodatis.odb.core.server.layers.layer2.meta.ClientNonNativeObjectInfo;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ICrossSessionCache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer1.introspector.LocalObjectIntrospector;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * Not thread safe
 * 
 * @author osmadja
 * 
 */
public class ClientObjectIntrospector extends LocalObjectIntrospector implements IClientObjectIntrospector {

	/** client oids are sequential ids created by the client side engine. When an object is sent to server, server ids are sent back from server
	 * and client engine replace all local(client) oids by the server oids. */
	protected IOdbList<OID> clientOids;

	/** A map of abstract object info, keys are local ids */
	protected Map<OID, ClientNonNativeObjectInfo> aois;

	protected Map<OID,Object> objects;
	
	protected ISession session;
	
	/** This represents the connection to the server*/ 
	protected String connectionId;


	public ClientObjectIntrospector(IStorageEngine storageEngine,String connectionId) {
		super(storageEngine);
		clientOids = new OdbArrayList<OID>();
		aois = new OdbHashMap<OID, ClientNonNativeObjectInfo>();
		objects = new OdbHashMap<OID, Object>();
		session = storageEngine.getSession(true);
		this.connectionId = connectionId;
	}

	public ISession getSession(){
		return session;
	}
	public NonNativeObjectInfo buildNnoi(Object o, ClassInfo info, AbstractObjectInfo[] values, long[] attributesIdentification,
			int[] attributeIds, Map<Object,NonNativeObjectInfo> alreadyReadObjects) {

		ClientNonNativeObjectInfo cnnoi = new ClientNonNativeObjectInfo(null, info, values, attributesIdentification, attributeIds);
		cnnoi.setLocalOid(OIDFactory.buildObjectOID(alreadyReadObjects.size() + 1));
		OID id = cnnoi.getLocalOid();

		ICache cache = getSession().getCache();
		// Check if object is in the cache, if so sets its id
		OID oid = cache.getOid(o, false);
		if (oid != null) {
			cnnoi.setOid(oid);
		}

		clientOids.add(id);
		aois.put(id, cnnoi);
		objects.put(id, o);
		return cnnoi;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.IClientObjectIntrospector#getLocalOids()
	 */
	public IOdbList<OID> getClientOids() {
		return clientOids;
	}

	public AbstractObjectInfo getMetaRepresentation(Object object, ClassInfo ci, boolean recursive, Map<Object,NonNativeObjectInfo> alreadyReadObjects, IIntrospectionCallback callback) {
		clientOids.clear();
		aois.clear();
		objects.clear();
		return super.getObjectInfo(object, ci, recursive, alreadyReadObjects, callback);
	}

	/**
	 * This method is used to make sure that client oids and server oids are equal.
	 * 
	 * <pre>
	 * When storing an object, the client side does not know the oid that each object will receive. So the client create
	 * temporary (sequential) oids. These oids are sent to the server in the object meta-representations. On the server side,
	 * real OIDs are created and associated to the objects and to the client side ids. After calling the store on the server side
	 * The client use the the synchronizeIds method to replace client ids by the correct server side ids. 
	 * 
	 * </pre>
	 */
	public void synchronizeIds(OID[] clientIds, OID[] serverIds) {
		if (clientIds.length != clientOids.size()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_SYNCHRONIZE_IDS.addParameter(clientOids.size()).addParameter(clientIds.length));
		}
		ClientNonNativeObjectInfo cnnoi = null;
		ICache cache = getSession().getCache();
		Object object = null;
		OID id = null;
		ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(storageEngine.getBaseIdentification().getIdentification());

		for (int i = 0; i < clientIds.length; i++) {
			id = clientIds[i];
			cnnoi = aois.get(id);
			object = objects.get(id);
			
			// Server ids may be null when an object or part of an object has been updated.
			// In these case local objects have already the correct ids
			if(serverIds[i]!=null){
				cnnoi.setOid(serverIds[i]);
				cache.addObject(serverIds[i], object, cnnoi.getHeader());
			}
			// As serverIds may be null, we need to check it
			if(OdbConfiguration.reconnectObjectsToSession() && serverIds[i]!=null){
				crossSessionCache.addObject(object, serverIds[i]);
			}
		}
	}
}
