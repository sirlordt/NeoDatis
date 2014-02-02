package org.neodatis.odb.test.transient_attributes;

import java.util.ArrayList;
import java.util.List;

public class VoWithTransientAttribute {
	private String name;
	private transient List<String> keys;

	public VoWithTransientAttribute(String name) {
		this.name = name;
	}

	public void addKey(String key) {
		if (keys == null) {
			keys = new ArrayList<String>();
		}
		keys.add(key);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

}
