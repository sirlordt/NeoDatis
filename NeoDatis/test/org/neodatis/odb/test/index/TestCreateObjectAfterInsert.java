package org.neodatis.odb.test.index;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestCreateObjectAfterInsert extends ODBTest {

	public void testLong() {
		println("" + Long.MAX_VALUE);
		long l = Long.MAX_VALUE - 1;
		l = l + 1;
		println("" + l);
		println("" + l + 1);

	}

	/**
	 * Test the creation of an index after having created objects. In this case
	 * ODB should creates the index and update it with already existing objects
	 * 
	 * @throws Exception
	 */
	public void test1Object() throws Exception {
		ODB odb = null;
		String baseName = getBaseName();
		try {
			odb = open(baseName);

			IndexedObject io = new IndexedObject("name", 5, new Date());
			odb.store(io);
			odb.close();

			odb = open(baseName);
			String[] names = { "name" };
			odb.getClassRepresentation(IndexedObject.class).addUniqueIndexOn("index1", names, true);
			Objects objects = odb.getObjects(new CriteriaQuery(IndexedObject.class, Where.equal("name", "name")), true);
			assertEquals(1, objects.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	/**
	 * Test the creation of an index after having created objects. In this case
	 * ODB should creates the index and update it with already existing objects
	 * 
	 * @throws Exception
	 */
	public void test20000Objects() throws Exception {
		long start = OdbTime.getCurrentTimeInMs();
		String baseName = getBaseName();
		ODB odb = null;
		int size = isLocal ? 20000 : 2000;
		try {
			odb = open(baseName);

			for (int i = 0; i < size; i++) {
				IndexedObject io = new IndexedObject("name" + i, i, new Date());
				odb.store(io);
			}
			odb.close();

			odb = open(baseName);
			String[] names = { "name" };
			odb.getClassRepresentation(IndexedObject.class).addUniqueIndexOn("index1", names, true);
			Objects objects = odb.getObjects(new CriteriaQuery(IndexedObject.class, Where.equal("name", "name0")), true);
			assertEquals(1, objects.size());

			objects = odb.getObjects(new CriteriaQuery(IndexedObject.class), true);
			MemoryMonitor.displayCurrentMemory("BTREE", true);
			assertEquals(size, objects.size());
		} finally {
			if (odb != null) {
				odb.close();
			}
			long end = OdbTime.getCurrentTimeInMs();
			println((end - start) + "ms");
		}
	}

	/**
	 * Test the creation of an index after having created objects. In this case
	 * ODB should creates the index and update it with already existing objects
	 * 
	 * @throws Exception
	 */
	public void test100000Objects() throws Exception {
		String baseName = getBaseName();
		ODB odb = null;
		int size = isLocal ? 100000 : 10001;
		long start = OdbTime.getCurrentTimeInMs();
		OdbConfiguration.monitorMemory(true);
		OdbConfiguration.setReconnectObjectsToSession(false);
		try {
			println("MaxNbObjects/cache = " + OdbConfiguration.getMaxNumberOfObjectInCache());
			odb = open(baseName);

			for (int i = 0; i < size; i++) {
				IndexedObject io = new IndexedObject("name" + i, i, new Date());
				odb.store(io);
				if (i % 10000 == 0) {
					MemoryMonitor.displayCurrentMemory(i + " objects created", true);
				}
			}
			odb.close();
			println("\n\n END OF INSERT \n\n");
			odb = open(baseName);
			String[] names = { "name" };
			odb.getClassRepresentation(IndexedObject.class).addUniqueIndexOn("index1", names, true);
			println("\n\n after create index\n\n");
			Objects objects = odb.getObjects(new CriteriaQuery(IndexedObject.class, Where.equal("name", "name0")), true);
			println("\n\nafter get Objects\n\n");
			assertEquals(1, objects.size());

			objects = odb.getObjects(new CriteriaQuery(IndexedObject.class, Where.equal("duration", 9)), true);
			assertEquals(1, objects.size());

			objects = odb.getObjects(new CriteriaQuery(IndexedObject.class), true);
			assertEquals(size, objects.size());
		} catch (Exception e) {
			throw e;
		} finally {

			/*
			 * if(odb!=null){ odb.close(); }
			 */
			long end = OdbTime.getCurrentTimeInMs();
			println((end - start) + "ms");
			OdbConfiguration.monitorMemory(false);
			odb.close();
		}
	}

	/**
	 * Test the creation of an index after having created objects. In this case
	 * ODB should creates the index and update it with already existing objects
	 * 
	 * @throws Exception
	 */
	public void test100000ObjectsIntiNdex() throws Exception {
		String baseName = getBaseName();

		ODB odb = null;
		int size = isLocal ? 90000 : 10100;
		long start = OdbTime.getCurrentTimeInMs();
		OdbConfiguration.monitorMemory(true);
		try {

			deleteBase(baseName);
			odb = open(baseName);

			for (int i = 0; i < size; i++) {
				IndexedObject io = new IndexedObject("name" + i, i, new Date());
				odb.store(io);
				if (i % 10000 == 0) {
					MemoryMonitor.displayCurrentMemory(i + " objects created", true);
				}
			}
			odb.close();
			println("\n\n END OF INSERT \n\n");
			odb = open(baseName);
			String[] names = { "duration" };
			odb.getClassRepresentation(IndexedObject.class).addUniqueIndexOn("index1", names, true);
			println("\n\n after create index\n\n");
			Objects objects = odb.getObjects(new CriteriaQuery(IndexedObject.class, Where.equal("name", "name0")), true);
			println("\n\nafter get Objects\n\n");
			assertEquals(1, objects.size());

			objects = odb.getObjects(new CriteriaQuery(IndexedObject.class, Where.equal("duration", 10000)), true);
			assertEquals(1, objects.size());

			objects = odb.getObjects(new CriteriaQuery(IndexedObject.class), true);
			assertEquals(size, objects.size());
		} catch (Exception e) {
			throw e;
		} finally {

			/*
			 * if(odb!=null){ odb.close(); }
			 */
			long end = OdbTime.getCurrentTimeInMs();
			println((end - start) + "ms");
			OdbConfiguration.monitorMemory(false);
		}
	}
}
