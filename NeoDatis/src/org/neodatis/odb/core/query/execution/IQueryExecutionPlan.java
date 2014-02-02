package org.neodatis.odb.core.query.execution;

import java.io.Serializable;

import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;

public interface IQueryExecutionPlan extends Serializable{
	boolean useIndex();
	ClassInfoIndex getIndex();
	String getDetails();
	long getDuration();
	void start();
	void end();

}
