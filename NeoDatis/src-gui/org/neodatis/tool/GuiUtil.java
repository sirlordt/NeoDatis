package org.neodatis.tool;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class GuiUtil {
	public static void setDefaultFont() {
		setUIFont(new FontUIResource("Arial", Font.PLAIN, 12));
	}

	public static void setUIFont(FontUIResource f) {
		//
		// sets the default font for all Swing components.
		// ex.
		// setUIFont (new
		// javax.swing.plaf.FontUIResource("Serif",Font.ITALIC,12));
		//
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public static void centerScreen(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int) screenSize.getWidth();
		int h = (int) screenSize.getHeight();
		int wdialog = (int) dialog.getBounds().getWidth();
		int hdialog = (int) dialog.getBounds().getHeight();
		int rw = w / 2 - wdialog / 2;
		int rh = h / 2 - hdialog / 2;
		dialog.setLocation(new Point(rw, rh));
		dialog.setVisible(true);
		dialog.requestFocus();
	}
}
