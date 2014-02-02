/**
 * 
 */
package org.neodatis.odb.test.oid;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestStoreWithOID extends ODBTest {

	public void test1() {
		ODB odb = open("withoid");

		OID oid = odb.store(new Function("f1"));
		odb.close();

		odb = open("withoid");
		Function f2 = new Function("f2");
		IStorageEngine engine = Dummy.getEngine(odb);
		engine.store(oid, f2);
		odb.close();

		odb = open("withoid");
		Function f = (Function) odb.getObjectFromId(oid);
		odb.close();

		assertEquals("f2", f.getName());
	}
}
