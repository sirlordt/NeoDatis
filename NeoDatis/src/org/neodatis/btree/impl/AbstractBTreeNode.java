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
package org.neodatis.btree.impl;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeNode;
import org.neodatis.btree.IKeyAndValue;
import org.neodatis.btree.exception.BTreeException;
import org.neodatis.btree.exception.BTreeNodeValidationException;
import org.neodatis.btree.tool.BTreeValidator;

public abstract class AbstractBTreeNode implements IBTreeNode {
	protected int degree;

	protected Comparable[] keys;
	protected Object[] values;
	protected int nbKeys;
	protected int nbChildren;
	protected int maxNbKeys;
	protected int maxNbChildren;

	/** The BTree owner of this node */
	transient protected IBTree btree;

	public AbstractBTreeNode() {
		this.btree = null;
		this.degree = -1;
		this.maxNbKeys = -1;
		this.maxNbChildren = -1;

		keys = null;
		values = null;
		nbKeys = 0;
		nbChildren = 0;
	}

	public AbstractBTreeNode(IBTree btree) {
		basicInit(btree);
	}

	private void basicInit(IBTree btree) {
		this.btree = btree;
		this.degree = btree.getDegree();
		this.maxNbKeys = 2 * degree - 1;
		this.maxNbChildren = 2 * degree;

		keys = new Comparable[maxNbKeys];
		values = new Object[maxNbKeys];
		nbKeys = 0;
		nbChildren = 0;
		init();
	}

	public abstract void insertKeyAndValue(Comparable key, Object value);
	
	protected abstract void init();

	public abstract IBTreeNode getChildAt(int index, boolean throwExceptionIfNotExist);

	public abstract IBTreeNode getParent();

	public abstract Object getParentId();

	public abstract void setParent(IBTreeNode node);

	public abstract boolean hasParent();

	public abstract void moveChildFromTo(int sourceIndex, int destinationIndex, boolean throwExceptionIfDoesNotExist);

	/**
	 * Creates a new node with the right part of this node. This should only be
	 * called on a full node
	 */
	public IBTreeNode extractRightPart() {
		if (!isFull()) {
			throw new BTreeException("extract right part called on non full node");
		}
		// Creates an empty new node
		IBTreeNode rightPart = btree.buildNode();

		int j = 0;
		for (int i = degree; i < maxNbKeys; i++) {
			rightPart.setKeyAndValueAt(keys[i], values[i], j, false, false);
			keys[i] = null;
			values[i] = null;
			rightPart.setChildAt(this, i, j, false);

			// TODO must we load all nodes to set new parent
			IBTreeNode c = rightPart.getChildAt(j, false);
			if (c != null) {
				c.setParent(rightPart);
			}

			// rightPart.setChildAt(getChildAt(i,false), j);
			setNullChildAt(i);
			j++;
		}

		// rightPart.setChildAt(getLastPositionChild(), j);
		rightPart.setChildAt(this, getMaxNbChildren() - 1, j, false);
		// correct father id
		IBTreeNode c1 = rightPart.getChildAt(j, false);
		if (c1 != null) {
			c1.setParent(rightPart);
		}
		setNullChildAt(maxNbChildren - 1); // resets last child
		keys[degree - 1] = null;// resets median element
		values[degree - 1] = null;
		// set numbers
		nbKeys = degree - 1;
		int originalNbChildren = nbChildren;
		nbChildren = Math.min(nbChildren, degree);
		rightPart.setNbKeys(degree - 1);
		rightPart.setNbChildren(originalNbChildren - nbChildren);

		BTreeValidator.validateNode(this);
		BTreeValidator.validateNode(rightPart);
		BTreeValidator.checkDuplicateChildren(this, rightPart);

		return rightPart;
	}

	public IKeyAndValue getKeyAndValueAt(int index) {
		if (keys[index] == null && values[index] == null) {
			return null;
		}
		return new KeyAndValue(keys[index], values[index]);
	}

