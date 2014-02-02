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
package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.IOUtil;

public class TestMultipleUpdates extends ODBTest {

	public static final String BASE = "trigger-auto-increment-server.neodatis";

	public void test3() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		try {

			myServer = ODBFactory.openServer(12001);
			myServer.addBase(BASE, BASE);
			myServer.startServer(true);

			odb = ODBFactory.openClient("localhost", 12001, BASE);
			Function f = new Function("function");
			for (int i = 0; i < 1000; i++) {
				f.setName("function " + i);
				odb.store(f);
				assertEquals("function " + i, f.getName());
				Objects oos = odb.getObjects(Function.class);
				assertEquals(1, oos.size());
				Function f2 = (Function) odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", f.getName()))).getFirst();
				assertEquals("function " + i, f2.getName());
			}
		} finally {
			if (odb != null) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}
}
