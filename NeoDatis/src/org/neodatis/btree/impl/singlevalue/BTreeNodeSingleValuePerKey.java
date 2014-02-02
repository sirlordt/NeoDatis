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
package org.neodatis.btree.impl.singlevalue;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeNodeOneValuePerKey;
import org.neodatis.btree.exception.DuplicatedKeyException;
import org.neodatis.btree.impl.AbstractBTreeNode;

public abstract class BTreeNodeSingleValuePerKey extends AbstractBTreeNode implements IBTreeNodeOneValuePerKey{
	
	

	public BTreeNodeSingleValuePerKey() {
		super();
	}

	public BTreeNodeSingleValuePerKey(IBTree btree) {
		super(btree);
	}

	public Object getValueAt(int index) {
		return values[index];
	}

	public void insertKeyAndValue(Comparable key, Object value) {

		int position = getPositionOfKey(key);

		int realPosition = 0;
		if (position >= 0) {
			throw new DuplicatedKeyException(String.valueOf(key));
		}
		realPosition = -position - 1;
		// If there is an element at this position, then right shift, size
		// safety is guaranteed by the rightShiftFrom method
		if (realPosition < nbKeys) {
			rightShiftFrom(realPosition, true);
		}
		keys[realPosition] = key;
		values[realPosition] = value;
		nbKeys++;
	}

	public Object search(Comparable key) {
		int position = getPositionOfKey(key);
		boolean keyIsHere = position > 0;
		int realPosition = -1;
		if (keyIsHere) {
			realPosition = position - 1;
			Object value = getValueAt(realPosition);
			return value;
		} else if (isLeaf()) {
			// key is not here and node is leaf
			return null;
		}

		realPosition = -position - 1;
		IBTreeNodeOneValuePerKey node = (IBTreeNodeOneValuePerKey) getChildAt(realPosition, true);
		return node.search(key);
	}
}
