/**
 * 
 */
package org.neodatis.odb.test.conversion;

import org.neodatis.odb.impl.core.layers.layer2.meta.compare.AttributeValueComparator;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestNumberConversion extends ODBTest {
	public void test1() {
		assertEquals(0.0, AttributeValueComparator.compare(new Integer(10), new Float(10)));
		assertEquals(0.0, AttributeValueComparator.compare(new Integer(10), new Long(10)));
		assertEquals(0.0, AttributeValueComparator.compare(new Integer(10), new Double(10)));
		assertEquals(0.0, AttributeValueComparator.compare(new Integer(10), new Byte((byte) 10)));
		assertEquals(0.0, AttributeValueComparator.compare(new Integer(10), new Integer(10)));
		assertEquals(0.0, AttributeValueComparator.compare(new Integer(10), new Short((short) 10)));

		assertEquals(1.0, AttributeValueComparator.compare(new Integer(10), new Short((short) 9)));

		assertEquals((double) new Integer(10).compareTo(new Integer(9)), AttributeValueComparator.compare(new Integer(10), new Short((short) 9)));
	}
}
