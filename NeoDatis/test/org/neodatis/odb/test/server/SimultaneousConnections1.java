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
package org.neodatis.odb.test.server;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class SimultaneousConnections1 extends ODBTest {
	public static boolean hasError = false;
	public static final int SIZE = 30;
	public static String BASE = null;

	public void test2Connections() throws Exception {
		if (isLocal)
			return;

		BASE = getBaseName();
		int poolSize = 10;
		List l = new ArrayList();
		// to be sure meta model will be created before thread concurrency!
		ODB odb = openClient(ODBTest.HOST, ODBTest.PORT, SimultaneousConnections1.BASE);
		odb.store(new Function("function  0"));
		odb.close();

		ODB[] odbs = new ODB[poolSize];
		for (int i = 0; i < poolSize; i++) {
			odbs[i] = openClient(ODBTest.HOST, ODBTest.PORT, SimultaneousConnections1.BASE);
		}

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < poolSize; j++) {
				odbs[j].store(new Function("function  sc1 " + j));
				assertEquals(1 + (i + 1), odbs[j].getObjects(Function.class).size());
			}
		}

		for (int i = 0; i < poolSize; i++) {
			assertEquals(1 + SIZE, odbs[i].getObjects(Function.class).size());
			odbs[i].close();
		}

		odb = openClient(ODBTest.HOST, ODBTest.PORT, SimultaneousConnections1.BASE);
		assertEquals(SIZE * poolSize + 1, odb.getObjects(Function.class).size());
		odb.close();

	}

	/**
	 * A non committed transaction tx2 should see objects that have been
	 * committed in tx1 that was created just before tx2 and committed before
	 * tx2 checking objects?
	 * 
	 * @throws Exception
	 */
	public void test2Connections2() throws Exception {
		if (isLocal || !testNewFeature)
			return;
		deleteBase(BASE);

		ODB odb1 = openClient(ODBTest.HOST, ODBTest.PORT, SimultaneousConnections1.BASE);
		ODB odb2 = openClient(ODBTest.HOST, ODBTest.PORT, SimultaneousConnections1.BASE);

		odb1.store(new Function("function  0"));
		odb2.store(new Function("function  1"));

		assertEquals(1, odb1.getObjects(Function.class).size());
		assertEquals(1, odb2.getObjects(Function.class).size());

		odb1.close();

		assertEquals("Should TX2 see committed objects from TX1? : ", 2, odb2.getObjects(Function.class).size());

		odb2.close();
	}

}
