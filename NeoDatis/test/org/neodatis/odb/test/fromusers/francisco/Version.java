package org.neodatis.odb.test.fromusers.francisco;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Version implements Serializable {

	private String name;

	public Version() {
	}

	public Version(String name) {
		this.name = name;
	}

	public static List<Version> getVersions() {
		return Arrays.asList(new Version("1.2"), new Version("1.3.x"), new Version("1.4-m1"), new Version("1.4-m2"), new Version("1.4-m3"),
				new Version("1.4-SNAPSHOT"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}