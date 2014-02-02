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
package org.neodatis.odb.test.xml;

import java.util.Calendar;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.arraycollectionmap.PlayerWithArray;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.odb.xml.XMLExporter;
import org.neodatis.odb.xml.XMLImporter;

public class TestXmlImporter extends ODBTest {
	public static final String NAME1 = "test-xml.neodatis";
	public static final String NAME2 = "test-xml-imp.neodatis";
	public static final String XML_NAME = "test-xml.xml";
	private static final int SIZE = 1000;

	public void test0() throws Exception {
		if (!isLocal)
			return;
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

		ODB odb1 = open(NAME1);
		User user2 = new User(null, null, null);
		odb1.store(user2);

		odb1.close();

		odb1 = open(NAME1);
		XMLExporter exporter = new XMLExporter(odb1);
		exporter.export(".", ODBTest.DIRECTORY + XML_NAME);
		long nuser1 = odb1.count(new CriteriaQuery(User.class)).longValue();
		long nprofile1 = odb1.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction1 = odb1.count(new CriteriaQuery(Function.class)).longValue();
		odb1.close();

		ODB odb2 = open(NAME2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + XML_NAME);
		odb2.close();

		odb2 = open(NAME2);
		long nuser2 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile2 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction2 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		assertEquals(nuser1, nuser2);
		assertEquals(nprofile1, nprofile2);
		assertEquals(nfunction1, nfunction2);
		User user = (User) odb2.getObjects(User.class).getFirst();
		assertEquals(null, user.getName());
		assertNull(user.getProfile());
		odb2.close();
		deleteBase(NAME1);
		deleteBase(NAME2);

	}

	/** Test with null array
	 * 
	 * @throws Exception
	 */
	public void test01NullArray() throws Exception {
		if (!isLocal)
			return;
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

		ODB odb1 = open(NAME1);
		PlayerWithArray p  =new PlayerWithArray("Player 1");
		p.setGames(null);
		odb1.store(p);

		odb1.close();

		odb1 = open(NAME1);
		XMLExporter exporter = new XMLExporter(odb1);
		exporter.export(".", ODBTest.DIRECTORY + XML_NAME);
		odb1.close();

		ODB odb2 = open(NAME2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + XML_NAME);
		odb2.close();

		odb2 = open(NAME2);
		long nPlayers = odb2.count(new CriteriaQuery(PlayerWithArray.class)).longValue();
		
		assertEquals(1,nPlayers);
		odb2.close();
		deleteBase(NAME1);
		deleteBase(NAME2);

	}

