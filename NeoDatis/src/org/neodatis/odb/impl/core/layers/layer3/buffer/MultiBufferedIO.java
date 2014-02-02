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
package org.neodatis.odb.impl.core.layers.layer3.buffer;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer3.IBufferedIO;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.io.MultiBufferVO;

/**
 * Abstract class allowing buffering for IO
 * 
 * This class is used to give a transparent access to buffered io : File, socket
 * The DefaultFileIO and DefaultSocketIO inherits from AbstractIO
 * 
 * @author olivier s
 * 
 */
public abstract class MultiBufferedIO implements IBufferedIO {
	public static long nbWrites;
	public static long totalWriteSize;

	/** Internal counter of flush */
	public static long numberOfFlush = 0;
	public static long totalFlushSize = 0;
	public static int nbFlushForOverlap = 0;
	
	public static int nbBufferOk;
	public static int nbBufferNotOk;
	public static int nbSamePositionForWrite;
	public static int nbSamePositionForRead;

	public static final String LOG_ID = "MultiBufferedIO";
	private final static int READ = 1;
	private final static int WRITE = 2;
	private String name;

	/** The length of the io device */
	private long ioDeviceLength;

	private MultiBufferVO multiBuffer;
	private int nbBuffers;
	private int[] overlappingBuffers;
	private int currentBufferIndex;

	/** The size of the buffer */
	private int bufferSize;

	

	/** A boolean value to check if read write are using buffer */
	private boolean isUsingBuffer;

	protected long currentPositionWhenUsingBuffer;

	private long currentPositionForDirectWrite;
	private boolean enableAutomaticDelete;

	private int nextBufferIndex;

	public MultiBufferedIO(int nbBuffers, String name, int bufferSize, boolean canWrite) {
		this.nbBuffers = nbBuffers;
		multiBuffer = new MultiBufferVO(nbBuffers, bufferSize);

		this.bufferSize = bufferSize;
		currentPositionWhenUsingBuffer = -1;
		currentPositionForDirectWrite = -1;
		
		overlappingBuffers = new int[nbBuffers];
		numberOfFlush = 0;
		isUsingBuffer = true;
		this.name = name;
		enableAutomaticDelete = true;

		nextBufferIndex = 0;
	}

	public abstract void goToPosition(long position);

	public abstract long getLength();

	public abstract void internalWrite(byte b);

	public abstract void internalWrite(byte[] bs, int size);

	public abstract byte internalRead();

	public abstract long internalRead(byte[] array, int size);

	public abstract void closeIO();

	

