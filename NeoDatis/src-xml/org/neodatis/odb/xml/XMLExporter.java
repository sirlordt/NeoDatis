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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBRuntimeException;
import org.neodatis.odb.Objects;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.AbstractObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ArrayObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.AtomicNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassAttributeInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.CollectionObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.EnumNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MapObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.MetaModel;
import org.neodatis.odb.core.layers.layer2.meta.NativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeNullObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer2.meta.ODBType;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.neodatis.odb.xml.tool.XMLGenerator;
import org.neodatis.odb.xml.tool.XMLNode;
import org.neodatis.tool.ConsoleLogger;
import org.neodatis.tool.ILogger;

/**
 * The class that export a whole database to a xml file
 * 
 *TODO encode the string to elimite special characters like <,>, ', ", $
 * 
 * < => &lt; > => &gt; & => &amp; " => &quot; ' => &apos;
 * 
 * @author olivier
 * 
 */
public class XMLExporter {
	private IStorageEngine storageEngine;
	private ILogger externalLogger;

	public XMLExporter(IStorageEngine storageEngine) {
		this.storageEngine = storageEngine;
	}

	public XMLExporter(ODB odb) {
		this.storageEngine = Dummy.getEngine(odb);
	}

	public void export(String directory, String filename) throws Exception {

		if (!storageEngine.isLocal()) {
			throw new ODBRuntimeException(NeoDatisError.NOT_YET_IMPLEMENTED.addParameter("Export in Client Server mode"));
		}
		String baseName = storageEngine.getBaseIdentification().getIdentification();
		String completeFileName = directory + "/" + filename;
		XMLGenerator.setIncrementalWriteOn(completeFileName);

		info("Exporting database ODB database " + baseName + " to " + completeFileName);

		XMLNode root = XMLGenerator.createRoot(XmlTags.TAG_ODB);

		root.addAttribute(XmlTags.ATTRIBUTE_NAME, format(baseName));
		root.addAttribute(XmlTags.ATTRIBUTE_EXPORT_DATE, format(new Date()));
		root.addAttribute(XmlTags.ATTRIBUTE_MAX_OID, String.valueOf(storageEngine.getMaxOid().getObjectId()));
		root.addAttribute(XmlTags.ATTRIBUTE_FILE_FORMAT_VERSION, String.valueOf(StorageEngineConstant.CURRENT_FILE_FORMAT_VERSION));
		root.endHeader();

		buildMetaModelXml(root);
		buildObjectsXml(root);

		root.end();
		XMLGenerator.close();
		info("End of Export");

	}

	private void buildObjectsXml(XMLNode root) throws Exception {
		XMLNode objectsXml = root.createNode(XmlTags.TAG_OBJECTS);
		objectsXml.endHeader();
		MetaModel metaModel = storageEngine.getSession(true).getMetaModel();

		info("Exporting Objects of " + metaModel.getNumberOfUserClasses() + " classes");

		Iterator iterator = metaModel.getUserClasses().iterator();
		while (iterator.hasNext()) {
			ClassInfo ci = (ClassInfo) iterator.next();
			buildObjectsOfClassXml(objectsXml, ci);
		}
		objectsXml.end();
		info("Exporting Objects done.");
	}

	private void buildObjectsOfClassXml(XMLNode objectsXml, ClassInfo ci) throws Exception {
		try{
			info(". Exporting Objects of " + ci.getFullClassName() + " (" + ci.getNumberOfObjects() + ")");
			Objects objects = storageEngine.getObjectInfos(new CriteriaQuery(ci.getFullClassName()), false, -1, -1, false);

			int i = 0;
			while (objects.hasNext()) {
				Object o = objects.next();
				AbstractObjectInfo aoi = (AbstractObjectInfo) o;
				buildOneObjectXml(objectsXml, ci, (NonNativeObjectInfo) aoi);
				i++;
				if (i % 10000 == 0) {
					info(". " + i + " objects");
				}
			}
			info(". Done." + i + " objects");
			objects.clear();
			objects = null;
			storageEngine.getSession(true).clearCache();
		}catch (Exception e) {
			System.err.println("Error while exporting objects of class "+ ci.getFullClassName());
			throw e;
		}
	}

