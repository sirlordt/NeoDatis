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

import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.tool.wrappers.list.IOdbList;

public interface IValuesQuery extends IQuery {
	IValuesQuery count(String alias);
	IValuesQuery sum(String fieldName);
	IValuesQuery sum(String fieldName, String alias);
	IValuesQuery avg(String fieldName, String alias);
	IValuesQuery avg(String fieldName);
	IValuesQuery max(String fieldName, String alias);
	IValuesQuery max(String fieldName);
	IValuesQuery field(String fieldName);
	IValuesQuery field(String fieldName, String alias);
	IValuesQuery sublist(String attributeName, String alias, int fromIndex, int size, boolean throwException);
	IValuesQuery sublist(String attributeName, int fromIndex, int size, boolean throwException);
	IValuesQuery sublist(String attributeName, String alias, int fromIndex, int toIndex);
	IValuesQuery sublist(String attributeName, int fromIndex, int toIndex);
	IValuesQuery size(String attributeName);
	IValuesQuery size(String attributeName, String alias);
	IValuesQuery groupBy(String fieldList);
	String[] getGroupByFieldList();
	boolean hasGroupBy();
	
	IOdbList<String> getAllInvolvedFields();

	/** A collection of IQueryFieldAction*/
	IOdbList<IQueryFieldAction> getObjectActions();
		
	/** To indicate if a query will return one row (for example, sum, average, max and min, or will return more than one row*/
	boolean isMultiRow();
	/**
	 * @return
	 */
	boolean returnInstance();
	/** To indicate if query execution must build instances or return object representation, Default value is true(return instance)*/
	void setReturnInstance(boolean returnInstance);
	
	
}
