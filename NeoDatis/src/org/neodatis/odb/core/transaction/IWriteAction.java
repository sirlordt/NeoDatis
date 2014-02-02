package org.neodatis.odb.core.transaction;

import org.neodatis.odb.core.layers.layer3.engine.IFileSystemInterface;

public interface IWriteAction {

	public abstract byte[] getBytes(int index);

	public abstract void applyTo(IFileSystemInterface fsi, int index);

	public void addBytes(byte[] bytes);

	public void persist(IFileSystemInterface fsi, int index);

	public boolean isEmpty();

	public long getPosition();

}