package org.neodatis.odb.gui;

public class GuiConfiguration {
	private static boolean displayAllClasses = false;

	public static boolean displayAllClasses() {
		return displayAllClasses;
	}

	public static void setDisplayAllClasses(boolean displayAllClasses) {
		GuiConfiguration.displayAllClasses = displayAllClasses;
	}

}
