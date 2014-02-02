package org.neodatis.odb.test.ee.fromusers.icosystem.neo.test;

import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ComplexDTO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.Entity;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ParentDTO;

public class TestComplex extends AbstractODBTestFixture{

	public void testCreate(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createComplex();
		assertEquals("Complex [id=1, objects=[]]", dao.getComplexes().get(0).toString());
		//Close + Start New Session
		dao.shutdown();
		dao = createDAO(baseName);
		//Object+Children Deleted Correctly?
		assertEquals(1, dao.getComplexes().size());
		assertEquals(0, dao.getSubClassWithList().size());
		assertEquals(0, dao.getSimples().size());
		assertEquals(0, dao.getParents().size());

		dao.delete(ComplexDTO.class, 1);

		dao.shutdown();
	}

	public void testCreateWithChildren(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createComplex();
		assertEquals("Complex [id=1, objects=[]]", dao.getComplexes().get(0).toString());
		//Close + Start New Session
		dao.shutdown();
		dao = createDAO(baseName);

		ComplexDTO complex = dao.getComplex(1);

		Entity parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class));
		Entity simple = dao.createSimple();
		Entity subClassWithList = dao.createSubClassWithList(int.class, java.lang.String.class);

		complex.add(parent);
		complex.add(simple);
		complex.add(subClassWithList);

		dao.save(complex);

		assertEquals(1, dao.getComplexes().size());
		assertEquals(2, dao.getSubClassWithList().size());
		assertEquals(2, dao.getSimples().size());
		assertEquals(1, dao.getParents().size());

		dao.shutdown();
	}

	public void testCreateWithChildren2(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createComplex();
		assertEquals("Complex [id=1, objects=[]]", dao.getComplexes().get(0).toString());
		//Close + Start New Session
		dao.shutdown();
		dao = createDAO(baseName);

		ComplexDTO complex = dao.getComplex(1);

		Entity parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class));
		Entity simple = dao.createSimple();
		Entity subClassWithList = dao.createSubClassWithList(int.class, java.lang.String.class);

		complex.add(parent);
		complex.add(simple);
		complex.add(subClassWithList);
		complex.add(parent);

		dao.save(complex);

		assertEquals(1, dao.getComplexes().size());
		assertEquals(2, dao.getSubClassWithList().size());
		assertEquals(2, dao.getSimples().size());
		assertEquals(1, dao.getParents().size());

		System.err.println("CheckPoint 1");

		dao.shutdown();
		dao = createDAO(baseName);

		parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class, java.util.LinkedHashSet.class));
		simple = dao.createSimple();
		subClassWithList = dao.createSubClassWithList(int.class, java.lang.String.class);

		complex = dao.createComplex();
		complex.add(parent);
		complex.add(simple);
		complex.add(subClassWithList);
		complex.add(parent);

		dao.shutdown();
		dao = createDAO(baseName);

		assertEquals(2, dao.getComplexes().size());
		assertEquals(4, dao.getSubClassWithList().size());
		assertEquals(4, dao.getSimples().size());
		assertEquals(2, dao.getParents().size());
	}

	public void testSingleSaved(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		//ID == 3 since Children Created 1st
		dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class));

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
		ParentDTO dto1 = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class));
		ParentDTO dto2 = new ParentDTO(dao.getNextId());
		dto2.setSimpleDTO(dto1.getSimpleDTO());
		dto2.setWithSubClass(dto1.getWithSubClass());
		dao.save(dto2);

		assertEquals(1, dao.getSubClassWithList().size());
		assertEquals(1, dao.getSimples().size());
		assertEquals(2, dao.getParents().size());

		dao.shutdown();
	}
}
