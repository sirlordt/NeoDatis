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

package org.neodatis.odb.impl;

import java.net.Socket;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IBufferedIO;
import org.neodatis.odb.core.layers.layer3.IIdManager;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.core.server.layers.layer1.IClientObjectIntrospector;
import org.neodatis.odb.core.server.layers.layer3.engine.IMessageStreamer;
import org.neodatis.odb.core.server.layers.layer3.engine.IServerStorageEngine;
import org.neodatis.odb.core.server.transaction.ISessionManager;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITransaction;
import org.neodatis.odb.core.transaction.IWriteAction;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.impl.core.layers.layer1.introspector.AndroidClassIntrospector;
import org.neodatis.odb.impl.core.layers.layer1.introspector.DefaultClassIntrospector;
import org.neodatis.odb.impl.core.layers.layer1.introspector.LocalObjectIntrospector;
import org.neodatis.odb.impl.core.layers.layer2.instance.LocalInstanceBuilder;
import org.neodatis.odb.impl.core.layers.layer2.instance.ODBClassPool;
import org.neodatis.odb.impl.core.layers.layer2.instance.ServerInstanceBuilder;
import org.neodatis.odb.impl.core.layers.layer3.buffer.MultiBufferedFileIO;
import org.neodatis.odb.impl.core.layers.layer3.engine.DefaultByteArrayConverter;
import org.neodatis.odb.impl.core.layers.layer3.engine.LocalObjectWriter;
import org.neodatis.odb.impl.core.layers.layer3.engine.LocalStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.ObjectReader;
import org.neodatis.odb.impl.core.layers.layer3.oid.DefaultIdManager;
import org.neodatis.odb.impl.core.layers.layer3.refactor.DefaultRefactorManager;
import org.neodatis.odb.impl.core.oid.OdbClassOID;
import org.neodatis.odb.impl.core.oid.OdbObjectOID;
import org.neodatis.odb.impl.core.query.criteria.CollectionQueryResultAction;
import org.neodatis.odb.impl.core.server.layers.layer1.ClientObjectIntrospector;
import org.neodatis.odb.impl.core.server.layers.layer1.ServerObjectIntrospector;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ClientStorageEngine;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ServerObjectReader;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ServerObjectWriter;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ServerStorageEngine;
import org.neodatis.odb.impl.core.server.layers.layer3.oid.DefaultServerIdManager;
import org.neodatis.odb.impl.core.server.transaction.ServerSession;
import org.neodatis.odb.impl.core.server.transaction.SessionManager;
import org.neodatis.odb.impl.core.server.trigger.DefaultServerTriggerManager;
import org.neodatis.odb.impl.core.transaction.ClientSession;
import org.neodatis.odb.impl.core.transaction.DefaultTransaction;
import org.neodatis.odb.impl.core.transaction.DefaultWriteAction;
import org.neodatis.odb.impl.core.transaction.LocalSession;
import org.neodatis.odb.impl.core.trigger.DefaultTriggerManager;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbSystem;
import org.neodatis.tool.wrappers.io.MessageStreamerBuilder;
import org.neodatis.tool.wrappers.io.OdbFile;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * The is the default implementation of ODB
 * 
 * @author olivier
 * 
 */
public class DefaultCoreProvider implements ICoreProvider {

	private static IClassPool classPool = new ODBClassPool();
	private static IByteArrayConverter byteArrayConverter = new DefaultByteArrayConverter();
	private static IClassIntrospector classIntrospector = null;
	private static ISessionManager sessionManager = new SessionManager();
	private static Map<IStorageEngine, ITriggerManager> triggerManagers = new OdbHashMap<IStorageEngine, ITriggerManager>();

	public void init2() {
		byteArrayConverter.init2();

		if (osIsAndroid()) {
			// One feature is currently not supported on Android : dynamic empty
			// constructor creation
			classIntrospector = new AndroidClassIntrospector();
		} else {
			classIntrospector = new DefaultClassIntrospector();
		}

		classIntrospector.init2();
		sessionManager.init2();
	}

