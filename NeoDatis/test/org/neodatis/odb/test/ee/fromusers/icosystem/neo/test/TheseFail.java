package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ParentDTO;

public class TheseFail extends AbstractODBTestFixture{
	public void testCascadeDelete(){
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
		
		assertEquals(2,dao.getPoly().size());
		
		dao.deleteCascade(ParentDTO.class, 3);
		
		//Close + Start New Session
		dao.shutdown();
		dao = createDAO(baseName);
		//Object+Children Deleted Correctly?
		assertEquals(0, dao.getSubClassWithList().size());
		assertEquals(0, dao.getSimples().size());
		assertEquals(0, dao.getParents().size());
		
		dao.shutdown();
	}
}
