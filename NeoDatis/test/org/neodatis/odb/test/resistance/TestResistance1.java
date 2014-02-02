package org.neodatis.odb.test.resistance;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.CIZoneInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.tool.StopWatch;
import org.neodatis.tool.wrappers.OdbTime;

public class TestResistance1 extends ODBTest {
	private static final String FILE_NAME = "resistance1";

	/**
	 * 1) insert 10000 objects 2) update 5000 * 10 times 3) delete other 5000
	 * 
	 * 4) check count : must be 5000 5) re-update 5000 * 10 times 6) delete the
	 * other 5000 7) check count - must be zero
	 * 
	 * @throws Exception
	 */
	public void test1WithCommit() throws Exception {

		if (!runAll) {
			return;
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		int size = 10000;
		int size2 = 5000;
		int nbFunctions = 2;

		deleteBase(FILE_NAME);

		ODB odb = open(FILE_NAME);
		Function f1 = new Function("function 1");

		// Create Objects
		for (int i = 0; i < size; i++) {
			Profile p = new Profile("profile number " + i, f1);
			for (int j = 0; j < nbFunctions; j++) {
				p.addFunction(new Function(" inner function of profile : number " + i + " - " + j));
			}
			User user = new User("user name " + i, "user email " + i, p);
			odb.store(user);
			if (i % 100 == 0) {
				// println("insert " + i);
			}
		}
		odb.close();
		println("created");
		// Updates 10 times the objects
		odb = open(FILE_NAME);
		Objects objects = odb.getObjects(User.class);
		println("got the object " + objects.size());
		for (int k = 0; k < 10; k++) {
			objects.reset();
			long start = OdbTime.getCurrentTimeInMs();
			for (int i = 0; i < size2; i++) {
				User user = (User) objects.next();
				user.getProfile().setName(user.getProfile().getName() + "-updated");
				odb.store(user);
				if (i % 100 == 0) {
					// println("update " + i + " - " + k);
				}

			}
			println("Update " + k + " - " + (OdbTime.getCurrentTimeInMs() - start) + " ms");
		}
		println("updated");
		// Delete the rest of the objects
		for (int i = size2; i < size; i++) {
			odb.delete(objects.next());
			if (i % 100 == 0) {
				println("delete " + i);
			}

		}
		println("deleted");
		odb.close();
		// Check object count
		odb = open(FILE_NAME);
		objects = odb.getObjects(User.class);
		assertEquals(size2, objects.size());

		// Check data of the objects
		int a = 0;
		while (objects.hasNext()) {
			User user = (User) objects.next();
			assertEquals("user name " + a, user.getName());
			assertEquals("user email " + a, user.getEmail());
			assertEquals("profile number " + a + "-updated-updated-updated-updated-updated-updated-updated-updated-updated-updated", user
					.getProfile().getName());
			a++;
		}
		println("checked");

		for (int k = 0; k < 10; k++) {
			objects.reset();
			for (int i = 0; i < size2; i++) {
				User user = (User) objects.next();
				user.getProfile().setName(user.getProfile().getName() + "-updated" + "-");
				odb.store(user);
			}
		}
		println("re-updated");
		odb.close();

		// delete objects
		odb = open(FILE_NAME);
		objects = odb.getObjects(User.class);
		a = 0;
		while (objects.hasNext()) {
			odb.delete(objects.next());
			a++;
		}
		assertEquals(size2, a);
		odb.close();
		odb = open(FILE_NAME);
		assertEquals(0, odb.getObjects(User.class).size());
		assertEquals(0, odb.count(new CriteriaQuery(User.class)).longValue());
		println("deleted");
		odb.close();
		stopWatch.end();
		println("Total time 1 = " + stopWatch.getDurationInMiliseconds());
		if (stopWatch.getDurationInMiliseconds() > 90700) {
			fail("time is > than " + 90700 + " = " + stopWatch.getDurationInMiliseconds());
		}
	}

	/**
	 * 1) insert 10000 objects 2) update 5000 * 10 times 3) delete other 5000
	 * 
	 * 4) check count : must be 5000 5) re-update 5000 * 10 times 6) delete the
	 * other 5000 7) check count - must be zero
	 * 
	 * @throws Exception
	 */
	public void test1WithoutCommit() throws Exception {

		if (!runAll) {
			return;
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		int size = 10000;
		int size2 = 5000;
		int nbFunctions = 10;

		deleteBase(FILE_NAME);

		ODB odb = open(FILE_NAME);
		Function f1 = new Function("function 1");

		// Create Objects
		for (int i = 0; i < size; i++) {
			Profile p = new Profile("profile number " + i, f1);
			for (int j = 0; j < nbFunctions; j++) {
				p.addFunction(new Function(" inner function of profile : number " + i + " - " + j));
			}
			User user = new User("user name " + i, "user email " + i, p);
			odb.store(user);
		}

		println("created");
		// Updates 10 times the objects
		Objects objects = odb.getObjects(User.class);
		for (int k = 0; k < 10; k++) {
			objects.reset();
			for (int i = 0; i < size2; i++) {
				User user = (User) objects.next();
				user.getProfile().setName(user.getProfile().getName() + "-updated");
				odb.store(user);
			}
		}
		println("updated");
		// Delete the rest of the objects
		for (int i = size2; i < size; i++) {
			odb.delete(objects.next());
		}
		println("deleted");

		// Check object count
		objects = odb.getObjects(User.class);
		assertEquals(size2, objects.size());

		// Check data of the objects
		int a = 0;
		while (objects.hasNext()) {
			User user = (User) objects.next();
			assertEquals("user name " + a, user.getName());
			assertEquals("user email " + a, user.getEmail());
			assertEquals("profile number " + a + "-updated-updated-updated-updated-updated-updated-updated-updated-updated-updated", user
					.getProfile().getName());
			a++;
		}
		println("checked");

		for (int k = 0; k < 10; k++) {
			objects.reset();
			for (int i = 0; i < size2; i++) {
				User user = (User) objects.next();
				user.getProfile().setName(user.getProfile().getName() + "-updated" + "-");
				odb.store(user);
			}
		}
		println("re-updated");

		objects = odb.getObjects(User.class);

		IStorageEngine engine = Dummy.getEngine(odb);

		CIZoneInfo uncommited = engine.getSession(true).getMetaModel().getClassInfo(User.class.getName(), true).getUncommittedZoneInfo();
		CIZoneInfo commited = engine.getSession(true).getMetaModel().getClassInfo(User.class.getName(), true).getCommitedZoneInfo();
		println("Before commit : uncommited=" + uncommited);
		println("Before commit : commited=" + commited);

		a = 0;
		while (objects.hasNext()) {
			// println("a="+a);
			odb.delete(objects.next());
			a++;
		}
		assertEquals(size2, a);

		assertEquals(0, odb.getObjects(User.class).size());
		assertEquals(0, odb.count(new CriteriaQuery(User.class)).longValue());
		println("deleted");
		odb.close();

		stopWatch.end();

		println("Total time 2 = " + stopWatch.getDurationInMiliseconds());
		if (stopWatch.getDurationInMiliseconds() > 108438) {
			fail("time is > than " + 108438 + " = " + stopWatch.getDurationInMiliseconds());
		}

	}

	public void test1WithCommit2() throws Exception {

		if (!runAll) {
			return;
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		int size = 2;
		int size2 = 1;
		int nbFunctions = 1;

		deleteBase(FILE_NAME);

		ODB odb = open(FILE_NAME);
		Function f1 = new Function("function 1");

		println(odb.count(new CriteriaQuery(User.class)));
		// Create Objects
		for (int i = 0; i < size; i++) {
			Profile p = new Profile("profile number " + i, f1);
			for (int j = 0; j < nbFunctions; j++) {
				p.addFunction(new Function(" inner function of profile : number " + i + " - " + j));
			}
			User user = new User("user name " + i, "user email " + i, p);
			odb.store(user);
			if (i % 100 == 0) {
				println("insert " + i);
			}
		}
		odb.close();
		println("created");
		// Updates 10 times the objects
		odb = open(FILE_NAME);
		Objects objects = odb.getObjects(User.class);
		println("got the object " + objects.size());
		for (int k = 0; k < 3; k++) {
			objects.reset();
			long start = OdbTime.getCurrentTimeInMs();
			for (int i = 0; i < size; i++) {
				User user = (User) objects.next();
				user.getProfile().setName(user.getProfile().getName() + "-updated");
				odb.store(user);
				if (i % 100 == 0) {
					println("update " + i + " - " + k);
				}

			}
			println("Update " + k + " - " + (OdbTime.getCurrentTimeInMs() - start) + " ms");
		}
		println("updated");

		println("deleted");
		odb.close();
		// Check object count
		odb = open(FILE_NAME);
		objects = odb.getObjects(User.class);
		assertEquals(objects.size(), size);

		// Check data of the objects
		int a = 0;
		while (objects.hasNext()) {
			User user = (User) objects.next();
			assertEquals("user name " + a, user.getName());
			assertEquals("user email " + a, user.getEmail());
			assertEquals("profile number " + a + "-updated-updated-updated", user.getProfile().getName());
			a++;
		}
		println("checked");

		for (int k = 0; k < 10; k++) {
			objects.reset();
			for (int i = 0; i < size2; i++) {
				User user = (User) objects.next();
				user.getProfile().setName(user.getProfile().getName() + "-updated" + "-");
				odb.store(user);
			}
		}
		println("re-updated");
		odb.close();

		// delete objects
		odb = open(FILE_NAME);
		objects = odb.getObjects(User.class);
		a = 0;
		while (objects.hasNext()) {
			odb.delete(objects.next());
			a++;
		}
		assertEquals(size, a);
		odb.close();
		odb = open(FILE_NAME);
		assertEquals(0, odb.getObjects(User.class).size());
		assertEquals(0, odb.count(new CriteriaQuery(User.class)).longValue());
		println("deleted");
		odb.close();
		stopWatch.end();
		println("Total time 1 = " + stopWatch.getDurationInMiliseconds());
		if (stopWatch.getDurationInMiliseconds() > 90700) {
			fail("time is > than " + 90700 + " = " + stopWatch.getDurationInMiliseconds());
		}
	}

	public void test1WithCommit3() throws Exception {

		if (!runAll) {
			return;
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);
		Function f1 = new Function("function 1");
		println(odb.count(new CriteriaQuery(User.class)));
		Profile p = new Profile("profile number 0", f1);
		p.addFunction(new Function("f1"));
		User user = new User("user name 0", "user email 0", p);
		odb.store(user);

		Profile p2 = new Profile("profile number 0", f1);
		p2.addFunction(new Function("f2"));
		User user2 = new User("user name 0", "user email 0", p2);
		odb.store(user2);

		odb.close();

		odb = open(FILE_NAME);
		Objects objects = null;

		for (int k = 0; k < 2; k++) {
			System.out.println(":" + k);
			objects = odb.getObjects(User.class);
			while (objects.hasNext()) {
				user = (User) objects.next();
				user.getProfile().setName(user.getProfile().getName() + "-updated");
				println(user.getProfile().getName());
				odb.store(user);
			}
		}
		odb.close();

		odb = open(FILE_NAME);
		objects = odb.getObjects(User.class);
		assertEquals(2, objects.size());
		odb.close();
	}

	public void test1WithCommit4() throws Exception {

		if (!runAll) {
			return;
		}
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		deleteBase(FILE_NAME);
		ODB odb = open(FILE_NAME);

		Function f1 = new Function("function1");
		odb.store(f1);
		Function f2 = new Function("function2");
		odb.store(f2);

		odb.close();

		odb = open(FILE_NAME);
		Objects objects = odb.getObjects(Function.class);
		Function f = null;
		println("got the object " + objects.size());
		for (int k = 0; k < 2; k++) {
			objects.reset();
			while (objects.hasNext()) {
				f = (Function) objects.next();
				f.setName(f.getName() + "updated-");
				odb.store(f);
			}
		}
		odb.close();

		odb = open(FILE_NAME);
		objects = odb.getObjects(Function.class);
		odb.close();
	}
}
