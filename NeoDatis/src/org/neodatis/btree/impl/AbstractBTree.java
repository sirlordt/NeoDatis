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
import org.neodatis.btree.IBTreePersister;
import org.neodatis.btree.IKeyAndValue;
import org.neodatis.btree.exception.BTreeNodeValidationException;
import org.neodatis.btree.tool.BTreeValidator;

public abstract class AbstractBTree implements IBTree {
	private String name;

	private int degree;

	private long size;

	private int height;

	private IBTreeNode root;

	protected transient IBTreePersister persister;

	protected int controlNumber;

	public abstract IBTreeNode buildNode();

	public AbstractBTree() {
		this.degree = 0;
		this.size = 0;
		this.height = 1;
		this.persister = null;
		root = null;
	}

	public AbstractBTree(String name, int degree, IBTreePersister persister) {
		this.name = name;
		this.degree = degree;
		this.size = 0;
		this.height = 1;
		this.persister = persister;
		root = buildNode();
		// TODO check if it is needed to store the root before the btree ->
		// saving btree will try to update root!
		persister.saveNode(root);
		persister.saveBTree(this);
		persister.flush();
	}

	/**
	 * TODO Manage collision
	 */
	public Object delete(Comparable key, Object value) {
		Object o = null;

		try {
			o = internalDelete(root, new KeyAndValue(key, value));
		} catch (Exception e) {
			throw new BTreeNodeValidationException("Error while deleting key='" + key + "' value='" + value + "'", e);
		}
		if (o != null) {
			size--;
		}
		getPersister().saveBTree(this);
		// TODO flush or not?
		// persister.flush();
		return o;
	}

	/**
	 * Returns the value of the deleted key
	 * 
	 * @param node
	 * @param keyAndValue
	 * @return
	 * @throws Exception
	 */
	protected Object internalDelete(IBTreeNode node, IKeyAndValue keyAndValue) throws Exception {

		int position = node.getPositionOfKey(keyAndValue.getKey());
		boolean keyIsHere = position > 0;
		int realPosition = -1;
		IBTreeNode leftNode = null;
		IBTreeNode rightNode = null;
		try {

			if (node.isLeaf()) {
				if (keyIsHere) {
					Object deletedValue = node.deleteKeyForLeafNode(keyAndValue);
					getPersister().saveNode(node);
					return deletedValue;
				}
				// key does not exist
				return null;
			}

			if (!keyIsHere) {
				// descend
				realPosition = -position - 1;
				IBTreeNode child = node.getChildAt(realPosition, true);
				if (child.getNbKeys() == degree - 1) {
					node = prepareForDelete(node, child, realPosition);
					return internalDelete(node, keyAndValue);
				}
				return internalDelete(child, keyAndValue);
			}

			// Here,the node is not a leaf and contains the key
			realPosition = position - 1;
			Comparable currentKey = node.getKeyAt(realPosition);
			Object currentValue = node.getValueAsObjectAt(realPosition);
			// case 2a
			leftNode = node.getChildAt(realPosition, true);
			if (leftNode.getNbKeys() >= degree) {
				IKeyAndValue prev = getBiggest(leftNode, true);
				node.setKeyAndValueAt(prev, realPosition);
				BTreeValidator.validateNode(node, node == root);
				getPersister().saveNode(node);
				return currentValue;
			}
			// case 2b
			rightNode = node.getChildAt(realPosition + 1, true);
			if (rightNode.getNbKeys() >= degree) {
				IKeyAndValue next = getSmallest(rightNode, true);
				node.setKeyAndValueAt(next, realPosition);
				BTreeValidator.validateNode(node, node == root);
				getPersister().saveNode(node);
				return currentValue;
			}
			// case 2c
			// Here, both left and right part have degree-1 keys
			// remove the element to be deleted from node (shifting left all
			// right
			// elements, link to right link does not exist anymore)
			// insert the key to be deleted in left child and merge the 2 nodes.
			// rightNode should be deleted
			// if node is root, then leftNode becomes the new root and node
			// should be deleted
			// 
			node.deleteKeyAndValueAt(realPosition, true);
			leftNode.insertKeyAndValue(currentKey, currentValue);
			leftNode.mergeWith(rightNode);
			// If node is the root and is empty
			if (!node.hasParent() && node.getNbKeys() == 0) {
				persister.deleteNode(node);
				root = leftNode;
				leftNode.setParent(null);
				// The height has been decreased. No need to save btree here.
				// The calling delete method will save it.
				height--;

			} else {
				node.setChildAt(leftNode, realPosition);
				// Node must only be validated if it is not the root
				BTreeValidator.validateNode(node, node == root);
			}
			persister.deleteNode(rightNode);

			BTreeValidator.validateNode(leftNode, leftNode == root);
			getPersister().saveNode(node);
			getPersister().saveNode(leftNode);
			return internalDelete(leftNode, keyAndValue);

		} finally {
		}

	}

