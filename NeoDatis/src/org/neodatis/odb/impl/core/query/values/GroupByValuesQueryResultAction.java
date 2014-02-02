package org.neodatis.odb.impl.core.query.values;

import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.core.query.execution.IndexTool;
import org.neodatis.odb.impl.core.query.list.values.InMemoryBTreeCollectionForValues;
import org.neodatis.odb.impl.core.query.list.values.SimpleListForValues;
import org.neodatis.tool.wrappers.OdbComparable;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class GroupByValuesQueryResultAction implements IMatchingObjectAction {
	private IValuesQuery query;

	private long nbObjects;

	/** When executing a group by result, results are temporary stored in a hash map and at the end transfered to a Values objects
	 * In this case, the key of the map is the group by composed key, the value is a ValuesQueryResultAction
	 */
	private Map<OdbComparable,ValuesQueryResultAction> groupByResult;
	
	private Values result;

	private boolean queryHasOrderBy;

	/** An object to build instances */
	protected IInstanceBuilder instanceBuilder;

	protected ClassInfo classInfo;

	private int returnArraySize;

	private String[] groupByFieldList;

	public GroupByValuesQueryResultAction(IValuesQuery query, IStorageEngine storageEngine, IInstanceBuilder instanceBuilder) {
		super();
		this.query = query;
		this.queryHasOrderBy = query.hasOrderBy();
		this.instanceBuilder = instanceBuilder;
		this.returnArraySize = query.getObjectActions().size();
		this.groupByFieldList = query.getGroupByFieldList();
		this.groupByResult = new OdbHashMap<OdbComparable, ValuesQueryResultAction>();
	}

	public void objectMatch(OID oid, OdbComparable orderByKey) {
		// This method os not used in Values Query API
		/*
		if (queryHasOrderBy) {
			result.addWithKey(orderByKey, oid);
		} else {
			result.add(oid);
		}*/
	}

	public void objectMatch(OID oid, Object object, OdbComparable orderByKey) {
		AttributeValuesMap values = (AttributeValuesMap) object;
		OdbComparable groupByKey = IndexTool.buildIndexKey("GroupBy",values, groupByFieldList);
		ValuesQueryResultAction result = groupByResult.get(groupByKey);
		
		if(result==null){
			result = new ValuesQueryResultAction(query,null,instanceBuilder);
			result.start();
			groupByResult.put(groupByKey, result);
		}
		result.objectMatch(oid, object, orderByKey);
	}


	public void start() {
		// Nothing to do
	}

	public void end() {

		if (query != null && query.hasOrderBy()) {
			result = new InMemoryBTreeCollectionForValues((int) nbObjects, query.getOrderByType());
		} else {
			result = new SimpleListForValues((int) nbObjects);
		}
		Iterator iterator = groupByResult.keySet().iterator();
		
		ValuesQueryResultAction vqra = null;
		OdbComparable key = null;
		while (iterator.hasNext()) {
			key = (OdbComparable) iterator.next();
			vqra = (ValuesQueryResultAction) groupByResult.get(key);
			vqra.end();
			merge(key,vqra.getValues());
		}
	}

	private void merge(OdbComparable key, Values values) {
		while(values.hasNext()){
			if(queryHasOrderBy){
				result.addWithKey(key, values.nextValues());
			}else{
				result.add(values.nextValues());
			}
		}
	}

	public <T>Objects<T> getObjects() {
		return (Objects<T>) result;
	}

}
