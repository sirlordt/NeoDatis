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
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.mutex.Mutex;
import org.neodatis.tool.mutex.MutexFactory;
import org.neodatis.tool.wrappers.OdbThread;
import org.neodatis.tool.wrappers.OdbTime;

public class SimultaneousConnections extends ODBTest {
	public static boolean hasError = false;
	public static final int SIZE = 60;
	public static String BASE = null;

	public void test2Connections() throws Exception {
		long start = OdbTime.getCurrentTimeInMs();
		if (isLocal)
			return;
		BASE = getBaseName();
		
		int poolSize = 60;
		List l = new ArrayList();
		// to be sure meta model will be created before thread concurrency!
		ODB odb = openClient(ODBTest.HOST, ODBTest.PORT, SimultaneousConnections.BASE);
		odb.store(new Function("function  0"));
		odb.close();

		for (int i = 0; i < poolSize; i++) {
			Thread1 t = new Thread1();
			t.start();
			l.add(t);
		}

		for (int i = 0; i < poolSize; i++) {
			((Thread) l.get(i)).join();
		}
		assertFalse(hasError);

		odb = openClient(ODBTest.HOST, ODBTest.PORT, SimultaneousConnections.BASE);
		long nbObjects = odb.getObjects(Function.class).size();
		odb.close();
		println("" + nbObjects);
		assertEquals(SIZE * poolSize + 1, nbObjects);
		println("Execution time = " + (OdbTime.getCurrentTimeInMs() - start));

	}

}

class Thread1 extends Thread {

	public void run() {
		try {
			ODB odb = ODBFactory.openClient(ODBTest.HOST, ODBTest.PORT, ODBTest.DIRECTORY + SimultaneousConnections.BASE);
			Mutex mutex = MutexFactory.get("m");
			for (int i = 0; i < SimultaneousConnections.SIZE; i++) {
				// mutex.acquire();
				odb.store(new Function("function  " + (i + 1) + " " + getName()));
				// mutex.release();
				int nb = odb.getObjects(Function.class).size();
				if (nb != i + 2) {
					throw new RuntimeException("Error in Thread " + getName() + " : " + (i + 1)
							+ " object have been inserted : but odb sees " + nb + " at " + OdbTime.getCurrentTimeInMs());
				}

				// Thread.sleep(200);
			}
			odb.close();
			System.out.println(OdbThread.getCurrentThreadName() + " has committed at " + OdbTime.getCurrentTimeInMs());
		} catch (Throwable e) {
			SimultaneousConnections.hasError = true;
			e.printStackTrace();

		}

	}
}
