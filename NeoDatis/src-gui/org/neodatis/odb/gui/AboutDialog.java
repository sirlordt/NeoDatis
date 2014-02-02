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
import java.awt.GridLayout;
import java.awt.HeadlessException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.neodatis.odb.core.Release;

public class AboutDialog extends JPanel {

	public AboutDialog() throws HeadlessException {
		super();
		init();
	}

	private void init() {
		setLayout(new BorderLayout(10, 10));

		JLabel label1 = new JLabel("NeoDatis ODB - www.neodatis.org");
		JPanel p2 = new JPanel(new GridLayout(3, 2));

		p2.add(new JLabel("Release date"));
		p2.add(new JLabel(Release.RELEASE_DATE));

		p2.add(new JLabel("Release build"));
		p2.add(new JLabel(Release.RELEASE_BUILD));

		p2.add(new JLabel("Version"));
		p2.add(new JLabel(Release.RELEASE_NUMBER));

		add(label1, BorderLayout.NORTH);
		add(p2, BorderLayout.CENTER);
	}
}
