package org.neodatis.odb.test.fromusers.LuisSFSoeiro;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

public class TestTransient extends ODBTest {

	public void test1() {
		String baseName = getBaseName();
		ODB odb = ODBFactory.open(baseName);

		ClassA a = new ClassA();
		// This prints 1- a.getBool()=true
		println("1- a.getBool()=" + a.getTransientBool());

		odb.store(a);
		odb.close();

		odb = ODBFactory.open(baseName);

		Objects objects = odb.getObjects(ClassA.class);
		odb.close();

		ClassA x = null;
		while (objects.hasNext()) {
			x = (ClassA) objects.next();
			assertEquals(Boolean.TRUE, x.getTransientBool());
			assertEquals("transient", x.getTransientString());
		}
	}
}
