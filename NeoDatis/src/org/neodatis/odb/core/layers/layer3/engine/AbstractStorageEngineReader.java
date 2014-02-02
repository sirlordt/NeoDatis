package org.neodatis.odb.core.layers.layer3.engine;

import java.util.Iterator;

import org.neodatis.btree.IBTree;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.instance.FullInstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.btree.LazyODBBTreePersister;
import org.neodatis.odb.impl.core.btree.ODBBTreeMultiple;
import org.neodatis.odb.impl.core.btree.ODBBTreeSingle;
import org.neodatis.odb.impl.core.layers.layer2.instance.ODBClassPool;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.main.DefaultClassRepresentation;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbTime;


/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public abstract class AbstractStorageEngineReader implements IStorageEngine{
	private static final String LOG_ID = "LocalStorageEngine";
	protected IObjectReader objectReader;
	/** To check if database has already been closed */
	protected boolean isClosed;
	/**
	 * The file parameters - if we are accessing a file, it will be a
	 * IOFileParameters that contains the file name
	 */
	protected IBaseIdentification baseIdentification;
	
	protected ICoreProvider provider;

	
	public void config(final IClassPool classPool){
		// - to enable storing Class instance
		ClassInfo ci = new ClassInfo(Class.class.getName());
		new DefaultClassRepresentation(this,ci).persistAttribute("name");

		provider.getClassIntrospector().addFullInstantiationHelper(ci.getFullClassName(), new FullInstantiationHelper() {

			public Object instantiate(NonNativeObjectInfo nnoi) {
					String className = (String) nnoi.getValueOf("name");
					return classPool.getClass(className);
			}
		});
		// -

	}
	
	
	public <T>Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}
		return objectReader.getObjects(query, inMemory, startIndex, endIndex);
	}


	public void defragmentTo(String newFileName) {
		long start = OdbTime.getCurrentTimeInMs();
		long totalNbObjects = 0;
		IStorageEngine newStorage = OdbConfiguration.getCoreProvider().getClientStorageEngine(new IOFileParameter(newFileName, true, baseIdentification.getUserName(),
				baseIdentification.getPassword()));

		Objects<Object> defragObjects = null;
		int j = 0;
		ClassInfo ci = null;
		// User classes
		Iterator iterator = getMetaModel().getUserClasses().iterator();
		while (iterator.hasNext()) {
			ci = (ClassInfo) iterator.next();
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("Reading " + ci.getCommitedZoneInfo().getNbObjects() + " objects of type " + ci.getFullClassName());
			}
			defragObjects = getObjects(new CriteriaQuery(ci.getFullClassName()), true, -1, -1);

			while (defragObjects.hasNext()) {
				newStorage.store(defragObjects.next());
				totalNbObjects++;
				if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
					if (j % 10000 == 0) {
						DLogger.info("\n" + totalNbObjects + " objects saved.");
					}
				}
				j++;
			}
		}
		// System classes
		iterator = getMetaModel().getSystemClasses().iterator();
		while (iterator.hasNext()) {
			ci = (ClassInfo) iterator.next();
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("Reading " + ci.getCommitedZoneInfo().getNbObjects() + " objects of type " + ci.getFullClassName());
			}
			defragObjects = getObjects(new CriteriaQuery(ci.getFullClassName()), true, -1, -1);

			while (defragObjects.hasNext()) {
				newStorage.store(defragObjects.next());
				totalNbObjects++;
				if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
					if (j % 10000 == 0) {
						DLogger.info("\n" + totalNbObjects + " objects saved.");
					}
				}
				j++;
			}
		}

		newStorage.commit();
		newStorage.close();
		long time = OdbTime.getCurrentTimeInMs() - start;
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.info("New storage " + newFileName + " created with " + totalNbObjects + " objects in " + time + " ms.");
		}

	}
	
	protected MetaModel getMetaModel() {
		return getSession(true).getMetaModel();
	}


	public abstract ISession getSession(boolean throwExceptionIfDoesNotExist);
	
	public void deleteIndex(String className, String indexName, boolean verbose){
		ClassInfo classInfo = getMetaModel().getClassInfo(className, true);
		if (!classInfo.hasIndex(indexName)) {
			throw new ODBRuntimeException(NeoDatisError.INDEX_DOES_NOT_EXIST.addParameter(indexName).addParameter(className));
		}
		ClassInfoIndex cii = classInfo.getIndexWithName(indexName);
		if(verbose){
			DLogger.info("Deleting index " + indexName + " on class " + className);
		}
		delete(cii,false);
		classInfo.removeIndex(cii);
		if(verbose){
			DLogger.info("Index " + indexName + " deleted");
		}
	}
	
	/** Used to rebuild an index*/
	public void rebuildIndex(String className, String indexName, boolean verbose){
		
		if(verbose){
			DLogger.info("Rebuilding index " + indexName + " on class " + className);
		}

		
		ClassInfo classInfo = getMetaModel().getClassInfo(className, true);
		if (!classInfo.hasIndex(indexName)) {
			throw new ODBRuntimeException(NeoDatisError.INDEX_DOES_NOT_EXIST.addParameter(indexName).addParameter(className));
		}
		ClassInfoIndex cii = classInfo.getIndexWithName(indexName);
		deleteIndex(className, indexName,verbose);
		addIndexOn(className, indexName, classInfo.getAttributeNames(cii.getAttributeIds()), verbose, !cii.isUnique());
		
		
	}
	public void addIndexOn(String className, String indexName, String[] indexFields, boolean verbose, boolean acceptMultipleValuesForSameKey) {
		ClassInfo classInfo = getMetaModel().getClassInfo(className, true);
		if (classInfo.hasIndex(indexName)) {
			throw new ODBRuntimeException(NeoDatisError.INDEX_ALREADY_EXIST.addParameter(indexName).addParameter(className));
		}
		ClassInfoIndex cii = classInfo.addIndexOn(indexName, indexFields,acceptMultipleValuesForSameKey);
		IBTree btree = null;

		if (acceptMultipleValuesForSameKey) {
			btree = new ODBBTreeMultiple(className, OdbConfiguration.getDefaultIndexBTreeDegree(), new LazyODBBTreePersister(this));

		} else {
			btree = new ODBBTreeSingle(className, OdbConfiguration.getDefaultIndexBTreeDegree(), new LazyODBBTreePersister(this));
		}
		cii.setBTree(btree);
		store(cii);

		// Now The index must be updated with all existing objects.
		if (classInfo.getNumberOfObjects() == 0) {
			// There are no objects. Nothing to do
			return;
		}
		if (verbose) {
			DLogger.info("Creating index " + indexName + " on class " + className + " - Class has already "
					+ classInfo.getNumberOfObjects() + " Objects. Updating index");
		}
		if (verbose) {
			DLogger.info(indexName + " : loading " + classInfo.getNumberOfObjects() + " objects from database");
		}

		// We must load all objects and insert them in the index!
		Objects<Object> objects = getObjectInfos(new CriteriaQuery(className), false, -1, -1, false);

		if (verbose) {
			DLogger.info(indexName + " : " + classInfo.getNumberOfObjects() + " objects loaded");
		}

		NonNativeObjectInfo nnoi = null;
		int i = 0;

		boolean monitorMemory = OdbConfiguration.isMonitoringMemory();

		while (objects.hasNext()) {
			nnoi = (NonNativeObjectInfo) objects.next();
			btree.insert(cii.computeKey(nnoi), nnoi.getOid());
			if (verbose && i % 1000 == 0) {
				if (monitorMemory) {
					MemoryMonitor.displayCurrentMemory("Index " + indexName + " " + i + " objects inserted", true);
				}
			}
			i++;
		}

		if (verbose) {
			DLogger.info(indexName + " created!");
		}

	}

	public <T>Objects<T> getObjectInfos(IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnObjects) {
		IMatchingObjectAction queryResultAction = provider.getCollectionQueryResultAction(this, query, inMemory, returnObjects);
		return objectReader.getObjectInfos(query, inMemory, startIndex, endIndex, returnObjects, queryResultAction);
	}

	public <T>Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex, int endIndex) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}
		return objectReader.getObjects(new CriteriaQuery(clazz.getName()), inMemory, startIndex, endIndex);
	}


}
