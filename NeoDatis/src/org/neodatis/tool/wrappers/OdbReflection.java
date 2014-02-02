package org.neodatis.tool.wrappers;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbReflection {
	public static int getArrayLength(Object array){
		return Array.getLength(array);
	}
	public static Object getArrayElement(Object array, int index){
		return Array.get(array, index);
	}
	
	public static Method[] getMethods(Class clazz){
		return clazz.getDeclaredMethods();
	}

	public static Class[] getAttributeTypes(Method method){
		return method.getParameterTypes();
	}

}
