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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version $Id: SubClassWithList.java,v 1.1 2009/10/12 18:44:58 olivier_smadja Exp $
 * @author ashton
 */
public class SubClassWithList extends SimpleDTO{
	private static final long serialVersionUID = 1L;

	private final List<Class<?>> clxList = new ArrayList<Class<?>>();

	public SubClassWithList(){
		this(-1);
	}

	public SubClassWithList(long id){
		super(id);
	}

	public void add(Class<?> clx){
		this.clxList.add(clx);
	}

	public void clear(){
		this.clxList.clear();
	}

	@Override
	public int hashCode(){
		return super.hashCode();
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof SubClassWithList)){
			return false;
		}
		return getId() == ((SubClassWithList) other).getId();
	}

	@Override
	public String toString(){
		return "SubClassWithList ["+super.toString()+"] clxList ="+ clxList.toString()+']';
	}
}
