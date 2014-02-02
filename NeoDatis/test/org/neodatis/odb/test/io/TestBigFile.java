package org.neodatis.odb.test.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.odb.test.vo.login.User;

public class TestBigFile extends ODBTest {

	public void test1() throws IOException {
		RandomAccessFile raf = new RandomAccessFile(ODBTest.DIRECTORY + "testBigFile", "rw");
		long l = 2 * 1024000;
		println(l);
		raf.seek(l);
		for (int i = 0; i < 1024000; i++) {
			raf.write((byte) 0);
		}
		raf.write((byte) 0);
		raf.close();

		/*
		 * for(long i=0;i<Long.MAX_VALUE;i++){ println(i); raf.write((byte)i); }
		 * raf.close();
		 * 
		 * raf = new RandomAccessFile("testBigFile","rw"); long l =
		 * raf.length(); println(l); raf.close();
		 */
	}

	private Object getUserInstance(int i) {
		Function login = new Function("login" + i);
		Function logout = new Function("logout" + i);
		List list = new ArrayList();
		list.add(login);
		list.add(logout);
		Profile profile = new Profile("operator" + i, list);
		User user = new User("olivier smadja" + i, "olivier@neodatis.com", profile);
		return user;
	}

	public void t2estBigFileWithOdb() throws IOException {
		int size1 = 10000;
		int size2 = 1000;

		String baseName = "big-file.neodatis";
		ODB odb = null;

		try {
			odb = open(baseName);
			/*
			 * if(!odb.getClassRepresentation(User.class).existIndex("user-index"
			 * )){
			 * odb.getClassRepresentation(User.class).addIndexOn("user-index",
			 * new String[]{"name"}, true);
			 * odb.getClassRepresentation(Profile.class
			 * ).addIndexOn("profile-index", new String[]{"name"}, true);
			 * odb.getClassRepresentation
			 * (Function.class).addIndexOn("function-index", new
			 * String[]{"name"}, true); }
			 */
			odb.close();

			int z = 0;
			for (int i = 0; i < size1; i++) {
				odb = open(baseName);
				for (int j = 0; j < size2; j++) {
					odb.store(getUserInstance(j));
					z++;
				}
				odb.close();
				println(i + "/" + size1 + " " + z + " objects");
			}

		} finally {
		}

	}

	public void t2estBigFileWithOdbSelect() throws IOException, InterruptedException {
		// OdbConfiguration.setUseIndex(false);
		String baseName = "big-file.neodatis";
		ODB odb = null;
		// Thread.sleep(20000);
		try {
			long start = System.currentTimeMillis();
			odb = open(baseName);
			IQuery q = new CriteriaQuery(Function.class, Where.equal("name", "login10000"));
			Objects<Function> functions = odb.getObjects(q, true, 0, 1);
			System.out.println(q.getExecutionPlan().getDetails());
			System.out.println(functions.size());
			println(System.currentTimeMillis() - start + "ms");

		} finally {
			if (odb != null) {
				odb.close();
			}
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		TestBigFile tt = new TestBigFile();
		// tt.t2estBigFileWithOdbSelect();
		tt.t2estBigFileWithOdb();
	}

}
