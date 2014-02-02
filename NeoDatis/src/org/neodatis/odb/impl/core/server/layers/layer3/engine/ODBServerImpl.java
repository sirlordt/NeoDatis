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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.Release;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.server.connection.ClientServerConnection;
import org.neodatis.odb.core.server.connection.ConnectionManager;
import org.neodatis.odb.core.server.connection.DefaultConnectionThread;
import org.neodatis.odb.core.server.layers.layer3.IODBServerExt;
import org.neodatis.odb.core.server.layers.layer3.ServerFileParameter;
import org.neodatis.odb.core.server.layers.layer3.engine.IServerStorageEngine;
import org.neodatis.odb.core.server.trigger.ServerDeleteTrigger;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;
import org.neodatis.odb.core.server.trigger.ServerSelectTrigger;
import org.neodatis.odb.core.server.trigger.ServerUpdateTrigger;
import org.neodatis.odb.core.trigger.OIDTrigger;
import org.neodatis.odb.impl.main.SameVMODBClient;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbRunnable;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.OdbThread;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * The ODB implementation for Server mode
 * 
 * @author osmadja
 * @sharpen.ignore
 * @port.todo
 * 
 */
public class ODBServerImpl implements OdbRunnable,  IODBServerExt {
	public static final String LOG_ID = "ODBServer";
	
	private int port;
	private OdbThread thread;
	private boolean serverIsUp;

	private ServerSocket socketServer;
	private boolean isRunning;
	private Map<String,IServerStorageEngine> bases;
	private Map<String,ConnectionManager> connectionManagers;
	private boolean automaticallyCreateDatabase;
	
	/** To keep track when the server started*/
	private long start;

	public ODBServerImpl(int port) {
		OdbConfiguration.setCheckModelCompatibility(true);
		this.port = port;
		this.automaticallyCreateDatabase = true;
		initServer();
	}

