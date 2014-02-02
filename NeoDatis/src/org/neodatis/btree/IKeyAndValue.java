/**
 * 
 */
package org.neodatis.btree;

/**
 * @author olivier
 * 
 */
public interface IKeyAndValue {

	String toString();

	Comparable getKey();

	void setKey(Comparable key);

	Object getValue();

	void setValue(Object value);
}