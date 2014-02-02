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
package org.neodatis.odb.test.other;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;

public class TestShutdownHook extends ODBTest {
	public void test1() throws Exception {
		deleteBase("hook.neodatis");
		ODB obase = open("hook.neodatis");
		obase.getObjects(TestClass.class);
		obase.store(new TestClass());
		obase.close();
		deleteBase("hook.neodatis");
	}
}
