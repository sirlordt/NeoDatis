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
package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.IOUtil;

public class TestServerWithTrigger extends ODBTest {
	public static String BASE_NAME = "server-trigger.neodatis";
	private int PORT = 10001;

	public void testInsert() throws Exception {
		IOUtil.deleteFile("server-trigger");
		ODBServer server = ODBFactory.openServer(PORT);
		server.addBase(BASE_NAME, "server-trigger");
		MyInsertTrigger insertTrigger = new MyInsertTrigger();
		MyInsertTriggerAllClasses insertTriggerAllClasses = new MyInsertTriggerAllClasses();
		server.addInsertTrigger(BASE_NAME, Function.class.getName(), insertTrigger);
		server.addInsertTrigger(BASE_NAME, null, insertTriggerAllClasses);

		// LogUtil.allOn(true);
		server.startServer(true);
		Thread.sleep(300);
		ODB odb = ODBFactory.openClient("localhost", PORT, BASE_NAME);
		odb.store(new Function("Function 1"));
		odb.close();

		server.close();
		assertEquals(1, insertTrigger.getNbInsertsBefore());
		assertEquals(1, insertTrigger.getNbInsertsAfter());

		assertEquals(1, insertTriggerAllClasses.getNbInsertsBefore());
		assertEquals(1, insertTriggerAllClasses.getNbInsertsAfter());
	}

	public void testInsert2() throws Exception {
		IOUtil.deleteFile("server-trigger");
		ODBServer server = ODBFactory.openServer(PORT);
		server.addBase(BASE_NAME, "server-trigger");
		MyInsertTrigger insertTrigger = new MyInsertTrigger();
		MyInsertTriggerAllClasses insertTriggerAllClasses = new MyInsertTriggerAllClasses();
		server.addInsertTrigger(BASE_NAME, Profile.class.getName(), insertTrigger);
		server.addInsertTrigger(BASE_NAME, null, insertTriggerAllClasses);

		// LogUtil.allOn(true);
		server.startServer(true);
		Thread.sleep(300);
		ODB odb = ODBFactory.openClient("localhost", PORT, BASE_NAME);
		Profile profile = new Profile("Profile name", new Function("F1"));
		odb.store(profile);
		odb.close();

		server.close();
		assertEquals(1, insertTrigger.getNbInsertsBefore());
		assertEquals(1, insertTrigger.getNbInsertsAfter());

		// Because it must be executed for Profile & Function
		assertEquals(2, insertTriggerAllClasses.getNbInsertsBefore());
		assertEquals(2, insertTriggerAllClasses.getNbInsertsAfter());
	}

	public void testInsertToGetExternalOid() throws Exception {
		IOUtil.deleteFile("server-trigger");
		ODBServer server = ODBFactory.openServer(PORT);
		server.addBase(BASE_NAME, "server-trigger");

		// LogUtil.allOn(true);
		server.startServer(true);
		Thread.sleep(100);

		// Make a first connection to retrieve the database id (needed to build
		// External OID)
		ODB odb = ODBFactory.openClient("localhost", PORT, BASE_NAME);
		DatabaseId databaseId = odb.ext().getDatabaseId();
		odb.close();
		println(databaseId);

		// Using internal API
		odb = ODBFactory.openClient("localhost", PORT, BASE_NAME);
		IStorageEngine engine = Dummy.getEngine(odb);
		DatabaseId databaseId2 = engine.getDatabaseId();
		odb.close();
		println(databaseId2);

		assertEquals(databaseId, databaseId2);

		// Create and Add the trigger
		ReplicationInsertTrigger replicationInsertTrigger = new ReplicationInsertTrigger();
		server.addInsertTrigger(BASE_NAME, Profile.class.getName(), replicationInsertTrigger);

		odb = ODBFactory.openClient("localhost", PORT, BASE_NAME);
		Profile profile = new Profile("Profile name", new Function("F1"));
		odb.store(profile);
		odb.close();

		server.close();
		assertEquals(1, replicationInsertTrigger.getNbInsertsAfter());
	}

