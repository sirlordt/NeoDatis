package org.neodatis.odb.test.performance;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestComparisonDotNet extends ODBTest {

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}

		ODB odb = null;

		try {
			deleteBase("mydb.neodatis");
			// Open the database
			odb = open("mydb.neodatis");

			long t0 = OdbTime.getCurrentTimeInMs();
			int nRecords = 100000;
			for (int i = 0; i < nRecords; i++) {
				Class1 ao = new Class1(189, "csdcsdc");
				odb.store(ao);
			}
			odb.close();
			long t1 = OdbTime.getCurrentTimeInMs();

			odb = open("mydb.neodatis");
			Objects ssss = odb.getObjects(Class1.class);
			long t2 = OdbTime.getCurrentTimeInMs();
			println("Elapsed time for inserting " + nRecords + " records: " + (t1 - t0) + " / select = " + (t2 - t1));

		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}
	}

}
