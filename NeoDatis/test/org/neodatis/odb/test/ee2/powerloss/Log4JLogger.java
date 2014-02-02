/**
 * 
 */
package org.neodatis.odb.test.ee2.powerloss;

import org.apache.log4j.Logger;
import org.neodatis.tool.ILogger;

/**
 * @author olivier
 *
 */
public class Log4JLogger implements ILogger{
	public Logger l = Logger.getLogger(Log4JLogger.class);

	public void info(Object message) {
		l.info(message);
	}

	public void warn(Object message) {
		l.warn(message);
	}

	/* (non-Javadoc)
	 * @see org.neodatis.tool.ILogger#debug(java.lang.Object)
	 */
	public void debug(Object object) {
		l.debug(object);
		
	}

	/* (non-Javadoc)
	 * @see org.neodatis.tool.ILogger#error(java.lang.Object)
	 */
	public void error(Object object) {
		l.error(object);
		
	}

	/* (non-Javadoc)
	 * @see org.neodatis.tool.ILogger#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object object, Throwable t) {
		l.error(object,t);
		
	}
	
	
	
	

}
