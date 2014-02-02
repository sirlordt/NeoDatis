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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.gui.LoggerPanel;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.tool.ILogger;

public class RemoteConnectionPanel extends ConnectionPanel implements ActionListener {

	private JTextField tfHost;
	private JTextField tfPort;
	private JTextField tfBase;

	private JTextField tfUserName;
	private JPasswordField tfPassword;

	private JComboBox cbConnections;
	private ILogger logger;

	public RemoteConnectionPanel(ILogger logger) {
		this.logger = logger;
		initGUI();
	}

	private void initGUI() {
		logger.info("local connection");
		tfHost = new JTextField(20);
		tfPort = new JTextField(10);
		tfUserName = new JTextField(20);
		tfPassword = new JPasswordField(20);
		tfBase = new JTextField(20);

		Vector<RemoteConnection> v = null;
		try {
			v = new Vector<RemoteConnection>(getRecentRemoteConnections());
		} catch (Exception e) {
			String error = "Error while loading recent conections";
			logger.error(error);
		}
		cbConnections = new JComboBox(v);
		cbConnections.setActionCommand("choose-recent");
		cbConnections.addActionListener(this);
		cbConnections.setPreferredSize(new Dimension((int) cbConnections.getPreferredSize().getWidth(), (int) tfHost.getPreferredSize()
				.getHeight()));

		JButton btChoose = new JButton(Messages.getString("Choose"));
		btChoose.setActionCommand("choose");
		btChoose.addActionListener(this);

		JPanel fieldsPanel = new JPanel(new BorderLayout(5, 5));

		JPanel labelsPanel = new JPanel(new GridLayout(5, 1));
		labelsPanel.add(new JLabel(Messages.getString("Recent")));
		labelsPanel.add(new JLabel(Messages.getString("Host")));
		labelsPanel.add(new JLabel(Messages.getString("Base")));
		labelsPanel.add(new JLabel(Messages.getString("User")));
		labelsPanel.add(new JLabel(Messages.getString("Password")));

		JPanel panel0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel0.add(cbConnections);

		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel1.add(tfHost);
		panel1.add(new JLabel(Messages.getString("Port")));
		panel1.add(tfPort);

		JPanel panel12 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel12.add(tfBase);

		JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel2.add(tfUserName);

		JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel3.add(tfPassword);

		JPanel inputPanel = new JPanel(new GridLayout(5, 1));
		inputPanel.add(panel0);
		inputPanel.add(panel1);
		inputPanel.add(panel12);
		inputPanel.add(panel2);
		inputPanel.add(panel3);

		fieldsPanel.add(labelsPanel, BorderLayout.WEST);
		fieldsPanel.add(inputPanel, BorderLayout.CENTER);

		setLayout(new BorderLayout(5, 5));
		add(fieldsPanel, BorderLayout.CENTER);

	}

	private Collection<RemoteConnection> getRecentRemoteConnections() throws Exception {
		ODB odb = null;
		try {
			odb = ODBFactory.open(Constant.CONNECT_FILE_NAME);
			return odb.getObjects(RemoteConnection.class, true);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	private boolean existConnection(String host, int port, String user) throws Exception {
		ODB odb = null;
		try {
			odb = ODBFactory.open(Constant.CONNECT_FILE_NAME);
			IQuery query = new CriteriaQuery(RemoteConnection.class, Where.and().add(Where.equal("host", host)).add(
					Where.equal("port", port)).add(Where.equal("user", user)));
			return !odb.getObjects(query).isEmpty();
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if ("choose-recent".equals(command)) {
			chooseRecent();
		}
	}

	private void chooseRecent() {
		RemoteConnection rc = (RemoteConnection) cbConnections.getSelectedItem();
		tfHost.setText(rc.getHost());
		tfPort.setText(String.valueOf(rc.getPort()));
		if (rc.getUser() != null) {
			tfUserName.setText(rc.getUser());
		}
		tfBase.setText(rc.getBaseIdentifier());
	}

	public boolean validateData() {
		if (tfHost.getText().length() == 0) {
			logger.error("Please inform the Host name");
			tfHost.requestFocus();
			return false;
		}
		if (tfPort.getText().length() == 0) {
			logger.error("Please inform the Port");
			tfPort.requestFocus();
			return false;
		}
		if (tfBase.getText().length() == 0) {
			logger.error("Please inform the Base");
			tfBase.requestFocus();
			return false;
		}
		return true;

	}

	public Connection getConnection() throws Exception {

		RemoteConnection rc = new RemoteConnection();
		rc.setHost(tfHost.getText());
		rc.setPort(Integer.parseInt(tfPort.getText()));
		rc.setBaseIdentifier(tfBase.getText());
		if (tfUserName.getText().length() > 0) {
			rc.setUser(tfUserName.getText());
			rc.setPassword(tfPassword.getText());
		}
		if (!existConnection(rc.getHost(), rc.getPort(), rc.getUser())) {
			saveConnection(rc);
		}
		logger.info("connecting to " + rc);
		return rc;
	}

	private void saveConnection(Connection connection) throws Exception {
		ODB odb = null;
		try {
			odb = ODBFactory.open(Constant.CONNECT_FILE_NAME);
			odb.store(connection);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}

	}

	public static void main(String[] args) {
		LoggerPanel loggerPanel = new LoggerPanel();
		RemoteConnectionPanel lcp = new RemoteConnectionPanel(loggerPanel);
		JFrame f = new JFrame("Test RemoteConnectionPanel");
		f.getContentPane().add(lcp);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.pack();
		f.setVisible(true);

		JFrame f2 = new JFrame("Test RemoteConnectionPanel");
		f2.getContentPane().add(loggerPanel);
		f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f2.pack();
		f2.setVisible(true);

	}

}
