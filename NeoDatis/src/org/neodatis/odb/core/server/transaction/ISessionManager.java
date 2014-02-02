package org.neodatis.odb.core.server.transaction;

import java.util.List;
import java.util.Map;

import org.neodatis.odb.core.ITwoPhaseInit;
import org.neodatis.odb.core.transaction.ISession;

/**
 * The interface for Client server Session Manager
 * @author olivier
 *
 */
public interface ISessionManager extends ITwoPhaseInit{

	ISession getSession(String baseIdentification, boolean throwExceptionIfDoesNotExist);
	//ISession getSessionByConnectionId(String connectionId, boolean throwExceptionIfDoesNotExist);

	void addSession(ISession session);

	void removeSession(String baseIdentification);

	List<String> getSessionDescriptions(Map connectionManagers);

	public long getNumberOfSessions();

}