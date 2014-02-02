package neodatis.entities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class D {
	private Map t1Map;
	private List t1List;
	
	public D() {
		t1Map = new Hashtable();
		t1List = new Vector();
	}
	
	
	
	public Map getT1Map() {
		return t1Map;
	}



	public void setT1Map(Map t1Map) {
		this.t1Map = t1Map;
	}



	public List getT1List() {
		return t1List;
	}



	public void setT1List(List t1List) {
		this.t1List = t1List;
	}



	public void addT1(T1 t1) {
		t1Map.put(t1.getIdentification(), t1);		
	}
	
	public T1 removeT1(String identification) {
		insertIntoList(identification);
		return (T1) t1Map.remove(identification);		
	}
	
	public T1 getT1(String identification) {
		return (T1) t1Map.get(identification);		
	}
	
	public List getAllT1() {
		return new ArrayList(t1Map.values());
	}
		
	public void insertIntoList(String identification) {
		t1List.remove(getT1(identification));		
	}
		
}
