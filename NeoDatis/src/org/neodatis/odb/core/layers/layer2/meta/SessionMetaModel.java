package org.neodatis.odb.core.layers.layer2.meta;

import java.util.Collection;

import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * The main implementation of the MetaModel abstract class.
 * 
 * @author osmadja
 * 
 */
public class SessionMetaModel extends MetaModel {

	/**
	 * A list of changed classes - that must be persisted back when commit is
	 * done
	 */
	private OdbHashMap<ClassInfo, ClassInfo> changedClasses;

	public SessionMetaModel() {
		super();
		changedClasses = new OdbHashMap<ClassInfo, ClassInfo>();
	}

	/**
	 * Saves the fact that something has changed in the class (number of objects
	 * or last object oid)
	 * 
	 * @param classInfo
	 * @param uci
	 */
	public void addChangedClass(ClassInfo classInfo) {
		changedClasses.put(classInfo, classInfo);
		setHasChanged(true);
	}

	public Collection<ClassInfo> getChangedClassInfo() {
		IOdbList<ClassInfo> l = new OdbArrayList<ClassInfo>();
		l.addAll(changedClasses.keySet());
		// TODO return an unmodifianle collection
		// return Collections.unmodifiableCollection(l);
		return l;
	}

	public void resetChangedClasses() {
		this.changedClasses.clear();
		setHasChanged(false);
	}

	public MetaModel duplicate() {
		SessionMetaModel model = new SessionMetaModel();
		IOdbList<ClassInfo> classes = getAllClasses();
		for (ClassInfo ci : classes) {
			model.addClass((ClassInfo) ci.duplicate(false));
		}
		model.changedClasses = new OdbHashMap<ClassInfo, ClassInfo>();
		model.changedClasses.putAll(changedClasses);
		return model;
	}
}
