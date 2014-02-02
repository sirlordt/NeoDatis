package org.neodatis.odb.core.query.execution;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.tool.wrappers.OdbComparable;

/**
 * The interface used to implement the classes that are called by the generic query executor when an object matches the query
 * @author osmadja
 *
 */
public interface IMatchingObjectAction {
	/** Called at the beginning of the query execution - used to prepare result object*/
	void start();
	
	/** Called (by the GenericQueryExecutor) when an object matches with lazy loading, only stores the OID*/
	void objectMatch(OID oid, OdbComparable orderByKey);
	
	/** Called (by the GenericQueryExecutor) when an object matches the query*/
	void objectMatch(OID oid, Object object, OdbComparable orderByKey);
	
	/** Called at the end of the query execution - used to clean or finish some task*/
	void end();
	
	/** Returns the resulting objects*/
	<T>Objects<T> getObjects();

}
