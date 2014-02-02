
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
package org.neodatis.odb.impl.core.layers.layer3.oid;

import org.neodatis.odb.OID;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.layers.layer3.IDTypes;
import org.neodatis.odb.core.layers.layer3.IIdManager;
import org.neodatis.odb.core.layers.layer3.IObjectReader;
import org.neodatis.odb.core.layers.layer3.IObjectWriter;
import org.neodatis.odb.core.transaction.ISession;
import org.neodatis.odb.impl.core.layers.layer3.block.BlockStatus;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.tool.DLogger;

/** Class to manage the ids of all the objects of the database.
 * 
 * @author osmadja
 *
 */
public class DefaultIdManager implements IIdManager {
    private static final String LOG_ID = "IdManager";
    private ICoreProvider provider;

	private long currentBlockIdPosition;

	private int currentBlockIdNumber;

	public OID nextId;

	public OID maxId;

	protected IObjectWriter objectWriter;
	protected IObjectReader objectReader;
    protected ISession session;
    

	/** Contains the last ids: id value,id position, id value, id position=> the array is created with twice the size*/
    protected OID [] lastIds;
    protected long [] lastIdPositions;
	private int lastIdIndex;
	private static final int ID_BUFFER_SIZE = 10;

	/**
	 * 
	 * @param objectWriter The object writer
	 * @param objectReader The object reader
	 * @param currentBlockIdPosition The position of the current block
	 * @param currentBlockIdNumber The number of the current block
	 * @param currentMaxId Maximum Database id
	 */
	public DefaultIdManager(IObjectWriter objectWriter, IObjectReader objectReader, long currentBlockIdPosition, int currentBlockIdNumber, OID currentMaxId) {
		this.provider = OdbConfiguration.getCoreProvider();
		this.objectWriter = objectWriter;
        this.objectReader = objectReader;
        this.session = objectWriter.getSession();
		this.currentBlockIdPosition = currentBlockIdPosition;
		this.currentBlockIdNumber = currentBlockIdNumber;
		this.maxId = provider.getObjectOID((long)currentBlockIdNumber * OdbConfiguration.getNB_IDS_PER_BLOCK(),0);
        this.nextId = provider.getObjectOID(currentMaxId.getObjectId()+1,0);
        lastIds = new OID[ID_BUFFER_SIZE];
        for(int i=0;i<ID_BUFFER_SIZE;i++){
        	lastIds[i] = StorageEngineConstant.NULL_OBJECT_ID;
        }
        lastIdPositions = new long[ID_BUFFER_SIZE];
        lastIdIndex = 0 ; 
        
	}

	/** To check if the id block must shift: that a new id block must be created
	 * 
	 * @return a boolean value to check if block of id is full
	 */
	synchronized public boolean mustShift(){
        return nextId.compareTo(maxId) > 0;
	}
	
