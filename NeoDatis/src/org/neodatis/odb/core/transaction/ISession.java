package org.neodatis.odb.core.transaction;

import java.util.Observer;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;

public interface ISession extends Observer{

	public  ICache getCache();

	public  ITmpCache getTmpCache();

	public  void rollback();

	public  void close();

	public  void clearCache();

	public  boolean isRollbacked();

	public  void clear();

	public  IStorageEngine getStorageEngine();

	public  boolean transactionIsPending();

	public  void commit();
	/** To indicate that session has already been committed once*/
	public boolean hasBeenCommitted();
	public void setHasBeenCommitted(boolean b);

	public  ITransaction getTransaction();

	public  void setFileSystemInterfaceToApplyTransaction(IFileSystemInterface fsi);

	public  String getBaseIdentification();

	public  MetaModel getMetaModel();
	public  void setMetaModel(MetaModel metaModel2);

	public String getId();
	public void setId(String id);

	public  void removeObjectFromCache(Object object);

	/**
	 * Add these information on a session cache.
	 * @param oid. This parameter can not be <code> null </code>
	 * @param object. This parameter can not be <code> null </code>
	 * @param oih. This parameter can not be <code> null </code>
	 */
	public  void addObjectToCache(OID oid, Object object, ObjectInfoHeader oih); 

}