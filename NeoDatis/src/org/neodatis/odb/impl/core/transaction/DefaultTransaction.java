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
package org.neodatis.odb.impl.core.transaction;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.CIZoneInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.ICommitListener;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITransaction;
import org.neodatis.odb.core.transaction.IWriteAction;
import org.neodatis.odb.impl.core.layers.layer3.engine.LocalFileSystemInterface;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.io.OdbFile;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * <pre>
 * The transaction class is used to guarantee ACID behavior. It keep tracks of all session
 * operations. It uses the WriteAction class to store all changes that can not be written to the file
 * before the commit.
 * The transaction is held by The Session class and manage commits and rollbacks.
 * 
 * All WriteActions are written in a transaction file to be sure to be able to commit and in case 
 * of very big transaction where all WriteActions can not be stored in memory.
 * </pre>
 * 
 * @author osmadja
 * 
 */
public class DefaultTransaction implements ITransaction {
	/** the log module name */
	public static final String LOG_ID = "Transaction";

	/** To indicate if transaction was confirmed = committed */
	private boolean isCommited;

	/** The transaction creation time */
	private long creationDateTime;

	/**
	 * All the pending writing that must be applied to actually commit the
	 * transaction
	 */
	private IOdbList<IWriteAction> writeActions;

	/** The same write action is reused for successive writes */
	public IWriteAction currentWriteAction;

	/** The position of the next write for WriteAction */
	public long currentWritePositionInWA;

	/**
	 * To indicate if all write actions are in memory - if not, transaction must
	 * read them from transaction file o commit the transaction
	 */
	private boolean hasAllWriteActionsInMemory;

	/** The number of write actions */
	public int numberOfWriteActions;

	/** A file interface to the transaction file - used to read/write the file */
	public IFileSystemInterface fsi;

	/** A file interface to the engine main file */
	private IFileSystemInterface fsiToApplyWriteActions;

	/** To indicate if transaction has already been persisted in file */
	private boolean hasBeenPersisted;

	/**
	 * When this flag is set,the transaction will not be deleted, but will be
	 * flagged as executed
	 */
	private boolean archiveLog;

	/** To indicate if transaction was rollbacked */
	private boolean wasRollbacked;

	/**
	 * A name to set the transaction file name. Used when reading transaction
	 * file
	 */
	private String overrideTransactionName;

	/** To indicate if transaction is read only */
	private boolean readOnlyMode;

	/** The transaction session */
	public ISession session;

	/** To indicate is transaction is used for local or remote engine */
	private boolean isLocal;

	private ICoreProvider provider;

	/**
	 * The main constructor
	 * 
	 * @param session
	 *            The transaction session
	 * @throws IOException
	 */
	public DefaultTransaction(ISession session) throws IOException {
		init(session);
	}

	public DefaultTransaction(ISession session, String overrideTransactionName) throws IOException {
		this.overrideTransactionName = overrideTransactionName;
		init(session);
		this.readOnlyMode = true;
	}

	public DefaultTransaction(ISession session, IFileSystemInterface fsiToApplyTransaction) {
		this.fsiToApplyWriteActions = fsiToApplyTransaction;
		init(session);
		readOnlyMode = false;
	}

	public void init(ISession session) {
		this.provider = OdbConfiguration.getCoreProvider();
		this.session = session;
		this.isCommited = false;
		creationDateTime = OdbTime.getCurrentTimeInMs();
		writeActions = new OdbArrayList<IWriteAction>(1000);
		hasAllWriteActionsInMemory = true;
		numberOfWriteActions = 0;
		hasBeenPersisted = false;
		wasRollbacked = false;
		currentWritePositionInWA = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#clear()
	 */
	public void clear() {
		if (writeActions != null) {
			writeActions.clear();
			writeActions = null;
		}
	}

	/**
	 * Reset the transaction
	 * 
	 * 
	 */
	public void reset() {
		clear();
		init(session);
		fsi = null;
	}

	/**
	 * Adds a write action to the transaction
	 * 
	 * @param writeAction
	 *            The write action to be added
	 */
	public void addWriteAction(IWriteAction writeAction) {
		addWriteAction(writeAction, true);
	}

	/**
	 * Adds a write action to the transaction
	 * 
	 * @param writeAction
	 *            The write action to be added
	 * @param persistWriteAcion
	 *            To indicate if write action must be persisted
	 */
	public void addWriteAction(IWriteAction writeAction, boolean persistWriteAcion) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.info("Adding WA in Transaction of session " + session.getId());
		}

