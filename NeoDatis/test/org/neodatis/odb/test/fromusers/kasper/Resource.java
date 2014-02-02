package org.neodatis.odb.test.fromusers.kasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kasper Benjamin Hansen (created December 2008)
 */
public class Resource implements Serializable {

	private Long id;

	/**
	 * A list of products that this resource can sell
	 */
	private List<Product> products;

	public Resource() {
		products = new ArrayList<Product>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

}