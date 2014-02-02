/**
 * 
 */
package org.neodatis.odb.test.fromusers.MatthewMorgan;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.fromusers.MatthewMorgan.Receipt.ItemLine;

/**
 * @author olivier
 * 
 */
public class TestCollection extends ODBTest {
	public void test1() {
		String baseName = getBaseName();

		ODB odb = null;

		try {
			odb = open(baseName);

			Receipt r = new Receipt(1, new Date(), 2);

			for (int i = 0; i < 10; i++) {
				r.addItem(i, "desc" + i);
			}
			odb.store(r);

			odb.commit();
			ItemLine il = (ItemLine) odb.getObjects(new CriteriaQuery(Receipt.ItemLine.class, Where.equal("itemNum", 8))).getFirst();

			assertEquals(8, il.getItemNum());
			assertEquals("desc8", il.getItemDescription());

			IQuery q = odb.criteriaQuery(Receipt.class, Where.contain("itemLines", il));
			Receipt r2 = (Receipt) odb.getObjects(q).getFirst();

			assertEquals(r.getCustomerId(), r2.getCustomerId());
			assertEquals(r.getDocId(), r2.getDocId());
			assertEquals(r.getTimestamp(), r2.getTimestamp());
			assertEquals(r.getItemLines().size(), r2.getItemLines().size());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

}
