/**
 * 
 */
package org.neodatis.odb.test.ee.insert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestInserting extends ODBTest {

	public void testHelpersWithAllUpdate() {
		String baseName = getBaseName();
		int size = 100;
		for(int i=0;i<size;i++){
			internalTestHelpersWithAll(baseName);
		}
	}
	
	public void internalTestHelpersWithAll(String baseName) {
		println(baseName);
		ODB odb = open(baseName);
		Objects<ClassWithClass> cc = odb.getObjects(ClassWithClass.class);
		int initialCount = cc.size();
		List<Class> classes = new ArrayList<Class>();
		classes.add(String.class);
		classes.add(Long.class);
		classes.add(ArrayList.class);
		classes.add(BigDecimal.class);
		classes.add(BigInteger.class);
		classes.add(ClassWithClass.class);
		classes.add(Function.class);
		classes.add(JFrame.class);
		classes.add(URI.class);
		classes.add(HashMap.class);
		classes.add(Short.class);
		classes.add(Byte.class);
		classes.add(Date.class);
		classes.add(java.util.Date.class);
		classes.add(Time.class);
		classes.add(Timestamp.class);
		classes.add(JPanel.class);
		classes.add(ODBTest.class);
		classes.add(LinkedHashSet.class);
		classes.add(int.class);
		classes.add(byte.class);
		classes.add(long.class);
		classes.add(float.class);
		classes.add(double.class);
		classes.add(short.class);
		classes.add(char.class);
		classes.add(boolean.class);
		
		for(Class clazz:classes){
			ClassWithClass cwc = new ClassWithClass(clazz.getName(),clazz);
			odb.store(cwc);
		}

		odb.close();
		
		odb = open(baseName);
		cc = odb.getObjects(ClassWithClass.class);
		assertEquals(classes.size()+initialCount, cc.size());
		// now update every class to string
		for(ClassWithClass cwc:cc){
			cwc.setClazz(String.class);
			odb.store(cwc);
		}

		odb.close();
		odb = open(baseName);
		cc = odb.getObjects(ClassWithClass.class);
		assertEquals(classes.size()+initialCount, cc.size());
		// now update every class to string
		for(ClassWithClass cwc:cc){
			assertEquals(String.class, cwc.getClazz());
		}
		odb.close();
	}
	public void testHelpersWithAll() {
		String baseName = getBaseName();
		println(baseName);
		ODB odb = open(baseName);
		Objects<ClassWithClass> cc = odb.getObjects(ClassWithClass.class);
		int initialCount = cc.size();
		List<Class> classes = new ArrayList<Class>();
		classes.add(String.class);
		classes.add(Long.class);
		classes.add(ArrayList.class);
		classes.add(BigDecimal.class);
		classes.add(BigInteger.class);
		classes.add(ClassWithClass.class);
		classes.add(Function.class);
		classes.add(JFrame.class);
		classes.add(URI.class);
		classes.add(HashMap.class);
		classes.add(Short.class);
		classes.add(Byte.class);
		classes.add(Date.class);
		classes.add(java.util.Date.class);
		classes.add(Time.class);
		classes.add(Timestamp.class);
		classes.add(JPanel.class);
		classes.add(ODBTest.class);
		classes.add(int.class);
		classes.add(byte.class);
		classes.add(long.class);
		classes.add(float.class);
		classes.add(double.class);
		classes.add(short.class);
		classes.add(char.class);
		classes.add(boolean.class);

		
		for(Class clazz:classes){
			ClassWithClass cwc = new ClassWithClass(clazz.getName(),clazz);
			odb.store(cwc);
		}

		odb.close();
		
		odb = open(baseName);
		cc = odb.getObjects(ClassWithClass.class);
		assertEquals(classes.size()+initialCount, cc.size());
		for(Class clazz:classes){
			assertEquals(clazz, cc.next().getClazz());
		}

		odb.close();

		
	}
	
	public void testHelpersWithString() {
		String baseName = getBaseName();
		println(baseName);

		ClassWithClass cwc = new ClassWithClass("string", String.class);
		ODB odb = open(baseName);
		odb.store(cwc);
		odb.close();

		odb = open(baseName);
		Objects<ClassWithClass> cc = odb.getObjects(ClassWithClass.class);
		cwc = cc.getFirst();
		odb.close();
		assertEquals(String.class, cwc.getClazz());
		assertEquals(1, cc.size());

	}

	public void testHelpersWithLong() {
		String baseName = getBaseName();
		println(baseName);

		ClassWithClass cwc = new ClassWithClass("long", Long.class);
		ODB odb = open(baseName);
		odb.store(cwc);
		odb.close();

		odb = open(baseName);
		Objects<ClassWithClass> cc = odb.getObjects(ClassWithClass.class);
		cwc = cc.getFirst();
		odb.close();
		assertEquals(Long.class, cwc.getClazz());
		assertEquals(1, cc.size());

	}
	
	public void testHelpersWithNativeInt() {
		String baseName = getBaseName();
		println(baseName);

		ClassWithClass cwc = new ClassWithClass("native int", int.class);
		println("int native class is " + int.class.getName());
		Class clazz = int.class;
		ODB odb = open(baseName);
		odb.store(cwc);
		odb.close();

		odb = open(baseName);
		Objects<ClassWithClass> cc = odb.getObjects(ClassWithClass.class);
		cwc = cc.getFirst();
		odb.close();
		assertEquals(int.class, cwc.getClazz());
		assertEquals(1, cc.size());

	}
	
	public void testHelpersWithArrayList() {
		String baseName = getBaseName();
		println(baseName);

		ClassWithClass cwc = new ClassWithClass("arrayList", ArrayList.class);
		ODB odb = open(baseName);
		odb.store(cwc);
		odb.close();

		odb = open(baseName);
		Objects<ClassWithClass> cc = odb.getObjects(ClassWithClass.class);
		cwc = cc.getFirst();
		odb.close();
		assertEquals(ArrayList.class, cwc.getClazz());
		assertEquals(1, cc.size());

	}
	public void testNumber() {
		String baseName = getBaseName();

		ClassWithNumber cwn = new ClassWithNumber(1,"name");
		ODB odb = open(baseName);
		odb.store(cwn);
		odb.close();

		odb = open(baseName);
		Objects<ClassWithNumber> cc = odb.getObjects(ClassWithNumber.class);
		ClassWithNumber cwn2 = cc.getFirst();
		odb.close();
		assertEquals(cwn.getN(),cwn2.getN());
		assertEquals(cwn.getName() , cwn2.getName());
	}
	

	public static void main(String[] args) {
	}

}
