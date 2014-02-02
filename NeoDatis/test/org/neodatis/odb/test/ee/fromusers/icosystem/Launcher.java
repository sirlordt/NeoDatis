package org.neodatis.odb.test.ee.fromusers.icosystem;

import org.neodatis.odb.test.ODBTest;

public class Launcher extends ODBTest{

	public void test1(){
		//OdbConfiguration.setDebugEnabled(true);
		//LogUtil.enable(AbstractObjectWriter.LOG_ID_DEBUG);	
		
		ExampleDAONeoDatis dao = new ExampleDAONeoDatis();
		//Create Test Data
		dao.create();
		dao.create();
		dao.create();

		for(ExampleDTO dto : dao.get()){
			System.out.println(dto);
		}
		
		dao.shutdown();
		dao = new ExampleDAONeoDatis();
		dao.shutdown();
	}
}
