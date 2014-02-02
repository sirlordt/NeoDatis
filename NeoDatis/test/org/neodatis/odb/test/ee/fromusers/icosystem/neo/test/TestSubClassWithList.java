package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import java.util.List;

import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SubClassWithList;

public class TestSubClassWithList extends AbstractODBTestFixture{

	public void testCreatePoly(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createSubClassWithList(int.class);
		//Create Single Object
		List<SubClassWithList> list = dao.getSubClassWithList();
		assertEquals(1, list.size());
		//Details Persisted Correctly?
		assertEquals("SubClassWithList [Simple [1] Class [int]] clxList =[int]]", list.get(0).toString());
		
		assertEquals(1,dao.getPoly().size());
		
		dao.delete(SubClassWithList.class, 1);
		
		//Close + Start New Session
		dao.shutdown();
		dao = createDAO(baseName);
		
		list = dao.getSubClassWithList();
		//Object Deleted Correctly?
		assertEquals(0, list.size());
		
		dao.shutdown();
	}
	
	public void testSingleSaved(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createSubClassWithList(int.class,java.lang.String.class);
		//Create Single Object
		List<SubClassWithList> list = dao.getSubClassWithList();
		assertEquals(1, list.size());
		//Details Persisted Correctly?
		assertEquals("SubClassWithList [Simple [1] Class [int]] clxList =[int, class java.lang.String]]", list.get(0).toString());
		
		dao.save(new SubClassWithList(1));
		list = dao.getSubClassWithList();
		assertEquals(1, list.size());//Should Only have 1 Object with Updated Class Info
		assertEquals("SubClassWithList [Simple [1] Class [No Class]] clxList =[]]", list.get(0).toString());
		
		dao.delete(SubClassWithList.class, 1);
		
		dao.shutdown();
	}
	
	public void test2Saved(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createSubClassWithList(int.class,java.lang.String.class);
		//Create Single Object
		List<SubClassWithList> list = dao.getSubClassWithList();
		assertEquals(1, list.size());
		//Details Persisted Correctly?
		assertEquals("SubClassWithList [Simple [1] Class [int]] clxList =[int, class java.lang.String]]", list.get(0).toString());
		
		dao.createSubClassWithList(java.util.ArrayList.class);
		list = dao.getSubClassWithList();
		assertEquals(2, list.size());//Should Only have 1 Object with Updated Class Info
		assertEquals("SubClassWithList [Simple [1] Class [int]] clxList =[int, class java.lang.String]]", list.get(0).toString());
		assertEquals("SubClassWithList [Simple [2] Class [int]] clxList =[class java.util.ArrayList]]", list.get(1).toString());
		
		
		dao.delete(SubClassWithList.class, 1);
		dao.delete(SubClassWithList.class, 2);
		
		list = dao.getSubClassWithList();
		assertEquals(0, list.size());//Should Only have 1 Object with Updated Class Info
		
		dao.shutdown();
	}
}
