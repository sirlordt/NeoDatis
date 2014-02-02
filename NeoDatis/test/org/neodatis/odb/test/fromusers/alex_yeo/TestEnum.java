package org.neodatis.odb.test.fromusers.alex_yeo;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

public class TestEnum extends ODBTest {

	
	public void testClassWithEnumInInterface() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		MyClass myclass = new MyClass("name", new McUsbDaqDevice());
		odb.store(myclass);
		odb.close();
		

		odb = open(baseName);
		Objects<MyClass> cc = odb.getObjects(MyClass.class, true);
		odb.close();
		MyClass my = cc.getFirst();
		assertEquals("name", cc.getFirst().getName());
		assertEquals(myclass.getDevice().getDescriptor(), cc.getFirst().getDevice().getDescriptor());
	}
	
	public void testClassWithEnumInInterface2() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String baseName = getBaseName();
		ODB odb = open(baseName);

		McUsbDaqDevice d = new McUsbDaqDevice();
		d.setInput(McUsbDaqInput.DIO0);
		d.setRangePolarity(VoltageRangePolarity.BIPOLAR10);
		MyClass myclass = new MyClass("name", d);
		odb.store(myclass);
		odb.close();
		

		odb = open(baseName);
		Objects<MyClass> cc = odb.getObjects(MyClass.class, true);
		odb.close();
		MyClass my = cc.getFirst();
		assertEquals("name", cc.getFirst().getName());
		assertEquals(myclass.getDevice().getDescriptor(), cc.getFirst().getDevice().getDescriptor());
		
		McUsbDaqDevice d2 = (McUsbDaqDevice) cc.getFirst().getDevice();
		assertEquals(McUsbDaqInput.DIO0, d2.getInput());
		assertEquals(VoltageRangePolarity.BIPOLAR10, d2.getRangePolarity());
		
		
	}

}
