package org.neodatis.odb.core.query;

import java.io.Serializable;

import org.neodatis.tool.wrappers.OdbComparable;

public abstract class CompareKey implements OdbComparable, Serializable {

	public abstract int compareTo(Object o) ;

}
