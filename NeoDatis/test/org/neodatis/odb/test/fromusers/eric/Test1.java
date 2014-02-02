/**
 * 
 */
package org.neodatis.odb.test.fromusers.eric;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.tutorial.Player;
import org.neodatis.odb.test.tutorial.Sport;
import org.neodatis.tool.IOUtil;

/**
 * @author olivier
 * 
 */
public class Test1 {

	public void store() {

		System.out.println(System.getProperty("user.dir"));
		String baseName = "test.neodatis";
		//IOUtil.deleteFile(baseName);
		Player player = new Player("Erico 2", new Date(), new Sport("Squash"));

		System.out.println(player);

		ODB odb = ODBFactory.open(baseName);
		odb.store(player);
		odb.close();

		odb = ODBFactory.open(baseName);
		Objects<Player> players = odb.getObjects(Player.class);
		System.out.println(players.size() + " players");
		System.out.println(players);

		IQuery query = new SimpleNativeQuery() {
			public boolean match(Player player) {
				System.out.println("Testando o player "+ player);
				boolean b = player.getFavoriteSport().getName().toLowerCase().startsWith("nat");
				return b;
			}
		};
		players = odb.getObjects(query);
		System.out.println(players.size() + " players");
		System.out.println(players);

		
		
		odb.close();

	}

	public void store2() {

		String baseName = "test.neodatis";
		//OdbConfiguration.setLogServerConnections(true);
		ODBServer server = ODBFactory.openServer(ODBTest.PORT);
		server.addBase("TEST", baseName);
		server.startServer(true);

		IOUtil.deleteFile(baseName);
		Player player = new Player("Erico", new Date(), new Sport("Natação"));

		System.out.println(player);

		ODB odb = ODBFactory.openClient("Localhost", 13000, "TEST");
		odb.store(player);
		odb.close();

		odb = ODBFactory.openClient("Localhost", 13000, "TEST");
		Objects<Player> players = odb.getObjects(Player.class);
		System.out.println(players.size() + " players");
		System.out.println(players);
		odb.close();

	}

	public void store3() {

		String baseName = "test.neodatis";

		ODBServer server = ODBFactory.openServer(ODBTest.PORT);
		server.addBase("TEST", baseName);
		server.startServer(true);

		IOUtil.deleteFile(baseName);
		Player player = new Player("Erico", new Date(), new Sport("Natação"));

		System.out.println(player);

		ODB odb = server.openClient("TEST");
		odb.store(player);
		odb.close();

		
		
		
		odb = ODBFactory.openClient("Localhost", 13000, "TEST");
		
		Objects<Player> players = odb.getObjects(Player.class);
		System.out.println(players.size() + " players");
		System.out.println(players);
		odb.close();

	}

	public static void main(String[] args) {
		Test1 t = new Test1();
		t.store2();

	}

}
