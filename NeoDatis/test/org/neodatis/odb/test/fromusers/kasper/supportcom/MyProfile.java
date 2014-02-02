/**
 * 
 */
package org.neodatis.odb.test.fromusers.kasper.supportcom;

import java.util.List;

import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;

/**
 * @author olivier
 * 
 */
public class MyProfile extends Profile {
	private Long id;

	public MyProfile(String name, List<Function> functions) {
		super(name, functions);
		for (Function f : functions) {
			MyFunction mf = (MyFunction) f;
			mf.setProfile(this);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
