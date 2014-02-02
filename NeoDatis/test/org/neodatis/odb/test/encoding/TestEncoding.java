package org.neodatis.odb.test.encoding;

import java.io.UnsupportedEncodingException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestEncoding extends ODBTest {

	public void test1() throws UnsupportedEncodingException {
		String baseName = getBaseName();
		println(baseName);
		String currentEncoding = OdbConfiguration.getDatabaseCharacterEncoding();
		OdbConfiguration.setDatabaseCharacterEncoding("ISO8859-5");
		ODB odb = null;
		try{
			odb = open(baseName);
			String nameWithCyrillicCharacters = "\u0410 \u0430 \u0431 \u0448 \u0429";
			Function f = new Function(nameWithCyrillicCharacters);
			OID oid = odb.store(f);
			odb.close();

			println(f);

			odb = open(baseName);
			Function f2 = (Function) odb.getObjectFromId(oid);
			odb.close();
			assertEquals(nameWithCyrillicCharacters, f2.getName());

			assertEquals('\u0410', f2.getName().charAt(0));
			assertEquals('\u0430', f2.getName().charAt(2));
			assertEquals('\u0431', f2.getName().charAt(4));
			assertEquals('\u0448', f2.getName().charAt(6));
			assertEquals('\u0429', f2.getName().charAt(8));

		}finally{
			OdbConfiguration.setDatabaseCharacterEncoding(currentEncoding);
		}

	}

	public void test2_ClientServer() throws UnsupportedEncodingException, InterruptedException {
		String baseName = getBaseName();
		println(baseName);
		String currentEncoding = OdbConfiguration.getDatabaseCharacterEncoding();
		OdbConfiguration.setDatabaseCharacterEncoding("ISO8859-5");
		ODBServer server = null;
		
		try{
			server = ODBFactory.openServer(ODBTest.PORT+1);
			server.addBase(baseName, baseName);
			server.startServer(true);
			Thread.sleep(200);
			ODB odb = ODBFactory.openClient("localhost",ODBTest.PORT+1, baseName);
			String nameWithCyrillicCharacters = "\u0410 \u0430 \u0431 \u0448 \u0429";
			Function f = new Function(nameWithCyrillicCharacters);
			OID oid = odb.store(f);
			odb.close();

			println(f);

			odb = ODBFactory.openClient("localhost",ODBTest.PORT+1, baseName);
			Function f2 = (Function) odb.getObjectFromId(oid);
			odb.close();
			assertEquals(nameWithCyrillicCharacters, f2.getName());

			assertEquals('\u0410', f2.getName().charAt(0));
			assertEquals('\u0430', f2.getName().charAt(2));
			assertEquals('\u0431', f2.getName().charAt(4));
			assertEquals('\u0448', f2.getName().charAt(6));
			assertEquals('\u0429', f2.getName().charAt(8));

		}finally{
			OdbConfiguration.setDatabaseCharacterEncoding(currentEncoding);
			if(server!=null){
				server.close();
			}
		}

	}

}
