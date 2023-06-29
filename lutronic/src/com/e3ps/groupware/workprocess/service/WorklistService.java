package com.e3ps.groupware.workprocess.service;

import java.util.Hashtable;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.method.RemoteInterface;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

@RemoteInterface
public interface WorklistService {

	String[] getWorkItemName(WTObject pbo);

	String getCreatorName(WTObject pbo);

	Persistable getPBO(WorkItem workItem);

	Vector completeWfActivity(WorkItem workItem, Hashtable params) throws WTException;

	ProcessData updateContext(ProcessData processdata, String variableName, String val) throws WTException;

	void completeWorkItem(WorkItem workItem, Vector vec);

	Object getWfProcessVariableValue(WfProcess process, String varName);

}
