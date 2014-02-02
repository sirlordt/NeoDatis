package org.neodatis.odb.test.ee2.atoji;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.Date;

import org.neodatis.odb.test.ee2.atoji.entities.A1;
import org.neodatis.odb.test.ee2.atoji.entities.A2;
import org.neodatis.odb.test.ee2.atoji.entities.A3;
import org.neodatis.odb.test.ee2.atoji.entities.B1;
import org.neodatis.odb.test.ee2.atoji.entities.D;
import org.neodatis.odb.test.ee2.atoji.entities.T1;
import org.neodatis.odb.test.ee2.atoji.entities.T2;


public class DBuilder {

	private final A1 a1;
	
	private final A2 a2;
	
	private final A3 a3;
	
	private final int maxDs;
	
	private final int maxB1;
	
	private final DecimalFormat numberFormat;
	
	public DBuilder(int maxDs, int maxBs) {
		a1 = buildA1();
		a2 = a1.getNextA2(null);
		a3 = a2.getNextA3(null);
		
		this.maxDs = maxDs;
		this.maxB1 = maxBs;
		
		this.numberFormat = (DecimalFormat)DecimalFormat.getIntegerInstance();
		this.numberFormat.applyPattern("000000000000");
	}
	
	private A1 buildA1() {
		A1 a1;
		try {		
			FileInputStream fis = new FileInputStream("test/org/neodatis/odb/test/ee2/atoji/a1.instance");
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			a1 = (A1)ois.readObject();
		} catch (IOException e) {
			a1 = null;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			a1 = null;
			e.printStackTrace();
		}
		return a1;
	}

	public D buildD() {
		D d = new D();
		
		for (int i = 0; i < maxDs; i++) {
			T1 t1 = buildT1(i);
			d.addT1(t1);
		}
		
		return d;
	}

	public T1 buildT1(int index) {
		T2 t2 = new T2(42);

		t2.setId2("Device " + index);
		t2.setIdentification(numberFormat.format(index));
		t2.setLastTime(new Date());
		t2.setStatus(T2.A);
		t2.setP(false);
		
		for (int i = 0; i < maxB1; i++) {
			B1 b1 = buildB1(t2);
			t2.addB1(b1);
		}
		
		return t2;
	}

	private B1 buildB1(T1 device) {
		B1 b1 = new B1();
		
		b1.setT1(device);
		b1.setStartTime(new Date());
		b1.setEndTime(new Date());
		b1.setStatus(B1.A);
		b1.setA3(a3);
		
		return b1;
	}

	
}
