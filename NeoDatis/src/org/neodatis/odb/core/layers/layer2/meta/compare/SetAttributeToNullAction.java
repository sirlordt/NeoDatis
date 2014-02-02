package org.neodatis.odb.core.layers.layer2.meta.compare;

import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;

public class SetAttributeToNullAction {
	private NonNativeObjectInfo nnoi;
	private int attributeId;
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
	public SetAttributeToNullAction(NonNativeObjectInfo nnoi, int attributeId) {
		super();
		this.nnoi = nnoi;
		this.attributeId = attributeId;
	}
	public long getUpdatePosition(){
		return nnoi.getAttributeDefinitionPosition(attributeId);
	}
	

}
