/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.luhan;

import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.server.connection.ConnectionManager;
import org.neodatis.odb.impl.tool.LogUtil;
import org.neodatis.tool.IOUtil;
import org.neodatis.tool.mutex.MutexFactory;

/**
 * @author olivier
 * 
 */
public class TestCurrent {
	public static final String ODB_NAME = "testCurrent.odb";
	private static ODBServer server = null;

	public static void connectServer() {
		IOUtil.deleteFile(ODB_NAME);
		server = ODBFactory.openServer(8989);
		server.addBase("base1", ODB_NAME);
		server.startServer(true);
	}

	public static void closeServer() {
		if (server != null) {
			server.close();
		}
	}

	public static void main(String[] args) throws Exception {
		// MutexFactory.setDebug(true);
		OdbConfiguration.setDebugEnabled(true);
		LogUtil.enable(ConnectionManager.LOG_ID);
		OdbConfiguration.lockObjectsOnSelect(true);
		connectServer();
		ClientThread2 c1 = new ClientThread2(1);
		ClientThread2 c2 = new ClientThread2(5);
		c1.start();
		c2.start();

		c1.join();
		c2.join();
		System.out.println("end");
		closeServer();
	}
}