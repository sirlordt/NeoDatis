package org.neodatis.odb.test.fromusers.benhoskins;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class TestWhereContain extends ODBTest {

	private static final String BASE = "BASE";

	public void testShouldRetrievePondByDuck() throws Exception {
		File databaseFile = new File("duckShoot", "duck_shoot");
		if (databaseFile.exists()) {
			Assert.assertTrue("Didn't manage to delete old dB", databaseFile.delete());
		}
		OdbConfiguration.setReconnectObjectsToSession(true);
		OdbConfiguration.useMultiThread(true, 20);
		OdbConfiguration.setDatabaseCharacterEncoding("UTF-8");
		ODBServer server = ODBFactory.openServer(19998);
		server.addBase(BASE, databaseFile.getAbsolutePath(), "elmer", "blunderbus");
		server.startServer(true);
		ODB odb = server.openClient(BASE);

		Duck daffy = new Duck("daffy");
		Duck donald = new Duck("donald");

		Pond pond = new Pond("swan_lake");
		pond.land(daffy);

		odb.store(daffy);
		odb.store(donald);
		odb.store(pond);
		odb.commit();

		// check the duck has safely landed on the pond
		Pond retrievedPond = (Pond) odb.<Pond> getObjects(new CriteriaQuery(Pond.class, Where.equal("name", "swan_lake"))).getFirst();
		Assert.assertEquals(daffy, retrievedPond.ducks.iterator().next());

		// check can retrieve the pond by the duck swimming on it
		Duck retrievedDaffy = odb.<Duck> getObjects(new CriteriaQuery(Duck.class, Where.equal("name", "daffy"))).getFirst();
		Assert.assertEquals("daffy", retrievedDaffy.name);

		// fails with "No more object in collection" when executing the line
		// below
		Pond retrievedByContainsPond1 = odb.<Pond> getObjects(new CriteriaQuery(Pond.class)).getFirst();
		
		IQuery q = odb.criteriaQuery(Pond.class, Where.contain("ducks", retrievedDaffy));
		
		Pond retrievedByContainsPond = odb.<Pond> getObjects(q).getFirst();
		Assert.assertEquals(pond, retrievedByContainsPond);

		Objects<Pond> objects = odb.<Pond> getObjects(new CriteriaQuery(Pond.class, Where.contain("ducks", donald)));
		Assert.assertFalse(objects.hasNext());

	}

	private static final class Pond {
		private Collection<Duck> ducks;
		private final String name;

		public Pond(String name) {
			this.name = name;
			ducks = new ArrayList<Duck>();
		}

		private void land(Duck duck) {
			ducks.add(duck);
		}
	}

	private static final class Duck {
		private String name;

		public Duck(String name) {
			this.name = name;
		}
	}

}
