package org.neodatis.btree;

import org.neodatis.odb.core.OrderByConstants;

/**
 * An iterator to iterate over NeoDatis BTree.
 * 
 * @author olivier
 * 
 */
public class BTreeIteratorSingleValuePerKey extends AbstractBTreeIterator {

	public BTreeIteratorSingleValuePerKey(IBTree tree, OrderByConstants orderByType) {
		super(tree, orderByType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.btree.AbstractBTreeIterator#getValueAt(org.neodatis.btree
	 * .IBTreeNode, int)
	 */
	public Object getValueAt(IBTreeNode node, int currentIndex) {
		IBTreeNodeOneValuePerKey n = (IBTreeNodeOneValuePerKey) node;
		return n.getValueAt(currentIndex);
	}
}
