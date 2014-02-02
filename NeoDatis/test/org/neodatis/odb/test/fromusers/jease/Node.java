/**
 * 
 */
package org.neodatis.odb.test.fromusers.jease;

/**
 * @author olivier
 *
 */
public class Node {
	String id;
	Node parent;
	Node[] children = new Node[] {};

	public Node(String id){
		this.id = id;
	}
	public Node(){
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("node ").append(id).append("[");
		
		for(Node n:children){
			buffer.append(n.id).append(",");
		}
		buffer.append("]");
		return buffer.toString();
	}
}
