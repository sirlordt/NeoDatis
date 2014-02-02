package org.neodatis.odb.impl.core.query.values;

import java.math.BigDecimal;

import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.odb.core.query.values.AbstractQueryFieldAction;
import org.neodatis.tool.wrappers.NeoDatisNumber;

/**
 * An action to compute the average value of a field
 * @author osmadja
 *
 */
public class AverageValueAction extends AbstractQueryFieldAction {
	private static BigDecimal ONE = new BigDecimal(1);
	private BigDecimal totalValue;
	private BigDecimal nbValues;
	private BigDecimal average;
	
	private int scale;
	private int roundType;
	
	public AverageValueAction(String attributeName, String alias) {
		super(attributeName,alias,false);
		this.totalValue = new BigDecimal(0);
		this.nbValues = new BigDecimal(0);
		this.attributeName = attributeName;
		
		this.scale = OdbConfiguration.getScaleForAverageDivision();
		this.roundType = OdbConfiguration.getRoundTypeForAverageDivision();
	}


	public void execute(OID oid, AttributeValuesMap values) {
		Number n = (Number) values.get(attributeName);
		totalValue = NeoDatisNumber.add(totalValue,ValuesUtil.convert(n));
		nbValues = NeoDatisNumber.add(nbValues,ONE);
	}


	public Object getValue() {
		return average;
	}


	public void end() {
		average = NeoDatisNumber.divide(totalValue,nbValues, roundType,scale);
	}

	public void start() {
		
	}

	public IQueryFieldAction copy() {
		return new AverageValueAction(attributeName,alias);
	}	
}
