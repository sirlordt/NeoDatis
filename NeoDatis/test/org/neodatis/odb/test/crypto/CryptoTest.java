package org.neodatis.odb.test.crypto;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.layers.layer3.crypto.AesMd5Cypher;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.io.OdbFileIO;

public class CryptoTest extends ODBTest {

	
	@Override
	public void setUp() throws Exception {
		cryptoOn = true;
	}
	
	@Override
	protected void tearDown() throws Exception {
		cryptoOn = false;
	}
	public void test1() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, IOException {
		CryptoFileIO cfio = new CryptoFileIO("crypto", "rw");
		byte[] bytes = { 1, 2, 3, 4, 5, 6 };
		cfio.write(bytes);
		cfio.close();

		bytes = new byte[6];
		cfio = new CryptoFileIO("crypto", "rw");
		long l = cfio.read(bytes);

		assertEquals(6, l);
		assertEquals(1, bytes[0]);
		assertEquals(2, bytes[1]);
		assertEquals(3, bytes[2]);
		assertEquals(4, bytes[3]);
		assertEquals(5, bytes[4]);
		assertEquals(6, bytes[5]);

	}

	public void test2() throws Exception {
		String baseName = getBaseName();
		OdbConfiguration.setIOClass(AesMd5Cypher.class, "bli blu");
		ODB odb = open(baseName);
		odb.store(new Function("test"));
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(Function.class);
		Function f = (Function) os.getFirst();
		odb.close();
		assertEquals("test", f.getName());
	}
	
	/**
	 * To test data are actually encrypted
	 * @throws Exception
	 */
	public void testDataAreEncrypted() throws Exception {
		String baseName = getBaseName();
		OdbConfiguration.setIOClass(AesMd5Cypher.class, "bli blu");
		ODB odb = open(baseName);
		odb.store(new Function("test"));
		odb.close();

		// now tries to open it without crypto, it must throw exception
		try{
			cryptoOn = false;
			odb = open(baseName);
			assertTrue(false);
		}catch(Exception e){
			
		}
		
	}

	public void test3() throws Exception {
		int size = 100;
		String baseName = getBaseName();
		OdbConfiguration.setIOClass(AesMd5Cypher.class, "bla bla bla");
		ODB odb = open(baseName);
		for (int i = 0; i < size; i++) {
			odb.store(new Function("test" + i));
		}
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(Function.class);
		assertEquals(size, os.size());
		Function f = (Function) os.getFirst();
		odb.close();
		assertEquals("test0", f.getName());

	}

	public void test4() throws Exception {
		long start = OdbTime.getCurrentTimeInMs();
		int size = 1000;
		String baseName = getBaseName();
		OdbConfiguration.setIOClass(AesMd5Cypher.class, "bla bla bla blu");
		ODB odb = open(baseName);
		for (int i = 0; i < size; i++) {
			odb.store(new Function("test" + i));
		}
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(Function.class);
		assertEquals(size, os.size());
		Function f = (Function) os.getFirst();
		odb.close();
		assertEquals("test0", f.getName());
		long end = OdbTime.getCurrentTimeInMs();
		println("Time with Encryption=" + (end - start));
	}

	/** Without encryption */
	public void test5() throws Exception {
		long start = OdbTime.getCurrentTimeInMs();
		int size = 1000;
		OdbConfiguration.setIOClass(OdbFileIO.class, "bla bla bla blu");
		String baseName = getBaseName();
		ODB odb = open(baseName);
		for (int i = 0; i < size; i++) {
			odb.store(new Function("test" + i));
		}
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(Function.class);
		assertEquals(size, os.size());
		Function f = (Function) os.getFirst();
		odb.close();
		assertEquals("test0", f.getName());
		long end = OdbTime.getCurrentTimeInMs();
		println("Time without Encryption=" + (end - start));
	}

}
