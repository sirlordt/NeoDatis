package org.neodatis.odb.test.tmp;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.xml.XMLImporter;
import org.neodatis.tool.IOUtil;

public class TestMikkel {

	public static void main(String[] args) throws Exception {
		ODB odb = ODBFactory.open("d:/download/boxing.db", "username", "password");
		IOUtil.deleteFile("import.neodatis");
		odb = ODBFactory.open("import.neodatis");

		XMLImporter importer = new XMLImporter(odb);
		importer.importFile("f:/tmp", "db2.xml");
		odb.close();
	}

}
