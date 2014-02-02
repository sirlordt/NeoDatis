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
package org.neodatis.odb.xml;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NullNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer2.meta.ObjectReference;
import org.neodatis.odb.core.layers.layer2.meta.SessionMetaModel;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.oid.OIDFactory;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.tool.ObjectTool;
import org.neodatis.tool.ConsoleLogger;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.ILogger;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLImporter implements ContentHandler {

	private static final String LOG_ID = "XMLImporter";

	private static final int STATE_UNKNOWN = 0;

	private static final int STATE_CLASS_INFO = 1;

	private static final int STATE_OBJECTS = 2;

	private static final int STATE_OBJECT = 3;

	private static final int STATE_ATTRIBUTE = 4;

	private static final int STATE_ATTRIBUTE_COLLECTION = 5;

	private static final int STATE_ATTRIBUTE_ARRAY = 6;

	private static final int STATE_ATTRIBUTE_MAP = 7;

	private static final int STATE_ODB = 8;

	private IStorageEngine storageEngine;

	private MetaModel metaModel;

	private ClassInfo ci;

	private IOdbList<ClassAttributeInfo> classAttributeInfo;

	private int state;

	private NonNativeObjectInfo nnoi;

	private List objectInfos;

	private OID objectId;

	private OID objectClassId;

	private List attributeCollection;

	private String realClassName;

	private Map attributeMap;

	private Object[] attributeArray;

	private int arrayIndex;

	private int currentLevel;

	private OID maxObjectId;

	/**
	 * The file format version . Information available since 1.9 release, If not
	 * present in the xml, we assume file format version is less that 9
	 */
	private int fileFormatVersion;

	/** When reading an attribute, this field is used to keep its id */
	private int attributeId;

	private int nbObjects;

	private ILogger externalLogger;

	public XMLImporter(IStorageEngine storageEngine) {
		this.storageEngine = storageEngine;
	}

	public XMLImporter(ODB odb) {
		this.storageEngine = Dummy.getEngine(odb);
	}

	public void importFile(String directory, String filename) throws Exception {

		String fullFileName = directory + "/" + filename;

		info("Importing file " + fullFileName);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(false);

		XMLReader xmlReader = factory.newSAXParser().getXMLReader();

		// This class also implements the LexicalHandler and DeclHandler
		// extensions

		// xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",this);
		// xmlReader.setProperty("http://xml.org/sax/properties/declaration-handler",this);

		xmlReader.setContentHandler(this);
		xmlReader.parse(fullFileName);

		storageEngine.commit();
		// storageEngine.close();

	}

	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void startElement(String uri, String localName, String tagName, Attributes attributes) throws SAXException {
		currentLevel++;
		try {

			if (tagName.equals(XmlTags.TAG_ODB)) {
				state = STATE_ODB;
				try {
					maxObjectId = OIDFactory.buildObjectOID(Long.parseLong(attributes.getValue(XmlTags.ATTRIBUTE_MAX_OID)));
					reserveIds(maxObjectId, false);
				} catch (IOException e) {
					throw new ODBRuntimeException(NeoDatisError.XML_RESERVING_IDS.addParameter(maxObjectId), e);
				}
				// Try to get version
				try {
					String version = attributes.getValue(XmlTags.ATTRIBUTE_FILE_FORMAT_VERSION);
					// If file version tag is not present, we have an old xml
					// file format
					if (version == null || version.length() == 0) {
						fileFormatVersion = StorageEngineConstant.VERSION_8;
					} else {
						fileFormatVersion = Integer.parseInt(version);
					}
				} catch (Exception e) {
					// file version tag is not present, we have an old xml file
					// format
					fileFormatVersion = StorageEngineConstant.VERSION_8;
				}
				return;
			}

			if (tagName.equals(XmlTags.TAG_METAMODEL)) {
				metaModel = new SessionMetaModel();
				info("Importing Meta Model");
				return;
			}
			if (tagName.equals(XmlTags.TAG_CLASS)) {
				nbObjects = 0;
				state = STATE_CLASS_INFO;
				String classId = attributes.getValue(XmlTags.ATTRIBUTE_ID);
				String name = attributes.getValue(XmlTags.ATTRIBUTE_NAME);

				// The IF is to manage old xml version
				if (fileFormatVersion >= StorageEngineConstant.CURRENT_FILE_FORMAT_VERSION) {
					ci = new ClassInfo(name, "");
				} else {
					// In the old version, class name and package name are in
					// two different tags
					// And we don't have extra info
					String packageName = attributes.getValue(XmlTags.ATTRIBUTE_PACKAGE_NAME);
					String fullClassName = packageName + "." + name;
					ci = new ClassInfo(fullClassName, "");
				}
				ci.setId(OIDFactory.buildClassOID(Long.parseLong(classId)));
				classAttributeInfo = new OdbArrayList<ClassAttributeInfo>();
				info(". Importing class " + ci.getFullClassName());
				return;
			}
			if (tagName.equals(XmlTags.TAG_ATTRIBUTE) && state == STATE_CLASS_INFO) {
				String id = attributes.getValue(XmlTags.ATTRIBUTE_ID);
				String name = attributes.getValue(XmlTags.ATTRIBUTE_NAME);
				String type = attributes.getValue(XmlTags.ATTRIBUTE_TYPE);
				String isEnum = attributes.getValue(XmlTags.ATTRIBUTE_IS_ENUM);

				ClassAttributeInfo cai = new ClassAttributeInfo(Integer.parseInt(id), name, type, null);

				// Enums must be managed in different way. They are native
				// objects by with a specific class
				// So we first create the cai with ODBType.ENUM.getName() so
				// that NeoDatis
				// undestands it is a native object and then sets the right enum
				// class name
				if (isEnum != null && isEnum.equals("true")) {
					cai = new ClassAttributeInfo(Integer.parseInt(id), name, ODBType.ENUM.getName(), null);
					ODBType odbType = cai.getAttributeType().copy();
					odbType.setName(type);
					cai.setAttributeType(odbType);
					cai.setFullClassName(type);
				}

				if (cai.getAttributeType().isArray()) {
					String arrayOfWhat = attributes.getValue(XmlTags.ATTRIBUTE_ARRAY_OF);
					cai.getAttributeType().setSubType(ODBType.getFromName(arrayOfWhat));
					// copy the odb type to avoid another cai uses the same reference
					cai.setAttributeType(cai.getAttributeType().copy());
				}
				classAttributeInfo.add(cai);
				return;
			}

			if (tagName.equals(XmlTags.TAG_OBJECTS)) {
				state = STATE_OBJECTS;
				objectInfos = new ArrayList();
				return;
			}
			if (tagName.equals(XmlTags.TAG_OBJECT) && state == STATE_OBJECTS) {
				state = STATE_OBJECT;
				String sObjectId = attributes.getValue(XmlTags.ATTRIBUTE_OID);
				objectId = OIDFactory.buildObjectOID(Long.parseLong(sObjectId));
				String sObjectClassId = attributes.getValue(XmlTags.ATTRIBUTE_CLASS_ID);
				objectClassId = OIDFactory.buildClassOID(Long.parseLong(sObjectClassId));
				nnoi = new NonNativeObjectInfo(metaModel.getClassInfoFromId(objectClassId));
				nnoi.setOid(objectId);
				nbObjects++;
				if (nbObjects % 1000 == 0) {
					info(". " + nbObjects + " objects");
				}
				return;
			}
			if (tagName.equals(XmlTags.TAG_ATTRIBUTE) && state == STATE_OBJECT) {
				state = STATE_ATTRIBUTE;
				String id = attributes.getValue(XmlTags.ATTRIBUTE_ID);
				attributeId = Integer.parseInt(id);
				String name = attributes.getValue(XmlTags.ATTRIBUTE_NAME);
				String value = attributes.getValue(XmlTags.ATTRIBUTE_VALUE);
				String encoding = OdbConfiguration.getDatabaseCharacterEncoding();
				if (value != null) {
					if (encoding == null || encoding.equals(StorageEngineConstant.NO_ENCODING)) {
						value = URLDecoder.decode(value);
					} else {
						value = URLDecoder.decode(value, encoding);
					}
				}
				String objectRefId = attributes.getValue(XmlTags.ATTRIBUTE_OBJECT_REF_ID);
				String sIsNull = attributes.getValue(XmlTags.ATTRIBUTE_NULL);
				boolean isNull = false;
				if (sIsNull != null) {
					isNull = true;
				}
				ClassInfo ci = metaModel.getClassInfoFromId(objectClassId);
				if (ci == null) {
					System.out.println("ci is null");
				}
				ClassAttributeInfo cai = ci.getAttributeInfoFromId(attributeId);
				if (cai == null) {
					System.out.println("cai is null");
				}
				ODBType type = cai.getAttributeType();

				if ((type.isArrayOrCollection() || type.isMap()) && !isNull) {
					nnoi.setAttributeValue(attributeId, new NullNativeObjectInfo(type.getId()));
					if (type.isCollection()) {
						state = STATE_ATTRIBUTE_COLLECTION;
					}
					if (type.isArray()) {
						state = STATE_ATTRIBUTE_ARRAY;
					}
					if (type.isMap()) {
						state = STATE_ATTRIBUTE_MAP;
					}
				} else {

					AbstractObjectInfo aoi = null;
					if (objectRefId != null) {
						aoi = new ObjectReference(OIDFactory.buildObjectOID(Long.parseLong(objectRefId)));
						nnoi.setAttributeValue(attributeId, aoi);
					} else {
						if (isNull) {
							if (type.isNonNative()) {
								nnoi.setAttributeValue(attributeId, new NonNativeNullObjectInfo(ci));
							} else {
								nnoi.setAttributeValue(attributeId, new NullNativeObjectInfo(type.getId()));
							}
						} else {
							// when it is an enum, attributeCi (class info) is
							// the real enum class
							ClassInfo attributeCi = null;
							if (type.isEnum()) {
								attributeCi = metaModel.getClassInfo(cai.getFullClassname(), true);
							}
							aoi = ObjectTool.stringToObjectInfo(type.getId(), value, ObjectTool.ID_CALLER_IS_XML, attributeCi);
							nnoi.setAttributeValue(attributeId, aoi);
						}
					}
				}
				return;
			}
			if (tagName.equals(XmlTags.TAG_COLLECTION) && state == STATE_ATTRIBUTE_COLLECTION) {
				int size = Integer.parseInt(attributes.getValue(XmlTags.ATTRIBUTE_SIZE));
				attributeCollection = new ArrayList(size);
				realClassName = attributes.getValue(XmlTags.ATTRIBUTE_REAL_CLASS_NAME);
				return;
			}
			if (tagName.equals(XmlTags.TAG_ARRAY) && state == STATE_ATTRIBUTE_ARRAY) {
				int size = Integer.parseInt(attributes.getValue(XmlTags.ATTRIBUTE_SIZE));
				attributeArray = new Object[size];
				arrayIndex = 0;
				realClassName = attributes.getValue(XmlTags.ATTRIBUTE_ARRAY_OF);
				return;
			}
			if (tagName.equals(XmlTags.TAG_MAP) && state == STATE_ATTRIBUTE_MAP) {
				int size = Integer.parseInt(attributes.getValue(XmlTags.ATTRIBUTE_SIZE));
				attributeMap = new OdbHashMap(size);
				realClassName = attributes.getValue(XmlTags.ATTRIBUTE_REAL_CLASS_NAME);
				return;
			}

			if (tagName.equals(XmlTags.TAG_ELEMENT) && state == STATE_ATTRIBUTE_COLLECTION) {
				String objectRefId = attributes.getValue(XmlTags.ATTRIBUTE_OBJECT_REF_ID);
				if (objectRefId != null) {
					attributeCollection.add(new ObjectReference(OIDFactory.buildObjectOID(Long.parseLong(objectRefId))));
				} else {
					String value = attributes.getValue(XmlTags.ATTRIBUTE_VALUE);
					ODBType type = ODBType.getFromName(attributes.getValue(XmlTags.ATTRIBUTE_TYPE));
					attributeCollection.add(ObjectTool.stringToObjectInfo(type.getId(), value, ObjectTool.ID_CALLER_IS_XML, null));
				}
				return;
			}
			if (tagName.equals(XmlTags.TAG_ELEMENT) && state == STATE_ATTRIBUTE_ARRAY) {
				String objectRefId = attributes.getValue(XmlTags.ATTRIBUTE_OBJECT_REF_ID);
				if (objectRefId != null) {
					attributeArray[arrayIndex++] = new ObjectReference(OIDFactory.buildObjectOID(Long.parseLong(objectRefId)));
				} else {
					String value = attributes.getValue(XmlTags.ATTRIBUTE_VALUE);
					ODBType type = ODBType.getFromName(realClassName);
					attributeArray[arrayIndex++] = ObjectTool.stringToObjectInfo(type.getId(), value, ObjectTool.ID_CALLER_IS_XML, null);
				}
				return;
			}
			if (tagName.equals(XmlTags.TAG_ELEMENT) && state == STATE_ATTRIBUTE_MAP) {
				String keyRefId = attributes.getValue(XmlTags.ATTRIBUTE_KEY_ID);
				String objectRefId = attributes.getValue(XmlTags.ATTRIBUTE_OBJECT_REF_ID);
				Object key = null;
				Object value = null;

				if (keyRefId != null) {
					key = new ObjectReference(OIDFactory.buildObjectOID(Long.parseLong(keyRefId)));
				} else {
					String v = attributes.getValue(XmlTags.ATTRIBUTE_KEY_VALUE);
					String keyType = attributes.getValue(XmlTags.ATTRIBUTE_KEY_TYPE);
					ODBType type = ODBType.getFromName(keyType);
					key = ObjectTool.stringToObjectInfo(type.getId(), v, ObjectTool.ID_CALLER_IS_XML, null);
					
					if(type.isEnum()){
						String ciId = attributes.getValue(XmlTags.ATTRIBUTE_ENUM_CLASS_OID);
						EnumNativeObjectInfo anoi = (EnumNativeObjectInfo) key;
						anoi.setEnumClassInfo(metaModel.getClassInfoFromId(OIDFactory.buildClassOID(Long.parseLong(ciId))));
					}
				}

				if (objectRefId != null) {
					value = new ObjectReference(OIDFactory.buildObjectOID(Long.parseLong(objectRefId)));
				} else {
					String v = attributes.getValue(XmlTags.ATTRIBUTE_VALUE);
					ODBType type = ODBType.getFromName(attributes.getValue(XmlTags.ATTRIBUTE_VALUE_TYPE));
					value = ObjectTool.stringToObjectInfo(type.getId(), v, ObjectTool.ID_CALLER_IS_XML, null);
				}
				attributeMap.put(key, value);
				return;
			}
		} catch (Exception e) {
			throw new ODBRuntimeException(NeoDatisError.IMPORT_ERROR
					.addParameter(storageEngine.getBaseIdentification().getIdentification()), e);
		} finally {
		}
	}

	private void reserveIds(OID maxOid, boolean onlyFirstBlock) throws IOException {

		long nbOids = maxOid.getObjectId();
		info("Allocating " + nbOids + " OIDs (Object IDs)");
		if (onlyFirstBlock) {
			if (nbOids > OdbConfiguration.getNB_IDS_PER_BLOCK()) {
				nbOids = OdbConfiguration.getNB_IDS_PER_BLOCK();
			}
		} else {
			if (nbOids <= OdbConfiguration.getNB_IDS_PER_BLOCK()) {
				// No more than one id block, nothing to do
				return;
			}
			// nbOids = nbOids - Configuration.getNB_IDS_PER_BLOCK();

		}
		storageEngine.getObjectWriter().getIdManager().reserveIds(nbOids);

	}

	public void endElement(String uri, String localName, String tagName) throws SAXException {
		try {
			if (tagName.equals(XmlTags.TAG_CLASS)) {
				ci.setAttributes(classAttributeInfo);
				metaModel.addClass(ci);
				state = STATE_UNKNOWN;
				if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
					DLogger.debug("Class " + ci.getFullClassName() + " created");
				}
				// info(". Class "+ci.getFullClassName()+" imported.");
				return;
			}
			if (tagName.equals(XmlTags.TAG_METAMODEL)) {
				state = STATE_UNKNOWN;
				// try {
				info("Persisting Meta Model");
				storageEngine.setMetaModel(metaModel);
				// Now that meta model has been stored, lets write the end of
				// the
				// ids
				// reserveIds(maxObjectId, false);
				// } catch (IOException e) {
				// throw new ODBRuntimeException(Error.XML_SETTING_META_MODEL,
				// e);
				// }
				info(". Meta Model persisted. " + metaModel.getNumberOfClasses() + " classes");
				return;
			}
			if (tagName.equals(XmlTags.TAG_OBJECT) && state == STATE_OBJECT) {
				try {
					// The last parameter is used to indicate that it is a new
					// object
					// The false : object are not written in transaction
					storageEngine.getObjectWriter().writeNonNativeObjectInfo(nnoi.getOid(), nnoi, -1, false, true);
					if (OdbConfiguration.isDebugEnabled(LOG_ID)) {
						info("Object of type " + nnoi.getClassInfo().getFullClassName() + " with oid " + nnoi.getOid() + " created");
					}
				} catch (Exception e) {
					throw new ODBRuntimeException(NeoDatisError.INTERNAL_ERROR.addParameter("in XmlImporter.endElement.writeObjectInfo"), e);
				}
				state = STATE_OBJECTS;
				return;
			}
			if (tagName.equals(XmlTags.TAG_ATTRIBUTE) && state == STATE_ATTRIBUTE) {
				state = STATE_OBJECT;
				return;
			}
			if (tagName.equals(XmlTags.TAG_COLLECTION) && state == STATE_ATTRIBUTE_COLLECTION) {
				CollectionObjectInfo coi = new CollectionObjectInfo(attributeCollection);
				coi.setRealCollectionClassName(realClassName);
				nnoi.setAttributeValue(attributeId, coi);
				state = STATE_ATTRIBUTE;
				attributeCollection = null;
				attributeId = -1;
				return;
			}
			if (tagName.equals(XmlTags.TAG_ARRAY) && state == STATE_ATTRIBUTE_ARRAY) {
				ArrayObjectInfo aoi = new ArrayObjectInfo(attributeArray);
				aoi.setRealArrayComponentClassName(realClassName);
				// copy the odb type 
				aoi.setOdbType(aoi.getOdbType().copy());
				aoi.getOdbType().setSubType(ODBType.getFromName(realClassName));
				nnoi.setAttributeValue(attributeId, aoi);
				state = STATE_ATTRIBUTE;
				attributeArray = null;
				attributeId = -1;
				return;
			}
			if (tagName.equals(XmlTags.TAG_MAP) && state == STATE_ATTRIBUTE_MAP) {
				MapObjectInfo moi = new MapObjectInfo(attributeMap, realClassName);
				nnoi.setAttributeValue(attributeId, moi);
				state = STATE_ATTRIBUTE;
				attributeMap = null;
				attributeId = -1;
				return;
			}
		} finally {
			currentLevel--;
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {

	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
	}

	public void processingInstruction(String target, String data) throws SAXException {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void setExternalLogger(ILogger logger) {
		this.externalLogger = logger;
	}
	public void logToConsole() {
		this.externalLogger = new ConsoleLogger();
	}


	protected void info(Object o) {
		if (externalLogger != null) {
			externalLogger.info(o);
		}
	}

}
