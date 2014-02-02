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
package org.neodatis.odb.test.buffer;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestBigData extends ODBTest {
	public void test1() throws Exception {
		ODB odb = open("big-data.neodatis");

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < 30000; i++) {
			buffer.append('a');
		}
		Function function = new Function(buffer.toString());
		odb.store(function);
		odb.close();

		odb = open("big-data.neodatis");
		Function f2 = (Function) odb.getObjects(Function.class).getFirst();
		assertEquals(30000, f2.getName().length());
		odb.close();

		odb = open("big-data.neodatis");
		f2 = (Function) odb.getObjects(Function.class).getFirst();
		f2.setName(f2.getName() + "ola chico");
		int newSize = f2.getName().length();
		odb.store(f2);
		odb.close();

		odb = open("big-data.neodatis");
		f2 = (Function) odb.getObjects(Function.class).getFirst();
		assertEquals(newSize, f2.getName().length());
		assertEquals(buffer.toString() + "ola chico", f2.getName());
		odb.close();

	}

	public void setUp() throws Exception {
		deleteBase("big-data.neodatis");
	}

	public void tearDown() throws Exception {
		deleteBase("big-data.neodatis");
	}
}
