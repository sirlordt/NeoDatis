package org.neodatis.odb.test.server;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.IOUtil;

public class TestServerClose extends ODBTest {

	public void test1() throws Exception {
		try {
			String name = getBaseName();
			IOUtil.deleteFile(name);
			ODBServer server = ODBFactory.openServer(5000);
			server.addBase(name, name);
			server.startServer(true);
			Thread.sleep(1000);
			server.close();
		} catch (Exception e) {
			fail("Should not throw Exception on server close");
		}
	}

	public void test2() throws Exception {
		try {
			String name = getBaseName();
			IOUtil.deleteFile(name);
			ODBServer server = ODBFactory.openServer(5000);
			server.addBase(name, name);
			server.startServer(true);

			ODB odb = ODBFactory.openClient("localhost", 5000, name);
			odb.store(new Function("Test"));
			odb.close();

			server.close();
		} catch (Exception e) {
			fail("Should not throw Exception on server close");
		}

	}
}
