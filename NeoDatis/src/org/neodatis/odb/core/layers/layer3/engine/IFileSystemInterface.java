package org.neodatis.odb.core.layers.layer3.engine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IBufferedIO;

public interface IFileSystemInterface {

	public abstract void useBuffer(boolean useBuffer) ;

	public abstract void flush() ;

	public abstract long getPosition() ;

	public abstract long getLength() ;

	/**
	 * Does the same thing than setWritePosition, but do not control write position
	 * @param position
	 * @param writeInTransacation
	 * 
	 */
	public abstract void setWritePositionNoVerification(long position, boolean writeInTransacation) ;

	public abstract void setWritePosition(long position, boolean writeInTransacation) ;

	public abstract void setReadPosition(long position) ;

	public abstract long getAvailablePosition() ;

	public abstract void ensureSpaceFor(ODBType type) ;

	public abstract void writeByte(byte i, boolean writeInTransaction) ;

	public abstract void writeByte(byte i, boolean writeInTransaction, String label) ;

	public abstract byte readByte() ;

	public abstract byte readByte(String label) ;

	public abstract void writeBytes(byte[] bytes, boolean writeInTransaction, String label) ;

	public abstract byte[] readBytes(int length) ;

	public abstract void writeChar(char c, boolean writeInTransaction) ;

	public abstract byte[] readCharBytes() ;

	public abstract char readChar() ;

	public abstract char readChar(String label) ;

	public abstract void writeShort(short s, boolean writeInTransaction) ;

	public abstract byte[] readShortBytes() ;

	public abstract short readShort() ;

	public abstract short readShort(String label) ;

	public abstract void writeInt(int i, boolean writeInTransaction, String label) ;

	public abstract byte[] readIntBytes() ;

	public abstract int readInt() ;

	public abstract int readInt(String label) ;

	public abstract void writeLong(long i, boolean writeInTransaction, String label, int writeActionType) ;

	public abstract byte[] readLongBytes() ;

	public abstract long readLong() ;

	public abstract long readLong(String label) ;

	public abstract void writeFloat(float f, boolean writeInTransaction) ;

	public abstract byte[] readFloatBytes() ;

	public abstract float readFloat() ;

	public abstract float readFloat(String label) ;

	public abstract void writeDouble(double d, boolean writeInTransaction) ;

	public abstract byte[] readDoubleBytes() ;

	public abstract double readDouble() ;

	public abstract double readDouble(String label) ;

	public abstract void writeBigDecimal(BigDecimal d, boolean writeInTransaction) ;

	public abstract byte[] readBigDecimalBytes() ;

	public abstract BigDecimal readBigDecimal() ;

	public abstract BigDecimal readBigDecimal(String label) ;

	public abstract void writeBigInteger(BigInteger d, boolean writeInTransaction) ;

	public abstract byte[] readBigIntegerBytes(boolean hasSize) ;

	public abstract BigInteger readBigInteger() ;

	public abstract BigInteger readBigInteger(String label) ;

	public abstract void writeDate(Date d, boolean writeInTransaction) ;

	public abstract byte[] readDateBytes() ;

	public abstract Date readDate() ;

	public abstract Date readDate(String label) ;

	public abstract void writeString(String s, boolean writeInTransaction, boolean useEncoding) ;

	public abstract void writeString(String s, boolean writeInTransaction, boolean useEncoding, int totalSpace) ;

	public abstract byte[] readStringBytes(boolean withSize) ;

	public abstract String readString(boolean useEncoding) ;

	public abstract String readString(boolean useEncoding, String label) ;

	public abstract void writeBoolean(boolean b, boolean writeInTransaction) ;

	public abstract void writeBoolean(boolean b, boolean writeInTransaction, String label) ;

	public abstract byte[] readBooleanBytes() ;

	public abstract boolean readBoolean() ;

	public abstract boolean readBoolean(String label) ;

	public abstract byte[] readNativeAttributeBytes(int attributeType);

	public abstract void close() ;

	public abstract void clear();

	/**
	 * @return Returns the parameters.
	 */
	public abstract IBaseIdentification getParameters();

	public abstract boolean delete() ;

	public abstract IBufferedIO getIo();

	public abstract void setDatabaseCharacterEncoding(String databaseCharacterEncoding);

}