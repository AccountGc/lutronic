package com.e3ps.common.workflow;

import java.util.Hashtable;
import java.util.Vector;

import wt.clients.vc.CheckInOutTaskLogic;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.OrganizationServicesMgr;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.OwnershipHelper;
import wt.ownership.OwnershipServerHelper;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignee;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WfPrincipalAssignee;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowServerHelper;

import com.e3ps.change.EChangeActivity;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.groupware.workprocess.WFItem;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;

@SuppressWarnings("serial")
public class StandardE3PSWorkflowService extends StandardManager implements E3PSWorkflowService {
	
	public static StandardE3PSWorkflowService newStandardE3PSWorkflowService() throws Exception {
		final StandardE3PSWorkflowService instance = new StandardE3PSWorkflowService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public void changeLCState(LifeCycleManaged lcm, String state) {
		if ((lcm == null) || (state == null))
			return;
		State newState = State.toState(state);
		try {
			if (((lcm instanceof Workable)) && (CheckInOutTaskLogic.isCheckedOut((Workable) lcm)))
				return;
			if (newState.equals(lcm.getLifeCycleState()))
				return;

			LifeCycleHelper.service.setLifeCycleState(lcm, newState, false);
		} catch (LifeCycleException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteRole(LifeCycleManaged lcm, String roleName) {
		if ((lcm instanceof TeamManaged)) {
			try {
				Team team = TeamHelper.service.getTeam(lcm);
				Role role = Role.toRole(roleName);

				Vector vecRole = team.getRoles();
				for (int i = vecRole.size() - 1; i > -1; i--) {
					if (!role.equals((Role) vecRole.get(i)))
						continue;
					team.deleteRole((Role) vecRole.get(i));
					break;
				}

			} catch (TeamException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void addParticipant(LifeCycleManaged lcm, String roleName, String id) {
		if ((lcm instanceof TeamManaged)) {
			try {
				Team team = TeamHelper.service.getTeam(lcm);
				Role role = Role.toRole(roleName);

				team.addPrincipal(role, id == null ? null : OrganizationServicesMgr.getPrincipal(id));
			} catch (TeamException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void deleteWfProcess(LifeCycleManaged _lcm) {
		try {
			delete(_lcm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** DeleteWorkflow 에서 가져옴
	 * @param _lcm
	 */
	@Override
	public void delete(LifeCycleManaged _lcm) {
		try {
			SessionHelper.manager.setAdministrator();
			QueryResult qr = E3PSWorkflowHelper.service.getWfProcess(_lcm);
			while (qr.hasMoreElements()) {
				WfProcess wfProcess = (WfProcess) qr.nextElement();
				WfEngineHelper.service.deleteProcess(wfProcess);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public QueryResult getWfProcess(LifeCycleManaged lcm) {
		try {
			QuerySpec query = new QuerySpec(WfProcess.class);
			SearchUtil.appendEQUAL(query, WfProcess.class, "businessObjReference", CommonUtil.getFullOIDString(lcm), 0);
			return PersistenceHelper.manager.find(query);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	   * 위임시 결재자 정보 수정 및 link 객체로 이력 남기기
	   * @param req
	   * @param res
	 * @throws Exception 
	   */
	@Override
	public void reassign(Hashtable hash) throws Exception {
		String oid = (String) hash.get("oid");
		WorkItem workitem = (WorkItem) CommonUtil.getObject(oid);
		String userOid = (String) hash.get("newUser");
		//System.out.println("userOid="+userOid);
		if(null==userOid || userOid.length()==0){
			//System.out.println("reassign ERROR Null userOid="+userOid);
			return;
		}
		People people = (People) CommonUtil.getObject(userOid);
		WTUser newUser = people.getUser();
		try {

			// WFItemUserLink 재설정
			WTUser oldUser = (WTUser) workitem.getOwnership().getOwner().getPrincipal();
			reassignWorkItem(workitem, newUser);
			WTUser currentUser = (WTUser) workitem.getOwnership().getOwner().getPrincipal();
			WTObject wtobject = (WTObject) getPBO(workitem);

			// Role에 할당된 사용자도 수정
			String assignRoleName = (String) hash.get("assignrolename");
			//System.out.println("assignrolename = " + assignRoleName);
			//System.out.println("wtobject = " + wtobject);
			if (assignRoleName != null) {
				E3PSWorkflowHelper.service.deleteParticipant( (LifeCycleManaged) wtobject, assignRoleName, currentUser.getName());
				E3PSWorkflowHelper.service.addParticipant( (LifeCycleManaged) wtobject, assignRoleName, newUser.getName());

				/* ECA 담당자 변경 */
				if (wtobject instanceof EChangeActivity) {
					EChangeActivity eca = (EChangeActivity) wtobject;
					eca.setActiveUser(newUser);
					PersistenceHelper.manager.modify(eca);
				}
			}

			WFItem wfitem = WFItemHelper.service.getWFItem(wtobject);
			if (wfitem != null) {

				WfActivity activity = (WfActivity) workitem.getSource().getObject();
				String activityName = WFItemHelper.service.getWFItemActivityName(activity.getName());
				WFItemUserLink link = WFItemHelper.service.getOwnerApplineLink(oldUser, wfitem, activityName);

				if (link != null) {
					// 오늘날짜
					// Timestamp today =
					// DateUtil.getTimestampFormat(DateUtil.getToDay("yyyy-MM-dd hh:mm:ss"),
					// "yyyy-MM-dd hh:mm:ss");
					// Timestamp today = new java.sql.Timestamp(new
					// java.util.Date().getTime());
					// newlink.setProcessDate(new java.sql.Timestamp(new
					// java.util.Date().getTime()));

					// 기존객체 수정
					link.setState("위임");
					String comment = "'" + oldUser.getFullName() + "'로 부터 '" + newUser.getFullName() + "'으로 업무 위임";
					link.setComment(comment);
					//System.out.println("disable true 변경");
					link.setDisabled(true);
					link.setCommissionDabled(false);
					link.setProcessDate(new java.sql.Timestamp(new java.util.Date().getTime()));
					int order = link.getProcessOrder();
					PersistenceHelper.manager.save(link);
					int seq = link.getSeqNo();

					// 새로운 객체 생성.
					WFItemUserLink newlink = WFItemUserLink.newWFItemUserLink(newUser, wfitem);
					PeopleDTO pData = new PeopleDTO(newUser);
					newlink.setDepartmentName(pData.departmentName);
					newlink.setActivityName(link.getActivityName());
					newlink.setProcessOrder(order);
					newlink.setDisabled(false);
					newlink.setCommissionDabled(true);
					newlink.setSeqNo(seq);
					newlink.setState("위임받음");
					PersistenceHelper.manager.save(newlink);
				}

			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 위임시 workitem 정보 수정
	 * 
	 * @param workitem
	 * @param newOwner
	 * @throws WTException
	 */
	@Override
	public void reassignWorkItem(WorkItem workitem, WTPrincipal newOwner) throws WTException {

		Transaction trx = new Transaction();
		trx.start();
		WTObject wtobject = (WTObject) workitem.getPrimaryBusinessObject().getObject();

		if (wtobject != null) {
			try {
				WfAssignment assignment = (WfAssignment) workitem.getParentWA().getObject();

				WfAssignee assignee = assignment.getAssignee();
				WTPrincipal oldOwner = OwnershipHelper.getOwner(workitem);
				WTPrincipalReference oldOwnerReference = WTPrincipalReference.newWTPrincipalReference(oldOwner);
				WTPrincipalReference newOwnerReference = WTPrincipalReference.newWTPrincipalReference(newOwner);

				WorkflowServerHelper.service.revokeTaskBasedRights(workitem);
				OwnershipServerHelper.service.changeOwner(workitem, newOwner, false);
				workitem.setStatus(WfAssignmentState.POTENTIAL);
				WorkflowServerHelper.service.setTaskBasedRights(workitem, newOwnerReference);
				PersistenceServerHelper.manager.update(workitem);

				Vector principals = assignment.getPrincipals();
				principals.removeElement(oldOwnerReference);
				principals.addElement(newOwnerReference);
				assignment.setPrincipals(principals);

				if (assignee instanceof WfPrincipalAssignee) {
					WTPrincipalReference principalReference = ((WfPrincipalAssignee) assignee).getPrincipal();
					if (principalReference.getObject() instanceof WTUser)
						((WfPrincipalAssignee) assignee).setPrincipal(newOwnerReference);
				}

				PersistenceServerHelper.manager.update(assignment);
				trx.commit();

			} catch (WTException e) {
				trx.rollback();
				e.printStackTrace();
			} catch (wt.util.WTPropertyVetoException e) {
				trx.rollback();
				e.printStackTrace();
			}
		}
		// ##end reassignWorkItem%412322FD0283.body
	}
	
	@Override
	public Persistable getPBO(WorkItem workItem) {
		Persistable pbo = null;
		try {
			pbo = workItem.getPrimaryBusinessObject().getObject();
		} catch (Exception e) {
			//System.out.println("사용가능하지 않은 WorkItem = " + workItem);
		}
		return pbo;
	}
	
	/**
	 * @param obj
	 * @param roleName
	 * @param id
	 */
	@Override
	public void deleteParticipant(LifeCycleManaged obj, String roleName, String id) {

		if (obj instanceof TeamManaged) {
			try {
				Team team = TeamHelper.service.getTeam((TeamManaged) obj);
				Role role = Role.toRole(roleName);

				Vector vecRole = team.getRoles();
				for (int i = vecRole.size() - 1; i > -1; i--) {
					if (role.equals((Role) vecRole.get(i))) {
						team.deletePrincipalTarget(role, OrganizationServicesMgr.getPrincipal(id));
						break;
					}
				}
			} catch (TeamException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
	}
}
