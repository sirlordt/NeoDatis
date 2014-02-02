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
package org.neodatis.odb.test.server;

import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.ee.startup.MyDatabaseStartupManager;

public class TestServer {
	public static void main(String[] args) throws Exception {
		MyDatabaseStartupManager manager = new MyDatabaseStartupManager();
		OdbConfiguration.registerDatabaseStartupManager(manager);
		// LogUtil.allOn(true);
		// LogUtil.enable(ConnectionManager.LOG_ID);
		// LogUtil.enable(Transaction.LOG_ID);
		// LogUtil.enable(SessionManager.LOG_ID);
		// OdbConfiguration.setLogServerConnections(true);
		OdbConfiguration.setReconnectObjectsToSession(false);
		ODBServer server = ODBFactory.openServer(ODBTest.PORT);
		// server.setAutomaticallyCreateDatabase(true);
		// LogUtil.allOn(true);
		//OdbConfiguration.setLogServerStartupAndShutdown(false);
		server.startServer(true);

	}

}