	private void buildOneObjectXml(XMLNode node, ClassInfo ci, NonNativeObjectInfo nnoi) throws UnsupportedEncodingException {

		XMLNode objectXml = node.createNode(XmlTags.TAG_OBJECT);
		objectXml.addAttribute(XmlTags.ATTRIBUTE_OID, String.valueOf(nnoi.getOid()));
		objectXml.addAttribute(XmlTags.ATTRIBUTE_CLASS_ID, String.valueOf(ci.getId()));
		objectXml.endHeader();
		int attributeId = -1;
		AbstractObjectInfo aoi = null;
		int maxId = ci.getMaxAttributeId();
		// WARNING IDs start with 1 and not 0
		for (int id = 1; id <= maxId; id++) {
			XMLNode attributeXml = objectXml.createNode(XmlTags.TAG_ATTRIBUTE);
			ClassAttributeInfo cai = ci.getAttributeInfoFromId(id);
			if (cai == null) {
				continue;
			}
			attributeId = cai.getId();
			aoi = nnoi.getAttributeValueFromId(attributeId);
			attributeXml.addAttribute(XmlTags.ATTRIBUTE_ID, String.valueOf(id));
			attributeXml.addAttribute(XmlTags.ATTRIBUTE_NAME, cai.getName());
			if (aoi.isNative()) {
				if (aoi.isNull()) {
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_NULL, "true");
				} else if (cai.getAttributeType().isCollection()) {
					CollectionObjectInfo coi = (CollectionObjectInfo) aoi;
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_TYPE, XmlTags.ATTRIBUTE_COLLECTION);
					attributeXml.endHeader();
					buildListXml(attributeXml, coi);
				} else if (cai.getAttributeType().isMap()) {
					MapObjectInfo moi = (MapObjectInfo) aoi;
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_TYPE, XmlTags.ATTRIBUTE_MAP);
					attributeXml.endHeader();
					buildMapXml(attributeXml, moi);
				} else if (cai.getAttributeType().isArray()) {
					ArrayObjectInfo aroi = (ArrayObjectInfo) aoi;
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_TYPE, XmlTags.ATTRIBUTE_ARRAY);
					attributeXml.endHeader();
					buildArrayXml(attributeXml, aroi);
				} else if (nnoi.getAttributeValueFromId(id) != null) {
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_VALUE, format(aoi));
				} else {
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_NULL, "true");
				}
			} else {
				if (nnoi.getAttributeValueFromId(id) instanceof NonNativeNullObjectInfo) {
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_NULL, "true");
				} else if (nnoi.getAttributeValueFromId(id).isDeletedObject()) {
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_DELETED, "true");
				} else {
					attributeXml.addAttribute(XmlTags.ATTRIBUTE_OBJECT_REF_ID, String.valueOf(((NonNativeObjectInfo) aoi).getOid()));
				}

			}
			attributeXml.end();
		}
		objectXml.end();
	}

	private String format(Object object) throws UnsupportedEncodingException {
		if (object instanceof Date) {
			Date date = (Date) object;
			return String.valueOf(date.getTime());
		}
		if (object instanceof AtomicNativeObjectInfo) {
			AtomicNativeObjectInfo an = (AtomicNativeObjectInfo) object;
			if (an.getObject() instanceof Date) {
				Date date = (Date) an.getObject();
				return String.valueOf(date.getTime());
				// return //Formater.dateToString((Date) object);
			}
			if (an.getObject() instanceof String) {
				String s = (String) an.getObject();
				String encoding = OdbConfiguration.getDatabaseCharacterEncoding();
				if (encoding == null || encoding.equals(StorageEngineConstant.NO_ENCODING)) {
					return URLEncoder.encode(s);
				}
				return URLEncoder.encode(s, encoding);
			}
		}
		return URLEncoder.encode(object.toString());
	}

	private void buildListXml(XMLNode node, CollectionObjectInfo coi) {

		if (coi.isNull()) {
			node.createNode(XmlTags.TAG_NULL_COLLECTION).end();

		}
		XMLNode element = null;
		XMLNode listXml = node.createNode(XmlTags.TAG_COLLECTION);
		listXml.addAttribute(XmlTags.ATTRIBUTE_REAL_CLASS_NAME, coi.getRealCollectionClassName());
		listXml.addAttribute(XmlTags.ATTRIBUTE_SIZE, "" + coi.getCollection().size());
		listXml.endHeader();
		Iterator iterator = coi.getCollection().iterator();
		while (iterator.hasNext()) {
			AbstractObjectInfo aoi = (AbstractObjectInfo) iterator.next();
			element = listXml.createNode(XmlTags.TAG_ELEMENT);
			if (aoi.isNative()) {
				NativeObjectInfo noi = (NativeObjectInfo) aoi;
				element.addAttribute(XmlTags.ATTRIBUTE_VALUE, String.valueOf(noi.getObject()));
				element.addAttribute(XmlTags.ATTRIBUTE_TYPE, ODBType.getNameFromId(aoi.getOdbTypeId()));
			} else {
				NonNativeObjectInfo nnoi = (NonNativeObjectInfo) aoi;
				element.addAttribute(XmlTags.ATTRIBUTE_OBJECT_REF_ID, String.valueOf(nnoi.getOid()));
			}
			element.end();
		}
		listXml.end();
	}

	private void buildArrayXml(XMLNode node, ArrayObjectInfo aoi) throws UnsupportedEncodingException {

		if (aoi.isNull()) {
			node.createNode(XmlTags.TAG_NULL_ARRAY).end();

		}
		XMLNode element = null;
		XMLNode listXml = node.createNode(XmlTags.TAG_ARRAY);
		listXml.addAttribute(XmlTags.ATTRIBUTE_ARRAY_OF, aoi.getRealArrayComponentClassName());
		listXml.addAttribute(XmlTags.ATTRIBUTE_SIZE, "" + aoi.getArrayLength());
		listXml.endHeader();

		for (int i = 0; i < aoi.getArrayLength(); i++) {
			AbstractObjectInfo aboi = (AbstractObjectInfo) aoi.getArray()[i];
			element = listXml.createNode(XmlTags.TAG_ELEMENT);
			if (aboi.isNative()) {
				if (aboi.isNull()) {
					element.addAttribute(XmlTags.ATTRIBUTE_NULL, "true");
				} else {
					NativeObjectInfo noi = (NativeObjectInfo) aboi;
					element.addAttribute(XmlTags.ATTRIBUTE_VALUE, format(noi.getObject().toString()));
				}
			} else {
				if (aboi.isNull()) {
					element.addAttribute(XmlTags.ATTRIBUTE_NULL, "true");
				} else {
					NonNativeObjectInfo nnoi = (NonNativeObjectInfo) aboi;
					element.addAttribute(XmlTags.ATTRIBUTE_OBJECT_REF_ID, String.valueOf(nnoi.getOid()));
				}
			}
			element.end();
		}
		listXml.end();
	}

	private void buildMapXml(XMLNode node, MapObjectInfo moi) throws UnsupportedEncodingException {

		if (moi.isNull()) {
			node.createNode(XmlTags.TAG_NULL_MAP).end();

		}
		XMLNode element = null;
		XMLNode listXml = node.createNode(XmlTags.TAG_MAP);
		listXml.addAttribute(XmlTags.ATTRIBUTE_REAL_CLASS_NAME, moi.getRealMapClassName());
		listXml.addAttribute(XmlTags.ATTRIBUTE_SIZE, "" + moi.getMap().size());
		listXml.endHeader();
		Iterator iterator = moi.getMap().keySet().iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			AbstractObjectInfo aoi = (AbstractObjectInfo) o;
			element = listXml.createNode(XmlTags.TAG_ELEMENT);
			if (aoi.isNative()) {
				NativeObjectInfo noi = (NativeObjectInfo) aoi;
				element.addAttribute(XmlTags.ATTRIBUTE_KEY_VALUE, format(String.valueOf(noi.getObject())));
				element.addAttribute(XmlTags.ATTRIBUTE_KEY_TYPE, ODBType.getNameFromId(aoi.getOdbTypeId()));
				if(noi.isEnumObject()){
					EnumNativeObjectInfo enoi = (EnumNativeObjectInfo) noi;
					element.addAttribute(XmlTags.ATTRIBUTE_ENUM_CLASS_OID, String.valueOf(enoi.getEnumClassInfo().getId().getObjectId()));
				}
			} else {
				NonNativeObjectInfo nnoi = (NonNativeObjectInfo) aoi;
				element.addAttribute(XmlTags.ATTRIBUTE_KEY_ID, String.valueOf(nnoi.getOid()));
			}
			aoi = (AbstractObjectInfo) moi.getMap().get(aoi);
			if (aoi.isNative()) {
				NativeObjectInfo noi = (NativeObjectInfo) aoi;
				element.addAttribute(XmlTags.ATTRIBUTE_VALUE, format( String.valueOf(noi.getObject())));
				element.addAttribute(XmlTags.ATTRIBUTE_VALUE_TYPE, ODBType.getNameFromId(aoi.getOdbTypeId()));
			} else {
				NonNativeObjectInfo nnoi = (NonNativeObjectInfo) aoi;
				element.addAttribute(XmlTags.ATTRIBUTE_OBJECT_REF_ID, String.valueOf(nnoi.getOid()));
			}
			element.end();
		}
		listXml.end();
	}

	private void buildMetaModelXml(XMLNode root) throws Exception {
		XMLNode metaModelXml = root.createNode(XmlTags.TAG_METAMODEL);
		metaModelXml.endHeader();

		MetaModel metaModel = storageEngine.getSession(true).getMetaModel();

		info("Exporting MetaModel : " + metaModel.getNumberOfClasses() + " classes");
		Iterator iterator = metaModel.getAllClasses().iterator();
		while (iterator.hasNext()) {
			ClassInfo ci = (ClassInfo) iterator.next();
			buildClassInfoXml(metaModelXml, ci);
		}
		info("Exporting MetaModel done");
	}
	

	private void buildClassInfoXml(XMLNode node, ClassInfo ci) {
		XMLNode classXml = node.createNode(XmlTags.TAG_CLASS);
		classXml.addAttribute(XmlTags.ATTRIBUTE_ID, String.valueOf(ci.getId()));
		classXml.addAttribute(XmlTags.ATTRIBUTE_NAME, ci.getFullClassName());
		// FIXME manage extra info

		classXml.endHeader();

		info(". Class " + ci.getFullClassName());

		for (int i = 0; i < ci.getAttributes().size(); i++) {
			ClassAttributeInfo cai = ci.getAttributeInfo(i);
			buildClassAttributeXml(classXml, cai, i + 1);
		}
		classXml.end();
	}

	private void buildClassAttributeXml(XMLNode node, ClassAttributeInfo cai, int index) {
		XMLNode classAttributeXml = node.createNode(XmlTags.TAG_ATTRIBUTE);
		classAttributeXml.addAttribute(XmlTags.ATTRIBUTE_ID, String.valueOf(cai.getId()));
		classAttributeXml.addAttribute(XmlTags.ATTRIBUTE_NAME, cai.getName());
		classAttributeXml.addAttribute(XmlTags.ATTRIBUTE_TYPE, cai.getAttributeType().getName());
		if (cai.getAttributeType().isArray()) {
			classAttributeXml.addAttribute(XmlTags.ATTRIBUTE_ARRAY_OF, cai.getAttributeType().getSubType().getName());
		}
		if (cai.getAttributeType().isEnum()) {
			classAttributeXml.addAttribute(XmlTags.ATTRIBUTE_IS_ENUM, "true");
		}

		classAttributeXml.end();
	}

	public void logToConsole() {
		this.externalLogger = new ConsoleLogger();
	}

	public void setExternalLogger(ILogger logger) {
		this.externalLogger = logger;
	}

	protected void info(Object o) {
		if (externalLogger != null) {
			externalLogger.info(o);
		}
	}
}
