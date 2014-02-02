package org.neodatis.odb.test.ee.utf8;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestEncodingPerformance extends ODBTest{
	public void t1estUTF8() throws UnsupportedEncodingException{
		String encoding = "UTF-8";
		String s = "NeoDatis Object Database is cool! éàíú";
		int size = 10000000;
		
		long start = System.currentTimeMillis();
		for(int i=0;i<size;i++){
			s.getBytes(encoding);
		}
		long end = System.currentTimeMillis();
		System.out.println("utf8=" + (end-start));
	}
	public void t1estISO8859() throws UnsupportedEncodingException{
		String encoding = "ISO8859-1";
		String s = "NeoDatis Object Database is cool! éàíúããããã";
		int size = 10000000;
		
		long start = System.currentTimeMillis();
		for(int i=0;i<size;i++){
			s.getBytes(encoding);
		}
		long end = System.currentTimeMillis();
		System.out.println("ISO8859=" + (end-start));
	}
	
	public void testUtf8() throws UnsupportedEncodingException{
		
		String s = "My name is hfdkjhkjfhkjdãããããíáéèâdãããããíáéèâdãããããíáéèâdãããããíáéèâdãããããíáéèâdãããããíáéèâdãããããíáéèâdãããããíáéèâdãããããíáéèâ";
		
		String baseName = getBaseName();
		System.out.println(baseName);
		OdbConfiguration.setDatabaseCharacterEncoding("UTF-8");
		long startUtf8  =System.currentTimeMillis();
		ODB odb = open(baseName+"utf8");
		for(int i=0;i<500;i++){
			odb.store(new Function(s+i));
		}
		odb.close();
		long endUtf8  =System.currentTimeMillis();
		
		long startIso  =System.currentTimeMillis();
		OdbConfiguration.setDatabaseCharacterEncoding("ISO8859-1");
		odb = open(baseName+"iso8859");
		for(int i=0;i<500;i++){
			odb.store(new Function(s+i));
		}
		odb.close();
		long endiso  =System.currentTimeMillis();
		
		File fileUtf8 = new File(ODBTest.DIRECTORY+ baseName+"utf8");
		File fileIso = new File(ODBTest.DIRECTORY+baseName+"iso8859");
		
		println("Time for utf8 = "+(endUtf8-startUtf8) + "   / file size = "+   fileUtf8.length());
		println("Time for iso = "+(endiso-startIso) + "   / file size = "+   fileIso.length());
		
		
	}
	public static void main(String[] args) throws UnsupportedEncodingException {
		TestEncodingPerformance t = new TestEncodingPerformance();
		t.t1estISO8859();
		t.t1estUTF8();
	}
}
