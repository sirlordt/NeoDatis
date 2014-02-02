package org.neodatis.odb.test.trigger;

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

public class LocalAutoIncrementTrigger extends InsertTrigger {

	public void afterInsert(Object object, OID oid) {
		System.out.println("after");

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
				// TODO Auto-generated catch block
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR, e);
			}
		} finally {
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
			ID id1 = new ID(idName, 1);
			odb.store(id1);
			return 1;
		}

		ID id = (ID) objects.getFirst();
		long lid = id.getNext();
		odb.store(id);
		return lid;
	}

}
