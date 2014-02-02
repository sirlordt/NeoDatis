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
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.xml.XMLImporter;

public class XmlImporterFrom1_8 extends ODBTest {
	public static final String NAME1 = "test-xml.neodatis";
	public static final String NAME2 = "test-xml-imp.neodatis";
	public static final String XML_NAME = "test-xml.xml";
	private static final int SIZE = 1000;

	public void test0() throws Exception {

		if (!isLocal)
			return;

		String baseName = getBaseName();
		println("base name = " + baseName);
		OdbConfiguration.setDatabaseCharacterEncoding("UTF-8");
		ODB odb = open(baseName);
		XMLImporter importer = new XMLImporter(odb);
		importer.importFile("test", "import-from-1.8.xml");
		odb.close();
	}
}
