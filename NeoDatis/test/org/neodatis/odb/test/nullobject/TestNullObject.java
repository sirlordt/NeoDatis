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
package org.neodatis.odb.test.nullobject;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.User;

public class TestNullObject extends ODBTest {

	public void test1() throws Exception {
		deleteBase("null.neodatis");
		ODB odb = open("null.neodatis");
		User user1 = new User("oli", "oli@sdsadf", null);
		User user2 = new User("karine", "karine@sdsadf", null);
		User user3 = new User(null, null, null);

		odb.store(user1);
		odb.store(user2);
		odb.store(user3);
		odb.close();

		odb = open("null.neodatis");
		Objects l = odb.getObjects(User.class, true);

		assertEquals(3, l.size());

		user1 = (User) l.next();
		assertEquals("oli", user1.getName());
		assertEquals("oli@sdsadf", user1.getEmail());
		assertEquals(null, user1.getProfile());

		user2 = (User) l.next();
		assertEquals("karine", user2.getName());
		assertEquals("karine@sdsadf", user2.getEmail());
		assertEquals(null, user2.getProfile());

		user3 = (User) l.next();
		assertEquals(null, user3.getName());
		assertEquals(null, user3.getEmail());
		assertEquals(null, user3.getProfile());

		odb.close();

		deleteBase("null.neodatis");
	}

	/**
	 * Test generic attribute of type Object receving a native type
	 * 
	 * @throws Exception
	 */
	public void test2() throws Exception {
		deleteBase("nullo");
		GenericClass gc = new GenericClass(null);

		ODB odb = open("nullo");
		odb.store(gc);
		odb.close();

		odb = open("nullo");
		Objects objects = odb.getObjects(GenericClass.class);
		GenericClass gc2 = (GenericClass) objects.getFirst();
		gc2.setObject("Ola");
		odb.store(gc2);
		odb.close();

		odb = open("nullo");
		objects = odb.getObjects(GenericClass.class);
		assertEquals(1, objects.size());
		GenericClass gc3 = (GenericClass) objects.getFirst();
		assertEquals("Ola", gc3.getObject());
		odb.close();

	}

	public void test21() throws Exception {
		deleteBase("nullo");
		GenericClass gc = new GenericClass(null);

		ODB odb = open("nullo");
		odb.store(gc);
		odb.close();

		odb = open("nullo");
		Objects objects = odb.getObjects(GenericClass.class);
		GenericClass gc2 = (GenericClass) objects.getFirst();
		Long[] longs = { new Long(1), new Long(2) };
		gc2.setObjects(longs);
		odb.store(gc2);
		odb.close();

		odb = open("nullo");
		objects = odb.getObjects(GenericClass.class);
		assertEquals(1, objects.size());
		GenericClass gc3 = (GenericClass) objects.getFirst();
		Long[] longs2 = (Long[]) gc3.getObjects();
		assertEquals(2, longs2.length);
		assertEquals(new Long(1), longs2[0]);
		assertEquals(new Long(2), longs2[1]);
		odb.close();

	}

	public void test22() throws Exception {
		deleteBase("nullo");
		GenericClass gc = new GenericClass(null);

		ODB odb = open("nullo");
		odb.store(gc);
		odb.close();

		odb = open("nullo");
		Objects objects = odb.getObjects(GenericClass.class);
		GenericClass gc2 = (GenericClass) objects.getFirst();
		gc2.getObjects()[0] = new Long(1);
		gc2.getObjects()[1] = new Long(2);
		odb.store(gc2);
		odb.close();

		odb = open("nullo");
		objects = odb.getObjects(GenericClass.class);
		assertEquals(1, objects.size());
		GenericClass gc3 = (GenericClass) objects.getFirst();
		Object[] longs2 = (Object[]) gc3.getObjects();
		assertEquals(10, longs2.length);
		assertEquals(new Long(1), longs2[0]);
		assertEquals(new Long(2), longs2[1]);
		odb.close();

	}

	public void test23() throws Exception {
		deleteBase("nullo");
		GenericClass gc = new GenericClass(null);
		gc.getObjects()[0] = new Function("f1");
		ODB odb = open("nullo");
		odb.store(gc);
		odb.close();

		odb = open("nullo");
		Objects objects = odb.getObjects(GenericClass.class);
		GenericClass gc2 = (GenericClass) objects.getFirst();
		gc2.getObjects()[0] = new Long(1);
		gc2.getObjects()[1] = new Long(2);
		odb.store(gc2);
		odb.close();

		odb = open("nullo");
		objects = odb.getObjects(GenericClass.class);
		assertEquals(1, objects.size());
		GenericClass gc3 = (GenericClass) objects.getFirst();
		Object[] longs2 = (Object[]) gc3.getObjects();
		assertEquals(10, longs2.length);
		assertEquals(new Long(1), longs2[0]);
		assertEquals(new Long(2), longs2[1]);
		odb.close();

	}

	public void test3() throws Exception {
		deleteBase("nullo");
		GenericClass gc = new GenericClass(null);
		String[] strings = { "OBJ1", "obj2" };
		gc.setObjects(strings);

		ODB odb = open("nullo");
		odb.store(gc);
		odb.close();

		odb = open("nullo");
		Objects objects = odb.getObjects(GenericClass.class);
		GenericClass gc2 = (GenericClass) objects.getFirst();
		gc2.setObject("Ola");
		odb.store(gc2);
		odb.close();

	}

	public void test4() throws Exception {
		deleteBase("nullo");
		GenericClass gc = new GenericClass(null);
		String[] strings = { "OBJ1", "obj2" };
		gc.setObject(strings);

		ODB odb = open("nullo");
		odb.store(gc);
		odb.close();

		odb = open("nullo");
		Objects objects = odb.getObjects(GenericClass.class);
		GenericClass gc2 = (GenericClass) objects.getFirst();
		gc2.setObject("Ola");
		odb.store(gc2);
		odb.close();

	}

	public void test5() throws Exception {
		deleteBase("nullo");
		Function f = new Function("a simple value");

		ODB odb = open("nullo");
		odb.store(f);
		odb.close();

		odb = open("nullo");
		Objects objects = odb.getObjects(Function.class);
		Function f2 = (Function) objects.getFirst();
		f2.setName(null);
		odb.store(f2);
		odb.close();

		odb = open("nullo");
		objects = odb.getObjects(Function.class);
		f2 = (Function) objects.getFirst();

		odb.close();
		assertEquals(null, f2.getName());

	}
}
