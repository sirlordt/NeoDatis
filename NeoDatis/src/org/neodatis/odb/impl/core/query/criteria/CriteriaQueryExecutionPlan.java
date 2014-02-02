package org.neodatis.odb.impl.core.query.criteria;

import org.neodatis.odb.core.layers.layer2.meta.ClassInfo;
import org.neodatis.odb.core.layers.layer2.meta.ClassInfoIndex;
import org.neodatis.odb.core.query.criteria.ICriterion;
import org.neodatis.odb.core.query.execution.IQueryExecutionPlan;
import org.neodatis.tool.wrappers.OdbTime;
import org.neodatis.tool.wrappers.list.IOdbList;

/**
 * A simple Criteria execution plan Check if the query can use index and tries
 * to find the best index to be used
 * 
 * @author osmadja
 * 
 */
public class CriteriaQueryExecutionPlan implements IQueryExecutionPlan {

	protected transient ClassInfo classInfo;

	protected transient CriteriaQuery query;

	protected boolean useIndex;

	protected transient ClassInfoIndex classInfoIndex;
	/** to keep track of the start date time of the plan*/
	protected long start;
	/** to keep track of the end date time of the plan*/
	protected long end;
	/** To keep the execution detail*/
	protected String details;

	public CriteriaQueryExecutionPlan() {
	}
	public CriteriaQueryExecutionPlan(ClassInfo classInfo, CriteriaQuery query) {
		this.classInfo = classInfo;
		this.query = query;
		this.query.setExecutionPlan(this);
		init();
	}

	protected void init() {
		start = 0;
		end = 0;
		// for instance, only manage index for one field query using 'equal'
		if (classInfo.hasIndex() && query.hasCriteria() && canUseIndex(query.getCriteria())) {
			IOdbList<String> fields = query.getAllInvolvedFields();
			if (fields.isEmpty()) {
				useIndex = false;
			} else {
				int[] fieldIds = getAllInvolvedFieldIds(fields);
				classInfoIndex = classInfo.getIndexForAttributeIds(fieldIds);
				if (classInfoIndex != null) {
					useIndex = true;
				}
			}
		}
		// Keep the detail
		details = getDetails();
	}
	
	/**Transform a list of field names into a list of field ids
	 * 
	 * @param fields
	 * @return The array of field ids
	 */
	protected int[] getAllInvolvedFieldIds(IOdbList<String> fields){
        int nbFields = fields.size();
        int[] fieldIds = new int[nbFields];
		for(int i=0;i<nbFields;i++){
			fieldIds[i] = classInfo.getAttributeId(fields.get(i).toString());
		}
        return fieldIds;
    }


	private boolean canUseIndex(ICriterion criteria) {
		return criteria.canUseIndex();
	}

	public ClassInfoIndex getIndex() {
		return classInfoIndex;
	}

	public boolean useIndex() {
		return useIndex;
	}

	public String getDetails() {
		if(details!=null){
			return details;
		}
		StringBuffer buffer = new StringBuffer();
		if (classInfoIndex == null) {
			buffer.append("No index used, Execution time=").append(getDuration()).append("ms");
			return buffer.toString();
		}
		return buffer.append("Following indexes have been used : ").append(classInfoIndex.getName()).append(", Execution time=").append(getDuration()).append("ms").toString();
	}

	public void end() {
		end = OdbTime.getCurrentTimeInMs();		
	}

	public long getDuration() {
		return (end-start);
	}

	public void start() {
		start = OdbTime.getCurrentTimeInMs();
		
	}

}
