package org.neodatis.odb.test.query.values;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.odb.test.vo.login.User2;

public class TestGetValuesGroupBy extends ODBTest {

	public void test1() throws IOException, Exception {
		deleteBase("values2");
		ODB odb = open("values2");
		TestClass tc1 = new TestClass();
		tc1.setInt1(45);
		odb.store(tc1);

		TestClass tc2 = new TestClass();
		tc2.setInt1(45);
		odb.store(tc2);

		TestClass tc3 = new TestClass();
		tc3.setInt1(46);
		odb.store(tc3);

		odb.close();

		odb = open("values2");
		IValuesQuery vq = new ValuesCriteriaQuery(TestClass.class).sum("int1", "sum of int1").groupBy("int1");
		vq.orderByAsc("int1");
		Values values = odb.getValues(vq);
		assertEquals(2, values.size());

		println(values);
		ObjectValues ov = values.nextValues();

		assertEquals(BigDecimal.valueOf(90), ov.getByAlias("sum of int1"));

		ov = values.nextValues();
		assertEquals(BigDecimal.valueOf(46), ov.getByAlias("sum of int1"));

		odb.close();

		assertEquals(2, values.size());

	}

	public void test2() throws IOException, Exception {
		deleteBase("values2");
		ODB odb = open("values2");
		TestClass tc1 = new TestClass();
		tc1.setInt1(45);
		odb.store(tc1);

		TestClass tc2 = new TestClass();
		tc2.setInt1(45);
		odb.store(tc2);

		TestClass tc3 = new TestClass();
		tc3.setInt1(46);
		odb.store(tc3);

		odb.close();

		odb = open("values2");
		IValuesQuery vq = new ValuesCriteriaQuery(TestClass.class).sum("int1", "sum of int1").count("count").groupBy("int1");
		vq.orderByAsc("int1");
		Values values = odb.getValues(vq);

		println(values);
		ObjectValues ov = values.nextValues();

		assertEquals(BigDecimal.valueOf(90), ov.getByAlias("sum of int1"));
		assertEquals(BigInteger.valueOf(2), ov.getByAlias("count"));

		ov = values.nextValues();
		assertEquals(BigDecimal.valueOf(46), ov.getByAlias("sum of int1"));
		assertEquals(BigInteger.valueOf(1), ov.getByAlias("count"));

		odb.close();

		assertEquals(2, values.size());

	}

	/**
	 * Retrieving the name of the profile, the number of user for that profile
	 * and their average login number grouped by the name of the profile
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	public void test3() throws IOException, Exception {
		deleteBase("values2");
		ODB odb = open("values2");

		Profile p1 = new Profile("profile1", new Function("f1"));
		Profile p2 = new Profile("profile2", new Function("f2"));

		User u1 = new User2("user1", "user@neodatis.org", p1, 1);
		User u2 = new User2("user2", "user@neodatis.org", p1, 2);
		User u3 = new User2("user3", "user@neodatis.org", p1, 3);
		User u4 = new User2("user4", "user@neodatis.org", p2, 4);
		User u5 = new User2("user5", "user@neodatis.org", p2, 5);

		odb.store(u1);
		odb.store(u2);
		odb.store(u3);
		odb.store(u4);
		odb.store(u5);
		odb.close();

		odb = open("values2");
		IValuesQuery q = new ValuesCriteriaQuery(User2.class).field("profile.name").count("count").avg("nbLogins", "avg").groupBy(
				"profile.name");
		q.orderByAsc("name");
		Values values = odb.getValues(q);

		println(values);
		ObjectValues ov = values.nextValues();
		assertEquals(2, values.size());

		assertEquals("profile1", ov.getByAlias("profile.name"));
		assertEquals(new BigInteger("3"), ov.getByAlias("count"));
		assertEquals(new BigDecimal("2.00"), ov.getByAlias("avg"));

		odb.close();

		assertEquals(2, values.size());

	}
}
