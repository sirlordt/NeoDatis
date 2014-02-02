/**
 * 
 */
package org.neodatis.odb.test.fromusers.kasper.supportcom;

import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

/**
 * @author olivier
 * 
 */
public class MyUser extends User {
	private Long id;

	public MyUser(String name, String email, Profile profile) {
		super(name, email, profile);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
