package org.neodatis.odb.test.ee;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.country.City;
import org.neodatis.odb.test.vo.country.Country;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestDeleteCascade extends ODBTest {

	public void test1() {
		if(!isLocal && !testNewFeature){
			return;
		}
		String baseName = getBaseName();

		ODB odb = open(baseName);

		Profile profile = new Profile("profile");
		for (int i = 0; i < 100; i++) {
			profile.addFunction(new Function("function 1"));
		}
		User user = new User("name", "email", profile);

		OID oid = odb.store(user);
		odb.close();

		odb = open(baseName);
		// user = (User) odb.getObjectFromId(oid);
		user = (User) odb.getObjects(User.class, true).getFirst();
		odb.deleteCascade(user);
		odb.close();

		odb = open(baseName);
		Objects<Function> functions = odb.getObjects(Function.class, true);
		Objects<Profile> profiles = odb.getObjects(Profile.class, true);
		Objects<User> users = odb.getObjects(User.class, true);
		deleteBase(baseName);
		assertEquals(0, functions.size());
		assertEquals(0, profiles.size());
		assertEquals(0, users.size());
	}

	public void test2() {
		if(!isLocal && !testNewFeature){
			return;
		}

		String baseName = getBaseName();

		ODB odb = open(baseName);

		Country brasil = new Country("France");

		for (int i = 0; i < 10; i++) {
			City city = new City("city" + i);

			city.setCountry(brasil);
			brasil.addCity(city);
		}
		odb.store(brasil);
		odb.close();
		
		odb = open(baseName);
		Objects<Country> countries = odb.getObjects(Country.class);
		Country france = countries.getFirst();
		
		odb.deleteCascade(france);
		odb.close();
		odb = open(baseName);
		countries = odb.getObjects(Country.class);
		Objects<City> cities = odb.getObjects(City.class);
		assertEquals(0, countries.size());
		assertEquals(0, cities.size());
		
	}

}
