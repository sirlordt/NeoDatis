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
package org.neodatis.odb.test.list;

import java.util.Date;

public class User {
	private String name;
	private String email;
	private String address;
	private long id;
	private Date date;
	private boolean ok;

	public User() {
		// TODO Auto-generated constructor stub
	}

	public User(String name, String email, String address, long id, Date date, boolean ok) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.email = email;
		this.address = address;
		this.id = id;
		this.date = date;
		this.ok = ok;
	}

	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name + " - " + email + " - " + address + " - id=" + id + " - " + date + " - ok=" + ok;
	}

	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

}