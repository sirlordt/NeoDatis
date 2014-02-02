
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
package org.neodatis.odb.impl.core.layers.layer3.engine;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer3.engine.IByteArrayConverter;


/**Converts array of bytes into native objects and native objects into array of bytes
 * 
 * @sharpen.ignore
 * 
 * @author osmadja
 *
 */
public class DefaultByteArrayConverter implements IByteArrayConverter {

	/** The encoding used for string to byte conversion*/
	private String encoding;
	private boolean hasEncoding;
	private static final byte BYTE_FOR_TRUE = 1;
	private static final byte BYTE_FOR_FALSE = 0;
	private static final byte[] BYTES_FOR_TRUE = {1};
	private static final byte[] BYTES_FOR_FALSE = {0};
	private static int INT_SIZE = 0;
	private static int INT_SIZE_x_2 = 0;

	/** Two Phase Init method*/
	public void init2() {
		INT_SIZE = ODBType.INTEGER.getSize();
		INT_SIZE_x_2 = INT_SIZE*2;
		setDatabaseCharacterEncoding(OdbConfiguration.getDatabaseCharacterEncoding());
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#booleanToByteArray(boolean)
	 */
	public  byte [] booleanToByteArray(boolean b) {
		if(b){
			return BYTES_FOR_TRUE;
		}
		return BYTES_FOR_FALSE;
	}
	public  void booleanToByteArray(boolean b, byte[] arrayWhereToWrite, int offset) {
		
		if(b){
			arrayWhereToWrite[offset] = BYTE_FOR_TRUE;
		}else{
			arrayWhereToWrite[offset] = BYTE_FOR_FALSE;
		}
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToBoolean(byte[])
	 */
	public  boolean byteArrayToBoolean(byte [] bytes, int offset) {
		if (bytes[offset] == 0) {
            bytes = null;
			return false;
		}
        bytes = null;
		return true;
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#shortToByteArray(short)
	 */
	public  byte[] shortToByteArray(short s) {
//		return ByteBuffer.allocate(8).putLong(i).array();
		byte b[] = new byte[2];
		int i, shift;
		for (i = 0, shift = 8; i < 2; i++, shift -= 8) {
			b[i] = (byte) (0xFF & (s >> shift));
		}
		return b;
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToShort(byte[])
	 */
	public  short byteArrayToShort(byte [] bytes )  {
		short result = 0;

		for (int i = 0; i < 2; i++) {
			result <<= 8; // left shift out the last byte
			result |= bytes[i] & 0xFF; // OR in the new byte
		}
        bytes = null;
		return result;
	}


	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#charToByteArray(char)
	 */
	public  byte [] charToByteArray(char c){
		byte b[] = new byte[2];
		int i, shift;
		for (i = 0, shift = 8; i < 2; i++, shift -= 8) {
			b[i] = (byte) (0xFF & (c >> shift));
		}
		return b;	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToChar(byte[])
	 */
	public  char byteArrayToChar(byte [] bytes) {
		char result = 0;

		for (int i = 0; i < 2; i++) {
			result <<= 8; // left shift out the last byte
			result |= bytes[i] & 0xFF; // OR in the new byte
		}
        bytes = null;
		return result;
	}

    /* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#getNumberOfBytesOfAString(java.lang.String)
	 * FIXME use encoding
	 */
    public  int getNumberOfBytesOfAString(String s, boolean useEncoding){
        if(useEncoding&&hasEncoding){
        	try {
				return s.getBytes(encoding).length;
			} catch (UnsupportedEncodingException e) {
				throw new ODBRuntimeException(org.neodatis.odb.core.NeoDatisError.UNSUPPORTED_ENCODING.addParameter(encoding));
			}
        }
        return s.getBytes().length;
    }
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#stringToByteArray(java.lang.String, boolean, int)
	 */
	public  byte [] stringToByteArray(String s,boolean withSize, int totalSpace, boolean withEncoding) {
		byte [] bytes = null;
		
		if(withEncoding&&hasEncoding){
			try {
				bytes = s.getBytes(encoding);
			} catch (UnsupportedEncodingException e) {
				throw new ODBRuntimeException(org.neodatis.odb.core.NeoDatisError.UNSUPPORTED_ENCODING.addParameter(encoding));
			}
		}else{
			bytes = s.getBytes();
		}
		if(!withSize){
			return bytes;
		}
        int totalSize = 0;
		
        if(totalSpace==-1){
		    // we always store a string with X the size to enable in place update for bigger string later
        	// + INT_SIZE_x_2 is because we store two ints with the total size and the real string size before the bytes of the string
		    totalSize = OdbConfiguration.getStringSpaceReserveFactor()*bytes.length;
        }else{
        	// + INT_SIZE_x_2 is because we store two ints with the total size and the real string size before the bytes of the string
            totalSize = totalSpace;
        }
        
        byte [] bytes2 = new byte[totalSize+INT_SIZE_x_2];
        // copy the bytes of the total size
        intToByteArray(totalSize,bytes2,0);
        // copy the bytes of the real size
		intToByteArray(bytes.length,bytes2,4);
		// the +INT_SIZE_x_2 is to keep 2 ints before the real string data : one int for total space, one int for real string size
		// Copy the string data byte 
		System.arraycopy(bytes, 0, bytes2, 8, bytes.length);

		return bytes2; 
	}
	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToString(byte[], boolean)
	 */
	public  String byteArrayToString(byte [] bytes,boolean hasSize, boolean useEncoding) {
		String s = null;
		if(hasSize){
			int realSize = byteArrayToInt(bytes, INT_SIZE);
			
			if(useEncoding&&hasEncoding){
				try {
					s = new String(bytes, INT_SIZE_x_2, realSize, encoding);
				} catch (UnsupportedEncodingException e) {
					throw new ODBRuntimeException(org.neodatis.odb.core.NeoDatisError.UNSUPPORTED_ENCODING.addParameter(encoding));
				}
			}else{
				s = new String(bytes, INT_SIZE_x_2, realSize);
			}

			bytes = null;
			return s;
		}
		
		if(useEncoding&&hasEncoding){
			try {
				s = new String(bytes, encoding);
			} catch (UnsupportedEncodingException e) {
				throw new ODBRuntimeException(org.neodatis.odb.core.NeoDatisError.UNSUPPORTED_ENCODING.addParameter(encoding));
			}
		}else{
			s = new String(bytes);
		}
        bytes = null;
        return s;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#bigDecimalToByteArray(java.math.BigDecimal, boolean)
	 */
	public  byte [] bigDecimalToByteArray(BigDecimal bigDecimal,boolean withSize)  {
		return stringToByteArray(bigDecimal.toString(),withSize,-1,false);
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToBigDecimal(byte[], boolean)
	 */
	public  BigDecimal byteArrayToBigDecimal(byte [] bytes,boolean hasSize)  {
		String s = byteArrayToString(bytes,hasSize,false);
		return new BigDecimal(s);
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#bigIntegerToByteArray(java.math.BigInteger, boolean)
	 */
	public  byte [] bigIntegerToByteArray(BigInteger bigInteger,boolean withSize)  {
		return stringToByteArray(bigInteger.toString(),withSize,-1,false);
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToBigInteger(byte[], boolean)
	 */
	public  BigInteger byteArrayToBigInteger(byte [] bytes,boolean hasSize)  {
		return new BigInteger(byteArrayToString(bytes,hasSize,false));
	}


	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#intToByteArray(int)
	 */
	public  byte [] intToByteArray(int l)  {
		byte b[] = new byte[4];
		intToByteArray(l, b,0);
		return b;

	}
	/**
	 * This method writes the byte directly to the array parameter
	 */
	public  void intToByteArray(int l, byte[] arrayWhereToWrite, int offset)  {
		int i, shift;
		for (i = 0, shift = 24; i < 4; i++, shift -= 8) {
			arrayWhereToWrite[offset+i] = (byte) (0xFF & (l >> shift));
		}
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToInt(byte[], int)
	 */
	public  int byteArrayToInt(byte [] bytes, int offset)  {
		int result = 0;

		for (int i = 0; i < 4; i++) {
			result <<= 8; // left shift out the last byte
			result |= bytes[i+offset] & 0xFF; // OR in the new byte
		}
        bytes = null;
		return result;
	}

	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#longToByteArray(long)
	 */
	public  byte [] longToByteArray(long l)  {
		//return ByteBuffer.allocate(8).putLong(i).array();
		byte b[] = new byte[8];
		longToByteArray(l, b,0);
		return b;

	}
	
	/**
	 * This method writes the byte directly to the array parameter
	 */
	public  void longToByteArray(long l, byte[] arrayWhereToWrite, int offset)  {
		int i, shift;
		for (i = 0, shift = 56; i < 8; i++, shift -= 8) {
			arrayWhereToWrite[offset+i] = (byte) (0xFF & (l >> shift));
		}
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToLong(byte[])
	 */
	public  long byteArrayToLong(byte [] bytes, int offset)  {
		//return ByteBuffer.wrap(bytes).getLong();
		long result = 0;

		for (int i = 0; i < 8; i++) {
			result <<= 8; // left shift out the last byte
			result |= bytes[i+offset] & 0xFF; // OR in the new byte			
		}
        bytes = null;
		return result;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#dateToByteArray(java.util.Date)
	 */
	public  byte [] dateToByteArray(Date date)  {
		return longToByteArray(date.getTime());
	}
	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToDate(byte[])
	 */
	public  Date byteArrayToDate(byte [] bytes){
		return new Date(byteArrayToLong(bytes,0));
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#floatToByteArray(float)
	 */
	public  byte [] floatToByteArray(float f) {
		int i = Float.floatToIntBits(f);
		return intToByteArray(i);
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToFloat(byte[])
	 */
	public  float byteArrayToFloat(byte[] bytes){
		return Float.intBitsToFloat(byteArrayToInt(bytes,0));
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#doubleToByteArray(double)
	 */
	public  byte [] doubleToByteArray(double d) {
		long i = Double.doubleToLongBits(d);
		return longToByteArray(i);
	}
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.engine.IByteArrayConverter#byteArrayToDouble(byte[])
	 */
	public  double byteArrayToDouble(byte[] bytes)  {
		return Double.longBitsToDouble(byteArrayToLong(bytes,0));
	}

	public void setDatabaseCharacterEncoding(String databaseCharacterEncoding) {
		encoding = databaseCharacterEncoding;
		if(encoding==null || encoding.equals(StorageEngineConstant.NO_ENCODING)){
			hasEncoding=false;
		}else{
			hasEncoding=true;
		}
		
	}

	public void testEncoding(String encoding) throws UnsupportedEncodingException {
		"test encoding".getBytes(encoding);
	}
	
}