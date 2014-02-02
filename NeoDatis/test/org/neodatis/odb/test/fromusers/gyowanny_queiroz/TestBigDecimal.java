/**
 * 
 */
package org.neodatis.odb.test.fromusers.gyowanny_queiroz;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestBigDecimal extends ODBTest {
	
	public void test1(){
		String baseName = getBaseName();
		
		ODB odb = null;
		
		Locale defaultLocale = Locale.getDefault();
		try{
			Locale.setDefault( new Locale("pt","BR"));
			odb = open(baseName);
			ItemVenda iv1 = new ItemVenda(new Long(1),new Long(2), new BigDecimal(10.45), "description 1");
			
			odb.store(iv1);
			odb.commit();
			
			iv1.setValue(new BigDecimal(9));
			odb.close();
			
			odb = open(baseName);
			Objects<ItemVenda> itens = odb.getObjects(ItemVenda.class, true);
			assertEquals(1, itens.size());
		}finally{
			if(odb!=null){
				odb.close();
			}
			Locale.setDefault(defaultLocale);
		}
	}
	
	public void test10(){
		String baseName = getBaseName();
		
		ODB odb = null;
		
		Locale defaultLocale = Locale.getDefault();
		try{
			Locale.setDefault( new Locale("pt","BR"));
			odb = open(baseName);
			ItemVenda iv1 = new ItemVenda(new Long(1),new Long(2), new BigDecimal(10), "description 1");
			iv1.getValue().setScale(10, BigDecimal.ROUND_CEILING);
			
			odb.store(iv1);
			odb.commit();
			
			iv1.setValue(new BigDecimal(9));
			odb.close();
			
			odb = open(baseName);
			Objects<ItemVenda> itens = odb.getObjects(ItemVenda.class, true);
			assertEquals(1, itens.size());
		}finally{
			if(odb!=null){
				odb.close();
			}
			Locale.setDefault(defaultLocale);
		}
	}
	
	public void test11(){
		String baseName = getBaseName();
		
		ODB odb = null;
		
		Locale defaultLocale = Locale.getDefault();
		try{
			Locale.setDefault( new Locale("pt","BR"));
			odb = open(baseName);
			ItemVenda iv1 = new ItemVenda(new Long(1),new Long(2), new BigDecimal(10), "description 1");
			iv1.getValue().setScale(10, BigDecimal.ROUND_CEILING);
			
			odb.store(iv1);
			odb.commit();
			
			odb.delete(iv1);
			odb.commit();
			
			odb.store(iv1);
			odb.close();
			
			odb = open(baseName);
			Objects<ItemVenda> itens = odb.getObjects(ItemVenda.class, true);
			assertEquals(1, itens.size());
		}finally{
			if(odb!=null){
				odb.close();
			}
			Locale.setDefault(defaultLocale);
		}
	}
	public void test12(){
		String baseName = getBaseName();
		
		ODB odb = null;
		
		Locale defaultLocale = Locale.getDefault();
		try{
			Locale.setDefault( new Locale("pt","BR"));
			odb = open(baseName);
			ItemVenda iv1 = new ItemVenda(new Long(1),new Long(2), new BigDecimal(10), "description 1");
			iv1.getValue().setScale(10, BigDecimal.ROUND_CEILING);
			
			odb.store(iv1);
			odb.close();
			
			odb = open(baseName);
			Objects<ItemVenda> ii = odb.getObjects(new CriteriaQuery(ItemVenda.class, Where.equal("id", new Long(1)))); 
			ItemVenda iv2 = ii.getFirst(); 
			odb.delete(iv2);
			odb.close();
			
			odb = open(baseName);
			odb.store(iv2);
			odb.close();
			
			odb = open(baseName);
			Objects<ItemVenda> itens = odb.getObjects(ItemVenda.class, true);
			assertEquals(1, itens.size());
		}finally{
			if(odb!=null){
				odb.close();
			}
			Locale.setDefault(defaultLocale);
		}
	}
	
	public void test13(){
		String baseName = getBaseName();
		
		ODB odb = null;
		
		Locale defaultLocale = Locale.getDefault();
		try{
			Locale.setDefault( new Locale("pt","BR"));
			
			List<ItemVenda> l = new ArrayList<ItemVenda>();
			l.add(new ItemVenda(new Long(1),new Long(2), new BigDecimal(10), "description 1"));
			l.add(new ItemVenda(new Long(2),new Long(3), new BigDecimal(10), "description 2"));
			l.add(new ItemVenda(new Long(3),new Long(4), new BigDecimal(10), "description 3"));
			l.add(new ItemVenda(new Long(4),new Long(2), new BigDecimal(10), "description 4"));
			l.add(new ItemVenda(new Long(5),new Long(2), new BigDecimal(10), "description 5"));
			l.add(new ItemVenda(new Long(6),new Long(2), new BigDecimal(10), "description 6"));
			
			for(int i=0;i<l.size();i++){
				odb = open(baseName);
				odb.store(l.get(i));
				odb.commit();
				odb.close();
			}
			
			
			
			odb = open(baseName);
			Objects<ItemVenda> ii = odb.getObjects(ItemVenda.class);
			odb.close();
			while(ii.hasNext()){
				odb = open(baseName);
				ItemVenda ivOriginal = ii.next();
				ItemVenda iv = (ItemVenda) odb.getObjects(new CriteriaQuery(ItemVenda.class,Where.equal("id", ivOriginal.getId()))).getFirst();
				odb.delete(iv);
				odb.commit();
				odb.close();
			}
			
			for(int i=0;i<l.size();i++){
				odb = open(baseName);
				odb.store(l.get(i));
				odb.commit();
				odb.close();
			}
			
			odb = open(baseName);
			Objects<ItemVenda> itens = odb.getObjects(ItemVenda.class, true);
			assertEquals(l.size(), itens.size());
		}finally{
			if(odb!=null){
				odb.close();
			}
			Locale.setDefault(defaultLocale);
		}
	}
	
	public void test2(){
		String baseName = getBaseName();
		
		ODB odb = null;
		
		Locale defaultLocale = Locale.getDefault();
		try{
			Locale.setDefault( new Locale("pt","BR"));
			odb = open(baseName);
			ItemVenda iv1 = new ItemVenda(new Long(1),new Long(2), new BigDecimal(10.45), "description 1");
			ItemVenda iv2 = new ItemVenda(new Long(10),new Long(20), new BigDecimal(100.45), "description 2");
			
			odb.store(iv1);
			odb.store(iv2);
			odb.commit();
			
			odb.delete(iv2);
			odb.close();
			
			odb = open(baseName);
			Objects<ItemVenda> itens = odb.getObjects(ItemVenda.class, true);
			assertEquals(1, itens.size());
		}finally{
			if(odb!=null){
				odb.close();
			}
			Locale.setDefault(defaultLocale);
		}
	}
	
	public void test3() throws UnsupportedEncodingException{
		String baseName = getBaseName();
		
		ODB odb = null;
		String defaultEncoding = OdbConfiguration.getDatabaseCharacterEncoding();
		Locale defaultLocale = Locale.getDefault();
		try{
			Locale.setDefault( new Locale("pt","BR"));
			OdbConfiguration.setLatinDatabaseCharacterEncoding();
			odb = open(baseName);
			
			ItemVenda iv1 = new ItemVenda(new Long(1),new Long(2), new BigDecimal(10.45), "description 1");
			ItemVenda iv2 = new ItemVenda(new Long(10),new Long(20), new BigDecimal(100.45), "description 2");
			
			byte[] bytes = iv1.getValue().toString().getBytes();
			odb.store(iv1);
			odb.store(iv2);
			odb.commit();
			
			for(int i=0;i<1000;i++){
				iv1.setValue(new BigDecimal( i*100/13));
				odb.store(iv1);
			}
			
			odb.delete(iv2);
			odb.close();
			
			odb = open(baseName);
			Objects<ItemVenda> itens = odb.getObjects(ItemVenda.class, true);
			assertEquals(1, itens.size());
		}finally{
			if(odb!=null){
				odb.close();
			}
			Locale.setDefault(defaultLocale);
			OdbConfiguration.setDatabaseCharacterEncoding(defaultEncoding);
		}
	}

}
