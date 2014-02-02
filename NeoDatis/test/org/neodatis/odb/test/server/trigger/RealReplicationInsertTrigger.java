package org.neodatis.odb.test.server.trigger;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.ObjectRepresentation;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.NonNativeObjectInfo;
import org.neodatis.odb.core.layers.layer3.IStorageEngine;
import org.neodatis.odb.core.server.trigger.ServerInsertTrigger;
import org.neodatis.odb.impl.core.layers.layer3.engine.Dummy;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.core.oid.ExternalObjectOID;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * A trigger to build a replication mechanism
 * 
 * @author olivier
 * 
 */
public class RealReplicationInsertTrigger extends ServerInsertTrigger {
	private int nbInsertsAfter;
	private int nbInsertsBefore;
	private ODB odbToReplicateTo;

	public RealReplicationInsertTrigger(ODB odbToReplicateTo) {
		this.odbToReplicateTo = odbToReplicateTo;
	}

	public void afterInsert(final ObjectRepresentation objectRepresentation, final OID oid) {
		System.out.println("New object has been inserted with oid " + oid);
		System.out.println("Internal OID = " + oid);
		System.out.println("External OID = " + new ExternalObjectOID(oid, getOdb().ext().getDatabaseId()));
		nbInsertsAfter++;

		// Retrieves the NonNativeObjectInfo that has been inserted in the
		// original database
		NonNativeObjectInfo nnoi = Dummy.getNnoi(objectRepresentation);

		// Retreive the engine of the database to replicate to
		IStorageEngine engineToReplicateTo = Dummy.getEngine(odbToReplicateTo);

		// Creates a clean copy of the original nnoi
		NonNativeObjectInfo cleanNnoi = (NonNativeObjectInfo) nnoi.createCopy(new OdbHashMap(), true);

		// Try to retrieve the class info (of the replicated database)
		// We do that for the main class info, but we should do that for
		// recursive classes.
		// As the createCopy method already traverses the nnoi, may b we could
		// do that in the createCopy method?
		ClassInfo ci = engineToReplicateTo.getSession(true).getMetaModel().getClassInfo(nnoi.getClassInfo().getFullClassName(), false);
		if (ci == null) {
			cleanNnoi.setClassInfo(nnoi.getClassInfo().duplicate(true));
		} else {
			cleanNnoi.setClassInfo(ci);
		}

		// Write the clean nnoi to the database
		OID newObjectOid = engineToReplicateTo.writeObjectInfo(StorageEngineConstant.NULL_OBJECT_ID, cleanNnoi,
				StorageEngineConstant.POSITION_NOT_INITIALIZED, false);

		// Commits the database
		Dummy.getEngine(odbToReplicateTo).commit();

		System.out.println("replicated:New Object OID = " + newObjectOid);
	}

	public boolean beforeInsert(final ObjectRepresentation objectRepresentation) {
		return false;
	}

	public int getNbInsertsAfter() {
		return nbInsertsAfter;
	}

	public int getNbInsertsBefore() {
		return nbInsertsBefore;
	}
}
