package org.neodatis.odb.test.multithread;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestTwoThread extends ODBTest {

	/**
	 * Opens the database in a thread and execute a query in another thread.
	 * This kind of problem can occur when using NeoDatis in a swing
	 * application
	 * 
	 * @throws Exception
	 * 
	 */
	public void test2() throws Exception {
		deleteBase("swing");
		Thread1 t1 = new Thread1();
		t1.start();
		Thread1.sleep(500);
		ODB odb = t1.getOdb();

		odb.store(new Function("f1"));
		odb.store(new Function("swing2"));

		Objects objects = odb.getObjects(Function.class);
		assertEquals(3, objects.size());
		objects = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "swing")));
		assertEquals(1, objects.size());

		odb.close();
		Thread.sleep(500);

	}

	public void test3() throws Exception {
		deleteBase("swing");
		Thread1 t1 = new Thread1();
		t1.start();
		Thread1.sleep(500);
		ODB odb = t1.getOdb();
		Thread2 t2 = new Thread2(odb);
		t2.start();

		odb.getObjects(Function.class);

	}
}

class Thread1 extends Thread {
	private ODB odb;

	public void run() {
		try {
			odb = ODBFactory.open(ODBTest.DIRECTORY + "swing");
			odb.store(new Function("swing"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ODB getOdb() {
		return odb;
	}
}

class Thread2 extends Thread {
	private ODB odb;

	public Thread2(ODB odb) {
		this.odb = odb;
	}

	public void run() {
		try {
			odb.store(new Function("f1"));

			Objects objects = odb.getObjects(Function.class);
			objects = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "swing")));
			odb.store(new Function("swing2"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ODB getOdb() {
		return odb;
	}
}