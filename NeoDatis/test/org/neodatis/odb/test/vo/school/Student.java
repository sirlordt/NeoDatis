/* 
 * $RCSfile: Student.java,v $
 * Tag : $Name:  $
 * $Revision: 1.5 $
 * $Author: olivier_smadja $
 * $Date: 2009/05/08 14:11:11 $
 * 
 * 
 */

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
package org.neodatis.odb.test.vo.school;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Student {
	private String id;
	private String name;
	private int age;
	private Course course;
	private Date firstDate;
	private List listHistory;

	public Student(int age, Course course, Date date, String id, String name) {
		this.age = age;
		this.course = course;
		firstDate = date;
		this.id = id;
		this.name = name;
		listHistory = new ArrayList();
	}

	public int getAge() {
		return age;
	}

	public Course getCourse() {
		return course;
	}

	public Date getFirstDate() {
		return firstDate;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List getListHistory() {
		return listHistory;
	}

	public void setListHistory(List listHistory) {
		this.listHistory = listHistory;
	}

	public void addHistory(History history) {
		history.setStudent(this);
		listHistory.add(history);
	}

	public String toString() {
		return "id=" + id + " | name=" + name + " | age= " + age + " | date=" + firstDate + " | course=" + course.getName() + " | history="
				+ listHistory.toString();
	}
}
