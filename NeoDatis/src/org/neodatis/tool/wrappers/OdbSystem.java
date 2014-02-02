package org.neodatis.tool.wrappers;


/**
 * A wrapper to system class
 * @author olivier
 * @sharpen.ignore
 *
 */
public class OdbSystem {
	/**
	 * Retrieve a system property
	 * @param name The name of the property
	 * @return The property value
	 */
	public static String getProperty(String name){
		return System.getProperty(name);
	}

}
