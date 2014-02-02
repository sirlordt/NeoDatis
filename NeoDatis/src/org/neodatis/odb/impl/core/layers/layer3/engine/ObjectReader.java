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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreePersister;
import org.neodatis.odb.CorruptedDatabaseException;
import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.ODBAuthenticationRuntimeException;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.instance.IClassPool;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NativeAttributeHeader;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeDeletedObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.QueryManager;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.core.query.execution.IQueryExecutor;
import org.neodatis.odb.core.transaction.ICache;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITmpCache;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.impl.core.btree.LazyODBBTreePersister;
import org.neodatis.odb.impl.core.layers.layer3.block.BlockTypes;
import org.neodatis.odb.impl.core.layers.layer3.oid.FullIDInfo;
import org.neodatis.odb.impl.core.layers.layer3.oid.IDStatus;
import org.neodatis.odb.impl.core.oid.DatabaseIdImpl;
import org.neodatis.odb.impl.core.oid.TransactionIdImpl;
import org.neodatis.odb.impl.core.query.criteria.CollectionQueryResultAction;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.query.values.GroupByValuesQueryResultAction;
import org.neodatis.odb.impl.core.query.values.ValuesQueryResultAction;
import org.neodatis.odb.impl.tool.Cryptographer;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbArray;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * Manage all IO Reading
 * 
 * @author olivier smadja
 */
public class ObjectReader implements IObjectReader {
	public static long timeToGetObjectFromId = 0;

	public static long calls = 0;

	public static final String LOG_ID = "ObjectReader";

	private static final String LOG_ID_DEBUG = "ObjectReader.debug";

	/** The storage engine */
	public IStorageEngine storageEngine;

	/**
	 * To hold block number. ODB compute the block number from the oid (as one
	 * block has 1000 oids), then it has to search the position of the block
	 * number! This cache is used to keep track of the positions of the block
	 * positions The key is the block number(Long) and the value the position
	 * (Long)
	 */
	private Map<Long,Long> blockPositions;

	/** The fsi is the object that knows how to write and read native types */
	private IFileSystemInterface fsi;

	/** A local variable to monitor object recursion */
	private int currentDepth;

	/** to build instances */
	private IInstanceBuilder instanceBuilder;

	/** to boost class fetch */
	private IClassPool classPool;
	
	protected IByteArrayConverter byteArrayConverter;
	
	protected ITriggerManager triggerManager;

