package org.neodatis.odb.test.vo.human;

public class Animal {
	protected String specie;
	protected String sex;
	protected String name;

	public Animal(String specie, String sex, String name) {
		super();
		this.specie = specie;
		this.sex = sex;
		this.name = name;
	}

	public String getSpecie() {
		return specie;
	}

	protected void setSpecie(String specie) {
		this.specie = specie;
	}

	public String getSex() {
		return sex;
	}

	protected void setSex(String sex) {
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
