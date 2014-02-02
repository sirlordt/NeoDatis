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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.connection.ConnectionManager;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbThread;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class SessionManager implements ISessionManager {

	public static final String LOG_ID = "SessionManager";

	protected Map<String,ISession> sessions;

	public SessionManager() {
		sessions = new OdbHashMap<String, ISession>();
	}

	public void init2() {
		// TODO Nothing to do 	
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.ISessionManager#getSession(java.lang.String, boolean)
	 */
	public ISession getSession(String baseIdentification, boolean throwExceptionIfDoesNotExist) {
		String threadName = OdbThread.getCurrentThreadName();
		StringBuffer id = new StringBuffer(threadName).append(baseIdentification);
		ISession session = sessions.get(id.toString());
		if (session == null && throwExceptionIfDoesNotExist) {
			throw new ODBRuntimeException(NeoDatisError.SESSION_DOES_NOT_EXIST_FOR_CONNECTION.addParameter(threadName).addParameter(baseIdentification).addParameter(id));
		}
		if(OdbConfiguration.isDebugEnabled(LOG_ID)){
			DLogger.debug("Getting session for base " + baseIdentification + " and thread " + threadName+" = "+id + " - sid="+session.getId());
		}
		return session;

	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.ISessionManager#addSession(org.neodatis.odb.core.transaction.ISession)
	 */
	public void addSession(ISession session) {
		String id = OdbThread.getCurrentThreadName()+session.getBaseIdentification();
		sessions.put(id, session);
		if(OdbConfiguration.isDebugEnabled(LOG_ID)){
			DLogger.debug("Associating id = " + id + " to session "+ session.getId());	
		}
		//DLogger.info(StringUtils.exceptionToString(new Exception()));
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		Iterator<String> iterator = sessions.keySet().iterator();
		String sid = null;
		ISession session = null;
		while (iterator.hasNext()) {
			sid = iterator.next();
			session = sessions.get(sid);
			buffer.append(sid).append(":").append(session.toString()).append("\n");
		}

		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.ISessionManager#removeSession(java.lang.String)
	 */
	public void removeSession(String baseIdentification) {
		String id = OdbThread.getCurrentThreadName()+baseIdentification;
		sessions.remove(id);
		//ISession session = sessions.remove(id);
		//session.close();
		//session = null;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.server.ISessionManager#getSessionDescriptions(java.util.Map)
	 */
	public List<String> getSessionDescriptions(Map connectionManagers) {
		List<String> l = new ArrayList<String>();

		Iterator<String> iterator = sessions.keySet().iterator();
		String sid = null;
		ISession session = null;
		ConnectionManager cm = null;
		StringBuffer buffer = null;
		
		while (iterator.hasNext()) {
			sid = iterator.next();
			session = sessions.get(sid);
			cm = (ConnectionManager) connectionManagers.get(session.getBaseIdentification());
			buffer = new StringBuffer("Session "+sid+" : "+session.toString());
			if(cm!=null){
				buffer.append( " - Number of connections=" + cm.getNbConnections());
				
				buffer.append(cm.getConnectionDescriptions());
			}
			l.add(buffer.toString());
		}
		return l;
	}

	public long getNumberOfSessions(){
		return sessions.size();
	}

}
