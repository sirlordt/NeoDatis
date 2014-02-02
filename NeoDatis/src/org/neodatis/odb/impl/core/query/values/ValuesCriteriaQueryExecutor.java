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
package org.neodatis.odb.impl.core.query.values;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.execution.GenericQueryExecutor;
import org.neodatis.odb.core.query.execution.IQueryExecutionPlan;
import org.neodatis.odb.core.query.execution.IndexTool;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQueryExecutionPlan;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQueryManager;
import org.neodatis.tool.wrappers.list.IOdbList;

public class ValuesCriteriaQueryExecutor extends GenericQueryExecutor {
	private IOdbList<String> involvedFields;

	private CriteriaQuery criteriaQuery;
	private AttributeValuesMap values;
	private CriteriaQueryManager manager;

	public ValuesCriteriaQueryExecutor(IQuery query, IStorageEngine engine) {
		super(query, engine);
		criteriaQuery = (CriteriaQuery) query;
		manager = new CriteriaQueryManager();
	}

	public IQueryExecutionPlan getExecutionPlan() {
		IQueryExecutionPlan plan = new CriteriaQueryExecutionPlan(classInfo, (CriteriaQuery) query);
		return plan;
	}

	public void prepareQuery() {
		criteriaQuery = (CriteriaQuery) query;
		criteriaQuery.setStorageEngine(storageEngine);
		involvedFields = criteriaQuery.getAllInvolvedFields();
	}

	public boolean matchObjectWithOid(OID oid, boolean returnObject, boolean inMemory) {
		currentOid = oid;
		
		boolean optimizeObjectCompararison = criteriaQuery.optimizeObjectComparison();
		
		// Gets a map with the values with the fields involved in the query
		values = objectReader.readObjectInfoValuesFromOID(classInfo, currentOid, true, involvedFields, involvedFields, 0, criteriaQuery
				.getOrderByFieldNames(),optimizeObjectCompararison);
		
		boolean objectMatches = true;

		if (!criteriaQuery.isForSingleOid()) {
			// Then apply the query on the field values
			objectMatches = manager.match(criteriaQuery, values);
		}

		ObjectInfoHeader oih = values.getObjectInfoHeader();
		// Stores the next position
		nextOID = oih.getNextObjectOID();
		return objectMatches;
	}

	public Comparable computeIndexKey(ClassInfo ci, ClassInfoIndex index) {
		return IndexTool.computeKey(classInfo, index, (CriteriaQuery) query);
	}

	public Object getCurrentObjectMetaRepresentation() {
		return values;
	}

	public String getFullClassName(IQuery query) {
		return manager.getFullClassName((CriteriaQuery) query);
	}

}
