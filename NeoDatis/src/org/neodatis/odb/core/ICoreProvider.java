package org.neodatis.odb.core;

import java.io.IOException;
import java.net.Socket;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer1.introspector.IObjectIntrospector;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IBufferedIO;
import org.neodatis.odb.core.layers.layer3.IIdManager;
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

/**
 * This is the default Core Object Provider.
 * 
 * 
 * @author olivier
 *
 */
public interface ICoreProvider extends ITwoPhaseInit {
	
	IStorageEngine getClientStorageEngine(IBaseIdentification baseIdentification);
	IServerStorageEngine getServerStorageEngine(IBaseIdentification baseIdentification);
	IByteArrayConverter getByteArrayConverter();
	
	/**TODO Return a list of IO to enable replication or other IO mechanism
	 * Used by the FileSystemInterface to actual write/read byte to underlying storage
	 * @param name The name of the buffered io
	 * @param parameters The parameters that define the buffer
	 * @param bufferSize The size of the buffers
	 * @return The buffer implementation
	 * @
	 */
	public IBufferedIO getIO(String name,IBaseIdentification parameters,int bufferSize);
	
	/**
	 * Returns the Local Instance Builder
	 */
	public IInstanceBuilder getLocalInstanceBuilder(IStorageEngine engine);
	public IInstanceBuilder getServerInstanceBuilder(IStorageEngine engine);
	
	public IObjectIntrospector getLocalObjectIntrospector(IStorageEngine engine);
	public IClientObjectIntrospector getClientObjectIntrospector(IStorageEngine engine, String connectionId);
	public IObjectIntrospector getServerObjectIntrospector(IStorageEngine engine);
	public IObjectWriter getClientObjectWriter(IStorageEngine engine);
	public IObjectReader getClientObjectReader(IStorageEngine engine) ;
	public ITriggerManager getLocalTriggerManager(IStorageEngine engine);
	public ITriggerManager getServerTriggerManager(IStorageEngine engine);
	public IClassIntrospector getClassIntrospector();
	public IIdManager getClientIdManager(IStorageEngine engine);
	public IIdManager getServerIdManager(IStorageEngine engine);
	public IObjectWriter getServerObjectWriter(IStorageEngine engine) ;
	public IObjectReader getServerObjectReader(IStorageEngine engine) ;
	
	// Transaction
	public ISessionManager getClientServerSessionManager();
	public ITransaction getTransaction(ISession session, IFileSystemInterface fsi) ;
	public IWriteAction getWriteAction(long position, byte[] bytes);
	public ISession getLocalSession(IStorageEngine engine) ;
	public ISession getClientSession(IStorageEngine engine) ;
	public ISession getServerSession(IStorageEngine engine, String sessionId) ;
	
	public IRefactorManager getRefactorManager(IStorageEngine engine);
	
	// For query result handler
	
	/** Returns the query result handler for normal query result (that return a collection of objects)
	 * 
	 */
	public IMatchingObjectAction getCollectionQueryResultAction(IStorageEngine engine, IQuery query,boolean inMemory, boolean returnObjects);
	
	// OIDs
	public OID getObjectOID(long objectOid, long classOid );
	public OID getClassOID(long oid);
	public OID getExternalObjectOID(long objectOid, long classOid );
	public OID getExternalClassOID(long oid);
	public IClassPool getClassPool();
	
	public void resetClassDefinitions();
	/**To retrieve the message streamer. used for client server communication
	 * @param socket
	 * @return
	 * @throws IOException 
	 */
	IMessageStreamer getMessageStreamer(Socket socket);
	IMessageStreamer getMessageStreamer(String host, int port, String name);
	public void removeLocalTriggerManager(IStorageEngine engine);
}
