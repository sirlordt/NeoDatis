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
package org.neodatis.odb.gui.objectbrowser.flat;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

class TableToolTipHeader extends JTableHeader {
	// The following String array holds all ToolTip text, with one entry
	// for each table column. If a column is to display default ToolTip
	// text, the corresponding entry is "".

	private String[] allToolTipsText;

	public TableToolTipHeader(TableColumnModel tcm) {
		// Pass the TableColumnModel object to the superclass, which
		// takes care of that object.

		super(tcm);
	}

	// The following method is automatically called when the mouse
	// cursor hotspot moves over any one of the header rectangles in a
	// table header.

	public String getToolTipText(MouseEvent e) {
		// Return the pixel position of the mouse cursor hotspot.

		Point p = e.getPoint();

		// Convert the pixel position to the zero-based column index of
		// the table header column over which the mouse cursor hotspot is
		// located. The result is a view-based column index.

		int viewColumnIndex = columnAtPoint(p);

		// Retrieve a reference to the JTable object associated with the
		// table header.

		JTable jt = getTable();

		// Convert the view-based column index to a model-based column
		// index.

		int modelColumnIndex = jt.convertColumnIndexToModel(viewColumnIndex);

		// If model's ToolTip text is not present in allToolTipsText,
		// that means the default ToolTip text should be returned.
		// Otherwise, return the actual ToolTip text.

		if (allToolTipsText[modelColumnIndex].length() == 0)
			return super.getToolTipText(e);

		return allToolTipsText[modelColumnIndex];
	}

	void setToolTipsText(String[] myToolTipsText) {
		// Save the ToolTips text array for use by getToolTipText().

		allToolTipsText = myToolTipsText;
	}
}
