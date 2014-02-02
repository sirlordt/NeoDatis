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

/**A simple class to store bytes
 * @author olivier
 * @sharpen.ignore
 *
 */
public class MultiBufferVO {
	/** The number of buffers*/
	private int numberOfBuffers;
	/** The buffer size*/
	private int bufferSize;
	public byte [][] buffers;
	
	protected long[] creations;
	/** The current start position of the buffer */
	public long[] bufferStartPosition;

	/** The current end position of the buffer */
	public long[] bufferEndPosition;

	/**
	 * The max position in the buffer, used to optimize the flush - to flush
	 * only new data and not all the buffer
	 */
	public int[] maxPositionInBuffer;

	/** To know if buffer has been used for write - to speedup flush */
	private boolean[] bufferHasBeenUsedForWrite;
	
	public MultiBufferVO(int numberOfBuffers, int bufferSize){
		this.numberOfBuffers = numberOfBuffers;
		this.bufferSize = bufferSize;
		buffers = new byte[numberOfBuffers][bufferSize];
		
		bufferStartPosition = new long[numberOfBuffers];
		bufferEndPosition = new long[numberOfBuffers];
		maxPositionInBuffer = new int[numberOfBuffers];
		creations = new long[numberOfBuffers];
		bufferHasBeenUsedForWrite = new boolean[numberOfBuffers];
	}
	
	public byte[] getBuffer2(int index){
		return buffers[index];
	}
	public byte getByte(int bufferIndex, int byteIndex){
		return buffers[bufferIndex][byteIndex];
	}

	/**
	 * @param i
	 */
	public void clearBuffer(int bufferIndex) {
		byte [] buffer = buffers[bufferIndex];
		int maxPosition = maxPositionInBuffer[bufferIndex];
		for (int i = 0; i < maxPosition; i++) {
			buffer[i] = 0;
		}
		bufferStartPosition[bufferIndex]=0;
		bufferEndPosition[bufferIndex]=0;
		maxPositionInBuffer[bufferIndex]=0;
		bufferHasBeenUsedForWrite[bufferIndex]=false;
	}

	/**
	 * @param bufferIndex
	 * @param positionInBuffer
	 * @param b
	 */
	public void setByte(int bufferIndex, int positionInBuffer, byte b) {
		if(buffers[bufferIndex]==null){
			buffers[bufferIndex] = new byte[bufferSize];
		}
		buffers[bufferIndex][positionInBuffer] = b;
		bufferHasBeenUsedForWrite[bufferIndex] = true;
		if (positionInBuffer > maxPositionInBuffer[bufferIndex]) {
			maxPositionInBuffer[bufferIndex] = positionInBuffer;
		}
	}
	
	public int getBufferIndexForPosition(long position, int size) {
		long max = position + size;
			
		for (int i = 0; i < numberOfBuffers; i++) {
			// Check if new position is in buffer
			if (max <= bufferEndPosition[i] && position >= bufferStartPosition[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param bufferIndex
	 * @param currentTimeInMs
	 */
	public void setCreationDate(int bufferIndex, long currentTimeInMs) {
		creations[bufferIndex] = currentTimeInMs;
		
	}

	/**
	 * @param bufferIndex
	 * @param newPosition
	 * @param endPosition
	 * @param i
	 */
	public void setPositions(int bufferIndex, long startPosition, long endPosition, int maxPosition) {
		bufferStartPosition[bufferIndex] = startPosition;
		bufferEndPosition[bufferIndex] = endPosition;
		maxPositionInBuffer[bufferIndex] = maxPosition;
	}
	

	private void clear(int bufferIndex, int position) {
		/*
		 * if (buffer == null) { buffer = new byte[bufferSize]; return; }
		 */
		byte[] buffer = buffers[bufferIndex];
		for (int i = 0; i < position; i++) {
			buffer[i] = 0;
		}
		bufferStartPosition[bufferIndex] = 0;
		bufferEndPosition[bufferIndex] = 0;
		maxPositionInBuffer[bufferIndex] = 0;
		bufferHasBeenUsedForWrite[bufferIndex] = false;

	}

	/**
	 * @param bufferIndex
	 * @param bytes
	 * @param startIndex
	 * @param i
	 * @param lengthToCopy
	 */
	public void writeBytes(int bufferIndex, byte[] bytes, int startIndex, int offsetWhereToCopy, int lengthToCopy) {
		System.arraycopy(bytes, startIndex, buffers[bufferIndex], offsetWhereToCopy, lengthToCopy);
		bufferHasBeenUsedForWrite[bufferIndex] = true;
		
		int positionInBuffer = offsetWhereToCopy + lengthToCopy - 1;
		if (positionInBuffer > maxPositionInBuffer[bufferIndex]) {
			maxPositionInBuffer[bufferIndex] = positionInBuffer;
		}
		
	}

	/**
	 * @param bufferIndex
	 * @return
	 */
	public boolean hasBeenUsedForWrite(int bufferIndex) {
		return bufferHasBeenUsedForWrite[bufferIndex];
	}

	/**
	 * 
	 */
	public void clear() {
		
		buffers = null;
		bufferStartPosition = null;
		bufferEndPosition = null;
		maxPositionInBuffer = null;
		bufferHasBeenUsedForWrite = null;
	}

	/**
	 * @param i
	 * @return
	 */
	public long getCreationDate(int bufferIndex) {
		return creations[bufferIndex];
	}
	
}
