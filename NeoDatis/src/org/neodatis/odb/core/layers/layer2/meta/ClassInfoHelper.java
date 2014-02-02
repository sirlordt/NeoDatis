/**
 * 
 */
package org.neodatis.odb.core.layers.layer2.meta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neodatis.tool.wrappers.list.IOdbList;

/**
 * @author olivier
 *
 */
public class ClassInfoHelper {
	public static List<String> getIndexDescriptions(ClassInfo classInfo) {
		IOdbList<ClassInfoIndex> indexes = classInfo.getIndexes();
		List<String> indexDescriptions = new ArrayList<String>();
		for(ClassInfoIndex index : indexes){
			String name = index.getName();
			String fieldNames = classInfo.getAttributeNamesAsList(index.getAttributeIds()).toString();
			String created = new Date(index.getCreationDate()).toString();
			indexDescriptions.add(String.format("Index %s, attributes %s, created on %s (%d)", name, fieldNames, created,index.getBTree().getSize()));
		}
		return indexDescriptions;
	}
	
	public static List<String> getIndexAttributes(ClassInfo classInfo, String indexName) {
		ClassInfoIndex index = classInfo.getIndexWithName(indexName);
		return classInfo.getAttributeNamesAsList(index.getAttributeIds());
	}
	public static long getIndexSize(ClassInfo classInfo, String indexName) {
		ClassInfoIndex index = classInfo.getIndexWithName(indexName);
		return index.getBTree().getSize();
	}
	
	public static boolean indexIsUnique(ClassInfo classInfo, String indexName) {
		ClassInfoIndex index = classInfo.getIndexWithName(indexName);
		return index.isUnique();
	}

	public static List<String> getIndexNames(ClassInfo classInfo) {
		IOdbList<ClassInfoIndex> indexes = classInfo.getIndexes();
		List<String> indexNames = new ArrayList<String>();
		for(ClassInfoIndex index : indexes){
			indexNames.add(index.getName());
		}
		return indexNames;
	}


}
