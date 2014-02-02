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

import java.math.BigDecimal;
import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;
import org.neodatis.tool.wrappers.OdbTime;

public class TestCriteriaQuery2 extends ODBTest {

	public static final String BASE_NAME = "sort-query.neodatis";

	public void test1() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery aq = new CriteriaQuery(TestClass.class, Where.or().add(Where.equal("string1", "test class 1")).add(
				Where.equal("string1", "test class 3")));
		aq.orderByAsc("string1");

		Objects l = odb.getObjects(aq, true, -1, -1);

		assertEquals(2, l.size());
		TestClass testClass = (TestClass) l.getFirst();
		assertEquals("test class 1", testClass.getString1());
		odb.close();

	}

	public void test2() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery aq = new CriteriaQuery(TestClass.class, Where.not(Where.equal("string1", "test class 2")));

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(49, l.size());
		TestClass testClass = (TestClass) l.getFirst();
		assertEquals("test class 0", testClass.getString1());
		odb.close();

	}

	public void test3() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery aq = new CriteriaQuery(TestClass.class, Where.not(Where.or().add(Where.equal("string1", "test class 0")).add(
				Where.equal("bigDecimal1", new BigDecimal("5")))));

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(48, l.size());
		TestClass testClass = (TestClass) l.getFirst();
		assertEquals("test class 1", testClass.getString1());
		odb.close();

	}

	public void test4Sort() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery aq = new CriteriaQuery(TestClass.class, Where.not(Where.or().add(Where.equal("string1", "test class 2")).add(
				Where.equal("string1", "test class 3"))));
		aq.orderByDesc("double1,int1");

		Objects l = odb.getObjects(aq, true, -1, -1);
		// println(l);
		assertEquals(48, l.size());
		TestClass testClass = (TestClass) l.getFirst();
		assertEquals("test class 9", testClass.getString1());
		odb.close();

	}

	public void test5Sort() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery aq = new CriteriaQuery(TestClass.class, Where.not(Where.or().add(Where.equal("string1", "test class 2")).add(
				Where.equal("string1", "test class 3"))));
		// aq.orderByDesc("double1,boolean1,int1");
		aq.orderByDesc("double1,int1");

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(48, l.size());
		TestClass testClass = (TestClass) l.getFirst();
		assertEquals("test class 9", testClass.getString1());
		odb.close();

	}

	public void test6Sort() throws Exception {
		ODB odb = open(BASE_NAME);

		ICriterion c = Where.or().add(Where.equal("string1", "test class 2")).add(Where.equal("string1", "test class 3")).add(
				Where.equal("string1", "test class 4")).add(Where.equal("string1", "test class 5"));
		CriteriaQuery aq = new CriteriaQuery(TestClass.class, c);
		aq.orderByDesc("boolean1,int1");

		Objects l = odb.getObjects(aq, true, -1, -1);
		assertEquals(4, l.size());
		TestClass testClass = (TestClass) l.getFirst();
		assertEquals("test class 3", testClass.getString1());
		odb.close();

	}

	public void setUp() throws Exception {
		super.setUp();
		deleteBase(BASE_NAME);
		ODB odb = open(BASE_NAME);
		long start = OdbTime.getCurrentTimeInMs();
		int size = 50;
		for (int i = 0; i < size; i++) {
			TestClass testClass = new TestClass();
			testClass.setBigDecimal1(new BigDecimal(i));
			testClass.setBoolean1(i % 3 == 0);
			testClass.setChar1((char) (i % 5));
			testClass.setDate1(new Date(start + i));
			testClass.setDouble1(new Double(((double) (i % 10)) / size));
			testClass.setInt1(size - i);
			testClass.setString1("test class " + i);

			odb.store(testClass);
			// println(testClass.getDouble1() + " | " + testClass.getString1() +
			// " | " + testClass.getInt1());
		}
		odb.close();
	}

	public void tearDown() throws Exception {
		deleteBase(BASE_NAME);
	}

}
