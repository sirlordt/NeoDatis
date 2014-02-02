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
package org.neodatis.odb.gui.xml;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.gui.LoggerPanel;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.component.GUITool;
import org.neodatis.odb.xml.XMLImporter;
import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.OdbTime;

public class XmlImportPanel extends JPanel implements ActionListener, Runnable {

	private JButton btImport;

	private JButton btCancel;

	private JTextField tfXmlFile;

	private JButton btBrowseXml;

	private JTextField tfOdbFile;

	private JButton btBrowseOdb;

	private JTextField tfUser;

	private JTextField tfPassword;

	private ILogger logger;

	private LoggerPanel loggerPanel;

	public XmlImportPanel(ILogger logger) {
		this.logger = logger;
		init();
	}

	private void init() {

		JLabel label1 = new JLabel(Messages.getString("Xml File name to import from"));
		JLabel label2 = new JLabel(Messages.getString("ODB file to import to"));
		tfXmlFile = new JTextField(20);
		tfOdbFile = new JTextField(20);

		btBrowseXml = new JButton(Messages.getString("..."));
		btBrowseOdb = new JButton(Messages.getString("..."));
		btImport = new JButton(Messages.getString("Import File"));
		btCancel = new JButton(Messages.getString("Cancel"));

		btBrowseOdb.setActionCommand("browse-odb");
		btBrowseXml.setActionCommand("browse-xml");
		btImport.setActionCommand("import");
		btCancel.setActionCommand("cancel");

		btBrowseOdb.addActionListener(this);
		btBrowseXml.addActionListener(this);
		btImport.addActionListener(this);
		btCancel.addActionListener(this);

		JPanel left = new JPanel(new GridLayout(3, 1, 2, 2));
		JPanel center = new JPanel(new GridLayout(3, 1, 2, 2));

		left.add(label1);
		JPanel panel1 = new JPanel();
		panel1.add(tfXmlFile);
		panel1.add(btBrowseXml);

		left.add(label2);
		left.add(new JLabel());
		JPanel panel2 = new JPanel();
		panel2.add(tfOdbFile);
		panel2.add(btBrowseOdb);

		JPanel panel3 = new JPanel();
		JLabel lbUser = new JLabel("User");
		JLabel lbPassword = new JLabel("Password");

		tfUser = new JTextField(5);
		tfPassword = new JPasswordField(5);

		loggerPanel = new LoggerPanel();

		panel3.add(lbUser);
		panel3.add(tfUser);
		panel3.add(lbPassword);
		panel3.add(tfPassword);

		center.add(panel1);
		center.add(panel2);
		center.add(panel3);

		JPanel wpanel = new JPanel(new BorderLayout(2, 2));
		wpanel.add(left, BorderLayout.WEST);
		wpanel.add(center, BorderLayout.CENTER);
		wpanel.add(new JScrollPane(loggerPanel), BorderLayout.SOUTH);
		wpanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel bpanel = new JPanel();
		bpanel.add(btCancel);
		bpanel.add(btImport);
		setLayout(new BorderLayout(4, 4));
		add(wpanel, BorderLayout.CENTER);
		add(bpanel, BorderLayout.SOUTH);
		add(GUITool.buildHeaderPanel("XML Import Wizard"), BorderLayout.NORTH);

	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if ("browse-xml".equals(action)) {
			browse("xml");
		}
		if ("browse-odb".equals(action)) {
			browse("odb");
		}

		if ("cancel".equals(action)) {
			cancel();
		}
		if ("import".equals(action)) {
			try {
				Thread t = new Thread(this);
				t.start();
			} catch (Exception e1) {
				logger.error("Error while importing XML file : ", e1);
			}
		}

	}

	private void importFromXml() throws Exception {
		File xmlFile = new File(tfXmlFile.getText());
		File odbFile = new File(tfOdbFile.getText());
		if (!xmlFile.exists()) {
			JOptionPane.showMessageDialog(this, Messages.getString("The file " + xmlFile.getPath() + " does not exist!"));
			return;
		}
		if (odbFile.exists()) {
			JOptionPane.showMessageDialog(this, Messages.getString("The database file " + odbFile.getPath() + " already exist!"));
			return;
		}
		long start = OdbTime.getCurrentTimeInMs();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		String user = tfUser.getText();
		String password = tfPassword.getText();
		if (user.length() == 0 && password.length() == 0) {
			user = null;
			password = null;
		}
		// Configuration.setUseLazyCache(true);
		ODB odb = ODBFactory.open(odbFile.getPath(), user, password);
		XMLImporter importer = new XMLImporter(odb);
		importer.setExternalLogger(loggerPanel);
		importer.importFile(xmlFile.getParent(), xmlFile.getName());
		odb.close();
		// JOptionPane.showMessageDialog(this,"Import from " + xmlFile.getName()
		// + " sucessfull!");
		disableFields();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		long end = OdbTime.getCurrentTimeInMs();
		loggerPanel.info("Import successfull (" + (end - start) + "ms)");
	}

	private void cancel() {
		disableFields();
	}

	private void disableFields() {
		tfOdbFile.setEnabled(false);
		tfXmlFile.setEnabled(false);
		btImport.setEnabled(false);
		btCancel.setEnabled(false);
		btBrowseOdb.setEnabled(false);
		btBrowseXml.setEnabled(false);
	}

	private void browse(String type) {
		final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		String title = null;

		if (type.equals("xml")) {
			title = Messages.getString("Choose the name of the xml file to import from");
		} else {
			title = Messages.getString("Choose the name of the ODB file to export to");
		}
		fc.setDialogTitle(title);
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (type.equals("xml")) {
				tfXmlFile.setText(fc.getSelectedFile().getPath());
			} else {
				tfOdbFile.setText(fc.getSelectedFile().getPath());
			}

		}
	}

	public void run() {
		try {
			importFromXml();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
			loggerPanel.error(e);
		}

	}

}
