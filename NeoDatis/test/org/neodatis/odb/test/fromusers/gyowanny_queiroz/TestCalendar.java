/**
 * 
 */
package org.neodatis.odb.test.fromusers.gyowanny_queiroz;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestCalendar extends ODBTest {

	public void test1() {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		ClassWithCalendar cwc = new ClassWithCalendar("c1", Calendar.getInstance());
		odb.store(cwc);
		odb.close();

		odb = open(baseName);
		Calendar from = Calendar.getInstance();
		from.add(Calendar.DAY_OF_MONTH, -1);
		Calendar to = Calendar.getInstance();
		to.add(Calendar.DAY_OF_MONTH, +1);

		ICriterion criteria = Where.and().add(Where.equal("name", "c1")).add(Where.ge("calendar", from)).add(Where.le("calendar", to));

		Objects<ClassWithCalendar> ww = odb.getObjects(new CriteriaQuery(ClassWithCalendar.class, criteria));
		odb.close();
		assertEquals(1, ww.size());

	}
	
	public void testCountWithCalendar() {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		ClassWithCalendar cwc = new ClassWithCalendar("c1", Calendar.getInstance());
		odb.store(cwc);
		odb.close();

		odb = open(baseName);
		Calendar from = Calendar.getInstance();
		from.add(Calendar.DAY_OF_MONTH, -1);
		Calendar to = Calendar.getInstance();
		to.add(Calendar.DAY_OF_MONTH, +1);

		ICriterion criteria = Where.and().add(Where.equal("name", "c1")).add(Where.ge("calendar", from));//.add(Where.le("calendar", to));
		CriteriaQuery q = new CriteriaQuery(ClassWithCalendar.class, criteria);
		//q.setOptimizeObjectComparison(false);

		BigInteger count = odb.count(q);
		odb.close();
		assertEquals(1, count.intValue());

	}

	public void test2() {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		ClassWithDate cwc = new ClassWithDate("c1", new Date());
		odb.store(cwc);
		odb.close();

		odb = open(baseName);
		Calendar from = Calendar.getInstance();
		from.add(Calendar.DAY_OF_MONTH, -1);
		Calendar to = Calendar.getInstance();
		to.add(Calendar.DAY_OF_MONTH, +1);

		ICriterion criteria = Where.and().add(Where.equal("name", "c1")).add(Where.ge("calendar", from.getTime())).add(
				Where.le("calendar", to.getTime()));
		println(criteria);

		Objects<ClassWithDate> ww = odb.getObjects(new CriteriaQuery(ClassWithDate.class, criteria));
		odb.close();
		assertEquals(1, ww.size());

	}

}
