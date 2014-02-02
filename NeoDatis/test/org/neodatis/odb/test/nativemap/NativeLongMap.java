
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

public class NativeLongMap {

	int initialCapacity;

	int size;

	int secondSize;

	Object[] array;

	public NativeLongMap(int initialCapacity) {
		array = new Object[initialCapacity];
		size = initialCapacity;
		secondSize = size / 10;
	}

	public Object get(long key) {
		int tkey = (int) key % size;
		Entry[] entries = (Entry[]) array[tkey];
		if (entries == null) {
			return null;
		}
		int i = 0;
		while (i < entries.length) {
			if (entries[i] == null) {
				return null;
			}
			if (entries[i].key == key) {
				return entries[i].o;
			}
		}
		return null;

	}

	public void put(long key, Object o) {
		int tkey = (int) key % size;
		Entry[] entries = null;
		if (array[tkey] == null) {
			entries = new Entry[secondSize];
			entries[0] = new Entry(key, o);
			array[tkey] = entries;
			return;
		}
		int i = 0;
		while (i < entries.length) {
			if (entries[i] == null) {
				entries[i] = new Entry(key, o);
				return;
			}
			i++;
		}
		throw new RuntimeException("Second array explosion");
	}

	public static void main(String[] args) {
		NativeLongMap nlm = new NativeLongMap(100);
		String s = new String("Ola");
		nlm.put(1, s);
	}
}

class Entry {
	public long key;

	public Object o;

	public Entry(long key, Object o) {
		this.key = key;
		this.o = o;
	}
}
