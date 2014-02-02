package org.neodatis.odb.test.refactoring;

import java.io.IOException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.test.ODBTest;
import org.neodatis.odb.test.performance.SimpleObject;
import org.neodatis.odb.test.vo.login.Function;
import org.neodatis.odb.test.vo.login.Profile;

public class TestAPI extends ODBTest {
	public final String FILE_NAME = "refactor.neodatis";

	public void testRenameClass() throws IOException, Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		ODB odb = null;

		try {
			deleteBase(FILE_NAME);
			odb = open(FILE_NAME);

			// Creates 3 different objects to create 3 classes in metamodel
			Object o1 = new Function("f1");
			Object o2 = new SimpleObject("name", 50, null);
			Object o3 = new Profile("profile");

			odb.store(o1);
			odb.store(o2);
			odb.store(o3);

			odb.close();
			String newClassName = "test1.MyClass";
			// Rename the second class
			odb = open(FILE_NAME);
			odb.getRefactorManager().renameClass(SimpleObject.class.getName(), newClassName);
			odb.close();

			OdbConfiguration.setCheckModelCompatibility(false);
			odb = open(FILE_NAME);
			IStorageEngine engine = Dummy.getEngine(odb);
			MetaModel metaModel = engine.getSession(true).getMetaModel();

			assertEquals(3, metaModel.getNumberOfClasses());
			assertNull(metaModel.getClassInfo(SimpleObject.class.getName(), false));
			assertNotNull(metaModel.getClassInfo(newClassName, true));
			odb.close();

		} finally {
		}
	}

	public void testRenameClassBigName() throws IOException, Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		ODB odb = null;
		try {
			deleteBase(FILE_NAME);
			odb = open(FILE_NAME);

			// Creates 3 different objects to create 3 classes in metamodel
			Object o1 = new Function("f1");
			Object o2 = new SimpleObject("name", 50, null);
			Object o3 = new Profile("profile");

			odb.store(o1);
			odb.store(o2);
			odb.store(o3);

			odb.close();
			String newClassName = SimpleObject.class.getName()
					+ ".test1.MyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClassMyClass";
			// Rename the second class
			odb = open(FILE_NAME);
			odb.getRefactorManager().renameClass(SimpleObject.class.getName(), newClassName);
			odb.close();

			OdbConfiguration.setCheckModelCompatibility(false);
			odb = open(FILE_NAME);
			IStorageEngine engine = Dummy.getEngine(odb);
			MetaModel metaModel = engine.getSession(true).getMetaModel();

			assertEquals(3, metaModel.getNumberOfClasses());
			ClassInfo ci = metaModel.getClassInfo(SimpleObject.class.getName(), false);
			assertNull(ci);
			assertNotNull(metaModel.getClassInfo(newClassName, true));

			assertEquals(1, odb.getObjects(Function.class).size());
			assertEquals(0, odb.getObjects(SimpleObject.class).size());
			assertEquals(1, odb.getObjects(Profile.class).size());
			assertEquals(1, Dummy.getEngine(odb).getObjectInfos(new CriteriaQuery(newClassName), true, -1, -1, false).size());
			odb.close();
		} finally {
		}
	}

	public void testRenameField() throws IOException, Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		ODB odb = null;

		try {
			deleteBase(FILE_NAME);
			odb = open(FILE_NAME);

			Object o1 = new SimpleObject("name", 50, null);
			Object o2 = new Function("f1");

			odb.store(o1);
			odb.store(o2);

			odb.close();

			odb = open(FILE_NAME);
			odb.getRefactorManager().renameField(SimpleObject.class.getName(), "duration", "myduration");
			odb.close();

			OdbConfiguration.setCheckModelCompatibility(false);
			odb = open(FILE_NAME);
			IStorageEngine engine = Dummy.getEngine(odb);
			MetaModel metaModel = engine.getSession(true).getMetaModel();

			assertEquals(2, metaModel.getNumberOfClasses());
			assertNotNull(metaModel.getClassInfo(SimpleObject.class.getName(), true));
			assertNull(metaModel.getClassInfo(SimpleObject.class.getName(), true).getAttributeInfoFromName("duration"));
			assertNotNull(metaModel.getClassInfo(SimpleObject.class.getName(), true).getAttributeInfoFromName("myduration"));
			odb.close();

		} finally {
		}
	}

	public void testAddStringField() throws IOException, Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		ODB odb = null;

		try {
			deleteBase(FILE_NAME);
			odb = open(FILE_NAME);

			Object o1 = new SimpleObject("name", 50, null);
			Object o2 = new Function("f1");

			odb.store(o1);
			odb.store(o2);

			odb.close();

			odb = open(FILE_NAME);
			odb.getRefactorManager().addField(SimpleObject.class.getName(), String.class, "mystring");
			odb.close();

			OdbConfiguration.setCheckModelCompatibility(false);
			odb = open(FILE_NAME);
			IStorageEngine engine = Dummy.getEngine(odb);
			MetaModel metaModel = engine.getSession(true).getMetaModel();

			assertEquals(2, metaModel.getNumberOfClasses());
			ClassInfo ci = metaModel.getClassInfo(SimpleObject.class.getName(), true);
			assertNotNull(ci);
			ClassAttributeInfo cai = ci.getAttributeInfoFromName("mystring");
			assertNotNull(cai);
			assertEquals(cai.getFullClassname(), String.class.getName());
			odb.close();

		} finally {
		}
	}

	public void testAddIntField() throws IOException, Exception {
		if (!isLocal && !testNewFeature) {
			return;
		}

		ODB odb = null;

		try {
			deleteBase(FILE_NAME);
			odb = open(FILE_NAME);

			Object o1 = new SimpleObject("name", 50, null);
			Object o2 = new Function("f1");

			odb.store(o1);
			odb.store(o2);

			odb.close();

			odb = open(FILE_NAME);
			odb.getRefactorManager().addField(SimpleObject.class.getName(), Integer.class, "myint");
			odb.close();

			OdbConfiguration.setCheckModelCompatibility(false);
			odb = open(FILE_NAME);
			IStorageEngine engine = Dummy.getEngine(odb);
			MetaModel metaModel = engine.getSession(true).getMetaModel();

			assertEquals(2, metaModel.getNumberOfClasses());
			ClassInfo ci = metaModel.getClassInfo(SimpleObject.class.getName(), true);
			assertNotNull(ci);
			ClassAttributeInfo cai = ci.getAttributeInfoFromName("myint");
			assertNotNull(cai);
			assertEquals(cai.getFullClassname(), Integer.class.getName());

			odb.close();

		} finally {
		}
	}
}
