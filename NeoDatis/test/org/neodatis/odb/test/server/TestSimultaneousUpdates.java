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

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.OID;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestSimultaneousUpdates extends ODBTest {
	public static boolean hasError = false;
	public static final int SIZE = 5;
	public static final String BASE = "simultaneous-update";

	public void test2Connections() throws Exception {
		if (isLocal || useSameVmOptimization)
			return;
		deleteBase(BASE);

		Function function = new Function("function1");

		ODB odb = openClient(ODBTest.HOST, ODBTest.PORT, TestSimultaneousUpdates.BASE);
		OID oid = odb.store(function);
		odb.close();

		ODB odb1 = openClient(ODBTest.HOST, ODBTest.PORT, BASE);
		ODB odb2 = openClient(ODBTest.HOST, ODBTest.PORT, BASE);

		Function functionConnection1 = (Function) odb1.getObjectFromId(oid);
		Function functionConnection2 = (Function) odb2.getObjectFromId(oid);

		functionConnection1.setName("function name connection1");
		functionConnection2.setName("function name connection2");

		// update object in connection 1
		odb1.store(functionConnection1);
		// commit
		odb1.close();

		// Check committed value
		odb1 = openClient(ODBTest.HOST, ODBTest.PORT, BASE);
		functionConnection1 = (Function) odb1.getObjectFromId(oid);
		assertEquals("function name connection1", functionConnection1.getName());
		odb1.close();

		// now update object in connection 2
		odb2.store(functionConnection2);
		// commit
		odb2.close();

		// Check committed value
		odb2 = openClient(ODBTest.HOST, ODBTest.PORT, BASE);
		functionConnection2 = (Function) odb2.getObjectFromId(oid);
		odb2.close();
		assertEquals("function name connection2", functionConnection2.getName());

	}

	public void test2ConcurrentUpdates() throws Exception {
		if (isLocal)
			return;
		deleteBase(BASE);

		Function function = new Function("function1");

		ODB odb = openClient(ODBTest.HOST, ODBTest.PORT, TestSimultaneousUpdates.BASE);
		OID oid = odb.store(function);
		odb.close();

		odb = openClient(ODBTest.HOST, ODBTest.PORT, TestSimultaneousUpdates.BASE);
		Function f = (Function) odb.getObjectFromId(oid);
		odb.close();

		ThreadToUpdate t1 = new ThreadToUpdate(oid, "1", 0);
		ThreadToUpdate t2 = new ThreadToUpdate(oid, "2", 50);
		t1.start();
		Thread.sleep(1000);
		t2.start();

		Thread.sleep(1000);

		// Check committed value
		odb = openClient(ODBTest.HOST, ODBTest.PORT, BASE);
		function = (Function) odb.getObjectFromId(oid);
		odb.close();
		assertEquals("function name connection 2", function.getName());

	}

}

class ThreadToUpdate extends Thread {
	private OID oid;
	private String connectionId;
	private boolean hasError;
	private long timeToWaitToCommit;

	public ThreadToUpdate(OID oid, String connectionId, long t) {
		this.oid = oid;
		this.connectionId = connectionId;
		this.timeToWaitToCommit = t;
	}

	public void run() {

		// commit
		try {
			ODB odb = ODBFactory.openClient(ODBTest.HOST, ODBTest.PORT, ODBTest.DIRECTORY + TestSimultaneousUpdates.BASE);

			Function functionConnection = (Function) odb.getObjectFromId(oid);

			functionConnection.setName("function name connection " + connectionId);

			// update object in connection 1 - This locks the objects
			odb.store(functionConnection);
			Thread.sleep(timeToWaitToCommit);
			odb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hasError = true;
		}

	}

	public boolean isHasError() {
		return hasError;
	}

}
