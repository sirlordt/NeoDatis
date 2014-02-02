package org.neodatis.tool.wrappers;


/** o wrapper to the native Comparable interface
 * 
 * @author olivier
 *
 * @param <T>
 */
public interface OdbComparable extends Comparable {
	int compareTo(Object o) ;
}
