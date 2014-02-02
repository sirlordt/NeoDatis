package org.neodatis.btree;

import java.util.List;

public interface IBTreeMultipleValuesPerKey extends IBTree {
	Object delete(Comparable key, Object value);

	List search(Comparable key);
}
