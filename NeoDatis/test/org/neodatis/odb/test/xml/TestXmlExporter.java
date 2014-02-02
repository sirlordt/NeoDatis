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

import java.net.URLEncoder;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.arraycollectionmap.Dictionnary;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.odb.xml.XMLExporter;
import org.neodatis.odb.xml.XMLImporter;
import org.neodatis.tool.ConsoleLogger;

public class TestXmlExporter extends ODBTest {
	public void test1() throws Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		deleteBase("test-xml.neodatis");
		deleteBase("test-xml.xml");

		ODB odb = open("test-xml.neodatis");
		odb.store(new User("olivier ", "olivier@neodatis.com ", new Profile("my profile", new Function("inner function "))));
		odb.close();
		odb = open("test-xml.neodatis");
		XMLExporter exporter = new XMLExporter(odb);
		exporter.setExternalLogger(new ConsoleLogger());
		exporter.export(".", ODBTest.DIRECTORY + "test-xml.xml");

		odb.close();
		deleteBase("test-xml.neodatis");
		deleteBase("test-xml.xml");
	}

	public void test2() throws Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		deleteBase("test-xml-with-map.neodatis");
		deleteBase("test-xml-with-map.xml");

		ODB odb = open("test-xml-with-map.neodatis");
		Dictionnary dictionnary1 = new Dictionnary("test1");
		dictionnary1.addEntry("olivier", "Smadja");
		dictionnary1.addEntry("kiko", "vidal");
		dictionnary1.addEntry("karine", "galvao");

		odb.store(dictionnary1);
		odb.close();
		odb = open("test-xml-with-map.neodatis");
		XMLExporter exporter = new XMLExporter(odb);
		exporter.export(".", ODBTest.DIRECTORY + "test-xml-with-map.xml");
		odb.close();
		deleteBase("test-xml-with-map.neodatis");
		deleteBase("test-xml-with-map.xml");

	}

	/**
	 * to provoque erro while exporting a prohibted character in xml, < for
	 * example
	 * 
	 * @throws Exception
	 */
	public void testXmlEncoding() throws Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		String baseName = getBaseName();
		String xml = baseName + ".xml";
		ODB odb = open(baseName);
		odb.store(new Function(">&"));
		odb.close();

		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.export(".", ODBTest.DIRECTORY + xml);
		odb.close();

		// Tries to import the xml file
		ODB odb2 = open(baseName + ".imp");
		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(ODBTest.DIRECTORY, xml);
		odb2.close();

		String impBase = baseName + ".imp";
		odb2 = open(impBase);
		Objects objects = odb2.getObjects(Function.class);
		odb2.close();
		assertEquals(1, objects.size());

		deleteBase(baseName);
		deleteBase(xml);
		deleteBase(impBase);

	}

	/**
	 * < => &lt; > => &gt; & => &amp; " => &quot; ' => &apos;
	 */

	public void testXmlEncoding2() throws Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		String baseName = getBaseName();
		String xml = baseName + ".xml";
		ODB odb = open(baseName);
		odb.store(new Function(">"));
		odb.store(new Function("&"));
		odb.store(new Function("\""));
		odb.store(new Function("'"));
		odb.close();

		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.export(".", ODBTest.DIRECTORY + xml);
		odb.close();

		// Tries to import the xml file
		ODB odb2 = open(baseName + ".imp");
		XMLImporter importer = new XMLImporter(odb2);
		importer.importFile(ODBTest.DIRECTORY, xml);
		odb2.close();

		String impBase = baseName + ".imp";
		odb2 = open(impBase);
		Objects objects = odb2.getObjects(Function.class);
		odb2.close();
		assertEquals(4, objects.size());
		Function f1 = (Function) objects.next();
		Function f2 = (Function) objects.next();
		Function f3 = (Function) objects.next();
		Function f4 = (Function) objects.next();

		assertEquals(">", f1.getName());
		assertEquals("&", f2.getName());
		assertEquals("\"", f3.getName());
		assertEquals("'", f4.getName());
		deleteBase(baseName);
		// deleteBase(xml);
		deleteBase(impBase);

	}

	public void testXml() {
		String s = "<";
		String se = URLEncoder.encode(s);
		println(se);
		assertEquals("%3C", se);
	}

	

}
