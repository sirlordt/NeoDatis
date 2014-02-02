/**
 * 
 */
package org.neodatis.odb.test.ee.multithread;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 *
 */
public class TestMultiThreadNonExclusiveLocalODB extends ODBTest {
	
	
	public void test0() throws InterruptedException{
		if(!isLocal){
			return;
		}
		boolean isExclusive = OdbConfiguration.multiThreadExclusive();
		try{
			OdbConfiguration.setMultiThreadExclusive(false);
			OdbConfiguration.useMultiThread(true);
			String baseName = getBaseName();
			ODB odb = open(baseName);
			
			Thread t1 = new TestThread(odb);
			Thread t2 = new TestThread(odb);
			t1.start();
			t2.start();
			
			t1.join();
			t2.join();
			Objects<Function> functions = odb.getObjects(Function.class);
			assertEquals(2, functions.size());
		}finally{
			OdbConfiguration.setMultiThreadExclusive(isExclusive);
		}
	}
	
	public void test2() throws InterruptedException{
		if(!isLocal){
			return;
		}
		boolean isExclusive = OdbConfiguration.multiThreadExclusive();
		
		try{
			OdbConfiguration.useMultiThread(true);
			OdbConfiguration.setMultiThreadExclusive(false);
			int size = 50;
			Thread[] threads = new Thread[size];
			String baseName = getBaseName();
			ODB odb = open(baseName);
			
			for(int i=0;i<size;i++){
				threads[i] = new TestThread(odb);
				threads[i].start();
			}
			
			for(int i=0;i<size;i++){
				threads[i].join();
			}

			Objects<Function> functions = odb.getObjects(Function.class);
			assertEquals(size, functions.size());
			
		}finally{
			OdbConfiguration.useMultiThread(false);
			OdbConfiguration.setMultiThreadExclusive(isExclusive);
		}
		
	}

}
