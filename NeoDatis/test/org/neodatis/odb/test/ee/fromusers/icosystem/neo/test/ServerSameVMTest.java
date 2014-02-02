package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SimpleDTO;

public class ServerSameVMTest extends AbstractODBTestFixture{

	public void testServer(){
		String baseName = getBaseName();
		//Create Server
		ODBServer server = ODBFactory.openServer(8000);
		server.addBase(baseName, baseName);
		server.startServer(true);
		
		//Fetch Client
		ODB odb = server.openClient(baseName);
		//Instantiate DAO
		DAO dao = new DAO(odb);
		
		//Create Single Object
		dao.createSimple();
		
		List<SimpleDTO> list = dao.getSimples();
		assertEquals(1, list.size());
		//Details Persisted Correctly?
		assertEquals("Simple [1] Class [java.lang.String]", list.get(0).toString());
		
		dao.delete(SimpleDTO.class, 1);
		
		//Close + Start New Session
		dao.shutdown();
		dao = new DAO(server.openClient(baseName));
		
		list = dao.getSimples();
		//Object Deleted Correctly?
		assertEquals(0, list.size());
		
		dao.shutdown();
	}
}
