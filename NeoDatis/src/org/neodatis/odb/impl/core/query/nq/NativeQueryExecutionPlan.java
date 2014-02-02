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
package org.neodatis.odb.impl.core.query.nq;

import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.execution.IQueryExecutionPlan;
import org.neodatis.tool.wrappers.OdbTime;

/**
 * A simple Criteria execution plan
 * Check if the query can use index and tries to find the best index to be used
 * @author osmadja
 *
 */
public class NativeQueryExecutionPlan implements IQueryExecutionPlan {

	protected ClassInfo classInfo;
	protected boolean useIndex;
	protected ClassInfoIndex classInfoIndex;
	protected IQuery query;
	
	/** to keep track of the start date time of the plan*/
	protected long start;
	/** to keep track of the end date time of the plan*/
	protected long end;
    
	public NativeQueryExecutionPlan(ClassInfo classInfo, IQuery query){
		this.classInfo = classInfo;
		this.query = query;
		query.setExecutionPlan(this);
		init();
	}

	protected void init(){
        useIndex = false;
	}
	public ClassInfoIndex getIndex() {
		return classInfoIndex;
	}

	public boolean useIndex() {
		return useIndex;		
	}
	public String getDetails() {
		StringBuffer buffer = new StringBuffer();
		if (classInfoIndex == null) {
			buffer.append("No index used, Execution time=").append(getDuration()).append("ms");
			return buffer.toString();
		}
		return buffer.append("Following indexes have been used : ").append(classInfoIndex.getName()).append(", Execution time=").append(getDuration()).append("ms").toString();
	}
	
	public void end() {
		end = OdbTime.getCurrentTimeInMs();		
	}

	public long getDuration() {
		return (end-start);
	}

	public void start() {
		start = OdbTime.getCurrentTimeInMs();
		
	}

}
