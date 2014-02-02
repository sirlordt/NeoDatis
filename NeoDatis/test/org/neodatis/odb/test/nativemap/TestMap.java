/* 

 */

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
package org.neodatis.odb.test.nativemap;

import java.util.Map;
import java.util.TreeMap;

import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class TestMap {
	public void t1() {
		long start = OdbTime.getCurrentTimeInMs();
		Map map = new OdbHashMap();
		for (int i = 0; i < 100000; i++) {
			Long l = new Long(i);
			map.put(l, l);
		}
		long end = OdbTime.getCurrentTimeInMs();
		map.get(5000);
		long end2 = OdbTime.getCurrentTimeInMs();
		System.out.println("HashMap:" + (end - start) + " - " + (end2 - end));
	}

	public void t2() {
		long start = OdbTime.getCurrentTimeInMs();
		Map map = new TreeMap();
		for (int i = 0; i < 100000; i++) {
			Long l = new Long(i);
			map.put(l, l);
		}
		long end = OdbTime.getCurrentTimeInMs();
		map.get(5000);
		long end2 = OdbTime.getCurrentTimeInMs();
		System.out.println("TreeMap:" + (end - start) + " - " + (end2 - end));
	}

	public static void main(String[] args) {
		TestMap tm = new TestMap();
		tm.t1();
		tm.t2();
	}
}
