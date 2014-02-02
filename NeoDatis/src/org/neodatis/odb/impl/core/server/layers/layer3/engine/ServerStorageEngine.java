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
package org.neodatis.odb.impl.core.server.layers.layer3.engine;

import java.io.IOException;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.engine.AbstractStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.core.server.layers.layer3.engine.IServerStorageEngine;
import org.neodatis.odb.core.server.layers.layer3.engine.ServerFileSystemInterface;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.impl.core.query.criteria.CollectionQueryResultAction;
import org.neodatis.odb.impl.core.server.transaction.ServerSession;

public class ServerStorageEngine extends AbstractStorageEngine implements IServerStorageEngine{

	private ISessionManager sessionManager;

	public ServerStorageEngine(IBaseIdentification parameters) {
		super(parameters);
	}

	public IObjectWriter buildObjectWriter()  {
		return provider.getServerObjectWriter(this);
	}

	public IObjectReader buildObjectReader()  {
		return provider.getServerObjectReader(this);
	}
	
	public IObjectIntrospector buildObjectIntrospector() {
    	return provider.getServerObjectIntrospector(this);
	}

	public ITriggerManager buildTriggerManager(){
    	return provider.getServerTriggerManager(this);
    }
	protected IFileSystemInterface buildFSI() throws IOException {
		return new ServerFileSystemInterface("data", baseIdentification, true, OdbConfiguration.getDefaultBufferSizeForData());
	}

	public ISession getSession(boolean throwExceptionIfDoesNotExist) {
		return sessionManager.getSession(baseIdentification.getIdentification(),throwExceptionIfDoesNotExist);
	}

	public ISession buildDefaultSession()  {
		ICoreProvider provider = OdbConfiguration.getCoreProvider();
		if (sessionManager == null) {
			sessionManager = provider.getClientServerSessionManager();
		}
		ISession session = provider.getServerSession(this,"default");
		//FIXME Remove commented line
		//session.setBaseIdentification(((ServerFileParameter) this.getBaseIdentification()).getBaseName());
		return session;
	}

	public void addSession(ISession session, boolean readMetamodel) {
		sessionManager.addSession(session);
		super.addSession(session,readMetamodel);
	}

	protected MetaModel getMetaModel() {
		return getSession(true).getMetaModel();
	}

	
	public void commit() {
		super.commit();
	}

	/**
	 * Write an object meta-representation. TODO Use a mutex to guarantee unique
	 * access to the file at this moment. This should be change
	 * 
	 * @param oid
	 * @param aoi
	 * @param position
	 * @param updatePointers
	 * @return The object position or id if negative
	 * @throws Exception
	 */
	public OID writeObjectInfo(OID oid, NonNativeObjectInfo nnoi, long position, boolean updatePointers) {
		oid = super.writeObjectInfo(oid, nnoi, position, updatePointers);
		return oid;
	}

	
	/**
	 * It is overiden to manage triggers
	 */
	public void deleteObjectWithOid(OID oid) {
		// Check if oih is in the cache
		ObjectInfoHeader oih = getSession(true).getCache().getObjectInfoHeaderFromOid(oid, false);
		if (oih == null) {
			oih = getObjectReader().readObjectInfoHeaderFromOid(oid, true);
		}
		
		// Only necessary to check if there is some trigger
		ClassInfo ci = getMetaModel().getClassInfoFromId(oih.getClassInfoId());
		String className = ci.getFullClassName();
		
		boolean hasTriggers = triggerManager.hasDeleteTriggersFor(className);
		NonNativeObjectInfo nnoi = null;
		if(hasTriggers){
			nnoi = getObjectReader().readNonNativeObjectInfoFromOid(ci, oid, true, false);
			triggerManager.manageInsertTriggerBefore(className,nnoi);
		}
		
		super.deleteObjectWithOid(oid,false);
		
		if(hasTriggers){
			triggerManager.manageInsertTriggerAfter(className, nnoi, oid);
		}
	}

	/**
	 * TODO Use a mutex to guarantee unique access to the file at this moment.
	 * This should be change
	 * 
	 * @param query
	 * @param inMemory
	 * @param startIndex
	 * @param endIndex
	 * @param returnOjects
	 * @return The object info list
	 * @throws Exception
	 */
	public <T>Objects<T> getObjectInfos(IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnOjects) {
		try {
			IObjectReader reader = getObjectReader();
			IMatchingObjectAction queryResultAction = new CollectionQueryResultAction(query,inMemory,this,returnOjects,reader.getInstanceBuilder());
			return reader.getObjectInfos(query, inMemory, startIndex, endIndex, returnOjects,queryResultAction);
		} finally {
		}
	}

	public ClassInfo addClass(ClassInfo newClassInfo, boolean addDependentClasses) throws IOException {

		ClassInfo ci = getObjectWriter().addClass(newClassInfo, addDependentClasses);
		ServerSession lsession = (ServerSession) getSession(true);
		lsession.setClassInfoId(newClassInfo.getFullClassName(), ci.getId());
		return ci;
	}

	public boolean isLocal() {
		return false;
	}

	public ClassInfoList addClasses(ClassInfoList classInfoList) {
		return getObjectWriter().addClasses(classInfoList);
	}

	
}
