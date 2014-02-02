package org.neodatis.btree;

import java.io.Serializable;
import java.util.Iterator;

import org.neodatis.odb.core.OrderByConstants;

public interface IBTree extends Serializable {
	void insert(Comparable key, Object value);

	void split(IBTreeNode parent, IBTreeNode node2Split, int childIndex);

	Object delete(Comparable key, Object value);

	int getDegree();

	long getSize();

	int getHeight();

	IBTreeNode getRoot();

	IBTreePersister getPersister();

	void setPersister(IBTreePersister persister);

	IBTreeNode buildNode();

	Object getId();

	void setId(Object id);

	void clear();

	IKeyAndValue getBiggest(IBTreeNode node, boolean delete);

	IKeyAndValue getSmallest(IBTreeNode node, boolean delete);

	Iterator iterator(OrderByConstants orderBy);
}
