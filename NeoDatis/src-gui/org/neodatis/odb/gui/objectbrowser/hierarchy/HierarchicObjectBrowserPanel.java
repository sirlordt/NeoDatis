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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.neodatis.odb.OID;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.IBrowserContainer;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.objectbrowser.update.ObjectIntrospectorPanel;
import org.neodatis.tool.ILogger;

public class HierarchicObjectBrowserPanel extends JPanel implements ActionListener, TreeSelectionListener {
	private ClassInfo ci;
	private List objectValues;
	private JTree tree;
	private ObjectBrowserModel model;
	JButton btDeleteObject;
	JButton btUpdateObject;
	JPanel updatePanel;
	private ILogger logger;

	private boolean isEditing;
	private NativeAttributeValueWrapper attributeBeingEdited;
	private NonNativeObjectInfoWrapper objectBeingEdited;
	private NonNativeObjectInfoWrapper selectedObject;
	private OID selectedOid;
	private IStorageEngine engine;
	private IBrowserContainer browser;

	public HierarchicObjectBrowserPanel(IBrowserContainer browser, IStorageEngine engine, ClassInfo ci, List objectValues,
			boolean withButtons, ILogger logger) {
		super();
		this.ci = ci;
		this.objectValues = objectValues;
		isEditing = false;
		attributeBeingEdited = null;
		this.engine = engine;
		this.browser = browser;
		this.logger = logger;
		initGUI(withButtons);
	}

	private void initGUI(boolean withButtons) {
		setBorder(new EmptyBorder(4, 4, 4, 4));
		setLayout(new BorderLayout(4, 4));
		TreeNode root = new DefaultMutableTreeNode(ci.getFullClassName());
		model = new ObjectBrowserModel(ci, objectValues, root);
		tree = new JTree(model);
		tree.addTreeSelectionListener(this);
		/*
		 * String os = System.getProperty("os.name"); // Bug or problem on Mac
		 * OS with setLargeModel if(!os.startsWith("Mac")){
		 * tree.setLargeModel(true); }
		 */
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		add(scrollPane);
		updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btDeleteObject = new JButton(Messages.getString("Delete Object"));
		btUpdateObject = new JButton(Messages.getString("Update/Introspect Object"));
		btDeleteObject.setActionCommand("delete-object");
		btUpdateObject.setActionCommand("update-object");

		btDeleteObject.addActionListener(this);
		btUpdateObject.addActionListener(this);

		updatePanel.add(btUpdateObject);
		updatePanel.add(btDeleteObject);

		enableUpdateButton(false);
		if (withButtons) {
			add(updatePanel, BorderLayout.SOUTH);
		}
	}

	private void enableUpdateButton(boolean enable) {
		btDeleteObject.setEnabled(enable);
		btUpdateObject.setEnabled(enable);
	}

	private void showUpdateButton(boolean yes) {
		btDeleteObject.setVisible(yes);
		btUpdateObject.setVisible(yes);
		if (yes) {
			enableUpdateButton(true);
		}
	}

	private void enableUpdateFields(boolean enable) {
		updatePanel.invalidate();
	}

	private void showUpdateFields(boolean visible) {
		showUpdateButton(!visible);
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		if (actionCommand.equals("delete-object")) {
			deleteObject();
			return;
		}

		if (actionCommand.equals("update-object")) {
			updateObject();
			return;
		}

	}

	private void updateObject() {

		ObjectIntrospectorPanel panel = new ObjectIntrospectorPanel(engine, ci, browser, selectedObject.getNnoi(), logger);
		browser.browse("Updating object", panel, -1);

	}

	private void deleteObject() {

		NonNativeObjectInfo nnoi = selectedObject.getNnoi();
		System.out.println("Deleting object with id " + selectedObject.getNnoi().getOid());
		try {
			int r = JOptionPane.showConfirmDialog(this, Messages.getString("Delete object of type ")
					+ nnoi.getClassInfo().getFullClassName() + Messages.getString(" with id ") + nnoi.getOid(), Messages
					.getString("Confirm Deletion"), JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.OK_OPTION) {
				engine.deleteObjectWithOid(selectedObject.getNnoi().getOid(),false);
				TreePath tp = tree.getSelectionModel().getLeadSelectionPath();
				System.out.println("delete done!");
				System.out.println(tp);
				selectedObject.setDeleted(true);
				tree.expandPath(tp);
				tree.collapsePath(tp);
				enableUpdateButton(false);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, Messages.getString("Error while deleting object (" + e.getMessage() + ")"));
		}

	}

	public void valueChanged(TreeSelectionEvent e) {
		Object object = tree.getLastSelectedPathComponent();
		TreePath tp = tree.getSelectionPath();

		if (object != null) {
			/*
			 * if(object instanceof NativeAttributeValueWrapper){
			 * objectBeingEdited = (NonNativeObjectInfoWrapper)
			 * tp.getParentPath().getLastPathComponent();
			 * showUpdateFields(true); startEditing(object); }
			 */

			if (object instanceof NonNativeObjectInfoWrapper && tp.getPathCount() == 2) {
				selectedObject = (NonNativeObjectInfoWrapper) object;
				selectedOid = selectedObject.getNnoi().getOid();
				if (!selectedObject.isDeleted()) {
					btDeleteObject.setText(Messages.getString("Delete object with oid ") + selectedObject.getNnoi().getOid());
					btUpdateObject.setText(Messages.getString("Update object with oid ") + selectedObject.getNnoi().getOid());
					showUpdateFields(false);
				} else {
					btDeleteObject.setText(Messages.getString("Delete ..."));
					enableUpdateButton(false);
				}

			}
		}
	}

	public OID getSelectedOid() {
		return selectedOid;
	}
}
