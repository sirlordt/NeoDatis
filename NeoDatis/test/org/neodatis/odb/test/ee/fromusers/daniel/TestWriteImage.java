/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.daniel;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestWriteImage extends ODBTest {
	public void test1() throws MalformedURLException, IOException {
		if(!testKnownProblems){
			return;
		}
		Image img = ImageIO.read(new URL("http://neodatis-odb.wdfiles.com/local--files/native-object-info/NativeObjectsTypeHierarchy.png"));
		
		String baseName = getBaseName();
		ODB odb = open(baseName);
		odb.store(img);
		odb.close();
		
		odb = open(baseName);
		Image img2 = (Image) odb.getObjects(Image.class).getFirst();
		odb.close();
		
		
		

	}

}
