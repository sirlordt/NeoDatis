/**
 * 
 */
package org.neodatis.odb.test.fromusers.martijn;

/**
 * @author olivier
 *
 */
public class Item {
	private String name;
	private Category category;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Item(String name, Category category) {
		super();
		this.name = name;
		this.category = category;
	}
	
	
}
