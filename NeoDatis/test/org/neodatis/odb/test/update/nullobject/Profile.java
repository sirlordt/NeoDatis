package org.neodatis.odb.test.update.nullobject;

/**
 * Profile
 * 
 */
public class Profile {

	private String name;

	public Profile(String name) {
		this.name = name;
	}

	public String toString() {
		return "[" + name + "]";
	}

	/**
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * return boolean
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (!(obj instanceof Profile)) {
			return false;
		} else {
			return name.equals(((Profile) obj).getName());
		}
	}
}