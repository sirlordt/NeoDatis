/**
 * 
 */
package org.neodatis.odb.test.server.trigger.autoincrement;

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

/**
 * @author olivier
 * 
 */
public class ClientAutoIncrementTrigger extends InsertTrigger {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.InsertTrigger#afterInsert(java.lang.Object,
	 * org.neodatis.odb.OID)
	 */
	@Override
	public void afterInsert(Object object, OID oid) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.InsertTrigger#beforeInsert(java.lang.Object
	 * )
	 */
	@Override
	public boolean beforeInsert(Object object) {
		String className = object.getClass().getName();

		// Check if it is the right class
		if (!className.equals(ObjectWithAutoIncrementId.class.getName())) {
			return false;
		}
		// The mutex is used to avoid concurrent access for this operation
		Mutex mutex = MutexFactory.get("auto increment mutex");
		try {
			try {
				// Acquire the trigger
				mutex.acquire("trigger");

				long id = getNextId(className);

				ObjectWithAutoIncrementId o = (ObjectWithAutoIncrementId) object;
				// Sets the value on the user object
				o.setId(id);
				return true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR, e);
			}
		} finally {
			// Release the mutex
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
