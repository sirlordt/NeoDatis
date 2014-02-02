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

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.gui.tool.GuiUtil;

public class ODBExplorerMain {

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		OdbConfiguration.setCheckModelCompatibility(false);
		OdbConfiguration.setAutomaticallyIncreaseCacheSize(true);

		boolean lfOk = false;
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					lfOk = true;
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another
			// look and feel.
		}
		if (!lfOk) {
			try {
				// Set System L&F
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				System.out.println("Look&Feel = " + UIManager.getSystemLookAndFeelClassName());

				// To be able to run without having the JGoodies classes
				String className = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
				UIManager.setLookAndFeel((LookAndFeel) Class.forName(className).newInstance());
			} catch (UnsupportedLookAndFeelException e) {
				// handle exception
			} catch (ClassNotFoundException e) {
				System.out.println("JGoodies Looks&feels are not available");
			} catch (InstantiationException e) {
				// handle exception
			} catch (IllegalAccessException e) {
				// handle exception
			}
		}
		GuiUtil.setDefaultFont();

		// Create and set up the window.
		final ODBExplorerFrame frame = new ODBExplorerFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
