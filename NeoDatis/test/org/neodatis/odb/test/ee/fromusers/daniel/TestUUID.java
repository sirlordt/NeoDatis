/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.daniel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestUUID extends ODBTest {
	public void test1() throws MalformedURLException, IOException {
		
		UUID uuid = new UUID(10,5);
		int hc1 = uuid.hashCode();
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.getClassRepresentation(UUID.class.getName(), false).persistAttribute("hashCode");
		odb.store(uuid);
		odb.close();
		
		
		odb = open(baseName);
		UUID uuid2 = (UUID) odb.getObjects(UUID.class).getFirst();
		int hc2 = uuid2.hashCode();
		odb.close();
		
		assertEquals(hc1, hc2);
		
		

	}

}
