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
package org.neodatis.odb.core.server.connection;

import java.util.List;
import java.util.Map;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.CheckMetaModelResult;
import org.neodatis.odb.core.query.QueryManager;
import org.neodatis.odb.core.query.execution.GenericQueryExecutor;
import org.neodatis.odb.core.server.layers.layer3.IODBServerExt;
import org.neodatis.odb.core.server.layers.layer3.engine.Command;
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
import org.neodatis.odb.core.server.message.DeleteBaseMessage;
import org.neodatis.odb.core.server.message.DeleteBaseMessageResponse;
import org.neodatis.odb.core.server.message.DeleteIndexMessage;
import org.neodatis.odb.core.server.message.DeleteIndexMessageResponse;
import org.neodatis.odb.core.server.message.DeleteObjectMessage;
import org.neodatis.odb.core.server.message.DeleteObjectMessageResponse;
import org.neodatis.odb.core.server.message.ErrorMessage;
import org.neodatis.odb.core.server.message.GetMessage;
import org.neodatis.odb.core.server.message.GetMessageResponse;
import org.neodatis.odb.core.server.message.GetObjectFromIdMessage;
import org.neodatis.odb.core.server.message.GetObjectFromIdMessageResponse;
import org.neodatis.odb.core.server.message.GetObjectHeaderFromIdMessage;
import org.neodatis.odb.core.server.message.GetObjectHeaderFromIdMessageResponse;
import org.neodatis.odb.core.server.message.GetObjectValuesMessage;
import org.neodatis.odb.core.server.message.GetObjectValuesMessageResponse;
import org.neodatis.odb.core.server.message.GetSessionsMessage;
import org.neodatis.odb.core.server.message.GetSessionsMessageResponse;
import org.neodatis.odb.core.server.message.NewClassInfoListMessage;
import org.neodatis.odb.core.server.message.NewClassInfoListMessageResponse;
import org.neodatis.odb.core.server.message.RebuildIndexMessage;
import org.neodatis.odb.core.server.message.RebuildIndexMessageResponse;
import org.neodatis.odb.core.server.message.RollbackMessage;
import org.neodatis.odb.core.server.message.RollbackMessageResponse;
import org.neodatis.odb.core.server.message.StoreMessage;
import org.neodatis.odb.core.server.message.StoreMessageResponse;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.server.transaction.ServerSession;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.IOUtil;
import org.neodatis.tool.mutex.Mutex;
import org.neodatis.tool.mutex.MutexFactory;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.io.OdbFile;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**
 * The abstract class that manages the client server connections. It is message
 * based and it manages all the client server messages.
 * 
 * @author olivier s
 * 
 */
public abstract class ClientServerConnection {
	private static final String LOG_ID = "ClientServerConnection";

	private static int nbMessages = 0;

	protected boolean connectionIsUp;

	protected String baseIdentifier;

	protected String connectionId;

	protected boolean debug;

	protected boolean automaticallyCreateDatabase;

	protected IODBServerExt server;

	protected ISessionManager sessionManager;

	private static final String COMMIT_CLOSE_CONNECT_MUTEX_NAME = "COMMIT_CLOSE_CONNECT_MUTEX_NAME";

	private static final String COUNT_MUTEX_NAME = "COUNT_MUTEX_NAME";

	private static final String GET_OBJECT_HEADER_FROM_ID_MUTEX_NAME = "GET_OBJECT_HEADER_FROM_ID_MUTEX_NAME";

	private static final String GET_OBJECT_FROM_ID_MUTEX_NAME = "GET_OBJECT_FROM_ID_MUTEX_NAME";

	private static final String GET_VALUES_MUTEX_NAME = "GET_VALUES_MUTEX_NAME";

	private static final String GET_OBJECTS_MUTEX_NAME = "GET_OBJECTS_MUTEX_NAME";

	private static final String DELETE_OBJECT_MUTEX_NAME = "DELETE_OBJECT_MUTEX_NAME";

	private static final String ADD_CLASS_INFO_LIST_MUTEX_NAME = "ADD_CLASS_INFO_LIST_MUTEX_NAME";

