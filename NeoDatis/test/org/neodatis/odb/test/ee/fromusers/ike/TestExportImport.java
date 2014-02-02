/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.ike;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.xml.XMLExporter;
import org.neodatis.odb.xml.XMLImporter;

/**
 * @author olivier
 *
 */
public class TestExportImport extends ODBTest{
	
	public void test1() throws Exception{
		
		if(!isLocal){
			return;
		}

		
		String baseName = getBaseName();
		System.out.println(baseName);
		Flock flock = new Flock();
		flock.getBirds().add(new Bird("b1", 1));
		flock.getBirds().add(new Bird("b2", 2));
		flock.getBirds().add(new Bird("b3", 3));
		
		ODB odb = open(baseName);
		
		odb.store(flock);
		odb.close();
		
		odb = open(baseName);
		XMLExporter exporter = new XMLExporter(odb);
		exporter.export(DIRECTORY, baseName+".xml");
		odb.close();
		
		odb = open(baseName+"2");
		XMLImporter importer = new XMLImporter(odb);
		importer.importFile(DIRECTORY, baseName+".xml");
		odb.close();
		
		odb = open(baseName+"2");
		Objects<Flock> flocks = odb.getObjects(Flock.class);
		assertEquals(1, flocks.size());
		assertEquals(3, flocks.getFirst().getBirds().size());
		odb.close();
		
	}
	public void test2() throws Exception{
		
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();
		System.out.println(baseName);
		Flock flock = new Flock();
		flock.getBirds().add(new Bird("b1", 1));
		flock.getBirds().add(new Bird("b2", 2));
		flock.getBirds().add(new Bird("b3", 3));
		
		ODB odb = open(baseName);
		
		odb.store(flock);
		odb.commit();

		XMLExporter exporter = new XMLExporter(odb);
		exporter.export(DIRECTORY, baseName+".xml");
		odb.close();
		
		odb = open(baseName+"2");
		XMLImporter importer = new XMLImporter(odb);
		importer.importFile(DIRECTORY, baseName+".xml");
		odb.close();
		
		odb = open(baseName+"2");
		Objects<Flock> flocks = odb.getObjects(Flock.class);
		assertEquals(1, flocks.size());
		assertEquals(3, flocks.getFirst().getBirds().size());
		odb.close();
		
	}

	public void test3() throws Exception{
		if(!isLocal){
			return;
		}

		boolean reconnect = OdbConfiguration.reconnectObjectsToSession();
		try{
			OdbConfiguration.setReconnectObjectsToSession(true);
			String baseName = getBaseName();
			System.out.println(baseName);
			Flock flock = new Flock();
			flock.getBirds().add(new Bird("b1", 1));
			flock.getBirds().add(new Bird("b2", 2));
			flock.getBirds().add(new Bird("b3", 3));
			
			ODB odb = open(baseName);
			
			odb.store(flock);
			odb.commit();

			XMLExporter exporter = new XMLExporter(odb);
			exporter.export(DIRECTORY, baseName+".xml");
			
			
			flock.getBirds().add(new Bird( "b4", 4));
			odb.store(flock);
			odb.close();
			
		}finally{
			// reset original value
			OdbConfiguration.setReconnectObjectsToSession(reconnect);
		}
		
	}

}
