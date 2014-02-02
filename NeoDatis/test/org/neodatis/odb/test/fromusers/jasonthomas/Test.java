package org.neodatis.odb.test.fromusers.jasonthomas;

import junit.framework.TestCase;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.test.ODBTest;

public class Test extends ODBTest {

	/**
	 * This test fails because each client should execute in different threads
	 * as the session id is stored in a thread specific map
	 * 
	 */
	public void testSameVm() {
		if (!testNewFeature) {
			return;
		}

		TestObject object1 = new TestObject();
		TestObject object2 = new TestObject();

		ODBServer server = null;

		try {
			server = ODBFactory.openServer(8000);
			server.addBase("test-db", "test-db");
			server.startServer(true);

			ODB client1 = server.openClient("test-db");
			ODB client2 = server.openClient("test-db");

			client2.store(object2);
			client2.close();

			client1.store(object1);
			client1.close();

		} finally {
			server.close();
		}
	}

	public void testRealClientServer() {
		TestObject object1 = new TestObject();
		TestObject object2 = new TestObject();

		ODBServer server = null;

		try {
			server = ODBFactory.openServer(8008);
			server.addBase("test-db", "test-db");
			server.startServer(true);

			ODB client1 = ODBFactory.openClient("localhost", 8000, "test-db");

			ODB client2 = ODBFactory.openClient("localhost", 8000, "test-db");
			client2.store(object2);
			client2.close();

			client1.store(object1);
			client1.close();

		} finally {
			if (server != null) {
				server.close();
			}
		}
	}

}

class TestObject {
	int i = 0;
}
