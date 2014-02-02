package org.neodatis.odb.impl.core.query.values;

import java.math.BigInteger;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.odb.core.query.values.AbstractQueryFieldAction;
import org.neodatis.tool.wrappers.NeoDatisNumber;

/**
 * An action to count objects of a  query
 * @author osmadja
 *
 */
public class CountAction extends AbstractQueryFieldAction {
	private static BigInteger ONE = NeoDatisNumber.newBigInteger(1);
	
	private BigInteger count;
	
	public CountAction(String alias) {
		super(alias,alias,false);
		count = NeoDatisNumber.newBigInteger(0);
	}


	public void execute(OID oid, AttributeValuesMap values) {
		count = NeoDatisNumber.add(count,ONE);
	}


	public BigInteger getCount() {
		return count;
	}


	public Object getValue() {
		return count;
	}


	public void end() {
		// Nothing to do
	}


	public void start() {
		// Nothing to do
	}

	public IQueryFieldAction copy() {
		return new CountAction(alias);
	}	


	
}
