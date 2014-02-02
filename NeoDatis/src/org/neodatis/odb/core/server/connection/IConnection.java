/**
 * 
 */
package org.neodatis.odb.core.server.connection;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;

/**
 * @author olivier
 *
 */
public interface IConnection {

	String getId();

	IStorageEngine getStorageEngine();

	void close() throws Exception;

	void commit() throws Exception;

	void unlockObjectWithOid(OID oid) throws Exception;

	void rollback() throws Exception;

	boolean lockObjectWithOid(OID oid) throws InterruptedException;
	boolean lockClass(String fullClassName) throws InterruptedException;
	void unlockClass(String fullClassName) throws Exception;

	void setCurrentAction(int action);

	void endCurrentAction();

	String getDescription();

}