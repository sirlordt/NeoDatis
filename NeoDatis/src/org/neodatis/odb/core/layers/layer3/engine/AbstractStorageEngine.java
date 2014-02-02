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
package org.neodatis.odb.core.layers.layer3.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.DatabaseStartupManager;
import org.neodatis.odb.ODBAuthenticationRuntimeException;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer1.introspector.IIntrospectionCallback;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoCompareResult;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.ICommitListener;
import org.neodatis.odb.core.layers.layer3.IDTypes;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ICrossSessionCache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.DeleteTrigger;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.core.trigger.OIDTrigger;
import org.neodatis.odb.core.trigger.SelectTrigger;
import org.neodatis.odb.core.trigger.UpdateTrigger;
import org.neodatis.odb.impl.core.layers.layer1.introspector.DefaultInstrospectionCallbackForStore;
import org.neodatis.odb.impl.core.layers.layer1.introspector.GetDependentObjectIntrospectingCallback;
import org.neodatis.odb.impl.core.layers.layer3.engine.FileMutex;
import org.neodatis.odb.impl.core.layers.layer3.engine.ObjectReader;
import org.neodatis.odb.impl.core.layers.layer3.oid.FullIDInfo;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.OdbSystem;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**
 * The storage Engine
 * 
 * <pre>
 * 
 * 
 *  The Local Storage Engine class in the most important class in ODB. It manages reading, writing and querying objects.
 *  
 *  All write operations are delegated to the ObjectWriter class.
 *  
 *  All read operations are delegated to the ObjectReader class.
 *  
 *  All Id operations are delegated to the IdManager class.
 *  
 *  All Introspecting operations are delegated to the ObjectIntrospector class.
 *  
 *  All Trigger operations are delegated to the TriggerManager class.
 *  
 *  All session related operations are executed by The Session class. Session Class using the Transaction
 *  class are responsible for ACID behavior. *
 * 
 * </pre>
 * 
 */

public abstract class AbstractStorageEngine extends AbstractStorageEngineReader implements IStorageEngine {
	private static final String LOG_ID = "LocalStorageEngine";

	private int version;

	private DatabaseId databaseId;

	private IObjectWriter objectWriter;

	protected IObjectIntrospector objectIntrospector;
	protected IClassIntrospector classIntrospector;

	/** The meta-model number of classes - used only for meta model loading */
	private long nbClasses;

	/** the last odb close status - to check if a recover is necessary */
	private boolean lastOdbCloseStatus;

	/** The position of the current block where IDs are stored */
	private long currentIdBlockPosition;

	/** The current id block number */
	private int currentIdBlockNumber;

	/** The max id already allocated in the current id block */
	private OID currentIdBlockMaxOid;

	protected ITriggerManager triggerManager;
	protected IOdbList<ICommitListener> commitListeners;
	/**
	 * Used to know if the storage engine is executed in local mode (embedded
	 * mode) or client server mode
	 */
	protected boolean isLocal;

	/**
	 * To keep track of current transaction Id
	 * 
	 */
	protected TransactionId currentTransactionId;

	/** This is a visitor used to execute some specific action(like calling 'Before Insert Trigger')  when introspecting an object
	 * 
	 */
	protected IIntrospectionCallback introspectionCallbackForInsert;
	
	/** This is a visitor used to execute some specific action when introspecting an object
	 * 
	 */
	protected IIntrospectionCallback introspectionCallbackForUpdate;
	
	

	
	
	/**
	 * The database file name
	 * 
	 * @
	 */
	public AbstractStorageEngine(IBaseIdentification parameters) {
		this.provider = OdbConfiguration.getCoreProvider();
		this.baseIdentification = parameters;
		init();
	}

	protected void init() {

		// some basic configs
		config(provider.getClassPool());

		checkRuntimeCompatibility();

		isClosed = false;
		isLocal = baseIdentification.isLocal();
		// The check if it is a new Database must be executed before object
		// writer initialization. Because Object Writer Init
		// Creates the file so the check (which is based on the file existence
		// would always return false*/
		boolean isNewDatabase = isNewDatabase();

		commitListeners = new OdbArrayList<ICommitListener>();
		classIntrospector = provider.getClassIntrospector();

		ISession session = buildDefaultSession();
		// Object Writer must be created before object Reader
		objectWriter = buildObjectWriter();
		// Object writer is a two Phase init object
		objectWriter.init2();
		objectReader = buildObjectReader();

		addSession(session, false);

		// If the file does not exist, then a default header must be created
		if (isNewDatabase) {
			objectWriter.createEmptyDatabaseHeader(OdbTime.getCurrentTimeInMs(), baseIdentification.getUserName(), baseIdentification.getPassword());
		} else {
			try {
				getObjectReader().readDatabaseHeader(baseIdentification.getUserName(), baseIdentification.getPassword());
			} catch (ODBAuthenticationRuntimeException e) {
				close();
				throw e;
			}
		}
		objectWriter.afterInit();
		objectIntrospector = buildObjectIntrospector();

		this.triggerManager = buildTriggerManager();
		
		MetaModel metaModel = null;
		// This forces the initialization of the meta model
		try{
			metaModel = getMetaModel();
		}catch (ODBRuntimeException e) {
			objectReader.close();
			objectWriter.close();
			throw e;
		}

		if (OdbConfiguration.checkModelCompatibility()) {
			checkMetaModelCompatibility(classIntrospector.instrospect(metaModel.getAllClasses()));
		}

		// logically locks access to the file (only for this Virtual machine)
		FileMutex.getInstance().openFile(getStorageDeviceName());

		// Updates the Transaction Id in the file
		objectWriter.writeLastTransactionId(getCurrentTransactionId());

		
		this.objectWriter.setTriggerManager(this.triggerManager);

		this.introspectionCallbackForInsert = new DefaultInstrospectionCallbackForStore(this,triggerManager,false);
		this.introspectionCallbackForUpdate = new DefaultInstrospectionCallbackForStore(this,triggerManager,true);
		
	}

