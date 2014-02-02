package org.neodatis.odb.impl.core.query.values;

import java.util.Iterator;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IValuesQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.core.query.execution.IQueryFieldAction;
import org.neodatis.odb.impl.core.oid.OdbObjectOID;
import org.neodatis.odb.impl.core.query.list.values.DefaultObjectValues;
import org.neodatis.odb.impl.core.query.list.values.InMemoryBTreeCollectionForValues;
import org.neodatis.odb.impl.core.query.list.values.SimpleListForValues;
import org.neodatis.tool.wrappers.OdbComparable;

public class ValuesQueryResultAction implements IMatchingObjectAction {
	private IValuesQuery query;
	/** A copy of the query object actions */
	private IQueryFieldAction[] queryFieldActions;

	private long nbObjects;

	private Values result;

	private boolean queryHasOrderBy;

	/** An object to build instances */
	protected IInstanceBuilder instanceBuilder;

	protected ClassInfo classInfo;

	private int returnArraySize;

	private IStorageEngine engine;

	public ValuesQueryResultAction(IValuesQuery query, IStorageEngine storageEngine, IInstanceBuilder instanceBuilder) {
		super();
		this.engine = storageEngine;
		this.query = query;
		this.queryHasOrderBy = query.hasOrderBy();
		this.instanceBuilder = instanceBuilder;
		this.returnArraySize = query.getObjectActions().size();
		Iterator iterator = query.getObjectActions().iterator();
		IQueryFieldAction qfa = null;
		queryFieldActions = new IQueryFieldAction[returnArraySize];
		int i = 0;
		while (iterator.hasNext()) {
			qfa = (IQueryFieldAction) iterator.next();
			queryFieldActions[i] = qfa.copy();
			queryFieldActions[i].setReturnInstance(query.returnInstance());
			queryFieldActions[i].setInstanceBuilder(instanceBuilder);
			i++;
		}
	}

	public void objectMatch(OID oid, OdbComparable orderByKey) {
		// This method os not used in Values Query API
		/*
		 * if (queryHasOrderBy) { result.addWithKey(orderByKey, oid); } else {
		 * result.add(oid); }
		 */
	}

	public void objectMatch(OID oid, Object object, OdbComparable orderByKey) {
		if (query.isMultiRow()) {
			ObjectValues values = convertObject((AttributeValuesMap) object);
			if (queryHasOrderBy) {
				result.addWithKey(orderByKey, values);
			} else {
				result.add(values);
			}
		} else {
			compute((AttributeValuesMap) object);
		}
	}

	private void compute(AttributeValuesMap values) {
		for (int i = 0; i < returnArraySize; i++) {
			queryFieldActions[i].execute(values.getObjectInfoHeader().getOid(), values);
		}
	}

	private ObjectValues convertObject(AttributeValuesMap values) {
		DefaultObjectValues dov = new DefaultObjectValues(returnArraySize);
		IQueryFieldAction qfa = null;
		for (int i = 0; i < returnArraySize; i++) {
			qfa = queryFieldActions[i];
			qfa.execute(values.getObjectInfoHeader().getOid(), values);
			
			Object o = qfa.getValue();
			
			// When Values queries return objects, they actually return the oid of the object
			// So we must load it here
			if (o != null && o instanceof OID) {
				OdbObjectOID oid = (OdbObjectOID) o;
				o = engine.getObjectFromOid(oid);
			}
			
			dov.set(i, qfa.getAlias(), o);
		}

		return dov;
	}

	public void start() {

		if (query != null && query.hasOrderBy()) {
			result = new InMemoryBTreeCollectionForValues((int) nbObjects, query.getOrderByType());
		} else {
			result = new SimpleListForValues((int) nbObjects);
		}

		IQueryFieldAction qfa = null;
		for (int i = 0; i < returnArraySize; i++) {
			qfa = queryFieldActions[i];
			qfa.start();
		}
	}

	public void end() {
		IQueryFieldAction qfa = null;
		DefaultObjectValues dov = null;

		if (!query.isMultiRow()) {
			dov = new DefaultObjectValues(returnArraySize);
		}
		for (int i = 0; i < returnArraySize; i++) {
			qfa = queryFieldActions[i];
			qfa.end();
			if (!query.isMultiRow()) {
				Object o = qfa.getValue();
				// When Values queries return objects, they actually return the oid of the object
				// So we must load it here
				if (o != null && o instanceof OID) {
					OdbObjectOID oid = (OdbObjectOID) o;
					o = engine.getObjectFromOid(oid);
				}

				// Sets the values now
				dov.set(i, qfa.getAlias(), o);
			}
		}
		if (!query.isMultiRow()) {
			result.add(dov);
		}
	}

	public Values getValues() {
		return result;
	}

	public <T> Objects<T> getObjects() {
		return (Objects<T>) result;
	}

}
