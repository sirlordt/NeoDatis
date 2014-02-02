package org.neodatis.odb.test.performance;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.instance.FullInstantiationHelper;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.layers.layer3.buffer.MultiBufferedIO;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.core.transaction.ReferenceQueueThread;
import org.neodatis.odb.impl.tool.MemoryMonitor;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.wrappers.OdbTime;

public class TestBatchInsert extends ODBTest {
	public static int TEST_SIZE = 2000000;
	public static final String ODB_FILE_NAME = "perf-batch.neodatis";

	public void testEmpty() {
		// to avoid junit junit.framework.AssertionFailedError: No tests found
		// in ...
	}

	public void t1est1(boolean force) throws Exception {
		if (!force ) {
			return;
		}
		//OdbConfiguration.setUseCache(false);
		deleteBase(ODB_FILE_NAME);
		//OdbConfiguration.set
		ODB odb = open(ODB_FILE_NAME);
		
		for(int i=0;i<TEST_SIZE;i++){
			odb.store(getSimpleObjectInstance(i));
			if(i%10000==0){
				MemoryMonitor.displayCurrentMemory(i+" objects", false);
				odb.close();
				odb = open(ODB_FILE_NAME);
			}
		}
		odb.close();
		
	}
	
	public void t1estSelect() throws Exception {
		//OdbConfiguration.setUseCache(false);
		//deleteBase(ODB_FILE_NAME);
		//OdbConfiguration.set
		ODB odb = open(ODB_FILE_NAME);
		
		Objects<Function> functions = odb.getObjects(new CriteriaQuery(SimpleObject.class,Where.equal("name", "Bonjour, comment allez vous?1000000")));
		odb.close();
		assertEquals(1, functions.size());
		
	}

	private SimpleObject getSimpleObjectInstance(int i) {
		SimpleObject so = new SimpleObject();
		so.setDate(new Date());
		so.setDuration(i);
		so.setName("Bonjour, comment allez vous?" + i);
		return so;
	}


	public static void main(String[] args) throws Exception {
		// Thread.sleep(15000);
		// OdbConfiguration.setMessageStreamerClass(HessianMessageStreamer.class);
		TestBatchInsert pt = new TestBatchInsert();
		pt.t1est1(true);
	}

}

