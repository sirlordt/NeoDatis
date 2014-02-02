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
package org.neodatis.odb.test.query.criteria;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestCriteriaQuery extends ODBTest {

	public void test1() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.or().add(Where.equal("name", "function 2")).add(
				Where.equal("name", "function 3")));

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(2, l.size());
		Function f = (Function) l.getFirst();
		assertEquals("function 2", f.getName());
		odb.close();

	}

	public void test2() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.not(Where.equal("name", "function 2")));

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(49, l.size());
		Function f = (Function) l.getFirst();
		assertEquals("function 0", f.getName());
		odb.close();

	}

	public void test3() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.not(Where.or().add(Where.equal("name", "function 2")).add(
				Where.equal("name", "function 3"))));

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(48, l.size());
		Function f = (Function) l.getFirst();
		assertEquals("function 0", f.getName());
		odb.close();

	}

	public void test4Sort() throws Exception {
		int d = OdbConfiguration.getDefaultIndexBTreeDegree();
		try {
			OdbConfiguration.setDefaultIndexBTreeDegree(40);
			ODB odb = open("criteria.neodatis");

			CriteriaQuery aq = new CriteriaQuery(Function.class, Where.not(Where.or().add(Where.equal("name", "function 2")).add(
					Where.equal("name", "function 3"))));
			aq.orderByDesc("name");
			// aq.orderByAsc("name");

			Objects l = odb.getObjects(aq, true, -1, -1);
			assertEquals(48, l.size());
			Function f = (Function) l.getFirst();
			assertEquals("function 9", f.getName());
			odb.close();
		} finally {
			OdbConfiguration.setDefaultIndexBTreeDegree(d);
		}

	}

	public void testDate1() throws Exception {
		ODB odb = open("criteria.neodatis");
		MyDates myDates = new MyDates();
		Date d1 = new Date();
		Thread.sleep(100);
		Date d2 = new Date();
		Thread.sleep(100);
		Date d3 = new Date();
		myDates.setDate1(d1);
		myDates.setDate2(d3);
		myDates.setI(5);
		odb.store(myDates);
		odb.close();

		odb = open("criteria.neodatis");

		IQuery query = new CriteriaQuery(MyDates.class, Where.and().add(Where.le("date1", d2)).add(Where.ge("date2", d2)).add(
				Where.equal("i", 5)));

		Objects objects = odb.getObjects(query);
		assertEquals(1, objects.size());
		odb.close();

	}

	public void testIequal() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.iequal("name", "FuNcTiOn 1"));
		aq.orderByDesc("name");

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(1, l.size());
		odb.close();
	}

	public void testEqual2() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.equal("name", "FuNcTiOn 1"));
		aq.orderByDesc("name");

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(0, l.size());
		odb.close();
	}

	public void testILike() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.ilike("name", "FUNc%"));
		aq.orderByDesc("name");

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(50, l.size());
		odb.close();
	}

	public void testLike() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.like("name", "func%"));
		aq.orderByDesc("name");

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(50, l.size());
		odb.close();
	}

	public void testLike2() throws Exception {
		ODB odb = open("criteria.neodatis");

		CriteriaQuery aq = new CriteriaQuery(Function.class, Where.like("name", "FuNc%"));
		aq.orderByDesc("name");

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(0, l.size());
		odb.close();
	}
	
	
	public void setUp() throws Exception {
		super.setUp();
		deleteBase("criteria.neodatis");
		ODB odb = open("criteria.neodatis");
		for (int i = 0; i < 50; i++) {
			odb.store(new Function("function " + i));
		}
		odb.close();
	}

	public void tearDown() throws Exception {
		deleteBase("criteria.neodatis");
	}

}
