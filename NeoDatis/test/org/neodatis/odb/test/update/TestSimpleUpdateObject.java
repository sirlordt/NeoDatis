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
package org.neodatis.odb.test.update;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestSimpleUpdateObject extends ODBTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.test.ODBTestCase#tearDown()
	 */
	public void tearDown() throws Exception {
	}

	public void test1() throws Exception {
		deleteBase("t1u.neodatis");
		ODB odb = open("t1u.neodatis");

		Function login = new Function("login");
		Function logout = new Function("logout");
		odb.store(login);
		println("--------");
		odb.store(login);
		odb.store(logout);

		// odb.commit();
		odb.close();

		odb = open("t1u.neodatis");

		Objects l = odb.getObjects(Function.class, true);
		Function f2 = (Function) l.getFirst();
		f2.setName("login function");
		odb.store(f2);
		odb.close();
		ODB odb2 = open("t1u.neodatis");
		Function f = (Function) odb2.getObjects(Function.class).getFirst();
		assertEquals("login function", f.getName());
		odb2.close();
		deleteBase("t1u.neodatis");

	}

	public void test2() throws Exception {

		deleteBase("t2.neodatis");
		ODB odb = open("t2.neodatis");

		int nbUsers = odb.getObjects(User.class).size();
		int nbProfiles = odb.getObjects(Profile.class, true).size();
		int nbFunctions = odb.getObjects(Function.class, true).size();

		Function login = new Function("login");
		Function logout = new Function("logout");
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator", list);
		User olivier = new User("olivier smadja", "olivier@neodatis.com", profile);
		User aisa = new User("Aísa Galvão Smadja", "aisa@neodRMuatis.com", profile);

		odb.store(olivier);
		odb.store(aisa);
		odb.commit();

		Objects users = odb.getObjects(User.class, true);
		Objects profiles = odb.getObjects(Profile.class, true);
		Objects functions = odb.getObjects(Function.class, true);
		odb.close();
		// println("Users:"+users);
		println("Profiles:" + profiles);
		println("Functions:" + functions);

		odb = open("t2.neodatis");
		Objects l = odb.getObjects(User.class, true);
		odb.close();

		assertEquals(nbUsers + 2, users.size());
		User user2 = (User) users.getFirst();

		assertEquals(olivier.toString(), user2.toString());
		assertEquals(nbProfiles + 1, profiles.size());
		assertEquals(nbFunctions + 2, functions.size());

		ODB odb2 = open("t2.neodatis");
		l = odb2.getObjects(Function.class, true);
		Function function = (Function) l.getFirst();
		function.setName("login function");
		odb2.store(function);

		odb2.close();

		ODB odb3 = open("t2.neodatis");
		Objects l2 = odb3.getObjects(User.class, true);

		int i = 0;
		while (l2.hasNext() && i < Math.min(2, l2.size())) {
			User user = (User) l2.next();
			assertEquals("login function", "" + user.getProfile().getFunctions().get(0));
			i++;
		}
		odb3.close();
		deleteBase("t2.neodatis");

	}

	public void test3() throws Exception {
		deleteBase("t1u2.neodatis");
		ODB odb = open("t1u2.neodatis");

		Function login = new Function(null);
		odb.store(login);
		odb.close();

		odb = open("t1u2.neodatis");

		login = (Function) odb.getObjects(new CriteriaQuery(Function.class, Where.isNull("name"))).getFirst();
		assertTrue(login.getName() == null);
		login.setName("login");
		odb.store(login);
		odb.close();

		odb = open("t1u2.neodatis");

		login = (Function) odb.getObjects(Function.class).getFirst();
		assertTrue(login.getName().equals("login"));
		odb.close();
		deleteBase("t1u2.neodatis");

	}

	public void test5() throws Exception {
		deleteBase("t5.neodatis");
		ODB odb = open("t5.neodatis");
		long nbFunctions = odb.count(new CriteriaQuery(Function.class)).longValue();
		long nbProfiles = odb.count(new CriteriaQuery(Profile.class)).longValue();
		long nbUsers = odb.count(new CriteriaQuery(User.class)).longValue();
		Function login = new Function("login");
		Function logout = new Function("logout");
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator", list);
		User olivier = new User("olivier smadja", "olivier@neodatis.com", profile);
		User aisa = new User("Aísa Galvão Smadja", "aisa@neodatis.com", profile);

		odb.store(olivier);
		odb.store(profile);
		odb.commit();

		odb.close();

		odb = open("t5.neodatis");

		Objects users = odb.getObjects(User.class, true);
		Objects profiles = odb.getObjects(Profile.class, true);
		Objects functions = odb.getObjects(Function.class, true);
		odb.close();
		assertEquals(nbUsers + 1, users.size());
		assertEquals(nbProfiles + 1, profiles.size());
		assertEquals(nbFunctions + 2, functions.size());

	}

	public void test6() throws Exception {
		// LogUtil.objectWriterOn(true);

		deleteBase("t6.neodatis");
		ODB odb = open("t6.neodatis");

		Function login = new Function("login");
		Function logout = new Function("logout");
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator", list);
		User olivier = new User("olivier smadja", "olivier@neodatis.com", profile);

		odb.store(olivier);
		odb.close();
		println("----------");
		odb = open("t6.neodatis");
		Objects users = odb.getObjects(User.class, true);
		User u1 = (User) users.getFirst();
		u1.getProfile().setName("operator 234567891011121314");
		odb.store(u1);
		odb.close();

		odb = open("t6.neodatis");
		Objects profiles = odb.getObjects(Profile.class, true);
		assertEquals(1, profiles.size());
		Profile p1 = (Profile) profiles.getFirst();
		assertEquals(u1.getProfile().getName(), p1.getName());

	}
}
