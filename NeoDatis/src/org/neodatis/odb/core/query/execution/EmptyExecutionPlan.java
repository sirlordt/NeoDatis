package org.neodatis.odb.core.query.execution;

import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;

public class EmptyExecutionPlan implements IQueryExecutionPlan{

	public void end() {
	}

	public String getDetails() {
		return "empty plan";
	}

	public long getDuration() {
		return 0;
	}

	public ClassInfoIndex getIndex() {
		return null;
	}

	public void start() {
	}

	public boolean useIndex() {
		return false;
	}

}
