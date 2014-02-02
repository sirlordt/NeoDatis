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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.IBrowserContainer;
import org.neodatis.odb.gui.objectbrowser.flat.FlatQueryResultPanel;
import org.neodatis.odb.gui.objectbrowser.hierarchy.HierarchicObjectBrowserPanel;
import org.neodatis.odb.gui.objectbrowser.update.NewObjectPanel;
import org.neodatis.odb.gui.query.CriteriaQueryPanel;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.OdbString;

public class ClassHierarchyPanel extends JPanel {

	public static final String ACTION_NONE = "none";
	public static final String ACTION_OBJECT_VIEW = "object-view";
	public static final String ACTION_TABLE_VIEW = "table-view";
	public static final String ACTION_NEW_OBJECT = "new-object";
	public static final String ACTION_QUERY = "query";
	public static final String ACTION_REFACTOR_RENAME_CLASS = "refactor-rename-class";
	public static final String ACTION_REFACTOR_ADD_FIELD = "refactor-add-field";

	public static final String ACTION_REFACTOR_RENAME_FIELD = "refactor-rename-field";
	public static final String ACTION_REFACTOR_REMOVE_FIELD = "refactor-remove-field";
	public static final String ACTION_REFACTOR_CHANGE_FIELD_TYPE = "refactor-change-field-type";
	public static final String ACTION_REBUILD_INDEX = "rebuild-index";
	
	private static final int MAX_OBJECTS = 300;
	
	private IStorageEngine engine;
	private ClassHierarchyModel model;

	private JTree tree;

	private IBrowserContainer browser;

	private JButton closeButton;

	private JButton commitButton;

	private JButton rollbackButton;

	private JButton fakeButton;

	/** to display errors */
	private ILogger logger;

	public ClassHierarchyPanel(IStorageEngine theEngine, IBrowserContainer browser, String title, ILogger logger) {
		super();
		this.engine = theEngine;
		this.browser = browser;
		this.logger = logger;

		System.out.println("Setting engine : " + engine);
		System.out.println("Title = " + title);
		initGUI(title);
	}

	private void initGUI(String title) {
		// setBorder(new EmptyBorder(4, 4, 4, 4));
		setLayout(new BorderLayout(4, 4));
		TreeNode root = new DefaultMutableTreeNode(getTitle());

		model = new ClassHierarchyModel(engine, root);
		tree = new JTree(model);

		ImageIcon classIcon = createImageIcon("/img/class.png");
		ImageIcon fieldIcon = createImageIcon("/img/field.png");

		// To add odb icons to nodes
		tree.setCellRenderer(new MyRenderer(classIcon, fieldIcon));

		// tree.addTreeSelectionListener(this);
		tree.addMouseListener(new MyMouseListener(tree, this));
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		add(scrollPane);

		// add(GUITool.buildHeaderPanel("ODB Explorer - Object model"),
		// BorderLayout.NORTH);
		setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

	}

