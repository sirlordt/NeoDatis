/* !BEGIN_LICENSE: Copyright 2009 Icosystem Corporation. All Rights Reserved.
 *
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * :END_LICENSE!
 */
/*
 * Original file:      SimpleDTO.java
 * Original project:   ico
 * Original name:      com.icosystem.app.gui.editor.dto.NamedDTO
 *
 * Created on:         Aug 18, 2009 11:23:09 AM
 * Created by:         ashton
 */
package org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto;



/**
 *
 * @version $Id: SimpleDTO.java,v 1.1 2009/10/12 18:44:58 olivier_smadja Exp $
 * @author ashton
 */
public class SimpleDTO implements Entity{
	private static final long serialVersionUID = 1L;

	private final long id;
	private Class<?> clx;

	public SimpleDTO(){
		this(-1);
	}

	public SimpleDTO(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}

	public Class<?> getClx(){
		return clx;
	}

	public void setCls(Class<?> clx){
		this.clx = clx;
	}

	@Override
	public int hashCode(){
		return (int) id;
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof SimpleDTO)){
			return false;
		}
		return id == ((SimpleDTO) other).id;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Simple [").append(id);
		sb.append("] Class [").append(clx != null ? clx.getName() : "No Class").append(']');
		return sb.toString();
	}
}
