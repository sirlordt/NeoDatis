package org.neodatis.btree.impl.singlevalue;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeNode;
import org.neodatis.btree.exception.BTreeException;

public class InMemoryBTreeNodeSingleValuePerkey extends BTreeNodeSingleValuePerKey {
	protected static int nextId = 1;
	protected  Integer id;
	public InMemoryBTreeNodeSingleValuePerkey(IBTree btree) {
		super(btree);
		id = new Integer(nextId++);
	}
	protected IBTreeNode[] children;
	protected IBTreeNode parent;
	
	public IBTreeNode getChildAt(int index, boolean throwExceptionIfNotExist) {
		if(children[index]==null&&throwExceptionIfNotExist){
			throw new BTreeException("Trying to load null child node at index " + index);
		}
		return children[index];
	}
	public IBTreeNode getParent() {
		return parent;
	}
	public void setChildAt(IBTreeNode child, int index) {
		children[index] = child;
		if (child != null) {
			child.setParent(this);
		}
	}
	public void setChildAt(IBTreeNode node, int childIndex, int index, boolean throwExceptionIfDoesNotExist) {
		IBTreeNode child = node.getChildAt(childIndex, throwExceptionIfDoesNotExist);
		children[index] = child;
		if (child != null) {
			child.setParent(this);
		}
	}
	public void setParent(IBTreeNode node) {
		parent = node;
	}
	public boolean hasParent() {
		return parent!=null;
	}
	protected void init() {
		children = new IBTreeNode[maxNbChildren];
	}
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = (Integer) id;
	}
	public void deleteChildAt(int index) {
		children[index] = null;
		nbChildren--;
	}
	public void moveChildFromTo(int sourceIndex, int destinationIndex, boolean throwExceptionIfDoesNotExist) {
		
		if(children[sourceIndex]==null && throwExceptionIfDoesNotExist){
			throw new BTreeException("Trying to move null child node at index " + sourceIndex);
		}
		children[destinationIndex] = children[sourceIndex];
	}
	public void setNullChildAt(int childIndex) {
		children[childIndex] = null;
	}
	public Object getChildIdAt(int childIndex, boolean throwExceptionIfDoesNotExist) {
		if(children[childIndex]==null && throwExceptionIfDoesNotExist){
			throw new BTreeException("Trying to move null child node at index " + childIndex);
		}
		return children[childIndex].getId();
	}
	public Object getParentId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see org.neodatis.btree.IBTreeNode#getValueAsObjectAt(int)
	 */
	public Object getValueAsObjectAt(int index) {
		return getValueAt(index);
	}

}
