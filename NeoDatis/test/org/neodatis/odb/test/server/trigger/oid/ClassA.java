/**
 * 
 */
package org.neodatis.odb.test.server.trigger.oid;

/**
 * @author olivier
 *
 */
public class ClassA {
	protected String name;
	protected ClassB b; 
	
	public ClassA(String name){
		this.name = name;
	}
	
	public ClassB getB(){
		return b;
	}

}
