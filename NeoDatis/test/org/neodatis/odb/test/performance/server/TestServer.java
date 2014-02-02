package org.neodatis.odb.test.performance.server;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.tool.IOUtil;
import org.neodatis.tool.wrappers.OdbTime;

public class TestServer {
	public static final String ODB = "odb";

	public static void main(String[] args) throws Exception {
		int size = 10000;
		System.out.println("Running Client Server and Local for " + size + " objects - ");
		IOUtil.deleteFile(ODB);
		ODBServer server = ODBFactory.openServer(8000);
		server.addBase("Test", ODB);
		server.startServer(true);
		ODB odb = ODBFactory.openClient("localhost", 8000, "Test");
		// ODB odb = server.openClient("Test");
		long time = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			odb.store(new TestClass("test"));
			if (i % 100000 == 0) {
				System.out.println(i);
			}
		}
		odb.close();
		server.close();
		double csTime = OdbTime.getCurrentTimeInMs() - time;
		System.out.println("CS time = " + csTime);

		IOUtil.deleteFile(ODB);
		odb = ODBFactory.open(ODB);
		time = OdbTime.getCurrentTimeInMs();
		for (int i = 0; i < size; i++) {
			odb.store(new TestClass("test"));
		}
		odb.close();
		double localTime = OdbTime.getCurrentTimeInMs() - time;
		System.out.println("Local Mode time = " + localTime);

		System.out.println("Ratio " + csTime / localTime);
	}

}
