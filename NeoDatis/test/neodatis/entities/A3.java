package neodatis.entities;

import java.io.Serializable;

public class A3 implements Serializable {
	private static final long serialVersionUID = -5892627344594681979L;
	private Long id;
	private String text;
	private String text2;
	private long length;
	private A2 a2;
	
	public A3() {
		
	}
	
	

	public String getText() {
		return text;
	}



	public void setText(String text) {
		this.text = text;
	}



	public String getText2() {
		return text2;
	}



	public void setText2(String text2) {
		this.text2 = text2;
	}



	public A2 getA2() {
		return a2;
	}



	public void setA2(A2 a2) {
		this.a2 = a2;
	}


	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
