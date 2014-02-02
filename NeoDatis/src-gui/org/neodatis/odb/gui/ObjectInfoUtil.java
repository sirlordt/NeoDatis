/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * Used to transform meta meta representations of object into table view. Used
 * by ODBExplorer.
 * 
 * @author osmadja
 * 
 */
public class ObjectInfoUtil {

	public static List buildAttributeNameList(ClassInfo ci) {
		return buildAttributeNameList(null, ci, null, null);
	}

	private static List buildAttributeNameList(String attributeName, ClassInfo ci, String base, Map objectsAlreadyVisited) {
		List result = new ArrayList();
		boolean firstTime = objectsAlreadyVisited == null;
		if (objectsAlreadyVisited == null) {
			objectsAlreadyVisited = new OdbHashMap();
		}
		String baseClassName = null;

		if (base == null) {
			baseClassName = "";
		} else {
			if (base.length() == 0) {
				baseClassName = attributeName;
			} else {
				baseClassName = base + "." + attributeName;
			}
		}

		for (int i = 0; i < ci.getAttributes().size(); i++) {
			ClassAttributeInfo cai = ci.getAttributeInfo(i);
			if (objectsAlreadyVisited.get(cai) != null && !cai.getAttributeType().isNative()) {
				result.add("<-");
				continue;
			}
			objectsAlreadyVisited.put(cai, cai);
			if (cai.getAttributeType().isNative()) {
				String localAttributeName = (baseClassName.length() == 0 ? "" : baseClassName + ".") + cai.getName();
				result.add(localAttributeName);
			} else {
				result.addAll(buildAttributeNameList(cai.getName(), cai.getClassInfo(), baseClassName, objectsAlreadyVisited));
			}
		}
		return result;
	}

	private static List buildAttributeTypeList(ClassInfo ci, String base, Map objectsAlreadyVisited) {
		List result = new ArrayList();
		boolean firstTime = objectsAlreadyVisited == null;
		if (objectsAlreadyVisited == null) {
			objectsAlreadyVisited = new OdbHashMap();
		}
		String baseClassName = null;

		if (base == null) {
			baseClassName = "";
		} else {
			if (base.length() == 0) {
				baseClassName = ci.getFullClassName();
			} else {
				baseClassName = base + "." + ci.getFullClassName().toLowerCase();
			}
		}

		for (int i = 0; i < ci.getAttributes().size(); i++) {
			ClassAttributeInfo cai = ci.getAttributeInfo(i);
			if (objectsAlreadyVisited.get(cai) != null) {
				// result.add("<-");
				continue;
			}
			objectsAlreadyVisited.put(cai, cai);
			if (cai.getAttributeType().isNative()) {
				String attributeName = (baseClassName.length() == 0 ? "" : baseClassName + ".") + cai.getName();
				result.add(attributeName);
			} else {
				result.addAll(buildAttributeNameList(cai.getName(), cai.getClassInfo(), baseClassName, objectsAlreadyVisited));
			}
		}
		return result;
	}

	public static List buildValueList(ClassInfo ci, Collection list) {
		return buildValueList(ci, list, null);
	}

