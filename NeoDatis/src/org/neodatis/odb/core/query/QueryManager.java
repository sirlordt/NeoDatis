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
package org.neodatis.odb.core.query;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.execution.IQueryExecutor;
import org.neodatis.odb.core.query.execution.MultiClassGenericQueryExecutor;
import org.neodatis.odb.core.query.nq.NativeQuery;
import org.neodatis.odb.core.query.nq.SimpleNativeQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQueryExecutor;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQueryManager;
import org.neodatis.odb.impl.core.query.nq.NativeQueryExecutor;
import org.neodatis.odb.impl.core.query.nq.NativeQueryManager;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQueryExecutor;

public class QueryManager {

	public static boolean needsInstanciation(IQuery query) {
		if (NativeQuery.class.isAssignableFrom(query.getClass())) {
			return true;
		}
		if (SimpleNativeQuery.class.isAssignableFrom(query.getClass())) {
			return true;
		}

		if (CriteriaQuery.class == query.getClass() || ValuesCriteriaQuery.class == query.getClass()) {
			return false;
		}
		throw new ODBRuntimeException(NeoDatisError.QUERY_TYPE_NOT_IMPLEMENTED.addParameter(query.getClass().getName()));

	}

	public static boolean isCriteriaQuery(IQuery query) {
		return CriteriaQuery.class.isAssignableFrom(query.getClass());
	}

	public static int[] getOrderByAttributeIds(ClassInfo classInfo, IQuery query) {
		String[] fieldNames = query.getOrderByFieldNames();
		int[] fieldIds = new int[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			fieldIds[i] = classInfo.getAttributeId(fieldNames[i]);
		}
		return fieldIds;
	}

	/**
	 * Returns a query executor according to the query type
	 * 
	 * @param query
	 * @param engine
	 * @param instanceBuilder
	 * @return
	 */
	public static IQueryExecutor getQueryExecutor(IQuery query, IStorageEngine engine, IInstanceBuilder instanceBuilder) {
		if (query.isPolymorphic()) {
			return getMultiClassQueryExecutor(query, engine, instanceBuilder);
		}

		return getSingleClassQueryExecutor(query, engine, instanceBuilder);
	}

	/**
	 * Return a single class query executor (polymorphic = false)
	 * 
	 * @param query
	 * @param engine
	 * @param instanceBuilder
	 * @return
	 */
	protected static IQueryExecutor getSingleClassQueryExecutor(IQuery query, IStorageEngine engine, IInstanceBuilder instanceBuilder) {

		if (CriteriaQuery.class == query.getClass()) {
			return new CriteriaQueryExecutor(query, engine);
		}
		if (ValuesCriteriaQuery.class == query.getClass()) {
			return new ValuesCriteriaQueryExecutor(query, engine);
		}

		if (NativeQuery.class.isAssignableFrom(query.getClass())) {
			return new NativeQueryExecutor(query, engine, instanceBuilder);
		}

		if (SimpleNativeQuery.class.isAssignableFrom(query.getClass())) {
			return new NativeQueryExecutor(query, engine, instanceBuilder);
		}

		throw new ODBRuntimeException(NeoDatisError.QUERY_TYPE_NOT_IMPLEMENTED.addParameter(query.getClass().getName()));
	}

	/**
	 * Returns a multi class query executor (polymorphic = true)
	 * 
	 * @param query
	 * @param engine
	 * @param instanceBuilder
	 * @return
	 */
	protected static IQueryExecutor getMultiClassQueryExecutor(IQuery query, IStorageEngine engine, IInstanceBuilder instanceBuilder) {

		if (CriteriaQuery.class == query.getClass()) {
			return new MultiClassGenericQueryExecutor(new CriteriaQueryExecutor(query, engine));
		}
		if (ValuesCriteriaQuery.class == query.getClass()) {
			return new MultiClassGenericQueryExecutor(new ValuesCriteriaQueryExecutor(query, engine));
		}

		if (NativeQuery.class.isAssignableFrom(query.getClass())) {
			return new MultiClassGenericQueryExecutor(new NativeQueryExecutor(query, engine, instanceBuilder));
		}

		if (SimpleNativeQuery.class.isAssignableFrom(query.getClass())) {
			return new MultiClassGenericQueryExecutor(new NativeQueryExecutor(query, engine, instanceBuilder));
		}

		throw new ODBRuntimeException(NeoDatisError.QUERY_TYPE_NOT_IMPLEMENTED.addParameter(query.getClass().getName()));
	}

	/**
	 * @param query
	 * @return
	 */
	public static String getFullClassName(IQuery query) {
		if (NativeQuery.class.isAssignableFrom(query.getClass())) {
			return new NativeQueryManager().getFullClassName((NativeQuery) query);
		}
		if (SimpleNativeQuery.class.isAssignableFrom(query.getClass())) {
			return new NativeQueryManager().getFullClassName((SimpleNativeQuery) query);
		}

		if (CriteriaQuery.class == query.getClass() || ValuesCriteriaQuery.class == query.getClass()) {
			return new CriteriaQueryManager().getFullClassName((CriteriaQuery) query);
		}
		throw new ODBRuntimeException(NeoDatisError.QUERY_TYPE_NOT_IMPLEMENTED.addParameter(query.getClass().getName()));
	}

}
