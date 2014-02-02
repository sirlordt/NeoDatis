/**
 * 
 */
package org.neodatis.odb.test.fromusers.kasper;

import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

/**
 * @author olivier
 * 
 */
public class MyUser extends User {
	private long id;

	public MyUser(String name, String email, Profile profile) {
		super(name, email, profile);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
