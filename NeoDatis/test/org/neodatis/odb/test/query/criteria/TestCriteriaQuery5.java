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
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;

public class TestCriteriaQuery5 extends ODBTest {

	private Date correctDate;
	public static final String BASE_NAME = "criteria-native-object.neodatis";

	public void testCriteriaWithDate() throws Exception {
		deleteBase(BASE_NAME);
		ODB odb = open(BASE_NAME);
		for (int i = 0; i < 10; i++) {
			TestClass tc = new TestClass();
			tc.setInt1(i);
			odb.store(tc);
		}
		odb.close();

		odb = open(BASE_NAME);
		Objects os = odb.getObjects(new CriteriaQuery(TestClass.class, Where.ge("int1", 0)));
		assertEquals(10, os.size());
		int j = 0;
		while (os.hasNext()) {
			TestClass tc = (TestClass) os.next();
			assertEquals(j, tc.getInt1());
			j++;
		}
		odb.close();
	}

	public void testIntLongCriteriaQuery() {
		deleteBase(BASE_NAME);
		ODB odb = open(BASE_NAME);
		ClassWithInt cwi = new ClassWithInt(1, "test");
		odb.store(cwi);
		odb.close();

		odb = open(BASE_NAME);
		Objects os = odb.getObjects(new CriteriaQuery(ClassWithInt.class, Where.equal("i", (long) 1)));
		assertEquals(1, os.size());
		odb.close();

	}

	public void testLongIntCriteriaQuery() {
		deleteBase(BASE_NAME);
		ODB odb = open(BASE_NAME);
		ClassWithLong cwl = new ClassWithLong(1L, "test");
		odb.store(cwl);
		odb.close();

		odb = open(BASE_NAME);
		Objects os = odb.getObjects(new CriteriaQuery(ClassWithLong.class, Where.equal("i", (int) 1)));
		assertEquals(1, os.size());
		odb.close();

	}

	public void testLongIntCriteriaQueryGt() {
		deleteBase(BASE_NAME);
		ODB odb = open(BASE_NAME);
		ClassWithLong cwl = new ClassWithLong(1L, "test");
		odb.store(cwl);
		odb.close();

		odb = open(BASE_NAME);
		Objects os = odb.getObjects(new CriteriaQuery(ClassWithLong.class, Where.ge("i", (int) 1)));
		assertEquals(1, os.size());
		os = odb.getObjects(new CriteriaQuery(ClassWithLong.class, Where.gt("i", (int) 1)));
		assertEquals(0, os.size());

		odb.close();

	}
}
