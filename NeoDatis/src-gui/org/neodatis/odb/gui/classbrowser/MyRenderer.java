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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

class MyRenderer extends DefaultTreeCellRenderer {
	private Icon classIcon;
	private Icon fieldIcon;

	public MyRenderer(Icon classIcon, Icon fieldIcon) {
		this.classIcon = classIcon;
		this.fieldIcon = fieldIcon;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (isClass(value)) {
			setIcon(classIcon);
			setToolTipText("Class ");
			return this;
		}
		if (isField(value)) {
			setIcon(fieldIcon);
			setToolTipText("Field ");
			return this;
		}
		setToolTipText(null); // no tool tip
		return this;
	}

	protected boolean isClass(Object value) {
		if (value instanceof ClassInfoWrapper) {
			return true;
		}
		return false;
	}

	protected boolean isField(Object value) {
		if (value instanceof ClassAttributeInfoWrapper) {
			return true;
		}
		return false;
	}
}