	public IKeyAndValue getLastKeyAndValue() {
		return getKeyAndValueAt(nbKeys - 1);
	}

	public Comparable getKeyAt(int index) {
		return keys[index];
	}

	public IKeyAndValue getMedian() {
		int medianPosition = degree - 1;
		return getKeyAndValueAt(medianPosition);
	}

	/**
	 * Returns the position of the key. If the key does not exist in node,
	 * returns the position where this key should be,multiplied by -1
	 * 
	 * <pre>
	 * for example for node of degree 3 : [1 89 452 789 - ],
	 *  calling getPositionOfKey(89) returns 2 (starts with 1)
	 *  calling getPositionOfKey(99) returns -2 (starts with 1),because the position should be done, but it does not exist so multiply by -1
	 * this is used to know the child we should descend to!in this case the getChild(2). 
	 * 
	 * </pre>
	 * 
	 * @param key
	 * @return The position of the key,as a negative number if key does not
	 *         exist, warning, the position starts with 1and not 0!
	 */
	public int getPositionOfKey(Comparable key) {
		int i = 0;
		while (i < nbKeys) {
			int result = keys[i].compareTo(key);
			if (result == 0) {
				return i + 1;
			}
			if (result > 0) {
				return -(i + 1);
			}
			i++;
		}
		return -(i + 1);
	}

	public void incrementNbChildren() {
		nbChildren++;

	}

	public void incrementNbKeys() {
		nbKeys++;
	}

	protected void rightShiftFrom(int position, boolean shiftChildren) {
		if (isFull()) {
			throw new BTreeException("Node is full, can't right shift!");
		}

		// Shift keys
		for (int i = nbKeys; i > position; i--) {
			keys[i] = keys[i - 1];
			values[i] = values[i - 1];
		}
		keys[position] = null;
		values[position] = null;

		// Shift children
		if (shiftChildren) {
			for (int i = nbChildren; i > position; i--) {
				moveChildFromTo(i - 1, i, true);
				// setChildAt(getChildAt(i-1,true),i);
			}
			setNullChildAt(position);
		}
	}

	protected void leftShiftFrom(int position, boolean shiftChildren) {
		for (int i = position; i < nbKeys - 1; i++) {
			keys[i] = keys[i + 1];
			values[i] = values[i + 1];
			if (shiftChildren) {
				moveChildFromTo(i + 1, i, false);
				// setChildAt(getChildAt(i+1,false), i);
			}
		}
		keys[nbKeys - 1] = null;
		values[nbKeys - 1] = null;
		if (shiftChildren) {
			moveChildFromTo(nbKeys, nbKeys - 1, false);
			// setChildAt(getChildAt(nbKeys,false), nbKeys-1);
			setNullChildAt(nbKeys);
		}
	}

	public void setKeyAndValueAt(Comparable key, Object value, int index) {
		keys[index] = key;
		values[index] = value;
	}

	public void setKeyAndValueAt(IKeyAndValue keyAndValue, int index) {
		setKeyAndValueAt(keyAndValue.getKey(), keyAndValue.getValue(), index);
	}

	public void setKeyAndValueAt(Comparable key, Object value, int index, boolean shiftIfAlreadyExist, boolean incrementNbKeys) {
		if (shiftIfAlreadyExist && index < nbKeys) {
			rightShiftFrom(index, true);
		}
		keys[index] = key;
		values[index] = value;
		if (incrementNbKeys) {
			nbKeys++;
		}
	}

	public void setKeyAndValueAt(IKeyAndValue keyAndValue, int index, boolean shiftIfAlreadyExist, boolean incrementNbKeys) {
		setKeyAndValueAt(keyAndValue.getKey(), keyAndValue.getValue(), index, shiftIfAlreadyExist, incrementNbKeys);
	}

	public boolean isFull() {
		return nbKeys == maxNbKeys;
	}

