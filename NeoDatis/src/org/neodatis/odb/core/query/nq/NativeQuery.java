
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
package org.neodatis.odb.core.query.nq;

import org.neodatis.odb.core.query.AbstractQuery;
import org.neodatis.odb.core.query.execution.IQueryExecutionPlan;

public abstract class NativeQuery extends AbstractQuery{

	abstract public boolean match(Object object);

	abstract public Class getObjectType();
    
	public Class[] getObjectTypes(){
		Class [] classes = new Class[1];
		classes[0] = getObjectType();
		return classes;
	}

	public String[] getIndexFields(){
		return new String[0];
	}
	public void setExecutionPlan(IQueryExecutionPlan plan) {
		executionPlan = plan;		
	}

}
