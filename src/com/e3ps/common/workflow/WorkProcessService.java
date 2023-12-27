package com.e3ps.common.workflow;

import java.util.Locale;
import java.util.Vector;

import wt.fc.WTObject;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTPrincipal;
import wt.team.Team;
import wt.team.TeamManaged;
import wt.util.WTException;

public interface WorkProcessService {

	void addParticipant(WTObject obj, String roleName, String id);

	void addPrincialToRole(WTObject obj, String roleName, String[] assignedId);

	void addPrincialToRole(WTObject obj, String roleName, Vector users);

	void addPrincialToRole(WTObject obj, String roleName, String assignedId);

	void addPrincialToRole(WTObject obj, String roleName, WTPrincipal lUser);

	void deleteAllRoles(WTObject obj);

	void deleteParticipant(WTObject obj, String roleName, String id);

	void deleteRole(WTObject obj, String roleName);

	boolean includeRole(WTObject obj, String roleName);

	Vector getRoleParticipant(TeamManaged obj, String roleName);

	String getTaskName(String key, Locale locale);

	LifeCycleManaged getLCMObject(String paramString) throws Exception;

	LifeCycleManaged getLCMObject(Team team) throws Exception;

	void setDefaultAssignee(WTObject obj) throws WTException;

	void setAssignee(WTObject obj, WTPrincipal assignee) throws WTException;

}
