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

package org.neodatis.odb.gui.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.neodatis.odb.core.server.message.GetSessionsMessage;
import org.neodatis.odb.core.server.message.GetSessionsMessageResponse;
import org.neodatis.odb.gui.Messages;
import org.neodatis.odb.gui.tool.GuiUtil;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.ServerAdmin;
import org.neodatis.tool.DisplayUtility;
import org.neodatis.tool.wrappers.OdbString;

public class SessionMonitorPanel extends JPanel implements ActionListener {
	public final static SimpleDateFormat sdt = new SimpleDateFormat("HH:mm:ss");

	private JButton btRefresh;
	private JTextArea textArea;
	private ServerAdmin serverAdmin;

	public SessionMonitorPanel(String host, int port) {
		super();
		this.serverAdmin = new ServerAdmin(host, port);
		initGUI();
	}

	private void initGUI() {
		GuiUtil.setDefaultFont();
		btRefresh = new JButton(Messages.getString("Update"));
		btRefresh.setActionCommand("refresh");
		btRefresh.addActionListener(this);
		textArea = new JTextArea(20, 50);

		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headerPanel.add(new JLabel(serverAdmin.getHost() + ":" + serverAdmin.getPort()));
		headerPanel.add(btRefresh);

		setLayout(new BorderLayout(5, 5));
		add(headerPanel, BorderLayout.NORTH);
		add(new JScrollPane(textArea), BorderLayout.CENTER);

	}

	public void actionPerformed(ActionEvent actionEvent) {
		String action = actionEvent.getActionCommand();
		if ("refresh".equals(action)) {
			refresh();
		}
	}

	private void refresh() {
		GetSessionsMessage message = new GetSessionsMessage();
		try {
			GetSessionsMessageResponse rmessage = (GetSessionsMessageResponse) serverAdmin.sendMessage(message);
			StringBuffer buffer = new StringBuffer();
			buffer.append("Last update : ").append(sdt.format(new Date())).append("\n");

			if (rmessage.hasError()) {
				buffer.append(rmessage.getError());
			} else {
				if (rmessage.getSessions() == null) {
					buffer.append("\nSessions were not retrieved!");
				} else if (rmessage.getSessions().isEmpty()) {
					buffer.append("\nno session!");
				} else {
					buffer.append("Number of sessions : ").append(rmessage.getSessions().size()).append("\n\n");
					buffer.append("\n").append(DisplayUtility.listToString(rmessage.getSessions()));
				}
			}
			textArea.setText(buffer.toString());
		} catch (Exception e) {
			String msg = "Error while getting sessions status " + OdbString.exceptionToString(e, false);
			textArea.setText(msg);
		}

	}

}
