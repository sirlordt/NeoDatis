package neodatis.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class A1 implements Serializable {

	private static final long serialVersionUID = 3768462512227898963L;
	private Long id;
	private Date lastDate;
	private String name;
	private List a2List;	
	
	public A1() {
		a2List = new LinkedList();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List getAdvertisements() {
		return a2List;
	}
	
	public A2 getNextA2(A2 a2) {
		if (a2 == null) {
			for (Iterator iAds = a2List.iterator(); iAds.hasNext();) {
				A2 currentA2 = (A2)iAds.next();
				return currentA2;
			}
		}
		return null;	
	}
	
	public void addA2(A2 a2) {
		a2List.add(a2);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public List getA2List() {
		return a2List;
	}

	public void setA2List(List a2List) {
		this.a2List = a2List;
	}
	
}
