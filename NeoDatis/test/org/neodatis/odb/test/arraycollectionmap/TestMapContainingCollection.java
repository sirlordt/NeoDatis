package org.neodatis.odb.test.arraycollectionmap;

import java.util.Collection;
import java.util.Vector;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class TestMapContainingCollection extends ODBTest {

	public void test1() throws Exception {
		deleteBase("map-with-collections");
		ODB odb = null;

		odb = open("map-with-collections");
		MyMapObject o = new MyMapObject("test");
		Collection c = new Vector();
		c.add("ola");
		o.getMap().put("c", c);
		odb.store(o);
		odb.close();

		odb = open("map-with-collections");
		Objects os = odb.getObjects(MyMapObject.class);
		MyMapObject mmo = (MyMapObject) os.getFirst();
		odb.close();
		deleteBase("map-with-collections");
		assertEquals(o.getName(), mmo.getName());
		assertEquals(o.getMap().size(), mmo.getMap().size());
		assertEquals(o.getMap().get("c"), mmo.getMap().get("c"));
	}

	public void test2() throws Exception {

		deleteBase("map-with-collections");
		ODB odb = null;

		odb = open("map-with-collections");
		MyMapObject o = new MyMapObject("test");
		Collection c = new Vector();
		c.add(o);
		o.getMap().put("c", c);
		odb.store(o);
		odb.close();

		odb = open("map-with-collections");
		Objects os = odb.getObjects(MyMapObject.class);
		MyMapObject mmo = (MyMapObject) os.getFirst();
		odb.close();
		deleteBase("map-with-collections");
		assertEquals(o.getName(), mmo.getName());
		assertEquals(o.getMap().size(), mmo.getMap().size());
		Collection c1 = (Collection) o.getMap().get("c");
		Collection c2 = (Collection) mmo.getMap().get("c");

		assertEquals(c1.size(), c2.size());
		assertEquals(mmo, c2.iterator().next());
	}

	public void test3() throws Exception {

		// LogUtil.objectReaderOn(true);

		deleteBase("map-with-collections");
		ODB odb = null;

		odb = open("map-with-collections");
		MyMapObject o = new MyMapObject("test");
		Collection c = new Vector();
		c.add(o);
		Function f1 = new Function("function1");

		o.getMap().put("a", c);
		int size = 1;
		for (int i = 0; i < size; i++) {
			o.getMap().put("A" + new Integer(i), f1);
		}

		o.getMap().put("c", f1);

		println("RealMap" + o.getMap());

		odb.store(o);
		odb.close();

		odb = open("map-with-collections");
		Objects os = odb.getObjects(MyMapObject.class);
		MyMapObject mmo = (MyMapObject) os.getFirst();
		odb.close();
		deleteBase("map-with-collections");
		assertEquals(o.getName(), mmo.getName());

		assertEquals(size + 2, mmo.getMap().size());
		assertEquals(mmo, ((Collection) mmo.getMap().get("a")).iterator().next());
		assertEquals("function1", mmo.getMap().get("c").toString());

	}
}

class MyMapObject {
	private String name;
	private OdbHashMap<Object,Object> map;

	public MyMapObject(String name) {
		this.name = name;
		this.map = new OdbHashMap<Object, Object>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OdbHashMap<Object, Object> getMap() {
		return map;
	}

	public void setMap(OdbHashMap<Object, Object> map) {
		this.map = map;
	}

}