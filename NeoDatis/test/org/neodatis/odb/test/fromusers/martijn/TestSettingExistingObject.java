/**
 * 
 */
package org.neodatis.odb.test.fromusers.martijn;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestSettingExistingObject extends ODBTest {
	
	public void test1(){
		String baseName = getBaseName();
		
		int numberOfItens = 10;
		ODB odb = null;
		try{
			odb = open(baseName);
			Category category = new Category("Category 1");
			for(int i=0;i<numberOfItens;i++){
				Item item = new Item("Item "+i,category);
				odb.store(item);
			}
			odb.close();
			
			// Now check the number of itens & categories
			// We should have one category and 'numberOfItens' itens
			odb = open(baseName);
			Objects<Category> categories = odb.getObjects(Category.class); 
			Objects<Item> itens = odb.getObjects(Item.class);
			
			odb.close();
			assertEquals(1, categories.size());
			assertEquals(numberOfItens, itens.size());
			
		}finally{
			if(odb!=null && !odb.isClosed()){
				odb.close();
			}
		}
	}

	public void test2(){
		String baseName = getBaseName();
		
		int numberOfItens = 10;
		ODB odb = null;
		try{
			odb = open(baseName);
			Category category1 = new Category("Category 1");
			Category category2 = new Category("Category 2");
			
			// creates itens for each categories
			for(int i=0;i<numberOfItens;i++){
				Item itemOfCategory1 = new Item("Item "+i,category1);
				odb.store(itemOfCategory1);

				Item itemOfCategory2 = new Item("Item "+i,category2);
				odb.store(itemOfCategory2);

			}
			odb.close();
			
			// Now retrieve category 2, and sets it on all itens that had category 1
			odb = open(baseName);
			Objects<Category> categories = odb.getObjects(new CriteriaQuery(Category.class,Where.equal("name", "Category 2")));
			Category c2 = categories.getFirst();
			
			// Get all itens of Category 1
			Objects<Item> itens = odb.getObjects(new CriteriaQuery(Item.class,Where.equal("category.name", "Category 1")));
			// Then update category
			while(itens.hasNext()){
				Item item = itens.next();
				item.setCategory(c2);
				odb.store(item);
			}
			odb.close();
			
			odb = open(baseName);
			categories = odb.getObjects(Category.class); 
			itens = odb.getObjects(Item.class);
			odb.close();
			// We should have 2 categories
			assertEquals(2, categories.size());
			// and numberOfItens*2 itens
			assertEquals(numberOfItens*2, itens.size());
			
		}finally{
			if(odb!=null && !odb.isClosed()){
				odb.close();
			}
		}
	}

	
	
}
