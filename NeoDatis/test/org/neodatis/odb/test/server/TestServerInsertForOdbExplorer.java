package org.neodatis.odb.test.server;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ODBServerImpl;
import org.neodatis.odb.impl.tool.LogUtil;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestServerInsertForOdbExplorer {

	public static void main2(String[] args) throws InterruptedException {
		ODB odb = null;

		int size = 20000;

		LogUtil.enable(ODBServerImpl.LOG_ID);
		OdbConfiguration.setLogServerConnections(true);
		ODBServer server = ODBFactory.openServer(ODBTest.PORT);
		server.setAutomaticallyCreateDatabase(true);
		// LogUtil.allOn(true);
		server.startServer(true);

		for (int i = 0; i < size; i++) {
			odb = ODBFactory.openClient("localhost", ODBTest.PORT, "explorer");
			odb.store(getUserInstance(i));
			Thread.sleep(1000);
			odb.close();
			if (i % 5 == 0) {
				MemoryMonitor.displayCurrentMemory("i=" + i, false);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ODB odb = null;

		int size = 100000;

		LogUtil.enable(ODBServerImpl.LOG_ID);
		// OdbConfiguration.setLogServerConnections(true);
		ODBServer server = ODBFactory.openServer(ODBTest.PORT);
		server.setAutomaticallyCreateDatabase(true);
		// LogUtil.allOn(true);
		server.startServer(true);

		for (int i = 0; i < size; i++) {
			odb = server.openClient("explorer");
			odb.store(getUserInstance(i));
			// Thread.sleep(1000);
			odb.close();
			if (i % 5 == 0) {
				MemoryMonitor.displayCurrentMemory("i=" + i, false);
			}
		}
	}

	public static Object getUserInstance(int i) {
		Function login = new Function("login" + i);
		Function logout = new Function("logout" + i);
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator" + i, list);
		User user = new User("olivier smadja" + i, "olivier@neodatis.com", profile);
		return user;
	}

}