	public void addSession(ISession session, boolean readMetamodel) {
		// Associate current session to the fsi -> all transaction writes
		// will be applied to this FileSystemInterface
		session.setFileSystemInterfaceToApplyTransaction(objectWriter.getFsi());

		if (readMetamodel) {
			MetaModel metaModel = null;
			try {
				objectReader.readDatabaseHeader(baseIdentification.getUserName(), baseIdentification.getPassword());
			} catch (ODBAuthenticationRuntimeException e) {
				close();
				throw e;
			}
			metaModel = new SessionMetaModel();
			session.setMetaModel(metaModel);
			metaModel = objectReader.readMetaModel(metaModel, true);

			// Updates the Transaction Id in the file
			objectWriter.writeLastTransactionId(getCurrentTransactionId());
		}

	}

	/**
	 * Receive the current class info (loaded from current java classes present on classpath
	 * and check against the persisted meta model
	 * @param currentCIs
	 */
	public CheckMetaModelResult checkMetaModelCompatibility(Map<String, ClassInfo> currentCIs) {
		ClassInfo persistedCI = null;
		ClassInfo currentCI = null;
		ClassInfoCompareResult result = null;
		CheckMetaModelResult checkMetaModelResult = new CheckMetaModelResult();

		// User classes
		Iterator<ClassInfo> iterator = getMetaModel().getUserClasses().iterator();
		while (iterator.hasNext()) {
			persistedCI = iterator.next();
			currentCI = currentCIs.get(persistedCI.getFullClassName());
			
			result = persistedCI.extractDifferences(currentCI, true);
			if (!result.isCompatible()) {
				throw new ODBRuntimeException(NeoDatisError.INCOMPATIBLE_METAMODEL.addParameter(result.toString()));
			}
			if (result.hasCompatibleChanges()) {
				checkMetaModelResult.add(result);
			}
		}
		// System classes
		iterator = getMetaModel().getSystemClasses().iterator();
		while (iterator.hasNext()) {
			persistedCI = iterator.next();
			currentCI = currentCIs.get(persistedCI.getFullClassName());
			result = persistedCI.extractDifferences(currentCI, true);
			if (!result.isCompatible()) {
				throw new ODBRuntimeException(NeoDatisError.INCOMPATIBLE_METAMODEL.addParameter(result.toString()));
			}
			if (result.hasCompatibleChanges()) {
				checkMetaModelResult.add(result);
			}
		}

		for (int i = 0; i < checkMetaModelResult.size(); i++) {
			result = checkMetaModelResult.getResults().get(i);
			DLogger.info("Class " + result.getFullClassName() + " has changed :");
			DLogger.info(result.toString());
		}
		if (!checkMetaModelResult.getResults().isEmpty()) {
			updateMetaModel();
			checkMetaModelResult.setModelHasBeenUpdated(true);
		}
		return checkMetaModelResult;
	}