	private static List buildValueList(ClassInfo ci, Collection list, Map objectsAlreadyVisited) {
		List result = new ArrayList();
		List oneLine = null;
		int nbLines = list.size();
		boolean firstTime = objectsAlreadyVisited == null;

		if (objectsAlreadyVisited == null) {
			objectsAlreadyVisited = new OdbHashMap();
		}

		Map otherMap = null;
		int i = 0;
		Iterator iterator = list.iterator();
		// For each line
		while (iterator.hasNext()) {
			if (firstTime) {
				// Here this is the first call in the recursive stack, for each,
				// we must reset the map of
				// alreader read objects
				objectsAlreadyVisited = new OdbHashMap();
			}
			Object element = iterator.next();
			// If element is a non native object
			if (element instanceof NonNativeObjectInfo) {
				NonNativeObjectInfo nnoi = (NonNativeObjectInfo) element;
				oneLine = new ArrayList();

				if (nnoi == null || nnoi.isNull()) {
					oneLine.addAll(nullNonNativeObject(ci, null));
					continue;
				}
				// For each attribute of the non native object
				for (int j = 0; j < nnoi.getAttributeValues().length; j++) {
					Object object = nnoi.getAttributeValues()[j];
					if (objectsAlreadyVisited.get(object) != null) {
						oneLine.addAll(manageNullOrRecursiveObjectInfo(nnoi.getClassInfo().getAttributeInfo(j), "@"));
						continue;
					}
					objectsAlreadyVisited.put(object, object);
					if (object instanceof NonNativeObjectInfo) {
						NonNativeObjectInfo oi = (NonNativeObjectInfo) object;
						if (oi instanceof NonNativeNullObjectInfo) {
							oneLine.addAll(nullNonNativeObject(ci, null));
						} else {
							if (oi.isDeletedObject()) {
								oneLine.add("Deleted Object");
							} else {
								List l = Arrays.asList(oi.getAttributeValues());
								otherMap = new OdbHashMap(objectsAlreadyVisited);
								oneLine.addAll(buildValueList(oi.getClassInfo(), l, objectsAlreadyVisited));
							}
						}
					} else {
						if (object instanceof NonNativeNullObjectInfo) {
							oneLine.addAll(manageNullOrRecursiveObjectInfo(nnoi.getClassInfo().getAttributeInfo(j), "null"));
						} else {
							if (object == null) {
								oneLine.add(null);
							} else {
								ODBType type = ODBType.getFromClass(object.getClass());
								if (type.isCollection()) {
									Collection l = (Collection) object;
									oneLine.add("[" + l.size() + "]:" + buildValueList(null, l, objectsAlreadyVisited));
								} else {
									oneLine.add(object);
								}
							}
						}
					}
				}
				if (firstTime) {
					result.add(oneLine);
				} else {
					result.addAll(oneLine);
				}
			} else {
				if (element != null) {
					ODBType type = ODBType.getFromClass(element.getClass());
					if (type.isCollection()) {

						Collection l = (Collection) element;
						result.add("[" + l.size() + "]:" + buildValueList(null, l, objectsAlreadyVisited));
					} else {

						if (element instanceof NonNativeNullObjectInfo) {
							if (ci != null) {
								result.addAll(manageNullOrRecursiveObjectInfo(ci.getAttributeInfo(i), "null"));
							} else {
								result.add(null);
							}
						} else {
							result.add(element);
						}
					}
				} else {
					result.add(null);
				}
			}
			i++;
		}
		return result;
	}

	private static Collection nullNonNativeObject(ClassInfo ci, Map alreadySet) {
		ArrayList l = new ArrayList();
		if (alreadySet == null) {
			alreadySet = new OdbHashMap();
		}
		ClassAttributeInfo cai = null;
		String attributeId = null;
		for (int i = 0; i < ci.getNumberOfAttributes(); i++) {
			cai = ci.getAttributeInfo(i);
			if (cai.getAttributeType().isNative()) {
				l.add("null");
			} else {
				attributeId = ci.getFullClassName() + "." + cai.getName();
				if (alreadySet.get(attributeId) != null) {
					l.add("<-");
				} else {
					alreadySet.put(attributeId, attributeId);
					l.addAll(nullNonNativeObject(cai.getClassInfo(), alreadySet));
				}
			}
		}
		return l;
	}

	private static List manageNullOrRecursiveObjectInfo(ClassAttributeInfo cai, String item) {
		List list = new ArrayList();
		// the object is null
		// get the type of the object
		if (cai.getAttributeType().isNative()) {
			list.add(new NullNativeObjectInfo(cai.getAttributeType()));
		} else {
			// The object is null and can have more than one attribute, we must
			// insert
			// one null per attribute for flat mode
			int numberOfNullsToInsert = cai.getClassInfo().getAttributes().size();
			for (int k = 0; k < numberOfNullsToInsert; k++) {
				list.add(item);
			}
		}
		return list;
	}
}
