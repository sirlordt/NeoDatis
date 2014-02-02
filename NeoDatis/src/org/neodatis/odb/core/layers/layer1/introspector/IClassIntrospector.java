package org.neodatis.odb.core.layers.layer1.introspector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.core.ITwoPhaseInit;
import org.neodatis.odb.core.layers.layer2.instance.FullInstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.InstantiationHelper;
import org.neodatis.odb.core.layers.layer2.instance.ParameterHelper;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoList;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.tool.wrappers.list.IOdbList;

public interface IClassIntrospector extends ITwoPhaseInit{
	
	public abstract void reset();

	public abstract void addInstanciationHelper(Class clazz, InstantiationHelper helper);

	public abstract void addParameterHelper(Class clazz, ParameterHelper helper);

	public abstract void addFullInstanciationHelper(Class clazz, FullInstantiationHelper helper);

	public abstract void addInstantiationHelper(String clazz, InstantiationHelper helper);

	public abstract void addParameterHelper(String clazz, ParameterHelper helper);

	public abstract void addFullInstantiationHelper(String clazz, FullInstantiationHelper helper);

	public abstract void removeInstantiationHelper(Class clazz);

	public abstract void removeInstantiationHelper(String canonicalName);

	public abstract void removeParameterHelper(Class clazz);

	public abstract void removeParameterHelper(String canonicalName);

	public abstract void removeFullInstantiationHelper(Class clazz);

	public abstract void removeFullInstantiationHelper(String canonicalName);

	/** introspect a list of classes
	 * 
	 * @param classInfos
	 * @return A map where the key is the class name and the key is the ClassInfo: the class meta representation
	 */
	public Map<String,ClassInfo> instrospect(IOdbList<ClassInfo> classInfos);
	
	/**
	 * 
	 * @param clazz
	 *            The class to instrospect
	 * @param recursive
	 *            If true, goes does the hierarchy to try to analyse all classes
	 * @return The list of class info detected while introspecting the class
	 */
	public abstract ClassInfoList introspect(Class clazz, boolean recursive);

	/**
	 * Builds a class info from a class and an existing class info
	 * 
	 * <pre>
	 * The existing class info is used to make sure that fields with the same name will have
	 * the same id
	 * </pre>
	 * 
	 * @param fullClassName
	 *            The name of the class to get info
	 * @param existingClassInfo
	 * @return A ClassInfo - a meta representation of the class
	 */
	public abstract ClassInfo getClassInfo(String fullClassName, ClassInfo existingClassInfo);

	/**
	 * 
	 * @param fullClassName
	 * @param includingThis
	 * @return The list of super classes
	 */
	public abstract List getSuperClasses(String fullClassName, boolean includingThis);

	public abstract IOdbList<Field> getAllFields(String fullClassName);

	public abstract IOdbList<Field> removeUnnecessaryFields(String className, IOdbList<Field> fields);

	public abstract ClassInfoList introspect(String fullClassName, boolean recursive);

	public abstract Constructor getConstructorOf(String fullClassName);

	public abstract Object newFullInstanceOf(Class clazz, NonNativeObjectInfo nnoi);

	public abstract Object newInstanceOf(Class clazz);

	public abstract byte getClassCategory(String fullClassName);
	
	public abstract boolean isSystemClass(String fullClassName);
	
	public Field getField(Class clazz, String fieldName);
	
	/** To force persisting a field or to avoid a field from being persiste
	 * <pre>
	 * </pre>
	 * @param className
	 * @param fieldName
	 * @param yesNo
	 */
	public void persistFieldOfClass(String className, String fieldName, boolean yesNo);

}