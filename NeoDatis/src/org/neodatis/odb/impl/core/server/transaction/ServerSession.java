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
package org.neodatis.odb.impl.core.server.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.server.layers.layer2.meta.ClientNonNativeObjectInfo;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.impl.core.server.ReturnValue;
import org.neodatis.odb.impl.core.server.trigger.ChangedValueNotification;
import org.neodatis.odb.impl.core.transaction.LocalSession;
import org.neodatis.odb.impl.core.transaction.ServerCache;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class ServerSession extends LocalSession {
	/** client object ids */
	protected OID[] clientIds;
	/**
	 * server object ids. The server ids are sent to client as a result of a
	 * store operation to enable client to synchronize ids with server
	 */
	protected OID[] serverIds;

	/**
	 * To keep track of class info creation on server. The ids of class info are
	 * then sent to client to update their ci ids
	 * 
	 */
	protected Map<String,OID> classInfoIds;
	protected List<ReturnValue> valuesToReturn;
	protected ISessionManager sessionManager;

	public ServerSession(IStorageEngine engine, String sessionId ) {
		super(engine, sessionId);
		classInfoIds = new OdbHashMap<String, OID>();
		this.sessionManager = OdbConfiguration.getCoreProvider().getClientServerSessionManager();
		this.valuesToReturn = new ArrayList<ReturnValue>();
	}

	public OID[] getClientIds() {
		return clientIds;
	}

	public void setClientIds(OID[] clientIds) {
		this.clientIds = clientIds;
		this.serverIds = new OID[clientIds.length];
	}

	public OID[] getServerIds() {
		return serverIds;
	}

	public void setServerIds(OID[] serverIds) {
		this.serverIds = serverIds;
	}

	public void associateIds(ClientNonNativeObjectInfo cnnoi, OID serverId, OID clientOid) {
		boolean associated = false;
		for (int i = 0; i < clientIds.length && !associated; i++) {
			if (clientOid.compareTo(clientIds[i]) == 0) {
				serverIds[i] = serverId;
				associated = true;
			}
		}
		// return values may have been created before the nnoi have an oid. So the we just inform the return values, the association of the nnoi and the oid
		// what each return value will do with that info is responsability of the return value
		if(valuesToReturn!=null && valuesToReturn.size()>0){
			for(ReturnValue rv:valuesToReturn){
				rv.setOid(cnnoi, serverId);
			}
		}
		if(!associated){
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_CAN_NOT_ASSOCIATE_OIDS.addParameter(serverId).addParameter(clientOid));
		}
	}

	public ICache buildCache() {
		return new ServerCache(this);
	}

	public MetaModel getMetaModel() {
		if (metaModel == null) {
			try {
				metaModel = new SessionMetaModel();
				if (getStorageEngine().getObjectReader() != null) {
					this.metaModel = getStorageEngine().getObjectReader().readMetaModel(metaModel, true);
				}
			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("in ServerSession.getMetaModel"), e);
			}
		}
		return metaModel;
	}

	public void setClassInfoId(String fullClassName, OID id) {
		classInfoIds.put(fullClassName, id);
	}

	public Map<String,OID> getClassInfoIds() {
		return classInfoIds;
	}

	public void resetClassInfoIds() {
		classInfoIds = new OdbHashMap<String, OID>();
	}

	public void clear() {
		super.clear();
		//sessionManager.removeSession(baseIdentification);
	}
	public void finalize(){
	}
	
	public void update(Observable o, Object value) {
		if(value!=null){
			storeReturnValue((ReturnValue)value);
		}
	}

	/**
	 * @param arg
	 */
	private void storeReturnValue(ReturnValue returnValue) {
		valuesToReturn.add(returnValue);
	}

	public List<ReturnValue> getValuesToReturn() {
		return valuesToReturn;
	}

	public void clearValuesToReturn(){
		valuesToReturn = new ArrayList<ReturnValue>();
	}
}
