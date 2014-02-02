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
package org.neodatis.tool.mutex;

import java.util.Map;

import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 *  A mutex factory
 * @author osmadja
 *
 */
public class MutexFactory {
	private static Map<String,Mutex> mutexs = new OdbHashMap<String, Mutex>();
	private static boolean debug = false;
	
	public static synchronized Mutex get(String name){
		Mutex mutex = mutexs.get(name);
		if(mutex==null){
			mutex = new Mutex(name);
			mutex.setDebug(debug);
			mutexs.put(name, mutex);
		}
		return mutex;
	}
	public static void setDebug(boolean debugValue){
		debug = debugValue;
	}

}
