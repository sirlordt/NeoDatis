package org.neodatis.odb.test.fromusers.kasper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kasper Benjamin Hansen (created December 2008)
 */
public class Provider implements Serializable {

	private Long id;

	private Settings settings;

	/**
	 * The total list of working Resources (employees)
	 */
	private List<Resource> resources;

	/**
	 * The total list of Products offered
	 */
	private List<Product> products;

	private List<ProductCategory> productCategories;

	public Provider() {
		products = new ArrayList<Product>();
		productCategories = new ArrayList<ProductCategory>();
		resources = new ArrayList<Resource>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public List<ProductCategory> getProductCategories() {
		return productCategories;
	}

	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

}
