package org.neodatis.odb.core.mock;

import org.neodatis.odb.OID;
import org.neodatis.odb.TransactionId;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;
import org.neodatis.odb.core.layers.layer3.IIdManager;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.ITriggerManager;

public class MockObjectWriter implements IObjectWriter {
	IStorageEngine engine;

	public MockObjectWriter(IStorageEngine engine) {
		this.engine = engine;
	}

	public ISession getSession() {
		return engine.getSession(true);
	}

	public ClassInfo addClass(ClassInfo newClassInfo, boolean addDependentClasses) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassInfoList addClasses(ClassInfoList classInfoList) {
		// TODO Auto-generated method stub
		return null;
	}

	public void afterInit() {
		// TODO Auto-generated method stub

	}

	public long associateIdToObject(byte idType, byte idStatus, long currentBlockIdPosition, OID oid, long objectPosition,
			boolean writeInTransaction) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public void createEmptyDatabaseHeader(long creationDate, String user, String password) {
		// TODO Auto-generated method stub

	}

	public OID delete(ObjectInfoHeader header) {
		// TODO Auto-generated method stub
		return null;
	}

	public void flush() {
		// TODO Auto-generated method stub

	}

	public IFileSystemInterface getFsi() {
		// TODO Auto-generated method stub
		return null;
	}

	public IIdManager getIdManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public int manageIndexesForDelete(OID oid, NonNativeObjectInfo nnoi) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int manageIndexesForInsert(OID oid, NonNativeObjectInfo nnoi) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int manageIndexesForUpdate(OID oid, NonNativeObjectInfo nnoi, NonNativeObjectInfo oldMetaRepresentation) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int markAsDeleted(long currentPosition, OID oid, boolean writeInTransaction) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long markIdBlockAsFull(long blockPosition, long nextBlockPosition, boolean writeInTransaction) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ClassInfo persistClass(ClassInfo newClassInfo, int lastClassInfoIndex, boolean addClass, boolean addDependentClasses) {
		// TODO Auto-generated method stub
		return null;
	}

	public OID storeObject(OID oid, NonNativeObjectInfo nnoi) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateClassInfo(ClassInfo classInfo, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public void updateClassPositionForClassOIDWithPosition(long idPosition, long objectPosition, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public void updateInstanceFieldsOfClassInfo(ClassInfo classInfo, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public void updateNextObjectFieldOfObjectInfo(OID objectOID, OID nextObjectOID, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public OID updateNonNativeObjectInfo(NonNativeObjectInfo nnoi, boolean forceUpdate) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateObjectPositionForObjectOIDWithPosition(long idPosition, long objectPosition, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public void updatePreviousObjectFieldOfObjectInfo(OID objectOID, OID previousObjectOID, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public void updateStatusForIdWithPosition(long idPosition, byte newStatus, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public long writeAtomicNativeObject(AtomicNativeObjectInfo anoi, boolean writeInTransaction, int totalSpaceIfString) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeClassInfoHeader(ClassInfo classInfo, long position, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public long writeIdBlock(long position, int idBlockSize, byte blockStatus, int blockNumber, long previousBlockPosition,
			boolean writeInTransaction) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeLastODBCloseStatus(boolean ok, boolean writeInTransaction) {
		// TODO Auto-generated method stub

	}

	public OID writeNonNativeObjectInfo(OID existingOid, NonNativeObjectInfo objectInfo, long position, boolean writeDataInTransaction,
			boolean isNewObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public void init2() {
		// TODO Auto-generated method stub

	}

	public void writeLastTransactionId(TransactionId transactionId) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IObjectWriter#setTriggerManager(org
	 * .neodatis.odb.core.trigger.ITriggerManager)
	 */
	public void setTriggerManager(ITriggerManager triggerManager) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.IObjectWriter#insertNonNativeObject
	 * (org.neodatis.odb.OID,
	 * org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo, boolean)
	 */
	public OID insertNonNativeObject(OID oid, NonNativeObjectInfo nnoi, boolean isNewObject) {
		// TODO Auto-generated method stub
		return null;
	}

}
