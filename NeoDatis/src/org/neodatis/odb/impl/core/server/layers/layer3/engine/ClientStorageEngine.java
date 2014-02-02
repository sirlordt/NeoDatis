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
package org.neodatis.odb.impl.core.server.layers.layer3.engine;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer1.introspector.IIntrospectionCallback;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.ICommitListener;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.lookup.LookupFactory;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.server.layers.layer1.IClientObjectIntrospector;
import org.neodatis.odb.core.server.layers.layer2.meta.ClientNonNativeObjectInfo;
import org.neodatis.odb.core.server.layers.layer3.engine.IMessageStreamer;
import org.neodatis.odb.core.server.layers.layer3.engine.Message;
import org.neodatis.odb.core.server.message.AddIndexMessage;
import org.neodatis.odb.core.server.message.AddIndexMessageResponse;
import org.neodatis.odb.core.server.message.CheckMetaModelCompatibilityMessage;
import org.neodatis.odb.core.server.message.CheckMetaModelCompatibilityMessageResponse;
import org.neodatis.odb.core.server.message.CloseMessage;
import org.neodatis.odb.core.server.message.CloseMessageResponse;
import org.neodatis.odb.core.server.message.CommitMessage;
import org.neodatis.odb.core.server.message.CommitMessageResponse;
import org.neodatis.odb.core.server.message.ConnectMessage;
import org.neodatis.odb.core.server.message.ConnectMessageResponse;
import org.neodatis.odb.core.server.message.CountMessage;
import org.neodatis.odb.core.server.message.CountMessageResponse;
import org.neodatis.odb.core.server.message.DeleteIndexMessage;
import org.neodatis.odb.core.server.message.DeleteIndexMessageResponse;
import org.neodatis.odb.core.server.message.DeleteObjectMessage;
import org.neodatis.odb.core.server.message.DeleteObjectMessageResponse;
import org.neodatis.odb.core.server.message.GetMessage;
import org.neodatis.odb.core.server.message.GetMessageResponse;
import org.neodatis.odb.core.server.message.GetObjectFromIdMessage;
import org.neodatis.odb.core.server.message.GetObjectFromIdMessageResponse;
import org.neodatis.odb.core.server.message.GetObjectHeaderFromIdMessage;
import org.neodatis.odb.core.server.message.GetObjectHeaderFromIdMessageResponse;
import org.neodatis.odb.core.server.message.GetObjectValuesMessage;
import org.neodatis.odb.core.server.message.GetObjectValuesMessageResponse;
import org.neodatis.odb.core.server.message.NewClassInfoListMessage;
import org.neodatis.odb.core.server.message.NewClassInfoListMessageResponse;
import org.neodatis.odb.core.server.message.RebuildIndexMessage;
import org.neodatis.odb.core.server.message.RebuildIndexMessageResponse;
import org.neodatis.odb.core.server.message.RollbackMessage;
import org.neodatis.odb.core.server.message.RollbackMessageResponse;
import org.neodatis.odb.core.server.message.StoreMessage;
import org.neodatis.odb.core.server.message.StoreMessageResponse;
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
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineAdapter;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.core.lookup.Lookups;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.list.objects.InMemoryBTreeCollection;
import org.neodatis.odb.impl.core.server.ReturnValue;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.odb.impl.main.DefaultClassRepresentation;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.net.NeoDatisIpAddress;

public class ClientStorageEngine extends StorageEngineAdapter {
	public static final String LOG_ID = "ClientStorageEngine";

	private Socket socket;

	static public int nbcalls = 0;

	static public int nbdiffcalls = 0;

	protected IMessageStreamer messageStreamer;

	protected String connectionId;

	private MetaModel localMetaModel;

	private IClientObjectIntrospector objectIntrospector;

	private IInstanceBuilder instanceBuilder;

	private IClassIntrospector classIntrospector;

	private ISession session;

	private boolean isRollbacked;

	protected IOSocketParameter parameters;

	protected ICoreProvider provider;

	/**
	 * This is a visitor used to execute some specific action(like calling
	 * 'Before Insert Trigger') when introspecting an object
	 * 
	 */
	protected IIntrospectionCallback introspectionCallbackForInsert;

	/**
	 * This is a visitor used to execute some specific action when introspecting
	 * an object
	 * 
	 */
	protected IIntrospectionCallback introspectionCallbackForUpdate;
	
	protected List<ReturnValueProcessor> returnValueProcessors;

