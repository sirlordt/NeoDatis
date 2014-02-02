/**
 * 
 */
package org.neodatis.odb.impl.core.layers.layer2.meta.compare;

/**@sharpen.ignore
 * @author olivier
 *
 */
public class AttributeValueComparator {

	/**
	 * A geenric compare method
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static double compare(Comparable c1, Comparable c2) {
			if (c1 instanceof Number && c2 instanceof Number) {
				return compareNumbers((Number) c1, (Number) c2);
			}
			return c1.compareTo(c2);
		}

		/**
		 * n1-n2
		 * 
		 * @param c1
		 * @param c2
		 * @return
		 */
		private static double compareNumbers(Number n1, Number n2) {
			double d1 = n1.doubleValue();
			double d2 = n2.doubleValue();
			double dif = d1-d2;
			return dif;
		}

		/** A generic equals 
		 * @param valueToMatch
		 * @param criterionValue
		 * @return
		 */
		public static boolean equals(Object object1, Object object2) {
			if (object1 instanceof Number && object2 instanceof Number) {
				return equalsNumbers((Number) object1, (Number) object2);
			}
			return object1.equals(object2);
		}

		/**
		 * @param object1
		 * @param object2
		 * @return
		 */
		private static boolean equalsNumbers(Number number1, Number number2) {
			double d1 = number1.doubleValue();
			double d2 = number2.doubleValue();
			return d1 == d2;
		}


}
