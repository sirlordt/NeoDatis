package org.neodatis.odb.impl.core.oid;

import org.neodatis.odb.DatabaseId;
import org.neodatis.tool.wrappers.OdbString;

public class DatabaseIdImpl implements DatabaseId {
	private long[] ids;

	public DatabaseIdImpl() {
		super();
	}

	public DatabaseIdImpl(long[] ids) {
		super();
		this.ids = ids;
	}

	/* (non-Javadoc)
	 * @see org.neodatis.odb.impl.core.oid.DatabaseId#getIds()
	 */
	public long[] getIds() {
		return ids;
	}

	public void setIds(long[] ids) {
		this.ids = ids;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		for(int i=0;i<ids.length;i++){
			if(i!=0){
				buffer.append("-");
			}
			buffer.append(String.valueOf(ids[i]));
		}
		return buffer.toString();
	}
	public static DatabaseId fromString(String sid) {
		String[] tokens = OdbString.split(sid,"-");
		long[] ids = new long[tokens.length];
		for(int i=0;i<ids.length;i++){
			ids[i] = Long.parseLong(tokens[i]);
		}
		return new DatabaseIdImpl(ids);
	}
	
	public boolean equals(Object object) {
		if(object==null || object.getClass()!=DatabaseIdImpl.class ){
			return false;
		}
		DatabaseIdImpl dbId = (DatabaseIdImpl) object;
		
		for(int i=0;i<ids.length;i++){
			if(ids[i]!=dbId.ids[i]){
				return false;
			}
		}
		return true;
	}
}
