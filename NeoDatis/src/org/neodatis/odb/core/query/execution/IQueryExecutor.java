package org.neodatis.odb.core.query.execution;

import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;

public interface IQueryExecutor {

	/**
	 * The main query execution method
	 * 
	 * @param inMemory
	 * @param startIndex
	 * @param endIndex
	 * @param returnObjects
	 * @param queryResultAction
	 * @return
	 * @throws Exception
	 */
	<T>Objects<T> execute(boolean inMemory, int startIndex, int endIndex, boolean returnObjects,
			IMatchingObjectAction queryResultAction);

	String getFullClassName(IQuery query);
}