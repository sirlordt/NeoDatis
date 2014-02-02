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
package org.neodatis.odb.test.insert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.ObjectWithDates;
import org.neodatis.odb.test.vo.attribute.TestClass;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestInsert extends ODBTest {
	public void testCompositeCollection2DifferentObjects() throws Exception {
		String baseName = getBaseName();		
		ODB odb = open(baseName);

		int nbUsers = odb.getObjects(User.class, true).size();
		int nbProfiles = odb.getObjects(Profile.class, true).size();
		int nbFunctions = odb.getObjects(Function.class, true).size();

		Function login = new Function("login");
		Function logout = new Function("logout");
		Function disconnect = new Function("disconnect");
		List list = new ArrayList();
		list.add(login);
		list.add(logout);

		List list2 = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile1 = new Profile("operator 1", list);
		Profile profile2 = new Profile("operator 2", list2);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile1);
		User userB = new User("Aása Galvão Smadja", "aisa@neodatis.com", profile2);

		odb.store(user);
		odb.store(userB);
		odb.commit();

		Objects functions = odb.getObjects(Function.class, true);
		Objects profiles = odb.getObjects(Profile.class, true);
		Objects users = odb.getObjects(User.class, true);

		odb.close();
		// assertEquals(nbUsers+2,users.size());
		User user2 = (User) users.getFirst();

		assertEquals(user.toString(), user2.toString());
		assertEquals(nbProfiles + 2, profiles.size());
		assertEquals(nbFunctions + 2, functions.size());

		

	}

	public void testCompositeCollection1() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		Function login = new Function("login");

		List list = new ArrayList();
		list.add(login);

		Profile profile1 = new Profile("operator 1", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile1);

		odb.store(user);
		odb.close();

		odb = open(baseName);

		Objects users = odb.getObjects(User.class, true);
		odb.close();
		// assertEquals(nbUsers+2,users.size());
		User user2 = (User) users.getFirst();

		assertEquals(user.toString(), user2.toString());

	}

	public void test1() throws Exception {
		String baseName = getBaseName();
		// LogUtil.allOn(true);
		ODB odb = open(baseName);

		// LogUtil.objectWriterOn(true);
		Function login = new Function("login");

		List list = new ArrayList();
		list.add(login);

		Profile profile1 = new Profile("operator 1", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile1);

		odb.store(user);
		odb.close();

		odb = open(baseName);

		Objects users = odb.getObjects(User.class, true);

		// assertEquals(nbUsers+2,users.size());
		User user2 = (User) users.getFirst();
		odb.close();

		assertEquals(user.toString(), user2.toString());

	}
	
	public void testProfile() throws Exception {
		String baseName = getBaseName();
		// LogUtil.allOn(true);
		ODB odb = open(baseName);

		// LogUtil.objectWriterOn(true);
		Function login = new Function("login");

		List list = new ArrayList();
		list.add(login);

		Profile profile1 = new Profile("operator 1", list);

		odb.store(profile1);
		odb.close();

		odb = open(baseName);

		Objects profiles = odb.getObjects(Profile.class, true);

		// assertEquals(nbUsers+2,users.size());
		Profile p = (Profile) profiles.getFirst();
		odb.close();

		assertEquals(profile1.toString(), p.toString());

	}

	public void testCompositeCollection2() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		int nbUsers = odb.getObjects(User.class, true).size();
		int nbProfiles = odb.getObjects(Profile.class, true).size();
		int nbFunctions = odb.getObjects(Function.class, true).size();

		Function login = new Function("login");
		Function logout = new Function("logout");

		List list = new ArrayList();
		list.add(login);
		list.add(logout);

		Profile profile1 = new Profile("operator 1", list);
		Profile profile2 = new Profile("operator 2", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile1);
		User userB = new User("Aása Galvão Smadja", "aisa@neodatis.com", profile2);

		odb.store(user);
		odb.store(userB);
		odb.close();

		odb = open(baseName);

		Objects users = odb.getObjects(User.class, true);
		Objects profiles = odb.getObjects(Profile.class, true);
		Objects functions = odb.getObjects(Function.class, true);

		// assertEquals(nbUsers+2,users.size());
		User user2 = (User) users.getFirst();

		assertEquals(user.toString(), user2.toString());
		assertEquals(nbProfiles + 2, profiles.size());
		assertEquals(nbFunctions + 2, functions.size());
		odb.close();

	}

	public void testCompositeCollection3() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		// Configuration.addLogId("ObjectWriter");
		// Configuration.addLogId("ObjectReader");
		// Configuration.addLogId("FileSystemInterface");

		int nbUsers = odb.getObjects(User.class, true).size();
		int nbProfiles = odb.getObjects(Profile.class, true).size();
		int nbFunctions = odb.getObjects(Function.class, true).size();

		Function login = new Function("login");
		Function logout = new Function("logout");

		List list = new ArrayList();
		list.add(login);
		list.add(logout);

		Profile profile1 = new Profile("operator 1", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile1);
		User userB = new User("Aísa Galvão Smadja", "aisa@neodatis.com", profile1);

		odb.store(user);
		odb.store(userB);
		odb.close();

		odb = open(baseName);

		Objects users = odb.getObjects(User.class, true);
		Objects profiles = odb.getObjects(Profile.class, true);
		Objects functions = odb.getObjects(Function.class, true);

		// assertEquals(nbUsers+2,users.size());
		User user2 = (User) users.getFirst();

		assertEquals(user.toString(), user2.toString());
		assertEquals(nbProfiles + 1, profiles.size());
		assertEquals(nbFunctions + 2, functions.size());
		odb.close();
		

	}

	public void testCompositeCollection4() throws Exception {
		String baseName = getBaseName();		
		ODB odb = open(baseName);

		int nbUsers = odb.getObjects(User.class, true).size();
		int nbProfiles = odb.getObjects(Profile.class, true).size();
		int nbFunctions = odb.getObjects(Function.class, true).size();

		Function login = new Function("login");
		Function logout = new Function("logout");

		List list = new ArrayList();
		list.add(login);
		list.add(logout);

		Profile profile1 = new Profile("operator 1", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile1);
		User userB = new User("Aísa Galvão Smadja", "aisa@neodatis.com", profile1);

		odb.store(user);
		odb.store(userB);
		odb.commit();

		Objects users = odb.getObjects(User.class, true);
		Objects profiles = odb.getObjects(Profile.class, true);
		Objects functions = odb.getObjects(Function.class, true);
		odb.close();
		// assertEquals(nbUsers+2,users.size());
		User user2 = (User) users.getFirst();

		assertEquals(user.toString(), user2.toString());
		assertEquals(nbProfiles + 1, profiles.size());
		assertEquals(nbFunctions + 2, functions.size());
		// 

	}

	public void testSimple() throws Exception {

		String baseName = getBaseName();
		ODB odb = open(baseName);

		int nbFunctions = odb.getObjects(Function.class, true).size();

		Function login = new Function("login");
		Function logout = new Function("logout");

		odb.store(login);
		odb.store(logout);
		odb.close();

		odb = open(baseName);
		Objects functions = odb.getObjects(Function.class, true);
		Function f1 = (Function) functions.getFirst();
		f1.setName("login1");

		odb.store(f1);

		odb.close();

		odb = open(baseName);

		functions = odb.getObjects(Function.class, true);
		odb.close();
		assertEquals(2, functions.size());
		assertEquals("login1", ((Function) functions.getFirst()).getName());
		
	}

	public void testBufferSize() throws Exception {
		String baseName = getBaseName();
		int size = OdbConfiguration.getDefaultBufferSizeForData();
		OdbConfiguration.setDefaultBufferSizeForData(5);
		
		ODB odb = open(baseName);

		StringBuffer b = new StringBuffer();

		for (int i = 0; i < 1000; i++) {
			b.append("login - login ");
		}

		Function login = new Function(b.toString());
		Profile profile1 = new Profile("operator 1", login);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile1);

		odb.store(user);
		odb.commit();

		Objects users = odb.getObjects(User.class, true);
		Objects profiles = odb.getObjects(Profile.class, true);
		Objects functions = odb.getObjects(Function.class, true);
		odb.close();
		// assertEquals(nbUsers+2,users.size());
		User user2 = (User) users.getFirst();

		assertEquals(user.toString(), user2.toString());
		assertEquals(b.toString(), user2.getProfile().getFunctions().iterator().next().toString());
		
		OdbConfiguration.setDefaultBufferSizeForData(size);

	}

	public void testDatePersistence() throws Exception {
		ODB odb = null;
		String baseName = getBaseName();
		try {
			odb = open(baseName);
			TestClass tc1 = new TestClass();
			tc1.setDate1(new Date());
			long t1 = tc1.getDate1().getTime();
			odb.store(tc1);
			odb.close();

			odb = open(baseName);
			Objects l = odb.getObjects(TestClass.class);
			assertEquals(1, l.size());
			TestClass tc2 = (TestClass) l.getFirst();
			assertEquals(t1, tc2.getDate1().getTime());
			assertEquals(tc1.getDate1(), tc2.getDate1());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
		
	}

	public void testStringPersistence() throws Exception {
		String baseName = getBaseName();
		ODB odb = null;
		try {
			odb = open(baseName);
			TestClass tc1 = new TestClass();
			tc1.setString1("");
			odb.store(tc1);
			odb.close();

			odb = open(baseName);
			Objects l = odb.getObjects(TestClass.class);
			assertEquals(1, l.size());
			TestClass tc2 = (TestClass) l.getFirst();
			assertEquals("", tc2.getString1());
			assertEquals(null, tc2.getBigDecimal1());
			assertEquals(null, tc2.getDouble1());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void test6() throws Exception {
		String baseName = getBaseName();	
		ODB odb = open(baseName);

		Function login = new Function("login");
		Function logout = new Function("logout");
		odb.store(login);
		odb.store(logout);

		odb.close();

		odb = open(baseName);
		Function login2 = new Function("login2");
		Function logout2 = new Function("logout2");
		odb.store(login2);
		odb.store(logout2);
		// select without committing
		Objects l = odb.getObjects(Function.class, true);
		assertEquals(4, l.size());
		// println(l);

		odb.close();

		odb = open(baseName);
		l = odb.getObjects(Function.class, true);
		assertEquals(4, l.size());
		// println(l);
		odb.close();
	}

	public void test7() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		Function login = new Function("login");
		Function logout = new Function("logout");
		odb.store(login);
		odb.store(logout);

		odb.commit();
		Function input = new Function("input");
		odb.store(input);

		odb.close();

		odb = open(baseName);
		Objects l = odb.getObjects(Function.class, true);
		assertEquals(3, l.size());
		// println(l);

		odb.close();
	}

	/**
	 * Test with java util Date and java sql Date
	 * 
	 */
	public void test8() {
		String baseName = getBaseName();
		println(baseName);

		ODB odb = null;

		Date utilDate = new Date();
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime() + 10000);
		Timestamp timestamp = new Timestamp(utilDate.getTime() + 20000);

		try {
			odb = open(baseName);
			ObjectWithDates o = new ObjectWithDates("object1", utilDate, sqlDate, timestamp);
			odb.store(o);
			odb.close();

			odb = open(baseName);
			Objects<ObjectWithDates> dates = odb.getObjects(ObjectWithDates.class);

			ObjectWithDates o2 = dates.getFirst();

			println(o2.getName());
			println(o2.getJavaUtilDate());
			println(o2.getJavaSqlDte());
			println(o2.getTimestamp());

			assertEquals("object1", o2.getName());
			assertEquals(utilDate, o2.getJavaUtilDate());
			assertEquals(sqlDate, o2.getJavaSqlDte());
			assertEquals(timestamp, o2.getTimestamp());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}
}
