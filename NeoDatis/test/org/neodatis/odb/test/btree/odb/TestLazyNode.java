package org.neodatis.odb.test.btree.odb;

import org.neodatis.odb.test.ODBTest;

public class TestLazyNode extends ODBTest {

	public void testEmpty() {
		// to avoid junit junit.framework.AssertionFailedError: No tests found
		// in ...
	}

	/*
	 * public void test1() throws Exception {
	 * 
	 * deleteBase("node.neodatis"); ODB odb = open("node.neodatis");
	 * 
	 * IBTree tree = new InMemoryBTree("default",3); IBTreeNode node = new
	 * InMemoryBTreeNode(tree); node.setKeyAndValueAt(new
	 * KeyAndValue("KEY 1","key 1"),0); node.setNbKeys(1);
	 * 
	 * node.setChild(0, new InMemoryNode(OIDFactory.buildObjectOID(2), 2));
	 * 
	 * node.setKeyAndValue(1, "KEY 2","key 2"); node.setNbKeys(2);
	 * 
	 * node.setChild(1, new InMemoryNode(OIDFactory.buildObjectOID(3), 2));
	 * 
	 * odb.store(node); odb.close();
	 * 
	 * odb = open("node.neodatis"); InMemoryNode ln2 = (InMemoryNode)
	 * odb.getObjects(InMemoryNode.class).getFirst();
	 * 
	 * assertEquals(2, ln2.getNbKeys());
	 * 
	 * assertEquals("KEY 1", ln2.getKey(0)); assertEquals(2,
	 * ln2.getChild(0).getOid().getObjectId());
	 * 
	 * assertEquals("KEY 2", ln2.getKey(1)); assertEquals(3,
	 * ln2.getChild(1).getOid().getObjectId()); odb.close();
	 * 
	 * } /* public void test2() throws Exception {
	 * 
	 * deleteBase("node.neodatis"); ODB odb = open("node.neodatis");
	 * 
	 * InMemoryNode node = new InMemoryNode(OIDFactory.buildObjectOID(1), 2);
	 * node.setKeyAndValue(0, "KEY 1","key 1"); node.setNbKeys(1);
	 * 
	 * node.setChild(0, new InMemoryNode(OIDFactory.buildObjectOID(2), 2));
	 * 
	 * odb.store(node);
	 * 
	 * node.setKeyAndValue(1, "KEY 2","key 2"); node.setNbKeys(2);
	 * 
	 * node.setChild(1, new InMemoryNode(OIDFactory.buildObjectOID(3), 2));
	 * node.setParent(new InMemoryNode(OIDFactory.buildObjectOID(4),2));
	 * odb.store(node);
	 * 
	 * odb.store(node);
	 * 
	 * odb.close();
	 * 
	 * odb = open("node.neodatis"); InMemoryNode ln2 = (InMemoryNode)
	 * odb.getObjects(InMemoryNode.class).getFirst();
	 * 
	 * assertEquals(2, ln2.getNbKeys());
	 * 
	 * assertEquals("KEY 1", ln2.getKey(0)); assertEquals(2,
	 * ln2.getChild(0).getOid().getObjectId());
	 * 
	 * assertEquals("KEY 2", ln2.getKey(1)); assertEquals(3,
	 * ln2.getChild(1).getOid().getObjectId()); assertEquals(4,
	 * ln2.getParent().getOid().getObjectId()); odb.close();
	 * 
	 * }
	 * 
	 * public void test3() throws Exception {
	 * 
	 * deleteBase("node.neodatis"); ODB odb = open("node.neodatis");
	 * 
	 * InMemoryNode node1 = new InMemoryNode(OIDFactory.buildObjectOID(1), 2);
	 * node1.setKeyAndValue(0, "KEY 1 1","key 1 1"); node1.setKeyAndValue(1,
	 * "KEY 1 2","key 1 2"); node1.setKeyAndValue(2, "KEY 1 3","key 1 3");
	 * node1.setNbKeys(3);
	 * 
	 * InMemoryNode node2 = new InMemoryNode(OIDFactory.buildObjectOID(2), 2);
	 * node2.setKeyAndValue(0, "KEY 2 1","key 2 1"); node2.setKeyAndValue(1,
	 * "KEY 2 2","key 2 2"); node2.setKeyAndValue(2, "KEY 2 3","key 2 3");
	 * node2.setNbKeys(3);
	 * 
	 * odb.store(node1); odb.store(node2);
	 * 
	 * node1.setKeyAndValue(0, "KEY 3 1","key 3 1"); node1.setKeyAndValue(1,
	 * "KEY 3 2","key 3 2");
	 * 
	 * odb.store(node1); odb.close();
	 * 
	 * odb = open("node.neodatis"); Objects objects =
	 * odb.getObjects(InMemoryNode.class); assertEquals(2, objects.size());
	 * //println("nb inplace updates = " + ObjectWriter.getNbInPlaceUpdates());
	 * //println("nb normal updates = " + ObjectWriter.getNbNormalUpdates());
	 * 
	 * odb.close(); }
	 * 
	 * public void test4() throws Exception { ObjectWriter.resetNbUpdates();
	 * deleteBase("node.neodatis"); ODB odb = open("node.neodatis");
	 * 
	 * InMemoryNode node1 = new InMemoryNode(OIDFactory.buildObjectOID(1), 2);
	 * node1.setKeyAndValue(0, new Integer("1"),"1"); node1.setKeyAndValue(1,
	 * new Integer("2"),"2"); node1.setKeyAndValue(2, new Integer("3"),"3");
	 * node1.setNbKeys(3);
	 * 
	 * InMemoryNode node2 = new InMemoryNode(OIDFactory.buildObjectOID(2), 2);
	 * node2.setKeyAndValue(0, new Integer("4"),"4"); node2.setKeyAndValue(1,
	 * new Integer("5"),"5"); node2.setKeyAndValue(2, new Integer("6"),"6");
	 * node2.setNbKeys(3);
	 * 
	 * odb.store(node1); odb.store(node2);
	 * 
	 * node1.setKeyAndValue(0, new Integer("10"),"10"); node1.setKeyAndValue(1,
	 * new Integer("11"),"10");
	 * 
	 * odb.store(node1); odb.close();
	 * 
	 * odb = open("node.neodatis"); Objects objects =
	 * odb.getObjects(InMemoryNode.class); assertEquals(2, objects.size());
	 * //println("nb inplace updates = " + ObjectWriter.getNbInPlaceUpdates());
	 * //println("nb normal updates = " + ObjectWriter.getNbNormalUpdates());
	 * assertEquals(0, ObjectWriter.getNbInPlaceUpdates()); assertEquals(1,
	 * ObjectWriter.getNbNormalUpdates()); odb.close();
	 * 
	 * }
	 * 
	 * public void testUpdateArrayWithNonNativeObjectDoesNotInPlaceUpdate()
	 * throws Exception { ObjectWriter.resetNbUpdates();
	 * deleteBase("node.neodatis"); ODB odb = open("node.neodatis");
	 * 
	 * InMemoryNode node1 = new InMemoryNode(OIDFactory.buildObjectOID(1), 2);
	 * node1.setKeyAndValue(0, new Integer(1),"1"); node1.setKeyAndValue(1, new
	 * Integer(2),"2"); node1.setKeyAndValue(2, new Integer(3),"3");
	 * node1.setNbKeys(3);
	 * 
	 * InMemoryNode node2 = new InMemoryNode(OIDFactory.buildObjectOID(2), 2);
	 * node2.setKeyAndValue(0, new Integer(4),"4"); node2.setKeyAndValue(1, new
	 * Integer(5),"5"); node2.setKeyAndValue(2, new Integer(6),"6");
	 * node2.setNbKeys(3);
	 * 
	 * odb.store(node1); odb.store(node2);
	 * 
	 * node1.setKeyAndValue(0, new Integer(10),"10"); node1.setKeyAndValue(1,
	 * new Integer(11),"11");
	 * 
	 * odb.store(node1); odb.close();
	 * 
	 * odb = open("node.neodatis"); Objects objects =
	 * odb.getObjects(InMemoryNode.class); assertEquals(2, objects.size());
	 * //println("nb inplace updates = " + ObjectWriter.getNbInPlaceUpdates());
	 * //println("nb normal updates = " + ObjectWriter.getNbNormalUpdates());
	 * assertEquals(0, ObjectWriter.getNbInPlaceUpdates()); assertEquals(1,
	 * ObjectWriter.getNbNormalUpdates()); odb.close();
	 * 
	 * }
	 * 
	 * public void test6() throws Exception { ObjectWriter.resetNbUpdates();
	 * deleteBase("node.neodatis"); ODB odb = open("node.neodatis"); int size =
	 * 50; InMemoryNode node = null; for(int i=0;i<size;i++){ node = new
	 * InMemoryNode(OIDFactory.buildObjectOID((i+1)), 2); node.setKeyAndValue(0,
	 * new Integer(i*3+1),null); node.setKeyAndValue(1, new
	 * Integer(i*3+2),null); node.setKeyAndValue(2, new Integer(i*3+3),null);
	 * node.setNbKeys(3); node.setParent(null); odb.store(node); } odb.close();
	 * 
	 * odb = open("node.neodatis"); Objects objects =
	 * odb.getObjects(InMemoryNode.class);
	 * 
	 * 
	 * int i=1; int size2 = 10; for(int j=0;j<size2;j++){ objects.reset();
	 * i=j+1; while(objects.hasNext()){ node = (InMemoryNode) objects.next();
	 * node.setKeyAndValue(0, new Integer(i*3+1),null); node.setKeyAndValue(1,
	 * new Integer(i*3+2),null); node.setKeyAndValue(2, new
	 * Integer(i*3+3),null);
	 * 
	 * node.setNbKeys(1); node.setParent(null);
	 * 
	 * odb.store(node); i++; } } assertEquals(size, objects.size());
	 * //println("nb inplace updates = " + ObjectWriter.getNbInPlaceUpdates());
	 * //println("nb normal updates = " + ObjectWriter.getNbNormalUpdates()); //
	 * Node have array of Comparable which is not native so it does not have a
	 * fix size => nb in place update is 0 assertEquals(0,
	 * ObjectWriter.getNbInPlaceUpdates()); assertEquals(size2*size,
	 * ObjectWriter.getNbNormalUpdates()); odb.close();
	 * 
	 * 
	 * odb = open("node.neodatis"); objects =
	 * odb.getObjects(InMemoryNode.class); assertEquals(size, objects.size());
	 * odb.close();
	 * 
	 * }
	 * 
	 * public void test7() throws Exception { ObjectWriter.resetNbUpdates();
	 * deleteBase("node.neodatis"); ODB odb = open("node.neodatis"); int size =
	 * 50; InMemoryNode node = null; for(int i=0;i<size;i++){ node = new
	 * InMemoryNode(OIDFactory.buildObjectOID((i+1)), 2); node.setKeyAndValue(0,
	 * new Integer(i*3+1),"value "+i*3+1); node.setKeyAndValue(1, new
	 * Integer(i*3+2),"value "+i*3+2); node.setKeyAndValue(2, new
	 * Integer(i*3+3),"value "+i*3+3); node.setNbKeys(3); node.setParent(null);
	 * 
	 * odb.store(node); } odb.close();
	 * 
	 * odb = open("node.neodatis"); Objects objects =
	 * odb.getObjects(InMemoryNode.class);
	 * 
	 * 
	 * int i=1; int size2 = 10; for(int j=0;j<size2;j++){ objects.reset();
	 * i=j+1; while(objects.hasNext()){ node = (InMemoryNode) objects.next();
	 * node.setKeyAndValue(0, new Integer(i*3+1),"value "+(i*3+1));
	 * node.setKeyAndValue(1, new Integer(i*3+2),"value "+(i*3+2));
	 * node.setKeyAndValue(2, new Integer(i*3+3),"value "+(i*3+3));
	 * 
	 * node.setNbKeys(1); node.setParent(null);
	 * 
	 * odb.store(node); i++; } } assertEquals(size, objects.size());
	 * //println("nb inplace updates = " + ObjectWriter.getNbInPlaceUpdates());
	 * //println("nb normal updates = " + ObjectWriter.getNbNormalUpdates());
	 * assertEquals(0, ObjectWriter.getNbInPlaceUpdates());
	 * assertEquals(size2*size, ObjectWriter.getNbNormalUpdates()); odb.close();
	 * 
	 * 
	 * odb = open("node.neodatis"); objects =
	 * odb.getObjects(InMemoryNode.class); assertEquals(size, objects.size());
	 * odb.close();
	 * 
	 * }
	 */

}
