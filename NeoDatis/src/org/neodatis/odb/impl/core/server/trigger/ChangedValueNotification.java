/**
 * 
 */
package org.neodatis.odb.impl.core.server.trigger;

import java.io.Serializable;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.impl.core.server.ReturnValue;

/**
 * @author olivier
 *
 */
public class ChangedValueNotification implements Serializable, ReturnValue{

	private transient NonNativeObjectInfo nnoi;
	private OID oid;
	private String attributeName;
	private Object value;

	/**
	 * @param oid
	 * @param attributeName
	 * @param value
	 */
	public ChangedValueNotification(NonNativeObjectInfo nnoi, OID oid, String attributeName, Object value) {
		this.nnoi = nnoi;
		this.oid = oid;
		this.attributeName = attributeName;
		this.value = value;
	}

	public OID getOid() {
		return oid;
	}

	public void setOid(OID oid) {
		this.oid = oid;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.impl.core.server.ReturnValue#getType()
	 */
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** The server tells us that the nnoi has been stored with the given oid
	 * 
	 */
	public void setOid(NonNativeObjectInfo nnoi, OID oid) {
		// Here we can use == as it must be the same instance
		if(nnoi==this.nnoi){
			this.oid = oid;
		}
	}
	public String toString() {
		return String.format("Object with oid %s, attribute '%s' has the new value '%s'", oid,attributeName,value);
	}
	

}
