package org.neodatis.odb.test.query.values;

import java.math.BigDecimal;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.impl.core.query.values.CustomQueryFieldAction;
import org.neodatis.odb.impl.core.query.values.ValuesUtil;

public class TestCustomQueryFieldAction extends CustomQueryFieldAction {

	private BigDecimal myValue;

	public TestCustomQueryFieldAction() {
		this.myValue = new BigDecimal(0);
	}

	public void execute(final OID oid, final AttributeValuesMap values) {
		Number n = ValuesUtil.convert((Number) values.get(attributeName));
		myValue = myValue.add(new BigDecimal(n.toString()).multiply(new BigDecimal(2)));
	}

	public Object getValue() {
		return myValue;
	}

	public boolean isMultiRow() {
		return false;
	}

	public void start() {
		// Nothing to do
	}

	public void end() {
		// Nothing to do
	}
}
