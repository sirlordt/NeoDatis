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
package org.neodatis.odb.test.fromusers.kasper;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.ODBServer;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.IOUtil;

public class TestAutoIncrementTrigger extends ODBTest {

	public static final String BASE = "trigger-auto-increment-kasper.neodatis";

	public void test1WithServerTrigger() throws Exception {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {
			int size = 10;
			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addInsertTrigger(BASE, MyUser.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, MyProfile.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, MyFunction.class.getName(), new SequenceTrigger());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);
			OID[] oids = new OID[size];
			for (int i = 0; i < size; i++) {
				MyUser user1 = getUserInstance(i);

				// Call the store
				oids[i] = odb.store(user1);
			}

			odb.close();

			// Re open the db to check if ID was set
			odb = ODBFactory.openClient("localhost", port, BASE);
			int nfunction = 1;

			for (int i = 0; i < size; i++) {
				MyUser user = (MyUser) odb.getObjectFromId(oids[i]);
				assertEquals((i + 1), user.getId());
				assertEquals((i + 1), ((MyProfile) user.getProfile()).getId());
				MyFunction f1 = (MyFunction) user.getProfile().getFunctions().get(0);
				MyFunction f2 = (MyFunction) user.getProfile().getFunctions().get(1);
				MyFunction f3 = (MyFunction) user.getProfile().getFunctions().get(2);

				assertEquals("function 1 " + i, f1.getName());
				assertEquals("function 2 " + i, f2.getName());
				assertEquals("function 3 " + i, f3.getName());

				assertEquals(nfunction++, f1.getId());
				assertEquals(nfunction++, f2.getId());
				assertEquals(nfunction++, f3.getId());

			}

		} finally {
			if (odb != null && !odb.isClosed()) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	private MyUser getUserInstance(int i) {
		MyFunction f1 = new MyFunction("function 1 " + i);
		MyFunction f2 = new MyFunction("function 2 " + i);
		MyFunction f3 = new MyFunction("function 3 " + i);
		List list = new ArrayList();
		list.add(f1);
		list.add(f2);
		list.add(f3);
		MyProfile profile = new MyProfile("operator " + i, list);
		MyUser user = new MyUser("user " + i, "userr@neodatis.com", profile);
		return user;
	}

	public void test2() {
		ODB odb = null;
		IOUtil.deleteFile(BASE);
		ODBServer myServer = null;
		int port = 12003;
		try {
			int size = 20;
			// Creates the server
			myServer = ODBFactory.openServer(port);
			// Adds the base for the test
			myServer.addBase(BASE, BASE);
			// Adds the insert trigger
			myServer.addInsertTrigger(BASE, Settings.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, Provider.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, Product.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, ProductCategory.class.getName(), new SequenceTrigger());
			myServer.addInsertTrigger(BASE, Resource.class.getName(), new SequenceTrigger());
			// Starts the server
			myServer.startServer(true);

			// Then open the client
			odb = ODBFactory.openClient("localhost", port, BASE);
			OID[] oids = new OID[size];
			for (int i = 0; i < size; i++) {
				Provider provider = createProvider(i);

				// Call the store
				oids[i] = odb.store(provider);
			}

			odb.close();

			// Re open the db to check if ID was set
			odb = ODBFactory.openClient("localhost", port, BASE);
			int nfunction = 1;

			for (int i = 0; i < size; i++) {
				Provider provider = (Provider) odb.getObjectFromId(oids[i]);
				assertEquals((i + 1), provider.getId().longValue());
			}

		} finally {
			if (odb != null && !odb.isClosed()) {
				odb.close();
			}
			if (myServer != null) {
				myServer.close();
			}
		}
	}

	public Provider createProvider(int i) {

		Resource resourceBolette = null;
		Resource resourceKerstine = null;
		Resource resourceLine = null;

		Provider provider = new Provider();

		// ------------- PROVIDER, NEW HAIR

		// ------------- SETTINGS
		Settings settings = new Settings();

		provider.setSettings(settings);

		// ------------- RESOURCE, BOLETTE (1)
		resourceBolette = new Resource();
		provider.getResources().add(resourceBolette);

		// ------------- RESOURCE, KIRSTINE (2)
		resourceKerstine = new Resource();
		provider.getResources().add(resourceKerstine);

		// ------------- RESOURCE, LINE (3) - TRAINEE
		resourceLine = new Resource();
		provider.getResources().add(resourceLine);

		ProductCategory productCategoryGentlemen = new ProductCategory();

		ProductCategory productCategoryLadies = new ProductCategory();

		ProductCategory productCategoryKids = new ProductCategory();

		// ---------------------- HERRE UDEN VASK
		Product product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryGentlemen);
		productCategoryGentlemen.getProducts().add(product);

		// ---------------------- HERRE MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryGentlemen);
		productCategoryGentlemen.getProducts().add(product);

		// ---------------------- DAME UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);

		// ---------------------- DAME MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);

		// ---------------------- BØRNEKLIP (0-5), UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- BØRNEKLIP (0-5), MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- BØRNEKLIP (6-12), UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- BØRNEKLIP (6-12), MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, DRENG, UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, DRENG, MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, PIGE, UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, PIGE, MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- SOLOPERMANENT, KORT HÅR, UDEN KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- SOLOPERMANENT, LANGT HÅR, UDEN KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- PERMANENT, KORT, INKL. KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- PERMANENT, LANGT, INKL. KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- BRYN & VIPPER
		product = new Product();

		product.getResources().add(resourceLine);
		// product.setProvider(provider);

		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		provider.getProducts().add(product);

		provider.getProductCategories().add(productCategoryGentlemen);
		provider.getProductCategories().add(productCategoryLadies);
		provider.getProductCategories().add(productCategoryKids);

		return provider;

	}
	