	/**
	 * This is a runtime compatibility check. Java version must be greater than 1.5
	 */
	public void checkRuntimeCompatibility() {

		if(!OdbConfiguration.checkRuntimeVersion()){
			return;
		}
		String runtimeVersion = null;
		try {
			runtimeVersion = OdbSystem.getProperty("java.version");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if (runtimeVersion != null) {
			// android : returns "0" as java runtime version=> ignore runtime versin check for android
			String os = OdbSystem.getProperty("os.name");
			String osArc = OdbSystem.getProperty("os.arch");
			String javaVendor = OdbSystem.getProperty("java.vendor");
			
			// This is just a protection
			if(javaVendor==null){
				DLogger.info("Current JVM does not have 'java.vendor' property defined => unable to check JVM runtime compatibility");
				return;
			}
			
			
			// android : we assume that java version is ok
			// Because the java version is equal to "0" on android :-(
			if( javaVendor.equals("The Android Project")){
				return;
			}
			// else we need to check
			// First : protection against bad formed version
			if(runtimeVersion==null || runtimeVersion.length()<3){
				DLogger.info("Current JVM does not have correct vava version => unable to check JVM runtime compatibility");
				return;
			}
			double version = Float.parseFloat(OdbString.substring(runtimeVersion,0, 3));
			
			
			if (version < 1.5) {
				throw new ODBRuntimeException(NeoDatisError.INCOMPATIBLE_JAVA_VM.addParameter(runtimeVersion));
			}
		}
	}

	public void updateMetaModel() {
		MetaModel metaModel = getMetaModel();
		DLogger.info("Automatic refactoring : updating meta model");
		
		// neodatisee
		// User classes : 
		List<ClassInfo> userClasses = new ArrayList<ClassInfo>(metaModel.getUserClasses());
		Iterator iterator = userClasses.iterator();
		//Iterator iterator = metaModel.getUserClasses().iterator();
		
		while (iterator.hasNext()) {
			objectWriter.updateClassInfo((ClassInfo) iterator.next(), true);
		}
		// System classes
		iterator = metaModel.getSystemClasses().iterator();
		while (iterator.hasNext()) {
			objectWriter.updateClassInfo((ClassInfo) iterator.next(), true);
		}
	}

	private String getStorageDeviceName() {
		return baseIdentification.getIdentification();
		/*
		if (baseIdentification instanceof IOFileParameter) {
			IOFileParameter fileParameter = (IOFileParameter) baseIdentification;
			return fileParameter.getFileName();
		}
		if (baseIdentification.getClass() == IOSocketParameter.class) {
			IOSocketParameter sparameter = (IOSocketParameter) baseIdentification;
			return sparameter.getBaseIdentifier();
		}
		return baseIdentification.getIdentification();
		//throw new ODBRuntimeException(Error.UNSUPPORTED_IO_TYPE.addParameter(baseIdentification.toString()));
		 * 
		 */
	}

	private boolean isNewDatabase() {
		return baseIdentification.isNew();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.io.IStorageEngine#mainStoreObject(java.lang.Object)
	 */
	public OID store(Object object) {
		return store(null, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.io.IStorageEngine#mainStoreObject(java.lang.Object)
	 */
	public OID store(OID oid, Object object) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}

		// triggers before
		// triggerManager.manageInsertTriggerBefore(object.getClass().getName(),
		// object);

		OID newOid = internalStore(oid, object,false);

		// triggers after - fixme
		// triggerManager.manageInsertTriggerAfter(object.getClass().getName(),
		// object, newOid);
		getSession(true).getCache().clearInsertingObjects();
		return newOid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.io.IStorageEngine#mainStoreObject(java.lang.Object)
	 */
	public OID update(OID oid, Object object) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}

		// triggers before
		// triggerManager.manageInsertTriggerBefore(object.getClass().getName(),
		// object);

		OID newOid = internalStore(oid, object,true);

		// triggers after - fixme
		// triggerManager.manageInsertTriggerAfter(object.getClass().getName(),
		// object, newOid);
		getSession(true).getCache().clearInsertingObjects();
		return newOid;
	}

	/**
	 * Store an object in ODBFactory database.
	 * 
	 * <pre>
	 *       	Transforms the object into meta representation and calls the internalStoreObject
	 * </pre>
	 * 
	 * @param object
	 * @return The object insertion position
	 * @throws IOException
	 */
	protected OID internalStore(Object object) {
		return internalStore(null, object, false);
	}

	/**
	 * Store an object with the specific id
	 * 
	 * @param oid
	 * @param object
	 * @return
	 * @
	 */
	protected OID internalStore(OID oid, Object object, boolean forceUpdate) {

		if (getSession(true).isRollbacked()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_HAS_BEEN_ROLLBACKED.addParameter(getBaseIdentification().toString()));
		}

		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_STORE_NULL_OBJECT);
		}

		Class clazz = object.getClass();

		if (ODBType.isNative(clazz)) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_STORE_NATIVE_OBJECT_DIRECTLY
					.addParameter(clazz.getName())
					.addParameter(ODBType.getFromClass(clazz).getName())
					.addParameter(clazz.getName()));
		}

		// The object must be transformed into meta representation
		ClassInfo ci = null;

		String className = clazz.getName();

		// first checks if the class of this object already exist in the
		// metamodel
		if (getMetaModel().existClass(className)) {
			ci = getMetaModel().getClassInfo(className, true);
		} else {
			ClassInfoList ciList = classIntrospector.introspect(object.getClass(), true);
			// All new classes found
			objectWriter.addClasses(ciList);
			ci = ciList.getMainClassInfo();

		}

		// first detects if we must perform an insert or an update
		// If object is in the cache, we must perform an update, else an insert

		boolean mustUpdate = forceUpdate;
		ICache cache = getSession(true).getCache();

		if (!mustUpdate && object != null) {
			OID cacheOid = cache.idOfInsertingObject(object);
			if (cacheOid != null) {
				return cacheOid;
			}

			// throw new ODBRuntimeException("Inserting meta representation of
			// an object without the object itself is not yet supported");
			mustUpdate = cache.existObject(object);

		}
		
		// The introspection callback is used to execute some specific task (like calling trigger, for example) while introspecting the object
		IIntrospectionCallback callback = introspectionCallbackForInsert;
		if(mustUpdate){
			callback = introspectionCallbackForUpdate;
		}
		// Transform the object into an ObjectInfo
		NonNativeObjectInfo nnoi = (NonNativeObjectInfo) objectIntrospector.getMetaRepresentation(object, ci, true, null,
				callback);

		// During the introspection process, if object is to be updated, then the oid has been set
		mustUpdate = nnoi.getOid()!=null;
		
		if (mustUpdate) {
			return objectWriter.updateNonNativeObjectInfo(nnoi, forceUpdate);
		}

		return objectWriter.insertNonNativeObject(oid, nnoi, true);
	}

	/**
	 * Warning, 
	 */
	public void deleteObjectWithOid(OID oid, boolean cascade) {
		ObjectInfoHeader oih = null;

		ISession lsession = getSession(true);
		ICache cache = lsession.getCache();

		// Check if oih is in the cache
		oih = cache.getObjectInfoHeaderFromOid(oid, false);
		if (oih == null) {
			oih = objectReader.readObjectInfoHeaderFromOid(oid, true);
		}

		if(OdbConfiguration.reconnectObjectsToSession()){
			CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification()).removeOid(oid);
		}

		
		objectWriter.delete(oih);
		// removes the object from the cache
		cache.removeObjectWithOid(oih.getOid());
		

	}

	/**
	 * Actually deletes an object database
	 * @param object
	 * @param cascade
	 * @param alreadyDeletedObjects Contain the oids of already deleted objects. This is to manage cyclic reference
	 */
	public OID delete(Object object, boolean cascade ) {
		return internalDelete(object, cascade, null);
	}
	
	protected OID internalDelete(Object object, boolean cascade, Map<OID, OID> alreadyDeletedObjects){
		if(isClosed){
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.toString()));
		}
		
		ISession lsession = getSession(true);
		if (lsession.isRollbacked()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_HAS_BEEN_ROLLBACKED.addParameter(baseIdentification.toString()));
		}

		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_DELETE_NULL_OBJECT);
		}
		if(alreadyDeletedObjects==null){
			alreadyDeletedObjects = new HashMap<OID, OID>();
		}
		ICache cache = lsession.getCache();

		boolean throwExceptionIfNotInCache = false;

		// Get header of the object (position, previous object position, next
		// object position and class info position)
		// Header must come from cache because it may have been updated before.
		ObjectInfoHeader header = cache.getObjectInfoHeaderFromObject(object, throwExceptionIfNotInCache);

		if (header == null) {
			OID cachedOid = cache.getOid(object, false);
			
			//reconnect object is turn on tries to get object from cross session
			if(cachedOid == null && OdbConfiguration.reconnectObjectsToSession()){
				ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification());
				cachedOid = crossSessionCache.getOid(object);
			}
			if (cachedOid == null) {
				throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE_FOR_DELETE.addParameter(object.getClass().getName()).addParameter(object.toString()));
			}
			header = objectReader.readObjectInfoHeaderFromOid(cachedOid, false);
		}

		OID oid = header.getOid();
		
		if(alreadyDeletedObjects.containsKey(oid)){
			return oid;
		}

		triggerManager.manageDeleteTriggerBefore(object.getClass().getName(), object, header.getOid());

		alreadyDeletedObjects.put(oid,oid);
		//@neodatisee
		if(cascade){
			//TODO call triggers for dependent objects too
			
			// gets the class info of  the object
			ClassInfo ci = getSession(true).getMetaModel().getClassInfoFromId(header.getClassInfoId());
			// actually reads the object
			NonNativeObjectInfo nnoi = objectReader.readNonNativeObjectInfoFromOid(ci, oid, true, true);
			
			if(nnoi.isDeletedObject()){
				throw new ODBRuntimeException(NeoDatisError.OBJECT_IS_MARKED_AS_DELETED_FOR_OID.addParameter(oid));
			}
			// then retrieve all dependent objects
			Map<OID, ObjectInfoHeader> oidsToDelete = new HashMap<OID, ObjectInfoHeader>();
			oidsToDelete.put(oid, header);
			oidsToDelete = getAllDependentObjects(ci, oid, oidsToDelete);
			
			ObjectReader or = (ObjectReader) objectReader;
			
			// delete each object
			for(OID o:oidsToDelete.keySet()){
				ObjectInfoHeader cachedOih = oidsToDelete.get(o);
				ObjectInfoHeader oihFromDb = objectReader.readObjectInfoHeaderFromOid(o, true);
				oid = objectWriter.delete(oihFromDb);
				
			}
		}else{
			oid = objectWriter.delete(header);	
		}
		

		triggerManager.manageDeleteTriggerAfter(object.getClass().getName(), object, oid);
		// removes the object from the cache
		cache.removeObjectWithOid(header.getOid());
		if(OdbConfiguration.reconnectObjectsToSession()){
			CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification()).removeObject(object);
		}

		return oid;
	}

	
	/**@neodataisee
	 * 
	 * To retrieve all dependent objects of a specific object
	 * @TODO optimize object loading
	 */
	public Map<OID, ObjectInfoHeader> getAllDependentObjects(ClassInfo ci, OID oid, Map<OID , ObjectInfoHeader> objectsToDelete){
	
		NonNativeObjectInfo nnoi = objectReader.readNonNativeObjectInfoFromOid(ci, oid, true, true);
		
		objectsToDelete.put(oid, nnoi.getHeader());
		
		for(AbstractObjectInfo aoi: nnoi.getAttributeValues()){
			
			if(aoi.isNonNativeObject()){
				NonNativeObjectInfo nnoi2 = (NonNativeObjectInfo) aoi;
				
				if(!nnoi2.isNull() && !objectsToDelete.containsKey(nnoi2.getOid())){
					
					/*Object o = nnoi2.getObject();
					
					if(o==null){
						o = objectReader.getInstanceBuilder().buildOneInstance(nnoi2);
						
					}*/
					getAllDependentObjects(nnoi2.getClassInfo(), nnoi2.getOid(), objectsToDelete);
				}
			}else{
				// if it is a collection,an array or a map 
				if(aoi.isGroup()){
					getAllDependentObjects(aoi, objectsToDelete);
				}
			}
		}
		return objectsToDelete;
	}
	
	/**
	 *  To retrieve all dependent objects of a specific object of a group Object (List,array and map)
	 * @neodatisee
	 * @param aoi
	 */
	private  Map<OID, ObjectInfoHeader>  getAllDependentObjects(AbstractObjectInfo objectInfo, Map<OID, ObjectInfoHeader> objectsToDelete ) {
		if(objectInfo.isCollectionObject()){
			CollectionObjectInfo coi = (CollectionObjectInfo) objectInfo;
			int size = coi.getCollection().size();
			for(AbstractObjectInfo aoi:coi.getCollection()){
				if(aoi.isNonNativeObject() && !aoi.isNull()){
					NonNativeObjectInfo nnoi = (NonNativeObjectInfo) aoi;
					OID oid =  nnoi.getOid();
					if(!objectsToDelete.containsKey(oid)){
						Object o = aoi.getObject();
						if(o==null){
							o = objectReader.getInstanceBuilder().buildOneInstance((NonNativeObjectInfo) aoi);
						}
						
						getAllDependentObjects(nnoi.getClassInfo(), oid, objectsToDelete);
					}
				}
			}
		}
		if(objectInfo.isArrayObject()){
			ArrayObjectInfo aroi = (ArrayObjectInfo) objectInfo;
			int size = aroi.getArrayLength();
			for(int i=0;i<size;i++){
				AbstractObjectInfo aoi = (AbstractObjectInfo) aroi.getArray()[i];
				if(aoi.isNonNativeObject() && !aoi.isNull()){
					NonNativeObjectInfo nnoi = (NonNativeObjectInfo) aoi;
					OID oid =  nnoi.getOid();	
					if(!objectsToDelete.containsKey(oid)){
						Object o = aoi.getObject();
						if(o==null){
							o = objectReader.getInstanceBuilder().buildOneInstance((NonNativeObjectInfo) aoi);
						}
						getAllDependentObjects(nnoi.getClassInfo(), oid, objectsToDelete);
					}
				}
			}
		}
		if(objectInfo.isMapObject()){
			MapObjectInfo moi = (MapObjectInfo) objectInfo;
			int size = moi.getMap().size();
			Iterator<AbstractObjectInfo> iterator = moi.getMap().keySet().iterator();
			while(iterator.hasNext()){
				AbstractObjectInfo key = iterator.next();
				AbstractObjectInfo value = moi.getMap().get(key);
				if(key.isNonNativeObject() && !key.isNull()){
					NonNativeObjectInfo nnoi = (NonNativeObjectInfo) key;
					OID oid =  nnoi.getOid();
					if(!objectsToDelete.containsKey(oid)){
						Object o = nnoi.getObject();
						if(o==null){
							o = objectReader.getInstanceBuilder().buildOneInstance(nnoi);
						}
						
						getAllDependentObjects(nnoi.getClassInfo(), oid, objectsToDelete);
					}
				}
				if(value.isNonNativeObject()&&!value.isNull()){
					NonNativeObjectInfo nnoi = (NonNativeObjectInfo) value;
					OID oid =  nnoi.getOid();	
					if(!objectsToDelete.containsKey(oid)){
						Object o = nnoi.getObject();
						if(o==null){
							o = objectReader.getInstanceBuilder().buildOneInstance(nnoi);
						}
						getAllDependentObjects(nnoi.getClassInfo(), oid, objectsToDelete);
					}
				}
			}
		}
		
		return objectsToDelete;
		
	}
	
	/**
	 * @neodatisee
	 * @param aoi
	 */
	private void deleteCascadeGroup(AbstractObjectInfo objectInfo, Map<OID, OID> alreadyDeletedObjects ) {
		if(objectInfo.isCollectionObject()){
			CollectionObjectInfo coi = (CollectionObjectInfo) objectInfo;
			int size = coi.getCollection().size();
			for(AbstractObjectInfo aoi:coi.getCollection()){
				if(aoi.isNonNativeObject() && !aoi.isNull()){
					Object o = aoi.getObject();
					if(o==null){
						o = objectReader.getInstanceBuilder().buildOneInstance((NonNativeObjectInfo) aoi);
					}
					internalDelete(o, true, alreadyDeletedObjects);
				}
			}
		}
		if(objectInfo.isArrayObject()){
			ArrayObjectInfo aroi = (ArrayObjectInfo) objectInfo;
			int size = aroi.getArrayLength();
			for(int i=0;i<size;i++){
				AbstractObjectInfo aoi = (AbstractObjectInfo) aroi.getArray()[i];
				if(aoi.isNonNativeObject() && !aoi.isNull()){
					Object o = aoi.getObject();
					if(o==null){
						o = objectReader.getInstanceBuilder().buildOneInstance((NonNativeObjectInfo) aoi);
					}

					internalDelete(o, true,alreadyDeletedObjects);
				}
			}
		}
		if(objectInfo.isMapObject()){
			MapObjectInfo moi = (MapObjectInfo) objectInfo;
			int size = moi.getMap().size();
			Iterator<AbstractObjectInfo> iterator = moi.getMap().keySet().iterator();
			while(iterator.hasNext()){
				AbstractObjectInfo key = iterator.next();
				AbstractObjectInfo value = moi.getMap().get(key);
				if(key.isNonNativeObject() && !key.isNull()){
					Object o = key.getObject();
					if(o==null){
						o = objectReader.getInstanceBuilder().buildOneInstance((NonNativeObjectInfo) key);
					}

					internalDelete(o, true,alreadyDeletedObjects);
				}
				if(value.isNonNativeObject()&&!value.isNull()){
					Object o = value.getObject();
					if(o==null){
						o = objectReader.getInstanceBuilder().buildOneInstance((NonNativeObjectInfo) value);
					}

					internalDelete(o, true,alreadyDeletedObjects);
				}
			}
		}
		
	}

	/**
	 * Returns a string of the meta-model
	 * 
	 * @return The engine description
	 */
	public String toString() {

		StringBuffer buffer = new StringBuffer();
		buffer.append(getMetaModel().toString());
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.io.IStorageEngine#close()
	 */
	public void close() {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}

		// When not local (client server) session can be null
		ISession lsession = getSession(isLocal);

		if (baseIdentification.canWrite()) {
			objectWriter.writeLastODBCloseStatus(true, false);
		}
		objectWriter.flush();
		if (isLocal && lsession.transactionIsPending()) {
			throw new ODBRuntimeException(NeoDatisError.TRANSACTION_IS_PENDING.addParameter(lsession.getId()));
		}

		isClosed = true;
		objectReader.close();
		objectWriter.close();
		// Logically release this file (only for this virtual machine)
		FileMutex.getInstance().releaseFile(getStorageDeviceName());
		if (lsession != null) {
			lsession.close();
		}

		if (objectIntrospector != null) {
			objectIntrospector.clear();
			objectIntrospector = null;
		}
		// remove trigger manager
		provider.removeLocalTriggerManager(this);
	}

	public long count(CriteriaQuery query) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}
		IValuesQuery q = new ValuesCriteriaQuery(query).count("count");
		Values values = getValues(q, -1, -1);
		Long count = (Long) values.nextValues().getByIndex(0);
		return count.longValue();
	}

	

	public IObjectReader getObjectReader() {
		return objectReader;
	}

	public IObjectWriter getObjectWriter() {
		return objectWriter;
	}

	public void commit() {
		if (isClosed()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}
		getSession(true).commit();
		objectWriter.flush();
	}

	public void rollback() {
		getSession(true).rollback();
	}

	public OID getObjectId(Object object, boolean throwExceptionIfDoesNotExist) {
		OID oid = null; 
		
		if (object != null) {
			oid = getSession(true).getCache().getOid(object, false);
			
			// If cross cache session is on, just check if current object has the OID on the cache
			if (oid==null && OdbConfiguration.reconnectObjectsToSession()) {
				ICrossSessionCache cache = CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification());
				oid = cache.getOid(object);
				if (oid != null) {
					return oid;
				}
			}

			oid = getSession(true).getCache().getOid(object, false);
			if (oid == null&&throwExceptionIfDoesNotExist) {
				throw new ODBRuntimeException(NeoDatisError.UNKNOWN_OBJECT_TO_GET_OID.addParameter(object.toString()));
			}
			return oid;
		}
		throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_RETURN_OID_OF_NULL_OBJECT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.io.IStorageEngine#getObjectFromId(long)
	 */
	public Object getObjectFromOid(OID oid) {
		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CAN_NOT_GET_OBJECT_FROM_NULL_OID);
		}
		NonNativeObjectInfo nnoi = getObjectReader().readNonNativeObjectInfoFromOid(null, oid, true, true);
		if (nnoi.isDeletedObject()) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_IS_MARKED_AS_DELETED_FOR_OID.addParameter(oid));
		}
		Object o = nnoi.getObject();
		if (o == null) {
			o = getObjectReader().getInstanceBuilder().buildOneInstance(nnoi);
		}
		ISession lsession = getSession(true);
		// Here oid can be different from nnoi.getOid(). This is the case when
		// the oid is an external oid. That`s why we use
		// nnoi.getOid() to put in the cache
		lsession.getCache().addObject(nnoi.getOid(), o, nnoi.getHeader());
		lsession.getTmpCache().clearObjectInfos();
		return o;
	}

	public NonNativeObjectInfo getMetaObjectFromOid(OID oid) {
		NonNativeObjectInfo nnoi = getObjectReader().readNonNativeObjectInfoFromOid(null, oid, true, false);
		getSession(true).getTmpCache().clearObjectInfos();
		return nnoi;
	}

	public ObjectInfoHeader getObjectInfoHeaderFromOid(OID oid, boolean useCache) {
		return getObjectReader().readObjectInfoHeaderFromOid(oid, useCache);
	}

	

	public List<Long> getAllObjectIds() {
		return objectReader.getAllIds(IDTypes.OBJECT);
	}

	public List<FullIDInfo> getAllObjectIdInfos(String objectType, boolean displayObjects) {
		return objectReader.getAllIdInfos(objectType, IDTypes.OBJECT, displayObjects);
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setDatabaseId(DatabaseId databaseId) {
		this.databaseId = databaseId;
	}

	public void setNbClasses(long nbClasses) {
		this.nbClasses = nbClasses;
	}

	public void setLastODBCloseStatus(boolean lastCloseStatus) {
		this.lastOdbCloseStatus = lastCloseStatus;
	}

	public void setCurrentIdBlockInfos(long currentBlockPosition, int currentBlockNumber, OID maxId) {
		this.currentIdBlockPosition = currentBlockPosition;
		this.currentIdBlockNumber = currentBlockNumber;
		this.currentIdBlockMaxOid = maxId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.io.IStorageEngine#getCurrentIdBlockNumber()
	 */
	public int getCurrentIdBlockNumber() {
		return currentIdBlockNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.io.IStorageEngine#getCurrentIdBlockPosition()
	 */
	public long getCurrentIdBlockPosition() {
		return currentIdBlockPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.io.IStorageEngine#getDatabaseId()
	 */
	public DatabaseId getDatabaseId() {
		return databaseId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.io.IStorageEngine#getCurrentIdBlockMaxId()
	 */
	public OID getCurrentIdBlockMaxOid() {
		return currentIdBlockMaxOid;
	}

	public OID getMaxOid() {
		return objectWriter.getIdManager().consultNextOid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.io.IStorageEngine#setMetaModel(org.neodatis.odb
	 * .core.meta.MetaModel)
	 */
	public void setMetaModel(MetaModel metaModel2) {

		ClassInfo ci = null;
		MetaModel metaModel = new SessionMetaModel();
		getSession(true).setMetaModel(metaModel);

		// Just add the classes
		Iterator iterator = metaModel2.getAllClasses().iterator();
		while (iterator.hasNext()) {
			this.getMetaModel().addClass((ClassInfo) iterator.next());
		}

		// Now persists classes
		iterator = metaModel.getAllClasses().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			ci = (ClassInfo) iterator.next();
			if (ci.getPosition() == -1) {
				objectWriter.persistClass(ci, (i == 0 ? -2 : i - 1), false, false);
			}
			i++;
		}
	}

	public boolean isClosed() {
		return isClosed;
	}

	public int getVersion() {
		return version;
	}

	public IBaseIdentification getBaseIdentification() {
		return baseIdentification;
	}

	public OID writeObjectInfo(OID oid, NonNativeObjectInfo aoi, long position, boolean updatePointers) {
		// TODO check if it must be written in transaction
		return objectWriter.writeNonNativeObjectInfo(oid, aoi, position, updatePointers, true);
	}

	public OID updateObject(NonNativeObjectInfo nnoi, boolean forceUpdate) {
		return objectWriter.updateNonNativeObjectInfo(nnoi, forceUpdate);
	}

	public Values getValues(IValuesQuery query, int startIndex, int endIndex) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(baseIdentification.getIdentification()));
		}
		return objectReader.getValues(query, startIndex, endIndex);
	}

	public void addCommitListener(ICommitListener commitListener) {
		this.commitListeners.add(commitListener);
	}

	public IOdbList<ICommitListener> getCommitListeners() {
		return commitListeners;
	}

	public IRefactorManager getRefactorManager() {
		return provider.getRefactorManager(this);
	}

	public void resetCommitListeners() {
		commitListeners.clear();
	}

	public boolean isLocal() {
		return isLocal;
	}

	public TransactionId getCurrentTransactionId() {
		return currentTransactionId;
	}

	public void setCurrentTransactionId(TransactionId transactionId) {
		currentTransactionId = transactionId;
	}

	public void disconnect(Object object) {
		getSession(true).removeObjectFromCache(object);
		
		//remove from cross session cache
		if(OdbConfiguration.reconnectObjectsToSession()){
			CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification()).removeObject(object);
		}

	}

	/**
	 * Reconnect an object to the current session. It connects the object and
	 * all the dependent objects (Objects accessible from the object graph of the
	 * root object
	 */
	public void reconnect(Object object) {

		if (object == null) {
			throw new ODBRuntimeException(
					NeoDatisError.RECONNECT_CAN_RECONNECT_NULL_OBJECT);
		}
		
		ICrossSessionCache crossSessionCache  = CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification());
		OID oid = crossSessionCache.getOid(object);
		
		//in some situation the user can control the disconnect and reconnect
		//so before throws an exception test if in the current session 
		//there is the object on the cache
		if(oid==null){
			throw new ODBRuntimeException(NeoDatisError.CROSS_SESSION_CACHE_NULL_OID_FOR_OBJECT.addParameter(object));
		}

		
		ObjectInfoHeader oih = objectReader.readObjectInfoHeaderFromOid(oid, false);
		
		getSession(true).addObjectToCache(oid, object, oih);

		// Retrieve Dependent Objects
		GetDependentObjectIntrospectingCallback getObjectsCallback = new GetDependentObjectIntrospectingCallback();
		ClassInfo ci = getSession(true).getMetaModel().getClassInfoFromId(
				oih.getClassInfoId());
		objectIntrospector.getMetaRepresentation(object, ci, true, null,
				getObjectsCallback);
		Collection<Object> dependentObjects = getObjectsCallback.getObjects();
		Iterator<Object> iterator = dependentObjects.iterator();

		while (iterator.hasNext()) {
			Object o = iterator.next();
			if (o != null) {
				oid = crossSessionCache.getOid(o);
				if(oid==null){
					throw new ODBRuntimeException(NeoDatisError.CROSS_SESSION_CACHE_NULL_OID_FOR_OBJECT.addParameter(o));
				}
				oih = objectReader.readObjectInfoHeaderFromOid(oid, false);
				getSession(true).addObjectToCache(oid, o, oih);
			}
		}
	}

	public ITriggerManager getTriggerManager() {
		return triggerManager;
	}

	public void addDeleteTriggerFor(String className, DeleteTrigger trigger) {
		triggerManager.addDeleteTriggerFor(className, trigger);
	}

	public void addInsertTriggerFor(String className, InsertTrigger trigger) {
		triggerManager.addInsertTriggerFor(className, trigger);
	}
	public void addOidTriggerFor(String className, OIDTrigger trigger) {
		triggerManager.addOidTriggerFor(className, trigger);
	}

	public void addSelectTriggerFor(String className, SelectTrigger trigger) {
		triggerManager.addSelectTriggerFor(className, trigger);
	}

	public void addUpdateTriggerFor(String className, UpdateTrigger trigger) {
		triggerManager.addUpdateTriggerFor(className, trigger);
	}

	public IObjectIntrospector getObjectIntrospector() {
		return objectIntrospector;
	}

	public CriteriaQuery criteriaQuery(Class clazz, ICriterion criterion) {
		CriteriaQuery q = new CriteriaQuery(clazz,criterion);
		q.setStorageEngine(this);
		if(criterion!=null){
			criterion.ready();
		}
		return q;
	}
	
	public CriteriaQuery criteriaQuery(Class clazz) {
		CriteriaQuery q = new CriteriaQuery(clazz);
		q.setStorageEngine(this);
		return q;
	}
	
}
