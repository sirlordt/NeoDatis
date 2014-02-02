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
package org.neodatis.odb.core.layers.layer3.engine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IBufferedIO;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.DisplayUtility;
import org.neodatis.tool.wrappers.OdbTime;

/**
 * Class that knows how to read/write all language native types : byte, char,
 * String, int, long,....
 * 
 * @author osmadja
 * 
 */
public abstract class FileSystemInterface implements IFileSystemInterface {

	public void setDatabaseCharacterEncoding(String databaseCharacterEncoding) {
		byteArrayConverter
				.setDatabaseCharacterEncoding(databaseCharacterEncoding);
	}

	private static final int INT_SIZE = ODBType.INTEGER.getSize();
	private static final int INT_SIZE_x_2 = INT_SIZE * 2;

	public static int nbCall1;
	public static int nbCall2;

	public static final String LOG_ID = "FileSystemInterface";

	private String name;

	private boolean canLog;

	private IBufferedIO io;

	protected IBaseIdentification parameters;

	protected IByteArrayConverter byteArrayConverter;

	private static final byte RESERVED_SPACE = (byte) 128;

	public FileSystemInterface(String name, String fileName, boolean canWrite,
			boolean canLog, int bufferSize) {
		this(name, new IOFileParameter(fileName, canWrite,null,null), canLog, bufferSize);
	}

	public FileSystemInterface(String name, IBaseIdentification parameters,
			boolean canLog, int bufferSize) {
		this.name = name;
		this.parameters = parameters;
		this.canLog = canLog;

		ICoreProvider provider = OdbConfiguration.getCoreProvider();
		this.io = provider.getIO(name, parameters, bufferSize);
		this.byteArrayConverter = provider.getByteArrayConverter();

	}

