/**
 * 
 */
package org.neodatis.odb.test.server;

import java.io.File;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.server.connection.ConnectionManager;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.server.connection.DefaultServerConnection;
import org.neodatis.odb.impl.tool.LogUtil;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class SimultaneousConnections3 extends ODBTest {
	boolean done = false;

	public void test1() {
		if(isLocal){
			return;
		}
		String baseName = getBaseName();

		ODB odb = openClient(HOST, PORT, baseName);
		odb.store(new Player("first player"));
		odb.close();

		ODB odb1 = null;
		ODB odb2 = null;
		try {
			odb1 = openClient(HOST, PORT, baseName);
			odb2 = openClient(HOST, PORT, baseName);

			Player playerA = new Player("playerA");
			Player playerB = new Player("playerB");

			odb1.store(playerA);
			odb2.store(playerB);

			odb1.commit();
			odb2.commit();

			//odb1.close();
			//odb2.close();

			//odb1 = ODBFactory.openClient("localhost", port, baseName);
			//odb2 = ODBFactory.openClient("localhost", port, baseName);

			IQuery query = new CriteriaQuery(Player.class);
			Objects<Player> players2 = odb2.getObjects(query);
			Objects<Player> players1 = odb1.getObjects(query);
			

			for (Player player : players1) {
				System.out.println("odb1 sees: " + player.name);
			}
			for (Player player : players2) {
				System.out.println("odb2 sees: " + player.name);
			}
			assertEquals(baseName, 3, players1.size());
			assertEquals(baseName, 3, players2.size());
		} finally {
			if (odb1 != null) {
				odb1.close();
			}
			if (odb2 != null) {
				odb2.close();
			}
			
		}

	}

	public void test2() throws InterruptedException {
		// OdbConfiguration.setDebugEnabled(true);
		// LogUtil.logOn(DefaultServerConnection.LOG_ID, true);
		// LogUtil.logOn(ConnectionManager.LOG_ID, true);

		if(isLocal){
			return;
		}
		int port = PORT + 10;
		final String baseName = getBaseName();
		final ODBServer server = ODBFactory.openServer(port);
		server.startServer(true);
		// OdbConfiguration.lockObjectsOnSelect(true);
		ODB odb = server.openClient(baseName);
		Player player = new Player("player1");
		// set the value to 0
		player.setValue(0);
		OID oid = odb.store(player);
		odb.close();

		ODB outerODB = server.openClient(baseName);
		Objects<Player> outerPlayers = outerODB.getObjects(Player.class);
		Player outerPlayer = outerPlayers.getFirst();
		// increment the value by 1
		outerPlayer.setValue(outerPlayer.getValue() + 1);

		Thread thread = new Thread() {
			public void run() {
				ODB innerODB = server.openClient(baseName);
				// deadlock should occur on this query because Player.class
				// objects should be locked
				Objects<Player> innerPlayers = innerODB.getObjects(Player.class);
				Player innerPlayer = innerPlayers.getFirst();
				// increment the value by 1
				innerPlayer.setValue(innerPlayer.getValue() + 1);
				innerODB.store(innerPlayer);
				innerODB.close();
				synchronized (this) {
					System.out.println("this=" + System.identityHashCode(this));
					done = true;
					this.notify();
				}
			}
		};

		thread.start();
		System.out.println("thread=" + System.identityHashCode(thread));
		synchronized (thread) {
			while (!done) {
				thread.wait();
			}
		}
		int version = outerODB.ext().getObjectVersion(oid, false);
		println("version +=" + version);
		assertEquals(2, version);
		outerODB.store(outerPlayer);

		odb = server.openClient(baseName);
		version = odb.ext().getObjectVersion(oid, false);
		println("version in another connection=" + version);
		odb.close();

		outerODB.close();
		System.out.println("List players:");
		odb = server.openClient(baseName);
		version = odb.ext().getObjectVersion(oid, false);
		println("version=" + version);
		Objects<Player> players = odb.getObjects(Player.class);
		for (Player p : players) {
			System.out.println(p);
		}
		odb.close();

		server.close();
		File file = new File(baseName);
		file.delete();
	}

	public static void main(String[] args) {
		new SimultaneousConnections3().test1();
	}

	public class Player {
		String name;
		int value;

		public Player(String name) {
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return name + " , value = " + value;
		}
	}
}