	public void updateEngine(IStorageEngine engine) {
		this.engine = engine;
		this.model.updateEngine(engine);
		tree.invalidate();
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = ClassHierarchyPanel.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}
		System.err.println("Couldn't find file: " + path);
		return null;
	}

	private Object getTitle() {
		if (engine == null) {
			System.out.println("Engine is null!");
		}
		IBaseIdentification p = engine.getBaseIdentification();
		return p.toString();
		/*
		 * if(p instanceof IOFileParameter){ IOFileParameter fp =
		 * (IOFileParameter) p; return fp.getFileName(); }else{
		 * IOSocketParameter sp = (IOSocketParameter) p; return sp.toString(); }
		 */
	}

	public void actionPerformed(String action, ClassInfo ci, ClassAttributeInfo cai) {
		String actionCommand = action;
		if (actionCommand == null) {
			return;
		}

		if (actionCommand.equals(ACTION_TABLE_VIEW)) {
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				flatBrowseObjects(ci);
			} catch (Exception e1) {
				logger.error("Error while browsing ", e1);
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, "Error while browsing : " + e1.getMessage());
			} finally {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		if (actionCommand.equals(ACTION_OBJECT_VIEW)) {
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				hierarchyBrowseObjects(ci);
			} catch (Exception e1) {
				logger.error("Error while browsing ", e1);
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, "Error while browsing : " + e1.getMessage());
			} finally {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		if (actionCommand.equals(ACTION_QUERY)) {
			try {
				queryObjects(ci);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				logger.error("Error while executing query ", e1);
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, "Error while executing query : " + e1.getMessage());
			} finally {
			}
		}
		if (actionCommand.equals(ACTION_NEW_OBJECT)) {
			try {
				newObjectPanel(ci);
			} catch (Exception e1) {
				logger.error("Error while creating a new object ", e1);
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, "Error while creating new Object : " + e1.getMessage());
			} finally {
			}
		}
		if (actionCommand.equals(ACTION_REFACTOR_RENAME_CLASS)) {
			try {
				logger.info("Renaming class " + ci.getFullClassName());
				String newClassName = JOptionPane.showInputDialog(this, "Enter the new full class name", ci.getFullClassName());
				if (newClassName == null) {
					JOptionPane.showMessageDialog(this, "The class name must be defined");
				}
				engine.getRefactorManager().renameClass(ci.getFullClassName(), newClassName);
				JOptionPane.showMessageDialog(this, "Refactor was successfull!");
			} catch (Exception e1) {
				logger.error("Error while renaming class " + ci.getFullClassName());
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, "Error while renaming class : " + e1.getMessage());
			} finally {
			}
		}
		if (actionCommand.startsWith(ACTION_REBUILD_INDEX)) {
			try {
				String indexName = actionCommand.split(":")[1];
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				logger.info("Rebuilding index " + indexName + " on class "+ ci.getFullClassName());
				engine.rebuildIndex(ci.getFullClassName(), indexName, true);
				JOptionPane.showMessageDialog(this, "Rebuild index was successfull!");
			} catch (Exception e1) {
				logger.error("Error while rebuilding index " + ci.getFullClassName());
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, "Error while rebuilding index : " + e1.getMessage());
			} finally {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
		if (actionCommand.equals(ACTION_REFACTOR_RENAME_FIELD)) {
			try {
				logger.info("Renaming field " + ci.getFullClassName() + "." + cai.getName());
				String newFieldName = JOptionPane.showInputDialog(this, "Enter the new field name", cai.getName());
				engine.getRefactorManager().renameField(ci.getFullClassName(), cai.getName(), newFieldName);
				JOptionPane.showMessageDialog(this, "Refactor was successfull!");
			} catch (Exception e1) {
				logger.error("Error while renaming class " + ci.getFullClassName());
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, "Error while renaming class : " + e1.getMessage());
			} finally {
			}
		}
		if (actionCommand.equals(ACTION_REFACTOR_REMOVE_FIELD)) {
			try {
				logger.info("Removing field " + ci.getFullClassName() + "." + cai.getName());
				int r = JOptionPane.showConfirmDialog(this, "Do you really want to remove field " + cai.getName() + " from "
						+ ci.getFullClassName());
				if (r == JOptionPane.OK_OPTION) {
					engine.getRefactorManager().removeField(ci.getFullClassName(), cai.getName());
					JOptionPane.showMessageDialog(this, "Field " + cai.getName() + " removed successfully!");
				}
			} catch (Exception e1) {
				String msg = "Error while removing field + " + cai.getName() + " from " + ci.getFullClassName() + " : "
						+ OdbString.exceptionToString(e1, true);
				logger.error(msg);
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, msg);
			} finally {
			}
		}
		if (actionCommand.equals(ACTION_REFACTOR_ADD_FIELD)) {
			String newFieldName = null;
			String newFieldType = null;

			try {
				logger.info("Adding a field on class " + ci.getFullClassName());
				newFieldName = JOptionPane.showInputDialog(this, "Enter the field name", "myNewField");
				newFieldType = JOptionPane.showInputDialog(this, "Enter the type of the field", "int or long or org.neodatis.test.MyClass");
				Class type = Class.forName(newFieldType);
				engine.getRefactorManager().addField(ci.getFullClassName(), type, newFieldName);
				JOptionPane.showMessageDialog(this, "Refactor was successfull : field " + newFieldName + " added");
			} catch (Exception e1) {
				String m = "Error while adding a field name=" + newFieldName + ", type=" + newFieldType + " to class "
						+ ci.getFullClassName();
				logger.error(m);
				logger.error(OdbString.exceptionToString(e1, true));
				JOptionPane.showMessageDialog(this, m + ":" + e1.getMessage());
			} finally {
			}
		}

	}

	private void newObjectPanel(ClassInfo ci) {
		if (ci == null) {
			JOptionPane.showMessageDialog(this, "Select a class");
			return;
		}

		String title = "Creating new object of type " + ci.getFullClassName();
		NewObjectPanel panel = new NewObjectPanel(engine, ci, browser, logger);
		browser.browse(title, panel, -1);

	}

	private void flatBrowseObjects(ClassInfo classInfoToBrowse) throws Exception {

		if (classInfoToBrowse == null) {
			JOptionPane.showMessageDialog(this, "Select a class to browse");
			return;
		}
		if (classInfoToBrowse.hasCyclicReference()) {
			JOptionPane.showMessageDialog(this, "<html>Class <b>" + classInfoToBrowse.getFullClassName()
					+ "</b> has cyclic references : it can not be displayed in table view. <br>Please use the Object View</html>");
			return;
		}
		String title = classInfoToBrowse.getFullClassName();
		long nbObjects = engine.getSession(true).getMetaModel().getClassInfo(classInfoToBrowse.getFullClassName(), true)
				.getNumberOfObjects();
		if (nbObjects > MAX_OBJECTS) {
			int userOption = JOptionPane.showConfirmDialog(this, "Class " + classInfoToBrowse.getFullClassName() + "\n has " + nbObjects
					+ " objects. Do you really want to display all ?", "Warning", JOptionPane.OK_CANCEL_OPTION);
			if (userOption == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}
		Objects l = engine.getObjectInfos(new CriteriaQuery(classInfoToBrowse.getFullClassName()), true, -1, -1, false);
		FlatQueryResultPanel panel = new FlatQueryResultPanel(engine, classInfoToBrowse.getFullClassName(), l);
		browser.browse(title, panel, l.size());
	}

	private void queryObjects(ClassInfo classInfoToBrowse) throws Exception {

		if (classInfoToBrowse == null) {
			JOptionPane.showMessageDialog(this, "Select a class to query");
			return;
		}

		String title = "query on " + classInfoToBrowse.getFullClassName();
		CriteriaQueryPanel panel = new CriteriaQueryPanel(engine, classInfoToBrowse, browser, logger);
		browser.browse(title, panel, -1);
	}

	private void hierarchyBrowseObjects(ClassInfo classInfoToBrowse) throws Exception {

		if (classInfoToBrowse == null) {
			JOptionPane.showMessageDialog(this, "Select a class to browse");
			return;
		}

		long nbObjects = engine.getSession(true).getMetaModel().getClassInfo(classInfoToBrowse.getFullClassName(), true)
				.getNumberOfObjects();
		if (nbObjects > MAX_OBJECTS) {
			int userOption = JOptionPane.showConfirmDialog(this, "Class " + classInfoToBrowse.getFullClassName() + "\n has " + nbObjects
					+ " objects. Do you really want to display all ?", "Warning", JOptionPane.OK_CANCEL_OPTION);
			if (userOption == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}

		String title = classInfoToBrowse.getFullClassName();
		Objects<AbstractObjectInfo> l = engine.getObjectInfos(new CriteriaQuery(classInfoToBrowse.getFullClassName()), true, -1, -1, false);
		// TODO: Tru to avoid copying the list here. TreeModel needs a list
		// because it uses get(index) and indexOf(Object)
		List list = new ArrayList<Objects<AbstractObjectInfo>>(l.size());
		list.addAll(l);
		HierarchicObjectBrowserPanel panel = new HierarchicObjectBrowserPanel(browser, engine, classInfoToBrowse, list, true, logger);
		browser.browse(title, panel, l.size());
	}
}
