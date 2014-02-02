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
package org.neodatis.odb.test.intropector;

import java.lang.reflect.Array;

import org.neodatis.odb.test.ODBTest;

public class TestArray extends ODBTest {
	public void test1() {
		String[] array = { "Ola", "chico" };

		assertEquals(true, array.getClass().isArray());
		assertEquals("[Ljava.lang.String;", array.getClass().getName());
		assertEquals("java.lang.String", array.getClass().getComponentType().getName());
	}

	public void test2() {
		int[] array = { 1, 2 };

		assertEquals(true, array.getClass().isArray());
		assertEquals("[I", array.getClass().getName());
		assertEquals("int", array.getClass().getComponentType().getName());
	}

	public void test3() {
		double[] array = { 1, 2 };

		assertEquals(true, array.getClass().isArray());
		assertEquals("[D", array.getClass().getName());
		assertEquals("double", array.getClass().getComponentType().getName());
	}

	public void test4() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		Object o = Array.newInstance(Integer.TYPE, 5);
		Array.setInt(o, 0, 1);
		Array.set(o, 1, new Integer(2));

		assertEquals(true, o.getClass().isArray());
		assertEquals("int", o.getClass().getComponentType().getName());
		assertEquals(1, Array.getInt(o, 0));
		assertEquals(2, Array.getInt(o, 1));
	}

}
