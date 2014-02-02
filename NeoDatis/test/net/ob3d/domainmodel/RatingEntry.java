package net.ob3d.domainmodel;

import java.io.Serializable;
import java.util.Date;

public abstract class RatingEntry implements Serializable {
	String ratingList;
	String username;
	int position = 1200;
	Date lastAdustmentDate;

	public String getRatingList() {
		return ratingList;
	}

	public void setRatingList(String ratingList) {
		this.ratingList = ratingList;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Date getLastAdustmentDate() {
		return lastAdustmentDate;
	}

	public void setLastAdustmentDate(Date lastAdustmentDate) {
		this.lastAdustmentDate = lastAdustmentDate;
	}
}
