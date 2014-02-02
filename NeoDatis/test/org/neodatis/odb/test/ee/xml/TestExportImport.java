/**
 * 
 */
package org.neodatis.odb.test.ee.xml;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.enumeration.UserRole;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.xml.XMLExporter;
import org.neodatis.odb.xml.XMLImporter;
import org.neodatis.tool.ConsoleLogger;

/**
 * @author olivier
 * 
 */
public class TestExportImport extends ODBTest {
	public static void main(String[] args) throws Exception {
		
		OdbConfiguration.setCheckModelCompatibility(false);
		ODB odb = ODBFactory.open("/Users/olivier/Downloads/erroronneodatisimport/neodatis.odb.orig");
		XMLExporter exporter = new XMLExporter(odb);
		exporter.setExternalLogger(new ConsoleLogger());
		exporter.export("/Users/olivier/Downloads/erroronneodatisimport", "oo.xml");
		odb.close();
		
		
		odb = ODBFactory.open("/Users/olivier/Downloads/erroronneodatisimport/neodatis.odb.orig.2");
		XMLImporter importer = new XMLImporter(odb);
		importer.setExternalLogger(new ConsoleLogger());
		importer.importFile("/Users/olivier/Downloads/erroronneodatisimport", "oo.xml");
		odb.close();
		
	}

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new ClassWithObject("object", new Function()));
		odb.close();

		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.setExternalLogger(new ConsoleLogger());
		exporter.export("unit-test-data", baseName + ".xml");
		odb.close();

		odb = open(baseName + "2");
		XMLImporter importer = new XMLImporter(odb);
		importer.importFile("unit-test-data", baseName + ".xml");
		odb.close();

	}
	public void test2() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new ClassWithObject("", new Function()));
		odb.close();

		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.setExternalLogger(new ConsoleLogger());
		exporter.export("unit-test-data", baseName + ".xml");
		odb.close();

		odb = open(baseName + "2");
		XMLImporter importer = new XMLImporter(odb);
		importer.importFile("unit-test-data", baseName + ".xml");
		odb.close();

	}
	public void test3MapWithEnum() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new ClassWithMapWithEnum("test").add(UserRole.OPERATOR, new Function("f1")));
		odb.close();

		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.setExternalLogger(new ConsoleLogger());
		exporter.export("unit-test-data", baseName + ".xml");
		odb.close();

		odb = open(baseName + "2");
		XMLImporter importer = new XMLImporter(odb);
		importer.importFile("unit-test-data", baseName + ".xml");
		odb.close();
		odb = open(baseName + "2");
		ClassWithMapWithEnum o = (ClassWithMapWithEnum) odb.getObjects(ClassWithMapWithEnum.class).getFirst();
		odb.close();
		assertEquals("test", o.getName());
		assertEquals(UserRole.OPERATOR, o.getRoles().keySet().iterator().next());
		assertEquals("f1", o.getRoles().values().iterator().next().getName());

	}
}
