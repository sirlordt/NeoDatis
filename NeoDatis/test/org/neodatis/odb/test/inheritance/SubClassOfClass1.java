package org.neodatis.odb.test.inheritance;

public class SubClassOfClass1 extends Class1 {

	private long l1;

	public SubClassOfClass1(String name, long l1) {
		super(name);
		this.l1 = l1;
	}

	public String getName() {
		return super.getName();
	}

	public long getL1() {
		return l1;
	}

	public void setL1(long l1) {
		this.l1 = l1;
	}

}
