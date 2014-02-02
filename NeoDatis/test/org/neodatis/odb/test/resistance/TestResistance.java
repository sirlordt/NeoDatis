package org.neodatis.odb.test.resistance;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestResistance extends ODBTest {
	public void test8() throws Exception {
		int size1 = 1000;
		int size2 = 1000;
		if (!runAll) {
			return;
		}
		String baseName = getBaseName();
		ODB odb = null;
		Objects os = null;

		for (int i = 0; i < size1; i++) {
			odb = open(baseName);
			for (int j = 0; j < size2; j++) {
				Function f = new Function("function " + j);
				odb.store(f);
			}
			odb.close();
			odb = open(baseName);
			os = odb.getObjects(Function.class);
			while (os.hasNext()) {
				Function f = (Function) os.next();
				odb.delete(f);
			}
			odb.close();
			if (i % 100 == 0) {
				println(i + "/" + size1);
			}
		}
		odb = open(baseName);
		os = odb.getObjects(Function.class);
		assertEquals(0, os.size());
		odb.close();
		println("step2");
		for (int i = 0; i < size1; i++) {
			odb = open(baseName);
			os = odb.getObjects(Function.class);
			while (os.hasNext()) {
				Function f = (Function) os.next();
				odb.delete(f);
			}
			odb.close();
			odb = open(baseName);
			for (int j = 0; j < size2; j++) {
				Function f = new Function("function " + j);
				odb.store(f);
			}
			odb.close();
			if (i % 100 == 0) {
				println(i + "/" + size1);
			}
		}
		odb = open(baseName);
		os = odb.getObjects(Function.class);
		assertEquals(size2, os.size());
		odb.close();

		deleteBase(baseName);
	}
}
