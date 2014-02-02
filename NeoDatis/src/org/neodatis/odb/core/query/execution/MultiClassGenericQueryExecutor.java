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
package org.neodatis.odb.core.query.execution;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.QueryManager;
import org.neodatis.tool.wrappers.list.IOdbList;

/**
 * 
 * <p>
 * A class to execute a query on more than one class and then merges the result. It is used when polymophic is set to true because
 * in this case, we must execute query on the main class and all its persistent subclasses
 * </p>
 * 
 * </P>
 * 
 */
public class MultiClassGenericQueryExecutor implements IQueryExecutor{

	private static final String LOG_ID = "MultiClassGenericQueryExecutor";
	
	private IMultiClassQueryExecutor executor;
	
	public MultiClassGenericQueryExecutor(IMultiClassQueryExecutor executor) {
		this.executor = executor;
		// To avoid reseting the result for each query
		this.executor.setExecuteStartAndEndOfQueryAction(false);
	}

	/**
	 * The main query execution method
	 * 
	 * @param query
	 * @param inMemory
	 * @param startIndex
	 * @param endIndex
	 * @param returnObjects
	 * @return
	 * @throws Exception
	 */
	public <T>Objects<T> execute(boolean inMemory, int startIndex, int endIndex, boolean returnObjects, IMatchingObjectAction queryResultAction) {

		if (executor.getStorageEngine().isClosed()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(executor.getStorageEngine().getBaseIdentification().getIdentification()));
		}

		if (executor.getStorageEngine().getSession(true).isRollbacked()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_HAS_BEEN_ROLLBACKED);
		}
		// Get the main class
		String fullClassName = executor.getFullClassName(executor.getQuery());

		// this is done once.
		queryResultAction.start();
		
		IOdbList<ClassInfo> allClassInfos = executor.getStorageEngine().getSession(true).getMetaModel().getPersistentSubclassesOf(fullClassName);
		int nbClasses = allClassInfos.size();
		ClassInfo ci = null;
		for(int i=0;i<nbClasses;i++){
			ci = allClassInfos.get(i);
			// Sets the class info to the current
			executor.setClassInfo(ci);
			// Then execute query
			executor.execute(inMemory, startIndex, endIndex, returnObjects, queryResultAction);
		}
		if(nbClasses==0){
			executor.getQuery().setExecutionPlan(new EmptyExecutionPlan());
		}
		queryResultAction.end();
		
		return queryResultAction.getObjects();
	}

	public boolean executeStartAndEndOfQueryAction() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.query.execution.IQueryExecutor#getFullClassName(org.neodatis.odb.core.query.IQuery)
	 */
	public String getFullClassName(IQuery query) {
		return executor.getFullClassName(executor.getQuery());
	}

}
