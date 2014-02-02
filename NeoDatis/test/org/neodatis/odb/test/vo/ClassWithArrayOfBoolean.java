/**
 * 
 */
package org.neodatis.odb.test.vo;

/**
 * @author olivier
 * 
 */
public class ClassWithArrayOfBoolean {
	private String name;
	private Boolean[] bools1;
	private boolean[] bools2;

	public ClassWithArrayOfBoolean(String name, Boolean[] bools1, boolean[] bools2) {
		super();
		this.name = name;
		this.bools1 = bools1;
		this.bools2 = bools2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean[] getBools1() {
		return bools1;
	}

	public void setBools1(Boolean[] bools1) {
		this.bools1 = bools1;
	}

	public boolean[] getBools2() {
		return bools2;
	}

	public void setBools2(boolean[] bools2) {
		this.bools2 = bools2;
	}

}
