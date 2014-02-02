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
package org.neodatis.odb.test.performance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.wrappers.OdbTime;

public class TestFilePersisterHighPerformance2 extends ODBTest {
	public static int TEST_SIZE = isLocal ? 1000 : 200;

	// public static final String ODB_FILE_NAME = "k:/tmp/perf.neodatis";

	public static final String DB4O_FILE_NAME = "perf.yap";

	public void testInsertUserODB() throws Exception {
		String baseName = getBaseName();
		long t1, t2, t3, t4, t5, t6, t7, t77, t8;
		t1 = OdbTime.getCurrentTimeInMs();
		ODB odb = open(baseName);
		for (int i = 0; i < TEST_SIZE; i++) {
			Object o = getUserInstance();
			odb.store(o);
			if (i % 1000 == 0) {
				System.out.print(".");
			}
		}
		t2 = OdbTime.getCurrentTimeInMs();
		// assertEquals(TEST_SIZE,
		// odb.getSession().getCache().getNumberOfObjects ());
		IStorageEngine engine = Dummy.getEngine(odb);
		if (isLocal) {
			println("NB WAs=" + engine.getSession(true).getTransaction().getNumberOfWriteActions());
		}
		odb.commit();
		t3 = OdbTime.getCurrentTimeInMs();
		println("end of insert");
		Objects l = odb.getObjects(User.class, false);

		t4 = OdbTime.getCurrentTimeInMs();

		int nbObjects = l.size();
		println(nbObjects + " objects ");

		User user = null;
		while (l.hasNext()) {
			// println(i);
			user = (User) l.next();
		}
		// assertEquals(TEST_SIZE,
		// odb.getSession().getCache().getNumberOfObjects ());
		println("end of real get objects");
		t5 = OdbTime.getCurrentTimeInMs();
		user = null;
		int j = 0;
		l.reset();
		while (l.hasNext()) {
			// println(i);
			user = (User) l.next();
			user.setName(user.getName() + " updated" + j);
			odb.store(user);
			j++;
		}

		t6 = OdbTime.getCurrentTimeInMs();
		println("end of update");
		odb.close();

		t7 = OdbTime.getCurrentTimeInMs();

		odb = open(baseName);
		l = odb.getObjects(User.class);
		t77 = OdbTime.getCurrentTimeInMs();
		j = 0;
		while (l.hasNext()) {

			user = (User) l.next();
			println(j + " " + user.getName());
			assertTrue(user.getName().endsWith("updated" + j));
			odb.delete(user);
			j++;
		}

		odb.close();

		t8 = OdbTime.getCurrentTimeInMs();

		odb = open(baseName);
		assertEquals(0, odb.getObjects(User.class).size());
		odb.close();

		displayResult("ODB " + TEST_SIZE + " User objects ", t1, t2, t3, t4, t5, t6, t7, t77, t8);
		// println("calls="+ObjectReader.calls + " / time="
		// +ObjectReader.timeToGetObjectFromId );

	}

