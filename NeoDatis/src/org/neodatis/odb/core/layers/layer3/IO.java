package org.neodatis.odb.core.layers.layer3;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The basic IO interface for basic IO operation like reading and writing bytes
 * @author olivier
 *
 */
public interface IO {
	void init(String fileName, boolean canWrite, String password) throws FileNotFoundException, Exception;

	void seek(long pos) throws IOException;

	void close() throws IOException;

	void write(byte b) throws IOException;

	void write(byte[] bytes, int offset, int size) throws IOException;

	long read(byte[] bytes, int offset, int size) throws IOException;

	int read() throws IOException;

	long length() throws IOException;

	boolean lockFile() throws IOException;
	boolean unlockFile() throws IOException;
	boolean isLocked() throws IOException;
	void flushIO() throws IOException;
}