	private IBTreeNode prepareForDelete(IBTreeNode parent, IBTreeNode child, int childIndex) {

		BTreeValidator.validateNode(parent);
		BTreeValidator.validateNode(child);

		// case 3a
		IBTreeNode leftSibling = null;
		IBTreeNode rightSibling = null;
		try {
			if (childIndex > 0 && parent.getNbChildren() > 0) {
				leftSibling = parent.getChildAt(childIndex - 1, false);
			}
			if (childIndex < parent.getNbChildren() - 1) {
				rightSibling = parent.getChildAt(childIndex + 1, false);
			}

			// case 3a left
			if (leftSibling != null && leftSibling.getNbKeys() >= degree) {
				IKeyAndValue elementToMoveDown = parent.getKeyAndValueAt(childIndex - 1);
				IKeyAndValue elementToMoveUp = leftSibling.getLastKeyAndValue();
				parent.setKeyAndValueAt(elementToMoveUp, childIndex - 1);
				child.insertKeyAndValue(elementToMoveDown.getKey(), elementToMoveDown.getValue());
				if (leftSibling.getNbChildren() > leftSibling.getNbKeys()) {
					// Take the last child of the left sibling and set it the
					// first child of the 'child' (incoming parameter)
					// child.setChildAt(leftSibling.getChildAt(leftSibling.getNbChildren()
					// - 1, true), 0);
					child.setChildAt(leftSibling, leftSibling.getNbChildren() - 1, 0, true);
					child.incrementNbChildren();
				}
				leftSibling.deleteKeyAndValueAt(leftSibling.getNbKeys() - 1, false);
				if (!leftSibling.isLeaf()) {
					leftSibling.deleteChildAt(leftSibling.getNbChildren() - 1);
				}

				persister.saveNode(parent);
				persister.saveNode(child);
				persister.saveNode(leftSibling);

				if (BTreeValidator.isOn()) {
					BTreeValidator.validateNode(parent, parent == root);
					BTreeValidator.validateNode(child, false);
					BTreeValidator.validateNode(leftSibling, false);
					BTreeValidator.checkDuplicateChildren(leftSibling, child);
				}
				return parent;
			}
			// case 3a right
			if (rightSibling != null && rightSibling.getNbKeys() >= degree) {
				IKeyAndValue elementToMoveDown = parent.getKeyAndValueAt(childIndex);
				IKeyAndValue elementToMoveUp = rightSibling.getKeyAndValueAt(0);
				parent.setKeyAndValueAt(elementToMoveUp, childIndex);
				child.insertKeyAndValue(elementToMoveDown.getKey(), elementToMoveDown.getValue());
				if (rightSibling.getNbChildren() > 0) {
					// Take the first child of the right sibling and set it the
					// last child of the 'child' (incoming parameter)
					child.setChildAt(rightSibling, 0, child.getNbChildren(), true);
					child.incrementNbChildren();
				}
				rightSibling.deleteKeyAndValueAt(0, true);

				persister.saveNode(parent);
				persister.saveNode(child);
				persister.saveNode(rightSibling);

				if (BTreeValidator.isOn()) {
					BTreeValidator.validateNode(parent, parent == root);
					BTreeValidator.validateNode(child, false);
					BTreeValidator.validateNode(rightSibling, false);
					BTreeValidator.checkDuplicateChildren(rightSibling, child);
				}
				return parent;
			}

			// case 3b
			boolean isCase3b = (leftSibling != null && leftSibling.getNbKeys() == degree - 1)
					|| (rightSibling != null && rightSibling.getNbKeys() >= degree - 1);
			boolean parentWasSetToNull = false;
			if (isCase3b) {
				// choose left sibling to execute merge
				if (leftSibling != null) {
					IKeyAndValue elementToMoveDown = parent.getKeyAndValueAt(childIndex - 1);
					leftSibling.insertKeyAndValue(elementToMoveDown.getKey(), elementToMoveDown.getValue());
					leftSibling.mergeWith(child);
					parent.deleteKeyAndValueAt(childIndex - 1, true);
					if (parent.getNbKeys() == 0) {
						// this is the root
						if (!parent.hasParent()) {
							root = leftSibling;
							root.setParent(null);
							height--;
							parentWasSetToNull = true;
						} else {
							throw new BTreeNodeValidationException("Unexpected empty node that is node the root!");
						}
					} else {
						parent.setChildAt(leftSibling, childIndex - 1);
					}
					if (parentWasSetToNull) {
						persister.deleteNode(parent);
					} else {
						persister.saveNode(parent);
						BTreeValidator.validateNode(parent, parent == root);
					}

					// child was merged with another node it must be deleted
					persister.deleteNode(child);
					persister.saveNode(leftSibling);

					// Validator.validateNode(child, child == root);
					BTreeValidator.validateNode(leftSibling, leftSibling == root);
					// Validator.checkDuplicateChildren(leftSibling, child);
					if (parentWasSetToNull) {
						return root;
					}
					return parent;
				}
				// choose right sibling to execute merge
				if (rightSibling != null) {
					IKeyAndValue elementToMoveDown = parent.getKeyAndValueAt(childIndex);
					child.insertKeyAndValue(elementToMoveDown.getKey(), elementToMoveDown.getValue());
					child.mergeWith(rightSibling);
					parent.deleteKeyAndValueAt(childIndex, true);

					if (parent.getNbKeys() == 0) {
						// this is the root
						if (!parent.hasParent()) {
							root = child;
							root.setParent(null);
							height--;
							parentWasSetToNull = true;
						} else {
							throw new BTreeNodeValidationException("Unexpected empty root node!");
						}
					} else {
						parent.setChildAt(child, childIndex);
					}
					if (parentWasSetToNull) {
						persister.deleteNode(parent);
					} else {
						persister.saveNode(parent);
						BTreeValidator.validateNode(parent, parent == root);
					}
					persister.deleteNode(rightSibling);
					persister.saveNode(child);

					BTreeValidator.validateNode(child, child == root);
					// Validator.validateNode(rightSibling, rightSibling ==
					// root);
					// Validator.checkDuplicateChildren(rightSibling, child);
					if (parentWasSetToNull) {
						return root;
					}
					return parent;
				}
				throw new BTreeNodeValidationException("deleting case 3b but no non null sibling!");
			}
		} finally {
		}
		throw new BTreeNodeValidationException("Unexpected case in executing prepare for delete");

	}

