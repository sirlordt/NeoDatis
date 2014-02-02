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
package org.neodatis.odb.test.xml;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.enumeration.User;
import org.neodatis.odb.test.enumeration.UserRole;
import org.neodatis.odb.test.vo.arraycollectionmap.Dictionnary;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.xml.XMLExporter;
import org.neodatis.odb.xml.XMLImporter;
import org.neodatis.tool.ConsoleLogger;


public class TestXmlExporter2 extends ODBTest {

	public void testExportEnumToXml() throws Exception {
		if(!testNewFeature){
			return;
		}
		String baseName = getBaseName();
		System.out.println(baseName);
		ODB odb = open(baseName);
		long d = 1024*1024*1024;
		long l = Long.MAX_VALUE;
		long k = l / d;
		System.out.println(d);
		System.out.println(l);
		System.out.println(k);
		for (int i = 0; i < 1000; i++) {
			User user = new User(UserRole.SUPERVISOR, "supervisor" + i);
			odb.store(user);

			user = new User(UserRole.ADMINISTRATOR, "admin" + i);
			odb.store(user);

			user = new User(UserRole.OPERATOR, "operator" + i);
			odb.store(user);
		}
		odb.store(new Profile("p1", new Function("f1")));
		odb.close();

		odb = open(baseName);
		Objects<User> users = odb.getObjects(User.class, true);
		int i = 0;
		while (users.hasNext()) {
			User u = users.next();
			assertEquals("supervisor" + i, u.getName());
			assertEquals(UserRole.SUPERVISOR, u.getRole());

			u = users.next();
			assertEquals("admin" + i, u.getName());
			assertEquals(UserRole.ADMINISTRATOR, u.getRole());

			u = users.next();
			assertEquals("operator" + i, u.getName());
			assertEquals(UserRole.OPERATOR, u.getRole());
			i++;
		}
		odb.close();
		assertEquals(3 * 1000, users.size());
		
		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.export(ODBTest.DIRECTORY, baseName+".xml");
		odb.close();
		
		odb = open("imp_"+baseName);
		XMLImporter importer = new XMLImporter(odb);
		importer.importFile(ODBTest.DIRECTORY, baseName+".xml");
		
		odb.close();
		odb = open("imp_"+baseName);
		
		users = odb.getObjects(User.class, true);
		i = 0;
		while (users.hasNext()) {
			User u = users.next();
			assertEquals("supervisor" + i, u.getName());
			assertEquals(UserRole.SUPERVISOR, u.getRole());

			u = users.next();
			assertEquals("admin" + i, u.getName());
			assertEquals(UserRole.ADMINISTRATOR, u.getRole());

			u = users.next();
			assertEquals("operator" + i, u.getName());
			assertEquals(UserRole.OPERATOR, u.getRole());
			i++;
		}
		odb.close();
		assertEquals(3 * 1000, users.size());

	}
	
	/** @throws Exception 
	 * @neodatisv2 : test exporting a db with an object that points to a deleted object
	 * 
	 */
	public void testExportWithDeletedObject() throws Exception{
		
		if(!isLocal){
			return;
		}
		String baseName = getBaseName();
		Profile p = new Profile("p1");
		org.neodatis.odb.test.vo.login.User u = new org.neodatis.odb.test.vo.login.User("name", "email", p);
		
		ODB odb = open(baseName);
		OID oid = odb.store(u);
		odb.close();
		
		// open the db and deletes the profile
		odb = open(baseName);
		org.neodatis.odb.test.vo.login.User u2 = (org.neodatis.odb.test.vo.login.User) odb.getObjectFromId(oid);
		odb.delete(u2.getProfile());
		odb.close();
		
		// reopen and check the user to see if its profile is null now
		odb = open(baseName);
		org.neodatis.odb.test.vo.login.User u3 = (org.neodatis.odb.test.vo.login.User) odb.getObjectFromId(oid);
		assertNull(u3.getProfile());
		odb.close();
		
		// try to export to xml
		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		
		exporter.export(".", baseName+".xml");
		odb.close();
		
		
		
	}

	/** @throws Exception 
	 * @neodatisv2 : test exporting a db with an object that points to a deleted object in a map
	 * 
	 */
	public void testExportWithDeletedObjectInMap() throws Exception{
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();
		Profile p = new Profile("p1");
		Dictionnary dico = new Dictionnary("dico1");
		dico.addEntry("profile", p);
		ODB odb = open(baseName);
		OID oid = odb.store(dico);
		odb.close();
		
		// open the db and deletes the profile
		odb = open(baseName);
		Dictionnary dico2 = (Dictionnary) odb.getObjectFromId(oid);
		Profile p2 = (Profile) dico2.get("profile");
		odb.delete(p2);
		odb.close();
		
		// reopen and check the user to see if its profile is null now
		odb = open(baseName);
		Dictionnary dico3 = (Dictionnary) odb.getObjectFromId(oid);
		assertNull(dico3.get("profile"));
		odb.close();
		
		// try to export to xml
		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		
		exporter.export(".", baseName+".xml");
		odb.close();
		
		
		
	}

}
