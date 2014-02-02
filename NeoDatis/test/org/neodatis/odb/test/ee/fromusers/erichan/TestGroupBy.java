/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.erichan;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.IOUtil;

/**
 * @author olivier
 * 
 */
public class TestGroupBy extends ODBTest{

	public void test1() {
		String baseName = getBaseName();
		ODB odb = null;
		try {
			odb = ODBFactory.open(baseName);

			Man m1 = new Man(1, 1, 20);
			odb.store(m1);
			Man m2 = new Man(2, 1, 30);
			odb.store(m2);
			Man m3 = new Man(3, 1, 27);
			odb.store(m3);
			Man m4 = new Man(4, 2, 21);
			odb.store(m4);

			IValuesQuery q = new ValuesCriteriaQuery(Man.class).field("id").max("age").groupBy("id");

			Values v = odb.getValues(q);

			assertEquals(v.size(), 2);
			for (ObjectValues objectValues : v) {
				println("=====" + objectValues.toString());
			}


		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}
}
