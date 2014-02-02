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
package org.neodatis.odb.test.oid;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.wrappers.OdbTime;

public class TestGetObjectByOid extends ODBTest {

	/**
	 * Getting object by id after re opening database
	 * 
	 * @throws Exception
	 */
	public void test1() throws Exception {
		deleteBase("getid.neodatis");
		Function function1 = new Function("f1");
		Function function2 = new Function("f2");

		ODB odb = open("getid.neodatis");

		odb.store(function1);
		odb.store(function2);

		OID id1 = odb.getObjectId(function1);
		OID id2 = odb.getObjectId(function2);
		odb.close();

		odb = open("getid.neodatis");
		Function function1bis = (Function) odb.getObjectFromId(id1);
		assertEquals(function1.getName(), function1bis.getName());

		Function function2bis = (Function) odb.getObjectFromId(id2);
		function2bis.setName("function 2");
		odb.store(function2bis);
		OID id2bis = odb.getObjectId(function2bis);
		odb.close();

		odb = open("getid.neodatis");

		Function function2ter = (Function) odb.getObjectFromId(id2);

		assertEquals("function 2", function2ter.getName());

		odb.close();
		deleteBase("getid.neodatis");
	}

	/**
	 * Getting object by id during the same transaction
	 * 
	 * @throws Exception
	 */
	public void test2() throws Exception {
		deleteBase("getid.neodatis");
		Function function1 = new Function("f1");
		Function function2 = new Function("f2");

		ODB odb = open("getid.neodatis");

		odb.store(function1);
		odb.store(function2);

		OID id1 = odb.getObjectId(function1);
		OID id2 = odb.getObjectId(function2);

		Function function1bis = (Function) odb.getObjectFromId(id1);

		odb.close();
		assertEquals(function1.getName(), function1bis.getName());
		deleteBase("getid.neodatis");
	}

	/**
	 * Getting object by id after an in place update in the same transaction
	 * than the insert
	 * 
	 * @throws Exception
	 */
	public void test3() throws Exception {
		deleteBase("getid.neodatis");
		Function function1 = new Function("f1");
		Function function2 = new Function("f2");

		ODB odb = open("getid.neodatis");

		odb.store(function1);
		odb.store(function2);

		function1.setName("f2");
		odb.store(function1);

		OID id1 = odb.getObjectId(function1);
		OID id2 = odb.getObjectId(function2);

		Function function1bis = (Function) odb.getObjectFromId(id1);

		odb.close();
		assertEquals(function1.getName(), function1bis.getName());
		deleteBase("getid.neodatis");
	}

	/**
	 * Getting object by id after an update(not in place) in the same
	 * transaction than the insert
	 * 
	 * @throws Exception
	 */
	public void test4() throws Exception {
		deleteBase("getid.neodatis");
		Function function1 = new Function("f1");
		Function function2 = new Function("f2");

		ODB odb = open("getid.neodatis");

		odb.store(function1);
		odb.store(function2);

		function1.setName("function login and logout");
		odb.store(function1);

		OID id1 = odb.getObjectId(function1);
		OID id2 = odb.getObjectId(function2);

		Function function1bis = (Function) odb.getObjectFromId(id1);

		odb.close();
		assertEquals(function1.getName(), function1bis.getName());
		deleteBase("getid.neodatis");
	}

