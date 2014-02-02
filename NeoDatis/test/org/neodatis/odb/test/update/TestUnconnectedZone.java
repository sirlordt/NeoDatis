package org.neodatis.odb.test.update;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestUnconnectedZone extends ODBTest {

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		deleteBase("unconnected");
		ODB odb = open("unconnected");
		OID oid = odb.store(new Function("f1"));
		odb.close();

		println("Oid=" + oid);
		odb = open("unconnected");
		Function f2 = (Function) odb.getObjectFromId(oid);
		f2.setName("New Function");
		odb.store(f2);

		IStorageEngine storageEngine = Dummy.getEngine(odb);

		// retrieve the class info to check connected and unconnected zone
		ClassInfo ci = storageEngine.getSession(true).getMetaModel().getClassInfo(Function.class.getName(), true);

		odb.close();

		assertEquals(1, ci.getCommitedZoneInfo().getNbObjects());
		assertNotNull(ci.getCommitedZoneInfo().first);
		assertNotNull(ci.getCommitedZoneInfo().last);

		assertEquals(0, ci.getUncommittedZoneInfo().getNbObjects());
		assertNull(ci.getUncommittedZoneInfo().first);
		assertNull(ci.getUncommittedZoneInfo().last);

		odb = open("unconnected");
		assertEquals(1, odb.getObjects(Function.class).size());
		odb.close();

	}
}
