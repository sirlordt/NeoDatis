package org.neodatis.odb.test.refactoring;

import java.util.Map;

import org.neodatis.tool.wrappers.map.OdbHashMap;

public class TestResult {
	private Map tests;
	int nbGoodTests;
	int nbBadTests;

	public TestResult() {
		tests = new OdbHashMap();
		nbGoodTests = 0;
		nbBadTests = 0;
	}

	/**
	 * @return the tests
	 */
	public Map getTests() {
		return tests;
	}

	/**
	 * @param tests
	 *            the tests to set
	 */
	public void setTests(Map tests) {
		this.tests = tests;
	}

	public void incrementGood() {
		nbGoodTests++;
	}

	public void incrementBad() {
		nbBadTests++;
	}

	/**
	 * @return the nbBadTests
	 */
	public int getNbBadTests() {
		return nbBadTests;
	}

	/**
	 * @param nbBadTests
	 *            the nbBadTests to set
	 */
	public void setNbBadTests(int nbBadTests) {
		this.nbBadTests = nbBadTests;
	}

	/**
	 * @return the nbGoodTests
	 */
	public int getNbGoodTests() {
		return nbGoodTests;
	}

	/**
	 * @param nbGoodTests
	 *            the nbGoodTests to set
	 */
	public void setNbGoodTests(int nbGoodTests) {
		this.nbGoodTests = nbGoodTests;
	}

}
