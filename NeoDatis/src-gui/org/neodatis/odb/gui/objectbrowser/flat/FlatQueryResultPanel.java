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

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

import org.neodatis.odb.core.layers.layer3.IStorageEngine;

public class FlatQueryResultPanel extends JPanel {
	public FlatQueryResultPanel(IStorageEngine engine, String fullClassName, Collection objectInfoValues) {
		super();
		init(engine, fullClassName, objectInfoValues);
	}

	/**
	 * 
	 * @param engine
	 * @param fullClassName
	 * @param objectInfoValues
	 *            A list AbstractObjectInfos
	 */
	private void init(IStorageEngine engine, String fullClassName, Collection objectInfoValues) {
		setBorder(new EmptyBorder(4, 4, 4, 4));
		setLayout(new BorderLayout(4, 4));
		FlatQueryTableModel model = new FlatQueryTableModel(engine, fullClassName, objectInfoValues);
		JTable table = new JTable(model);
		// table.setPreferredScrollableViewportSize(new Dimension(1500,400));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		initTable(table, model.getAttributeList(), model.getValueList());
		JScrollPane pane = new JScrollPane(table);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(pane);
	}

	private void initTable(JTable table, List attributeNames, List valueList) {

		TableToolTipHeader tth = new TableToolTipHeader(table.getColumnModel());

		tth.setToolTipsText((String[]) attributeNames.toArray(new String[attributeNames.size()]));

		// Assign default ToolTip text for those headers that do not have
		// their own ToolTip text (as indicated by "" in myToolTipsText),
		// to the TTHeader object.

		tth.setToolTipText("Default ToolTip text");

		// Assign the TTHeader to the JTable object as that table's
		// header.

		table.setTableHeader(tth);

		TableColumn column = null;

		int[] sizes = estimateColumnSizes(valueList, table.getColumnCount());
		for (int i = 0; i < table.getColumnCount(); i++) {
			column = table.getColumnModel().getColumn(i);
			int dataWidth = sizes[i];
			int titleWidth = Math.min(80, attributeNames.get(i).toString().length());
			int realWidth = Math.max(dataWidth, titleWidth) * 6 + 10;

			column.setPreferredWidth(realWidth);
		}
	}

	private int[] estimateColumnSizes(List valueList, int numberOfColumns) {
		int[] sizes = new int[numberOfColumns];

		for (int i = 0; i < valueList.size(); i++) {
			List l = (List) valueList.get(i);
			for (int j = 0; j < numberOfColumns; j++) {
				if (l.size() > j) {
					Object o = l.get(j);
					if (o != null) {
						int length = o.toString().length();
						if (length > sizes[j]) {
							sizes[j] = length;
						}
					}
				}
			}
		}
		return sizes;
	}

}
