package org.neodatis.odb.impl.core.query.values;

import java.util.List;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.odb.core.query.values.AbstractQueryFieldAction;

/**
 * An action to retrieve a size of a list. It is used by the Object Values API.
 * 
 * When calling odb.getValues(new ValuesCriteriaQuery(Handler.class, Where  
.equal("id", id)).size("parameters"); 

The sublist action will return  Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.

 if parameters list contains [param1,param2,param3,param4], sublist("parameters",1,2) will return a sublist
containing [param2,param3]
 * @author osmadja
 *
 */
public class SizeAction extends AbstractQueryFieldAction {

	private long size;
	
	public SizeAction(String attributeName, String alias) {
		super(attributeName,alias,true);
	}


	public void execute(OID oid, AttributeValuesMap values) {
		List l = (List) values.get(attributeName);
		this.size = l.size();
	}


	public Object getValue() {
		return new Long(size);
	}


	public void end() {
		// nothing to do
	}

	public void start() {
		// Nothing to do
	}

	public long getSize() {
		return size;
	}
	
	public IQueryFieldAction copy() {
		return new SizeAction(attributeName,alias);
	}	
	
}
