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
package org.neodatis.odb.test.server.trigger.autoincrement;

import java.util.ArrayList;
import java.util.List;

public class ProfileWithId extends ClassWithId{
	private String name;

	private List<FunctionWithId> functions;

	public ProfileWithId() {
	}

	public ProfileWithId(String name) {
		super();
		this.name = name;
	}

	public ProfileWithId(String name, List<FunctionWithId> functions) {
		super();
		this.functions = functions;
		this.name = name;
	}

	public ProfileWithId(String name, FunctionWithId function) {
		super();
		this.functions = new ArrayList<FunctionWithId>();
		this.functions.add(function);
		this.name = name;
	}

	public void addFunction(FunctionWithId function) {
		if (functions == null) {
			functions = new ArrayList();
		}
		functions.add(function);
	}

	public List<FunctionWithId> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FunctionWithId> functions) {
		this.functions = functions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name + " - " + (functions != null ? functions.toString() : "null");
	}

	public boolean equals2(Object obj) {
		if (obj == null || obj.getClass() != ProfileWithId.class) {
			return false;
		}
		ProfileWithId p = (ProfileWithId) obj;
		if (name == null && p.name != null) {
			return false;
		}
		return (name == null && p.name == null) || (name.equals(p.name));
	}

}
