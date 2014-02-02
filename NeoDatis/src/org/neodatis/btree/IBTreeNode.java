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
package org.neodatis.btree;

import java.io.Serializable;

/**
 * The interface for btree node.
 * 
 * @author olivier
 * 
 */

public interface IBTreeNode extends Serializable {
	boolean isFull();

	boolean isLeaf();

	IKeyAndValue getKeyAndValueAt(int index);

	public Comparable getKeyAt(int index);

	public Object getValueAsObjectAt(int index);

	public IKeyAndValue getLastKeyAndValue();

	IBTreeNode getChildAt(int index, boolean throwExceptionIfNotExist);

	IBTreeNode getLastChild();

	IBTreeNode getLastPositionChild();

	IBTreeNode getParent();

	Object getParentId();

	void setKeyAndValueAt(Comparable key, Object value, int index);

	void setKeyAndValueAt(IKeyAndValue keyAndValue, int index);

	void setKeyAndValueAt(Comparable key, Object value, int index, boolean shiftIfAlreadyExist, boolean incrementNbKeys);

	void setKeyAndValueAt(IKeyAndValue keyAndValue, int index, boolean shiftIfAlreadyExist, boolean incrementNbKeys);

	IBTreeNode extractRightPart();

	IKeyAndValue getMedian();

	void setChildAt(IBTreeNode node, int childIndex, int indexDestination, boolean throwExceptionIfDoesNotExist);

	void setChildAt(IBTreeNode child, int index);

	public void setNullChildAt(int childIndex);

	void moveChildFromTo(int sourceIndex, int destinationIndex, boolean throwExceptionIfDoesNotExist);

	void incrementNbKeys();

	void incrementNbChildren();

	/**
	 * Returns the position of the key. If the key does not exist in node,
	 * returns the position where this key should be,multiplied by -1
	 * 
	 * <pre>
	 * or example for node of degree 3 : [1 89 452 789 - ],
	 *  calling getPositionOfKey(89) returns 2 (starts with 1)
	 *  calling getPositionOfKey(99) returns -2 (starts with 1),because the position should be done, but it does not exist so multiply by -1
	 * his is used to know the child we should descend to!in this case the getChild(2).
	 * 
	 * </pre>
	 * 
	 * @param key
	 * @return The position of the key,as a negative number if key does not
	 *         exist, warning, the position starts with 1and not 0!
	 */
	int getPositionOfKey(Comparable key);

	void insertKeyAndValue(Comparable key, Object value);

	void mergeWith(IBTreeNode node);

	void removeKeyAndValueAt(int index);

	int getNbKeys();

	void setNbKeys(int nbKeys);

	void setNbChildren(int nbChildren);

	int getDegree();

	int getNbChildren();

	int getMaxNbChildren();

	void setParent(IBTreeNode node);

	Object deleteKeyForLeafNode(IKeyAndValue keyAndValue);

	Object deleteKeyAndValueAt(int index, boolean shiftChildren);

	boolean hasParent();

	Object getId();

	void setId(Object id);

	void setBTree(IBTree btree);

	IBTree getBTree();

	void clear();

	void deleteChildAt(int index);

	Object getChildIdAt(int childIndex, boolean throwExceptionIfDoesNotExist);
}
