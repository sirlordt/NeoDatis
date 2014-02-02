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
package org.neodatis.odb.test.list;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.DisplayUtility;

public class TestFieldComparatorSort extends ODBTest {
	public void test1() {
		String baseName = getBaseName();
		ODB odb = null;
		int k = 10;
		long t1 = System.currentTimeMillis();
		odb = open(baseName);
		for (int i = 0; i < k; i++) {
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i + 1, new Date(t1 - i * 1000), i % 2 == 0));
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i, new Date(t1 - i * 1000), (i + 1) % 2 == 0));
		}
		odb.close();

		odb = open(baseName);
		IQuery q = new CriteriaQuery(User.class).orderByAsc("name,id");
		Objects<org.neodatis.odb.test.list.User> users = odb.getObjects(q);
		odb.close();
		if (k < 11)
			DisplayUtility.display("test1", users);

		User user = users.getFirst();
		assertTrue(user.getName().startsWith("john1"));
		assertEquals(1, user.getId());
	}

	public void test1_2() {
		String baseName = getBaseName();
		ODB odb = null;
		int k = 10;
		long t1 = System.currentTimeMillis();
		odb = open(baseName);
		for (int i = 0; i < k; i++) {
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i + 1, new Date(t1 - i * 1000), i % 2 == 0));
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i, new Date(t1 - i * 1000), (i + 1) % 2 == 0));
		}
		odb.close();

		odb = open(baseName);
		IQuery q = new CriteriaQuery(User.class).orderByDesc("name,id");
		Objects<org.neodatis.odb.test.list.User> users = odb.getObjects(q);
		odb.close();
		if (k < 11)
			DisplayUtility.display("test1", users);

		User user = users.getFirst();
		assertTrue(user.getName().startsWith("john9"));
		assertEquals(10, user.getId());
	}

	public void test2() {
		String baseName = getBaseName();
		ODB odb = null;
		int k = 10;
		long t1 = System.currentTimeMillis();
		String[] fields = { "ok", "id", "name" };

		odb = open(baseName);
		for (int i = 0; i < k; i++) {
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i + 1, new Date(t1 - i * 1000), i % 2 == 0));
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i, new Date(t1 - i * 1000), (i + 1) % 2 == 0));
		}
		odb.close();

		odb = open(baseName);
		IQuery q = new CriteriaQuery(User.class).orderByAsc("ok,id,name");
		Objects<org.neodatis.odb.test.list.User> users = odb.getObjects(q);
		odb.close();

		if (k < 11)
			DisplayUtility.display("test1", users);

		User user = users.getFirst();
		assertTrue(user.getName().startsWith("john1"));
		assertEquals(2, user.getId());
	}

	public void test2_2() {
		String baseName = getBaseName();
		ODB odb = null;
		int k = 10;
		long t1 = System.currentTimeMillis();
		String[] fields = { "ok", "id", "name" };

		odb = open(baseName);
		for (int i = 0; i < k; i++) {
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i + 1, new Date(t1 - i * 1000), i % 2 == 0));
			odb.store(new User("john" + (k - i), "john@ibm.com", "ny 875", k - i, new Date(t1 - i * 1000), (i + 1) % 2 == 0));
		}
		odb.close();

		odb = open(baseName);
		IQuery q = new CriteriaQuery(User.class).orderByDesc("ok,id,name");
		Objects<org.neodatis.odb.test.list.User> users = odb.getObjects(q);
		odb.close();

		if (k < 11)
			DisplayUtility.display("test1", users);

		User user = users.getFirst();
		assertTrue(user.getName().startsWith("john10"));
		assertEquals(11, user.getId());
	}

}