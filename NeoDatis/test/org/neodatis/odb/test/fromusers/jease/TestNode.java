/**
 * 
 */
package org.neodatis.odb.test.fromusers.jease;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestNode extends ODBTest {

	public static class Content extends Node {
		String title;
	}

	public static class Text extends Content {
		String text;
	}

	public static class Folder extends Content {
	}
	public void test0() {
		String baseName = getBaseName();
		boolean reconnect = OdbConfiguration.reconnectObjectsToSession();

		try {
			OdbConfiguration.setReconnectObjectsToSession(true);

			ODB odb = ODBFactory.open(baseName);

			Node root = new Node("root");
			Node child1 = new Node("c1");
			Node child2 = new Node("c2");

			child1.parent = root;
			child2.parent = root;
			root.children = new Node[] { child1, child2 };
			odb.store(root);
			odb.store(child1);
			odb.store(child2);
			odb.commit();

			root.children = new Node[] { child2, child1 };
			odb.store(root);
			odb.commit();

			odb.close();

			odb = ODBFactory.open(baseName);
			Objects<Node> nodes = odb.getObjects(Node.class);
			odb.close();

			assertEquals(3, nodes.size());

			Node n = nodes.getFirst();
			assertEquals(2, n.children.length);
		} finally {
			OdbConfiguration.setReconnectObjectsToSession(reconnect);
		}

	}

	public void test1() {
		String baseName = getBaseName();

		ODB odb = ODBFactory.open(baseName);

		Node root = new Node("root");
		Node child1 = new Node("c1");
		Node child2 = new Node("c2");

		child1.parent = root;
		child2.parent = root;
		root.children = new Node[] { child1, child2 };
		odb.store(root);
		odb.store(child1);
		odb.store(child2);
		odb.commit();

		root.children = new Node[] { child2, child1 };
		odb.store(root);
		odb.store(child1);
		odb.store(child2);
		odb.commit();

		odb.close();

		odb = ODBFactory.open(baseName);
		Objects<Node> nodes = odb.getObjects(Node.class);
		odb.close();

		assertEquals(3, nodes.size());

		Node n = nodes.getFirst();
		assertEquals(2, n.children.length);
		println(n);

	}

	public void test12() {
		String baseName = getBaseName();

		ODB odb = ODBFactory.open(baseName);

		Node root = new Node("root");
		Node child1 = new Node("c1");
		Node child2 = new Node("c2");

		child1.parent = root;
		child2.parent = root;
		root.children = new Node[] { child1, child2 };
		odb.store(root);
		odb.commit();

		root.children = new Node[] { child2, child1 };
		odb.store(root);
		odb.commit();

		odb.close();

		odb = ODBFactory.open(baseName);
		Objects<Node> nodes = odb.getObjects(new CriteriaQuery(Node.class, Where.equal("id", "root")));
		odb.close();

		assertEquals(1, nodes.size());

		Node n = nodes.getFirst();
		assertEquals(2, n.children.length);

		assertEquals("c2", n.children[0].id);
		assertEquals("c1", n.children[1].id);

	}

	public void test2() {
		boolean isReconnect = OdbConfiguration.reconnectObjectsToSession();
		
		try{
			OdbConfiguration.setReconnectObjectsToSession(false);
			String baseName = getBaseName();

			ODB odb = ODBFactory.open(baseName);

			Node root = new Node("root");
			Node child1 = new Node("c1");
			Node child2 = new Node("c2");

			child1.parent = root;
			child2.parent = root;
			root.children = new Node[] { child1, child2 };
			odb.store(root);
			odb.store(child1);
			odb.store(child2);
			odb.close();

			odb = ODBFactory.open(baseName);
			root.children = new Node[] { child2, child1 };
			odb.store(root);
			odb.store(child1);
			odb.store(child2);

			odb.close();

			odb = ODBFactory.open(baseName);

			Objects<Node> nodes = odb.getObjects(Node.class);
			odb.close();
			assertEquals(6, nodes.size());
			
		}finally{
			OdbConfiguration.setReconnectObjectsToSession(isReconnect);
		}

	}

	public void test3() {
		String baseName = getBaseName();

		ODB odb = ODBFactory.open(baseName);

		Folder root = new Folder();
		Text text = new Text();
		Folder folder = new Folder();

		// Comment out to see ClassCastException
		text.id = "text";
		folder.id = "folder";

		text.parent = root;
		folder.parent = root;
		root.children = new Node[] { text, folder };

		odb.store(root);
		odb.store(text);
		odb.store(folder);
		odb.commit();

		root.children = new Node[] { folder, text };
		odb.store(root);
		odb.store(text);
		odb.store(folder);
		odb.commit();

		odb.close();
		
		odb = ODBFactory.open(baseName);

		Objects<Node> nodes = odb.getObjects(new CriteriaQuery(Node.class).setPolymorphic(true));
		odb.close();
		assertEquals(3, nodes.size());

	}

}