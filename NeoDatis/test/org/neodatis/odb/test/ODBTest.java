/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.server.message.DeleteBaseMessage;
import org.neodatis.odb.core.server.message.DeleteBaseMessageResponse;
import org.neodatis.odb.impl.core.layers.layer3.crypto.AesMd5Cypher;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ServerAdmin;
import org.neodatis.odb.impl.core.transaction.CrossSessionCache;
import org.neodatis.tool.IOUtil;
import org.neodatis.tool.wrappers.io.OdbFileIO;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class ODBTest extends TestCase{
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS");

	public static boolean loadPropertiesFromFile = false;
	public static boolean isLocal = true;
	public static boolean useSameVmOptimization = false;
	public static boolean startServerAutomatically = false;
	public static String HOST = "localhost";
	public static int PORT = 13000;
	public static String DIRECTORY = "unit-test-data/";

	public static boolean runAll = false;

	/**
	 * as we use junit to test new feature and they may not be implemented yet,
	 * there is a way to disable testing them
	 */
	public static boolean testNewFeature = false;
	/**
	 * as we use junit for performance test and they depend on the computer on
	 * which they are run, there is a way to disable testing them
	 */
	public static boolean testPerformance = false;
	/**
	 * Some bugs are know but have a low priority because we can live with them
	 * so they may not be resolved now, so we can use with property to avoid
	 * executing theses tests
	 */
	public static boolean testKnownProblems = true;

	public static boolean cryptoOn = false;
	public static String cryptoPassword = "blabla";

	private static ODBServer server = null;

	private String name;
	
	
	static{
		if(loadPropertiesFromFile){
			Properties properties = new Properties();
			String propertyFileName = null;
			try {
				propertyFileName = System.getProperty("test.property.file","test/test.properties");
				properties.load(new FileInputStream(propertyFileName));
				String mode = properties.getProperty("mode");
				
				if(mode.equals("local")){
					isLocal = true;
				}
				if(mode.equals("same-vm-cs")){
					isLocal = false;
					startServerAutomatically = true;
					useSameVmOptimization = true;
				}
				if(mode.equals("cs")){
					isLocal = false;
					startServerAutomatically = false;
					useSameVmOptimization = false;
				}

				System.out.println(String.format(" NeoDatis Test Mode = %s, property file = %s",mode, propertyFileName));
			} catch (Exception e) {
				System.err.println("Error while loading test properties from "+propertyFileName);
			}

		}
	}

	public ODBTest() {
		super();
		name = getClass().getName();
		
		
	}

	public ODB open(String fileName, String user, String password) {

		if (cryptoOn) {
			OdbConfiguration.setIOClass(AesMd5Cypher.class, cryptoPassword);
		} else {
			OdbConfiguration.setIOClass(OdbFileIO.class, null);
		}

		if (isLocal) {
			return ODBFactory.open(DIRECTORY + fileName, user, password);
		}
		return openClient(HOST, PORT, fileName, user, password);
	}

	public ODB open(String fileName) {
		return open(fileName, null, null);
	}

	public ODB openLocal(String fileName) {
		return open(DIRECTORY + fileName, null, null);
	}

	public ODBServer openServer(int port) {
		ODBServer s = ODBFactory.openServer(port);
		return s;
	}

	public ODB openClient(String host, int port, String baseIdentifier) {
		return openClient(host, port, baseIdentifier, null, null);
	}

	public ODB openClient(String host, int port, String baseIdentifier, String user, String password) {
		if (startServerAutomatically) {
			startServer();
		}
		if (useSameVmOptimization) {
			return server.openClient( DIRECTORY + baseIdentifier);
		}

		return ODBFactory.openClient(host, port, DIRECTORY + baseIdentifier, user, password);
	}

	public void failCS() {
		assertTrue(true);
		// fail("Native query not supported in Client/ServerMode");
	}

	protected void failNotImplemented(String string) {
		// fail(string + " not implemented in Client/ServerMode");
		assertTrue(true);
	}

	/**
	 * The deleteBase even in SameVmClientServerMode user socket to reach the
	 * server
	 * 
	 * @param baseName
	 */
	public void deleteBase(String baseName) {
		if (isLocal) {
			String s = DIRECTORY + baseName;
			boolean b = IOUtil.deleteFile(s);
		} else {
			if (startServerAutomatically) {
				startServer();
			}
			ServerAdmin sa = new ServerAdmin(HOST, PORT);
			DeleteBaseMessage message = new DeleteBaseMessage(DIRECTORY + baseName);
			DeleteBaseMessageResponse rmessage = (DeleteBaseMessageResponse) sa.sendMessage(message);
			if (rmessage.hasError()) {
				throw new ODBRuntimeException(NeoDatisError.SERVER_ERROR.addParameter(rmessage.getError()));
			}
			
//			if(startServerAutomatically){
//				server.close();
//				server = null;
//			}
		}
	}

	public void t1estzzzz() {

	}

	public void print(Object o) {
		System.out.print(o);
	}

	public void println(Object o) {
		System.out.println(o);
	}

	public void println(long l) {
		System.out.println(l);
	}

	public void println(int i) {
		System.out.println(i);
	}

	public void println(float i) {
		System.out.println(i);
	}

	public void println(double i) {
		System.out.println(i);
	}

	public void startServer() {
		if (server == null) {
			println("+++++++ ODB TEST : Starting server on port " + PORT );
			server = openServer(ODBTest.PORT);
			server.setAutomaticallyCreateDatabase(true);
			// LogUtil.allOn(true);
			server.startServer(true);
			
		}
	}

	public String getBaseName() {
		return getName() + "." + sdf.format(new Date()) + "-" + Math.random() * 1000 + ".neodatis";
	}

	public static String getHOST() {
		return HOST;
	}

	public static void setHOST(String host) {
		HOST = host;
	}

	public static String getDIRECTORY() {
		return DIRECTORY;
	}

	public static void setDIRECTORY(String directory) {
		DIRECTORY = directory;
	}

	public static int getPORT() {
		return PORT;
	}

	public static void setPORT(int port) {
		PORT = port;
	}

	public void setUp() throws Exception{
		CrossSessionCache.clearAll();
		
	}

	/**
	 * 
	 */
	public void closeServer() {
		if(server!=null){
			println("Closing server on port " + PORT);
			server.close();
			server=null;
		}else{
			println("NOT Closing server on port " + PORT + " because it is null");
		}
		
	}
}
