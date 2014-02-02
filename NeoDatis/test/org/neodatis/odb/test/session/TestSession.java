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
package org.neodatis.odb.test.session;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.arraycollectionmap.PlayerWithList;
import org.neodatis.odb.test.vo.login.Function;

public class TestSession extends ODBTest {

	public void test1() throws Exception {
		deleteBase("session.neodatis");
		ODB odb = open("session.neodatis");

		odb.close();

		ODB odb2 = open("session.neodatis");
		Objects l = odb2.getObjects(PlayerWithList.class, true);
		assertEquals(0, l.size());
		odb2.close();
		deleteBase("session.neodatis");
	}

	public void test2() throws Exception {
		deleteBase("session.neodatis");
		ODB odb = open("session.neodatis");

		Function f = new Function("f1");
		odb.store(f);
		odb.commit();

		f.setName("f1 -1");
		odb.store(f);
		odb.close();

		odb = open("session.neodatis");
		Objects os = odb.getObjects(Function.class);
		assertEquals(1, os.size());

		Function f2 = (Function) os.getFirst();
		odb.close();
		deleteBase("session.neodatis");

		assertEquals("f1 -1", f2.getName());

	}

}
