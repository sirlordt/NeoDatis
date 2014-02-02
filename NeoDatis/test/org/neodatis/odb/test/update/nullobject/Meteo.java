package org.neodatis.odb.test.update.nullobject;

import java.util.Date;

/**
 * PMV - Painel
 * 
 * 
 */
public class Meteo implements Device {
	private String ipAddress;
	private int port;
	private String name;
	private String physicalAddress;
	private boolean deleted; // S ou N
	private boolean status; // Sim ou Nao
	private Constructor constructor;
	private Date creationDate;
	private Date updateDate;
	private User user;

	public String toString() {
		return "[" + ipAddress + "][" + port + "][" + name + "]";
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public Constructor getConstructor() {
		return constructor;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public boolean getStatus() {
		return status;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public User getUser() {
		return user;
	}

	public void setConstructor(Constructor constructor) {
		this.constructor = constructor;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPhysicalAddress() {
		return physicalAddress;
	}

	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

}
