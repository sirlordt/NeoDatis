
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
package org.neodatis.odb.impl.core.server.connection;

import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.server.connection.ConnectionAction;
import org.neodatis.odb.core.server.connection.ConnectionManager;
import org.neodatis.odb.core.server.connection.IConnection;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class DefaultServerConnection implements IConnection {
	
	
	public static final String LOG_ID = "Connection";
	
	private String id;
	private ConnectionManager connectionManager;
	private IStorageEngine storageEngine;
	private String baseIdentifier;
	/** To keep locked id for this session : key = oid, value=timestamp (Long)*/
	private Map<OID,Long> oidsLockedForUpdate;
	private Map<String,Long> lockedClasses;
	
	/** Current action being executed*/
	private String currentAction;
	private long currentActionStart;
	
	private long lastActionDuration;
	private String lastAction;
	
	private int [] actions;
	
	public DefaultServerConnection(ConnectionManager connectionManager, String connectionId, IStorageEngine storageEngine) {
		this.connectionManager = connectionManager;
		this.id = connectionId;
		this.storageEngine = storageEngine;
		this.baseIdentifier = storageEngine.getBaseIdentification().getIdentification();
		this.oidsLockedForUpdate = new OdbHashMap<OID, Long>();
		this.lockedClasses = new OdbHashMap<String, Long>();
		
		actions = new int[ConnectionAction.getNumberOfActions()];
	}


	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#getId()
	 */
	public String getId() {
		return id;
	}


	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#getStorageEngine()
	 */
	public IStorageEngine getStorageEngine() {
		return storageEngine;
	}


	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#close()
	 */
	public void close() throws Exception {
		commit();
	}
	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#commit()
	 */
	public void commit() throws Exception {
		storageEngine.commit();
		releaseOidLocks();
		releaseClassLocks();
	}
	
	protected void releaseOidLocks() throws InterruptedException{
		OID oid = null;
		// release update mutexes
		Iterator iterator = oidsLockedForUpdate.keySet().iterator();
		while(iterator.hasNext()){
			oid = (OID) iterator.next();
			connectionManager.unlockOidForConnection(oid, this);
			if(OdbConfiguration.isDebugEnabled(LOG_ID)){
				DLogger.debug("Release object lock for "+oid);
			}
		}
		oidsLockedForUpdate.clear();
	}
	
	protected void releaseClassLocks() throws InterruptedException{
		Iterator<String> iterator = lockedClasses.keySet().iterator();
		while(iterator.hasNext()){
			String fullClassName = iterator.next();
			connectionManager.unlockClass(fullClassName, this);
			if(OdbConfiguration.isDebugEnabled(LOG_ID)){
				DLogger.debug("Release class lock for "+fullClassName);
			}
		}
		lockedClasses.clear();
		
	}

	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#unlockObjectWithOid(org.neodatis.odb.OID)
	 */
	public synchronized void unlockObjectWithOid(OID oid) throws Exception {
		connectionManager.unlockOidForConnection(oid, this);
		oidsLockedForUpdate.remove(oid);		
	}
	public synchronized void unlockClass(String fullClassName) throws Exception {
		connectionManager.unlockClass(fullClassName, this);
		lockedClasses.remove(fullClassName);		
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#rollback()
	 */
	public void rollback() throws Exception {
		storageEngine.rollback();
		releaseOidLocks();
		releaseClassLocks();
	}


	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#lockObjectWithOid(org.neodatis.odb.OID)
	 */
	public synchronized boolean lockObjectWithOid(OID oid) throws InterruptedException {
		connectionManager.lockOidForConnection(oid, this);
		oidsLockedForUpdate.put(oid, new Long(OdbTime.getCurrentTimeInMs()));
		return true;
	}
	
	public boolean lockClass(String fullClassName) throws InterruptedException {
		connectionManager.lockClassForConnection(fullClassName, this);
		lockedClasses.put(fullClassName, new Long(OdbTime.getCurrentTimeInMs()));
		return true;
	}

	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#setCurrentAction(int)
	 */
	public void setCurrentAction(int action){
		currentActionStart = OdbTime.getCurrentTimeInMs();
		currentAction = ConnectionAction.getActionLabel(action);
		actions[action]=actions[action]+1;
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#endCurrentAction()
	 */
	public void endCurrentAction(){
		lastAction = currentAction;
		lastActionDuration = (OdbTime.getCurrentTimeInMs()-currentActionStart);
		currentAction = ConnectionAction.ACTION_NO_ACTION_LABEL;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.connection.IConnection#getDescription()
	 */
	public String getDescription() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("cid="+id).append("\n\t\t+ Current action : ");
		buffer.append(currentAction).append("(").append(OdbTime.getCurrentTimeInMs()-currentActionStart).append("ms) | last action : ");
		buffer.append(lastAction).append("(").append(lastActionDuration).append("ms)");
		buffer.append("\n\t\t+ Actions : ");
		for(int i=0;i<actions.length;i++){
			buffer.append(ConnectionAction.getActionLabel(i)).append("=").append(actions[i]).append(" | ");
		}
		buffer.append("\n\t\t+ Blocked Oid (").append(oidsLockedForUpdate.size()).append(") : ");
		buffer.append(oidsLockedForUpdate.keySet());
		return buffer.toString();
	}
	
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof DefaultServerConnection)){
			return false;
		}
		DefaultServerConnection c = (DefaultServerConnection) obj;
		
		return id.equals(c.id);
	}


}
