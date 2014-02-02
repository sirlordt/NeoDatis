/**
 * 
 */
package org.neodatis.odb.test.index;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestIndexesWithValueQueries extends ODBTest {

	public void test1() {
		String baseName = getBaseName();

		ODB odb = null;
		int size = 10000;
		try {
			odb = open(baseName);
			odb.getClassRepresentation(Function.class).addIndexOn("index1", new String[] { "name" }, true);

			for (int i = 0; i < size; i++) {
				odb.store(new Function("function " + i));
			}
			odb.close();

			odb = open(baseName);
			// build a value query to retrieve only the name of the function
			IValuesQuery vq = new ValuesCriteriaQuery(Function.class, Where.equal("name", "function " + (size - 1))).field("name");

			Values values = odb.getValues(vq);
			assertEquals(1, values.size());
			println(vq.getExecutionPlan().getDetails());
			assertEquals(true, vq.getExecutionPlan().useIndex());

		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

}
