/**
 * 
 */
package org.neodatis.odb.test.fromusers.francisco;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 * 
 */
public class TestReconnect extends ODBTest {
	public void setUp() throws Exception {
		super.setUp();
		OdbConfiguration.setReconnectObjectsToSession(true);
	}

	public void tearDown() throws Exception {
		OdbConfiguration.setReconnectObjectsToSession(true);
	}

	/**
	 * this junits fails because of byte code instrumentation in
	 * ServerObjectWriter.java line 72 in public OID
	 * updateNonNativeObjectInfo(NonNativeObjectInfo nnoi, boolean forceUpdate)
	 */
	public void test1() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10008);
		ODB odb = server.openClient(baseName);

		Module module1 = buildModule("a1", "description 1111", "www.neodatis.org");
		Module module2 = buildModule("a2", "description 2222", "www.oracle.com");

		odb.store(module1);
		odb.store(module2);

		module1.setAuthor("new author of module 1");
		odb.store(module1);
		odb.close();

		// Check number of objects
		odb = server.openClient(baseName);
		Objects modules = odb.getObjects(Module.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, modules.size());
		assertEquals(2, tags.size());
		assertEquals(2, versions.size());

	}

	public void test2() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10009);
		ODB odb = server.openClient(baseName);

		Module module1 = buildModule("a1", "description 1111", "www.neodatis.org");
		Module module2 = buildModule("a2", "description 2222", "www.oracle.com");

		odb.store(module1);
		odb.store(module2);

		module1.setAuthor("new author of module 1");
		odb.store(module1);
		odb.commit();
		/*
		 * module1.setAuthor("new author of module 2"); odb.store(module1);
		 * odb.commit();
		 * 
		 * module1.setAuthor("new author of module 3"); odb.store(module1);
		 * odb.commit();
		 * 
		 * module1.setAuthor("new author of module 4"); odb.store(module1);
		 * odb.commit();
		 */

		Objects modules = odb.getObjects(Module.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, modules.size());
		assertEquals(2, tags.size());
		assertEquals(2, versions.size());

	}

	public void test3() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10010);
		ODB odb = server.openClient(baseName);

		Module module1 = buildModule("a1", "description 1111", "www.neodatis.org");
		Module module2 = buildModule("a2", "description 2222", "www.oracle.com");

		odb.store(module1);
		odb.store(module2);

		module1.setAuthor("new author of module 1");
		module1.addTopic(new Tag("new topic 1"));
		module1.addTopic(new Tag("new topic 2"));

		odb.store(module1);
		odb.commit();

		module1.setAuthor("new author of module 2");
		module1.addTopic(new Tag("new topic 3"));
		module1.addTopic(new Tag("new topic 4"));
		odb.store(module1);
		odb.commit();

		module1.setAuthor("new author of module 3");
		module1.addTopic(new Tag("new topic 5"));
		module1.addTopic(new Tag("new topic 6"));

		odb.store(module1);
		odb.commit();

		module1.setAuthor("new author of module 4");
		odb.store(module1);
		module1.addTopic(new Tag("new topic 7"));
		module1.addTopic(new Tag("new topic 8"));

		odb.commit();

		Objects modules = odb.getObjects(Module.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, modules.size());
		assertEquals(8, tags.size());
		assertEquals(2, versions.size());

	}

	public void test2Function() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10011);
		ODB odb = server.openClient(baseName);
		Function f1 = new Function("f1");
		Function f2 = new Function("f2");
		odb.store(f1);
		odb.store(f2);

		odb.store(f1);
		odb.commit();

		Objects functions = odb.getObjects(Function.class);

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, functions.size());

	}

	public void test2FunctionLocal() {

		String baseName = getBaseName();
		ODB odb = ODBFactory.open(baseName);
		Function f1 = new Function("f1");
		Function f2 = new Function("f2");
		odb.store(f1);
		odb.store(f2);

		odb.store(f1);
		odb.commit();

		Objects functions = odb.getObjects(Function.class);

		odb.close();
		deleteBase(baseName);

		assertEquals(2, functions.size());

	}

	public ODB getClientServerOdb(ODBServer server, String baseName, boolean sameVm) {
		if (sameVm) {
			return server.openClient(baseName);
		}
		return ODBFactory.openClient("localhost", 10007, baseName);
	}

	public void test2SameVm() {
		internalTest(true);
	}

	public void test2ClientServer() {
		internalTest(false);
	}

	public void test2SameVm2() {
		internalTest2(true);
	}

	public void test2ClientServer2() {
		internalTest2(false);
	}

	private void internalTest(boolean sameVm) {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10007);
		if (!sameVm) {
			server.startServer(true);
		}
		ODB odb = getClientServerOdb(server, baseName, sameVm);
		println(odb.getName());

		Module module1 = buildModule("a1", "description 1111", "www.neodatis.org");
		Module module2 = buildModule("a2", "description 2222", "www.oracle.com");

		odb.store(module1);
		odb.store(module2);

		odb.close();
		odb = server.openClient(baseName);
		println(odb.getName());

		module1.setAuthor("new author of module 1");
		odb.store(module1);
		odb.close();

		// Check number of objects
		odb = server.openClient(baseName);
		Objects modules = odb.getObjects(Module.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, modules.size());
		assertEquals(2, tags.size());
		assertEquals(2, versions.size());

	}

	private void internalTest2(boolean sameVm) {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10007);
		if (!sameVm) {
			server.startServer(true);
		}
		ODB odb = getClientServerOdb(server, baseName, sameVm);
		int size = 1000;
		List modules = new ArrayList();
		for (int i = 0; i < size; i++) {
			Module module = buildModule("author " + i, "description " + i, "www.neodatis.org" + i);
			odb.store(module);
			modules.add(module);
		}

		odb.close();
		odb = server.openClient(baseName);
		for (int i = 0; i < modules.size(); i++) {
			Module m = (Module) modules.get(i);
			m.setAuthor(m.getAuthor() + " updated - updated");
			odb.store(m);
		}
		odb.close();

		// Check number of objects
		odb = server.openClient(baseName);
		Objects storedModules = odb.getObjects(Module.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);

		odb.close();
		server.close();
		deleteBase(baseName);
		println(storedModules.size() + " stored modules");
		assertEquals(size, storedModules.size());
		assertEquals(size, tags.size());
		assertEquals(size, versions.size());

	}

	public void test2SameVmWithList() {
		internTestWithList(true);
	}

	public void test2ClientServerWithList() {
		internTestWithList(false);
	}

	public void test2SameVmWithListAndUpdate() {
		internTestWithListAndUpdate(true);
	}

	public void test2ClientServerWithListAndUpdate() {
		internTestWithListAndUpdate(false);
	}

	public void test2SameVmWithListAndUpdateCommit() {
		internTestWithListAndUpdateCommit(true);
	}

	public void test2ClientServerWithListAndUpdateCommit() {
		internTestWithListAndUpdateCommit(false);
	}

	private void internTestWithList(boolean sameVm) {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10007);
		if (!sameVm) {
			server.startServer(true);
		}
		ODB odb = getClientServerOdb(server, baseName, sameVm);

		ModuleWithList module1 = buildModuleWithList("a1", "description 1111", "www.neodatis.org");
		ModuleWithList module2 = buildModuleWithList("a2", "description 2222", "www.oracle.com");

		odb.store(module1);
		odb.store(module2);

		odb.close();
		odb = server.openClient(baseName);

		module1.setAuthor("new author of module 1");
		odb.store(module1);
		odb.close();

		// Check number of objects
		odb = server.openClient(baseName);
		Objects modules = odb.getObjects(ModuleWithList.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, modules.size());
		assertEquals(2, tags.size());
		assertEquals(2, versions.size());

	}

	private void internTestWithListAndUpdate(boolean sameVm) {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10007);
		if (!sameVm) {
			server.startServer(true);
		}
		ODB odb = getClientServerOdb(server, baseName, sameVm);

		ModuleWithList module1 = buildModuleWithList("a1", "description 1111", "www.neodatis.org");
		ModuleWithList module2 = buildModuleWithList("a2", "description 2222", "www.oracle.com");

		odb.store(module1);
		odb.store(module2);

		odb.close();
		odb = server.openClient(baseName);

		module1.setAuthor("new author of module 1");

		module1.addTopic(new Tag("new topic one"));
		module1.addTopic(new Tag("new topic two"));

		odb.store(module1);

		odb.close();

		// Check number of objects
		odb = server.openClient(baseName);
		Objects modules = odb.getObjects(ModuleWithList.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);
		Objects tags2 = odb.getObjects(new CriteriaQuery(Tag.class, Where.like("name", "new topic%")));

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, modules.size());
		assertEquals(4, tags.size());
		assertEquals(2, tags2.size());
		assertEquals(2, versions.size());

	}

	private void internTestWithListAndUpdateCommit(boolean sameVm) {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		ODBServer server = ODBFactory.openServer(10007);
		if (!sameVm) {
			server.startServer(true);
		}
		ODB odb = getClientServerOdb(server, baseName, sameVm);

		ModuleWithList module1 = buildModuleWithList("a1", "description 1111", "www.neodatis.org");
		ModuleWithList module2 = buildModuleWithList("a2", "description 2222", "www.oracle.com");

		odb.store(module1);
		odb.store(module2);

		odb.commit();

		module1.setAuthor("new author of module 1");

		module1.addTopic(new Tag("new topic one"));
		module1.addTopic(new Tag("new topic two"));

		odb.store(module1);

		odb.close();

		// Check number of objects
		odb = server.openClient(baseName);
		Objects modules = odb.getObjects(ModuleWithList.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);
		Objects tags2 = odb.getObjects(new CriteriaQuery(Tag.class, Where.like("name", "new topic%")));

		odb.close();
		server.close();
		deleteBase(baseName);

		assertEquals(2, modules.size());
		assertEquals(4, tags.size());
		assertEquals(2, tags2.size());
		assertEquals(2, versions.size());

	}

	private Module buildModule(String author, String description, String homePage) {
		Module module = new Module();

		module.setAuthor(author);
		module.setDateTime(new Date());
		module.setDescription(description);
		module.setExamplesUrl("exampleUrl");
		module.setHomePageUrl(homePage);
		module.setMavenArtifactUrl("maven url");
		module.setSourceCodeExamplesUrl("src");
		module.setSourceCodeUrl("src2");
		module.setTitle("My Title");

		Tag t1 = new Tag("tag1-" + author);
		Set set1 = new HashSet<Tag>();
		set1.add(t1);
		module.setTopics(set1);

		Version v1 = new Version("v1-" + author);
		Set set2 = new HashSet<Version>();
		set2.add(v1);
		module.setVersions(set2);

		return module;

	}

	private ModuleWithList buildModuleWithList(String author, String description, String homePage) {
		ModuleWithList module = new ModuleWithList();

		module.setAuthor(author);
		module.setDateTime(new Date());
		module.setDescription(description);
		module.setExamplesUrl("exampleUrl");
		module.setHomePageUrl(homePage);
		module.setMavenArtifactUrl("maven url");
		module.setSourceCodeExamplesUrl("src");
		module.setSourceCodeUrl("src2");
		module.setTitle("My Title");

		Tag t1 = new Tag("tag1-" + author);
		List<Tag> set1 = new ArrayList<Tag>();
		set1.add(t1);
		module.setTopics(set1);

		Version v1 = new Version("v1-" + author);
		List set2 = new ArrayList<Version>();
		set2.add(v1);
		module.setVersions(set2);

		return module;

	}
}
