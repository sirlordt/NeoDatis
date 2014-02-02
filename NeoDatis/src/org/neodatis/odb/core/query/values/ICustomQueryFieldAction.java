package org.neodatis.odb.core.query.values;

import org.neodatis.odb.core.query.execution.IQueryFieldAction;


/**
 * Used to implement custom query action. 
 * @author osmadja
 *
 */
public interface ICustomQueryFieldAction extends IQueryFieldAction{
	void setAttributeName(String attributeName);
	void setAlias(String alias);
}
