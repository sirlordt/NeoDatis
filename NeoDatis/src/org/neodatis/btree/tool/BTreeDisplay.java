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
package org.neodatis.btree.tool;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.IBTreeNode;
import org.neodatis.btree.IKeyAndValue;

/**
 * an utility to display a btree
 * @author osmadja
 *
 */
public class BTreeDisplay {
	private StringBuffer[] lines;
	private StringBuffer result;

	public BTreeDisplay() {
	}

	public StringBuffer build(IBTree btree, boolean withIds) {
		lines = new StringBuffer[btree.getHeight()];
		for (int i = 0; i < btree.getHeight(); i++) {
			lines[i] = new StringBuffer();
		}
		buildDisplay(btree.getRoot(), 0,0,"0",withIds);
		buildRepresentation();
		return result;
	}
	
	public StringBuffer build(IBTreeNode node, int height,boolean withIds) {
		lines = new StringBuffer[height];
		for (int i = 0; i < height; i++) {
			lines[i] = new StringBuffer();
		}
		buildDisplay(node, 0,0,"0",withIds);
		buildRepresentation();
		return result;
	}

	private void buildRepresentation() {
		int maxLineSize = lines[lines.length-1].length();
		result = new StringBuffer();
		for(int i=0;i<lines.length;i++){
			result.append(format(lines[i],i,maxLineSize)).append("\n");
		}		
	}

	public StringBuffer getResult() {
		return result;
	}

	private StringBuffer format(StringBuffer line, int height,int maxLineSize) {
		int diff = maxLineSize - line.length();
		StringBuffer lineResult = new StringBuffer();
		lineResult.append("h=").append(height+1).append(":");
		lineResult.append(fill(diff/2,' '));
		lineResult.append(line);
		lineResult.append(fill(diff/2,' '));
		return lineResult;
	}

	private StringBuffer fill(int size, char c) {
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<size;i++){
			buffer.append(c);
		}
		return buffer;
	}

	private void buildDisplay(IBTreeNode node, int currentHeight, int childIndex,Object parentId, boolean withIds) {
		if(currentHeight>lines.length-1){
			return;
		}
		// get string buffer of this line
		StringBuffer line = lines[currentHeight];

		if(withIds){
			line.append(node.getId()).append(":[");
		}else{
			line.append("[");
		}

		for (int i = 0; i < node.getNbKeys(); i++) {
			if (i > 0) {
				line.append(" , ");
			}
			IKeyAndValue kav = node.getKeyAndValueAt(i);
			line.append(kav.getKey());
		}
		if(withIds){
			line.append("]:").append(node.getParentId()).append("/").append(parentId).append("    ");
		}else{
			line.append("]  ");
		}
		
		for(int i=0;i<node.getNbChildren();i++){
			IBTreeNode child = node.getChildAt(i,false);
			if(child!=null){
				buildDisplay(child, currentHeight+1,i,node.getId(),withIds);
			}else{
				lines[currentHeight+1].append("[Child "+(i+1)+" null!] ");
			}
		}
		
		
	}

}
