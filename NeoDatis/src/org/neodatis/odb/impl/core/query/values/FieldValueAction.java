package org.neodatis.odb.impl.core.query.values;

import java.util.Collection;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.odb.core.query.values.AbstractQueryFieldAction;
import org.neodatis.odb.impl.core.query.list.objects.LazySimpleListOfAOI;

/**
 * An action to retrieve an object field
 * @author osmadja
 *
 */
public class FieldValueAction extends AbstractQueryFieldAction {
	/** The value of the attribute*/
	private Object value;
	
	public FieldValueAction(String attributeName,String alias) {
		super(attributeName,alias,true);
		this.value = null;
	}


	public void execute(OID oid, AttributeValuesMap values) {
		this.value = values.get(attributeName);
		
		if(ODBType.isCollection(this.value.getClass())){
			// For collection,we encapsulate it in an lazy load list that will create objects on demand
			Collection<Object> c = (Collection<Object>) this.value;
			LazySimpleListOfAOI<Object> l = new LazySimpleListOfAOI<Object>(c.size(), getInstanceBuilder(),returnInstance());
			l.addAll(c);
			this.value = l;
		}
	}

	public Object getValue() {
		return value;
	}


	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(attributeName).append("=").append(value);
		return buffer.toString();
	}


	public void end() {
		// Nothing to do		
	}


	public void start() {
		// Nothing to do
	}

	public IQueryFieldAction copy() {
		return new FieldValueAction(attributeName,alias);
	}	

}
