package org.neodatis.btree;

public interface IBTreeSingleValuePerKey extends IBTree {
	Object delete(Comparable key, Object value);

	Object search(Comparable key);
}
