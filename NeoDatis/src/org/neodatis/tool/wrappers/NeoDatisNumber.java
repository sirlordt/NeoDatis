package org.neodatis.tool.wrappers;

import java.math.BigDecimal;
import java.math.BigInteger;

/** A class to wrap some number methods
 * 
 * @author olivier
 * @sharpen.ignore
 *
 */
public class NeoDatisNumber {
	public static BigInteger newBigInteger(long l){
		return BigInteger.valueOf(l);
	}
	public static BigDecimal add(BigDecimal bd1, BigDecimal bd2){
		return bd1.add(bd2);
	}
	public static BigDecimal divide(BigDecimal bd1, BigDecimal bd2, int roundType, int scale){
		BigDecimal bd = bd1.divide(bd2, roundType,scale).setScale(scale);
		return bd;
	}
	public static BigInteger add(BigInteger bi1, BigInteger bi2) {
		return bi1.add(bi2);
	}

	public static BigDecimal createDecimalFromString(String s){
		return new BigDecimal(s);
	}
	public static BigInteger createBigIntegerFromString(String s){
		return new BigInteger(s);
	}
}
