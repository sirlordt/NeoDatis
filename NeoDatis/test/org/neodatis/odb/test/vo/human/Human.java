package org.neodatis.odb.test.vo.human;

public class Human extends Animal {

	public Human(String sex, String name) {
		super("human", sex, name);
	}

	public Human(String specie, String sex, String name) {
		super(specie, sex, name);
	}

}
