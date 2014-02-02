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
package org.neodatis.odb.test.performance;

import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestArrayCopy extends ODBTest {

	public void test1() {
		int size = 1000;
		int arraySize = 100000;
		byte[] bs1 = new byte[arraySize];
		byte[] bs2 = new byte[arraySize];

		/*
		 * for(int i=0;i<1000;i++){ bs1[i] = (byte)i; }
		 */
		long start = OdbTime.getCurrentTimeInMs();

		for (int i = 0; i < size; i++) {
			System.arraycopy(bs1, 0, bs2, 0, arraySize);
		}
		long step1 = OdbTime.getCurrentTimeInMs();
		long time1 = step1 - start;
		for (int i = 0; i < size; i++) {

			for (int j = 0; j < arraySize; j++) {
				bs2[j] = bs1[j];
			}
		}

		long step2 = OdbTime.getCurrentTimeInMs();
		long time2 = step2 - step1;

		for (int i = 0; i < size; i++) {
			bs2 = (byte[]) bs1.clone();
		}

		long step3 = OdbTime.getCurrentTimeInMs();
		long time3 = step3 - step2;

		println("ArraySize=" + arraySize + " : arraycopy=" + time1 + " - loop copy=" + time2 + " - clone=" + time3);
		assertTrue(time1 <= time2);

	}

}
