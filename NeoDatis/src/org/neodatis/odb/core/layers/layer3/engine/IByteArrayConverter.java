package org.neodatis.odb.core.layers.layer3.engine;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.neodatis.odb.core.ITwoPhaseInit;

public interface IByteArrayConverter extends ITwoPhaseInit {

	public abstract byte[] booleanToByteArray(boolean b);

	public abstract boolean byteArrayToBoolean(byte[] bytes, int offset);

	public abstract byte[] shortToByteArray(short s);

	public abstract short byteArrayToShort(byte[] bytes);

	public abstract byte[] charToByteArray(char c);

	public abstract char byteArrayToChar(byte[] bytes);

	public abstract int getNumberOfBytesOfAString(String s, boolean useEncoding);

	/**
	 * 
	 * @param s
	 * @param withSize if true, returns an array with an initial int with its size
	 * @param totalSpace The total space of the string (can be bigger that the real string size - to support later in place update)
	 * @param withEncoding To specify if SPECIFIC encoding must be used
	 * @return The byte array that represent the string
	 */
	public abstract byte[] stringToByteArray(String s, boolean withSize, int totalSpace, boolean withEncoding);

	/**
	 * 
	 * @param bytes
	 * @param hasSize If hasSize is true, the first four bytes are the size of the string
	 * @return The String represented by the byte array
	 * @throws UnsupportedEncodingException
	 */
	public abstract String byteArrayToString(byte[] bytes, boolean hasSize, boolean useEncoding);

	public abstract byte[] bigDecimalToByteArray(BigDecimal bigDecimal, boolean withSize);

	public abstract BigDecimal byteArrayToBigDecimal(byte[] bytes, boolean hasSize);

	public abstract byte[] bigIntegerToByteArray(BigInteger bigInteger, boolean withSize);

	public abstract BigInteger byteArrayToBigInteger(byte[] bytes, boolean hasSize);

	public abstract byte[] intToByteArray(int l);
	/**
	 * This method writes the byte directly to the array parameter
	 */
	public abstract void intToByteArray(int l, byte[] arrayWhereToWrite, int offset);

	public abstract int byteArrayToInt(byte[] bytes, int offset);

	public abstract byte[] longToByteArray(long l);
	/**
	 * This method writes the byte directly to the array parameter
	 */
	public abstract void longToByteArray(long l, byte[] arrayWhereToWrite, int offset);


	public abstract long byteArrayToLong(byte[] bytes, int offset);

	public abstract byte[] dateToByteArray(Date date);

	public abstract Date byteArrayToDate(byte[] bytes);

	public abstract byte[] floatToByteArray(float f);

	public abstract float byteArrayToFloat(byte[] bytes);

	public abstract byte[] doubleToByteArray(double d);

	public abstract double byteArrayToDouble(byte[] bytes);

	public abstract void setDatabaseCharacterEncoding(String databaseCharacterEncoding);

	/**
	 * @param b
	 * @param bytes
	 * @param i
	 */
	public abstract void booleanToByteArray(boolean b, byte[] arrayWhereToWrite, int offset);

	public void testEncoding(String encoding) throws UnsupportedEncodingException;
}