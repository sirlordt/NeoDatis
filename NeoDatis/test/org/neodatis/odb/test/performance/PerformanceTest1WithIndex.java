package org.neodatis.odb.test.performance;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class PerformanceTest1WithIndex extends ODBTest {
	public static int TEST_SIZE = 110;

	public void test1() throws Exception {
		if (!testPerformance) {
			return;
		}
		if (isLocal) {
			t1estInsertSimpleObjectODB(20000);
		} else {
			t1estInsertSimpleObjectODB(2000);
		}
	}

	public void test2() throws Exception {
		t1estInsertSimpleObjectODB(200);
	}

	public void t1estInsertSimpleObjectODB(int size) throws Exception {
		String baseName = getBaseName();
		TEST_SIZE = size;
		boolean doUpdate = true;
		boolean doDelete = true;
		int commitInterval = 100;

		// Configuration.setUseLazyCache(true);
		boolean inMemory = true;
		// Configuration.monitorMemory(true);
		// Configuration.setUseModifiedClass(true);
		// Deletes the database file
		long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0, t6 = 0, t7 = 0, t77 = 0, t8 = 0;
		ODB odb = null;
		Objects l = null;
		SimpleObject so = null;

		// Insert TEST_SIZE objects
		println("Inserting " + TEST_SIZE + " objects");

		t1 = OdbTime.getCurrentTimeInMs();
		odb = open(baseName);

		String[] fields = { "name" };
		odb.getClassRepresentation(SimpleObject.class).addUniqueIndexOn("index1", fields, true);

		for (int i = 0; i < TEST_SIZE; i++) {
			Object o = getSimpleObjectInstance(i);
			odb.store(o);
			if (i % 10000 == 0) {
				// println("i="+i);
				MemoryMonitor.displayCurrentMemory("" + i, false);
				// println("Cache="+Dummy.getEngine(odb).getSession().getCache().toString());
			}
		}
		t2 = OdbTime.getCurrentTimeInMs();
		// Closes the database
		odb.close();
		// if(true)return;
		t3 = OdbTime.getCurrentTimeInMs();

		println("Retrieving " + TEST_SIZE + " objects");
		// Reopen the database
		odb = open(baseName);

		// Gets the TEST_SIZE objects
		t4 = OdbTime.getCurrentTimeInMs();
		IQuery q = null;

		for (int j = 0; j < TEST_SIZE; j++) {
			// println("Bonjour, comment allez vous?" + j);
			q = new CriteriaQuery(SimpleObject.class, Where.equal("name", "Bonjour, comment allez vous?" + j));
			Objects objects = odb.getObjects(q);
			assertTrue(q.getExecutionPlan().useIndex());
			so = (SimpleObject) objects.getFirst();
			if (!so.getName().equals("Bonjour, comment allez vous?" + j)) {
				throw new Exception("error while getting object : expected = " + "Bonjour, comment allez vous?" + j + " / actual = "
						+ so.getName());
			}
			if (j % 1000 == 0) {
				println("got " + j + " objects");
			}

		}

		t5 = OdbTime.getCurrentTimeInMs();

		odb.close();
		odb = open(baseName);

		if (doUpdate) {
			println("Updating " + TEST_SIZE + " objects");
			so = null;
			l = odb.getObjects(SimpleObject.class, inMemory);
			while (l.hasNext()) {
				so = (SimpleObject) l.next();
				so.setName(so.getName().toUpperCase());
				odb.store(so);
			}
		}

		/*
		 * l.reset(); // Actually get objects while (l.hasNext()) { so =
		 * (SimpleObject) l.next(); so.setName(so.getName() + " updated");
		 * odb.store(so); }
		 */

		t6 = OdbTime.getCurrentTimeInMs();

		odb.close();
		// if(true)return;
		t7 = OdbTime.getCurrentTimeInMs();

		if (doDelete) {

			println("Deleting " + TEST_SIZE + " objects");
			odb = open(baseName);
			println("After open - before delete");
			l = odb.getObjects(SimpleObject.class, inMemory);
			t77 = OdbTime.getCurrentTimeInMs();
			println("After getting objects - before delete");

			int i = 0;

			while (l.hasNext()) {
				so = (SimpleObject) l.next();
				if (!so.getName().startsWith("BONJOUR")) {
					throw new RuntimeException("Update  not ok for " + so.getName());
				}
				odb.delete(so);
				if (i % 10000 == 0) {
					println("s=" + i);
					// println("Cache="+Dummy.getEngine(odb).getSession().getCache().toString());
				}
				i++;
			}
			/*
			 * while(l.hasNext()){ so = (SimpleObject) l.next(); odb.delete(so);
			 * }
			 */
			odb.close();

		}

		t8 = OdbTime.getCurrentTimeInMs();
		// t4 2 times
		displayResult("ODB " + TEST_SIZE + " SimpleObject objects ", t1, t2, t4, t4, t5, t6, t7, t77, t8);
	}

	private SimpleObject getSimpleObjectInstance(int i) {
		SimpleObject so = new SimpleObject();
		so.setDate(new Date());
		so.setDuration(i);
		so.setName("Bonjour, comment allez vous?" + i);
		return so;
	}

	private void displayResult(String string, long t1, long t2, long t3, long t4, long t5, long t6, long t7, long t77, long t8) {

		String s1 = " total=" + (t8 - t1);
		String s2 = " total insert=" + (t3 - t1) + " -- " + "insert=" + (t2 - t1) + " commit=" + (t3 - t2) + " o/s=" + (float) TEST_SIZE
				/ (float) ((t3 - t1)) * 1000;
		String s3 = " total select=" + (t5 - t3) + " -- " + "select=" + (t4 - t3) + " get=" + (t5 - t4) + " o/s=" + (float) TEST_SIZE
				/ (float) ((t5 - t3)) * 1000;
		String s4 = " total update=" + (t7 - t5) + " -- " + "update=" + (t6 - t5) + " commit=" + (t7 - t6) + " o/s=" + (float) TEST_SIZE
				/ (float) ((t7 - t5)) * 1000;
		String s5 = " total delete=" + (t8 - t7) + " -- " + "select=" + (t77 - t7) + " - delete=" + (t8 - t77) + " o/s="
				+ (float) TEST_SIZE / (float) ((t8 - t7)) * 1000;

		println(string + s1 + " | " + s2 + " | " + s3 + " | " + s4 + " | " + s5);

		long tinsert = t3 - t1;
		long tselect = t5 - t3;
		long tupdate = t7 - t5;
		long tdelete = t8 - t7;

		if (isLocal) {
			assertTrue("Performance", tinsert < 1050);
			assertTrue("Performance", tselect < 535);
			assertTrue("Performance", tupdate < 582);
			assertTrue("Performance", tdelete < 740);
		} else {
			// System.out.println(tinsert);
			// System.out.println(tselect);
			// System.out.println(tupdate);
			// System.out.println(tdelete);
			assertTrue(tinsert < 17000);
			assertTrue(tselect < 25000);
			assertTrue(tupdate < 32000);
			assertTrue(tdelete < 15500);
		}
	}

	public static void main(String[] args) throws Exception {
		PerformanceTest1WithIndex pt = new PerformanceTest1WithIndex();
		// Thread.sleep(20000);
		// LogUtil.allOn(true);
		pt.t1estInsertSimpleObjectODB(10000);
	}

}
