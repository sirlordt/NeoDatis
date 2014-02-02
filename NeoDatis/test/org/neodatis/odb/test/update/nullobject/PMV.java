package org.neodatis.odb.test.update.nullobject;

import java.util.Date;

/**
 * PMV - Painel
 * 
 * 
 */
public class PMV implements Device {
	private String physicalAddress;
	private String ipAddress;
	private int port;
	private String name;
	private int state;
	private int way;
	private Float km;
	private boolean deleted; // S ou N
	private boolean status; // Sim ou Nao
	private Constructor constructor;
	private Date creationDate;
	private Date updateDate;
	private User user;

	public String toString() {
		return "[" + ipAddress + "][" + port + "][" + name + "][" + state + "][" + km + "][" + creationDate + "][" + updateDate + "]["
				+ user + "]";
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public Float getKm() {
		return km;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public int getState() {
		return state;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public User getUser() {
		return user;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public Constructor getConstructor() {
		return constructor;
	}

	public boolean getStatus() {
		return status;
	}

	public void setConstructor(Constructor constructor) {
		this.constructor = constructor;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setKm(Float km) {
		this.km = km;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getPhysicalAddress() {
		return physicalAddress;
	}

	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

	public int getWay() {
		return way;
	}

	public void setWay(int way) {
		this.way = way;
	}

}
