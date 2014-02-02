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
package org.neodatis.odb.test.performance;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.wrappers.OdbTime;

public class TestLazy extends ODBTest {
	public static final int SIZE = 4000;

	/** Test the timeof lazy get */
	public void te1st1() throws Exception {
		String baseName = getBaseName();
		// println("Start inserting " + SIZE + " objects");
		long startinsert = OdbTime.getCurrentTimeInMs();
		ODB odb = open(baseName);
		for (int i = 0; i < SIZE; i++) {
			odb.store(getInstance());
		}
		odb.close();
		long endinsert = OdbTime.getCurrentTimeInMs();
		// println("End inserting " + SIZE + " objects  - " +
		// (endinsert-startinsert) + " ms");
		// println("totalObjects = "+ odb.count(User.class));

		odb = open(baseName);
		long start1 = OdbTime.getCurrentTimeInMs();
		Objects lazyList = odb.getObjects(User.class, false);
		long end1 = OdbTime.getCurrentTimeInMs();
		long startget1 = OdbTime.getCurrentTimeInMs();
		while (lazyList.hasNext()) {
			// t1 = OdbTime.getCurrentTimeInMs();
			lazyList.next();
			// t2 = OdbTime.getCurrentTimeInMs();

			// println(t2-t1);
		}
		long endget1 = OdbTime.getCurrentTimeInMs();
		assertEquals(odb.count(new CriteriaQuery(User.class)).longValue(), lazyList.size());
		odb.close();
		/*
		 * odb = open(FILENAME); long start2 = OdbTime.getCurrentTimeInMs();
		 * List list = odb.getObjects (User.class,true); long end2 =
		 * OdbTime.getCurrentTimeInMs(); long startget2 =
		 * OdbTime.getCurrentTimeInMs(); for(int i=0;i<lazyList.size();i++){
		 * list.get(i); } long endget2 = OdbTime.getCurrentTimeInMs();
		 * 
		 * odb.close();
		 * 
		 * assertEquals(odb.count(User.class),list.size());
		 */
		long t01 = end1 - start1;
		long tget1 = endget1 - startget1;
		// long t2 = end2-start2;
		// long tget2 = endget2-startget2;
		// println("t1(lazy)="+t1 + " - " +tget1+ "      t2(memory)="+t2 +" - "
		// + tget2);
		// println("t1(lazy)="+t1 + " - " +tget1);
		// assertTrue(t1<t2);
		// println(endinsert-startinsert);
		boolean c = (isLocal ? 501 : 15000) > tget1;
		println("Time for " + SIZE + " lazy gets : " + (tget1));
		if (!c) {
			println("Time for " + SIZE + " lazy gets : " + (tget1));
		}
		deleteBase(baseName);
		if (testPerformance && !c) {
			fail("Time for " + SIZE + " lazy gets : " + (tget1));
		}
	}

	private Object getInstance() {
		Function login = new Function("login");
		Function logout = new Function("logout");
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);
		return user;
	}

}
