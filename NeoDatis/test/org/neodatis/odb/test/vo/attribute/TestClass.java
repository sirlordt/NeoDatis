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
package org.neodatis.odb.test.vo.attribute;

import java.math.BigDecimal;
import java.util.Date;

public class TestClass {
	private int int1;
	private boolean boolean1;
	private Boolean boolean2;
	private String string1;
	private char char1;
	private BigDecimal bigDecimal1;
	private Double double1;
	private Date date1;

	public TestClass(String s, BigDecimal bd, Double d, Date dt){
		this.string1  =s;
		this.bigDecimal1 = bd;
		this.double1 = d;
		this.date1 = dt;
	}
	
	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public TestClass() {
	}

	public BigDecimal getBigDecimal1() {
		return bigDecimal1;
	}

	public void setBigDecimal1(BigDecimal bigDecimal1) {
		this.bigDecimal1 = bigDecimal1;
	}

	public boolean isBoolean1() {
		return boolean1;
	}

	public void setBoolean1(boolean boolean1) {
		this.boolean1 = boolean1;
	}

	public char getChar1() {
		return char1;
	}

	public void setChar1(char char1) {
		this.char1 = char1;
	}

	public Double getDouble1() {
		return double1;
	}

	public void setDouble1(Double double1) {
		this.double1 = double1;
	}

	public int getInt1() {
		return int1;
	}

	public void setInt1(int int1) {
		this.int1 = int1;
	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	};

	public void change() {
		string1 = "ola";
	}

	/*
	 * public String toString() { return string1 +
	 * "  i="+int1+"   double="+double1
	 * +"   bd="+double1+"   char="+char1+"   bool="+boolean1+
	 * "    time="+date1.getTime(); }
	 */
	public String toString() {
		return double1 + " | " + string1 + " | " + int1;
	}

	public Boolean getBoolean2() {
		return boolean2;
	}

	public void setBoolean2(Boolean boolean2) {
		this.boolean2 = boolean2;
	}
	
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof TestClass)){
			return false;
		}
		TestClass tc = (TestClass) obj;
		boolean b1 = string1!=null && string1.equals(tc.string1);
		boolean b2 = bigDecimal1!=null && bigDecimal1.equals(tc.bigDecimal1); 
		boolean b3 = double1!=null && double1.equals(tc.double1);
		boolean b = b1 && b2 && b3;
		return b;
	}

}
