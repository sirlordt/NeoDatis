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
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.layers.layer3.IBaseIdentification;
import org.neodatis.odb.core.layers.layer3.IOFileParameter;
import org.neodatis.odb.core.layers.layer3.IOSocketParameter;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.gui.connect.Connection;
import org.neodatis.odb.gui.connect.LocalConnection;
import org.neodatis.odb.gui.connect.MainConnectPanel;
import org.neodatis.odb.gui.connect.RemoteConnection;
import org.neodatis.odb.gui.tool.GuiUtil;
import org.neodatis.odb.gui.xml.XmlExportPanel;
import org.neodatis.odb.gui.xml.XmlImportPanel;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.OdbThread;

public class ODBExplorerFrame extends JFrame implements ActionListener, ItemListener {
	private JDesktopPane desktop;

	private final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));

	private final LoggerPanel logger;

	private String nextThreadAction;

	public ODBExplorerFrame() {
		super("NeoDatis Object Explorer");

		// Make the big window be indented 50 pixels from each edge
		// of the screen.
		int inset = 5;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 5 - 50);
		// setPreferredSize(new Dimension(screenSize.width - inset * 2,
		// screenSize.height - inset * 2));

		// Set up the GUI.
		desktop = new JDesktopPane(); // a specialized layered pane
		desktop.setBackground(Color.WHITE);
		logger = new LoggerPanel();
		DLogger.register(logger);
		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, desktop, logger);
		pane.setOneTouchExpandable(true);
		pane.setDividerLocation(550);
		setContentPane(pane);
		setJMenuBar(createMenuBar());

		// Make dragging a little faster but perhaps uglier.
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

	}

	protected JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// Set up the lone menu.
		JMenu menu = new JMenu("NeoDatis ODB");
		menu.setMnemonic(KeyEvent.VK_K);
		menuBar.add(menu);

		/*
		 * // Set up the first menu item. JMenuItem menuItem = new
		 * JMenuItem("New Knowledge Base"); menuItem.setMnemonic(KeyEvent.VK_N);
		 * menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
		 * ActionEvent.ALT_MASK)); menuItem.setActionCommand("new");
		 * menuItem.addActionListener(this); menu.add(menuItem);
		 */
		// Set up the second menu item.
		JMenuItem menuItem = new JMenuItem("Open DataBase");
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("open");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Export to XML");
		menuItem.setMnemonic(KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("export");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Import from XML");
		menuItem.setMnemonic(KeyEvent.VK_I);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("import");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Set up the second menu item.
		menuItem = new JMenuItem("Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("quit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Set up the options menu.
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
		menuBar.add(menu);

		JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem("Tolerate inconsistency", false);
		cbmi.setMnemonic(KeyEvent.VK_T);
		cbmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		cbmi.setActionCommand("tolerate-inconsistency");
		cbmi.addItemListener(this);
		menu.add(cbmi);

		// Set up the options menu.
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		JMenuItem cbAbout = new JMenuItem("About");
		cbAbout.setMnemonic(KeyEvent.VK_A);
		cbAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		cbAbout.setActionCommand("about");
		cbAbout.addActionListener(this);
		menu.add(cbAbout);

		return menuBar;
	}

	// React to menu selections.
	public void actionPerformed(ActionEvent e) {

		if ("new".equals(e.getActionCommand())) { // new
			return;
		}

		if ("about".equals(e.getActionCommand())) { // new
			displayAbout();
		}

		if ("open".equals(e.getActionCommand())) { // new
			try {
				openODBBase();
			} catch (Exception e1) {
				logger.error("Error while opening base", e1);
			}
			return;
		}
		if ("export".equals(e.getActionCommand())) { // new
			try {
				exportToXml();
			} catch (Exception e1) {
				logger.error("Error while exporting base", e1);
			}
			return;
		}
		if ("import".equals(e.getActionCommand())) { // new
			try {
				importFromXml();
			} catch (Exception e1) {
				logger.error("Error while importing base", e1);
			}
			return;
		}
		if ("quit".equals(e.getActionCommand())) { // new
			quit();
		}
	}

	/**
	 * 
	 */
	private void displayAbout() {
		JInternalFrame iframe = new JInternalFrame("About NeoDatis ODB");
		iframe.setContentPane(new AboutDialog());
		iframe.pack();
		iframe.setVisible(true); // necessary as of 1.3
		iframe.setClosable(true);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int inset = 100;

		iframe.setLocation(screenSize.width / 2 - inset, screenSize.height / 2 - inset);
		desktop.add(iframe);
		try {
			iframe.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
		}

	}

	private void tolerateInconsistency(boolean tolerate) {
		OdbConfiguration.setThrowExceptionWhenInconsistencyFound(!tolerate);
	}

	public void openODBBase() throws Exception {

		IStorageEngine engine = null;
		MainConnectPanel mcp = null;
		Connection connection = null;

		mcp = new MainConnectPanel(this, logger);
		mcp.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int) screenSize.getWidth();
		int h = (int) screenSize.getHeight();
		int wdialog = (int) mcp.getBounds().getWidth();
		int hdialog = (int) mcp.getBounds().getHeight();
		int rw = w / 2 - wdialog / 2;
		int rh = h / 2 - hdialog / 2;
		mcp.setLocation(new Point(rw, rh));
		mcp.setVisible(true);

		connection = mcp.getConnection();
		if (connection == null) {
			return;
		}
		// build the base identification based upon the connection
		IBaseIdentification baseIdentification = null;
		if (connection instanceof LocalConnection) {
			LocalConnection lc = (LocalConnection) connection;
			baseIdentification = new IOFileParameter(lc.getFileName(), true, lc.getUser(), lc.getUser() == null ? null : lc.getPassword());
		} else {
			RemoteConnection rc = (RemoteConnection) connection;
			baseIdentification = new IOSocketParameter(rc.getHost(), rc.getPort(), rc.getBaseIdentifier(), IOSocketParameter.TYPE_DATABASE,
					rc.getUser(), rc.getPassword());
		}

		JInternalFrame iframe = createInternalFrame(connection.toString(), logger);
		ODBExplorerPanel oep = new ODBExplorerPanel(iframe, baseIdentification, connection.toString(), logger);

		iframe.setContentPane(oep);
		iframe.pack();
		iframe.setVisible(true); // necessary as of 1.3

		iframe.setLocation(15, 15);
		desktop.add(iframe);
		try {
			iframe.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
			logger.error("Error while opening base", e);
		}
		mcp.dispose();
	}

	private JInternalFrame createInternalFrame(String title, ILogger logger) {
		ODBInternalFrame iframe = new ODBInternalFrame(title, logger);
		return iframe;
	}

	// Quit the application.
	protected void quit() {
		System.exit(0);
	}

	public void run() {
		logger.info("Iniciando da thread " + OdbThread.getCurrentThreadName());
		if ("open".equals(nextThreadAction)) {
			try {
				openODBBase();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		if ("import".equals(nextThreadAction)) {
			try {
				importFromXml();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		logger.info("Saindo da thread " + OdbThread.getCurrentThreadName());
	}

	private void exportToXml() {

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

	private void importFromXml() {

		XmlImportPanel panel = new XmlImportPanel(logger);
		final JDialog dialog = new JDialog();
		dialog.setTitle(Messages.getString("XML importation"));
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

	public void itemStateChanged(ItemEvent e) {
		tolerateInconsistency(e.getStateChange() == ItemEvent.SELECTED);
	}

}

class MyFileFilter implements FileFilter {

	public boolean accept(File pathname) {
		return pathname.getName().endsWith("*.odb");
	}

}
