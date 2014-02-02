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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeMultipleValuesPerKey;
import org.neodatis.btree.IBTreeSingleValuePerKey;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.QueryManager;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.btree.LazyODBBTreePersister;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbComparable;

/**
 * 
 * <p>
 * Generic query executor. This class does all the job of iterating in the
 * object list and call particular query matching to check if the object must be
 * included in the query result.
 * </p>
 * 
 * <p>
 * If the query has index, An execution plan is calculated to optimize the
 * execution. The query execution plan is calculated by subclasses (using
 * abstract method getExecutionPlan).
 * 
 * </P>
 * 
 */
public abstract class GenericQueryExecutor implements IMultiClassQueryExecutor {

	public static final String LOG_ID = "GenericQueryExecutor";

	/** The storage engine */
	protected IStorageEngine storageEngine;

	/** The query being executed */
	protected IQuery query;

	/** The class of the object being fetched */
	protected ClassInfo classInfo;

	/** The object used to read object data from database */
	protected IObjectReader objectReader;

	/** The current database session */
	protected ISession session;

	/** The next object position */
	protected OID nextOID;

	/** A boolean to indicate if query must be ordered */
	private boolean queryHasOrderBy;

	/** The key for ordering */
	private OdbComparable orderByKey;

	protected OID currentOid;

	protected NonNativeObjectInfo currentNnoi;

	protected IQueryExecutorCallback callback;

	/**
	 * Used for multi class executor to indicate not to execute start and end
	 * method of query result action
	 */
	protected boolean executeStartAndEndOfQueryAction;

	public GenericQueryExecutor(IQuery query, IStorageEngine engine) {
		this.query = query;
		this.storageEngine = engine;
		this.objectReader = storageEngine.getObjectReader();
		this.session = storageEngine.getSession(true);
		this.callback = OdbConfiguration.getQueryExecutorCallback();
		this.executeStartAndEndOfQueryAction = true;
	}

	public abstract IQueryExecutionPlan getExecutionPlan();

	public abstract void prepareQuery();

	public abstract Comparable computeIndexKey(ClassInfo ci, ClassInfoIndex index);

	/**
	 * This can be a NonNAtiveObjectInf or AttributeValuesMap
	 * 
	 * @return
	 */
	public abstract Object getCurrentObjectMetaRepresentation();

