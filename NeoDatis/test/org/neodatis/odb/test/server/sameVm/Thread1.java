package org.neodatis.odb.test.server.sameVm;

import org.neodatis.odb.ODB;

public class Thread1 extends Thread{
	TestConcurrentAccess test;
	
	public Thread1(TestConcurrentAccess access){
		this.test = access;
	}
	public void run() {
		ODB odb = test.createClient();
		Data d = new Data();
		odb.store(d);
		odb.close();
		System.out.println("debug: inserted " + d.id );
		
	}


}
