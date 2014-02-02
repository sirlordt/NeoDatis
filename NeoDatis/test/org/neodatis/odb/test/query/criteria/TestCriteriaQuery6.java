/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.test.query.criteria;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.criteria.EqualCriterion;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.tool.wrappers.OdbString;

public class TestCriteriaQuery6 extends ODBTest {

	public void test1() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		List<Profile> profiles = new ArrayList<Profile>();
		profiles.add(new Profile("p1",new Function("f1")));
		profiles.add(new Profile("p2",new Function("f2")));
		ClassB cb = new ClassB( "name" , profiles);
		
		odb.store(cb);
		odb.close();

		odb = open(baseName);

		// this object is not known y NeoDatis so the query will not return anything
		Profile p = new Profile("p1",(List)null);
		
		CriteriaQuery query = odb.criteriaQuery(ClassB.class, Where.contain("profiles", p));
		Objects<ClassB> l = odb.getObjects(query);
		odb.close();
		
		assertEquals(0, l.size());
	}
	
	public void test2() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		List<Profile> profiles = new ArrayList<Profile>();
		profiles.add(new Profile("p1",new Function("f1")));
		profiles.add(new Profile("p2",new Function("f2")));
		ClassB cb = new ClassB( "name" , profiles);
		
		odb.store(cb);
		odb.close();

		odb = open(baseName);

		Profile p = (Profile) odb.getObjects(Profile.class).getFirst();
		
		CriteriaQuery query = odb.criteriaQuery(ClassB.class, Where.contain("profiles", p));
		Objects<ClassB> l = odb.getObjects(query);
		odb.close();
		
		assertEquals(1, l.size());

	}
	
	public void testReuse() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		List<Profile> profiles = new ArrayList<Profile>();
		profiles.add(new Profile("p1",new Function("f1")));
		profiles.add(new Profile("p2",new Function("f2")));
		ClassB cb = new ClassB( "name" , profiles);
		
		odb.store(cb);
		odb.close();

		odb = open(baseName);

		Profile p = (Profile) odb.getObjects(Profile.class).getFirst();
		
		CriteriaQuery query = odb.criteriaQuery(ClassB.class, Where.equal("profiles", p));
		EqualCriterion ec = (EqualCriterion) query.getCriteria();
		try{
			Objects<ClassB> l = odb.getObjects(query);
		}catch (Exception e) {
			println(e.getMessage());
			String s= OdbString.exceptionToString(e,true);
			assertTrue(s.indexOf("1063")!=-1);
		}
		odb.close();
		
		

	}


}