	public Provider createProvider2(int i) {

		Resource resourceBolette = null;
		Resource resourceKerstine = null;
		Resource resourceLine = null;

		Provider provider = new Provider();

		// ------------- PROVIDER, NEW HAIR

		// ------------- SETTINGS
		Settings settings = new Settings();

		provider.setSettings(settings);

		// ------------- RESOURCE, BOLETTE (1)
		resourceBolette = new Resource();
		provider.getResources().add(resourceBolette);

		// ------------- RESOURCE, KIRSTINE (2)
		resourceKerstine = new Resource();
		provider.getResources().add(resourceKerstine);

		// ------------- RESOURCE, LINE (3) - TRAINEE
		resourceLine = new Resource();
		provider.getResources().add(resourceLine);

		ProductCategory productCategoryGentlemen = new ProductCategory();

		ProductCategory productCategoryLadies = new ProductCategory();

		ProductCategory productCategoryKids = new ProductCategory();

		// ---------------------- HERRE UDEN VASK
		Product product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);
		
		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryGentlemen);
		productCategoryGentlemen.getProducts().add(product);

		// ---------------------- HERRE MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryGentlemen);
		productCategoryGentlemen.getProducts().add(product);

		// ---------------------- DAME UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);

		// ---------------------- DAME MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);

		// ---------------------- BØRNEKLIP (0-5), UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- BØRNEKLIP (0-5), MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- BØRNEKLIP (6-12), UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		
		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- BØRNEKLIP (6-12), MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, DRENG, UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, DRENG, MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, PIGE, UDEN VASK
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- TEENAGEKLIP, PIGE, MED VASK
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		product.setProvider(provider);
		provider.getProducts().add(product);
		product.setProductCategory(productCategoryKids);
		productCategoryKids.getProducts().add(product);

		// ---------------------- SOLOPERMANENT, KORT HÅR, UDEN KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- SOLOPERMANENT, LANGT HÅR, UDEN KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- PERMANENT, KORT, INKL. KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- PERMANENT, LANGT, INKL. KLIPNING
		// 1 TICK = VASK TIL LINE
		product = new Product();

		product.getResources().add(resourceBolette);
		product.getResources().add(resourceKerstine);

		resourceBolette.getProducts().add(product);
		resourceKerstine.getProducts().add(product);

		provider.getProducts().add(product);
		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		product.setProvider(provider);

		// ---------------------- BRYN & VIPPER
		product = new Product();

		product.getResources().add(resourceLine);
		// product.setProvider(provider);

		resourceLine.getProducts().add(product);

		product.setProductCategory(productCategoryLadies);
		productCategoryLadies.getProducts().add(product);
		provider.getProducts().add(product);

		provider.getProductCategories().add(productCategoryGentlemen);
		provider.getProductCategories().add(productCategoryLadies);
		provider.getProductCategories().add(productCategoryKids);

		return provider;

	}

	public void test3() {
		int size = 1;
		ODB odb = null;
		String baseName = getBaseName();
		println(baseName);

		OID oid = null;
		odb = open(baseName);
		Provider provider = null;
		for (int i = 0; i < size; i++) {
			provider = createProvider2(i);

			// Call the store
			odb.store(provider);
			
			if(oid==null){
				Product p = provider.getProducts().get(0);
				oid = odb.getObjectId(p);
				println("oid = "+ oid);
			}
			
		}

		odb.close();

		// Re open the db to check if ID was set
		odb = open(baseName);
		Product product = (Product) odb.getObjectFromId(oid);

		CriteriaQuery criteriaQuery = odb.criteriaQuery(Resource.class, Where.contain("products", product));

		Objects<Resource> resources = odb.getObjects(criteriaQuery);
		
		
		if (odb != null && !odb.isClosed()) {
			odb.close();
		}
		assertEquals(true, resources.size()>0);
	}
	
	

}