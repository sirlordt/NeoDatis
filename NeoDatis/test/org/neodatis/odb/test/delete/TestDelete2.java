/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.test.delete;

import java.math.BigDecimal;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.arraycollectionmap.catalog.Catalog;
import org.neodatis.odb.test.vo.arraycollectionmap.catalog.Product;
import org.neodatis.odb.test.vo.arraycollectionmap.catalog.ProductCategory;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;

public class TestDelete2 extends ODBTest {

	public void testDeleteListElements() {

		String baseName = getBaseName();

		ODB odb = open(baseName);

		Profile p = new Profile("name");
		p.addFunction(new Function("f1"));
		p.addFunction(new Function("f2"));
		p.addFunction(new Function("3"));

		odb.store(p);
		odb.close();

		odb = open(baseName);
		Objects objects = odb.getObjects(Profile.class);

		while (objects.hasNext()) {
			Profile profile = (Profile) objects.next();

			List functions = profile.getFunctions();
			for (int j = 0; j < functions.size(); j++) {
				odb.delete(functions.get(j));
			}
			odb.delete(profile);
		}
		odb.close();
	}

	public void testDeleteListElements2() {

		String baseName = getBaseName();

		ODB odb = open(baseName);

		Catalog catalog = new Catalog("Fnac");

		ProductCategory books = new ProductCategory("Books");
		books.getProducts().add(new Product("Book1", new BigDecimal(10.1)));
		books.getProducts().add(new Product("Book2", new BigDecimal(10.2)));
		books.getProducts().add(new Product("Book3", new BigDecimal(10.3)));

		ProductCategory computers = new ProductCategory("Computers");
		computers.getProducts().add(new Product("MacBook", new BigDecimal(1300.1)));
		computers.getProducts().add(new Product("BookBookPro", new BigDecimal(2000.2)));
		computers.getProducts().add(new Product("MacMini", new BigDecimal(1000.3)));

		catalog.getCategories().add(books);
		catalog.getCategories().add(computers);

		odb.store(catalog);
		odb.close();

		odb = open(baseName);
		Objects objects = odb.getObjects(Catalog.class);

		println(objects.size() + " catalog(s)");
		while (objects.hasNext()) {
			Catalog c = (Catalog) objects.next();

			List pCategories = c.getCategories();
			println(c.getCategories().size() + " product categories");
			for (int j = 0; j < pCategories.size(); j++) {
				ProductCategory pc = (ProductCategory) pCategories.get(j);
				println("\tProduct Category : " + pc.getName() + " : " + pc.getProducts().size() + " products");
				for (int k = 0; k < pc.getProducts().size(); k++) {
					Product p = pc.getProducts().get(k);
					println("\t\tProduct " + p.getName());
					odb.delete(p);
				}
				odb.delete(pc);
			}
			odb.delete(c);
		}
		odb.close();

		odb = open(baseName);
		Objects catalogs = odb.getObjects(Catalog.class);
		Objects productCategories = odb.getObjects(ProductCategory.class);
		Objects products = odb.getObjects(Product.class);

		assertTrue(catalogs.isEmpty());
		assertTrue(productCategories.isEmpty());
		assertTrue(products.isEmpty());

		deleteBase(baseName);
	}

}
