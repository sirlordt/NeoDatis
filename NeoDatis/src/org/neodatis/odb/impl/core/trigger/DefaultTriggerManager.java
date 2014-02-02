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
package org.neodatis.odb.impl.core.trigger;

import java.util.Iterator;
import java.util.Map;

import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.IError;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.trigger.DeleteTrigger;
import org.neodatis.odb.core.trigger.ITriggerManager;
import org.neodatis.odb.core.trigger.InsertTrigger;
import org.neodatis.odb.core.trigger.OIDTrigger;
import org.neodatis.odb.core.trigger.SelectTrigger;
import org.neodatis.odb.core.trigger.Trigger;
import org.neodatis.odb.core.trigger.UpdateTrigger;
import org.neodatis.odb.impl.core.server.trigger.DefaultObjectRepresentation;
import org.neodatis.odb.impl.main.ODBForTrigger;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.wrappers.OdbString;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;
import org.neodatis.tool.wrappers.map.OdbHashMap;

public class DefaultTriggerManager implements ITriggerManager {

	private static final String ALL_CLASS_TRIGGER = "__all_class_";

	private IStorageEngine storageEngine;

	/** key is class Name, value is the collection of triggers for the class */
	protected Map<String,IOdbList<Trigger>> listOfUpdateTriggers;
	/** key is class Name, value is the collection of triggers for the class */
	protected Map<String,IOdbList<Trigger>> listOfInsertTriggers;
	/** key is class Name, value is the collection of triggers for the class */
	protected Map<String,IOdbList<Trigger>> listOfDeleteTriggers;
	/** key is class Name, value is the collection of triggers for the class */
	protected Map<String,IOdbList<Trigger>> listOfSelectTriggers;
	/** key is class Name, value is the collection of triggers for the class */
	protected Map<String,IOdbList<Trigger>> listOfOIdTriggers;

	public DefaultTriggerManager(IStorageEngine engine) {
		this.storageEngine = engine;
		init();
	}

	/**
	 * 
	 */
	private void init() {
		listOfUpdateTriggers = new OdbHashMap<String, IOdbList<Trigger>>();
		listOfDeleteTriggers = new OdbHashMap<String, IOdbList<Trigger>>();
		listOfSelectTriggers = new OdbHashMap<String, IOdbList<Trigger>>();
		listOfInsertTriggers = new OdbHashMap<String, IOdbList<Trigger>>();
		listOfOIdTriggers = new OdbHashMap<String, IOdbList<Trigger>>();
	}

	public void addUpdateTriggerFor(String className, UpdateTrigger trigger) {
		if (className == null) {
			className = ALL_CLASS_TRIGGER;
		}
		IOdbList<Trigger> c =  listOfUpdateTriggers.get(className);
		if (c == null) {
			c = new OdbArrayList<Trigger>();
			listOfUpdateTriggers.put(className, c);
		}
		c.add(trigger);
	}

	public void addInsertTriggerFor(String className, InsertTrigger trigger) {
		if (className == null) {
			className = ALL_CLASS_TRIGGER;
		}
		IOdbList<Trigger> c = listOfInsertTriggers.get(className);
		if (c == null) {
			c = new OdbArrayList<Trigger>();
			listOfInsertTriggers.put(className, c);
		}
		c.add(trigger);
	}
	public void addOidTriggerFor(String className, OIDTrigger trigger) {
		if (className == null) {
			className = ALL_CLASS_TRIGGER;
		}
		IOdbList<Trigger> c = listOfOIdTriggers.get(className);
		if (c == null) {
			c = new OdbArrayList<Trigger>();
			listOfOIdTriggers.put(className, c);
		}
		c.add(trigger);
	}

	public void addDeleteTriggerFor(String className, DeleteTrigger trigger) {
		if (className == null) {
			className = ALL_CLASS_TRIGGER;
		}
		IOdbList<Trigger> c = listOfDeleteTriggers.get(className);
		if (c == null) {
			c = new OdbArrayList<Trigger>();
			listOfDeleteTriggers.put(className, c);
		}
		c.add(trigger);
	}

