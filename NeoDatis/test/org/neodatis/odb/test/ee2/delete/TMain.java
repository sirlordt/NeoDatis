package org.neodatis.odb.test.ee2.delete;
import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.tool.IOUtil;


public class TMain {

	private static final int MAXPARENTS=5;
	private static final int MAXCHILDRENPERPARENT=2;
	
	public static void main(String[] args){
		
		IOUtil.deleteFile("tbug");
		ODB odb = ODBFactory.open("tbug");
		List<TBugger> listParents = new ArrayList<TBugger>();
		List<TBugger> listOfChildren = new ArrayList<TBugger>();
		
		for ( int x=0; x < MAXPARENTS; x++){
			TBugger tbugger = new TBugger(x);
			
			if (x%2==0 && x%7==0 ){
				tbugger.setDelete(1);
			}
			listParents.add(tbugger);
		}
		
		for ( int y = 0; y < MAXCHILDRENPERPARENT; y++){
			TBugger tbugger = new TBugger(y+MAXPARENTS);
			
			if ( y%2==0 && y%7==0 ){
				tbugger.setDelete(1);
			}
			listOfChildren.add(tbugger);
		}
		
		int i=1;
		for (TBugger tbuggerParent : listParents ){
			
			
			int j=1;
			for (TBugger tbuggerChild : listOfChildren ){
				
				System.out.println("Adding child " + j + " for parent " + i);
				tbuggerChild.addTBuggerChildren(tbuggerParent, 2);
				
				odb.store(tbuggerChild);
				odb.commit();
				tbuggerParent.addTBuggerChildren(tbuggerChild, j++);
				
			}
			
			odb.store(tbuggerParent);
			odb.commit();
			
			i++;
			
		}

		Objects<TBugger> bb = odb.getObjects(TBugger.class);
		System.out.println(bb);

		
		for (TBugger tbugger : listOfChildren){
			
			if ( tbugger.delete==1){
				System.out.println("Deleting child [" + tbugger.id + "]");
				odb.deleteCascade(tbugger);
				odb.commit();
			}else{
				System.out.println("Not Deleting child [" + tbugger.id + "] cos its delete is [" + tbugger.delete + "]");
			}
			bb = odb.getObjects(TBugger.class);
			System.out.println(bb);
			
		}
		
		odb.close();

	}
}
