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
package org.neodatis.odb.test.trigger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestTrigger extends ODBTest {

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		ODB odb = null;
		deleteBase("trigger.neodatis");
		MyTrigger myTrigger = new MyTrigger();
		try {

			odb = open("trigger.neodatis");
			odb.addInsertTrigger(User.class, myTrigger);

			Function f1 = new Function("function1");
			Function f2 = new Function("function2");

			Profile profile = new Profile("profile1", f1);

			User user = new User("oli", "oli@neodatis.com", profile);

			odb.store(user);

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
		odb = open("trigger.neodatis");
		odb.close();
		deleteBase("trigger.neodatis");
		assertEquals(1, myTrigger.nbInsertsBefore);
		assertEquals(1, myTrigger.nbInsertsAfter);
	}

	// To test if triggers are called on recursive objects
	public void test2() throws Exception {
		if (!isLocal) {
			return;
		}
		ODB odb = null;
		deleteBase("trigger.neodatis");
		MyTrigger myTrigger = new MyTrigger();
		try {

			odb = open("trigger.neodatis");
			odb.addInsertTrigger(Function.class, myTrigger);

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
		odb = open("trigger.neodatis");
		odb.close();
		deleteBase("trigger.neodatis");
		assertEquals(2, myTrigger.nbInsertsBefore);
		assertEquals(2, myTrigger.nbInsertsAfter);
	}

	// To test select triggers
	public void testSelectTrigger() throws Exception {
		if (!isLocal) {
			return;
		}
		ODB odb = null;
		deleteBase("trigger.neodatis");
		MySelectTrigger myTrigger = new MySelectTrigger();
		try {

			odb = open("trigger.neodatis");

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
		odb = open("trigger.neodatis");
		odb.addSelectTrigger(Function.class, myTrigger);
		Objects<Function> functions = odb.getObjects(Function.class);
		odb.close();
		deleteBase("trigger.neodatis");
		assertEquals(2, functions.size());
		assertEquals(2, myTrigger.nbCalls);
	}

}
