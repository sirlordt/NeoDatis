package org.neodatis.odb.test.query.values;

import java.math.BigInteger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User2;

public class TestPerfObjectValuesVsCriteriaQueryWithIndexes extends ODBTest {

	public void populate() throws Exception {

		ODB odb = open("perfOValuesVsCriteriaIndex");
		String[] atts = { "name" };
		try {
			odb.getClassRepresentation(User2.class).addUniqueIndexOn("Index", atts, true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		int nbProfiles = 200;
		int nbUsers = 500000;
		Profile[] profiles = new Profile[nbProfiles];
		User2[] users = new User2[nbUsers];

		int userStart = 1500000;
		int profileStart = 600;
		// First creates profiles
		for (int i = 0; i < nbProfiles; i++) {
			profiles[i] = new Profile("profile " + (i + profileStart), new Function("function Profile" + i));
			odb.store(profiles[i]);
		}

		// Then creates users
		for (int i = 0; i < nbUsers; i++) {
			users[i] = new User2("user" + (i + userStart), "user mail" + i, profiles[getProfileIndex(nbProfiles)], i);
			odb.store(users[i]);
			if (i % 10000 == 0) {
				println(i);
			}
		}
		odb.close();

	}

	private int getProfileIndex(int nbProfiles) {
		return (int) Math.random() * nbProfiles;
	}

	public static void main(String[] args) throws Exception {
		TestPerfObjectValuesVsCriteriaQueryWithIndexes t = new TestPerfObjectValuesVsCriteriaQueryWithIndexes();
		// t.populate();
		t.t1est1();
		t.t1estA();
	}

	public void t1est() throws Exception {
		ODB odb = open("perfOValuesVsCriteriaIndex");
		OdbConfiguration.monitorMemory(true);
		IQuery q = new CriteriaQuery(User2.class);
		BigInteger b = odb.count(new CriteriaQuery(User2.class));
		println(b);
		System.out.println(q.getExecutionPlan().getDetails());
		assertEquals(new BigInteger("500000"), b);

		odb.close();
	}

	public void t1estA() throws Exception {
		ODB odb = open("perfOValuesVsCriteriaIndex");
		OdbConfiguration.monitorMemory(true);
		IQuery q = new CriteriaQuery(User2.class, Where.equal("name", "user1999999"));
		Objects objects = odb.getObjects(q, false);
		println(objects.size());
		System.out.println(q.getExecutionPlan().getDetails());
		assertEquals(1, objects.size());

		objects = odb.getObjects(q, false);
		println(objects.size());
		System.out.println(q.getExecutionPlan().getDetails());
		assertEquals(1, objects.size());

		odb.close();
	}

	public void t1est1() throws Exception {
		ODB odb = open("perfOValuesVsCriteriaIndex");
		OdbConfiguration.monitorMemory(true);
		IValuesQuery q = new ValuesCriteriaQuery(User2.class, Where.equal("name", "user1999999")).field("name");
		Values v = odb.getValues(q);
		println(v.size());
		System.out.println(q.getExecutionPlan().getDetails());
		assertEquals(1, v.size());

		odb.close();
	}

	public void test() {

	}

}
