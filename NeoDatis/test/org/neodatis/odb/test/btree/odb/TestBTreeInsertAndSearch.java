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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeMultipleValuesPerKey;
import org.neodatis.btree.impl.multiplevalue.InMemoryBTreeMultipleValuesPerKey;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestBTreeInsertAndSearch extends ODBTest {

	public void testInsertUsingInt1() {
		IBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("default", 5);
		tree.insert(new Integer(50), "50");
		tree.insert(new Integer(40), "40");
		tree.insert(new Integer(30), "30");
		tree.insert(new Integer(20), "20");
		tree.insert(new Integer(10), "10");

		tree.insert(new Integer(15), "15");
		tree.insert(new Integer(25), "25");

		tree.insert(new Integer(35), "35");
		tree.insert(new Integer(21), "21");
		tree.insert(new Integer(22), "22");
		tree.insert(new Integer(23), "23");

		List l = tree.search(new Integer(22));
		assertEquals("22", l.get(0));
	}

	public void testInsertUsingInt2() {
		int size = 8000;

		IBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("default", 5);
		for (int i = 1; i < size; i++) {
			tree.insert(new Integer(i), String.valueOf(i));
		}
		List l = tree.search(new Integer(1));
		assertEquals("[1]", l.toString());
		l = tree.search(new Integer(1000));
		assertEquals("1000", l.get(0));
		l = tree.search(new Integer(2000));
		assertEquals("2000", l.get(0));
		l = tree.search(new Integer(9800));
		assertNull(l);
		l = tree.search(new Integer(99999));
		assertEquals(null, l);

	}

	public void testString1() {
		IBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("default", 5);
		tree.insert("50", "50");
		tree.insert("40", "40");
		tree.insert("30", "30");
		tree.insert("20", "20");
		tree.insert("10", "10");

		tree.insert("15", "15");
		tree.insert("25", "25");

		tree.insert("35", "35");
		tree.insert("21", "21");
		tree.insert("22", "22");
		tree.insert("23", "23");

		List p = tree.search("22");
		assertEquals("22", p.get(0));
	}

	public void testString2() {
		int size = 300;
		int max = 0;
		IBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("default", 5);
		for (int i = 1; i < size; i++) {
			for (int j = 1; j < size; j++) {
				String key = String.valueOf((i + 1) * size + j);
				String value = String.valueOf(i * j);
				tree.insert(key, value);
				if (i * j > max) {
					max = i * j;
				}
				/*
				 * println(i + "," + j); println(tree.getRoot());
				 * println(tree.getRoot().getChild(0));
				 */
			}
		}
		// println("max = " + max);
		for (int i = 1; i < size; i++) {
			for (int j = 1; j < size; j++) {
				String key = String.valueOf((i + 1) * size + j);
				String value = String.valueOf(i * j);
				List p = tree.search(key);
				assertEquals(value, p.get(0));
			}
		}
	}

	public void test1() {
		int degree = 3;
		IBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test1", degree);

		tree.insert(new Integer(1), "Value 1");
		tree.insert(new Integer(20), "Value 20");
		tree.insert(new Integer(25), "Value 25");
		tree.insert(new Integer(29), "Value 29");
		tree.insert(new Integer(21), "Value 21");

		assertEquals(5, tree.getRoot().getNbKeys());
		assertEquals(0, tree.getRoot().getNbChildren());
		assertEquals(new Integer(21), tree.getRoot().getMedian().getKey());
		assertEquals("[Value 21]", tree.getRoot().getMedian().getValue().toString());
		assertEquals(0, tree.getRoot().getNbChildren());

		// println(tree.getRoot());

		tree.insert(new Integer(45), "Value 45");

		assertEquals(2, tree.getRoot().getNbChildren());
		assertEquals(1, tree.getRoot().getNbKeys());
		assertEquals(new Integer(21), tree.getRoot().getKeyAt(0));
		assertEquals("[Value 21]", tree.getRoot().getValueAsObjectAt(0).toString());

		// println(tree.getRoot());

		List o = tree.search(new Integer(20));
		assertEquals("Value 20", o.get(0));

		o = tree.search(new Integer(29));
		assertEquals("Value 29", o.get(0));

		o = tree.search(new Integer(45));
		assertEquals("Value 45", o.get(0));

	}

	public void test2() {
		int degree = 10;

		IBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test2", degree);

		for (int i = 0; i < 50000; i++) {
			tree.insert(new Integer(i), "Value " + i);
		}

		assertEquals("Value 0", tree.search(new Integer(0)).get(0));
		assertEquals("Value 1000", tree.search(new Integer(1000)).get(0));
		assertEquals("Value 2000", tree.search(new Integer(2000)).get(0));
		assertEquals("Value 3000", tree.search(new Integer(3000)).get(0));

		// tree.resetNbRead();
		assertEquals("Value 4999", tree.search(new Integer(4999)).get(0));
		// println("Nb reads = " + tree.getNbRead());
		// println("root = " + tree.getRoot().keysToString(false));
		// println("root[0] = " +
		// tree.getRoot().getChild(0).keysToString(false));
		// println("root[1] = " +
		// tree.getRoot().getChild(1).keysToString(false));
		// println("root[5] = " +
		// tree.getRoot().getChild(3).keysToString(false));

	}

	public void test3() {
		int degree = 3;
		IBTree tree = new InMemoryBTreeMultipleValuesPerKey("test3", degree);

		tree.insert(new Integer(1), "A");
		// tree.insert(new Integer(2),"B");
		tree.insert(new Integer(3), "C");
		tree.insert(new Integer(4), "D");
		tree.insert(new Integer(5), "E");
		// tree.insert(new Integer(6),"F");
		tree.insert(new Integer(7), "G");
		// tree.insert(new Integer(8),"H");
		// tree.insert(new Integer(9),"I");
		tree.insert(new Integer(10), "J");
		tree.insert(new Integer(11), "K");
		// tree.insert(new Integer(12),"L");
		tree.insert(new Integer(13), "M");
		tree.insert(new Integer(14), "N");
		tree.insert(new Integer(15), "O");
		tree.insert(new Integer(16), "P");
		// tree.insert(new Integer(17),"Q");
		tree.insert(new Integer(18), "R");
		tree.insert(new Integer(19), "S");
		tree.insert(new Integer(20), "T");
		tree.insert(new Integer(21), "U");
		tree.insert(new Integer(22), "V");
		// tree.insert(new Integer(23),"W");
		tree.insert(new Integer(24), "X");
		tree.insert(new Integer(25), "Y");
		tree.insert(new Integer(26), "Z");

		// assertEquals(4, tree.getRoot().getNbKeys());
		// assertEquals(0, tree.getRoot().getNbChildren());
		// assertEquals(21, tree.getRoot().getMedianKey());
		// assertEquals("Value 21", tree.getRoot().getMedianValue());
		// assertEquals(0, tree.getRoot().getNbChildren());

		/*
		 * println("Test 3"); println(tree.getRoot().keysToString(true));
		 * println(tree.getRoot().getChild(0).keysToString(true));
		 * println(tree.getRoot().getChild(1).keysToString(true));
		 * println(tree.getRoot().getChild(2).keysToString());
		 * println(tree.getRoot().getChild(3).keysToString());
		 */

	}

	public void test4() {
		int degree = 3;
		IBTreeMultipleValuesPerKey tree1 = new InMemoryBTreeMultipleValuesPerKey("1", degree);

		tree1.insert(new Integer(1), "A");
		// tree.insert(new Integer(2),"B");
		tree1.insert(new Integer(3), "C");
		tree1.insert(new Integer(4), "D");
		tree1.insert(new Integer(5), "E");

		IBTreeMultipleValuesPerKey tree2 = new InMemoryBTreeMultipleValuesPerKey("2", degree);
		tree2.insert(new Integer(10), "J");
		tree2.insert(new Integer(11), "K");

		IBTreeMultipleValuesPerKey tree3 = new InMemoryBTreeMultipleValuesPerKey("3", degree);
		tree3.insert(new Integer(14), "N");
		tree3.insert(new Integer(15), "O");

		IBTreeMultipleValuesPerKey tree4 = new InMemoryBTreeMultipleValuesPerKey("4", degree);
		tree4.insert(new Integer(18), "R");
		tree4.insert(new Integer(19), "S");
		tree4.insert(new Integer(20), "T");
		tree4.insert(new Integer(21), "U");
		tree4.insert(new Integer(22), "V");

		IBTreeMultipleValuesPerKey tree5 = new InMemoryBTreeMultipleValuesPerKey("5", degree);
		tree5.insert(new Integer(25), "Y");
		tree5.insert(new Integer(26), "Z");

		IBTreeMultipleValuesPerKey tree6 = new InMemoryBTreeMultipleValuesPerKey("6", degree);
		tree6.insert(new Integer(7), "G");
		tree6.insert(new Integer(13), "M");
		tree6.insert(new Integer(16), "P");
		tree6.insert(new Integer(24), "X");

		tree6.getRoot().setChildAt(tree1.getRoot(), 0);
		tree6.getRoot().setChildAt(tree2.getRoot(), 1);
		tree6.getRoot().setChildAt(tree3.getRoot(), 2);
		tree6.getRoot().setChildAt(tree4.getRoot(), 3);
		tree6.getRoot().setChildAt(tree5.getRoot(), 4);
		tree6.getRoot().setNbChildren(5);

		// println("Test 4");
		/*
		 * println(tree6.getRoot().keysToString(true));
		 * println(tree6.getRoot().getChild(0).keysToString(true));
		 * println(tree6.getRoot().getChild(1).keysToString(true));
		 * println(tree6.getRoot().getChild(2).keysToString(true));
		 * println(tree6.getRoot().getChild(3).keysToString(true));
		 * println(tree6.getRoot().getChild(4).keysToString(true));
		 */
		tree6.insert(new Integer(2), "B");
		// println(tree6.getRoot().getChild(0).keysToString(true));
		assertEquals("[B]", tree6.getRoot().getChildAt(0, true).getValueAsObjectAt(1).toString());

		tree6.insert(new Integer(17), "Q");
		// println(tree6.getRoot().keysToString(true));
		assertEquals(5, tree6.getRoot().getNbKeys());
		// println(tree6.getRoot().getChild(3).keysToString(true));
		assertEquals("[Q]", tree6.getRoot().getChildAt(3, true).getValueAsObjectAt(0).toString());
		assertEquals("[R]", tree6.getRoot().getChildAt(3, true).getValueAsObjectAt(1).toString());
		assertEquals("[S]", tree6.getRoot().getChildAt(3, true).getValueAsObjectAt(2).toString());
		// println(tree6.getRoot().getChild(4).keysToString(true));
		assertEquals("[U]", tree6.getRoot().getChildAt(4, true).getValueAsObjectAt(0).toString());
		assertEquals("[V]", tree6.getRoot().getChildAt(4, true).getValueAsObjectAt(1).toString());

		tree6.insert(new Integer(12), "L");
		// println(tree6.getRoot().keysToString(true));
		assertEquals(1, tree6.getRoot().getNbKeys());
		assertEquals(2, tree6.getRoot().getChildAt(0, true).getNbKeys());
		// println(tree6.getRoot().getChild(0).keysToString(true));
		assertEquals("[G]", tree6.getRoot().getChildAt(0, true).getValueAsObjectAt(0).toString());
		assertEquals("[M]", tree6.getRoot().getChildAt(0, true).getValueAsObjectAt(1).toString());

		// println(tree6.getRoot().getChild(0).getChild(1).keysToString(true));
		assertEquals("[J]", tree6.getRoot().getChildAt(0, true).getChildAt(1, true).getValueAsObjectAt(0).toString());
		assertEquals("[K]", tree6.getRoot().getChildAt(0, true).getChildAt(1, true).getValueAsObjectAt(1).toString());
		assertEquals("[L]", tree6.getRoot().getChildAt(0, true).getChildAt(1, true).getValueAsObjectAt(2).toString());

		tree6.insert(new Integer(6), "F");
		// println(tree6.getRoot().keysToString(true));
		assertEquals(1, tree6.getRoot().getNbKeys());
		assertEquals(3, tree6.getRoot().getChildAt(0, true).getNbKeys());
		assertEquals(2, tree6.getRoot().getChildAt(0, true).getChildAt(0, true).getNbKeys());
		// println(tree6.getRoot().getChild(0).getChild(0).keysToString(true));

		assertEquals("[A]", tree6.getRoot().getChildAt(0, true).getChildAt(0, true).getValueAsObjectAt(0).toString());
		assertEquals("[B]", tree6.getRoot().getChildAt(0, true).getChildAt(0, true).getValueAsObjectAt(1).toString());

		// println(tree6.getRoot().getChild(1).getChild(2).keysToString(true));
		assertEquals("[Z]", tree6.getRoot().getChildAt(1, true).getChildAt(2, true).getValueAsObjectAt(1).toString());

	}

	public void test5() {
		int degree = 40;

		IBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("5", degree);

		long a0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < 500000; i++) {
			tree.insert(new Integer(i), "Value " + i);
		}
		long a1 = OdbTime.getCurrentTimeInMs();

		// println("insert time = " + (a1 - a0));
		assertEquals("[Value 0]", tree.search(new Integer(0)).toString());
		assertEquals("[Value 1000]", tree.search(new Integer(1000)).toString());
		assertEquals("[Value 2000]", tree.search(new Integer(2000)).toString());
		assertEquals("[Value 48000]", tree.search(new Integer(48000)).toString());

		// tree.resetNbRead();
		long t0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < 100000; i++) {
			assertEquals("[Value 490000]", tree.search(new Integer(490000)).toString());
		}
		long t1 = OdbTime.getCurrentTimeInMs();
		// tree.resetNbRead();
		assertEquals("[Value 490000]", tree.search(new Integer(490000)).toString());
		// println("Test5 compl- Nb reads = " + tree.getNbRead()+ " -
		// nb comp="+IntKeyBTree.getNbComparison()+ " - t="+(t1-t0));
	}

	public void testNonUniqueKey() {
		int degree = 3;
		IBTreeMultipleValuesPerKey tree1 = new InMemoryBTreeMultipleValuesPerKey("7", degree);

		tree1.insert(new Integer(1), "A");
		tree1.insert(new Integer(1), "AA");
		tree1.insert(new Integer(1), "AAA");
		assertEquals(3, tree1.search(new Integer(1)).size());
		assertEquals("[A, AA, AAA]", tree1.search(new Integer(1)).toString());
		assertEquals(3, tree1.getSize());
	}

	public void testNonUniqueKey2() {
		int degree = 3;
		IBTreeMultipleValuesPerKey tree1 = new InMemoryBTreeMultipleValuesPerKey("7", degree);

		tree1.insert(new Integer(1), "A");
		tree1.insert(new Integer(1), "AA");
		tree1.insert(new Integer(1), "AAA");
		tree1.insert(new Integer(1), "BBB");
		Collection c = tree1.search(new Integer(1));
		assertEquals(4, c.size());
		Iterator iterator = c.iterator();
		assertEquals("A", iterator.next());
		assertEquals("AA", iterator.next());
		assertEquals(4, tree1.getSize());
		assertEquals("[A, AA, AAA, BBB]", c.toString());

	}

}
