package org.neodatis.odb.test.ee.fromusers.icosystem.neo.example;

import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dao.DAO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ComplexDTO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.ParentDTO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SimpleDTO;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto.SubClassWithList;
import org.neodatis.odb.test.ee.fromusers.icosystem.neo.test.AbstractODBTestFixture;


public class TestMain extends AbstractODBTestFixture{

	public void testCreateWithChildren2(){
		String baseName = getBaseName();
		DAO dao = createDAO(baseName);
		dao.createComplex();
		assertEquals("Complex [id=1, objects=[]]", dao.getComplexes().get(0).toString());
		//Close + Start New Session
		dao.shutdown();
		dao = createDAO(baseName);

		ComplexDTO complex = dao.getComplex(1);

		ParentDTO parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class));
		SimpleDTO simple = dao.createSimple();
		SubClassWithList subClassWithList = dao.createSubClassWithList(int.class, java.lang.String.class);

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
		dao =createDAO(baseName);

		parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class, java.util.LinkedHashSet.class));
		//parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class, java.lang.String.class));
		simple = dao.createSimple();
		subClassWithList = dao.createSubClassWithList(int.class, java.lang.String.class);

		complex = dao.getComplex(1);
		complex.add(parent);
		complex.add(simple);
		complex.add(subClassWithList);
		complex.add(parent);
		dao.save(complex);
		
		assertEquals(1, dao.getComplexes().size());
		assertEquals(4, dao.getSubClassWithList().size());
		assertEquals(4, dao.getSimples().size());
		assertEquals(2, dao.getParents().size());

		System.err.println("CheckPoint 2");

		dao.shutdown();
		dao = createDAO(baseName);

		parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class, java.util.LinkedHashSet.class));
		parent = dao.createParent(dao.createSimple(), dao.createSubClassWithList(int.class, java.lang.String.class));
		simple = parent.getSimpleDTO();
		subClassWithList = dao.createSubClassWithList(int.class, java.lang.String.class);

		complex = dao.createComplex();
		complex.add(parent);
		complex.add(simple);
		complex.add(subClassWithList);
		complex.add(parent);
		dao.save(complex);
		
		complex = dao.createComplex();

		System.err.printf("Complex, Expected #[%d], Found [%d]\n",3, dao.getComplexes().size());
		System.err.printf("   SCWL, Expected #[%d], Found [%d]\n",6, dao.getSubClassWithList().size());
		System.err.printf(" Simple, Expected #[%d], Found [%d]\n",5, dao.getSimples().size());
		System.err.printf(" Parent, Expected #[%d], Found [%d]\n",3, dao.getParents().size());
		
		dao.shutdown();
		dao = createDAO(baseName);
		
		for(ComplexDTO cdto:dao.getComplexes()) {
			System.err.println(cdto);
		}
		
		System.err.println("All Done");
	}

	public static void main(String[] args){
		System.err.println("Starting");
		TestMain testMain=new TestMain();
		testMain.tearDown();
		testMain.testCreateWithChildren2();
	}
}
