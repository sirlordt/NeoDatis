
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
package org.neodatis.odb.core.query.criteria;

import java.io.Serializable;

public class Operator implements Serializable{
	
	private String name;
	
	public final static Operator EQUAL = new Operator("=");
	public final static Operator CONTAIN = new Operator("contain");	
    public final static Operator LIKE = new Operator("like");
    public final static Operator CASE_INSENTIVE_LIKE = new Operator("ilike");
    public final static Operator GREATER_THAN = new Operator(">");
    public final static Operator GREATER_OR_EQUAL = new Operator(">=");
    public final static Operator LESS_THAN = new Operator("<");
    public final static Operator LESS_OR_EQUAL = new Operator("<=");
	
	protected Operator(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
    
    public String toString() {
        return name;
    }

	public boolean equals(Object obj) {
		if(!(obj instanceof Operator) || obj == null){
			return false;
		}
		Operator operator = (Operator) obj;
		return name.equals(operator.name);
	}
	
    
	

}