	public void test01() throws Exception {
		if (!isLocal)
			return;
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

		ODB odb1 = open(NAME1);
		Function f1 = new Function("f1");
		Function f2 = new Function("f2");
		odb1.store(f1);
		odb1.store(f2);
		odb1.close();

		odb1 = open(NAME1);
		XMLExporter exporter = new XMLExporter(odb1);
		exporter.export(".", ODBTest.DIRECTORY + XML_NAME);
		long nfunction1 = odb1.count(new CriteriaQuery(Function.class)).longValue();
		odb1.close();

		ODB odb2 = open(NAME2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + XML_NAME);
		odb2.close();

		odb2 = open(NAME2);
		long nfunction2 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		assertEquals(nfunction1, nfunction2);
		Function f = (Function) odb2.getObjects(Function.class).getFirst();
		assertEquals("f1", f.getName());
		odb2.close();
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);
	}

	public void test1() throws Exception {
		println("start test1");
		if (!isLocal)
			return;
		String basename1 = getBaseName();
		String basename2 = basename1 + "-2";
		String xmlName = DIRECTORY + basename1 + ".xml";

		String currentEncoding = OdbConfiguration.getDatabaseCharacterEncoding();
		OdbConfiguration.setLatinDatabaseCharacterEncoding();

		try {
			ODB odb1 = open(basename1);
			User user1 = new User("olivier andr\u00E9", "olivier@neodatis.com", new Profile("profile", new Function("inner function")));
			odb1.store(user1);
			OID user1Id = odb1.getObjectId(user1);
			OID profile1Id = odb1.getObjectId(user1.getProfile());

			User user2 = new User(null, null, null);
			odb1.store(user2);

			odb1.close();

			odb1 = open(basename1);
			XMLExporter exporter = new XMLExporter(odb1);
			exporter.export(".", xmlName);
			long nuser1 = odb1.count(new CriteriaQuery(User.class)).longValue();
			long nprofile1 = odb1.count(new CriteriaQuery(Profile.class)).longValue();
			long nfunction1 = odb1.count(new CriteriaQuery(Function.class)).longValue();
			Objects obs = odb1.getObjects(User.class);
			odb1.close();

			ODB odb2 = open(basename2);

			XMLImporter importer = new XMLImporter(odb2);
			importer.importFile(".", xmlName);
			odb2.close();

			odb2 = open(basename2);
			long nuser2 = odb2.count(new CriteriaQuery(User.class)).longValue();
			long nprofile2 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
			long nfunction2 = odb2.count(new CriteriaQuery(Function.class)).longValue();

			assertEquals(nuser1, nuser2);
			assertEquals(nprofile1, nprofile2);
			assertEquals(nfunction1, nfunction2);
			User user = (User) odb2.getObjects(User.class).getFirst();
			assertEquals("olivier andr\u00E9", user.getName());
			assertNotNull(user.getProfile());
			assertEquals(user1Id, odb2.getObjectId(user));
			assertEquals(profile1Id, odb2.getObjectId(user.getProfile()));
			odb2.close();

		} finally {
			deleteBase(basename1);
			deleteBase(basename2);
			OdbConfiguration.setDatabaseCharacterEncoding(currentEncoding);
			println("end test1");

		}

	}

	public void test2() throws Exception {
		if (!isLocal)
			return;
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

		ODB odb1 = open(NAME1);
		for (int i = 0; i < SIZE; i++) {
			odb1.store(new User("olivier andr\u00E9" + i, "olivier@neodatis.com " + i, new Profile("profile " + i, new Function(
					"inner function " + i))));
		}
		odb1.close();
		odb1 = open(NAME1);

		XMLExporter exporter = new XMLExporter(odb1);
		exporter.export(".", ODBTest.DIRECTORY + XML_NAME);
		long nuser1 = odb1.count(new CriteriaQuery(User.class)).longValue();
		long nprofile1 = odb1.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction1 = odb1.count(new CriteriaQuery(Function.class)).longValue();
		odb1.close();

		ODB odb2 = open(NAME2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + XML_NAME);
		odb2.close();

		odb2 = open(NAME2);
		long nuser2 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile2 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction2 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		Objects users = odb2.getObjects(User.class);
		User user = (User) users.getFirst();
		assertNotNull(user.getProfile());
		assertEquals(nuser1, nuser2);
		assertEquals(nprofile1, nprofile2);
		assertEquals(nfunction1, nfunction2);
		odb2.close();
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

	}

	public void test3() throws Exception {
		if (!isLocal)
			return;
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

		ODB odb1 = open(NAME1);
		for (int i = 0; i < SIZE; i++) {
			odb1.store(new User("olivier andr\u00E9" + i, "olivier@neodatis.com " + i, new Profile("profile " + i, new Function(
					"inner function " + i))));
		}
		odb1.close();
		odb1 = open(NAME1);

		XMLExporter exporter = new XMLExporter(odb1);
		exporter.export(".", ODBTest.DIRECTORY + XML_NAME);
		long nuser1 = odb1.count(new CriteriaQuery(User.class)).longValue();
		long nprofile1 = odb1.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction1 = odb1.count(new CriteriaQuery(Function.class)).longValue();
		odb1.close();

		ODB odb2 = open(NAME2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + XML_NAME);
		odb2.close();

		odb2 = open(NAME2);
		long nuser2 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile2 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction2 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		Objects users = odb2.getObjects(User.class);
		User user = (User) users.getFirst();
		assertNotNull(user.getProfile());
		assertEquals(nuser1, nuser2);
		assertEquals(nprofile1, nprofile2);
		assertEquals(nfunction1, nfunction2);

		int size = 50;
		for (int i = 0; i < size; i++) {
			User newUser = new User("peter pan " + i, "peter@pan.com " + i, user.getProfile());
			odb2.store(newUser);
		}

		odb2.close();

		odb2 = open(NAME2);
		long nuser3 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile3 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction3 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		assertEquals(nuser3, nuser1 + size);
		assertEquals(nprofile3, nprofile2);
		assertEquals(nfunction3, nfunction2);
		odb2.close();
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

	}

	public void test4() throws Exception {
		if (!isLocal)
			return;
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);

		ODB odb1 = open(NAME1);
		for (int i = 0; i < SIZE; i++) {
			odb1.store(new User("olivier andr\u00E9" + i, "olivier@neodatis.com " + i, new Profile("profile " + i, new Function(
					"inner function " + i))));
		}
		odb1.close();
		odb1 = open(NAME1);

		XMLExporter exporter = new XMLExporter(odb1);
		exporter.export(".", ODBTest.DIRECTORY + XML_NAME);
		long nuser1 = odb1.count(new CriteriaQuery(User.class)).longValue();
		long nprofile1 = odb1.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction1 = odb1.count(new CriteriaQuery(Function.class)).longValue();
		odb1.close();

		ODB odb2 = open(NAME2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + XML_NAME);
		odb2.close();

		odb2 = open(NAME2);
		long nuser2 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile2 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction2 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		Objects users = odb2.getObjects(User.class);
		User user = (User) users.getFirst();
		assertNotNull(user.getProfile());
		assertEquals(nuser1, nuser2);
		assertEquals(nprofile1, nprofile2);
		assertEquals(nfunction1, nfunction2);

		//
		int size = 50;
		for (int i = 0; i < size; i++) {
			User newUser = new User("peter" + i, "peter@pan.com" + i,
					new Profile("flying profile" + i, new Function("Flying function" + i)));
			odb2.store(newUser);
		}

		odb2.close();

		odb2 = open(NAME2);
		long nuser3 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile3 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction3 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		assertEquals(nuser3, nuser1 + size);
		assertEquals(nprofile3, nprofile2 + size);
		assertEquals(nfunction3, nfunction2 + size);
		odb2.close();
		deleteBase(NAME1);
		// deleteBase(NAME2);
		// deleteBase(XML_NAME);

	}

	public void test5() throws Exception {

		if (!isLocal)
			return;
		deleteBase(NAME1);
		deleteBase(NAME2);
		deleteBase(XML_NAME);
		User firstUser = null;
		OID oid = null;
		ODB odb1 = open(NAME1);
		for (int i = 0; i < SIZE; i++) {
			Object object = new User("olivier andr\u00E9" + i, "olivier@neodatis.com " + i, new Profile("profile " + i, new Function(
					"inner function " + i)));
			odb1.store(object);
			if (i == 0) {
				firstUser = (User) object;
				oid = odb1.getObjectId(object);
			}
		}
		odb1.close();
		odb1 = open(NAME1);

		XMLExporter exporter = new XMLExporter(odb1);
		exporter.export(".", ODBTest.DIRECTORY + XML_NAME);
		long nuser1 = odb1.count(new CriteriaQuery(User.class)).longValue();
		long nprofile1 = odb1.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction1 = odb1.count(new CriteriaQuery(Function.class)).longValue();
		odb1.close();

		ODB odb2 = open(NAME2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + XML_NAME);
		odb2.close();

		odb2 = open(NAME2);
		long nuser2 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile2 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction2 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		Objects users = odb2.getObjects(User.class);
		User user = (User) users.getFirst();
		assertNotNull(user.getProfile());
		assertEquals(nuser1, nuser2);
		assertEquals(nprofile1, nprofile2);
		assertEquals(nfunction1, nfunction2);

		//
		int size = 50;
		for (int i = 0; i < size; i++) {
			User newUser = new User("peter" + i, "peter@pan.com" + i,
					new Profile("flying profile" + i, new Function("Flying function" + i)));
			odb2.store(newUser);
		}

		odb2.close();

		odb2 = open(NAME2);
		long nuser3 = odb2.count(new CriteriaQuery(User.class)).longValue();
		long nprofile3 = odb2.count(new CriteriaQuery(Profile.class)).longValue();
		long nfunction3 = odb2.count(new CriteriaQuery(Function.class)).longValue();

		assertEquals(nuser3, nuser1 + size);
		assertEquals(nprofile3, nprofile2 + size);
		assertEquals(nfunction3, nfunction2 + size);
		// println("Looking object with oid "+oid);
		User user10 = (User) odb2.getObjectFromId(oid);
		assertEquals(firstUser.getName(), user10.getName());
		assertEquals(firstUser.getEmail(), user10.getEmail());

		odb2.close();
		deleteBase(NAME1);
		// deleteBase(NAME2);
		// deleteBase(XML_NAME);

	}
	
	public void testWithCalendar() throws Exception {

		if (!isLocal)
			return;

		String baseName = getBaseName();
		String baseName2 = baseName+"2";
		String xml = baseName+".xml";
		
		ODB odb = open(baseName);
		ClassWithCalendar cwc = new ClassWithCalendar("Test", Calendar.getInstance());
		odb.store(cwc);
		odb.close();
		
		
		
		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.export(".", ODBTest.DIRECTORY + xml);
		odb.close();
		
		
		ODB odb2 = open(baseName2);

		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(".", ODBTest.DIRECTORY + xml);
		odb2.close();

		odb2 = open(baseName2);
		long nobjects = odb2.count(new CriteriaQuery(ClassWithCalendar.class)).longValue();
		odb2.close();
		
		assertEquals(1, nobjects);

	}
	
	
}
