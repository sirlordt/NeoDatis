package org.neodatis.odb.test.ee2.delete;
import java.util.HashMap;


public class TBugger {

	HashMap<TBugger, Integer> tBuggerChildren;
	int delete;
	int id;
	
	public TBugger(int id){
		this.id = id;
		tBuggerChildren = new HashMap<TBugger, Integer>();
		
	}
	
	public void addTBuggerChildren(TBugger tBugger, int id){

		tBuggerChildren.put(tBugger, id);

	}
	
	public void listChildren(){
		
		
		tBuggerChildren.values();
		
	}
	
	public void setDelete(int delete){
		
		this.delete = delete;
	}
	
}
