package org.neodatis.odb.test.fromusers.LuisSFSoeiro;

public class ClassA {
	// Some field
	private String name = null;

	// This variable get set to null when retrieved
	transient private Boolean transientBool = new Boolean(true);
	transient private String transientString = "transient";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTransientString() {
		return transientString;
	}

	public void setTransientString(String transientString) {
		this.transientString = transientString;
	}

	public Boolean getTransientBool() {
		return transientBool;
	}

	public void setTransientBool(Boolean transientBool) {
		this.transientBool = transientBool;
	}

}
