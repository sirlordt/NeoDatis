
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
package org.neodatis.odb.core.layers.layer3;

import java.io.IOException;


/**The interface for buffered IO
 * 
 * @author osmadja
 *
 */
public interface IBufferedIO {

	void goToPosition(long position) ;

	long getLength() ;

	/**
	 * Checks if the new position is in the buffer, if not, flushes the buffer
	 * and rebuilds it to the correct position
	 * 
	 * @param newPosition
	 * @param readOrWrite
	 * @param size
	 *            Size if the data that must be stored
	 * @return The index of the buffer where that contains the position 
	 * 
	 */
	int manageBufferForNewPosition(long newPosition, int readOrWrite, int size) ;

	boolean isUsingbuffer();

	void setUseBuffer(boolean useBuffer) ;

	long getCurrentPosition();

	void setCurrentWritePosition(long currentPosition) ;

	void setCurrentReadPosition(long currentPosition) ;

	void writeByte(byte b) ;

	byte[] readBytesOld(int size) ;

	byte[] readBytes(int size) ;

	byte readByte() ;

	void writeBytes(byte[] bytes);

	void flushBuffer(int bufferIndex);
	void flushAllBuffers();
	void flushIO() throws IOException;

	long getIoDeviceLength();

	void setIoDeviceLength(long ioDeviceLength);

	void close();

    void clear();
    boolean delete();
    boolean isForTransaction();
    void enableAutomaticDelete(boolean yesOrNo);
    boolean automaticDeleteIsEnabled();

}