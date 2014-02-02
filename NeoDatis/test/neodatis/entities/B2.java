package neodatis.entities;

import java.io.Serializable;
import java.util.Date;

public class B2 implements Serializable {
	
	private static final long serialVersionUID = -4686857740375898315L;

	private Date startTime;
	
	private Date endTime;

	public B2() {
		
		
	}
	
	
	
	public B2(Date startTime, Date endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
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
	

}
