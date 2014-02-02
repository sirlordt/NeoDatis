package org.neodatis.odb.test.query.values;

import java.io.IOException;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class TestGetValuesHandlerParameter extends ODBTest {

	public void test1() throws IOException, Exception {
		deleteBase("valuesA1");
		ODB odb = open("valuesA1");
		Handler handler = new Handler();
		for (int i = 0; i < 10; i++) {
			handler.addParameter(new Parameter("test " + i, "value" + i));
		}

		odb.store(handler);
		odb.close();

		odb = open("valuesA1");
		Values values = odb.getValues(new ValuesCriteriaQuery(Handler.class).field("parameters"));

		println(values);
		ObjectValues ov = values.nextValues();
		List l = (List) ov.getByAlias("parameters");
		assertEquals(10, l.size());
		odb.close();
	}

	public void test2() throws IOException, Exception {
		deleteBase("valuesA1");
		ODB odb = open("valuesA1");
		Handler handler = new Handler();
		for (int i = 0; i < 10; i++) {
			handler.addParameter(new Parameter("test " + i, "value" + i));
		}

		odb.store(handler);
		odb.close();

		odb = open("valuesA1");
		// ValuesQuery in getObjects
		try {
			Objects objects = odb.getObjects(new ValuesCriteriaQuery(Handler.class).field("parameters"));
			fail("Should throw exception");
		} catch (Exception e) {
			e.printStackTrace();
		}

		odb.close();
	}

}
