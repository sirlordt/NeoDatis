package org.neodatis.odb.test.performance;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

public class TestSeveralDatabases extends ODBTest {

	/**
	 * Test opening x different databases
	 * 
	 */
	public void test1() {
		String baseName = getBaseName();

		int size = 1000;

		for (int i = 0; i < size; i++) {
			String name = baseName + "_" + i;

			ODB odb = open(name);
			odb.store(new Function("Function jdlksjflksjflkdsjlfdsjlfjsdfjsdajfskd " + i));
			odb.close();

			odb = open(name);
			Objects<Function> ffs = odb.getObjects(Function.class);
			odb.close();
			
			assertEquals(1, ffs.size());
			
			if(i%100==0){
				MemoryMonitor.displayCurrentMemory(""+i, false);
			}
		}
	}
	
	public void test2MultiThread() {
		String baseName = getBaseName();

		int size = 1000;

		ODB odb = open(baseName);
		odb.store(new Function("Function jdlksjflksjflkdsjlfdsjlfjsdfjsdajfskd " + baseName));
		odb.close();
	
		
		for (int i = 0; i < size; i++) {
			String name = baseName + "_" + i;

			Thread t = new ThreadToOpenDb(name);
			t.start();
			
			if(i%100==0){
				MemoryMonitor.displayCurrentMemory(""+i, false);
			}
		}
	}

	class ThreadToOpenDb extends Thread{
		
		protected String name;
		
		public ThreadToOpenDb(String name){
			this.name = name;
		}
		@Override
		public void run() {
			try{
				//System.out.println("Opening " + name);
				ODB odb = open(name);
				odb.store(new Function("Function jdlksjflksjflkdsjlfdsjlfjsdfjsdajfskd " + name));
				odb.close();

				odb = open(name);
				Objects<Function> ffs = odb.getObjects(Function.class);
				odb.close();
			}catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
