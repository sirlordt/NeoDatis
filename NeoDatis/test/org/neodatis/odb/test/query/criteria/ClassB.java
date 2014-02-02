/**
 * 
 */
package org.neodatis.odb.test.query.criteria;

import java.util.List;

import org.neodatis.odb.test.vo.login.Profile;

/**
 * @author olivier
 *
 */
public class ClassB {
	private String name;
	private List<Profile> profiles;
	public ClassB(String name, List<Profile> profiles) {
		super();
		this.name = name;
		this.profiles = profiles;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Profile> getProfiles() {
		return profiles;
	}
	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}
	
	
}
