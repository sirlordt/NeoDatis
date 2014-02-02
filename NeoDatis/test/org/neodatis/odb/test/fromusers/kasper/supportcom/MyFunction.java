/**
 * 
 */
package org.neodatis.odb.test.fromusers.kasper.supportcom;

import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class MyFunction extends Function {
	private Long id;
	private MyProfile profile;

	public MyFunction(String name) {
		super(name);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MyProfile getProfile() {
		return profile;
	}

	public void setProfile(MyProfile profile) {
		this.profile = profile;
	}

}
