package neodatis.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class A2 implements Serializable {
	private static final long serialVersionUID = 1812928595476527164L;
	private Long id;
	private String name;
	private A1 a1;
	private int r1; // seconds
	private int r2;
	private int r3; // seconds	
	private List b2List = new LinkedList();
	private List a3List = new LinkedList();
	
	public A2() {
		
	}

	public void addB2(B2 b2) {
		b2List.add(b2);
	}
	
	public List getB2List() {
		return b2List;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public A1 getA1() {
		return a1;
	}

	public void setA1(A1 a1) {
		this.a1 = a1;
	}

	public int getR1() {
		return r1;
	}

	public void setR1(int r1) {
		this.r1 = r1;
	}

	public int getR2() {
		return r2;
	}

	public void setR2(int r2) {
		this.r2 = r2;
	}

	public int getR3() {
		return r3;
	}

	public void setR3(int r3) {
		this.r3 = r3;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List getA3List() {
		return a3List;
	}
	
	public void addA3(A3 a3) {
		a3List.add(a3);
	}
	
	public A3 getNextA3(A3 a3) {
		if (a3 == null) {
			if (a3List.size() > 0)
				return (A3)a3List.get(0);
			return null;
		}
		
		return null;
	}

	public void setB2List(List b2List) {
		this.b2List = b2List;
	}

	public void setA3List(List a3List) {
		this.a3List = a3List;
	}
	
	
	
}
