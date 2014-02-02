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
package org.neodatis.odb.gui.objectbrowser.update;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.neodatis.odb.OID;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ObjectReference;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.server.layers.layer2.meta.ClientNonNativeObjectInfo;
import org.neodatis.odb.gui.GUIConstant;
import org.neodatis.odb.gui.IBrowserContainer;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.component.GUITool;
import org.neodatis.odb.gui.objectbrowser.hierarchy.HierarchicObjectBrowserPanel;
import org.neodatis.odb.gui.objectbrowser.hierarchy.ModalObjectBrowserDialog;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.tool.ObjectTool;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class ObjectIntrospectorPanel extends JPanel implements ActionListener {

	private IStorageEngine storageEngine;
	private ClassInfo classInfo;

	private JButton btCreate;
	private JButton btCancel;
	private Map textFields;
	private Map idsTextFieldsForButton;
	private Map classNames;

	private IBrowserContainer browser;
	private NonNativeObjectInfo nnoi;
	private ILogger logger;

	public ObjectIntrospectorPanel(IStorageEngine aStorageEngine, ClassInfo ci, IBrowserContainer aBrowser, NonNativeObjectInfo nnoi,
			ILogger logger) {
		this.storageEngine = aStorageEngine;
		this.classInfo = ci;
		textFields = new OdbHashMap();
		idsTextFieldsForButton = new OdbHashMap();
		classNames = new OdbHashMap();
		this.browser = aBrowser;
		this.nnoi = nnoi;
		this.logger = logger;

		init();
	}

	private void init() {
		JTextField textField = null;
		ClassAttributeInfo cai = null;
		int nbAttributes = classInfo.getAttributes().size();

		JPanel panelLabels = new JPanel(new GridLayout(nbAttributes + 1, 1, 5, 5));
		JPanel panelFields = new JPanel(new GridLayout(nbAttributes + 1, 1, 5, 5));
		JPanel panel1 = null;
		JPanel panel2 = null;
		String text = Messages.getString("Update object of type ") + classInfo.getFullClassName() + Messages.getString(" with oid= ")
				+ nnoi.getOid();
		JPanel headerPanel = GUITool.buildHeaderPanel(text);

		setLayout(new BorderLayout(4, 4));
		add(headerPanel, BorderLayout.NORTH);
		Color headerCellColor = new Color(0, 100, 15);

		JLabel label1 = new JLabel(Messages.getString("Attributes"));
		label1.setBackground(headerCellColor);
		panel1 = new JPanel();
		panel1.add(label1);
		panelLabels.add(panel1);

		JLabel label3 = new JLabel(Messages.getString("Value"));
		label3.setBackground(headerCellColor);

		panel2 = new JPanel();
		panel2.add(label3);
		panelFields.add(panel2);

		JButton btChoose = null;
		Dimension labelDimension = new Dimension(80, 20);
		JLabel label = null;
		AbstractObjectInfo aoi = null;
		for (int i = 0; i < nbAttributes; i++) {
			cai = classInfo.getAttributeInfo(i);
			aoi = nnoi.getAttributeValueFromId(cai.getId());
			if (cai.isNative()) {
				if (cai.getAttributeType().isAtomicNative()) {

					panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
					label = new JLabel(cai.getName(), JLabel.LEFT);
					label.setPreferredSize(labelDimension);
					panel1.add(label);
					panelLabels.add(panel1);

					/*
					 * if (cai.getAttributeType().isDate()) { DateTimePanel
					 * dtPanel = new DateTimePanel((Date) aoi.getObject());
					 * panel1.add(dtPanel); } else {
					 */
					textField = new JTextField(20);

					if (nnoi != null && !aoi.isNull() && aoi.isAtomicNativeObject()) {
						textField.setText(ObjectTool.atomicNativeObjectToString((AtomicNativeObjectInfo) aoi,
								ObjectTool.ID_CALLER_IS_ODB_EXPLORER));
					} else {
						textField.setText(GUIConstant.NULL_OBJECT_LABEL);
					}
					panel1.add(textField);
					// }
				} else if (cai.getAttributeType().isArrayOrCollection()) {
					textField = new JTextField(8);
					panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

					label = new JLabel(cai.getName(), JLabel.LEFT);
					label.setPreferredSize(labelDimension);

					panel1.add(label);
					panel1.add(textField);
					JComboBox combo = buildClassesCombo();
					panel1.add(combo);
					btChoose = new JButton(Messages.getString("Add an object"));
					panel1.add(btChoose);
					btChoose.setActionCommand("browse-add." + cai.getFullClassname());
					btChoose.addActionListener(this);
					panelLabels.add(panel1);
					idsTextFieldsForButton.put(btChoose, textField);
					classNames.put(btChoose, combo);
				}

			} else {
				textField = new JTextField(4);
				NonNativeObjectInfo nn = (NonNativeObjectInfo) nnoi.getAttributeValueFromId(cai.getId());
				if (nnoi != null && nn != null && !nn.isNull()) {
					textField.setText(String.valueOf(nn.getOid()));
				} else {
					textField.setText("null object");
				}
				panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

				label = new JLabel(cai.getName(), JLabel.LEFT);
				label.setPreferredSize(labelDimension);

				panel1.add(label);
				panel1.add(textField);
				btChoose = new JButton(Messages.getString("Choose the object"));
				panel1.add(btChoose);
				btChoose.setActionCommand("browse-set." + cai.getFullClassname());
				btChoose.addActionListener(this);

				panelLabels.add(panel1);
				idsTextFieldsForButton.put(btChoose, textField);
			}

			textFields.put(cai, textField);
		}

		JPanel optionPanel = new JPanel();
		btCreate = new JButton(Messages.getString("Save Object"));
		btCancel = new JButton(Messages.getString("Cancel"));
		optionPanel.add(btCancel);
		optionPanel.add(btCreate);
		btCreate.setActionCommand("update");
		btCancel.setActionCommand("cancel");

		btCreate.addActionListener(this);
		btCancel.addActionListener(this);

		JPanel panelContent = new JPanel(new BorderLayout(5, 5));

		JPanel panel4 = new JPanel();
		panel4.add(panelLabels);
		panelContent.add(panel4, BorderLayout.CENTER);
		JPanel panel3 = new JPanel(new BorderLayout(5, 5));

		panel3.add(new JScrollPane(panelContent), BorderLayout.CENTER);
		panel3.add(optionPanel, BorderLayout.SOUTH);

		add(panel3, BorderLayout.CENTER);

	}

	private JComboBox buildClassesCombo() {
		Vector vector = new Vector();
		Iterator iterator = storageEngine.getSession(true).getMetaModel().getAllClasses().iterator();
		ClassInfo ci = null;
		while (iterator.hasNext()) {
			ci = (ClassInfo) iterator.next();
			vector.add(ci.getFullClassName());
		}
		return new JComboBox(vector);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		String tokenSet = "browse-set.";
		String tokenAdd = "browse-add.";
		if (action.startsWith(tokenSet)) {
			String className = OdbString.substring(action, tokenSet.length(), action.length());
			try {
				OID oid = chooseObject(className);
				if (oid != StorageEngineConstant.NULL_OBJECT_ID) {
					JTextField idTextField = (JTextField) idsTextFieldsForButton.get(e.getSource());
					idTextField.setText(String.valueOf(oid));
				}
			} catch (Exception e1) {
				logger.error("Error setting field", e1);
			}
		}
		if (action.startsWith(tokenAdd)) {
			String className = ((JComboBox) classNames.get(e.getSource())).getSelectedItem().toString();
			try {
				OID oid = chooseObject(className);
				if (oid != StorageEngineConstant.NULL_OBJECT_ID) {
					JTextField idTextField = (JTextField) idsTextFieldsForButton.get(e.getSource());
					String ids = idTextField.getText();
					idTextField.setText(ids + (ids.length() > 0 ? "," : "") + String.valueOf(oid));
				}
			} catch (Exception e1) {
				logger.error("Error setting field", e1);
			}
		}
		if ("update".equals(action)) {
			try {
				updateObject();
			} catch (Exception e1) {
				logger.error("Error setting updating", e1);
				JOptionPane.showMessageDialog(this, "Error while updating object : " + e1.getMessage());
			}
		}
		if ("cancel".equals(action)) {
			btCancel.setEnabled(false);
			btCreate.setEnabled(false);
		}

	}

	private void updateObject() throws Exception {

		Iterator iterator = textFields.keySet().iterator();
		ClassAttributeInfo cai = null;
		NonNativeObjectInfo newNnoi = null;
		if(storageEngine.isLocal()){
			newNnoi = new NonNativeObjectInfo(classInfo);	
		}else{
			newNnoi = new ClientNonNativeObjectInfo(classInfo);
		}
		
		newNnoi.setOid(nnoi.getOid());
		newNnoi.setPosition(nnoi.getPosition());

		while (iterator.hasNext()) {
			cai = (ClassAttributeInfo) iterator.next();
			String value = null;
			/*
			 * if (cai.getAttributeType().isDate()) { Date dt = null; try { dt =
			 * ((DateTimePanel) textFields.get(cai)).getDate(); value =
			 * ObjectTool.format.format(dt);
			 * 
			 * } catch (Exception e) { value = GUIConstant.NULL_OBJECT_LABEL; }
			 * } else {
			 */
			JTextField tfValue = (JTextField) textFields.get(cai);
			value = tfValue.getText();
			// }
			if (cai.isNative()) {
				if (value.equals(GUIConstant.NULL_OBJECT_LABEL)) {
					newNnoi.setAttributeValue(cai.getId(), new NullNativeObjectInfo(cai.getAttributeType()));
				} else if (cai.getAttributeType().isAtomicNative()) {
					newNnoi.setAttributeValue(cai.getId(), ObjectTool.stringToObjectInfo(cai.getAttributeType().getId(), value,
							ObjectTool.ID_CALLER_IS_ODB_EXPLORER,null));
				} else {
					if (cai.getAttributeType().isCollection()) {
						// Must be array of collection
						Collection c = new ArrayList();
						StringTokenizer tokenizer = new StringTokenizer(value, ",");
						while (tokenizer.hasMoreElements()) {
							c.add(new ObjectReference(OIDBuilder.buildObjectOID(tokenizer.nextElement().toString())));
						}
						CollectionObjectInfo coi = new CollectionObjectInfo(c);
						newNnoi.setAttributeValue(cai.getId(), coi);
					}
					if (cai.getAttributeType().isArray()) {
						// Must be array of collection

						StringTokenizer tokenizer = new StringTokenizer(value, ",");
						Object[] objects = new Object[tokenizer.countTokens()];
						int i = 0;
						while (tokenizer.hasMoreElements()) {
							objects[i++] = new ObjectReference(OIDBuilder.buildObjectOID(tokenizer.nextElement().toString()));
						}
						ArrayObjectInfo aoi = new ArrayObjectInfo(objects);
						newNnoi.setAttributeValue(cai.getId(), aoi);
					}

				}
			} else {
				if (value != null && value.length() > 0) {
					newNnoi.setAttributeValue(cai.getId(), new ObjectReference(OIDBuilder.buildObjectOID(value)));
				} else {
					newNnoi.setAttributeValue(cai.getId(), new NonNativeNullObjectInfo(cai.getClassInfo()));
				}
			}
		}
		DLogger.info("Updating object:" + newNnoi);
		OID id = storageEngine.updateObject(newNnoi, false);
		btCancel.setEnabled(false);
		btCreate.setEnabled(false);
		btCreate.setText(Messages.getString("Object updated : id = " + id));

	}

	private OID chooseObject(String className) throws Exception {
		Objects l = storageEngine.getObjectInfos(new CriteriaQuery(className), true, -1, -1, false);
		// TODO: Try to avoid copying the list here. TreeModel needs a list
		// because it uses get(index) and indexOf(Object)
		List list = new ArrayList(l.size());
		list.addAll(l);
		ClassInfo classInfoToBrowse = storageEngine.getSession(true).getMetaModel().getClassInfo(className, true);
		HierarchicObjectBrowserPanel panel = new HierarchicObjectBrowserPanel(browser, storageEngine, classInfoToBrowse, list, false,
				logger);
		ModalObjectBrowserDialog modalBrowser = new ModalObjectBrowserDialog(panel);
		modalBrowser.pack();
		modalBrowser.setVisible(true);
		if (modalBrowser.objectHasBeenChoosen()) {
			return panel.getSelectedOid();
		}
		return StorageEngineConstant.NULL_OBJECT_ID;
	}

}
