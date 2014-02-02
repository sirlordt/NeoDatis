package net.ob3d.domainmodel;

import java.io.Serializable;

public class Score implements Serializable {
	int point1;
	int point2;

	public int getPoint1() {
		return point1;
	}

	public void setPoint1(int point1) {
		this.point1 = point1;
	}

	public int getPoint2() {
		return point2;
	}

	public void setPoint2(int point2) {
		this.point2 = point2;
	}

}
