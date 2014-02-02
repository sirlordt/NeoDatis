/**
 * 
 */
package org.neodatis.odb.test.fromusers.francisco;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.transaction.CacheFactory;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestReconnectCrossSessionCacheTest extends ODBTest{
	public void setUp() throws Exception {
		super.setUp();
		OdbConfiguration.setReconnectObjectsToSession(true);
	}
	public void tearDown() throws Exception {
		OdbConfiguration.setReconnectObjectsToSession(true);
	}

	public void test1(){
		if(!OdbConfiguration.reconnectObjectsToSession()){
			return;
		}
			
		String baseName = getBaseName();
		ODB odb = open(baseName);
		
		Module module1 = buildModule("a1", "description 1111", "www.neodatis.org");
		Module module2 = buildModule("a2", "description 2222", "www.oracle.com");
		

		odb.store(module1);
		odb.store(module2);
		
		module1.setAuthor("new author of module 1");
		odb.store(module1);
		odb.close();
		
		// Check number of objects
		odb = open(baseName);
		Objects modules = odb.getObjects(Module.class);
		Objects tags = odb.getObjects(Tag.class);
		Objects versions = odb.getObjects(Version.class);
		
		odb.close();
		deleteBase(baseName);
		
		assertEquals(2, modules.size());
		assertEquals(2, tags.size());
		assertEquals(2, versions.size());
		
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
		
	}
	
	public void testDelete(){
		if(!OdbConfiguration.reconnectObjectsToSession()){
			return;
		}
			
		String baseName = getBaseName();
		ODB odb = open(baseName);
		
		Module module1 = buildModule("a3", "description 3333", "www.neodatis.org");		
		odb.store(module1);
		odb.close();
		
		// Check number of objects
		odb = open(baseName);
		Objects modules = odb.getObjects(Module.class);
		assertEquals(1, modules.size());
		odb.delete(module1);
		odb.close();
		
		odb = open(baseName);
		modules = odb.getObjects(Module.class);
		odb.close();
		assertEquals(0, modules.size());
		
				
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
		
	}

	public ODB getClientServerOdb(ODBServer server, String baseName, boolean sameVm){
		if(sameVm){
			return server.openClient(baseName);
		}
		return ODBFactory.openClient("localhost",10007, baseName);
	}
	
	
	private Module buildModule(String author, String description, String homePage){
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
		
		Tag t1 = new Tag("tag1-"+author);
		Set set1 = new HashSet<Tag>();
		set1.add(t1);
		module.setTopics(set1);
		
		Version v1 = new Version("v1-"+author);
		Set set2 = new HashSet<Version>();
		set2.add(v1);
		module.setVersions(set2);
		
		return module;
		
	}
	
	
	public void testDisconnect() throws Exception {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function login = new Function("login");
		Function logout = new Function("logout");
		odb.store(login);
		odb.store(logout);
		odb.close();
		odb = open(baseName);
		Objects objects = odb.getObjects(new CriteriaQuery(Function.class));
		assertEquals(2, objects.size());
		Function f = (Function) objects.getFirst();
		odb.disconnect(f);
		// Storing after disconnect should create a new one
		OID oid3 = odb.store(f);
		odb.close();

		odb = open(baseName);
		objects = odb.getObjects(new CriteriaQuery(Function.class));
		odb.close();
		println(objects.size() + " objects");

		assertEquals(3, objects.size());

		odb = open(baseName);
		odb.reconnect(f);
		f.setName("This is a reconnected function!");
		odb.store(f);
		odb.close();

		odb = open(baseName);
		objects = odb.getObjects(new CriteriaQuery(Function.class));
		Function ff = (Function) odb.getObjectFromId(oid3);
		odb.close();
		assertEquals(3, objects.size());
		assertEquals("This is a reconnected function!", ff.getName());
		println(objects.size() + " objects");
		OdbConfiguration.setReconnectObjectsToSession(true);

	}

	public void testReconnect() {

		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function f1 = new Function("f1");
		odb.store(f1);
		odb.close();

		odb = open(baseName);
		f1.setName("Function 1");
		odb.store(f1);
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(Function.class);
		assertEquals(1, os.size());
		Function ff1 = (Function) os.getFirst();
		odb.close();

		assertEquals("Function 1", ff1.getName());
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());

	}

	public void testReconnectXXFunctions() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function f1 = new Function("f1");
		odb.store(f1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			f1.setName("Function " + i);
			odb.store(f1);
			odb.close();

			odb = open(baseName);
			assertEquals("Function " + i, f1.getName());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
	}

	public void testAutoReconnectXXFunctions() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		Function f1 = new Function("f1");
		odb.store(f1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			f1.setName("Function " + i);
			odb.store(f1);
			odb.close();

			odb = open(baseName);
			assertEquals("Function " + i, f1.getName());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
	}

	public void testReconnectUser() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user B", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		odb.store(user1);
		odb.close();

		odb = open(baseName);
		user1.setName("USER 11");
		odb.store(user1);
		odb.close();

		odb = open(baseName);
		Objects os = odb.getObjects(User.class);
		assertEquals(1, os.size());
		User uu1 = (User) os.getFirst();
		odb.close();

		assertEquals("USER 11", uu1.getName());
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
	}

	public void testReconnectXXUsers() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user A", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		OID oid = odb.store(user1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			user1.setName("USER " + i);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("USER " + i, u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
		
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
	}

	/**
	 * to test automatical reconnect
	 * 
	 */
	public void testAutoReconnectXXUsers() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user D", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		OID oid = odb.store(user1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			user1.setName("USER " + i);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("USER " + i, u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
		
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
	}

	public void testAutoReconnectXXUsersWithNullProfile() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}

		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user F", "user@neodatis.org", new Profile("profile1", new Function("f1")));
		OID oid = odb.store(user1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			user1.setName("USER " + i);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("USER " + i, u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(1, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
		}
		
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
	}

	public void testAutoReconnectXXUsersNoModificationWithClose() {
		if (!OdbConfiguration.reconnectObjectsToSession()) {
			return;
		}
		String baseName = getBaseName();
		deleteBase(baseName);
		ODB odb = open(baseName);

		User user1 = new User("user X", "user@neodatis.org", null);
		OID oid = odb.store(user1);
		odb.close();

		for (int i = 0; i < 1000; i++) {
			odb = open(baseName);
			odb.store(user1);
			odb.close();

			// check value
			odb = open(baseName);
			User u = (User) odb.getObjectFromId(oid);
			assertEquals("user X", u.getName());
			assertEquals(1, odb.count(new CriteriaQuery(User.class)).longValue());
			assertEquals(0, odb.count(new CriteriaQuery(Profile.class)).longValue());
			assertEquals(0, odb.count(new CriteriaQuery(Function.class)).longValue());
			odb.close();
			println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
		}
		println("Size of cache: " + CacheFactory.getCrossSessionCache(odb.getName()).size());
	}



}
