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
package org.neodatis.odb.test.gui;

import java.util.Date;

import javax.swing.JFrame;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.objectbrowser.flat.FlatQueryResultPanel;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.country.City;
import org.neodatis.odb.test.vo.country.Country2;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;
import org.neodatis.odb.test.vo.school.Course;
import org.neodatis.odb.test.vo.school.Discipline;
import org.neodatis.odb.test.vo.school.History;
import org.neodatis.odb.test.vo.school.Student;
import org.neodatis.odb.test.vo.school.Teacher;

public class TestGUI extends ODBTest {

	public void testEmpty() {
		// to avoid junit junit.framework.AssertionFailedError: No tests found
		// in ...
	}

	void setUp1() throws Exception {
		ODB odb = open("t1.neodatis");
		Function f1 = new Function("login");
		Function f2 = new Function("logout");
		Profile profile = new Profile("profile 1", f1);
		profile.addFunction(f2);
		User user = new User("André", "andre@neodatis.com", profile);

		Profile profile2 = new Profile("profile 2", f1);
		profile2.addFunction(f2);
		User user2 = new User("Olivier", "olivier@neodatis.com", profile2);

		odb.store(user);
		odb.store(user2);

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

		Country2 france = new Country2("France");
		City paris = new City("paris");
		paris.setCountry(france);
		france.setCapital(paris);
		odb.store(france);

		odb.close();
	}

	public static void main(String[] args) throws Exception {
		new TestGUI().setUp1();

		JFrame frame = new JFrame("Teste Flat Query Panel");

		IBaseIdentification parameters = new IOFileParameter("t1.neodatis", true, null, null);
		// IOParameters parameters = new
		// IOFileParameter("knowledger.neodatis",true);
		IStorageEngine storageEngine = OdbConfiguration.getCoreProvider().getClientStorageEngine(parameters);
		Objects l = storageEngine.getObjectInfos(new CriteriaQuery(User.class), true, -1, -1, false);
		// List l =
		// storageEngine.getObjectInfos(KnowledgeBaseDescription.class,null,true,-1,-1,false);

		FlatQueryResultPanel flatQueryResultPanel = new FlatQueryResultPanel(storageEngine, User.class.getName(), l);
		// FlatQueryResultPanel flatQueryResultPanel = new
		// FlatQueryResultPanel(storageEngine,KnowledgeBaseDescription.class,l);
		frame.getContentPane().add(flatQueryResultPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

}
