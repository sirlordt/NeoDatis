package org.neodatis.tool.wrappers;


/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbClassUtil {
	public static boolean isEnum(Class clazz){
		return clazz.isEnum();
	}

	public static String getClassName(String fullClassName){
    	int index = fullClassName.lastIndexOf('.');
		if(index==-1){
			// no dot -> must be a primitive type
			return fullClassName;
		}
    	// get class name
		String className = fullClassName.substring(index+1,fullClassName.length());
		return className;
    }
    
	public static String getPackageName(String fullClassName){
    	int index = fullClassName.lastIndexOf('.');

		if(index==-1){
			// no dot -> must be a primitive type
			return "";
		}

    	// get package class name
		return fullClassName.substring(0,index);
    }
	public static String getFullName(Class aClass){
		return aClass.getName();
	}
}
