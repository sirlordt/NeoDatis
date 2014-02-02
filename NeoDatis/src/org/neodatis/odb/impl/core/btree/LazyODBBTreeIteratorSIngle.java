package org.neodatis.odb.impl.core.btree;

import org.neodatis.btree.BTreeError;
import org.neodatis.btree.BTreeIteratorSingleValuePerKey;
import org.neodatis.btree.IBTree;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;

/**
 * A Lazy BTree Iterator : It iterate on the object OIDs and lazy load objects from them (OIDs) 
 * Used by the LazyBTreeCollection
 * @author osmadja
 *
 */
public class LazyODBBTreeIteratorSIngle extends BTreeIteratorSingleValuePerKey{
	private IStorageEngine storageEngine;
    private boolean returnObjects;
	
    
    /**
     * 
     * @param tree
     * @param orderByType
     * @param storageEngine
     * @param returnObjects
     */
    public LazyODBBTreeIteratorSIngle(IBTree tree, OrderByConstants orderByType, IStorageEngine storageEngine, boolean returnObjects) {
		super(tree, orderByType);
		this.storageEngine = storageEngine;
		this.returnObjects = returnObjects;
	}
	public Object next() {
		OID oid = (OID) super.next();
		try {
			return loadObject(oid);
		} catch (Exception e) {
			throw new ODBRuntimeException(BTreeError.LAZY_LOADING_NODE.addParameter(oid),e);
		}
	}
    
	private Object loadObject(OID oid) throws Exception {
        // true = to use cache
		NonNativeObjectInfo nnoi = (NonNativeObjectInfo) storageEngine.getObjectReader().readNonNativeObjectInfoFromOid(null,oid,true,returnObjects); 
        if(returnObjects){
        	Object o = nnoi.getObject();
        	if(o!=null){
        		return o;
        	}
        	return storageEngine.getObjectReader().buildOneInstance(nnoi);
        }
        return nnoi;
	}


}
