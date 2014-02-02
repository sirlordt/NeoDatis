/**
 * 
 */
package org.neodatis.odb.core.layers.layer3.engine;

import java.io.Serializable;

import org.neodatis.odb.core.layers.layer2.meta.ClassInfoCompareResult;
import org.neodatis.tool.wrappers.list.IOdbList;
import org.neodatis.tool.wrappers.list.OdbArrayList;

/**
 * @author olivier
 *
 */
public class CheckMetaModelResult implements Serializable{
	private boolean modelHasBeenUpdated;
	private IOdbList<ClassInfoCompareResult> results;
	
	public CheckMetaModelResult(){
		this.modelHasBeenUpdated = false;
		this.results = new OdbArrayList<ClassInfoCompareResult>();
	}

	public boolean isModelHasBeenUpdated() {
		return modelHasBeenUpdated;
	}

	public void setModelHasBeenUpdated(boolean modelHasBeenUpdated) {
		this.modelHasBeenUpdated = modelHasBeenUpdated;
	}

	public IOdbList<ClassInfoCompareResult> getResults() {
		return results;
	}

	public void setResults(IOdbList<ClassInfoCompareResult> results) {
		this.results = results;
	}
	
	public void add(ClassInfoCompareResult result){
		this.results.add(result);
	}
	
	public int size(){
		return this.results.size();
	}

}
