/**
 * 
 */
package org.neodatis.odb.test.enumeration;

/**
 * @author olivier
 * 
 */
public enum MyEnum {
	ATIVO("A"), BLOQUEADO("B"), FORA_DE_LINHA("FL");

	private String rep;

	private MyEnum(String rep) {
		this.rep = rep;
	}

	/*
	public String toString() {
		return rep;
	}*/

}
