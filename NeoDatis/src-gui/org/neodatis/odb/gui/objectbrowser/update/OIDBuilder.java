package org.neodatis.odb.gui.objectbrowser.update;

import org.neodatis.odb.Configuration;
import org.neodatis.odb.OID;

public class OIDBuilder {
	static OID buildObjectOID(String soid) {
		return Configuration.getCoreProvider().getObjectOID(Long.parseLong(soid), 0);
	}

}
