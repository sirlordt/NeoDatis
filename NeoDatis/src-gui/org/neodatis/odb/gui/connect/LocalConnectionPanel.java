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
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.gui.LoggerPanel;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.tool.ILogger;

public class LocalConnectionPanel extends ConnectionPanel implements ActionListener {

	private JTextField tfFileName;

	private JTextField tfUserName;

	private JPasswordField tfPassword;

	private JComboBox cbConnections;
	private ILogger logger;

	public LocalConnectionPanel(ILogger logger) {
		this.logger = logger;
		initGUI();
	}

	private void initGUI() {
		tfFileName = new JTextField(30);
		tfUserName = new JTextField(20);
		tfPassword = new JPasswordField(20);

		Vector<LocalConnection> v = null;
		try {
			v = new Vector<LocalConnection>(getRecentLocalConnections());
		} catch (Exception e) {
			String error = "Error while loading recent conections :" + e.getMessage();
			logger.error(error, e);
		}
		cbConnections = new JComboBox(v);
		cbConnections.setActionCommand("choose-recent");
		cbConnections.addActionListener(this);

		JButton btBrowseFile = new JButton(Messages.getString("..."));
		btBrowseFile.setActionCommand("browse-file");
		btBrowseFile.addActionListener(this);
		btBrowseFile.setPreferredSize(new Dimension((int) btBrowseFile.getPreferredSize().getWidth(), (int) tfFileName.getPreferredSize()
				.getHeight()));

		JButton btChoose = new JButton(Messages.getString("Choose"));
		btChoose.setActionCommand("choose");
		btChoose.addActionListener(this);

		JPanel fieldsPanel = new JPanel(new BorderLayout(5, 5));

		JPanel labelsPanel = new JPanel(new GridLayout(4, 1));
		labelsPanel.add(new JLabel(Messages.getString("Recent")));
		labelsPanel.add(new JLabel(Messages.getString("File name")));
		labelsPanel.add(new JLabel(Messages.getString("User")));
		labelsPanel.add(new JLabel(Messages.getString("Password")));

		JPanel panel0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel0.add(cbConnections);

		JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel1.add(tfFileName);
		panel1.add(btBrowseFile);

		JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel2.add(tfUserName);

		JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel3.add(tfPassword);

		JPanel inputPanel = new JPanel(new GridLayout(4, 1));
		inputPanel.add(panel0);
		inputPanel.add(panel1);
		inputPanel.add(panel2);
		inputPanel.add(panel3);

		fieldsPanel.add(labelsPanel, BorderLayout.WEST);
		fieldsPanel.add(inputPanel, BorderLayout.CENTER);

		setLayout(new BorderLayout(5, 5));
		add(fieldsPanel, BorderLayout.CENTER);

	}

	private Collection<LocalConnection> getRecentLocalConnections() throws Exception {
		ODB odb = null;
		try {
			odb = ODBFactory.open(Constant.CONNECT_FILE_NAME);
			IQuery query = new CriteriaQuery(LocalConnection.class);
			return odb.getObjects(query);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if ("browse-file".equals(command)) {
			try {
				browseFile();
			} catch (Exception e1) {
				logger.error("Error while browsing file:", e1);
			}
		}
		if ("choose-recent".equals(command)) {
			chooseRecent();
		}
	}

	private void chooseRecent() {
		LocalConnection lc = (LocalConnection) cbConnections.getSelectedItem();
		tfFileName.setText(lc.getFileName());
		if (lc.getUser() != null) {
			tfUserName.setText(lc.getUser());
		}
	}

	public boolean validateData() {
		if (tfFileName.getText().length() == 0) {
			logger.error("Please inform the file name");
			tfFileName.requestFocus();
			return false;
		}

		return true;
	}

	public Connection getConnection() throws Exception {
		LocalConnection lc = new LocalConnection();
		lc.setDate(new Date());
		lc.setFileName(tfFileName.getText());
		if (tfUserName.getText().length() > 0) {
			lc.setUser(tfUserName.getText());
			lc.setPassword(tfPassword.getText());
		}
		if (!existConnection(tfFileName.getText(), tfUserName.getName())) {
			saveConnection(lc);
		}
		logger.info("connecting to " + lc);
		return lc;
	}

	private void saveConnection(Connection connection) throws Exception {
		ODB odb = null;
		try {
			odb = ODBFactory.open(Constant.CONNECT_FILE_NAME);
			odb.store(connection);
			Objects l = odb.getObjects(ConnectConfig.class);
			ConnectConfig cc = null;
			LocalConnection lc = (LocalConnection) connection;
			File f = new File(lc.getFileName());

			if (l.isEmpty()) {
				cc = new ConnectConfig();

				cc.setLastDirectory(f.getPath());

			} else {
				cc = (ConnectConfig) l.getFirst();
				cc.setLastDirectory(f.getPath());
			}
			odb.store(cc);
		} finally {
			if (odb != null) {
				odb.close();
			}
		}

	}

	private void browseFile() throws Exception {
		String lastDirectory = getLastDirectory();
		JFileChooser chooser = null;
		if (lastDirectory != null) {
			chooser = new JFileChooser(lastDirectory);
		} else {
			String userDir = System.getProperty("user.dir");
			chooser = new JFileChooser(userDir);
		}
		int returnValue = chooser.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			tfFileName.setText(file.getAbsolutePath());
		}
	}

	private boolean existConnection(String fileName, String user) throws Exception {
		ODB odb = null;
		try {
			odb = ODBFactory.open(Constant.CONNECT_FILE_NAME);
			IQuery query = new CriteriaQuery(LocalConnection.class, Where.and().add(Where.equal("fileName", fileName)).add(
					Where.equal("user", user)));
			return !odb.getObjects(query).isEmpty();
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	private String getLastDirectory() throws Exception {
		ODB odb = null;
		try {
			odb = ODBFactory.open(Constant.CONNECT_FILE_NAME);
			Objects l = odb.getObjects(ConnectConfig.class);
			if (l.isEmpty()) {
				return null;
			}
			ConnectConfig cc = (ConnectConfig) l.getFirst();
			return cc.getLastDirectory();
		} finally {
			if (odb != null) {
				odb.close();
			}
		}
	}

	public static void main(String[] args) {
		LoggerPanel loggerPanel = new LoggerPanel();
		LocalConnectionPanel lcp = new LocalConnectionPanel(loggerPanel);
		JFrame f = new JFrame("Test LocalConnectionPanel");
		f.getContentPane().add(lcp);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.pack();
		f.setVisible(true);

		JFrame f2 = new JFrame("Test LocalConnectionPanel");
		f2.getContentPane().add(loggerPanel);
		f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f2.pack();
		f2.setVisible(true);

	}

}
