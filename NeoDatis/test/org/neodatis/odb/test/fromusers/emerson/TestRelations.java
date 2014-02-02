package org.neodatis.odb.test.fromusers.emerson;

import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

public class TestRelations extends ODBTest {

	public void test1(){
		
		String baseName = getBaseName();
		ODB odb = open(baseName);

		Date dataInicio = null;
		Date dataFim = null;
		Objects<ReceitaVO> receitas = odb.getObjects(new CriteriaQuery(ReceitaVO.class, 
				Where.and()
					.add(Where.ge("data", dataInicio))
					.add(Where.le("data", dataFim))));
		while(receitas.hasNext()){
			System.out.println(receitas.next().getAnimal().getCliente());
		}
	}
}
