package org.neodatis.odb.test.transient_attributes;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

public class TestTransient extends ODBTest {
	public void test1() {
		String baseName = getBaseName();

		ODB odb = open(baseName);

		VoWithTransientAttribute vo = new VoWithTransientAttribute("vo1");
		odb.store(vo);

		odb.close();

		odb = open(baseName);
		Objects<VoWithTransientAttribute> vos = odb.getObjects(VoWithTransientAttribute.class);

		odb.close();

		println(vos.getFirst().getName());
		assertEquals(1, vos.size());
		assertEquals("vo1", vos.getFirst().getName());

	}

}
