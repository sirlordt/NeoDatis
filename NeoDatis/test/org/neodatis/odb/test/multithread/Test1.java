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
package org.neodatis.odb.test.multithread;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class Test1 extends ODBTest {
	public static String FILE_NAME = "multithread1.neodatis";
	int size = 50;

	public void testT1() {

	}

	public void t1est() throws Exception {
		// Configuration.setDebugEnabled(true);
		OdbConfiguration.useMultiThread(true, size);
		deleteBase(FILE_NAME);
		Thread[] threads = new Thread[size];

		for (int i = 0; i < size; i++) {
			threads[i] = new Thread(new MyRunnable("multithread", i));
			threads[i].start();
		}

		// Waits for the end of all threads
		for (int j = 0; j < size; j++) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ODB odb;
		odb = odb = open(FILE_NAME);
		long nb = odb.count(new CriteriaQuery(MyValueObject.class)).longValue();
		assertEquals(size, nb);

	}

	public void t1est2() {
		OdbConfiguration.useMultiThread(false);
	}

	public static void main(String[] args) throws Exception {
		Test1 test1 = new Test1();
		test1.t1est();
	}
}

class MyRunnable implements Runnable {
	private int index;
	private String name;

	private boolean ok;

	public MyRunnable(String name, int i) {
		super();
		this.index = i;
		this.name = name;
	}

	public void run() {
		Thread.currentThread().setName("thread " + index);
		ODB odb = null;
		try {
			odb = ODBFactory.open(ODBTest.DIRECTORY + Test1.FILE_NAME);
			odb.store(new MyValueObject(name, index));
			ok = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ok = false;
		} finally {
			if (odb != null) {
				try {
					odb.close();
					if (ok) {
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					ok = false;
				}
			}
		}
	}

}