	private boolean osIsAndroid() {
		String javaVendor = OdbSystem.getProperty("java.vendor");
		if (javaVendor != null && javaVendor.equals("The Android Project")) {
			return true;
		}
		return false;
	}

	public void resetClassDefinitions() {
		classIntrospector.reset();
		classPool.reset();

	}

	public IStorageEngine getClientStorageEngine(
			IBaseIdentification baseIdentification) {
		if (baseIdentification instanceof IOFileParameter) {
			return new LocalStorageEngine(baseIdentification);
		}
		if (baseIdentification instanceof IOSocketParameter) {
			IOSocketParameter p = (IOSocketParameter) baseIdentification;
			return new ClientStorageEngine(p);
		}
		throw new ODBRuntimeException(NeoDatisError.UNSUPPORTED_IO_TYPE
				.addParameter(baseIdentification.toString()));
	}

	public IObjectWriter getClientObjectWriter(IStorageEngine engine) {
		return new LocalObjectWriter(engine);
	}

	public IObjectReader getClientObjectReader(IStorageEngine engine) {
		return new ObjectReader(engine);
	}

	public IObjectWriter getServerObjectWriter(IStorageEngine engine) {
		return new ServerObjectWriter(engine);
	}

	public IObjectReader getServerObjectReader(IStorageEngine engine) {
		return new ServerObjectReader(engine);
	}

	public IServerStorageEngine getServerStorageEngine(
			IBaseIdentification baseIdentification) {
		return new ServerStorageEngine(baseIdentification);
	}

	public IByteArrayConverter getByteArrayConverter() {
		return byteArrayConverter;
	}

	/**
	 * TODO Return a list of IO to enable replication or other IO mechanism Used
	 * by the FileSystemInterface to actual write/read byte to underlying
	 * storage
	 * 
	 * @param name
	 *            The name of the buffered io
	 * @param parameters
	 *            The parameters that define the buffer
	 * @param bufferSize
	 *            The size of the buffers
	 * @return The buffer implementation @
	 */
	public IBufferedIO getIO(String name, IBaseIdentification parameters,
			int bufferSize) {
		if (parameters instanceof IOFileParameter) {

			IOFileParameter fileParameters = (IOFileParameter) parameters;
			// Guarantee that file directory structure exist
			OdbFile f = new OdbFile(fileParameters.getFileName());
			OdbFile fparent = f.getParentFile();
			if (fparent != null && !fparent.exists()) {
				DLogger.debug(fparent + " does not exist, creating it.");
				fparent.mkdirs();
			}

			return new MultiBufferedFileIO(OdbConfiguration.getNbBuffers(),
					name, fileParameters.getFileName(), fileParameters
							.canWrite(), bufferSize);
		}
		/*
		 * if(parameters.getClass() == IOSocketParameter.class){
		 * IOSocketParameter socketParameters = (IOSocketParameter) parameters;
		 * return new MultiBufferedNetIO(Configuration.getNbBuffers(),
		 * name,session
		 * ,socketParameters.getBaseIdentifier(),socketParameters.getDestinationHost
		 * (),socketParameters.getPort(),bufferSize); }
		 */
		throw new ODBRuntimeException(NeoDatisError.UNSUPPORTED_IO_TYPE
				.addParameter(parameters.toString()));
	}

	/**
	 * Returns the Local Instance Builder
	 */
	public IInstanceBuilder getLocalInstanceBuilder(IStorageEngine engine) {
		return new LocalInstanceBuilder(engine);
	}

	/**
	 * Returns the Server Instance Builder
	 */
	public IInstanceBuilder getServerInstanceBuilder(IStorageEngine engine) {
		return new ServerInstanceBuilder(engine);
	}

	public IObjectIntrospector getLocalObjectIntrospector(IStorageEngine engine) {
		return new LocalObjectIntrospector(engine);
	}

