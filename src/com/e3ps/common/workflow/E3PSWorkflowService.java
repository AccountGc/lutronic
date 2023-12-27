package com.e3ps.common.workflow;

import java.util.Hashtable;

import com.e3ps.change.EChangeActivity;

import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteInterface;
import wt.org.WTPrincipal;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

@RemoteInterface
public interface E3PSWorkflowService {

	void changeLCState(LifeCycleManaged lcm, String state);

	void deleteRole(LifeCycleManaged lcm, String roleName);

	void addParticipant(LifeCycleManaged lcm, String roleName, String id);

	void deleteWfProcess(LifeCycleManaged _lcm);

	/** DeleteWorkflow 에서 가져옴
	 * @param _lcm
	 */
	void delete(LifeCycleManaged _lcm);

	QueryResult getWfProcess(LifeCycleManaged lcm);

	void reassign(Hashtable hash) throws Exception;

	void reassignWorkItem(WorkItem workitem, WTPrincipal newOwner)
			throws WTException;

	Persistable getPBO(WorkItem workItem);

	void deleteParticipant(LifeCycleManaged obj, String roleName, String id);

}
