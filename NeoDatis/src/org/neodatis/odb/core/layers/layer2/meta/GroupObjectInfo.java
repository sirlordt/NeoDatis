package org.neodatis.odb.core.layers.layer2.meta;

import java.util.ArrayList;
import java.util.Collection;


/**
 * A super class for CollectionObjectInfo, MapObjectInfo and ArrayObjectInfo. It keeps a list
 * of reference to non native objects contained in theses structures
 * @author osmadja
 *
 */
public abstract class  GroupObjectInfo extends NativeObjectInfo {

	private Collection<NonNativeObjectInfo> nonNativeObjects;
	
	public GroupObjectInfo(Object object, int odbTypeId) {
		super(object, odbTypeId);
		this.nonNativeObjects = new ArrayList<NonNativeObjectInfo>();
	}

	public GroupObjectInfo(Object object, ODBType odbType) {
		super(object, odbType);
		this.nonNativeObjects = new ArrayList<NonNativeObjectInfo>();
	}

	public Collection<NonNativeObjectInfo> getNonNativeObjects() {
		return nonNativeObjects;
	}

	public void setNonNativeObjects(Collection<NonNativeObjectInfo> nonNativeObjects) {
		this.nonNativeObjects = nonNativeObjects;
	}
	
	public void addNonNativeObjectInfo(NonNativeObjectInfo nnoi){
		this.nonNativeObjects.add(nnoi);
	}

}
