package org.neodatis.odb.core;

import java.io.Serializable;

/**
 * Some constants used for ordering queries and creating ordered collection
 * iterators
 * 
 * @author osmadja
 * 
 */
public class OrderByConstants implements Serializable {

	private static final int ORDER_BY_NONE_TYPE = 0;
	private static final int ORDER_BY_DESC_TYPE = 1;
	private static final int ORDER_BY_ASC_TYPE = 2;

	public static final OrderByConstants ORDER_BY_NONE = new OrderByConstants(ORDER_BY_NONE_TYPE);
	public static final OrderByConstants ORDER_BY_DESC = new OrderByConstants(ORDER_BY_DESC_TYPE);
	public static final OrderByConstants ORDER_BY_ASC = new OrderByConstants(ORDER_BY_ASC_TYPE);

	private int type;

	private OrderByConstants(int type) {
		this.type = type;
	}

	public boolean isOrderByDesc() {
		return type == ORDER_BY_DESC_TYPE;
	}

	public boolean isOrderByAsc() {
		return type == ORDER_BY_ASC_TYPE;
	}

	public boolean isOrderByNone() {
		return type == ORDER_BY_NONE_TYPE;
	}

	public String toString() {
		switch (type) {
		case ORDER_BY_ASC_TYPE:
			return "order by asc";
		case ORDER_BY_DESC_TYPE:
			return "order by desc";
		case ORDER_BY_NONE_TYPE:
			return "no order by";
		}
		return "?";
	}
}
