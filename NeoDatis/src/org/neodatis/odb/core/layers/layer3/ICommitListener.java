package org.neodatis.odb.core.layers.layer3;

public interface ICommitListener {
	void beforeCommit();
	void afterCommit();
}
