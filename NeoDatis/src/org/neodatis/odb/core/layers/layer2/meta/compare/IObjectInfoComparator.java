package org.neodatis.odb.core.layers.layer2.meta.compare;

import java.util.List;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;

public interface IObjectInfoComparator {

	boolean hasChanged(AbstractObjectInfo aoi1, AbstractObjectInfo aoi2);


	void clear();

	int getNbChanges();


	boolean supportInPlaceUpdate();


	public List<ChangedObjectInfo> getChanges();

	public List<NewNonNativeObjectAction> getNewObjectMetaRepresentations();

	public NewNonNativeObjectAction getNewObjectMetaRepresentation(int i);

	public List<Object> getNewObjects();

	public int getMaxObjectRecursionLevel();

	public List<ChangedAttribute> getChangedAttributeActions();
	
	public List<ArrayModifyElement> getArrayChanges() ;
	
	public List<SetAttributeToNullAction> getAttributeToSetToNull();
	
	public AbstractObjectInfo getChangedObjectMetaRepresentation(int i) ;
	
}