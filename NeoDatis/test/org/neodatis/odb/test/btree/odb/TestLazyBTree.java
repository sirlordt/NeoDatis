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
package org.neodatis.odb.test.btree.odb;

import java.util.Iterator;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.impl.multiplevalue.InMemoryBTreeMultipleValuesPerKey;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.test.ODBTest;

public class TestLazyBTree extends ODBTest {

	public void test1() {
		int size = 100000;
		IBTree tree = new InMemoryBTreeMultipleValuesPerKey("test1", 2);
		for (int i = 0; i < size; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}
		assertEquals(size, tree.getSize());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_ASC);
		int j = 0;
		while (iterator.hasNext()) {
			Object o = iterator.next();
			// println(o);
			j++;
			if (j == size) {
				assertEquals("value " + size, o);
			}
		}
	}

	public void test2() {
		int size = 100000;
		IBTree tree = new InMemoryBTreeMultipleValuesPerKey("test2", 2);
		for (int i = 0; i < size; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}
		assertEquals(size, tree.getSize());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_DESC);
		int j = 0;
		while (iterator.hasNext()) {
			Object o = iterator.next();
			// println(o);
			j++;
			if (j == size) {
				assertEquals("value " + 1, o);
			}
		}
	}

	public void test3() {
		int size = 100000;
		IBTree tree = new InMemoryBTreeMultipleValuesPerKey("test1", 2);
		for (int i = 0; i < size; i++) {
			// println(i);
			tree.insert(String.valueOf(i + 1), "value " + (i + 1));
		}
		assertEquals(size, tree.getSize());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_ASC);
		int j = 0;
		while (iterator.hasNext()) {
			Object o = iterator.next();
			// println(o);
			j++;
			if (j == size) {
				assertEquals("value " + (size - 1), o);
			}
		}
	}
}