	/** Gets an id for an object (instance)
	 * 
	 * @param objectPosition the object position (instance) 
	 * @param idType The type id : object,class, unknown
	 * @param label A label for debug
	 * @return The id
	 */
	synchronized OID getNextId(long objectPosition,byte idType,byte idStatus, String label) {

		if(OdbConfiguration.isDebugEnabled(LOG_ID)){
		    DLogger.debug("  Start of "+label+" for object with position "+ objectPosition);
        }
        if (mustShift()) {
			shiftBlock();
		} 
        // Keep the current id
        OID currentNextId = nextId;
        if(idType==IDTypes.CLASS){
        	// If its a class, build a class OID instead.
        	currentNextId = provider.getClassOID(currentNextId.getObjectId());
        }

        
        // Compute the new index to be used to store id and its position in the lastIds and lastIdPositions array
        int currentIndex = (lastIdIndex+1)%ID_BUFFER_SIZE;
        // Stores the id
        lastIds[currentIndex] = currentNextId;
        // really associate id to the object position
		long idPosition = associateIdToObject(idType,idStatus, objectPosition);
		// Store the id position
		lastIdPositions[currentIndex] = idPosition;
        
        if(OdbConfiguration.isDebugEnabled(LOG_ID)){
            DLogger.debug("  End of " + label+" for object with position "+ idPosition + " : returning " + currentNextId);
        }
		// Update the id buffer index
		lastIdIndex = currentIndex;
		return currentNextId;
	}
	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.oid.IIdManager#getNextObjectId(long)
	 */
	synchronized public OID getNextObjectId(long objectPosition) {
		return getNextId(objectPosition,IDTypes.OBJECT,IDStatus.ACTIVE,"getNextObjectId");
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.oid.IIdManager#getNextClassId(long)
	 */
	synchronized public OID getNextClassId(long objectPosition) {
		return getNextId(objectPosition,IDTypes.CLASS,IDStatus.ACTIVE,"getNextClassId");
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.oid.IIdManager#updateObjectPositionForOid(org.neodatis.odb.core.OID, long, boolean)
	 */
	public void updateObjectPositionForOid(OID oid, long objectPosition,boolean writeInTransaction){
		//TODO Remove comments here
		// Id may be negative to differ from positions
		//if(id<0){
		//	id = -id;
		//}
		long idPosition = getIdPosition(oid);
		objectWriter.updateObjectPositionForObjectOIDWithPosition(idPosition,objectPosition,writeInTransaction);
        if(OdbConfiguration.isDebugEnabled(LOG_ID)){
            DLogger.debug("IDManager : Updating id "+oid+" with position "+objectPosition);
        }
	}
    /* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.oid.IIdManager#updateClassPositionForId(org.neodatis.odb.core.OID, long, boolean)
	 */
    public void updateClassPositionForId(OID classId, long objectPosition,boolean writeInTransaction) {
        // TODO Remove comments here
    	// Id may be negative to differ from positions
        //if(classId<0){
        //    classId = -classId;
        //}
        long idPosition = getIdPosition(classId);
        objectWriter.updateClassPositionForClassOIDWithPosition(idPosition,objectPosition,writeInTransaction);
        if(OdbConfiguration.isDebugEnabled(LOG_ID)){
            DLogger.debug("Updating id "+classId+" with position "+objectPosition);
        }
    }
	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.oid.IIdManager#updateIdStatus(org.neodatis.odb.core.OID, byte)
	 */
	public void updateIdStatus(OID id, byte newStatus) {
		long idPosition = getIdPosition(id);
		objectWriter.updateStatusForIdWithPosition(idPosition,newStatus,true); 
	}

	private long getIdPosition(OID oid) {
		// first check if it is the last
		if(lastIds[lastIdIndex] != null && lastIds[lastIdIndex].equals(oid)){
			return  lastIdPositions[(lastIdIndex)];
		}
		for(int i=0;i<ID_BUFFER_SIZE;i++){
			if(lastIds[i]!=null && lastIds[i].equals(oid)){
				return lastIdPositions[i];
			}
		}
        // object id is not is cache
        return objectReader.readOidPosition(oid);
	}

	private long associateIdToObject(byte idType, byte idStatus, long objectPosition) {
		long idPosition = objectWriter.associateIdToObject(idType,idStatus, currentBlockIdPosition,nextId,objectPosition,false);

		nextId = provider.getObjectOID(nextId.getObjectId()+1,0);
		return idPosition;
	}

	private void shiftBlock() {
		long currentBlockPosition = this.currentBlockIdPosition;
		// the block has reached the end, , must create a new id block
		long newBlockPosition = createNewBlock();
		// Mark the current block as full
		markBlockAsFull(currentBlockPosition, newBlockPosition);

		this.currentBlockIdNumber++;
		this.currentBlockIdPosition = newBlockPosition;
		this.maxId = provider.getObjectOID((long)currentBlockIdNumber * OdbConfiguration.getNB_IDS_PER_BLOCK(),0);
	}
	private void markBlockAsFull(long currentBlockIdPosition, long nextBlockPosition)  {
		objectWriter.markIdBlockAsFull(currentBlockIdPosition, nextBlockPosition, false);

	}

	private long createNewBlock() {
		long position = objectWriter.writeIdBlock(-1, OdbConfiguration.getIdBlockSize(), BlockStatus.BLOCK_NOT_FULL, currentBlockIdNumber+1, currentBlockIdPosition, false);
		return position;
	}

	public synchronized OID consultNextOid(){
		return nextId;
	}
	
	/* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.oid.IIdManager#reserveIds(long)
	 */
	public void reserveIds(long nbIds) {
		OID id = null;
		while(nextId.getObjectId()<nbIds+1){
			id = getNextId(-1,IDTypes.UNKNOWN,IDStatus.UNKNOWN,"reserving id");
			if(OdbConfiguration.isDebugEnabled(LOG_ID)){
				DLogger.debug("reserving id "+id);
			}
		}
		return;
	}
    /* (non-Javadoc)
	 * @see org.neodatis.odb.core.impl.layers.layer3.oid.IIdManager#getObjectPositionWithOid(org.neodatis.odb.core.OID, boolean)
	 */
    public long getObjectPositionWithOid(OID oid, boolean useCache) {
    	return objectReader.getObjectPositionFromItsOid(oid,useCache, true);
    }

    public void clear() {
        objectReader = null;
        objectWriter = null;
        session = null;
        lastIdPositions = null;
        lastIds = null;
    }
    
    protected ISession getSession(){
    	return session;
    }
    
}
