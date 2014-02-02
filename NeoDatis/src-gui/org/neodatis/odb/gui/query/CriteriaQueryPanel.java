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
package org.neodatis.odb.gui.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.neodatis.odb.Objects;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.query.criteria.ComposedExpression;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.criteria.Operator;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.gui.IBrowserContainer;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.component.GUITool;
import org.neodatis.odb.gui.objectbrowser.flat.FlatQueryResultPanel;
import org.neodatis.odb.gui.objectbrowser.hierarchy.HierarchicObjectBrowserPanel;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.impl.tool.ObjectTool;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class CriteriaQueryPanel extends JPanel implements ActionListener {

	private IStorageEngine storageEngine;
	private ClassInfo classInfo;

	private JButton btExecute;
	private Map<String, JTextField> textFields;
	private Map operatorCombos;
	private IBrowserContainer browser;
	/** Flat or hierarchy */
	private JComboBox cbBrowserType;
	private JTextField tfFrom;
	private JTextField tfTo;
	private ILogger logger;

	public CriteriaQueryPanel(IStorageEngine aStorageEngine, ClassInfo ci, IBrowserContainer aBrowser, ILogger logger) {
		this.storageEngine = aStorageEngine;
		this.classInfo = ci;
		textFields = new OdbHashMap<String, JTextField>();
		operatorCombos = new OdbHashMap();
		this.browser = aBrowser;
		this.logger = logger;

		init();
	}

	private void init() {
		JTextField textField = null;
		JComboBox operatorCombo = null;
		JButton btHelp = null;
		ClassAttributeInfo cai = null;
		int nbAttributes = classInfo.getAttributes().size();

		// Creates a panel for fields
		JPanel fieldsPanel = new JPanel(new GridLayout(nbAttributes + 2, 4, 4, 4));

		setLayout(new BorderLayout(4, 4));
		Color headerFontColor = Color.LIGHT_GRAY;
		Color headerCellColor = new Color(0, 100, 15);

		JLabel label1 = new JLabel("Attribute Name");
		// label1.setForeground(headerFontColor);
		label1.setBackground(headerCellColor);
		fieldsPanel.add(label1);

		JLabel label2 = new JLabel("Operator");
		// label2.setForeground(headerFontColor);
		label2.setBackground(headerCellColor);
		fieldsPanel.add(label2);

		JLabel label3 = new JLabel("Value");
		// label3.setForeground(headerFontColor);
		label3.setBackground(headerCellColor);
		fieldsPanel.add(label3);

		JLabel label4 = new JLabel("ToolTip with Type");
		// label4.setForeground(headerFontColor);
		label4.setBackground(headerCellColor);
		fieldsPanel.add(label4);

		for (int i = 0; i < nbAttributes; i++) {
			cai = classInfo.getAttributeInfo(i);
			textField = new JTextField(8);
			operatorCombo = buildOperatorCombo();
			fieldsPanel.add(new JLabel(cai.getName(), JLabel.LEFT));
			fieldsPanel.add(operatorCombo);
			fieldsPanel.add(textField);

			textFields.put(cai.getName(), textField);
			btHelp = new JButton("?");
			btHelp.setToolTipText("Type is " + cai.getFullClassname());
			fieldsPanel.add(btHelp);
			operatorCombos.put(cai.getName(), operatorCombo);
		}
		textField = new JTextField(8);
		operatorCombo = buildOperatorCombo();
		fieldsPanel.add(textField);
		fieldsPanel.add(operatorCombo);
		textFields.put("other.field.name", textField);
		operatorCombos.put("other.field.name", operatorCombo);

		textField = new JTextField(8);
		fieldsPanel.add(textField);
		textFields.put("other.field.value", textField);
		fieldsPanel.add(new JLabel(""));

		JPanel optionPanel = new JPanel();
		String[] types = { "Object View", "Table View" };
		cbBrowserType = new JComboBox(types);
		tfFrom = new JTextField("-1", 5);
		tfTo = new JTextField("-1", 5);
		optionPanel.add(cbBrowserType);
		optionPanel.add(tfFrom);
		optionPanel.add(tfTo);

		JPanel panel2 = new JPanel(new FlowLayout());
		panel2.add(fieldsPanel);
		JPanel panel = new JPanel(new BorderLayout(5, 5));

		panel.add(new JScrollPane(panel2), BorderLayout.CENTER);
		panel.add(optionPanel, BorderLayout.SOUTH);

		add(panel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		btExecute = new JButton(Messages.getString("execute"));
		btExecute.setActionCommand("execute");
		btExecute.addActionListener(this);
		buttonPanel.add(btExecute);

		add(buttonPanel, BorderLayout.SOUTH);
		add(GUITool.buildHeaderPanel("Criteria Query Wizard"), BorderLayout.NORTH);

	}

	private JComboBox buildOperatorCombo() {

		Operator[] operators = { Operator.EQUAL, Operator.LIKE, Operator.GREATER_THAN, Operator.GREATER_OR_EQUAL, Operator.LESS_THAN,
				Operator.LESS_OR_EQUAL, Operator.CONTAIN };
		return new JComboBox(operators);
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if ("execute".equals(action)) {
			executeQuery();
		}

	}

	private void executeQuery() {

		Iterator iterator = textFields.keySet().iterator();
		ComposedExpression and = Where.and();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			if (key.equals("other.field.value") || key.equals("other.field.name")) {
				continue;
			}
			JTextField tfValue = textFields.get(key);
			JComboBox comboBox = (JComboBox) operatorCombos.get(key);
			String value = tfValue.getText();
			Operator operator = (Operator) comboBox.getSelectedItem();

			if (value.length() != 0) {
				ClassAttributeInfo cai = classInfo.getAttributeInfoFromName(key.toString());
				try {
					// Transform the string in the right format : the format of the attribute
					Object oo = ObjectTool.stringToObject(cai.getAttributeType().getId(), value, ObjectTool.ID_CALLER_IS_ODB_EXPLORER);
					and.add(Where.get(key.toString(), operator, oo));
				} catch (Exception e) {
					logger.error("Error while executing query", e);
					JOptionPane.showMessageDialog(this, "Error while executing query : " + e.getMessage());
				}
				
			}
		}

		// Manage specific fields
		JTextField tfName = textFields.get("other.field.name");
		JTextField tfValue = textFields.get("other.field.value");
		JComboBox comboBox = (JComboBox) operatorCombos.get("other.field.name");
		Operator operator = (Operator) comboBox.getSelectedItem();

		if (tfName.getText().length() != 0 && tfValue.getText().length() != 0) {
			and.add(Where.get(tfName.getText(), operator, tfValue.getText()));
		}

		CriteriaQuery criteriaQuery = null;

		ICriterion criteria = and;

		// Transform AND in a single criteria if there is only one.
		if (and.getNbCriteria() == 1) {
			criteria = and.getCriterion(0);
		}
		if (!and.isEmpty()) {
			criteriaQuery = new CriteriaQuery(classInfo.getFullClassName(), criteria);
		} else {
			criteriaQuery = new CriteriaQuery(classInfo.getFullClassName());
		}
		String browserType = cbBrowserType.getSelectedItem().toString();
		int from = Integer.valueOf(tfFrom.getText()).intValue();
		int to = Integer.valueOf(tfTo.getText()).intValue();
		DLogger.info("Executing Restrictions Query on " + classInfo.getFullClassName() + " : " + criteriaQuery.toString());
		Objects l = null;
		try {
			l = storageEngine.getObjectInfos(criteriaQuery, true, from, to, false);
			String title = classInfo.getFullClassName() + " - query : " + criteriaQuery.toString();
			JPanel panel = null;
			if (browserType.equals("Table View")) {
				panel = new FlatQueryResultPanel(storageEngine, classInfo.getFullClassName(), l);
			} else {
				// TODO: Try to avoid copying the list here. TreeModel needs a
				// list because it uses get(index) and indexOf(Object)
				List list = new ArrayList(l.size());
				list.addAll(l);
				panel = new HierarchicObjectBrowserPanel(browser, storageEngine, classInfo, list, true, logger);
			}

			browser.browse(title, panel, l.size());
		} catch (Exception e) {
			logger.error("Error while executing query", e);
		}

	}

}
