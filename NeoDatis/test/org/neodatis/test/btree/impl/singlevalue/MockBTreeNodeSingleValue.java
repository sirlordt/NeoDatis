package org.neodatis.test.btree.impl.singlevalue;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.impl.singlevalue.InMemoryBTreeNodeSingleValuePerkey;

public class MockBTreeNodeSingleValue extends InMemoryBTreeNodeSingleValuePerkey {
	private String name;

	public MockBTreeNodeSingleValue(IBTree btree, String name) {
		super(btree);
		this.name = name;
	}

}
