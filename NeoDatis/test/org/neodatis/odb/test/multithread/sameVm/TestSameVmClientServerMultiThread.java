/**
 * 
 */
package org.neodatis.odb.test.multithread.sameVm;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.tool.LogUtil;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestSameVmClientServerMultiThread extends ODBTest {

	public void testSameVmStoreInSameThread() {
		if (!testNewFeature) {
			return;
		}
		Function function1 = new Function("f1");
		Function function2 = new Function("f2");

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(8000);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ODB client1 = server.openClient(baseName);
			ODB client2 = server.openClient(baseName);

			client2.store(function1);
			client2.close();

			client1.store(function2);
			client1.close();

			ODB client = server.openClient(baseName);
			Objects<Function> functions = client.getObjects(Function.class);
			assertEquals(2, functions.size());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreads() throws Exception {
		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ODB client = server.openClient(baseName);

			ThreadOdbStore t1 = new ThreadOdbStore(client, "f1");
			t1.start();

			ThreadOdbStore t2 = new ThreadOdbStore(client, "f2");
			t2.start();
			while (!t1.isDone()) {
				Thread.sleep(10);
			}

			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(2, functions.size());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreadsX() throws Exception {
		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ODB client = server.openClient(baseName);

			int size = 1000;

			for (int i = 0; i < size; i++) {
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();
			}
			Thread.sleep(100);
			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size, functions.size());

			client = server.openClient(baseName);
			functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size, functions.size());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreadsXWithUpdates() throws Exception {
		if (!testNewFeature) {
			return;
		}

		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ODB client = server.openClient(baseName);

			int size = 1000;

			for (int i = 0; i < size; i++) {
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();

				ThreadOdbUpdate t2 = new ThreadOdbUpdate(client);
				t2.start();
			}
			Thread.sleep(100);
			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size, functions.size());

			client = server.openClient(baseName);
			functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size, functions.size());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreadsXWithDeletesInNewConnection() throws Exception {
		if (!testNewFeature) {
			return;
		}

		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ODB client = server.openClient(baseName);

			ThreadOdbDeleteWithNewConnection t2 = new ThreadOdbDeleteWithNewConnection(server, baseName);
			t2.start();

			int size = 60;

			for (int i = 0; i < size; i++) {
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();
				Thread.sleep(20);
				if (i % 10 == 0) {
					t2.setid("" + (int) i / 10);
				}
			}
			Thread.sleep(2000);
			t2.end();
			Thread.sleep(100);
			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size - t2.nbDeleted, functions.size());

			client = server.openClient(baseName);
			functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size - t2.nbDeleted, functions.size());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreadsXWithDeletesInSameConnection() throws Exception {
		if (!testNewFeature) {
			return;
		}

		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ODB client = server.openClient(baseName);

			ThreadOdbDelete t2 = new ThreadOdbDelete(server, baseName);
			t2.start();

			int size = 600;

			for (int i = 0; i < size; i++) {
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();
				Thread.sleep(500);
				if (i % 10 == 0) {
					t2.setid("" + (int) i / 10);
				}
			}
			t2.end();
			Thread.sleep(100);
			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size - t2.nbDeleted, functions.size());

			client = server.openClient(baseName);
			functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size - t2.nbDeleted, functions.size());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreadsXWithGet() throws Exception {
		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ThreadOdbGet get = new ThreadOdbGet(server, baseName);
			get.start();

			ODB client = server.openClient(baseName);

			int size = 100;

			for (int i = 0; i < size; i++) {
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();
				Thread.sleep(100);
			}

			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size, functions.size());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreadsXWithGet2() throws Exception {
		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ThreadOdbGet get = new ThreadOdbGet(server, baseName);
			get.start();

			ODB client = server.openClient(baseName);

			int size = 100;

			for (int i = 0; i < size; i++) {
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();
				Thread.sleep(100);
				ThreadOdbGetWithSameConnection t2 = new ThreadOdbGetWithSameConnection(client);
				t2.start();
			}
			Thread.sleep(500);
			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size, functions.size());

		} finally {
			server.close();
		}
	}
	
	public void testSameVmOneOdbInTwoThreadsXWithGetOneThread() throws Exception {
		ODBServer server = null;
		String baseName = getBaseName();
		try {
			// Open and start the server
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			// Creates a same vm connection
			ODB client = server.openClient(baseName);

			// Creates a background thread that will select objects
			ThreadOdbGetWithSameConnectionForEver getThread = new ThreadOdbGetWithSameConnectionForEver(client);
			getThread.start();

			int size = 100;

			for (int i = 0; i < size; i++) {
				// Creates a thread to insert an object
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();
				
				Thread.sleep(100);
			}
			Thread.sleep(500);
			// Retrieve all the functions 
			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			// Test the size of the result, it must be equal to size
			assertEquals(size, functions.size());
			// Test the maximum number of functions retrived by the background thread, it must be size too
			assertEquals(size, getThread.getMaxSize());

		} finally {
			server.close();
		}
	}

	public void testSameVmOneOdbInTwoThreadsXWithGet3() throws Exception {
		OdbConfiguration.setShareSameVmConnectionMultiThread(true);

		ODBServer server = null;
		String baseName = getBaseName();
		try {
			server = ODBFactory.openServer(PORT+11);
			server.addBase(baseName, baseName);
			server.startServer(true);

			ThreadOdbGet get = new ThreadOdbGet(server, baseName);
			get.start();

			ODB client = server.openClient(baseName);

			int size = 100;

			for (int i = 0; i < size; i++) {
				ThreadOdbStore t1 = new ThreadOdbStore(client, "f" + i);
				t1.start();
				Thread.sleep(100);
				ThreadOdbGetWithSameConnectionWithNQ t2 = new ThreadOdbGetWithSameConnectionWithNQ(client);
				t2.start();
			}
			Thread.sleep(500);
			Objects<Function> functions = client.getObjects(Function.class);
			client.close();
			System.out.println(functions);
			assertEquals(size, functions.size());

		} finally {
			server.close();
		}
	}

}
