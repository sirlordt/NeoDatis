package org.neodatis.odb.test.error;

import org.neodatis.odb.test.ODBTest;

public class TestError extends ODBTest {

	/**
	 * Submitted by Tom Davies (tgdavies) Source forge Feature request 1900092
	 * 
	 */
	public void testDollarInParam() {
		org.neodatis.odb.core.NeoDatisError e = new org.neodatis.odb.core.NeoDatisError(0, "x @1 y");
		e.addParameter("foo$bar");
		assertEquals("0:x foo$bar y", e.toString());
	}

	public void test2() {
		org.neodatis.odb.core.NeoDatisError e = new org.neodatis.odb.core.NeoDatisError(0, "x @1 @2 @3 @5 y");
		e.addParameter("param1");
		e.addParameter("param2");
		e.addParameter("param3");
		e.addParameter("param4");
		assertEquals("0:x param1 param2 param3 @5 y", e.toString());
	}

	public void test3() {
		org.neodatis.odb.core.NeoDatisError e = new org.neodatis.odb.core.NeoDatisError(0, "x y");
		e.addParameter("param1");
		e.addParameter("param2");
		e.addParameter("param3");
		e.addParameter("param4");
		assertEquals("0:x y", e.toString());
	}

	public void test4() {
		org.neodatis.odb.core.NeoDatisError e = new org.neodatis.odb.core.NeoDatisError(12, "x @1 @2 @3 @5 y");
		assertEquals("12:x @1 @2 @3 @5 y", e.toString());
	}

}
