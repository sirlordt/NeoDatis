package org.neodatis.odb.test.list;

public class MyInt implements Comparable {
	private int value;

	public MyInt(int value) {
		super();
		this.value = value;
	}

	public int compareTo(Object object) {
		if (object == null || !(object instanceof MyInt)) {
			return -10;
		}
		MyInt ml = (MyInt) object;
		return (int) (value - ml.value);
	}

	public int intValue() {
		return value;
	}

}
