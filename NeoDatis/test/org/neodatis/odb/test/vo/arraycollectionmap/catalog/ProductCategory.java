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
public class ProductCategory {
	private String name;
	private List<Product> products;

	public ProductCategory(String name) {
		super();
		this.name = name;
		products = new ArrayList<Product>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

}
