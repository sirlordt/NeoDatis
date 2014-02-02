/**
 * 
 */
package org.neodatis.odb.test.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestEncoding extends ODBTest {

	public void test1() {
		String s = "&^<>";
		String encoded = URLEncoder.encode(s);
		println(encoded);
		String s1 = URLDecoder.decode(encoded);
		println(s1);
		assertEquals(s, s1);
	}

	public void test2() throws UnsupportedEncodingException {
		String s = "&^<>";
		String encoded = URLEncoder.encode(s, "utf-8");
		println(encoded);
		String s1 = URLDecoder.decode(encoded, "utf-8");
		println(s1);
		assertEquals(s, s1);
	}

	public void test3() throws UnsupportedEncodingException {
		String s = "&^<>";
		String encoded = URLEncoder.encode(s, "iso-8859-1");
		println(encoded);
		String s1 = URLDecoder.decode(encoded, "iso-8859-1");
		println(s1);
		assertEquals(s, s1);
	}

}
