package org.neodatis.odb.test.query.criteria;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.vo.company.Address;
import org.neodatis.odb.test.vo.company.City;
import org.neodatis.odb.test.vo.company.Code;
import org.neodatis.odb.test.vo.company.Company;

public class TestRelation extends ODBTest {

	public void testNullRelation() throws Exception {
		deleteBase("null-rel.neodatis");
		ODB odb = open("null-rel.neodatis");

		odb.store(new Class2());
		odb.close();

		odb = open("null-rel.neodatis");
		IQuery q = new CriteriaQuery(Class2.class, Where.isNull("class1.name"));
		Objects os = odb.getObjects(q);
		odb.close();
		assertEquals(1, os.size());
		Class2 c2 = (Class2) os.getFirst();

		assertEquals(null, c2.getClass1());

	}
	public void testRelation() throws Exception {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		Company company = new Company("NeoDatis",new Address(new City("curitiba",new Code("123456"))));
		odb.store(company);
		odb.close();

		odb = open(baseName);
		IQuery q = new CriteriaQuery(Company.class, Where.equal("address.city.code.id","123456"));
		Objects<Company> os = odb.getObjects(q);
		odb.close();
		assertEquals(1, os.size());
		Company c = os.getFirst();

		assertEquals("NeoDatis",c.getName());

	}

}
