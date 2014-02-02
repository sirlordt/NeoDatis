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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.component.GUITool;

public class ModalObjectBrowserDialog extends JDialog implements ActionListener {
	private HierarchicObjectBrowserPanel panel;
	private boolean choose;

	public ModalObjectBrowserDialog(HierarchicObjectBrowserPanel panel) {
		super();
		setModal(true);
		getContentPane().setLayout(new BorderLayout(5, 5));
		this.panel = panel;
		getContentPane().add(GUITool.buildHeaderPanel(Messages.getString("Choose an object")), BorderLayout.NORTH);
		getContentPane().add(panel, BorderLayout.CENTER);

		JButton btChoose = new JButton("Choose");
		JButton btCancel = new JButton("Cancel");
		btChoose.setActionCommand("choose");
		btCancel.setActionCommand("cancel");

		btChoose.addActionListener(this);
		btCancel.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btCancel);
		buttonPanel.add(btChoose);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if ("cancel".equals(e.getActionCommand())) {
			setVisible(false);
			choose = false;
		}

		if ("choose".equals(e.getActionCommand())) {
			setVisible(false);
			choose = true;
		}
	}

	/**
	 * @return the choose
	 */
	public boolean objectHasBeenChoosen() {
		return choose;
	}

}
