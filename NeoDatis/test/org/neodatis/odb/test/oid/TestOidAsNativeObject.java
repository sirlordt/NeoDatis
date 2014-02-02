package org.neodatis.odb.test.oid;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.test.ODBTest;

public class TestOidAsNativeObject extends ODBTest {

	public void test1() throws Exception {
		ClassWithOid cwo = new ClassWithOid("test", OIDFactory.buildObjectOID(47));
		deleteBase("native-oid");
		ODB odb = open("native-oid");
		odb.store(cwo);
		odb.close();

		odb = open("native-oid");
		Objects objects = odb.getObjects(ClassWithOid.class);
		assertEquals(1, objects.size());
		ClassWithOid cwo2 = (ClassWithOid) objects.getFirst();
		assertEquals(47, cwo2.getOid().getObjectId());

	}

}
