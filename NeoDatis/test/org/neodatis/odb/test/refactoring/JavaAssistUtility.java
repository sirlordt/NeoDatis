package org.neodatis.odb.test.refactoring;

import java.io.IOException;
import java.util.Date;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class JavaAssistUtility {

	Class createClass(String name, String[] fieldNames, Class[] fieldTypes) throws CannotCompileException, NotFoundException, IOException {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass(name);
		cc.stopPruning(true);
		for (int i = 0; i < fieldNames.length; i++) {
			cc.addField(new CtField(pool.get(fieldTypes[i].getName()), fieldNames[i], cc));
		}
		Class c = cc.toClass();
		cc.writeFile();
		return c;
	}

	Class updateClass(String name, String[] fieldNames, Class[] fieldTypes) throws CannotCompileException, NotFoundException, IOException {
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get(name);
		cc.stopPruning(true);
		cc.defrost();
		CtField[] fields = cc.getFields();
		for (int i = 0; i < fields.length; i++) {
			cc.removeField(fields[i]);
		}
		cc.stopPruning(true);
		for (int i = 0; i < fieldNames.length; i++) {
			cc.addField(new CtField(pool.get(fieldTypes[i].getName()), fieldNames[i], cc));
		}
		Class c = cc.toClass();
		return c;
	}

	public static String getClassDescription(Class aClass) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Class name=").append(aClass.getName()).append("\n");

		for (int i = 0; i < aClass.getDeclaredFields().length; i++) {
			buffer.append(i + 1).append(":").append(
					aClass.getDeclaredFields()[i].getName() + " : " + aClass.getDeclaredFields()[i].getType().getName()).append("\n");
		}

		return buffer.toString();
	}

	public static void main(String[] args) throws CannotCompileException, NotFoundException, IOException {
		JavaAssistUtility jau = new JavaAssistUtility();
		String className = "Test3";
		String[] fieldNames = { "field1", "field2" };
		Class[] fieldTypes = { String.class, Integer.TYPE };
		Class c = jau.createClass(className, fieldNames, fieldTypes);
		System.out.println(getClassDescription(c));
		String[] fieldNames2 = { "field1", "field2", "field3" };
		Class[] fieldTypes2 = { String.class, Integer.TYPE, Date.class };
		Class c2 = jau.updateClass(className, fieldNames2, fieldTypes2);

		System.out.println(getClassDescription(c));
	}

}
