package org.neodatis.tool.wrappers.io;

import java.io.File;

/**
 * @sharpen.ignore
 * @author olivier
 *
 */
public class OdbFile {
	private File file;
	
	public OdbFile(String fileName){
		String fullPath = new File(fileName).getAbsolutePath();
		this.file = new File(fullPath);
	}
	
	public String getDirectory(){
		return file.getParentFile().getAbsolutePath();
	}

	public String getCleanFileName(){
		return file.getName();
	}
	
	public String getFullPath(){
		return file.getAbsolutePath();
	}

	public boolean exists() {
		return file.exists();
	}
	
	public void clear(){
		file = null;
	}

	public OdbFile getParentFile() {
		return new OdbFile(file.getParent());
	}

	public void mkdirs() {
		file.mkdirs();
	}

	public boolean delete() {
		return file.delete();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return file.getAbsolutePath();
	}
}
