package org.neodatis.odb.impl.core.query.criteria;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.instance.IInstanceBuilder;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.execution.IMatchingObjectAction;
import org.neodatis.odb.impl.core.query.list.objects.InMemoryBTreeCollection;
import org.neodatis.odb.impl.core.query.list.objects.LazyBTreeCollection;
import org.neodatis.odb.impl.core.query.list.objects.LazySimpleListFromOid;
import org.neodatis.odb.impl.core.query.list.objects.SimpleList;
import org.neodatis.tool.wrappers.OdbComparable;


/**
 * Class that manage normal query. Query that return a list of objects. For each object
 * That matches the query criteria, the objectMatch method is called and it keeps the objects in the 'objects' instance.
 * @author olivier
 *
 */
public class CollectionQueryResultAction implements IMatchingObjectAction {
	private IQuery query;

	private boolean inMemory;

	private long nbObjects;

	private IStorageEngine storageEngine;

	private boolean returnObjects;

	// TODO check if Object is ok here
	private Objects<Object> result;

	private boolean queryHasOrderBy;

	/** An object to build instances */
	protected IInstanceBuilder instanceBuilder;

	public CollectionQueryResultAction(IQuery query, boolean inMemory, IStorageEngine storageEngine, boolean returnObjects, IInstanceBuilder instanceBuilder) {
		super();
		this.query = query;
		this.inMemory = inMemory;
		this.storageEngine = storageEngine;
		this.returnObjects = returnObjects;
		this.queryHasOrderBy = query.hasOrderBy();
		this.instanceBuilder = instanceBuilder;
	}

	public void objectMatch(OID oid, OdbComparable orderByKey) {
		if(queryHasOrderBy){
			result.addWithKey(orderByKey,oid);
		}else{
			result.add(oid);
		}
	}

	public void objectMatch(OID oid, Object object, OdbComparable orderByKey) {
		NonNativeObjectInfo nnoi = (NonNativeObjectInfo) object;
		
		if (inMemory) {
			if (returnObjects) {
				if (queryHasOrderBy) {
					result.addWithKey(orderByKey, getCurrentInstance(nnoi));
				} else {
					result.add(getCurrentInstance(nnoi));
				}
			} else {
				if (queryHasOrderBy) {
					result.addWithKey(orderByKey, nnoi);
				} else {
					result.add(nnoi);
				}
			}
		} else {
			if (queryHasOrderBy) {
				result.addWithKey(orderByKey, oid);
			} else {
				result.add(oid);
			}
		}
	}

	public void start() {

		if (inMemory) {
			if (query != null && query.hasOrderBy()) {
				result = new InMemoryBTreeCollection((int) nbObjects, query.getOrderByType());
			} else {
				result = new SimpleList((int) nbObjects);
				// result = new InMemoryBTreeCollection((int) nbObjects);
			}
		} else {
			if (query != null && query.hasOrderBy()) {
				result = new LazyBTreeCollection((int) nbObjects, storageEngine, returnObjects);
			} else {
				result = new LazySimpleListFromOid((int) nbObjects, storageEngine, returnObjects);
			}
		}
	}

	public void end() {

	}

	public Object getCurrentInstance(NonNativeObjectInfo nnoi)  {
		//FIXME no need
		if(nnoi.getObject()!=null){
			return nnoi.getObject();	
		}
		return instanceBuilder.buildOneInstance(nnoi);
		
	}

	public Objects<Object> getObjects() {
		return result;
	}

}