	private static final String STORE_MUTEX_NAME = "STORE_MUTEX_NAME";

	public ClientServerConnection(IODBServerExt server, boolean automaticallyCreateDatabase) {
		this.debug = OdbConfiguration.logServerConnections();
		this.automaticallyCreateDatabase = automaticallyCreateDatabase;
		this.server = server;
		this.sessionManager = OdbConfiguration.getCoreProvider().getClientServerSessionManager();
	}

	public abstract String getName();

	/**
	 * The main method. It is the message dispatcher. Checks the message type
	 * and calls the right message handler.
	 * 
	 * @param message
	 * @return
	 */
	public Message manageMessage(Message message) {
		long start = OdbTime.getCurrentTimeInMs();

		try {
			nbMessages++;
			int commandId = message.getCommandId();

			switch (commandId) {
			case Command.CONNECT:
				return manageConnectCommand((ConnectMessage) message);
			case Command.GET:
				return manageGetObjectsCommand((GetMessage) message);
			case Command.GET_OBJECT_FROM_ID:
				return manageGetObjectFromIdCommand((GetObjectFromIdMessage) message);
			case Command.GET_OBJECT_HEADER_FROM_ID:
				return manageGetObjectHeaderFromIdCommand((GetObjectHeaderFromIdMessage) message);
			case Command.STORE:
				return manageStoreCommand((StoreMessage) message);
			case Command.DELETE_OBJECT:
				return manageDeleteObjectCommand((DeleteObjectMessage) message);
			case Command.CLOSE:
				return manageCloseCommand((CloseMessage) message);
			case Command.COMMIT:
				return manageCommitCommand((CommitMessage) message);
			case Command.ROLLBACK:
				return manageRollbackCommand((RollbackMessage) message);
			case Command.DELETE_BASE:
				return manageDeleteBaseCommand((DeleteBaseMessage) message);
			case Command.GET_SESSIONS:
				return manageGetSessionsCommand((GetSessionsMessage) message);
			case Command.ADD_UNIQUE_INDEX:
				return manageAddIndexCommand((AddIndexMessage) message);
			case Command.REBUILD_INDEX:
				return manageRebuildIndexCommand((RebuildIndexMessage) message);
			case Command.DELETE_INDEX:
				return manageDeleteIndexCommand((DeleteIndexMessage) message);
			case Command.ADD_CLASS_INFO_LIST:
				return manageAddClassInfoListCommand((NewClassInfoListMessage) message);
			case Command.COUNT:
				return manageCountCommand((CountMessage) message);
			case Command.GET_OBJECT_VALUES:
				return manageGetObjectValuesCommand((GetObjectValuesMessage) message);
			case Command.CHECK_META_MODEL_COMPATIBILITY:
				return manageCheckMetaModelCompatibilityCommand((CheckMetaModelCompatibilityMessage) message);

			default:
				break;
			}

			StringBuffer buffer = new StringBuffer();
			buffer.append("ODBServer.ConnectionThread:command ").append(commandId).append(" not implemented");
			return new ErrorMessage("?", "?", buffer.toString());

		} finally {
			long end = OdbTime.getCurrentTimeInMs();
			if (debug) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("[").append(nbMessages).append("] ");
				buffer.append(message.toString()).append(" - Thread=").append(getName()).append(" - connectionId =").append(connectionId)
						.append(" - duration=").append((end - start));
				DLogger.info(buffer);
			}

		}

	}

	public ServerSession getSession(String baseIdentifier) {
		return (ServerSession) sessionManager.getSession(baseIdentifier, true);
	}

	/**
	 * Used to check if client classes meta model is compatible with the meta
	 * model persisted in the database
	 * 
	 * @param message
	 * @return
	 */
	private CheckMetaModelCompatibilityMessageResponse manageCheckMetaModelCompatibilityCommand(CheckMetaModelCompatibilityMessage message) {
		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		try {
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new CheckMetaModelCompatibilityMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			ServerSession session = getSession(baseIdentifier);
			IStorageEngine engine = connection.getStorageEngine();
			Map<String, ClassInfo> currentCIs = message.getCurrentCIs();

			CheckMetaModelResult result = engine.checkMetaModelCompatibility(currentCIs);

			MetaModel updatedMetaModel = null;
			if (result.isModelHasBeenUpdated()) {
				updatedMetaModel = session.getMetaModel().duplicate();
				// This is to avoid message streamer think that meta model did
				// not change
				clearMessageStreamerCache();
			}
			// If meta model has been updated, returns it to clients
			return new CheckMetaModelCompatibilityMessageResponse(baseIdentifier, message.getConnectionId(), result, updatedMetaModel);
		} catch (Exception e) {
			DLogger.error(baseIdentifier + ":Server error while closing", e);
			return new CheckMetaModelCompatibilityMessageResponse(baseIdentifier, message.getConnectionId(), OdbString.exceptionToString(e,
					false));
		}

	}

	/**
	 * Manage Index Message
	 * 
	 * @param message
	 * @return
	 */
	private Message manageAddIndexCommand(AddIndexMessage message) {
		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		try {
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new AddIndexMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());

			IStorageEngine engine = connection.getStorageEngine();
			engine.addIndexOn(message.getClassName(), message.getIndexName(), message.getIndexFieldNames(), message.isVerbose(), message
					.acceptMultipleValuesForSameKey());
		} catch (Exception e) {
			DLogger.error(baseIdentifier + ":Server error while closing", e);
			return new AddIndexMessageResponse(baseIdentifier, message.getConnectionId(), OdbString.exceptionToString(e, false));
		}

		return new AddIndexMessageResponse(baseIdentifier, message.getConnectionId());
	}

	/**
	 * Rebuild an index Index Message
	 * 
	 * @param message
	 * @return
	 */
	private Message manageRebuildIndexCommand(RebuildIndexMessage message) {
		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		try {
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new RebuildIndexMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());

			IStorageEngine engine = connection.getStorageEngine();
			engine.rebuildIndex(message.getClassName(), message.getIndexName(), message.isVerbose());
		} catch (Exception e) {
			DLogger.error(baseIdentifier + ":Server error while closing", e);
			return new RebuildIndexMessageResponse(baseIdentifier, message.getConnectionId(), OdbString.exceptionToString(e, false));
		}

		return new RebuildIndexMessageResponse(baseIdentifier, message.getConnectionId());
	}

	/**
	 * Delete an index Index Message
	 * 
	 * @param message
	 * @return
	 */
	private Message manageDeleteIndexCommand(DeleteIndexMessage message) {
		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		try {
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new DeleteIndexMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());

			IStorageEngine engine = connection.getStorageEngine();
			engine.deleteIndex(message.getClassName(), message.getIndexName(), message.isVerbose());
		} catch (Exception e) {
			DLogger.error(baseIdentifier + ":Server error while closing", e);
			return new DeleteIndexMessageResponse(baseIdentifier, message.getConnectionId(), OdbString.exceptionToString(e, false));
		}

		return new DeleteIndexMessageResponse(baseIdentifier, message.getConnectionId());
	}

	private ConnectionManager getConnectionManager(String baseIdentifier) throws Exception {
		return getConnectionManager(baseIdentifier, null, null, false);
	}

	/**
	 * Gets the connection manager for the base
	 * 
	 * @param baseIdentifier
	 * @param user
	 * @param password
	 * @param returnNullIfDoesNotExit
	 * @return
	 * @throws Exception
	 */
	private ConnectionManager getConnectionManager(String baseIdentifier, String user, String password, boolean returnNullIfDoesNotExit)
			throws Exception {

		try {

			// Gets the connection manager for this base identifier
			ConnectionManager connectionManager = (ConnectionManager) server.getConnectionManagers().get(baseIdentifier);
			if (connectionManager == null && returnNullIfDoesNotExit) {
				return null;
			}
			if (connectionManager == null && automaticallyCreateDatabase) {
				server.addBase(baseIdentifier, baseIdentifier, user, password);
				connectionManager = (ConnectionManager) server.getConnectionManagers().get(baseIdentifier);
			}
			if (connectionManager == null && !automaticallyCreateDatabase) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return null;
			}
			return connectionManager;
		} finally {
		}

	}


	/**
	 * manages the Close Message
	 * 
	 * @param message
	 * @return
	 */
	private Message manageCloseCommand(CloseMessage message) {
		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(COMMIT_CLOSE_CONNECT_MUTEX_NAME);

			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new CloseMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_CLOSE);
			connection.close();
			connectionManager.removeConnection(connection);
			sessionManager.removeSession(baseIdentifier);

			connectionIsUp = false;
			return new CloseMessageResponse(baseIdentifier, message.getConnectionId());

		} catch (Exception e) {
			DLogger.error(baseIdentifier + ":Server error while closing", e);
			return new CloseMessageResponse(baseIdentifier, message.getConnectionId(), OdbString.exceptionToString(e, false));
		} finally {
			if (mutex != null) {
				mutex.release("close");
			}
		}

	}

	private Message manageGetSessionsCommand(GetSessionsMessage message) {
		try {
			List<String> descriptions = sessionManager.getSessionDescriptions(server.getConnectionManagers());
			return new GetSessionsMessageResponse(descriptions);
		} catch (Exception e) {
			DLogger.error("Server error while getting session descriptions", e);
			return new GetSessionsMessageResponse(OdbString.exceptionToString(e, false));
		}
	}

	private Message manageCommitCommand(CommitMessage message) {
		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(COMMIT_CLOSE_CONNECT_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new CommitMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_COMMIT);

			connection.commit();
			return new CommitMessageResponse(baseIdentifier, message.getConnectionId(), true);
		} catch (Exception e) {
			DLogger.error(baseIdentifier + ":Server error while commiting", e);
			return new CommitMessageResponse(baseIdentifier, message.getConnectionId(), OdbString.exceptionToString(e, false));
		} finally {
			if (mutex != null) {
				mutex.release("commit");
			}
			connection.endCurrentAction();
		}

	}

	private Message manageRollbackCommand(RollbackMessage message) {
		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(COMMIT_CLOSE_CONNECT_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new RollbackMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_ROLLBACK);

			connection.rollback();
		} catch (Exception e) {
			DLogger.error(baseIdentifier + ":Server error while rollbacking", e);
			return new RollbackMessageResponse(baseIdentifier, message.getConnectionId(), OdbString.exceptionToString(e, false));

		} finally {
			if (mutex != null) {
				mutex.release("rollback");
			}
			connection.endCurrentAction();
		}
		return new RollbackMessageResponse(baseIdentifier, message.getConnectionId(), true);

	}

	/**
	 * manage the store command. The store command can be an insert(oid==null)
	 * or an update(oid!=null)
	 * 
	 * If insert get the base mutex IF update, first get the mutex of the oid to
	 * update then get the base mutex, to avoid dead lock in case of concurrent
	 * update.
	 * 
	 * @param message
	 * @return
	 */

	private Message manageStoreCommand(StoreMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		Mutex mutex = null;
		OID oid = message.getNnoi().getOid();
		ServerSession session = null;
		try {
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new StoreMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());

			session = getSession(baseIdentifier);
			IStorageEngine engine = connection.getStorageEngine();
			session.setClientIds(message.getClientIds());
			boolean objectIsNew = oid == StorageEngineConstant.NULL_OBJECT_ID;

			if (objectIsNew) {
				connection.setCurrentAction(ConnectionAction.ACTION_INSERT);
				mutex = MutexFactory.get(baseIdentifier).acquire("store");
				oid = engine.writeObjectInfo(StorageEngineConstant.NULL_OBJECT_ID, message.getNnoi(),
						StorageEngineConstant.POSITION_NOT_INITIALIZED, false);
			} else {
				connection.setCurrentAction(ConnectionAction.ACTION_UPDATE);
				// If object is not new, ODB is going to execute an Update.
				// Here we must lock the object with the oid to avoid a
				// concurrent update
				// This is done by creating a special mutex with the base
				// identifier and the oid.
				// This mutex will be kept in the connection and only released
				// when committing
				// or rollbacking the connection
				connection.lockObjectWithOid(oid);
				// If object lock is ok, then get the mutex of the database
				mutex = MutexFactory.get(baseIdentifier).acquire(STORE_MUTEX_NAME);
				// If oid is not -1, the object already exist, we must update
				oid = engine.updateObject(message.getNnoi(), false);
			}

			return new StoreMessageResponse(baseIdentifier, message.getConnectionId(), oid, objectIsNew, message.getClientIds(), session
					.getServerIds(), session.getValuesToReturn());
		} catch (Exception e) {
			if (oid != null) {
				try {
					connection.unlockObjectWithOid(message.getNnoi().getOid());
				} catch (Exception e1) {
					DLogger.error("Error while unlocking object with oid " + oid + " : " + OdbString.exceptionToString(e1, true));
				}
			}
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while storing object " + message.getNnoi();
			DLogger.error(msg, e);
			return new StoreMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("store");
			}
			connection.endCurrentAction();
			if(session!=null){
				session.clearValuesToReturn();
			}
		}
	}

	private Message manageAddClassInfoListCommand(NewClassInfoListMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(ADD_CLASS_INFO_LIST_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new StoreMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());

			ServerSession session = getSession(baseIdentifier);
			IStorageEngine engine = connection.getStorageEngine();

			ClassInfoList ciList = message.getClassInfoList();
			ciList = engine.getObjectWriter().addClasses(ciList);
			// here we must create a new list with all class info because
			// Serialization hold object references
			// In this case, it holds the reference of the previous class info
			// list. Serialization thinks object did not change so it will send
			// the reference
			// instead of the new object. Creating the new list force the
			// serialization
			// mechanism to send object
			IOdbList<ClassInfo> allClassInfos = new OdbArrayList<ClassInfo>();
			allClassInfos.addAll(session.getMetaModel().getAllClasses());
			NewClassInfoListMessageResponse r = new NewClassInfoListMessageResponse(baseIdentifier, message.getConnectionId(),
					allClassInfos);
			session.resetClassInfoIds();
			return r;
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while adding new Class Info List" + message.getClassInfoList();
			DLogger.error(msg, e);
			return new NewClassInfoListMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("addClassInfoList");
			}
		}
	}

	private Message manageDeleteObjectCommand(DeleteObjectMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(DELETE_OBJECT_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier, null, null, true);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new StoreMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_DELETE);

			ServerSession session = (ServerSession) sessionManager.getSession(baseIdentifier, true);
			IStorageEngine engine = connection.getStorageEngine();
			engine.deleteObjectWithOid(message.getOid(),message.isCascade());
			return new DeleteObjectMessageResponse(baseIdentifier, message.getConnectionId(), message.getOid());
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while deleting object " + message.getOid();
			DLogger.error(msg, e);
			return new DeleteObjectMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("deleteObject");
			}
			connection.endCurrentAction();
		}
	}

	private Message manageDeleteBaseCommand(DeleteBaseMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();
		ConnectionManager connectionManager = null;
		try {
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier, null, null, true);

			StringBuffer log = new StringBuffer();
			String fileName = message.getBaseIdentifier();
			OdbFile file = new OdbFile(fileName);

			if (connectionManager == null) {
				try {
					if (debug) {
						log.append("Server:Connection manager is null Deleting base " + file.getFullPath()).append(" | exists?").append(file.exists());
					}
					if (file.exists()) {
						boolean b = IOUtil.deleteFile(file.getFullPath());
						if (debug) {
							log.append("| deleted=").append(b);
						}
						b = !file.exists();

						if (debug) {
							log.append("| deleted=").append(b);
						}
						if (b) {
							return new DeleteBaseMessageResponse(baseIdentifier);
						}
						return new DeleteBaseMessageResponse(baseIdentifier, "[1] could not delete base " + file.getFullPath());
					}
					return new DeleteBaseMessageResponse(baseIdentifier);
				} finally {
					if (debug) {
						DLogger.info(log.toString());
					}
				}
			}

			IStorageEngine engine = connectionManager.getStorageEngine();
			if (!engine.isClosed()) {
				// Simulate a session
				sessionManager.addSession(OdbConfiguration.getCoreProvider().getServerSession(engine, "temp"));
				// engine.rollback();
				engine.close();
				sessionManager.removeSession(baseIdentifier);
				removeConnectionManager(baseIdentifier);

			}
			boolean b = IOUtil.deleteFile(fileName); 
			log.append("| deleted=").append(b);
			if (b) {
				return new DeleteBaseMessageResponse(baseIdentifier);
			}
			return new DeleteBaseMessageResponse(baseIdentifier, "[2] could not delete base "
					+ new OdbFile(message.getBaseIdentifier()).getFullPath());
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while deleting base " + message.getBaseIdentifier();
			DLogger.error(msg, e);
			return new DeleteBaseMessageResponse(baseIdentifier, msg + ":\n" + se);
		} finally {
			sessionManager.removeSession(baseIdentifier);
			removeConnectionManager(baseIdentifier);
			connectionIsUp = false;
		}
	}

	private void removeConnectionManager(String baseId) {
		server.getConnectionManagers().remove(baseId);

	}

	private Message manageGetObjectsCommand(GetMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;

		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(GET_OBJECTS_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new GetMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_SELECT);

			if (OdbConfiguration.lockObjectsOnSelect()) {
				// first lock the class
				String fullClassName = QueryManager.getFullClassName(message.getQuery());
				//connection.lockClass(fullClassName);
			}

			IStorageEngine engine = connection.getStorageEngine();
			Objects<NonNativeObjectInfo> metaObjects = null;
			metaObjects = engine.getObjectInfos(message.getQuery(), true, message.getStartIndex(), message.getEndIndex(), false);
			
			if (OdbConfiguration.lockObjectsOnSelect()) {
				// then lock objects
				while(metaObjects.hasNext()){
					OID oid = metaObjects.next().getOid();
					connection.lockObjectWithOid(oid);	
					System.out.println("locking object with oid " + oid);
				}
				// and unlock class
				String fullClassName = QueryManager.getFullClassName(message.getQuery());
				//connection.unlockClass(fullClassName);
			}

			
			// message.getQuery().setStorageEngine(null);
			return new GetMessageResponse(baseIdentifier, message.getConnectionId(), metaObjects, message.getQuery().getExecutionPlan());
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while getting objects for query " + message.getQuery();
			DLogger.error(msg, e);

			return new GetMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("getObjects");
			}
			connection.endCurrentAction();
		}
	}

	private Message manageGetObjectValuesCommand(GetObjectValuesMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;

		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(GET_VALUES_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new GetMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_SELECT);

			IStorageEngine engine = connection.getStorageEngine();
			Values values = engine.getValues(message.getQuery(), message.getStartIndex(), message.getEndIndex());
			return new GetObjectValuesMessageResponse(baseIdentifier, message.getConnectionId(), values, message.getQuery()
					.getExecutionPlan());
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while getting objects for query " + message.getQuery();
			DLogger.error(msg, e);

			return new GetObjectValuesMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("getObjects");
			}
			connection.endCurrentAction();
		}
	}

	private Message manageGetObjectFromIdCommand(GetObjectFromIdMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;

		IConnection connection = null;
		OID oid = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(GET_OBJECT_FROM_ID_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new GetObjectFromIdMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_SELECT);

			IStorageEngine engine = connection.getStorageEngine();
			oid = message.getOid();
			NonNativeObjectInfo nnoi = engine.getMetaObjectFromOid(oid);
			return new GetObjectFromIdMessageResponse(baseIdentifier, message.getConnectionId(), nnoi);
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while getting object of id " + oid;
			DLogger.error(msg, e);

			return new GetObjectFromIdMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("getObjectFromId");
			}
			connection.endCurrentAction();
		}
	}

	private Message manageGetObjectHeaderFromIdCommand(GetObjectHeaderFromIdMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;

		IConnection connection = null;
		OID oid = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(GET_OBJECT_HEADER_FROM_ID_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new GetObjectHeaderFromIdMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());
			connection.setCurrentAction(ConnectionAction.ACTION_SELECT);

			IStorageEngine engine = connection.getStorageEngine();
			oid = message.getOid();
			ObjectInfoHeader oih = engine.getObjectInfoHeaderFromOid(oid, message.useCache());
			// the oih.duplicate method is called to create a new instance of
			// the ObjectInfoHeader becasue of
			// the java Serialization problem : Serialization will check the
			// reference of the object and only send the reference if the object
			// has already
			// been changed. => creating a new will avoid this problem
			return new GetObjectHeaderFromIdMessageResponse(baseIdentifier, message.getConnectionId(), oih.duplicate());
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while getting object of id " + oid;
			DLogger.error(msg, e);

			return new GetObjectHeaderFromIdMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("getObjectFromId");
			}
			connection.endCurrentAction();
		}
	}

	private Message manageCountCommand(CountMessage message) {

		// Gets the base identifier
		String baseIdentifier = message.getBaseIdentifier();

		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;

		IConnection connection = null;
		Mutex mutex = null;
		try {
			mutex = MutexFactory.get(baseIdentifier).acquire(COUNT_MUTEX_NAME);
			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("ODBServer.ConnectionThread:Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new GetObjectFromIdMessageResponse(baseIdentifier, message.getConnectionId(), buffer.toString());
			}

			connection = connectionManager.getConnection(message.getConnectionId());

			IStorageEngine engine = connection.getStorageEngine();
			CriteriaQuery query = message.getCriteriaQuery();
			long nbObjects = engine.count(query);
			return new CountMessageResponse(baseIdentifier, message.getConnectionId(), nbObjects);
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while counting objects for " + message.getCriteriaQuery();
			DLogger.error(msg, e);

			return new CountMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("count");
			}
		}
	}

	private Message manageConnectCommand(ConnectMessage message) {

		// Gets the base identifier
		baseIdentifier = message.getBaseIdentifier();
		// Gets the connection manager for this base identifier
		ConnectionManager connectionManager = null;
		IConnection connection = null;
		Mutex mutex = null;
		try {

			mutex = MutexFactory.get(baseIdentifier).acquire(COMMIT_CLOSE_CONNECT_MUTEX_NAME);

			// Gets the connection manager for this base identifier
			connectionManager = getConnectionManager(baseIdentifier, message.getUser(), message.getPassword(), false);

			if (connectionManager == null) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("Base ").append(baseIdentifier).append(" is not registered on this server!");
				return new ConnectMessageResponse(baseIdentifier, "?", buffer.toString());
			}

			String ip = message.getIp();
			long dateTime = message.getDateTime();

			connection = connectionManager.newConnection(ip, dateTime, connectionManager.getNbConnections());
			connection.setCurrentAction(ConnectionAction.ACTION_CONNECT);

			connectionId = connection.getId();
			// Creates a new session for this connection
			ISession session = new ServerSession(connection.getStorageEngine(), connectionId);
			IStorageEngine engine = connection.getStorageEngine();
			// adds the session to the storage engine (it will be associated to
			// the current thread
			// The add session sets the correct meta model
			engine.addSession(session, true);
			TransactionId transactionId = engine.getCurrentTransactionId();

			if (debug) {
				DLogger.info(new StringBuffer("Connection from ").append(ip).append(" - cid=").append(connection.getId()).append(
						" - session=").append(session.getId()).append(" - Base Id=").append(baseIdentifier).toString());
			}

			// Returns the meta-model to the client
			MetaModel metaModel = engine.getSession(true).getMetaModel();
			ConnectMessageResponse cmr = new ConnectMessageResponse(baseIdentifier, connection.getId(), metaModel, transactionId);

			return cmr;
		} catch (Exception e) {
			String se = OdbString.exceptionToString(e, false);
			String msg = baseIdentifier + ":Error while connecting to  " + message.getBaseIdentifier();
			DLogger.error(msg, e);
			return new ConnectMessageResponse(baseIdentifier, message.getConnectionId(), msg + ":\n" + se);
		} finally {
			if (mutex != null) {
				mutex.release("connect");
			}
			if (connection != null) {
				connection.endCurrentAction();
			}
		}
	}

	public abstract void clearMessageStreamerCache();

}
