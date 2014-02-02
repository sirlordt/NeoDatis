package org.neodatis.odb.test.server;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.IOUtil;

public class TestOpenCloseClient extends ODBTest {

	public void test1() throws Exception {
		IOUtil.deleteFile("base1.neodatis");
		int port = PORT + 7;
		ODBServer server = ODBFactory.openServer(port);
		server.setAutomaticallyCreateDatabase(true);

		server.startServer(true);
		int size = isLocal ? 1000 : 100;
		ODB odb = ODBFactory.openClient("localhost", port, "base1.neodatis");
		println("nb objects=" + odb.getObjects(MyObject.class).size());
		// First create 100 Objects
		for (int i = 0; i < 100; i++) {
			odb.store(new MyObject("start"));
		}
		odb.close();
		for (int i = 0; i < size; i++) {
			odb = ODBFactory.openClient("localhost", port, "base1.neodatis");
			MyObject object = new MyObject("object-" + i);

			// Create object
			odb.store(object);
			odb.commit();
			odb.close();

			odb = ODBFactory.openClient("localhost", port, "base1.neodatis");
			Objects os = odb.getObjects(new CriteriaQuery(MyObject.class, Where.equal("name", "start")));
			Object o = odb.getObjects(new CriteriaQuery(MyObject.class, Where.equal("name", "object-" + i))).getFirst();
			odb.delete(o);
			odb.close();

			if (i % 100 == 0) {
				println("");
				MemoryMonitor.displayCurrentMemory("" + i, false);
			} else {
				print(".");
			}
		}
		server.close();
	}

	/**
	 * Create the database in client server mode with user and password, then
	 * try to open locally the file and check data
	 * 
	 * @throws Exception
	 */
	public void test2() throws Exception {
		IOUtil.deleteFile("base1.neodatis");
		int port = PORT + 7;
		ODBServer server = ODBFactory.openServer(port);
		server.setAutomaticallyCreateDatabase(true);

		server.startServer(true);
		int size = 100;
		ODB odb = ODBFactory.openClient("localhost", port, "base1.neodatis", "user", "password");
		// First create 100 Objects
		for (int i = 0; i < size; i++) {
			odb.store(new MyObject("start"));
		}
		odb.close();
		server.close();

		odb = ODBFactory.open("base1.neodatis", "user", "password");
		Objects<MyObject> objects = odb.getObjects(MyObject.class);
		odb.close();
		IOUtil.deleteFile("base1.neodatis");
		assertEquals(size, objects.size());

	}

	/**
	 * Create the database in client server mode with no user and password, then
	 * try to open locally the file and check data
	 * 
	 * @throws Exception
	 */
	public void test3() throws Exception {
		IOUtil.deleteFile("base1.neodatis");
		int port = PORT + 7;
		ODBServer server = ODBFactory.openServer(port);
		server.setAutomaticallyCreateDatabase(true);

		server.startServer(true);
		int size = 100;
		ODB odb = ODBFactory.openClient("localhost", port, "base1.neodatis");
		// First create 100 Objects
		for (int i = 0; i < size; i++) {
			odb.store(new MyObject("start"));
		}
		odb.close();
		server.close();

		odb = ODBFactory.open("base1.neodatis");
		Objects<MyObject> objects = odb.getObjects(MyObject.class);
		odb.close();
		IOUtil.deleteFile("base1.neodatis");
		assertEquals(size, objects.size());

	}

}

class MyObject {
	private String name;

	public MyObject(String name) {
		this.name = name;
	}
}
