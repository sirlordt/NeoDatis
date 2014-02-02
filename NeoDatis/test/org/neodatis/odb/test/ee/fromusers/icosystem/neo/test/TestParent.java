package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ParentDTO;

public class TestParent extends AbstractODBTestFixture{
	public void testSingleSaved(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		//ID == 3 since Children Created 1st
		dao.createParent(dao.createSimple(),dao.createSubClassWithList(int.class));
		
		//Create Single Object
		assertEquals(1, dao.getSubClassWithList().size());
		assertEquals(1, dao.getSimples().size());
		assertEquals(1, dao.getParents().size());
		//Details Persisted Correctly?
		assertEquals("ParentDTO [3] [simpleDTO=Simple [1] Class [java.lang.String], withSubClass=SubClassWithList [Simple [2] Class [int]] clxList =[int]]]", dao.getParents().get(0).toString());
		
		dao.save(new ParentDTO(3));
		
		assertEquals(1, dao.getParents().size());//Should Only have 1 Object with Updated Class Info
		assertEquals("ParentDTO [3] [simpleDTO=null, withSubClass=null]", dao.getParents().get(0).toString());
		
		dao.delete(ParentDTO.class, 3);
		//Orphans Not Deleted? Good for 'Shared' Objects, but a bit surprising
		assertEquals(1, dao.getSubClassWithList().size());
		assertEquals(1, dao.getSimples().size());
		
		dao.shutdown();
	}
	
	public void test2Saved(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		//ID == 3 since Children Created 1st
		ParentDTO dto1=dao.createParent(dao.createSimple(),dao.createSubClassWithList(int.class));
		long parent2Id=dao.getNextId();
		ParentDTO dto2=new ParentDTO(parent2Id);
		dao.save(dto2);
		dto2.setSimpleDTO(dto1.getSimpleDTO());
		dto2.setWithSubClass(dto1.getWithSubClass());
		
		assertEquals(1, dao.getSubClassWithList().size());
		assertEquals(1, dao.getSimples().size());
		assertEquals(2, dao.getParents().size());
		
		ParentDTO parent=dao.getParent(parent2Id);
		assertEquals(parent.getSimpleDTO(),dto2.getSimpleDTO());
		assertEquals(parent.getWithSubClass(),dto2.getWithSubClass());
		
		dao.shutdown();
	}
}