	public boolean isLeaf() {
		return nbChildren == 0;
	}

	/**
	 * Can only merge node without intersection => the greater key of this must
	 * be smaller than the smallest key of the node
	 * 
	 */
	public void mergeWith(IBTreeNode node) {

		BTreeValidator.validateNode(this);
		BTreeValidator.validateNode(node);
		checkIfCanMergeWith(node);
		int j = nbKeys;
		for (int i = 0; i < node.getNbKeys(); i++) {
			setKeyAndValueAt(node.getKeyAt(i), node.getValueAsObjectAt(i), j, false, false);
			setChildAt(node, i, j, false);

			j++;
		}
		// in this, we have to take the last child
		if (node.getNbChildren() > node.getNbKeys()) {
			setChildAt(node, node.getNbChildren() - 1, j, true);
		}
		nbKeys += node.getNbKeys();
		nbChildren += node.getNbChildren();
		BTreeValidator.validateNode(this);
	}

	private void checkIfCanMergeWith(IBTreeNode node) {
		if (nbKeys + node.getNbKeys() > maxNbKeys) {
			throw new BTreeException("Trying to merge two nodes with too many keys " + nbKeys + " + " + node.getNbKeys() + " > "
					+ maxNbKeys);
		}
		if (nbKeys > 0) {
			Comparable greatestOfThis = keys[nbKeys - 1];
			Comparable smallestOfOther = node.getKeyAt(0);
			if (greatestOfThis.compareTo(smallestOfOther) >= 0) {
				throw new BTreeNodeValidationException("Trying to merge two nodes that have intersections :  " + toString() + " / " + node);
			}
		}
		if (nbKeys < nbChildren) {
			throw new BTreeNodeValidationException("Trying to merge two nodes where the first one has more children than keys");
		}
	}

	public void removeKeyAndValueAt(int index) {
		throw new BTreeException("Not implemented");
	}

	public IBTreeNode getLastChild() {
		return getChildAt(nbChildren - 1, true);
	}

	public IBTreeNode getLastPositionChild() {
		return getChildAt(maxNbChildren - 1, false);
	}

	public int getNbKeys() {
		return nbKeys;
	}

	public void setNbChildren(int nbChildren) {
		this.nbChildren = nbChildren;
	}

	public void setNbKeys(int nbKeys) {
		this.nbKeys = nbKeys;
	}

	public int getDegree() {
		return degree;
	}

	public int getNbChildren() {
		return nbChildren;
	}

	public Object deleteKeyForLeafNode(IKeyAndValue keyAndValue) {
		int position = getPositionOfKey(keyAndValue.getKey());
		if (position < 0) {
			return null;
		}
		int realPosition = position - 1;
		Object value = values[realPosition];
		leftShiftFrom(realPosition, false);
		nbKeys--;
		BTreeValidator.validateNode(this);
		return value;
	}

	public Object deleteKeyAndValueAt(int keyIndex, boolean shiftChildren) {
		Object currentValue = values[keyIndex];
		leftShiftFrom(keyIndex, shiftChildren);
		nbKeys--;

		// only decrease child number if child are involved in shifting
		if (shiftChildren && nbChildren > keyIndex) {
			nbChildren--;
		}
		// BTreeValidator.validateNode(this);
		return currentValue;

	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("id=").append(getId()).append(" {keys(").append(nbKeys).append(")=(");
		for (int i = 0; i < nbKeys; i++) {
			if (i > 0) {
				buffer.append(",");
			}
			buffer.append(keys[i]).append("/").append(values[i]);
		}
		buffer.append("), child(").append(nbChildren).append(")}");
		return buffer.toString();
	}

	public int getMaxNbChildren() {
		return maxNbChildren;
	}

	public void setBTree(IBTree btree) {
		this.btree = btree;
	}

	public IBTree getBTree() {
		return btree;
	}

	public void clear() {
		basicInit(btree);
	}
}