	public int getDegree() {
		return degree;
	}

	public void insert(Comparable key, Object value) {
		// check if root is full
		if (root.isFull()) {
			IBTreeNode newRoot = buildNode();
			IBTreeNode oldRoot = root;
			newRoot.setChildAt(root, 0);
			newRoot.setNbChildren(1);
			root = newRoot;
			split(newRoot, oldRoot, 0);
			height++;

			persister.saveNode(oldRoot);
			// TODO Remove the save of the new root : the save on the btree
			// should do the save on the new root(after introspector
			// refactoring)
			persister.saveNode(newRoot);
			persister.saveBTree(this);

			BTreeValidator.validateNode(newRoot, true);
		}
		insertNonFull(root, key, value);
		size++;
		persister.saveBTree(this);
		// Commented by Olivier 05/11/2007
		// persister.flush();
	}

	private void insertNonFull(IBTreeNode node, Comparable key, Object value) {
		if (node.isLeaf()) {
			node.insertKeyAndValue(key, value);
			persister.saveNode(node);
			return;
		}
		int position = node.getPositionOfKey(key);// return an index starting
		// from 1 instead of 0
		int realPosition = -position - 1;

		// If position is positive, the key must be inserted in this node
		if (position >= 0) {
			realPosition = position - 1;
			node.insertKeyAndValue(key, value);
			persister.saveNode(node);
			return;
		}

		// descend
		IBTreeNode nodeToDescend = node.getChildAt(realPosition, true);

		if (nodeToDescend.isFull()) {
			split(node, nodeToDescend, realPosition);
			if (node.getKeyAt(realPosition).compareTo(key) < 0) {
				nodeToDescend = node.getChildAt(realPosition + 1, true);
			}
		}
		insertNonFull(nodeToDescend, key, value);

	}

