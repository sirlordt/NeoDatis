package org.neodatis.odb.impl.core.oid;

import org.neodatis.odb.DatabaseId;
import org.neodatis.odb.TransactionId;


public class TransactionIdImpl implements TransactionId {
	private long id1;
	private long id2;
	
	private DatabaseId databaseId;

	public TransactionIdImpl(DatabaseId databaseID, long id1, long id2) {
		super();
		this.databaseId = databaseID;
		this.id1 = id1;
		this.id2 = id2;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.impl.core.oid.TransactionId#getId1()
	 */
	public long getId1() {
		return id1;
	}

	

	/* (non-Javadoc)
	 * @see org.neodatis.odb.impl.core.oid.TransactionId#getDatabaseID()
	 */
	public DatabaseId getDatabaseId() {
		return databaseId;
	}

	public long getId2() {
		return id2;
	}

	public TransactionId next() {
		return new TransactionIdImpl(databaseId,id1,id2+1);
	}
	public TransactionId prev() {
		return new TransactionIdImpl(databaseId,id1,id2-1);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer("tid=").append(String.valueOf(id1)).append(String.valueOf(id2));
		buffer.append(" - dbid=").append(databaseId);
		return buffer.toString();
	}

	public boolean equals(Object object) {
		if(object==null || object.getClass()!=TransactionIdImpl.class ){
			return false;
		}
		TransactionIdImpl tid = (TransactionIdImpl) object;
		return id1==tid.id1 && id2==tid.id2 && databaseId.equals(tid.databaseId);
	}
	
	

}
