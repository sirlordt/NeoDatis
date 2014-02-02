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

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.layers.layer3.engine.FileMutex;
import org.neodatis.odb.test.ODBTest;

public class Test0 extends ODBTest {
	public static String FILE_NAME = "multithread1.neodatis";
	int size = 10;

	public void test1() throws Exception {
		// Configuration.setDebugEnabled(true);
		try{
			OdbConfiguration.useMultiThread(true, size);
			deleteBase(FILE_NAME);
			Thread[] threads = new Thread[size];

			for (int i = 0; i < size; i++) {
				threads[i] = new MyThreadForMutex("multithread", i);
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
			System.out.println(MyThreadForMutex.ended);
			assertEquals(size, MyThreadForMutex.ended);
			
		}finally{
			OdbConfiguration.useMultiThread(false, size);
		}
	}

	public void t1est2() {
		OdbConfiguration.useMultiThread(false);
	}

	public static void main(String[] args) throws Exception {
		Test0 test1 = new Test0();
		test1.test1();
	}
}

class MyThreadForMutex extends Thread {
	private int index;
	private String name;

	private boolean ok;
	public static int ended;

	public MyThreadForMutex(String name, int i) {
		super();
		this.index = i;
		this.name = name;
	}

	public void run() {
		String threadName = "thread " + index;
		Thread.currentThread().setName(threadName);
		boolean result = FileMutex.getInstance().openFile(Test0.FILE_NAME);
		if (result) {
		} else {
		}

		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileMutex.getInstance().releaseFile(Test0.FILE_NAME);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ended++;
	}

}