	/**
	 * <pre>
	 * 1 take median element
	 * 2 insert the median in the parent  (shifting necessary elements)
	 * 3 create a new node with right part elements (moving keys and values and children)
	 * 4 set this new node as a child of parent
	 * </pre>
	 */
	public void split(IBTreeNode parent, IBTreeNode node2Split, int childIndex) {
		// BTreeValidator.validateNode(parent, parent == root);
		// BTreeValidator.validateNode(node2Split, false);

		// 1
		IKeyAndValue median = node2Split.getMedian();
		// 2
		parent.setKeyAndValueAt(median, childIndex, true, true);
		// 3
		IBTreeNode rightPart = node2Split.extractRightPart();
		// 4
		parent.setChildAt(rightPart, childIndex + 1);
		parent.setChildAt(node2Split, childIndex);

		parent.incrementNbChildren();

		persister.saveNode(parent);
		persister.saveNode(rightPart);
		persister.saveNode(node2Split);

		if (BTreeValidator.isOn()) {
			BTreeValidator.validateNode(parent, parent == root);
			BTreeValidator.validateNode(rightPart, false);
			BTreeValidator.validateNode(node2Split, false);
		}
	}

	public long getSize() {
		return size;
	}

	public IBTreeNode getRoot() {
		return root;
	}

	public int getHeight() {
		return height;
	}

	public IBTreePersister getPersister() {
		return persister;
	}

	public void setPersister(IBTreePersister persister) {
		this.persister = persister;
		this.persister.setBTree(this);
		if (root.getBTree() == null) {
			root.setBTree(this);
		}
	}

	public String getName() {
		return name;
	}

	public void clear() {
		root.clear();
	}

	public IKeyAndValue getBiggest(IBTreeNode node, boolean delete) {
		int lastKeyIndex = node.getNbKeys() - 1;
		int lastChildIndex = node.getNbChildren() - 1;
		if (lastChildIndex > lastKeyIndex) {
			IBTreeNode child = node.getChildAt(lastChildIndex, true);
			if (child.getNbKeys() == degree - 1) {
				node = prepareForDelete(node, child, lastChildIndex);
			}
			lastChildIndex = node.getNbChildren() - 1;
			child = node.getChildAt(lastChildIndex, true);
			return getBiggest(child, delete);
		}
		IKeyAndValue kav = node.getKeyAndValueAt(lastKeyIndex);
		if (delete) {
			node.deleteKeyAndValueAt(lastKeyIndex, false);
			persister.saveNode(node);
		}
		return kav;
	}

	public IKeyAndValue getSmallest(IBTreeNode node, boolean delete) {
		if (!node.isLeaf()) {
			IBTreeNode child = node.getChildAt(0, true);
			if (child.getNbKeys() == degree - 1) {
				node = prepareForDelete(node, child, 0);
			}
			child = node.getChildAt(0, true);
			return getSmallest(child, delete);
		}
		IKeyAndValue kav = node.getKeyAndValueAt(0);
		if (delete) {
			node.deleteKeyAndValueAt(0, true);
			persister.saveNode(node);
		}
		return kav;
	}
}
