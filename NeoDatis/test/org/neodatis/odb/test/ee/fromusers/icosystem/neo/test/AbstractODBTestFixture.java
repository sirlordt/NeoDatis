package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import java.io.File;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;


public abstract class AbstractODBTestFixture extends ODBTest{

	/**
	 * Ensure Old State is Removed (Delete Database)
	 */
	public void tearDown(){
		try {
			super.tearDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public DAO createDAO(String fileName){
		ODB odb = ODBFactory.open(fileName);
		return new DAO(odb);
	}
}
