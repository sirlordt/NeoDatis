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
package org.neodatis.odb.gui.objectbrowser.hierarchy;

import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;

public class ObjectBrowserModel extends DefaultTreeModel {

	private ClassInfo ci;

	private List objectValues;

	public ObjectBrowserModel(ClassInfo ci, List objectValues, TreeNode node) {
		super(node);
		this.ci = ci;
		this.objectValues = objectValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.DefaultTreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		if (parent instanceof DefaultMutableTreeNode) {

			Object o = objectValues.get(index);
			return manageRepresentation(o);
		}
		if (parent instanceof NonNativeObjectInfoWrapper) {
			NonNativeObjectInfoWrapper nnaiw = (NonNativeObjectInfoWrapper) parent;
			NonNativeObjectInfo nnoi = nnaiw.getNnoi();
			ClassAttributeInfo cai = nnaiw.getNnoi().getClassInfo().getAttributeInfo(index);
			String attributeName = cai.getName();
			Object value = nnaiw.getNnoi().getAttributeValues()[index];

			if (value instanceof NonNativeNullObjectInfo) {
				return new NonNativeObjectInfoWrapper(attributeName, (NonNativeObjectInfo) value);
			}

			if (cai.getAttributeType().isNative()) {
				NativeObjectInfo noi = null;
				try {
					noi = (NativeObjectInfo) value;
				} catch (Exception e) {
					noi = new AtomicNativeObjectInfo("Error:" + e.getMessage(), ODBType.STRING_ID);
					System.out.println(noi.getObject());
				}

				if (cai.getAttributeType().isCollection() && !noi.isNull()) {
					return new CollectionWrapper(nnoi, attributeName, (CollectionObjectInfo) value);
				}
				if (cai.getAttributeType().isMap() && !noi.isNull()) {
					return new MapWrapper(nnoi, attributeName, (MapObjectInfo) value);
				}
				if (cai.getAttributeType().isArray() && !noi.isNull()) {
					return new ArrayWrapper(nnoi, attributeName, (ArrayObjectInfo) value);
				}
				return new NativeAttributeValueWrapper(nnoi, attributeName, value, (long) nnaiw.getNnoi().getHeader()
						.getAttributeIdentificationFromId(index + 1));
			}

			if (value instanceof NonNativeObjectInfo) {
				return new NonNativeObjectInfoWrapper(attributeName, (NonNativeObjectInfo) nnaiw.getNnoi().getAttributeValues()[index]);
			}
			return value.getClass().getName();
		}

		if (parent instanceof CollectionWrapper) {
			CollectionWrapper cw = (CollectionWrapper) parent;
			Iterator iterator = cw.getCollection().iterator();
			Object element = null;
			for (int i = 0; i <= index && iterator.hasNext(); i++) {
				element = iterator.next();
			}
			return manageRepresentation(element);
		}
		if (parent instanceof ArrayWrapper) {
			ArrayWrapper aw = (ArrayWrapper) parent;
			Object element = null;
			try{
				//element = Array.get(aw.getArray(), index);
				Object[] a = (Object[]) aw.getArray();
				element = a[index];
				System.out.println("Getting array element for index " + index);
				Object o = manageRepresentation(element);
				return o;
			}catch (Exception e) {
				return new StringWrapper("Null");
			}
		}
		if (parent instanceof MapWrapper) {
			MapWrapper mw = (MapWrapper) parent;
			Iterator iterator = mw.getMap().keySet().iterator();
			Object keyElement = null;
			Object valueElement = null;
			for (int i = 0; i <= index && iterator.hasNext(); i++) {
				keyElement = iterator.next();
			}
			valueElement = mw.getMap().get(keyElement);
			return new MapElementWrapper((AbstractObjectInfo) keyElement, (AbstractObjectInfo) valueElement);
		}
		if (parent instanceof MapElementWrapper) {
			MapElementWrapper mew = (MapElementWrapper) parent;
			if (index == 0) {
				return new MapKeyWrapper(mew.getKeyAoi());
			}
			return new MapValueWrapper(mew.getValueAoi());
		}
		if (parent instanceof MapKeyWrapper) {
			MapKeyWrapper mkw = (MapKeyWrapper) parent;
			return manageRepresentation(mkw.getAoi());
		}
		if (parent instanceof MapValueWrapper) {
			MapValueWrapper mvw = (MapValueWrapper) parent;
			return manageRepresentation(mvw.getAoi());
		}

		return "unknown";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.DefaultTreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof DefaultMutableTreeNode) {
			return objectValues.size();
		}
		if (parent instanceof NonNativeObjectInfoWrapper) {
			NonNativeObjectInfoWrapper nnaiw = (NonNativeObjectInfoWrapper) parent;
			AbstractObjectInfo[] aois = nnaiw.getNnoi().getAttributeValues();
			if (aois != null) {
				return aois.length;
			}
			return 0;

		}
		if (parent instanceof CollectionWrapper) {
			CollectionWrapper cw = (CollectionWrapper) parent;
			return cw.getCollection().size();
		}
		if (parent instanceof MapWrapper) {
			MapWrapper mw = (MapWrapper) parent;
			return mw.getMap().size();
		}
		if (parent instanceof ArrayWrapper) {
			ArrayWrapper aw = (ArrayWrapper) parent;
			int size = aw.getArraySize();
			return size;
		}
		if (parent instanceof MapElementWrapper) {
			return 2;
		}
		if (parent instanceof MapKeyWrapper) {
			return 1;
		}
		if (parent instanceof MapValueWrapper) {
			return 1;
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.DefaultTreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		return node instanceof String || node instanceof NativeObjectInfo;
	}

	public int getIndexOfChild(Object parent, Object child) {

		if (parent instanceof DefaultMutableTreeNode) {
			return objectValues.indexOf(child);
		}
		if (parent instanceof NonNativeObjectInfoWrapper) {
			NonNativeObjectInfoWrapper nnaiw = (NonNativeObjectInfoWrapper) parent;
			Wrapper childWrapper = (Wrapper) child;
			return nnaiw.getNnoi().getAttributeId(childWrapper.getObject());
		}
		return 0;
	}

	public Object manageRepresentation(Object object) {

		if (object == null || object instanceof NonNativeNullObjectInfo || object instanceof NullNativeObjectInfo) {
			return new StringWrapper("Null");
		}
		if (object instanceof NonNativeObjectInfo) {
			NonNativeObjectInfo nnoi = (NonNativeObjectInfo) object;
			return new NonNativeObjectInfoWrapper("oid=" + nnoi.getOid(), nnoi);
		}
		if (object instanceof NativeObjectInfo) {
			NativeObjectInfo noi = (NativeObjectInfo) object;
			Object o = noi.getObject();
			String s = "object is null";
			if(o!=null){
				s = new String(o.toString() + " (type="+o.getClass().getName()+")");	
			}
			System.out.println("manageRepresentation returning string " + s);
			return new StringWrapper(s);
		}
		System.out.println("manageRepresentation returning object " + object);
		return object;

	}

	public void refresh(TreePath tp) {
		reload();
		TreePath[] tps = { tp };

		fireTreeNodesRemoved(getRoot(), tps, null, null);

	}

}