		if (writeAction.isEmpty()) {
			return;
		}
		checkRollback();

		if (!hasBeenPersisted && persistWriteAcion) {
			persist();
		}
		if (persistWriteAcion) {
			writeAction.persist(fsi, numberOfWriteActions + 1);
		}
		// Only adds the writeaction to the list if the transaction keeps all in
		// memory
		if (hasAllWriteActionsInMemory) {
			writeActions.add(writeAction);
		}
		numberOfWriteActions++;

		if (hasAllWriteActionsInMemory && numberOfWriteActions > OdbConfiguration.getMaxNumberOfWriteObjectPerTransaction()) {
			hasAllWriteActionsInMemory = false;
			Iterator iterator = writeActions.iterator();
			DefaultWriteAction wa = null;
			while (iterator.hasNext()) {
				wa = (DefaultWriteAction) iterator.next();
				wa.clear();
			}
			writeActions.clear();
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.info("Number of objects has exceeded the max number " + numberOfWriteActions + "/"
						+ OdbConfiguration.getMaxNumberOfWriteObjectPerTransaction() + ": switching to persistent transaction managment");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#getName()
	 */
	public String getName() {
		IBaseIdentification p = fsiToApplyWriteActions.getParameters();
		if (p instanceof IOFileParameter) {
			IOFileParameter ifp = (IOFileParameter) fsiToApplyWriteActions.getParameters();
			StringBuffer buffer = new StringBuffer(ifp.getCleanFileName()).append("-").append(creationDateTime).append("-").append(
					session.getId()).append(".transaction");
			return buffer.toString();
		}
		if (p instanceof IOSocketParameter) {
			IOSocketParameter sp = (IOSocketParameter) fsiToApplyWriteActions.getParameters();
			return sp.getBaseIdentifier();
		}
		throw new ODBRuntimeException(NeoDatisError.UNSUPPORTED_IO_TYPE.addParameter(p.getClass().getName()));
	}

	IBaseIdentification getParameters(boolean canWrite) {
		IBaseIdentification p = fsiToApplyWriteActions.getParameters();
		if (p instanceof IOFileParameter) {
			IOFileParameter ifp = (IOFileParameter) fsiToApplyWriteActions.getParameters();
			StringBuffer buffer = new StringBuffer(ifp.getDirectory()).append("/").append(ifp.getCleanFileName()).append("-").append(creationDateTime).append("-").append(
					session.getId()).append(".transaction");
			return new IOFileParameter(buffer.toString(), canWrite, ifp.getUserName(), ifp.getPassword());
		}
		if (p instanceof IOSocketParameter) {
			IOSocketParameter sp = (IOSocketParameter) fsiToApplyWriteActions.getParameters();
			return new IOSocketParameter(sp.getDestinationHost(), sp.getPort(), sp.getBaseIdentifier(), IOSocketParameter.TYPE_TRANSACTION,
					creationDateTime, null, null);
		}
		throw new ODBRuntimeException(NeoDatisError.UNSUPPORTED_IO_TYPE.addParameter(p.getClass().getName()));
	}

	private void checkFileAccess(boolean canWrite) {
		checkFileAccess(canWrite, null);
	}

	private synchronized void checkFileAccess(boolean canWrite, String fileName) {
		if (fsi == null) {

			IBaseIdentification p = null;
			// to unable direct junit test of FileSystemInterface
			if (fsiToApplyWriteActions == null) {
				p = new IOFileParameter(fileName, canWrite,null,null);
			} else {
				p = getParameters(canWrite);
			}
			// To enable unit test
			if (session != null) {
				isLocal = session.getStorageEngine().isLocal();
			}

			fsi = new LocalFileSystemInterface("transaction", session, p, false, OdbConfiguration.getDefaultBufferSizeForTransaction());
		}
	}

	protected void persist() {
		checkFileAccess(true);
		try {
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("# Persisting transaction " + getName());
			}
			fsi.setWritePosition(0, false);
			fsi.writeBoolean(isCommited, false);
			fsi.writeLong(creationDateTime, false, "creation date", DefaultWriteAction.DIRECT_WRITE_ACTION);
			// Size
			fsi.writeLong(0, false, "size", DefaultWriteAction.DIRECT_WRITE_ACTION);
			hasBeenPersisted = true;
		} finally {
		}
	}

