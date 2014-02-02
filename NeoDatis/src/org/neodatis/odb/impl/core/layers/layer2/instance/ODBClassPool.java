/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.impl.core.layers.layer2.instance;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * A simple class pool, to optimize instance creation
 * 
 * @author osmadja
 * 
 */
public class ODBClassPool implements IClassPool {
	//neodatisee : synchronize the map
	private static Map<String,Class> classMap = new OdbHashMap<String, Class>();

	//neodatisee : synchronize the map
	private static Map<String,Constructor> construtorsMap = new OdbHashMap<String, Constructor>();
	
	//neodatisee : to resolve the problem : Class.forName("int") does not work
	private static Map<String, Class> nativeClasses = new HashMap<String, Class>();
	
	static{
		Class[] classes = new Class[]{void.class, boolean.class, byte.class, char.class, short.class, int.class, float.class, double.class, long.class};
		for(Class clazz:classes){
			nativeClasses.put(clazz.getName(), clazz);
		}
	}

	public void reset() {
		classMap.clear();
		construtorsMap.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer2.instance.IClassPool#getClass
	 * (java.lang.String)
	 */
	public synchronized Class getClass(String className) {
		Class clazz = classMap.get(className);
		if (clazz == null) {
			try {
				// clazz =
				// Thread.currentThread().getContextClassLoader().loadClass
				// (className);
				clazz = OdbConfiguration.getClassLoader().loadClass(className);
			} catch (Exception e) {
				
				// Check if it is a native class
				clazz = nativeClasses.get(className);
				
				if(clazz==null){
					throw new ODBRuntimeException(NeoDatisError.CLASS_POOL_CREATE_CLASS
							.addParameter(className), e);
				}				
			}
			classMap.put(className, clazz);
		}
		return clazz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer2.instance.IClassPool#getConstrutor
	 * (java.lang.String)
	 */
	public Constructor getConstructor(String className) {
		return construtorsMap.get(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer2.instance.IClassPool#addConstrutor
	 * (java.lang.String, java.lang.reflect.Constructor)
	 */
	public void addConstructor(String className, Constructor constructor) {
		construtorsMap.put(className, constructor);
	}
}
