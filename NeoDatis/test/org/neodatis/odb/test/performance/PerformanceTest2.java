package org.neodatis.odb.test.performance;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.instance.FullInstantiationHelper;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.IOUtil;
import org.neodatis.tool.wrappers.OdbTime;

public class PerformanceTest2 {
	public static int TEST_SIZE = 10000;

	public static final String ODB_FILE_NAME = "perf.neodatis";

	public void testInsertSimpleObjectODB() throws Exception {

		boolean inMemory = true;
		// Deletes the database file
		IOUtil.deleteFile(ODB_FILE_NAME);
		long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0, t6 = 0, t7 = 0, t77 = 0, t8 = 0;
		ODB odb = null;
		Objects l = null;
		User so = null;

		// Insert TEST_SIZE objects
		System.out.println("Inserting " + TEST_SIZE + " objects");

		t1 = OdbTime.getCurrentTimeInMs();
		odb = ODBFactory.open(ODB_FILE_NAME);

		odb.getClassRepresentation(User.class).addFullInstantiationHelper(new UserFullInstantiationHelper());

		for (int i = 0; i < TEST_SIZE; i++) {
			Object o = getUserInstance(i);
			odb.store(o);
		}
		t2 = OdbTime.getCurrentTimeInMs();
		// Closes the database
		odb.close();

		t3 = OdbTime.getCurrentTimeInMs();

		System.out.println("Retrieving " + TEST_SIZE + " objects");
		// Reopen the database
		odb = ODBFactory.open(ODB_FILE_NAME);

		// Gets retrieve the TEST_SIZE objects
		l = odb.getObjects(User.class, inMemory);

		t4 = OdbTime.getCurrentTimeInMs();

		// Actually get objects
		while (l.hasNext()) {
			Object o = l.next();
		}
		t5 = OdbTime.getCurrentTimeInMs();
		System.out.println("Updating " + TEST_SIZE + " objects");

		so = null;
		l.reset();
		// Actually get objects
		while (l.hasNext()) {
			so = (User) l.next();
			// so.setName(so.getName() + " updated");
			// so.setName(so.getName() + " updated-updated-updated-updated");
			so.getProfile().setName(so.getName() + " updated-updated-updated");
			odb.store(so);
		}

		t6 = OdbTime.getCurrentTimeInMs();

		odb.close();

		t7 = OdbTime.getCurrentTimeInMs();

		System.out.println("Deleting " + TEST_SIZE + " objects");
		odb = ODBFactory.open(ODB_FILE_NAME);
		l = odb.getObjects(User.class, inMemory);
		t77 = OdbTime.getCurrentTimeInMs();

		// Actually get objects
		while (l.hasNext()) {
			so = (User) l.next();
			odb.delete(so);
		}

		odb.close();

		t8 = OdbTime.getCurrentTimeInMs();

		odb = ODBFactory.open(ODB_FILE_NAME);
		odb.close();

		displayResult("ODB " + TEST_SIZE + " User objects ", t1, t2, t3, t4, t5, t6, t7, t77, t8);
	}

	private Object getUserInstance(int i) {
		Function login = new Function("login" + i);
		Function logout = new Function("logout" + i);
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator" + i, list);
		User user = new User("olivier smadja" + i, "olivier@neodatis.com", profile);
		return user;
	}

	private void displayResult(String string, long t1, long t2, long t3, long t4, long t5, long t6, long t7, long t77, long t8) {

		String s1 = " total=" + (t8 - t1);
		String s2 = " total insert=" + (t3 - t1) + " -- " + "insert=" + (t2 - t1) + " commit=" + (t3 - t2);
		String s3 = " total select=" + (t5 - t3) + " -- " + "select=" + (t4 - t3) + " get=" + (t5 - t4);
		String s4 = " total update=" + (t7 - t5) + " -- " + "update=" + (t6 - t5) + " commit=" + (t7 - t6);
		String s5 = " total delete=" + (t8 - t7) + " -- " + "select=" + (t77 - t7) + " - delete=" + (t8 - t77);

		System.out.println(string + s1 + " | " + s2 + " | " + s3 + " | " + s4 + " | " + s5);

	}

	public static void main(String[] args) throws Exception {
		PerformanceTest2 pt = new PerformanceTest2();
		pt.testInsertSimpleObjectODB();
	}

}

class UserFullInstantiationHelper implements FullInstantiationHelper {

	public Object instantiate(NonNativeObjectInfo nnoi) {
		User user = new User();
		/*
		 * so.setDate((Date) nnoi.getValueOf("date")); so.setDuration(((Integer)
		 * nnoi.getValueOf("duration")).intValue()); so.setName((String)
		 * nnoi.getValueOf("name"));
		 */
		return user;
	}

}
