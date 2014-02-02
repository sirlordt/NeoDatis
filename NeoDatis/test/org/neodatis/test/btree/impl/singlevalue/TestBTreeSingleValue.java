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
package org.neodatis.test.btree.impl.singlevalue;

import junit.framework.TestCase;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeNodeOneValuePerKey;
import org.neodatis.btree.IBTreeSingleValuePerKey;
import org.neodatis.btree.impl.singlevalue.InMemoryBTreeSingleValuePerKey;
import org.neodatis.btree.tool.BTreeDisplay;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.OdbTime;

public class TestBTreeSingleValue extends TestCase {

	public void setUp() throws Exception {
		super.setUp();
	}

	public void testDelete() throws Exception {
		IBTreeSingleValuePerKey btree = getBTree(3);
		btree.insert(new Integer(1), "key 1");
		assertEquals(1, btree.getSize());
		assertEquals("key 1", btree.search(new Integer(1)));
		Object o = btree.delete(new Integer(1), "key 1");
		assertEquals("key 1", o);
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());

	}

	public void testDelete2() throws Exception {
		IBTreeSingleValuePerKey btree = getBTree(3);
		btree.insert(new Integer(1), "key 1");
		btree.insert(new Integer(2), "key 2");
		btree.insert(new Integer(3), "key 3");
		btree.insert(new Integer(4), "key 4");
		btree.insert(new Integer(5), "key 5");
		assertEquals(5, btree.getSize());
		assertEquals("key 1", btree.search(new Integer(1)));
		assertEquals("key 2", btree.search(new Integer(2)));
		assertEquals("key 3", btree.search(new Integer(3)));
		assertEquals("key 4", btree.search(new Integer(4)));
		assertEquals("key 5", btree.search(new Integer(5)));

		Object o = btree.delete(new Integer(1), "key 1");
		assertEquals("key 1", o);

		o = btree.delete(new Integer(2), "key 2");
		assertEquals("key 2", o);

		o = btree.delete(new Integer(3), "key 3");
		assertEquals("key 3", o);

		o = btree.delete(new Integer(4), "key 4");
		assertEquals("key 4", o);

		o = btree.delete(new Integer(5), "key 5");
		assertEquals("key 5", o);

		assertEquals(0, btree.getSize());
		assertEquals(0, btree.getRoot().getNbKeys());
	}

	public void testDelete25() {
		IBTree tree = getBTree(3);
		tree.insert("A", "A");
		tree.insert("B", "B");
		tree.insert("C", "C");
		tree.insert("D", "D");
		tree.insert("E", "E");
		tree.insert("F", "F");
		tree.insert("G", "G");
		tree.insert("J", "J");
		tree.insert("K", "K");
		tree.insert("L", "L");
		tree.insert("M", "M");
		tree.insert("N", "N");
		tree.insert("O", "O");
		tree.insert("P", "P");
		tree.insert("Q", "Q");
		tree.insert("R", "R");
		tree.insert("S", "S");
		tree.insert("T", "T");
		tree.insert("U", "U");
		tree.insert("V", "V");
		tree.insert("X", "X");
		tree.insert("Y", "Y");
		tree.insert("Z", "Z");

	}

	public void testDelete26() {
		IBTree tree = getBTree(3);
		tree.insert("Z", "Z");
		tree.insert("Y", "Y");
		tree.insert("X", "X");
		tree.insert("V", "V");
		tree.insert("U", "U");
		tree.insert("T", "T");
		tree.insert("S", "S");
		tree.insert("R", "R");
		tree.insert("Q", "Q");
		tree.insert("P", "P");
		tree.insert("O", "O");
		tree.insert("N", "N");
		tree.insert("M", "M");
		tree.insert("L", "L");
		tree.insert("K", "K");
		tree.insert("J", "J");
		tree.insert("G", "G");
		tree.insert("F", "F");
		tree.insert("E", "E");
		tree.insert("D", "D");
		tree.insert("C", "C");
		tree.insert("B", "B");
		tree.insert("A", "A");

		assertEquals(23, tree.getSize());

		IBTreeNodeOneValuePerKey child1 = (IBTreeNodeOneValuePerKey) tree.getRoot().getChildAt(0, false);
		IBTreeNodeOneValuePerKey child14 = (IBTreeNodeOneValuePerKey) child1.getChildAt(3, false);
		assertNotNull(child14);

	}

	/**
	 * Cromen example, second edition, page 450
	 * 
	 * @throws Exception
	 * 
	 */
	public void testDelete27() throws Exception {
		IBTree tree = getBTree(3);
		tree.insert("P", "P");
		IBTreeNodeOneValuePerKey PNode = (IBTreeNodeOneValuePerKey) tree.getRoot();

		IBTreeNodeOneValuePerKey CGMNode = getBTreeNode(tree, "cgm");
		CGMNode.insertKeyAndValue("C", "C");
		CGMNode.insertKeyAndValue("G", "G");
		CGMNode.insertKeyAndValue("M", "M");

		IBTreeNodeOneValuePerKey TXNode = getBTreeNode(tree, "tx");
		TXNode.insertKeyAndValue("T", "T");
		TXNode.insertKeyAndValue("X", "X");

		PNode.setChildAt(CGMNode, 0);
		PNode.setChildAt(TXNode, 1);
		PNode.setNbChildren(2);

		IBTreeNodeOneValuePerKey ABNode = getBTreeNode(tree, "ab");
		ABNode.insertKeyAndValue("A", "A");
		ABNode.insertKeyAndValue("B", "B");

		IBTreeNodeOneValuePerKey DEFode = getBTreeNode(tree, "def");
		DEFode.insertKeyAndValue("D", "D");
		DEFode.insertKeyAndValue("E", "E");
		DEFode.insertKeyAndValue("F", "F");

		IBTreeNodeOneValuePerKey JKLNode = getBTreeNode(tree, "jkl");
		JKLNode.insertKeyAndValue("J", "J");
		JKLNode.insertKeyAndValue("K", "K");
		JKLNode.insertKeyAndValue("L", "L");

		IBTreeNodeOneValuePerKey NONode = getBTreeNode(tree, "no");
		NONode.insertKeyAndValue("N", "N");
		NONode.insertKeyAndValue("O", "O");

		CGMNode.setChildAt(ABNode, 0);
		CGMNode.setChildAt(DEFode, 1);
		CGMNode.setChildAt(JKLNode, 2);
		CGMNode.setChildAt(NONode, 3);
		CGMNode.setNbChildren(4);

		IBTreeNodeOneValuePerKey QRSNode = getBTreeNode(tree, "qrs");
		QRSNode.insertKeyAndValue("Q", "Q");
		QRSNode.insertKeyAndValue("R", "R");
		QRSNode.insertKeyAndValue("S", "S");

		IBTreeNodeOneValuePerKey UVNode = getBTreeNode(tree, "uv");
		UVNode.insertKeyAndValue("U", "U");
		UVNode.insertKeyAndValue("V", "V");

		IBTreeNodeOneValuePerKey YZNode = getBTreeNode(tree, "yz");
		YZNode.insertKeyAndValue("Y", "Y");
		YZNode.insertKeyAndValue("Z", "Z");

		TXNode.setChildAt(QRSNode, 0);
		TXNode.setChildAt(UVNode, 1);
		TXNode.setChildAt(YZNode, 2);
		TXNode.setNbChildren(3);

		String s1 = "h=1:[P]" + "h=2:[C,G,M][T,X]" + "h=3:[A,B][D,E,F][J,K,L][N,O][Q,R,S][U,V][Y,Z]";
		// case 1
		String s2AfterDeleteingF = "h=1:[P]" + "h=2:[C,G,M][T,X]" + "h=3:[A,B][D,E][J,K,L][N,O][Q,R,S][U,V][Y,Z]";
		Object F = tree.delete("F", "F");
		assertEquals("F", F);
		String s = new BTreeDisplay().build(tree.getRoot(), 3, false).toString();
		s = OdbString.replaceToken(s, " ", "");
		s = OdbString.replaceToken(s, "\n", "");
		assertEquals(s2AfterDeleteingF, s);

		// case 2a
		String s2AfterDeleteingM = "h=1:[P]" + "h=2:[C,G,L][T,X]" + "h=3:[A,B][D,E][J,K][N,O][Q,R,S][U,V][Y,Z]";
		Object M = tree.delete("M", "M");
		assertEquals("M", M);
		s = new BTreeDisplay().build(tree.getRoot(), 3, false).toString();
		s = OdbString.replaceToken(s, " ", "");
		s = OdbString.replaceToken(s, "\n", "");
		assertEquals(s2AfterDeleteingM, s);

		// case 2c
		String s2AfterDeleteingG = "h=1:[P]" + "h=2:[C,L][T,X]" + "h=3:[A,B][D,E,J,K][N,O][Q,R,S][U,V][Y,Z]";
		Object G = tree.delete("G", "G");
		assertEquals("G", G);
		s = new BTreeDisplay().build(tree.getRoot(), 3, false).toString();
		s = OdbString.replaceToken(s, " ", "");
		s = OdbString.replaceToken(s, "\n", "");
		assertEquals(s2AfterDeleteingG, s);

		// case 3b
		String s2AfterDeleteingD = "h=1:[C,L,P,T,X]" + "h=2:[A,B][E,J,K][N,O][Q,R,S][U,V][Y,Z]";
		Object D = tree.delete("D", "D");
		// assertEquals(2, tree.getHeight());
		assertEquals("D", D);
		s = new BTreeDisplay().build(tree.getRoot(), 3, false).toString();
		s = OdbString.replaceToken(s, " ", "");
		s = OdbString.replaceToken(s, "\n", "");
		s = OdbString.replaceToken(s, "h=3:", "");
		assertEquals(s2AfterDeleteingD, s);

		// case 3a
		String s2AfterDeleteingB = "h=1:[E,L,P,T,X]" + "h=2:[A,C][J,K][N,O][Q,R,S][U,V][Y,Z]";
		Object B = tree.delete("B", "B");
		assertEquals("B", B);
		s = new BTreeDisplay().build(tree.getRoot(), 3, false).toString();
		s = OdbString.replaceToken(s, " ", "");
		s = OdbString.replaceToken(s, "\n", "");
		s = OdbString.replaceToken(s, "h=3:", "");
		assertEquals(s2AfterDeleteingB, s);

	}

	public void testDelete3() throws Exception {
		IBTree btree = getBTree(3);
		int size = 10;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(size, btree.getSize());
		for (int i = 0; i < size; i++) {
			assertEquals("key " + i, btree.delete(new Integer(i), "key " + i));
		}
		assertEquals(0, btree.getSize());
	}

	public void testDelete10000() throws Exception {
		IBTree btree = getBTree(20);
		int size = 100000;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(size, btree.getSize());
		for (int i = 0; i < size; i++) {
			assertEquals("key " + i, btree.delete(new Integer(i), "key " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDelete100000() throws Exception {
		IBTree btree = getBTree(3);
		int size = 10000;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(size, btree.getSize());
		for (int i = 0; i < size; i++) {
			assertEquals("key " + i, btree.delete(new Integer(i), "key " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDelete10000Alpha_2() throws Exception {
		IBTreeSingleValuePerKey btree = getBTree(2);
		int size = 10000;
		for (int i = 0; i < size; i++) {
			btree.insert("key" + i, "value " + i);
		}

		Object o = btree.search("key71");
		assertEquals(size, btree.getSize());
		for (int i = size - 1; i >= 0; i--) {
			// println(new BTreeDisplay().build(btree));
			assertEquals("value " + i, btree.delete("key" + i, "value " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDelete10000Alpha_3() throws Exception {
		IBTreeSingleValuePerKey btree = getBTree(3);
		int size = 10000;
		for (int i = 0; i < size; i++) {
			btree.insert("key" + i, "value " + i);
		}

		Object o = btree.search("key71");
		assertEquals(size, btree.getSize());
		for (int i = size - 1; i >= 0; i--) {
			// println(new BTreeDisplay().build(btree));
			assertEquals("value " + i, btree.delete("key" + i, "value " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDelete100000Alpha_2() throws Exception {
		IBTreeSingleValuePerKey btree = getBTree(2);
		int size = 100000;
		for (int i = 0; i < size; i++) {
			btree.insert("key" + i, "value " + i);
		}

		Object o = btree.search("key71");
		assertEquals(size, btree.getSize());
		for (int i = size - 1; i >= 0; i--) {
			// println(new BTreeDisplay().build(btree));
			assertEquals("value " + i, btree.delete("key" + i, "value " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDeleteStringKey() throws Exception {
		IBTree btree = getBTree(3);
		btree.insert("key70", "70");
		btree.insert("key71", "71");

		// println(new BTreeDisplay().build(btree));
		assertEquals("70", btree.getRoot().getKeyAndValueAt(0).getValue());
		assertEquals("71", btree.getRoot().getKeyAndValueAt(1).getValue());
	}

	public void testDeleteStringKey2() throws Exception {
		IBTree btree = getBTree(3);
		btree.insert("key700", "700");
		btree.insert("key710", "710");
		btree.insert("key720", "720");
		btree.insert("key730", "730");
		btree.insert("key740", "740");
		btree.insert("key715", "715");

		// println(new BTreeDisplay().build(btree));

		// assertEquals("70", btree.getRoot().getKeyAndValueAt(0).getValue());
		// assertEquals("71", btree.getRoot().getKeyAndValueAt(1).getValue());
	}

	public void testDelete10_3() throws Exception {
		IBTree btree = getBTree(3);
		int size = 10;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "value " + i);
		}
		assertEquals(size, btree.getSize());
		for (int i = size - 1; i >= 0; i--) {
			assertEquals("value " + i, btree.delete(new Integer(i), "value " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDelete100_3() throws Exception {
		IBTree btree = getBTree(3);
		int size = 100;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "value " + i);
		}
		assertEquals(size, btree.getSize());
		for (int i = size - 1; i >= 0; i--) {
			assertEquals("value " + i, btree.delete(new Integer(i), "value " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDelete1000_3() throws Exception {
		IBTree btree = getBTree(3);
		int size = 1000;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "value " + i);
		}
		assertEquals(size, btree.getSize());
		for (int i = size - 1; i >= 0; i--) {
			assertEquals("value " + i, btree.delete(new Integer(i), "value " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testDeleteInsert100000() throws Exception {
		IBTree btree = getBTree(3);
		int size = 200000;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(size, btree.getSize());
		for (int i = 0; i < size; i++) {
			assertEquals("key " + i, btree.delete(new Integer(i), "key " + i));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());

		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(size + i), "key " + (i + size));
		}
		for (int i = 0; i < size; i++) {
			assertEquals("key " + (i + size), btree.delete(new Integer(i + size), "key " + (i + size)));
		}

		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void testInsert() {
		IBTreeSingleValuePerKey btree = getBTree(3);
		btree.insert(new Integer(1), "key 1");
		assertEquals(1, btree.getSize());
		assertEquals("key 1", btree.search(new Integer(1)));
	}

	public void testInsert2() {
		IBTreeSingleValuePerKey btree = getBTree(3);
		btree.insert(new Integer(1), "key 1");
		btree.insert(new Integer(2), "key 2");
		btree.insert(new Integer(3), "key 3");
		btree.insert(new Integer(4), "key 4");
		btree.insert(new Integer(5), "key 5");
		assertEquals(5, btree.getSize());
		assertEquals("key 1", btree.search(new Integer(1)));
		assertEquals("key 2", btree.search(new Integer(2)));
		assertEquals("key 3", btree.search(new Integer(3)));
		assertEquals("key 4", btree.search(new Integer(4)));
		assertEquals("key 5", btree.search(new Integer(5)));

	}

	public void testInsert3() {
		IBTreeSingleValuePerKey btree = getBTree(3);
		btree.insert(new Integer(1), "key 1");
		btree.insert(new Integer(2), "key 2");
		btree.insert(new Integer(3), "key 3");
		btree.insert(new Integer(4), "key 4");
		btree.insert(new Integer(5), "key 5");
		btree.insert(new Integer(6), "key 6");

		assertEquals(6, btree.getSize());
		assertEquals("key 1", btree.search(new Integer(1)));
		assertEquals("key 2", btree.search(new Integer(2)));
		assertEquals("key 3", btree.search(new Integer(3)));
		assertEquals("key 4", btree.search(new Integer(4)));
		assertEquals("key 5", btree.search(new Integer(5)));
		assertEquals("key 6", btree.search(new Integer(6)));

		assertEquals(2, btree.getRoot().getNbChildren());

		// child 1 should be [1,2]
		IBTreeNodeOneValuePerKey child1 = (IBTreeNodeOneValuePerKey) btree.getRoot().getChildAt(0, false);
		assertEquals(2, child1.getNbKeys());
		assertEquals(0, child1.getNbChildren());
		assertEquals("key 1", child1.getKeyAndValueAt(0).getValue());
		assertEquals(new Integer(1), child1.getKeyAndValueAt(0).getKey());

		// child 2 should be [4,5,6]
		IBTreeNodeOneValuePerKey child2 = (IBTreeNodeOneValuePerKey) btree.getRoot().getChildAt(1, false);
		assertEquals(3, child2.getNbKeys());
		assertEquals(0, child2.getNbChildren());
		assertEquals("key 4", child2.getKeyAndValueAt(0).getValue());
		assertEquals("key 5", child2.getKeyAndValueAt(1).getValue());
		assertEquals("key 6", child2.getKeyAndValueAt(2).getValue());

		// child 2 should be null
		IBTreeNodeOneValuePerKey child3 = (IBTreeNodeOneValuePerKey) btree.getRoot().getChildAt(2, false);
		assertEquals(null, child3);
	}

	private IBTreeSingleValuePerKey getBTree(int degree) {
		return new InMemoryBTreeSingleValuePerKey("default", degree);
	}

	public void testsearch10() {
		IBTreeSingleValuePerKey btree = getBTree(3);
		for (int i = 0; i < 10; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(10, btree.getSize());
		assertEquals("key 1", btree.search(new Integer(1)));
		assertEquals("key 9", btree.search(new Integer(9)));
		IBTreeNodeOneValuePerKey child3 = (IBTreeNodeOneValuePerKey) btree.getRoot().getChildAt(2, false);
		assertEquals(4, child3.getNbKeys());
		assertEquals(new Integer(6), child3.getKeyAt(0));
		assertEquals(new Integer(7), child3.getKeyAt(1));
		assertEquals(new Integer(8), child3.getKeyAt(2));
		assertEquals(new Integer(9), child3.getKeyAt(3));
		assertEquals(null, child3.getKeyAt(4));
	}

	public void testsearch500() {
		IBTreeSingleValuePerKey btree = getBTree(3);
		for (int i = 0; i < 500; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(500, btree.getSize());
		assertEquals("key 1", btree.search(new Integer(1)));
		assertEquals("key 499", btree.search(new Integer(499)));
	}

	public void testsearch10000() {
		IBTreeSingleValuePerKey btree = getBTree(10);
		int size = 110000;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(size, btree.getSize());
		for (int i = 0; i < size; i++) {
			assertEquals("key " + i, btree.search(new Integer(i)));
		}
	}

	public void testsearch500000() {
		IBTreeSingleValuePerKey btree = getBTree(10);
		int size = 500000;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(size, btree.getSize());
		for (int i = 0; i < size; i++) {
			assertEquals("key " + i, btree.search(new Integer(i)));
		}
	}

	/**
	 * <pre>
	 * 
	 * node1 = [    10,     100]
	 *           |        |
	 *           |        |
	 *           c1       c2
	 *           
	 * c1 = [1,2,3]
	 * 
	 * result of split should be
	 * 
	 * node1 = [    2  ,    10,     100]
	 *           |        |      |
	 *           |        |      |
	 *           c1       c1'    c2
	 *           
	 *           
	 * where c1 = [1]
	 * and c1'=[3]
	 * </pre>
	 * 
	 */
	public void testSplit() {
		IBTree tree = getBTree(2);
		tree.insert(new Integer(10), "Key 10");
		tree.insert(new Integer(100), "Key 100");

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree, "child 1");
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree, "child 2");

		IBTreeNodeOneValuePerKey node1 = (IBTreeNodeOneValuePerKey) tree.getRoot();

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setNbKeys(2);
		node1.setNbChildren(2);

		c1.setKeyAndValueAt(new Integer(1), "Key 1", 0);
		c1.setKeyAndValueAt(new Integer(2), "Key 2", 1);
		c1.setKeyAndValueAt(new Integer(3), "Key 3", 2);
		c1.setNbKeys(3);

		assertEquals(0, c1.getNbChildren());

		tree.split(node1, c1, 0);

		assertEquals(3, node1.getNbKeys());
		assertEquals(3, node1.getNbChildren());

		assertEquals(new Integer(2), node1.getKeyAndValueAt(0).getKey());
		assertEquals(new Integer(10), node1.getKeyAndValueAt(1).getKey());
		assertEquals(new Integer(100), node1.getKeyAndValueAt(2).getKey());

		IBTreeNodeOneValuePerKey c1New = (IBTreeNodeOneValuePerKey) node1.getChildAt(0, false);
		assertEquals(1, c1New.getNbKeys());
		assertEquals(0, c1New.getNbChildren());
		assertEquals(new Integer(1), c1New.getKeyAt(0));
		assertEquals(null, c1New.getKeyAt(1));
		assertEquals(null, c1New.getKeyAt(2));
		assertEquals(node1, c1New.getParent());

		IBTreeNodeOneValuePerKey c1bis = (IBTreeNodeOneValuePerKey) node1.getChildAt(1, false);
		assertEquals(1, c1bis.getNbKeys());
		assertEquals(0, c1bis.getNbChildren());
		assertEquals(new Integer(3), c1bis.getKeyAt(0));
		assertEquals(node1, c1bis.getParent());
		assertEquals(null, c1bis.getKeyAt(1));
		assertEquals(null, c1bis.getKeyAt(2));

		IBTreeNodeOneValuePerKey c2New = (IBTreeNodeOneValuePerKey) node1.getChildAt(2, false);
		assertEquals(c2, c2New);
	}

	private IBTreeNodeOneValuePerKey getBTreeNode(IBTree tree, String name) {
		return new MockBTreeNodeSingleValue(tree, name);
	}

	public void testgetBiggestSmallest1() throws Exception {
		IBTree btree = getBTree(3);
		btree.insert(new Integer(1), "key 1");
		btree.insert(new Integer(2), "key 2");
		btree.insert(new Integer(3), "key 3");
		btree.insert(new Integer(4), "key 4");
		btree.insert(new Integer(5), "key 5");
		assertEquals(5, btree.getSize());
		assertEquals("key 5", btree.getBiggest(btree.getRoot(), false).getValue());
		assertEquals("key 1", btree.getSmallest(btree.getRoot(), false).getValue());
	}

	public void testgetBiggestSmallest1WithDelete() throws Exception {
		IBTreeSingleValuePerKey btree = getBTree(3);
		btree.insert(new Integer(1), "key 1");
		btree.insert(new Integer(2), "key 2");
		btree.insert(new Integer(3), "key 3");
		btree.insert(new Integer(4), "key 4");
		btree.insert(new Integer(5), "key 5");
		assertEquals(5, btree.getSize());
		assertEquals("key 5", btree.getBiggest(btree.getRoot(), true).getValue());
		assertEquals("key 1", btree.getSmallest(btree.getRoot(), true).getValue());

		assertEquals(null, btree.search(new Integer(1)));
		assertEquals(null, btree.search(new Integer(5)));
	}

	public void testgetBiggestSmallest1WithDelete2() throws Exception {
		IBTreeSingleValuePerKey btree = getBTree(10);
		int size = 500000;
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}

		assertEquals(size, btree.getSize());
		assertEquals("key 499999", btree.getBiggest(btree.getRoot(), true).getValue());
		assertEquals("key 0", btree.getSmallest(btree.getRoot(), true).getValue());

		assertEquals(null, btree.search(new Integer(0)));
		assertEquals(null, btree.search(new Integer(499999)));
	}

	public void testDelete1() throws Exception {
		IBTree btree = getBTree(10);
		int size = 500000;
		long t0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			btree.insert(new Integer(i), "key " + i);
		}
		long t1 = OdbTime.getCurrentTimeInMs();
		// println("insert time=" + (t1-t0));
		assertEquals(size, btree.getSize());
		assertEquals("key 499999", btree.delete(new Integer(499999), "key 499999"));
	}

}
