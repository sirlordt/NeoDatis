package org.neodatis.odb.test.acid;

import java.lang.reflect.Method;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestStopEngineWithoutCommit extends ODBTest {
	private boolean simpleObject;
	private ODBTest test = new ODBTest();

	public void test1() {
		// just to avoid junit warning
	}

	public void t1estA1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");

		odb.store(getInstance("f1"));
	}

	public void t1estA2() throws Exception {
		ODB odb = test.open("acid1");

		assertEquals(0, odb.getObjects(Function.class).size());
	}

	public void t1estB1() throws Exception {
		ODB odb = test.open("acid1");

		odb.store(getInstance("f1"));
		odb.commit();
	}

	public void t1estB2() throws Exception {
		ODB odb = test.open("acid1");
		int size = 0;
		if (simpleObject) {
			size = odb.getObjects(Function.class).size();
		} else {
			size = odb.getObjects(User.class).size();
		}

		if (size != 1) {
			throw new Exception("Size should be " + 1 + " and it is " + size);
		}
	}

	public void t1estC1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		int size = 1;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
		}
		for (int i = 0; i < size; i++) {
			odb.deleteObjectWithId(oids[i]);
		}

	}

	private Object getInstance(String string) {
		if (simpleObject) {
			return new Function(string);
		}

		Profile p = new Profile(string);
		p.addFunction(new Function("function " + string + "1"));
		p.addFunction(new Function("function " + string + "2"));
		User user = new User(string, "email" + string, p);
		return user;
	}

	public void t1estC2() throws Exception {
		ODB odb = test.open("acid1");
		assertEquals(0, odb.getObjects(Function.class).size());
	}

	public void t1estD1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		int size = 1000;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
		}
		for (int i = 0; i < size; i++) {
			odb.deleteObjectWithId(oids[i]);
		}
	}

	public void t1estD2() throws Exception {
		ODB odb = test.open("acid1");
		assertEquals(0, odb.getObjects(Function.class).size());
	}

	public void t1estE1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		int size = 1000;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
			if (simpleObject) {
				Function f = (Function) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
			} else {
				User f = (User) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
			}
			odb.deleteObjectWithId(oids[i]);
		}
	}

	public void t1estE2() throws Exception {
		ODB odb = test.open("acid1");
		assertEquals(0, odb.getObjects(Function.class).size());
	}

	public void t1estF1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		int size = 1000;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
			if (simpleObject) {
				Function f = (Function) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.store(f);
				odb.store(f);
				odb.store(f);
			} else {
				User f = (User) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.store(f);
				odb.store(f);
				odb.store(f);

			}
		}
		for (int i = 0; i < size; i++) {
			Object o = odb.getObjectFromId(oids[i]);
			odb.delete(o);
		}
	}

	public void t1estF2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(0, odb.getObjects(Function.class).size());
		} else {
			assertEquals(0, odb.getObjects(User.class).size());
		}
	}

	public void t1estG1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		int size = 1000;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
			if (simpleObject) {
				Function f = (Function) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.store(f);
				odb.store(f);
				odb.store(f);
			} else {
				User f = (User) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.store(f);
				odb.store(f);
				odb.store(f);

			}
		}
		odb.commit();
	}

	public void t1estG2() throws Exception {
		ODB odb = test.open("acid1");
		int size = 1000;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
			if (simpleObject) {
				Function f = (Function) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.store(f);
				odb.store(f);
				odb.store(f);
			} else {
				User f = (User) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.store(f);
				odb.store(f);
				odb.store(f);
			}
		}
		for (int i = 0; i < size; i++) {
			Object o = null;
			o = odb.getObjectFromId(oids[i]);
			odb.delete(o);
		}
	}

	public void t1estG3() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(1000, odb.getObjects(Function.class).size());
		} else {
			assertEquals(1000, odb.getObjects(User.class).size());
		}
	}

	public void t1estH1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		int size = 1000;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
			if (simpleObject) {
				Function f = (Function) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.delete(f);
				oids[i] = odb.store(f);
				odb.delete(f);
				oids[i] = odb.store(f);
				odb.delete(f);
				oids[i] = odb.store(f);
			} else {
				User f = (User) odb.getObjectFromId(oids[i]);
				f.setName("function " + i);
				odb.store(f);
				odb.delete(f);
				oids[i] = odb.store(f);
				odb.delete(f);
				oids[i] = odb.store(f);
				odb.delete(f);
				oids[i] = odb.store(f);
			}
		}
		for (int i = 0; i < size; i++) {
			Object o = odb.getObjectFromId(oids[i]);
			odb.delete(o);
		}
	}

	public void t1estH2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(0, odb.getObjects(Function.class).size());
		} else {
			assertEquals(0, odb.getObjects(User.class).size());
		}
	}

	public void t1estI1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		odb.store(getInstance("f1"));
		odb.store(getInstance("f2"));
		odb.store(getInstance("f3"));

		odb.close();
		odb = test.open("acid1");
		Object o = getInstance("f4");
		odb.store(o);
		odb.delete(o);
	}

	public void t1estI2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(3, odb.getObjects(Function.class).size());
		} else {
			assertEquals(3, odb.getObjects(User.class).size());
		}
	}

	public void t1estJ1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		odb.store(getInstance("f1"));
		odb.store(getInstance("f2"));
		odb.store(getInstance("f3"));

		odb.commit();

		Object o = getInstance("f4");
		odb.store(o);
		odb.delete(o);
	}

	public void t1estJ2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(3, odb.getObjects(Function.class).size());
		} else {
			assertEquals(3, odb.getObjects(User.class).size());
		}
	}

	public void t1estK1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		odb.store(getInstance("f1"));
		odb.store(getInstance("f2"));
		OID oid = odb.store(getInstance("f3"));

		odb.commit();

		Object o = odb.getObjectFromId(oid);
		odb.delete(o);
		odb.rollback();
	}

	public void t1estK2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(3, odb.getObjects(Function.class).size());
		} else {
			assertEquals(3, odb.getObjects(User.class).size());
		}
	}

	public void t1estL1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		odb.store(getInstance("f1"));
		odb.store(getInstance("f2"));
		OID oid = odb.store(getInstance("f3"));

		odb.commit();

		Object o = odb.getObjectFromId(oid);
		if (simpleObject) {
			Function f = (Function) o;
			f.setName("flksjdfjs;dfsljflsjflksjfksjfklsdjfksjfkalsjfklsdjflskd");
			odb.store(f);
		} else {
			User f = (User) o;
			f.setName("flksjdfjs;dfsljflsjflksjfksjfklsdjfksjfkalsjfklsdjflskd");
			odb.store(f);
		}
		odb.rollback();
	}

	public void t1estL2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(3, odb.getObjects(Function.class).size());
		} else {
			assertEquals(3, odb.getObjects(User.class).size());
		}
	}

	public void t1estM1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		int size = 1;
		OID[] oids = new OID[size];
		for (int i = 0; i < size; i++) {
			oids[i] = odb.store(getInstance("f" + i));
		}
		for (int i = 0; i < size; i++) {
			odb.deleteObjectWithId(oids[i]);
		}
		odb.rollback();
	}

	public void t1estM2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			assertEquals(0, odb.getObjects(Function.class).size());
		} else {
			assertEquals(0, odb.getObjects(User.class).size());
		}
	}

	public void t1estN1() throws Exception {
		test.deleteBase("acid1");
		ODB odb = test.open("acid1");
		for (int i = 0; i < 10; i++) {
			odb.store(getInstance("f" + i));
		}
		odb.close();

		odb = test.open("acid1");
		odb.store(getInstance("f1000"));
		odb.commit();
		Thread.sleep(1000);
	}

	public void t1estN2() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			Objects objects = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "f1000")));
			Function f = (Function) objects.getFirst();
			f.setName("new name");
			odb.store(f);
		} else {
			Objects objects = odb.getObjects(new CriteriaQuery(User.class, Where.equal("name", "f1000")));
			User f = (User) objects.getFirst();
			f.setName("new name");
			odb.store(f);
		}
		odb.commit();
	}

	public void t1estN3() throws Exception {
		ODB odb = test.open("acid1");
		if (simpleObject) {
			Objects objects = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "new name")));
			odb.delete(objects.getFirst());
		} else {
			Objects objects = odb.getObjects(new CriteriaQuery(User.class, Where.equal("name", "new name")));
			odb.delete(objects.getFirst());
		}
		odb.commit();
	}

	public void t1estN4() throws Exception {
		ODB odb = test.open("acid1");
		int nb = 0;
		if (simpleObject) {
			Objects objects = odb.getObjects(new CriteriaQuery(Function.class, Where.equal("name", "f1000")));
			nb = objects.size();
		} else {
			Objects objects = odb.getObjects(new CriteriaQuery(User.class, Where.equal("name", "f1000")));
			nb = objects.size();
		}
		if (nb != 0) {
			throw new Exception("Object f1000 still exist :-(");
		}
	}

	public void execute(String[] args) throws Exception {
		String step = args[0];
		simpleObject = args[1].equals("simple");

		Method method = this.getClass().getDeclaredMethod(step, new Class[0]);
		try {
			method.invoke(this, new Object[0]);
			testOk(step);
		} catch (Exception e) {
			// println("Error while calling " + step);
			testBad(step, e);
			// e.printStackTrace();
		}
	}

	private void testBad(String step, Exception e) {
		println(step + " Not ok " + e.getCause().getMessage());
		e.printStackTrace();

	}

	private void testOk(String step) {
		println(step + " Ok ");
	}

	public static void main(String[] args) throws Exception {
		TestStopEngineWithoutCommit tf = new TestStopEngineWithoutCommit();
		try {
			tf.execute(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
