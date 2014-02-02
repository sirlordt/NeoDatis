package org.neodatis.btree.impl;

import junit.framework.TestCase;

import org.neodatis.btree.IBTreeNodeOneValuePerKey;
import org.neodatis.btree.IKeyAndValue;
import org.neodatis.btree.impl.singlevalue.InMemoryBTreeNodeSingleValuePerkey;
import org.neodatis.btree.impl.singlevalue.InMemoryBTreeSingleValuePerKey;

public class TestBTreeNodeSingleValue extends TestCase {

	public void setUp() {
	}

	public void testBTreeNodeImpl() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 4);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);
		assertEquals(4, node.getDegree());
		assertEquals(0, node.getNbKeys());
		assertEquals(0, node.getNbChildren());
		assertFalse(node.isFull());
		assertTrue(node.isLeaf());
	}

	/** Extracting right part for non full node */
	public void testExtractRightPart1() {

		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
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

		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(11), "Key 11 value", 1);
		node1.setKeyAndValueAt(new Integer(111), "Key 111 value", 2);
		node1.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 3);
		node1.setKeyAndValueAt(new Integer(11111), "Key 11111 value", 4);

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c4 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c6 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setChildAt(c4, 3);
		node1.setChildAt(c5, 4);
		node1.setChildAt(c6, 5);
		node1.setNbKeys(5);
		node1.setNbChildren(6);

		IBTreeNodeOneValuePerKey rightPart = (IBTreeNodeOneValuePerKey) node1.extractRightPart();

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

		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(11), "Key 11 value", 1);
		node1.setKeyAndValueAt(new Integer(111), "Key 111 value", 2);
		node1.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 3);
		node1.setKeyAndValueAt(new Integer(11111), "Key 11111 value", 4);

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setNbKeys(5);
		node1.setNbChildren(3);

		IBTreeNodeOneValuePerKey rightPart = (IBTreeNodeOneValuePerKey) node1.extractRightPart();

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
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c4 = getBTreeNode(tree);

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
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(11), "Key 11 value", 1);
		node1.setKeyAndValueAt(new Integer(111), "Key 111 value", 2);
		node1.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 3);
		node1.setKeyAndValueAt(new Integer(11111), "Key 11111 value", 4);

		node1.setChildAt(getBTreeNode(tree), 0);
		node1.setChildAt(getBTreeNode(tree), 1);
		node1.setChildAt(getBTreeNode(tree), 2);
		node1.setChildAt(getBTreeNode(tree), 3);
		node1.setChildAt(getBTreeNode(tree), 4);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		node1.setChildAt(c5, 5);

		node1.setNbKeys(5);
		node1.setNbChildren(6);
		assertEquals(new Integer(1), node1.getKeyAt(0));
		assertEquals("Key 1 value", node1.getValueAt(0));

		assertEquals(new Integer(11), node1.getKeyAndValueAt(1).getKey());
		assertEquals("Key 11 value", node1.getKeyAndValueAt(1).getValue());

	}

	public void testGetLastKeyAndValue() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(11), "Key 11 value", 1);
		node1.setKeyAndValueAt(new Integer(111), "Key 111 value", 2);
		node1.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 3);
		node1.setKeyAndValueAt(new Integer(11111), "Key 11111 value", 4);

		node1.setChildAt(getBTreeNode(tree), 0);
		node1.setChildAt(getBTreeNode(tree), 1);
		node1.setChildAt(getBTreeNode(tree), 2);
		node1.setChildAt(getBTreeNode(tree), 3);
		node1.setChildAt(getBTreeNode(tree), 4);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		node1.setChildAt(c5, 5);

		node1.setNbKeys(5);
		node1.setNbChildren(6);
		assertEquals(new Integer(11111), node1.getLastKeyAndValue().getKey());
		assertEquals("Key 11111 value", node1.getLastKeyAndValue().getValue());
	}

	public void testGetMedian() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(11), "Key 11 value", 2);
		node1.setKeyAndValueAt(new Integer(12), "Key 12 value", 3);
		node1.setKeyAndValueAt(new Integer(13), "Key 13 value", 4);
		node1.setNbKeys(5);

		IKeyAndValue median = node1.getMedian();

		assertEquals(new Integer(11), median.getKey());
		assertEquals("Key 11 value", median.getValue());
	}

	public void testGetParent() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey node2 = getBTreeNode(tree);

		node.setParent(node2);

		assertEquals(node2, node.getParent());
	}

	public void testGetPositionOfKey() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);
		node.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node.setKeyAndValueAt(new Integer(10), "Key 2 value", 1);
		node.setKeyAndValueAt(new Integer(100), "Key 3 value", 2);
		node.setKeyAndValueAt(new Integer(1000), "Key 4 value", 3);
		node.setKeyAndValueAt(new Integer(10000), "Key 5 value", 4);
		node.setNbKeys(5);

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
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 4);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);

		assertEquals(0, node.getNbKeys());
		assertEquals(0, node.getNbChildren());

		node.incrementNbKeys();
		node.incrementNbChildren();

		assertEquals(1, node.getNbKeys());
		assertEquals(1, node.getNbChildren());

	}

	public void testInsertKeyAndValue() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 4);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);
		node.insertKeyAndValue("Key 1", "Key 1 value");
		assertEquals("Key 1", node.getKeyAt(0));
		assertEquals("Key 1 value", node.getValueAt(0));
		assertEquals(1, node.getNbKeys());
	}

	public void testSetKeyAndValueAt() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 4);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);
		node.setKeyAndValueAt("Key 1", "Key 1 value", 0);
		node.setKeyAndValueAt("Key 2", "Key 2 value", 1);
		node.setKeyAndValueAt("Key 3", "Key 3 value", 2);
		node.setKeyAndValueAt("Key 4", "Key 4 value", 3);
		node.setKeyAndValueAt("Key 5", "Key 5 value", 4);
		node.setNbKeys(5);
		assertEquals("Key 1", node.getKeyAt(0));
		assertEquals("Key 1 value", node.getValueAt(0));

		assertEquals("Key 5", node.getKeyAndValueAt(4).getKey());
		assertEquals("Key 5 value", node.getKeyAndValueAt(4).getValue());

		assertEquals(5, node.getNbKeys());
	}

	public void testIsFull() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);
		node.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node.setKeyAndValueAt(new Integer(10), "Key 2 value", 1);
		node.setKeyAndValueAt(new Integer(100), "Key 3 value", 2);
		node.setKeyAndValueAt(new Integer(1000), "Key 4 value", 3);
		node.setKeyAndValueAt(new Integer(10000), "Key 5 value", 4);
		node.setNbKeys(5);
		assertTrue(node.isFull());
	}

	public void testIsLeaf() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node = getBTreeNode(tree);
		assertTrue(node.isLeaf());
	}

	public void testMergeWith1() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 2 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 3 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 4 value", 3);
		node1.setKeyAndValueAt(new Integer(10000), "Key 5 value", 4);
		node1.setNbKeys(5);

		IBTreeNodeOneValuePerKey node2 = getBTreeNode(tree);
		node2.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node2.setNbKeys(1);

		try {
			node1.mergeWith(node2);
			fail("mergeWith on full node");
		} catch (Exception e) {

		}

	}

	public void testMergeWith2() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 2 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 3 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 4 value", 3);
		node1.setNbKeys(4);

		IBTreeNodeOneValuePerKey node2 = getBTreeNode(tree);
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

	public void testMergeWith3() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);
		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setNbKeys(3);
		node1.setNbChildren(3);

		IBTreeNodeOneValuePerKey node2 = getBTreeNode(tree);
		node2.setKeyAndValueAt(new Integer(111), "Key 111 value", 0);
		node2.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 1);
		IBTreeNodeOneValuePerKey c4 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);

		node2.setChildAt(c4, 0);
		node2.setChildAt(c5, 1);
		node2.setNbKeys(2);
		node2.setNbChildren(2);

		node1.mergeWith(node2);

		assertEquals(5, node1.getNbKeys());
		assertEquals(5, node1.getNbChildren());
		assertEquals("Key 1 value", node1.getKeyAndValueAt(0).getValue());
		assertEquals("Key 10 value", node1.getKeyAndValueAt(1).getValue());
		assertEquals("Key 100 value", node1.getKeyAndValueAt(2).getValue());
		assertEquals("Key 111 value", node1.getKeyAndValueAt(3).getValue());
		assertEquals("Key 1111 value", node1.getKeyAndValueAt(4).getValue());

		assertEquals(c1, node1.getChildAt(0, false));
		assertEquals(c2, node1.getChildAt(1, false));
		assertEquals(c3, node1.getChildAt(2, false));
		assertEquals(c4, node1.getChildAt(3, false));
		assertEquals(c5, node1.getChildAt(4, false));

	}

	public void testMergeWith4() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setChildAt(getBTreeNode(tree), 0);
		node1.setChildAt(getBTreeNode(tree), 1);
		node1.setNbKeys(2);
		node1.setNbChildren(2);

		IBTreeNodeOneValuePerKey node2 = getBTreeNode(tree);
		node2.setKeyAndValueAt(new Integer(11), "Key 11 value", 0);
		node2.setKeyAndValueAt(new Integer(111), "Key 111 value", 1);
		node2.setNbKeys(2);

		node1.mergeWith(node2);

		assertEquals(4, node1.getNbKeys());
		assertEquals(2, node1.getNbChildren());
		assertEquals("Key 1 value", node1.getKeyAndValueAt(0).getValue());
		assertEquals("Key 10 value", node1.getKeyAndValueAt(1).getValue());
		assertEquals("Key 11 value", node1.getKeyAndValueAt(2).getValue());
		assertEquals("Key 111 value", node1.getKeyAndValueAt(3).getValue());
	}

	public void testMergeWith5() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setNbKeys(0);

		IBTreeNodeOneValuePerKey node2 = getBTreeNode(tree);
		node2.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node2.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node2.setKeyAndValueAt(new Integer(11), "Key 11 value", 2);
		node2.setKeyAndValueAt(new Integer(111), "Key 111 value", 3);
		node2.setKeyAndValueAt(new Integer(1111), "Key 1111 value", 4);
		IBTreeNodeOneValuePerKey child = getBTreeNode(tree);
		node2.setChildAt(child, 0);
		node2.setNbChildren(1);
		node2.setNbKeys(5);

		node1.mergeWith(node2);

		assertEquals(5, node1.getNbKeys());
		assertEquals(1, node2.getNbChildren());
		assertEquals("Key 1 value", node1.getKeyAndValueAt(0).getValue());
		assertEquals("Key 10 value", node1.getKeyAndValueAt(1).getValue());
		assertEquals("Key 11 value", node1.getKeyAndValueAt(2).getValue());
		assertEquals("Key 111 value", node1.getKeyAndValueAt(3).getValue());
		assertEquals("Key 1111 value", node1.getKeyAndValueAt(4).getValue());

		assertEquals(child, node1.getChildAt(0, false));
		assertNull(node1.getChildAt(1, false));
		assertNull(node1.getChildAt(2, false));
		assertNull(node1.getChildAt(3, false));
		assertNull(node1.getChildAt(4, false));
	}

	public void testGetLastChild() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setChildAt(getBTreeNode(tree), 0);
		node1.setChildAt(getBTreeNode(tree), 1);
		node1.setChildAt(getBTreeNode(tree), 2);
		node1.setChildAt(getBTreeNode(tree), 3);
		node1.setChildAt(getBTreeNode(tree), 4);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		node1.setChildAt(c5, 5);

		node1.setNbKeys(2);
		node1.setNbChildren(6);
		assertEquals(c5, node1.getLastChild());

	}

	public void testShiftRight() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = (IBTreeNodeOneValuePerKey) getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c4 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c6 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setChildAt(c4, 3);
		node1.setChildAt(c5, 4);
		node1.setNbKeys(4);
		node1.setNbChildren(5);

		((InMemoryBTreeNodeSingleValuePerkey) node1).rightShiftFrom(2, true);
		assertEquals("Key 1 value", node1.getValueAt(0));
		assertEquals("Key 10 value", node1.getValueAt(1));
		assertEquals(null, node1.getValueAt(2));
		assertEquals("Key 100 value", node1.getValueAt(3));
		assertEquals("Key 1000 value", node1.getValueAt(4));

		assertEquals(c1, node1.getChildAt(0, false));
		assertEquals(c2, node1.getChildAt(1, false));

		assertEquals(null, node1.getChildAt(2, false));

		assertEquals(c3, node1.getChildAt(3, false));
		assertEquals(c4, node1.getChildAt(4, false));
		assertEquals(c5, node1.getChildAt(5, false));
	}

	/** one key two children */
	public void testShiftRight11() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = (IBTreeNodeOneValuePerKey) getBTreeNode(tree);

		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setNbKeys(1);
		node1.setNbChildren(2);

		((InMemoryBTreeNodeSingleValuePerkey) node1).rightShiftFrom(0, true);
		assertEquals(null, node1.getValueAt(0));
		assertEquals("Key 1 value", node1.getValueAt(1));
		assertEquals(null, node1.getValueAt(2));
		assertEquals(null, node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

		assertEquals(null, node1.getChildAt(0, false));
		assertEquals(c1, node1.getChildAt(1, false));
		assertEquals(c2, node1.getChildAt(2, false));
	}

	public void testShiftRight2() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c4 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c6 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setChildAt(c4, 3);
		node1.setChildAt(c5, 4);
		node1.setNbKeys(4);
		node1.setNbChildren(5);

		((InMemoryBTreeNodeSingleValuePerkey) node1).rightShiftFrom(0, true);
		assertEquals(null, node1.getValueAt(0));
		assertEquals("Key 1 value", node1.getValueAt(1));
		assertEquals("Key 10 value", node1.getValueAt(2));
		assertEquals("Key 100 value", node1.getValueAt(3));
		assertEquals("Key 1000 value", node1.getValueAt(4));

		assertEquals(null, node1.getChildAt(0, false));
		assertEquals(c1, node1.getChildAt(1, false));
		assertEquals(c2, node1.getChildAt(2, false));
		assertEquals(c3, node1.getChildAt(3, false));
		assertEquals(c4, node1.getChildAt(4, false));
	}

	public void testShiftLeft() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c4 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c6 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setChildAt(c4, 3);
		node1.setChildAt(c5, 4);
		node1.setNbKeys(4);
		node1.setNbChildren(5);

		((InMemoryBTreeNodeSingleValuePerkey) node1).leftShiftFrom(2, true);
		assertEquals("Key 1 value", node1.getValueAt(0));
		assertEquals("Key 10 value", node1.getValueAt(1));
		assertEquals("Key 1000 value", node1.getValueAt(2));
		assertEquals(null, node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

		assertEquals(c1, node1.getChildAt(0, false));
		assertEquals(c2, node1.getChildAt(1, false));
		assertEquals(c4, node1.getChildAt(2, false));
		assertEquals(c5, node1.getChildAt(3, false));
		assertEquals(null, node1.getChildAt(4, false));
		assertEquals(null, node1.getChildAt(5, false));
	}

	public void testShiftLeft2() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);

		IBTreeNodeOneValuePerKey c1 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c2 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c3 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c4 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c5 = getBTreeNode(tree);
		IBTreeNodeOneValuePerKey c6 = getBTreeNode(tree);

		node1.setChildAt(c1, 0);
		node1.setChildAt(c2, 1);
		node1.setChildAt(c3, 2);
		node1.setChildAt(c4, 3);
		node1.setChildAt(c5, 4);
		node1.setNbKeys(4);
		node1.setNbChildren(5);

		((InMemoryBTreeNodeSingleValuePerkey) node1).leftShiftFrom(0, true);
		assertEquals("Key 10 value", node1.getValueAt(0));
		assertEquals("Key 100 value", node1.getValueAt(1));
		assertEquals("Key 1000 value", node1.getValueAt(2));
		assertEquals(null, node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

		assertEquals(c2, node1.getChildAt(0, false));
		assertEquals(c3, node1.getChildAt(1, false));
		assertEquals(c4, node1.getChildAt(2, false));
		assertEquals(c5, node1.getChildAt(3, false));
		assertEquals(null, node1.getChildAt(4, false));
		assertEquals(null, node1.getChildAt(5, false));
	}

	public void testDeleteKeyOnLeafNode() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);
		node1.setKeyAndValueAt(new Integer(10000), "Key 10000 value", 4);
		node1.setNbKeys(5);

		node1.deleteKeyForLeafNode(new KeyAndValue(new Integer(1), null));
		assertEquals("Key 10 value", node1.getValueAt(0));
		assertEquals("Key 100 value", node1.getValueAt(1));
		assertEquals("Key 1000 value", node1.getValueAt(2));
		assertEquals("Key 10000 value", node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

	}

	public void testDeleteKeyOnLeafNode2() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);
		node1.setKeyAndValueAt(new Integer(10000), "Key 10000 value", 4);
		node1.setNbKeys(5);

		node1.deleteKeyForLeafNode(new KeyAndValue(new Integer(10), null));
		assertEquals("Key 1 value", node1.getValueAt(0));
		assertEquals("Key 100 value", node1.getValueAt(1));
		assertEquals("Key 1000 value", node1.getValueAt(2));
		assertEquals("Key 10000 value", node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

	}

	public void testDeleteKeyOnLeafNode3() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);
		node1.setKeyAndValueAt(new Integer(10000), "Key 10000 value", 4);
		node1.setNbKeys(5);

		node1.deleteKeyForLeafNode(new KeyAndValue(new Integer(1), null));
		assertEquals("Key 10 value", node1.getValueAt(0));
		assertEquals("Key 100 value", node1.getValueAt(1));
		assertEquals("Key 1000 value", node1.getValueAt(2));
		assertEquals("Key 10000 value", node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

	}

	public void testDeleteKeyOnLeafNode4() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);
		node1.setKeyAndValueAt(new Integer(10000), "Key 10000 value", 4);
		node1.setNbKeys(5);

		node1.deleteKeyForLeafNode(new KeyAndValue(new Integer(1000), null));
		assertEquals("Key 1 value", node1.getValueAt(0));
		assertEquals("Key 10 value", node1.getValueAt(1));
		assertEquals("Key 100 value", node1.getValueAt(2));
		assertEquals("Key 10000 value", node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

	}

	public void testDeleteKeyOnLeafNode5() {
		InMemoryBTreeSingleValuePerKey tree = new InMemoryBTreeSingleValuePerKey("test", 3);
		IBTreeNodeOneValuePerKey node1 = getBTreeNode(tree);
		node1.setKeyAndValueAt(new Integer(1), "Key 1 value", 0);
		node1.setKeyAndValueAt(new Integer(10), "Key 10 value", 1);
		node1.setKeyAndValueAt(new Integer(100), "Key 100 value", 2);
		node1.setKeyAndValueAt(new Integer(1000), "Key 1000 value", 3);
		node1.setKeyAndValueAt(new Integer(10000), "Key 10000 value", 4);
		node1.setNbKeys(5);

		node1.deleteKeyForLeafNode(new KeyAndValue(new Integer(10000), null));
		assertEquals("Key 1 value", node1.getValueAt(0));
		assertEquals("Key 10 value", node1.getValueAt(1));
		assertEquals("Key 100 value", node1.getValueAt(2));
		assertEquals("Key 1000 value", node1.getValueAt(3));
		assertEquals(null, node1.getValueAt(4));

	}

	private IBTreeNodeOneValuePerKey getBTreeNode(InMemoryBTreeSingleValuePerKey tree) {
		return new InMemoryBTreeNodeSingleValuePerkey(tree);
	}

}
