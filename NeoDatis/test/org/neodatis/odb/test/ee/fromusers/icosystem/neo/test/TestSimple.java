package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import java.util.List;

import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SimpleDTO;

public class TestSimple extends AbstractODBTestFixture{

	public void testSimple(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createSimple();
		//Create Single Object
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

	public void testSingleSaved(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createSimple();
		//Create Single Object
		List<SimpleDTO> list = dao.getSimples();
		assertEquals(1, list.size());
		//Details Persisted Correctly?
		assertEquals("Simple [1] Class [java.lang.String]", list.get(0).toString());

		dao.save(new SimpleDTO(1));
		list = dao.getSimples();
		assertEquals(1, list.size());//Should Only have 1 Object with Updated Class Info
		assertEquals("Simple [1] Class [No Class]", list.get(0).toString());

		dao.delete(SimpleDTO.class, 1);

		dao.shutdown();
	}

	public void test2Saved(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createSimple();
		//Create Single Object
		List<SimpleDTO> list = dao.getSimples();
		assertEquals(1, list.size());
		//Details Persisted Correctly?
		assertEquals("Simple [1] Class [java.lang.String]", list.get(0).toString());

		dao.save(new SimpleDTO(2));
		list = dao.getSimples();
		assertEquals(2, list.size());//Should Only have 1 Object with Updated Class Info
		assertEquals("Simple [1] Class [java.lang.String]", list.get(0).toString());
		assertEquals("Simple [2] Class [No Class]", list.get(1).toString());

		dao.delete(SimpleDTO.class, 1);
		dao.delete(SimpleDTO.class, 2);

		list = dao.getSimples();
		assertEquals(0, list.size());//Should Only have 1 Object with Updated Class Info

		dao.shutdown();
	}
}
