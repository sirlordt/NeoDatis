/**
 * 
 */
package org.neodatis.odb.test.ee2.joda;

import org.joda.time.DateTime;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestjodaTime extends ODBTest {
	public void test1(){
		String baseName = getBaseName();
		
		ODB odb = open(baseName);
		ClassWithJodaTime o1 = new ClassWithJodaTime(new DateTime(2009,12,01,1,1,1,1),"test"); 
		odb.store(o1);
		print(o1.getDateTime());
		odb.close();
		
		odb = open(baseName);
		Objects<ClassWithJodaTime> oos = odb.getObjects(ClassWithJodaTime.class);
		odb.close();
		assertEquals(1, oos.size());
		
		
		
		
	}

}
