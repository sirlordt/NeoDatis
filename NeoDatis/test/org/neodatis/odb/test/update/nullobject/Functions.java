package org.neodatis.odb.test.update.nullobject;

import java.util.ArrayList;
import java.util.List;

/**
 * Functions
 * 
 */
public class Functions {

	private String name;
	private String nameUrl;
	private String description;
	private List listProfile;

	public String toString() {
		return "[" + name + "][" + nameUrl + "][" + description + "][" + listProfile + "]";
	}

	/**
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return Returns the name_url.
	 */
	public String getNameUrl() {
		return nameUrl;
	}

	/**
	 * 
	 * @return Returns the list of profile
	 */
	public List getListProfile() {
		return listProfile;
	}

	public void addProfile(Profile profile) {
		if (listProfile == null)
			listProfile = new ArrayList();
		listProfile.add(profile);
	}

	public void setListProfile(List listProfile) {
		this.listProfile = listProfile;
	}

	/**
	 * @param name_url
	 *            The name_url to set.
	 */
	public void setNameUrl(String name_url) {
		this.nameUrl = name_url;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}