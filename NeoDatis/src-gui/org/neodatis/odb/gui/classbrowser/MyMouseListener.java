package org.neodatis.odb.gui.classbrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoHelper;
import org.neodatis.tool.wrappers.OdbClassUtil;

public class MyMouseListener extends MouseAdapter implements ActionListener {
	private static final int TYPE_CLASS = 1;
	private static final int TYPE_FIELD = 2;

	private JTree tree;
	private ClassHierarchyPanel panel;
	private ClassInfo currentClassInfo;
	private ClassAttributeInfo currentClassAttributeInfo;
	private int actionType;

	public MyMouseListener(JTree tree, ClassHierarchyPanel panel) {
		this.tree = tree;
		this.panel = panel;
		this.currentClassInfo = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
		Object object = path.getLastPathComponent();
		if (object == null) {
			System.out.println("Object is null");
			return;
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			if (object instanceof ClassInfoWrapper) {
				ClassInfoWrapper ciw = (ClassInfoWrapper) object;
				currentClassInfo = ciw.getCi();
				actionType = TYPE_CLASS;
				buildPopupForClass(ciw, e.getX(), e.getY());
			}
			if (object instanceof ClassAttributeInfoWrapper) {
				ClassAttributeInfoWrapper caiw = (ClassAttributeInfoWrapper) object;
				actionType = TYPE_CLASS;
				currentClassInfo = caiw.getCi();
				currentClassAttributeInfo = caiw.getCai();
				buildPopupForAttribute(caiw, e.getX(), e.getY());
			}

		}
	}

	public void buildPopupForClass(ClassInfoWrapper classInfoToBrowse, int x, int y) {

		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(":: Class " + OdbClassUtil.getClassName(classInfoToBrowse.getCi().getFullClassName()));

		popup.add(menuItem);
		popup.addSeparator();

		popup.add(buildMenuItem("Object view", ClassHierarchyPanel.ACTION_OBJECT_VIEW, null));
		popup.add(buildMenuItem("Table view", ClassHierarchyPanel.ACTION_TABLE_VIEW, null));
		popup.add(buildMenuItem("Query", ClassHierarchyPanel.ACTION_QUERY, null));
		popup.add(buildMenuItem("New Object", ClassHierarchyPanel.ACTION_NEW_OBJECT, null));

		JMenu menuRefactor = new JMenu("Refactor");
		menuItem = buildMenuItem("Rename class", ClassHierarchyPanel.ACTION_REFACTOR_RENAME_CLASS, null);
		menuRefactor.add(menuItem);
		menuItem = buildMenuItem("Add a field to class", ClassHierarchyPanel.ACTION_REFACTOR_ADD_FIELD, null);
		menuRefactor.add(menuItem);

		/*
		 * menuItem =
		 * buildMenuItem("Add field",ClassHierarchyPanel.ACTION_REFACTOR_ADD_FIELD
		 * , null); menu2.add(menuItem);
		 */

		popup.add(menuRefactor);

		List<String> indexNames = ClassInfoHelper.getIndexNames(classInfoToBrowse.getCi());
		JMenu menuIndexes = null;
		if(indexNames.isEmpty()){
			menuIndexes = new JMenu("No index");
		}else{
			menuIndexes = new JMenu(String.format("%d index(es)",indexNames.size()));
		}
		JMenu menu = null;
		for(String name:indexNames){
			menu = buildMenu(name,null);
			menuIndexes.add(menu);
			
			menuItem = buildMenuItem("is unique ? "+ (ClassInfoHelper.indexIsUnique(classInfoToBrowse.getCi(), name)?"yes":"no"),ClassHierarchyPanel.ACTION_NONE,null);
			menu.add(menuItem);

			menuItem = buildMenuItem("attributes = "+ ClassInfoHelper.getIndexAttributes(classInfoToBrowse.getCi(), name), ClassHierarchyPanel.ACTION_NONE,null);
			menu.add(menuItem);
			
			menuItem = buildMenuItem("size = "+ ClassInfoHelper.getIndexSize(classInfoToBrowse.getCi(), name), ClassHierarchyPanel.ACTION_NONE,null);
			menu.add(menuItem);
			
			menuItem = buildMenuItem("rebuild", ClassHierarchyPanel.ACTION_REBUILD_INDEX+":"+name,null);
			menu.add(menuItem);
			
		}
		popup.add(menuIndexes);

		
		popup.show(tree, x, y);
	}

	public void buildPopupForAttribute(ClassAttributeInfoWrapper classAttributeInfoWrapper, int x, int y) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Field " + classAttributeInfoWrapper.getCai().getName());

		popup.add(menuItem);
		popup.addSeparator();

		JMenu menu2 = new JMenu("Refactor");
		menuItem = buildMenuItem("Rename field", ClassHierarchyPanel.ACTION_REFACTOR_RENAME_FIELD, null);
		menu2.add(menuItem);

		menuItem = buildMenuItem("Remove field", ClassHierarchyPanel.ACTION_REFACTOR_REMOVE_FIELD, null);
		menu2.add(menuItem);
		/*
		 * menuItem =buildMenuItem("Change type",ClassHierarchyPanel.
		 * ACTION_REFACTOR_CHANGE_FIELD_TYPE, null); menu2.add(menuItem);
		 */
		popup.add(menu2);

		popup.show(tree, x, y);
	}

	protected JMenuItem buildMenuItem(String label, String action, String img) {
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.setActionCommand(action);
		menuItem.addActionListener(this);
		return menuItem;
	}
	protected JMenu buildMenu(String label, String img) {
		JMenu menu = new JMenu(label);
		menu.addActionListener(this);
		return menu;
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		panel.actionPerformed(action, currentClassInfo, currentClassAttributeInfo);
		actionType = 0;
		currentClassAttributeInfo = null;
		currentClassInfo = null;
	}

}
