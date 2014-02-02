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
package org.neodatis.odb.test.cache;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestCache extends ODBTest {

	public static int NB_OBJECTS = 300;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
		Thread.sleep(100);
		// Configuration.setUseModifiedClass(true);
		deleteBase("cache.neodatis");
		ODB odb = open("cache.neodatis");
		for (int i = 0; i < NB_OBJECTS; i++) {
			odb.store(new Function("function " + (i + i)));
			odb.store(new User("olivier " + i, "olivier@neodatis.com " + i,
					new Profile("profile " + i, new Function("inner function " + i))));
		}
		odb.close();
	}

	public void test1() throws Exception {
		ODB odb = open("cache.neodatis");

		Objects l = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "function 10")));
		assertFalse(l.isEmpty());
		// Cache must have only one object : The function
		assertEquals(l.size(), Dummy.getEngine(odb).getSession(true).getCache().getNumberOfObjects());
		odb.close();
	}

	public void test2() throws Exception {
		ODB odb = open("cache.neodatis");

		Objects l = odb.getObjects(new CriteriaQuery(User.class, Where.equal("name", "olivier 10")));
		assertFalse(l.isEmpty());
		// Cache must have 3 times the number of Users in list l (check the
		// setup method to understand this)
		assertEquals(l.size() * 3, Dummy.getEngine(odb).getSession(true).getCache().getNumberOfObjects());
		odb.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		deleteBase("cache.neodatis");

	}

}