	/**
	 * Check if the object with oid matches the query, returns true
	 * 
	 * This method must compute the next object oid and the orderBy key if it
	 * exists!
	 * 
	 * @param oid
	 *            The object position
	 * @param loadObjectInfo
	 *            To indicate if object must loaded (when the query indicator
	 *            'in memory' is false, we do not need to load object, only ids)
	 * @param inMemory To indicate if object must be actually loaded to memory
	 */
	public abstract boolean matchObjectWithOid(OID oid, boolean loadObjectInfo, boolean inMemory);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.query.execution.IQueryExecutor#execute(boolean,
	 * int, int, boolean,
	 * org.neodatis.odb.core.query.execution.IMatchingObjectAction)
	 */
	public <T>Objects<T> execute(boolean inMemory, int startIndex, int endIndex, boolean returnObjects, IMatchingObjectAction queryResultAction) {

		if (storageEngine.isClosed()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(storageEngine.getBaseIdentification().getIdentification()));
		}

		if (session.isRollbacked()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_HAS_BEEN_ROLLBACKED);
		}

		// When used as MultiClass Executor, classInfo is already set
		if (classInfo == null) {
			// Class to execute query on
			String fullClassName = getFullClassName(query);

			// If the query class does not exist in meta model, return an empty
			// collection
			if (!session.getMetaModel().existClass(fullClassName)) {
				queryResultAction.start();
				queryResultAction.end();
				query.setExecutionPlan(new EmptyExecutionPlan());
				return queryResultAction.getObjects();
			}

			classInfo = session.getMetaModel().getClassInfo(fullClassName, true);
		}

		// Get the query execution plan
		IQueryExecutionPlan plan = getExecutionPlan();

		plan.start();
		try {
			if (plan.useIndex() && OdbConfiguration.useIndex()) {
				return executeUsingIndex(plan.getIndex(), inMemory, startIndex, endIndex, returnObjects, queryResultAction);
			}
			// When query must be applied to a single object
			if (query.isForSingleOid()) {
				return executeForOneOid(inMemory, returnObjects, queryResultAction);
			}
			return executeFullScan(inMemory, startIndex, endIndex, returnObjects, queryResultAction);
		} finally {
			plan.end();
		}
	}

	public abstract String getFullClassName(IQuery query);

	/**
	 * Query execution full scan
	 * 
	 * <pre>
	 * 
	 * startIndex &amp; endIndex
	 * A B C D E F G H I J K L
	 * 
	 * 
	 * [1,3] : nb &gt;=1 &amp;&amp; nb&lt;3
	 * 
	 * 1) 
	 * analyze A
	 * nb = 0
	 * nb E [1,3] ? no
	 * r=[]
	 * 2) 
	 * analyze B
	 * nb = 1
	 * nb E [1,3] ? yes
	 * r=[B]
	 * 3) analyze C
	 * nb = 2
	 * nb E [1,3] ? yes
	 * r=[B,C]
	 * 4) analyze C
	 * nb = 3
	 * nb E [1,3] ? no and 3&gt; upperBound([1,3]) =&gt; exit
	 * 
	 * </pre>
	 * 
	 * @param inMemory
	 * @param startIndex
	 * @param endIndex
	 * @param returnObjects
	 * @return
	 * @throws Exception
	 */
	private <T>Objects<T> executeFullScan(boolean inMemory, int startIndex, int endIndex, boolean returnObjects,
			IMatchingObjectAction queryResultAction) {

		boolean objectInRange = false;
		boolean objectMatches = false;

		if (storageEngine.isClosed()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(storageEngine.getBaseIdentification().getIdentification()));
		}

		long nbObjects = classInfo.getNumberOfObjects();

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("loading " + nbObjects + " instance(s) of " + classInfo.getFullClassName());
		}

		if (executeStartAndEndOfQueryAction()) {
			queryResultAction.start();
		}

		OID currentOID = null;
		OID prevOID = null;
		// TODO check if all instances are in the cache! and then load from the
		// cache
		nextOID = classInfo.getCommitedZoneInfo().first;

		if (nbObjects > 0 && nextOID == null) {
			// This means that some changes have not been commited!
			// Take next position from uncommited zone
			nextOID = classInfo.getUncommittedZoneInfo().first;
		}

		prepareQuery();

		if (query != null) {
			queryHasOrderBy = query.hasOrderBy();
		}
		boolean monitorMemory = OdbConfiguration.isMonitoringMemory();

		// used when startIndex and endIndex are not negative
		int nbObjectsInResult = 0;

		for (int i = 0; i < nbObjects; i++) {
		//int i=0;
		//while(nextOID!=null){
			if (monitorMemory && i % 10000 == 0) {
				MemoryMonitor.displayCurrentMemory("" + (i + 1), true);
			}
			// Reset the order by key
			orderByKey = null;

			objectMatches = false;
			prevOID = currentOID;
			currentOID = nextOID;
			
			

			// This is an error
			if (currentOID == null) {
				if (OdbConfiguration.throwExceptionWhenInconsistencyFound()) {
					throw new ODBRuntimeException(NeoDatisError.NULL_NEXT_OBJECT_OID.addParameter(classInfo.getFullClassName()).addParameter(i)
							.addParameter(nbObjects).addParameter(prevOID));
				}
				break;
			}

			// If there is an endIndex condition
			if (endIndex != -1 && nbObjectsInResult >= endIndex) {
				break;
			}

			// If there is a startIndex condition
			if (startIndex != -1 && nbObjectsInResult < startIndex) {
				objectInRange = false;
			} else {
				objectInRange = true;
			}
			/*
			 * // Object is not is the range, just peek next position if
			 * (!objectInRange) { nextOID =
			 * objectReader.getNextObjectOID(currentOID); continue; }
			 */

			// There is no query
			if (!inMemory && query == null ) {

				nbObjectsInResult++;

				// keep object position if we must
				if (objectInRange) {
					orderByKey = buildOrderByKey(currentNnoi);
					// TODO Where is the key for order by
					queryResultAction.objectMatch(nextOID, orderByKey);
				}
				nextOID = objectReader.getNextObjectOID(currentOID);
			} else {

				objectMatches = matchObjectWithOid(currentOID, returnObjects,inMemory);

				if (objectMatches) {
					nbObjectsInResult++;
					if (objectInRange) {
						if (queryHasOrderBy) {
							orderByKey = buildOrderByKey(getCurrentObjectMetaRepresentation());
						}
						queryResultAction.objectMatch(currentOID, getCurrentObjectMetaRepresentation(), orderByKey);
						if (callback != null) {
							callback.readingObject(i, -1);
						}
					}
				}
			}
			//i++;
		}
		if (executeStartAndEndOfQueryAction()) {
			queryResultAction.end();
		}
		return queryResultAction.getObjects();
	}

	/**
	 * Execute query using index
	 * 
	 * @param index
	 * @param inMemory
	 * @param startIndex
	 * @param endIndex
	 * @param returnObjects
	 * @return
	 * @throws Exception
	 */
	private <T>Objects<T> executeUsingIndex(ClassInfoIndex index, boolean inMemory, int startIndex, int endIndex, boolean returnObjects,
			IMatchingObjectAction queryResultAction) {

		// Index that have not been used yet do not have persister!
		if (index.getBTree().getPersister() == null) {
			index.getBTree().setPersister(new LazyODBBTreePersister(storageEngine));
		}

		boolean objectMatches = false;

		long nbObjects = classInfo.getNumberOfObjects();
		long btreeSize = index.getBTree().getSize();
		
		// the two values should be equal
		if(nbObjects!=btreeSize){
			ClassInfo ci = storageEngine.getSession(true).getMetaModel().getClassInfoFromId(index.getClassInfoId());
 
			throw new ODBRuntimeException(NeoDatisError.INDEX_IS_CORRUPTED.addParameter(index.getName()).addParameter(ci.getFullClassName()).addParameter(nbObjects).addParameter(btreeSize));
		}

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("loading " + nbObjects + " instance(s) of " + classInfo.getFullClassName());
		}

		if (executeStartAndEndOfQueryAction()) {
			queryResultAction.start();
		}

		prepareQuery();

		if (query != null) {
			queryHasOrderBy = query.hasOrderBy();
		}

		IBTree tree = index.getBTree();

		boolean isUnique = index.isUnique();

		// Iterator iterator = new BTreeIterator(tree,
		// OrderByConstants.ORDER_BY_ASC);
		Comparable key = computeIndexKey(classInfo, index);

		List list = null;
		// If index is unique, get the object
		if (isUnique) {
			IBTreeSingleValuePerKey treeSingle = (IBTreeSingleValuePerKey) tree;
			Object o = treeSingle.search(key);
			if (o != null) {
				list = new ArrayList();
				list.add(o);
			}
		} else {
			IBTreeMultipleValuesPerKey treeMultiple = (IBTreeMultipleValuesPerKey) tree;
			list = treeMultiple.search(key);
		}
		if (list != null) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				OID oid = (OID) iterator.next();
				// FIXME Why calling this method
				long position = objectReader.getObjectPositionFromItsOid(oid, true, true);
				orderByKey = null;

				objectMatches = matchObjectWithOid(oid, returnObjects,inMemory);

				if (objectMatches) {

					queryResultAction.objectMatch(oid, getCurrentObjectMetaRepresentation(), orderByKey);
				}
			}
			queryResultAction.end();
			return queryResultAction.getObjects();
		}
		if (executeStartAndEndOfQueryAction()) {
			queryResultAction.end();
		}
		return queryResultAction.getObjects();
	}

	/**
	 * Execute query using index
	 * 
	 * @param inMemory
	 * @param returnObjects
	 * @return
	 * @throws Exception
	 */
	private <T>Objects<T> executeForOneOid(boolean inMemory, boolean returnObjects, IMatchingObjectAction queryResultAction) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("loading Object with oid " + query.getOidOfObjectToQuery() + " - class " + classInfo.getFullClassName());
		}

		if (executeStartAndEndOfQueryAction()) {
			queryResultAction.start();
		}

		prepareQuery();

		OID oid = query.getOidOfObjectToQuery();
		// FIXME Why calling this method
		long position = objectReader.getObjectPositionFromItsOid(oid, true, true);
		boolean objectMatches = matchObjectWithOid(oid, returnObjects,inMemory);
		queryResultAction.objectMatch(oid, getCurrentObjectMetaRepresentation(), orderByKey);
		queryResultAction.end();
		return queryResultAction.getObjects();

	}

	/**
	 * TODO very bad. Should remove the instanceof
	 * 
	 * @param object
	 * @return
	 */
	public OdbComparable buildOrderByKey(Object object) {
		if (object instanceof AttributeValuesMap) {
			return buildOrderByKey((AttributeValuesMap) object);
		}
		return buildOrderByKey((NonNativeObjectInfo) object);
	}

	public OdbComparable buildOrderByKey(NonNativeObjectInfo nnoi) {
		// TODO cache the attributes ids to compute them only once
		return IndexTool.buildIndexKey("OrderBy", nnoi, QueryManager.getOrderByAttributeIds(classInfo, query));
	}

	public OdbComparable buildOrderByKey(AttributeValuesMap values) {
		return IndexTool.buildIndexKey("OrderBy", values, query.getOrderByFieldNames());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.query.execution.IQueryExecutor#
	 * executeStartAndEndOfQueryAction()
	 */
	public boolean executeStartAndEndOfQueryAction() {
		return executeStartAndEndOfQueryAction;
	}

	public void setExecuteStartAndEndOfQueryAction(boolean yes) {
		this.executeStartAndEndOfQueryAction = yes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.query.execution.IQueryExecutor#getStorageEngine()
	 */
	public IStorageEngine getStorageEngine() {
		return storageEngine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.query.execution.IQueryExecutor#getQuery()
	 */
	public IQuery getQuery() {
		return query;
	}

	public void setClassInfo(ClassInfo classInfo) {
		this.classInfo = classInfo;
	}

}
