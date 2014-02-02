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
package org.neodatis.odb.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;

import org.neodatis.odb.ODBAuthenticationRuntimeException;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.classbrowser.ClassHierarchyPanel;
import org.neodatis.odb.gui.tool.GuiUtil;
import org.neodatis.odb.gui.xml.XmlExportPanel;
import org.neodatis.tool.ILogger;

public class ODBExplorerPanel extends JPanel implements IBrowserContainer, ActionListener {

	private IBaseIdentification baseIdentification;
	private IStorageEngine engine;

	private ClassHierarchyPanel classHierarchyPanel;

	private JTabbedPane browsingPanel;

	private JSplitPane pane;

	private JInternalFrame graphicContainer;

	private ILogger logger;

	private JButton closeButton;

	private JButton updateButton;
	private JButton commitButton;

	private JButton rollbackButton;

	public ODBExplorerPanel(JInternalFrame graphicContainer, IBaseIdentification baseIdentification, String title, ILogger logger) {
		this.baseIdentification = baseIdentification;
		this.graphicContainer = graphicContainer;
		this.logger = logger;
		init(title);
	}

	private void init(String title) {
		updateEngine();
		classHierarchyPanel = new ClassHierarchyPanel(engine, this, title, logger);
		browsingPanel = new JTabbedPane();
		browsingPanel.setPreferredSize(new Dimension(600, 400));
		browsingPanel.setAutoscrolls(true);
		// browsingPanel.addTab("Query",new JPanel());

		pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, classHierarchyPanel, browsingPanel);
		pane.setDividerLocation(0.3);

		setLayout(new BorderLayout(4, 4));
		add(pane);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel1 = new JPanel();
		JPanel buttonPanel2 = new JPanel();

		/*
		 * JButton btExportXML = new
		 * JButton(Messages.getString("Export all to XML"));
		 * btExportXML.addActionListener(this);
		 * btExportXML.setActionCommand("export-xml");
		 * buttonPanel1.add(btExportXML);
		 */

		updateButton = new JButton(Messages.getString("Refresh"));
		updateButton.addActionListener(this);
		updateButton.setActionCommand("refresh");
		buttonPanel1.add(updateButton);

		commitButton = new JButton(Messages.getString("Commit"));
		commitButton.addActionListener(this);
		commitButton.setActionCommand("commit");
		buttonPanel1.add(commitButton);

		rollbackButton = new JButton(Messages.getString("Rollback"));
		rollbackButton.addActionListener(this);
		rollbackButton.setActionCommand("rollback");
		buttonPanel1.add(rollbackButton);

		closeButton = new JButton(Messages.getString("Close Database"));
		closeButton.addActionListener(this);
		closeButton.setActionCommand("close");
		buttonPanel2.add(closeButton);

		buttonPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

		buttonPanel.add(buttonPanel1, BorderLayout.CENTER);
		buttonPanel.add(buttonPanel2, BorderLayout.EAST);
		add(buttonPanel, BorderLayout.SOUTH);

	}

	public void updateEngine() {
		if (engine != null) {
			engine.close();
		}

		if (baseIdentification.isLocal()) {
			try {
				engine = OdbConfiguration.getCoreProvider().getClientStorageEngine(baseIdentification);
				logger.info("connected!");
			} catch (ODBAuthenticationRuntimeException e) {
				JOptionPane.showMessageDialog(this, "Invalid user/password");
			} catch (Exception e) {
				logger.error("Error while opening base", e);
			}
		} else {

			try {
				engine = OdbConfiguration.getCoreProvider().getClientStorageEngine(baseIdentification);
			} catch (ODBAuthenticationRuntimeException e) {
				JOptionPane.showMessageDialog(this, "Invalid user/password");
			}
		}
		if (classHierarchyPanel != null) {
			classHierarchyPanel.updateEngine(engine);
		}

	}

	public void browse(String title, JPanel panel, int nbObjects) {
		JPanel container = new JPanel(new BorderLayout(4, 4));
		container.add(panel);
		JButton closeButton = new JButton(Messages.getString("Click here to close the Tab"));
		final int index = browsingPanel.getTabCount();
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				browsingPanel.removeTabAt(index);
			}
		});
		container.add(closeButton, BorderLayout.SOUTH);
		if (nbObjects != -1) {
			browsingPanel.addTab(title + "(" + nbObjects + " objects)", container);
		} else {
			browsingPanel.addTab(title, container);
		}
		browsingPanel.setSelectedIndex(browsingPanel.getTabCount() - 1);
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand == null) {
			return;
		}
		if (actionCommand.equals("commit")) {
			try {
				commit();
			} catch (Exception e1) {
				logger.error("Error while commiting", e1);
			}
		}
		if (actionCommand.equals("refresh")) {
			try {
				updateEngine();
			} catch (Exception e1) {
				logger.error("Error while refreshing", e1);
			}
		}
		if (actionCommand.equals("rollback")) {
			try {
				rollback();
			} catch (IOException e1) {
				logger.error("Error while rollbacking", e1);
			}
		}

		if (actionCommand.equals("close")) {
			close();
		}
		if (actionCommand.equals("export-xml")) {
			try {
				exportToXML();
			} catch (Exception e1) {
				logger.error("Error while exporting to xml", e1);
				JOptionPane.showMessageDialog(this, "Error while exporting to xml : " + e1.getMessage());
			}
		}

	}

	private void close() {
		if (engine.getSession(true).transactionIsPending()) {
			logger.error(Messages.getString("Some work has not been commited,\nplease commit or rollback current session!"));
		} else {
			if (graphicContainer != null) {
				int r = JOptionPane.showConfirmDialog(this, Messages.getString("Do you really want to close the database"), Messages
						.getString("Confirmation"), JOptionPane.YES_NO_OPTION);
				if (r == JOptionPane.YES_OPTION) {
					graphicContainer.dispose();
					logger.info("Closing engine");
					engine.close();
				}
			}
		}
	}

	private void commit() throws Exception {
		engine.commit();
		logger.info("Commit executed");

	}

	private void rollback() throws IOException {
		engine.rollback();
		logger.info("Rollback executed");
	}

	private void exportToXML() {
		// Configuration.setUseLazyCache(true);
		XmlExportPanel panel = new XmlExportPanel(logger);
		final JDialog dialog = new JDialog();
		dialog.setTitle(Messages.getString("XML Exportation"));
		dialog.getContentPane().add(panel);
		JButton btClose = new JButton(Messages.getString("Close"));
		btClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		dialog.getContentPane().add(btClose, BorderLayout.SOUTH);
		dialog.setModal(true);
		dialog.pack();
		GuiUtil.centerScreen(dialog);

	}
}
