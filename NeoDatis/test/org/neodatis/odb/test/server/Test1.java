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

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.wrappers.OdbTime;

public class Test1 extends ODBTest {
	static boolean started = false;

	public void setUp() throws Exception {
		super.setUp();
		if (!started) {
			started = true;
			// ODBServer server = openServer(10000);
			// server.startServer(true);
			println("starting******************");
			deleteBase("b1.neodatis");
			deleteBase("b12.neodatis");
			deleteBase("s.neodatis");
			deleteBase("base1.neodatis");

			/*
			 * ODB odb = open("b1.odb.neodatis"); odb.store(new Function("f1"));
			 * odb.close();
			 */

			// server.addBase("b1", "b1.neodatis");
			// server.addBase("b2", "b12.neodatis");
			// server.addBase("base1", "s.neodatis");
			// server.setAutomaticallyCreateDatabase(true);
		}
	}

	public void test11() throws Exception {
		if (isLocal)
			return;
		try {
			println("Start TEST1");
			deleteBase("b1.neodatis");
			ODB odb = openClient("localhost", ODBTest.PORT, "b1.neodatis");
			Objects l = odb.getObjects(Function.class);
			println("Before insert :" + l);

			Function f2 = new Function("f2");
			odb.store(f2);
			odb.close();

			odb = openClient("localhost", ODBTest.PORT, "b1.neodatis");
			l = odb.getObjects(Function.class);
			println("After insert :" + l);
			odb.close();
			// server.close();
			assertEquals(1, l.size());
			println("END TEST1");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testUpdate() throws Exception {
		if (isLocal)
			return;
		try {
			deleteBase("b1.neodatis");
			ODB odb = openClient("localhost", ODBTest.PORT, "b1.neodatis");
			Objects l = odb.getObjects(Function.class);
			println("Before insert :" + l);
			// assertEquals(2, l.size());

			Function f2 = new Function("f2");
			odb.store(f2);
			odb.close();

			odb = openClient("localhost", ODBTest.PORT, "b1.neodatis");
			l = odb.getObjects(Function.class);

			Function f = (Function) l.getFirst();
			f.setName("function f1");
			odb.store(f);
			odb.close();

			odb = openClient("localhost", ODBTest.PORT, "b1.neodatis");

			assertEquals(1, odb.getObjects(Function.class).size());
			odb.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testInserts() throws Exception {
		if (isLocal)
			return;
		try {
			deleteBase("b1.neodatis");
			ODB odb = openClient("localhost", ODBTest.PORT, "b1.neodatis");
			Objects l = odb.getObjects(Function.class);
			println("Before insert :" + l);
			assertEquals(0, l.size());

			int size = 100;
			long start = OdbTime.getCurrentTimeInMs();
			for (int i = 0; i < size; i++) {
				odb.store(new Function("function " + (i + 1)));
			}
			long t2 = OdbTime.getCurrentTimeInMs();
			odb.close();
			long t3 = OdbTime.getCurrentTimeInMs();
			println(size + " objects " + (t2 - start) + " - " + (t3 - t2));

			odb = openClient("localhost", ODBTest.PORT, "b1.neodatis");
			assertEquals(size, odb.getObjects(Function.class).size());
			odb.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void test20() throws Exception {
		if (isLocal)
			return;
		deleteBase("base1.neodatis");
		println("Test20");
		// Create instance
		Function function = new Function("function 1");

		ODB odb = null;
		try {
			// Open the databse client on the localhost on port 10000 and
			// specify
			// which database instance
			odb = openClient("localhost", ODBTest.PORT, "base1.neodatis");

			// Store the object
			odb.store(function);
			odb.close();

			odb = openClient("localhost", ODBTest.PORT, "base1.neodatis");
			assertEquals(1, odb.getObjects(Function.class).size());
		} finally {
			if (odb != null) {
				// First close the client
				odb.close();
			}
		}
	}

	public void test21() throws Exception {
		if (isLocal)
			return;
		// Create instance
		Function function1 = new Function("function 1");
		Function function2 = new Function("function 1");
		Profile profile1 = new Profile("p1", function1);
		Profile profile2 = new Profile("p1", function2);
		User user = new User("user1", "mail1", profile1);
		User user2 = new User("user2", "mail2", profile2);

		deleteBase("base2.neodatis");
		ODB odb = null;
		try {
			// Open the databse client on the localhost on port 10000 and
			// specify
			// which database instance
			odb = openClient("localhost", ODBTest.PORT, "base2.neodatis");

			// Store the object
			odb.store(user);
			odb.store(user2);
			odb.close();

			odb = openClient("localhost", ODBTest.PORT, "base2.neodatis");
			println(odb.getObjects(User.class).size());
			assertEquals(2, odb.getObjects(User.class).size());
		} finally {
			if (odb != null) {
				// First close the client
				odb.close();
			}
		}
	}
}
