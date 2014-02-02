/**
 * 
 */
package org.neodatis.odb.test.vo.arraycollectionmap.catalog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author olivier
 * 
 */
public class Catalog {
	private String name;
	private List<ProductCategory> categories;

	public Catalog(String name) {
		this.name = name;
		categories = new ArrayList<ProductCategory>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<ProductCategory> categories) {
		this.categories = categories;
	}

}
