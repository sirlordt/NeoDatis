/**
 * 
 */
package org.neodatis.odb.test.tutorial;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestWithContain extends ODBTest{
	
	public void test1(){
		String baseName = getBaseName();
		// Create instance
		Sport volleyball = new Sport("volley-ball");

		// Create 4 players
		Player player1 = new Player("olivier", new Date(), volleyball);
		Player player2 = new Player("pierre", new Date(), volleyball);
		Player player3 = new Player("elohim", new Date(), volleyball);
		Player player4 = new Player("minh", new Date(), volleyball);

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
		Game game = new Game(new Date(), volleyball, team1, team2);

		ODB odb = null;

		try {
			// Open the database
			odb = open(baseName);

			// Store the object
			odb.store(game);
			
			odb.close();
			
			odb = open(baseName);
			
			Player player = (Player) odb.getObjects(new CriteriaQuery(Player.class, Where.equal("name", "olivier"))).getFirst();
			Objects<Team> teams = odb.getObjects(odb.criteriaQuery(Team.class, Where.contain("players", player)));
			assertEquals(1, teams.size());
			
		} finally {
			if (odb != null) {
				// Close the database
				odb.close();
			}
		}
	}

}
