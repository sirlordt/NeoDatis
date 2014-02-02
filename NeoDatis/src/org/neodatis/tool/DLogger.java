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
package org.neodatis.tool;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.tool.wrappers.OdbString;

/**
 * Simple logging class
 * <p>
 * 
 * </p>
 * 
 */
public class DLogger {

	private static List<ILogger> iloggers = new ArrayList<ILogger>();

	public static void register(ILogger logger) {
		iloggers.add(logger);
	}

	public static void debug(Object object) {

		System.out.println(object == null ? "null" : object.toString());
		for (int i = 0; i < iloggers.size(); i++) {
			iloggers.get(i).debug(object);
		}

	}

	public static void info(Object object) {
		System.out.println(object == null ? "null" : object.toString());
		for (int i = 0; i < iloggers.size(); i++) {
			iloggers.get(i).info(object);
		}
	}

	/**
	 * @param object
	 *            The object to be logged
	 */
	public static void error(Object object) {
		System.out.println(object == null ? "null" : object.toString());
		for (int i = 0; i < iloggers.size(); i++) {
			iloggers.get(i).error(object);
		}
	}

	public static void error(Object object, Throwable t) {
		System.out.println(object == null ? "null" : object.toString());
		System.out.println(OdbString.exceptionToString(t, false));
		for (int i = 0; i < iloggers.size(); i++) {
			iloggers.get(i).error(object, t);
		}
	}
}