	public void testInsertSimpleObjectODB() throws Exception {
		String baseName = getBaseName();
		long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0, t6 = 0, t7 = 0, t77 = 0, t8 = 0;
		ODB odb = null;
		Objects l = null;
		SimpleObject so = null;
		t1 = OdbTime.getCurrentTimeInMs();
		odb = open(baseName);
		for (int i = 0; i < TEST_SIZE; i++) {
			Object o = getSimpleObjectInstance(i);
			odb.store(o);
			if (i % 20000 == 0) {
				System.out.print(".");
				println("After insert=" + Dummy.getEngine(odb).getSession(true).getCache().toString());
			}

		}
		//

		IStorageEngine engine = Dummy.getEngine(odb);

		if (isLocal) {
			// println("NB WA="+WriteAction.count);
			println("NB WAs=" + engine.getSession(true).getTransaction().getNumberOfWriteActions());
		}

		t2 = OdbTime.getCurrentTimeInMs();
		odb.commit();
		if (isLocal) {
			println("after commit : NB WAs=" + engine.getSession(true).getTransaction().getNumberOfWriteActions());
		}

		// if(true)return;
		// println("After commit="+Dummy.getEngine(odb).getSession().getCache().toString());
		// println("NB WA="+WriteAction.count);

		t3 = OdbTime.getCurrentTimeInMs();
		// println("end of insert");
		l = odb.getObjects(SimpleObject.class, false);
		// println("end of getObjects ");
		t4 = OdbTime.getCurrentTimeInMs();

		// println("After getObjects ="+Dummy.getEngine(odb).getSession().getCache().toString());
		// println("NB WA="+WriteAction.count);
		if (isLocal) {
			println("after select : NB WAs=" + engine.getSession(true).getTransaction().getNumberOfWriteActions());
		}

		int nbObjects = l.size();
		println(nbObjects + " objects ");

		int k = 0;
		while (l.hasNext()) {
			Object o = l.next();
			if (k % 9999 == 0) {
				println(((SimpleObject) o).getName());
			}
			k++;
		}

		// println("end of real get ");
		t5 = OdbTime.getCurrentTimeInMs();
		println("select " + (t5 - t3) + " - " + (t5 - t4));

		so = null;
		k = 0;
		l.reset();
		while (l.hasNext()) {
			so = (SimpleObject) l.next();
			so.setName(so.getName() + " updated");
			odb.store(so);
			if (k % 10000 == 0) {
				println("update " + k);
				if (isLocal) {
					println("after update : NB WAs=" + engine.getSession(true).getTransaction().getNumberOfWriteActions());
					println("After update=" + Dummy.getEngine(odb).getSession(true).getCache().toString());
				}
			}
			/*
			 * if(i%100==0){
			 * //println(i+"="+odb.getSession().getCache().toString());
			 * //println("NB WA="+WriteAction.count); }
			 */
			k++;
		}

		if (isLocal) {
			println("after update : NB WAs=" + engine.getSession(true).getTransaction().getNumberOfWriteActions());
		}
		t6 = OdbTime.getCurrentTimeInMs();

		odb.close();

		t7 = OdbTime.getCurrentTimeInMs();

		odb = open(baseName);
		l = odb.getObjects(SimpleObject.class, false);
		t77 = OdbTime.getCurrentTimeInMs();
		int j = 0;
		while (l.hasNext()) {
			so = (SimpleObject) l.next();
			assertTrue(so.getName().endsWith("updated"));
			odb.delete(so);
			if (j % 10000 == 0) {
				println("delete " + j);
			}
			j++;
		}

		odb.close();

		t8 = OdbTime.getCurrentTimeInMs();

		odb = open(baseName);
		assertEquals(0, odb.getObjects(SimpleObject.class).size());
		odb.close();

		displayResult("ODB " + TEST_SIZE + " SimpleObject objects ", t1, t2, t3, t4, t5, t6, t7, t77, t8);
	}

	private void displayResult(String string, long t1, long t2, long t3, long t4, long t5, long t6, long t7, long t77, long t8) {

		String s1 = " total=" + (t8 - t1);
		String s2 = " total insert=" + (t3 - t1) + " -- " + "insert=" + (t2 - t1) + " commit=" + (t3 - t2);
		String s3 = " total select=" + (t5 - t3) + " -- " + "select=" + (t4 - t3) + " get=" + (t5 - t4);
		String s4 = " total update=" + (t7 - t5) + " -- " + "update=" + (t6 - t5) + " commit=" + (t7 - t6);
		String s5 = " total delete=" + (t8 - t7) + " -- " + "select=" + (t77 - t7) + " - delete=" + (t8 - t77);

		println(string + s1 + " | " + s2 + " | " + s3 + " | " + s4 + " | " + s5);

	}

	private Object getUserInstance() {
		Function login = new Function("login");
		Function logout = new Function("logout");
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator", list);
		User user = new User("olivier smadja", "olivier@neodatis.com", profile);
		return user;
	}

	private TestClass getTestClassInstance() {
		TestClass tc = new TestClass();
		tc.setBigDecimal1(new BigDecimal("1.123456789"));
		tc.setBoolean1(true);
		tc.setChar1('d');
		tc.setDouble1(new Double(154.78998989));
		tc.setInt1(78964);
		tc.setString1("Ola chico como vc está ???");
		tc.setDate1(new Date());
		return tc;
	}

	private SimpleObject getSimpleObjectInstance(int i) {
		SimpleObject so = new SimpleObject();
		so.setDate(new Date());
		so.setDuration(i);
		so.setName("ola chico, como vc esta?" + i);
		return so;
	}

	public static void main(String[] args) throws Exception {

		new TestFilePersisterHighPerformance2().testInsertSimpleObjectODB();

		// new TestFilePersisterHighPerformance2().testInsertUserODB();

		// new TestFilePersisterHighPerformance2().testDeleteSimpleObjectODB();

	}
}