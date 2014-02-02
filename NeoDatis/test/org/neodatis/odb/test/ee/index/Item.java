/**
 * 
 */
package org.neodatis.odb.test.ee.index;

import java.util.Date;

/**
 * @author olivier
 *
 */
public class Item {
	String uuid;
	String parent;
	Date createdAt;
	String objectType;
	Boolean active;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Item(String uuid, String parent, Date createdAt, String objectType, Boolean active) {
		super();
		this.uuid = uuid;
		this.parent = parent;
		this.createdAt = createdAt;
		this.objectType = objectType;
		this.active = active;
	}
	
	

}
