package org.neodatis.odb.test.performance;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer3.engine.FileSystemInterface;
import org.neodatis.odb.impl.core.layers.layer3.buffer.MultiBufferedIO;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.tool.IOUtil;
import org.neodatis.tool.wrappers.OdbTime;

public class PerformanceTest1OnlySelect {
	public static int TEST_SIZE = 50000;
	public static final String ODB_FILE_NAME = "perf-select.neodatis";

	public void buildBase() throws Exception {

		boolean inMemory = true;
		// Deletes the database file
		IOUtil.deleteFile(ODB_FILE_NAME);
		long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0, t6 = 0, t7 = 0, t77 = 0, t8 = 0;
		ODB odb = null;
		Objects l = null;
		SimpleObject so = null;

		// Insert TEST_SIZE objects
		System.out.println("Inserting " + TEST_SIZE + " objects");

		t1 = OdbTime.getCurrentTimeInMs();
		odb = ODBFactory.open(ODB_FILE_NAME);
		for (int i = 0; i < TEST_SIZE; i++) {
			Object o = getSimpleObjectInstance(i);
			odb.store(o);
			if (i % 10000 == 0) {
				// System.out.println("i="+i);
				MemoryMonitor.displayCurrentMemory("" + i, true);
				// System.out.println("Cache="+Dummy.getEngine(odb).getSession().getCache().toString());
			}
		}
		t2 = OdbTime.getCurrentTimeInMs();
		// Closes the database
		odb.close();
	}

	public void testSelectSimpleObjectODB() throws Exception {
		long t3 = OdbTime.getCurrentTimeInMs();
		boolean inMemory = true;
		System.out.println("Retrieving " + TEST_SIZE + " objects");
		// Reopen the database
		ODB odb = ODBFactory.open(ODB_FILE_NAME);

		// Gets the TEST_SIZE objects
		Objects l = odb.getObjects(SimpleObject.class, inMemory);
		System.out.println(l.getClass().getName());
		long t4 = OdbTime.getCurrentTimeInMs();
		System.out.println("l.size=" + l.size());

		int i = 0;
		while (l.hasNext()) {
			Object o = l.next();
			if (i % 10000 == 0) {
				MemoryMonitor.displayCurrentMemory("select " + i, true);
				// System.out.println("Cache="+Dummy.getEngine(odb).getSession().getCache().toString());
			}
			i++;
		}
		long t5 = OdbTime.getCurrentTimeInMs();

		odb.close();

		displayResult("ODB " + TEST_SIZE + " SimpleObject objects ", t3, t4, t5);

		System.out.println("buffer Ok=" + MultiBufferedIO.nbBufferOk + " / buffer not ok =" + MultiBufferedIO.nbBufferNotOk);
		System.out.println("nb1=" + FileSystemInterface.nbCall1 + " / nb2 =" + FileSystemInterface.nbCall2);
	}

	private SimpleObject getSimpleObjectInstance(int i) {
		SimpleObject so = new SimpleObject();
		so.setDate(new Date());
		so.setDuration(i);
		so.setName("Bonjour, comment allez vous?" + i);
		return so;
	}

	private void displayResult(String string, long t1, long t2, long t3) {

		String s1 = " total=" + (t3 - t1);
		String s3 = " total select=" + (t3 - t1) + " -- " + "select=" + (t2 - t1) + " get=" + (t3 - t2);
		String s4 = " time/object=" + (float) (t3 - t1) / +TEST_SIZE;

		System.out.println(string + s1 + " | " + s3 + " | " + s4);

	}

	public static void main(String[] args) throws Exception {
		PerformanceTest1OnlySelect pt = new PerformanceTest1OnlySelect();
		Thread.sleep(20000);
		pt.testSelectSimpleObjectODB();

	}

}
