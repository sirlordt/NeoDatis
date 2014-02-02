package org.neodatis.odb.test.update.nullobject;

import java.util.Date;

/**
 * SensorAT
 * 
 * 
 */
public class SensorAT {
	private String name;
	private int state;
	private Float km;
	private int lane;
	private int way;
	private AT at;
	private boolean deleted; // S ou N
	private boolean status; // Sim ou Nao
	private Date creationDate;
	private Date updateDate;
	private User user;

	public String toString() {
		return "[" + at + "][" + name + "][" + state + "][" + lane + "][" + way + "][" + km + "]";
	}

	public Float getKm() {
		return km;
	}

	public String getName() {
		return name;
	}

	public int getState() {
		return state;
	}

	public int getWay() {
		return way;
	}

	public int getLane() {
		return lane;
	}

	public AT getAt() {
		return at;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public User getUser() {
		return user;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public boolean getStatus() {
		return status;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setAt(AT at) {
		this.at = at;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setWay(int way) {
		this.way = way;
	}

	public void setLane(int lane) {
		this.lane = lane;
	}

	public void setKm(Float km) {
		this.km = km;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(int state) {
		this.state = state;
	}

}
