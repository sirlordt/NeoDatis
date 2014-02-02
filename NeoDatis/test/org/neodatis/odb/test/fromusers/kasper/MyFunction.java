/**
 * 
 */
package org.neodatis.odb.test.fromusers.kasper;

import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class MyFunction extends Function {
	private long id;
	private MyProfile profile;

	public MyFunction(String name) {
		super(name);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MyProfile getProfile() {
		return profile;
	}

	public void setProfile(MyProfile profile) {
		this.profile = profile;
	}

}
