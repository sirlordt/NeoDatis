package org.neodatis.odb.test.ee2.delete;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.layers.layer3.engine.ObjectReader;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.test.ODBTest;

/**
 * Test created by Thiago Santana
 * 
 * @author tsantana
 * 
 */
public class TestDeleteWithMap extends ODBTest {

	private static final int MAXPARENTS = 5;
	private static final int MAXCHILDRENPERPARENT = 2;

	public void testCreateObjectsWithMapWithoutChildren() {
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();

		ODB odb = open(baseName);
		List<TBugger> listParents = new ArrayList<TBugger>();

		for (int x = 0; x < MAXPARENTS; x++) {
			TBugger tbugger = new TBugger(x);

			if (x % 2 == 0 && x % 7 == 0) {
				tbugger.setDelete(1);
			}
			listParents.add(tbugger);
		}

		// to keep track of parent oids
		List<OID> parentOids = new ArrayList<OID>();

		int i = 1;
		for (TBugger tbuggerParent : listParents) {

			parentOids.add(odb.store(tbuggerParent));
			odb.commit();

			i++;

		}
		System.out.println("Parent OIDs = " + parentOids);

		// display object chaining
		IStorageEngine engine = Dummy.getEngine(odb);
		// gets the neodatis reader
		ObjectReader reader = (ObjectReader) engine.getObjectReader();
		// gets the class info (The meta representation of the class
		ClassInfo ci = engine.getSession(false).getMetaModel().getClassInfo(TBugger.class.getName(), false);
		System.out.println(" Number of objects of CI =" + ci.getNumberOfObjects());
		System.out.println(" Object chaining =" + reader.getClassInfoFullObjectChaining(ci));

		Objects<TBugger> tbuggers = odb.getObjects(TBugger.class);
		odb.close();
		System.out.println(tbuggers.size());

		assertEquals(MAXPARENTS, tbuggers.size());

	}

	public void testCreateObjectsWithMapWithCommit() {
		createObjectsWithMap(true);
	}

	public void testCreateObjectsWithMapWithoutCommit() {
		createObjectsWithMap(false);
	}

	public void createObjectsWithMap(boolean commit) {
		if(!isLocal){
			return;
		}

		String baseName = getBaseName();

		ODB odb = open(baseName);
		List<TBugger> listParents = new ArrayList<TBugger>();
		List<TBugger> listOfChildren = new ArrayList<TBugger>();

		for (int x = 0; x < MAXPARENTS; x++) {
			TBugger tbugger = new TBugger(x);

			if (x % 2 == 0 && x % 7 == 0) {
				tbugger.setDelete(1);
			}
			listParents.add(tbugger);
		}

		for (int y = 0; y < MAXCHILDRENPERPARENT; y++) {
			TBugger tbugger = new TBugger(y + MAXPARENTS);

			if (y % 2 == 0 && y % 7 == 0) {
				tbugger.setDelete(1);
			}
			listOfChildren.add(tbugger);
		}

		// to keep track of parent oids
		List<OID> parentOids = new ArrayList<OID>();
		// to keep track of children oids
		List<OID> childrenOids = new ArrayList<OID>();

		int i = 1;
		for (TBugger tbuggerParent : listParents) {
			displayObjectChaining(odb, "\n\nfor parent " + i);
			int j = 1;
			
			for (TBugger tbuggerChild : listOfChildren) {

				System.out.println("Adding child " + j + " for parent " + i);
				tbuggerChild.addTBuggerChildren(tbuggerParent, 2);

				childrenOids.add(odb.store(tbuggerChild));
				if (commit) {
					displayObjectChaining(odb, "Before commit " + i + " : ");
					odb.commit();
					displayObjectChaining(odb, " After commit " + i + " : ");
				}
				displayObjectChaining(odb, " After commit " + i + " : ");
				tbuggerParent.addTBuggerChildren(tbuggerChild, j++);

			}
			

			parentOids.add(odb.store(tbuggerParent));
			if (commit) {
				//displayObjectChaining(odb, "Before commit " + i + " : ");
				odb.commit();
				//displayObjectChaining(odb, " After commit " + i + " : ");
			}

			i++;

		}
		odb.commit();
		System.out.println("Parent OIDs = " + parentOids);
		System.out.println("Children OIDs = " + childrenOids);

		displayObjectChaining(odb, " Before select");

		Objects<TBugger> tbuggers = odb.getObjects(TBugger.class);
		odb.close();
		System.out.println(tbuggers.size());

		assertEquals(MAXCHILDRENPERPARENT + MAXPARENTS, tbuggers.size());

	}

	protected void displayObjectChaining(ODB odb, String string) {
		// display object chaining
		IStorageEngine engine = Dummy.getEngine(odb);
		// gets the neodatis reader
		ObjectReader reader = (ObjectReader) engine.getObjectReader();
		// gets the class info (The meta representation of the class
		ClassInfo ci = engine.getSession(false).getMetaModel().getClassInfo(TBugger.class.getName(), false);
		if (ci != null) {
			System.out.println(string + "Nb Objects="+ ci.getNumberOfObjects() +   " - Object chaining =" + reader.getClassInfoFullObjectChaining(ci));
			System.out.println("\t\t:"+ ci.getCommitedZoneInfo() + " | " + ci.getUncommittedZoneInfo());
		}else{
			System.out.println(string + " Class does not existt");
		}

	}
}
