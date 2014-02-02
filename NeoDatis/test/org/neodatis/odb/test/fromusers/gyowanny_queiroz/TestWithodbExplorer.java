package org.neodatis.odb.test.fromusers.gyowanny_queiroz;

import java.math.BigDecimal;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

public class TestWithodbExplorer extends ODBTest {

	public void test1() {
		ODB odb = null;
		String baseName = getBaseName();

		try {
			odb = open(baseName);

			ItemVenda2 iv = new ItemVenda2(new Long(1), new Long(2), new Long(3), new BigDecimal(1), new BigDecimal(2), "obs", "desc");
			odb.store(iv);
			odb.store(new ItemVenda2());
			odb.close();
			odb = open(baseName);
			Objects<ItemVenda2> ii = odb.getObjects(ItemVenda2.class);
			assertEquals(2, ii.size());
			iv = ii.getFirst();
			iv.setQuantidade(new BigDecimal(10.3));
			iv.setObservacao("obs1");
			odb.store(iv);
			odb.close();
			odb = open(baseName);

			assertEquals(2, ii.size());
		} finally {
			if (odb != null) {
				odb.close();
			}
			// deleteBase(baseName);
		}
	}

	public void test2() {
		ODB odb = null;
		String baseName = getBaseName();

		try {
			odb = open(baseName);

			ItemVenda2 iv = new ItemVenda2(new Long(1), new Long(2), new Long(3), new BigDecimal(1), new BigDecimal(2), "obs", "desc");
			odb.store(iv);
			odb.store(new ItemVenda2());
			odb.commit();

			Objects<ItemVenda2> ii = odb.getObjects(ItemVenda2.class);
			assertEquals(2, ii.size());
			iv = ii.getFirst();
			iv.setQuantidade(new BigDecimal(10.3));
			odb.store(iv);
			odb.commit();

			ii = odb.getObjects(ItemVenda2.class);
			assertEquals(2, ii.size());
			iv = ii.getFirst();
			iv.setQuantidade(new BigDecimal(12.9));
			odb.store(iv);
			odb.commit();

			odb.close();
			odb = open(baseName);
			ii = odb.getObjects(ItemVenda2.class);

			assertEquals(2, ii.size());
		} finally {
			if (odb != null) {
				odb.close();
			}
			// deleteBase(baseName);
		}
	}

	public void test3() {
		ODB odb = null;
		String baseName = getBaseName();

		try {
			odb = open(baseName);

			ItemVenda2 iv = new ItemVenda2(new Long(1), new Long(2), new Long(3), new BigDecimal(1), new BigDecimal(2), "obs", "desc");
			odb.store(iv);
			odb.store(new ItemVenda2());
			odb.close();

			odb = open(baseName);

			Objects<ItemVenda2> ii = odb.getObjects(ItemVenda2.class);
			assertEquals(2, ii.size());
			iv = ii.getFirst();
			iv.setQuantidade(new BigDecimal(10.3));
			odb.store(iv);
			odb.close();

			odb = open(baseName);
			ii = odb.getObjects(ItemVenda2.class);
			assertEquals(2, ii.size());
			iv = ii.getFirst();
			iv.setQuantidade(new BigDecimal(12.9));
			odb.store(iv);

			odb.close();
			odb = open(baseName);
			ii = odb.getObjects(ItemVenda2.class);

			assertEquals(2, ii.size());
		} finally {
			if (odb != null) {
				odb.close();
			}
			deleteBase(baseName);
		}
	}

}
