
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
package org.neodatis.odb.impl.core.query.criteria;

import java.util.Map;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.query.AbstractQuery;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.execution.IQueryExecutionPlan;
import org.neodatis.tool.wrappers.OdbClassUtil;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;

public class CriteriaQuery extends AbstractQuery{
	private String fullClassName;
	private ICriterion criterion;
	
	
	public boolean hasCriteria(){
		return criterion!=null;
	}
	public boolean match(AbstractObjectInfo aoi){
		if(criterion==null){
		    return true;      
        }
        return criterion.match(aoi);
	}
	public boolean match(Map map){
		if(criterion==null){
		    return true;      
        }
        return criterion.match(map);
	}
	
    public CriteriaQuery(Class aClass,ICriterion criteria){
        this(OdbClassUtil.getFullName(aClass),criteria);
        
    }

    public CriteriaQuery(Class aClass){
    	this(OdbClassUtil.getFullName(aClass));
    }
    public CriteriaQuery(String aFullClassName){
    	super();
        this.fullClassName = aFullClassName;
        this.criterion = null;
    }
    public CriteriaQuery(String aFullClassName,ICriterion criteria){
    	super();
		this.fullClassName = aFullClassName;		
		if(criteria!=null){
			this.criterion = criteria;
			this.criterion.setQuery(this);
		}
	}
	public String getFullClassName() {
		return fullClassName;
	}

	public ICriterion getCriteria() {
		return criterion;
	}
	
    public String toString() {
        if(criterion==null){
            return "no criterion";
        }
        return criterion.toString();
    }
    
    public IOdbList<String> getAllInvolvedFields(){
        if(criterion==null){
            return new OdbArrayList<String>();
        }
    	return criterion.getAllInvolvedFields();
    }
	public void setCriterion(ICriterion criterion) {
		this.criterion = criterion;
	}
	public void setExecutionPlan(IQueryExecutionPlan plan) {
		executionPlan = plan;		
	}
	
}
