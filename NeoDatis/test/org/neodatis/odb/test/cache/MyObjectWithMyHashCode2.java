package org.neodatis.odb.test.cache;

public class MyObjectWithMyHashCode2 {
	private Long myLong;

	public MyObjectWithMyHashCode2(Long myLong) {
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
		if (myLong == null) {
			return 0;
		}
		return myLong.hashCode();
	}

}
