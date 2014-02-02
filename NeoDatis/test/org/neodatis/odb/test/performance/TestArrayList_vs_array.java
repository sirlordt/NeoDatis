/**
 * 
 */
package org.neodatis.odb.test.performance;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestArrayList_vs_array extends ODBTest {

	public void test1() {
		int size = 100000;
		Integer[] arrayOfInts = new Integer[size];
		List<Integer> listOfInts = new ArrayList<Integer>(size);

		long startArray = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			arrayOfInts[i] = new Integer(i);
		}
		for (int i = 0; i < size; i++) {
			Integer ii = arrayOfInts[i];
		}
		long endArray = System.currentTimeMillis();

		long startList = System.currentTimeMillis();
		for (int i = 0; i < size; i++) {
			listOfInts.add(new Integer(i));
		}
		for (int i = 0; i < size; i++) {
			Integer ii = listOfInts.get(i);
		}
		long endList = System.currentTimeMillis();

		println("Time for array = " + (endArray - startArray));
		println("Time for list = " + (endList - startList));

	}

}
