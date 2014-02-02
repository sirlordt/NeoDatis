/**
 * 
 */
package org.neodatis.odb.test.tools;

import org.neodatis.odb.test.ODBTest;
import org.neodatis.tool.wrappers.OdbString;

/**
 * @author olivier
 * 
 */
public class TestString extends ODBTest {
	public void test1() {
		String s = "ola $1 ola $2";
		s = OdbString.replaceToken(s, "$1", "param1");

		assertEquals("ola param1 ola $2", s);
	}

	public void test2() {
		String s = "ola $1 ola $2";
		s = OdbString.replaceToken(s, "$1", "param1");
		s = OdbString.replaceToken(s, "$2", "param2");

		assertEquals("ola param1 ola param2", s);
	}

	public void test3() {
		String s = "ola $1 ola $2";
		s = OdbString.replaceToken(s, "$", "param");

		assertEquals("ola param1 ola param2", s);
	}

	public void test4() {
		String s = "ola $1 ola $2";
		s = OdbString.replaceToken(s, "$", "param", 1);

		assertEquals("ola param1 ola $2", s);
	}

	public void test5() {
		String s = "ola $1 ola $2";
		s = OdbString.replaceToken(s, "$$", "param1");

		assertEquals("ola $1 ola $2", s);
	}

	public void test6() {
		String s = "ola $1 ola $2 ola $3 ola $4";
		s = OdbString.replaceToken(s, "$", "param", 2);

		assertEquals("ola param1 ola param2 ola $3 ola $4", s);
	}

	public void test7() {
		int size = 100;
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < size; i++) {
			b.append("text").append(i).append(" ");
		}
		String s = OdbString.replaceToken(b.toString(), "text", "");

		// Check that there is no more "text"in the string
		assertTrue(s.indexOf("text") == -1);
	}

	public void test8subString() {
		String s = "NeoDatis ODB - The open source object database";

		for (int i = 0; i < 10; i++) {
			String s1 = s.substring(i, i + 15);
			String s2 = OdbString.substring(s, i, i + 15);
			assertEquals(s1, s2);
		}
	}

	public void test9subString() {
		String s = "NeoDatis ODB - The open source object database";

		String s1 = s.substring(0, s.length());
		String s2 = OdbString.substring(s, 0, s.length());
		assertEquals(s1, s2);
	}

}
