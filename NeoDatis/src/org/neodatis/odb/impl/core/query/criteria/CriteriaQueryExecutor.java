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

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.SimpleCompareKey;
import org.neodatis.odb.core.query.execution.GenericQueryExecutor;
import org.neodatis.odb.core.query.execution.IQueryExecutionPlan;
import org.neodatis.odb.core.query.execution.IndexTool;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.tool.wrappers.list.IOdbList;

public class CriteriaQueryExecutor extends GenericQueryExecutor {
	private IOdbList<String> involvedFields;

	private CriteriaQuery criteriaQuery;
	private CriteriaQueryManager manager;

	public CriteriaQueryExecutor(IQuery query, IStorageEngine engine) {
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
		ITmpCache tmpCache = session.getTmpCache();
		ObjectInfoHeader oih = null;
		try {
			if (!criteriaQuery.hasCriteria()) {
				// true, false = use cache, false = do not return object
				// TODO Warning setting true to useCache will put all objects in
				// the cache
				// This is not a good idea for big queries!, But use cache=true
				// resolves when object have not been committed yet!
				// for big queries, user should use a LazyCache!
				// If query has order by , we need to load the nnoi to compute the order by key. It could be done by using getValues ?!
				if (inMemory || criteriaQuery.hasOrderBy()) {
					currentNnoi = objectReader.readNonNativeObjectInfoFromOid(classInfo, currentOid, true, returnObject);

					if (currentNnoi.isDeletedObject()) {
						return false;
					}
					currentOid = currentNnoi.getOid();
					nextOID = currentNnoi.getNextObjectOID();
				}else{
					oih = objectReader.readObjectInfoHeaderFromOid(currentOid, false);
					nextOID = oih.getNextObjectOID();
				}
				return true;
			}
			boolean optimizeObjectCompararison = criteriaQuery.optimizeObjectComparison();
			// Gets a map with the values with the fields involved in the query
			AttributeValuesMap attributeValues = objectReader.readObjectInfoValuesFromOID(classInfo, currentOid, true, involvedFields,
					involvedFields, 0, criteriaQuery.getOrderByFieldNames(),optimizeObjectCompararison);
			// Then apply the query on the field values
			boolean objectMatches = manager.match(criteriaQuery, attributeValues);

			if (objectMatches) {
				// Then load the entire object
				// true, false = use cache
				currentNnoi = objectReader.readNonNativeObjectInfoFromOid(classInfo, currentOid, true, returnObject);
				currentOid = currentNnoi.getOid();
			}
			oih = attributeValues.getObjectInfoHeader();
			// Stores the next position
			nextOID = oih.getNextObjectOID();
			return objectMatches;
		} finally {
			tmpCache.clearObjectInfos();
		}
	}

	public Comparable computeIndexKey(ClassInfo ci, ClassInfoIndex index) {
		CriteriaQuery q = (CriteriaQuery) query;
		AttributeValuesMap values = q.getCriteria().getValues();
		// if values.hasOid() is true, this means that we are working of the full object,
		// the index key is then the oid and not the object itself
		if(values.hasOid()){
			return new SimpleCompareKey(values.getOid());
		}
		
		return IndexTool.computeKey(classInfo, index, (CriteriaQuery) query);
	}

	public Object getCurrentObjectMetaRepresentation() {
		return currentNnoi;
	}

	public String getFullClassName(IQuery query) {
		return manager.getFullClassName((CriteriaQuery) query);
	}
}
