/**
 * 
 */
package org.neodatis.odb.test.server.trigger.oid;

/**
 * @author olivier
 *
 */
public class Tracklet {
	protected String id;
	protected String name;
	
	public Tracklet(String name){
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return String.format("id=%s and name=%s", id,name);
	}
}
