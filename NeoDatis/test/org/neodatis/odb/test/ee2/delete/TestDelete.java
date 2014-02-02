package org.neodatis.odb.test.ee2.delete;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.layers.layer3.engine.ObjectReader;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.country.City;
import org.neodatis.odb.test.vo.country.Country;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;

public class TestDelete extends ODBTest {
	
	/** testing the object with oid xx not found **/
	public void test1(){
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();
		
		ODB odb = open(baseName);
		
		Function f1 = new Function("f1");
		Function f2 = new Function("f2");
		Function f3 = new Function("f3");
		Function f4 = new Function("f4");
		
		Profile p = new Profile("p1");
		
		OID oidf1 = odb.store(f1);
		OID oidf2 = odb.store(f2);
		OID oidf3 = odb.store(f3);
		OID oidf4 = odb.store(f4);
		
		p.addFunction(f1);
		p.addFunction(f2);
		p.addFunction(f3);
		p.addFunction(f4);
		
		OID oidp1 = odb.store(p);
		
		System.out.println("f1="+oidf1);
		System.out.println("f2="+oidf2);
		System.out.println("f3="+oidf3);
		System.out.println("f4="+oidf4);
		System.out.println("profile="+oidp1);
		odb.deleteCascade(f3);
		odb.deleteCascade(p);
		//odb.deleteCascade(f3);
		odb.close();
		
	}
	
	public void test1WithCommits(){
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();
		
		// Open the db
		ODB odb = open(baseName);
		
		Function f1 = new Function("f1");
		Function f2 = new Function("f2");
		
		// store function 1 and commit
		OID oidf1 = odb.store(f1);
		odb.commit();
		
		// Stores function 2
		OID oidf2 = odb.store(f2);

		// change function 1 and re-store it
		f1.setName("updated function 1");
		oidf1 = odb.store(f1);
		displayObjectChaining(odb, "before commit");
		odb.commit();
		displayObjectChaining(odb, "after commit");
		

		Objects<Function> functions = odb.getObjects(Function.class);
		assertEquals(2, functions.size());
		System.out.println("f1="+oidf1);
		System.out.println("f2="+oidf2);
		odb.close();
		
	}
	
	protected void displayObjectChaining(ODB odb, String string) {
		// display object chaining
		IStorageEngine engine = Dummy.getEngine(odb);
		// gets the neodatis reader
		ObjectReader reader = (ObjectReader) engine.getObjectReader();
		// gets the class info (The meta representation of the class
		ClassInfo ci = engine.getSession(false).getMetaModel().getClassInfo(Function.class.getName(), false);
		if (ci != null) {
			System.out.println(string + "Nb Objects="+ ci.getNumberOfObjects() +   " - Object chaining =" + reader.getClassInfoFullObjectChaining(ci));
			System.out.println("\t\t:"+ ci.getCommitedZoneInfo() + " | " + ci.getUncommittedZoneInfo());
		}else{
			System.out.println(string + " Class does not existt");
		}

	}
	public void test2(){
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();
		
		ODB odb = open(baseName);
		
		City city1 = new City("City1" );
		City city2 = new City("City2" );
		City city3 = new City("City3" );
		City city4 = new City("City4" );
		
		Country country1 = new Country("country1");
		Country country2 = new Country("country2");
		Country country3 = new Country("country3");
		Country country4 = new Country("country4");
		
		country1.addCity(city1);
		country1.addCity(city2);
		country1.addCity(city3);
		country1.addCity(city4);
		
		country2.addCity(city1);
		country2.addCity(city2);
		country2.addCity(city3);
		country2.addCity(city4);
		
		city1.setCountry(country1);
		city2.setCountry(country1);
		city3.setCountry(country1);
		city4.setCountry(country1);
		
		odb.store(country1);
		odb.store(country2);
		odb.store(country3);
		odb.store(country4);
		
		odb.commit();
		
		//odb = open(baseName);
		
		Country c1 = (Country) odb.getObjects(new CriteriaQuery(Country.class, Where.equal("name", "country1" ))).getFirst();
		odb.deleteCascade(c1);

		Country c2 = (Country) odb.getObjects(new CriteriaQuery(Country.class, Where.equal("name", "country2" ))).getFirst();
		odb.deleteCascade(c2);
		
		odb.close();
	}

}
