package org.neodatis.odb.test.ee2.atoji;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ee2.atoji.entities.D;
import org.neodatis.odb.test.ee2.atoji.entities.T1;
import org.neodatis.odb.test.ee2.atoji.entities.T2;
import org.neodatis.odb.xml.XMLExporter;
import org.neodatis.odb.xml.XMLImporter;

public class Main {
	
	private static final int INITIAL_T2 = 25;

	private static final int INITIAL_ID = 20;
	
	private static final int RANDOM_MARKED = 5;
	
	private static final int NEW_T2 = 2;
	
	private static final int T2_REMOVALS = 15;

	private static String DATABASE_NAME = "d.db";
	
	private static String TEST_DATABASE_NAME = "d-test.db";
	
	private static int nextId = INITIAL_ID + 100;

	public static void main(String[] args) throws InterruptedException {
		sanityCheck();
		duplicateTestDB();
		
		ODB odb = null;
		try {
			odb = ODBFactory.open(TEST_DATABASE_NAME);
			System.out.println("Opening database...");
			
			Objects<D> list = odb.getObjects(D.class);
			D d = list.getFirst();
			
			System.out.print("Running tests... ");
			runTests(d, odb, T2_REMOVALS, NEW_T2);
		} finally {
			if (odb != null)
				odb.close();
			System.out.println("Database closed");
		}
		
		packDatabase();
		
		try {
			odb = ODBFactory.open(TEST_DATABASE_NAME);
			System.out.println("Opening database again...");
			
			Objects<D> list = odb.getObjects(D.class);
			D d = list.getFirst();
			
			System.out.print("Running tests again...");
			runTests(d, odb, T2_REMOVALS, NEW_T2);
		} finally {
			if (odb != null)
				odb.close();
			System.out.println("Database closed again");
		}
	}

	private static void packDatabase() {
		ODB odb = null;
		
		try {
			odb = ODBFactory.open(TEST_DATABASE_NAME);
			XMLExporter exporter = new XMLExporter(odb);
			exporter.export(".", "packed.xml");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			if (odb != null)
				odb.close();
		}
		
		try {
			odb = ODBFactory.open("packed.db");
			XMLImporter importer = new XMLImporter(odb);
			importer.importFile(".", "packed.xml");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (odb != null)
				odb.close();
		}
	}

	private static void runTests(D d, ODB odb, int markedDeletes, int inOut) {
		int i = 0,j = 0;
		
		for (i = 0; i < markedDeletes; i++) {
			for (j = 0; j < inOut; j++) {
				insertNewT2(d, odb, NEW_T2);
				markRandomT2(d, odb, RANDOM_MARKED);
			}
			
			removeMarkedT2(d, odb);
		}

	}
	
	private static void removeMarkedT2(D d, ODB odb) {
		List<T2> all = d.getAllT1();
		
		for (T2 t2: all) {
			if (t2.getStatus() == T2.MARK_TO_REMOVE) {
				d.removeT1(t2.getIdentification());
				odb.deleteCascade(t2);
			}
		}
	}

	private static void markRandomT2(D d, ODB odb, int randomMarked) {
		List<T2> allD = d.getAllT1();
		List<T2> markedList = getRandomT2(allD, randomMarked);

		for (T2 target: markedList) {
			target.setStatus(T2.MARK_TO_REMOVE);
			odb.store(target);
		}		
	}

	private static void insertNewT2(D d, ODB odb, int newD) {
		DBuilder builder = new DBuilder(0, 0);
		T1 t1;
		
		for (int i = 0; i < newD; i++) {
			t1 = builder.buildT1(nextId++);
			d.addT1(t1);
			odb.store(t1);
		}
	}

	private static List<T2> getRandomT2(List<T2> allT2, int maxRandom) {
		List<T2> markedList = new LinkedList<T2>();
		int count = allT2.size();

		int max = maxRandom;
		if (count < maxRandom)
			max = count;
		
		int nextMarked;
		Random random = new Random();

		for (int i = 0; i < max; i++) {
			 nextMarked = random.nextInt(count);
			 markedList.add(allT2.get(nextMarked));
		}
		
		return markedList;
	}
	
	private static void duplicateTestDB() {
		File databaseFile = new File(DATABASE_NAME);
		File duplicateFile = new File(TEST_DATABASE_NAME);
		
		try {
			duplicateFile.createNewFile();
			copy(databaseFile, duplicateFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 private static void copy(File source, File target) throws IOException {
		 InputStream in = new FileInputStream(source);
		 OutputStream out = new FileOutputStream(target);

		 byte[] buf = new byte[1024];
		 int len;

		 while ((len = in.read(buf)) > 0) {
			 out.write(buf, 0, len);
		 }

		 in.close();
		 out.close();
	 }

	private static void sanityCheck() {
		File databaseFile = new File(DATABASE_NAME);
		
		if (!databaseFile.exists()) {
			System.out.println("Generating sample database...");
			
			DBuilder builder = new DBuilder(INITIAL_ID, INITIAL_T2);
			D deviceRegistry = builder.buildD();

			ODB odb = null;
			
			try {
				odb = ODBFactory.open(DATABASE_NAME);
				odb.store(deviceRegistry);				
			} finally {
				if (odb != null)
					odb.close();
			}
		}
	}
	
}
