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
package org.neodatis.test.btree.impl.singlevalue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.impl.singlevalue.InMemoryBTreeSingleValuePerKey;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.DisplayUtility;
import org.neodatis.tool.wrappers.OdbTime;

public class TestBTree2 extends ODBTest {
	long nbSolutions;
	long total;
	long nbFailures;
	long maxExecutions;
	String failureFileName;
	String successFileName;

	public void setUp() throws Exception {
		super.setUp();
	}

	private void testDelete(Integer[] numbers) throws Exception {
		IBTree btree = getBTree(2);
		for (int i = 0; i < numbers.length; i++) {
			btree.insert(numbers[i], "v " + numbers[i]);
		}

		assertEquals(numbers.length, btree.getSize());
		for (int i = 0; i < numbers.length; i++) {
			assertEquals("v " + (i + 1), btree.delete(new Integer(i + 1), "v " + (i + 1)));
		}
		assertEquals(0, btree.getSize());
		assertEquals(1, btree.getHeight());
		assertEquals(0, btree.getRoot().getNbKeys());
		assertEquals(0, btree.getRoot().getNbChildren());
	}

	public void te1stDeleteAll() throws Exception {

		long time = OdbTime.getCurrentTimeInMs();
		failureFileName = "btree-failures." + time + ".txt";
		successFileName = "btree-successes." + time + ".txt";

		int size = 10000;
		total = factorial(size);
		Integer[] array = new Integer[size];
		List elements = new ArrayList();
		for (int i = 0; i < size; i++) {
			elements.add(new Integer(i + 1));
		}
		buildArray(array, elements, 0);
		println(nbSolutions + " solutions");
	}

	public void testDeleteAllRandom() throws Exception {

		long time = OdbTime.getCurrentTimeInMs();
		failureFileName = "btree-failures." + time + ".txt";
		successFileName = "btree-successes." + time + ".txt";

		int size = 1000;
		maxExecutions = 100;
		int nbPossibilities = 3;
		total = (long) Math.pow(nbPossibilities, size);
		Integer[] array = new Integer[size];
		List elements = new ArrayList();
		for (int i = 0; i < size; i++) {
			elements.add(new Integer(i + 1));
		}
		// buildArray(array, elements, 0);
		buildRandomArray(array, elements, 0, nbPossibilities);
		println(nbSolutions + " solutions");
		assertEquals(0, nbFailures);
	}

	private long factorial(int value) {
		if (value == 1) {
			return value;
		}
		return value * factorial(value - 1);
	}

	void buildArray(Integer[] array, List elements, int currentPosition) throws Exception {
		if (elements.size()==0) {
			post(array);
		}

		for (int i = 0; i < elements.size(); i++) {
			List myElements = new ArrayList(elements);
			Integer e = (Integer) myElements.remove(i);
			array[currentPosition] = e;
			buildArray(array, myElements, currentPosition + 1);
		}
	}

	void buildRandomArray(Integer[] array, List elements, int currentPosition, int nbPossibilites) throws Exception {
		if (elements.size()==0) {
			post(array);
		}

		for (int i = 0; i < nbPossibilites && i < elements.size() && nbSolutions < maxExecutions; i++) {
			List myElements = new ArrayList(elements);
			int elementToPick = (int) (Math.random() * elements.size());
			Integer e = (Integer) myElements.remove(elementToPick);
			array[currentPosition] = e;
			buildRandomArray(array, myElements, currentPosition + 1, nbPossibilites);
		}
	}

	private void post(Integer[] array) throws IOException {
		// println(DisplayUtility.ojbectArrayToString(array));
		try {
			testDelete(array);
			// storeSuccess(array);
		} catch (Exception e) {
			try {
				storeFailure(array);
			} catch (IOException e1) {
				throw e1;
			}
		}
		nbSolutions++;
		if (nbSolutions % 1000 == 0) {
			println(nbSolutions + "/" + total + "  -  " + nbFailures + " failures");
		}

	}

	private void storeFailure(Integer[] array) throws IOException {
		FileWriter fw = new FileWriter(failureFileName, true);
		fw.write(DisplayUtility.objectArrayToString(array));
		fw.write("\n");
		fw.close();
		nbFailures++;
	}

	private void storeSuccess(Integer[] array) throws IOException {
		FileWriter fw = new FileWriter(successFileName, true);
		fw.write(DisplayUtility.objectArrayToString(array));
		fw.write("\n");
		fw.close();
	}

	private IBTree getBTree(int degree) {
		return new InMemoryBTreeSingleValuePerKey("default", degree);
	}
}
