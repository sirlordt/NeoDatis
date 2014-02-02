package neodatis.entities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class T1 implements Serializable {
	private static final long serialVersionUID = -8592477239999604538L;
	protected String id;
	protected String id2;
	protected List b1List;
	protected B1 b1;
	protected boolean p;	

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}

	public List getB1List() {
		return b1List;
	}

	public void setB1List(List b1List) {
		this.b1List = b1List;
	}

	public B1 getB1() {
		return b1;
	}

	public void setB1(B1 b1) {
		this.b1 = b1;
	}

	public boolean isP() {
		return p;
	}

	public void setP(boolean p) {
		this.p = p;
	}

	public T1() {
		b1List = new LinkedList();
	}

	public String getIdentification() {
		return id;
	}

	public void setIdentification(String identification) {
		this.id = identification;
	}

	public void addB1(B1 b1) {
		this.b1 = b1;
		b1List.add(b1);
	}
	
}
