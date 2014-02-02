package org.neodatis.odb.test.dotnet;

import java.io.IOException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbTime;

public class TestBTree {

	public void runJava() throws IOException, Exception {

		ODB odb = null;
		ODBTest test = new ODBTest();
		try {
			test.deleteBase("mydb7.neodatis");
			// Open the database
			odb = test.open("mydb7.neodatis");
			long start0 = OdbTime.getCurrentTimeInMs();
			int nRecords = 100000;
			for (int i = 0; i < nRecords; i++) {
				aa ao = new aa();
				ao.ccc = "csdcsdc";
				ao.ww = i;
				odb.store(ao);
			}
			long end0 = OdbTime.getCurrentTimeInMs();

			IQuery query = new CriteriaQuery(aa.class);
			query.orderByAsc("ww");

			long start = OdbTime.getCurrentTimeInMs();
			Objects object12 = odb.getObjects(query, false);
			while (object12.hasNext()) {
				aa s = (aa) object12.next();

				int id = s.ww;

				// println(id);

			}

			long end = OdbTime.getCurrentTimeInMs();
			test.println("Time=" + (end - start) + " / " + (end - start0) + " / " + (end0 - start0));
		} finally {
			if (odb != null) {

				// Close the database
				odb.close();
			}
		}

	}

}

class aa {
	public String ccc;
	public int ww;
}
