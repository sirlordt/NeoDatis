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
package org.neodatis.odb.test.server.trigger.autoincrement;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.IOUtil;

public class TestAutoIncrementTrigger extends ODBTest {

	public static final String BASE = "trigger-auto-increment-server.neodatis";

	public void test1WithServerTrigger() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {

			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addInsertTrigger(BASE, ObjectWithAutoIncrementId.class.getName(), new ServerAutoIncrementTrigger());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);

			// Creates the object to be inserted
			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");

			// Call the store
			OID oid = odb.store(o);

			// Close the database

			// The assert is commented, as the id is not sent back in real time.
			// The ID is set on the server but not reflected
			// now on the client. This is a bug and should be implemented.
			if (testKnownProblems) {
				assertEquals(1, o.getId());
			}
			odb.close();

			// Re open the db to check if ID was set
			odb = ODBFactory.openClient("localhost", port, BASE);
			ObjectWithAutoIncrementId oo = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid);
			assertEquals(1, oo.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}
	
	public void test1WithServerTriggerWithRecursiveObjects() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {

			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addInsertTrigger(BASE, UserWithId.class.getName(), new ServerAutoIncrementTrigger());
			myServer.addInsertTrigger(BASE, ProfileWithId.class.getName(), new ServerAutoIncrementTrigger());
			myServer.addInsertTrigger(BASE, FunctionWithId.class.getName(), new ServerAutoIncrementTrigger());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);

			
			FunctionWithId f1 = new FunctionWithId("f1");
			FunctionWithId f2 = new FunctionWithId("f2");
			ProfileWithId p = new ProfileWithId("profile");
			p.addFunction(f1);
			p.addFunction(f2);
			UserWithId user = new UserWithId("user","email",p);
			// Call the store
			OID oid = odb.store(user);

			// Close the database

			assertEquals(1, user.getId());
			assertEquals(1, user.getProfile().getId());
			assertEquals(1, user.getProfile().getFunctions().get(0).getId());
			assertEquals(2, user.getProfile().getFunctions().get(1).getId());
			
		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	public void test1WithClientTrigger() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {

			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);
			odb.addInsertTrigger(ObjectWithAutoIncrementId.class, new ClientAutoIncrementTrigger());

			// Creates the object to be inserted
			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");

			// Call the store
			OID oid = odb.store(o);

			// Close the database

			assertEquals(1, o.getId());
			odb.close();

			// Re open the db to check if ID was set
			odb = ODBFactory.openClient("localhost", port, BASE);
			ObjectWithAutoIncrementId oo = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid);
			assertEquals(1, oo.getId());

		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	public void test2Objects() throws Exception {

		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12009;

		try {

			myServer = ODBFactory.openServer(port);
			myServer.addBase(BASE, BASE);
			myServer.addInsertTrigger(BASE, ObjectWithAutoIncrementId.class.getName(), new ServerAutoIncrementTrigger());
			myServer.startServer(true);

			odb = ODBFactory.openClient("localhost", port, BASE);
			ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object 1");
			odb.store(o);
			if (testKnownProblems) {
				assertEquals(1, o.getId());
			}

			odb.close();

			odb = ODBFactory.openClient("localhost", port, BASE);
			ObjectWithAutoIncrementId o2 = new ObjectWithAutoIncrementId("Object 2");
			odb.store(o2);
			if (testKnownProblems) {
				assertEquals(2, o2.getId());
			}

			odb.close();

			odb = ODBFactory.openClient("localhost", port, BASE);
			ObjectWithAutoIncrementId o3 = new ObjectWithAutoIncrementId("Object 3");
			OID oid = odb.store(o3);
			if (testKnownProblems) {
				assertEquals(3, o3.getId());
			}
			odb.close();

			odb = ODBFactory.openClient("localhost", port, BASE);
			ObjectWithAutoIncrementId oo = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid);
			odb.close();
			assertEquals(3, oo.getId());

		} finally {
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	public void test1000Objects() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);

		ODBServer myServer = null;
		int port = 12009;
		try {

			myServer = ODBFactory.openServer(port);
			myServer.addBase(BASE, BASE);
			myServer.addInsertTrigger(BASE, ObjectWithAutoIncrementId.class.getName(), new ServerAutoIncrementTrigger());
			myServer.startServer(true);

			odb = ODBFactory.openClient("localhost", port, BASE);

			for (int i = 0; i < 1000; i++) {
				ObjectWithAutoIncrementId o = new ObjectWithAutoIncrementId("Object " + (i + 1));
				odb.store(o);
				if (testKnownProblems) {
					assertEquals(i + 1, o.getId());
				}
			}
			odb.close();

			odb = ODBFactory.openClient("localhost", port, BASE);
			OID oid = null;
			for (int j = 0; j < 1000; j++) {
				ObjectWithAutoIncrementId o2 = new ObjectWithAutoIncrementId("Object - bis - " + (j + 1));
				oid = odb.store(o2);
				if (testKnownProblems) {
					assertEquals(1000 + j + 1, o2.getId());
				}
			}
			odb.close();
			odb = ODBFactory.openClient("localhost", port, BASE);
			ObjectWithAutoIncrementId o2o3 = (ObjectWithAutoIncrementId) odb.getObjectFromId(oid);
			assertEquals(2000, o2o3.getId());

		} finally {
			if (myServer != null) {
				myServer.close();
			}
		}
	}
}
