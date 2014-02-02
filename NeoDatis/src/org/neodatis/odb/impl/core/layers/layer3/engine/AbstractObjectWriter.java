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
package org.neodatis.odb.impl.core.layers.layer3.engine;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.btree.IBTree;
import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.CIZoneInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer2.meta.ObjectReference;
import org.neodatis.odb.core.layers.layer2.meta.compare.ArrayModifyElement;
import org.neodatis.odb.core.layers.layer2.meta.compare.ChangedAttribute;
import org.neodatis.odb.core.layers.layer2.meta.compare.ChangedNativeAttributeAction;
import org.neodatis.odb.core.layers.layer2.meta.compare.ChangedObjectReferenceAttributeAction;
import org.neodatis.odb.core.layers.layer2.meta.compare.IObjectInfoComparator;
import org.neodatis.odb.core.layers.layer2.meta.compare.NewNonNativeObjectAction;
import org.neodatis.odb.core.layers.layer2.meta.compare.SetAttributeToNullAction;
import org.neodatis.odb.core.layers.layer3.IDTypes;
import org.neodatis.odb.core.layers.layer3.IIdManager;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ICrossSessionCache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.impl.core.layers.layer2.meta.compare.ObjectInfoComparator;
import org.neodatis.odb.impl.core.layers.layer2.meta.history.InsertHistoryInfo;
import org.neodatis.odb.impl.core.layers.layer3.block.BlockStatus;
import org.neodatis.odb.impl.core.layers.layer3.block.BlockTypes;
import org.neodatis.odb.impl.core.layers.layer3.oid.IDStatus;
import org.neodatis.odb.impl.core.oid.OdbClassOID;
import org.neodatis.odb.impl.core.oid.OdbObjectOID;
import org.neodatis.odb.impl.core.oid.TransactionIdImpl;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.odb.impl.core.transaction.DefaultWriteAction;
import org.neodatis.odb.impl.tool.Cryptographer;
import org.neodatis.odb.impl.tool.UUID;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.DisplayUtility;
import org.neodatis.tool.wrappers.OdbComparable;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.list.IOdbList;

/**
 * Manage all IO writing
 * 
 * @author olivier s
 * 
 * 
 * 
 * 
 */
public abstract class AbstractObjectWriter implements IObjectWriter {
	static final private int NON_NATIVE_HEADER_BLOCK_SIZE = ODBType.INTEGER.getSize() + ODBType.BYTE.getSize() + ODBType.LONG.getSize();
	static final private int NATIVE_HEADER_BLOCK_SIZE = ODBType.INTEGER.getSize() + ODBType.BYTE.getSize() + ODBType.INTEGER.getSize()
			+ ODBType.BOOLEAN.getSize();
	static private byte[] NATIVE_HEADER_BLOCK_SIZE_BYTE = null;

	protected static int nbInPlaceUpdates = 0;

	protected static int nbNormalUpdates = 0;

	public static final String LOG_ID = "ObjectWriter";

	public static final String LOG_ID_DEBUG = "ObjectWriter.debug";

	protected IStorageEngine storageEngine;

	protected IObjectReader objectReader;

	public IClassIntrospector classIntrospector;

	// public ISession session;

	public IFileSystemInterface fsi;

	// Just for display matters
	private int currentDepth;

	protected IIdManager idManager;

	/** To manage triggers */
	protected ITriggerManager triggerManager;

	protected IByteArrayConverter byteArrayConverter;

	private static int nbCallsToUpdate;

	private boolean isLocalMode;
	
	protected IObjectInfoComparator comparator;

	public AbstractObjectWriter(IStorageEngine engine) {
		this.storageEngine = engine;
		this.objectReader = storageEngine.getObjectReader();
		this.isLocalMode = this.storageEngine.isLocal();

		ICoreProvider provider = OdbConfiguration.getCoreProvider();

		this.byteArrayConverter = provider.getByteArrayConverter();
		this.classIntrospector = provider.getClassIntrospector();

		NATIVE_HEADER_BLOCK_SIZE_BYTE = byteArrayConverter.intToByteArray(NATIVE_HEADER_BLOCK_SIZE);
		comparator = new ObjectInfoComparator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#getSession
	 * ()
	 */
	public abstract ISession getSession();

	public abstract IFileSystemInterface buildFSI();

	/**
	 * The init2 method is the two phase init implementation The
	 * FileSystemInterface depends on the session creation which is done by
	 * subclasses after the ObjectWriter constructor So we can not execute the
	 * buildFSI in the constructor as it would result in a non initialized
	 * object reference (the session)
	 */
	public void init2() {
		this.fsi = buildFSI();
	}

	public void afterInit() {
		this.objectReader = storageEngine.getObjectReader();
		this.idManager = OdbConfiguration.getCoreProvider().getClientIdManager(storageEngine);
	}

	/**
	 * Creates the header of the file
	 * 
	 * @param creationDate
	 *            The creation date
	 * @param user
	 *            The user
	 * @param password
	 *            The password @
	 */
	public void createEmptyDatabaseHeader(long creationDate, String user, String password) {
		writeEncrytionFlag(false, false);
		writeVersion(false);
		DatabaseId databaseId = writeDatabaseId(creationDate, false);
		writeReplicationFlag(false, false);
		// Create the first Transaction Id
		TransactionId tid = new TransactionIdImpl(databaseId, 0, 1);
		storageEngine.setCurrentTransactionId(tid);
		writeLastTransactionId(tid);
		writeNumberOfClasses(0, false);
		writeFirstClassInfoOID(StorageEngineConstant.NULL_OBJECT_ID, false);
		writeLastODBCloseStatus(false, false);
		writeDatabaseCharacterEncoding(false);
		writeUserAndPassword(user, password, false);

		// This is the position of the first block id. But it will always
		// contain the position of the current id block
		fsi.writeLong(StorageEngineConstant.DATABASE_HEADER_FIRST_ID_BLOCK_POSITION, false, "current id block position",
				DefaultWriteAction.DIRECT_WRITE_ACTION);
		// Write an empty id block
		writeIdBlock(-1, OdbConfiguration.getIdBlockSize(), BlockStatus.BLOCK_NOT_FULL, 1, -1, false);

		storageEngine
				.setCurrentIdBlockInfos(StorageEngineConstant.DATABASE_HEADER_FIRST_ID_BLOCK_POSITION, 1, OIDFactory.buildObjectOID(0));
		
		// force IO sync
		fsi.flush();
	}

	public void writeUserAndPassword(String user, String password, boolean writeInTransaction) {
		if (user != null && password != null) {
			String encryptedPassword = Cryptographer.encrypt(password);
			fsi.writeBoolean(true, writeInTransaction, "has user and password");
			if (user.length() > 20) {
				throw new ODBRuntimeException(NeoDatisError.USER_NAME_TOO_LONG.addParameter(user).addParameter(20));
			}
			if (password.length() > 20) {
				throw new ODBRuntimeException(NeoDatisError.PASSWORD_TOO_LONG.addParameter(20));
			}
			fsi.writeString(user, writeInTransaction, true, 50);
			fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_DATABASE_PASSWORD, writeInTransaction);
			fsi.writeString(encryptedPassword, writeInTransaction, true, 50);
		} else {
			fsi.writeBoolean(false, writeInTransaction, "database without user and password");
			fsi.writeString("no-user", writeInTransaction, true, 50);
			fsi.writeString("no-password", writeInTransaction, true, 50);
		}
	}

