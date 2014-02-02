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

import java.util.Iterator;
import java.util.List;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeMultipleValuesPerKey;
import org.neodatis.btree.IBTreePersister;
import org.neodatis.odb.ODB;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.impl.core.btree.LazyODBBTreePersister;
import org.neodatis.odb.impl.core.btree.ODBBTreeMultiple;
import org.neodatis.odb.impl.core.layers.layer3.engine.AbstractObjectWriter;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestODBLazyBTree extends ODBTest {
	private static final int SIZE = 10000;

	private IBTreePersister getPersister(String baseName) throws Exception {
		ODB odb = open(baseName);
		/*
		 * odb.addInsertTrigger(new InsertTriggerAdapter(){
		 * 
		 * public boolean beforeInsert(Object object) {
		 * println("inserting new object " + object); return true; }
		 * 
		 * });
		 */
		return new LazyODBBTreePersister(odb);
		// return new InMemoryBTreePersister();
	}

	public void test01() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		IBTreePersister persister = getPersister(baseName);
		IBTree tree = new ODBBTreeMultiple("test1", 2, persister);
		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < SIZE; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
			// println(i);
			// println(new BTreeDisplay().build(tree,true));
		}
		long end = OdbTime.getCurrentTimeInMs();
		println("time/object=" + (float) (end - start) / (float) SIZE);
		assertTrue((end - start) < 0.34 * SIZE);
		// println("insert of "+SIZE+" elements in BTREE = " +
		// (end-start)+"ms");
		// persister.close();
		// persister = getPersister();

		assertEquals(SIZE, tree.getSize());

		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_ASC);
		int j = 0;
		while (iterator.hasNext()) {
			Object o = iterator.next();
			assertEquals("value " + (j + 1), o);
			j++;
			if (j % 1000 == 0) {
				println(j);
			}
		}
		persister.close();
		deleteBase(baseName);
	}

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		IBTreePersister persister = getPersister(baseName);
		IBTree tree = new ODBBTreeMultiple("test1", 2, persister);
		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < SIZE; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}
		long end = OdbTime.getCurrentTimeInMs();
		println(end - start);
		if(testPerformance){
			assertTrue((end - start) < 0.34 * SIZE);
		}
		// println("insert of "+SIZE+" elements in BTREE = " +
		// (end-start)+"ms");
		// persister.close();
		// persister = getPersister();

		assertEquals(SIZE, tree.getSize());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_ASC);
		int j = 0;
		while (iterator.hasNext()) {

			Object o = iterator.next();
			assertEquals("value " + (j + 1), o);
			j++;
			if (j % 1000 == 0) {
				println(j);
			}
		}
		persister.close();
		deleteBase(baseName);
	}

	public void testLazyCache() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		IBTreePersister persister = getPersister(baseName);
		IBTree tree = new ODBBTreeMultiple("test1", 2, persister);
		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < SIZE; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}
		long end = OdbTime.getCurrentTimeInMs();
		if(testPerformance){
			assertTrue((end - start) < 0.34 * SIZE);
		}
		// println("insert of "+SIZE+" elements in BTREE = " +
		// (end-start)+"ms");
		// persister.close();
		// persister = getPersister();

		// /assertEquals(SIZE,tree.size());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_ASC);
		int j = 0;
		while (iterator.hasNext()) {
			Object o = iterator.next();
			j++;
			if (j == SIZE) {
				assertEquals("value " + SIZE, o);
			}
		}
		persister.close();
		deleteBase(baseName);
	}

	public void test1a() throws Exception {

		if (!isLocal) {
			return;
		}

		String baseName = getBaseName();
		// Configuration.setInPlaceUpdate(true);
		IBTreePersister persister = getPersister(baseName);
		IBTree tree = new ODBBTreeMultiple("test1a", 2, persister);

		for (int i = 0; i < SIZE; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}

		// println(new BTreeDisplay().build(tree,true).toString());
		persister.close();
		/*
		 * ODB odb = open(baseName); odb.getObjects(LazyNode.class);
		 * odb.close();
		 */

		persister = getPersister(baseName);
		tree = persister.loadBTree(tree.getId());
		// println(new BTreeDisplay().build(tree,true).toString());

		assertEquals(SIZE, tree.getSize());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_ASC);
		int j = 0;
		while (iterator.hasNext()) {
			Object o = iterator.next();
			assertEquals("value " + (j + 1), o);
			j++;
			if (j == SIZE) {
				assertEquals("value " + SIZE, o);
			}
		}
		persister.close();
		deleteBase(baseName);
	}

	public void test2a() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		AbstractObjectWriter.resetNbUpdates();
		// LogUtil.allOn(true);
		deleteBase(baseName);
		IBTreePersister persister = getPersister(baseName);
		IBTreeMultipleValuesPerKey tree = new ODBBTreeMultiple("test2a", 20, persister);
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < SIZE; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}

		// println("Commiting");
		persister.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		// println("insert of "+SIZE+" elements in BTREE = " +
		// (end0-start0)+"ms");

		// println("end Commiting");
		// println("updates : IP="+ObjectWriter.getNbInPlaceUpdates()+" , N="+ObjectWriter.getNbNormalUpdates());

		// ODB odb = open(baseName);
		// odb.getObjects(LazyNode.class);
		// odb.close();

		persister = getPersister(baseName);
		// println("reloading btree");
		tree = (IBTreeMultipleValuesPerKey) persister.loadBTree(tree.getId());
		// println("end reloading btree , size="+tree.size());
		assertEquals(SIZE, tree.getSize());

		long totalSearchTime = 0;
		long oneSearchTime = 0;
		long minSearchTime = 10000;
		long maxSearchTime = -1;
		for (int i = 0; i < SIZE; i++) {
			long start = OdbTime.getCurrentTimeInMs();
			List o = tree.search(new Integer(i + 1));
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals("value " + (i + 1), o.get(0));
			oneSearchTime = (end - start);
			// println("Search time for "+o+" = "+oneSearchTime);
			if (oneSearchTime > maxSearchTime) {
				maxSearchTime = oneSearchTime;
			}
			if (oneSearchTime < minSearchTime) {
				minSearchTime = oneSearchTime;
			}
			totalSearchTime += oneSearchTime;
		}

		persister.close();
		// println("total search time="+totalSearchTime +
		// " - mean st="+((double)totalSearchTime/SIZE));
		// println("min search time="+minSearchTime + " - max="+maxSearchTime);
		// Median search time must be smaller than 1ms
		deleteBase(baseName);
		assertTrue(totalSearchTime < 1 * SIZE);

	}

	public void test2() throws Exception {

		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		IBTreePersister persister = getPersister(baseName);
		IBTree tree = new ODBBTreeMultiple("test2", 2, persister);
		for (int i = 0; i < SIZE; i++) {
			tree.insert(new Integer(i + 1), "value " + (i + 1));
		}
		assertEquals(SIZE, tree.getSize());
		Iterator iterator = tree.iterator(OrderByConstants.ORDER_BY_DESC);
		int j = 0;
		while (iterator.hasNext()) {
			Object o = iterator.next();
			// println(o);
			j++;
			if (j == SIZE) {
				assertEquals("value " + 1, o);
			}
		}
		persister.close();
		deleteBase(baseName);
	}

	public static void main(String[] args) throws Exception {
		TestODBLazyBTree t = new TestODBLazyBTree();
		for (int i = 0; i < 1000; i++) {
			try {
				t.test1a();
			} catch (Exception e) {
				System.out.println("ERROR On loop " + i);
				throw e;
			}
		}

	}
}
