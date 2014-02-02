package org.neodatis.odb.test.ee.fromusers.icosystem.neo.example;

import java.io.File;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SimpleDTO;
import org.neodatis.tool.wrappers.OdbThread;

public class ServerExampleSameVM{

	public static void cleanup(){
		File file = new File("/odb1");
		if(file.exists()){
			file.delete();
			System.err.println("Deleted Old Database");
		}
	}

	public static void main(String[] args){
		ODBTest test = new ODBTest();
		//Create Server
		ODBServer server = ODBFactory.openServer(8000);
		server.addBase("odb", "odb.neodatis");
		server.startServer(true);

		//Fetch Client
		ODB odb = server.openClient("odb");
		//Instantiate DAO
		DAO dao = new DAO(odb);

		//Create Single Object
		dao.createSimple();

		List<SimpleDTO> list = dao.getSimples();
		test.assertEquals(1, list.size());
		//Details Persisted Correctly?
		test.assertEquals("Simple [1] Class [java.lang.String]", list.get(0).toString());

		dao.delete(SimpleDTO.class, 1);

		//Close + Start New Session
		dao.shutdown();
		dao = new DAO(server.openClient("odb"));

		list = dao.getSimples();
		//Object Deleted Correctly?
		test.assertEquals(0, list.size());

		dao.shutdown();
	}

}
