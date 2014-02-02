package org.neodatis.odb.impl.core.query.values;

import java.util.List;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.odb.core.query.values.AbstractQueryFieldAction;
import org.neodatis.odb.impl.core.query.list.objects.LazySimpleListOfAOI;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.NeoDatisCollectionUtil;

/**
 * An action to retrieve a sublist of list. It is used by the Object Values API.
 * 
 * When calling odb.getValues(new ValuesCriteriaQuery(Handler.class, Where  
.equal("id", id)).sublist("parameters",fromIndex, size); 

The sublist action will return  Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.

 if parameters list contains [param1,param2,param3,param4], sublist("parameters",1,2) will return a sublist
containing [param2,param3]
 * @author osmadja
 *
 */
public class SublistAction extends AbstractQueryFieldAction {

	private IOdbList<Object> sublist;
	private int fromIndex;
	private int size;
	private boolean throwExceptionIfOutOfBound;
	
	public SublistAction(String attributeName, String alias, int fromIndex, int size, boolean throwExceptionIfOutOfBound) {
		super(attributeName,alias,true);
		this.fromIndex = fromIndex;
		this.size = size;
		this.throwExceptionIfOutOfBound = throwExceptionIfOutOfBound;
	}

	public SublistAction(String attributeName, String alias, int fromIndex, int toIndex) {
		super(attributeName,alias,true);
		this.fromIndex = fromIndex;
		this.size = toIndex - fromIndex;
		this.throwExceptionIfOutOfBound = true;
	}

	public void execute(OID oid, AttributeValuesMap values) {
		List<Object> l = (List<Object>) values.get(attributeName);
		int localFromIndex = fromIndex;
		int localEndIndex = fromIndex+size;
		// If not throw exception, we must implement 
		// Index Out Of Bound protection
		if(!throwExceptionIfOutOfBound){
			// Check from index
			if(localFromIndex>l.size()-1){
				localFromIndex = 0;
			}
			// Check end index
			if(localEndIndex>l.size()){
				localEndIndex = l.size();
			}
		}
		
		sublist = new LazySimpleListOfAOI<Object>(size,getInstanceBuilder(),returnInstance());
		sublist.addAll( NeoDatisCollectionUtil.sublistGeneric(l,localFromIndex, localEndIndex));
		
	}


	public Object getValue() {
		return sublist;
	}


	public void end() {
		// nothing to do
	}

	public void start() {
		// Nothing to do
	}

	public List<Object> getSubList() {
		return sublist;
	}
	
	public IQueryFieldAction copy() {
		return new SublistAction(attributeName,alias,fromIndex, size, throwExceptionIfOutOfBound);
	}	
	
}
