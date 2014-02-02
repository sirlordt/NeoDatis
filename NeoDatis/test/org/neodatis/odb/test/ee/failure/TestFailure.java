package org.neodatis.odb.test.ee.failure;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;
import org.neodatis.tool.IOUtil;

public class TestFailure  extends ODBTest{
	
	public void t1est1(){
		String baseName = "failure.neodatis";
		deleteBase(baseName);
		ODB odb = open(baseName);
		
		Profile p = new Profile("profile");
		p.addFunction(new Function("function1"));
		p.addFunction(new Function("function2"));
		odb.store(p);
		odb.store(new Function("function3"));
		odb.store(new Function("function4"));
		odb.store(new Function("function5"));
		odb.close();
		
		odb = open(baseName);
		Objects<Profile> profiles = odb.getObjects(Profile.class);
		
		Profile profile = profiles.getFirst();
		
		profile.setName("profile - updated");
		profile.getFunctions().get(0).setName("function1 - updated");
		profile.getFunctions().get(1).setName("function2 - updated");
		odb.store(profile);
		odb.commit();
		
	}
	
	public void t1est2(){
		String baseName = "failure.neodatis";
		deleteBase(baseName);
		ODB odb = open(baseName);
		
		Profile p = new Profile("profile");
		p.addFunction(new Function("function1"));
		p.addFunction(new Function("function2"));
		odb.store(p);
		odb.store(new Function("function3"));
		odb.store(new Function("function4"));
		odb.store(new Function("function5"));
		odb.close();
		
		odb = open(baseName);
		Objects<Profile> profiles = odb.getObjects(Profile.class);
		
		Profile profile = profiles.getFirst();
		odb.delete(profile.getFunctions().get(0));
		odb.delete(profile.getFunctions().get(1));
		profile.setName("profile - updated");
		//profile.getFunctions().get(0).setName("function1 - updated");
		//profile.getFunctions().get(1).setName("function2 - updated");
		odb.store(profile);
		odb.commit();
		
	}
	
	public static void main(String[] args) {
		System.out.println("Main");
		TestFailure tf = new TestFailure();
		String baseName = "failure.neodatis";
		ODB odb = tf.open(baseName);
		System.out.println(odb.getObjects(Profile.class).getFirst().toString());
	}

}
