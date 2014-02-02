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
package org.neodatis.odb.test.acid;

import java.io.IOException;
import java.math.BigDecimal;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;
import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;
import org.neodatis.odb.core.mock.MockSession;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.core.transaction.ITransaction;
import org.neodatis.odb.core.transaction.IWriteAction;
import org.neodatis.odb.impl.core.layers.layer3.engine.LocalFileSystemInterface;
import org.neodatis.odb.impl.core.transaction.DefaultTransaction;
import org.neodatis.odb.impl.core.transaction.DefaultWriteAction;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.DisplayUtility;
import org.neodatis.tool.mutex.MutexFactory;
import org.neodatis.tool.wrappers.OdbTime;

public class TestTransactionPersistence extends ODBTest {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		MutexFactory.setDebug(true);
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		MutexFactory.setDebug(false);
	}
	public void test4() throws IOException, ClassNotFoundException {
		IByteArrayConverter byteArrayConverter = OdbConfiguration.getCoreProvider().getByteArrayConverter();

		IWriteAction wa1 = new DefaultWriteAction(1, byteArrayConverter.intToByteArray(1), "size");
		assertEquals(wa1.getBytes(0).length, 4);

		IWriteAction wa2 = new DefaultWriteAction(1, byteArrayConverter.stringToByteArray("olá chico", true, -1, true), "size");

		IWriteAction wa3 = new DefaultWriteAction(1, byteArrayConverter.bigDecimalToByteArray(new BigDecimal("1.123456789"), true), "size");
	}

	public void test2B() throws Exception {
		if (!isLocal) {
			return;
		}
		String baseName = getBaseName();

		IByteArrayConverter byteArrayConverter = OdbConfiguration.getCoreProvider().getByteArrayConverter();
		// 155 : to avoid protected zone
		IWriteAction wa1 = new DefaultWriteAction(300 + 1, byteArrayConverter.intToByteArray(1), "size");
		IWriteAction wa2 = new DefaultWriteAction(300 + 15, byteArrayConverter.stringToByteArray(" 1 - olá chico! - 1", true, -1, true),
				"name");

		IWriteAction wa3 = new DefaultWriteAction(300 + 1, byteArrayConverter.intToByteArray(2), "size");
		IWriteAction wa4 = new DefaultWriteAction(300 + 15, byteArrayConverter.stringToByteArray(" 2 - olá chico! - 2", true, -1, true),
				"name");

		IStorageEngine se = OdbConfiguration.getCoreProvider().getClientStorageEngine(
				new IOFileParameter(ODBTest.DIRECTORY + baseName, true, null, null));
		// se.close();
		IFileSystemInterface fsi = se.getObjectWriter().getFsi();
		// new FileSystemInterface(null,se.getSession(),new
		// IOFileParameter("test.neodatis",true),false,Configuration.getDefaultBufferSizeForData());
		ITransaction transaction = se.getSession(true).getTransaction();
		transaction.setArchiveLog(true);

		transaction.manageWriteAction(wa1.getPosition(), wa1.getBytes(0));
		transaction.manageWriteAction(wa2.getPosition(), wa2.getBytes(0));
		transaction.manageWriteAction(wa3.getPosition(), wa3.getBytes(0));
		transaction.manageWriteAction(wa4.getPosition(), wa4.getBytes(0));

		// transaction.getFsi().flush();
		IWriteAction wat1 = (IWriteAction) ((DefaultTransaction) transaction).getWriteActions().get(2);
		byte[] bytes = wat1.getBytes(0);

		transaction.commit();
		DefaultTransaction transaction2 = DefaultTransaction.read(ODBTest.DIRECTORY + transaction.getName());

		
		IWriteAction wat2 = (IWriteAction) transaction2.getWriteActions().get(2);
		assertEquals(DisplayUtility.byteArrayToString(bytes), DisplayUtility.byteArrayToString(wat2.getBytes(0)));
		assertEquals(wat1.getPosition(), wat2.getPosition());
		transaction2.rollback();
		fsi.close();
		
	}

	/** @TODO this junit hangs when executing as part of the whole junit test suite
	 * 
	 * @throws Exception
	 */
	public void t1est3() throws Exception {
		IByteArrayConverter byteArrayConverter = OdbConfiguration.getCoreProvider().getByteArrayConverter();
		int size = 1000;
		String baseName = getBaseName();
		ISession session = new MockSession(baseName);
		IFileSystemInterface fsi = new LocalFileSystemInterface(baseName, session, new IOFileParameter(baseName+".transaction2",
				true, null, null), false, OdbConfiguration.getDefaultBufferSizeForData());
		DefaultTransaction transaction = new DefaultTransaction(session, fsi);
		transaction.setArchiveLog(true);
		for (int i = 0; i < size; i++) {
			// 155 : to avoid protected zone

			transaction.manageWriteAction(300 + i * 4 * 2, byteArrayConverter.intToByteArray(i));
		}
		IWriteAction wa1 = (IWriteAction) transaction.getWriteActions().get(size - 2);
		byte[] bytes = wa1.getBytes(0);
		transaction.commit();

		long start = OdbTime.getCurrentTimeInMs();
		DefaultTransaction transaction2 = DefaultTransaction.read(transaction.getName());
		long t = OdbTime.getCurrentTimeInMs() - start;

		IWriteAction wa2 = (IWriteAction) transaction2.getWriteActions().get(size - 2);
		assertEquals(DisplayUtility.byteArrayToString(bytes), DisplayUtility.byteArrayToString(wa2.getBytes(0)));
		assertEquals(wa1.getPosition(), wa2.getPosition());

		transaction2.rollback();
		fsi.close();
	}

	public void t1est5() throws Exception {
		IByteArrayConverter byteArrayConverter = OdbConfiguration.getCoreProvider().getByteArrayConverter();
		int size = 1000;
		String baseName = getBaseName();

		ISession session = new MockSession(baseName);
		IFileSystemInterface fsi = new LocalFileSystemInterface(baseName, session, new IOFileParameter(baseName+".transaction2",
				true, null, null), false, OdbConfiguration.getDefaultBufferSizeForData());
		DefaultTransaction transaction = new DefaultTransaction(session, fsi);
		transaction.setArchiveLog(true);
		for (int i = 0; i < size; i++) {
			// 155 : to avoid protected zone

			transaction.manageWriteAction(300 + i * 4, byteArrayConverter.intToByteArray(i));
		}
		// All write action were together so the transaction should have
		// appended all the bytes
		// in one WriteAction. As it as not been committed, the current
		// writeAction
		assertEquals(0, transaction.getWriteActions().size());

		transaction.commit();
		fsi.close();
	}
}
