package org.neodatis.odb.test.fromusers.kasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A ProductCategory is a collection of products. Examples on categories could
 * be; Gentleman, Ladies & kids.
 * 
 * @author Kasper Benjamin Hansen (created April 2009)
 * 
 */
public class ProductCategory implements Serializable {

	private Long id;

	private List<Product> products;

	public ProductCategory() {
		products = new ArrayList<Product>();
	}

	public static ProductCategory getProductCategory(List<ProductCategory> productCategories, Long id) {
		for (ProductCategory productCategory : productCategories) {
			if (productCategory.getId().compareTo(id) == 0)
				return productCategory;
		}

		return null;
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
