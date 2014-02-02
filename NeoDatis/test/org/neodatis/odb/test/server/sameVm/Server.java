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
package org.neodatis.odb.test.server.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ODBServerImpl;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.IOUtil;

public class Server extends ODBTest {
	private final static String NAME = ODBTest.DIRECTORY + "samevm";

	public void test1() throws Exception {
		int port = PORT + 6;
		deleteBase(NAME);
		ODBServer server = openServer(port);
		server.setAutomaticallyCreateDatabase(true);
		server.startServer(true);

		ODB odb = server.openClient(ODBTest.DIRECTORY + NAME);
		// ODB odb = openClient("localhost",PORT,NAME);

		odb.store(new Function("ff11"));
		odb.close();

		odb = server.openClient(ODBTest.DIRECTORY + NAME);
		// odb = openClient("localhost",PORT,NAME);
		Objects objects = odb.getObjects(Function.class);
		assertEquals(1, objects.size());
		odb.close();

		server.close();
	}

	/**
	 * Tests same vm connection and and a remote connection
	 * 
	 * @throws Exception
	 */
	public void testSameCoonectionWithRemoteConnection() throws Exception {
		int port = PORT + 6;
		deleteBase(NAME);
		// Creates a server
		ODBServer server = openServer(port);
		server.setAutomaticallyCreateDatabase(true);
		server.startServer(true);

		ODB odb = server.openClient(ODBTest.DIRECTORY + NAME);

		odb.store(new Function("ff11"));
		odb.close();

		odb = openClient(getHOST(), port, NAME);
		Objects<Function> objects = odb.getObjects(Function.class);
		assertEquals(1, objects.size());
		odb.close();

		server.close();
	}

	/**
	 * 
	 */
	public void tes2tSameCoonectionWithManualRemoteConnection() throws Exception {
		int port = PORT + 6;
		OdbConfiguration.addLogId(ODBServerImpl.LOG_ID);
		String baseName = "test.neodatis";// getBaseName();
		IOUtil.deleteFile(baseName);
		// Creates a server
		ODBServer server = openServer(port);
		server.setAutomaticallyCreateDatabase(true);
		server.startServer(true);

		ODB odb = server.openClient(baseName);

		odb.store(new Function("ff11"));
		odb.close();

		odb = ODBFactory.openClient(getHOST(), port, baseName);
		Objects<Function> objects = odb.getObjects(Function.class);
		assertEquals(1, objects.size());
		odb.close();
		Thread.sleep(10 * 60 * 60 * 1000);
		server.close();
	}

}
