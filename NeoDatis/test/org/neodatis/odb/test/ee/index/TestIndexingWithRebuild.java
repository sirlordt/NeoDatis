package org.neodatis.odb.test.ee.index;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.layers.layer3.engine.AbstractObjectWriter;
import org.neodatis.odb.impl.tool.LogUtil;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;

/**
 * Junit to test indexing an object when the index field is an object and not a
 * native attribute
 */
public class TestIndexingWithRebuild extends ODBTest {

	public void test1() throws Exception {
		String baseName = "test-with-rebuild.neodatis";
		//deleteBase(baseName);
		ODB odb = open(baseName);
		String [] fields = {"uuid", "createdAt", "parent", "active"};
		if (!odb.getClassRepresentation(Soul.class).existIndex("Csoul-index")) {
			odb.getClassRepresentation(Soul.class).addIndexOn("Csoul-index", fields, true);
		} else {
			odb.getClassRepresentation(Soul.class).rebuildIndex ("Csoul-index", true);
		}
		if (!odb.getClassRepresentation(Body.class).existIndex("Cbody-index")) {
			odb.getClassRepresentation(Body.class).addIndexOn("Cbody-index", fields, true);
		} else {
			odb.getClassRepresentation(Body.class).rebuildIndex ("Cbody-index", true);
		}
		/*
		if (!odb.getClassRepresentation(CObject.class).existIndex("Cobject-index")) {
			odb.getClassRepresentation(CObject.class).addIndexOn("Cobject-index", fields, true);
		} else {
			odb.getClassRepresentation(CObject.class).rebuildIndex ("Cobject-index", true);
		}
		if (!odb.getClassRepresentation(Item.class).existIndex("Citem-index")) {
			odb.getClassRepresentation(Item.class).addIndexOn("Citem-index", fields, true);
		} else {
			odb.getClassRepresentation(Item.class).rebuildIndex ("Citem-index", true);
		}*/
		odb.close();
		
		
		odb = open(baseName);
		int size = 10;
		for(int i=0;i<size;i++){
			odb.store(new Soul("soul"+i, "parent-soul-"+i, new Date(), "objectType-soul-"+i, Boolean.TRUE));
			odb.store(new Body("body"+i, "parent-body-"+i, new Date(), "objectType-body-"+i, Boolean.TRUE));
			odb.store(new Item("item"+i, "parent-item"+i, new Date(), "objectType-parent-"+i, Boolean.TRUE));
			odb.store(new Soul("cobject"+i, "parent-cobject-"+i, new Date(), "objectType-cobject-"+i, Boolean.TRUE));
		}
			
		odb.close();
	}	
	
	public void testDelete(){
		String baseName = getBaseName();
		
		ODB odb = open(baseName);
		odb.store(new Function("function1"));
		odb.store(new Function("function2"));
		odb.close();
		
		odb = open(baseName);
		int i=1;
		Objects<Function> functions =  odb.getObjects(Function.class);
		while(functions.hasNext()){
			odb.delete(functions.next());
			odb.store(new Function("New Function " + i));
			i++;
		}
		functions =  odb.getObjects(Function.class);
		assertEquals(2, functions.size());
		odb.close();
	}
	public void testDelete2(){
		String baseName = getBaseName();
		int size = 1000;
		ODB odb = open(baseName);
		for(int i=0;i<size;i++){
			odb.store(new Function("function"+i));
		}
		odb.close();
		
		odb = open(baseName);
		int i=1;
		Objects<Function> functions =  odb.getObjects(Function.class);
		while(functions.hasNext()){
			odb.delete(functions.next());
			odb.store(new Function("New Function " + i));
			i++;
		}
		functions =  odb.getObjects(Function.class);
		assertEquals(size, functions.size());
		odb.close();
	}
}
