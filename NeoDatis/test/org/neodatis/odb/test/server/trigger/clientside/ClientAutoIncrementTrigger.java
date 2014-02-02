package org.neodatis.odb.test.server.trigger.clientside;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.tool.mutex.Mutex;
import org.neodatis.tool.mutex.MutexFactory;

public class ClientAutoIncrementTrigger extends InsertTrigger {

	protected int nbCallsBeforeInsert;
	protected int nbCallsAfterInsert;
	
	public void afterInsert(Object object, OID oid) {
		System.out.println("after insert with oid "+ oid + " for object "+ object);
		nbCallsAfterInsert++;
	}

	public boolean beforeInsert(Object object) {
		if (object.getClass() != ObjectWithAutoIncrementId.class) {
			return false;
		}
		ObjectWithAutoIncrementId o = (ObjectWithAutoIncrementId) object;

		Mutex mutex = MutexFactory.get("auto increment mutex");
		try {
			try {
				mutex.acquire("trigger");

				long id = getNextId("test");
				o.setId(id);
				// System.out.println("setting new id "+ id);
				return true;
			} catch (InterruptedException e) {
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR, e);
			}
		} finally {
			nbCallsBeforeInsert++;
			if (mutex != null) {
				mutex.release("trigger");
			}
		}

	}

	/**
	 * Actually gets the next id Gets the object of type ID from the database
	 * with the specific name. Then increment the id value and returns. If
	 * object does not exist, creates t.
	 * 
	 * @param idName
	 * @return
	 */
	private long getNextId(String idName) {
		ODB odb = getOdb();
		Objects objects = odb.getObjects(new CriteriaQuery(ID.class, Where.equal("idName", idName)));

		if (objects.isEmpty()) {
			ID iid1d = new ID(idName, 1);
			odb.store(iid1d);
			return 1;
		}

		ID id = (ID) objects.getFirst();
		long lid = id.getNext();
		odb.store(id);
		return lid;
	}

	/**
	 * @return
	 */
	public int getNbCallsForBeforeInsert() {
		return nbCallsBeforeInsert;
	}
	public int getNbCallsForAfterInsert() {
		return nbCallsAfterInsert;
	}

}