	public void testUpdate() throws Exception {
		IOUtil.deleteFile("server-trigger");
		int port = PORT + 3;
		ODBServer server = ODBFactory.openServer(port);
		server.addBase(BASE_NAME, "server-trigger");
		MyUpdateTrigger updateTrigger = new MyUpdateTrigger();
		MyUpdateTriggerAllClasses updateTriggerAllClasses = new MyUpdateTriggerAllClasses();
		server.addUpdateTrigger(BASE_NAME, Function.class.getName(), updateTrigger);
		server.addUpdateTrigger(BASE_NAME, null, updateTriggerAllClasses);

		// LogUtil.allOn(true);
		server.startServer(true);
		Thread.sleep(300);
		ODB odb = ODBFactory.openClient("localhost", port, BASE_NAME);
		odb.store(new Function("Function 1"));
		odb.close();

		odb = ODBFactory.openClient("localhost", port, BASE_NAME);
		Function f = (Function) odb.getObjects(Function.class).getFirst();
		f.setName("new name");
		odb.store(f);
		odb.close();

		server.close();
		assertEquals(1, updateTrigger.getNbUpdatesBefore());
		assertEquals(1, updateTrigger.getNbUpdatesAfter());

		assertEquals(1, updateTriggerAllClasses.getNbUpdatesBefore());
		assertEquals(1, updateTriggerAllClasses.getNbUpdatesAfter());

	}

	public void testDelete() throws Exception {
		IOUtil.deleteFile("server-trigger");
		int port = PORT + 3;

		ODBServer server = ODBFactory.openServer(port);
		server.addBase(BASE_NAME, "server-trigger");
		MyDeleteTrigger deleteTrigger = new MyDeleteTrigger();
		MyDeleteTriggerForAllClasses deleteTriggerAllClasses = new MyDeleteTriggerForAllClasses();
		server.addDeleteTrigger(BASE_NAME, Function.class.getName(), deleteTrigger);
		server.addDeleteTrigger(BASE_NAME, null, deleteTriggerAllClasses);

		// LogUtil.allOn(true);
		server.startServer(true);
		Thread.sleep(300);
		ODB odb = ODBFactory.openClient("localhost", port, BASE_NAME);
		odb.store(new Function("Function 1"));
		odb.close();

		odb = ODBFactory.openClient("localhost", port, BASE_NAME);
		Function f = (Function) odb.getObjects(Function.class).getFirst();
		odb.delete(f);
		odb.close();

		server.close();
		assertEquals(1, deleteTrigger.getNbDeletesBefore());
		assertEquals(1, deleteTrigger.getNbDeletesAfter());

		// Check if the "all class" trigger has been executed correctly
		assertEquals(1, deleteTriggerAllClasses.getNbDeletesBefore());
		assertEquals(1, deleteTriggerAllClasses.getNbDeletesAfter());

	}

	/**
	 * Uses server side trigger to build a simple replication mechanism
	 * 
	 * @throws Exception
	 */
	public void testSimpleReplication() throws Exception {
		String baseName = getBaseName();
		String replicatedBaseName = "replicated-" + getBaseName();
		int port = PORT + 1;

		ODBServer server = ODBFactory.openServer(port);
		server.addBase(baseName, baseName);

		// LogUtil.allOn(true);
		server.startServer(true);
		Thread.sleep(100);

		ODB replicatedODB = ODBFactory.open(replicatedBaseName);

		// Create and Add the trigger
		RealReplicationInsertTrigger replicationInsertTrigger = new RealReplicationInsertTrigger(replicatedODB);
		// register trigger for all classes (null)
		server.addInsertTrigger(baseName, null, replicationInsertTrigger);

		ODB odb = ODBFactory.openClient("localhost", port, baseName);
		Profile profile = new Profile("Profile name", new Function("F1"));
		odb.store(profile);

		odb.close();
		server.close();
		replicatedODB.close();
		// 2 inserts : 1 Profile & 1 Function
		assertEquals(2, replicationInsertTrigger.getNbInsertsAfter());

		replicatedODB = ODBFactory.open(replicatedBaseName);
		Objects profiles = replicatedODB.getObjects(Profile.class);
		Objects functions = replicatedODB.getObjects(Function.class);
		replicatedODB.close();

		assertEquals(1, profiles.size());
		assertEquals(1, functions.size());

	}

	/**
	 * Uses server side trigger to build a simple replication mechanism
	 * 
	 * @throws Exception
	 */
	public void testSimpleReplicationOneFunction() throws Exception {

		String baseName = getBaseName();
		String replicatedBaseName = "replicated-" + getBaseName();
		int port = PORT + 1;

		ODBServer server = ODBFactory.openServer(port);
		server.addBase(baseName, baseName);

		// LogUtil.allOn(true);
		server.startServer(true);
		Thread.sleep(100);

		ODB replicatedODB = ODBFactory.open(replicatedBaseName);

		// Create and Add the trigger
		RealReplicationInsertTrigger replicationInsertTrigger = new RealReplicationInsertTrigger(replicatedODB);
		// register trigger for all classes (null)
		server.addInsertTrigger(baseName, null, replicationInsertTrigger);

		ODB odb = ODBFactory.openClient("localhost", port, baseName);
		Function f = new Function("F1");
		odb.store(f);
		odb.close();

		server.close();
		replicatedODB.close();
		// 2 inserts : 1 Function
		assertEquals(1, replicationInsertTrigger.getNbInsertsAfter());

		replicatedODB = ODBFactory.open(replicatedBaseName);
		Objects functions = replicatedODB.getObjects(Function.class);
		replicatedODB.close();

		assertEquals(1, functions.size());
		Function f2 = (Function) functions.getFirst();
		assertEquals("F1", f2.getName());

	}

