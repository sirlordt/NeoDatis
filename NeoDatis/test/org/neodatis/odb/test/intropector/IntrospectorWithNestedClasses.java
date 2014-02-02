package org.neodatis.odb.test.intropector;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.test.ODBTest;

public class IntrospectorWithNestedClasses extends ODBTest {

	public void test1() throws Exception {
		deleteBase("test-nested");
		ODB odb = open("test-nested");
		Class1 c1 = new Class1("name1", "name2", "name3");
		OID oid = odb.store(c1);
		odb.close();

		odb = open("test-nested");
		Class1 c11 = (Class1) odb.getObjectFromId(oid);
		assertEquals(c1.getName1(), c11.getName1());
		assertEquals(c1.getClass2().getClass3().getName3(), c11.getClass2().getClass3().getName3());

	}

}
