package org.neodatis.odb.impl.core.query.values;

import java.math.BigDecimal;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.odb.core.query.values.AbstractQueryFieldAction;
import org.neodatis.tool.wrappers.NeoDatisNumber;

public class SumAction extends AbstractQueryFieldAction {
	private BigDecimal sum;
	
	public SumAction(String attributeName, String alias) {
		super(attributeName,alias,false);
		sum = new BigDecimal(0);
	}


	public void execute(OID oid, AttributeValuesMap values) {
		Number n = (Number) values.get(attributeName);
		sum = NeoDatisNumber.add(sum,ValuesUtil.convert(n));
	}


	public BigDecimal getSum() {
		return sum;
	}

	public Object getValue() {
		return sum;
	}


	public void end() {
		// Nothing to do		
	}


	public void start() {
		// Nothing to do		
	}

	public IQueryFieldAction copy() {
		return new SumAction(attributeName,alias);
	}	

	
}
