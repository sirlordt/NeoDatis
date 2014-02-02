package org.neodatis.odb.core.layers.layer2.meta;

import org.neodatis.odb.OID;

/**
 * Used for committed zone info. It has one more attribute than the super class. It is used 
 * to keep track of committed deleted objects 
 * @author osmadja
 *
 */
public class CommittedCIZoneInfo extends CIZoneInfo {
	public long nbDeletedObjects;
	
	public CommittedCIZoneInfo(ClassInfo ci, OID first, OID last, long nbObjects) {
		super(ci, first, last, nbObjects);
		nbDeletedObjects = 0;
	}

	public void decreaseNbObjects() {
		nbDeletedObjects++;
	}

	public long getNbDeletedObjects() {
		return nbDeletedObjects;
	}

	public void setNbDeletedObjects(long nbDeletedObjects) {
		this.nbDeletedObjects = nbDeletedObjects;
	}

	public long getNbObjects() {
		return nbObjects - nbDeletedObjects;
	}

	public void setNbObjects(long nb) {
		this.nbObjects = nb;
		this.nbDeletedObjects = 0;
	}

	public void setNbObjects(CommittedCIZoneInfo cizi) {
		this.nbObjects = cizi.nbObjects;
		this.nbDeletedObjects = cizi.nbDeletedObjects;
	}

	public String toString() {
		return "(first=" + first + ",last=" + last + ",nb=" + nbObjects + "-" +nbDeletedObjects+ ")";
	}
	public boolean hasObjects() {
		return nbObjects-nbDeletedObjects != 0;
	}
}
