/**
 * 
 */
package org.neodatis.odb;

/**
 * @author olivier
 * 
 */
public class OIDTypes {
	public static final int TYPE_CLASS_OID = 1;
	public static final int TYPE_OBJECT_OID = 2;
	public static final int TYPE_NATIVE_OID = 3;

	public static final int TYPE_EXTERNAL_CLASS_OID = 4;
	public static final int TYPE_EXTERNAL_OBJECT_OID = 5;

	public static final String TYPE_NAME_UNKNOW = "unknown";

	public static final String TYPE_NAME_CLASS_OID = "class-oid";
	public static final String TYPE_NAME_OBJECT_OID = "object-oid";
	public static final String TYPE_NAME_NATIVE_OID = "native-oid";

	public static final String TYPE_NAME_EXTERNAL_CLASS_OID = "ext-class-oid";
	public static final String TYPE_NAME_EXTERNAL_OBJECT_OID = "ext-object-oid";

	public static final String[] names = { TYPE_NAME_CLASS_OID, TYPE_NAME_OBJECT_OID, TYPE_NAME_NATIVE_OID, TYPE_NAME_EXTERNAL_CLASS_OID,
			TYPE_NAME_EXTERNAL_OBJECT_OID };

	public static String getTypeName(int type) {
		return names[type];
	}
}
