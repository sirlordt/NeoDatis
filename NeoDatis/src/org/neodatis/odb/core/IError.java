package org.neodatis.odb.core;


public interface IError {
	public IError addParameter(Object o);
	public IError addParameter(String s);
	public IError addParameter(int i);
	public IError addParameter(byte i);
	public IError addParameter(long l);
}
