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
package org.neodatis.odb.gui.connect;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.neodatis.odb.gui.LoggerPanel;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.component.GUITool;
import org.neodatis.tool.ILogger;

public class MainConnectPanel extends JDialog implements ActionListener {

	private JTabbedPane tabs;
	private ILogger logger;
	private Connection connection;
	private boolean dataAreOk;

	public MainConnectPanel(Frame frame, ILogger logger) {
		super(frame, "ODB Connection", true);
		this.logger = logger;
		initGUI();
	}

	private void initGUI() {
		JPanel panel1 = new LocalConnectionPanel(logger);
		JPanel panel2 = new RemoteConnectionPanel(logger);

		panel1.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel2.setBorder(new EmptyBorder(10, 10, 10, 10));
		tabs = new JTabbedPane();
		tabs.add(Messages.getString("Local connections"), panel1);
		tabs.add(Messages.getString("Remote connections"), panel2);

		JPanel panel = new JPanel(new BorderLayout(5, 5));

		panel.add(GUITool.buildHeaderPanel("ODB Connection Wizard"), BorderLayout.NORTH);
		panel.add(tabs, BorderLayout.CENTER);

		JButton btConnect = new JButton(Messages.getString("Connect"));
		btConnect.setActionCommand("connect");
		btConnect.addActionListener(this);
		JPanel panelButton = new JPanel();

		JButton btQuit = new JButton(Messages.getString("Quit"));
		btQuit.setActionCommand("quit");
		btQuit.addActionListener(this);

		panelButton.add(btQuit);
		panelButton.add(btConnect);

		panel.add(panelButton, BorderLayout.SOUTH);
		getContentPane().add(panel);
	}

	public static void main(String[] args) {
		LoggerPanel loggerPanel = new LoggerPanel();
		MainConnectPanel mcp = new MainConnectPanel(null, loggerPanel);
		mcp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mcp.pack();
		mcp.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if ("connect".equals(action)) {
			try {
				connect();
			} catch (Exception e1) {
				logger.error("Unable to connect", e1);
			}
		}
		if ("quit".equals(action)) {
			setVisible(false);
			connection = null;
		}
	}

	private void connect() throws Exception {
		ConnectionPanel panel = (ConnectionPanel) tabs.getSelectedComponent();
		dataAreOk = panel.validateData();
		if (dataAreOk) {
			setVisible(false);
			connection = panel.getConnection();
		}
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

}
