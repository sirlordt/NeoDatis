/**
 * 
 */
package org.neodatis.odb.test.fromusers.adauto;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class Dao<T> {

	private ODB odb = null;

	public Dao(String fileName) {

		odb = ODBFactory.open(fileName);

	}

	public void create(T object) {
		OID oid = odb.store(object);

	}

	public Objects<T> read(IQuery query) {

		Objects<T> obj = odb.getObjects(query);
		return obj;

	}

	public void update(T object) {

		create(object);

	}

	public void close() {
		odb.close();
	}

}
