package org.neodatis.odb.test.fromusers.kasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kasper Benjamin Hansen (created December 2008)
 */
public class Product implements Serializable {

	private static int interval = 15;

	private Long id;

	private Provider provider;

	private ProductCategory productCategory;

	/**
	 * Specifies who is providing this product
	 */
	private List<Resource> resources;

	public Product() {
		resources = new ArrayList<Resource>();
	}

	public static int getInterval() {
		return interval;
	}

	public static void setInterval(int interval) {
		Product.interval = interval;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}
