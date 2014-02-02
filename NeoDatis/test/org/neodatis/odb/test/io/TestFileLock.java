package org.neodatis.odb.test.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.io.OdbFileIO;

public class TestFileLock extends ODBTest {

	public void test1() throws IOException {

		RandomAccessFile raf = new RandomAccessFile(ODBTest.DIRECTORY + "testLock1", "rw");
		raf.seek(1024);
		raf.write(10);

		FileLock fileLock = raf.getChannel().lock(0, 1024, false);

		assertEquals(true, fileLock != null);

		RandomAccessFile raf2 = new RandomAccessFile(ODBTest.DIRECTORY + "testLock1", "rw");
		try{
			FileLock fileLock2 = raf2.getChannel().lock(0, 1024, false);
			fail("The lock did not work");
		}catch (Exception e) {
			
		}
		fileLock.release();
		raf.close();

	}

	public void test2NoWrite() throws IOException {

		RandomAccessFile raf = new RandomAccessFile(ODBTest.DIRECTORY + "testLock1", "rw");
		raf.seek(1024);

		FileLock fileLock = raf.getChannel().lock(0, 1, false);

		assertEquals(true, fileLock != null);

		raf.close();

	}

	/**
	 * Simple lock
	 * 
	 * @throws IOException
	 */
	public void testOdbFileIo() throws IOException {

		OdbFileIO fileIO = new OdbFileIO(ODBTest.DIRECTORY + "testLock1", true, null);
		fileIO.seek(1024);
		fileIO.write((byte) 10);

		fileIO.lockFile();

		assertEquals(true, fileIO.isLocked());

		fileIO.close();

	}

	/**
	 * Simple lock
	 * 
	 * @throws IOException
	 */
	public void testOdbFileIo2() throws IOException {

		OdbFileIO fileIO = new OdbFileIO(ODBTest.DIRECTORY + "testLock2", true, null);
		fileIO.seek(1024);
		fileIO.write((byte) 10);

		boolean isLocked = fileIO.lockFile();
		assertTrue(isLocked);

		OdbFileIO fileIO2 = new OdbFileIO(ODBTest.DIRECTORY + "testLock2", true, null);
		try{
			fileIO2.lockFile();
			fail("The lock did not work");
		}catch (Exception e) {
			// TODO: handle exception
		}

		//assertEquals(true, fileIO2.isLocked());

		fileIO.close();

	}

}