	private void initServer() {
		this.bases = new OdbHashMap<String, IServerStorageEngine>();
		this.connectionManagers = new OdbHashMap<String, ConnectionManager>();

		try {
			socketServer = new ServerSocket(port);
			isRunning = true;
		} catch (BindException e1) {
			isRunning = false;
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_PORT_IS_BUSY.addParameter(port), e1);
		} catch (IOException e2) {
			isRunning = false;
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_CAN_NOT_OPEN_ODB_SERVER_ON_PORT.addParameter(port), e2);
		}

	}

	public void addBase(String baseIdentifier, String fileName) {
		addBase(baseIdentifier, fileName, null, null);
	}

	public void addBase(String baseIdentifier, String fileName, String user, String password) {
		ServerFileParameter fileParameter = new ServerFileParameter(baseIdentifier, fileName, true,user,password);
		IServerStorageEngine engine = null;
		engine = OdbConfiguration.getCoreProvider().getServerStorageEngine(fileParameter);
		engine.commit();
		bases.put(baseIdentifier, engine);
		connectionManagers.put(baseIdentifier, new ConnectionManager(engine));
		
		if (OdbConfiguration.isInfoEnabled(LOG_ID)) {
			DLogger.info("ODBServer:Adding base : name=" + baseIdentifier + " (file=" + fileName + ") to server");
		}
	}

	public void addUserForBase(String baseIdentifier, String user, String password) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED);
	}

	public void startServer(boolean inThread) {
		if (inThread) {
			thread = new OdbThread(this);
			thread.start();
		} else {
			run();
		}
	}

	public void run() {

		try {
			startServer();
		} catch (IOException e) {
			DLogger.error(OdbString.exceptionToString(e, true));
		}
	}

	public void startServer() throws IOException {
		start = OdbTime.getCurrentTimeInMs();
		if (OdbConfiguration.logServerStartupAndShutdown()) {
			DLogger.info("NeoDatis ODB Server [version="+ Release.RELEASE_NUMBER + " - build="+Release.RELEASE_BUILD + "-" + Release.RELEASE_DATE+"] running on port "+port);
			if(bases.size()!=0){
				DLogger.info("Managed bases: " + bases.keySet());
			}
		}

		while (isRunning) {
			try {
				waitForRemoteConnection();
			} catch (SocketException e) {
				if (isRunning) {
					DLogger.error("ODBServer:ODBServerImpl.startServer:" + OdbString.exceptionToString(e, true));
				}
			}
		}
	}

	public ClientServerConnection waitForRemoteConnection() throws IOException {
		Socket connection = socketServer.accept();
		connection.setTcpNoDelay(true);
		DefaultConnectionThread connectionThread = new DefaultConnectionThread(this, connection, automaticallyCreateDatabase);
		OdbThread thread = new OdbThread(connectionThread);
		connectionThread.setName(thread.getName());
		thread.start();
		return connectionThread;
	}

	public void close() {
		if (OdbConfiguration.logServerStartupAndShutdown()) {
			long end = OdbTime.getCurrentTimeInMs();
			double timeInHour = ((double)(end-start))/1000/60/60; 
			DLogger.info(String.format("NeoDatis ODB Server (port %d) shutdown [uptime=%dHours]",port, (long) timeInHour));
		}
		try{
			isRunning = false;
			socketServer.close();
			Iterator iterator = bases.keySet().iterator();
			String baseIdentifier = null;
			IStorageEngine engine = null;

			while (iterator.hasNext()) {
				baseIdentifier = (String) iterator.next();
				engine = (IStorageEngine) bases.get(baseIdentifier);
				if (OdbConfiguration.isInfoEnabled(LOG_ID)) {
					DLogger.info("Closing Base " + baseIdentifier);
				}
				if(engine!=null && !engine.isClosed()){
					engine.close();
				}
			}
			if (thread != null) {
				thread.interrupt();

			}
		}catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_ERROR.addParameter("While closing server"), e);
		}
	}

	public void setAutomaticallyCreateDatabase(boolean yes) {
		automaticallyCreateDatabase = yes;
	}

	public ODB openClient(String baseIdentifier) {
		return new SameVMODBClient(this, baseIdentifier);
	}

	public Map getConnectionManagers() {
		return connectionManagers;
	}

	public IOSocketParameter getParameters(String baseIdentifier, boolean clientAndServerRunInSameVM) {
			return new IOSocketParameter("localhost", port, baseIdentifier, IOSocketParameter.TYPE_DATABASE,
					OdbTime.getCurrentTimeInMs(), null, null, clientAndServerRunInSameVM);
	}

	public void addDeleteTrigger(String baseIdentifier, String className, ServerDeleteTrigger trigger) {
		IServerStorageEngine engine = (IServerStorageEngine) bases.get(baseIdentifier);

		if (engine == null) {
			throw new ODBRuntimeException(NeoDatisError.UNREGISTERED_BASE_ON_SERVER.addParameter(baseIdentifier));
		}

		engine.addDeleteTriggerFor(className, trigger);
	}

	public void addInsertTrigger(String baseIdentifier, String className, ServerInsertTrigger trigger) {
		IServerStorageEngine engine = (IServerStorageEngine) bases.get(baseIdentifier);

		if (engine == null) {
			throw new ODBRuntimeException(NeoDatisError.UNREGISTERED_BASE_ON_SERVER.addParameter(baseIdentifier));
		}

		engine.addInsertTriggerFor(className, trigger);

	}

	public void addOidTrigger(String baseIdentifier, String className, OIDTrigger trigger) {
		IServerStorageEngine engine = (IServerStorageEngine) bases.get(baseIdentifier);

		if (engine == null) {
			throw new ODBRuntimeException(NeoDatisError.UNREGISTERED_BASE_ON_SERVER.addParameter(baseIdentifier));
		}

		engine.addOidTriggerFor(className, trigger);

	}

	public void addSelectTrigger(String baseIdentifier, String className, ServerSelectTrigger trigger) {
		IServerStorageEngine engine = (IServerStorageEngine) bases.get(baseIdentifier);

		if (engine == null) {
			throw new ODBRuntimeException(NeoDatisError.UNREGISTERED_BASE_ON_SERVER.addParameter(baseIdentifier));
		}

		engine.addSelectTriggerFor(className, trigger);

	}

	public void addUpdateTrigger(String baseIdentifier, String className, ServerUpdateTrigger trigger) {
		IServerStorageEngine engine = (IServerStorageEngine) bases.get(baseIdentifier);

		if (engine == null) {
			throw new ODBRuntimeException(NeoDatisError.UNREGISTERED_BASE_ON_SERVER.addParameter(baseIdentifier));
		}

		engine.addUpdateTriggerFor(className, trigger);

	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		Set<String> baseIds = connectionManagers.keySet();
		b.append(String.format("%d connection managers", connectionManagers.size()));
		Iterator<String> iterator = baseIds.iterator();
		while(iterator.hasNext()){
			ConnectionManager cm = connectionManagers.get(iterator.next());
			b.append(String.format("\n\t%s : %s sessions",cm.getStorageEngine().getBaseIdentification().getIdentification(),cm.getNbConnections()));
		}
		
		return b.toString();
	}

}
