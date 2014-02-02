/**
 * 
 */
package org.neodatis.odb.test.oid;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.impl.core.oid.ExternalClassOID;
import org.neodatis.odb.impl.core.oid.OdbClassOID;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.IOUtil;

/**
 * @author olivier
 * 
 */
public class TestOIDToString extends ODBTest {
	public void test1() {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		OID oid = odb.store(new Function("My Function"));
		odb.close();
		IOUtil.deleteFile(baseName);
		String soid = oid.oidToString();

		OID oid2 = OIDFactory.oidFromString(soid);

		assertEquals(oid, oid2);

	}

	public void test3() {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		OID oid = odb.store(new Function("My Function"));
		oid = odb.ext().convertToExternalOID(oid);
		odb.close();
		IOUtil.deleteFile(baseName);
		String soid = oid.oidToString();
		println(soid);

		OID oid2 = OIDFactory.oidFromString(soid);

		assertEquals(oid, oid2);

	}

	public void test2() {
		OdbClassOID oid = new OdbClassOID(10002);
		String soid = oid.oidToString();

		OID oid2 = OIDFactory.oidFromString(soid);

		assertEquals(oid, oid2);

	}

	public void test4() {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		ExternalClassOID oid = new ExternalClassOID(new OdbClassOID(19), odb.ext().getDatabaseId());
		String soid = oid.oidToString();

		OID oid2 = OIDFactory.oidFromString(soid);

		assertEquals(oid, oid2);

	}

}
