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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.OdbString;

public class LoggerPanel extends JPanel implements ILogger {

	private JTextArea ta;

	public LoggerPanel() {
		initGUI();
	}

	private void initGUI() {
		ta = new JTextArea(5, 30);
		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(ta));

	}

	public void debug(Object object) {
		String s = ta.getText();
		ta.setText(s + "\n" + String.valueOf(object));
		ta.setCaretPosition(ta.getText().length());
	}

	public void error(Object object) {
		String header = "An internal error occured,please email the error stack trace displayed below to odb.support@neodatis.org";
		String s = ta.getText();
		ta.setText(s + "\n" + header + "\n" + String.valueOf(object));
		ta.setCaretPosition(ta.getText().length());
		JOptionPane.showMessageDialog(null, header);
	}

	public void error(Object object, Throwable throwable) {
		String header = "An internal error occured,please email the error stack trace displayed below to odb.support@neodatis.org";
		String s = ta.getText();
		ta.setText(s + "\n" + header + "\n" + String.valueOf(object) + ":\n" + OdbString.exceptionToString(throwable, false));
		ta.setCaretPosition(ta.getText().length());
		JOptionPane.showMessageDialog(null, header);
	}

	public void info(Object object) {
		String s = ta.getText();
		ta.setText(s + "\n" + String.valueOf(object));
		ta.setCaretPosition(ta.getText().length());
	}

}
