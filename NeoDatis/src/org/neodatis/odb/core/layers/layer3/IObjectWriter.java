package org.neodatis.odb.core.layers.layer3;

import org.neodatis.odb.OID;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.core.ITwoPhaseInit;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;

public interface IObjectWriter extends ITwoPhaseInit {

	/*
	 * Adds a list of class to the metamodel, if it already exists simply returns the original one 
	 */
	public abstract ClassInfoList addClasses(ClassInfoList classInfoList);

	/**
	 * Write the class info header to the database file
	 * 
	 * @param classInfo
	 *            The class info to be written
	 * @param position
	 *            The position at which it must be written
	 * @param writeInTransaction
	 *            true if the write must be done in transaction, false to write
	 *            directly
	 * 
	 */
	public abstract void writeClassInfoHeader(ClassInfo classInfo,
			long position, boolean writeInTransaction) ;

	public abstract void updateClassInfo(ClassInfo classInfo,
			boolean writeInTransaction);

	/**
	 * Updates an object.
	 * 
	 * <pre>
	 * Try to update in place. Only change what has changed. This is restricted to particular types (fixed size types). If in place update is 
	 * not possible, then deletes the current object and creates a new at the end of the database file and updates
	 * OID object position.
	 * 
	 * @param nnoi The meta representation of the object to be updated
	 * @param forceUpdate when true, no verification is done to check if update must be done.
	 * @return The oid of the object, as a negative number
	 * 
	 * 
	 */
	public OID updateNonNativeObjectInfo(NonNativeObjectInfo nnoi, boolean forceUpdate);
	
	/**
	 * Write an object representation to database file
	 * 
	 * @param existingOid
	 *            The oid of the object, can be null
	 * @param objectInfo
	 *            The Object meta representation
	 * @param position
	 *            The position where the object must be written, can be -1
	 * @param writeDataInTransaction
	 *            To indicate if the write must be done in or out of transaction
	 * @return The oid of the object
	 * @throws Exception
	 */
	public abstract OID writeNonNativeObjectInfo(OID existingOid,
			NonNativeObjectInfo objectInfo, long position,
			boolean writeDataInTransaction, boolean isNewObject);

	public abstract long writeAtomicNativeObject(AtomicNativeObjectInfo anoi,
			boolean writeInTransaction, int totalSpaceIfString);

	public abstract IIdManager getIdManager();

	public abstract ISession getSession();

	public abstract void close();

	public abstract IFileSystemInterface getFsi();

	/**
	 * Creates the header of the file
	 * 
	 * @param creationDate
	 *            The creation date
	 * @param user
	 *            The user
	 * @param password
	 *            The password
	 * 
	 * 
	 */
	public void createEmptyDatabaseHeader(long creationDate, String user, String password);

	/**
	 * Mark a block as deleted
	 * 
	 * @return The block size
	 * 
	 * @param currentPosition
	 * 
	 */
	public int markAsDeleted(long currentPosition, OID oid, boolean writeInTransaction);

	/**
	 * Insert the object in the index
	 * 
	 * @param oid
	 *            The object id
	 * @param nnoi
	 *            The object meta represenation
	 * @return The number of indexes
	 */
	public int manageIndexesForInsert(OID oid, NonNativeObjectInfo nnoi);

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
	public int manageIndexesForDelete(OID oid, NonNativeObjectInfo nnoi);

	public int manageIndexesForUpdate(OID oid, NonNativeObjectInfo nnoi, NonNativeObjectInfo oldMetaRepresentation);

	/** Write the status of the last odb close */
	public void writeLastODBCloseStatus(boolean ok, boolean writeInTransaction);

	public void flush();

	public OID delete(ObjectInfoHeader header);

	public void updateStatusForIdWithPosition(long idPosition, byte newStatus, boolean writeInTransaction);

	/**
	 * Updates the real object position of the object OID
	 * @param idPosition The OID position
	 * @param objectPosition The real object position
	 * @param writeInTransaction To indicate if write must be done in transaction
	 * 
	 */
	public void updateObjectPositionForObjectOIDWithPosition(long idPosition, long objectPosition, boolean writeInTransaction);

	/**Udates the real class positon of the class OID
	 * 
	 * @param idPosition
	 * @param objectPosition
	 * @param writeInTransaction
	 * 
	 */
	public void updateClassPositionForClassOIDWithPosition(long idPosition, long objectPosition, boolean writeInTransaction);

	/**
	 * Associate an object OID to its position
	 * @param idType The type : can be object or class
	 * @param idStatus The status of the OID
	 * @param currentBlockIdPosition The current OID block position
	 * @param oid The OID
	 * @param objectPosition The position
	 * @param writeInTransaction To indicate if write must be executed in transaction
	 * @return
	 * 
	 */
	public long associateIdToObject(byte idType, byte idStatus, long currentBlockIdPosition, OID oid, long objectPosition, boolean writeInTransaction);

	/**
	 * Marks a block of type id as full, changes the status and the next block
	 * position
	 * 
	 * @param blockPosition
	 * @param nextBlockPosition
	 * @param writeInTransaction
	 * @return The block position
	 * 
	 */
	public long markIdBlockAsFull(long blockPosition, long nextBlockPosition, boolean writeInTransaction);

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
	 * @return The position of the id
	 * 
	 */
	public long writeIdBlock(long position, int idBlockSize, byte blockStatus, int blockNumber, long previousBlockPosition, boolean writeInTransaction) ;

	/**
	 * Updates the previous object position field of the object at
	 * objectPosition
	 * 
	 * @param objectOID
	 * @param previousObjectOID
	 * @param writeInTransaction
	 * 
	 */
	public void updatePreviousObjectFieldOfObjectInfo(OID objectOID, OID previousObjectOID, boolean writeInTransaction);

	/**
	 * Update next object oid field of the object at the specific position
	 * 
	 * @param objectOID
	 * @param nextObjectOID
	 * @param writeInTransaction
	 * 
	 */
	public void updateNextObjectFieldOfObjectInfo(OID objectOID, OID nextObjectOID, boolean writeInTransaction);

	/**
	 * Updates the instance related field of the class info into the database
	 * file Updates the number of objects, the first object oid and the next
	 * class oid
	 * 
	 * @param classInfo
	 *            The class info to be updated
	 * @param writeInTransaction
	 *            To specify if it must be part of a transaction
	 * 
	 */
	public void updateInstanceFieldsOfClassInfo(ClassInfo classInfo, boolean writeInTransaction);

	public void afterInit();

	public ClassInfo addClass(ClassInfo newClassInfo, boolean addDependentClasses);

	/** Persist a single class info - This method is used by the XML Importer.
	 */
	public ClassInfo persistClass(ClassInfo newClassInfo, int lastClassInfoIndex, boolean addClass, boolean addDependentClasses);
	
	public void writeLastTransactionId(TransactionId transactionId);
	public void setTriggerManager(ITriggerManager triggerManager);
	
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

	public OID insertNonNativeObject(OID oid, NonNativeObjectInfo nnoi, boolean isNewObject);

}