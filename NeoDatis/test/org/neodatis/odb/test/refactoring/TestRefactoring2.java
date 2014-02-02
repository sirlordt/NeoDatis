package org.neodatis.odb.test.refactoring;

import junit.framework.Assert;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;

public class TestRefactoring2 extends ByteCodeTest {
	public void start() throws Exception {
		resetDb();
	}

	public void step1() throws Exception {

		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test4";
		int nbFields = 20;
		String[] fieldNames = new String[nbFields];
		Class[] fieldTypes = new Class[nbFields];

		for (int i = 0; i < nbFields; i++) {
			fieldNames[i] = "field" + i;
			fieldTypes[i] = String.class;
		}
		Class c = jau.createClass(className, fieldNames, fieldTypes);

		ODB odb = null;
		try {
			odb = open();
			Objects objects = odb.getObjects(c);
			System.out.println("size=" + objects.size());
			Object o = c.newInstance();
			for (int i = 0; i < nbFields; i++) {
				setFieldValue(o, "field" + i, "step2:another string value of " + i);
			}
			odb.store(o);
			odb.close();
			closeServer();
			testOk("step1");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	public void step2() throws Exception {
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test4";
		int nbFields = 200;
		String[] fieldNames = new String[nbFields];
		Class[] fieldTypes = new Class[nbFields];

		for (int i = 0; i < nbFields; i++) {
			fieldNames[i] = "field" + i;
			fieldTypes[i] = String.class;
		}
		Class c = jau.createClass(className, fieldNames, fieldTypes);

		ODB odb = null;
		try {
			odb = open();
			Objects objects = odb.getObjects(c);
			System.out.println("size=" + objects.size());
			Object o = c.newInstance();
			for (int i = 0; i < nbFields; i++) {
				setFieldValue(o, "field" + i, "step2:another string value of " + i);
			}
			odb.store(o);
			odb.close();
			closeServer();
			testOk("step2");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	public void step3() throws Exception {
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test4";
		int nbFields = 200;
		String[] fieldNames = new String[nbFields];
		Class[] fieldTypes = new Class[nbFields];

		for (int i = 0; i < nbFields; i++) {
			fieldNames[i] = "field" + i;
			fieldTypes[i] = String.class;
		}
		Class c = jau.createClass(className, fieldNames, fieldTypes);

		ODB odb = null;
		try {
			odb = open();
			Objects objects = odb.getObjects(c);
			Assert.assertEquals(2, objects.size());
			System.out.println("size=" + objects.size());
			for (int j = 0; j < 100; j++) {
				Object o = c.newInstance();
				for (int i = 0; i < nbFields; i++) {
					setFieldValue(o, "field" + i, "step2:another string value of " + i);
				}
				odb.store(o);
			}
			odb.close();
			closeServer();
			testOk("step3");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	public void step4() throws Exception {
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test4";
		int nbFields = 200;
		String[] fieldNames = new String[nbFields];
		Class[] fieldTypes = new Class[nbFields];

		for (int i = 0; i < nbFields; i++) {
			fieldNames[i] = "field" + i;
			fieldTypes[i] = String.class;
		}
		Class c = jau.createClass(className, fieldNames, fieldTypes);

		ODB odb = null;
		try {
			odb = open();
			Objects objects = odb.getObjects(c);
			Assert.assertEquals(102, objects.size());
			odb.close();
			closeServer();
			testOk("step4");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	public void step5() throws Exception {
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test4";
		int nbFields = 200;
		String[] fieldNames = new String[nbFields];
		Class[] fieldTypes = new Class[nbFields];

		for (int i = 0; i < nbFields; i++) {
			fieldNames[i] = "field" + i;
			fieldTypes[i] = String.class;
		}
		Class c = jau.createClass(className, fieldNames, fieldTypes);

		ODB odb = null;
		try {
			odb = open();
			Objects objects = odb.getObjects(c);
			Assert.assertEquals(102, objects.size());
			System.out.println("size=" + objects.size());
			while (objects.hasNext()) {
				Object o = objects.next();
				for (int i = 0; i < nbFields; i++) {
					setFieldValue(o, "field" + i, "step2:another string" + i);
				}
				odb.store(o);
			}
			odb.close();
			closeServer();
			testOk("step5");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	public void step6() throws Exception {
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test4";
		int nbFields = 200;
		String[] fieldNames = new String[nbFields];
		Class[] fieldTypes = new Class[nbFields];

		for (int i = 0; i < nbFields; i++) {
			fieldNames[i] = "field" + i;
			fieldTypes[i] = String.class;
		}
		Class c = jau.createClass(className, fieldNames, fieldTypes);

		ODB odb = null;
		try {
			odb = open();
			Objects objects = odb.getObjects(c);
			Assert.assertEquals(102, objects.size());
			System.out.println("size=" + objects.size());
			while (objects.hasNext()) {
				Object o = objects.next();
				for (int i = 0; i < nbFields; i++) {
					Assert.assertEquals("step2:another string" + i, getFieldValue(o, "field" + i));
				}
			}
			odb.close();
			closeServer();
			testOk("step6");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	public static void main(String[] args) throws Exception {
		TestRefactoring2 tf = new TestRefactoring2();
		tf.execute(args);
	}

}
