/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.test.vo.download;

import java.util.Date;

public class User {
	private String name;
	private String email;
	private String runtimeVersion;
	private String country;
	private String city;
	private int nbDownloads;
	private Date lastDownload;

	public User() {
		super();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRuntimeVersion() {
		return runtimeVersion;
	}

	public void setRuntimeVersion(String runtimeVersion) {
		this.runtimeVersion = runtimeVersion;
	}

	public int getNbDownloads() {
		return nbDownloads;
	}

	public void setNbDownloads(int nbDownloads) {
		this.nbDownloads = nbDownloads;
	}

	public Date getLastDownload() {
		return lastDownload;
	}

	public void setLastDownload(Date lastDownload) {
		this.lastDownload = lastDownload;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("name=").append(name).append(" - email=").append(email);
		buffer.append("lastDownload").append(lastDownload).append(" - nb downloads=").append(nbDownloads);
		return buffer.toString();
	}

}
