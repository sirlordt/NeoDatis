/**
 * 
 */
package org.neodatis.odb.test.other;

import org.neodatis.odb.ODB;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestGetDatabaseName extends ODBTest {
	
	public void test1(){
		String baseName = "name.neodatis";
		ODB odb = open(baseName);
		IStorageEngine engine = Dummy.getEngine(odb);
		String s = engine.getBaseIdentification().getIdentification();
		if(isLocal){
			assertEquals(baseName, s);
		}else{
			assertEquals("unit-test-data/name.neodatis@127.0.0.1:13000", s);
			
		}
		deleteBase(baseName);
		
	}
	public void test2(){
		String baseName = "name2.neodatis";
		ODB odb = open(baseName);
		String s = odb.getName();
		if(isLocal){
			assertEquals(baseName, s);
		}else{
			assertEquals("unit-test-data/name2.neodatis@127.0.0.1:13000", s);
			
		}
		deleteBase(baseName);
	}

}