	/** Write the encryption flag : 0= no encryption, 1=with encryption */
	public void writeEncrytionFlag(boolean useEncryption, boolean writeInTransaction) {
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_USE_ENCRYPTION_POSITION, writeInTransaction);
		fsi.writeByte(useEncryption ? StorageEngineConstant.WITH_ENCRYPTION : StorageEngineConstant.NO_ENCRYPTION, writeInTransaction,
				"encryption flag");
	}

	/** Write the version in the database file */
	public void writeVersion(boolean writeInTransaction) {
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_VERSION_POSITION, writeInTransaction);
		fsi.writeInt(StorageEngineConstant.CURRENT_FILE_FORMAT_VERSION, writeInTransaction, "database file format version");
		storageEngine.setVersion(StorageEngineConstant.CURRENT_FILE_FORMAT_VERSION);
	}

	public DatabaseId writeDatabaseId(long creationDate, boolean writeInTransaction) {
		DatabaseId databaseId = UUID.getDatabaseId(creationDate);
		fsi.writeLong(databaseId.getIds()[0], writeInTransaction, "database id 1/4", DefaultWriteAction.DIRECT_WRITE_ACTION);
		fsi.writeLong(databaseId.getIds()[1], writeInTransaction, "database id 2/4", DefaultWriteAction.DIRECT_WRITE_ACTION);
		fsi.writeLong(databaseId.getIds()[2], writeInTransaction, "database id 3/4", DefaultWriteAction.DIRECT_WRITE_ACTION);
		fsi.writeLong(databaseId.getIds()[3], writeInTransaction, "database id 4/4", DefaultWriteAction.DIRECT_WRITE_ACTION);
		storageEngine.setDatabaseId(databaseId);
		return databaseId;
	}

	/** Write the replication flag : 0= No replication, 1= Use replication */
	public void writeReplicationFlag(boolean useReplication, boolean writeInTransaction) {
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_USE_REPLICATION_POSITION, writeInTransaction);
		fsi.writeByte(useReplication ? StorageEngineConstant.WITH_REPLICATION : StorageEngineConstant.NO_REPLICATION, writeInTransaction,
				"replication flag");
	}

	/**
	 * Write the current transaction Id, out of transaction
	 * 
	 * @param transactionId
	 *            @
	 */
	public void writeLastTransactionId(TransactionId transactionId) {
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_LAST_TRANSACTION_ID, false);
		// FIXME This should always be written directly without transaction
		fsi.writeLong(transactionId.getId1(), false, "last transaction id 1/2", DefaultWriteAction.DIRECT_WRITE_ACTION);
		fsi.writeLong(transactionId.getId2(), false, "last transaction id 2/2", DefaultWriteAction.DIRECT_WRITE_ACTION);
	}

	/** Write the number of classes in meta-model */
	public void writeNumberOfClasses(long number, boolean writeInTransaction) {
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_NUMBER_OF_CLASSES_POSITION, writeInTransaction);
		fsi.writeLong(number, writeInTransaction, "nb classes", DefaultWriteAction.DIRECT_WRITE_ACTION);
	}

	/** Write the status of the last odb close */
	public void writeLastODBCloseStatus(boolean ok, boolean writeInTransaction) {
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_LAST_CLOSE_STATUS_POSITION, writeInTransaction);
		fsi.writeBoolean(ok, writeInTransaction, "odb last close status");
	}

	/** Write the database characterEncoding */
	public void writeDatabaseCharacterEncoding(boolean writeInTransaction) {
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_DATABASE_CHARACTER_ENCODING_POSITION, writeInTransaction);
		if (OdbConfiguration.hasEncoding()) {
			fsi.writeString(OdbConfiguration.getDatabaseCharacterEncoding(), writeInTransaction, true, 50);
		} else {
			fsi.writeString(StorageEngineConstant.NO_ENCODING, writeInTransaction, false, 50);
		}
	}

	/**
	 * Writes the header of a block of type ID - a block that contains ids of
	 * objects and classes
	 * 
	 * @param position
	 *            Position at which the block must be written, if -1, take the
	 *            next available position
	 * @param idBlockSize
	 *            The block size in byte
	 * @param blockStatus
	 *            The block status
	 * @param blockNumber
	 *            The number of the block
	 * @param previousBlockPosition
	 *            The position of the previous block of the same type
	 * @param writeInTransaction
	 *            To indicate if write must be done in transaction
	 * @return The position of the id @
	 */
	public long writeIdBlock(long position, int idBlockSize, byte blockStatus, int blockNumber, long previousBlockPosition,
			boolean writeInTransaction) {
		if (position == -1) {
			position = fsi.getAvailablePosition();
		}
		// LogUtil.fileSystemOn(true);
		// Updates the database header with the current id block position
		fsi.setWritePosition(StorageEngineConstant.DATABASE_HEADER_CURRENT_ID_BLOCK_POSITION, writeInTransaction);
		fsi.writeLong(position, false, "current id block position", DefaultWriteAction.DIRECT_WRITE_ACTION);

		fsi.setWritePosition(position, writeInTransaction);

		fsi.writeInt(idBlockSize, writeInTransaction, "block size");
		// LogUtil.fileSystemOn(false);
		fsi.writeByte(BlockTypes.BLOCK_TYPE_IDS, writeInTransaction);
		fsi.writeByte(blockStatus, writeInTransaction);
		// prev position
		fsi.writeLong(previousBlockPosition, writeInTransaction, "prev block pos", DefaultWriteAction.DIRECT_WRITE_ACTION);
		// next position
		fsi.writeLong(-1, writeInTransaction, "next block pos", DefaultWriteAction.DIRECT_WRITE_ACTION);
		fsi.writeInt(blockNumber, writeInTransaction, "id block number");
		fsi.writeLong(0, writeInTransaction, "id block max id", DefaultWriteAction.DIRECT_WRITE_ACTION);
		fsi.setWritePosition(position + OdbConfiguration.getIdBlockSize() - 1, writeInTransaction);
		fsi.writeByte((byte) 0, writeInTransaction);

		if (OdbConfiguration.isDebugEnabled(LOG_ID_DEBUG)) {
			DLogger.debug(depthToSpaces() + "After create block, available position is " + fsi.getAvailablePosition());
		}

		return position;
	}

	/**
	 * Marks a block of type id as full, changes the status and the next block
	 * position
	 * 
	 * @param blockPosition
	 * @param nextBlockPosition
	 * @param writeInTransaction
	 * @return The block position @
	 */
	public long markIdBlockAsFull(long blockPosition, long nextBlockPosition, boolean writeInTransaction) {
		fsi.setWritePosition(blockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_BLOCK_STATUS, writeInTransaction);
		fsi.writeByte(BlockStatus.BLOCK_FULL, writeInTransaction);
		fsi.setWritePosition(blockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_NEXT_BLOCK, writeInTransaction);
		fsi.writeLong(nextBlockPosition, writeInTransaction, "next id block pos", DefaultWriteAction.DIRECT_WRITE_ACTION);
		return blockPosition;
	}

	/**
	 * Associate an object OID to its position
	 * 
	 * @param idType
	 *            The type : can be object or class
	 * @param idStatus
	 *            The status of the OID
	 * @param currentBlockIdPosition
	 *            The current OID block position
	 * @param oid
	 *            The OID
	 * @param objectPosition
	 *            The position
	 * @param writeInTransaction
	 *            To indicate if write must be executed in transaction
	 * @return @
	 */
	public long associateIdToObject(byte idType, byte idStatus, long currentBlockIdPosition, OID oid, long objectPosition,
			boolean writeInTransaction) {

		// Update the max id of the current block
		fsi.setWritePosition(currentBlockIdPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_MAX_ID, writeInTransaction);
		fsi.writeLong(oid.getObjectId(), writeInTransaction, "id block max id update", DefaultWriteAction.POINTER_WRITE_ACTION);

		long l1 = (oid.getObjectId() - 1) % OdbConfiguration.getNB_IDS_PER_BLOCK();
		long l2 = StorageEngineConstant.BLOCK_ID_OFFSET_FOR_START_OF_REPETITION;
		long idPosition = currentBlockIdPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_START_OF_REPETITION + (l1)
				* OdbConfiguration.getID_BLOCK_REPETITION_SIZE();
		// go to the next id position
		fsi.setWritePosition(idPosition, writeInTransaction);
		// id type
		fsi.writeByte(idType, writeInTransaction, "id type");
		// id
		fsi.writeLong(oid.getObjectId(), writeInTransaction, "oid", DefaultWriteAction.POINTER_WRITE_ACTION);
		// id status
		fsi.writeByte(idStatus, writeInTransaction, "id status");
		// object position
		fsi.writeLong(objectPosition, writeInTransaction, "obj pos", DefaultWriteAction.POINTER_WRITE_ACTION);

		return idPosition;
	}

	/**
	 * Updates the real object position of the object OID
	 * 
	 * @param idPosition
	 *            The OID position
	 * @param objectPosition
	 *            The real object position
	 * @param writeInTransactionTo
	 *            indicate if write must be done in transaction @
	 */
	public void updateObjectPositionForObjectOIDWithPosition(long idPosition, long objectPosition, boolean writeInTransaction) {
		fsi.setWritePosition(idPosition, writeInTransaction);
		fsi.writeByte(IDTypes.OBJECT, writeInTransaction, "id type");
		fsi.setWritePosition(idPosition + StorageEngineConstant.BLOCK_ID_REPETITION_ID_STATUS, writeInTransaction);
		fsi.writeByte(IDStatus.ACTIVE, writeInTransaction);
		fsi.writeLong(objectPosition, writeInTransaction, "Updating object position of id", DefaultWriteAction.POINTER_WRITE_ACTION);
	}

	/**
	 * Udates the real class positon of the class OID
	 * 
	 * @param idPosition
	 * @param objectPosition
	 * @param writeInTransaction
	 *            @
	 */
	public void updateClassPositionForClassOIDWithPosition(long idPosition, long objectPosition, boolean writeInTransaction) {
		fsi.setWritePosition(idPosition, writeInTransaction);
		fsi.writeByte(IDTypes.CLASS, writeInTransaction, "id type");
		fsi.setWritePosition(idPosition + StorageEngineConstant.BLOCK_ID_REPETITION_ID_STATUS, writeInTransaction);
		fsi.writeByte(IDStatus.ACTIVE, writeInTransaction);
		fsi.writeLong(objectPosition, writeInTransaction, "Updating class position of id", DefaultWriteAction.POINTER_WRITE_ACTION);
	}

	public void updateStatusForIdWithPosition(long idPosition, byte newStatus, boolean writeInTransaction) {
		fsi.setWritePosition(idPosition + StorageEngineConstant.BLOCK_ID_REPETITION_ID_STATUS, writeInTransaction);
		fsi.writeByte(newStatus, writeInTransaction, "Updating id status");
	}

	/**
	 * Persist a single class info - This method is used by the XML Importer.
	 */
	public ClassInfo persistClass(ClassInfo newClassInfo, int lastClassInfoIndex, boolean addClass, boolean addDependentClasses) {
		MetaModel metaModel = getSession().getMetaModel();

		OID classInfoId = newClassInfo.getId();
		if (classInfoId == null) {
			classInfoId = getIdManager().getNextClassId(-1);
			newClassInfo.setId(classInfoId);
		}

		long writePosition = fsi.getAvailablePosition();
		newClassInfo.setPosition(writePosition);

		getIdManager().updateClassPositionForId(classInfoId, writePosition, true);
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Persisting class into database : " + newClassInfo.getFullClassName() + " with oid " + classInfoId + " at pos "
					+ writePosition);
			DLogger.debug("class " + newClassInfo.getFullClassName() + " has " + newClassInfo.getNumberOfAttributes() + " attributes : "
					+ newClassInfo.getAttributes());
		}

		// The class info oid is created in ObjectWriter.writeClassInfoHeader

		if (metaModel.getNumberOfClasses() > 0 && lastClassInfoIndex != -2) {
			ClassInfo lastClassinfo = null;
			if (lastClassInfoIndex == -1) {
				lastClassinfo = metaModel.getLastClassInfo();
			} else {
				lastClassinfo = metaModel.getClassInfo(lastClassInfoIndex);
			}

			lastClassinfo.setNextClassOID(newClassInfo.getId());
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("changing next class oid. of class info " + lastClassinfo.getFullClassName() + " @ "
						+ lastClassinfo.getPosition() + " + offset " + StorageEngineConstant.CLASS_OFFSET_NEXT_CLASS_POSITION + " to "
						+ newClassInfo.getId() + "(" + newClassInfo.getFullClassName() + ")");
			}

			fsi.setWritePosition(lastClassinfo.getPosition() + StorageEngineConstant.CLASS_OFFSET_NEXT_CLASS_POSITION, true);
			fsi.writeLong(newClassInfo.getId().getObjectId(), true, "next class oid", DefaultWriteAction.POINTER_WRITE_ACTION);
			newClassInfo.setPreviousClassOID(lastClassinfo.getId());

		}

		if (addClass) {
			metaModel.addClass(newClassInfo);
		}

		// updates number of classes
		writeNumberOfClasses(metaModel.getNumberOfClasses(), true);

		// If it is the first class , updates the first class OID
		if (newClassInfo.getPreviousClassOID() == null) {
			writeFirstClassInfoOID(newClassInfo.getId(), true);
		}

		// Writes the header of the class - out of transaction (FIXME why out of
		// transaction)
		writeClassInfoHeader(newClassInfo, writePosition, false);

		if (addDependentClasses) {
			IOdbList<ClassAttributeInfo> dependingAttributes = newClassInfo.getAllNonNativeAttributes();

			ClassAttributeInfo cai = null;
			for (int i = 0; i < dependingAttributes.size(); i++) {
				cai = dependingAttributes.get(i);
				try {
					ClassInfo existingCI = metaModel.getClassInfo(cai.getFullClassname(), false);
					if (existingCI == null) {
						// TODO check if this getClassInfo is ok. Maybe, should
						// use
						// a buffered one
						addClasses(classIntrospector.introspect(cai.getFullClassname(), true));
					} else {
						// Even,if it exist,take the one from metamodel
						cai.setClassInfo(existingCI);
					}
				} catch (Exception e) {
					throw new ODBRuntimeException(NeoDatisError.CLASS_INTROSPECTION_ERROR.addParameter(cai.getFullClassname()), e);
				}
			}
		}
		writeClassInfoBody(newClassInfo, fsi.getAvailablePosition(), true);
		return newClassInfo;
	}

	/*
	 * Adds a class to the metamodel, if it already exists simply returns the
	 * original one
	 */
	public ClassInfo addClass(ClassInfo newClassInfo, boolean addDependentClasses) {

		ClassInfo classInfo = getSession().getMetaModel().getClassInfo(newClassInfo.getFullClassName(), false);

		if (classInfo != null && classInfo.getPosition() != -1) {
			return classInfo;
		}
		return persistClass(newClassInfo, -1, true, addDependentClasses);

	}

	/*
	 * Adds a list of class to the metamodel, if it already exists simply
	 * returns the original one
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#addClasses
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.ClassInfoList)
	 */
	public ClassInfoList addClasses(ClassInfoList classInfoList) {

		Iterator iterator = classInfoList.getClassInfos().iterator();

		while (iterator.hasNext()) {
			addClass((ClassInfo) iterator.next(), true);
		}
		return classInfoList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#
	 * writeClassInfoHeader
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.ClassInfo, long, boolean)
	 */
	public void writeClassInfoHeader(ClassInfo classInfo, long position, boolean writeInTransaction) {

		OID classId = classInfo.getId();
		if (classId == null) {
			classId = idManager.getNextClassId(position);
			classInfo.setId(classId);
		} else {
			idManager.updateClassPositionForId(classId, position, true);
		}

		fsi.setWritePosition(position, writeInTransaction);
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Writing new Class info header at " + position + " : " + classInfo.toString());
		}

		// Real value of block size is only known at the end of the writing
		fsi.writeInt(0, writeInTransaction, "block size");
		fsi.writeByte(BlockTypes.BLOCK_TYPE_CLASS_HEADER, writeInTransaction, "class header block type");

		fsi.writeByte(classInfo.getClassCategory(), writeInTransaction, "Class info category");
		fsi.writeLong(classId.getObjectId(), writeInTransaction, "class id", DefaultWriteAction.DATA_WRITE_ACTION);

		writeOid(classInfo.getPreviousClassOID(), writeInTransaction, "prev class oid", DefaultWriteAction.DATA_WRITE_ACTION);
		writeOid(classInfo.getNextClassOID(), writeInTransaction, "next class oid", DefaultWriteAction.DATA_WRITE_ACTION);

		fsi.writeLong(classInfo.getCommitedZoneInfo().getNbObjects(), writeInTransaction, "class nb objects",
				DefaultWriteAction.DATA_WRITE_ACTION);

		writeOid(classInfo.getCommitedZoneInfo().first, writeInTransaction, "class first obj pos", DefaultWriteAction.DATA_WRITE_ACTION);
		writeOid(classInfo.getCommitedZoneInfo().last, writeInTransaction, "class last obj pos", DefaultWriteAction.DATA_WRITE_ACTION);

		// FIXME : append extra info if not empty (.net compatibility)
		fsi.writeString(classInfo.getFullClassName(), false, writeInTransaction);

		fsi.writeInt(classInfo.getMaxAttributeId(), writeInTransaction, "Max attribute id");

		if (classInfo.getAttributesDefinitionPosition() != -1) {
			fsi.writeLong(classInfo.getAttributesDefinitionPosition(), writeInTransaction, "class att def pos",
					DefaultWriteAction.DATA_WRITE_ACTION);
		} else {
			// @todo check this
			fsi.writeLong(-1, writeInTransaction, "class att def pos", DefaultWriteAction.DATA_WRITE_ACTION);
		}

		int blockSize = (int) (fsi.getPosition() - position);
		writeBlockSizeAt(position, blockSize, writeInTransaction, classInfo);

	}

	public void encodeOid(OID oid, byte[] bytes, int offset) {
		if (oid == null) {
			byteArrayConverter.longToByteArray(-1, bytes, offset);
			// fsi.writeLong(-1, writeInTransaction, label, writeAction);
		} else {
			byteArrayConverter.longToByteArray(oid.getObjectId(), bytes, offset);
			// fsi.writeLong(oid.getObjectId(), writeInTransaction, label,
			// writeAction);
		}
	}

	public void writeOid(OID oid, boolean writeInTransaction, String label, int writeAction) {
		if (oid == null) {
			fsi.writeLong(-1, writeInTransaction, label, writeAction);
		} else {
			fsi.writeLong(oid.getObjectId(), writeInTransaction, label, writeAction);
		}
	}

	/**
	 * Write the class info body to the database file. TODO Check if we really
	 * must recall the writeClassInfoHeader
	 * 
	 * @param classInfo
	 * @param position
	 *            The position
	 * @param writeInTransaction
	 *            @
	 */
	public void writeClassInfoBody(ClassInfo classInfo, long position, boolean writeInTransaction) {
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Writing new Class info body at " + position + " : " + classInfo.toString());
		}

		// updates class info
		classInfo.setAttributesDefinitionPosition(position);
		// FIXME : change this to write only the position and not the whole
		// header
		writeClassInfoHeader(classInfo, classInfo.getPosition(), writeInTransaction);

		fsi.setWritePosition(position, writeInTransaction);
		// block definition
		fsi.writeInt(0, writeInTransaction, "block size");
		fsi.writeByte(BlockTypes.BLOCK_TYPE_CLASS_BODY, writeInTransaction);

		// number of attributes
		fsi.writeLong(classInfo.getAttributes().size(), writeInTransaction, "class nb attributes", DefaultWriteAction.DATA_WRITE_ACTION);

		ClassAttributeInfo cai = null;
		for (int i = 0; i < classInfo.getAttributes().size(); i++) {
			cai = classInfo.getAttributes().get(i);
			writeClassAttributeInfo(cai, writeInTransaction);
		}
		int blockSize = (int) (fsi.getPosition() - position);
		writeBlockSizeAt(position, blockSize, writeInTransaction, classInfo);
	}

	public long writeClassInfoIndexes(ClassInfo classInfo) {
		boolean writeInTransaction = true;
		long position = fsi.getAvailablePosition();
		fsi.setWritePosition(position, writeInTransaction);
		ClassInfoIndex cii = null;
		long previousIndexPosition = -1;
		long currentIndexPosition = position;
		long nextIndexPosition = -1;
		long currentPosition = -1;
		for (int i = 0; i < classInfo.getNumberOfIndexes(); i++) {
			currentIndexPosition = fsi.getPosition();
			cii = classInfo.getIndex(i);
			fsi.writeInt(0, writeInTransaction, "block size");
			fsi.writeByte(BlockTypes.BLOCK_TYPE_INDEX, true, "Index block type");

			fsi.writeLong(previousIndexPosition, writeInTransaction, "prev index pos", DefaultWriteAction.POINTER_WRITE_ACTION);
			// The next position is only know at the end of the write
			fsi.writeLong(-1, writeInTransaction, "next index pos", DefaultWriteAction.POINTER_WRITE_ACTION);

			fsi.writeString(cii.getName(), false, writeInTransaction);
			fsi.writeBoolean(cii.isUnique(), writeInTransaction, "index is unique");
			fsi.writeByte(cii.getStatus(), writeInTransaction, "index status");
			fsi.writeLong(cii.getCreationDate(), writeInTransaction, "creation date", DefaultWriteAction.DATA_WRITE_ACTION);
			fsi.writeLong(cii.getLastRebuild(), writeInTransaction, "last rebuild", DefaultWriteAction.DATA_WRITE_ACTION);
			fsi.writeInt(cii.getAttributeIds().length, writeInTransaction, "number of fields");
			for (int j = 0; j < cii.getAttributeIds().length; j++) {
				fsi.writeInt(cii.getAttributeIds()[j], writeInTransaction, "attr id");
			}
			currentPosition = fsi.getPosition();
			// Write the block size
			int blockSize = (int) (fsi.getPosition() - currentIndexPosition);
			writeBlockSizeAt(currentIndexPosition, blockSize, writeInTransaction, classInfo);

			// Write the next index position
			if (i + 1 < classInfo.getNumberOfIndexes()) {
				nextIndexPosition = currentPosition;
			} else {
				nextIndexPosition = -1;
			}
			// reset cursor to write the next position
			fsi.setWritePosition(currentIndexPosition + ODBType.INTEGER.getSize() + ODBType.BYTE.getSize() + ODBType.LONG.getSize(),
					writeInTransaction);
			fsi.writeLong(nextIndexPosition, writeInTransaction, "next index pos", DefaultWriteAction.POINTER_WRITE_ACTION);
			previousIndexPosition = currentIndexPosition;

			// reset the write cursor
			fsi.setWritePosition(currentPosition, writeInTransaction);

		}

		return position;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#updateClassInfo
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.ClassInfo, boolean)
	 */
	public void updateClassInfo(ClassInfo classInfo, boolean writeInTransaction) {
		// first check dependent classes
		IOdbList<ClassAttributeInfo> dependingAttributes = classInfo.getAllNonNativeAttributes();

		MetaModel metaModel = getSession().getMetaModel();
		ClassAttributeInfo cai = null;
		for (int i = 0; i < dependingAttributes.size(); i++) {
			cai = dependingAttributes.get(i);
			try {
				ClassInfo existingCI = metaModel.getClassInfo(cai.getFullClassname(), false);
				if (existingCI == null) {
					// TODO check if this getClassInfo is ok. Maybe, should
					// use
					// a buffered one
					addClasses(classIntrospector.introspect(cai.getFullClassname(), true));
				} else {
					// FIXME should we update class info?
					cai.setClassInfo(existingCI);
				}
			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.CLASS_INTROSPECTION_ERROR.addParameter(cai.getFullClassname()), e);
			}
		}

		// To force the rewrite of class info body
		classInfo.setAttributesDefinitionPosition(-1);
		long newCiPosition = fsi.getAvailablePosition();
		classInfo.setPosition(newCiPosition);
		writeClassInfoHeader(classInfo, newCiPosition, writeInTransaction);
		writeClassInfoBody(classInfo, fsi.getAvailablePosition(), writeInTransaction);
	}

	/**
	 * Resets the position of the first class of the metamodel. It Happens when
	 * database is being refactored
	 * 
	 * @param classInfoPosition
	 *            @
	 */
	public void writeFirstClassInfoOID(OID classInfoID, boolean inTransaction) {
		long positionToWrite = StorageEngineConstant.DATABASE_HEADER_FIRST_CLASS_OID;
		fsi.setWritePosition(positionToWrite, inTransaction);
		writeOid(classInfoID, inTransaction, "first class info oid", DefaultWriteAction.DATA_WRITE_ACTION);
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Updating first class info oid at " + positionToWrite + " with oid " + classInfoID);
		}
	}

	private void updateNextClassInfoPositionOfClassInfo(long classInfoPosition, long newCiPosition) {
		fsi.setWritePosition(classInfoPosition + StorageEngineConstant.CLASS_OFFSET_NEXT_CLASS_POSITION, true);
		fsi.writeLong(newCiPosition, true, "new next ci position", DefaultWriteAction.DATA_WRITE_ACTION);
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger
					.debug(depthToSpaces() + "Updating next class info of class info at " + classInfoPosition + " with "
							+ classInfoPosition);
		}
	}

	private void updatePreviousClassInfoPositionOfClassInfo(long classInfoPosition, long newCiPosition) {
		fsi.setWritePosition(classInfoPosition + StorageEngineConstant.CLASS_OFFSET_PREVIOUS_CLASS_POSITION, true);
		fsi.writeLong(newCiPosition, true, "new prev ci position", DefaultWriteAction.DATA_WRITE_ACTION);
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger
					.debug(depthToSpaces() + "Updating prev class info of class info at " + classInfoPosition + " with "
							+ classInfoPosition);
		}
	}

	/**
	 * Writes a class attribute info, an attribute of a class
	 * 
	 * @param cai
	 * @param writeInTransaction
	 *            @
	 */
	private void writeClassAttributeInfo(ClassAttributeInfo cai, boolean writeInTransaction) {
		fsi.writeInt(cai.getId(), writeInTransaction, "attribute id");
		fsi.writeBoolean(cai.isNative(), writeInTransaction);
		if (cai.isNative() ) {
			fsi.writeInt(cai.getAttributeType().getId(), writeInTransaction, "att odb type id");
			if (cai.getAttributeType().isArray()) {
				fsi.writeInt(cai.getAttributeType().getSubType().getId(), writeInTransaction, "att array sub type");
				// when the attribute is not native, then write its class info
				// position
				if (cai.getAttributeType().getSubType().isNonNative()) {
					fsi.writeLong(storageEngine.getSession(true).getMetaModel().getClassInfo(cai.getAttributeType().getSubType().getName(),
							true).getId().getObjectId(), writeInTransaction, "class info id of array subtype",
							DefaultWriteAction.DATA_WRITE_ACTION);
				}
			}
			// For enum, we write the class info id of the enum class
			if(cai.getAttributeType().isEnum()){
				fsi.writeLong(storageEngine.getSession(true).getMetaModel().getClassInfo(cai.getFullClassname(), true).getId().getObjectId(),
						writeInTransaction, "class info id", DefaultWriteAction.DATA_WRITE_ACTION);
			}
		} else {
			fsi.writeLong(storageEngine.getSession(true).getMetaModel().getClassInfo(cai.getFullClassname(), true).getId().getObjectId(),
					writeInTransaction, "class info id", DefaultWriteAction.DATA_WRITE_ACTION);
		}
		fsi.writeString(cai.getName(), false, writeInTransaction);
		fsi.writeBoolean(cai.isIndex(), writeInTransaction);
	}

	/*
	 * protected long writeObjectInfo(long oid, AbstractObjectInfo objectInfo,
	 * boolean updatePointers) throws Exception { return writeObjectInfo(oid,
	 * objectInfo, -1, updatePointers); }
	 */

	/**
	 * Actually write the object data to the database file
	 * 
	 * @param oidOfObjectToQuery
	 *            The object id, can be -1 (not set)
	 * @param aoi
	 *            The object meta infor The object info to be written
	 * @param position
	 *            if -1, it is a new instance, if not, it is an update
	 * @param updatePointers
	 * @return The object posiiton or id(if <0)
	 * @throws Exception
	 *             @ * public OID writeObjectInfo(OID oid, AbstractObjectInfo
	 *             aoi, long position, boolean updatePointers) throws Exception
	 *             { currentDepth++;
	 * 
	 *             try {
	 * 
	 *             if (aoi.isNative()) { return
	 *             writeNativeObjectInfo((NativeObjectInfo) aoi, position,
	 *             updatePointers, false); }
	 * 
	 *             return writeNonNativeObjectInfo(oid, aoi, position,
	 *             updatePointers, false); } finally { currentDepth--; } }
	 */

	private long writeNativeObjectInfo(NativeObjectInfo noi, long position, boolean updatePointers, boolean writeInTransaction) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID_DEBUG)) {
			DLogger.debug(depthToSpaces() + "Writing native object at " + position + " : Type=" + ODBType.getNameFromId(noi.getOdbTypeId())
					+ " | Value=" + noi.toString());
		}

		if (noi.isAtomicNativeObject()) {
			return writeAtomicNativeObject((AtomicNativeObjectInfo) noi, writeInTransaction);
		}

		if (noi.isNull()) {
			writeNullNativeObjectHeader(noi.getOdbTypeId(), writeInTransaction);
			return position;
		}

		if (noi.isCollectionObject()) {
			return writeCollection((CollectionObjectInfo) noi, writeInTransaction);
		}
		if (noi.isMapObject()) {
			return writeMap((MapObjectInfo) noi, writeInTransaction);
		}
		if (noi.isArrayObject()) {
			return writeArray((ArrayObjectInfo) noi, writeInTransaction);
		}

		if (noi.isEnumObject()) {
			return writeEnumNativeObject((EnumNativeObjectInfo) noi, writeInTransaction);
		}
		throw new ODBRuntimeException(NeoDatisError.NATIVE_TYPE_NOT_SUPPORTED.addParameter(noi.getOdbTypeId()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#
	 * writeNonNativeObjectInfo(org.neodatis.odb.core.OID,
	 * org.neodatis.odb.core.impl.layers.layer2.meta.NonNativeObjectInfo, long,
	 * boolean, boolean)
	 */
	public OID writeNonNativeObjectInfo(OID existingOid, NonNativeObjectInfo objectInfo, long position, boolean writeDataInTransaction,
			boolean isNewObject) {

		ISession lsession = getSession();
		ICache cache = lsession.getCache();
		boolean hasObject = objectInfo.getObject() != null;

		// Insert triggers for CS Mode, local mode insert triggers are called in the DefaultInstrumentationCallbackForStore class
		if (isNewObject && !isLocalMode) {
			triggerManager.manageInsertTriggerBefore(objectInfo.getClassInfo().getFullClassName(), objectInfo);
		}
		// Checks if object is null,for null objects,there is nothing to do
		if (objectInfo.isNull()) {
			return StorageEngineConstant.NULL_OBJECT_ID;
		}

		MetaModel metaModel = lsession.getMetaModel();
		String className = objectInfo.getClassInfo().getFullClassName();
		// first checks if the class of this object already exist in the
		// metamodel
		if (!metaModel.existClass(className)) {
			addClass(objectInfo.getClassInfo(), true);
		}

		// if position is -1, gets the position where to write the object
		if (position == -1) {
			// Write at the end of the file
			position = fsi.getAvailablePosition();
			// Updates the meta object position
			objectInfo.setPosition(position);
		}

		// Gets the object id
		OID oid = existingOid;

		if (oid == null) {
			// If, to get the next id, a new id block must be created, then
			// there is an extra work
			// to update the current object position
			if (idManager.mustShift()) {
				oid = idManager.getNextObjectId(position);
				// The id manager wrote in the file so the position for the
				// object must be re-computed
				position = fsi.getAvailablePosition();
				// The oid must be associated to this new position - id
				// operations are always out of transaction
				// in this case, the update is done out of the transaction as a
				// rollback won t need to
				// undo this. We are just creating the id
				// => third parameter(write in transaction) = false
				idManager.updateObjectPositionForOid(oid, position, false);

			} else {
				oid = idManager.getNextObjectId(position);
			}
		} else {
			// If an oid was passed, it is because object already exist and
			// is being updated. So we
			// must update the object position
			// Here the update of the position of the id must be done in
			// transaction as the object
			// position of the id is being updated, and a rollback should undo
			// this
			// => third parameter(write in transaction) = true
			idManager.updateObjectPositionForOid(oid, position, true);
			// Keep the relation of id and position in the cache until the
			// commit
			cache.savePositionOfObjectWithOid(oid, position);
		}

		// Sets the oid of the object in the inserting cache
		cache.updateIdOfInsertingObject(objectInfo.getObject(), oid);
		// Only add the oid to unconnected zone if it is a new object
		if (isNewObject) {
			cache.addOIDToUnconnectedZone(oid);
			
			if (OdbConfiguration.reconnectObjectsToSession()) {

				ICrossSessionCache crossSessionCache = CacheFactory
						.getCrossSessionCache(storageEngine.getBaseIdentification().getIdentification());
				crossSessionCache.addObject(objectInfo.getObject(), oid);
			}
		}

		objectInfo.setOid(oid);
		
		if(isNewObject){
			// a new oid has been set, check if we have oid triggers
			if(triggerManager.hasOidTriggersFor(className)){
				triggerManager.manageOidTrigger(objectInfo, oid);
			}
		}
		
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Start Writing non native object of type " + objectInfo.getClassInfo().getFullClassName()
					+ " at " + position + " , oid = " + oid + " : " + objectInfo.toString());
		}

		if (objectInfo.getClassInfo() == null || objectInfo.getClassInfo().getId() == null) {

			if (objectInfo.getClassInfo() != null) {
				ClassInfo clinfo = storageEngine.getSession(true).getMetaModel().getClassInfo(objectInfo.getClassInfo().getFullClassName(),
						true);
				objectInfo.setClassInfo(clinfo);
			} else {
				throw new ODBRuntimeException(NeoDatisError.UNDEFINED_CLASS_INFO.addParameter(objectInfo.toString()));
			}
		}

		// updates the meta model - If class already exist, it returns the
		// metamodel class, which contains
		// a bit more informations
		ClassInfo classInfo = addClass(objectInfo.getClassInfo(), true);
		objectInfo.setClassInfo(classInfo);

		// 

		if (isNewObject) {
			manageNewObjectPointers(objectInfo, classInfo, position, metaModel);
		}
		/*
		 * else{ throw new
		 * ODBRuntimeException(Error.UNEXPECTED_SITUATION.addParameter
		 * ("WritingNonNativeObject that is not new and without updating
		 * pointers")); }
		 */

		if (OdbConfiguration.saveHistory()) {
			classInfo.addHistory(new InsertHistoryInfo("insert", oid, position, objectInfo.getPreviousObjectOID(), objectInfo
					.getNextObjectOID()));
		}

		fsi.setWritePosition(position, writeDataInTransaction);
		objectInfo.setPosition(position);

		int nbAttributes = objectInfo.getClassInfo().getAttributes().size();
		// compute the size of the array of byte needed till the attibute
		// positions
		// BlockSize + Block Type + ObjectId + ClassInfoId + Previous + Next +
		// CreatDate + UpdateDate + VersionNumber + ObjectRef + isSync + NbAttri
		// + Attributes
		// Int + Int + Long + Long + Long + Long + Long + Long + int + Long +
		// Bool + int + variable
		// 7 Longs + 4Ints + 1Bool + variable
		int tsize = 7 * ODBType.SIZE_OF_LONG + 3 * ODBType.SIZE_OF_INT + 2 * ODBType.SIZE_OF_BYTE;

		byte[] bytes = new byte[tsize];

		// Block size
		byteArrayConverter.intToByteArray(0, bytes, 0);

		// Block type
		bytes[4] = BlockTypes.BLOCK_TYPE_NON_NATIVE_OBJECT;
		// fsi.writeInt(BlockTypes.BLOCK_TYPE_NON_NATIVE_OBJECT,
		// writeDataInTransaction, "block size");

		// The object id
		encodeOid(oid, bytes, 5);
		// fsi.writeLong(oid.getObjectId(), writeDataInTransaction, "oid",
		// DefaultWriteAction.DATA_WRITE_ACTION);
		// Class info id
		byteArrayConverter.longToByteArray(classInfo.getId().getObjectId(), bytes, 13);
		// fsi.writeLong(classInfo.getId().getObjectId(),
		// writeDataInTransaction, "class info id",
		// DefaultWriteAction.DATA_WRITE_ACTION);

		// previous instance
		encodeOid(objectInfo.getPreviousObjectOID(), bytes, 21);
		// writeOid(objectInfo.getPreviousObjectOID(), writeDataInTransaction,
		// "prev instance", DefaultWriteAction.DATA_WRITE_ACTION);
		// next instance
		encodeOid(objectInfo.getNextObjectOID(), bytes, 29);
		// writeOid(objectInfo.getNextObjectOID(), writeDataInTransaction,
		// "next instance", DefaultWriteAction.DATA_WRITE_ACTION);
		// creation date, for update operation must be the original one
		byteArrayConverter.longToByteArray(objectInfo.getHeader().getCreationDate(), bytes, 37);
		// fsi.writeLong(objectInfo.getHeader().getCreationDate(),
		// writeDataInTransaction, "creation date",
		// DefaultWriteAction.DATA_WRITE_ACTION);

		byteArrayConverter.longToByteArray(OdbTime.getCurrentTimeInMs(), bytes, 45);
		// fsi.writeLong(OdbTime.getCurrentTimeInMs(), writeDataInTransaction,
		// "update date", DefaultWriteAction.DATA_WRITE_ACTION);

		// TODO check next version number
		byteArrayConverter.intToByteArray(objectInfo.getHeader().getObjectVersion(), bytes, 53);
		// fsi.writeInt(objectInfo.getHeader().getObjectVersion(),
		// writeDataInTransaction, "object version number");
		// not used yet. But it will point to an internal object of type
		// ObjectReference that will have details on the references:
		// All the objects that point to it: to enable object integrity
		byteArrayConverter.longToByteArray(-1, bytes, 57);
		// fsi.writeLong(-1, writeDataInTransaction, "object reference pointer",
		// DefaultWriteAction.DATA_WRITE_ACTION);

		// True if this object have been synchronized with main database, else
		// false
		byteArrayConverter.booleanToByteArray(false, bytes, 65);
		// fsi.writeBoolean(false, writeDataInTransaction,
		// "is syncronized with external db");

		// now write the number of attributes and the position of all
		// attributes, we do not know them yet, so write 00 but at the end
		// of the write operation
		// These positions will be updated
		// The positions that is going to be written are 'int' representing
		// the offset position of the attribute
		// first write the number of attributes
		// fsi.writeInt(nbAttributes, writeDataInTransaction, "nb attr");
		byteArrayConverter.intToByteArray(nbAttributes, bytes, 66);

		// Then write the array of bytes
		fsi.writeBytes(bytes, writeDataInTransaction, "NonNativeObjectInfoHeader");

		// Store the position
		long attributePositionStart = fsi.getPosition();

		int attributeSize = ODBType.SIZE_OF_INT + ODBType.SIZE_OF_LONG;
		byte[] abytes = new byte[nbAttributes * (attributeSize)];
		// here, just write an empty (0) array, as real values will be set at
		// the end
		fsi.writeBytes(abytes, writeDataInTransaction, "Empty Attributes");

		long[] attributesIdentification = new long[nbAttributes];
		int[] attributeIds = new int[nbAttributes];

		// Puts the object info in the cache
		// storageEngine.getSession().getCache().addObject(position,
		// aoi.getObject(), objectInfo.getHeader());

		ClassAttributeInfo cai = null;
		AbstractObjectInfo aoi2 = null;
		long nativeAttributePosition = -1;
		OID nonNativeAttributeOid = null;
		long maxWritePosition = fsi.getPosition();

		// Loop on all attributes
		for (int i = 0; i < nbAttributes; i++) {
			// Gets the attribute meta description
			cai = classInfo.getAttributeInfo(i);
			// Gets the id of the attribute
			attributeIds[i] = cai.getId();
			// Gets the attribute data
			aoi2 = objectInfo.getAttributeValueFromId(cai.getId());

			if (aoi2 == null) {
				// This only happens in 1 case : when a class has a field with
				// the same name of one of is superclass. In this, the deeper
				// attribute is null
				if (cai.isNative()) {
					aoi2 = new NullNativeObjectInfo(cai.getAttributeType().getId());
				} else {
					aoi2 = new NonNativeNullObjectInfo(cai.getClassInfo());
				}
			}

			if (aoi2.isNative()) {
				nativeAttributePosition = internalStoreObject((NativeObjectInfo) aoi2);
				// For native objects , odb stores their position
				attributesIdentification[i] = nativeAttributePosition;

			} else {
				if (aoi2.isObjectReference()) {
					ObjectReference or = (ObjectReference) aoi2;
					nonNativeAttributeOid = or.getOid();
				} else {
					nonNativeAttributeOid = storeObject(null, (NonNativeObjectInfo) aoi2);
				}
				// For non native objects , odb stores its oid as a negative
				// number!!u
				if (nonNativeAttributeOid != null) {
					attributesIdentification[i] = -nonNativeAttributeOid.getObjectId();
				} else {
					attributesIdentification[i] = StorageEngineConstant.NULL_OBJECT_ID_ID;
				}
			}

			long p = fsi.getPosition();
			if (p > maxWritePosition) {
				maxWritePosition = p;
			}
		}

		// Updates attributes identification in the object info header
		objectInfo.getHeader().setAttributesIdentification(attributesIdentification);
		objectInfo.getHeader().setAttributesIds(attributeIds);

		long positionAfterWrite = maxWritePosition;

		// Now writes back the attribute positions
		fsi.setWritePosition(attributePositionStart, writeDataInTransaction);

		abytes = new byte[attributesIdentification.length * (attributeSize)];
		for (int i = 0; i < attributesIdentification.length; i++) {

			byteArrayConverter.intToByteArray(attributeIds[i], abytes, i * attributeSize);
			byteArrayConverter.longToByteArray(attributesIdentification[i], abytes, i * (attributeSize) + ODBType.SIZE_OF_INT);
			
			AbstractObjectInfo aoi = objectInfo.getAttributeValueFromId(attributeIds[i]);
			if (aoi!=null && aoi.isNonNativeObject() && attributesIdentification[i] > 0) {
				throw new ODBRuntimeException(NeoDatisError.NON_NATIVE_ATTRIBUTE_STORED_BY_POSITION_INSTEAD_OF_OID.addParameter(
						classInfo.getAttributeInfo(i).getName()).addParameter(classInfo.getFullClassName()).addParameter(
						attributesIdentification[i]));
			}
		}
		fsi.writeBytes(abytes, writeDataInTransaction, "Filled Attributes");

		fsi.setWritePosition(positionAfterWrite, writeDataInTransaction);

		int blockSize = (int) (positionAfterWrite - position);

		try {
			writeBlockSizeAt(position, blockSize, writeDataInTransaction, objectInfo);
		} catch (ODBRuntimeException e) {
			DLogger.debug("Error while writing block size. pos after write " + positionAfterWrite + " / start pos = " + position);
			// throw new ODBRuntimeException(storageEngine,"Error while writing
			// block size. pos after write " + positionAfterWrite + " / start
			// pos = " + position,e);
			throw e;
		}

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "  Attributes positions of object with oid " + oid + " are "
					+ DisplayUtility.longArrayToString(attributesIdentification));
			DLogger.debug(depthToSpaces() + "End Writing non native object at " + position + " with oid " + oid + " - prev oid="
					+ objectInfo.getPreviousObjectOID() + " / next oid=" + objectInfo.getNextObjectOID());
			if (OdbConfiguration.isDebugEnabled(LOG_ID_DEBUG)) {
				DLogger.debug(" - current buffer : " + fsi.getIo().toString());
			}
		}

		// Only insert in index for new objects
		if (isNewObject) {
			// insert object id in indexes, if exist
			manageIndexesForInsert(oid, objectInfo);
			if (hasObject) {
				triggerManager.manageInsertTriggerAfter(objectInfo.getClassInfo().getFullClassName(), objectInfo.getObject(), oid);
			} else {
				// triggers
				triggerManager.manageInsertTriggerAfter(objectInfo.getClassInfo().getFullClassName(), objectInfo, oid);
			}
		}
		return oid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#
	 * writeNonNativeObjectInfo(org.neodatis.odb.core.OID,
	 * org.neodatis.odb.core.impl.layers.layer2.meta.NonNativeObjectInfo, long,
	 * boolean, boolean)
	 */
	public OID writeNonNativeObjectInfoOld(OID existingOid, NonNativeObjectInfo objectInfo, long position, boolean writeDataInTransaction,
			boolean isNewObject) {

		ISession lsession = getSession();
		ICache cache = lsession.getCache();
		boolean hasObject = objectInfo.getObject() != null;
		if (isNewObject) {
			if (hasObject) {
				// triggers
				triggerManager.manageInsertTriggerBefore(objectInfo.getClassInfo().getFullClassName(), objectInfo.getObject());
			} else {
				triggerManager.manageInsertTriggerBefore(objectInfo.getClassInfo().getFullClassName(), objectInfo);
			}
		}
		// Checks if object is null,for null objects,there is nothing to do
		if (objectInfo.isNull()) {
			return StorageEngineConstant.NULL_OBJECT_ID;
		}

		MetaModel metaModel = lsession.getMetaModel();

		// first checks if the class of this object already exist in the
		// metamodel
		if (!metaModel.existClass(objectInfo.getClassInfo().getFullClassName())) {
			addClass(objectInfo.getClassInfo(), true);
		}

		// if position is -1, gets the position where to write the object
		if (position == -1) {
			// Write at the end of the file
			position = fsi.getAvailablePosition();
			// Updates the meta object position
			objectInfo.setPosition(position);
		}

		// Gets the object id
		OID oid = existingOid;

		if (oid == null) {
			// If, to get the next id, a new id block must be created, then
			// there is an extra work
			// to update the current object position
			if (idManager.mustShift()) {
				oid = idManager.getNextObjectId(position);
				// The id manager wrote in the file so the position for the
				// object must be re-computed
				position = fsi.getAvailablePosition();
				// The oid must be associated to this new position - id
				// operations are always out of transaction
				// in this case, the update is done out of the transaction as a
				// rollback won t need to
				// undo this. We are just creating the id
				// => third parameter(write in transaction) = false
				idManager.updateObjectPositionForOid(oid, position, false);

			} else {
				oid = idManager.getNextObjectId(position);
			}
		} else {
			// If an oid was passed, it is because object already exist and
			// is being updated. So we
			// must update the object position
			// Here the update of the position of the id must be done in
			// transaction as the object
			// position of the id is being updated, and a rollback should undo
			// this
			// => third parameter(write in transaction) = true
			idManager.updateObjectPositionForOid(oid, position, true);
			// Keep the relation of id and position in the cache until the
			// commit
			cache.savePositionOfObjectWithOid(oid, position);
		}

		// Sets the oid of the object in the inserting cache
		cache.updateIdOfInsertingObject(objectInfo.getObject(), oid);
		// Only add the oid to unconnected zone if it is a new object
		if (isNewObject) {
			cache.addOIDToUnconnectedZone(oid);
			
			if (OdbConfiguration.reconnectObjectsToSession()) {

				ICrossSessionCache crossSessionCache = CacheFactory
						.getCrossSessionCache(storageEngine.getBaseIdentification().getIdentification());
				crossSessionCache.addObject(objectInfo.getObject(), oid);
			}
		}

		objectInfo.setOid(oid);
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Start Writing non native object of type " + objectInfo.getClassInfo().getFullClassName()
					+ " at " + position + " , oid = " + oid + " : " + objectInfo.toString());
		}

		if (objectInfo.getClassInfo() == null || objectInfo.getClassInfo().getId() == null) {

			if (objectInfo.getClassInfo() != null) {
				ClassInfo clinfo = storageEngine.getSession(true).getMetaModel().getClassInfo(objectInfo.getClassInfo().getFullClassName(),
						true);
				objectInfo.setClassInfo(clinfo);
			} else {
				throw new ODBRuntimeException(NeoDatisError.UNDEFINED_CLASS_INFO.addParameter(objectInfo.toString()));
			}
		}

		// updates the meta model - If class already exist, it returns the
		// metamodel class, which contains
		// a bit more informations
		ClassInfo classInfo = addClass(objectInfo.getClassInfo(), true);
		objectInfo.setClassInfo(classInfo);

		// 

		if (isNewObject) {
			manageNewObjectPointers(objectInfo, classInfo, position, metaModel);
		}
		/*
		 * else{ throw new
		 * ODBRuntimeException(Error.UNEXPECTED_SITUATION.addParameter
		 * ("WritingNonNativeObject that is not new and without updating
		 * pointers")); }
		 */

		if (OdbConfiguration.saveHistory()) {
			classInfo.addHistory(new InsertHistoryInfo("insert", oid, position, objectInfo.getPreviousObjectOID(), objectInfo
					.getNextObjectOID()));
		}

		fsi.setWritePosition(position, writeDataInTransaction);
		objectInfo.setPosition(position);

		// Block size
		fsi.writeInt(0, writeDataInTransaction, "block size");
		// Block type
		fsi.writeByte(BlockTypes.BLOCK_TYPE_NON_NATIVE_OBJECT, writeDataInTransaction, "object block type");
		// The object id
		fsi.writeLong(oid.getObjectId(), writeDataInTransaction, "oid", DefaultWriteAction.DATA_WRITE_ACTION);
		// Class info id
		fsi.writeLong(classInfo.getId().getObjectId(), writeDataInTransaction, "class info id", DefaultWriteAction.DATA_WRITE_ACTION);

		// previous instance
		writeOid(objectInfo.getPreviousObjectOID(), writeDataInTransaction, "prev instance", DefaultWriteAction.DATA_WRITE_ACTION);
		// next instance
		writeOid(objectInfo.getNextObjectOID(), writeDataInTransaction, "next instance", DefaultWriteAction.DATA_WRITE_ACTION);
		// creation date, for update operation must be the original one
		fsi.writeLong(objectInfo.getHeader().getCreationDate(), writeDataInTransaction, "creation date",
				DefaultWriteAction.DATA_WRITE_ACTION);
		fsi.writeLong(OdbTime.getCurrentTimeInMs(), writeDataInTransaction, "update date", DefaultWriteAction.DATA_WRITE_ACTION);
		// TODO check next version number
		fsi.writeInt(objectInfo.getHeader().getObjectVersion(), writeDataInTransaction, "object version number");
		// not used yet. But it will point to an internal object of type
		// ObjectReference that will have details on the references:
		// All the objects that point to it: to enable object integrity
		fsi.writeLong(-1, writeDataInTransaction, "object reference pointer", DefaultWriteAction.DATA_WRITE_ACTION);

		// True if this object have been synchronized with main database, else
		// false
		fsi.writeBoolean(false, writeDataInTransaction, "is syncronized with external db");

		int nbAttributes = objectInfo.getClassInfo().getAttributes().size();

		// now write the number of attributes and the position of all
		// attributes, we do not know them yet, so write 00 but at the end
		// of the write operation
		// These positions will be updated
		// The positions that is going to be written are 'int' representing
		// the offset position of the attribute
		// first write the number of attributes
		fsi.writeInt(nbAttributes, writeDataInTransaction, "nb attr");

		// Store the position
		long attributePositionStart = fsi.getPosition();

		// TODO Could remove this, and pull to the right position
		for (int i = 0; i < nbAttributes; i++) {
			fsi.writeInt(0, writeDataInTransaction, "attr id -1");
			fsi.writeLong(0, writeDataInTransaction, "att pos", DefaultWriteAction.DATA_WRITE_ACTION);
		}

		long[] attributesIdentification = new long[nbAttributes];
		int[] attributeIds = new int[nbAttributes];

		// Puts the object info in the cache
		// storageEngine.getSession().getCache().addObject(position,
		// aoi.getObject(), objectInfo.getHeader());

		ClassAttributeInfo cai = null;
		AbstractObjectInfo aoi2 = null;
		long nativeAttributePosition = -1;
		OID nonNativeAttributeOid = null;
		long maxWritePosition = fsi.getPosition();

		// Loop on all attributes
		for (int i = 0; i < nbAttributes; i++) {
			// Gets the attribute meta description
			cai = classInfo.getAttributeInfo(i);
			// Gets the id of the attribute
			attributeIds[i] = cai.getId();
			// Gets the attribute data
			aoi2 = objectInfo.getAttributeValueFromId(cai.getId());

			if (aoi2 == null) {
				// This only happens in 1 case : when a class has a field with
				// the same name of one of is superclass. In this, the deeper
				// attribute is null
				if (cai.isNative()) {
					aoi2 = new NullNativeObjectInfo(cai.getAttributeType().getId());
				} else {
					aoi2 = new NonNativeNullObjectInfo(cai.getClassInfo());
				}
			}

			if (aoi2.isNative()) {
				nativeAttributePosition = internalStoreObject((NativeObjectInfo) aoi2);
				// For native objects , odb stores their position
				attributesIdentification[i] = nativeAttributePosition;

			} else {
				if (aoi2.isObjectReference()) {
					ObjectReference or = (ObjectReference) aoi2;
					nonNativeAttributeOid = or.getOid();
				} else {
					nonNativeAttributeOid = storeObject(null, (NonNativeObjectInfo) aoi2);
				}
				// For non native objects , odb stores its oid as a negative
				// number!!u
				if (nonNativeAttributeOid != null) {
					attributesIdentification[i] = -nonNativeAttributeOid.getObjectId();
				} else {
					attributesIdentification[i] = StorageEngineConstant.NULL_OBJECT_ID_ID;
				}
			}

			long p = fsi.getPosition();
			if (p > maxWritePosition) {
				maxWritePosition = p;
			}
		}

		// Updates attributes identification in the object info header
		objectInfo.getHeader().setAttributesIdentification(attributesIdentification);
		objectInfo.getHeader().setAttributesIds(attributeIds);

		long positionAfterWrite = maxWritePosition;

		// Now writes back the attribute positions
		fsi.setWritePosition(attributePositionStart, writeDataInTransaction);

		for (int i = 0; i < attributesIdentification.length; i++) {
			fsi.writeInt(attributeIds[i], writeDataInTransaction, "attr id");
			fsi.writeLong(attributesIdentification[i], writeDataInTransaction, "att real pos", DefaultWriteAction.DATA_WRITE_ACTION);

			// if (classInfo.getAttributeInfo(i).isNonNative() &&
			// attributesIdentification[i] > 0) {
			if (objectInfo.getAttributeValueFromId(attributeIds[i]).isNonNativeObject() && attributesIdentification[i] > 0) {
				throw new ODBRuntimeException(NeoDatisError.NON_NATIVE_ATTRIBUTE_STORED_BY_POSITION_INSTEAD_OF_OID.addParameter(
						classInfo.getAttributeInfo(i).getName()).addParameter(classInfo.getFullClassName()).addParameter(
						attributesIdentification[i]));
			}
		}
		fsi.setWritePosition(positionAfterWrite, writeDataInTransaction);

		int blockSize = (int) (positionAfterWrite - position);

		try {
			writeBlockSizeAt(position, blockSize, writeDataInTransaction, objectInfo);
		} catch (ODBRuntimeException e) {
			DLogger.debug("Error while writing block size. pos after write " + positionAfterWrite + " / start pos = " + position);
			// throw new ODBRuntimeException(storageEngine,"Error while writing
			// block size. pos after write " + positionAfterWrite + " / start
			// pos = " + position,e);
			throw e;
		}

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "  Attributes positions of object with oid " + oid + " are "
					+ DisplayUtility.longArrayToString(attributesIdentification));
			DLogger.debug(depthToSpaces() + "End Writing non native object at " + position + " with oid " + oid + " - prev oid="
					+ objectInfo.getPreviousObjectOID() + " / next oid=" + objectInfo.getNextObjectOID());
			if (OdbConfiguration.isDebugEnabled(LOG_ID_DEBUG)) {
				DLogger.debug(" - current buffer : " + fsi.getIo().toString());
			}
		}

		// Only insert in index for new objects
		if (isNewObject) {
			// insert object id in indexes, if exist
			manageIndexesForInsert(oid, objectInfo);
			if (hasObject) {
				triggerManager.manageInsertTriggerAfter(objectInfo.getClassInfo().getFullClassName(), objectInfo.getObject(), oid);
			} else {
				// triggers
				triggerManager.manageInsertTriggerAfter(objectInfo.getClassInfo().getFullClassName(), objectInfo, oid);
			}
		}
		return oid;
	}

	/**
	 * Updates pointers of objects, Only changes uncommitted info pointers
	 * 
	 * @param objectInfo
	 *            The meta representation of the object being inserted
	 * @param classInfo
	 *            The class of the object being inserted
	 * @param position
	 *            The position where the object is being inserted @
	 */
	private void manageNewObjectPointers(NonNativeObjectInfo objectInfo, ClassInfo classInfo, long position, MetaModel metaModel) {
		ICache cache = storageEngine.getSession(true).getCache();
		boolean isFirstUncommitedObject = !classInfo.getUncommittedZoneInfo().hasObjects();
		// if it is the first uncommitted object
		if (isFirstUncommitedObject) {
			classInfo.getUncommittedZoneInfo().first = objectInfo.getOid();
			OID lastCommittedObjectOid = classInfo.getCommitedZoneInfo().last;
			if (lastCommittedObjectOid != null) {
				// Also updates the last committed object next object oid in
				// memory to connect the committed
				// zone with unconnected for THIS transaction (only in memory)
				ObjectInfoHeader oih = cache.getObjectInfoHeaderFromOid(lastCommittedObjectOid, false);
				
				// if session has already been committed once, then , oih cache has been reset => tries to reload from disk
				if(oih==null && getSession().hasBeenCommitted()){
					oih = objectReader.readObjectInfoHeaderFromOid(lastCommittedObjectOid, false);
				}
				
				// if oih continues null
				if(oih==null){
					throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST.addParameter(lastCommittedObjectOid));
				}
				oih.setNextObjectOID(objectInfo.getOid());

				// And sets the previous oid of the current object with the last
				// committed oid
				objectInfo.setPreviousInstanceOID(lastCommittedObjectOid);
			}
		} else {
			// Gets the last object, updates its (next object)
			// pointer to the new object and updates the class info 'last
			// uncommitted object
			// oid' field
			ObjectInfoHeader oip = classInfo.getLastObjectInfoHeader();

			if (oip == null) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("last OIP is null in manageNewObjectPointers oid="
						+ objectInfo.getOid()));
			}
			if (oip.getNextObjectOID() != objectInfo.getOid()) {
				oip.setNextObjectOID(objectInfo.getOid());
				// Here we are working in unconnected zone, so this
				// can be done without transaction: actually
				// write in database file
				updateNextObjectFieldOfObjectInfo(oip.getOid(), oip.getNextObjectOID(), false);
				objectInfo.setPreviousInstanceOID(oip.getOid());
				// Resets the class info oid: In some case,
				// (client // server) it may be -1.
				oip.setClassInfoId(classInfo.getId());
				// object info oip has been changed, we must put it
				// in the cache to turn this change available for current
				// transaction until the commit
				storageEngine.getSession(true).getCache().addObjectInfo(oip);

			}
		}
		// always set the new last object oid and the number of objects
		classInfo.getUncommittedZoneInfo().last = objectInfo.getOid();
		classInfo.getUncommittedZoneInfo().increaseNbObjects();
		// Then updates the last info pointers of the class info
		// with this new created object
		// At this moment, the objectInfo.getHeader() do not have the
		// attribute ids.
		// but later in this code, the attributes will be set, so the class
		// info also will have them
		classInfo.setLastObjectInfoHeader(objectInfo.getHeader());

		// // Saves the fact that something has changed in the class (number of
		// objects and/or last object oid)
		storageEngine.getSession(true).getMetaModel().addChangedClass(classInfo);
	}

	/**
	 * Insert the object in the index
	 * 
	 * @param oid
	 *            The object id
	 * @param nnoi
	 *            The object meta represenation
	 * @return The number of indexes
	 */
	public int manageIndexesForInsert(OID oid, NonNativeObjectInfo nnoi) {
		IOdbList<ClassInfoIndex> indexes = nnoi.getClassInfo().getIndexes();
		ClassInfoIndex index = null;
		for (int i = 0; i < indexes.size(); i++) {
			index = indexes.get(i);
			try {
				OdbComparable key = index.computeKey(nnoi);
				int hc = key.hashCode();
				index.getBTree().insert(key, oid);
			} catch (Exception e) {
				// rollback what has been done
				// bug #2510966
				getSession().rollback();
				throw new ODBRuntimeException(NeoDatisError.ERROR_WHILE_MANAGING_INDEX.addParameter(index.getName()),e);
			}

			// Check consistency : index should have size equal to the class
			// info element number
			if (index.getBTree().getSize() != nnoi.getClassInfo().getNumberOfObjects()) {
				throw new ODBRuntimeException(NeoDatisError.BTREE_SIZE_DIFFERS_FROM_CLASS_ELEMENT_NUMBER.addParameter(index.getBTree().getSize())
						.addParameter(nnoi.getClassInfo().getNumberOfObjects()));
			}

		}
		return indexes.size();
	}

	/**
	 * Insert the object in the index
	 * 
	 * @param oid
	 *            The object id
	 * @param nnoi
	 *            The object meta represenation
	 * @return The number of indexes
	 * @throws Exception
	 */
	public int manageIndexesForDelete(OID oid, NonNativeObjectInfo nnoi) {
		IOdbList<ClassInfoIndex> indexes = nnoi.getClassInfo().getIndexes();
		ClassInfoIndex index = null;

		for (int i = 0; i < indexes.size(); i++) {
			index = indexes.get(i);
			// TODO manage collision!
			index.getBTree().delete(index.computeKey(nnoi), oid);

			// Check consistency : index should have size equal to the class
			// info element number
			if (index.getBTree().getSize() != nnoi.getClassInfo().getNumberOfObjects()) {
				throw new ODBRuntimeException(NeoDatisError.BTREE_SIZE_DIFFERS_FROM_CLASS_ELEMENT_NUMBER.addParameter(index.getBTree().getSize())
						.addParameter(nnoi.getClassInfo().getNumberOfObjects()));
			}
		}

		return indexes.size();
	}

	public int manageIndexesForUpdate(OID oid, NonNativeObjectInfo nnoi, NonNativeObjectInfo oldMetaRepresentation) {
		// takes the indexes from the oldMetaRepresentation because noi comes
		// from the client and is not always
		// in sync with the server meta model (In Client Server mode)
		IOdbList<ClassInfoIndex> indexes = oldMetaRepresentation.getClassInfo().getIndexes();
		ClassInfoIndex index = null;
		OdbComparable oldKey = null;
		OdbComparable newKey = null;
		for (int i = 0; i < indexes.size(); i++) {
			index = indexes.get(i);
			oldKey = index.computeKey(oldMetaRepresentation);
			newKey = index.computeKey(nnoi);
			// Only update index if key has changed!
			if (oldKey.compareTo(newKey) != 0) {
				IBTree btree = index.getBTree();
				// TODO manage collision!
				Object old = btree.delete(oldKey, oid);
				// TODO check if old is equal to oldKey
				btree.insert(newKey, oid);

				// Check consistency : index should have size equal to the class
				// info element number
				if (index.getBTree().getSize() != nnoi.getClassInfo().getNumberOfObjects()) {
					throw new ODBRuntimeException(NeoDatisError.BTREE_SIZE_DIFFERS_FROM_CLASS_ELEMENT_NUMBER.addParameter(
							index.getBTree().getSize()).addParameter(nnoi.getClassInfo().getNumberOfObjects()));
				}
			}
		}
		return indexes.size();
	}

	/*
	 * private long insertObject(long oid, AbstractObjectInfo aoi, boolean
	 * updatePointers) throws Exception { if (aoi.isNonNativeObject()) { return
	 * insertNonNativeObject(oid, (NonNativeObjectInfo) aoi, updatePointers); }
	 * if (aoi.isNative()) { return insertNativeObject((NativeObjectInfo) aoi);
	 * }
	 * 
	 * throw new
	 * ODBRuntimeException(Error.ABSTRACT_OBJECT_INFO_TYPE_NOT_SUPPORTED
	 * .addParameter(aoi.getClass().getName())); }
	 */

	/**
	 * @param oid
	 *            The Oid of the object to be inserted
	 * @param nnoi
	 *            The object meta representation The object to be inserted in
	 *            the database
	 * @param isNewObject
	 *            To indicate if object is new
	 * @return The position of the inserted object
	 */

	public OID insertNonNativeObject(OID oid, NonNativeObjectInfo nnoi, boolean isNewObject) {
		try {

			ClassInfo ci = nnoi.getClassInfo();
			Object object = nnoi.getObject();

			// First check if object is already being inserted
			// This method returns -1 if object is not being inserted
			OID cachedOid = getSession().getCache().idOfInsertingObject(object);
			if (cachedOid != null) {
				return cachedOid;
			}

			// Then checks if the class of this object already exist in the
			// meta model
			ci = addClass(ci, true);
			// Resets the ClassInfo in the objectInfo to be sure it contains all
			// updated class info data
			nnoi.setClassInfo(ci);

			// Mark this object as being inserted. To manage cyclic relations
			// The oid may be equal to -1
			// Later in the process the cache will be updated with the right oid
			getSession().getCache().startInsertingObjectWithOid(object, oid, nnoi);

			// false : do not write data in transaction. Data are always written
			// directly to disk. Pointers are written in transaction
			OID newOid = writeNonNativeObjectInfo(oid, nnoi, -1, false, isNewObject);
			if (newOid != StorageEngineConstant.NULL_OBJECT_ID) {
				getSession().getCache().addObject(newOid, object, nnoi.getHeader());
			}

			return newOid;

		} finally {
			// This will be done by the mainStoreObject method
			// Context.getCache().endInsertingObject(object);

		}
	}

	/**
	 * 
	 * @param noi
	 *            The native object meta representation The object to be
	 *            inserted in the database
	 * @return The position of the inserted object
	 */
	private long insertNativeObject(NativeObjectInfo noi) {
		long writePosition = fsi.getAvailablePosition();
		fsi.setWritePosition(writePosition, true);

		// true,false = update pointers,do not write in transaction, writes
		// directly to hard disk
		long position = writeNativeObjectInfo(noi, writePosition, true, false);
		return position;
	}

	/**
	 * Store a meta representation of an object(already as meta
	 * representation)in ODBFactory database.
	 * 
	 * To detect if object must be updated or insert, we use the cache. To
	 * update an object, it must be first selected from the database. When an
	 * object is to be stored, if it exist in the cache, then it will be
	 * updated, else it will be inserted as a new object. If the object is null,
	 * the cache will be used to check if the meta representation is in the
	 * cache
	 * 
	 * @param oid
	 *            The oid of the object to be inserted/updates
	 * @param nnoi
	 *            The meta representation of an object
	 * @return The object position
	 * 
	 */
	public OID storeObject(OID oid, NonNativeObjectInfo nnoi) {

		// first detects if we must perform an insert or an update
		// If object is in the cache, we must perform an update, else an insert

		Object object = nnoi.getObject();

		boolean mustUpdate = false;
		ICache cache = getSession().getCache();

		if (object != null) {
			OID cacheOid = cache.idOfInsertingObject(object);
			if (cacheOid != null) {
				return cacheOid;
			}

			// throw new ODBRuntimeException("Inserting meta representation of
			// an object without the object itself is not yet supported");
			mustUpdate = cache.existObject(object);

		}

		if (!mustUpdate) {
			mustUpdate = nnoi.getOid() != StorageEngineConstant.NULL_OBJECT_ID;
		}

		// To enable auto - reconnect object loaded from previous sessions
		// auto reconnect is on
		if (!mustUpdate && OdbConfiguration.reconnectObjectsToSession()) {


			ICrossSessionCache crossSessionCache = CacheFactory.getCrossSessionCache(storageEngine.getBaseIdentification().getIdentification());
			if (crossSessionCache.existObject(object)) {
				storageEngine.reconnect(object);
				mustUpdate = true;
			}
		}
		
		if (mustUpdate) {
			return updateNonNativeObjectInfo(nnoi, false);
		}

		return insertNonNativeObject(oid, nnoi, true);
	}

	/**
	 * Store a meta representation of a native object(already as meta
	 * representation)in ODBFactory database. A Native object is an object that
	 * use native language type, String for example
	 * 
	 * To detect if object must be updated or insert, we use the cache. To
	 * update an object, it must be first selected from the database. When an
	 * object is to be stored, if it exist in the cache, then it will be
	 * updated, else it will be inserted as a new object. If the object is null,
	 * the cache will be used to check if the meta representation is in the
	 * cache
	 * 
	 * @param nnoi
	 *            The meta representation of an object
	 * @return The object position @
	 */
	long internalStoreObject(NativeObjectInfo noi) {
		return insertNativeObject(noi);
	}

	public OID updateObject(AbstractObjectInfo aoi, boolean forceUpdate) {
		if (aoi.isNonNativeObject()) {
			return updateNonNativeObjectInfo((NonNativeObjectInfo) aoi, forceUpdate);
		}
		if (aoi.isNative()) {
			return updateObject(aoi, forceUpdate);
		}

		// TODO : here should use if then else
		throw new ODBRuntimeException(NeoDatisError.ABSTRACT_OBJECT_INFO_TYPE_NOT_SUPPORTED.addParameter(aoi.getClass().getName()));

	}

	/**
	 * Updates an object.
	 * 
	 * <pre>
	 * Try to update in place. Only change what has changed. This is restricted to particular types (fixed size types). If in place update is 
	 * not possible, then deletes the current object and creates a new at the end of the database file and updates
	 * OID object position.
	 * 
	 * &#064;param object The object to be updated
	 * &#064;param forceUpdate when true, no verification is done to check if update must be done.
	 * &#064;return The oid of the object, as a negative number
	 * &#064;
	 * 
	 */
	public OID updateNonNativeObjectInfo(NonNativeObjectInfo nnoi, boolean forceUpdate) {
		nbCallsToUpdate++;
		boolean hasObject = true;
		String message = null;
		Object object = nnoi.getObject();
		OID oid = nnoi.getOid();

		if (object == null) {
			hasObject = false;
		}
		// When there is index,we must *always* load the old meta representation
		// to compute index keys
		boolean withIndex = !nnoi.getClassInfo().getIndexes().isEmpty();

		NonNativeObjectInfo oldMetaRepresentation = null;

		// Used to check consistency, at the end, the number of
		// nbConnectedObjects must and nbUnconnected must remain unchanged
		long nbConnectedObjects = nnoi.getClassInfo().getCommitedZoneInfo().getNbObjects();
		long nbNonConnectedObjects = nnoi.getClassInfo().getUncommittedZoneInfo().getNbObjects();

		boolean objectHasChanged = false;
		try {
			ISession lsession = getSession();
			long positionBeforeWrite = fsi.getPosition();
			ITmpCache tmpCache = lsession.getTmpCache();
			ICache cache = lsession.getCache();

			// Get header of the object (position, previous object position,
			// next object position and class info position)
			// The header may not be in cache if session has been committed
			ObjectInfoHeader lastHeader = cache.getObjectInfoHeaderFromOid(oid, false);

			if(lastHeader==null && lsession.hasBeenCommitted()){
				// try to reload from disk
				lastHeader = objectReader.readObjectInfoHeaderFromOid(oid, false);
			}
			if (lastHeader == null) {
				throw new ODBRuntimeException(NeoDatisError.UNEXPECTED_SITUATION.addParameter("Header is null in update"));
			}

			if (lastHeader.getOid() == null) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("Header oid is null for oid " + oid));
			}

			boolean objectIsInConnectedZone = cache.objectWithIdIsInCommitedZone(oid);

			long currentPosition = lastHeader.getPosition();

			// When using client server mode, we must re-read the position of
			// the object with oid. Because, another session may
			// have updated the object, and in this case, the position of the
			// object in the cache may be invalid
			// TODO It should be done only when the object has been deleted or
			// updated by another session. Should check this
			// Doing this with new objects (created in the current session, the
			// last committed
			// object position will be negative, in this case we must use the
			// currentPosition
			if (!isLocalMode) {
				long lastCommitedObjectPosition = idManager.getObjectPositionWithOid(oid, false);
				if (lastCommitedObjectPosition > 0) {
					currentPosition = lastCommitedObjectPosition;
				}
				// Some infos that come from the client are not set
				// So we overwrite them here : example : object version. Update
				// date is not important here
				// Because, as we are updating the object, the update date will
				// be updated too
				nnoi.getHeader().setObjectVersion(lastHeader.getObjectVersion());
				nnoi.getHeader().setUpdateDate(lastHeader.getUpdateDate());
			}

			// for client server
			if (nnoi.getPosition() == -1) {
				nnoi.getHeader().setPosition(currentPosition);
			}

			if (!forceUpdate && currentPosition == -1) {
				throw new ODBRuntimeException(NeoDatisError.INSTANCE_POSITION_IS_NEGATIVE.addParameter(currentPosition).addParameter(oid)
						.addParameter("In Object Info Header"));
			}

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				message = depthToSpaces() + "start updating object at " + currentPosition + ", oid=" + oid + " : "
						+ (nnoi != null ? nnoi.toString() : "null");
				DLogger.debug(message);
			}

			// triggers,FIXME passing null to old object representation
			if (hasObject) {
				// This is local mode. The update trigger is being called in the DefaultInstrospectionCallbackForStore
				//storageEngine.getTriggerManager().manageUpdateTriggerBefore(nnoi.getClassInfo().getFullClassName(), null, object, oid);
			} else {
				storageEngine.getTriggerManager().manageUpdateTriggerBefore(nnoi.getClassInfo().getFullClassName(), null, nnoi, oid);
			}

			// Use to control if the in place update is ok. The
			// ObjectInstrospector stores the number of changes
			// that were detected and here we try to apply them using in place
			// update.If at the end
			// of the in place update the number of applied changes is smaller
			// then the number
			// of detected changes, then in place update was not successfully,
			// we
			// must do a real update,
			// creating an object elsewhere :-(
			int nbAppliedChanges = 0;
			if (!forceUpdate) {

				OID cachedOid = cache.idOfInsertingObject(object);
				if (cachedOid != null) {
					// The object is being inserted (must be a cyclic
					// reference), simply returns id id
					return cachedOid;
				}

				// the nnoi (NonNativeObjectInfo is the meta representation of
				// the object to update
				// To know what must be upated we must get the meta
				// representation of this object before
				// The modification. Taking this 'old' meta representation from
				// the
				// cache does not resolve
				// : because cache is a reference to the real object and object
				// has been changed,
				// so the cache is pointing to the reference, that has changed!
				// This old meta representation must be re-read from the last
				// committed database
				// false, = returnInstance (java object) = false
				try {
					boolean useCache = !objectIsInConnectedZone;
					oldMetaRepresentation = objectReader.readNonNativeObjectInfoFromPosition(null, oid, currentPosition, useCache, false);
					tmpCache.clearObjectInfos();
				} catch (ODBRuntimeException e) {
					throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("Error while reading old Object Info of oid " + oid
							+ " at pos " + currentPosition), e);
				}

				// Make sure we work with the last version of the object
				int onDiskVersion = oldMetaRepresentation.getHeader().getObjectVersion();
				long onDiskUpdateDate = oldMetaRepresentation.getHeader().getUpdateDate();

				int inCacheVersion = lastHeader.getObjectVersion();
				long inCacheUpdateDate = lastHeader.getUpdateDate();

				// @BUG this causes the database corruption bug 3006949. When object is updated twice in a transaction and its pointers are updated.
				// The following line just ignores the new version of the OIH(ObjectInfoHeader) and overwrites correct pointers (prev/next) 
				// This happens if transaction is commited in the meanwhile (before the 2 updates)
				// see junit
				// org.neodatis.odb.test.ee2.delete.TestDelete.test1WithCommits
				if (onDiskUpdateDate > inCacheUpdateDate || onDiskVersion > inCacheVersion) {
					//lastHeader = oldMetaRepresentation.getHeader();
				}
				nnoi.setHeader(lastHeader);
				// increase the object version number from the old meta
				// representation
				nnoi.getHeader().incrementVersionAndUpdateDate();
				// Keep the creation date
				nnoi.getHeader().setCreationDate(oldMetaRepresentation.getHeader().getCreationDate());
				// Set the object of the old meta to make the object comparator
				// understand, they are 2
				// meta representation of the same object
				// TODO , check if if is the best way to do
				oldMetaRepresentation.setObject(nnoi.getObject());

				// Reset the comparator
				comparator.clear();
				objectHasChanged = comparator.hasChanged(oldMetaRepresentation, nnoi);

				if (!objectHasChanged) {
					fsi.setWritePosition(positionBeforeWrite, true);

					if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
						DLogger.debug(depthToSpaces() + "updateObject : Object is unchanged - doing nothing");
					}
					return oid;
				}

				if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
					DLogger.debug(depthToSpaces() + "\tmax recursion level is " + comparator.getMaxObjectRecursionLevel());
					DLogger.debug(depthToSpaces() + "\tattribute actions are : " + comparator.getChangedAttributeActions());
					DLogger.debug(depthToSpaces() + "\tnew objects are : " + comparator.getNewObjects());
				}

				if (OdbConfiguration.inPlaceUpdate() && comparator.supportInPlaceUpdate()) {
					nbAppliedChanges = manageInPlaceUpdate(comparator, object, oid, lastHeader, cache, objectIsInConnectedZone);

					// if number of applied changes is equal to the number of
					// detected change
					if (nbAppliedChanges == comparator.getNbChanges()) {
						nbInPlaceUpdates++;
						updateUpdateTimeAndObjectVersionNumber(lastHeader, true);
						cache.addObject(oid, object, lastHeader);
						return oid;
					}
				}

			}

			// If we reach this update, In Place Update was not possible. Do a
			// normal update. Deletes the
			// current object and creates a new one

			if (oldMetaRepresentation == null && withIndex) {
				// We must load old meta representation to be able to compute
				// old index key to update index
				oldMetaRepresentation = objectReader.readNonNativeObjectInfoFromPosition(null, oid, currentPosition, false, false);
			}
			nbNormalUpdates++;
			if (hasObject) {
				cache.startInsertingObjectWithOid(object, oid, nnoi);
			}

			// gets class info from in memory meta model
			ClassInfo ci = lsession.getMetaModel().getClassInfoFromId(lastHeader.getClassInfoId());

			if (hasObject) {
				// removes the object from the cache
				// cache.removeObjectWithOid(oid, object);
				cache.endInsertingObject(object);
			}

			OID previousObjectOID = lastHeader.getPreviousObjectOID();
			OID nextObjectOid = lastHeader.getNextObjectOID();

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug(depthToSpaces() + "Updating object " + nnoi.toString());
				DLogger.debug(depthToSpaces() + "position =  " + currentPosition + " | prev instance = " + previousObjectOID
						+ " | next instance = " + nextObjectOid);
			}

			nnoi.setPreviousInstanceOID(previousObjectOID);
			nnoi.setNextObjectOID(nextObjectOid);

			// Mark the block of current object as deleted
			markAsDeleted(currentPosition, oid, objectIsInConnectedZone);

			// Creates the new object
			oid = insertNonNativeObject(oid, nnoi, false);
			// This position after write must be call just after the insert!!
			long positionAfterWrite = fsi.getPosition();

			if (hasObject) {
				// update cache
				cache.addObject(oid, object, nnoi.getHeader());
				
				//TODO check if we must update cross session cache
			}

			fsi.setWritePosition(positionAfterWrite, true);

			long nbConnectedObjectsAfter = nnoi.getClassInfo().getCommitedZoneInfo().getNbObjects();
			long nbNonConnectedObjectsAfter = nnoi.getClassInfo().getUncommittedZoneInfo().getNbObjects();
			if (nbConnectedObjectsAfter != nbConnectedObjects || nbNonConnectedObjectsAfter != nbNonConnectedObjects) {
				// TODO check this
				// throw new
				// ODBRuntimeException(Error.INTERNAL_ERROR.addParameter("Error
				// in nb connected/unconnected counter"));
			}

			return oid;
		} catch (Exception e) {
			message = depthToSpaces() + "Error updating object " + nnoi.toString() + " : " + OdbString.exceptionToString(e, true);
			DLogger.error(message);
			throw new ODBRuntimeException(e, message);
		} finally {
			if (objectHasChanged) {
				if (withIndex) {
					manageIndexesForUpdate(oid, nnoi, oldMetaRepresentation);
				}
				// triggers,FIXME passing null to old object representation
				// (oldMetaRepresentation may be null)
				if (hasObject) {
					storageEngine.getTriggerManager().manageUpdateTriggerAfter(nnoi.getClassInfo().getFullClassName(),
							oldMetaRepresentation, object, oid);
				} else {
					storageEngine.getTriggerManager().manageUpdateTriggerAfter(nnoi.getClassInfo().getFullClassName(),
							oldMetaRepresentation, nnoi, oid);
				}
			}

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug(depthToSpaces() + "end updating object with oid=" + oid + " at pos " + nnoi.getPosition() + " => "
						+ nnoi.toString());
			}
		}
	}

	/**
	 * Upate the version number of the object
	 * 
	 * @param header
	 * @param writeInTransaction
	 */
	private void updateUpdateTimeAndObjectVersionNumber(ObjectInfoHeader header, boolean writeInTransaction) {
		long objectPosition = header.getPosition();
		fsi.setWritePosition(objectPosition + StorageEngineConstant.OBJECT_OFFSET_UPDATE_DATE, writeInTransaction);
		fsi.writeLong(header.getUpdateDate(), writeInTransaction, "update date time", DefaultWriteAction.DATA_WRITE_ACTION);
		fsi.writeInt(header.getObjectVersion(), writeInTransaction, "object version");
	}

	protected ObjectInfoHeader getObjectInfoHeader(OID oid, ICache cache) {
		ObjectInfoHeader oih = cache.getObjectInfoHeaderFromOid(oid, false);
		// If object is not in the cache, then read the header from the file
		if (oih == null) {
			oih = objectReader.readObjectInfoHeaderFromOid(oid, false);
		}
		return oih;
	}

	public ObjectInfoHeader updateNextObjectPreviousPointersInCache(OID nextObjectOID, OID previousObjectOID, ICache cache) {
		ObjectInfoHeader oip = cache.getObjectInfoHeaderFromOid(nextObjectOID, false);
		// If object is not in the cache, then read the header from the file
		if (oip == null) {
			oip = objectReader.readObjectInfoHeaderFromOid(nextObjectOID, false);
			cache.addObjectInfo(oip);
		}
		oip.setPreviousObjectOID(previousObjectOID);

		return oip;
	}

	public ObjectInfoHeader updatePreviousObjectNextPointersInCache(OID nextObjectOID, OID previousObjectOID, ICache cache) {
		ObjectInfoHeader oip = cache.getObjectInfoHeaderFromOid(previousObjectOID, false);
		// If object is not in the cache, then read the header from the file
		if (oip == null) {
			oip = objectReader.readObjectInfoHeaderFromOid(previousObjectOID, false);
			cache.addObjectInfo(oip);
		}
		oip.setNextObjectOID(nextObjectOID);

		return oip;
	}

	/**
	 * Manage in place update. Just write the value at the exact position if
	 * possible.
	 * 
	 * @param objectComparator
	 *            Contains all infos about differences between all version
	 *            objects and new version
	 * @param object
	 *            The object being modified (new version)
	 * @param oid
	 *            The oid of the object being modified
	 * @param header
	 *            The header of the object meta representation (Comes from the
	 *            cache)
	 * @param cache
	 *            The cache it self
	 * @param objectInInConnectedZone
	 *            A boolean value to indicate if object is in connected zone. I
	 *            true, change must be made in transaction. If false, changes
	 *            can be made in the database file directly.
	 * @return The number of in place update successfully executed
	 * @throws Exception
	 */
	private int manageInPlaceUpdate(IObjectInfoComparator objectComparator, Object object, OID oid, ObjectInfoHeader header, ICache cache,
			boolean objectIsInConnectedZone) throws Exception {
		boolean canUpdateInPlace = true;
		// If object is is connected zone, changes must be done in transaction,
		// if not in connected zone, changes can be made out of
		// transaction, directly to the database
		boolean writeInTransaction = objectIsInConnectedZone;
		int nbAppliedChanges = 0;
		// if 0, only direct attribute have been changed
		// if (objectComparator.getMaxObjectRecursionLevel() == 0) {
		// if some direct native attribute have changed
		if (objectComparator.getChangedAttributeActions().size() > 0) {
			ChangedNativeAttributeAction caa = null;

			// Check if in place update is possible
			List<ChangedAttribute> actions = objectComparator.getChangedAttributeActions();
			for (int i = 0; i < actions.size(); i++) {
				if (actions.get(i) instanceof ChangedNativeAttributeAction) {
					caa = (ChangedNativeAttributeAction) actions.get(i);
					if (caa.reallyCantDoInPlaceUpdate()) {
						canUpdateInPlace = false;
						break;
					}

					if (false && !caa.inPlaceUpdateIsGuaranteed()) {
						if (caa.isString() && caa.getUpdatePosition() != StorageEngineConstant.NULL_OBJECT_POSITION) {
							long position = safeOverWriteAtomicNativeObject(caa.getUpdatePosition(), (AtomicNativeObjectInfo) caa
									.getNoiWithNewValue(), writeInTransaction);
							canUpdateInPlace = position != -1;
							if (!canUpdateInPlace) {
								break;
							}
						} else {
							canUpdateInPlace = false;
							break;
						}
					} else {
						fsi.setWritePosition(caa.getUpdatePosition(), true);
						writeAtomicNativeObject((AtomicNativeObjectInfo) caa.getNoiWithNewValue(), writeInTransaction);
					}
				} else if (actions.get(i) instanceof ChangedObjectReferenceAttributeAction) {
					ChangedObjectReferenceAttributeAction coraa = (ChangedObjectReferenceAttributeAction) actions.get(i);
					updateObjectReference(coraa.getUpdatePosition(), coraa.getNewId(), writeInTransaction);
				}
				nbAppliedChanges++;
			}
			if (canUpdateInPlace) {
				if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
					DLogger.debug(depthToSpaces() + "Sucessfull in place updating");
				}
				/**
				 * Here we do not need to remove from the cache and add it
				 * again. Just let it. cache.removeObjectWithOid(oid, object);
				 * cache.addObject(oid, object, header);
				 */
			}
		}

		// if canUpdateInplace is false, a full update (writing
		// object elsewhere) is necessary so
		// there is no need to try to update object references.
		if (canUpdateInPlace) {
			NewNonNativeObjectAction nnnoa = null;
			// For non native attribute that have been replaced!
			for (int i = 0; i < objectComparator.getNewObjectMetaRepresentations().size(); i++) {
				// to avoid stackOverFlow, check if the object is
				// already beeing inserted
				nnnoa = objectComparator.getNewObjectMetaRepresentation(i);
				if (cache.idOfInsertingObject(nnnoa) == null) {
					OID ooid = nnnoa.getNnoi().getOid();
					// If Meta representation have an id == null, then
					// this is a new object
					// it must be inserted, else just update
					// reference
					if (ooid == null) {
						ooid = insertNonNativeObject(null, nnnoa.getNnoi(), true);
					}
					updateObjectReference(nnnoa.getUpdatePosition(), ooid, writeInTransaction);
					nbAppliedChanges++;
				}
			}
			SetAttributeToNullAction satna = null;
			// For attribute that have been set to null
			for (int i = 0; i < objectComparator.getAttributeToSetToNull().size(); i++) {
				satna = (SetAttributeToNullAction) objectComparator.getAttributeToSetToNull().get(i);
				updateObjectReference(satna.getUpdatePosition(), StorageEngineConstant.NULL_OBJECT_ID, writeInTransaction);
				nbAppliedChanges++;
			}
			ArrayModifyElement ame = null;
			// For attribute that have been set to null
			for (int i = 0; i < objectComparator.getArrayChanges().size(); i++) {
				ame = (ArrayModifyElement) objectComparator.getArrayChanges().get(i);
				if (!ame.supportInPlaceUpdate()) {
					break;
				}
				fsi.setReadPosition(ame.getArrayPositionDefinition());
				long arrayPosition = fsi.readLong();
				// If we reach this line,the ArrayModifyElement
				// suuports In Place Update so it must be a Native
				// Object Info!
				// The cast is safe :-)
				updateArrayElement(arrayPosition, ame.getArrayElementIndexToChange(), (NativeObjectInfo) ame.getNewValue(),
						writeInTransaction);
				nbAppliedChanges++;
			}
		}
		// }// only direct attribute have been changed
		/*
		 * else { // check if objects of other recursion levels have been
		 * changed! for (int i = 0; i <
		 * objectComparator.getChangedObjectMetaRepresentations().size(); i++) {
		 * // to avoid stackOverFlow, check if the object is // already beeing
		 * inserted // olivier:19/10/2006, changed from != to == if
		 * (cache.idOfInsertingObject
		 * (objectComparator.getChangedObjectMetaRepresentation(i)) == -1) {
		 * updateObject(objectComparator.getChangedObjectMetaRepresentation(i),
		 * false); } } }
		 */
		return nbAppliedChanges;

	}

	private boolean canDoInPlaceUpdate(long updatePosition, String value) {
		fsi.setReadPosition(updatePosition + StorageEngineConstant.NATIVE_OBJECT_OFFSET_DATA_AREA);
		int totalSize = fsi.readInt("String total size");
		int stringNumberOfBytes = byteArrayConverter.getNumberOfBytesOfAString(value, true);
		// Checks if there is enough space to store this new string in place
		return totalSize >= stringNumberOfBytes;
	}

	public String depthToSpaces() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < currentDepth; i++) {
			buffer.append("  ");
		}
		return buffer.toString();
	}

	private void writeBlockSizeAt(long writePosition, int blockSize, boolean writeInTransaction, Object object) {
		if (blockSize < 0) {
			throw new ODBRuntimeException(NeoDatisError.NEGATIVE_BLOCK_SIZE.addParameter(writePosition).addParameter(blockSize).addParameter(
					object.toString()));
		}

		long currentPosition = fsi.getPosition();
		fsi.setWritePosition(writePosition, writeInTransaction);
		fsi.writeInt(blockSize, writeInTransaction, "block size");
		// goes back where we were
		fsi.setWritePosition(currentPosition, writeInTransaction);

	}

	/**
	 * TODO check if we should pass the position instead of requesting if to fsi
	 * 
	 * <pre>
	 *                          Write a collection to the database
	 *                          
	 *                          This is done by writing the number of element s and then the position of all elements.
	 *                          
	 *                          Example : a list with two string element : 'ola' and 'chico'
	 *                          
	 *                          write 2 (as an int) : the number of elements
	 *                          write two times 0 (as long) to reserve the space for the elements positions
	 *                          
	 *                          then write the string 'ola', and keeps its position in the 'positions' array of long 
	 *                          then write the string 'chico' and keeps its position in the 'positions' array of long
	 *                          
	 *                          Then write back all the positions (in this case , 2 positions) after the size of the collection
	 *                          &lt;pre&gt;
	 *                          &#064;param coi
	 *                          &#064;param writeInTransaction
	 * &#064;
	 * 
	 */
	private long writeCollection(CollectionObjectInfo coi, boolean writeInTransaction) {
		long firstObjectPosition = 0;
		long[] attributeIdentifications;
		long startPosition = fsi.getPosition();

		writeNativeObjectHeader(coi.getOdbTypeId(), coi.isNull(), BlockTypes.BLOCK_TYPE_COLLECTION_OBJECT, writeInTransaction);

		if (coi.isNull()) {
			return startPosition;
		}
		Collection<AbstractObjectInfo> collection = coi.getCollection();
		int collectionSize = collection.size();
		Iterator iterator = collection.iterator();

		// write the real type of the collection
		fsi.writeString(coi.getRealCollectionClassName(), false, writeInTransaction);

		// write the size of the collection
		fsi.writeInt(collectionSize, writeInTransaction, "collection size");
		// build a n array to store all element positions
		attributeIdentifications = new long[collectionSize];
		// Gets the current position, to know later where to put the
		// references
		firstObjectPosition = fsi.getPosition();

		// reserve space for object positions : write 'collectionSize' long
		// with zero to store each object position
		for (int i = 0; i < collectionSize; i++) {
			fsi.writeLong(0, writeInTransaction, "collection element pos ", DefaultWriteAction.DATA_WRITE_ACTION);
		}
		int currentElement = 0;
		AbstractObjectInfo element = null;
		while (iterator.hasNext()) {
			element = (AbstractObjectInfo) iterator.next();
			attributeIdentifications[currentElement] = internalStoreObjectWrapper(element);
			currentElement++;
		}

		long positionAfterWrite = fsi.getPosition();
		// now that all objects have been stored, sets their position in the
		// space that have been reserved
		fsi.setWritePosition(firstObjectPosition, writeInTransaction);
		for (int i = 0; i < collectionSize; i++) {
			fsi.writeLong(attributeIdentifications[i], writeInTransaction, "collection element real pos ",
					DefaultWriteAction.DATA_WRITE_ACTION);
		}
		// Goes back to the end of the array
		fsi.setWritePosition(positionAfterWrite, writeInTransaction);

		return startPosition;
	}

	/**
	 * <pre>
	 *                          Write an array to the database
	 *                          
	 *                          This is done by writing :
	 *                          - the array type : array
	 *                          - the array element type (String if it os a String [])
	 *                          - the position of the non native type, if element are non java / C# native
	 *                          - the number of element s and then the position of all elements.
	 *                          
	 *                          Example : an array with two string element : 'ola' and 'chico'
	 *                          write 22 : array
	 *                          write  20 : array of STRING
	 *                          write 0 : it is a java native object
	 *                          write 2 (as an int) : the number of elements
	 *                          write two times 0 (as long) to reserve the space for the elements positions
	 *                          
	 *                          then write the string 'ola', and keeps its position in the 'positions' array of long 
	 *                          then write the string 'chico' and keeps its position in the 'positions' array of long
	 *                          
	 *                          Then write back all the positions (in this case , 2 positions) after the size of the array
	 *                          
	 *                          
	 *                          Example : an array with two User element : user1 and user2
	 *                          write 22 : array
	 *                          write  23 : array of NON NATIVE Objects
	 *                          write 251 : if 250 is the position of the user class info in database
	 *                          write 2 (as an int) : the number of elements
	 *                          write two times 0 (as long) to reserve the space for the elements positions
	 *                          
	 *                          then write the user user1, and keeps its position in the 'positions' array of long 
	 *                          then write the user user2 and keeps its position in the 'positions' array of long
	 *                          &lt;pre&gt;
	 *                          &#064;param object
	 *                          &#064;param odbType
	 *                          &#064;param position
	 *                          &#064;param writeInTransaction
	 * &#064;
	 * 
	 */
	private long writeArray(ArrayObjectInfo aoi, boolean writeInTransaction) {
		long firstObjectPosition = 0;
		long[] attributeIdentifications;
		long startPosition = fsi.getPosition();

		writeNativeObjectHeader(aoi.getOdbTypeId(), aoi.isNull(), BlockTypes.BLOCK_TYPE_ARRAY_OBJECT, writeInTransaction);

		if (aoi.isNull()) {
			return startPosition;
		}

		Object[] array = aoi.getArray();
		int arraySize = array.length;

		// Writes the fact that it is an array
		fsi.writeString(aoi.getRealArrayComponentClassName(), false, writeInTransaction);
		/*
		 * // Write the java natuive type of teh elements of the array,
		 * NON_NATIVE // if not java native
		 * fsi.writeInt(aoi.getOdbType().getSubType().getId(),
		 * writeInTransaction, "native array type id"); if
		 * (aoi.getOdbType().getSubType() == ODBType.NON_NATIVE) {
		 * fsi.writeLong(
		 * storageEngine.getMetaModel().getClassInfo(aoi.getOdbType
		 * ().getSubType().getName()).getPosition(), writeInTransaction,
		 * "non native array class position"); }
		 */
		// write the size of the array
		fsi.writeInt(arraySize, writeInTransaction, "array size");
		// build a n array to store all element positions
		attributeIdentifications = new long[arraySize];
		// Gets the current position, to know later where to put the
		// references
		firstObjectPosition = fsi.getPosition();

		// reserve space for object positions : write 'arraySize' long
		// with zero to store each object position
		for (int i = 0; i < arraySize; i++) {
			fsi.writeLong(0, writeInTransaction, "array element pos ", DefaultWriteAction.DATA_WRITE_ACTION);
		}

		AbstractObjectInfo element = null;
		for (int i = 0; i < arraySize; i++) {
			element = (AbstractObjectInfo) array[i];

			if (element == null || element.isNull()) {
				// TODO Check this
				attributeIdentifications[i] = StorageEngineConstant.NULL_OBJECT_ID_ID;
				continue;
			}
			attributeIdentifications[i] = internalStoreObjectWrapper(element);
		}

		long positionAfterWrite = fsi.getPosition();
		// now that all objects have been stored, sets their position in the
		// space that have been reserved
		fsi.setWritePosition(firstObjectPosition, writeInTransaction);
		for (int i = 0; i < arraySize; i++) {
			fsi.writeLong(attributeIdentifications[i], writeInTransaction, "array real element pos", DefaultWriteAction.DATA_WRITE_ACTION);
		}
		// Gos back to the end of the array
		fsi.setWritePosition(positionAfterWrite, writeInTransaction);

		return startPosition;
	}

	/**
	 * <pre>
	 *                          Write a map to the database
	 *                          
	 *                          This is done by writing the number of element s and then the key and value pair of all elements.
	 *                          
	 *                          Example : a map with two string element : '1/olivier' and '2/chico'
	 *                          
	 *                          write 2 (as an int) : the number of elements
	 *                          write 4 times 0 (as long) to reserve the space for the elements positions
	 *                          
	 *                          then write the object '1' and 'olivier', and keeps the two posiitons in the 'positions' array of long 
	 *                          then write the object '2' and the string chico' and keep the two position in the 'positions' array of long
	 *                          
	 *                          Then write back all the positions (in this case , 4 positions) after the size of the map
	 *                          
	 *                          &#064;param object
	 *                          &#064;param writeInTransaction To specify if these writes must be done in or out of a transaction
	 * &#064;
	 * 
	 */
	private long writeMap(MapObjectInfo moi, boolean writeInTransaction) {
		long firstObjectPosition = 0;
		long[] positions;
		long startPosition = fsi.getPosition();

		writeNativeObjectHeader(moi.getOdbTypeId(), moi.isNull(), BlockTypes.BLOCK_TYPE_MAP_OBJECT, writeInTransaction);

		if (moi.isNull()) {
			return startPosition;
		}

		Map<AbstractObjectInfo,AbstractObjectInfo> map = moi.getMap();
		int mapSize = map.size();
		Iterator<AbstractObjectInfo> keys = map.keySet().iterator();

		// write the map class
		fsi.writeString(moi.getRealMapClassName(), false, writeInTransaction);
		// write the size of the map
		fsi.writeInt(mapSize, writeInTransaction, "map size");

		// build a n array to store all element positions
		positions = new long[mapSize * 2];
		// Gets the current position, to know later where to put the
		// references
		firstObjectPosition = fsi.getPosition();

		// reserve space for object positions : write 'mapSize*2' long
		// with zero to store each object position
		for (int i = 0; i < mapSize * 2; i++) {
			fsi.writeLong(0, writeInTransaction, "map element pos", DefaultWriteAction.DATA_WRITE_ACTION);
		}
		int currentElement = 0;
		while (keys.hasNext()) {
			AbstractObjectInfo key = keys.next();
			AbstractObjectInfo value = map.get(key);

			ODBType keyType = ODBType.getFromClass(key.getClass());
			ODBType valueType = ODBType.getFromClass(value.getClass());

			positions[currentElement++] = internalStoreObjectWrapper( key);
			positions[currentElement++] = internalStoreObjectWrapper( value);
		}

		long positionAfterWrite = fsi.getPosition();
		// now that all objects have been stored, sets their position in the
		// space that have been reserved
		fsi.setWritePosition(firstObjectPosition, writeInTransaction);
		for (int i = 0; i < mapSize * 2; i++) {
			fsi.writeLong(positions[i], writeInTransaction, "map real element pos", DefaultWriteAction.DATA_WRITE_ACTION);
		}
		// Gos back to the end of the array
		fsi.setWritePosition(positionAfterWrite, writeInTransaction);

		return startPosition;
	}

	/**
	 * This method is used to store the object : natibe or non native and return
	 * a number : - The position of the object if it is a native object - The
	 * oid (as a negative number) if it is a non native object
	 * 
	 * @param aoi
	 * @return
	 * @throws Exception
	 */
	private long internalStoreObjectWrapper(AbstractObjectInfo aoi) {
		if (aoi.isNative()) {
			return internalStoreObject((NativeObjectInfo) aoi);
		}
		if (aoi.isNonNativeObject()) {
			OID oid = storeObject(null, (NonNativeObjectInfo) aoi);
			return -oid.getObjectId();
		}

		// Object references are references to object already stored.
		// But in the case of map, the reference can appear before the real
		// object (as order may change)
		// If objectReference.getOid() is null, it is the case. In this case,
		// We take the object being referenced and stores it directly.
		ObjectReference objectReference = (ObjectReference) aoi;
		if (objectReference.getOid() == null) {
			OID oid = storeObject(null, objectReference.getNnoi());
			return -oid.getObjectId();
		}

		return -objectReference.getOid().getObjectId();
	}

	protected void writeNullNativeObjectHeader(int OdbTypeId, boolean writeInTransaction) {
		writeNativeObjectHeader(OdbTypeId, true, BlockTypes.BLOCK_TYPE_NATIVE_NULL_OBJECT, writeInTransaction);
	}

	protected void writeNonNativeNullObjectHeader(OID classInfoId, boolean writeInTransaction) {
		// Block size
		fsi.writeInt(NON_NATIVE_HEADER_BLOCK_SIZE, writeInTransaction, "block size");
		// Block type
		fsi.writeByte(BlockTypes.BLOCK_TYPE_NON_NATIVE_NULL_OBJECT, writeInTransaction);
		// class info id
		fsi.writeLong(classInfoId.getObjectId(), writeInTransaction, "null non native obj class info position",
				DefaultWriteAction.DATA_WRITE_ACTION);
	}

	/**
	 * Write the header of a native attribute
	 * 
	 * @param odbTypeId
	 * @param isNull
	 * @param writeDataInTransaction
	 *            @
	 */
	protected void writeNativeObjectHeader(int odbTypeId, boolean isNull, byte blockType, boolean writeDataInTransaction) {
		byte[] bytes = new byte[10];
		bytes[0] = NATIVE_HEADER_BLOCK_SIZE_BYTE[0];
		bytes[1] = NATIVE_HEADER_BLOCK_SIZE_BYTE[1];
		bytes[2] = NATIVE_HEADER_BLOCK_SIZE_BYTE[2];
		bytes[3] = NATIVE_HEADER_BLOCK_SIZE_BYTE[3];
		bytes[4] = blockType;
		byte[] bytesTypeId = byteArrayConverter.intToByteArray(odbTypeId);
		bytes[5] = bytesTypeId[0];
		bytes[6] = bytesTypeId[1];
		bytes[7] = bytesTypeId[2];
		bytes[8] = bytesTypeId[3];
		bytes[9] = byteArrayConverter.booleanToByteArray(isNull)[0];

		fsi.writeBytes(bytes, writeDataInTransaction, "NativeObjectHeader");
	}

	public long safeOverWriteAtomicNativeObject(long position, AtomicNativeObjectInfo newAnoi, boolean writeInTransaction)
			throws NumberFormatException, IOException {

		// If the attribute an a non fix ize, check if this write is safe
		if (ODBType.hasFixSize(newAnoi.getOdbTypeId())) {
			fsi.setWritePosition(position, writeInTransaction);
			return writeAtomicNativeObject(newAnoi, writeInTransaction);
		}
		if (ODBType.isStringOrBigDicemalOrBigInteger(newAnoi.getOdbTypeId())) {
			fsi.setReadPosition(position + StorageEngineConstant.NATIVE_OBJECT_OFFSET_DATA_AREA);
			int totalSize = fsi.readInt("String total size");
			int stringNumberOfBytes = byteArrayConverter.getNumberOfBytesOfAString(newAnoi.getObject().toString(), true);
			// Checks if there is enough space to store this new string in place
			boolean canUpdate = totalSize >= stringNumberOfBytes;
			if (canUpdate) {
				fsi.setWritePosition(position, writeInTransaction);
				return writeAtomicNativeObject(newAnoi, writeInTransaction, totalSize);
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#
	 * writeAtomicNativeObject
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.AtomicNativeObjectInfo,
	 * boolean, int)
	 */
	public long writeEnumNativeObject(EnumNativeObjectInfo anoi, boolean writeInTransaction) {
		long startPosition = fsi.getPosition();
		int odbTypeId = anoi.getOdbTypeId();

		writeNativeObjectHeader(odbTypeId, anoi.isNull(), BlockTypes.BLOCK_TYPE_NATIVE_OBJECT, writeInTransaction);
		ClassInfo enumCi = anoi.getEnumClassInfo();
		
		if(enumCi==null){
			System.out.println("");
		}
		// Writes the Enum ClassName
		fsi.writeLong(enumCi.getId().getObjectId(), writeInTransaction, "enum class info id",
				DefaultWriteAction.DATA_WRITE_ACTION);
		// Write the Enum String value
		fsi.writeString(anoi.getObject().toString(), writeInTransaction, true, -1);
		return startPosition;

	}

	/**
	 * Writes a natibve attribute
	 * 
	 * @param anoi
	 * @param writeInTransaction
	 *            To specify if data must be written in the transaction or
	 *            directly to database file
	 * @return The object position
	 * @throws NumberFormatException
	 *             @ * TODO the block is set to 0
	 */
	public long writeAtomicNativeObject(AtomicNativeObjectInfo anoi, boolean writeInTransaction) {
		return writeAtomicNativeObject(anoi, writeInTransaction, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#
	 * writeAtomicNativeObject
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.AtomicNativeObjectInfo,
	 * boolean, int)
	 */
	public long writeAtomicNativeObject(AtomicNativeObjectInfo anoi, boolean writeInTransaction, int totalSpaceIfString) {
		long startPosition = fsi.getPosition();
		int odbTypeId = anoi.getOdbTypeId();

		writeNativeObjectHeader(odbTypeId, anoi.isNull(), BlockTypes.BLOCK_TYPE_NATIVE_OBJECT, writeInTransaction);

		if (anoi.isNull()) {
			// Even if object is null, reserve space for to simplify/enable in
			// place update
			fsi.ensureSpaceFor(anoi.getOdbType());
			return startPosition;
		}

		Object object = anoi.getObject();

		switch (odbTypeId) {
		case ODBType.BYTE_ID:
		case ODBType.NATIVE_BYTE_ID:
			fsi.writeByte(((Byte) object).byteValue(), writeInTransaction);
			break;
		case ODBType.BOOLEAN_ID:
		case ODBType.NATIVE_BOOLEAN_ID:
			fsi.writeBoolean(((Boolean) object).booleanValue(), writeInTransaction);
			break;
		case ODBType.CHARACTER_ID:
			fsi.writeChar(((Character) object).charValue(), writeInTransaction);
			break;
		case ODBType.NATIVE_CHAR_ID:
			fsi.writeChar(object.toString().charAt(0), writeInTransaction);
			break;
		case ODBType.FLOAT_ID:
		case ODBType.NATIVE_FLOAT_ID:
			fsi.writeFloat(((Float) object).floatValue(), writeInTransaction);
			break;
		case ODBType.DOUBLE_ID:
		case ODBType.NATIVE_DOUBLE_ID:
			fsi.writeDouble(((Double) object).doubleValue(), writeInTransaction);
			break;
		case ODBType.INTEGER_ID:
		case ODBType.NATIVE_INT_ID:
			fsi.writeInt(((Integer) object).intValue(), writeInTransaction, "native attr");
			break;
		case ODBType.LONG_ID:
		case ODBType.NATIVE_LONG_ID:
			fsi.writeLong(((Long) object).longValue(), writeInTransaction, "native attr", DefaultWriteAction.DATA_WRITE_ACTION);
			break;
		case ODBType.SHORT_ID:
		case ODBType.NATIVE_SHORT_ID:
			fsi.writeShort(((Short) object).shortValue(), writeInTransaction);
			break;
		case ODBType.BIG_DECIMAL_ID:
			fsi.writeBigDecimal((BigDecimal) object, writeInTransaction);
			break;
		case ODBType.BIG_INTEGER_ID:
			fsi.writeBigInteger((BigInteger) object, writeInTransaction);
			break;
		case ODBType.DATE_ID:
		case ODBType.DATE_SQL_ID:
		case ODBType.DATE_TIMESTAMP_ID:
			fsi.writeDate((java.util.Date) object, writeInTransaction);
			break;
		case ODBType.DATE_CALENDAR_ID:
		case ODBType.DATE_GREGORIAN_CALENDAR_ID:
			Calendar c = (Calendar) object;
			fsi.writeDate(c.getTime(), writeInTransaction);
			break;

		case ODBType.STRING_ID:
			fsi.writeString((String) object, writeInTransaction, true, totalSpaceIfString);
			break;
		case ODBType.OID_ID:
			long oid = ((OdbObjectOID) object).getObjectId();
			fsi.writeLong(oid, writeInTransaction, "ODB OID", DefaultWriteAction.DATA_WRITE_ACTION);
			break;
		case ODBType.OBJECT_OID_ID:
			long ooid = ((OdbObjectOID) object).getObjectId();
			fsi.writeLong(ooid, writeInTransaction, "ODB OID", DefaultWriteAction.DATA_WRITE_ACTION);
			break;
		case ODBType.CLASS_OID_ID:
			long coid = ((OdbClassOID) object).getObjectId();
			fsi.writeLong(coid, writeInTransaction, "ODB OID", DefaultWriteAction.DATA_WRITE_ACTION);
			break;

		default:
			// FIXME replace RuntimeException by a
			throw new RuntimeException("native type with odb type id " + odbTypeId + " (" + ODBType.getNameFromId(odbTypeId)
					+ ") for attribute ? is not suported");
		}
		return startPosition;
	}

	/**
	 * Updates the previous object position field of the object at
	 * objectPosition
	 * 
	 * @param objectOID
	 * @param previousObjectOID
	 * @param writeInTransaction
	 *            @
	 */
	public void updatePreviousObjectFieldOfObjectInfo(OID objectOID, OID previousObjectOID, boolean writeInTransaction) {
		long objectPosition = idManager.getObjectPositionWithOid(objectOID, true);
		fsi.setWritePosition(objectPosition + StorageEngineConstant.OBJECT_OFFSET_PREVIOUS_OBJECT_OID, writeInTransaction);
		writeOid(previousObjectOID, writeInTransaction, "prev object position", DefaultWriteAction.POINTER_WRITE_ACTION);
	}

	/**
	 * Update next object oid field of the object at the specific position
	 * 
	 * @param objectOID
	 * @param nextObjectOID
	 * @param writeInTransaction
	 *            @
	 */
	public void updateNextObjectFieldOfObjectInfo(OID objectOID, OID nextObjectOID, boolean writeInTransaction) {
		long objectPosition = idManager.getObjectPositionWithOid(objectOID, true);
		fsi.setWritePosition(objectPosition + StorageEngineConstant.OBJECT_OFFSET_NEXT_OBJECT_OID, writeInTransaction);
		writeOid(nextObjectOID, writeInTransaction, "next object oid of object info", DefaultWriteAction.POINTER_WRITE_ACTION);
	}

	/**
	 * Mark a block as deleted
	 * 
	 * @return The block size
	 * 
	 * @param currentPosition
	 *            @
	 */

	public int markAsDeleted(long currentPosition, OID oid, boolean writeInTransaction) {

		fsi.setReadPosition(currentPosition);
		int blockSize = fsi.readInt();
		fsi.setWritePosition(currentPosition + StorageEngineConstant.NATIVE_OBJECT_OFFSET_BLOCK_TYPE, writeInTransaction);
		// Do not write block size, leave it as it is, to know the available
		// space for future use
		fsi.writeByte(BlockTypes.BLOCK_TYPE_DELETED, writeInTransaction);

		storeFreeSpace(currentPosition, blockSize);

		return blockSize;
	}

	public void storeFreeSpace(long currentPosition, int blockSize) {
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Storing free space at position " + currentPosition + " | block size = " + blockSize);
		}
	}

	/**
	 * Writes a pointer block : A pointer block is like a goto. It can be used
	 * for example when an instance has been updated. To enable all the
	 * references to it to be updated, we just create o pointer at the place of
	 * the updated instance. When searching for the instance, if the block type
	 * is POINTER, then the position will be set to the pointer position
	 * 
	 * @param currentPosition
	 * @param newObjectPosition
	 *            @
	 */
	protected void markAsAPointerTo(OID oid, long currentPosition, long newObjectPosition) {
		throw new ODBRuntimeException(NeoDatisError.FOUND_POINTER.addParameter(oid.getObjectId()).addParameter(newObjectPosition));
	}

	/**
	 * Updates the instance related field of the class info into the database
	 * file Updates the number of objects, the first object oid and the next
	 * class oid
	 * 
	 * @param classInfo
	 *            The class info to be updated
	 * @param writeInTransaction
	 *            To specify if it must be part of a transaction @
	 */
	public void updateInstanceFieldsOfClassInfo(ClassInfo classInfo, boolean writeInTransaction) {
		long currentPosition = fsi.getPosition();
		if (OdbConfiguration.isDebugEnabled(LOG_ID_DEBUG)) {
			DLogger.debug(depthToSpaces() + "Start of updateInstanceFieldsOfClassInfo for " + classInfo.getFullClassName());
		}
		long position = classInfo.getPosition() + StorageEngineConstant.CLASS_OFFSET_CLASS_NB_OBJECTS;
		fsi.setWritePosition(position, writeInTransaction);

		long nbObjects = classInfo.getNumberOfObjects();

		fsi.writeLong(nbObjects, writeInTransaction, "class info update nb objects", DefaultWriteAction.POINTER_WRITE_ACTION);
		writeOid(classInfo.getCommitedZoneInfo().first, writeInTransaction, "class info update first obj oid",
				DefaultWriteAction.POINTER_WRITE_ACTION);
		writeOid(classInfo.getCommitedZoneInfo().last, writeInTransaction, "class info update last obj oid",
				DefaultWriteAction.POINTER_WRITE_ACTION);
		if (OdbConfiguration.isDebugEnabled(LOG_ID_DEBUG)) {
			DLogger.debug(depthToSpaces() + "End of updateInstanceFieldsOfClassInfo for " + classInfo.getFullClassName());
		}
		fsi.setWritePosition(currentPosition, writeInTransaction);
	}

	/**
	 * Updates the last instance field of the class info into the database file
	 * 
	 * @param classInfoPosition
	 *            The class info to be updated
	 * @param lastInstancePosition
	 *            The last instance position @
	 */
	protected void updateLastInstanceFieldOfClassInfoWithId(OID classInfoId, long lastInstancePosition) {
		long currentPosition = fsi.getPosition();
		// TODO CHECK LOGIC of getting position of class using this method for
		// object)
		long classInfoPosition = idManager.getObjectPositionWithOid(classInfoId, true);

		fsi.setWritePosition(classInfoPosition + StorageEngineConstant.CLASS_OFFSET_CLASS_LAST_OBJECT_POSITION, true);
		fsi.writeLong(lastInstancePosition, true, "class info update last instance field", DefaultWriteAction.POINTER_WRITE_ACTION);

		// TODO check if we need this
		fsi.setWritePosition(currentPosition, true);
	}

	/**
	 * Updates the first instance field of the class info into the database file
	 * 
	 * @param classInfoPosition
	 *            The class info to be updated
	 * @param firstInstancePosition
	 *            The first instance position @
	 */
	protected void updateFirstInstanceFieldOfClassInfoWithId(OID classInfoId, long firstInstancePosition) {
		long currentPosition = fsi.getPosition();

		// TODO CHECK LOGIC of getting position of class using this method for
		// object)
		long classInfoPosition = idManager.getObjectPositionWithOid(classInfoId, true);

		fsi.setWritePosition(classInfoPosition + StorageEngineConstant.CLASS_OFFSET_CLASS_FIRST_OBJECT_POSITION, true);
		fsi.writeLong(firstInstancePosition, true, "class info update first instance field", DefaultWriteAction.POINTER_WRITE_ACTION);

		// TODO check if we need this
		fsi.setWritePosition(currentPosition, true);
	}

	/**
	 * Updates the number of objects of the class info into the database file
	 * 
	 * @param classInfoPosition
	 *            The class info to be updated
	 * @param nbObjects
	 *            The number of object @
	 */

	protected void updateNbObjectsFieldOfClassInfo(OID classInfoId, long nbObjects) {
		long currentPosition = fsi.getPosition();
		long classInfoPosition = getSession().getMetaModel().getClassInfoFromId(classInfoId).getPosition();
		fsi.setWritePosition(classInfoPosition + StorageEngineConstant.CLASS_OFFSET_CLASS_NB_OBJECTS, true);
		fsi.writeLong(nbObjects, true, "class info update nb objects", DefaultWriteAction.POINTER_WRITE_ACTION);

		// TODO check if we need this
		fsi.setWritePosition(currentPosition, true);
	}

	/**
	 * <pre>
	 *                      Class User{
	 *                       private String name;
	 *                       private Function function;
	 *                     }
	 *                     
	 *                      When an object of type User is stored, it stores a reference to its function object.
	 *                      If the function is set to another, the pointer to the function object must be changed.
	 *                      for example, it was pointing to a function at the position 1407, the 1407 value is stored while
	 *                      writing the USer object, let's say at the position 528. To make the user point to another function object (which exist at the position 1890)
	 *                      The position 528 must be updated to 1890.
	 * 
	 * 
	 * </pre>
	 * 
	 * @param positionWhereTheReferenceIsStored
	 * @param newOid
	 *            @
	 */
	public void updateObjectReference(long positionWhereTheReferenceIsStored, OID newOid, boolean writeInTransaction) {
		long position = positionWhereTheReferenceIsStored;
		if (position < 0) {
			throw new ODBRuntimeException(NeoDatisError.NEGATIVE_POSITION.addParameter(position));
			/*
			 * long id = position; // This is an id position =
			 * objectReader.getObjectPositionFromItsOid(id, true);
			 */
		}
		fsi.setWritePosition(position, writeInTransaction);
		// Ids are always stored as negative value to differ from a position!
		long oid = StorageEngineConstant.NULL_OBJECT_ID_ID;
		if (newOid != null) {
			oid = -newOid.getObjectId();
		}
		fsi.writeLong(oid, writeInTransaction, "object reference", DefaultWriteAction.POINTER_WRITE_ACTION);
	}

	/**
	 * In place update for array element, only do in place update for atomic
	 * native fixed size elements
	 * 
	 * @param arrayPosition
	 * @param arrayElementIndexToChange
	 * @param newValue
	 * @return true if in place update has been done,false if not
	 * @throws Exception
	 */
	private boolean updateArrayElement(long arrayPosition, int arrayElementIndexToChange, NativeObjectInfo newValue,
			boolean writeInTransaction) throws Exception {
		// block size, block type, odb typeid,is null?
		long offset = ODBType.INTEGER.getSize() + ODBType.BYTE.getSize() + ODBType.INTEGER.getSize() + ODBType.BOOLEAN.getSize();
		fsi.setReadPosition(arrayPosition + offset);
		// read class name of array elements
		String arrayElementClassName = fsi.readString(false);
		// TODO try to get array element type from the ArrayObjectInfo
		// Check if the class has fixed size : array support in place update
		// only for fixed size class like int, long, date,...
		// String array,for example do not support in place update
		ODBType arrayElementType = ODBType.getFromName(arrayElementClassName);
		if (!arrayElementType.isAtomicNative() || !arrayElementType.hasFixSize()) {
			return false;
		}
		ArrayObjectInfo a = null;

		// reads the size of the array
		int arraySize = fsi.readInt();

		if (arrayElementIndexToChange >= arraySize) {
			throw new ODBRuntimeException(NeoDatisError.INPLACE_UPDATE_NOT_POSSIBLE_FOR_ARRAY.addParameter(arraySize).addParameter(
					arrayElementIndexToChange));
		}

		// Gets the position where to write the object
		// Skip the positions where we have the pointers to each array element
		// then
		// jump to the right position
		long skip = arrayElementIndexToChange * ODBType.LONG.getSize();

		fsi.setReadPosition(fsi.getPosition() + skip);
		long elementArrayPosition = fsi.readLong();

		fsi.setWritePosition(elementArrayPosition, writeInTransaction);
		// Actually update the array element
		writeNativeObjectInfo(newValue, elementArrayPosition, true, writeInTransaction);

		return true;

	}

	public void flush() {
		fsi.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#getIdManager
	 * ()
	 */
	public IIdManager getIdManager() {
		return idManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#close()
	 */
	public void close() {
		objectReader = null;
		if (idManager != null) {
			idManager.clear();
			idManager = null;
		}

		storageEngine = null;
		fsi.close();
		fsi = null;

	}

	public static int getNbInPlaceUpdates() {
		return nbInPlaceUpdates;
	}

	public static void setNbInPlaceUpdates(int nbInPlaceUpdates) {
		AbstractObjectWriter.nbInPlaceUpdates = nbInPlaceUpdates;
	}

	public static int getNbNormalUpdates() {
		return nbNormalUpdates;
	}

	public static void setNbNormalUpdates(int nbNormalUpdates) {
		AbstractObjectWriter.nbNormalUpdates = nbNormalUpdates;
	}

	public static void resetNbUpdates() {
		nbInPlaceUpdates = 0;
		nbNormalUpdates = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectWriter#getFsi()
	 */
	public IFileSystemInterface getFsi() {
		return fsi;
	}

	/*
	 * Actually deletes an object from database
	 * 
	 * <pre>
	 * 
	 * 
	 * 
	 * 
	 * </pre>
	 */
	public OID delete(ObjectInfoHeader header) {

		ISession lsession = getSession();
		ICache cache = lsession.getCache();

		long objectPosition = header.getPosition();
		OID classInfoId = header.getClassInfoId();
		OID oid = header.getOid();
		// gets class info from in memory meta model
		ClassInfo ci = getSession().getMetaModel().getClassInfoFromId(classInfoId);

		boolean withIndex = !ci.getIndexes().isEmpty();
		NonNativeObjectInfo nnoi = null;

		// When there is index,we must *always* load the old meta representation
		// to compute index keys
		if (withIndex) {
			nnoi = objectReader.readNonNativeObjectInfoFromPosition(ci, header.getOid(), objectPosition, true, false);
		}

		// a boolean value to indicate if object is in connected zone or not
		// This will be used to know if work can be done out of transaction
		// for unconnected object,changes can be written directly, else we must
		// use Transaction (using WriteAction)
		boolean objectIsInConnectedZone = cache.objectWithIdIsInCommitedZone(header.getOid());

		// triggers
		// FIXME
		triggerManager.manageDeleteTriggerBefore(ci.getFullClassName(), null, header.getOid());

		long nbObjects = ci.getNumberOfObjects();

		OID previousObjectOID = header.getPreviousObjectOID();
		OID nextObjectOID = header.getNextObjectOID();

		boolean isFirstObject = previousObjectOID == null;
		boolean isLastObject = nextObjectOID == null;

		boolean mustUpdatePreviousObjectPointers = false;
		boolean mustUpdateNextObjectPointers = false;
		boolean mustUpdateLastObjectOfCI = false;

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("\nDeleting object with id " + header.getOid() + " - In connected zone =" + objectIsInConnectedZone
					+ " -  with index =" + withIndex + " , type="+ ci.getFullClassName());
			DLogger.debug("position =  " + objectPosition + " | prev oid = " + previousObjectOID + " | next oid = " + nextObjectOID);
			DLogger.debug("isFirst="+ isFirstObject + "| isLast=" + isLastObject + " | nbObjects=" + ci.getNumberOfObjects());
			DLogger.debug("\n");
		}

		CIZoneInfo commitedZI = ci.getCommitedZoneInfo();

		if (isFirstObject || isLastObject) {
			if (isFirstObject) {
				// The deleted object is the first, must update first instance
				// OID field of the class

				if (objectIsInConnectedZone) {
					// update first object oid of the class info in memory
					ci.getCommitedZoneInfo().first = nextObjectOID;
				} else {
					// update first object oid of the class info in memory
					ci.getUncommittedZoneInfo().first = nextObjectOID;
				}

				if (nextObjectOID != null) {
					// Update next object 'previous object oid' to null
					updatePreviousObjectFieldOfObjectInfo(nextObjectOID, null, objectIsInConnectedZone);
					mustUpdateNextObjectPointers = true;
				}
			}
			// It can be first and last
			if (isLastObject) {
				// The deleted object is the last, must update last instance
				// OID field of the class

				// update last object position of the class info in memory
				if (objectIsInConnectedZone) {
					// the object is a committed object
					ci.getCommitedZoneInfo().last = previousObjectOID;
				} else {
					// The object is not committed and it is the last and is
					// being deleted
					ci.getUncommittedZoneInfo().last = previousObjectOID;
				}
				if (previousObjectOID != null) {
					// Update 'next object oid' of previous object to null
					// if we are in unconnected zone, change can be done
					// directly,else it must be done in transaction
					updateNextObjectFieldOfObjectInfo(previousObjectOID, null, objectIsInConnectedZone);
					// Now update data of the cache
					mustUpdatePreviousObjectPointers = true;
					mustUpdateLastObjectOfCI = true;
				}

			}
		} else {
			// Normal case, the deleted object has previous and next object

			// pull the deleted object
			// Mark the 'next object oid field' of the previous object
			// pointing the next object
			updateNextObjectFieldOfObjectInfo(previousObjectOID, nextObjectOID, objectIsInConnectedZone);
			// Mark the 'previous object position field' of the next object
			// pointing the previous object
			updatePreviousObjectFieldOfObjectInfo(nextObjectOID, previousObjectOID, objectIsInConnectedZone);
			mustUpdateNextObjectPointers = true;
			mustUpdatePreviousObjectPointers = true;
		}

		if (mustUpdateNextObjectPointers) {
			updateNextObjectPreviousPointersInCache(nextObjectOID, previousObjectOID, cache);
		}
		if (mustUpdatePreviousObjectPointers) {
			ObjectInfoHeader oih = updatePreviousObjectNextPointersInCache(nextObjectOID, previousObjectOID, cache);
			if (mustUpdateLastObjectOfCI) {
				ci.setLastObjectInfoHeader(oih);
			}
		}

		MetaModel metaModel = lsession.getMetaModel();
		// Saves the fact that something has changed in the class (number of
		// objects and/or last object oid)
		metaModel.addChangedClass(ci);

		// Manage deleting the last object of the committed zone
		
		if (objectIsInConnectedZone) {
			commitedZI.decreaseNbObjects();
		} else {
			ci.getUncommittedZoneInfo().decreaseNbObjects();
		}

		
		
		
		boolean isLastObjectOfCommitedZone = oid.equals(commitedZI.last);
		
		if (isLastObjectOfCommitedZone) {
			// Load the object info header of the last committed object
			ObjectInfoHeader oih = objectReader.readObjectInfoHeaderFromOid(oid, true);
			// Updates last committed object id of the committed zone.
			// Here, it can be null, but there is no problem
			commitedZI.last = oih.getPreviousObjectOID();
			// A simple check, if commitedZI.last is null, nbObject must be 0
			if (commitedZI.last == null && commitedZI.hasObjects()) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR
						.addParameter("The last object of the commited zone has been deleted but the Zone still have objects : nbobjects="
								+ commitedZI.getNbObjects()));
			}
		}

		// Manage deleting the first object of the uncommitted zone
		CIZoneInfo uncommitedZI = ci.getUncommittedZoneInfo();
		boolean isFirstObjectOfUncommitedZone = oid.equals(uncommitedZI.first);
		if (isFirstObjectOfUncommitedZone) {
			if (uncommitedZI.hasObjects()) {
				// Load the object info header of the first uncommitted object
				ObjectInfoHeader oih = objectReader.readObjectInfoHeaderFromOid(oid, true);

				// Updates first uncommitted oid with the second uncommitted oid
				// Here, it can be null, but there is no problem
				uncommitedZI.first = oih.getNextObjectOID();

			} else {
				uncommitedZI.first = null;
			}
		}

		if (isFirstObject && isLastObject) {
			// The object was the first and the last object => it was the only
			// object
			// There is no more objects of this type => must set to null the
			// ClassInfo LastObjectOID
			ci.setLastObjectInfoHeader(null);
		}

		getIdManager().updateIdStatus(header.getOid(), IDStatus.DELETED);
		// The update of the place must be done in transaction if object is in
		// committed zone, else it can be done directly in the file
		markAsDeleted(objectPosition, header.getOid(), objectIsInConnectedZone);
		cache.markIdAsDeleted(header.getOid());

		if (withIndex) {
			manageIndexesForDelete(header.getOid(), nnoi);
		}
		// triggers
		triggerManager.manageDeleteTriggerAfter(ci.getFullClassName(), null, header.getOid());

		return header.getOid();

	}

	public void setTriggerManager(ITriggerManager triggerManager) {
		this.triggerManager = triggerManager;
	}

	
}