	public int manageBufferForNewPosition(long newPosition, int readOrWrite, int size) {
		int bufferIndex = multiBuffer.getBufferIndexForPosition(newPosition, size);

		if (bufferIndex != -1) {
			nbBufferOk++;
			return bufferIndex;
		}
		nbBufferNotOk++;

		// checks if there is any overlapping buffer
		overlappingBuffers = getOverlappingBuffers(newPosition, bufferSize);

		// Choose the first overlaping buffer
		bufferIndex = overlappingBuffers[0];

		if (nbBuffers > 1 && overlappingBuffers[1] != -1 && bufferIndex == currentBufferIndex) {
			bufferIndex = overlappingBuffers[1];
		}

		if (bufferIndex == -1) {
			bufferIndex = nextBufferIndex;
			nextBufferIndex = (nextBufferIndex + 1) % nbBuffers;
			if (bufferIndex == currentBufferIndex) {
				bufferIndex = nextBufferIndex;
				nextBufferIndex = (nextBufferIndex + 1) % nbBuffers;
			}
			flushBuffer(bufferIndex);
		}

		currentBufferIndex = bufferIndex;
		// TODO check length and getLength
		long length = getLength();

		if (readOrWrite == READ && newPosition >= length) {
			String message = "End Of File reached - position = " + newPosition + " : Length = " + length;
			DLogger.error(message);
			throw new ODBRuntimeException(NeoDatisError.END_OF_FILE_REACHED.addParameter(newPosition).addParameter(length));
		}

		// The buffer must be initialized with real data, so the first thing we
		// must do is read data from file and puts it in the array

		long nread = bufferSize;

		// if new position is in the file
		if (newPosition < length) {
			// We are in the file, we are updating content. to create the
			// buffer, we first read the content of the file
			goToPosition(newPosition);
			// Actually loads data from the file to the buffer
			nread = internalRead(multiBuffer.buffers[bufferIndex], bufferSize);
			multiBuffer.setCreationDate(bufferIndex,OdbTime.getCurrentTimeInMs());
		}else{
			goToPosition(newPosition);
		}

		
		long endPosition = -1;
		// If we are in READ, sets the size equal to what has been read
		if (readOrWrite == READ) {
			endPosition = newPosition + nread;
		} else {
			endPosition = newPosition + bufferSize;
		}
		multiBuffer.setPositions(bufferIndex, newPosition,endPosition,0);
		currentPositionWhenUsingBuffer = newPosition;

		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Creating buffer " + name + "-" + bufferIndex + " : " + "[" + multiBuffer.bufferStartPosition[bufferIndex] + ","
					+ multiBuffer.bufferEndPosition[bufferIndex]+ "]");
		}
		return bufferIndex;
	}

	/**
	 * Check if a new buffer starting at position with a size ='size' would
	 * overlap with an existing buffer
	 * 
	 * @param position
	 * @param size
	 * @return @
	 */
	private int[] getOverlappingBuffers(long position, int size) {

		long start1 = position;
		long end1 = position + size;

		long start2 = 0;
		long end2 = 0;

		int[] indexes = new int[nbBuffers];

		int index = 0;
		for (int i = 0; i < nbBuffers; i++) {
			start2 = multiBuffer.bufferStartPosition[i];
			end2 = multiBuffer.bufferEndPosition[i];

			if ((start1 >= start2 && start1 < end2) || (start2 >= start1 && start2 < end1) || start2 <= start1 && end2 >= end1) {
				// This buffer is overlapping the buffer
				indexes[index++] = i;
				// Flushes the buffer
				flushBuffer(i);
				nbFlushForOverlap++;
			}
		}
		for (int i = index; i < nbBuffers; i++) {
			indexes[i] = -1;
		}

		return indexes;
	}

	

	public boolean isUsingbuffer() {
		return isUsingBuffer;
	}

	public void setUseBuffer(boolean useBuffer) {
		// If we are using buffer, and the new useBuffer indicator if false
		// Then we need to flush all buffers
		if (isUsingBuffer && !useBuffer) {
			flushAllBuffers();
		}
		this.isUsingBuffer = useBuffer;
	}

	public long getCurrentPosition() {
		if (!isUsingBuffer) {
			return currentPositionForDirectWrite;
		}

		return currentPositionWhenUsingBuffer;
	}

	public void setCurrentWritePosition(long currentPosition) {
		if (isUsingBuffer) {
			if(this.currentPositionWhenUsingBuffer == currentPosition){
				nbSamePositionForWrite++;
				return;
			}
			this.currentPositionWhenUsingBuffer = currentPosition;
			//manageBufferForNewPosition(currentPosition, WRITE, 1);
		} else {
			this.currentPositionForDirectWrite = currentPosition;
			goToPosition(currentPosition);
		}
	}

	public void setCurrentReadPosition(long currentPosition) {
		if (isUsingBuffer) {
			if(this.currentPositionWhenUsingBuffer == currentPosition){
				nbSamePositionForRead++;
				return;
			}
			this.currentPositionWhenUsingBuffer = currentPosition;
			manageBufferForNewPosition(currentPosition, READ, 1);
		} else {
			this.currentPositionForDirectWrite = currentPosition;
			goToPosition(currentPosition);
		}
	}

	public void writeByte(byte b) {

		if (!isUsingBuffer) {
			goToPosition(currentPositionForDirectWrite);
			internalWrite(b);
			currentPositionForDirectWrite++;
			return;
		}

		int bufferIndex = multiBuffer.getBufferIndexForPosition(currentPositionWhenUsingBuffer, 1);
		if (bufferIndex == -1) {
			bufferIndex = manageBufferForNewPosition(currentPositionWhenUsingBuffer, WRITE, 1);
		}

		int positionInBuffer = (int) (currentPositionWhenUsingBuffer - multiBuffer.bufferStartPosition[bufferIndex]);
		multiBuffer.setByte(bufferIndex,positionInBuffer,b);
		currentPositionWhenUsingBuffer++;


		if (currentPositionWhenUsingBuffer > ioDeviceLength) {
			ioDeviceLength = currentPositionWhenUsingBuffer;
		}
	}

	public byte[] readBytesOld(int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = readByte();
		}
		return bytes;
	}

	public byte[] readBytes(int size) {
		byte[] bytes = new byte[size];

		if (!isUsingBuffer) {
			// If there is no buffer, simply read data
			goToPosition(currentPositionForDirectWrite);
			long realSize = internalRead(bytes, size);
			currentPositionForDirectWrite += realSize;
			return bytes;
		}

		// If the size to read in smaller than the buffer size
		if (size <= bufferSize) {
			return readBytes(bytes, 0, size);
		}

		// else the read have to use various buffers
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			DLogger.debug("Data is larger than buffer size " + bytes.length + " > " + bufferSize + " : cutting the data");
		}
		int nbBuffersNeeded = bytes.length / bufferSize + 1;
		int currentStart = 0;
		int currentEnd = bufferSize;
		for (int i = 0; i < nbBuffersNeeded; i++) {
			readBytes(bytes, currentStart, currentEnd);
			currentStart += bufferSize;
			if (currentEnd + bufferSize < bytes.length) {
				currentEnd += bufferSize;
			} else {
				currentEnd = bytes.length;
			}
		}
		return bytes;

	}

	public byte[] readBytes(byte[] bytes, int startIndex, int endIndex) {
		int size = endIndex - startIndex;
		int bufferIndex = manageBufferForNewPosition(currentPositionWhenUsingBuffer, READ, size);

		int start = (int) (currentPositionWhenUsingBuffer - multiBuffer.bufferStartPosition[bufferIndex]);

		byte[] buffer = multiBuffer.buffers[bufferIndex];
		System.arraycopy(buffer, start, bytes, startIndex, size);
		
		//for (int i = 0; i < size; i++) {
		//	bytes[startIndex + i] = buffer[start + i];
		//}
		
		
		
		currentPositionWhenUsingBuffer += size;

		return bytes;
	}

	public byte readByte() {

		if (!isUsingBuffer) {
			goToPosition(currentPositionForDirectWrite);
			byte b = internalRead();
			currentPositionForDirectWrite++;
			return b;
		}

		int bufferIndex = manageBufferForNewPosition(currentPositionWhenUsingBuffer, READ, 1);

		byte byt = multiBuffer.getByte(bufferIndex, (int) (currentPositionWhenUsingBuffer - multiBuffer.bufferStartPosition[bufferIndex]));
		currentPositionWhenUsingBuffer++;

		return byt;
	}

	public void writeBytes(byte[] bytes) {

		if (bytes.length > bufferSize) {
			// throw new ODBRuntimeException(session,"The buffer has a size of "
			// + bufferSize + " but there exist data with " + bytes.length +
			// " size! - please set manually the odb data buffer to a greater value than "
			// + bytes.length +
			// " using Configuration.setDefaultBufferSizeForData(int)");
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("Data is larger than buffer size " + bytes.length + " > " + bufferSize + " : cutting the data");
			}
			int nbBuffersNeeded = bytes.length / bufferSize + 1;
			int currentStart = 0;
			int currentEnd = bufferSize;
			for (int i = 0; i < nbBuffersNeeded; i++) {
				writeBytes(bytes, currentStart, currentEnd);
				currentStart += bufferSize;
				if (currentEnd + bufferSize < bytes.length) {
					currentEnd += bufferSize;
				} else {
					currentEnd = bytes.length;
				}
			}
		} else {
			writeBytes(bytes, 0, bytes.length);
		}
	}

	public void writeBytes(byte[] bytes, int startIndex, int endIndex) {

		if (!isUsingBuffer) {
			goToPosition(currentPositionForDirectWrite);
			internalWrite(bytes, bytes.length);
			currentPositionForDirectWrite += bytes.length;
			return;
		}

		int lengthToCopy = endIndex - startIndex;
		nbWrites++;
		totalWriteSize += lengthToCopy;
		int bufferIndex = manageBufferForNewPosition(currentPositionWhenUsingBuffer, WRITE, lengthToCopy);

		int positionInBuffer = (int) (currentPositionWhenUsingBuffer - multiBuffer.bufferStartPosition[bufferIndex]);

		// Here, the bytes.length seems to have an average value lesser that 70,
		// and in this
		// It is faster to copy using System.arraycopy
		// see org.neodatis.odb.test.performance.TestArrayCopy	
		multiBuffer.writeBytes(bufferIndex,bytes,startIndex,positionInBuffer,lengthToCopy);
		positionInBuffer = positionInBuffer + lengthToCopy - 1;

		currentPositionWhenUsingBuffer += lengthToCopy;

		if (currentPositionWhenUsingBuffer > ioDeviceLength) {
			ioDeviceLength = currentPositionWhenUsingBuffer;
		}
	}

	public void flushAllBuffers() {
		for (int i = 0; i < nbBuffers; i++) {
			flushBuffer(i);
		}
		
	}

	public void flushBuffer(int bufferIndex) {

		byte[] buffer = multiBuffer.buffers[bufferIndex];
		if (buffer != null && multiBuffer.hasBeenUsedForWrite(bufferIndex)) {
			goToPosition(multiBuffer.bufferStartPosition[bufferIndex]);
			// the +1 is because the maxPositionInBuffer is a position and the
			// parameter is a length
			int bufferSizeToFlush = multiBuffer.maxPositionInBuffer[bufferIndex] + 1;
			internalWrite(buffer, bufferSizeToFlush);
			numberOfFlush++;
			totalFlushSize += bufferSizeToFlush;

			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("Flushing buffer " + name + "-" + bufferIndex + " : [" + multiBuffer.bufferStartPosition[bufferIndex] + ":"
						+ multiBuffer.bufferEndPosition[bufferIndex] + "] - flush size=" + bufferSizeToFlush + "  flush number = " + numberOfFlush);
			}
			multiBuffer.clearBuffer(bufferIndex);
		} else {
			if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
				DLogger.debug("Flushing buffer " + name + "-" + bufferIndex + " : [" + multiBuffer.bufferStartPosition[bufferIndex] + ":"
						+ multiBuffer.bufferEndPosition[bufferIndex] + "] - Nothing to flush!");
			}
			multiBuffer.clearBuffer(bufferIndex);
		}
	}

	/**
	 * @return Returns the numberOfFlush.
	 */
	public long getNumberOfFlush() {
		return numberOfFlush;
	}

	public long getIoDeviceLength() {
		return ioDeviceLength;
	}

	public void setIoDeviceLength(long ioDeviceLength) {
		this.ioDeviceLength = ioDeviceLength;
	}

	public void close() {
		clear();
		closeIO();
	}

	public void clear() {
		flushAllBuffers();
		multiBuffer.clear();
		multiBuffer = null;
		overlappingBuffers = null;
	}

	public boolean isForTransaction() {
		return name != null && name.equals("transaction");
	}

	public void enableAutomaticDelete(boolean yesOrNo) {
		this.enableAutomaticDelete = yesOrNo;
	}

	public boolean automaticDeleteIsEnabled() {
		return enableAutomaticDelete;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Buffers=").append("currbuffer=").append(currentBufferIndex).append(" : \n");
		for (int i = 0; i < nbBuffers; i++) {
			buffer.append(i).append(":[").append(multiBuffer.bufferStartPosition[i]).append(",").append(multiBuffer.bufferEndPosition[i]).append("] : write=")
					.append(multiBuffer.hasBeenUsedForWrite(i)).append(" - when=").append(multiBuffer.getCreationDate(i));
			if (i + 1 < nbBuffers) {
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
}