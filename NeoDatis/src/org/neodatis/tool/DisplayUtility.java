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
package org.neodatis.tool;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DisplayUtility {
	public static String byteArrayToString(byte[] bytes) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buffer.append((int) bytes[i]).append(" ");
		}
		return buffer.toString();
	}

	public static String longArrayToString(long[] longs) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < longs.length; i++) {
			buffer.append(longs[i]).append(" ");
		}
		return buffer.toString();
	}

	public static String objectArrayToString(Object[] objects) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < objects.length; i++) {
			buffer.append(objects[i]).append(" ");
		}
		return buffer.toString();
	}

	public static void display(String title, Collection list) {
		System.out.println("***" + title);
		Iterator iterator = list.iterator();
		int i = 1;
		while (iterator.hasNext()) {
			System.out.println(i + "=" + iterator.next());
			i++;
		}
	}

	public static String listToString(List list) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			buffer.append((i + 1) + "=" + list.get(i)).append("\n");
		}
		return buffer.toString();
	}
}
