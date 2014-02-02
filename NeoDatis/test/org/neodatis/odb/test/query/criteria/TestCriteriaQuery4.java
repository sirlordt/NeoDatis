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
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;
import org.neodatis.tool.wrappers.OdbTime;

public class TestCriteriaQuery4 extends ODBTest {

	private Date correctDate;
	public static final String BASE_NAME = "soda-native-object.neodatis";

	public void testSodaWithDate() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery query = new CriteriaQuery(TestClass.class, Where.and().add(Where.equal("string1", "test class with values")).add(
				Where.equal("date1", new Date(correctDate.getTime()))));

		Objects l = odb.getObjects(query);
		// assertEquals(1,l.size());

		query = new CriteriaQuery(TestClass.class, Where.and().add(Where.equal("string1", "test class with values")).add(
				Where.ge("date1", new Date(correctDate.getTime()))));
		l = odb.getObjects(query);
		if (l.size() != 1) {
			query = new CriteriaQuery(TestClass.class, Where.equal("string1", "test class with null BigDecimal"));
			Objects l2 = odb.getObjects(query);
			println(l2);
			println(correctDate.getTime());
			l = odb.getObjects(query);
		}

		assertEquals(1, l.size());

		odb.close();
	}

	public void testSodaWithBoolean() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery query = new CriteriaQuery(TestClass.class, Where.equal("boolean1", true));
		Objects l = odb.getObjects(query);
		assertTrue(l.size() > 1);

		query = new CriteriaQuery(TestClass.class, Where.equal("boolean1", Boolean.TRUE));
		l = odb.getObjects(query);
		assertTrue(l.size() > 1);

		odb.close();
	}

	public void testSodaWithInt() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery query = new CriteriaQuery(TestClass.class, Where.equal("int1", 190));
		Objects l = odb.getObjects(query);
		assertEquals(1, l.size());

		query = new CriteriaQuery(TestClass.class, Where.gt("int1", 189));
		l = odb.getObjects(query);
		assertTrue(l.size() >= 1);

		query = new CriteriaQuery(TestClass.class, Where.lt("int1", 191));
		l = odb.getObjects(query);
		assertTrue(l.size() >= 1);

		odb.close();
	}

	public void testSodaWithDouble() throws Exception {
		ODB odb = open(BASE_NAME);

		CriteriaQuery query = new CriteriaQuery(TestClass.class, Where.equal("double1", 190.99));
		Objects l = odb.getObjects(query);
		assertEquals(1, l.size());

		query = new CriteriaQuery(TestClass.class, Where.gt("double1", (double) 189));
		l = odb.getObjects(query);
		assertTrue(l.size() >= 1);

		query = new CriteriaQuery(TestClass.class, Where.lt("double1", (double) 191));
		l = odb.getObjects(query);
		assertTrue(l.size() >= 1);

		odb.close();
	}

	public void testIsNull() throws Exception {
		ODB odb = null;

		try {
			odb = open(BASE_NAME);
			CriteriaQuery query = new CriteriaQuery(TestClass.class, Where.isNull("bigDecimal1"));
			Objects l = odb.getObjects(query);
			assertEquals(2, l.size());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void testIsNotNull() throws Exception {
		ODB odb = null;

		try {
			odb = open(BASE_NAME);
			CriteriaQuery query = new CriteriaQuery(TestClass.class, Where.isNotNull("bigDecimal1"));
			Objects l = odb.getObjects(query);
			assertEquals(51, l.size());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void setUp() throws Exception {
		super.setUp();
		deleteBase(BASE_NAME);
		ODB odb = open(BASE_NAME);
		long start = OdbTime.getCurrentTimeInMs();
		int size = 50;
		for (int i = 0; i < size; i++) {
			TestClass tc = new TestClass();
			tc.setBigDecimal1(new BigDecimal(i));
			tc.setBoolean1(i % 3 == 0);
			tc.setChar1((char) (i % 5));
			tc.setDate1(new Date(1000 + start + i));
			tc.setDouble1(new Double(((double) (i % 10)) / size));
			tc.setInt1(size - i);
			tc.setString1("test class " + i);

			odb.store(tc);
		}
		TestClass testClass = new TestClass();
		testClass.setBigDecimal1(new BigDecimal("190.95"));
		testClass.setBoolean1(true);
		testClass.setChar1('s');
		correctDate = new Date();
		testClass.setDate1(correctDate);
		testClass.setDouble1(new Double(190.99));
		testClass.setInt1(190);
		testClass.setString1("test class with values");
		odb.store(testClass);

		TestClass testClass2 = new TestClass();
		testClass2.setBigDecimal1(null);
		testClass2.setBoolean1(true);
		testClass2.setChar1('s');
		correctDate = new Date();
		testClass2.setDate1(correctDate);
		testClass2.setDouble1(new Double(191.99));
		testClass2.setInt1(1901);
		testClass2.setString1("test class with null BigDecimal");
		odb.store(testClass2);

		TestClass testClass3 = new TestClass();
		odb.store(testClass3);
		odb.close();
	}

	public void tearDown() throws Exception {
		deleteBase(BASE_NAME);
	}

}
