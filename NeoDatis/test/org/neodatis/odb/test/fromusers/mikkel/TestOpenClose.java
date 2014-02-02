package org.neodatis.odb.test.fromusers.mikkel;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;

public class TestOpenClose {
	public void test(int size) {
		long t1 = System.currentTimeMillis();
		long t2 = 0;
		for (int i = 0; i < size; i++) {
			
			String testString = "Test " + i + System.currentTimeMillis();
			ODB o = ODBFactory.open("unit-test-data/test.db");
			//System.out.println(i + " CREATING TEST " + testString);
			Object obj = new DatabaseTestObject(testString);
			o.store(obj);
			o.commit();
			o.close();
			if(i%100==0){
				t2 = System.currentTimeMillis();
				System.out.println("i="+ i + " = "+(t2-t1));
				t1=t2;
			}
		}
	}

	public static void main(String[] args) {
		TestOpenClose test = new TestOpenClose();
		test.test(100000);
	}
}
