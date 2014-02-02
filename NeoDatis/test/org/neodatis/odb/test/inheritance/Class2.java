package org.neodatis.odb.test.inheritance;

public class Class2 {
	private int nb;
	private IInterface interface1;

	public Class2(int nb, IInterface interface1) {
		this.nb = nb;
		this.interface1 = interface1;
	}

	public IInterface getInterface1() {
		return interface1;
	}

	public void setInterface1(IInterface interface1) {
		this.interface1 = interface1;
	}

	public int getNb() {
		return nb;
	}

	public void setNb(int nb) {
		this.nb = nb;
	}

}
