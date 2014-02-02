package org.neodatis.odb.impl.core.btree;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeNode;
import org.neodatis.btree.exception.BTreeException;
import org.neodatis.btree.impl.singlevalue.BTreeNodeSingleValuePerKey;
import org.neodatis.odb.OID;


/** 
 * The NeoDatis ODB BTree Node implementation. It extends the DefaultBTreeNode generic implementation to be able to be stored in the ODB database.
 * @author osmadja
 *
 */
public class ODBBTreeNodeSingle extends BTreeNodeSingleValuePerKey {
	protected OID oid;
	public ODBBTreeNodeSingle() {
		super();
	}
	protected OID[] childrenOids;
	protected OID parentOid;
	/** lazy loaded*/
	protected transient IBTreeNode parent;

	
	public ODBBTreeNodeSingle(IBTree btree) {
		super(btree);
	}

	public IBTreeNode getChildAt(int index, boolean throwExceptionIfNotExist) {
		OID oid = childrenOids[index];
		if(oid==null){
			if(throwExceptionIfNotExist){
				throw new BTreeException("Trying to load null child node at index " + index);
			}
			return null;
		}
		return btree.getPersister().loadNodeById(oid);
	}
	public IBTreeNode getParent() {
		if(parent!=null){
			return parent;
		}
		parent = btree.getPersister().loadNodeById(parentOid);
		return parent;
	}
	public void setChildAt(IBTreeNode child, int index) {
		if(child!=null){
			if(child.getId()==null){
				btree.getPersister().saveNode(child);
			}
			childrenOids[index] = (OID) child.getId();
			child.setParent(this);
		}else{
			childrenOids[index] = null;
		}
	}
	public void setParent(IBTreeNode node) {
		parent = node;
		if(parent!=null){
			if(parent.getId()==null){
				btree.getPersister().saveNode(parent);
			}
			parentOid = (OID) parent.getId();
		}else{
			parentOid = null;
		}
		
	}
	public boolean hasParent() {
		return parentOid!=null;
	}
	protected void init() {
		childrenOids = new OID[maxNbChildren];
		parentOid = null;
		parent = null;
	}
	public Object getId() {
		return oid;
	}
	public void setId(Object id) {
		this.oid = (OID) id;
		
	}
	public void clear() {
		super.clear();
		parent = null;
		parentOid = null;
		childrenOids = null;
		oid = null;		
	}

	public void deleteChildAt(int index) {
		childrenOids[index] = null;
		nbChildren--;
		
	}

	public void moveChildFromTo(int sourceIndex, int destinationIndex, boolean throwExceptionIfDoesNotExist) {
		if(throwExceptionIfDoesNotExist&& childrenOids[sourceIndex]==null){
			throw new BTreeException("Trying to load null child node at index " + sourceIndex);
		}
		childrenOids[destinationIndex] = childrenOids[sourceIndex];
	}

	public void setChildAt(IBTreeNode node, int childIndex, int indexDestination, boolean throwExceptionIfDoesNotExist) {
		OID childOid =  (OID) node.getChildIdAt(childIndex, throwExceptionIfDoesNotExist);
		childrenOids[indexDestination] = childOid;
		if(childOid!=null){
			// The parent of the child has changed
			IBTreeNode child = btree.getPersister().loadNodeById(childOid);
			child.setParent(this);
			btree.getPersister().saveNode(child);
		}
	}

	public void setNullChildAt(int childIndex) {
		childrenOids[childIndex] = null;
	}
	public Object getChildIdAt(int childIndex, boolean throwExceptionIfDoesNotExist){
		if(throwExceptionIfDoesNotExist&& childrenOids[childIndex]==null){
			throw new BTreeException("Trying to load null child node at index " + childIndex);
		}
		return childrenOids[childIndex];
	}
	public Object getParentId() {
		return parentOid;
	}

	public Object getValueAsObjectAt(int index) {
		return getValueAt(index);
	}

}
