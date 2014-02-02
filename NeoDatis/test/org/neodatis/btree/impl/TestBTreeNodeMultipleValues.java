package org.neodatis.btree.impl;

import java.util.Iterator;
import java.util.List;

import org.neodatis.btree.BTreeIteratorMultipleValuesPerKey;
import org.neodatis.btree.IBTreeNodeMultipleValuesPerKey;
import org.neodatis.btree.impl.multiplevalue.InMemoryBTreeMultipleValuesPerKey;
import org.neodatis.btree.impl.multiplevalue.InMemoryBTreeNodeMultipleValuesPerKey;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.test.ODBTest;

public class TestBTreeNodeMultipleValues extends ODBTest {

	public void setUp() {
	}

	public void testBTreeNodeImpl() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 4);
		IBTreeNodeMultipleValuesPerKey node = getBTreeNode(tree);
		assertEquals(4, node.getDegree());
		assertEquals(0, node.getNbKeys());
		assertEquals(0, node.getNbChildren());
		assertFalse(node.isFull());
		assertTrue(node.isLeaf());
	}

	/** Extracting right part for non full node */
	public void testExtractRightPart1() {

		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setChildAt(getBTreeNode(tree), 0);
		node1.setChildAt(getBTreeNode(tree), 1);
		node1.setChildAt(getBTreeNode(tree), 2);
		node1.setNbKeys(2);
		node1.setNbChildren(3);

		try {
			node1.extractRightPart();
			fail("Should not be abble to call extract right parrt on non full nodes");
		} catch (Exception e) {
		}
	}

	/** Extracting right part for full node */
	public void testExtractRightPart2() {

		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(11), "Key 11 value", 1);
		node1.setKeyAndValueAt(new Integer(111), "Key 111 value", 2);
		node1.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 3);
		node1.setKeyAndValueAt(new Integer(11111), "Key 11111 value", 4);

		IBTreeNodeMultipleValuesPerKey c1 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c2 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c3 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c4 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c5 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c6 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setChildAt(c4, 3);
		node1.setChildAt(c5, 4);
		node1.setChildAt(c6, 5);
		node1.setNbKeys(5);
		node1.setNbChildren(6);

		IBTreeNodeMultipleValuesPerKey rightPart = (IBTreeNodeMultipleValuesPerKey) node1.extractRightPart();

		assertEquals(2, node1.getNbKeys());
		assertEquals(3, node1.getNbChildren());

		assertEquals(2, node1.getNbKeys());
		assertEquals(3, node1.getNbChildren());

		assertEquals(new Integer(1), node1.getKeyAndValueAt(0).getKey());
		assertEquals(new Integer(11), node1.getKeyAndValueAt(1).getKey());
		assertEquals(null, node1.getKeyAt(2));
		assertEquals(c1, node1.getChildAt(0, false));
		assertEquals(c2, node1.getChildAt(1, false));
		assertEquals(c3, node1.getChildAt(2, false));
		assertEquals(null, node1.getChildAt(3, false));
		assertEquals(null, node1.getChildAt(4, false));
		assertEquals(null, node1.getChildAt(5, false));

		assertEquals(new Integer(1111), rightPart.getKeyAt(0));
		assertEquals(new Integer(11111), rightPart.getKeyAt(1));
		assertEquals(null, rightPart.getKeyAt(2));
		assertEquals(c4, rightPart.getChildAt(0, false));
		assertEquals(c5, rightPart.getChildAt(1, false));
		assertEquals(c6, rightPart.getChildAt(2, false));
		assertEquals(null, rightPart.getChildAt(3, false));
		assertEquals(null, rightPart.getChildAt(4, false));
		assertEquals(null, rightPart.getChildAt(5, false));
	}

	/** Extracting right part for full node */
	public void testExtractRightPart3() {

		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(11), "Key 11 value", 1);
		node1.setKeyAndValueAt(new Integer(111), "Key 111 value", 2);
		node1.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 3);
		node1.setKeyAndValueAt(new Integer(11111), "Key 11111 value", 4);

		IBTreeNodeMultipleValuesPerKey c1 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c2 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c3 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setNbKeys(5);
		node1.setNbChildren(3);

		IBTreeNodeMultipleValuesPerKey rightPart = (IBTreeNodeMultipleValuesPerKey) node1.extractRightPart();

		assertEquals(2, node1.getNbKeys());
		assertEquals(3, node1.getNbChildren());

		assertEquals(2, rightPart.getNbKeys());
		assertEquals(0, rightPart.getNbChildren());

		assertEquals(new Integer(1), node1.getKeyAt(0));
		assertEquals(new Integer(11), node1.getKeyAt(1));
		assertEquals(null, node1.getKeyAt(2));
		assertEquals(c1, node1.getChildAt(0, false));
		assertEquals(c2, node1.getChildAt(1, false));
		assertEquals(c3, node1.getChildAt(2, false));
		assertEquals(null, node1.getChildAt(3, false));
		assertEquals(null, node1.getChildAt(4, false));
		assertEquals(null, node1.getChildAt(5, false));

		assertEquals(new Integer(1111), rightPart.getKeyAt(0));
		assertEquals(new Integer(11111), rightPart.getKeyAt(1));
		assertEquals(null, rightPart.getKeyAt(2));
		assertEquals(null, rightPart.getChildAt(0, false));
		assertEquals(null, rightPart.getChildAt(1, false));
		assertEquals(null, rightPart.getChildAt(2, false));
		assertEquals(null, rightPart.getChildAt(3, false));
		assertEquals(null, rightPart.getChildAt(4, false));
		assertEquals(null, rightPart.getChildAt(5, false));
	}

	public void testGetChildAt() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		IBTreeNodeMultipleValuesPerKey c1 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c2 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c3 = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey c4 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setChildAt(c4, 3);
		node1.setNbKeys(2);
		node1.setNbChildren(4);

		assertEquals(c1, node1.getChildAt(0, false));
		assertEquals(c2, node1.getChildAt(1, false));
		assertEquals(c3, node1.getChildAt(2, false));
		assertEquals(c4, node1.getChildAt(3, false));
	}

	public void testGetFistKeyAndValue() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(11), "Key 11 value");
		tree.insert(new Integer(111), "Key 111 value");
		tree.insert(new Integer(1111), "Key 1111 value");
		tree.insert(new Integer(11111), "Key 11111 value");

		assertEquals(1, tree.search(new Integer(1)).size());
		assertEquals(5, tree.getSize());

	}

	public void testGetFistKeyAndValue2() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(1), "Key 11 value");
		tree.insert(new Integer(1), "Key 111 value");
		tree.insert(new Integer(11), "Key 1111 value");
		tree.insert(new Integer(11), "Key 11111 value");

		assertEquals(3, tree.search(new Integer(1)).size());
		assertEquals(2, tree.search(new Integer(11)).size());
		assertEquals(5, tree.getSize());

		System.out.println(tree.search(new Integer(1)));
		System.out.println(tree.search(new Integer(11)));
	}

	public void testGetFistKeyAndValue3() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(1), "Key 11 value");
		tree.insert(new Integer(1), "Key 111 value");
		tree.insert(new Integer(11), "Key 1111 value");
		tree.insert(new Integer(11), "Key 11111 value");

		for (int i = 0; i < 1000; i++) {
			tree.insert(new Integer(10), "Key 1 value");
		}

		assertEquals(3, tree.search(new Integer(1)).size());
		assertEquals(2, tree.search(new Integer(11)).size());
		assertEquals(1005, tree.getSize());

		System.out.println(tree.search(new Integer(1)));
		System.out.println(tree.search(new Integer(11)));
	}

	public void testIterator() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(1), "Key 11 value");
		tree.insert(new Integer(1), "Key 111 value");
		tree.insert(new Integer(11), "aKey 1111 value");
		tree.insert(new Integer(11), "aKey 11111 value");
		tree.insert(new Integer(11), "aKey 111111 value");
		tree.insert(new Integer(11), "aKey 1111111 value");
		tree.insert(new Integer(11), "aKey 11111111 value");
		tree.insert(new Integer(11), "aKey 111111111 value");
		tree.insert(new Integer(11), "aKey 1111111111 value");
		tree.insert(new Integer(1), "Key 1111 value");

		tree.insert(new Integer(2), "Key 2 value");
		tree.insert(new Integer(3), "Key 3 value");
		tree.insert(new Integer(4), "Key 4 value");
		tree.insert(new Integer(5), "Key 5 value");
		tree.insert(new Integer(6), "Key 6 value");
		tree.insert(new Integer(7), "Key 7 value");

		println(tree.getSize());
		BTreeIteratorMultipleValuesPerKey iterator = new BTreeIteratorMultipleValuesPerKey(tree, OrderByConstants.ORDER_BY_ASC);
		assertEquals("Key 1 value", iterator.next());
		assertEquals("Key 11 value", iterator.next());
		assertEquals("Key 111 value", iterator.next());
		assertEquals("Key 1111 value", iterator.next());

		assertEquals("Key 2 value", iterator.next());
		assertEquals("Key 3 value", iterator.next());
		assertEquals("Key 4 value", iterator.next());
		assertEquals("Key 5 value", iterator.next());
		assertEquals("Key 6 value", iterator.next());
		assertEquals("Key 7 value", iterator.next());

	}

	public void testIteratorDesc() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(1), "Key 11 value");
		tree.insert(new Integer(1), "Key 111 value");
		tree.insert(new Integer(11), "aKey 1111 value");
		tree.insert(new Integer(11), "aKey 11111 value");
		tree.insert(new Integer(11), "aKey 111111 value");
		tree.insert(new Integer(11), "aKey 1111111 value");
		tree.insert(new Integer(11), "aKey 11111111 value");
		tree.insert(new Integer(11), "aKey 111111111 value");
		tree.insert(new Integer(11), "aKey 1111111111 value");
		tree.insert(new Integer(1), "Key 1111 value");

		tree.insert(new Integer(2), "Key 2 value");
		tree.insert(new Integer(3), "Key 3 value");
		tree.insert(new Integer(4), "Key 4 value");
		tree.insert(new Integer(5), "Key 5 value");
		tree.insert(new Integer(6), "Key 6 value");
		tree.insert(new Integer(7), "Key 7 value");

		println(tree.getSize());
		BTreeIteratorMultipleValuesPerKey iterator = new BTreeIteratorMultipleValuesPerKey(tree, OrderByConstants.ORDER_BY_DESC);

		assertEquals("aKey 1111 value", iterator.next());
		assertEquals("aKey 11111 value", iterator.next());
		assertEquals("aKey 111111 value", iterator.next());
		assertEquals("aKey 1111111 value", iterator.next());
		assertEquals("aKey 11111111 value", iterator.next());
		assertEquals("aKey 111111111 value", iterator.next());
		assertEquals("aKey 1111111111 value", iterator.next());

		assertEquals("Key 7 value", iterator.next());
		assertEquals("Key 6 value", iterator.next());
		assertEquals("Key 5 value", iterator.next());
		assertEquals("Key 4 value", iterator.next());
		assertEquals("Key 3 value", iterator.next());
		assertEquals("Key 2 value", iterator.next());

		assertEquals("Key 1 value", iterator.next());
		assertEquals("Key 11 value", iterator.next());
		assertEquals("Key 111 value", iterator.next());
		assertEquals("Key 1111 value", iterator.next());
	}

	public void testIteratorDesc2() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(2), "Key 2 value");
		tree.insert(new Integer(3), "Key 3 value");
		tree.insert(new Integer(4), "Key 4 value");

		println(tree.getSize());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_DESC);

		assertEquals("Key 4 value", iterator.next());
		assertEquals("Key 3 value", iterator.next());
		assertEquals("Key 2 value", iterator.next());
		assertEquals("Key 1 value", iterator.next());
	}

	public void testGetParent() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node = getBTreeNode(tree);
		IBTreeNodeMultipleValuesPerKey node2 = getBTreeNode(tree);

		node.setParent(node2);

		assertEquals(node2, node.getParent());
	}

	public void testGetPositionOfKey() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(10), "Key 11 value");
		tree.insert(new Integer(100), "Key 111 value");
		tree.insert(new Integer(1000), "Key 1111 value");
		tree.insert(new Integer(10000), "Key 11111 value");

		IBTreeNodeMultipleValuesPerKey node = (IBTreeNodeMultipleValuesPerKey) tree.getRoot();

		assertEquals(1, node.getPositionOfKey(new Integer(1)));
		assertEquals(2, node.getPositionOfKey(new Integer(10)));
		assertEquals(3, node.getPositionOfKey(new Integer(100)));
		assertEquals(4, node.getPositionOfKey(new Integer(1000)));
		assertEquals(5, node.getPositionOfKey(new Integer(10000)));

		assertEquals(-2, node.getPositionOfKey(new Integer(2)));
		assertEquals(-3, node.getPositionOfKey(new Integer(11)));
		assertEquals(-4, node.getPositionOfKey(new Integer(101)));
		assertEquals(-5, node.getPositionOfKey(new Integer(1001)));
		assertEquals(-6, node.getPositionOfKey(new Integer(10001)));

		assertEquals(-1, node.getPositionOfKey(new Integer(0)));

		assertEquals(5, node.getNbKeys());
	}

	public void testIncrementNbChildrenAndKeys() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 4);
		IBTreeNodeMultipleValuesPerKey node = getBTreeNode(tree);

		assertEquals(0, node.getNbKeys());
		assertEquals(0, node.getNbChildren());

		node.incrementNbKeys();
		node.incrementNbChildren();

		assertEquals(1, node.getNbKeys());
		assertEquals(1, node.getNbChildren());

	}

	public void testInsertKeyAndValue() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 4);
		IBTreeNodeMultipleValuesPerKey node = getBTreeNode(tree);
		node.insertKeyAndValue("Key 1", "Key 1 value");
		assertEquals(1, node.getValueAt(0).size());

		assertEquals("Key 1", node.getKeyAt(0));
		assertEquals("Key 1 value", node.getValueAt(0).get(0));
		assertEquals(1, node.getNbKeys());
	}

	public void testIsFull() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(10), "Key 11 value");
		tree.insert(new Integer(100), "Key 111 value");
		tree.insert(new Integer(1000), "Key 1111 value");
		tree.insert(new Integer(10000), "Key 11111 value");

		assertTrue(tree.getRoot().isFull());
	}

	public void testIsNotFull() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(1), "Key 11 value");
		tree.insert(new Integer(1), "Key 111 value");
		tree.insert(new Integer(1), "Key 1111 value");
		tree.insert(new Integer(1), "Key 11111 value");

		assertFalse(tree.getRoot().isFull());
	}

	public void testIsLeaf() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node = getBTreeNode(tree);
		assertTrue(node.isLeaf());
	}

	public void testMergeWith1() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 2 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 3 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 4 value", 3);
		node1.setKeyAndValueAt(new Integer(10000), "Key 5 value", 4);
		node1.setNbKeys(5);

		IBTreeNodeMultipleValuesPerKey node2 = getBTreeNode(tree);
		node2.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node2.setNbKeys(1);

		try {
			node1.mergeWith(node2);
			fail("mergeWith on full node");
		} catch (Exception e) {

		}

	}

	public void testMergeWith2() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 2 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 3 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 4 value", 3);
		node1.setNbKeys(4);

		IBTreeNodeMultipleValuesPerKey node2 = getBTreeNode(tree);
		node2.setKeyAndValueAt(new Integer(11), "Key 11 value", 0);
		node2.setKeyAndValueAt(new Integer(111), "Key 111 value", 0);
		node2.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 0);
		node2.setNbKeys(3);

		try {
			node1.mergeWith(node2);
			fail("mergeWith on full node");
		} catch (Exception e) {

		}
	}

	public void testGetLastChild() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);
		IBTreeNodeMultipleValuesPerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setChildAt(getBTreeNode(tree), 0);
		node1.setChildAt(getBTreeNode(tree), 1);
		node1.setChildAt(getBTreeNode(tree), 2);
		node1.setChildAt(getBTreeNode(tree), 3);
		node1.setChildAt(getBTreeNode(tree), 4);
		IBTreeNodeMultipleValuesPerKey c5 = getBTreeNode(tree);
		node1.setChildAt(c5, 5);

		node1.setNbKeys(2);
		node1.setNbChildren(6);
		assertEquals(c5, node1.getLastChild());

	}

	public void testDeleteKeyOnLeafNode() {
		InMemoryBTreeMultipleValuesPerKey tree = new InMemoryBTreeMultipleValuesPerKey("test", 3);

		tree.insert(new Integer(1), "Key 1 value");
		tree.insert(new Integer(1), "Key 11 value");
		tree.insert(new Integer(1), "Key 111 value");
		tree.insert(new Integer(1), "Key 1111 value");
		tree.insert(new Integer(1), "Key 11111 value");

		tree.delete(new Integer(1), "Key 1111 value");

		assertEquals(4, tree.getSize());

		List l = tree.search(new Integer(1));
		assertNotNull(l);
		assertEquals(4, l.size());
		// try to delete the already deleted value
		Object o = tree.delete(new Integer(1), "Key 1111 value");
		assertEquals(null, o);
		assertEquals(4, tree.getSize());

		tree.delete(new Integer(1), "Key 1 value");
		assertEquals(3, tree.getSize());

		tree.delete(new Integer(1), "Key 11 value");
		assertEquals(2, tree.getSize());

		tree.delete(new Integer(1), "Key 111 value");
		assertEquals(1, tree.getSize());

		tree.delete(new Integer(1), "Key 11111 value");
		assertEquals(0, tree.getSize());
	}

	private IBTreeNodeMultipleValuesPerKey getBTreeNode(InMemoryBTreeMultipleValuesPerKey tree) {
		return new InMemoryBTreeNodeMultipleValuesPerKey(tree);
	}

}
