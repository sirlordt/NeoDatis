package org.neodatis.odb.core.query.values;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;

public abstract class AbstractQueryFieldAction implements IQueryFieldAction{
	protected String attributeName;
	protected String alias;
	protected boolean isMultiRow;
	
	protected IInstanceBuilder instanceBuilder;
	protected boolean returnInstance;
	
	
	
	public AbstractQueryFieldAction(String attributeName, String alias, boolean isMultiRow) {
		super();
		this.attributeName = attributeName;
		this.alias = alias;
		this.isMultiRow = isMultiRow;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public String getAlias() {
		return alias;
	}
	public abstract void execute(final OID oid, final AttributeValuesMap values);
	public boolean isMultiRow() {
		return isMultiRow;
	}
	public void setMultiRow(boolean isMultiRow) {
		this.isMultiRow = isMultiRow;
	}
	public IInstanceBuilder getInstanceBuilder() {
		return instanceBuilder;
	}
	public void setInstanceBuilder(IInstanceBuilder instanceBuilder) {
		this.instanceBuilder = instanceBuilder;
	}
	public boolean returnInstance() {
		return returnInstance;
	}
	public void setReturnInstance(boolean returnInstance) {
		this.returnInstance = returnInstance;
	}
}
