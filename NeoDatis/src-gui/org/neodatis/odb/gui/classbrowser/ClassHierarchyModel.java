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
package org.neodatis.odb.gui.classbrowser;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.GuiConfiguration;

public class ClassHierarchyModel extends DefaultTreeModel {
	private IStorageEngine engine;

	public ClassHierarchyModel(IStorageEngine engine, TreeNode node) {
		super(node);
		this.engine = engine;
	}

	public void updateEngine(IStorageEngine engine) {
		this.engine = engine;
		// We should fire some event to notify listeners to rebuild the tree
		fireTreeStructureChanged(getRoot(), new Object[] { getRoot() }, new int[] { 0 }, new Object[] { getChild(getRoot(), 0) });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.DefaultTreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		if (parent instanceof DefaultMutableTreeNode) {
			if (GuiConfiguration.displayAllClasses()) {
				return new ClassInfoWrapper((ClassInfo) engine.getSession(true).getMetaModel().getAllClasses().get(index));
			}
			return new ClassInfoWrapper(engine.getSession(true).getMetaModel().slowGetUserClassInfo(index));
		}
		if (parent instanceof ClassInfoWrapper) {
			ClassInfoWrapper ciw = (ClassInfoWrapper) parent;
			ClassAttributeInfo cai = (ClassAttributeInfo) ciw.getCi().getAttributes().get(index);
			return new ClassAttributeInfoWrapper(cai, ciw.getCi());
		}
		if (parent instanceof ClassAttributeInfoWrapper) {
			ClassAttributeInfoWrapper caiw = (ClassAttributeInfoWrapper) parent;
			ClassAttributeInfo cai = caiw.getCai();
			String name = cai.getFullClassname();
			if(cai.getAttributeType().isArray()){
				name = String.format("%s of %s", cai.getFullClassname(),cai.getAttributeType().getSubType().getName());
			}

			switch (index) {
			case 0:
				return name;
			default:
				return "unkown";
			}
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
			if (GuiConfiguration.displayAllClasses()) {
				return engine.getSession(true).getMetaModel().getNumberOfClasses();
			}
			return engine.getSession(true).getMetaModel().getNumberOfUserClasses();
		}
		if (parent instanceof ClassInfoWrapper) {
			ClassInfoWrapper ciw = (ClassInfoWrapper) parent;
			return ciw.getCi().getAttributes().size();
		}
		if (parent instanceof ClassAttributeInfoWrapper) {
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
		return node instanceof String;
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof String) {
			if (GuiConfiguration.displayAllClasses()) {
				return engine.getSession(true).getMetaModel().getAllClasses().indexOf(child);
			}
			return engine.getSession(true).getMetaModel().slowGetUserClassInfoIndex((ClassInfo) child);

		}
		if (parent instanceof ClassInfo) {
			ClassInfo ci = (ClassInfo) parent;
			return ci.getAttributes().indexOf(child);
		}
		if (parent instanceof ClassAttributeInfo) {
			return 0;
		}
		return 0;
	}

}
