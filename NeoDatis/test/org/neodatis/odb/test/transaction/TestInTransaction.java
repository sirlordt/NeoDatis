package org.neodatis.odb.test.transaction;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.odb.test.vo.sport.Game;
import org.neodatis.odb.test.vo.sport.Player;
import org.neodatis.odb.test.vo.sport.Sport;
import org.neodatis.odb.test.vo.sport.Team;

public class TestInTransaction extends ODBTest {

	/**
	 * Test select objects that are not yet commited
	 * 
	 * @throws Exception
	 * 
	 */
	public void testSelectUnCommitedObject() throws Exception {
		ODB odb = null;

		try {
			String baseName = getBaseName();
			
			odb = open(baseName);

			for (int i = 0; i < 4; i++) {
				odb.store(new Function("function " + i));
			}
			odb.close();

			// reopen the database
			odb = open(baseName);
			// stores a new function
			odb.store(new Function("function uncommited"));

			Objects functions = odb.getObjects(Function.class);
			assertEquals(5, functions.size());

		} finally {
			if (odb != null) {
				odb.close();
				String baseName = getBaseName();
			}
		}

	}

	/**
	 * Test select objects that are not yet commited
	 * 
	 * @throws Exception
	 * 
	 */
	public void testSelectUnCommitedObject2() throws Exception {
		ODB odb = null;

		try {
			String baseName = getBaseName();
			odb = open(baseName);

			for (int i = 0; i < 4; i++) {
				odb.store(new User("user" + i, "email" + i, new Profile("profile" + i, new Function("function" + i))));
			}
			odb.close();

			// reopen the database
			odb = open(baseName);
			// stores a new function
			odb
					.store(new User("uncommited user", "uncommied email", new Profile("uncommiedt profile", new Function(
							"uncommited function"))));

			Objects users = odb.getObjects(User.class);
			assertEquals(5, users.size());
			Objects functions = odb.getObjects(Function.class);
			assertEquals(5, functions.size());
			Objects profiles = odb.getObjects(Profile.class);
			assertEquals(5, profiles.size());

		} finally {
			if (odb != null) {
				odb.close();
				String baseName = getBaseName();
			}
		}

	}

	/**
	 * Test select objects that are not yet commited. It also test the meta
	 * model class reference for in transaction class creation
	 * 
	 * @throws Exception
	 * 
	 */
	public void testSelectUnCommitedObject3() throws Exception {
		String baseName = getBaseName();

		// Create instance
		Sport sport = new Sport("volley-ball");

		ODB odb = null;

		try {
			// Open the database
			odb = open(baseName);

			// Store the object
			odb.store(sport);
		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}

		try {
			// Open the database
			odb = open(baseName);
			// Let's insert a tennis player
			Player agassi = new Player("André Agassi", new Date(), new Sport("Tennis"));
			odb.store(agassi);

			IQuery query = new CriteriaQuery(Player.class, Where.equal("favoriteSport.name", "volley-ball"));

			Objects players = odb.getObjects(query);

			println("\nStep 4 : Players of Voller-ball");

			int i = 1;
			// display each object
			while (players.hasNext()) {
				println((i++) + "\t: " + players.next());
			}
		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}
	}

	/**
	 * Test select objects that are not yet committed
	 * 
	 * @throws Exception
	 * 
	 */
	public void testSelectUnCommitedObject4() throws Exception {
		String baseName = getBaseName();

		// Create instance
		Sport sport = new Sport("volley-ball");

		ODB odb = null;

		try {
			// Open the database
			odb = open(baseName);

			// Store the object
			odb.store(sport);
		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}

		// Create instance
		Sport volleyball = new Sport("volley-ball");

		// Create 4 players
		Player player1 = new Player("olivier", new Date(), volleyball);
		Player player2 = new Player("pierre", new Date(), volleyball);
		Player player3 = new Player("elohim", new Date(), volleyball);
		org.neodatis.odb.test.vo.sport.Player player4 = new Player("minh", new Date(), volleyball);

		// Create two teams
		Team team1 = new Team("Paris");
		Team team2 = new Team("Montpellier");

		// Set players for team1
		team1.addPlayer(player1);
		team1.addPlayer(player2);

		// Set players for team2
		team2.addPlayer(player3);
		team2.addPlayer(player4);

		// Then create a volley ball game for the two teams
		org.neodatis.odb.test.vo.sport.Game game = new Game(new Date(), volleyball, team1, team2);

		odb = null;

		try {
			// Open the database
			odb = open(baseName);

			// Store the object
			odb.store(game);
		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}
		try {
			// Open the database
			odb = open(baseName);
			IQuery query = new CriteriaQuery(Player.class, Where.equal("name", "olivier"));
			Objects players = odb.getObjects(query);

			println("\nStep 3 : Players with name olivier");

			int i = 1;
			// display each object
			while (players.hasNext()) {
				println((i++) + "\t: " + players.next());
			}
		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}

		try {
			// Open the database
			odb = open(baseName);
			// Let's insert a tennis player
			Player agassi = new Player("André Agassi", new Date(), new Sport("Tennis"));
			OID oid = odb.store(agassi);

			IQuery query = new CriteriaQuery(Player.class, Where.equal("favoriteSport.name", "volley-ball"));

			Objects players = odb.getObjects(query);

			println("\nStep 4 : Players of Voller-ball");

			int i = 1;
			// display each object
			while (players.hasNext()) {
				println((i++) + "\t: " + players.next());
			}
		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}
	}

}
