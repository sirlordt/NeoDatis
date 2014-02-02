/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.icosystem;

import org.neodatis.odb.ODB;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 *
 */
public class TestExecutionPlan extends ODBTest {
	public void test1(){
		
		String baseName  =getBaseName();
		
		ODB odb = open(baseName);
		
		assertEquals(0,odb.getObjects(new CriteriaQuery(Function.class).setPolymorphic(true)).size());
		odb.close();
		
		
	}

}
