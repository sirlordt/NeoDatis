/**
 * 
 */
package org.neodatis.odb.test.ee.metamodel;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer1.introspector.IClassIntrospector;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 *
 */
public class TestMetaModel extends ODBTest {
	
	public void testGetMetaModel(){
		
		String baseName = "test.neodatis";
		OdbConfiguration.setCheckModelCompatibility(false);
		//ODB odb = open(baseName);
		//odb.store(new User("user name", "email", new Profile("profile name", new Function("function name"))));
		//odb.close();
		
		ODB odb = open(baseName);
		IStorageEngine engine = Dummy.getEngine(odb);
		MetaModel metaModel = engine.getSession(true).getMetaModel();
		
		IClassIntrospector classIntrospector = engine.getObjectIntrospector().getClassIntrospector();
		
		engine.checkMetaModelCompatibility(classIntrospector.instrospect(metaModel.getAllClasses()));
		
		for(ClassInfo ci : metaModel.getUserClasses()){
			//System.out.println(ci);
			
			System.out.println(ci.getFullClassName());
			System.out.println(ci.getNumberOfAttributes() + " attributes");
			
			for(ClassAttributeInfo cai : ci.getAttributes()){
				System.out.println("\t" + cai.getName() + " - " + cai.getFullClassname() +  " - " + cai.getAttributeType().isNonNative() ) ;
				// check if attribute is non native
				if(cai.getAttributeType().isNonNative()){
					String nativeClassName = cai.getFullClassname();
				}
			}
		}
		odb.close();
		
		
	
	}

}
