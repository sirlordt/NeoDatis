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
package org.neodatis.btree.impl.multiplevalue;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeNodeMultipleValuesPerKey;
import org.neodatis.btree.IKeyAndValue;
import org.neodatis.btree.exception.BTreeException;
import org.neodatis.btree.impl.AbstractBTreeNode;
import org.neodatis.btree.tool.BTreeValidator;

public abstract class BTreeNodeMultipleValuesPerKey extends AbstractBTreeNode implements IBTreeNodeMultipleValuesPerKey {

	public BTreeNodeMultipleValuesPerKey() {
		super();
	}

	public BTreeNodeMultipleValuesPerKey(IBTree btree) {
		super(btree);
	}
	
	public List getValueAt(int index){
		return (List) values[index];
	}

	public void insertKeyAndValue(Comparable key, Object value) {

		int position = getPositionOfKey(key);
		boolean addToExistingCollection = false;
		int realPosition = 0;
		if (position >= 0) {
			addToExistingCollection = true;
			realPosition = position - 1;
		} else {
			realPosition = -position - 1;
		}
		// If there is an element at this position and the key is different,
		// then right shift, size
		// safety is guaranteed by the rightShiftFrom method
		if (realPosition < nbKeys && key.compareTo(keys[realPosition]) != 0) {
			rightShiftFrom(realPosition, true);
		}
		keys[realPosition] = key;
		// This is a non unique btree node, manage collection
		manageCollectionValue(realPosition, value);

		if (!addToExistingCollection) {
			nbKeys++;
		}
	}

	/**
	 * @param realPosition
	 * @param value
	 */
	private void manageCollectionValue(int realPosition, Object value) {
		Object o = values[realPosition];

		if (o == null) {
			o = new ArrayList();
			values[realPosition] = o;
		} else {
			if (!(o instanceof List)) {
				throw new BTreeException("Value of Non Unique Value BTree should be collection and it is " + o.getClass().getName());
			}
		}
		List l = (List) o;
		l.add(value);
	}

	public List search(Comparable key) {
		int position = getPositionOfKey(key);
		boolean keyIsHere = position > 0;
		int realPosition = -1;

		if (keyIsHere) {
			realPosition = position - 1;
			List value = getValueAt(realPosition);
			return value;
		} else if (isLeaf()) {
			// key is not here and node is leaf
			return null;
		}

		realPosition = -position - 1;
		IBTreeNodeMultipleValuesPerKey node = (IBTreeNodeMultipleValuesPerKey) getChildAt(realPosition, true); 
		return node.search(key);
	}
	
	public Object deleteKeyForLeafNode(IKeyAndValue keyAndValue) {
		boolean objectHasBeenFound = false;
		int position = getPositionOfKey(keyAndValue.getKey());
		if (position < 0) {
			return null;
		}
		int realPosition = position - 1;
		// In Multiple Values per key, the value is a list
		List value = (List) values[realPosition];
		
		// Here we must search for the right object. The list can contains more than 1 object
		int size = value.size();
		for(int i=0;i<size&&!objectHasBeenFound;i++){
			if(value.get(i).equals(keyAndValue.getValue())){
				value.remove(i);
				objectHasBeenFound = true;
				
			}
		}
		if(!objectHasBeenFound){
			return null;
		}
		
		// If after removal, the list is empty, then remove the key from the node
		if(value.size()==0){
			// If we get there
			leftShiftFrom(realPosition, false);
			nbKeys--;
		}
		BTreeValidator.validateNode(this);
		return keyAndValue.getValue();
	}
}
