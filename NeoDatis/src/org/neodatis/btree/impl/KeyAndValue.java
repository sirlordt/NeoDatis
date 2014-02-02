package org.neodatis.btree.impl;

import java.io.Serializable;

import org.neodatis.btree.IKeyAndValue;

public class KeyAndValue implements Serializable, IKeyAndValue{
	private Comparable key;
	private Object value;
	
	public KeyAndValue(Comparable key, Object value){
		this.key = key;
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see org.neodatis.btree.IKeyAndValue#toString()
	 */
	public String toString() {
		return new StringBuffer("(").append(key).append("=").append(value).append(") ").toString();
	}

	/* (non-Javadoc)
	 * @see org.neodatis.btree.IKeyAndValue#getKey()
	 */
	public Comparable getKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.btree.IKeyAndValue#setKey(java.lang.Comparable)
	 */
	public void setKey(Comparable key) {
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.btree.IKeyAndValue#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.btree.IKeyAndValue#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
