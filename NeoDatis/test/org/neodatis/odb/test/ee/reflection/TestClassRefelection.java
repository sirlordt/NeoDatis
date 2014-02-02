/**
 * 
 */
package org.neodatis.odb.test.ee.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestClassRefelection extends ODBTest{
	public void testLinkedHashSet() throws Exception{
		Class clazz = Class.class;
		
		Field nameField = clazz.getDeclaredField("name");
		nameField.setAccessible(true);
		String name = (String) nameField.get(LinkedHashSet.class);
		System.out.println(name);
		assertNotNull(name);
	}
	public void testArrayList() throws Exception{
		// Get thse class class
		Class clazz = Class.class;
		// Get the field 'name' (transient)
		Field nameField = clazz.getDeclaredField("name");
		nameField.setAccessible(true);
		
		// Get the arrayList class
		Class class2 = ArrayList.class;
		
		// Get the value of the field name by reflection
		String name = (String) nameField.get(class2);
		
		// Check name is not null
		assertNotNull(name);
	}
	public void testArrayList2() throws Exception{
		// Get thse class class
		Class clazz = Class.class;
		// Get the field 'name' (transient)
		Field nameField = clazz.getDeclaredField("name");
		nameField.setAccessible(true);
		
		// Get the arrayList class
		Class class2 = ArrayList.class;
		
		class2.getName();
		
		// Get the value of the field name by reflection
		String name = (String) nameField.get(class2);
		
		// Check name is not null
		assertNotNull(name);
	}

}
