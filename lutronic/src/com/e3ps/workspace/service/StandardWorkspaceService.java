package com.e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.ApprovalUserLine;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class StandardWorkspaceService extends StandardManager implements WorkspaceService {

	public static StandardWorkspaceService newStandardWorkspaceService() throws WTException {
		StandardWorkspaceService instance = new StandardWorkspaceService();
		instance.initialize();
		return instance;
	}

	@Override
	public void convert() throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WorkItem.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WorkItem workItem = (WorkItem) obj[0];
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void register(Persistable per, ArrayList<Map<String, String>> agreeRows,
			ArrayList<Map<String, String>> approvalRows, ArrayList<Map<String, String>> receiveRows) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp startTime = new Timestamp(new Date().getTime());
			Ownership ownership = CommonUtil.sessionOwner();
			String name = WorkspaceHelper.manager.getName(per);
//			String description = WorkspaceHelper.manager.getDescription(per);

			// 마스터 생성..
			ApprovalMaster master = ApprovalMaster.newApprovalMaster();
			master.setName(name);
			master.setCompleteTime(null);
			master.setOwnership(ownership);
			master.setPersist(per);
			master.setStartTime(startTime);
			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_APPROVING);
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);

			// 기안 라인
			ApprovalLine startLine = ApprovalLine.newApprovalLine();
			startLine.setName(name);
			startLine.setOwnership(ownership);
			startLine.setMaster(master);
			startLine.setReads(true);
			startLine.setSort(-50);
			startLine.setStartTime(startTime);
			startLine.setType(WorkspaceHelper.SUBMIT_LINE);
			startLine.setRole(WorkspaceHelper.WORKING_SUBMITTER);
			startLine.setDescription(null);
			startLine.setCompleteTime(startTime);
			startLine.setState(WorkspaceHelper.STATE_SUBMIT_COMPLETE);
			PersistenceHelper.manager.save(startLine);

			int sort = 0;
			// 결재부터 샘플로
			for (Map<String, String> approval : approvalRows) {
				String woid = approval.get("woid");
				WTUser wtuser = (WTUser) CommonUtil.getObject(woid);
				// 결재 라인 생성
				ApprovalLine approvalLine = ApprovalLine.newApprovalLine();
				approvalLine.setName(name);
				approvalLine.setOwnership(Ownership.newOwnership(wtuser));
				approvalLine.setMaster(master);
				approvalLine.setReads(false);
				approvalLine.setSort(sort);
				approvalLine.setType(WorkspaceHelper.APPROVAL_LINE);
				approvalLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
				approvalLine.setDescription(null);

				if (sort == 0) {
					approvalLine.setStartTime(startTime);
					approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
					approvalLine.setCompleteTime(null);
				} else {
					approvalLine.setStartTime(null);
					approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
					approvalLine.setCompleteTime(null);
				}
				PersistenceHelper.manager.save(approvalLine);
				sort += 1;
			}

			// 수신라인
			for (Map<String, String> receive : receiveRows) {
				String woid = receive.get("woid");
				WTUser wtuser = (WTUser) CommonUtil.getObject(woid);
				// 결재 라인 생성
				ApprovalLine receiveLine = ApprovalLine.newApprovalLine();
				receiveLine.setName(name);
				receiveLine.setOwnership(Ownership.newOwnership(wtuser));
				receiveLine.setMaster(master);
				receiveLine.setReads(false);
				receiveLine.setSort(0);
				receiveLine.setStartTime(startTime);
				receiveLine.setType(WorkspaceHelper.RECEIVE_LINE);
				receiveLine.setRole(WorkspaceHelper.WORKING_RECEIVE);
				receiveLine.setDescription(null);
				receiveLine.setCompleteTime(null);
				receiveLine.setState(WorkspaceHelper.STATE_RECEIVE_READY);
				PersistenceHelper.manager.save(receiveLine);
			}

			afterRegisterAction(master);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	/**
	 * 결재 등록시 이뤄지는 작업 상태값 변경
	 */
	private void afterRegisterAction(ApprovalMaster master) throws Exception {
		master = (ApprovalMaster) PersistenceHelper.manager.refresh(master);
		Persistable per = master.getPersist();

		if (per instanceof LifeCycleManaged) {
			LifeCycleManaged lcm = (LifeCycleManaged) per;
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) lcm, State.toState("APPROVING"));
		}
		// 일괄 격제 관련해서 처리
	}

	@Override
	public void self(Persistable per) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void save(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		ArrayList<Map<String, Object>> approvalList = (ArrayList<Map<String, Object>>) params.get("approvalList");
		ArrayList<Map<String, Object>> agreeList = (ArrayList<Map<String, Object>>) params.get("agreeList");
		ArrayList<Map<String, Object>> receiveList = (ArrayList<Map<String, Object>>) params.get("receiveList");
		Transaction trs = new Transaction();
		try {
			trs.start();

			ArrayList<String> _approvalList = new ArrayList<>();
			ArrayList<String> _agreeList = new ArrayList<>();
			ArrayList<String> _receiveList = new ArrayList<>();

			ApprovalUserLine line = ApprovalUserLine.newApprovalUserLine();
			line.setName(name);
			line.setOwnership(CommonUtil.sessionOwner());

			for (Map<String, Object> approval : approvalList) {
				String oid = (String) approval.get("poid");
				_approvalList.add(oid);
			}

			for (Map<String, Object> agree : agreeList) {
				String oid = (String) agree.get("poid");
				_agreeList.add(oid);
			}

			for (Map<String, Object> receive : receiveList) {
				String oid = (String) receive.get("poid");
				_receiveList.add(oid);
			}
			line.setFavorite(false);
			line.setApprovalList(_approvalList);
			line.setAgreeList(_agreeList);
			line.setReceiveList(_receiveList);
			PersistenceHelper.manager.save(line);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ApprovalUserLine line = (ApprovalUserLine) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(line);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void favorite(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		boolean checked = (boolean) params.get("checked");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser sessionUser = CommonUtil.sessionUser();

			ApprovalUserLine line = (ApprovalUserLine) CommonUtil.getObject(oid);

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ApprovalUserLine.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalUserLine.class, "ownership.owner.key.id", sessionUser);
			QueryResult rs = PersistenceHelper.manager.find(query);
			while (rs.hasMoreElements()) {
				Object[] obj = (Object[]) rs.nextElement();
				ApprovalUserLine aLine = (ApprovalUserLine) obj[0];
				if (line.getPersistInfo().getObjectIdentifier().getId() == aLine.getPersistInfo().getObjectIdentifier()
						.getId()) {
					aLine.setFavorite(checked);
				} else {
					aLine.setFavorite(false);
				}
				PersistenceHelper.manager.modify(aLine);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void _reset(Map<String, ArrayList<String>> params) throws Exception {
		ArrayList<String> arr = params.get("arr");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : arr) {
				ApprovalLine l = (ApprovalLine) CommonUtil.getObject(oid);
				ApprovalMaster master = l.getMaster();
				ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(master);
				for (ApprovalLine line : list) {
					// 모든결재 라인 삭제
					PersistenceHelper.manager.delete(line);
				} // 결재마스터 객체 삭제
				PersistenceHelper.manager.delete(master);

			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void _approval(Map<String, String> params) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void _reject(Map<String, String> params) throws Exception {
		String oid = params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
			ApprovalMaster m = line.getMaster();

			WTUser sessionUser = CommonUtil.sessionUser();

			// 기안라인은 제외한다?
			ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m, false);
			for (ApprovalLine l : list) {

				String t = l.getType();
				// 검토라인
				if (t.equals(WorkspaceHelper.AGREE_LINE)) {
					l.setState(WorkspaceHelper.STATE_AGREE_REJECT);
				} else if (t.equals(WorkspaceHelper.APPROVAL_LINE)) {
					l.setState(WorkspaceHelper.STATE_APPROVAL_REJECT);
				} else if (t.equals(WorkspaceHelper.RECEIVE_LINE)) {
					l.setState(WorkspaceHelper.STATE_RECEIVE_REJECT);
				}
				l.setCompleteTime(new Timestamp(new Date().getTime()));
				PersistenceHelper.manager.modify(l);
			}

			m.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_REJECT);
			m.setCompleteTime(new Timestamp(new Date().getTime()));
			m = (ApprovalMaster) PersistenceHelper.manager.modify(m);

			// 반려후 작업할 행위.. 객체 상태값 반려됨으로 처리한다.
			afterRejectAction(m);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}

	}

	/**
	 * 결재 반려후 이뤄지는 함수
	 */
	private void afterRejectAction(ApprovalMaster m) throws Exception {
		Persistable per = m.getPersist();
		if (per instanceof LifeCycleManaged) {
			LifeCycleManaged lcm = (LifeCycleManaged) per;
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) lcm, State.toState("RETURN"));
		}
	}
}
