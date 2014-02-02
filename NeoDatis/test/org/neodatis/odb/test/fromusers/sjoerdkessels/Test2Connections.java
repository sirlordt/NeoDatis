/**
 * 
 */
package org.neodatis.odb.test.fromusers.sjoerdkessels;

import java.util.Date;

import org.neodatis.odb.ClassRepresentation;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class Test2Connections extends ODBTest {

	public void test1() {
		if (!testNewFeature) {
			return;
		}

		int port = 15000;
		String baseName = getBaseName();

		// create the server
		ODBServer server = ODBFactory.openServer(port);
		// Start the server
		server.startServer(true);

		// Create the index
		ODB c1 = ODBFactory.openClient("localhost", port, baseName);
		ODB c2 = ODBFactory.openClient("localhost", port, baseName);
		ClassRepresentation classRepresentation = c1.getClassRepresentation(Person.class);
		if (!classRepresentation.existIndex("name-index")) {
			classRepresentation.addIndexOn("name-index", new String[] { "name" }, true);
		}

		c1.store(new Person("myname", "myemail", new Date()));
		c1.close();
		IQuery q = new CriteriaQuery(Person.class, Where.equal("name", "myname"));
		Objects<Person> people = c2.getObjects(q);
		c2.close();
		server.close();

		assertEquals(true, q.getExecutionPlan().useIndex());

		assertEquals(1, people.size());

	}

	public void test2() {

		int port = 15000;
		String baseName = getBaseName();

		// create the server
		ODBServer server = ODBFactory.openServer(port);
		// Start the server
		server.startServer(true);

		// Create the index
		ODB c1 = ODBFactory.openClient("localhost", port, baseName);

		c1.getClassRepresentation(Person.class).addIndexOn("name-index", new String[] { "name" }, true);
		c1.store(new Person("name", "email", new Date()));
		c1.close();

		ODB c2 = ODBFactory.openClient("localhost", port, baseName);

		Objects<Person> people = c2.getObjects(Person.class, true);
		c2.close();
		server.close();

		assertEquals(1, people.size());

	}

	public void test3() {
		if (!testNewFeature) {
			return;
		}

		int port = 15000;
		String baseName = getBaseName();

		// create the server
		ODBServer server = ODBFactory.openServer(port);
		// Start the server
		server.startServer(true);

		// Create the index
		ODB c1 = ODBFactory.openClient("localhost", port, baseName);
		ODB c2 = ODBFactory.openClient("localhost", port, baseName);

		c1.store(new Person("name", "email", new Date()));
		c1.close();

		Objects<Person> people = c2.getObjects(Person.class, true);
		c2.close();
		server.close();

		assertEquals(1, people.size());

	}

	public void test5() {
		if (!testNewFeature) {
			return;
		}

		int port = 15000;
		String baseName = getBaseName();

		// create the server
		ODBServer server = ODBFactory.openServer(port);
		// Start the server
		server.startServer(true);

		ODB c1 = ODBFactory.openClient("localhost", port, baseName);
		c1.store(new Person("name", "email", new Date()));
		c1.close();

		c1 = ODBFactory.openClient("localhost", port, baseName);
		ODB c2 = ODBFactory.openClient("localhost", port, baseName);

		c1.store(new Person("name", "email", new Date()));
		c1.close();

		Objects<Person> people = c2.getObjects(Person.class, true);
		c2.close();
		server.close();
		assertEquals(2, people.size());

	}

	public void test4() {

		int port = 15000;
		String baseName = getBaseName();

		// create the server
		ODBServer server = ODBFactory.openServer(port);
		// Start the server
		server.startServer(true);

		// Create the index
		ODB c1 = ODBFactory.openClient("localhost", port, baseName);

		c1.store(new Person("name", "email", new Date()));
		c1.close();

		ODB c2 = ODBFactory.openClient("localhost", port, baseName);
		Objects<Person> people = c2.getObjects(Person.class, true);
		c2.close();
		server.close();
		assertEquals(1, people.size());

	}

}
