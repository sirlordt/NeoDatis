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

import java.util.Date;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class TestIndex2 extends ODBTest {

	public void testIndexFail() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		String indexName = "index1";
		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1", "i2", "i3" };
		clazz.addUniqueIndexOn(indexName, indexFields1, true);

		base.close();

		base = open(baseName);
		IndexedObject3 io = new IndexedObject3(1, 2, 3, "1", "2", "3", new Date(), new Date(), new Date());
		base.store(io);

		try {
			IndexedObject3 io2 = new IndexedObject3(1, 2, 3, "1", "2", "3", new Date(), new Date(), new Date());
			base.store(io2);
		} catch (Exception e) {
			assertTrue(e.getMessage().indexOf(indexName) != -1);
			// println(e.getMessage());
		}

		base.close();
		base = open(baseName);
		Objects<IndexedObject3> oo3 = base.getObjects(IndexedObject3.class);
		base.close();

		assertEquals(0, oo3.size());
		deleteBase(baseName);
	}

	public void testSaveIndex() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1", "i2", "i3" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);

		String[] indexFields2 = { "s1", "s2", "s3" };
		clazz.addUniqueIndexOn("index2", indexFields2, true);

		String[] indexFields3 = { "dt1", "dt2", "dt3" };
		clazz.addUniqueIndexOn("index3", indexFields3, true);

		String[] indexFields4 = { "i1", "i2", "i3", "s1", "s2", "s3", "dt1", "dt2", "dt3" };
		clazz.addUniqueIndexOn("index4", indexFields4, true);

		base.close();

		base = open(baseName);

		ISession session = Dummy.getEngine(base).getSession(true);
		MetaModel metaModel = session.getStorageEngine().getSession(true).getMetaModel();
		ClassInfo ci = metaModel.getClassInfo(IndexedObject3.class.getName(), true);
		assertEquals(4, ci.getNumberOfIndexes());

		assertEquals(ci.getIndex(0).getName(), "index1");
		assertEquals(3, ci.getIndex(0).getAttributeIds().length);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(0).getStatus());

		assertEquals(ci.getIndex(1).getName(), "index2");
		assertEquals(3, ci.getIndex(1).getAttributeIds().length);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(1).getStatus());

		assertEquals(ci.getIndex(2).getName(), "index3");
		assertEquals(3, ci.getIndex(2).getAttributeIds().length);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(2).getStatus());

		assertEquals(ci.getIndex(3).getName(), "index4");
		assertEquals(9, ci.getIndex(3).getAttributeIds().length);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(3).getStatus());
		base.close();

		base = open(baseName);

		for (int i = 0; i < 10; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			base.store(io);
		}

		base.close();

		deleteBase(baseName);
	}

	/**
	 * Test index creation without commit
	 * 
	 * @throws Exception
	 */
	public void testCreateIndexWithoutCommit() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB base = open(baseName);

		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);

		for (int i = 0; i < 10; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			base.store(io);
		}

		base.close();

		base = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject3.class, Where.equal("i1", 1));
		Objects<IndexedObject3> iis = base.getObjects(q);
		base.close();
		assertEquals(1, iis.size());
		assertTrue(q.getExecutionPlan().useIndex());

		deleteBase(baseName);
	}

	/**
	 * Opens a connection C1, then create the index in another connection C2 and
	 * then stores the object in connection C1
	 * 
	 * @throws Exception
	 */
	public void testCreateIndexInOtherConnection() throws Exception {
		if (isLocal || !testNewFeature) {
			return;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb1 = open(baseName);
		ODB odb2 = open(baseName);

		ClassRepresentation clazz = odb2.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);
		odb2.close();

		for (int i = 0; i < 10; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			odb1.store(io);
		}

		odb1.close();

		ODB odb = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject3.class, Where.equal("i1", 1));
		Objects<IndexedObject3> iis = odb.getObjects(q);
		odb.close();
		assertEquals(1, iis.size());
		assertTrue(q.getExecutionPlan().useIndex());

		deleteBase(baseName);
	}

	/**
	 * Opens a connection C1, then create the index in another connection C2 and
	 * then stores the object in connection C1
	 * 
	 * @throws Exception
	 */
	public void testCreateIndexInOtherConnectionNoClose() throws Exception {
		if (isLocal || !testNewFeature) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb1 = open(baseName);
		ODB odb2 = open(baseName);

		ClassRepresentation clazz = odb2.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);
		odb2.commit();

		for (int i = 0; i < 10; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			odb1.store(io);
		}

		odb1.close();

		ODB odb = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject3.class, Where.equal("i1", 1));
		Objects<IndexedObject3> iis = odb.getObjects(q);
		odb.close();
		odb2.close();
		assertEquals(1, iis.size());
		assertTrue(q.getExecutionPlan().useIndex());

		deleteBase(baseName);
	}

	/**
	 * Opens a connection C1, then create the index in another connection C2 and
	 * then stores the object in connection C1
	 * 
	 * @throws Exception
	 */
	public void testCreateIndexInOtherConnectionNoCommit1() throws Exception {
		if (isLocal || !testNewFeature) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb1 = open(baseName);
		ODB odb2 = open(baseName);

		ClassRepresentation clazz = odb2.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);

		for (int i = 0; i < 10; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			odb1.store(io);
		}

		odb2.close();
		odb1.close();

		ODB odb = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject3.class, Where.equal("i1", 1));
		Objects<IndexedObject3> iis = odb.getObjects(q);
		odb.close();
		assertEquals(1, iis.size());
		assertTrue(q.getExecutionPlan().useIndex());

		deleteBase(baseName);
	}

	/**
	 * Opens a connection C1, then create the index in another connection C2 and
	 * then stores the object in connection C1
	 * 
	 * @throws Exception
	 */
	public void testCreateIndexInOtherConnectionNoCommit2() throws Exception {
		if (isLocal || !testNewFeature) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb1 = open(baseName);
		ODB odb2 = open(baseName);

		ClassRepresentation clazz = odb2.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);

		for (int i = 0; i < 10; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			odb1.store(io);
		}

		odb1.close();
		odb2.close();

		ODB odb = open(baseName);
		IQuery q = new CriteriaQuery(IndexedObject3.class, Where.equal("i1", 1));
		Objects<IndexedObject3> iis = odb.getObjects(q);
		odb.close();
		assertEquals(1, iis.size());
		assertTrue(q.getExecutionPlan().useIndex());

		deleteBase(baseName);
	}

	/**
	 * Create objects, then create index, then execute a select with index, then
	 * rebuild index e execute
	 * 
	 * @throws Exception
	 */
	public void testRebuildIndex() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);

		ODB base = open(baseName);

		for (int i = 0; i < 2500; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			base.store(io);
		}

		base.close();

		base = open(baseName);

		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1", "i2", "i3" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);

		base.close();

		base = open(baseName);
		ISession session = Dummy.getEngine(base).getSession(true);
		MetaModel metaModel = session.getStorageEngine().getSession(true).getMetaModel();
		ClassInfo ci = metaModel.getClassInfo(IndexedObject3.class.getName(), true);
		assertEquals(1, ci.getNumberOfIndexes());

		assertEquals(ci.getIndex(0).getName(), "index1");
		assertEquals(3, ci.getIndex(0).getAttributeIds().length);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(0).getStatus());

		IQuery q = new CriteriaQuery(IndexedObject3.class, Where.and().add(Where.equal("i1", 10)).add(Where.equal("i2", 2)).add(
				Where.equal("i3", 3)));
		Objects<IndexedObject3> objects = base.getObjects(q);
		assertEquals(true, q.getExecutionPlan().useIndex());

		base.getClassRepresentation(IndexedObject3.class).rebuildIndex("index1", true);
		base.close();

		base = open(baseName);
		objects = base.getObjects(q);
		assertEquals(true, q.getExecutionPlan().useIndex());
		base.close();

		deleteBase(baseName);
	}

	/**
	 * Create objects, then create index, then execute a select with index, then
	 * rebuild index e execute
	 * 
	 * @throws Exception
	 */
	public void testDeleteIndex() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);

		ODB base = open(baseName);

		for (int i = 0; i < 2500; i++) {
			IndexedObject3 io = new IndexedObject3(1 + i, 2, 3, "1" + i, "2", "3", new Date(2009, i, 1), new Date(), new Date());
			base.store(io);
		}

		base.close();

		base = open(baseName);

		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject3.class);
		String[] indexFields1 = { "i1", "i2", "i3" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);

		base.close();

		base = open(baseName);
		ISession session = Dummy.getEngine(base).getSession(true);
		MetaModel metaModel = session.getStorageEngine().getSession(true).getMetaModel();
		ClassInfo ci = metaModel.getClassInfo(IndexedObject3.class.getName(), true);
		assertEquals(1, ci.getNumberOfIndexes());

		assertEquals(ci.getIndex(0).getName(), "index1");
		assertEquals(3, ci.getIndex(0).getAttributeIds().length);
		assertEquals(ClassInfoIndex.ENABLED, ci.getIndex(0).getStatus());

		IQuery q = new CriteriaQuery(IndexedObject3.class, Where.and().add(Where.equal("i1", 10)).add(Where.equal("i2", 2)).add(
				Where.equal("i3", 3)));
		Objects<IndexedObject3> objects = base.getObjects(q);
		assertEquals(true, q.getExecutionPlan().useIndex());

		base.getClassRepresentation(IndexedObject3.class).deleteIndex("index1", true);
		base.close();

		base = open(baseName);
		objects = base.getObjects(q);
		assertEquals(false, q.getExecutionPlan().useIndex());
		base.close();

		deleteBase(baseName);
	}
	
	public void testUpdateObject() throws Exception {
		String baseName = getBaseName();
		deleteBase(baseName);

		ODB base = open(baseName);

		ClassRepresentation clazz = base.getClassRepresentation(IndexedObject.class);
		String[] indexFields1 = { "name" };
		clazz.addUniqueIndexOn("index1", indexFields1, true);

		base.close();

		base = open(baseName);
		base.store(new IndexedObject("name", 10, new Date()));
		base.close();
		

		base = open(baseName);
		IndexedObject io = (IndexedObject) base.getObjects(IndexedObject.class).getFirst();
		io.setDuration(1000);
		base.store(io);
		base.close();

	}
}