	// To test select triggers
	public void testSelectTrigger() throws Exception {
		ODB odb = null;
		MyServerSelectTrigger myTrigger = new MyServerSelectTrigger();
		ODBServer server = null;
		String baseName = getBaseName();
		try {

			server = ODBFactory.openServer(PORT);
			server.addBase(baseName, DIRECTORY + baseName);

			// LogUtil.allOn(true);
			server.startServer(true);
			Thread.sleep(100);

			odb = ODBFactory.openClient("localhost", PORT, baseName);

			Function f1 = new Function("function1");
			Function f2 = new Function("function2");

			Profile profile = new Profile("profile1", f1);

			User user = new User("oli", "oli@neodatis.com", profile);

			odb.store(user);
			odb.store(f2);

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
		odb = ODBFactory.openClient("localhost", PORT, baseName);
		try {
			odb.addSelectTrigger(Function.class, myTrigger);
			fail("Should have thrown exception because of associating server trigger to local or client odb");
		} catch (Exception e) {
			// TODO: handle exception
		}
		server.addSelectTrigger(baseName, Function.class.getName(), myTrigger);
		Objects<Function> functions = odb.getObjects(Function.class);

		odb.close();
		server.close();
		assertEquals(2, functions.size());
		assertEquals(2, myTrigger.nbCalls);
	}

	// To test select triggers
	public void testSelectTrigger2() throws Exception {
		ODB odb = null;
		MyServerSelectTrigger myTrigger = new MyServerSelectTrigger();
		ODBServer server = null;
		String baseName = getBaseName();
		try {

			server = ODBFactory.openServer(PORT+9);
			server.addBase(baseName, DIRECTORY + baseName);

			// LogUtil.allOn(true);
			server.startServer(true);
			Thread.sleep(100);

			odb = ODBFactory.openClient("localhost", PORT+9, baseName);

			Function f1 = new Function("function1");
			Function f2 = new Function("function2");

			Profile profile = new Profile("profile1", f1);

			User user = new User("oli", "oli@neodatis.com", profile);

			odb.store(user);
			odb.store(f2);

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
		odb = ODBFactory.openClient("localhost", PORT+9, baseName);
		try {
			odb.addSelectTrigger(Function.class, myTrigger);
			fail("Should have thrown exception because of associating server trigger to local or client odb");
		} catch (Exception e) {
			// TODO: handle exception
		}
		server.addSelectTrigger(baseName, Function.class.getName(), myTrigger);
		Objects<Function> functions = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "function1")));

		odb.close();
		server.close();
		assertEquals(1, functions.size());
		assertEquals(1, myTrigger.nbCalls);
	}

	// To test select triggers
	public void testSelectTrigger3() throws Exception {
		ODB odb = null;
		MyServerSelectTrigger myTrigger = new MyServerSelectTrigger();
		ODBServer server = null;
		String baseName = getBaseName();
		try {

			server = ODBFactory.openServer(PORT);
			server.addBase(baseName, DIRECTORY + baseName);

			// LogUtil.allOn(true);
			server.startServer(true);
			Thread.sleep(100);

			odb = ODBFactory.openClient("localhost", PORT, baseName);

			Function f1 = new Function("function1");
			Function f2 = new Function("function2");

			Profile profile = new Profile("profile1", f1);

			User user = new User("oli", "oli@neodatis.com", profile);

			odb.store(user);
			odb.store(f2);

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
		odb = ODBFactory.openClient("localhost", PORT, baseName);
		try {
			odb.addSelectTrigger(Function.class, myTrigger);
			fail("Should have thrown exception because of associating server trigger to local or client odb");
		} catch (Exception e) {
			// TODO: handle exception
		}
		server.addSelectTrigger(baseName, null, myTrigger);
		Objects<User> users = odb.getObjects(new CriteriaQuery(User.class, Where.equal("name", "oli")));

		odb.close();
		server.close();
		assertEquals(1, users.size());
		assertEquals(3, myTrigger.nbCalls);
	}

}
