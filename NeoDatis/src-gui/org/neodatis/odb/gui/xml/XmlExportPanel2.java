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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.LoggerPanel;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.component.GUITool;
import org.neodatis.odb.xml.XMLExporter;
import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.OdbTime;

public class XmlExportPanel2 extends JPanel implements ActionListener, Runnable {

	private IStorageEngine storageEngine;

	private JButton btExport;
	private JButton btCancel;

	private JTextField tfFile;
	private JButton btBrowse;

	private ILogger logger;
	private LoggerPanel loggerPanel;

	public XmlExportPanel2(ILogger logger) {
		this.storageEngine = null;// StorageEngineFactory.get(file, user,
									// password)aStorageEngine;
		this.logger = logger;
		init();
	}

	private void init() {

		JLabel label1 = new JLabel(Messages.getString("Xml File name to export to"));
		tfFile = new JTextField(20);

		btBrowse = new JButton(Messages.getString("..."));
		btExport = new JButton(Messages.getString("Export to XML"));
		btCancel = new JButton(Messages.getString("Cancel"));

		btBrowse.setActionCommand("browse");
		btExport.setActionCommand("export");
		btCancel.setActionCommand("cancel");

		btBrowse.addActionListener(this);
		btExport.addActionListener(this);
		btCancel.addActionListener(this);

		loggerPanel = new LoggerPanel();

		JPanel fpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fpanel.add(label1);
		fpanel.add(tfFile);
		fpanel.add(btBrowse);

		JPanel cpanel = new JPanel(new BorderLayout(4, 4));
		cpanel.add(fpanel, BorderLayout.NORTH);
		cpanel.add(new JScrollPane(loggerPanel), BorderLayout.CENTER);

		JPanel bpanel = new JPanel();
		bpanel.add(btCancel);
		bpanel.add(btExport);

		setLayout(new BorderLayout(4, 4));
		add(cpanel, BorderLayout.CENTER);
		add(bpanel, BorderLayout.SOUTH);
		add(GUITool.buildHeaderPanel("Export to XML Wizard"), BorderLayout.NORTH);

	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if ("browse".equals(action)) {
			browse();
		}
		if ("cancel".equals(action)) {
			cancel();
		}
		if ("export".equals(action)) {
			try {
				storageEngine.close();
				Thread t = new Thread(this);
				t.start();
			} catch (Exception e1) {
				logger.error("Error while export to XML : ", e1);
			}
		}

	}

	private void export() throws Exception {

		File file = new File(tfFile.getText());
		if (file.exists()) {
			int r = JOptionPane.showConfirmDialog(this, Messages.getString("File already exist, do you want to overwrite?"),
					"File already exist", JOptionPane.YES_NO_OPTION);
			if (r != JOptionPane.YES_OPTION) {
				return;
			}
		}
		long start = OdbTime.getCurrentTimeInMs();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		XMLExporter exporter = new XMLExporter(storageEngine);
		exporter.setExternalLogger(loggerPanel);
		exporter.export(file.getParent(), file.getName());
		// JOptionPane.showMessageDialog(this,"Export to " + file.getName() +
		// " sucessfull!");
		disableFields();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		long end = OdbTime.getCurrentTimeInMs();
		loggerPanel.info((end - start) + " ms");

	}

	private void cancel() {
		disableFields();
	}

	private void disableFields() {
		tfFile.setEnabled(false);
		btExport.setEnabled(false);
		btCancel.setEnabled(false);
		btBrowse.setEnabled(false);
	}

	private void browse() {
		final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));

		fc.setDialogTitle(Messages.getString("Choose the name of the xml file to export to"));
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			tfFile.setText(fc.getSelectedFile().getPath());
		}
	}

	public void run() {
		try {
			export();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
			loggerPanel.error(e);
		}

	}

}
