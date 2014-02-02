/**
 * 
 */
package org.neodatis.odb.test.fromusers.adauto;

import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.IOUtil;

/**
 * @author olivier
 *
 */
public class TestList extends ODBTest {
	public void test1(){
		String baseName="teste-lista.neodatis";
		IOUtil.deleteFile(baseName);

		Dao<ListWrapper> dao = new Dao<ListWrapper>(baseName);
		ListWrapper lw = new ListWrapper("Genero");
		lw.getList().add("item1");
		lw.getList().add("item2");
		dao.create(lw);
		dao.close();
		
		dao = new Dao<ListWrapper>(baseName);
		IQuery query = new CriteriaQuery(ListWrapper.class,Where.equal("type","Genero")); 
		lw = dao.read(query).getFirst(); 
		lw.getList().add("item B1");  
		lw.getList().add("item B2");  
		dao.update(lw); 
		dao.close();
		
		dao = new Dao<ListWrapper>(baseName);
		Objects<ListWrapper> lws = dao.read(query);
		dao.close();
		assertEquals(1, lws.size());
		lw = lws.getFirst();

		assertEquals(4, lw.getList().size());
	}
}
