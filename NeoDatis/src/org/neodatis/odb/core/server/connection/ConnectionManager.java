
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
package org.neodatis.odb.core.server.connection;

import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.server.connection.DefaultServerConnection;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbThread;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class ConnectionManager {
	public static final String LOG_ID = "IConnectionManager";
	private IStorageEngine storageEngine;
	private Map<String,IConnection> connections ;
	// A map that contains oids that are locked. The key is the oid, the value is the connection that hold the object
	private Map<OID,IConnection> lockedOids;
	private Map<String,IConnection> lockedClasses;
	
	public ConnectionManager(IStorageEngine engine) {
		this.storageEngine = engine;
		connections = new OdbHashMap<String, IConnection>();
		lockedOids = new OdbHashMap<OID, IConnection>();
		lockedClasses = new OdbHashMap<String, IConnection>();
	}

	public IConnection newConnection(String ip, long dateTime, int sequence){
		String connectionId = ConnectionIdGenerator.newId(ip, dateTime, sequence);
		IConnection connection = new DefaultServerConnection(this,connectionId,storageEngine);
		connections.put(connectionId, connection);
		return connection;
	}
	
	public IConnection getConnection(String connectionId){
		IConnection c = (IConnection) connections.get(connectionId);
		
		if(c==null){
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_CONNECTION_IS_NULL.addParameter(connectionId).addParameter(connections));
		}
		
		return c;
	}
	public void removeConnection(IConnection connection){
		connections.remove(connection.getId());
	}

	public IStorageEngine getStorageEngine() {
		return storageEngine;
	}
	public int getNbConnections(){
		return connections.size();
	}
	
	public String getConnectionDescriptions(){
		Iterator iterator = connections.values().iterator();
		IConnection connection = null;
		StringBuffer buffer = new StringBuffer();
		while(iterator.hasNext()){
			connection = (IConnection) iterator.next();
			buffer.append("\n\t+ ").append(connection.getDescription()).append("\n");
		}
		return buffer.toString();
	}
	
	public synchronized void lockOidForConnection(OID oid, IConnection connection) throws InterruptedException{
		long start = OdbTime.getCurrentTimeInMs();
		
		if(OdbConfiguration.isDebugEnabled(LOG_ID)){
			start = OdbTime.getCurrentTimeInMs();
			DLogger.debug("Trying to lock object with oid "+oid+" - id="+connection.getId() + " - Thread " + OdbThread.getCurrentThreadName());
		}
		try{
			IConnection c = lockedOids.get(oid);
			if(c==null){
				lockedOids.put(oid, connection);
				return;
			}
			// If oid is locked for by the passed connection, no problem, it is not considered as being locked
			if(c!=null&&c.equals(connection)){
				return;
			}
			while(c!=null){
				OdbThread.sleep(10);
				c = lockedOids.get(oid);
			}
			lockedOids.put(oid, connection);
		}finally{
			if(OdbConfiguration.isDebugEnabled(LOG_ID)){
				DLogger.debug("Object with oid "+oid+" locked ("+(OdbTime.getCurrentTimeInMs()-start)+"ms) - "+connection.getId() + " - Thread " + OdbThread.getCurrentThreadName());
			}
		}
	}
	
	public synchronized void lockClassForConnection(String fullClassName, IConnection connection) throws InterruptedException{
		long start = OdbTime.getCurrentTimeInMs();
		
		if(OdbConfiguration.isDebugEnabled(LOG_ID)){
			start = OdbTime.getCurrentTimeInMs();
			DLogger.debug(String.format("CM:Trying to lock class %s - id=%s",fullClassName,connection.getId()));
		}
		try{
			IConnection c = lockedClasses.get(fullClassName);
			if(c==null){
				lockedClasses.put(fullClassName, connection);
				return;
			}
			// If oid is locked for by the passed connection, no problem, it is not considered as being locked
			if(c!=null&&c.equals(connection)){
				return;
			}
			while(c!=null){
				OdbThread.sleep(10);
				c = lockedClasses.get(fullClassName);
			}
			lockedClasses.put(fullClassName, connection);
		}finally{
			if(OdbConfiguration.isDebugEnabled(LOG_ID)){
				DLogger.debug(String.format("Class %s locked (%dms) - %s",fullClassName,(OdbTime.getCurrentTimeInMs()-start),connection.getId()));
			}
		}
	}
	public void unlockOidForConnection(OID oid, IConnection connection) throws InterruptedException{
		long start = OdbTime.getCurrentTimeInMs();
		
		if(OdbConfiguration.isDebugEnabled(LOG_ID)){
			start = OdbTime.getCurrentTimeInMs();
			DLogger.debug("Trying to unlock lock object with oid "+oid+" - id="+connection.getId());
		}

		try{
			lockedOids.remove(oid);
		}finally{
			if(OdbConfiguration.isDebugEnabled(LOG_ID)){
				DLogger.debug("Object with oid "+oid+" unlocked ("+(OdbTime.getCurrentTimeInMs()-start)+"ms) - "+connection.getId());
			}
		}
	}

	public void unlockClass(String fullClassName, IConnection connection) throws InterruptedException{
		long start = OdbTime.getCurrentTimeInMs();
		
		if(OdbConfiguration.isDebugEnabled(LOG_ID)){
			start = OdbTime.getCurrentTimeInMs();
			DLogger.debug("Trying to unlock class "+fullClassName+" - id="+connection.getId());
		}

		try{
			lockedClasses.remove(fullClassName);
		}finally{
			if(OdbConfiguration.isDebugEnabled(LOG_ID)){
				DLogger.debug("Class  "+fullClassName+" unlocked ("+(OdbTime.getCurrentTimeInMs()-start)+"ms) - "+connection.getId());
			}
		}
	}

	/*
	public synchronized boolean oidIsLockedFor(OID oid, IConnection connection){
		IConnection c = (IConnection) lockedOids.get(oid);
		if(c==null){
			return false;
		}
		// If oid is locked for by the passed connection, no problem, it is not considered as being locked
		if(c!=null&&c.equals(connection)){
			return false;
		}
		return true;
	}
	*/
}