	public abstract ISession getSession();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#useBuffer
	 * (boolean)
	 */
	public void useBuffer(boolean useBuffer) {
		io.setUseBuffer(useBuffer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#flush()
	 */
	public void flush() {
		io.flushAllBuffers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#getPosition
	 * ()
	 */
	public long getPosition() {
		return io.getCurrentPosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#getLength
	 * ()
	 */
	public long getLength() {
		return io.getLength();
	}

	/**
	 * Writing at position < DATABASE_HEADER_PROTECTED_ZONE_SIZE is writing in
	 * ODB Header place. Here we check the positions where the writing is done.
	 * Search for 'page format' in ODB wiki to understand the positions
	 * 
	 * @param position
	 * @return
	 */
	boolean isWritingInWrongPlace(long position) {
		if (position < StorageEngineConstant.DATABASE_HEADER_PROTECTED_ZONE_SIZE) {
			int size = StorageEngineConstant.DATABASE_HEADER_POSITIONS.length;

			for (int i = 0; i < size; i++) {
				if (position == StorageEngineConstant.DATABASE_HEADER_POSITIONS[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * setWritePositionNoVerification(long, boolean)
	 */
	public void setWritePositionNoVerification(long position,
			boolean writeInTransacation) {
		io.setCurrentWritePosition(position);
		if (writeInTransacation) {
			getSession().getTransaction().setWritePosition(position);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * setWritePosition(long, boolean)
	 */
	public void setWritePosition(long position, boolean writeInTransacation) {
		if (position < StorageEngineConstant.DATABASE_HEADER_PROTECTED_ZONE_SIZE) {
			if (isWritingInWrongPlace(position)) {
				throw new ODBRuntimeException(
						NeoDatisError.INTERNAL_ERROR
								.addParameter("Trying to write in Protected area at position "
										+ position));
			}
		}

		io.setCurrentWritePosition(position);
		if (writeInTransacation) {
			getSession().getTransaction().setWritePosition(position);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * setReadPosition(long)
	 */
	public void setReadPosition(long position) {
		io.setCurrentReadPosition(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * getAvailablePosition()
	 */
	public long getAvailablePosition() {
		return io.getLength();
	}

	private boolean pointerAtTheEndOfTheFile() {
		return io.getCurrentPosition() == io.getLength();
	}

	/**
	 * Reserve space in the file when it is at the end of the file Used in
	 * transaction mode where real write will happen later
	 * 
	 * @param quantity
	 *            The number of object to reserve space for
	 * @param type
	 *            The type of the object to reserve space for
	 * 
	 */
	private void ensureSpaceFor(long quantity, ODBType type) {
		long space = type.getSize() * quantity;
		// We are in transaction mode - do not write just reserve space if
		// necessary
		// ensure space will be available when applying transaction
		if (pointerAtTheEndOfTheFile()) {
			if (space != 1) {
				io.setCurrentWritePosition(io.getCurrentPosition() + space - 1);
			}
			io.writeByte(RESERVED_SPACE);
			if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
				// DLogger.debug("Reserving " + space + " bytes (" + quantity +
				// " " + type.getName() + ")");
			}
		} else {
			// We must simulate the move
			io.setCurrentWritePosition(io.getCurrentPosition() + space);
		}
		/*
		 * if (Configuration.isDebugEnabled(LOG_ID) && canLog) { //
		 * DLogger.debug("File Size after write " + io.getCurrentPosition() + "
		 * | length = " + io.getLength()); }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * ensureSpaceFor(org.neodatis.odb.core.layers.layer2.meta.ODBType)
	 */
	public void ensureSpaceFor(ODBType type) {
		ensureSpaceFor(1, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeByte
	 * (byte, boolean)
	 */
	public void writeByte(byte i, boolean writeInTransaction) {
		writeByte(i, writeInTransaction, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeByte
	 * (byte, boolean, java.lang.String)
	 */
	public void writeByte(byte i, boolean writeInTransaction, String label) {
		byte[] bytes = { i };

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("writing byte " + i + " at " + getPosition()
					+ (label != null ? " : " + label : ""));
		}

		if (!writeInTransaction) {
			io.writeByte(i);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_BYTE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readByte
	 * ()
	 */
	public byte readByte() {
		return readByte(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readByte
	 * (java.lang.String)
	 */
	public byte readByte(String label) {
		long position = io.getCurrentPosition();
		byte i = io.readByte();
		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("reading byte " + i + " at " + position
					+ (label != null ? " : " + label : ""));
		}
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeBytes
	 * (byte[], boolean)
	 */
	public void writeBytes(byte[] bytes, boolean writeInTransaction, String label) {
		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("writing " +bytes.length+" bytes at " + getPosition()+  (label != null ? " : " + label : "")+ " = "+ DisplayUtility.byteArrayToString(bytes));
		}
		
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(bytes.length, ODBType.NATIVE_BYTE);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readBytes
	 * (int)
	 */
	public byte[] readBytes(int length) {
		long position = io.getCurrentPosition();
		byte[] bytes = io.readBytes(length);
		int byteCount = bytes.length;
		if (byteCount != length) {
			throw new ODBRuntimeException(NeoDatisError.FILE_INTERFACE_READ_ERROR
					.addParameter(length).addParameter(position).addParameter(
							byteCount));
		}
		return bytes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeChar
	 * (char, boolean)
	 */
	public void writeChar(char c, boolean writeInTransaction) {
		byte[] bytes = byteArrayConverter.charToByteArray(c);
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_CHAR);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readCharBytes
	 * ()
	 */
	public byte[] readCharBytes() {
		return io.readBytes(ODBType.CHARACTER.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readChar
	 * ()
	 */
	public char readChar() {
		return readChar(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readChar
	 * (java.lang.String)
	 */
	public char readChar(String label) {
		long position = io.getCurrentPosition();
		char c = byteArrayConverter.byteArrayToChar(readCharBytes());

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog && label != null) {
			DLogger.debug("reading char " + c + " at " + position + " : "
					+ label);
		}

		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeShort
	 * (short, boolean)
	 */
	public void writeShort(short s, boolean writeInTransaction) {
		byte[] bytes = byteArrayConverter.shortToByteArray(s);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("writing short " + s + " at " + getPosition());
		}
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_SHORT);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readShortBytes()
	 */
	public byte[] readShortBytes() {
		return io.readBytes(ODBType.NATIVE_SHORT.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readShort
	 * ()
	 */
	public short readShort() {
		return readShort(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readShort
	 * (java.lang.String)
	 */
	public short readShort(String label) {
		long position = io.getCurrentPosition();
		short s = byteArrayConverter.byteArrayToShort(readShortBytes());
		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog && label != null) {
			DLogger.debug("reading short " + s + " at " + position + " : "
					+ label);
		}

		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeInt
	 * (int, boolean, java.lang.String)
	 */
	public void writeInt(int i, boolean writeInTransaction, String label) {
		byte[] bytes = byteArrayConverter.intToByteArray(i);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("writing int " + i + " at " + getPosition() + " : "
					+ label);
		}

		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_INT);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readIntBytes
	 * ()
	 */
	public byte[] readIntBytes() {
		return io.readBytes(ODBType.INTEGER.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readInt()
	 */
	public int readInt() {
		return readInt(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readInt
	 * (java.lang.String)
	 */
	public int readInt(String label) {
		long position = io.getCurrentPosition();

		int i = byteArrayConverter.byteArrayToInt(readIntBytes(), 0);
		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("reading int " + i + " at " + position
					+ (label != null ? " : " + label : ""));
		}
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeLong
	 * (long, boolean, java.lang.String, int)
	 */
	public void writeLong(long i, boolean writeInTransaction, String label,
			int writeActionType) {
		byte[] bytes = byteArrayConverter.longToByteArray(i);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog && label != null) {
			DLogger.debug("writing long " + i + " at " + getPosition() + " : "
					+ label);
		}
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_LONG);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readLongBytes
	 * ()
	 */
	public byte[] readLongBytes() {
		return io.readBytes(ODBType.LONG.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readLong
	 * ()
	 */
	public long readLong() {
		return readLong(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readLong
	 * (java.lang.String)
	 */
	public long readLong(String label) {
		long position = io.getCurrentPosition();
		long l = byteArrayConverter.byteArrayToLong(readLongBytes(), 0);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("reading long " + l + " at " + position
					+ (label != null ? " : " + label : ""));
		}

		return l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeFloat
	 * (float, boolean)
	 */
	public void writeFloat(float f, boolean writeInTransaction) {
		byte[] bytes = byteArrayConverter.floatToByteArray(f);
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_FLOAT);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readFloatBytes()
	 */
	public byte[] readFloatBytes() {
		return io.readBytes(ODBType.FLOAT.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readFloat
	 * ()
	 */
	public float readFloat() {
		return readFloat(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readFloat
	 * (java.lang.String)
	 */
	public float readFloat(String label) {
		long position = io.getCurrentPosition();
		float f = byteArrayConverter.byteArrayToFloat(readFloatBytes());

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("Reading float '" + f + "' at " + position
					+ (label != null ? " : " + label : ""));
		}

		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeDouble
	 * (double, boolean)
	 */
	public void writeDouble(double d, boolean writeInTransaction) {
		byte[] bytes = byteArrayConverter.doubleToByteArray(d);
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_DOUBLE);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readDoubleBytes()
	 */
	public byte[] readDoubleBytes() {
		return io.readBytes(ODBType.DOUBLE.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readDouble
	 * ()
	 */
	public double readDouble() {
		return readDouble(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readDouble
	 * (java.lang.String)
	 */
	public double readDouble(String label) {
		long position = io.getCurrentPosition();
		double d = byteArrayConverter.byteArrayToDouble(readDoubleBytes());

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("Reading double '" + d + "' at " + position
					+ (label != null ? " : " + label : ""));
		}

		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * writeBigDecimal(java.math.BigDecimal, boolean)
	 */
	public void writeBigDecimal(BigDecimal d, boolean writeInTransaction) {
		byte[] bytes = byteArrayConverter.bigDecimalToByteArray(d, true);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("writing BigDecimal " + d + " at " + getPosition());
		}
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(bytes.length, ODBType.BIG_DECIMAL);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readBigDecimalBytes()
	 */
	public byte[] readBigDecimalBytes() {
		return readStringBytes(false);
		// return BigDecimal(io.readBytes(ODBType.BIG_DECIMAL.getSize()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readBigDecimal()
	 */
	public BigDecimal readBigDecimal() {
		return readBigDecimal(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readBigDecimal(java.lang.String)
	 */
	public BigDecimal readBigDecimal(String label) {
		long position = io.getCurrentPosition();
		BigDecimal d = byteArrayConverter.byteArrayToBigDecimal(
				readBigDecimalBytes(), false);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("Reading bigDecimal '" + d + "' at " + position
					+ (label != null ? " : " + label : ""));
		}

		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * writeBigInteger(java.math.BigInteger, boolean)
	 */
	public void writeBigInteger(BigInteger d, boolean writeInTransaction) {
		byte[] bytes = byteArrayConverter.bigIntegerToByteArray(d, true);
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(bytes.length, ODBType.BIG_INTEGER);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readBigIntegerBytes(boolean)
	 */
	public byte[] readBigIntegerBytes(boolean hasSize) {
		return readStringBytes(hasSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readBigInteger()
	 */
	public BigInteger readBigInteger() {
		return readBigInteger(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readBigInteger(java.lang.String)
	 */
	public BigInteger readBigInteger(String label) {

		long position = io.getCurrentPosition();
		BigInteger d = byteArrayConverter.byteArrayToBigInteger(
				readBigIntegerBytes(true), true);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("Reading bigInteger '" + d + "' at " + position
					+ (label != null ? " : " + label : ""));
		}

		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeDate
	 * (java.util.Date, boolean)
	 */
	public void writeDate(Date d, boolean writeInTransaction) {
		byte[] bytes = byteArrayConverter.dateToByteArray(d);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("writing Date " + OdbTime.getMilliseconds(d) + " at "
					+ getPosition());
		}

		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.DATE);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readDateBytes
	 * ()
	 */
	public byte[] readDateBytes() {
		return io.readBytes(ODBType.DATE.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readDate
	 * ()
	 */
	public Date readDate() {
		return readDate(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readDate
	 * (java.lang.String)
	 */
	public Date readDate(String label) {
		long position = io.getCurrentPosition();
		Date date = byteArrayConverter.byteArrayToDate(readDateBytes());

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			DLogger.debug("Reading date '" + date + "' at " + position
					+ (label != null ? " : " + label : ""));
		}

		return date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeString
	 * (java.lang.String, boolean)
	 */
	public void writeString(String s, boolean writeInTransaction,
			boolean useEncoding) {
		writeString(s, writeInTransaction, useEncoding, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeString
	 * (java.lang.String, boolean, int)
	 */
	public void writeString(String s, boolean writeInTransaction,
			boolean useEncoding, int totalSpace) {
		byte[] bytes = byteArrayConverter.stringToByteArray(s, true,
				totalSpace, useEncoding);
		if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
			long position = getPosition();
			DLogger.debug("Writing string '" + s + "' at " + position
					+ " size=" + bytes.length + " bytes");
		}
		if (!writeInTransaction) {
			long startPosition = io.getCurrentPosition();
			io.writeBytes(bytes);
			long endPosition = io.getCurrentPosition();
			if (OdbConfiguration.isEnableAfterWriteChecking()) {
				// To check the write
				io.setCurrentWritePosition(startPosition);
				String s2 = readString(useEncoding);
				// DLogger.debug("s1 : " + s.length() + " = " + s + "\ts2 : " +
				// s2.length() + " = " + s2);
				// FIXME replace RuntimeException by a ODBRuntimeException with
				// an Error constant
				throw new RuntimeException("error while writing string at "
						+ startPosition + " :  " + s
						+ " / check after writing =" + s2);
			}
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(bytes.length, ODBType.STRING);
		}
		bytes = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readStringBytes(boolean)
	 */
	public byte[] readStringBytes(boolean withSize) {
		if (withSize) {
			byte[] sizeBytes = io.readBytes(INT_SIZE_x_2);
			int totalSize = byteArrayConverter.byteArrayToInt(sizeBytes, 0);
			// Use offset of int size to read real size
			int stringSize = byteArrayConverter.byteArrayToInt(sizeBytes,
					INT_SIZE);
			byte[] bytes = readBytes(stringSize);
			nbCall2++;
			// Reads extra bytes
			byte[] extraBytes = readBytes(totalSize - stringSize);
			byte[] bytes2 = new byte[stringSize + INT_SIZE_x_2];
			for (int i = 0; i < INT_SIZE_x_2; i++) {
				bytes2[i] = sizeBytes[i];
			}
			for (int i = 0; i < bytes.length; i++) {
				bytes2[i + 8] = bytes[i];
			}
			extraBytes = null;
			sizeBytes = null;
			return bytes2;

		}

		byte[] sizeBytesNoSize = io.readBytes(INT_SIZE_x_2);
		int stringSizeNoSize = byteArrayConverter.byteArrayToInt(sizeBytesNoSize, INT_SIZE);
		byte[] bytesNoSize = readBytes(stringSizeNoSize);
		nbCall1++;
		sizeBytesNoSize = null;
		return bytesNoSize;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readString
	 * ()
	 */
	public String readString(boolean useEncoding) {
		return readString(useEncoding, OdbConfiguration.getDatabaseCharacterEncoding());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readString
	 * (java.lang.String)
	 */
	public String readString(boolean useEncoding, String label) {
		String s = byteArrayConverter.byteArrayToString(readStringBytes(true),
				true, useEncoding);
		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog) {
			long startPosition = io.getCurrentPosition();
			DLogger.debug("Reading string '" + s + "' at " + startPosition
					+ (label != null ? " : " + label : ""));
		}
		return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeBoolean
	 * (boolean, boolean)
	 */
	public void writeBoolean(boolean b, boolean writeInTransaction) {
		writeBoolean(b, writeInTransaction, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#writeBoolean
	 * (boolean, boolean, java.lang.String)
	 */
	public void writeBoolean(boolean b, boolean writeInTransaction, String label) {

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog && label != null) {
			DLogger.debug("writing boolean " + b + " at " + getPosition()
					+ " : " + label);
		}

		byte[] bytes = byteArrayConverter.booleanToByteArray(b);
		if (!writeInTransaction) {
			io.writeBytes(bytes);
		} else {
			getSession().getTransaction().manageWriteAction(
					io.getCurrentPosition(), bytes);
			ensureSpaceFor(ODBType.NATIVE_BOOLEAN);
		}
		bytes = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readBooleanBytes()
	 */
	public byte[] readBooleanBytes() {
		return io.readBytes(ODBType.BOOLEAN.getSize());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readBoolean
	 * ()
	 */
	public boolean readBoolean() {
		return readBoolean(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#readBoolean
	 * (java.lang.String)
	 */
	public boolean readBoolean(String label) {
		long position = io.getCurrentPosition();

		boolean b = byteArrayConverter.byteArrayToBoolean(readBooleanBytes(),0);

		if (OdbConfiguration.isDebugEnabled(LOG_ID) && canLog && label != null) {
			DLogger.debug("reading boolean " + b + " at " + position + " : "
					+ label);
		}

		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#
	 * readNativeAttributeBytes(int)
	 */
	public byte[] readNativeAttributeBytes(int attributeType) {
		switch (attributeType) {
		case ODBType.NATIVE_BYTE_ID:
			byte[] bytes = new byte[1];
			bytes[0] = readByte();
			return bytes;
		case ODBType.NATIVE_BOOLEAN_ID:
			return readBooleanBytes();
		case ODBType.NATIVE_CHAR_ID:
			return readCharBytes();
		case ODBType.NATIVE_FLOAT_ID:
			return readFloatBytes();
		case ODBType.NATIVE_DOUBLE_ID:
			return readDoubleBytes();
		case ODBType.NATIVE_INT_ID:
			return readIntBytes();
		case ODBType.NATIVE_LONG_ID:
			return readLongBytes();
		case ODBType.NATIVE_SHORT_ID:
			return readShortBytes();
		case ODBType.BIG_DECIMAL_ID:
			return readBigDecimalBytes();
		case ODBType.BIG_INTEGER_ID:
			return readBigIntegerBytes(true);
		case ODBType.BOOLEAN_ID:
			return readBooleanBytes();
		case ODBType.CHARACTER_ID:
			return readCharBytes();
			
		case ODBType.DATE_ID:
		case ODBType.DATE_SQL_ID:
		case ODBType.DATE_TIMESTAMP_ID:			
		case ODBType.DATE_CALENDAR_ID:
		case ODBType.DATE_GREGORIAN_CALENDAR_ID:
			return readDateBytes();
			
		case ODBType.FLOAT_ID:
			return readFloatBytes();
		case ODBType.DOUBLE_ID:
			return readDoubleBytes();
		case ODBType.INTEGER_ID:
			return readIntBytes();
		case ODBType.STRING_ID:
			return readStringBytes(true);

		default:
			throw new ODBRuntimeException(NeoDatisError.NATIVE_TYPE_NOT_SUPPORTED
					.addParameter(attributeType).addParameter(""));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#close()
	 */
	public void close() {
		clear();
		io.close();
		io = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#clear()
	 */
	public void clear() {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#getParameters
	 * ()
	 */
	public IBaseIdentification getParameters() {
		return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#delete()
	 */
	public boolean delete() {
		return io.delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface#getIo()
	 */
	public IBufferedIO getIo() {
		return io;
	}

}
