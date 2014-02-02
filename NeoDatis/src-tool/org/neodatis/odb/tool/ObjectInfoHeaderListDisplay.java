package org.neodatis.odb.tool;

import java.util.List;

import org.neodatis.odb.core.layers.layer2.meta.ObjectInfoHeader;

/**
 * An utility class to build a string description from a list of
 * ObjectInfoHeader
 * 
 * @author osmadja
 * 
 */
public class ObjectInfoHeaderListDisplay {

	public static String build(List objectInfoHeaderList, boolean withDetail) {
		StringBuffer buffer = new StringBuffer();
		ObjectInfoHeader oih = null;
		buffer.append(objectInfoHeaderList.size()).append(" objects : ");
		for (int i = 0; i < objectInfoHeaderList.size(); i++) {
			oih = (ObjectInfoHeader) objectInfoHeaderList.get(i);
			if (withDetail) {
				buffer.append("(").append(oih.getPreviousObjectOID()).append(" <= ").append(oih.getOid()).append(" => ").append(
						oih.getNextObjectOID()).append(") ");
			} else {
				buffer.append(oih.getOid()).append(" ");
			}
		}
		return buffer.toString();
	}
}
