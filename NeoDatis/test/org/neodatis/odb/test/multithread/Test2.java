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

import java.io.IOException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class Test2 extends ODBTest {
	public static String FILE_NAME = "multithread1.neodatis";
	public static ODB odb = null;
	int size = 2;

	public void test1() {

	}

	public void t1est1() throws Exception {

		odb = open(FILE_NAME);
		// Configuration.setDebugEnabled(true);
		OdbConfiguration.useMultiThread(true, size);
		deleteBase(FILE_NAME);
		Thread[] threads = new Thread[size];

		for (int i = 0; i < size; i++) {
			threads[i] = new MyThread2("multithread", i);
			threads[i].start();
		}

		// Waits for the end of all threads
		for (int i = 0; i < size; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ODB odb;
		try {
			odb = open(FILE_NAME);
			long nb = odb.count(new CriteriaQuery(MyValueObject.class)).longValue();
			if (size != nb) {
				throw new Exception(size + " should be equal to " + nb);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void t1est2() {
		OdbConfiguration.useMultiThread(false);
	}

	public static void main(String[] args) throws Exception {
		Test2 test1 = new Test2();
		test1.t1est1();
	}
}

class MyThread2 extends Thread {
	private int index;
	private String name;

	private boolean ok;

	public MyThread2(String name, int i) {
		super();
		this.index = i;
		this.name = name;
	}

	public void run() {
		Thread.currentThread().setName("thread " + index);

		try {
			Test2.odb.store(new MyValueObject(name, index));
			Test2.odb.commit();
			ok = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ok = false;
		} finally {
		}
	}

}