package org.neodatis.odb.test.cache;

public class MyObjectWithMyHashCode {
	private Long myLong;

	public MyObjectWithMyHashCode(Long myLong) {
		super();
		this.myLong = myLong;
	}

	public Long getMyLong() {
		return myLong;
	}

	public void setMyLong(Long myLong) {
		this.myLong = myLong;
	}

	public int hashCode() {
		return myLong.hashCode();
	}

}
