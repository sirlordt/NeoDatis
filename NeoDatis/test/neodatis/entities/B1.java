package neodatis.entities;

import java.io.Serializable;
import java.util.Date;

public class B1 implements Serializable {

	private static final long serialVersionUID = 8342459887365369672L;
	public final static int A = 1;
	public final static int B = 2;
	public final static int C = 3;
	public final static int D = 4;
	public final static int E = 5;
	
	private A3 a3;
	private T1 t1;
	private Date startTime;
	private Date endTime;
	private int status;	
	
	public B1() {
		
	}
	
	

	public A3 getA3() {
		return a3;
	}



	public void setA3(A3 a3) {
		this.a3 = a3;
	}



	public T1 getT1() {
		return t1;
	}



	public void setT1(T1 t1) {
		this.t1 = t1;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}