	protected ClientStorageEngine(String hostName, int port, String baseId) {
		this(new IOSocketParameter(hostName, port, baseId, IOSocketParameter.TYPE_DATABASE, null, null));
	}

	protected ClientStorageEngine(String hostName, int port, String baseId, String user, String password) {
		this(new IOSocketParameter(hostName, port, baseId, IOSocketParameter.TYPE_DATABASE, user, password));
	}

	public ClientStorageEngine(IOSocketParameter parameters) {
		init(parameters);
	}

	public ISession buildDefaultSession() {
		session = OdbConfiguration.getCoreProvider().getClientSession(this);
		return session;
	}

	private void init(IOSocketParameter parameter) {
		this.provider = OdbConfiguration.getCoreProvider();
		this.parameters = parameter;

		ICoreProvider provider = OdbConfiguration.getCoreProvider();
		// Class Introspector must be built before as it is used by the
		// initODBConnection
		classIntrospector = provider.getClassIntrospector();

		buildDefaultSession();
		initODBConnection();

		provider.getClientServerSessionManager().addSession(session);
		objectIntrospector = (IClientObjectIntrospector) buildObjectIntrospector();
		instanceBuilder = provider.getLocalInstanceBuilder(this);

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("ODBRemote:Connected to " + parameters.getDestinationHost() + ":" + parameters.getPort() + " - connection id=" + connectionId);
		}

		triggerManager = OdbConfiguration.getCoreProvider().getLocalTriggerManager(this);

		this.introspectionCallbackForInsert = new DefaultInstrospectionCallbackForStore(this, triggerManager, false);
		this.introspectionCallbackForUpdate = new DefaultInstrospectionCallbackForStore(this, triggerManager, true);
		
