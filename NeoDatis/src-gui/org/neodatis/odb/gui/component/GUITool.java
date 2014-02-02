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
package org.neodatis.odb.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class GUITool {
	private static final String FONT = "<font size=\"3\" face=\"Verdana\">";

	private static final String HEADER_IMG_FILE_NAME = "/img/header.png";

	private static final String LOGO_IMG = "/img/logo.png";

	private static BufferedImage HEADER_IMG = null;
	static {
		try {
			final URL url = new File(HEADER_IMG_FILE_NAME).toURL();

			HEADER_IMG = ImageIO.read(url.getClass().getResourceAsStream(HEADER_IMG_FILE_NAME));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JPanel buildLogoPanel(String text) {
		final JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		headerPanel.setPreferredSize(new Dimension(600, 30));
		String surl = "/img/logo.jpg";

		Runnable r = new Runnable() {
			public void run() {
				URL url = null;
				try {
					url = new File(LOGO_IMG).toURL();
					final Border bkgrnd = new CentredBackgroundBorder(ImageIO.read(url));
					headerPanel.setBorder(bkgrnd);
					headerPanel.repaint();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		SwingUtilities.invokeLater(r);

		// headerPanel.setBorder(new EmptyBorder(4,4,4,4));
		JLabel headerLabel = new JLabel("<html>" + FONT + "<b>" + text + "</b></font><html>", JLabel.RIGHT);
		headerLabel.setForeground(Color.WHITE);
		headerPanel.add(headerLabel);
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new BevelBorder(BevelBorder.RAISED));
		panel.add(headerPanel);
		return panel;
	}

	public static JPanel buildHeaderPanel(String text) {

		final JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headerPanel.setPreferredSize(new Dimension(600, 30));
		Border bkgrnd = null;
		if (HEADER_IMG != null) {
			bkgrnd = new CentredBackgroundBorder(HEADER_IMG);
			headerPanel.setBorder(bkgrnd);
		}

		JLabel headerLabel = new JLabel("<html>" + FONT + "<b>" + text + "</b></font><html>", JLabel.LEFT);
		headerLabel.setForeground(Color.WHITE);
		headerPanel.add(headerLabel);
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY, Color.DARK_GRAY));
		panel.add(headerPanel);
		return panel;
	}

}
