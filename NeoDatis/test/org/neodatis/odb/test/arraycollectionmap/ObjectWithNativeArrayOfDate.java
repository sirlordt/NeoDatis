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
package org.neodatis.odb.test.arraycollectionmap;

import java.util.Date;

public class ObjectWithNativeArrayOfDate {
	private String name;
	private Date[] numbers;

	public ObjectWithNativeArrayOfDate(String name, Date[] numbers) {
		super();
		this.name = name;
		this.numbers = numbers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date[] getNumbers() {
		return numbers;
	}

	public Date getNumber(int index) {
		return numbers[index];
	}

	public void setNumbers(Date[] numbers) {
		this.numbers = numbers;
	}

	public void setNumber(int index, Date bd) {
		numbers[index] = bd;
	}

}
