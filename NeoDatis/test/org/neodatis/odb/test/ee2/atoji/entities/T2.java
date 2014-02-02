package org.neodatis.odb.test.ee2.atoji.entities;

import java.io.Serializable;
import java.util.Date;

public class T2 extends T1 implements Serializable {

	private static final long serialVersionUID = -7177107847655222492L;
	public final static int A = 1;
	public final static int B = 2;
	public final static int C = 3;
	public final static int D = 4;
	public final static int MARK_TO_REMOVE = 5; 
	
	private int status;
	private Date lastTime;
	private int r1;
	private int s1; 
	private boolean p;
	private int i = -1;
	
	private final int nd;
	private int td;
	private int tnd;
	
	public T2() {
		this(0);
	}
	
	
	
	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}



	public Date getLastTime() {
		return lastTime;
	}



	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}



	public int getR1() {
		return r1;
	}



	public void setR1(int r1) {
		this.r1 = r1;
	}



	public int getS1() {
		return s1;
	}



	public void setS1(int s1) {
		this.s1 = s1;
	}



	public boolean isP() {
		return p;
	}



	public void setP(boolean p) {
		this.p = p;
	}



	public int getI() {
		return i;
	}



	public void setI(int i) {
		this.i = i;
	}



	public int getTd() {
		return td;
	}



	public void setTd(int td) {
		this.td = td;
	}



	public int getTnd() {
		return tnd;
	}



	public void setTnd(int tnd) {
		this.tnd = tnd;
	}



	public int getNd() {
		return nd;
	}



	public T2(int nd) {
		this.status = C;
		this.r1 = 0;
		this.td = 0;
		this.tnd = 0;
		this.s1 = 0;
		this.nd = nd;
	}

		
}