	/**
	 * Test performance of retrieving 2 objects by oid
	 * 
	 * @throws Exception
	 */
	public void test5() throws Exception {
		deleteBase("getid.neodatis");
		Function function1 = new Function("f1");
		Function function2 = new Function("f2");

		ODB odb = open("getid.neodatis");

		odb.store(function1);
		odb.store(function2);

		OID id1 = odb.getObjectId(function1);
		OID id2 = odb.getObjectId(function2);

		odb.close();
		odb = open("getid.neodatis");

		long t1 = OdbTime.getCurrentTimeInMs();
		Function function1bis = (Function) odb.getObjectFromId(id1);
		Function function2bis = (Function) odb.getObjectFromId(id2);
		long t2 = OdbTime.getCurrentTimeInMs();
		odb.close();
		deleteBase("getid.neodatis");

		assertEquals(function1.getName(), function1bis.getName());
		assertEquals(function2.getName(), function2bis.getName());
		long time = t2 - t1;
		println(time);
		long acceptableTime = isLocal ? 1 : 17;
		if (time > acceptableTime) { // ms
			fail("Getting two objects by oid lasted more than " + acceptableTime + "ms : " + time);
		}
	}

	/**
	 * Test performance of retrieving many simple objects by oid
	 * 
	 * @throws Exception
	 */
	public void test6() throws Exception {
		deleteBase("getid.neodatis");
		int size = isLocal ? 20001 : 2001;
		ODB odb = open("getid.neodatis");
		OID[] oids = new OID[size];

		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(new Function("function " + i));
		}

		odb.close();
		odb = open("getid.neodatis");

		long t1 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			Function f = (Function) odb.getObjectFromId(oids[i]);
			assertEquals("function " + i, f.getName());
			if (i % 3000 == 0) {
				println(i + "/" + size);
			}
		}

		long t2 = OdbTime.getCurrentTimeInMs();
		odb.close();
		deleteBase("getid.neodatis");

		long time = t2 - t1;
		double timeForEachGet = (double) time / (double) size;
		double acceptableTime = isLocal ? 0.022 : 0.5; // 0.04294785260736963
		println("time for each get = " + time + "/" + size + " = " + timeForEachGet);
		if (testPerformance && timeForEachGet > acceptableTime) { // ms
			fail("Getting " + size + " simple objects by oid lasted more than " + acceptableTime + "ms : " + timeForEachGet);
		}
	}

	/**
	 * Test performance of retrieving many complex objects by oid
	 * 
	 * @throws Exception
	 */
	public void test7() throws Exception {
		deleteBase("getid.neodatis");
		int size = isLocal ? 10001 : 1000;
		ODB odb = open("getid.neodatis");
		OID[] oids = new OID[size];

		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance(i));
		}

		odb.close();
		odb = open("getid.neodatis");

		long t1 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			User u = (User) odb.getObjectFromId(oids[i]);
			assertEquals("kiko" + i, u.getName());
		}

		long t2 = OdbTime.getCurrentTimeInMs();
		odb.close();
		deleteBase("getid.neodatis");

		long time = t2 - t1;
		double timeForEachGet = (double) time / (double) size;
		double acceptableTime = isLocal ? 0.086 : 1.6; // 0.1561843815618438
		println("time for each get = " + timeForEachGet + " - Total time for " + size + " objects = " + time);
		if (testPerformance && timeForEachGet > acceptableTime) { // ms
			println("time for each get = " + timeForEachGet + " - Total time for " + size + " objects = " + time);
			fail("Getting " + size + " complex objects by oid lasted more than " + acceptableTime + "ms : " + timeForEachGet);
		}
	}

	/**
	 * Trying to get an object with OID that does not exist
	 * 
	 * @throws Exception
	 */
	public void testGetOIDThatDoesNotExist() throws Exception {
		deleteBase("getid.neodatis");
		Function function2 = new Function("f2");

		ODB odb = open("getid.neodatis");

		OID oid = OIDFactory.buildObjectOID(49);
		try {
			Object o = odb.getObjectFromId(oid);
			fail("It should have thrwon an exception");
		} catch (Exception e) {
			odb.close();
			assertFalse(e.getMessage().indexOf(" does not exist in the database") == -1);
		}

	}

	private Object getInstance(int i) {
		Function login = new Function("login " + i);
		Function logout = new Function("logout" + i);
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator" + i, list);
		User user = new User("kiko" + i, "olivier@neodatis.com" + i, profile);
		return user;
	}
}
