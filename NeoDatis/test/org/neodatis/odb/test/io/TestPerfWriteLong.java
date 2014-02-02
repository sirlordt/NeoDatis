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
package org.neodatis.odb.test.io;

import java.io.RandomAccessFile;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestPerfWriteLong extends ODBTest {
	public void test1() throws Exception {
		int size = 100;
		RandomAccessFile raf = new RandomAccessFile(ODBTest.DIRECTORY + "test1.neodatis", "rw");
		byte b = 1;
		byte[] bs = { b, b, b, b, b, b, b, b };
		long l = 48987978;

		long t1 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			raf.write(b);
			raf.write(b);
			raf.write(b);
			raf.write(b);
			raf.write(b);
			raf.write(b);
			raf.write(b);
			raf.write(b);
		}
		long t2 = OdbTime.getCurrentTimeInMs();

		long t3 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			raf.write(bs);
		}
		long t4 = OdbTime.getCurrentTimeInMs();
		long t5 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			raf.writeLong(l);
		}
		long t6 = OdbTime.getCurrentTimeInMs();

		long t7 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			raf.write(bs, 0, 8);
		}
		long t8 = OdbTime.getCurrentTimeInMs();
		raf.close();
		deleteBase("test1.txt");
	}

	public void test2() {
		long l = 121654545;
		int size = 1000000;
		long t1 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			byte[] bytes = OdbConfiguration.getCoreProvider().getByteArrayConverter().longToByteArray(i);
		}
		long t2 = OdbTime.getCurrentTimeInMs();

	}

	public void t2est3() {
		int size = 60000;
		int arraySize = 5000;
		byte bytes[] = null;
		long t1 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			bytes = new byte[arraySize];
		}
		long t2 = OdbTime.getCurrentTimeInMs();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 800; j++) {
				bytes[j] = 0;
			}
		}

		long t3 = OdbTime.getCurrentTimeInMs();

	}

	public void test4() {
		double d = Math.random() * 5;
		int i = (int) (d);
		int i2 = (int) (Math.random() * 5);
	}
}
