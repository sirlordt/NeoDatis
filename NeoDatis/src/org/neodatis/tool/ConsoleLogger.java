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

import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.tool.wrappers.OdbString;

public class ConsoleLogger implements ILogger {
	private int i;
	private IStorageEngine engine;

	public ConsoleLogger(IStorageEngine engine) {
		this.engine = engine;
		i = 0;
	}

	public ConsoleLogger() {
		i = 0;
	}

	public void debug(Object o) {
		System.out.println(o);
	}

	public void error(Object o) {
		String header = "An internal error occured,please email the error stack trace displayed below to odb.support@neodatis.org";
		System.out.println(header);
		System.out.println(o);
	}

	public void error(Object o, Throwable throwable) {
		String header = "An internal error occured,please email the error stack trace displayed below to odb.support@neodatis.org";
		System.out.println(header);
		System.out.println(o);
		System.out.println(OdbString.exceptionToString(throwable, false));
	}

	public void info(Object o) {
		if (i % 20 == 0) {
			if (engine != null) {
				System.out.println(engine.getSession(true).getCache().toString());
			}
		}
		System.out.println(o);
		i++;
	}

}
