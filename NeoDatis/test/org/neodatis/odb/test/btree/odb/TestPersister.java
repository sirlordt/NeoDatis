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
package org.neodatis.odb.test.btree.odb;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeMultipleValuesPerKey;
import org.neodatis.btree.IBTreeNodeMultipleValuesPerKey;
import org.neodatis.btree.impl.InMemoryPersister;
import org.neodatis.btree.impl.KeyAndValue;
import org.neodatis.odb.ODB;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.btree.LazyODBBTreePersister;
import org.neodatis.odb.impl.core.btree.ODBBTreeMultiple;
import org.neodatis.odb.impl.core.btree.ODBBTreeNodeMultiple;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.ODBTest;

public class TestPersister extends ODBTest {
	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase("btree45.neodatis");
		ODB odb = open("btree45.neodatis");
		IStorageEngine storageEngine = Dummy.getEngine(odb);
		LazyODBBTreePersister persister = new LazyODBBTreePersister(storageEngine);

		IBTreeMultipleValuesPerKey tree = new ODBBTreeMultiple("t", 3, persister);

		tree.insert(new Integer(1), new MyObject("Value 1"));
		tree.insert(new Integer(20), new MyObject("Value 20"));
		tree.insert(new Integer(25), new MyObject("Value 25"));
		tree.insert(new Integer(29), new MyObject("Value 29"));
		tree.insert(new Integer(21), new MyObject("Value 21"));

		assertEquals(5, tree.getRoot().getNbKeys());
		assertEquals(0, tree.getRoot().getNbChildren());
		assertEquals(new Integer(21), tree.getRoot().getMedian().getKey());
		assertEquals("[Value 21]", tree.getRoot().getMedian().getValue().toString());
		assertEquals(0, tree.getRoot().getNbChildren());

		// println(tree.getRoot());

		tree.insert(new Integer(45), new MyObject("Value 45"));

		assertEquals(2, tree.getRoot().getNbChildren());
		assertEquals(1, tree.getRoot().getNbKeys());
		assertEquals(new Integer(21), tree.getRoot().getKeyAt(0));
		assertEquals("[Value 21]", tree.getRoot().getValueAsObjectAt(0).toString());

		persister.close();
		odb = open("btree45.neodatis");
		storageEngine = Dummy.getEngine(odb);
		persister = new LazyODBBTreePersister(storageEngine);
		tree = (IBTreeMultipleValuesPerKey) persister.loadBTree(tree.getId());
		assertEquals(6, tree.getSize());

		// println(tree.getRoot());

		MyObject o = (MyObject) tree.search(new Integer(20)).get(0);
		assertEquals("Value 20", o.getName());

		o = (MyObject) tree.search(new Integer(29)).get(0);
		assertEquals("Value 29", o.getName());

		o = (MyObject) tree.search(new Integer(45)).get(0);
		assertEquals("Value 45", o.getName());

		odb.close();
		deleteBase("btree45.neodatis");
	}

	public void testDirectSave() throws Exception {
		deleteBase("btree46.neodatis");
		ODB odb = open("btree46.neodatis");

		IBTree tree = new ODBBTreeMultiple("t", 3, new InMemoryPersister());

		IBTreeNodeMultipleValuesPerKey node = new ODBBTreeNodeMultiple(tree);
		odb.store(node);
		for (int i = 0; i < 4; i++) {
			node.setKeyAndValueAt(new KeyAndValue(new Integer(i + 1), "String" + (i + 1)), i);
			odb.store(node);
		}
		odb.close();
		deleteBase("btree46.neodatis");
	}

}

class MyObject {
	private String name;

	public MyObject(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

}