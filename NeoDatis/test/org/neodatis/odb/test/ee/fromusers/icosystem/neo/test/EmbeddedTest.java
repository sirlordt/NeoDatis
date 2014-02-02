package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SimpleDTO;

public class EmbeddedTest extends AbstractODBTestFixture{
	public void testEmbedded(){
		String baseName = getBaseName();
		//Fetch Client
		ODB odb = ODBFactory.open(baseName);
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
		dao = createDAO(baseName);

		list = dao.getSimples();
		//Object Deleted Correctly?
		assertEquals(0, list.size());

		dao.shutdown();
	}
}
