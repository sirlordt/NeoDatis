package org.neodatis.odb.test.vo.login;

public class User2 extends User {
	private int nbLogins;

	public User2(int nbLogins) {
		super();
		this.nbLogins = nbLogins;
	}

	public User2() {
		super();
	}

	public User2(String name, String email, Profile profile, int nbLogins) {
		super(name, email, profile);
		this.nbLogins = nbLogins;
	}

}
