package com.e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.ApprovalUserLine;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
//			String name = WorkspaceHelper.manager.getName(per);
//			String description = WorkspaceHelper.manager.getDescription(per);

			// 마스터 생성..
			ApprovalMaster master = ApprovalMaster.newApprovalMaster();
			master.setName("결재 테스트..");
			master.setCompleteTime(null);
			master.setOwnership(ownership);
			master.setPersist(per);
			master.setStartTime(startTime);
//			if (isAgree) {
//				master.setState(WorkspaceHelper.STATE_AGREE_READY);
//			} else {
//				master.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
//			}
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);

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

			ApprovalUserLine line = (ApprovalUserLine) CommonUtils.getObject(oid);
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

}
