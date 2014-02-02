/**
 * 
 */
package org.neodatis.odb.test.ee.get;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

/**
 * @author olivier
 *
 */
public class TestGetAllObjects extends ODBTest{
	public void getAll(){
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(new User("name", "email", new Profile("profile name", new Function("function name"))));
		odb.close();
		
		odb = open(baseName);
		IQuery q = new CriteriaQuery(Object.class);
		q.setPolymorphic(true);
		Objects<Object> allObjects = odb.getObjects(q);
		odb.close();
		assertEquals(3, allObjects.size());
		print(allObjects);
		
	}

}
