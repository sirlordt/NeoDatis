package org.neodatis.tool.wrappers;

import java.util.Date;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbTime {
	public static long getCurrentTimeInMs(){
		return System.currentTimeMillis();
	}
	
	public static long getMilliseconds(Date d){
		return d.getTime();
	}

}