	public void addSelectTriggerFor(String className, SelectTrigger trigger) {
		if (className == null) {
			className = ALL_CLASS_TRIGGER;
		}
		IOdbList<Trigger> c = listOfSelectTriggers.get(className);
		if (c == null) {
			c = new OdbArrayList<Trigger>();
			listOfSelectTriggers.put(className, c);
		}
		c.add(trigger);
	}

	public boolean hasDeleteTriggersFor(String classsName) {
		return listOfDeleteTriggers.containsKey(classsName) || listOfDeleteTriggers.containsKey(ALL_CLASS_TRIGGER);
	}

	public boolean hasOidTriggersFor(String classsName) {
		return listOfOIdTriggers.containsKey(classsName) || listOfOIdTriggers.containsKey(ALL_CLASS_TRIGGER);
	}

	public boolean hasInsertTriggersFor(String className) {
		return listOfInsertTriggers.containsKey(className) || listOfInsertTriggers.containsKey(ALL_CLASS_TRIGGER);
	}

	public boolean hasSelectTriggersFor(String className) {
		return listOfSelectTriggers.containsKey(className) || listOfSelectTriggers.containsKey(ALL_CLASS_TRIGGER);
	}

	public boolean hasUpdateTriggersFor(String className) {
		return listOfUpdateTriggers.containsKey(className) || listOfUpdateTriggers.containsKey(ALL_CLASS_TRIGGER);
	}

	/**
	 * FIXME try to cache l1+l2
	 * 
	 * @param className
	 * @return
	 */
	public IOdbList<Trigger> getListOfDeleteTriggersFor(String className) {
		IOdbList<Trigger> l1 = listOfDeleteTriggers.get(className);
		IOdbList<Trigger> l2 = listOfDeleteTriggers.get(ALL_CLASS_TRIGGER);

		if (l2 != null) {
			int size = l2.size();
			if (l1 != null) {
				size = size + l1.size();
			}
			IOdbList<Trigger> r = new OdbArrayList<Trigger>(size);
			if (l1 != null) {
				r.addAll(l1);
			}
			r.addAll(l2);
			return r;
		}

		return l1;
	}

	public IOdbList<Trigger> getListOfInsertTriggersFor(String className) {
		IOdbList<Trigger> l1 = listOfInsertTriggers.get(className);
		IOdbList<Trigger> l2 = listOfInsertTriggers.get(ALL_CLASS_TRIGGER);

		if (l2 != null) {
			int size = l2.size();
			if (l1 != null) {
				size = size + l1.size();
			}
			IOdbList<Trigger> r = new OdbArrayList<Trigger>(size);
			if (l1 != null) {
				r.addAll(l1);
			}
			r.addAll(l2);
			return r;
		}


		return l1;
	}

	public IOdbList<Trigger> getListOfOidTriggersFor(String className) {
		IOdbList<Trigger> l1 = listOfOIdTriggers.get(className);
		IOdbList<Trigger> l2 = listOfOIdTriggers.get(ALL_CLASS_TRIGGER);

		if (l2 != null) {
			int size = l2.size();
			if (l1 != null) {
				size = size + l1.size();
			}
			IOdbList<Trigger> r = new OdbArrayList<Trigger>(size);
			if (l1 != null) {
				r.addAll(l1);
			}
			r.addAll(l2);
			return r;
		}


		return l1;
	}

	public IOdbList<Trigger> getListOfSelectTriggersFor(String className) {
		IOdbList<Trigger> l1 = listOfSelectTriggers.get(className);
		IOdbList<Trigger> l2 = listOfSelectTriggers.get(ALL_CLASS_TRIGGER);
		if (l2 != null) {
			int size = l2.size();
			if (l1 != null) {
				size = size + l1.size();
			}
			IOdbList<Trigger> r = new OdbArrayList<Trigger>(size);
			if (l1 != null) {
				r.addAll(l1);
			}
			r.addAll(l2);
			return r;
		}


		return l1;
	}

