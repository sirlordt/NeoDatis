package org.neodatis.odb.test.arraycollectionmap;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

public class TestArrayOfDouble extends ODBTest {
	public void test1(){
		System.out.println(System.getProperty("java.version"));
		String baseName = getBaseName();
		
		ODB odb = open(baseName);
		
		ClassWithArrayOfDouble o = new ClassWithArrayOfDouble("Object 1");
		o.setDouble(0, new Double(1));
		o.setDouble(1, new Double(2));
		o.setDouble(2, new Double(3));
		
		odb.store(o);
		odb.close();
		
		odb = open(baseName);
		Objects<ClassWithArrayOfDouble> oos = odb.getObjects(ClassWithArrayOfDouble.class);
		odb.close();
		assertEquals(1, oos.size());
		ClassWithArrayOfDouble o2 = oos.getFirst();
		for(int i=0;i<3;i++){
			assertEquals(new Double(i+1), o2.getDoubles()[i]);
		}
	}

}
