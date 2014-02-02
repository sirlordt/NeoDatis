/**
 * 
 */
package org.neodatis.odb.test.locale;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Locale;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.attribute.TestClass;

/**
 * @author olivier
 *
 */
public class TestLocale extends ODBTest {
	public void test1() throws UnsupportedEncodingException{
		
		ODB odb = null;
		String baseName = getBaseName();
		Locale defaultLocale = Locale.getDefault(); 
		String encoding = OdbConfiguration.getDatabaseCharacterEncoding();
		try{
			odb = open(baseName);
			OdbConfiguration.setLatinDatabaseCharacterEncoding();
			 
			Locale brLocale = new Locale("pt","BR");
			Locale.setDefault(brLocale);
			TestClass tc = new TestClass();
			tc.setBigDecimal1(new BigDecimal(5.3));
			println(tc.getBigDecimal1().toString());
			
			odb.store(tc);
			odb.close();
			
			odb = open(baseName);
			Objects<TestClass> objects = odb.getObjects(TestClass.class);
			assertEquals(1, objects.size());
			
			assertEquals(new BigDecimal(5.3), objects.getFirst().getBigDecimal1());
			
		}finally{
			if(odb!=null && !odb.isClosed()){
				odb.close();
			}
			OdbConfiguration.setDatabaseCharacterEncoding(encoding);
			Locale.setDefault(defaultLocale);
		}
	}
	
	public void test2() throws UnsupportedEncodingException{
		
		ODB odb = null;
		String baseName = getBaseName();
		Locale defaultLocale = Locale.getDefault(); 
		String encoding = OdbConfiguration.getDatabaseCharacterEncoding();
		try{
			odb = open(baseName);
			OdbConfiguration.setLatinDatabaseCharacterEncoding();
			 
			Locale brLocale = new Locale("pt","BR");
			Locale.setDefault(brLocale);
			TestClass tc = new TestClass();
			tc.setBigDecimal1(new BigDecimal("5.3"));
			println(tc.getBigDecimal1().toString());
			
			odb.store(tc);
			odb.close();
			
			odb = open(baseName);
			Objects<TestClass> objects = odb.getObjects(TestClass.class);
			assertEquals(1, objects.size());
			
			assertEquals(new BigDecimal("5.3"), objects.getFirst().getBigDecimal1());
			
		}finally{
			if(odb!=null && !odb.isClosed()){
				odb.close();
			}
			OdbConfiguration.setDatabaseCharacterEncoding(encoding);
			Locale.setDefault(defaultLocale);
		}
	}

}
