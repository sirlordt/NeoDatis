package net.ob3d.domainmodel;

import java.io.Serializable;

public class PlayerInfo implements Serializable {

	String name = "";
	String username = "";
	int careerLevel;

	public PlayerInfo(String username) {
		super();
		this.username = username;

	}

	public PlayerInfo(String username, String name) {
		this(username);
		this.name = name;
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getCareerLevel() {
		return careerLevel;
	}

	public void setCareerLevel(int careerLevel) {
		this.careerLevel = careerLevel;
	}
}
