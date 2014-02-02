package org.neodatis.odb.impl.main;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer3.IRefactorManager;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.trigger.DeleteTrigger;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.core.trigger.SelectTrigger;
import org.neodatis.odb.core.trigger.UpdateTrigger;

public class ODBForTrigger extends ODBAdapter {

	public ODBForTrigger(IStorageEngine storageEngine) {
		super(storageEngine);
	}

	public void addDeleteTrigger(DeleteTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void addInsertTrigger(InsertTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void addSelectTrigger(SelectTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void addUpdateTrigger(UpdateTrigger trigger) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void close() {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void commit() {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void commitAndClose() {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void defragmentTo(String newFileName) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void disconnect(Object object) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public ClassRepresentation getClassRepresentation(Class clazz) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public ClassRepresentation getClassRepresentation(String fullClassName) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public IRefactorManager getRefactorManager() {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public ISession getSession() {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void reconnect(Object object) {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void rollback() {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

	public void run() {
		throw new ODBRuntimeException(NeoDatisError.OPERATION_NOT_ALLOWED_IN_TRIGGER);
	}

}
