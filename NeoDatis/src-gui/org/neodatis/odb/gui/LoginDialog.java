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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginDialog extends JDialog implements ActionListener {
	private JTextField tfUserName;
	private JPasswordField tfPassword;
	private boolean isOk;

	public LoginDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title, true);
		init();
	}

	private void init() {
		isOk = false;
		getContentPane().setLayout(new BorderLayout(5, 5));

		tfUserName = new JTextField(10);
		tfPassword = new JPasswordField(10);

		JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
		fieldsPanel.add(new JLabel(getLabel("user")));
		fieldsPanel.add(tfUserName);
		fieldsPanel.add(new JLabel(getLabel("password")));
		fieldsPanel.add(tfPassword);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btLogin = new JButton(getLabel("login"));
		btLogin.addActionListener(this);
		buttonPanel.add(btLogin);
		JButton btNoUser = new JButton(getLabel("without user"));
		btNoUser.addActionListener(this);
		buttonPanel.add(btNoUser);

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));

		panel.add(fieldsPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(panel);

		setBackground(Color.WHITE);
		panel.setBackground(getBackground());
		fieldsPanel.setBackground(getBackground());
		buttonPanel.setBackground(getBackground());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int inset = 100;
		setLocation(screenSize.width / 2 - inset, screenSize.height / 2 - inset);
	}

	private String getLabel(String string) {
		return string;
	}

	public void actionPerformed(ActionEvent e) {
		this.isOk = true;
		dispose();
	}

	public String getUserName() {
		if (!isOk || tfUserName.getText().length() == 0) {
			return null;
		}
		return tfUserName.getText();
	}

	public String getPassword() {
		if (!isOk || tfPassword.getPassword().length == 0) {
			return null;
		}
		return new String(tfPassword.getPassword());
	}

	public static void main(String[] args) {
		LoginDialog dialog = new LoginDialog(null, "login");
		dialog.pack();
		dialog.setVisible(true);
	}

}
