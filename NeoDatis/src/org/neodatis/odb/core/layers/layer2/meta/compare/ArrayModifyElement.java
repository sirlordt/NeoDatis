package org.neodatis.odb.core.layers.layer2.meta.compare;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

public class ArrayModifyElement {
	private NonNativeObjectInfo nnoi;
	/** The array id*/
	private int attributeId;
	private int arrayElementIndexToChange;
	private AbstractObjectInfo newValue;
	private boolean supportInPlaceUpdate;
	
	public int getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}
	public NonNativeObjectInfo getNnoi() {
		return nnoi;
	}
	public void setNnoi(NonNativeObjectInfo nnoi) {
		this.nnoi = nnoi;
	}
	public ArrayModifyElement(NonNativeObjectInfo nnoi, int attributeId, int index, AbstractObjectInfo newValue, boolean supportInPlaceUpdate) {
		super();
		this.nnoi = nnoi;
		this.attributeId = attributeId;
		this.supportInPlaceUpdate = supportInPlaceUpdate;
		this.newValue = newValue;
		this.arrayElementIndexToChange = index;
	}
	public long getUpdatePosition(){
		return nnoi.getAttributeDefinitionPosition(attributeId);
	}
	public int getArrayElementIndexToChange() {
		return arrayElementIndexToChange;
	}
	public AbstractObjectInfo getNewValue() {
		return newValue;
	}
	/**
	 * Return the position where the array position is stored
	 * @return
	 */
	public long getArrayPositionDefinition(){
		return nnoi.getAttributeDefinitionPosition(attributeId);
	}
	public boolean supportInPlaceUpdate() {
		return supportInPlaceUpdate;
	}

}