		this.returnValueProcessors = new ArrayList<ReturnValueProcessor>();
		this.returnValueProcessors.add(new ChangedValueProcessor(classIntrospector));

	}

	protected void initMessageStreamer() {
		this.messageStreamer = OdbConfiguration.getCoreProvider().getMessageStreamer(parameters.getDestinationHost(), parameters.getPort(),
				parameters.getIdentification());
	}

	protected void initODBConnection() {
		String localhost = null;

		localhost = NeoDatisIpAddress.get("localhost");
		
		localhost = transformIfV6(localhost);

		initMessageStreamer();

		ConnectMessage msg = new ConnectMessage(parameters.getBaseIdentifier(), localhost, parameters.getUserName(), parameters.getPassword());

		ConnectMessageResponse rmsg = (ConnectMessageResponse) sendMessage(msg);

		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while getting a connection from ODB Server").addParameter(
					rmsg.getError()));
		}

		// The client server connection id is the server session id.
		connectionId = rmsg.getConnectionId();
		session.setMetaModel(rmsg.getMetaModel());
		session.setId(connectionId);

		setDatabaseId(rmsg.getTransactionId().getDatabaseId());
		setCurrentTransactionId(rmsg.getTransactionId());

		// Now we have to send back the meta model extracted from current java
		// classes to check the compatibility
		if (OdbConfiguration.checkModelCompatibility()) {
			// retrieve the current version of meta-model, based on the client
			// classes
			Map<String, ClassInfo> currentCIs = classIntrospector.instrospect(rmsg.getMetaModel().getAllClasses());
			// Creates the message to be sent
			CheckMetaModelCompatibilityMessage compatibilityMessage = new CheckMetaModelCompatibilityMessage(parameters.getBaseIdentifier(), rmsg
					.getConnectionId(), currentCIs);

			CheckMetaModelCompatibilityMessageResponse rmsg2 = (CheckMetaModelCompatibilityMessageResponse) sendMessage(compatibilityMessage);

			if (rmsg2.getResult().isModelHasBeenUpdated()) {
				DLogger.info("Meta-model has changed:");
				DLogger.info(rmsg2.getResult().getResults());
				// Meta model has been updated
				session.setMetaModel(rmsg2.getUpdatedMetaModel());
			}

		}

	}

	/** When IP V6 is used the host contains some ':' . As we use the ip in the transaction file name, we must remove the ':' to avoid any problem. If IPV4 used, nothing is done.
	 * @param host
	 * @return The new host ip without ':' 
	 */
	private String transformIfV6(String host) {
		return OdbString.replaceToken(host, ":", ".");
	}

	/**
	 * Opens socket send message and close.
	 * 
	 * @TODO This is bad,should keep the socket alive..
	 * 
	 * @param msg
	 * @return The response message
	 */
	public Message sendMessage(Message msg) {
		
		Message rmsg;
		try {
			messageStreamer.write(msg);
			rmsg = messageStreamer.read();
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_NET_ERROR.addParameter(OdbString.exceptionToString(e, true)));
		}

		return rmsg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#commit()
	 */
	public void commit() {
		CommitMessage msg = new CommitMessage(parameters.getBaseIdentifier(), connectionId);
		CommitMessageResponse rmsg = (CommitMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while committing database").addParameter(rmsg.getError()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#close()
	 */
	public void close() {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getIdentification()));
		}
		CloseMessage msg = new CloseMessage(parameters.getBaseIdentifier(), connectionId);
		CloseMessageResponse rmsg = (CloseMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while closing database").addParameter(rmsg.getError()));
		}
		messageStreamer.close();
		isClosed = true;
		provider.removeLocalTriggerManager(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#rollback()
	 */
	public void rollback() {
		RollbackMessage msg = new RollbackMessage(parameters.getBaseIdentifier(), connectionId);
		RollbackMessageResponse rmsg = (RollbackMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while executing rollback").addParameter(rmsg.getError()));
		}
		isRollbacked = true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#store(java.lang.Object)
	 */
	public OID store(Object object) {
		return store(StorageEngineConstant.NULL_OBJECT_ID, object);
	}

	public synchronized OID store(OID oid, Object object) {
		return internalStore(oid, object, false);
	}
	/**
	 * This method is synchronized to allow multi-threading. if not synchronized, the client/server id synchronization will fail
	 * check junit org.neodatis.odb.test.multithread.sameVm.TestSameVmClientServerMultiThread.testSameVmOneOdbInTwoThreadsX
	 */
	public synchronized OID internalStore(OID oid, Object object, boolean forceUpdate) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}
		/*
		 * // To enable auto - reconnect object loaded from previous sessions //
		 * If ODB did detect an update yet & byte code instrumentation is on and
		 * // auto reconnect is on if
		 * (OdbConfiguration.reconnectObjectsToSession()) { boolean mustUpdate =
		 * getSession(true).getCache().existObject(object);
		 * 
		 * // Even with byte instrumentation on, some internal objects may not
		 * // be instrumented ICrossSessionCache crossSessionCache =
		 * CacheFactory.getCrossSessionCache(); OID oidReconnect =
		 * crossSessionCache.getOid(object); if (!mustUpdate && oidReconnect !=
		 * null) { reconnect(object, oidReconnect); } }
		 */

		ICache cache = session.getCache();
		
		ClientNonNativeObjectInfo nnoi = objectToMetaRepresentation(object);
		boolean isUpdate = nnoi.getOid()!=null;
		if (nnoi.getOid() == StorageEngineConstant.NULL_OBJECT_ID) {
			nnoi.setOid(oid);
		}
		StoreMessage msg = new StoreMessage(parameters.getBaseIdentifier(), connectionId, nnoi, convertToOIDArray(objectIntrospector.getClientOids()));
		StoreMessageResponse rmsg = (StoreMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while storing object").addParameter(rmsg.getError()));
		}
		nnoi.setOid(rmsg.getOid());
		
		

		if (OdbConfiguration.reconnectObjectsToSession()) {

			ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification());
			crossSessionCache.addObject(object, rmsg.getOid());
		}
		
		// FIXME We should synchronize ids even in SameVM Mode
		// FIXME When byte code is on, oid and header should be set for all
		// objects, not only root one
		// Store the object in cache?
		session.getCache().addObject(rmsg.getOid(), object, nnoi.getHeader());

		// if (!parameters.clientAndServerRunInSameVM()) {
		objectIntrospector.synchronizeIds(rmsg.getClientIds(), rmsg.getServerIds());
		/*
		 * FIXME must we leave this for real client server connections? if
		 * (rmsg.isNewObject()) {
		 * nnoi.getClassInfo().getUncommittedZoneInfo().increaseNbObjects(); }
		 */
		// }
		
		for(ReturnValue rv: rmsg.getReturnValues()){
			for(ReturnValueProcessor rvp: returnValueProcessors){
				try {
					rvp.process(rv, cache.getObjectWithOid(rv.getOid()));
				} catch (Exception e) {
					throw new ODBRuntimeException(NeoDatisError.ERROR_IN_RETURN_VALUE_PROCESSOR.addParameter(rvp.getClass().getName()).addParameter(rv.toString()),e);
				}
			}
		}
		
		if(rmsg.isNewObject()){
			triggerManager.manageInsertTriggerAfter(nnoi.getClassInfo().getFullClassName(), object, nnoi.getOid());
		}else{
			triggerManager.manageUpdateTriggerAfter(nnoi.getClassInfo().getFullClassName(), nnoi, object, nnoi.getOid());
		}
		return rmsg.getOid();
	}

	private OID[] convertToOIDArray(IOdbList<OID> localOids) {
		OID[] array = new OID[localOids.size()];
		Iterator<OID> iterator = localOids.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array[i++] = iterator.next();
		}
		return array;
	}

	private ClientNonNativeObjectInfo objectToMetaRepresentation(Object object) {
		/*
		 * TODO if (session.isRollbacked()) { throw new
		 * ODBRuntimeException("Session has been rollbacked"); }
		 */

		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_STORE_NULL_OBJECT);
		}

		if (object.getClass().isArray()) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_STORE_ARRAY_DIRECTLY);
		}
		if (ODBType.isNative(object.getClass())) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_STORE_NATIVE_OBJECT_DIRECTLY);
		}

		// The object must be transformed into meta representation
		ClassInfo ci = null;

		String className = object.getClass().getName();

		// first checks if the class of this object already exist in the
		// metamodel
		if (session.getMetaModel().existClass(className)) {
			ci = session.getMetaModel().getClassInfo(className, true);
		} else {
			ClassInfoList ciList = classIntrospector.introspect(object.getClass(), true);
			addClasses(ciList);
			ci = ciList.getMainClassInfo();
		}

		boolean mustUpdate = false;

		OID oid = getSession(true).getCache().getOid(object, false);
		if (oid != null) {
			mustUpdate = true;
		}

		// The introspection callback is used to execute some specific task
		// (like calling trigger, for example) while introspecting the object
		IIntrospectionCallback callback = introspectionCallbackForInsert;
		if (mustUpdate) {
			callback = introspectionCallbackForUpdate;
		}

		// Transform the object into a ObjectInfo
		NonNativeObjectInfo nnoi = (NonNativeObjectInfo) objectIntrospector.getMetaRepresentation(object, ci, true, null, callback);

		if (mustUpdate) {
			nnoi.setOid(oid);
		}

		return (ClientNonNativeObjectInfo) nnoi;
	}

	/**
	 * TODO Remove comment public ClassInfo addClass(ClassInfo newClassInfo,
	 * boolean addDependentClasses) { ClassInfoList ciList = new
	 * ClassInfoList(newClassInfo); ciList = addClasses(ciList); return
	 * session.getMetaModel
	 * ().getClassInfo(newClassInfo.getFullClassName(),true); }
	 */
	/*
	 * Adds a list of class to the metamodel, call the server to update meta
	 * model
	 */
	public ClassInfoList addClasses(ClassInfoList classInfoList) {

		// Call server to add the class info list to the meta model on the
		// server and retrieve class info id from server
		NewClassInfoListMessage msg = new NewClassInfoListMessage(parameters.getBaseIdentifier(), connectionId, classInfoList);
		NewClassInfoListMessageResponse rmsg = (NewClassInfoListMessageResponse) sendMessage(msg);

		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while adding  new Class Info list").addParameter(rmsg.getError()));
		}

		MetaModel metaModel = MetaModel.fromClassInfos(rmsg.getClassInfos());
		session.setMetaModel(metaModel);

		// Updates the main class info from new meta model
		String mainClassName = classInfoList.getMainClassInfo().getFullClassName();
		classInfoList.setMainClassInfo(metaModel.getClassInfo(mainClassName, true));
		return classInfoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjects(java.lang.Class)
	 */
	public <T> Objects<T> getObjects(Class clazz) {
		return getObjects(clazz, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjects(java.lang.Class, boolean)
	 */
	public <T> Objects<T> getObjects(Class clazz, boolean inMemory) {
		return getObjects(clazz,inMemory,-1,-1);
	}

	@Override
	public <T> Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex, int endIndex) {
		IQuery query = new CriteriaQuery(clazz);
		return getObjects(query, inMemory, startIndex, endIndex);
	}

	private <T> Objects<T> buildInstances(Objects<NonNativeObjectInfo> metaObjects) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Building instances for " + metaObjects.size() + " meta objects");
		}
		// FIXME Do we need to create the btree collection?
		Objects<T> list = new InMemoryBTreeCollection<T>(metaObjects.size(), OrderByConstants.ORDER_BY_ASC);
		Iterator<NonNativeObjectInfo> iterator = metaObjects.iterator();
		T o = null;
		NonNativeObjectInfo nnoi = null;
		int i = 0;
		while (iterator.hasNext()) {
			nnoi = iterator.next();
			Object obj = instanceBuilder.buildOneInstance(nnoi); 
			o = (T) obj;
			list.add(o);
			i++;
		}
		return list;
	}

	private Object buildOneInstance(NonNativeObjectInfo nnoi) {
		Object o = instanceBuilder.buildOneInstance(nnoi);
		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#count(java.lang.Class)
	 */
	public long count(CriteriaQuery query) {

		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}
		CountMessage msg = new CountMessage(parameters.getBaseIdentifier(), connectionId, query);
		CountMessageResponse rmsg = (CountMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while counting objects").addParameter(rmsg.getError()));
		}
		return rmsg.getNbObjects();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#delete(java.lang.Object)
	 */
	public OID delete(Object object, boolean cascade) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}

		if(cascade){
			throw new ODBRuntimeException(NeoDatisError.NOT_YET_SUPPORTED.addParameter("delete cascade in client server mode"));
		}
		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_DELETE_NULL_OBJECT);
		}

		OID oid = getObjectId(object, false);

		if (oid == null && OdbConfiguration.reconnectObjectsToSession()) {
			oid = CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification()).getOid(object);
		}

		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_DOES_NOT_EXIST_IN_CACHE_FOR_DELETE.addParameter(object.getClass().getName())
					.addParameter(object));
		}
		internalDeleteObjectWithOid(oid,cascade);

		if (OdbConfiguration.reconnectObjectsToSession()) {
			CacheFactory.getCrossSessionCache(this.getBaseIdentification().getIdentification()).removeObject(object);
		}
		return oid;
	}

	/**
	 * Delete an object from the database with the id
	 * 
	 * @param oid
	 *            The object id to be deleted @
	 */
	public void deleteObjectWithOid(OID oid, boolean cascade) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}
		internalDeleteObjectWithOid(oid,cascade);

		if (OdbConfiguration.reconnectObjectsToSession()) {
			CacheFactory.getCrossSessionCache(this.getBaseIdentification().getIdentification()).removeOid(oid);
		}
	}

	/**
	 * Delete an object from the database with the id
	 * 
	 * @param oid
	 *            The object id to be deleted @
	 * @neodatisee
	 */
	public void internalDeleteObjectWithOid(OID oid, boolean cascade) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}
		DeleteObjectMessage msg = new DeleteObjectMessage(parameters.getBaseIdentifier(), connectionId, oid, cascade);
		DeleteObjectMessageResponse rmsg = (DeleteObjectMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while deleting objects").addParameter(rmsg.getError()));
		}
		ICache cache = getSession(true).getCache();
		cache.markIdAsDeleted(oid);
		cache.removeObjectWithOid(oid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.main.IODB#getObjects(org.neodatis.odb.core.query.IQuery)
	 */
	public <T> Objects<T> getObjects(IQuery query) {
		return getObjects(query, true, -1, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.main.IODB#getObjects(org.neodatis.odb.core.query.IQuery,
	 * boolean)
	 */
	public <T> Objects<T> getObjects(IQuery query, boolean inMemory) {
		return getObjects(query, inMemory, -1, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.main.IODB#getObjects(org.neodatis.odb.core.query.IQuery,
	 * boolean, int, int)
	 */
	public <T> Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex) {
		if (isRollbacked) {
			throw new ODBRuntimeException(NeoDatisError.ODB_HAS_BEEN_ROLLBACKED);
		}

		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}

		GetMessage msg = new GetMessage(parameters.getBaseIdentifier(), connectionId, query, inMemory, startIndex, endIndex);
		GetMessageResponse rmsg = (GetMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while getting objects").addParameter(rmsg.getError()));
		}
		// Sets execution plan
		query.setExecutionPlan(rmsg.getPlan());
		return (Objects<T>) buildInstances(rmsg.getMetaObjects());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.main.IODB#getObjects(org.neodatis.odb.core.query.IQuery,
	 * boolean, int, int)
	 */
	public Values getValues(IValuesQuery query, int startIndex, int endIndex) {
		if (isRollbacked) {
			throw new ODBRuntimeException(NeoDatisError.ODB_HAS_BEEN_ROLLBACKED);
		}

		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}

		GetObjectValuesMessage msg = new GetObjectValuesMessage(parameters.getBaseIdentifier(), connectionId, query, startIndex, endIndex);
		Object o = sendMessage(msg);
		GetObjectValuesMessageResponse rmsg = (GetObjectValuesMessageResponse) o;
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while getting object values").addParameter(rmsg.getError()));
		}
		Values values = rmsg.getValues();
		// Sets execution plan
		query.setExecutionPlan(rmsg.getPlan());
		// When object values API is used, Lazy list have to get a reference to
		// the client storage engine
		// This reference is obtained via lookup
		LookupFactory.get(getSession(true).getId()).set(Lookups.INSTANCE_BUILDER, instanceBuilder);
		return values;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjectId(java.lang.Object)
	 */
	public OID getObjectId(Object object, boolean throwExceptionIfDoesNotExist) {
		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.ODB_CAN_NOT_RETURN_OID_OF_NULL_OBJECT);
		}

		// If byte code instrumentation is on, just check if current object has
		// the OID

		if (OdbConfiguration.reconnectObjectsToSession()) {
			ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification());
			if (crossSessionCache.getOid(object) != null) {
				return crossSessionCache.getOid(object);
			}
		}

		OID oid = getSession(true).getCache().getOid(object, false);
		if (oid == null && throwExceptionIfDoesNotExist) {
			throw new ODBRuntimeException(NeoDatisError.UNKNOWN_OBJECT_TO_GET_OID.addParameter(object.toString()));
		}
		return oid;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#getObjectFromId(long)
	 */
	public Object getObjectFromOid(OID oid) {

		if (oid == null) {
			throw new ODBRuntimeException(NeoDatisError.CAN_NOT_GET_OBJECT_FROM_NULL_OID);
		}

		GetObjectFromIdMessage message = new GetObjectFromIdMessage(parameters.getBaseIdentifier(), connectionId, oid);
		GetObjectFromIdMessageResponse rmsg = (GetObjectFromIdMessageResponse) sendMessage(message);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_ERROR.addParameter("Error while getting object from id :" + rmsg.getError()));
		}
		return buildOneInstance(rmsg.getMetaRepresentation());

	}

	/**
	 * FIXME : not very efficient because it retrieves the full object
	 * 
	 */
	public NonNativeObjectInfo getMetaObjectFromOid(OID oid) {
		GetObjectFromIdMessage message = new GetObjectFromIdMessage(parameters.getBaseIdentifier(), connectionId, oid);
		GetObjectFromIdMessageResponse rmsg = (GetObjectFromIdMessageResponse) sendMessage(message);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_ERROR.addParameter("Error while getting object from id :" + rmsg.getError()));
		}
		return rmsg.getMetaRepresentation();
	}

	public ObjectInfoHeader getObjectInfoHeaderFromOid(OID oid, boolean useCache) {

		GetObjectHeaderFromIdMessage message = new GetObjectHeaderFromIdMessage(parameters.getBaseIdentifier(), connectionId, oid,useCache);
		GetObjectHeaderFromIdMessageResponse rmsg = (GetObjectHeaderFromIdMessageResponse) sendMessage(message);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_ERROR.addParameter("Error while getting object header from id :" + rmsg.getError()));
		}
		return rmsg.getObjectInfoHeader();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.main.IODB#defragmentTo(java.lang.String)
	 */
	public void defragmentTo(String newFileName) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.defragmentTo"));
	}

	public ClassRepresentation getClassRepresentation(Class clazz) {
		String fullClassName = clazz.getName();
		ClassInfo classInfo = session.getMetaModel().getClassInfo(fullClassName, false);
		if (classInfo == null) {
			ClassInfoList ciList = classIntrospector.introspect(clazz, false);
			classInfo = ciList.getMainClassInfo();
		}
		return new DefaultClassRepresentation(null, classInfo);
	}

	/** or shutdown hook */
	public void run() {
		if (!isClosed) {
			DLogger.debug("ODBFactory has not been closed and VM is exiting : force ODBFactory close");
			close();
		}
	}

	public void addUpdateTrigger(UpdateTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.addUpdateTrigger"));
	}

	public void addInsertTrigger(InsertTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.addInsertTrigger"));
	}

	public void addOidTrigger(OIDTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.addOidTrigger"));
	}

	public void addDeleteTrigger(DeleteTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.addDeleteTrigger"));
	}

	public void addSelectTrigger(SelectTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.addSelectTrigger"));
	}

	public IBaseIdentification getBaseIdentification() {
		return parameters;
	}

	public <T> Objects<T> getObjectInfos(IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnOjects) {
		GetMessage msg = new GetMessage(parameters.getBaseIdentifier(), connectionId, query, inMemory, startIndex, endIndex);
		GetMessageResponse rmsg = (GetMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_ERROR.addParameter("Error while getting objects :" + rmsg.getError()));
		}
		return (Objects<T>) rmsg.getMetaObjects();

	}

	public <T> Objects<T> getObjectInfos(String fullClassName, boolean inMemory, int startIndex, int endIndex, boolean returnOjects) {
		return getObjectInfos(new CriteriaQuery(fullClassName), inMemory, startIndex, endIndex, returnOjects);
	}

	public ISession getSession(boolean throwExceptionIfDoesNotExist) {
		return session;
	}

	public OID updateObject(NonNativeObjectInfo nnoi2, boolean forceUpdate) {
		if (isClosed) {
			throw new ODBRuntimeException(NeoDatisError.ODB_IS_CLOSED.addParameter(parameters.getBaseIdentifier()));
		}
		ClientNonNativeObjectInfo nnoi = (ClientNonNativeObjectInfo) nnoi2;
		StoreMessage msg = new StoreMessage(parameters.getBaseIdentifier(), connectionId, nnoi, convertToOIDArray(objectIntrospector.getClientOids()));
		StoreMessageResponse rmsg = (StoreMessageResponse) sendMessage(msg);
		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.SERVER_SIDE_ERROR.addParameter("Error while storing object").addParameter(rmsg.getError()));
		}
		nnoi.setOid(rmsg.getOid());

		// if (!parameters.clientAndServerRunInSameVM()) {
		objectIntrospector.synchronizeIds(rmsg.getClientIds(), rmsg.getServerIds());
		/*
		 * FIXME must we leave this for real client server connections? if
		 * (rmsg.isNewObject()) {
		 * nnoi.getClassInfo().getUncommittedZoneInfo().increaseNbObjects(); }
		 */
		// }
		return rmsg.getOid();
	}

	public OID writeObjectInfo(OID oid, NonNativeObjectInfo aoi, long position, boolean updatePointers) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED
				.addParameter("writeObjectInfo from meta representation not implemented in ClientStorageEngine"));
	}

	public void addSession(ISession session, boolean readMetamodel) {
		this.session = session;
	}

	public void addIndexOn(String className, String indexName, String[] indexFields, boolean verbose, boolean acceptMultipleValuesForSameKey) {
		AddIndexMessage message = new AddIndexMessage(parameters.getBaseIdentifier(), connectionId, className, indexName, indexFields,
				acceptMultipleValuesForSameKey, verbose);
		AddIndexMessageResponse rmsg = (AddIndexMessageResponse) sendMessage(message);

		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_ERROR.addParameter(indexName + ":" + rmsg.getError()));
		}
	}

	public void rebuildIndex(String className, String indexName, boolean verbose) {
		RebuildIndexMessage message = new RebuildIndexMessage(parameters.getBaseIdentifier(), connectionId, className, indexName, verbose);
		RebuildIndexMessageResponse rmsg = (RebuildIndexMessageResponse) sendMessage(message);

		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_ERROR.addParameter(indexName + ":" + rmsg.getError()));
		}
	}

	public void deleteIndex(String className, String indexName, boolean verbose) {
		DeleteIndexMessage message = new DeleteIndexMessage(parameters.getBaseIdentifier(), connectionId, className, indexName, verbose);
		DeleteIndexMessageResponse rmsg = (DeleteIndexMessageResponse) sendMessage(message);

		if (rmsg.hasError()) {
			throw new ODBRuntimeException(NeoDatisError.CLIENT_SERVER_ERROR.addParameter(indexName + ":" + rmsg.getError()));
		}
	}

	public void addCommitListener(ICommitListener commitListener) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.addCommitListener"));
	}

	public IOdbList<ICommitListener> getCommitListeners() {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.getCommitListeners"));
	}

	public IRefactorManager getRefactorManager() {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.getRefactorManager"));
	}

	public void resetCommitListeners() {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("ClientStorageEngine.resetCommitListeners"));

	}

	public boolean isLocal() {
		return false;
	}

	public ITriggerManager getTriggerManager() {
		return triggerManager;
	}

	public void disconnect(Object object) {
		getSession(true).removeObjectFromCache(object);

		// remove from cross session cache
		if (OdbConfiguration.reconnectObjectsToSession()) {
			CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification()).removeObject(object);
		}

	}

	/**
	 * Reconnect an object to the current session. It connects the object and
	 * all the dependent objects (Objects accessible from the object graph of
	 * the root object
	 * 
	 * <pre>
	 * 	This code is duplicated here because we don't have ObjectReader on client side,
	 * so all needed object reader methods are implement in the ClientStorageEngine class
	 * </pre>
	 */
	public void reconnect(Object object, OID oid) {

		if (object == null) {
			throw new ODBRuntimeException(NeoDatisError.RECONNECT_CAN_RECONNECT_NULL_OBJECT);
		}

		if (!OdbConfiguration.reconnectObjectsToSession()) {
			throw new ODBRuntimeException(NeoDatisError.RECONNECT_ONLY_WITH_BYTE_CODE_AGENT_CONFIGURED);
		}

		ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(getBaseIdentification().getIdentification());

		ObjectInfoHeader oih = getObjectInfoHeaderFromOid(oid,true);

		getSession(true).addObjectToCache(oid, object, oih);

		// Retrieve Dependent Objects
		GetDependentObjectIntrospectingCallback getObjectsCallback = new GetDependentObjectIntrospectingCallback();
		ClassInfo ci = getSession(true).getMetaModel().getClassInfoFromId(oih.getClassInfoId());
		objectIntrospector.getMetaRepresentation(object, ci, true, null, getObjectsCallback);
		Collection<Object> dependentObjects = getObjectsCallback.getObjects();
		Iterator<Object> iterator = dependentObjects.iterator();

		while (iterator.hasNext()) {
			Object o = iterator.next();

			if (o != null) {
				oid = crossSessionCache.getOid(o);
				if (oid == null) {
					throw new ODBRuntimeException(NeoDatisError.CROSS_SESSION_CACHE_NULL_OID_FOR_OBJECT.addParameter(o));
				}
				oih = getObjectInfoHeaderFromOid(oid,true);
				getSession(true).addObjectToCache(oid, o, oih);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineAdapter#
	 * addDeleteTrigger(java.lang.Class,
	 * org.neodatis.odb.core.trigger.DeleteTrigger)
	 */
	@Override
	public void addDeleteTrigger(Class clazz, DeleteTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("addDeleteTrigger not implemented in ClientStorageEngine"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineAdapter#
	 * addInsertTrigger(java.lang.Class,
	 * org.neodatis.odb.core.trigger.InsertTrigger)
	 */
	@Override
	public void addInsertTrigger(Class clazz, InsertTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("addInsertTrigger not implemented in ClientStorageEngine"));
	}
	
	public void addOidTrigger(Class clazz, OIDTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("addOidTrigger not implemented in ClientStorageEngine"));

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineAdapter#
	 * addSelectTrigger(java.lang.Class,
	 * org.neodatis.odb.core.trigger.SelectTrigger)
	 */
	@Override
	public void addSelectTrigger(Class clazz, SelectTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("addSelectTrigger not implemented in ClientStorageEngine"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineAdapter#
	 * addUpdateTrigger(java.lang.Class,
	 * org.neodatis.odb.core.trigger.UpdateTrigger)
	 */
	@Override
	public void addUpdateTrigger(Class clazz, UpdateTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("addUpdateTrigger not implemented in ClientStorageEngine"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IStorageEngine#buildObjectIntrospector
	 * ()
	 */
	public IObjectIntrospector buildObjectIntrospector() {
		return provider.getClientObjectIntrospector(this, connectionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IStorageEngine#buildObjectReader()
	 */
	public IObjectReader buildObjectReader() {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("buildObjectReader not implemented in ClientStorageEngine"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IStorageEngine#buildObjectWriter()
	 */
	public IObjectWriter buildObjectWriter() {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("buildObjectWriter not implemented in ClientStorageEngine"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IStorageEngine#buildTriggerManager()
	 */
	public ITriggerManager buildTriggerManager() {
		throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("buildTriggerManager not implemented in ClientStorageEngine"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IStorageEngine#getObjectIntrospector
	 * ()
	 */
	public IObjectIntrospector getObjectIntrospector() {
		return objectIntrospector;
	}

}
