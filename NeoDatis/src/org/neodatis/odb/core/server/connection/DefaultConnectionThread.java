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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.layers.layer3.IODBServerExt;
import org.neodatis.odb.core.server.layers.layer3.engine.IMessageStreamer;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbRunnable;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.OdbThread;

/**
 * A thread to manage client connections via socket
 * 
 * @sharpen.ignore
 * @author olivier s
 * 
 */
public class DefaultConnectionThread extends ClientServerConnection implements OdbRunnable {
	private static final String LOG_ID = "DefaultConnectionThread";

	private Socket socketConnection;
	private String name;
	private IMessageStreamer messageStreamer;

	public DefaultConnectionThread(IODBServerExt server, Socket connection, boolean automaticallyCreateDatabase) {
		super(server, automaticallyCreateDatabase);
		this.socketConnection = connection;
	}

	public void run() {
		OutputStream out = null;
		InputStream in = null;
		String messageType = null;
		try {
			// socketConnection.setKeepAlive(true);
			// socketConnection.setSoTimeout(0);
			socketConnection.setTcpNoDelay(true);
			connectionIsUp = true;
			out = socketConnection.getOutputStream();
			in = socketConnection.getInputStream();
			messageStreamer = OdbConfiguration.getCoreProvider().getMessageStreamer((socketConnection));
			// in,out,ois,oos);
			Message message = null;
			Message rmessage = null;
			do {
				message = null;
				message = messageStreamer.read();

				if (message != null) {
					messageType = message.getClass().getName();
					rmessage = manageMessage(message);
					messageStreamer.write(rmessage);
				} else {
					messageType = "Null Message";
				}
				// To force disconnection
				// connectionIsUp = false;
			} while (connectionIsUp && message != null);
		} catch (EOFException eoe) {
			DLogger.error("Warning : Thread " + OdbThread.getCurrentThreadName() + ", baseId=" + baseIdentifier
					+ " , cid=" + connectionId + ": Client has terminated the connection without closing it");
			connectionIsUp = false;
		} catch (Throwable e) {
			String m = OdbString.exceptionToString(e, false);
			DLogger.error("Thread " + OdbThread.getCurrentThreadName() + ": Error in connection thread baseId=" + baseIdentifier
					+ " and cid=" + connectionId + " for message of type " + messageType + " : \n" + m);
			connectionIsUp = false;
			throw new ODBRuntimeException(NeoDatisError.NET_SERIALISATION_ERROR.addParameter(e.getMessage()).addParameter(m));
		} finally {
			try {
				messageStreamer.close();
				socketConnection.close();
			} catch (IOException e) {
				DLogger.error("Error while closing socket - connection thread baseId=" + baseIdentifier + " and cid=" + connectionId
						+ ": \n" + OdbString.exceptionToString(e, false));
			}
			if (debug) {
				DLogger.info("Exiting thread " + OdbThread.getCurrentThreadName());
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void clearMessageStreamerCache(){
		messageStreamer.clearCache();
	}
}
