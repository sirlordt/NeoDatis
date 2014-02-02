package org.neodatis.btree;

import java.util.List;

import org.neodatis.odb.core.OrderByConstants;

/**
 * An iterator to iterate over NeoDatis BTree that accept more than one value
 * per key. This is used for non unique index and collection that return ordered
 * by results
 * 
 * @author olivier
 * 
 */
public class BTreeIteratorMultipleValuesPerKey extends AbstractBTreeIterator {

	/**
	 * @param tree
	 * @param orderByType
	 */
	public BTreeIteratorMultipleValuesPerKey(IBTree tree, OrderByConstants orderByType) {
		super(tree, orderByType);
		currenListIndex = 0;
		currentValue = null;
	}

	/**
	 * The index in the list of the current value, Here values of a key are
	 * lists!
	 */
	private int currenListIndex;
	/** The current value(List) of the current key being read. */
	private List currentValue;

	public Object next() {
		// Here , the value of a specific key is a list, so we must iterate
		// through the list before going
		// to the next node
		if (currentNode != null && currentValue != null) {
			int listSize = currentValue.size();
			if (listSize > currenListIndex) {
				Object value = currentValue.get(currenListIndex);
				currenListIndex++;
				nbReturnedElements++;
				return value;
			}
			// We have reached the end of the list or the list is empty
			// We must continue iterate in the current node / btree
			currenListIndex = 0;
			currentValue = null;
		}

		return super.next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.btree.AbstractBTreeIterator#getValueAt(org.neodatis.btree
	 * .IBTreeNode, int)
	 */
	public Object getValueAt(IBTreeNode node, int currentIndex) {
		if (currentValue == null) {
			currentValue = (List) node.getValueAsObjectAt(currentIndex);
		}
		int listSize = currentValue.size();
		if (listSize > currenListIndex) {
			Object value = currentValue.get(currenListIndex);
			currenListIndex++;
			return value;
		}
		// We have reached the end of the list or the list is empty
		// We must continue iterate in the current node / btree
		currenListIndex = 0;
		currentValue = null;
		return null;
	}
}
