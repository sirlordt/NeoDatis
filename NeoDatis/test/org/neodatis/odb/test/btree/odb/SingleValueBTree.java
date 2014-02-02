/**
 * 
 */
package org.neodatis.odb.test.btree.odb;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.impl.singlevalue.InMemoryBTreeSingleValuePerKey;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class SingleValueBTree extends ODBTest {
	public void test2SameKeySingleBTree() {
		int size = 1000;
		IBTree tree = new InMemoryBTreeSingleValuePerKey("test1", 50);
		for (int i = 0; i < size; i++) {
			if (i % 10000 == 0) {
				println(i);
			}
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}
		try {
			for (int i = 0; i < 10; i++) {
				if (i % 10000 == 0) {
					println(i);
				}
				tree.insert(new Integer(100), "value " + (i + 1));
				fail("Single Value Btree should not accept duplcited key");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
