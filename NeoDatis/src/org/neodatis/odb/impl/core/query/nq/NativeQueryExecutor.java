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

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.QueryManager;
import org.neodatis.odb.core.query.execution.GenericQueryExecutor;
import org.neodatis.odb.core.query.execution.IQueryExecutionPlan;
import org.neodatis.odb.core.query.execution.IndexTool;
import org.neodatis.odb.core.query.nq.NativeQuery;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;

public class NativeQueryExecutor extends GenericQueryExecutor {

    private Object currentObject;
    private IInstanceBuilder instanceBuilder;
    private NativeQueryManager manager;
    
    public NativeQueryExecutor(IQuery query, IStorageEngine engine, IInstanceBuilder instanceBuilder) {
        super(query, engine);
        this.instanceBuilder = instanceBuilder;
        this.manager = new NativeQueryManager();
    }

    public IQueryExecutionPlan getExecutionPlan() {
        IQueryExecutionPlan plan = new NativeQueryExecutionPlan(classInfo,query);
        return plan;
    }

    public void prepareQuery() {
    }
    
    /**
     * Check if the object at position currentPosition matches the query, returns true
     * 
     * This method must compute the next object position and the orderBy key if it exists!
     */
    public boolean matchObjectWithOid(OID oid, boolean loadObjectInfo, boolean inMemory)  {
        AbstractObjectInfo aoitemp = objectReader.readNonNativeObjectInfoFromOid(classInfo, oid, true, true);
        boolean objectMatches = false;
        if (!aoitemp.isDeletedObject()) {
            currentNnoi = (NonNativeObjectInfo) aoitemp;
            currentObject = instanceBuilder.buildOneInstance(currentNnoi);
            objectMatches = query == null || manager.match(query, currentObject);
            nextOID = currentNnoi.getNextObjectOID();
        }
        return objectMatches;
    }

    public Comparable computeIndexKey(ClassInfo ci, ClassInfoIndex index) {
        return null;
    }

    public Comparable buildOrderByKey() {
        return IndexTool.buildIndexKey("OrderBy",currentNnoi, QueryManager.getOrderByAttributeIds(classInfo, query));
    }

    public Object getCurrentInstance() throws Exception {
        return currentObject;
    }

    public Object getCurrentObjectMetaRepresentation(){
        return currentNnoi;
    }

	public String getFullClassName(IQuery query) {
		return manager.getFullClassName(query);
	}

}
