package org.neodatis.odb.test.oid;

import java.util.Map;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.impl.core.oid.OdbObjectOID;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.StopWatch;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class TestOid extends ODBTest {
	public void testEquals() {
		OID oid1 = OIDFactory.buildObjectOID(1);
		OID oid2 = OIDFactory.buildObjectOID(1);

		assertEquals(oid1, oid2);
	}

	public void testOIdInMap() {
		OID oid1 = OIDFactory.buildObjectOID(1);
		OID oid2 = OIDFactory.buildObjectOID(1);

		Map map = new OdbHashMap();
		map.put(oid1, "oid1");

		assertNotNull(map.get(oid2));
	}

	/** Performance test. Using ID or long in hash map */
	public void testPerformanceLong() {
		int size = 300000;
		Map longMap = new OdbHashMap();

		// LONG
		StopWatch timeLongMapCreation = new StopWatch();

		timeLongMapCreation.start();
		// Creates a hashmap with 100000 Longs
		for (int i = 0; i < size; i++) {
			longMap.put(new Long(i), String.valueOf(i));
		}
		timeLongMapCreation.end();

		StopWatch timeLongMapGet = new StopWatch();

		timeLongMapGet.start();
		// get all map elements
		for (int i = 0; i < size; i++) {
			assertNotNull(longMap.get(new Long(i)));
		}
		timeLongMapGet.end();

		println(size + " objects : Long Map creation=" + timeLongMapCreation.getDurationInMiliseconds() + " - get="
				+ timeLongMapGet.getDurationInMiliseconds());
	}

	/** Performance test. Using ID or long in hash map */
	public void testPerformanceOid() {
		int size = 300000;
		Map oidMap = new OdbHashMap();

		// OID

		StopWatch timeOidMapCreation = new StopWatch();

		timeOidMapCreation.start();
		// Creates a hashmap with 100000 Longs
		for (int i = 0; i < size; i++) {
			oidMap.put(OIDFactory.buildObjectOID(i), String.valueOf(i));
		}
		timeOidMapCreation.end();

		StopWatch timeOidMapGet = new StopWatch();

		timeOidMapGet.start();
		// get all map elements
		for (int i = 0; i < size; i++) {
			assertNotNull(oidMap.get(OIDFactory.buildObjectOID(i)));
		}
		timeOidMapGet.end();

		println(size + " objects : OID Map creation=" + timeOidMapCreation.getDurationInMiliseconds() + " - get="
				+ timeOidMapGet.getDurationInMiliseconds());

	}

	public void testAndy1() {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		B b1 = new B("b");
		A a1 = new A("a", b1);

		odb.store(a1);
		OID oida = odb.getObjectId(a1);
		OID oidb = odb.getObjectId(b1);
		odb.close();

		odb = open(baseName);
		A a2 = (A) odb.getObjectFromId(oida);
		B b2 = (B) odb.getObjectFromId(oidb);
		odb.close();
		assertNotNull(a2);
		assertNotNull(b2);
		assertNotNull(a2.getB());

	}

	public void testAndy2() {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		B b1 = new B("b");
		A a1 = new A("a", b1);

		odb.store(a1);
		long oida = ((OID) odb.getObjectId(a1)).getObjectId();
		long oidb = ((OID) odb.getObjectId(b1)).getObjectId();
		odb.close();

		odb = open(baseName);
		A a2 = (A) odb.getObjectFromId(new OdbObjectOID(oida));
		B b2 = (B) odb.getObjectFromId(new OdbObjectOID(oidb));
		odb.close();
		assertNotNull(a2);
		assertNotNull(b2);
		assertNotNull(a2.getB());

	}

	public void testAndy3() {
		String baseName = getBaseName();
		ODB odb = open(baseName);
		try {
			A a2 = (A) odb.getObjectFromId(new OdbObjectOID(34));
			fail("Should have thrown Exception");
		} catch (Exception e) {
			// ok must enter the catch block
		}
	}

}
