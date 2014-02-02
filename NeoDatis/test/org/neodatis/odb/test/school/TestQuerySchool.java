/* 
 * $RCSfile: TestQuerySchool.java,v $
 * Tag : $Name:  $
 * $Revision: 1.12 $
 * $Author: olivier_smadja $
 * $Date: 2009/06/26 14:45:23 $
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
package org.neodatis.odb.test.school;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.school.Course;
import org.neodatis.odb.test.vo.school.Discipline;
import org.neodatis.odb.test.vo.school.History;
import org.neodatis.odb.test.vo.school.Student;
import org.neodatis.odb.test.vo.school.Teacher;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class TestQuerySchool extends ODBTest {

	// possiveis consultas
	// Listar todos os alunos de determinado professor
	// Listar alunos com nota abaixo de x
	// Listar disciplinas que um professor ministrou no semestre

	public void test1() throws Exception {
		if (!isLocal) {
			return;
		}
		ODB odb = open("t-school.neodatis");

		// List students by name
		SchoolNativeQueryStudent natQuery = new SchoolNativeQueryStudent("Brenna", 23);
		Objects students = odb.getObjects(natQuery);

		SchoolSimpleNativeQueryStudent sNatQuery = new SchoolSimpleNativeQueryStudent("Brenna");
		students = odb.getObjects(sNatQuery);

		// list disciplines of one teacher by semester

		SchoolNativeQueryTeacher natQuery2 = new SchoolNativeQueryTeacher("Jeremias");
		Objects historys = odb.getObjects(natQuery2);
		HashMap listDiscipline = new OdbHashMap();
		for (Iterator iter = historys.iterator(); iter.hasNext();) {
			History h = (History) iter.next();
			listDiscipline.put(h.getDiscipline().getName(), h.getDiscipline());
		}

		odb.close();

	}

	public void test12() throws Exception {
		ODB odb = null;

		try {
			odb = open("t-school.neodatis");
			ClassInfo ci = Dummy.getEngine(odb).getSession(true).getMetaModel().getClassInfo(Student.class.getName(), true);
			assertFalse(ci.hasCyclicReference());
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void setUp() throws Exception {
		super.setUp();
		deleteBase("t-school.neodatis");
		ODB odb = open("t-school.neodatis");

		Objects students = odb.getObjects(Student.class, true);
		int numStudents = students.size();

		Course computerScience = new Course("Computer Science");
		Teacher teacher = new Teacher("Jeremias", "Java");
		Discipline dw1 = new Discipline("Des. Web 1", 3);
		Discipline is = new Discipline("Intranet/Segurança", 4);

		Student std1 = new Student(20, computerScience, new Date(), "1cs", "Brenna");

		History h1 = new History(new Date(), dw1, 0, teacher);
		History h2 = new History(new Date(), is, 0, teacher);

		std1.addHistory(h1);
		std1.addHistory(h2);

		odb.store(std1);

		odb.commit();
		odb.close();

		odb = open("t-school.neodatis");
		students = odb.getObjects(Student.class, true);
		odb.close();
		assertEquals(numStudents + 1, students.size());
	}

	public void tearDown() throws Exception {
		deleteBase("t-school.neodatis");
	}

}