	public IOdbList<Trigger> getListOfUpdateTriggersFor(String className) {
		IOdbList<Trigger> l1 = listOfUpdateTriggers.get(className);
		IOdbList<Trigger> l2 = listOfUpdateTriggers.get(ALL_CLASS_TRIGGER);

		if (l2 != null) {
			int size = l2.size();
			if (l1 != null) {
				size = size + l1.size();
			}
			IOdbList<Trigger> r = new OdbArrayList<Trigger>(size);
			if (l1 != null) {
				r.addAll(l1);
			}
			r.addAll(l2);
			return r;
		}


		return l1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.ITriggerManager#manageInsertTriggerBefore
	 * (java.lang.Object)
	 */
	public boolean manageInsertTriggerBefore(String className, Object object) {
		if (hasInsertTriggersFor(className)) {
			InsertTrigger trigger = null;
			Iterator iterator = getListOfInsertTriggersFor(className).iterator();
			while (iterator.hasNext()) {
				trigger = (InsertTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				try {
					if(!isNull(object)){
						trigger.beforeInsert(transform(object));
					}
				} catch (Exception e) {
					IError warning = NeoDatisError.BEFORE_INSERT_TRIGGER_HAS_THROWN_EXCEPTION.addParameter(trigger.getClass().getName())
							.addParameter(OdbString.exceptionToString(e, false));
					if (OdbConfiguration.displayWarnings()) {
						DLogger.info(warning);
					}
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.ITriggerManager#manageInsertTriggerAfter
	 * (java.lang.Object, org.neodatis.odb.core.OID, long)
	 */
	public void manageInsertTriggerAfter(String className, Object object, OID oid) {
		if (hasInsertTriggersFor(className)) {
			InsertTrigger trigger = null;
			Iterator iterator = getListOfInsertTriggersFor(className).iterator();

			while (iterator.hasNext()) {
				trigger = (InsertTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				try {
					trigger.afterInsert(transform(object), oid);
				} catch (Exception e) {
					IError warning = NeoDatisError.AFTER_INSERT_TRIGGER_HAS_THROWN_EXCEPTION.addParameter(trigger.getClass().getName())
							.addParameter(OdbString.exceptionToString(e, false));
					if (OdbConfiguration.displayWarnings()) {
						DLogger.info(warning);
					}
				}
			}
		}
	}


	public boolean manageOidTrigger(NonNativeObjectInfo nnoi, OID oid) {
		String className = nnoi.getClassInfo().getFullClassName();
		if (hasOidTriggersFor(className)) {
			OIDTrigger trigger = null;
			Iterator iterator = getListOfOidTriggersFor(className).iterator();
			while (iterator.hasNext()) {
				trigger = (OIDTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				try {
					if(!isNull(nnoi)){
						DefaultObjectRepresentation or = new DefaultObjectRepresentation(nnoi);
						or.addObserver(storageEngine.getSession(true));
						trigger.setOid(or,oid);
					}
				} catch (Exception e) {
					IError warning = NeoDatisError.BEFORE_INSERT_TRIGGER_HAS_THROWN_EXCEPTION.addParameter(trigger.getClass().getName())
							.addParameter(OdbString.exceptionToString(e, false));
					if (OdbConfiguration.displayWarnings()) {
						DLogger.info(warning);
					}
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.ITriggerManager#manageUpdateTriggerBefore
	 * (java.lang.Object, java.lang.Object, org.neodatis.odb.core.OID)
	 */
	public boolean manageUpdateTriggerBefore(String className, NonNativeObjectInfo oldNnoi, Object newObject, OID oid) {
		if (hasUpdateTriggersFor(className)) {
			UpdateTrigger trigger = null;
			Iterator iterator = getListOfUpdateTriggersFor(className).iterator();
			while (iterator.hasNext()) {
				trigger = (UpdateTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				try {
					trigger.beforeUpdate(new DefaultObjectRepresentation(oldNnoi), transform(newObject), oid);
				} catch (Exception e) {
					IError warning = NeoDatisError.BEFORE_UPDATE_TRIGGER_HAS_THROWN_EXCEPTION.addParameter(trigger.getClass().getName())
							.addParameter(OdbString.exceptionToString(e, false));
					if (OdbConfiguration.displayWarnings()) {
						DLogger.info(warning);
					}
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.ITriggerManager#manageUpdateTriggerAfter
	 * (java.lang.Object, java.lang.Object, org.neodatis.odb.core.OID)
	 */
	public void manageUpdateTriggerAfter(String className, NonNativeObjectInfo oldNnoi, Object newObject, OID oid) {
		if (hasUpdateTriggersFor(className)) {
			UpdateTrigger trigger = null;
			Iterator iterator = getListOfUpdateTriggersFor(className).iterator();
			while (iterator.hasNext()) {
				trigger = (UpdateTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				try {
					trigger.afterUpdate(new DefaultObjectRepresentation(oldNnoi), transform(newObject), oid);
				} catch (Exception e) {
					IError warning = NeoDatisError.AFTER_UPDATE_TRIGGER_HAS_THROWN_EXCEPTION.addParameter(trigger.getClass().getName())
							.addParameter(OdbString.exceptionToString(e, false));
					if (OdbConfiguration.displayWarnings()) {
						DLogger.info(warning);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.ITriggerManager#manageDeleteTriggerBefore
	 * (java.lang.Object, org.neodatis.odb.core.OID)
	 */
	public boolean manageDeleteTriggerBefore(String className, Object object, OID oid) {
		if (hasDeleteTriggersFor(className)) {
			DeleteTrigger trigger = null;
			Iterator iterator = getListOfDeleteTriggersFor(className).iterator();
			while (iterator.hasNext()) {
				trigger = (DeleteTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				try {
					trigger.beforeDelete(transform(object), oid);
				} catch (Exception e) {
					IError warning = NeoDatisError.BEFORE_DELETE_TRIGGER_HAS_THROWN_EXCEPTION.addParameter(trigger.getClass().getName())
							.addParameter(OdbString.exceptionToString(e, true));
					if (OdbConfiguration.displayWarnings()) {
						DLogger.info(warning);
					}
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.ITriggerManager#manageDeleteTriggerAfter
	 * (java.lang.Object, org.neodatis.odb.core.OID)
	 */
	public void manageDeleteTriggerAfter(String className, Object object, OID oid) {
		if (hasDeleteTriggersFor(className)) {
			DeleteTrigger trigger = null;
			Iterator iterator = getListOfDeleteTriggersFor(className).iterator();
			while (iterator.hasNext()) {
				trigger = (DeleteTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				try {
					trigger.afterDelete(transform(object), oid);
				} catch (Exception e) {
					IError warning = NeoDatisError.AFTER_DELETE_TRIGGER_HAS_THROWN_EXCEPTION.addParameter(trigger.getClass().getName())
							.addParameter(OdbString.exceptionToString(e, false));
					if (OdbConfiguration.displayWarnings()) {
						DLogger.info(warning);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.neodatis.odb.core.trigger.ITriggerManager#manageSelectTriggerAfter
	 * (java.lang.Object, org.neodatis.odb.core.OID)
	 */
	public void manageSelectTriggerAfter(String className, Object object, OID oid) {
		if (hasSelectTriggersFor(className)) {
			SelectTrigger trigger = null;
			Iterator iterator = getListOfSelectTriggersFor(className).iterator();
			while (iterator.hasNext()) {
				trigger = (SelectTrigger) iterator.next();
				if (trigger.getOdb() == null) {
					trigger.setOdb(new ODBForTrigger(storageEngine));
				}
				if(!isNull(object)){
					trigger.afterSelect(transform(object), oid);
				}
			}
		}
	}
	protected boolean isNull(Object object){
		return object == null;
	}

	/**
	 * For the default object trigger, no transformation is needed
	 */
	public Object transform(Object object) {
		return object;
	}

	public IStorageEngine getStorageEngine() {
		return storageEngine;
	}

}
