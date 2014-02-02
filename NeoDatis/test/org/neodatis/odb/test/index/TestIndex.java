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
package org.neodatis.odb.test.index;

import java.math.BigInteger;
import java.util.Date;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.btree.LazyODBBTreePersister;
import org.neodatis.odb.impl.core.layers.layer3.engine.AbstractObjectWriter;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.wrappers.OdbTime;

public class TestIndex extends ODBTest {
	public void testSaveIndex() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name", "duration" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		String[] indexFields2 = { "name", "creation" };
		clazz.addUniqueIndexOn("index2", indexFields2, true);

		String[] indexFields3 = { "duration", "creation" };
		clazz.addUniqueIndexOn("index3", indexFields3, true);
		base.close();

		base = open(baseName);

		ISession session = Dummy.getEngine(base).getSession(true);
		MetaModel metaModel = session.getStorageEngine().getSession(true).getMetaModel();
		ClassInfo ci = metaModel.getClassInfo(IndexedObject.class.getName(), true);
		assertEquals(3, ci.getNumberOfIndexes());

		assertEquals(ci.getIndex(0).getName(), "index1");
		assertEquals(1, ci.getIndex(0).getAttributeIds()[0]);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(0).getStatus());

		assertEquals(ci.getIndex(1).getName(), "index2");
		assertEquals(1, ci.getIndex(1).getAttributeIds()[0]);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(1).getStatus());

		assertEquals(ci.getIndex(2).getName(), "index3");
		assertEquals(2, ci.getIndex(2).getAttributeIds()[0]);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(0).getStatus());
		base.close();
		deleteBase(baseName);
	}

	public void testInsertWithIndex() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name", "duration" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		/*
		 * String[] indexFields2 = { "name", "creation" };
		 * clazz.addUniqueIndexOn("index2", indexFields2);
		 * 
		 * String[] indexFields3 = { "duration", "creation" };
		 * clazz.addUniqueIndexOn("index3", indexFields3);
		 */
		base.close();

		base = open(baseName);

		IndexedObject io1 = new IndexedObject("olivier", 15, new Date());
		base.store(io1);
		base.close();

		base = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject.class, Where.isNotNull("name"));
		Objects objects = base.getObjects(q, true);
		base.close();

		assertEquals(1, objects.size());
		IndexedObject io2 = (IndexedObject) objects.getFirst();
		assertEquals("olivier", io2.getName());
		assertEquals(15, io2.getDuration());
		assertFalse(q.getExecutionPlan().getDetails().indexOf("index1") != -1);

		// deleteBase(baseName);
	}

	public void testIndexWithOneFieldAndQueryWithTwoFields() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		base.close();

		base = open(baseName);

		IndexedObject io1 = new IndexedObject("olivier", 15, new Date());
		base.store(io1);
		base.close();

		base = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject.class, Where.and().add(Where.equal("name", "olivier")).add(Where.equal("duration", 15)));
		Objects objects = base.getObjects(q, true);
		base.close();
		println(q.getExecutionPlan().toString());
		assertEquals(false, q.getExecutionPlan().useIndex());
		assertEquals(1, objects.size());
		deleteBase(baseName);
	}

	public void testInsertWithIndex1() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		/*
		 * String[] indexFields2 = { "name", "creation" };
		 * clazz.addUniqueIndexOn("index2", indexFields2);
		 * 
		 * String[] indexFields3 = { "duration", "creation" };
		 * clazz.addUniqueIndexOn("index3", indexFields3);
		 */
		base.close();

		base = open(baseName);

		int size = 1000;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
			if (i % 1000 == 0)
				println(i);
		}
		long tt0 = OdbTime.getCurrentTimeInMs();
		base.close();
		long tt1 = OdbTime.getCurrentTimeInMs();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));
		println("commit time=" + (tt1 - tt0));
		println(LazyODBBTreePersister.counters());
		base = open(baseName);

		long totalTime = 0;
		long maxTime = 0;
		long minTime = 100000;
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + (i + 1), io2.getName());
			assertEquals(15 + i, io2.getDuration());
			long d = end - start;
			totalTime += d;

			if (d > maxTime) {
				maxTime = d;
			}
			if (d < minTime) {
				minTime = d;
			}

		}
		base.close();
		deleteBase(baseName);

		println("total duration=" + totalTime + " / " + (double) totalTime / size);
		println("duration max=" + maxTime + " / min=" + minTime);
		if (testPerformance&& totalTime / size > 2) {
			fail("Total/size is > than 2 : " + totalTime);
		}
		if (testPerformance) {
			// TODO Try to get maxTime < 10!
			assertTrue(maxTime < 100);
			assertTrue(minTime < 1);
		}
	}

	public void testInsertWithIndex2() throws Exception {
		String baseName = getBaseName();
		if (!runAll) {
			return;
		}
		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		int size = 10000;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
			if (i % 1000 == 0) {
				println(i);
			}
		}
		long tt0 = OdbTime.getCurrentTimeInMs();
		base.close();
		long tt1 = OdbTime.getCurrentTimeInMs();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));
		println("commit time=" + (tt1 - tt0));
		println(LazyODBBTreePersister.counters());
		base = open(baseName);

		long totalTime = 0;
		long maxTime = 0;
		long minTime = 100000;
		long t0 = OdbTime.getCurrentTimeInMs();
		long t1 = 0;
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + (i + 1), io2.getName());
			assertEquals(15 + i, io2.getDuration());
			long d = end - start;
			totalTime += d;

			if (d > maxTime) {
				maxTime = d;
			}
			if (d < minTime) {
				minTime = d;
			}
			if (i % 1000 == 0) {
				t1 = OdbTime.getCurrentTimeInMs();
				println("i=" + i + " - time=" + (t1 - t0));
				t0 = t1;
				// /println(LazyODBBTreePersister.counters());
			}
		}
		base.close();
		deleteBase(baseName);

		// println("total duration=" + totalTime + " / " + (double) totalTime /
		// size);
		// println("duration max=" + maxTime + " / min=" + minTime);
		if (totalTime / size > 1) {
			fail("Total/size is > than 1 : " + (float) ((float) totalTime / (float) size));
		}

		println("Max time=" + maxTime);
		println("Min time=" + minTime);
		// TODO Try to get maxTime < 10!
		assertTrue(maxTime < 250);
		assertTrue(minTime < 1);
	}

	/** Test with on e key index */
	public void testInsertWithIndex3() throws Exception {
		String baseName = getBaseName();
		// LogUtil.logOn(LazyODBBTreePersister.LOG_ID, true);
		deleteBase(baseName);
		ODB base = open(baseName);
		OdbConfiguration.setUseLazyCache(false);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		/*
		 * String[] indexFields2 = { "name", "creation" };
		 * clazz.addUniqueIndexOn("index2", indexFields2);
		 * 
		 * String[] indexFields3 = { "duration", "creation" };
		 * clazz.addUniqueIndexOn("index3", indexFields3);
		 */
		base.close();

		base = open(baseName);

		int size = isLocal ? 1300 : 300;
		int commitInterval = 10;
		long start0 = OdbTime.getCurrentTimeInMs();
		IStorageEngine engine = Dummy.getEngine(base);
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + size, new Date());
			base.store(io1);
			if (i % commitInterval == 0) {
				base.commit();
				base.close();
				base = open(baseName);
				engine = Dummy.getEngine(base);
			}
			if (io1.getName().equals("olivier" + size)) {
				println("Ola chico");
			}
		}

		engine = Dummy.getEngine(base);
		// println(new
		// BTreeDisplay().build(engine.getSession(true).getMetaModel().getClassInfo(IndexedObject.class.getName(),
		// true).getIndex(0).getBTree(), true));

		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		// println("IPU=" + ObjectWriter.getNbInPlaceUpdates() + " - NU=" +
		// ObjectWriter.getNbNormalUpdates());
		// println("inserting time with index=" + (end0 - start0));

		base = open(baseName);

		engine = Dummy.getEngine(base);

		// println("After load = unconnected : "+
		// engine.getSession(true).getMetaModel().getClassInfo(IndexedObject.class.getName(),
		// true).getUncommittedZoneInfo());
		// println("After Load = connected : "+
		// engine.getSession(true).getMetaModel().getClassInfo(IndexedObject.class.getName(),
		// true).getCommitedZoneInfo());
		// println(new
		// BTreeDisplay().build(engine.getSession(true).getMetaModel().getClassInfo(IndexedObject.class.getName(),
		// true).getIndex(0).getBTree(), true));

		IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + size));
		long start = OdbTime.getCurrentTimeInMs();
		Objects objects = base.getObjects(q, false);
		long end = OdbTime.getCurrentTimeInMs();
		/*
		 * engine = Dummy.getEngine(base); ClassInfo ci =
		 * engine.getSession(true)
		 * .getMetaModel().getClassInfo(IndexedObject.class.getName(), true);
		 * long sizebtree = ci.getIndex(0).getBTree().getSize();
		 * println("Size btree="+sizebtree); //println(new
		 * BTreeDisplay().build(ci.getIndex(0).getBTree(), false));
		 */

		try {
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + size, io2.getName());
			assertEquals(15 + size, io2.getDuration());
			long duration = end - start;

			println("duration=" + duration);

			OdbConfiguration.setUseLazyCache(false);
			if (testPerformance) {
				if (isLocal) {
					if (duration > 2) {
						fail("Time of search in index is greater than 2ms : " + duration);
					}
				} else {
					if (duration > 32) {
						fail("Time of search in index is greater than 2ms : " + duration);
					}
				}
			}

		} finally {
			base.close();
			deleteBase(baseName);
		}

	}

	public void testInsertWithIndex3Part1() throws Exception {
		String baseName = "index.neodatis";
		// LogUtil.logOn(LazyODBBTreePersister.LOG_ID, true);
		deleteBase(baseName);
		ODB base = open(baseName);
		OdbConfiguration.setUseLazyCache(false);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		/*
		 * String[] indexFields2 = { "name", "creation" };
		 * clazz.addUniqueIndexOn("index2", indexFields2);
		 * 
		 * String[] indexFields3 = { "duration", "creation" };
		 * clazz.addUniqueIndexOn("index3", indexFields3);
		 */
		base.close();

		base = open(baseName);

		int size = 1300;
		int commitInterval = 10;
		long start0 = OdbTime.getCurrentTimeInMs();
		IStorageEngine engine = Dummy.getEngine(base);
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + size, new Date());
			base.store(io1);
			if (i % commitInterval == 0) {
				base.commit();
				base.close();
				base = open(baseName);
				engine = Dummy.getEngine(base);
			}
			if (io1.getName().equals("olivier" + size)) {
				println("Ola chico");
			}
		}

		engine = Dummy.getEngine(base);
		// println(new
		// BTreeDisplay().build(engine.getSession(true).getMetaModel().getClassInfo(IndexedObject.class.getName(),
		// true).getIndex(0).getBTree(), true));

		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();

	}

	public void testInsertWithIndex3Part2() throws Exception {
		String baseName = "index.neodatis";
		int size = 1300;
		ODB base = open(baseName);

		IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + size));
		long start = OdbTime.getCurrentTimeInMs();
		Objects objects = base.getObjects(q, false);
		long end = OdbTime.getCurrentTimeInMs();
		/*
		 * engine = Dummy.getEngine(base); ClassInfo ci =
		 * engine.getSession(true)
		 * .getMetaModel().getClassInfo(IndexedObject.class.getName(), true);
		 * long sizebtree = ci.getIndex(0).getBTree().getSize();
		 * println("Size btree="+sizebtree); //println(new
		 * BTreeDisplay().build(ci.getIndex(0).getBTree(), false));
		 */

		try {
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + size, io2.getName());
			assertEquals(15 + size, io2.getDuration());
			long duration = end - start;

			println("duration=" + duration);

			OdbConfiguration.setUseLazyCache(false);

			if (testPerformance) {
				if (isLocal) {
					if (duration > 2) {
						fail("Time of search in index is greater than 2ms : " + duration);
					}
				} else {
					if (duration > 32) {
						fail("Time of search in index is greater than 2ms : " + duration);
					}
				}
			}

		} finally {
			base.close();
			// deleteBase(baseName);
		}

	}

	/** Test with one key index */
	public void testInsertWithIntIndex3CheckAll() throws Exception {
		String baseName = getBaseName();
		// LogUtil.logOn(LazyODBBTreePersister.LOG_ID, true);
		deleteBase(baseName);
		ODB base = open(baseName);
		OdbConfiguration.setUseLazyCache(false);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "duration" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		/*
		 * String[] indexFields2 = { "name", "creation" };
		 * clazz.addUniqueIndexOn("index2", indexFields2);
		 * 
		 * String[] indexFields3 = { "duration", "creation" };
		 * clazz.addUniqueIndexOn("index3", indexFields3);
		 */
		base.close();

		base = open(baseName);

		int size = 5000;
		int commitInterval = 10;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), i, new Date());
			base.store(io1);
			if (i % commitInterval == 0) {
				//base.commit();
				// println(i+" : commit / " + size);
			}
		}
		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		// println("IPU=" + ObjectWriter.getNbInPlaceUpdates() + " - NU=" +
		// ObjectWriter.getNbNormalUpdates());
		// println("inserting time with index=" + (end0 - start0));

		base = open(baseName);
		/*
		 * IStorageEngine engine = Dummy.getEngine(base); ClassInfo ci =
		 * engine.getSession
		 * (true).getMetaModel().getClassInfo(IndexedObject.class.getName(),
		 * true); long sizebtree = ci.getIndex(0).getBTree().getSize();
		 * println("Size btree="+sizebtree); //println(new
		 * BTreeDisplay().build(ci.getIndex(0).getBTree(), false));
		 */
		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("duration", i));
			Objects objects = base.getObjects(q, false);
			// println("olivier" + (i+1));
			assertEquals(1, objects.size());
		}
		long end = OdbTime.getCurrentTimeInMs();

		try {
			float duration = (float) (end - start) / (float) size;

			if (testPerformance&& duration > 2) {
				fail("Time of search in index is greater than 2ms : " + duration);
			}

		} finally {
			base.close();
			deleteBase(baseName);
		}

	}

	/** Test with one key index */
	public void testInsertWithDateIndex3CheckAll() throws Exception {
		String baseName = getBaseName();
		// LogUtil.logOn(LazyODBBTreePersister.LOG_ID, true);
		deleteBase(baseName);
		ODB base = open(baseName);
		OdbConfiguration.setUseLazyCache(false);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "creation" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		/*
		 * String[] indexFields2 = { "name", "creation" };
		 * clazz.addUniqueIndexOn("index2", indexFields2);
		 * 
		 * String[] indexFields3 = { "duration", "creation" };
		 * clazz.addUniqueIndexOn("index3", indexFields3);
		 */
		base.close();

		base = open(baseName);

		int size = 1300;
		int commitInterval = 1000;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), i, new Date(start0 + i));
			base.store(io1);
			if (i % commitInterval == 0) {
				base.commit();
				// println(i+" : commit / " + size);
			}
		}
		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		// println("IPU=" + ObjectWriter.getNbInPlaceUpdates() + " - NU=" +
		// ObjectWriter.getNbNormalUpdates());
		// println("inserting time with index=" + (end0 - start0));

		base = open(baseName);
		/*
		 * IStorageEngine engine = Dummy.getEngine(base); ClassInfo ci =
		 * engine.getSession
		 * (true).getMetaModel().getClassInfo(IndexedObject.class.getName(),
		 * true); long sizebtree = ci.getIndex(0).getBTree().getSize();
		 * println("Size btree="+sizebtree); //println(new
		 * BTreeDisplay().build(ci.getIndex(0).getBTree(), false));
		 */
		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("creation", new Date(start0 + i)));
			Objects objects = base.getObjects(q, false);
			// println("olivier" + (i+1));
			assertEquals(1, objects.size());
		}
		long end = OdbTime.getCurrentTimeInMs();

		try {
			float duration = (float) (end - start) / (float) size;
			println(duration);
			double d = 0.144;
			if (!isLocal) {
				d = 1.16;
			}
			if (testPerformance && duration > d) {
				fail("Time of search in index is greater than " + d + " ms : " + duration);
			}

		} finally {
			base.close();
			deleteBase(baseName);
		}

	}

	/** Test with 3 indexes */
	public void testInsertWith3IndexesCheckAll() throws Exception {
		String baseName = getBaseName();
		// LogUtil.logOn(LazyODBBTreePersister.LOG_ID, true);
		deleteBase(baseName);
		ODB base = open(baseName);
		OdbConfiguration.setUseLazyCache(false);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "duration" };
		clazz.addIndexOn("index1", indexFields, true);

		String[] indexFields2 = { "creation" };
		clazz.addIndexOn("index2", indexFields2, true);

		String[] indexFields3 = { "name" };
		clazz.addIndexOn("index3", indexFields3, true);

		/*
		 * String[] indexFields2 = { "name", "creation" };
		 * clazz.addUniqueIndexOn("index2", indexFields2);
		 * 
		 * String[] indexFields3 = { "duration", "creation" };
		 * clazz.addUniqueIndexOn("index3", indexFields3);
		 */
		base.close();

		base = open(baseName);

		int size = 5000;
		int commitInterval = 10;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), i, new Date());
			base.store(io1);
			if (i % commitInterval == 0) {
				//base.commit();
				//println(i + " : commit / " + size);
			}
		}
		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		// println("IPU=" + ObjectWriter.getNbInPlaceUpdates() + " - NU=" +
		// ObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));

		base = open(baseName);
		/*
		 * IStorageEngine engine = Dummy.getEngine(base); ClassInfo ci =
		 * engine.getSession(true).getMetaModel().getClassInfo(
		 * IndexedObject.class.getName(), true); long sizebtree =
		 * ci.getIndex(0).getBTree().getSize(); println("Size btree=" +
		 * sizebtree); // println(new
		 * BTreeDisplay().build(ci.getIndex(0).getBTree(), false));
		 */
		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("duration", i));
			Objects objects = base.getObjects(q, false);
			// println("olivier" + (i+1));
			assertEquals(1, objects.size());
		}
		long end = OdbTime.getCurrentTimeInMs();

		try {
			float duration = (float) (end - start) / (float) size;

			println(duration);
			double d = 0.144;
			if (!isLocal) {
				d = 1.16;
			}
			if (testPerformance && duration > d) {
				fail("Time of search in index is greater than " + d + " ms : " + duration);
			}

		} finally {
			base.close();
			deleteBase(baseName);
		}

	}

	/** Test with on e key index */
	public void testInsertWithoutIndex3() throws Exception {
		String baseName = getBaseName();
		if (!runAll) {
			return;
		}
		deleteBase(baseName);
		/*
		 * ODB base = open(baseName); Configuration.setUseLazyCache(true);
		 * //base.store(new IndexedObject()); ClassRepresentation clazz =
		 * base.getClassRepresentation(IndexedObject.class); String[]
		 * indexFields = { "name" }; clazz.addUniqueIndexOn("index1",
		 * indexFields, true);
		 * 
		 * base.close();
		 */

		ODB base = open(baseName);

		int size = 30000;
		int commitInterval = 1000;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + size, new Date());
			base.store(io1);
			if (i % commitInterval == 0) {
				base.commit();
				// println(i+" : commit");
			}
		}
		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));

		base = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + size));
		long start = OdbTime.getCurrentTimeInMs();
		Objects objects = base.getObjects(q, false);
		long end = OdbTime.getCurrentTimeInMs();
		assertEquals(1, objects.size());
		IndexedObject io2 = (IndexedObject) objects.getFirst();
		assertEquals("olivier" + size, io2.getName());
		assertEquals(15 + size, io2.getDuration());
		long duration = end - start;
		println("duration=" + duration);

		base.close();
		deleteBase(baseName);
		OdbConfiguration.setUseLazyCache(false);
		println(duration);
		double d = 408;
		if (!isLocal) {
			d = 3500;
		}
		if (duration > d) {
			fail("Time of search in index is greater than " + d + " ms : " + duration);
		}

	}

	/** Test with two key index */
	public void testInsertWith3Indexes() throws Exception {
		String baseName = getBaseName();

		deleteBase(baseName);
		ODB base = open(baseName);
		// Configuration.setUseLazyCache(true);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);

		String[] indexFields3 = { "name" };
		clazz.addUniqueIndexOn("index3", indexFields3, true);

		String[] indexFields2 = { "name", "creation" };
		clazz.addUniqueIndexOn("index2", indexFields2, true);

		String[] indexField4 = { "duration", "creation" };
		clazz.addUniqueIndexOn("inde3", indexField4, true);

		base.close();

		base = open(baseName);

		int size = isLocal ? 10000 : 1000;
		long start0 = OdbTime.getCurrentTimeInMs();

		Date[] dates = new Date[size];
		for (int i = 0; i < size; i++) {
			// println(i);
			dates[i] = new Date();
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), i, dates[i]);
			base.store(io1);
			if (i % 100 == 0) {
				println(i);
			}
		}
		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));

		base = open(baseName);

		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.and().add(Where.equal("duration", i)).add(
					Where.equal("creation", dates[i])));
			Objects objects = base.getObjects(q, true);
			assertEquals(1, objects.size());
			assertTrue(q.getExecutionPlan().useIndex());
		}
		long end = OdbTime.getCurrentTimeInMs();
		double duration = (end - start);
		duration = duration / size;
		println("duration=" + duration);
		base.close();
		deleteBase(baseName);
		OdbConfiguration.setUseLazyCache(false);

		println(duration);
		double d = 0.11;
		if (!isLocal) {
			d = 10;
		}
		if (duration > d) {
			fail("Time of search in index is greater than " + d + " ms : " + duration);
		}
	}

	/** Test with two key index */
	public void testInsertWith4IndexesAndCommits() throws Exception {
		String baseName = getBaseName();
		if (!runAll) {
			return;
		}
		deleteBase(baseName);
		ODB base = open(baseName);
		// Configuration.setUseLazyCache(true);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);

		String[] indexField1 = { "duration" };
		clazz.addUniqueIndexOn("inde1", indexField1, true);

		String[] indexFields3 = { "name" };
		clazz.addUniqueIndexOn("index3", indexFields3, true);

		String[] indexFields2 = { "name", "creation" };
		clazz.addUniqueIndexOn("index2", indexFields2, true);

		String[] indexField4 = { "duration", "creation" };
		clazz.addUniqueIndexOn("inde4", indexField4, true);

		base.close();

		base = open(baseName);

		int size = 10000;
		int commitInterval = 10;
		long start0 = OdbTime.getCurrentTimeInMs();

		for (int i = 0; i < size; i++) {
			// println(i);
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), i, new Date());
			base.store(io1);
			if (i % 1000 == 0) {
				println(i);
			}
			if (i % commitInterval == 0) {
				base.commit();
			}
		}
		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));

		base = open(baseName);

		long start = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("duration", i));
			Objects objects = base.getObjects(q, false);
			// println("olivier" + (i+1));
			assertEquals(1, objects.size());
		}
		long end = OdbTime.getCurrentTimeInMs();
		long duration = end - start;
		println("duration=" + duration);
		base.close();
		deleteBase(baseName);
		OdbConfiguration.setUseLazyCache(false);
		if (testPerformance && duration > 111) {
			fail("Time of search in index : " + duration + ", should be less than 111");
		}
	}

	/** Test with two key index */
	public void testInsertWithIndex4() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);

		String[] indexFields3 = { "name" };
		clazz.addUniqueIndexOn("index3", indexFields3, true);

		String[] indexFields2 = { "name", "creation" };
		clazz.addUniqueIndexOn("index2", indexFields2, true);

		String[] indexField4 = { "duration", "creation" };
		clazz.addUniqueIndexOn("inde3", indexField4, true);

		base.close();

		base = open(baseName);

		int size = isLocal ? 50000 : 1000;
		int commitInterval = 1000;
		long start0 = OdbTime.getCurrentTimeInMs();

		for (int i = 0; i < size; i++) {
			// println(i);
			IndexedObject ioio = new IndexedObject("olivier" + (i + 1), i + 15 + size, new Date());
			base.store(ioio);
			if (i % commitInterval == 0) {
				long t0 = OdbTime.getCurrentTimeInMs();
				base.commit();
				long t1 = OdbTime.getCurrentTimeInMs();
				println(i + " : commit - ctime " + (t1 - t0) + " -ttime=");
				// println(LazyODBBTreePersister.counters());
				LazyODBBTreePersister.resetCounters();
			}
		}
		Date theDate = new Date();
		String theName = "name indexed";
		IndexedObject io1 = new IndexedObject(theName, 45, theDate);
		base.store(io1);
		base.close();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));

		base = open(baseName);
		// IQuery q = new
		// CriteriaQuery(IndexedObject.class,Restrictions.and().add(Restrictions.equal("name",theName)).add(Restrictions.equal("creation",
		// theDate)));
		IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", theName));

		long start = OdbTime.getCurrentTimeInMs();
		Objects objects = base.getObjects(q, true);
		long end = OdbTime.getCurrentTimeInMs();

		if (isLocal) {
			assertEquals("index3", q.getExecutionPlan().getIndex().getName());
		}

		assertEquals(1, objects.size());
		IndexedObject io2 = (IndexedObject) objects.getFirst();
		assertEquals(theName, io2.getName());
		assertEquals(45, io2.getDuration());
		assertEquals(theDate, io2.getCreation());
		long duration = end - start;
		println("duration=" + duration);
		base.close();
		deleteBase(baseName);
		OdbConfiguration.setUseLazyCache(false);
		if (testPerformance && duration > 1) {
			fail("Time of search in index > 1 : " + duration);
		}
	}

	public void testInsertAndDeleteWithIndex() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		base.close();

		base = open(baseName);

		int size = 1000;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
			if (i % 1000 == 0)
				println(i);
		}
		long tt0 = OdbTime.getCurrentTimeInMs();
		base.close();
		long tt1 = OdbTime.getCurrentTimeInMs();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));
		println("commit time=" + (tt1 - tt0));
		println(LazyODBBTreePersister.counters());
		base = open(baseName);

		long totalTime = 0;
		long maxTime = 0;
		long minTime = 100000;
		for (int i = 0; i < size; i++) {
			IQuery query = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(query, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + (i + 1), io2.getName());
			assertEquals(15 + i, io2.getDuration());
			long d = end - start;
			totalTime += d;

			if (d > maxTime) {
				maxTime = d;
			}
			if (d < minTime) {
				minTime = d;
			}
			base.delete(io2);

		}
		base.close();

		base = open(baseName);

		IQuery q = new CriteriaQuery(IndexedObject.class);
		Objects oos = base.getObjects(q, true);

		for (int i = 0; i < size; i++) {
			q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			oos = base.getObjects(q, true);
			assertEquals(0, oos.size());
		}
		base.close();
		deleteBase(baseName);

		println("total duration=" + totalTime + " / " + (double) totalTime / size);
		println("duration max=" + maxTime + " / min=" + minTime);
		if(testPerformance){
			assertTrue(totalTime / size < 0.9);
			// TODO Try to get maxTime < 10!
			assertTrue(maxTime < 20);
			assertTrue(minTime == 0);
		}

	}

	public void testInsertAndDeleteWithIndex1() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		base.close();

		base = open(baseName);

		int size = 1400;
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
		}
		base.close();
		System.out.println("----ola");
		base = open(baseName);

		IQuery q = new CriteriaQuery(IndexedObject.class);
		Objects<IndexedObject> objects = base.getObjects(q);
		while (objects.hasNext()) {
			IndexedObject io = objects.next();
			println(io);
			base.delete(io);
		}
		base.close();

	}

	public void testInsertAndDeleteWithIndexWith10000() throws Exception {
		String baseName = getBaseName();
		if (!runAll) {
			return;
		}

		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		base.close();

		base = open(baseName);

		int size = 10000;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
			if (i % 1000 == 0)
				println(i);
		}
		long tt0 = OdbTime.getCurrentTimeInMs();
		base.close();
		long tt1 = OdbTime.getCurrentTimeInMs();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));
		println("commit time=" + (tt1 - tt0));
		println(LazyODBBTreePersister.counters());
		base = open(baseName);

		long totalSelectTime = 0;
		long maxTime = 0;
		long minTime = 100000;
		long t0 = OdbTime.getCurrentTimeInMs();
		long t1 = 0;
		long ta1 = 0;
		long ta2 = 0;
		long totalTimeDelete = 0;
		long totalTimeSelect = 0;

		for (int j = 0; j < size; j++) {

			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (j + 1)));

			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + (j + 1), io2.getName());
			assertEquals(15 + j, io2.getDuration());
			long d = end - start;
			totalSelectTime += d;

			if (d > maxTime) {
				maxTime = d;
			}
			if (d < minTime) {
				minTime = d;
			}
			ta1 = OdbTime.getCurrentTimeInMs();
			base.delete(io2);
			ta2 = OdbTime.getCurrentTimeInMs();
			totalTimeDelete += (ta2 - ta1);
			totalTimeSelect += (end - start);
			if (j % 100 == 0 && j > 0) {
				t1 = OdbTime.getCurrentTimeInMs();
				println(j + " - t= " + (t1 - t0) + " - delete=" + (totalTimeDelete / j) + " / select=" + (totalTimeSelect / j));
				println(LazyODBBTreePersister.counters());
				LazyODBBTreePersister.resetCounters();
				t0 = t1;
			}
		}
		base.close();

		println("total select=" + totalSelectTime + " / " + (double) totalSelectTime / size);
		println("total delete=" + totalTimeDelete + " / " + (double) totalTimeDelete / size);
		println("duration max=" + maxTime + " / min=" + minTime);

		base = open(baseName);

		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(0, objects.size());
			if (i % 100 == 0)
				println(i);
		}

		base.close();
		deleteBase(baseName);
		float timePerObject = (float) totalSelectTime / (float) size;
		println("Time per object = " + timePerObject);
		if (timePerObject > 1) {
			println("Time per object = " + timePerObject);
		}
		assertTrue(timePerObject < 0.16);

		// TODO Try to get maxTime < 10!
		assertTrue(maxTime < 250);
		assertTrue(minTime < 1);

	}

	public void testInsertAndDeleteWithIndexWith4Elements() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		base.close();

		base = open(baseName);

		int size = 4;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
			if (i % 1000 == 0)
				println(i);
		}
		long tt0 = OdbTime.getCurrentTimeInMs();
		base.close();
		long tt1 = OdbTime.getCurrentTimeInMs();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));
		println("commit time=" + (tt1 - tt0));
		println(LazyODBBTreePersister.counters());
		base = open(baseName);
		long totalTime = 0;
		long maxTime = 0;
		long minTime = 100000;
		long t0 = OdbTime.getCurrentTimeInMs();
		long t1 = 0;
		/*
		 * IStorageEngine e = Dummy.getEngine(base); ClassInfoIndex cii =
		 * e.getSession(true).getMetaModel().getClassInfo(
		 * IndexedObject.class.getName(), true).getIndex(0); // println(new
		 * BTreeDisplay().build(cii.getBTree(),true));
		 */

		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + (i + 1), io2.getName());
			assertEquals(15 + i, io2.getDuration());
			long d = end - start;
			totalTime += d;

			if (d > maxTime) {
				maxTime = d;
			}
			if (d < minTime) {
				minTime = d;
			}
			base.delete(io2);

			if (i % 100 == 0) {
				t1 = OdbTime.getCurrentTimeInMs();
				println(i + " - t= " + (t1 - t0));
				t0 = t1;
			}
		}

		base.close();

		base = open(baseName);

		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(0, objects.size());
			if (i % 100 == 0)
				println(i);
		}
		base.close();
		deleteBase(baseName);
		double unitTime = (double) totalTime / size;
		println("total duration=" + totalTime + " / " + (double) totalTime / size);
		println("duration max=" + maxTime + " / min=" + minTime);

		if (isLocal) {
			assertTrue(unitTime < 1);
		} else {
			assertTrue(unitTime < 6);
		}

		// TODO Try to get maxTime < 10!
		if(testPerformance){
			assertTrue(maxTime < 250);
			assertTrue(minTime <= 1);
		}
	}

	public void testInsertAndDeleteWithIndexWith40Elements() throws Exception {
		String baseName = getBaseName();
		OdbConfiguration.setDefaultIndexBTreeDegree(3);
		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		base.close();

		base = open(baseName);

		int size = 6;
		long start0 = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
			if (i % 1000 == 0)
				println(i);
		}
		long tt0 = OdbTime.getCurrentTimeInMs();
		/*
		 * IStorageEngine e = Dummy.getEngine(base); ClassInfoIndex cii =
		 * e.getSession(true).getMetaModel().getClassInfo(
		 * IndexedObject.class.getName(), true).getIndex(0); // println(new
		 * BTreeDisplay().build(cii.getBTree(),true));
		 */
		base.close();
		long tt1 = OdbTime.getCurrentTimeInMs();
		long end0 = OdbTime.getCurrentTimeInMs();
		println("IPU=" + AbstractObjectWriter.getNbInPlaceUpdates() + " - NU=" + AbstractObjectWriter.getNbNormalUpdates());
		println("inserting time with index=" + (end0 - start0));
		println("commit time=" + (tt1 - tt0));
		println(LazyODBBTreePersister.counters());
		base = open(baseName);
		long totalTime = 0;
		long maxTime = 0;
		long minTime = 100000;
		long t0 = OdbTime.getCurrentTimeInMs();
		long t1 = 0;
		/*
		 * e = Dummy.getEngine(base); cii =
		 * e.getSession(true).getMetaModel().getClassInfo(
		 * IndexedObject.class.getName(), true).getIndex(0); println(new
		 * BTreeDisplay().build(cii.getBTree(), true));
		 */
		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(1, objects.size());
			IndexedObject io2 = (IndexedObject) objects.getFirst();
			assertEquals("olivier" + (i + 1), io2.getName());
			assertEquals(15 + i, io2.getDuration());
			long d = end - start;
			totalTime += d;

			if (d > maxTime) {
				maxTime = d;
			}
			if (d < minTime) {
				minTime = d;
			}
			base.delete(io2);

			if (i % 100 == 0) {
				t1 = OdbTime.getCurrentTimeInMs();
				println(i + " - t= " + (t1 - t0));
				t0 = t1;
			}
		}
		// println(new BTreeDisplay().build(cii.getBTree(), true));

		base.close();

		base = open(baseName);

		for (int i = 0; i < size; i++) {
			IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", "olivier" + (i + 1)));
			long start = OdbTime.getCurrentTimeInMs();
			Objects objects = base.getObjects(q, true);
			long end = OdbTime.getCurrentTimeInMs();
			assertEquals(0, objects.size());
			if (i % 100 == 0)
				println(i);
		}
		double unitTime = (double) totalTime / size;
		println("total duration=" + totalTime + " / " + unitTime);
		println("duration max=" + maxTime + " / min=" + minTime);
		base.close();
		deleteBase(baseName);

		if (isLocal) {
			assertTrue(unitTime < 1);
		} else {
			assertTrue(unitTime < 6);
		}

		// TODO Try to get maxTime < 10!
		if(testPerformance){
			assertTrue(maxTime < 250);
			assertTrue(minTime <= 1);
		}
		OdbConfiguration.setDefaultIndexBTreeDegree(20);
	}

	public void testSizeBTree() throws Exception {

		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields, true);

		base.close();

		base = open(baseName);

		int size = 4;
		for (int i = 0; i < size; i++) {
			IndexedObject io1 = new IndexedObject("olivier" + (i + 1), 15 + i, new Date());
			base.store(io1);
			if (i % 1000 == 0)
				println(i);
		}
		base.close();
		base = open(baseName);
		IStorageEngine e = Dummy.getEngine(base);
		ClassInfoIndex cii = e.getSession(true).getMetaModel().getClassInfo(IndexedObject.class.getName(), true).getIndex(0);
		base.close();
		deleteBase(baseName);
		assertEquals(size, cii.getBTree().getSize());
	}

	/**
	 * Test index with 3 keys .
	 * 
	 * Select using only one field to verify that query does not use index, then
	 * execute a query with the 3 fields and checks than index is used
	 */
	public void testInsertWith3Keys() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);
		// base.store(new IndexedObject());
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);

		String[] indexFields = { "name", "duration", "creation" };
		clazz.addUniqueIndexOn("index", indexFields, true);

		base.close();

		base = open(baseName);

		int size = isLocal ? 50000 : 500;
		int commitInterval = isLocal ? 10000 : 100;
		long start0 = OdbTime.getCurrentTimeInMs();

		for (int i = 0; i < size; i++) {
			IndexedObject io2 = new IndexedObject("olivier" + (i + 1), i + 15 + size, new Date());
			base.store(io2);
			if (i % commitInterval == 0) {
				long t0 = OdbTime.getCurrentTimeInMs();
				base.commit();
				long t1 = OdbTime.getCurrentTimeInMs();
				println(i + " : commit - ctime " + (t1 - t0) + " -ttime=");
				// println(LazyODBBTreePersister.counters());
				LazyODBBTreePersister.resetCounters();
			}
		}
		Date theDate = new Date();
		String theName = "name indexed";
		int theDuration = 45;
		IndexedObject io1 = new IndexedObject(theName, theDuration, theDate);
		base.store(io1);
		base.close();

		base = open(baseName);

		// first search without index
		IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", theName));

		Objects objects = base.getObjects(q, true);
		assertFalse(q.getExecutionPlan().useIndex());
		println(q.getExecutionPlan().getDetails());
		assertEquals(1, objects.size());

		IndexedObject io3 = (IndexedObject) objects.getFirst();
		assertEquals(theName, io3.getName());
		assertEquals(theDuration, io3.getDuration());
		assertEquals(theDate, io3.getCreation());
		base.close();

		base = open(baseName);

		// Then search usin index
		q = new CriteriaQuery(IndexedObject.class, Where.and().add(Where.equal("name", theName)).add(Where.equal("creation", theDate)).add(
				Where.equal("duration", theDuration)));

		objects = base.getObjects(q, true);
		assertTrue(q.getExecutionPlan().useIndex());
		if (isLocal) {
			assertEquals("index", q.getExecutionPlan().getIndex().getName());
		}
		println(q.getExecutionPlan().getDetails());
		assertEquals(1, objects.size());

		io3 = (IndexedObject) objects.getFirst();
		assertEquals(theName, io3.getName());
		assertEquals(theDuration, io3.getDuration());
		assertEquals(theDate, io3.getCreation());
		base.close();

	}

	/**
	 * Test index. Creates 1000 objects. Take 10 objects to update 10000 times.
	 * Then check if all objects are ok
	 * 
	 */
	public void testXUpdatesWithIndex() throws Exception {
		String baseName = getBaseName();
		try {
			deleteBase(baseName);
			ODB base = open(baseName);
			// base.store(new IndexedObject());
			ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);

			String[] indexFields = { "name" };
			clazz.addUniqueIndexOn("index", indexFields, true);

			base.close();

			base = open(baseName);

			long start = System.currentTimeMillis();
			int size = 1000;
			int nbObjects = 10;
			int nbUpdates = isLocal ? 100 : 50;
			for (int i = 0; i < size; i++) {
				IndexedObject io1 = new IndexedObject("IO-" + i + "-0", i + 15 + size, new Date());
				base.store(io1);
			}
			base.close();
			println("Time of insert " + size + " objects = " + size);

			String[] indexes = { "IO-0-0", "IO-100-0", "IO-200-0", "IO-300-0", "IO-400-0", "IO-500-0", "IO-600-0", "IO-700-0", "IO-800-0",
					"IO-900-0" };

			long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0, t6 = 0;

			for (int i = 0; i < nbUpdates; i++) {
				start = OdbTime.getCurrentTimeInMs();

				for (int j = 0; j < nbObjects; j++) {
					t1 = System.currentTimeMillis();
					base = open(baseName);
					t2 = System.currentTimeMillis();
					IQuery q = new CriteriaQuery(IndexedObject.class, Where.equal("name", indexes[j]));
					Objects os = base.getObjects(q);
					t3 = System.currentTimeMillis();
					assertTrue(q.getExecutionPlan().useIndex());
					assertEquals(1, os.size());
					// check if index has been used
					assertTrue(q.getExecutionPlan().useIndex());
					IndexedObject io = (IndexedObject) os.getFirst();
					if (i > 0) {
						assertTrue(io.getName().endsWith(("-" + (i - 1))));
					}
					io.setName(io.getName() + "-updated-" + i);
					base.store(io);
					t4 = System.currentTimeMillis();
					if (isLocal && j == 0) {
						IStorageEngine engine = Dummy.getEngine(base);
						ClassInfo ci = engine.getSession(true).getMetaModel().getClassInfo(IndexedObject.class.getName(), true);
						ClassInfoIndex cii = ci.getIndex(0);
						assertEquals(size, cii.getBTree().getSize());
					}
					indexes[j] = io.getName();
					assertEquals(new BigInteger("" + size), base.count(new CriteriaQuery(IndexedObject.class)));
					t5 = System.currentTimeMillis();
					base.commit();
					base.close();
					t6 = System.currentTimeMillis();

				}
				long end = OdbTime.getCurrentTimeInMs();
				System.out.println("Nb Updates of " + nbObjects + " =" + i + " - " + (end - start) + "ms  -- open=" + (t2 - t1)
						+ " - getObjects=" + (t3 - t2) + " - update=" + (t4 - t3) + " - count=" + (t5 - t4) + " - close=" + (t6 - t5));
			}
		} finally {
		}
	}

	public void simpleUniqueIndex() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		ClassRepresentation clazz = odb.getClassRepresentation(Function.class);

		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("index", indexFields, true);

		odb.close();

		odb = open(baseName);

		// inserting 3 objects with 3 different index keys
		odb.store(new Function("function1"));
		odb.store(new Function("function2"));
		odb.store(new Function("function3"));

		odb.close();

		odb = open(baseName);
		try {
			// Tries to store another function with name function1 => send an
			// exception because of duplicated keys
			odb.store(new Function("function1"));
			fail("Should have thrown Exception");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testIndexExist1() {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		ClassRepresentation clazz = odb.getClassRepresentation(Function.class);

		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("my-index", indexFields, true);
		odb.store(new Function("test"));
		odb.close();

		odb = open(baseName);
		assertTrue(odb.getClassRepresentation(Function.class).existIndex("my-index"));
		assertFalse(odb.getClassRepresentation(Function.class).existIndex("my-indexdhfdjkfhdjkhj"));

		odb.close();
	}

	public void testIndexExist2() {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		ClassRepresentation clazz = odb.getClassRepresentation(Function.class);

		String[] indexFields = { "name" };
		clazz.addUniqueIndexOn("my-index", indexFields, true);

		odb.close();

		odb = open(baseName);
		assertTrue(odb.getClassRepresentation(Function.class).existIndex("my-index"));
		assertFalse(odb.getClassRepresentation(Function.class).existIndex("my-indexdhfdjkfhdjkhj"));

		odb.close();
	}

	/** neodatisee
	 * 
	 * @throws Exception
	 */
	public void testIndexWillNullKey() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields = { "name", "duration" };
		clazz.addUniqueIndexOn("index1", indexFields, true);
		base.close();

		base = open(baseName);
		

		base.close();
		deleteBase(baseName);
	}
	public static void main(String[] args) throws Exception {
		TestIndex ti = new TestIndex();
		ti.testInsertWithIndex3Part2();
	}
}