	/** A small method for indentation */
	public String depthToSpaces() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < currentDepth; i++) {
			buffer.append("  ");
		}
		return buffer.toString();
	}

	/**
	 * The constructor
	 * 
	 * @param engine
	 * @param triggerManager
	 */
	public ObjectReader(IStorageEngine engine) {
		this.storageEngine = engine;
		this.fsi = engine.getObjectWriter().getFsi();
		blockPositions = new OdbHashMap<Long, Long>();
		this.instanceBuilder = buildInstanceBuilder();
		this.classPool = OdbConfiguration.getCoreProvider().getClassPool();
		this.byteArrayConverter = OdbConfiguration.getCoreProvider().getByteArrayConverter();
		this.triggerManager = storageEngine.getTriggerManager();
	}

	protected IInstanceBuilder buildInstanceBuilder() {
		return OdbConfiguration.getCoreProvider().getLocalInstanceBuilder(storageEngine);
	}

	/**
	 * Read the version of the database file
	 */
	protected int readVersion() {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_VERSION_POSITION);
		return fsi.readInt();
	}

	/**
	 * Read the encryption flag of the database file
	 */
	protected boolean readEncryptionFlag() {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_USE_ENCRYPTION_POSITION);
		byte b = fsi.readByte();
		return b == StorageEngineConstant.WITH_ENCRYPTION;
	}

	/**
	 * Read the replication flag of the database file
	 */
	protected boolean readReplicationFlag() {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_USE_REPLICATION_POSITION);
		byte b = fsi.readByte();
		return b == StorageEngineConstant.WITH_ENCRYPTION;
	}

	/**
	 * Read the last transaction id
	 */
	protected TransactionId readLastTransactionId(DatabaseId databaseId) {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_LAST_TRANSACTION_ID);
		long[] id = new long[2];
		id[0] = fsi.readLong();
		id[1] = fsi.readLong();
		return new TransactionIdImpl(databaseId, id[0], id[1]);

	}

	/**
	 * Reads the number of classes in database file
	 */
	protected long readNumberOfClasses() {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_NUMBER_OF_CLASSES_POSITION);
		return fsi.readLong();
	}

	/**
	 * Reads the first class OID
	 */
	protected long readFirstClassOid() {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_FIRST_CLASS_OID);
		return fsi.readLong();
	}

	/**
	 * Reads the status of the last odb close
	 */
	protected boolean readLastODBCloseStatus() {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_LAST_CLOSE_STATUS_POSITION);
		return fsi.readBoolean("last odb status");
	}

	/**
	 * Reads the database character encoding
	 */
	protected String readDatabaseCharacterEncoding() {
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_DATABASE_CHARACTER_ENCODING_POSITION);
		return fsi.readString(false);
	}

	/**
	 * see http://wiki.neodatis.org/odb-file-format
	 * 
	 */
	public void readDatabaseHeader(String user, String password) {

		boolean useEncryption = readEncryptionFlag();

		// Reads the version of the database file
		int version = readVersion();

		boolean versionIsCompatible = version == StorageEngineConstant.CURRENT_FILE_FORMAT_VERSION;
		if (!versionIsCompatible) {
			throw new ODBRuntimeException(NeoDatisError.RUNTIME_INCOMPATIBLE_VERSION.addParameter(version).addParameter(
					StorageEngineConstant.CURRENT_FILE_FORMAT_VERSION));
		}

		long[] databaseIdsArray = new long[4];
		databaseIdsArray[0] = fsi.readLong();
		databaseIdsArray[1] = fsi.readLong();
		databaseIdsArray[2] = fsi.readLong();
		databaseIdsArray[3] = fsi.readLong();
		DatabaseId databaseId = new DatabaseIdImpl(databaseIdsArray);

		boolean isReplicated = readReplicationFlag();

		TransactionId lastTransactionId = readLastTransactionId(databaseId);
		// Increment transaction id
		lastTransactionId = lastTransactionId.next();

		long nbClasses = readNumberOfClasses();
		long firstClassPosition = readFirstClassOid();

		if (nbClasses < 0) {
			throw new CorruptedDatabaseException(NeoDatisError.NEGATIVE_CLASS_NUMBER_IN_HEADER.addParameter(nbClasses).addParameter(firstClassPosition));
		}

		boolean lastCloseStatus = readLastODBCloseStatus();

		String databaseCharacterEncoding = readDatabaseCharacterEncoding();
		fsi.setDatabaseCharacterEncoding(databaseCharacterEncoding);

		boolean hasUserAndPassword = fsi.readBoolean("has user&password?");

		// even if database is not user/password protected, there is a fake
		// user/password that has been written
		String userRead = fsi.readString(true);
		String passwordRead = fsi.readString(true);

		if (hasUserAndPassword) {
			String encryptedPassword = Cryptographer.encrypt(password);
			if (!userRead.equals(user) || !passwordRead.equals(encryptedPassword)) {
				throw new ODBAuthenticationRuntimeException();
			}
		} else {
			if (user != null) {
				throw new ODBAuthenticationRuntimeException();
			}
		}

		long currentBlockPosition = fsi.readLong("current block position");

		// Gets the current id block number
		fsi.setReadPosition(currentBlockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_BLOCK_NUMBER);
		int currentBlockNumber = fsi.readInt("current block id number");
		OID maxId = OIDFactory.buildObjectOID(fsi.readLong("Block max id"));

		storageEngine.setVersion(version);
		storageEngine.setDatabaseId(databaseId);
		storageEngine.setNbClasses(nbClasses);
		storageEngine.setLastODBCloseStatus(lastCloseStatus);
		storageEngine.setCurrentIdBlockInfos(currentBlockPosition, currentBlockNumber, maxId);
		storageEngine.setCurrentTransactionId(lastTransactionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#readMetaModel
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.MetaModel, boolean)
	 */
	public MetaModel readMetaModel(MetaModel metaModel, boolean full) {

		OID classOID = null;
		ClassInfo classInfo = null;

		long nbClasses = readNumberOfClasses();
		if (nbClasses == 0) {
			return metaModel;
		}
		// Set the cursor Where We Can Find The First Class info OID
		fsi.setReadPosition(StorageEngineConstant.DATABASE_HEADER_FIRST_CLASS_OID);
		classOID = OIDFactory.buildClassOID(readFirstClassOid());

		// read headers
		for (int i = 0; i < nbClasses; i++) {
			classInfo = readClassInfoHeader(classOID);

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug(depthToSpaces() + "Reading class header for " + classInfo.getFullClassName() + " - oid = " + classOID + " prevOid="
						+ classInfo.getPreviousClassOID() + " - nextOid=" + classInfo.getNextClassOID());
			}

			metaModel.addClass(classInfo);
			classOID = classInfo.getNextClassOID();
		}

		if (!full) {
			return metaModel;
		}

		IOdbList<ClassInfo> allClasses = metaModel.getAllClasses();
		Iterator iterator = allClasses.iterator();
		ClassInfo tempCi = null;
		// Read class info bodies
		while (iterator.hasNext()) {
			tempCi = (ClassInfo) iterator.next();
			try {
				classInfo = readClassInfoBody(tempCi);
			} catch (ODBRuntimeException e) {
				e.addMessageHeader("Error while reading the class info body of " + tempCi);
				throw e;
			}

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug(depthToSpaces() + "Reading class body for " + classInfo.getFullClassName());
			}
			// No need to add it to metamodel, it is already in it.
			// metaModel.addClass(classInfo);
		}

		// Read last object of each class
		iterator = allClasses.iterator();
		while (iterator.hasNext()) {
			classInfo = (ClassInfo) iterator.next();

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug(depthToSpaces() + "Reading class info last instance " + classInfo.getFullClassName());
			}
			if (classInfo.getCommitedZoneInfo().hasObjects()) {
				// TODO Check if must use true or false in return object
				// parameter
				try {
					// Retrieve the object by oid instead of position
					OID oid = classInfo.getCommitedZoneInfo().last;
					classInfo.setLastObjectInfoHeader(readObjectInfoHeaderFromOid(oid, true));
				} catch (ODBRuntimeException e) {
					throw new ODBRuntimeException(NeoDatisError.METAMODEL_READING_LAST_OBJECT.addParameter(classInfo.getFullClassName()).addParameter(
							classInfo.getCommitedZoneInfo().last), e);
				}

			}
		}

		IOdbList<ClassInfoIndex> indexes = null;
		IBTreePersister persister = null;
		ClassInfoIndex cii = null;
		IQuery queryClassInfo = null;
		IBTree btree = null;

		storageEngine.resetCommitListeners();

		// Read class info indexes
		iterator = allClasses.iterator();
		while (iterator.hasNext()) {
			classInfo = (ClassInfo) iterator.next();
			indexes = new OdbArrayList<ClassInfoIndex>();
			queryClassInfo = new CriteriaQuery(ClassInfoIndex.class, Where.equal("classInfoId", classInfo.getId()));
			Objects<ClassInfoIndex> classIndexes = getObjects(queryClassInfo, true, -1, -1);
			indexes.addAll(classIndexes);
			// Sets the btree persister
			for (int j = 0; j < indexes.size(); j++) {
				cii = indexes.get(j);
				persister = new LazyODBBTreePersister(storageEngine);
				btree = cii.getBTree();
				btree.setPersister(persister);
				btree.getRoot().setBTree(btree);
			}
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug(depthToSpaces() + "Reading indexes for " + classInfo.getFullClassName() + " : " + indexes.size() + " indexes");
			}
			classInfo.setIndexes(indexes);
		}

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Current Meta Model is :" + metaModel);
		}
		return metaModel;
	}

	/**
	 * Read the class info header with the specific oid
	 * 
	 * @param startPosition
	 * @return The read class info object @
	 */
	protected ClassInfo readClassInfoHeader(OID classInfoOid) {
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Reading new Class info Header with oid " + classInfoOid);
		}

		long classInfoPosition = getObjectPositionFromItsOid(classInfoOid, true, true);
		fsi.setReadPosition(classInfoPosition);
		int blockSize = fsi.readInt("class info block size");
		byte blockType = fsi.readByte("class info block type");

		if (!BlockTypes.isClassHeader(blockType)) {
			throw new ODBRuntimeException(NeoDatisError.WRONG_TYPE_FOR_BLOCK_TYPE.addParameter("Class Header").addParameter(blockType).addParameter(classInfoPosition));
		}
		byte classInfoCategory = fsi.readByte("class info category");
		ClassInfo classInfo = new ClassInfo();
		classInfo.setClassCategory(classInfoCategory);
		classInfo.setPosition(classInfoPosition);
		classInfo.setId(OIDFactory.buildClassOID(fsi.readLong()));

		classInfo.setBlockSize(blockSize);
		classInfo.setPreviousClassOID(readOid("prev class oid"));
		classInfo.setNextClassOID(readOid("next class oid"));

		classInfo.getOriginalZoneInfo().setNbObjects(fsi.readLong());
		classInfo.getOriginalZoneInfo().first = readOid("ci first object oid");
		classInfo.getOriginalZoneInfo().last = readOid("ci last object oid");

		classInfo.getCommitedZoneInfo().set(classInfo.getOriginalZoneInfo());

		classInfo.setFullClassName(fsi.readString(false));
		// FIXME : Extract extra info : c# compatibility
		classInfo.setExtraInfo("");

		classInfo.setMaxAttributeId(fsi.readInt());
		classInfo.setAttributesDefinitionPosition(fsi.readLong());

		// FIXME Convert block size to long ??
		int realBlockSize = (int) (fsi.getPosition() - classInfoPosition);

		if (blockSize != realBlockSize) {
			throw new ODBRuntimeException(NeoDatisError.WRONG_BLOCK_SIZE.addParameter(blockSize).addParameter(realBlockSize).addParameter(classInfoPosition));
		}

		return classInfo;
	}

	private OID decodeOid(byte[] bytes, int offset) {
		long oid = byteArrayConverter.byteArrayToLong(bytes, offset);
		if (oid == -1) {
			return null;
		}
		return OIDFactory.buildObjectOID(oid);
	}

	private OID readOid(String label) {
		long oid = fsi.readLong(label);
		if (oid == -1) {
			return null;
		}
		return OIDFactory.buildObjectOID(oid);
	}

	/**
	 * Reads the body of a class info
	 * 
	 * @param classInfo
	 *            The class info to be read with already read header
	 * @return The read class info @
	 */
	protected ClassInfo readClassInfoBody(ClassInfo classInfo) {
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Reading new Class info Body at " + classInfo.getAttributesDefinitionPosition());
		}
		fsi.setReadPosition(classInfo.getAttributesDefinitionPosition());

		int blockSize = fsi.readInt();
		byte blockType = fsi.readByte();

		if (!BlockTypes.isClassBody(blockType)) {
			throw new ODBRuntimeException(NeoDatisError.WRONG_TYPE_FOR_BLOCK_TYPE.addParameter("Class Body").addParameter(blockType).addParameter(
					classInfo.getAttributesDefinitionPosition()));
		}
		// TODO This should be a short instead of long
		long nbAttributes = fsi.readLong();
		IOdbList<ClassAttributeInfo> attributes = new OdbArrayList<ClassAttributeInfo>((int) nbAttributes);

		for (int i = 0; i < nbAttributes; i++) {
			attributes.add(readClassAttributeInfo());
		}
		classInfo.setAttributes(attributes);

		// FIXME Convert blocksize to long ??
		int realBlockSize = (int) (fsi.getPosition() - classInfo.getAttributesDefinitionPosition());

		if (blockSize != realBlockSize) {
			throw new ODBRuntimeException(NeoDatisError.WRONG_BLOCK_SIZE.addParameter(blockSize).addParameter(realBlockSize).addParameter(
					classInfo.getAttributesDefinitionPosition()));
		}

		return classInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#
	 * readClassInfoIndexesAt(long,
	 * org.neodatis.odb.core.impl.layers.layer2.meta.ClassInfo)
	 */
	public IOdbList<ClassInfoIndex> readClassInfoIndexesAt(long position, ClassInfo classInfo) {
		IOdbList<ClassInfoIndex> indexes = new OdbArrayList<ClassInfoIndex>();
		fsi.setReadPosition(position);
		ClassInfoIndex cii = null;
		long previousIndexPosition = -1;
		long nextIndexPosition = position;

		byte blockType = 0;
		int blockSize = -1;
		int nbAttributes = -1;
		int[] attributeIds = null;

		do {
			cii = new ClassInfoIndex();

			fsi.setReadPosition(nextIndexPosition);
			blockSize = fsi.readInt("block size");
			blockType = fsi.readByte("block type");

			if (!BlockTypes.isIndex(blockType)) {
				throw new ODBRuntimeException(NeoDatisError.WRONG_TYPE_FOR_BLOCK_TYPE.addParameter(BlockTypes.BLOCK_TYPE_INDEX).addParameter(blockType).addParameter(
						position).addParameter("while reading indexes for " + classInfo.getFullClassName()));
			}
			previousIndexPosition = fsi.readLong("prev index pos");
			nextIndexPosition = fsi.readLong("next index pos");

			cii.setName(fsi.readString(false, "Index name"));
			cii.setUnique(fsi.readBoolean("index is unique"));
			cii.setStatus(fsi.readByte("index status"));
			cii.setCreationDate(fsi.readLong("creation date"));
			cii.setLastRebuild(fsi.readLong("last rebuild"));
			nbAttributes = fsi.readInt("number of fields");
			attributeIds = new int[nbAttributes];
			for (int j = 0; j < nbAttributes; j++) {
				attributeIds[j] = fsi.readInt("attr id");
			}
			cii.setAttributeIds(attributeIds);
			indexes.add(cii);
		} while (nextIndexPosition != -1);

		return indexes;

	}

	/**
	 * Read an attribute of a class at the current position
	 * 
	 * @return The ClassAttributeInfo description of the class attribute @
	 */
	private ClassAttributeInfo readClassAttributeInfo() {
		ClassAttributeInfo cai = new ClassAttributeInfo();
		int attributeId = fsi.readInt();
		boolean isNative = fsi.readBoolean();
		if (isNative) {
			int attributeTypeId = fsi.readInt();
			ODBType type = ODBType.getFromId(attributeTypeId);
			// if it is an array, read also the subtype
			if (type.isArray()) {
				type = type.copy();
				int subTypeId = fsi.readInt();
				ODBType subType = ODBType.getFromId(subTypeId);
				if (subType.isNonNative()) {
					subType = subType.copy();
					subType.setName(storageEngine.getSession(true).getMetaModel().getClassInfoFromId(OIDFactory.buildClassOID(fsi.readLong()))
							.getFullClassName());
				}
				type.setSubType(subType);
			}
			cai.setAttributeType(type);
			// For enum, we get the class info id of the enum class
			if(type.isEnum()){
				long classInfoId = fsi.readLong();
				MetaModel metaModel = storageEngine.getSession(true).getMetaModel();
				cai.setFullClassName(metaModel.getClassInfoFromId(OIDFactory.buildClassOID(classInfoId)).getFullClassName());
				// For enum, we need to create a new type just to set the real enum class name
				type = type.copy();
				type.setName(cai.getFullClassname());
				cai.setAttributeType(type);
			}else{
				cai.setFullClassName(cai.getAttributeType().getName());
			}
		} else {
			// This is a non native, gets the id of the type and gets it from
			// meta-model
			MetaModel metaModel = storageEngine.getSession(true).getMetaModel();
			long typeId = fsi.readLong();
			cai.setFullClassName(metaModel.getClassInfoFromId(OIDFactory.buildClassOID(typeId)).getFullClassName());
			cai.setClassInfo(metaModel.getClassInfo(cai.getFullClassname(), true));
			cai.setAttributeType(ODBType.getFromName(cai.getFullClassname()));
		}
		cai.setName(fsi.readString(false));
		cai.setIndex(fsi.readBoolean());
		cai.setId(attributeId);
		return cai;
	}

	/**
	 * Reads an object at the specific position
	 * 
	 * @param position
	 *            The position to read
	 * @param useCache
	 *            To indicate if cache must be used
	 * @param To
	 *            indicate if an instance must be return of just the meta info
	 * 
	 * @return The object with position @
	 */
	public Object readNonNativeObjectAtPosition(long position, boolean useCache, boolean returnInstance) {

		// First reads the object info - which is a meta representation of the
		// object
		NonNativeObjectInfo nnoi = readNonNativeObjectInfoFromPosition(null, null, position, useCache, returnInstance);

		if (nnoi.isDeletedObject()) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_IS_MARKED_AS_DELETED_FOR_POSITION.addParameter(position));
		}

		if (!returnInstance) {
			return nnoi;
		}
		// Then converts it to the real object
		Object o = instanceBuilder.buildOneInstance(nnoi);

		return o;
	}

	public AbstractObjectInfo readObjectInfo(long objectIdentification, boolean useCache, boolean returnObjects) {
		// If object identification is negative, it is an oid.
		if (objectIdentification < 0) {
			OID oid = OIDFactory.buildObjectOID(-objectIdentification);
			return readNonNativeObjectInfoFromOid(null, oid, useCache, returnObjects);
		}
		return readObjectInfoFromPosition(null, objectIdentification, useCache, returnObjects);
	}

	/**
	 * Reads the pointers(ids or positions) of an object that has the specific
	 * oid
	 * 
	 * @param oid
	 *            The oid of the object we want to read the pointers
	 * @return The ObjectInfoHeader @
	 */
	public ObjectInfoHeader readObjectInfoHeaderFromOid(OID oid, boolean useCache) {
		ObjectInfoHeader oih = null;
		if (useCache) {
			oih = getSession().getCache().getObjectInfoHeaderFromOid(oid, false);
			if (oih != null) {
				return oih;
			}
		}
		long position = getObjectPositionFromItsOid(oid, useCache, true);
		return readObjectInfoHeaderFromPosition(oid, position, useCache);
	}

	public ObjectInfoHeader readObjectInfoHeaderFromPosition(OID oid, long position, boolean useCache) {
		OID classInfoId = null;

		if (position > fsi.getLength()) {
			throw new CorruptedDatabaseException(NeoDatisError.INSTANCE_POSITION_OUT_OF_FILE.addParameter(position).addParameter(fsi.getLength()));
		}
		if (position < 0) {
			throw new CorruptedDatabaseException(NeoDatisError.INSTANCE_POSITION_IS_NEGATIVE.addParameter(position).addParameter(String.valueOf(oid)));
		}

		// adds an integer because, we pull the block size
		fsi.setReadPosition(position + ODBType.INTEGER.getSize());
		byte blockType = fsi.readByte("object block type");

		if (BlockTypes.isNonNative(blockType)) {
			
			// compute the number of bytes to read
			// OID + ClassOid + PrevOid + NextOid + createDate + update Date + objectVersion + objectRefPointer + isSync + nbAttributes
			// Long + Long +    Long    +  Long    + Long       + Long       +   int         +   Long            + Bool    + Int       
			// atsize = ODBType.SIZE_OF_INT+ODBType.SIZE_OF_LONG;
 			int tsize = 7*ODBType.SIZE_OF_LONG + 2*ODBType.SIZE_OF_INT + 1*ODBType.SIZE_OF_BOOL;
 			byte[] abytes = fsi.readBytes(tsize);
 			
 			
			OID readOid = decodeOid(abytes, 0);
			// oid can be -1 (if was not set),in this case there is no way to
			// check
			if (oid != null && readOid.compareTo(oid) != 0) {
				throw new CorruptedDatabaseException(NeoDatisError.WRONG_OID_AT_POSITION.addParameter(oid).addParameter(position).addParameter(readOid));
			}
			// If oid is not defined, uses the one that has been read
			if (oid == null) {
				oid = readOid;
			}
			// It is a non native object
			classInfoId = OIDFactory.buildClassOID(byteArrayConverter.byteArrayToLong(abytes, 8));
			OID prevObjectOID =  decodeOid(abytes, 16); 
			OID nextObjectOID = decodeOid(abytes, 24);

			long creationDate = byteArrayConverter.byteArrayToLong(abytes, 32);
			long updateDate = byteArrayConverter.byteArrayToLong(abytes, 40);
			int objectVersion = byteArrayConverter.byteArrayToInt(abytes, 48);
			long objectReferencePointer = byteArrayConverter.byteArrayToLong(abytes, 52);
			boolean isSynchronized = byteArrayConverter.byteArrayToBoolean(abytes, 60);

			// Now gets info about attributes
			int nbAttributesRead = byteArrayConverter.byteArrayToInt(abytes, 61);

			// Now gets an array with the identification all attributes (can be
			// positions(for native objects) or ids(for non native objects))
			long[] attributesIdentification = new long[nbAttributesRead];
			int[] attributeIds = new int[nbAttributesRead];
			int atsize = ODBType.SIZE_OF_INT+ODBType.SIZE_OF_LONG;
			// Reads the bytes and then convert to values
			byte[] bytes = fsi.readBytes(nbAttributesRead*atsize);
			
			for (int i = 0; i < nbAttributesRead; i++) {
				attributeIds[i] = byteArrayConverter.byteArrayToInt(bytes, i*atsize);
				attributesIdentification[i] = byteArrayConverter.byteArrayToLong(bytes, i*atsize+ODBType.SIZE_OF_INT);
			}
			ObjectInfoHeader oip = new ObjectInfoHeader(position, prevObjectOID, nextObjectOID, classInfoId, attributesIdentification, attributeIds);
			oip.setObjectVersion(objectVersion);
			oip.setCreationDate(creationDate);
			oip.setUpdateDate(updateDate);

			oip.setOid(oid);
			oip.setClassInfoId(classInfoId);
			// oip.setCreationDate(creationDate);
			// oip.setUpdateDate(updateDate);
			// oip.setObjectVersion(objectVersion);
			if (useCache) {
				// the object info does not exist in the cache
				storageEngine.getSession(true).getCache().addObjectInfo(oip);
			}
			return oip;
		}

		if (BlockTypes.isPointer(blockType)) {
			throw new CorruptedDatabaseException(NeoDatisError.FOUND_POINTER.addParameter(oid).addParameter(position));
		}

		throw new CorruptedDatabaseException(NeoDatisError.WRONG_TYPE_FOR_BLOCK_TYPE.addParameter(BlockTypes.BLOCK_TYPE_NON_NATIVE_OBJECT).addParameter(blockType)
				.addParameter(position + "/oid=" + oid));
	}

	/**
	 * Reads an object info(Object meta information like its type and its
	 * values) from the database file
	 * <p/>
	 * 
	 * <pre>
	 *             reads its type and then read all its attributes.
	 *             If one attribute is a non native object, it will be read (recursivly).
	 *            &lt;p/&gt;
	 * </pre>
	 * 
	 * @param classInfo
	 *            If null, we are probably reading a native instance : String
	 *            for example
	 * @param position
	 * @param useCache
	 *            To indicate if cache must be used. If not, the old version of
	 *            the object will read
	 * @return The object abstract meta representation @
	 */
	public AbstractObjectInfo readObjectInfoFromPosition(ClassInfo classInfo, long objectPosition, boolean useCache, boolean returnObjects) {
		currentDepth++;
		try {
			// Protection against bad parameter value
			if (objectPosition > fsi.getLength()) {
				throw new ODBRuntimeException(NeoDatisError.INSTANCE_POSITION_OUT_OF_FILE.addParameter(objectPosition).addParameter(fsi.getLength()));
			}

			if (objectPosition == StorageEngineConstant.DELETED_OBJECT_POSITION || objectPosition == StorageEngineConstant.NULL_OBJECT_POSITION) {
				// TODO Is this correct ?
				return new NonNativeDeletedObjectInfo(objectPosition, null);
			}
			ICache cache = storageEngine.getSession(true).getCache();

			// Read block size and block type
			// block type is used to decide what to do
			fsi.setReadPosition(objectPosition);
			// Reads the block size
			int blockSize = fsi.readInt("object block size");
			// And the block type
			byte blockType = fsi.readByte("object block type");

			// Null objects
			if (BlockTypes.isNullNonNativeObject(blockType)) {
				return new NonNativeNullObjectInfo(classInfo);
			}
			if (BlockTypes.isNullNativeObject(blockType)) {
				return NullNativeObjectInfo.getInstance();
			}
			// Deleted objects
			if (BlockTypes.isDeletedObject(blockType)) {
				return new NonNativeDeletedObjectInfo(objectPosition, null);
			}

			// Checks if what we are reading is only a pointer to the real
			// block, if
			// it is the case, just recall this method with the right position
			if (BlockTypes.isPointer(blockType)) {
				throw new CorruptedDatabaseException(NeoDatisError.FOUND_POINTER.addParameter(objectPosition));
			}

			// Native of non native object ?
			if (BlockTypes.isNative(blockType)) {
				// Reads the odb type id of the native objects
				int odbTypeId = fsi.readInt();
				// Reads a boolean to know if object is null
				boolean isNull = fsi.readBoolean("Native object is null ?");

				if (isNull) {
					return new NullNativeObjectInfo(odbTypeId);
				}
				// last parameter is false=> no need to read native object
				// header, it has been done
				return readNativeObjectInfo(odbTypeId, objectPosition, useCache, returnObjects, false);

			}

			if (BlockTypes.isNonNative(blockType)) {
				throw new ODBRuntimeException(NeoDatisError.OBJECT_READER_DIRECT_CALL);
			}

			throw new ODBRuntimeException(NeoDatisError.UNKNOWN_BLOCK_TYPE.addParameter(blockType).addParameter(fsi.getPosition() - 1));
		} finally {
			currentDepth--;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#
	 * readNonNativeObjectInfoFromOid
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.ClassInfo,
	 * org.neodatis.odb.core.OID, boolean, boolean)
	 */
	public NonNativeObjectInfo readNonNativeObjectInfoFromOid(ClassInfo classInfo, OID oid, boolean useCache, boolean returnObjects) {
		// FIXME if useCache, why not directly search the cache?

		long position = getObjectPositionFromItsOid(oid, useCache, false);

		if (position == StorageEngineConstant.DELETED_OBJECT_POSITION) {
			return new NonNativeDeletedObjectInfo(position, oid);
		}
		if (position == StorageEngineConstant.OBJECT_DOES_NOT_EXIST) {
			throw new ODBRuntimeException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST.addParameter(oid));
		}
		NonNativeObjectInfo nnoi = readNonNativeObjectInfoFromPosition(classInfo, oid, position, useCache, returnObjects);

		// Manage CS select triggers here
		if(!storageEngine.isLocal()){
			// Lazy instantiation
			if(triggerManager==null){
				triggerManager = storageEngine.getTriggerManager();
			}
			
			String fullClassName = nnoi.getClassInfo().getFullClassName();
			if(triggerManager.hasSelectTriggersFor(fullClassName)){
				// We are in cs mode, manage select triggers here
				triggerManager.manageSelectTriggerAfter(fullClassName, nnoi, oid);
			}
		}
		
		return nnoi;
	}

	/**
	 * Reads a non non native Object Info (Layer2) from its position
	 * 
	 * @param classInfo
	 * @param oid
	 *            can be null
	 * @param position
	 * @param useCache
	 * @param returnInstance
	 * @return The meta representation of the object @
	 */
	public NonNativeObjectInfo readNonNativeObjectInfoFromPosition(ClassInfo classInfo, OID oid, long position, boolean useCache, boolean returnInstance) {
		ISession lsession = storageEngine.getSession(true);

		// Get a temporary cache just to cache NonNativeObjectInfo being read to
		// avoid duplicated reads

		ICache cache = lsession.getCache();
		ITmpCache tmpCache = lsession.getTmpCache();
		// ICache tmpCache =cache;

		// We are dealing with a non native object
		NonNativeObjectInfo objectInfo = null;

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Reading Non Native Object info with oid " + oid);
		}

		// If the object is already being read, then return from the cache
		if (tmpCache.isReadingObjectInfoWithOid(oid)) {
			return tmpCache.getReadingObjectInfoFromOid(oid);
		}
		ObjectInfoHeader objectInfoHeader = getObjectInfoHeader(oid, position, useCache, cache);

		if (classInfo == null) {
			classInfo = storageEngine.getSession(true).getMetaModel().getClassInfoFromId(objectInfoHeader.getClassInfoId());
		}
		oid = objectInfoHeader.getOid();

		// if class info do not match, reload class info
		if (!classInfo.getId().equals(objectInfoHeader.getClassInfoId())) {
			classInfo = storageEngine.getSession(true).getMetaModel().getClassInfoFromId(objectInfoHeader.getClassInfoId());
		}

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Reading Non Native Object info of " + (classInfo == null ? "?" : classInfo.getFullClassName()) + " at "
					+ objectInfoHeader.getPosition() + " with id " + oid);
			DLogger.debug(depthToSpaces() + "  Object Header is " + objectInfoHeader);
		}

		objectInfo = new NonNativeObjectInfo(objectInfoHeader, classInfo);
		objectInfo.setOid(oid);
		objectInfo.setClassInfo(classInfo);
		objectInfo.setPosition(objectInfoHeader.getPosition());

		// Adds the Object Info in cache. The remove (cache clearing) is done by
		// the Query Executor. This tmp cache is used to resolve cyclic reference problem.
		// When an object has cyclic reference, if we don t cache the object info, we will read the reference for ever!
		// With the cache , we detect the cyclic reference and return what has been read already
		tmpCache.startReadingObjectInfoWithOid(objectInfo.getOid(), objectInfo);

		ClassAttributeInfo cai = null;
		AbstractObjectInfo aoi = null;
		long attributeIdentification = -1;
		IOdbList<PendingReading> pendingReadings = new OdbArrayList<PendingReading>();
		for (int id = 1; id <= classInfo.getMaxAttributeId(); id++) {
			cai = objectInfo.getClassInfo().getAttributeInfoFromId(id);

			if (cai == null) {
				// the attribute does not exist anymore
				continue;
			}
			attributeIdentification = objectInfoHeader.getAttributeIdentificationFromId(id);

			if (attributeIdentification == StorageEngineConstant.NULL_OBJECT_POSITION || attributeIdentification == StorageEngineConstant.NULL_OBJECT_ID_ID) {
				if (cai.isNative()) {
					aoi = NullNativeObjectInfo.getInstance();
				} else {
					aoi = new NonNativeNullObjectInfo();
				}
				objectInfo.setAttributeValue(id, aoi);
			} else {
				// Here we can not use cai.isNonNative because of interfaces :
				// because an interface will always be considered as non native
				// (Object for example) but
				// could contain a String for example. So we assume that if
				// attributeIdentification is negative
				// the object is non native,if positive the object is native.
				if (attributeIdentification < 0) {
					// ClassInfo ci =
					// storageEngine.getSession(true).getMetaModel().getClassInfo(cai.getFullClassname(),
					// true);
					// For non native objects. attribute identification is the
					// oid (*-1)
					OID attributeOid = OIDFactory.buildObjectOID(-attributeIdentification);
					// We do not read now, store the reading as pending and
					// reads it later
					pendingReadings.add(new PendingReading(id, null/* ci */, attributeOid));
				} else {
					aoi = readObjectInfo(attributeIdentification, useCache, returnInstance);
					objectInfo.setAttributeValue(id, aoi);
				}
			}
		}
		PendingReading pr = null;
		for (int i = 0; i < pendingReadings.size(); i++) {
			pr = pendingReadings.get(i);
			// If object is not in connected zone , the cache must be used
			boolean useCacheForAttribute = useCache || !cache.objectWithIdIsInCommitedZone(pr.getAttributeOID());
			aoi = readNonNativeObjectInfoFromOid(pr.getCi(), pr.getAttributeOID(), useCacheForAttribute, returnInstance);
			objectInfo.setAttributeValue(pr.getId(), aoi);
		}

		// FIXME Check if instance is being built on client server mode
		if (returnInstance) {
			//objectInfo.setObject(instanceBuilder.buildOneInstance(objectInfo));
		}
		return objectInfo;
	}

	public AttributeValuesMap readObjectInfoValuesFromOID(ClassInfo classInfo, OID oid, boolean useCache, IOdbList<String> attributeNames, IOdbList<String> relationAttributeNames,
			int recursionLevel, String[] orderByFields, boolean useOidForObject) {
		long position = getObjectPositionFromItsOid(oid, useCache, true);
		return readObjectInfoValuesFromPosition(classInfo, oid, position, useCache, attributeNames, relationAttributeNames, recursionLevel, orderByFields, useOidForObject);
	}

	/**
	 * 
	 * @param classInfo
	 *            The class info of the objects to be returned
	 * @param oid
	 *            The Object id of the object to return data
	 * @param position
	 *            The position of the object to read
	 * @param useCache
	 *            To indicate if cache must be used
	 * @param attributeNames
	 *            The list of the attribute name for which we need to return a
	 *            value, an attributename can contain relation like profile.name
	 * @param relationAttributeNames
	 *            The original names of attributes to read the values, an
	 *            attributename can contain relation like profile.name
	 * @param recursionLevel
	 *            The recursion level of this call
	 * @param orderByFields
	 *            ?
	 * 
	 * @param useOidForObject To indicate that if the object being read has an attribute that is not native, we will put the oid of the attribute (object) instead of reading the whole object
	 * @return A Map where keys are attributes names and values are the values
	 *         of there attributes @
	 */
	protected AttributeValuesMap readObjectInfoValuesFromPosition(ClassInfo classInfo, OID oid, long position, boolean useCache, IOdbList<String> attributeNames,
			IOdbList<String> relationAttributeNames, int recursionLevel, String[] orderByFields, boolean useOidForObject) {
		currentDepth++;
		// The resulting map
		AttributeValuesMap map = new AttributeValuesMap();

		// Protection against bad parameter value
		if (position > fsi.getLength()) {
			throw new ODBRuntimeException(NeoDatisError.INSTANCE_POSITION_OUT_OF_FILE.addParameter(position).addParameter(fsi.getLength()));
		}

		ICache cache = storageEngine.getSession(true).getCache();

		// If object is already being read, simply return its cache - to avoid
		// stackOverflow for cyclic references
		// FIXME check this : should we use cache?
		/*
		 * if (cache.isReadingObjectInfo(position)) { return
		 * cache.getReadingObjectInfo(position); }
		 */

		// Go to the object position
		fsi.setReadPosition(position);

		// Read the block size of the object
		int blockSize = fsi.readInt();
		// Read the block type of the object
		byte blockType = fsi.readByte();

		if (BlockTypes.isNull(blockType) || BlockTypes.isDeletedObject(blockType)) {
			return map;
		}
		// Checks if what we are reading is only a pointer to the real block, if
		// it is the case, Throw an exception. Pointer are not used anymore
		if (BlockTypes.isPointer(blockType)) {
			throw new CorruptedDatabaseException(NeoDatisError.FOUND_POINTER.addParameter(oid).addParameter(position));
		}

		try {
			// Read the header of the object, no need to cache when reading
			// object infos
			// For local mode, we need to use cache to get unconnected objects.
			// TestDelete.test14
			ObjectInfoHeader objectInfoHeader = getObjectInfoHeader(oid, position, true, cache);
			// Get the object id
			oid = objectInfoHeader.getOid();

			// If class info is not defined, define it
			if (classInfo == null) {
				classInfo = storageEngine.getSession(true).getMetaModel().getClassInfoFromId(objectInfoHeader.getClassInfoId());
			}
			if (recursionLevel == 0) {
				map.setObjectInfoHeader(objectInfoHeader);
			}
			// If object is native, it can have attributes, just return the
			// empty
			// map
			if (BlockTypes.isNative(blockType)) {
				return map;
			}

			ClassAttributeInfo cai = null;
			int nbAttributes = attributeNames.size();
			String attributeNameToSearch = null;
			String relationNameToSearch = null;
			String singleAttributeName = null;
			boolean mustNavigate = false;

			// The query contains a list of attribute to search
			// Loop on attribute to search
			for (int attributeIndex = 0; attributeIndex < nbAttributes; attributeIndex++) {

				attributeNameToSearch = attributeNames.get(attributeIndex);
				relationNameToSearch = (String) relationAttributeNames.get(attributeIndex);

				// If an attribute name has a ., it is a relation
				mustNavigate = attributeNameToSearch.indexOf(".") != -1;
				long attributeIdentification = -1;
				long attributePosition = -1;
				OID attributeOid = null;

				if (mustNavigate) {
					// Get the relation name and the relation attribute name
					// profile.name => profile = singleAttributeName, name =
					// relationAttributeName
					int firstDotIndex = attributeNameToSearch.indexOf(".");
					String relationAttributeName = OdbString.substring(attributeNameToSearch, firstDotIndex + 1);
					singleAttributeName = OdbString.substring(attributeNameToSearch, 0, firstDotIndex);

					int attributeId = classInfo.getAttributeId(singleAttributeName);
					if (attributeId == -1) {
						throw new ODBRuntimeException(NeoDatisError.CRITERIA_QUERY_UNKNOWN_ATTRIBUTE.addParameter(attributeNameToSearch).addParameter(
								classInfo.getFullClassName()));
					}

					cai = classInfo.getAttributeInfoFromId(attributeId);
					// Gets the identification (id or position from the object
					// info) for the attribute with the id of the class
					// attribute info
					attributeIdentification = objectInfoHeader.getAttributeIdentificationFromId(cai.getId());

					// When object is non native, then attribute identification
					// is the oid of the object. It is stored as negative, so we
					// must do *-1

					if (!cai.isNative()) {
						// Relations can be null
						if (attributeIdentification == StorageEngineConstant.NULL_OBJECT_ID_ID) {
							map.put(relationNameToSearch, null);
							continue;
						}

						attributeOid = OIDFactory.buildObjectOID(-attributeIdentification);
						attributePosition = getObjectPositionFromItsOid(attributeOid, useCache, false);
						IOdbList<String> list1 = new OdbArrayList<String>(1);
						list1.add(relationAttributeName);
						IOdbList<String> list2 = new OdbArrayList<String>(1);
						list2.add(relationNameToSearch);
						map.putAll(readObjectInfoValuesFromPosition(cai.getClassInfo(), attributeOid, attributePosition, useCache, list1, list2,
								recursionLevel + 1, orderByFields,useOidForObject));
					} else {

						throw new ODBRuntimeException(NeoDatisError.CRITERIA_QUERY_UNKNOWN_ATTRIBUTE.addParameter(attributeNameToSearch).addParameter(
								classInfo.getFullClassName()));
					}
				} else {

					int attributeId = classInfo.getAttributeId(attributeNameToSearch);
					if (attributeId == -1) {
						throw new ODBRuntimeException(NeoDatisError.CRITERIA_QUERY_UNKNOWN_ATTRIBUTE.addParameter(attributeNameToSearch).addParameter(
								classInfo.getFullClassName()));
					}

					cai = classInfo.getAttributeInfoFromId(attributeId);

					// Gets the identification (id or position from the object
					// info) for the attribute with the id of the class
					// attribute info
					attributeIdentification = objectInfoHeader.getAttributeIdentificationFromId(cai.getId());

					// When object is non native, then attribute identification
					// is the oid of the object. It is stored as negative, so we
					// must do *-1
					if (cai.isNonNative()) {
						attributeOid = OIDFactory.buildObjectOID(-attributeIdentification);
					}

					// For non native object, the identification is the oid,
					// which is stored as negative long
					// @TODO The attributeIdentification <0 clause should not be
					// necessary
					// But there is a case (found by Jeremias) where even for
					// non
					// native the attribute
					// is a position and not an id! identification
					if (cai.isNonNative() && attributeIdentification < 0) {
						attributePosition = getObjectPositionFromItsOid(attributeOid, useCache, false);
					} else {
						attributePosition = attributeIdentification;
					}

					if (attributePosition == StorageEngineConstant.DELETED_OBJECT_POSITION || attributePosition == StorageEngineConstant.NULL_OBJECT_POSITION
							|| attributePosition == StorageEngineConstant.FIELD_DOES_NOT_EXIST) {
						// TODO is this correct?
						continue;
					}

					fsi.setReadPosition(attributePosition);
					AbstractObjectInfo aoi = null;
					Object object = null;
					if (cai.isNative()) {
						aoi = readNativeObjectInfo(cai.getAttributeType().getId(), attributePosition, useCache, true, true);
						object = aoi.getObject();
						map.put(relationNameToSearch, object);
					} else {
						NonNativeObjectInfo nnoi = readNonNativeObjectInfoFromOid(cai.getClassInfo(), attributeOid, true, false);
						if(useOidForObject){
							map.put(relationNameToSearch, nnoi.getOid());
						}else{
							object = nnoi.getObject();
							if (object == null) {
								object = instanceBuilder.buildOneInstance(nnoi);
							}
							map.put(relationNameToSearch, object);
						}
					}
					
				}
			}
			return map;

		} finally {
			currentDepth--;
		}

	}

	public ObjectInfoHeader getObjectInfoHeader(OID oid, long position, boolean useCache, ICache cache) {
		// first check if the object info pointers exist in the cache
		ObjectInfoHeader objectInfoHeader = null;

		if (useCache && oid != null) {
			objectInfoHeader = cache.getObjectInfoHeaderFromOid(oid, false);
		}

		if (objectInfoHeader == null) {
			// Here we read by position because it is possible to have the
			// oid == null. And it is faster by position than by oid
			objectInfoHeader = readObjectInfoHeaderFromPosition(oid, position, false);
			boolean oidWasNull = oid == null;
			oid = objectInfoHeader.getOid();
			if (useCache) {
				boolean needToUpdateCache = true;
				if (oidWasNull) {
					// The oid was null, now we have it, check the cache again !
					ObjectInfoHeader cachedOih = cache.getObjectInfoHeaderFromOid(oid, false);
					if (cachedOih != null) {
						// Then use the one from the cache
						objectInfoHeader = cachedOih;
						// In this case the cache is up to date , no need to
						// update
						needToUpdateCache = false;
					}
				}
				if (needToUpdateCache) {
					cache.addObjectInfo(objectInfoHeader);
				}
			}
		}
		return objectInfoHeader;
	}

	/**
	 * Read the header of a native attribute
	 * <pre>
	 * 
	 * 
	 * 
	 * The header contains
	 *  - The block size = int
	 *  - The block type = byte
	 *  - The OdbType ID = int
	 *  - A boolean to indicate if object is nulls.
	 *  
	 *  This method reads all the bytes and then convert the byte array to the values
	 * </pre> 
	 * @param odbTypeId
	 * 
	 * @param isNull
	 * @param writeDataInTransaction
	 *            @
	 */
	protected NativeAttributeHeader readNativeAttributeHeader() {

		NativeAttributeHeader nah = new NativeAttributeHeader();
		int size = ODBType.INTEGER.getSize() + ODBType.BYTE.getSize() + ODBType.INTEGER.getSize() + ODBType.BOOLEAN.getSize();
		byte[] bytes = fsi.readBytes(size);
		
		/*
		nah.setBlockSize(fsi.readInt("native block size"));
		nah.setBlockType(fsi.readByte("native block type"));
		nah.setOdbTypeId(fsi.readInt("native odb type"));
		nah.setNull(fsi.readBoolean("object is null"));
		*/
		int blockSize = byteArrayConverter.byteArrayToInt(bytes, 0);
		byte blockType = bytes[4];
		int odbTypeId = byteArrayConverter.byteArrayToInt(bytes, 5);
		boolean isNull = byteArrayConverter.byteArrayToBoolean(bytes, 9);
		nah.setBlockSize(blockSize);
		nah.setBlockType(blockType);
		nah.setOdbTypeId(odbTypeId);
		nah.setNull(isNull);

		return nah;
	}

	/**
	 * Reads a meta representation of a native object
	 * 
	 * @param odbDeclaredTypeId
	 *            The type of attribute declared in the ClassInfo. May be
	 *            different from actual attribute type in caso of OID and
	 *            OdbObjectId
	 * @param position
	 * @param useCache
	 * @param returnObject
	 * @param readHeader
	 * @return The native object representation @
	 */
	private AbstractObjectInfo readNativeObjectInfo(int odbDeclaredTypeId, long position, boolean useCache, boolean returnObject, boolean readHeader) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "Reading native object of type " + ODBType.getNameFromId(odbDeclaredTypeId) + " at position " + position);
		}
		// The realType is initialized with the declared type
		int realTypeId = odbDeclaredTypeId;
		if (readHeader) {
			NativeAttributeHeader nah = readNativeAttributeHeader();
			/*
			 * This check was disabled for OId by osmadja:09/12/2007: The
			 * declared type(in class info) can be an interface or a generic
			 * class and and the real type may be different. It is the case of
			 * OID and OdbObjectid
			 */
			/*
			 * if ( odbDeclaredTypeId!=ODBType.OID_ID && odbDeclaredTypeId !=
			 * nah.getOdbTypeId()) { throw new
			 * CorruptedDatabaseException(Error.NATIVE_TYPE_DIVERGENCE
			 * .addParameter
			 * (odbDeclaredTypeId).addParameter(nah.getOdbTypeId())); }
			 */
			// since version 3 of ODB File Format, the native object header has
			// an info to indicate
			// if object is null!
			if (nah.isNull()) {
				return new NullNativeObjectInfo(odbDeclaredTypeId);
			}
			realTypeId = nah.getOdbTypeId();
		}
		if (ODBType.isAtomicNative(realTypeId)) {
			return readAtomicNativeObjectInfo(position, realTypeId);
		}
		if (ODBType.isNull(realTypeId)) {
			return new NullNativeObjectInfo(realTypeId);
		}

		if (ODBType.isCollection(realTypeId)) {
			return readCollection(position, useCache, returnObject);
		}
		if (ODBType.isArray(realTypeId)) {
			return readArray(position, useCache, returnObject);
		}
		if (ODBType.isMap(realTypeId)) {
			return readMap(position, useCache, returnObject);
		}

		if (ODBType.isEnum(realTypeId)) {
			return readEnumObjectInfo(position, realTypeId);
		}
		throw new ODBRuntimeException(NeoDatisError.NATIVE_TYPE_NOT_SUPPORTED.addParameter(realTypeId));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#
	 * readAtomicNativeObjectInfoAsObject(long, int)
	 */
	public Object readAtomicNativeObjectInfoAsObject(long position, int odbTypeId) {

		Object o = null;

		switch (odbTypeId) {
		case ODBType.BYTE_ID:
		case ODBType.NATIVE_BYTE_ID:
			o = new Byte(fsi.readByte("atomic"));
			break;
		case ODBType.BOOLEAN_ID:
		case ODBType.NATIVE_BOOLEAN_ID:
			boolean b = fsi.readBoolean("atomic");
			if (b) {
				o = Boolean.TRUE;
			} else {
				o = Boolean.FALSE;
			}
			break;
		case ODBType.CHARACTER_ID:
		case ODBType.NATIVE_CHAR_ID:
			o = new Character(fsi.readChar("atomic"));
			break;
		case ODBType.FLOAT_ID:
		case ODBType.NATIVE_FLOAT_ID:
			o = new Float(fsi.readFloat("atomic"));
			break;
		case ODBType.DOUBLE_ID:
		case ODBType.NATIVE_DOUBLE_ID:
			o = new Double(fsi.readDouble("atomic"));
			break;
		case ODBType.INTEGER_ID:
		case ODBType.NATIVE_INT_ID:
			o = new Integer(fsi.readInt("atomic"));
			break;
		case ODBType.LONG_ID:
		case ODBType.NATIVE_LONG_ID:
			o = new Long(fsi.readLong("atomic"));
			break;
		case ODBType.SHORT_ID:
		case ODBType.NATIVE_SHORT_ID:
			o = new Short(fsi.readShort("atomic"));
			break;
		case ODBType.BIG_DECIMAL_ID:
			o = fsi.readBigDecimal("atomic");
			break;
		case ODBType.BIG_INTEGER_ID:
			o = fsi.readBigInteger("atomic");
			break;
		case ODBType.DATE_ID:
			o = fsi.readDate("atomic");
			break;
		case ODBType.DATE_SQL_ID:
			o = new java.sql.Date(fsi.readDate("atomic").getTime());
			break;
		case ODBType.DATE_TIMESTAMP_ID:
			o = new java.sql.Timestamp(fsi.readDate("atomic").getTime());
			break;
		case ODBType.DATE_CALENDAR_ID:
		case ODBType.DATE_GREGORIAN_CALENDAR_ID:
			Calendar c = Calendar.getInstance();
			c.setTime(fsi.readDate("atomic"));
			o = c;
			break;

		case ODBType.OBJECT_OID_ID:
			long oid = fsi.readLong("oid");
			o = OIDFactory.buildObjectOID(oid);
			break;
		case ODBType.CLASS_OID_ID:
			long cid = fsi.readLong("oid");
			o = OIDFactory.buildClassOID(cid);
			break;

		case ODBType.STRING_ID:
			o = fsi.readString(true);
			break;
		case ODBType.ENUM_ID:
			o = fsi.readString(false);
			break;
		}

		if (o == null) {
			throw new ODBRuntimeException(NeoDatisError.NATIVE_TYPE_NOT_SUPPORTED.addParameter(odbTypeId).addParameter(ODBType.getNameFromId(odbTypeId)));
		}
		return o;
	}

	/**
	 * Reads an atomic object
	 */
	public AtomicNativeObjectInfo readAtomicNativeObjectInfo(long position, int odbTypeId) {
		Object object = readAtomicNativeObjectInfoAsObject(position, odbTypeId);

		return new AtomicNativeObjectInfo(object, odbTypeId);
	}

	/**
	 * Reads an enum object
	 */
	public EnumNativeObjectInfo readEnumObjectInfo(long position, int odbTypeId) {
		long enumClassInfoId = fsi.readLong("EnumClassInfoId");
		String enumValue = fsi.readString(true);
		ClassInfo enumCi = getSession().getMetaModel().getClassInfoFromId(OIDFactory.buildClassOID(enumClassInfoId));
		
		return new EnumNativeObjectInfo(enumCi, enumValue);
	}

	/**
	 * Reads a collection from the database file
	 * <p/>
	 * 
	 * <pre>
	 *            This method do not returns the object but a collection of representation of the objects using AsbtractObjectInfo
	 *            &lt;p/&gt;
	 *            The conversion to a real Map object will be done by the buildInstance method
	 * </pre>
	 * 
	 * @param position
	 *            The position to be read
	 * @return The meta representation of a collection @
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private CollectionObjectInfo readCollection(long position, boolean useCache, boolean returnObjects) {
		long[] objectIdentifications;
		AbstractObjectInfo aoi = null;

		String realCollectionClassName = fsi.readString(false, "Real collection class name");
		// read the size of the collection
		int collectionSize = fsi.readInt("Collection size");

		Class clazz = null;

		Collection<AbstractObjectInfo> c = new ArrayList<AbstractObjectInfo>(collectionSize);
		// build a n array to store all element positions
		objectIdentifications = new long[collectionSize];
		for (int i = 0; i < collectionSize; i++) {
			objectIdentifications[i] = fsi.readLong("position of element " + (i + 1));
		}

		for (int i = 0; i < collectionSize; i++) {
			try {
				aoi = readObjectInfo(objectIdentifications[i], useCache, returnObjects);
				if (!(aoi instanceof NonNativeDeletedObjectInfo)) {
					c.add(aoi);
				}
			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("in ObjectReader.readCollection - at position " + position), e);
			}
		}
		CollectionObjectInfo coi = new CollectionObjectInfo(c);
		coi.setRealCollectionClassName(realCollectionClassName);
		return coi;
	}

	/**
	 * Reads an array from the database file
	 * 
	 * @param position
	 *            The position to be read
	 * @return The Collection or the array @
	 */
	private ArrayObjectInfo readArray(long position, boolean useCache, boolean returnObjects) {
		long[] objectIdentifications = null;
		boolean arrayComponentHasFixedSize = true;

		String realArrayComponentClassName = fsi.readString(false, "real array class name");

		ODBType subTypeId = ODBType.getFromName(realArrayComponentClassName);
		boolean componentIsNative = subTypeId.isNative();
		/*
		 * int subTypeId = fsi.readInt(); ODBType subType =
		 * ODBType.getFromId(subTypeId);
		 * 
		 * Class clazz = null; if (subTypeId == ODBType.NON_NATIVE_ID) { long
		 * classInfoPosition = fsi.readLong(); clazz =
		 * ODBClassPool.getClass(storageEngine
		 * .getMetaModel().getClassInfoFromPosition
		 * (classInfoPosition).getFullClassname()); } else { clazz =
		 * subType.getNativeClass(); }
		 */
		// read the size of the array
		int arraySize = fsi.readInt();

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug(depthToSpaces() + "reading an array of " + realArrayComponentClassName + " with " + arraySize + " elements");
		}

		// Class clazz = ODBClassPool.getClass(realArrayClassName);
		// Object array = Array.newInstance(clazz, arraySize);
		Object[] array = new Object[arraySize];

		// build a n array to store all element positions
		objectIdentifications = new long[arraySize];
		for (int i = 0; i < arraySize; i++) {
			objectIdentifications[i] = fsi.readLong();
		}

		for (int i = 0; i < arraySize; i++) {
			try {
				if (objectIdentifications[i] != StorageEngineConstant.NULL_OBJECT_ID_ID) {
					Object o = readObjectInfo(objectIdentifications[i], useCache, returnObjects);
					if (!(o instanceof NonNativeDeletedObjectInfo)) {
						OdbArray.setValue(array, i, o);
					}
				} else {
					if (componentIsNative) {
						OdbArray.setValue(array, i, NullNativeObjectInfo.getInstance());
					} else {
						OdbArray.setValue(array, i, new NonNativeNullObjectInfo());
					}
				}
			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("in ObjectReader.readArray - at position " + position), e);
			}
		}
		ArrayObjectInfo aoi = new ArrayObjectInfo(array);
		aoi.setRealArrayComponentClassName(realArrayComponentClassName);
		aoi.setComponentTypeId(subTypeId.getId());
		return aoi;
	}

	/**
	 * Reads a map from the database file
	 * <p/>
	 * 
	 * <pre>
	 *            WARNING : this method returns a collection representation of the map
	 *            &lt;p/&gt;
	 *            Firts it does not return the objects but its meta information using AbstractObjectInfo
	 *            &lt;p/&gt;
	 *            So for example, the map [1=olivier,2=chico]
	 *            will be returns as a collection : [1,olivier,2,chico]
	 *            and each element of the collection is an abstractObjectInfo (NativeObjectInfo or NonNativeObjectInfo)
	 *            &lt;p/&gt;
	 *            The conversion to a real Map object will be done by the buildInstance method
	 * </pre>
	 * 
	 * @param position
	 *            The position to be read
	 * @param useCache
	 * @param returnObjects
	 * @return The Map @
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private MapObjectInfo readMap(long position, boolean useCache, boolean returnObjects) {
		long[] objectIdentifications;
		AbstractObjectInfo aoiKey = null;
		AbstractObjectInfo aoiValue = null;

		// Reads the real map class
		String realMapClassName = fsi.readString(false);

		// read the size of the map
		int mapSize = fsi.readInt();

		Class clazz = null;

		try {
			// Try the get the real map class
			// By if there is an error(this is probably because of Class Not
			// Found),
			// Then use an default map class
			clazz = classPool.getClass(realMapClassName);
		} catch (ODBRuntimeException e) {
			clazz = HashMap.class;
		}
		clazz = HashMap.class;
		Map map = null;
		try {
			map = (Map) clazz.newInstance();
		} catch (Exception e1) {
			throw new ODBRuntimeException(NeoDatisError.MAP_INSTANCIATION_ERROR.addParameter(realMapClassName));
		}

		// build a n array to store all element positions
		objectIdentifications = new long[mapSize * 2];

		for (int i = 0; i < mapSize * 2; i++) {
			objectIdentifications[i] = fsi.readLong();
		}

		for (int i = 0; i < mapSize; i++) {
			try {
				aoiKey = readObjectInfo(objectIdentifications[2 * i], useCache, returnObjects);
				aoiValue = readObjectInfo(objectIdentifications[2 * i + 1], useCache, returnObjects);
				if (!aoiKey.isDeletedObject() && !aoiValue.isDeletedObject()) {
					map.put(aoiKey, aoiValue);
				}
			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("in ObjectReader.readMap - at position " + position), e);
			}
		}
		return new MapObjectInfo(map, realMapClassName);
	}

	/**
	 * Gets the next object oid of the object with the specific oid
	 * 
	 * @param position
	 * @return The position of the next object. If there is no next object,
	 *         return -1 @
	 */
	public OID getNextObjectOID(OID oid) {
		long position = storageEngine.getObjectWriter().getIdManager().getObjectPositionWithOid(oid, true);
		fsi.setReadPosition(position + StorageEngineConstant.OBJECT_OFFSET_NEXT_OBJECT_OID);
		return OIDFactory.buildObjectOID(fsi.readLong());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#readOidPosition
	 * (org.neodatis.odb.core.OID)
	 */
	public long readOidPosition(OID oid) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("  Start of readOidPosition for oid " + oid);
		}
		long blockNumber = getIdBlockNumberOfOid(oid);
		long blockPosition = -1;

		blockPosition = getIdBlockPositionFromNumber(blockNumber);

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("  Block number of oid " + oid + " is " + blockNumber + " / block position = " + blockPosition);
		}

		long position = blockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_START_OF_REPETITION
				+ ((oid.getObjectId() - 1) % OdbConfiguration.getNB_IDS_PER_BLOCK()) * OdbConfiguration.getID_BLOCK_REPETITION_SIZE();

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("  End of readOidPosition for oid " + oid + " returning position " + position);
		}

		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#
	 * getObjectFromOid(org.neodatis.odb.core.OID, boolean)
	 */
	public Object getObjectFromOid(OID oid, boolean returnInstance, boolean useCache) {

		long position = getObjectPositionFromItsOid(oid, useCache, true);
		Object o = readNonNativeObjectAtPosition(position, useCache, returnInstance);
		// Clear the tmp cache. This cache is use to resolve cyclic references
		getSession().getTmpCache().clearObjectInfos();
		return o;
	}

	/**
	 * Returns the name of the class of an object from its position
	 * 
	 * @param objectPosition
	 * @return The object class name @
	 */
	public String getObjectTypeFromPosition(long objectPosition) {
		long blockPosition = objectPosition + StorageEngineConstant.OBJECT_OFFSET_BLOCK_TYPE;
		fsi.setReadPosition(blockPosition);
		byte blockType = fsi.readByte();

		if (BlockTypes.isNull(blockType)) {
			OID classIdForNullObject = OIDFactory.buildClassOID(fsi.readLong("class id of object"));
			return "null " + storageEngine.getSession(true).getMetaModel().getClassInfoFromId(classIdForNullObject).getFullClassName();
		}

		long classIdPosition = objectPosition + StorageEngineConstant.OBJECT_OFFSET_CLASS_INFO_ID;
		fsi.setReadPosition(classIdPosition);

		OID classId = OIDFactory.buildClassOID(fsi.readLong("class id of object"));
		return storageEngine.getSession(true).getMetaModel().getClassInfoFromId(classId).getFullClassName();
	}

	/**
	 * Gets the real object position from its OID
	 * 
	 * @param oid
	 *            The oid of the object to get the position
	 * @param throwException
	 *            To indicate if an exception must be thrown if object is not
	 *            found
	 * @return The object position, if object has been marked as deleted then
	 *         return StorageEngineConstant.DELETED_OBJECT_POSITION @
	 */
	public long getObjectPositionFromItsOid(OID oid, boolean useCache, boolean throwException) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("  getObjectPositionFromItsId for oid " + oid);
		}
		// Check if oid is in cache
		long position = StorageEngineConstant.OBJECT_IS_NOT_IN_CACHE;

		if (useCache) {
			// This return -1 if not in the cache
			position = storageEngine.getSession(true).getCache().getObjectPositionByOid(oid);
		}
		
		// FIXME Check if we need this. Removing it causes the TestDelete.test6 to fail 
		if (position == StorageEngineConstant.DELETED_OBJECT_POSITION) {
			if (throwException) {
				throw new CorruptedDatabaseException(NeoDatisError.OBJECT_IS_MARKED_AS_DELETED_FOR_OID.addParameter(oid));
			}
			return StorageEngineConstant.DELETED_OBJECT_POSITION;
		}
		

		if (position != StorageEngineConstant.OBJECT_IS_NOT_IN_CACHE && position != StorageEngineConstant.DELETED_OBJECT_POSITION) {
			return position;
		}
		// The position was not found is the cache
		position = readOidPosition(oid);
		position += StorageEngineConstant.BLOCK_ID_REPETITION_ID_STATUS;
		fsi.setReadPosition(position);

		byte idStatus = fsi.readByte();
		long objectPosition = fsi.readLong();
		if (!IDStatus.isActive(idStatus)) {
			// if object position == 0, The object dos not exist
			if (throwException) {
				if (objectPosition == 0) {
					throw new CorruptedDatabaseException(NeoDatisError.OBJECT_WITH_OID_DOES_NOT_EXIST.addParameter(oid));
				}
				throw new CorruptedDatabaseException(NeoDatisError.OBJECT_IS_MARKED_AS_DELETED_FOR_OID.addParameter(oid));
			}
			if (objectPosition == 0) {
				return StorageEngineConstant.OBJECT_DOES_NOT_EXIST;
			}
			return StorageEngineConstant.DELETED_OBJECT_POSITION;
		}

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("  object position of object with oid " + oid + " is " + objectPosition);
		}

		return objectPosition;
	}

	/**
	 * @param blockNumberToFind
	 * @return The block position @
	 */
	private long getIdBlockPositionFromNumber(long blockNumberToFind) {
		//TODO remove new Long
		// first check if it exist in cache
		Long lposition = blockPositions.get(blockNumberToFind);
		
		if (lposition != null) {
			return lposition.longValue();
		}
		long nextBlockPosition = 0;
		long currentBlockPosition = StorageEngineConstant.DATABASE_HEADER_FIRST_ID_BLOCK_POSITION;
		int blockNumber = -1;

		while (currentBlockPosition != -1) {
			// Gets the next block position
			fsi.setReadPosition(currentBlockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_NEXT_BLOCK);
			nextBlockPosition = fsi.readLong();
			// Reads the block number
			blockNumber = fsi.readInt();

			if (blockNumber == blockNumberToFind) {
				// Put result in map
				blockPositions.put(blockNumberToFind, currentBlockPosition);
				return currentBlockPosition;
			}
			currentBlockPosition = nextBlockPosition;
		}
		throw new CorruptedDatabaseException(NeoDatisError.BLOCK_NUMBER_DOES_EXIST.addParameter(blockNumberToFind));
	}

	private long getIdBlockNumberOfOid(OID oid) {
		long number = -1;
		long objectId = oid.getObjectId();
		if (objectId % OdbConfiguration.getNB_IDS_PER_BLOCK() == 0) {
			number = objectId / OdbConfiguration.getNB_IDS_PER_BLOCK();
		} else {
			number = objectId / OdbConfiguration.getNB_IDS_PER_BLOCK() + 1;
		}
		return number;
	}

	/**
	 * Returns information about all OIDs of the database
	 * 
	 * @param idType
	 * @return @
	 */
	public List<Long> getAllIds(byte idType) {
		long blockMaxId = 0;
		long currentId = 0;
		byte idTypeRead = 0;
		byte idStatus = 0;
		long nextRepetitionPosition = 0;
		List<Long> ids = new ArrayList<Long>(5000);
		long nextBlockPosition = 0;
		long currentBlockPosition = StorageEngineConstant.DATABASE_HEADER_FIRST_ID_BLOCK_POSITION;

		while (currentBlockPosition != -1) {
			// Gets the next block position
			fsi.setReadPosition(currentBlockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_NEXT_BLOCK);
			nextBlockPosition = fsi.readLong();

			// Gets the block max id
			fsi.setReadPosition(currentBlockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_MAX_ID);
			blockMaxId = fsi.readLong();

			do {
				nextRepetitionPosition = fsi.getPosition() + OdbConfiguration.getID_BLOCK_REPETITION_SIZE();
				idTypeRead = fsi.readByte();
				currentId = fsi.readLong();
				idStatus = fsi.readByte();
				if (idType == idTypeRead && IDStatus.isActive(idStatus)) {
					ids.add(new Long(currentId));
				}
				fsi.setReadPosition(nextRepetitionPosition);
			} while (currentId != blockMaxId);
			currentBlockPosition = nextBlockPosition;
		}
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#getAllIdInfos
	 * (java.lang.String, byte, boolean)
	 */
	public List<FullIDInfo> getAllIdInfos(String objectTypeToDisplay, byte idType, boolean displayObject) {
		long blockId = 0;
		long blockMaxId = 0;
		long currentId = 0;
		byte idTypeRead = 0;
		byte idStatus = 0;
		long objectPosition = 0;
		long nextRepetitionPosition = 0;
		String objectType = null;
		List<FullIDInfo> idInfos = new ArrayList<FullIDInfo>(5000);
		long nextBlockPosition = 0;
		OID prevObjectOID = null;
		OID nextObjectOID = null;
		long currentBlockPosition = StorageEngineConstant.DATABASE_HEADER_FIRST_ID_BLOCK_POSITION;
		FullIDInfo info = null;
		String objectToString = "empty";
		while (currentBlockPosition != -1) {
			DLogger.debug("Current block position = " + currentBlockPosition);
			fsi.setReadPosition(currentBlockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_BLOCK_NUMBER);

			fsi.setReadPosition(currentBlockPosition + StorageEngineConstant.BLOCK_ID_OFFSET_FOR_NEXT_BLOCK);
			nextBlockPosition = fsi.readLong();
			// Gets block number
			blockId = fsi.readInt();
			blockMaxId = fsi.readLong();

			do {
				nextRepetitionPosition = fsi.getPosition() + OdbConfiguration.getID_BLOCK_REPETITION_SIZE();
				idTypeRead = fsi.readByte();
				currentId = fsi.readLong();
				idStatus = fsi.readByte();
				objectPosition = fsi.readLong();
				if (idType == idTypeRead) { // && IDStatus.isActive(idStatus)) {
					long currentPosition = fsi.getPosition();
					if (displayObject) {
						AbstractObjectInfo aoi = null;
						try {
							aoi = readNonNativeObjectInfoFromPosition(null, null, objectPosition, false, false);
							if (!(aoi instanceof NonNativeDeletedObjectInfo)) {
								objectToString = aoi.toString();
								NonNativeObjectInfo nnoi = (NonNativeObjectInfo) aoi;
								prevObjectOID = nnoi.getPreviousObjectOID();
								nextObjectOID = nnoi.getNextObjectOID();
							} else {
								objectToString = " deleted";
								prevObjectOID = null;
								nextObjectOID = null;
							}
						} catch (Exception e) {
							// info = new IDInfo(currentId, objectPosition,
							// idStatus, blockId, "unknow", "Error", -1, -1);
							// idInfos.add(info);
							objectToString = "?";
							prevObjectOID = null;
							nextObjectOID = null;
						}

					}

					try {
						objectType = getObjectTypeFromPosition(objectPosition);
					} catch (Exception e) {
						objectType = "(error?)";
					}
					if (objectTypeToDisplay == null || objectTypeToDisplay.equals(objectType)) {
						fsi.setReadPosition(currentPosition);
						info = new FullIDInfo(currentId, objectPosition, idStatus, blockId, objectType, objectToString, prevObjectOID, nextObjectOID);
						idInfos.add(info);
					}
				} else {
					try {
						ClassInfo ci = readClassInfoHeader(OIDFactory.buildClassOID(currentId));
						objectType = "Class def. of " + ci.getFullClassName();
						objectToString = ci.toString();
						prevObjectOID = ci.getPreviousClassOID();
						nextObjectOID = ci.getNextClassOID();
						info = new FullIDInfo(currentId, objectPosition, idStatus, blockId, objectType, objectToString, prevObjectOID, nextObjectOID);
						idInfos.add(info);
					} catch (Exception e) {
						info = new FullIDInfo(currentId, objectPosition, idStatus, blockId, "unknow", "Error", null, null);
						idInfos.add(info);
					}
				}

				fsi.setReadPosition(nextRepetitionPosition);
			} while (currentId != blockMaxId);
			currentBlockPosition = nextBlockPosition;
		}
		return idInfos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#getIdOfObjectAt
	 * (long, boolean)
	 */
	public OID getIdOfObjectAt(long position, boolean includeDeleted) {
		fsi.setReadPosition(position + ODBType.INTEGER.getSize());
		byte blockType = fsi.readByte("object block type");

		if (BlockTypes.isPointer(blockType)) {
			return getIdOfObjectAt(fsi.readLong("new position"), includeDeleted);
		}

		if (BlockTypes.isNonNative(blockType)) {
			return OIDFactory.buildObjectOID(fsi.readLong("oid"));
		}

		if (includeDeleted && BlockTypes.isDeletedObject(blockType)) {
			return OIDFactory.buildObjectOID(fsi.readLong("oid"));
		}

		throw new CorruptedDatabaseException(NeoDatisError.WRONG_TYPE_FOR_BLOCK_TYPE.addParameter(BlockTypes.BLOCK_TYPE_NON_NATIVE_OBJECT).addParameter(blockType)
				.addParameter(position));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#close()
	 */
	public void close() {
		storageEngine = null;
		blockPositions.clear();
		blockPositions = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#
	 * buildOneInstance
	 * (org.neodatis.odb.core.impl.layers.layer2.meta.AbstractObjectInfo)
	 */
	public Object buildOneInstance(NonNativeObjectInfo objectInfo) {
		return instanceBuilder.buildOneInstance(objectInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.io.IStorageEngine#getObjects(java.lang.Class,
	 * org.neodatis.odb.core.query.IQuery, boolean, int, int)
	 */
	public <T> Objects<T> getObjects(Class clazz, boolean inMemory, int startIndex, int endIndex) {
		return getObjects(new CriteriaQuery(clazz), inMemory, startIndex, endIndex);
	}

	public <T> Objects<T> getObjects(String fullClassName, boolean inMemory, int startIndex, int endIndex) {
		return getObjects(new CriteriaQuery(fullClassName), inMemory, startIndex, endIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#getObjects
	 * (org.neodatis.odb.core.query.IQuery, boolean, int, int)
	 */
	public <T> Objects<T> getObjects(IQuery query, boolean inMemory, int startIndex, int endIndex) {

		IMatchingObjectAction queryResultAction = new CollectionQueryResultAction(query, inMemory, storageEngine, true, instanceBuilder);

		/*
		 * // Some type of query can be resolved without instantiating all
		 * objects, // check first // TODO builds a facade to put this IF! if
		 * (query != null && !QueryManager.needsInstanciation(query)) { return
		 * getObjectInfos(query, inMemory, startIndex, endIndex, true,
		 * queryResultAction); }
		 */
		return QueryManager.getQueryExecutor(query, storageEngine, instanceBuilder).execute(inMemory, startIndex, endIndex, false, queryResultAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#getValues
	 * (org.neodatis.odb.core.query.IValuesQuery, int, int)
	 */
	public Values getValues(IValuesQuery valuesQuery, int startIndex, int endIndex) {

		IMatchingObjectAction queryResultAction = null;

		if (valuesQuery.hasGroupBy()) {
			queryResultAction = new GroupByValuesQueryResultAction(valuesQuery, storageEngine, instanceBuilder);
		} else {
			queryResultAction = new ValuesQueryResultAction(valuesQuery, storageEngine, instanceBuilder);
		}
		Objects<ObjectValues> objects = getObjectInfos(valuesQuery, true, startIndex, endIndex, false, queryResultAction);
		return (Values) objects;
	}

	public ISession getSession() {
		return storageEngine.getSession(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#getObjectInfos
	 * (org.neodatis.odb.core.query.IQuery, boolean, int, int, boolean,
	 * org.neodatis.odb.core.query.execution.IMatchingObjectAction)
	 */
	public <T> Objects<T> getObjectInfos(IQuery query, boolean inMemory, int startIndex, int endIndex, boolean returnObjects,
			IMatchingObjectAction queryResultAction) {

		IQueryExecutor executor = QueryManager.getQueryExecutor(query, storageEngine, instanceBuilder);
		return executor.execute(inMemory, startIndex, endIndex, returnObjects, queryResultAction);
	}

	public <T> Objects<T> getObjectInfos(String fullClassName, boolean inMemory, int startIndex, int endIndex, boolean returnOjects) {
		IQuery query = new CriteriaQuery(fullClassName);
		IMatchingObjectAction queryResultAction = new CollectionQueryResultAction(query, inMemory, storageEngine, returnOjects, instanceBuilder);
		return getObjectInfos(query, inMemory, startIndex, endIndex, returnOjects, queryResultAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#
	 * getBaseIdentification()
	 */
	public String getBaseIdentification() {
		return storageEngine.getBaseIdentification().getIdentification();
	}

	/**
	 * This is an utility method to get the linked list of All Object Info
	 * Header. For debug purpose
	 * 
	 * @param classInfo
	 *            @
	 */
	public IOdbList<ObjectInfoHeader> getObjectInfoHeaderList(ClassInfo classInfo) {
		if (classInfo.getNumberOfObjects() == 0) {
			return new OdbArrayList<ObjectInfoHeader>();
		}
		IOdbList<ObjectInfoHeader> list = new OdbArrayList<ObjectInfoHeader>((int) classInfo.getNumberOfObjects());
		ObjectInfoHeader oih = null;
		OID oid = classInfo.getCommitedZoneInfo().first;
		if (oid == null) {
			oid = classInfo.getUncommittedZoneInfo().first;
		}

		while (oid != null) {
			oih = readObjectInfoHeaderFromOid(oid, true);
			list.add(oih);
			oid = oih.getNextObjectOID();
		}
		return list;
	}

	public String getClassInfoFullObjectChaining(ClassInfo ci){
		IOdbList<ObjectInfoHeader> oihs = getObjectInfoHeaderList(ci);
		
		StringBuilder builder = new StringBuilder();
		
		for(ObjectInfoHeader oih:oihs){
			builder.append(oih.oidsToString());
		}
		if(oihs.isEmpty()){
			builder.append(" No Object!");
		}
		return builder.toString();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.impl.layers.layer3.engine.IObjectReader#
	 * getInstanceBuilder()
	 */
	public IInstanceBuilder getInstanceBuilder() {
		return instanceBuilder;
	}

}
