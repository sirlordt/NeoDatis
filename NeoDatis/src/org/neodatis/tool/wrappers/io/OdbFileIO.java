
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


package org.neodatis.tool.wrappers.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer3.IO;

/**
 * @sharpen.ignore
 * A class to wrap all accesses to File IO
 * @author olivier
 *
 */
public class OdbFileIO implements IO{
	private String fileName;
	private RandomAccessFile fileAccess;
	private FileLock fileLock;
	
	
	public OdbFileIO() throws IOException {
		
	}
	public OdbFileIO(String fileName, boolean canWrite, String password) throws IOException {
		init(fileName,canWrite,password);
	}
	
	public void init(String fileName, boolean canWrite, String password) throws IOException{
		this.fileName = fileName;
		try{
			this.fileAccess = new RandomAccessFile(fileName, canWrite ? "rw" : "r");
		}catch (IOException e) {
			throw new ODBRuntimeException(e, "Error while opening file " + fileName);
		}

	}

	public long length() throws IOException {
		return fileAccess.length();
	}
	public void seek(long position) throws IOException {
		try {
			if(position<0){
				throw new ODBRuntimeException(NeoDatisError.NEGATIVE_POSITION.addParameter(position));
			}
			fileAccess.seek(position);
		} catch (IOException e) {
			throw new ODBRuntimeException(NeoDatisError.GO_TO_POSITION.addParameter(position).addParameter(fileAccess.length()), e);
		}
	}
	public void write(byte b) throws IOException {
		fileAccess.writeByte(b);
	}
	public void write(byte[] bs, int offset, int size) throws IOException {
		fileAccess.write(bs,offset,size);
		
	}
	public int read() throws IOException {
		return fileAccess.read();
	}
	public long read(byte[] array, int offset, int size) throws IOException {
		return fileAccess.read(array, offset, size);
	}
	public void close() throws IOException {
		flushIO();
		fileAccess.close();		
		
	}

	public boolean lockFile() throws IOException{
		fileLock = fileAccess.getChannel().tryLock(0L, 1L, false);
		return fileLock!=null;
	}
	public boolean unlockFile() throws IOException{
		fileLock.release();
		fileLock = null;
		return true;
	}
	public boolean isLocked(){
		return fileLock!=null;
	}
	public void flushIO() throws IOException {
		this.fileAccess.getChannel().force(true);
	}
}
