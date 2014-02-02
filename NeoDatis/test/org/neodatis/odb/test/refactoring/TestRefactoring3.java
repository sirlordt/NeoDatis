package org.neodatis.odb.test.refactoring;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;

public class TestRefactoring3 extends ByteCodeTest {
	public void start() throws Exception {
		resetDb();
	}

	public void step1() throws Exception {
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test5";
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
			for (int j = 0; j < 100; j++) {
				Object o = c.newInstance();
				for (int i = 0; i < nbFields; i++) {
					setFieldValue(o, "field" + i, "step17:another string value of " + i);
				}
				odb.store(o);
			}
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
		String className = "Test5";
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
			odb.close();
			closeServer();
			assertEquals(100, objects.size());
			testOk("step2");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}
	
	/** Test update meta model with attribute of non native type test3 & test4
	 * 
	 * @throws Exception
	 */
	public void step3() throws Exception {

		System.out.println("Test update meta model with attribute of non native type");
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "ClassA";
		String[] fieldNames = new String[1];
		Class[] fieldTypes = new Class[1];

		fieldNames[0] = "field0";
		fieldTypes[0] = String.class;

		Class classA = jau.createClass(className, fieldNames, fieldTypes);
		

		String className2 = "ClassC";
		String[] fieldNames2 = new String[1];
		Class[] fieldTypes2 = new Class[1];

		fieldNames2[0] = "field0";
		fieldTypes2[0] = String.class;

		Class classC = jau.createClass(className2, fieldNames2, fieldTypes2);


		ODB odb = null;
		try {
			odb = open();
			odb.store(classA.newInstance());
			odb.close();
			closeServer();
			testOk("step3");
			System.exit(0);
		} catch (Exception e) {
			testBad("test3", e);
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	/** Test update meta model with attribute of non native type test3 & test4
	 * 
	 * @throws Exception
	 */
	public void step4() throws Exception {

		System.out.println("Test update meta model with attribute of non native type");
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "com.ClassB";
		String[] fieldNames = new String[1];
		Class[] fieldTypes = new Class[1];

		fieldNames[0] = "field0";
		fieldTypes[0] = String.class;

		Class classB = jau.createClass(className, fieldNames, fieldTypes);
		
		// recreate class A adding an attribute of type ClassB
		String className2 = "ClassA";
		String[] fieldNames2 = new String[2];
		Class[] fieldTypes2 = new Class[2];

		fieldNames2[0] = "field0";
		fieldTypes2[0] = String.class;
		fieldNames2[1] = "fieldClassB";
		fieldTypes2[1] = classB;

		Class classA = jau.createClass(className2, fieldNames2, fieldTypes2);


		ODB odb = null;
		try {
			odb = open();
			odb.store(classA.newInstance());
			odb.getObjects(classA);
			odb.close();
			closeServer();
			testOk("step3");
			System.exit(0);
		} catch (Exception e) {
			testBad("test3", e);
			e.printStackTrace();
			odb.close();
			closeServer();
			System.exit(1);
		}
	}

	public static void main(String[] args) throws Exception {
		TestRefactoring3 tf = new TestRefactoring3();
		tf.execute(args);
	}

}
