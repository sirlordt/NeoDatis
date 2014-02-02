package org.neodatis.odb.test.inheritance;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

public class TestInheritance extends ODBTest {
	private final static String NAME = "inheritance.neodatis";

	/**
	 * Test persistence of attributes declared by an interface
	 * 
	 * @throws Exception
	 */
	public void testInterface() throws Exception {
		deleteBase(NAME);
		Class1 class1 = new Class1("olivier");
		Class2 class2 = new Class2(10, class1);

		ODB odb = open(NAME);

		odb.store(class2);

		odb.close();

		odb = open(NAME);
		Class2 c2 = (Class2) odb.getObjects(Class2.class).getFirst();
		assertEquals(class2.getNb(), c2.getNb());
		assertEquals(class2.getInterface1().getName(), c2.getInterface1().getName());

		odb.close();
	}

	/**
	 * Test persistence of attributes declared by an interface
	 * 
	 * @throws Exception
	 */
	public void testSubClass() throws Exception {

		deleteBase(NAME);
		Class1 class1 = new SubClassOfClass1("olivier", 78);
		Class3 class3 = new Class3(10, class1);

		ODB odb = open(NAME);

		odb.store(class3);

		odb.close();

		odb = open(NAME);
		Class3 c3 = (Class3) odb.getObjects(Class3.class).getFirst();
		assertEquals(class3.getNb(), c3.getNb());
		assertEquals(class3.getClass1().getName(), c3.getClass1().getName());

		odb.close();
	}

}