	public IOdbList<IWriteAction> getWriteActions() {
		return writeActions;
	}

	public long getCreationDateTime() {
		return creationDateTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#isCommited()
	 */
	public boolean isCommited() {
		return isCommited;
	}

	/**
	 * Mark te transaction file as committed
	 * 
	 * @param isConfirmed
	 */
	private void setCommited(boolean isConfirmed) {
		this.isCommited = isConfirmed;
		checkFileAccess(true);
		try {
			// TODO Check atomicity

			// Writes the number of write actions after the byte and date
			fsi.setWritePositionNoVerification(ODBType.BYTE.getSize() + ODBType.LONG.getSize(), false);
			fsi.writeLong(numberOfWriteActions, false, "nb write actions", DefaultWriteAction.DIRECT_WRITE_ACTION);
			// FIXME The fsi.flush should not be called after the last write?
			fsi.flush();
			// Only set useBuffer = false when it is a local database to avoid
			// net io overhead
			if (isLocal) {
				fsi.useBuffer(false);
			}
			fsi.setWritePositionNoVerification(0, false);
			fsi.writeByte((byte) 1, false);
		} finally {
		}
	}

	private void checkRollback() {
		if (wasRollbacked) {
			throw new ODBRuntimeException(NeoDatisError.ODB_HAS_BEEN_ROLLBACKED);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#rollback()
	 */
	public void rollback() {
		wasRollbacked = true;
		if (fsi != null) {
			fsi.close();
			delete();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#commit()
	 */
	public void commit() {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.info("Commiting " + numberOfWriteActions + " write actions - In Memory : " + hasAllWriteActionsInMemory + " - sid="
					+ session.getId());
		}
		// Check if database has been rollbacked
		checkRollback();

		// call the commit listeners
		manageCommitListenersBefore();

		if (currentWriteAction != null && !currentWriteAction.isEmpty()) {
			addWriteAction(currentWriteAction);
			currentWriteAction = null;
		}
		if (fsi == null && numberOfWriteActions != 0) {
			throw new ODBRuntimeException(NeoDatisError.TRANSACTION_ALREADY_COMMITED_OR_ROLLBACKED);
		}

		if (numberOfWriteActions == 0 || readOnlyMode) {
			// FIXME call commitMetaModel in realOnlyMode?
			commitMetaModel();
			// Nothing to do
			if (fsi != null) {
				fsi.close();
				fsi = null;
			}
			if (session != null) {
				session.getCache().clearOnCommit();
			}

			return;
		}
		// Marks the transaction as committed
		setCommited(true);

		// Apply the write actions the main database file
		applyTo();
		// Commit Meta Model changes
		commitMetaModel();

		if (archiveLog) {
			fsi.setWritePositionNoVerification(0, false);
			fsi.writeByte((byte) 2, false);
			fsi.getIo().enableAutomaticDelete(false);
			fsi.close();

			fsi = null;
		} else {
			fsi.close();
			delete();
			fsi = null;
		}
		if (session != null) {
			session.getCache().clearOnCommit();
		}
		manageCommitListenersAfter();
	}

	private void manageCommitListenersAfter() {
		IOdbList<ICommitListener> listeners = session.getStorageEngine().getCommitListeners();
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		Iterator<ICommitListener> iterator = listeners.iterator();
		ICommitListener commitListener = null;
		while (iterator.hasNext()) {
			commitListener = iterator.next();
			commitListener.afterCommit();
		}
	}

	private void manageCommitListenersBefore() {
		IOdbList<ICommitListener> listeners = session.getStorageEngine().getCommitListeners();
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		Iterator<ICommitListener> iterator = listeners.iterator();
		ICommitListener commitListener = null;
		while (iterator.hasNext()) {
			commitListener = iterator.next();
			commitListener.beforeCommit();
		}
	}

	/**
	 * Used to commit meta model : classes This is useful when running in client
	 * server mode TODO Check this
	 */
	protected void commitMetaModel() {

		MetaModel sessionMetaModel = session.getMetaModel();
		// If meta model has not been modified, there is nothing to do
		if (!sessionMetaModel.hasChanged()) {
			return;
		}

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Start commitMetaModel");
		}

		MetaModel lastCommitedMetaModel = new SessionMetaModel();

		if (isLocal) {
			// In local mode, we must not reload the meta model as there is no
			// concurrent access
			lastCommitedMetaModel = sessionMetaModel;
		} else {
			// In ClientServer mode, re-read the meta-model from the database
			// base to get last update.
			lastCommitedMetaModel = session.getStorageEngine().getObjectReader().readMetaModel(lastCommitedMetaModel, false);
		}

		// Gets the classes that have changed (that have modified ,deleted or
		// inserted objects)
		Iterator<ClassInfo> cis = sessionMetaModel.getChangedClassInfo().iterator();

		ClassInfo newCi = null;
		ClassInfo lastCommittedCI = null;
		IObjectWriter writer = session.getStorageEngine().getObjectWriter();
		OID lastCommittedObjectOIDOfThisTransaction = null;
		OID lastCommittedObjectOIDOfPrevTransaction = null;

		// for all changes between old and new meta model
		while (cis.hasNext()) {
			newCi = cis.next();

			if (lastCommitedMetaModel.existClass(newCi.getFullClassName())) {
				// The last CI represents the last committed meta model of the
				// database
				lastCommittedCI = lastCommitedMetaModel.getClassInfoFromId(newCi.getId());
				// Just be careful to keep track of current CI committed zone
				// deleted objects
				lastCommittedCI.getCommitedZoneInfo().setNbDeletedObjects(newCi.getCommitedZoneInfo().getNbDeletedObjects());
			} else {
				lastCommittedCI = newCi;
			}

			lastCommittedObjectOIDOfThisTransaction = newCi.getCommitedZoneInfo().last;
			lastCommittedObjectOIDOfPrevTransaction = lastCommittedCI.getCommitedZoneInfo().last;
			/*
			 * Take last committed number of objects and sets to CI
			 * newCi.getCommitedZoneInfo().nbObjects =
			 * lastCommittedCI.getCommitedZoneInfo().nbObjects;
			 * newCi.getCommitedZoneInfo().first =
			 * lastCommittedCI.getCommitedZoneInfo().first;
			 * newCi.getCommitedZoneInfo().last =
			 * lastCommittedCI.getCommitedZoneInfo().last;
			 */
			OID lastCommittedObjectOID = lastCommittedObjectOIDOfPrevTransaction;
			// If some object have been created then
			if (lastCommittedObjectOIDOfPrevTransaction != null) {
				// Checks if last object of committed meta model has not been
				// deleted
				if (session.getCache().isDeleted(lastCommittedObjectOIDOfPrevTransaction)) {
					// TODO This is wrong: if a committed transaction deleted a
					// committed object and creates x new
					// objects, then all these new objects will be lost:
					// if it has been deleted then use the last object of the
					// session class info
					lastCommittedObjectOID = lastCommittedObjectOIDOfThisTransaction;
					newCi.getCommitedZoneInfo().last = lastCommittedObjectOID;
				}
			}
			// Connect Unconnected zone to connected zone
			// make next oid of last committed object point to first
			// uncommitted object
			// make previous oid of first uncommitted object point to
			// last committed object
			if (lastCommittedObjectOID != null && newCi.getUncommittedZoneInfo().hasObjects()) {
				if (newCi.getCommitedZoneInfo().hasObjects()) {
					// these 2 updates are executed directly without
					// transaction, because
					// We are in the commit process.
					writer.updateNextObjectFieldOfObjectInfo(lastCommittedObjectOID, newCi.getUncommittedZoneInfo().first, false);
					writer.updatePreviousObjectFieldOfObjectInfo(newCi.getUncommittedZoneInfo().first, lastCommittedObjectOID, false);
				} else {
					// Committed zone has 0 object
					writer.updatePreviousObjectFieldOfObjectInfo(newCi.getUncommittedZoneInfo().first, null, false);
				}
			}

			// The number of committed objects must be updated with the number
			// of the last committed CI because a transaction may have been
			// committed changing this number.
			// Notice that the setNbObjects receive the full CommittedCIZoneInfo
			// object
			// because it will set the number of objects and the number of
			// deleted objects
			newCi.getCommitedZoneInfo().setNbObjects(lastCommittedCI.getCommitedZoneInfo());
			// and don't forget to set the deleted objects
			// This sets the number of objects, the first object OID and the
			// last object OID
			newCi = buildClassInfoForCommit(newCi);
			writer.updateInstanceFieldsOfClassInfo(newCi, false);

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("Analysing class " + newCi.getFullClassName());
				DLogger.debug("\t-Commited CI   = " + newCi);
				DLogger.debug("\t-connect last commited object with oid " + lastCommittedObjectOID + " to first uncommited object "
						+ newCi.getUncommittedZoneInfo().first);
				DLogger.debug("\t-Commiting new Number of objects = " + newCi.getNumberOfObjects());
			}

		}

		sessionMetaModel.resetChangedClasses();
		// To guarantee integrity after commit, the meta model is set to null
		// If the user continues using odb instance after commit the meta model
		// will be lazy-reloaded. Only for Client Server mode
		if (!isLocal) {
			session.setMetaModel(null);
		}

	}

	/**
	 * Shift all unconnected infos to connected (committed) infos
	 * 
	 * @param classInfo
	 * @return The updated class info
	 */
	public ClassInfo buildClassInfoForCommit(ClassInfo classInfo) {
		long nbObjects = classInfo.getNumberOfObjects();

		classInfo.getCommitedZoneInfo().setNbObjects(nbObjects);

		if (classInfo.getCommitedZoneInfo().first != null) {
			// nothing to change
		} else {
			classInfo.getCommitedZoneInfo().first = classInfo.getUncommittedZoneInfo().first;
		}

		if (classInfo.getUncommittedZoneInfo().last != null) {
			classInfo.getCommitedZoneInfo().last = classInfo.getUncommittedZoneInfo().last;
		}
		// Resets the unconnected zone info
		classInfo.getUncommittedZoneInfo().set(new CIZoneInfo(classInfo, null, null, 0));

		return classInfo;
	}

	public static DefaultTransaction read(String fileName) throws IOException, ClassNotFoundException {
		OdbFile file = new OdbFile(fileName);
		if(!file.exists()){
			throw new ODBRuntimeException(NeoDatisError.FILE_NOT_FOUND.addParameter(fileName));
		}
		// @TODO check this
		DefaultTransaction transaction = new DefaultTransaction(null, fileName);
		transaction.loadWriteActions(fileName, false);
		return transaction;
	}

	public void loadWriteActions(boolean apply) {
		loadWriteActions(getName(), apply);
	}

	public void loadWriteActions(String filename, boolean apply) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Load write actions of " + filename);
		}
		DefaultWriteAction wa = null;
		try {
			checkFileAccess(false, filename);
			fsi.useBuffer(true);
			fsi.setReadPosition(0);
			isCommited = fsi.readByte() == 1;
			creationDateTime = fsi.readLong();
			long totalNumberOfWriteActions = fsi.readLong();
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.info(writeActions.size() + " write actions in file");
			}
			for (int i = 0; i < totalNumberOfWriteActions; i++) {
				wa = DefaultWriteAction.read(fsi, i + 1);

				if (apply) {
					wa.applyTo(fsiToApplyWriteActions, i + 1);
					wa.clear();
				} else {
					addWriteAction(wa, false);
				}
			}
			if (apply) {
				fsiToApplyWriteActions.flush();
			}
		} finally {
		}
	}

	public void loadWriteActionsBackwards(String filename, boolean apply) throws IOException, ClassNotFoundException {

		int executedWriteAction = 0;
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Load write actions of " + filename);
		}
		IWriteAction wa = null;
		try {
			checkFileAccess(false, filename);
			fsi.useBuffer(true);
			fsi.setReadPosition(0);
			isCommited = fsi.readByte() == 1;
			creationDateTime = fsi.readLong();

			Map<Long,Long> writtenPositions = null;

			if (apply) {
				writtenPositions = new OdbHashMap<Long, Long>();
			}
			Long position = new Long(-1);
			int i = numberOfWriteActions;

			long previousWriteActionPosition = fsi.getLength();
			while (i > 0) {
				// Sets the position 8 bytes backwards
				fsi.setReadPosition(previousWriteActionPosition - ODBType.LONG.getSize());
				// And then the read a long, this will be the previous write
				// action position
				previousWriteActionPosition = fsi.readLong();
				// Then sets the read position to read the write action
				fsi.setReadPosition(previousWriteActionPosition);
				wa = DefaultWriteAction.read(fsi, i + 1);

				if (apply) {
					position = new Long(wa.getPosition());

					if (writtenPositions.get(position) != null) {
						// It has already been written something more recent at
						// this position, do not write again
						i--;
						continue;
					}

					wa.applyTo(fsiToApplyWriteActions, i + 1);
					writtenPositions.put(position, position);
					executedWriteAction++;
				} else {
					addWriteAction(wa, false);
				}
				i--;
			}
			if (apply) {
				fsiToApplyWriteActions.flush();
				if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
					DLogger.debug("Total Write actions : " + i + " / position cache = " + writtenPositions.size());
				}
				DLogger.info("Total write actions = " + numberOfWriteActions + " : executed = " + executedWriteAction);
				writtenPositions.clear();
				writtenPositions = null;
			}
		} finally {
		}
	}

	/**
	 * deletes the transaction file
	 * 
	 * @throws IOException
	 */
	protected void delete() {
		// The delete is done automatically by underlying api
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("state=").append(isCommited).append(" | creation=").append(creationDateTime).append(" | write actions numbers=")
				.append(numberOfWriteActions);
		return buffer.toString();
	}

	private void applyTo() {
		int realWriteNumber = 0;
		int noPointerWA = 0;
		if (!isCommited) {
			DLogger.info("can not execute a transaction that is not confirmed");
			return;
		}
		if (hasAllWriteActionsInMemory) {
			for (int i = 0; i < writeActions.size(); i++) {
				DefaultWriteAction wa = (DefaultWriteAction) writeActions.get(i);
				wa.applyTo(fsiToApplyWriteActions, i + 1);
				wa.clear();
			}

			fsiToApplyWriteActions.flush();
		} else {
			loadWriteActions(true);
			fsiToApplyWriteActions.flush();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#setFsiToApplyWriteActions(org.neodatis.odb.core.impl.layers.layer3.engine.FileSystemInterface)
	 */
	public void setFsiToApplyWriteActions(IFileSystemInterface fsi) {
		this.fsiToApplyWriteActions = fsi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#isArchiveLog()
	 */
	public boolean isArchiveLog() {
		return archiveLog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#setArchiveLog(boolean)
	 */
	public void setArchiveLog(boolean archiveLog) {
		this.archiveLog = archiveLog;
	}

	/**
	 * @return Returns the numberOfWriteActions.
	 */
	public int getNumberOfWriteActions() {
		if (currentWriteAction != null && !currentWriteAction.isEmpty()) {
			return numberOfWriteActions + 1;
		}
		return numberOfWriteActions;
	}

	public IFileSystemInterface getFsi() throws IOException {
		if (fsi == null) {
			checkFileAccess(!readOnlyMode);
		}
		return fsi;
	}

	/**
	 * Set the write position (position in main database file). This is used to
	 * know if the next write can be appended to the previous one (in the same
	 * current Write Action) or not.
	 * 
	 * @param position
	 */
	public void setWritePosition(long position) {
		if (position != this.currentWritePositionInWA) {
			this.currentWritePositionInWA = position;
			if (currentWriteAction != null) {
				addWriteAction(currentWriteAction);
			}
			this.currentWriteAction = new DefaultWriteAction(position);
		} else {
			if (currentWriteAction == null) {
				this.currentWriteAction = new DefaultWriteAction(position);
				this.currentWritePositionInWA = position;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.neodatis.odb.core.impl.transaction.ITransaction#manageWriteAction(long,
	 *      byte[])
	 */
	public void manageWriteAction(long position, byte[] bytes) {
		if (this.currentWritePositionInWA == position) {
			if (currentWriteAction == null) {
				currentWriteAction = provider.getWriteAction(position, null);
			}
			currentWriteAction.addBytes(bytes);
			this.currentWritePositionInWA += bytes.length;
		} else {
			if (currentWriteAction != null) {
				addWriteAction(currentWriteAction);
			}
			this.currentWriteAction = provider.getWriteAction(position, bytes);
			this.currentWritePositionInWA = position + bytes.length;
		}
	}

}
