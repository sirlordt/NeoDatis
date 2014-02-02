package org.neodatis.odb.test.other;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.test.ODBTest;

/**
 * Reported bug by Moises > on 1.5.6
 * 
 * @author osmadja
 * 
 */
public class TestObjectWithOid extends ODBTest {

	public void test1() throws Exception {
		deleteBase("test-object-with-oid");
		ODB odb = open("test-object-with-oid");
		ObjectWithOid o = new ObjectWithOid("15", "test");
		OID oid = odb.store(o);
		odb.close();

		odb = open("test-object-with-oid");
		ObjectWithOid o2 = (ObjectWithOid) odb.getObjectFromId(oid);
		odb.close();
		assertEquals(o.getOid(), o2.getOid());
		assertEquals(o.getName(), o2.getName());
	}
}
