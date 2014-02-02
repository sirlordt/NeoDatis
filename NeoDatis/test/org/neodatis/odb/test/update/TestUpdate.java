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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;
import org.neodatis.odb.impl.core.layers.layer2.meta.history.InsertHistoryInfo;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.wrappers.OdbTime;

public class TestUpdate extends ODBTest {
	public static int NB_OBJECTS = 50;
	public static String FILE_NAME = "update.neodatis";
	private static boolean first = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
		super.setUp();
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);
		for (int i = 0; i < NB_OBJECTS; i++) {
			odb.store(new Function("function " + (i + i)));
			odb.store(new User("olivier " + i, "olivier@neodatis.com " + i,
					new Profile("profile " + i, new Function("inner function " + i))));
		}
		odb.close();

		odb = open(FILE_NAME);
		Objects l = odb.getObjects(Function.class);
		println(l.size());
		assertEquals(2 * NB_OBJECTS, l.size());
		odb.close();

	}

	public void test1() throws Exception {
		ODB odb = open(FILE_NAME);

		IQuery query = new CriteriaQuery(Function.class, Where.equal("name", "function 10"));
		Objects l = odb.getObjects(query);
		int size = l.size();
		assertFalse(l.isEmpty());
		Function f = (Function) l.getFirst();
		OID id = odb.getObjectId(f);

		assertEquals("function 10", f.getName());
		String newName = String.valueOf(OdbTime.getCurrentTimeInMs());
		f.setName(newName);
		odb.store(f);
		odb.close();

		odb = open(FILE_NAME);

		l = odb.getObjects(query);

		query = new CriteriaQuery(Function.class, Where.equal("name", newName));

		assertTrue(size == l.size() + 1);

		l = odb.getObjects(query);

		assertFalse(l.isEmpty());
		assertEquals(1, l.size());
		assertEquals(id, odb.getObjectId(l.getFirst()));
		odb.close();

	}

	public void test2() throws Exception {

		ODB odb = open(FILE_NAME);
		int nbProfiles = odb.getObjects(Profile.class).size();
		IQuery query = new CriteriaQuery(User.class, Where.equal("profile.name", "profile 10"));
		Objects l = odb.getObjects(query);
		int size = l.size();

		assertFalse(l.isEmpty());
		User u = (User) l.getFirst();

		assertEquals("profile 10", u.getProfile().getName());
		Profile p2 = u.getProfile();
		final String newName = String.valueOf(OdbTime.getCurrentTimeInMs()) + "-";
		p2.setName(newName);
		odb.store(p2);
		odb.close();

		odb = open(FILE_NAME);

		l = odb.getObjects(query);

		assertTrue(l.size() == size - 1);

		if (!isLocal) {
			query = new CriteriaQuery(User.class, Where.equal("profile.name", newName));
		} else {
			query = new SimpleNativeQuery() {
				public boolean match(User user) {
					return user.getProfile().getName().equals(newName);
				}
			};
		}

		l = odb.getObjects(query);

		assertFalse(l.isEmpty());
		l = odb.getObjects(Profile.class, false);
		assertEquals(nbProfiles, l.size());
		odb.close();

	}

	public void test3() throws Exception {
		ODB odb = open(FILE_NAME);
		IQuery pquery = new CriteriaQuery(Profile.class, Where.equal("name", "profile 10"));
		long nbProfiles = odb.count(new CriteriaQuery(Profile.class)).longValue();
		long nbProfiles10 = odb.getObjects(pquery).size();
		IQuery query = new CriteriaQuery(User.class, Where.equal("profile.name", "profile 10"));

		Objects l = odb.getObjects(query);
		int size = l.size();
		assertFalse(l.isEmpty());
		User u = (User) l.getFirst();

		assertEquals("profile 10", u.getProfile().getName());
		final String newName = String.valueOf(OdbTime.getCurrentTimeInMs()) + "+";
		Profile p2 = u.getProfile();
		p2.setName(newName);
		odb.store(u);
		odb.close();

		odb = open(FILE_NAME);

		l = odb.getObjects(query);

		assertEquals(l.size() + 1, size);
		assertEquals(nbProfiles10, odb.getObjects(pquery).size() + 1);

		if (!isLocal) {
			query = new CriteriaQuery(User.class, Where.equal("profile.name", newName));
		} else {
			query = new SimpleNativeQuery() {
				public boolean match(User user) {
					return user.getProfile().getName().equals(newName);
				}
			};
		}

		l = odb.getObjects(query);

		assertEquals(1, l.size());
		l = odb.getObjects(Profile.class, false);
		assertEquals(nbProfiles, l.size());
		odb.close();

	}

	public void test4() throws Exception {

		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);
		OdbConfiguration.setMaxNumberOfObjectInCache(10);
		try {
			List list = new ArrayList();
			for (int i = 0; i < 15; i++) {
				Function function = new Function("function " + i);
				try {
					odb.store(function);
				} catch (Exception e) {
					odb.rollback();
					odb.close();
					assertTrue(e.getMessage().indexOf("Cache is full!") != -1);
					return;
				}
				list.add(function);
			}
			odb.close();

			odb = open(FILE_NAME);
			Objects l = odb.getObjects(Function.class, true);
			l.next();
			l.next();
			odb.store(l.next());
			odb.close();

			odb = open(FILE_NAME);
			assertEquals(15, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();

		} finally {
			OdbConfiguration.setMaxNumberOfObjectInCache(300000);
		}

	}

	public void test5() throws Exception {
		try {
			deleteBase(FILE_NAME);
			ODB odb = open(FILE_NAME);
			List list = new ArrayList();
			for (int i = 0; i < 15; i++) {
				Function function = new Function("function " + i);
				odb.store(function);
				list.add(function);
			}
			odb.close();

			OdbConfiguration.setMaxNumberOfObjectInCache(15);
			odb = open(FILE_NAME);
			IQuery query = new CriteriaQuery(Function.class, Where.or().add(Where.like("name", "%9")).add(Where.like("name", "%8")));
			Objects l = odb.getObjects(query, false);

			assertEquals(2, l.size());
			l.next();
			odb.store(l.next());
			odb.close();

			odb = open(FILE_NAME);
			assertEquals(15, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		} finally {
			OdbConfiguration.setMaxNumberOfObjectInCache(300000);
		}

	}

	public void test6() throws Exception {
		MyObject mo = null;
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);
		mo = new MyObject(15, "oli");
		mo.setDate(new Date());
		odb.store(mo);
		odb.close();

		odb = open(FILE_NAME);
		MyObject mo2 = (MyObject) odb.getObjects(MyObject.class).getFirst();
		mo2.setDate(new Date(mo.getDate().getTime() + 10));
		mo2.setSize(mo.getSize() + 1);
		odb.store(mo2);
		odb.close();

		odb = open(FILE_NAME);
		MyObject mo3 = (MyObject) odb.getObjects(MyObject.class).getFirst();
		assertEquals(mo3.getDate().getTime(), mo2.getDate().getTime());
		assertTrue(mo3.getDate().getTime() > mo.getDate().getTime());
		assertTrue(mo3.getSize() == mo.getSize() + 1);
		odb.close();

		// println("before:" + mo.getDate().getTime() + " - " + mo.getSize());
		// println("after:" + mo3.getDate().getTime() + " - " + mo3.getSize());

	}

	/**
	 * When an object an a collection attribute, and this colllection is changed
	 * (adding one object),no update in place is possible for instance.
	 * 
	 * @throws Exception
	 */
	public void test7() throws Exception {
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);

		Function function = new Function("login");
		Profile profile = new Profile("operator", function);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);

		odb.store(user);
		odb.close();

		odb = open(FILE_NAME);
		User user2 = (User) odb.getObjects(User.class).getFirst();
		user2.getProfile().addFunction(new Function("new Function"));
		odb.store(user2);
		odb.close();

		odb = open(FILE_NAME);
		User user3 = (User) odb.getObjects(User.class).getFirst();
		assertEquals(2, user3.getProfile().getFunctions().size());
		Function f1 = (Function) user3.getProfile().getFunctions().get(0);
		Function f2 = (Function) user3.getProfile().getFunctions().get(1);
		assertEquals("login", f1.getName());
		assertEquals("new Function", f2.getName());
		odb.close();

	}

	/**
	 * setting one attribute to null
	 * 
	 * 
	 * @throws Exception
	 */
	public void test8() throws Exception {
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);

		Function function = new Function("login");
		Profile profile = new Profile("operator", function);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);

		odb.store(user);
		odb.close();

		odb = open(FILE_NAME);
		User user2 = (User) odb.getObjects(User.class).getFirst();
		user2.setProfile(null);
		odb.store(user2);
		odb.close();

		odb = open(FILE_NAME);
		User user3 = (User) odb.getObjects(User.class).getFirst();
		assertNull(user3.getProfile());
		odb.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		OdbConfiguration.setMaxNumberOfObjectInCache(300000);
		deleteBase(FILE_NAME);
	}

	/** Test updaing a non native attribute with a new non native object */
	public void testUpdateObjectReference() throws Exception {
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);

		Function function = new Function("login");
		Profile profile = new Profile("operator", function);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);

		odb.store(user);
		odb.close();

		Profile profile2 = new Profile("new operator", function);

		odb = open(FILE_NAME);
		User user2 = (User) odb.getObjects(User.class).getFirst();
		user2.setProfile(profile2);
		odb.store(user2);
		odb.close();

		odb = open(FILE_NAME);
		user2 = (User) odb.getObjects(User.class).getFirst();
		assertEquals("new operator", user2.getProfile().getName());
		assertEquals(2, odb.getObjects(Profile.class).size());
		odb.close();

	}

	/**
	 * Test updaing a non native attribute with an already existing non native
	 * object - with commit
	 */
	public void testUpdateObjectReference2() throws Exception {
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);

		Function function = new Function("login");
		Profile profile = new Profile("operator", function);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);

		odb.store(user);
		odb.close();

		Profile profile2 = new Profile("new operator", function);

		odb = open(FILE_NAME);
		odb.store(profile2);
		odb.close();

		odb = open(FILE_NAME);
		profile2 = (Profile) odb.getObjects(new CriteriaQuery(Profile.class, Where.equal("name", "new operator"))).getFirst();
		User user2 = (User) odb.getObjects(User.class).getFirst();

		user2.setProfile(profile2);
		odb.store(user2);
		odb.close();

		odb = open(FILE_NAME);
		user2 = (User) odb.getObjects(User.class).getFirst();
		assertEquals("new operator", user2.getProfile().getName());
		assertEquals(2, odb.getObjects(Profile.class).size());
		odb.close();
	}

	/**
	 * Test updating a non native attribute with an already existing non native
	 * object without comit
	 */
	public void testUpdateObjectReference3() throws Exception {
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);

		Function function = new Function("login");
		Profile profile = new Profile("operator", function);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);

		odb.store(user);
		odb.close();

		Profile profile2 = new Profile("new operator", function);

		odb = open(FILE_NAME);
		odb.store(profile2);

		User user2 = (User) odb.getObjects(User.class).getFirst();

		user2.setProfile(profile2);
		odb.store(user2);
		odb.close();

		odb = open(FILE_NAME);
		user2 = (User) odb.getObjects(User.class).getFirst();
		assertEquals("new operator", user2.getProfile().getName());
		assertEquals(2, odb.getObjects(Profile.class).size());
		odb.close();

	}

	/**
	 * Test updating a non native attribute than wall null with an already
	 * existing non native object without comit
	 */
	public void testUpdateObjectReference4() throws Exception {
		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);

		Function function = new Function("login");
		User user = new User("olivier smadja", "olivier@neodatis.com", null);

		odb.store(user);
		odb.close();

		Profile profile2 = new Profile("new operator", function);

		odb = open(FILE_NAME);
		odb.store(profile2);

		User user2 = (User) odb.getObjects(User.class).getFirst();

		user2.setProfile(profile2);
		odb.store(user2);
		odb.close();

		odb = open(FILE_NAME);
		user2 = (User) odb.getObjects(User.class).getFirst();
		assertEquals("new operator", user2.getProfile().getName());
		assertEquals(1, odb.getObjects(Profile.class).size());
		odb.close();
	}

	public void testDirectSave() throws Exception {
		if (!isLocal) {
			return;
		}

		OdbConfiguration.setSaveHistory(true);
		deleteBase("btree.neodatis");
		ODB odb = open("btree.neodatis");

		Function function = new Function("f1");
		odb.store(function);
		for (int i = 0; i < 2; i++) {
			function.setName(function.getName() + function.getName() + function.getName() + function.getName());
			odb.store(function);
		}
		IStorageEngine engine = Dummy.getEngine(odb);
		if (isLocal) {

		}
		Map history = engine.getSession(true).getMetaModel().getHistory();
		List functionHistory = (List) history.get(Function.class.getName());
		InsertHistoryInfo ihi = (InsertHistoryInfo) functionHistory.get(functionHistory.size() - 1);
		println(functionHistory);
		println(ihi);
		ClassInfo ci = engine.getSession(true).getMetaModel().getClassInfo(Function.class.getName(), true);
		println(ci);
		assertEquals(ihi.getOid(), ci.getUncommittedZoneInfo().first);
		assertEquals(ihi.getOid(), ci.getUncommittedZoneInfo().last);
		assertEquals(null, ci.getCommitedZoneInfo().first);
		assertEquals(null, ci.getCommitedZoneInfo().last);
		assertEquals(1, ci.getUncommittedZoneInfo().getNbObjects());
		odb.close();
	}

	public void testUpdateRelation() {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		// first create a function
		Function f = new Function("f1");
		odb.store(f);
		odb.close();

		odb = open(baseName);

		// reloads the function
		Objects<Function> functions = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "f1")));
		Function f1 = functions.getFirst();

		// Create a profile with the loaded function
		Profile profile = new Profile("test", f1);

		odb.store(profile);
		odb.close();

		odb = open(baseName);
		Objects<Profile> profiles = odb.getObjects(Profile.class);
		functions = odb.getObjects(Function.class);

		odb.close();
		deleteBase(baseName);
		assertEquals(1, functions.size());
		assertEquals(1, profiles.size());
	}
}
