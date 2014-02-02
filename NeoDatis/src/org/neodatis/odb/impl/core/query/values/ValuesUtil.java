package org.neodatis.odb.impl.core.query.values;

import java.math.BigDecimal;

import org.neodatis.tool.wrappers.NeoDatisNumber;

public class ValuesUtil {

	public static BigDecimal convert(Number number){
		BigDecimal bd = NeoDatisNumber.createDecimalFromString(number.toString());
		return bd;
	}	
}