	public IClientObjectIntrospector getClientObjectIntrospector(
			IStorageEngine engine, String connectionId) {
		return new ClientObjectIntrospector(engine,connectionId);
	}

	public IObjectIntrospector getServerObjectIntrospector(IStorageEngine engine) {
		return new ServerObjectIntrospector(engine);
	}

	public ITriggerManager getLocalTriggerManager(IStorageEngine engine) {
		// First check if trigger manager has already been built for the engine
		ITriggerManager triggerManager = triggerManagers.get(engine);
		if(triggerManager!=null){
			return triggerManager;
		}
		triggerManager = new DefaultTriggerManager(engine);
		triggerManagers.put(engine, triggerManager);
		return triggerManager;
	}
	
	public void removeLocalTriggerManager(IStorageEngine engine) {
		triggerManagers.remove(engine);
	}

	public ITriggerManager getServerTriggerManager(IStorageEngine engine) {
		return new DefaultServerTriggerManager(engine);
	}

	public IClassIntrospector getClassIntrospector() {
		return classIntrospector;
	}

	public IIdManager getClientIdManager(IStorageEngine engine) {
		return new DefaultIdManager(engine.getObjectWriter(), engine
				.getObjectReader(), engine.getCurrentIdBlockPosition(), engine
				.getCurrentIdBlockNumber(), engine.getCurrentIdBlockMaxOid());
	}

	public IIdManager getServerIdManager(IStorageEngine engine) {
		return new DefaultServerIdManager(engine.getObjectWriter(), engine
				.getObjectReader(), engine.getCurrentIdBlockPosition(), engine
				.getCurrentIdBlockNumber(), engine.getCurrentIdBlockMaxOid());
	}

	// Transaction related
	public ISessionManager getClientServerSessionManager() {
		return sessionManager;
	}

	public IWriteAction getWriteAction(long position, byte[] bytes) {
		return new DefaultWriteAction(position, bytes);
	}

	public ITransaction getTransaction(ISession session,
			IFileSystemInterface fsi) {
		return new DefaultTransaction(session, fsi);
	}

	public ISession getLocalSession(IStorageEngine engine) {
		return new LocalSession(engine);
	}

	public ISession getClientSession(IStorageEngine engine) {
		return new ClientSession(engine);
	}

	public ISession getServerSession(IStorageEngine engine, String sessionId) {
		return new ServerSession(engine, sessionId);
	}

	public IRefactorManager getRefactorManager(IStorageEngine engine) {
		return new DefaultRefactorManager(engine);
	}

	// For query result handler

	public IMatchingObjectAction getCollectionQueryResultAction(
			IStorageEngine engine, IQuery query, boolean inMemory,
			boolean returnObjects) {
		return new CollectionQueryResultAction(query, inMemory, engine,
				returnObjects, engine.getObjectReader().getInstanceBuilder());
	}

	// OIDs
	public OID getObjectOID(long objectOid, long classOid) {
		return new OdbObjectOID(objectOid);
	}

	public OID getClassOID(long oid) {
		return new OdbClassOID(oid);
	}

	public OID getExternalObjectOID(long objectOid, long classOid) {
		return new OdbObjectOID(objectOid);
	}

	public OID getExternalClassOID(long oid) {
		return new OdbClassOID(oid);
	}

	public IClassPool getClassPool() {
		return classPool;
	}

	/** (non-Javadoc)
	 * @see org.neodatis.odb.core.ICoreProvider#getMessageStreamer(java.net.Socket)
	 * 
	 */
	public IMessageStreamer getMessageStreamer(Socket socket) {
		return MessageStreamerBuilder.getMessageStreamer(socket);
	}
	/**
	 * 
	 */
	public IMessageStreamer getMessageStreamer(String host, int port, String name) {
		return MessageStreamerBuilder.getMessageStreamer(host, port, name);
	}
	
	

}
