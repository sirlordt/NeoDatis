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

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.layers.layer3.IODBServerExt;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.odb.core.server.message.ConnectMessage;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.server.transaction.ServerSession;
import org.neodatis.tool.mutex.Mutex;
import org.neodatis.tool.mutex.MutexFactory;

/**
 * A class to manage client server connections being executed in the same Vm. In
 * this case, we don't use network IO.
 * 
 * @author olivier s
 * 
 */
public class SameVmConnection extends ClientServerConnection {
	private static final String LOG_ID = "SameVmConnectionThread";
	private ISession originalSession;

	public SameVmConnection(String baseIdentifier, IODBServerExt server, boolean automaticallyCreateDatabase) {
		super(server, automaticallyCreateDatabase);
	}

	public String getName() {
		return "Same vm client ";
	}

	public ServerSession getSession(String baseIdentifier) {
		return (ServerSession) sessionManager.getSession(baseIdentifier, true);
	}

	public Message manageMessage(Message message) {

		boolean isConnectMessage = message instanceof ConnectMessage;

		if (!isConnectMessage) {
			// check session
			ISession session = sessionManager.getSession(message.getBaseIdentifier(), false);
			if (session == null && OdbConfiguration.shareSameVmConnectionMultiThread()) {
				// it seems the connection has been created by another
				// thread.
				// as odbConfiguration.shareSameVmConnectionMultiThread is
				// true, we associate
				// automatically the current thread to the connection
				sessionManager.addSession(originalSession);
			}
		}

		Message response = super.manageMessage(message);

		if (isConnectMessage) {
			// Keep original session
			this.originalSession = sessionManager.getSession(message.getBaseIdentifier(), true);
		}
		return response;
	}

	public void clearMessageStreamerCache() {
		// nothing to do
	}
}
