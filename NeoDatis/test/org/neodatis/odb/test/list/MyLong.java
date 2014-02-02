package org.neodatis.odb.test.list;

public class MyLong implements Comparable {
	private long value;

	public MyLong(long value) {
		super();
		this.value = value;
	}

	public int compareTo(Object object) {
		if (object == null || !(object instanceof MyLong)) {
			return -10;
		}
		MyLong ml = (MyLong) object;
		return (int) (value - ml.value);
	}

	public long longValue() {
		return value;
	}

}
