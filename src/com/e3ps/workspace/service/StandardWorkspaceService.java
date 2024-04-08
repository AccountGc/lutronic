package com.e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.e3ps.change.CrToEcprLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.mail.MailHtmlContentTemplate;
import com.e3ps.common.mail.MailUtil;
import com.e3ps.common.mail.MailUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.org.MailUser;
import com.e3ps.org.MailWTobjectLink;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.workspace.AppPerLink;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.ApprovalUserLine;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.PerWorkDataLink;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.WorkDataMailUserLink;
import com.e3ps.workspace.dto.WorkDataDTO;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

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
	public void register(WorkData data, Persistable per, String description, ArrayList<Map<String, String>> agreeRows,
			ArrayList<Map<String, String>> approvalRows, ArrayList<Map<String, String>> receiveRows) throws Exception {
		boolean isAgree = !agreeRows.isEmpty();

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
		if (isAgree) {
			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_AGREE);
		} else {
			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_APPROVING);
		}
		master = (ApprovalMaster) PersistenceHelper.manager.save(master);

		master = (ApprovalMaster) PersistenceHelper.manager.refresh(master);
		data = (WorkData) PersistenceHelper.manager.refresh(data);

		// 마스터 세팅
		data.setAppMaster(master);
		PersistenceHelper.manager.modify(data);

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
		if (StringUtil.checkString(description)) {
			startLine.setDescription(description);
		} else {
			startLine.setDescription(ownership.getOwner().getFullName() + " 사용자가 결재를 기안하였습니다.");
		}
		startLine.setState(WorkspaceHelper.STATE_SUBMIT_COMPLETE);
		startLine.setCompleteTime(startTime);

		PersistenceHelper.manager.save(startLine);

		int sort = 0;
		if (isAgree) {
//			sort = 1;
			for (Map<String, String> agree : agreeRows) {
				String woid = agree.get("woid");
				WTUser wtuser = (WTUser) CommonUtil.getObject(woid);
				// 합의 라인 생성
				ApprovalLine agreeLine = ApprovalLine.newApprovalLine();
				agreeLine.setName(name);
				agreeLine.setOwnership(Ownership.newOwnership(wtuser));
				agreeLine.setMaster(master);
				agreeLine.setReads(false);
				agreeLine.setSort(0);
				agreeLine.setStartTime(startTime);
				agreeLine.setType(WorkspaceHelper.AGREE_LINE);
				agreeLine.setRole(WorkspaceHelper.WORKING_AGREE);
				agreeLine.setDescription(null);
				agreeLine.setCompleteTime(null);
				agreeLine.setState(WorkspaceHelper.STATE_AGREE_START);
				PersistenceHelper.manager.save(agreeLine);

				// 모든 합의자에게 메일 전송
				MailUtils.manager.sendAgreeMailMethod(per, agreeLine);
			}
		}

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

			// 합의가 있을 경우
			if (isAgree) {
				approvalLine.setStartTime(null);
				approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
				approvalLine.setCompleteTime(null);
			} else {
				// 합의 없을경우
				if (sort == 0) {
					approvalLine.setStartTime(startTime);
					approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
					approvalLine.setCompleteTime(null);
					// 합의 없을 경우 메일 보낸다..
				} else {
					approvalLine.setStartTime(null);
					approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
					approvalLine.setCompleteTime(null);
				}
			}
			PersistenceHelper.manager.save(approvalLine);

			if (!isAgree && sort == 0) {
				MailUtils.manager.sendApprovalMailMethod(per, approvalLine);
			}

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
//			receiveLine.setStartTime(startTime);
			receiveLine.setType(WorkspaceHelper.RECEIVE_LINE);
			receiveLine.setRole(WorkspaceHelper.WORKING_RECEIVE);
			receiveLine.setDescription(null);
			receiveLine.setCompleteTime(null);
			// 결재 완료후 볼수 있어야 한다.
			receiveLine.setState(WorkspaceHelper.STATE_RECEIVE_READY);
			PersistenceHelper.manager.save(receiveLine);
		}
		afterRegisterAction(master);
	}

	/**
	 * 결재 등록시 이뤄지는 작업 상태값 변경
	 */
	private void afterRegisterAction(ApprovalMaster master) throws Exception {
		master = (ApprovalMaster) PersistenceHelper.manager.refresh(master);
		Persistable per = master.getPersist();

		if (per instanceof LifeCycleManaged) {
			LifeCycleManaged lcm = (LifeCycleManaged) per;
			// 상태값 변경
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) lcm, State.toState("APPROVING"));

			if (per instanceof ECPRRequest) {
//				ECPRRequest ecpr = (ECPRRequest) per;
//				QueryResult qr = PersistenceHelper.manager.navigate(ecpr, "cr", CrToEcprLink.class);
//				while (qr.hasMoreElements()) {
//					EChangeRequest cr = (EChangeRequest) qr.nextElement();
//					LifeCycleHelper.service.setLifeCycleState(cr, State.toState("APPROVAL_ECPR"));
//				}
			}

//			if (lcm instanceof EChangeOrder) {
//				// EO..
//
//			} else if (lcm instanceof WTDocument) {
//				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) lcm, State.toState("APPROVING"));
//			} else if (lcm instanceof EChangeRequest) {
//				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) lcm, State.toState("APPROVING"));
//			} else if (lcm instanceof AsmApproval) {
//				AsmApproval asm = (AsmApproval) per;
//				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) lcm, State.toState("APPROVING"));
//			}
			if (lcm instanceof AsmApproval) {
				AsmApproval asm = (AsmApproval) per;
				QueryResult result = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class);
				while (result.hasMoreElements()) {
					Persistable persistable = (Persistable) result.nextElement();
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) persistable,
							State.toState("APPROVING"));
				}
			}
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
				ApprovalMaster master = (ApprovalMaster) CommonUtil.getObject(oid);
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
		String oid = (String) params.get("oid");
		String tapOid = (String) params.get("tapOid");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);

			if (!StringUtil.checkString(description)) {
				description = "승인합니다.";
			}

			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setState(WorkspaceHelper.STATE_APPROVAL_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();
			Persistable per = master.getPersist();

			ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
			for (ApprovalLine approvalLine : approvalLines) {
				int sort = approvalLine.getSort();
				approvalLine.setSort(sort - 1);
				approvalLine = (ApprovalLine) PersistenceHelper.manager.modify(approvalLine);
			}

			for (ApprovalLine approvalLine : approvalLines) {
				int sort = approvalLine.getSort();
				if (sort == 0) {
					approvalLine.setStartTime(completeTime);
					approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
					approvalLine = (ApprovalLine) PersistenceHelper.manager.modify(approvalLine);
					MailUtils.manager.sendApprovalMailMethod(per, approvalLine);

				}
			}

			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_APPROVING);
			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

			boolean isEndApprovalLine = WorkspaceHelper.manager.isEndApprovalLine(master, 0);
			if (isEndApprovalLine) {

				// 모든 수신라인 상태 변경
				ArrayList<ApprovalLine> ll = WorkspaceHelper.manager.getReceiveLines(master);
				for (ApprovalLine rLine : ll) {
					rLine.setStartTime(new Timestamp(new Date().getTime()));
					rLine.setState(WorkspaceHelper.STATE_RECEIVE_START);
					PersistenceHelper.manager.modify(rLine);
					// 모든 수신라인에 메일 전송
					MailUtils.manager.sendReceiveMailMethod(per, rLine);
				}

				master.setCompleteTime(completeTime);
				master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_COMPLETE);
				PersistenceHelper.manager.modify(master);

				// 외부 메일 전송
				MailUtils.manager.sendExternalMailMethod(per);
				// 기안자 메일 전송

				ApprovalLine submit = WorkspaceHelper.manager.getSubmitLine(master);
				MailUtils.manager.sendSubmitterMailMethod(per, submit);

				afterApprovalAction(per, tapOid);
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

	/**
	 * 최종 결재 이후 시작할 함수
	 */
	private void afterApprovalAction(Persistable per, String tapOid) throws Exception {

		// 객체 상태 승인됨으로 변경한다.
		if (per instanceof LifeCycleManaged) {
			State state = State.toState("APPROVED");
			per = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, state);
			per = PersistenceHelper.manager.refresh(per);

			if (per instanceof WTDocument) {
				WTDocument doc = (WTDocument) per;
				String docType = doc.getDocType().toString();
				// 금형 아닐경우
				if (!"$$MMDocument".equals(docType) && !"$$ROHS".equals(docType)) {
					System.out.println("표지 생성한다.");
					DocumentHelper.service.createCover(doc);
//					DocumentHelper.manager.createCoverMethod(per);
				}
			}

			// 일괄결재일경우 대상도.. 변경
			if (per instanceof AsmApproval) {
				AsmApproval asm = (AsmApproval) per;
				QueryResult result = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class);
				while (result.hasMoreElements()) {
					Persistable persistable = (Persistable) result.nextElement();
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) persistable,
							State.toState("APPROVED"));
				}
			}

			// aspose pdf 변경 3가지가..
			if (per instanceof ECPRRequest) {
//				System.out.println("ECPR PDF 변경!");
				ECPRRequest ecpr = (ECPRRequest) per;
				ecpr.setApproveDate(new Timestamp(new Date().getTime()).toString().substring(0, 10));
				ecpr = (ECPRRequest) PersistenceHelper.manager.modify(ecpr);
			}

			if (per instanceof ECRMRequest) {
//				System.out.println("ECRM PDF 변경!");
				ECRMRequest ecrm = (ECRMRequest) per;
				ecrm.setApproveDate(new Timestamp(new Date().getTime()).toString().substring(0, 10));
				ecrm = (ECRMRequest) PersistenceHelper.manager.modify(ecrm);
			}

			if (per instanceof EChangeRequest) {
				EChangeRequest cr = (EChangeRequest) per;
				cr.setApproveDate(new Timestamp(new Date().getTime()).toString().substring(0, 10));
				cr = (EChangeRequest) PersistenceHelper.manager.modify(cr);
			}

			// EO/ ECO
			if (per instanceof EChangeOrder) {
				EChangeOrder e = (EChangeOrder) per;
				// 승인일.. 오케이하기

				Timestamp today = new Timestamp(new Date().getTime());
				e.setEoApproveDate(today.toString().substring(0, 10));
				e = (EChangeOrder) PersistenceHelper.manager.modify(e);

				String t = e.getEoType();
				Hashtable<String, String> hash = new Hashtable<>();
				hash.put("oid", e.getPersistInfo().getObjectIdentifier().getStringValue());
				if ("CHANGE".equals(t)) {
					System.out.println("ECO 결재 완료");
//					EcoHelper.manager.postAfterAction(hash);
					EChangeUtils.afterEcoAction(hash);
					// EO
				} else {
					Map<String, Object> s = EoHelper.manager.checkCheckout(e);
					if (!(boolean) s.get("result")) {
						throw new Exception((String) s.get("msg"));
					}

					System.out.println("EO 결재 완료");
//					EoHelper.manager.postAfterAction(e);
					EChangeUtils.afterEoAction(hash);
				}
			}
		}
	}

	@Override
	public void _reject(Map<String, String> params) throws Exception {
		String oid = params.get("oid");
		String description = params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
			ApprovalMaster m = line.getMaster();

			if (!StringUtil.checkString(description)) {
				WTUser sessionUser = CommonUtil.sessionUser();
				description = sessionUser.getFullName() + " 사용자의 합의반려로 인해 모든 결재가 반려 처리 되었습니다.";
			}
			line.setDescription(description);
			line.setState(WorkspaceHelper.STATE_APPROVAL_REJECT);
			line.setCompleteTime(new Timestamp(new Date().getTime()));
			PersistenceHelper.manager.modify(line);

			// 기안라인은 제외한다?
//			ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m, true);
//			for (ApprovalLine l : list) {
//				String t = l.getType();
//				// 합의라인
//				if (t.equals(WorkspaceHelper.AGREE_LINE)) {
//					l.setState(WorkspaceHelper.STATE_AGREE_REJECT);
//					// 결재라인
//				} else if (t.equals(WorkspaceHelper.APPROVAL_LINE)) {
//					l.setState(WorkspaceHelper.STATE_APPROVAL_REJECT);
//					// 수신라인
//				} else if (t.equals(WorkspaceHelper.RECEIVE_LINE)) {
//					l.setState(WorkspaceHelper.STATE_RECEIVE_REJECT);
//				}
//				l.setCompleteTime(new Timestamp(new Date().getTime()));
//				l.setDescription(description);
//
//				PersistenceHelper.manager.modify(l);
//			}

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

			if (lcm instanceof ECPRRequest) {
				ECPRRequest ecpr = (ECPRRequest) lcm;
				QueryResult qr = PersistenceHelper.manager.navigate(ecpr, "cr", CrToEcprLink.class);
				while (qr.hasMoreElements()) {
					EChangeRequest cr = (EChangeRequest) qr.nextElement();
					LifeCycleHelper.service.setLifeCycleState(cr, State.toState("RETURN_ECPR"));
				}
			}

			if (lcm instanceof AsmApproval) {
				AsmApproval asm = (AsmApproval) per;
				QueryResult result = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class);
				while (result.hasMoreElements()) {
					Persistable persistable = (Persistable) result.nextElement();
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) persistable, State.toState("RETURN"));
				}
			}
		}
	}

	@Override
	public void read(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
			line.setReads(true);
			PersistenceHelper.manager.modify(line);

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
	public void _receive(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = (String) params.get("oid");
			String description = (String) params.get("description");
			if (!StringUtil.checkString(description)) {
				description = "수신확인합니다.";
			}
			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
			line.setDescription(description);
			line.setState(WorkspaceHelper.STATE_RECEIVE_COMPLETE);
			line.setCompleteTime(new Timestamp(new Date().getTime()));
			PersistenceHelper.manager.modify(line);

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
	public void receives(Map<String, ArrayList<Map<String, String>>> params) throws Exception {
		ArrayList<Map<String, String>> list = params.get("list");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, String> map : list) {
				String oid = map.get("oid");
				ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
				line.setDescription("수신확인합니다");
				line.setState(WorkspaceHelper.STATE_RECEIVE_COMPLETE);
				line.setCompleteTime(new Timestamp(new Date().getTime()));
				PersistenceHelper.manager.modify(line);
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
	public void delegate(Map<String, String> params) throws Exception {
		String oid = params.get("oid"); // 결재라인
		String reassignUserOid = params.get("reassignUserOid");
		String tapOid = params.get("tapOid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = (WTUser) CommonUtil.getObject(reassignUserOid);
			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
			line.setOwnership(Ownership.newOwnership(user));
			PersistenceHelper.manager.modify(line);

			// 메일 처리

			// 발신인
			WTUser adminUser = (WTUser) SessionHelper.manager.getAdministrator();
			// 수신인
			Map<String, Object> toPerson = new HashMap<String, Object>();
			toPerson.put(user.getEMail(), user.getFullName());
			// 제목
			String subject = "";
			String[] processTarget = new String[3];
			// 내용
			String content = "";
			Hashtable chash = new Hashtable();
			String creatorName = "";
			String description = "";
			String deadlineStr = "";

			Persistable per = CommonUtil.getObject(tapOid);
			if (per instanceof WTDocument) {
				WTDocument doc = (WTDocument) CommonUtil.getObject(tapOid);
				creatorName = doc.getCreatorFullName();
				description = StringUtil.checkNull(doc.getDescription());
				processTarget[0] = null;
				processTarget[1] = doc.getNumber();
				processTarget[2] = doc.getName();
			} else if (per instanceof WTPart) {
				WTPart part = (WTPart) CommonUtil.getObject(tapOid);
				creatorName = part.getCreatorFullName();
				processTarget[0] = null;
				processTarget[1] = part.getNumber();
				processTarget[2] = part.getName();
			} else if (per instanceof EChangeOrder) {
				EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(tapOid);
				creatorName = eo.getCreatorFullName();
				description = StringUtil.checkNull(eo.getEoCommentA());
				processTarget[0] = null;
				processTarget[1] = eo.getEoNumber();
				processTarget[2] = eo.getEoName();
			} else if (per instanceof EChangeRequest) {
				EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(tapOid);
				creatorName = cr.getCreatorFullName();
				description = StringUtil.checkNull(cr.getEoCommentB());
				processTarget[0] = null;
				processTarget[1] = cr.getEoNumber();
				processTarget[2] = cr.getEoName();
			} else if (per instanceof ECPRRequest) {
				ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(tapOid);
				creatorName = ecpr.getCreatorFullName();
				description = StringUtil.checkNull(ecpr.getEoCommentB());
				processTarget[0] = null;
				processTarget[1] = ecpr.getEoNumber();
				processTarget[2] = ecpr.getEoName();
			} else if (per instanceof ROHSMaterial) {
				ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(tapOid);
				creatorName = rohs.getCreatorFullName();
				description = StringUtil.checkNull(rohs.getDescription());
				processTarget[0] = null;
				processTarget[1] = rohs.getNumber();
				processTarget[2] = rohs.getName();
			}

			String viewString = processTarget[1] + " (" + processTarget[2] + ")";
			String workName = line.getType();
			if (null != processTarget[0] && processTarget[0].length() > 0) {
				subject = processTarget[0] + "의 " + workName + "요청 알림 메일입니다.";
				chash.put("gubun", processTarget[0]);
			} else {
				subject = processTarget[1] + "의 " + workName + "요청 알림 메일입니다.";
				chash.put("gubun", processTarget[1]);
			}

			chash.put("viewString", viewString);
			chash.put("workName", workName);
			chash.put("deadlineStr", deadlineStr);
			chash.put("creatorName", creatorName);
			chash.put("description", description);

			MailHtmlContentTemplate mhct = MailHtmlContentTemplate.getInstance();
			content = mhct.htmlContent(chash, "mail_notice.html");
			Hashtable hash = new Hashtable();

			hash.put("FROM", adminUser);
			hash.put("TO", toPerson);
			hash.put("SUBJECT", subject);
			hash.put("CONTENT", content);

			boolean mmmm = MailUtil.manager.sendMail(hash);

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
	public WorkItem getWorkItem(Persistable per) throws WTException {
		QueryResult qr = WorkflowHelper.service.getWorkItems(per);
		if (qr.hasMoreElements()) {
			return (WorkItem) qr.nextElement();
		}
		return null;
	}

	@Override
	public void _agree(Map<String, String> params) throws Exception {
		String oid = (String) params.get("oid");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();
			// 합의시 상태값 합의 완료로 변경
			// 모든 합의가 끝나면 결재를 시작하는데 결재는 직렬로 순서대로 진행
			Timestamp completeTime = new Timestamp(new Date().getTime());
			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);

			if (!StringUtil.checkString(description)) {
				description = "합의합니다.";
			}

			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setState(WorkspaceHelper.STATE_AGREE_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();
			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_AGREE);
			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);
			Persistable per = master.getPersist();
			boolean isAgreeApprovalLine = WorkspaceHelper.manager.isAgreeApprovalLine(master);
			if (!isAgreeApprovalLine) { // 합의중 없으면
				ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
				for (ApprovalLine approvalLine : approvalLines) {
					int sort = approvalLine.getSort();
					if (sort == 0) {
						// 결재라인 메일 전송
						approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
						approvalLine.setStartTime(completeTime);
						approvalLine = (ApprovalLine) PersistenceHelper.manager.modify(approvalLine);
						MailUtils.manager.sendApprovalMailMethod(per, approvalLine);
					}
				}
				master = (ApprovalMaster) PersistenceHelper.manager.refresh(master);
				master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_APPROVING);
				master = (ApprovalMaster) PersistenceHelper.manager.modify(master);
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
	public Persistable removeHistory(Persistable per) throws Exception {
		ApprovalMaster m = WorkspaceHelper.manager.getMaster(per);
		if (m != null) {
			ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m);
			for (ApprovalLine line : list) {
				PersistenceHelper.manager.delete(line);
			}
		}
		PersistenceHelper.manager.delete(m);
		return PersistenceHelper.manager.refresh(per);
	}

	@Override
	public void stand(Persistable per) throws Exception {
		ApprovalMaster m = WorkspaceHelper.manager.getMaster(per);
		if (m != null) {
			// 기안 라인 제외
			// 시작일 전부 NULL처리
			ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m, true);
			for (ApprovalLine line : list) {
				String type = line.getType();
				if (type.equals(WorkspaceHelper.APPROVAL_LINE)) {
					line.setState(WorkspaceHelper.STATE_APPROVAL_READY);
				} else if (type.equals(WorkspaceHelper.RECEIVE_LINE)) {
					line.setState(WorkspaceHelper.STATE_RECEIVE_READY);
				} else if (type.equals(WorkspaceHelper.AGREE_LINE)) {
					line.setState(WorkspaceHelper.STATE_AGREE_READY);
				}
				line.setStartTime(null);
				PersistenceHelper.manager.modify(line);
			}
		}
	}

	@Override
	public void start(Persistable per) throws Exception {
		ApprovalMaster m = WorkspaceHelper.manager.getMaster(per);

		if (m != null) {

			// 합의 라인 부터
			ArrayList<ApprovalLine> agrees = WorkspaceHelper.manager.getAgreeLines(m);
			if (agrees.size() > 0) {
				for (ApprovalLine agree : agrees) {
					agree.setState(WorkspaceHelper.STATE_AGREE_START);
					agree.setStartTime(new Timestamp(new Date().getTime()));
					PersistenceHelper.manager.modify(agree);
				}
			} else {
				// 수신은 아무 변화 업는것으로..
				ArrayList<ApprovalLine> approvals = WorkspaceHelper.manager.getApprovalLines(m);
				for (ApprovalLine approval : approvals) {
					approval.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
					approval.setStartTime(new Timestamp(new Date().getTime()));
					PersistenceHelper.manager.modify(approval);
				}
			}
		}
	}

	@Override
	public void _unagree(Map<String, String> params) throws Exception {
		String oid = params.get("oid");
		String description = params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			if (!StringUtil.checkString(description)) {
				WTUser sessionUser = CommonUtil.sessionUser();
				description = sessionUser.getFullName() + " 사용자의 합의반려로 인해 모든 결재가 반려 처리 되었습니다.";
			}

			ApprovalLine line = (ApprovalLine) CommonUtil.getObject(oid);
			line.setDescription(description);
			PersistenceHelper.manager.modify(line);

			ApprovalMaster m = line.getMaster();
			// 기안라인 제외
			ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m, true);
			for (ApprovalLine l : list) {
				String t = l.getType();
				// 합의라인
				if (t.equals(WorkspaceHelper.AGREE_LINE)) {
					l.setState(WorkspaceHelper.STATE_AGREE_REJECT);
					// 결재라인
				} else if (t.equals(WorkspaceHelper.APPROVAL_LINE)) {
					l.setState(WorkspaceHelper.STATE_APPROVAL_REJECT);
					// 수신라인
				} else if (t.equals(WorkspaceHelper.RECEIVE_LINE)) {
					l.setState(WorkspaceHelper.STATE_RECEIVE_REJECT);
				}
				l.setCompleteTime(new Timestamp(new Date().getTime()));
//				l.setDescription(description);

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

	@Override
	public void withdraw(String oid, String remove) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean _remove = Boolean.parseBoolean(remove);

			Persistable per = CommonUtil.getObject(oid);
			// 안남기면 모두 삭제한다..
			if (_remove) {

				if (!(per instanceof AsmApproval)) {
					QueryResult result = PersistenceHelper.manager.navigate(per, "approval", AppPerLink.class);
					if (result.size() > 0) {
						throw new Exception("일괄결재로 진행중인 결재입니다.\n결재 회수는 일괄결재를 선택해서 진행하세요.");
					}
				}

				ApprovalMaster m = WorkspaceHelper.manager.getMaster(per);

				if (m != null) {
					ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m);

					for (ApprovalLine line : list) {
						PersistenceHelper.manager.delete(line);
					}
					PersistenceHelper.manager.delete(m);
				}

				QueryResult qr = PersistenceHelper.manager.navigate((WTObject) per, "user", MailWTobjectLink.class,
						false);
				while (qr.hasMoreElements()) {
					MailWTobjectLink link = (MailWTobjectLink) qr.nextElement();
					PersistenceHelper.manager.delete(link);
				}

				qr.reset();
				qr = PersistenceHelper.manager.navigate(per, "workData", PerWorkDataLink.class);
				if (qr.hasMoreElements()) {
					WorkData d = (WorkData) qr.nextElement();
					PersistenceHelper.manager.delete(d);
				}
				// 바로 다시 결재선 지정 모드..
				WorkDataHelper.service.create(per);
			} else {

				// 일괄결재 체크
				if (!(per instanceof AsmApproval)) {
					QueryResult result = PersistenceHelper.manager.navigate(per, "approval", AppPerLink.class);
					if (result.size() > 0) {
						throw new Exception("일괄결재로 진행중인 결재입니다.\n결재 회수는 일괄결재를 선택해서 진행하세요.");
					}
				}

				QueryResult qr = PersistenceHelper.manager.navigate(per, "workData", PerWorkDataLink.class);
				WorkData d = null;
				if (qr.hasMoreElements()) {
					d = (WorkData) qr.nextElement();
					d.setProcess(false); // 결재선 지정 상태로 돌린다.
					PersistenceHelper.manager.modify(d);
				}

				d = (WorkData) PersistenceHelper.manager.refresh(d);

				ApprovalMaster m = d.getAppMaster();
				// 진행 되었던 모든 결재 정보를 초기화를 한다.
				resetLines(d);

				// 상태값 결재선 지정으로 변경
				if (per instanceof LifeCycleManaged) {
					LifeCycleManaged lcm = (LifeCycleManaged) per;
					LifeCycleHelper.service.setLifeCycleState(lcm, State.toState("LINE_REGISTER"));
				}

				if (per instanceof AsmApproval) {
					AsmApproval asm = (AsmApproval) per;
					QueryResult rs = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class);
					while (rs.hasMoreElements()) {
						Persistable persistable = (Persistable) rs.nextElement();
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) persistable,
								State.toState("LINE_REGISTER"));
					}
				}

				// 마스터 상태값 변경
//				ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLine(m);
//				m.setStartTime(null);
//				if (agreeLines.size() > 0) {
//					m.setState(WorkspaceHelper.STATE_AGREE_READY);
//				} else {
//					m.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
//				}
//				PersistenceHelper.manager.modify(m);
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
	public void resetLines(WorkData data) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ApprovalMaster m = data.getAppMaster();
			if (m != null) {
				ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m);
				for (ApprovalLine line : list) {
					// 완료가 된 결재는 제외 시킨다.
					String t = line.getType();
					// 기안라인은 되돌린다..
//					if (t.equals(WorkspaceHelper.SUBMIT_LINE)) {
//						line.setState(WorkspaceHelper.STATE_SUBMIT_READY);
//						line.setDescription(null);
//						line.setStartTime(null);
//						line.setCompleteTime(null);
//						line.setReads(false);
//					}

					if (line.getCompleteTime() == null) {
						line.setDescription(null);
						line.setStartTime(null);
						line.setCompleteTime(null);
						line.setReads(false);
						// 기안
						if (t.equals(WorkspaceHelper.AGREE_LINE)) {
							line.setState(WorkspaceHelper.STATE_AGREE_READY);
							// 결재
						} else if (t.equals(WorkspaceHelper.APPROVAL_LINE)) {
							// 수신
							line.setState(WorkspaceHelper.STATE_APPROVAL_READY);
						} else if (t.equals(WorkspaceHelper.RECEIVE_LINE)) {
							// 수신값 변경 없음
//							line.setState(WorkspaceHelper.STATE_RECEIVE_READY);
						}
						PersistenceHelper.manager.modify(line);
					}
				}
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
	public void deleteAllLines(Persistable per) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ApprovalMaster m = WorkspaceHelper.manager.getMaster(per);
			if (m != null) {
				ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m);
				for (ApprovalLine line : list) {
					PersistenceHelper.manager.delete(line);
				}
				PersistenceHelper.manager.delete(m);
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
	public void deleteAllLines(ApprovalMaster m) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(m);
			for (ApprovalLine line : list) {
				PersistenceHelper.manager.delete(line);
			}
			PersistenceHelper.manager.delete(m);

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
	public void mailSave(Map<String, Object> params) throws Exception {
		ArrayList<String> data = (ArrayList<String>) params.get("data");
		String oid = (String) params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkData wd = (WorkData) CommonUtil.getObject(oid);

			// 삭제 처리 제외

//			QueryResult qr = PersistenceHelper.manager.navigate(wd, "mailUser", WorkDataMailUserLink.class, false);
//			while (qr.hasMoreElements()) {
//				WorkDataMailUserLink link = (WorkDataMailUserLink) qr.nextElement();
//				PersistenceHelper.manager.delete(link);
//			}

			for (String s : data) {
				MailUser user = (MailUser) CommonUtil.getObject(s);
				MailWTobjectLink link = MailWTobjectLink.newMailWTobjectLink((WTObject) wd.getPer(), user);
				PersistenceHelper.manager.save(link);
				// 메일유저 이력 연결
				WorkDataMailUserLink mLink = WorkDataMailUserLink.newWorkDataMailUserLink(wd, user);
				PersistenceHelper.manager.save(mLink);
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
	public void removeMail(Map<String, Object> params) throws Exception {
		ArrayList<String> data = (ArrayList<String>) params.get("data");
		String oid = (String) params.get("oid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkData wd = (WorkData) CommonUtil.getObject(oid);
			Persistable per = wd.getPer();
			QueryResult qr = PersistenceHelper.manager.navigate((WTObject) per, "user", MailWTobjectLink.class, false);
			while (qr.hasMoreElements()) {
				MailWTobjectLink link = (MailWTobjectLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			for (String s : data) {
				WorkDataMailUserLink link = (WorkDataMailUserLink) CommonUtil.getObject(s);
				PersistenceHelper.manager.delete(link);
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
	public void reworkSubmit(WorkDataDTO dto, ApprovalMaster appMaster) throws Exception {
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String name = appMaster.getName();

			Timestamp startTime = new Timestamp(new Date().getTime());
			ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLine(appMaster);
			appMaster.setStartTime(startTime);
			boolean isAgree = agreeLines.size() > 0;
			if (isAgree) {
				appMaster.setState(WorkspaceHelper.STATE_AGREE_READY);
			} else {
				appMaster.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
			}
			appMaster = (ApprovalMaster) PersistenceHelper.manager.modify(appMaster);

			// 기안 라인 가져오기 - 변경없음...

			// 합의 라인 가져오기
			for (Map<String, String> agreeMap : agreeRows) {
				String oid = agreeMap.get("oid");
				System.out.println("oid=" + oid);
				String gridState = agreeMap.get("gridState");
				if (StringUtil.checkString(oid)) {
					// 기존껀 수정
					ApprovalLine agreeLine = (ApprovalLine) CommonUtil.getObject(oid);
					if ("removed".equals(gridState)) {
						PersistenceHelper.manager.delete(agreeLine);
					} else {
						// 완료가 안되것들만 일단 수정을 한다.
						if (agreeLine.getCompleteTime() == null) {
							agreeLine.setStartTime(startTime);
							agreeLine.setState(WorkspaceHelper.STATE_AGREE_START);
							PersistenceHelper.manager.modify(agreeLine);
						}
					}
				} else {
					// 새로온거는 생성
					System.out.println("새로운거 생성 안함??");
					String woid = agreeMap.get("woid");
					WTUser wtuser = (WTUser) CommonUtil.getObject(woid);
					ApprovalLine agreeLine = ApprovalLine.newApprovalLine();
					agreeLine.setName(name);
					agreeLine.setOwnership(Ownership.newOwnership(wtuser));
					agreeLine.setMaster(appMaster);
					agreeLine.setReads(false);
					agreeLine.setSort(0);
					agreeLine.setStartTime(startTime);
					agreeLine.setType(WorkspaceHelper.AGREE_LINE);
					agreeLine.setRole(WorkspaceHelper.WORKING_AGREE);
					agreeLine.setDescription(null);
					agreeLine.setCompleteTime(null);
					agreeLine.setState(WorkspaceHelper.STATE_AGREE_START);
					PersistenceHelper.manager.save(agreeLine);
					System.out.println("agreeLine=" + agreeLine.getState());
				}
			}

			int sort = 0;

			// 결재 라인 가져오기
			for (Map<String, String> approvalMap : approvalRows) {
				String oid = approvalMap.get("oid");
				String gridState = approvalMap.get("gridState");
				if (StringUtil.checkString(oid)) {
					ApprovalLine approvalLine = (ApprovalLine) CommonUtil.getObject(oid);
					if ("removed".equals(gridState)) {
						PersistenceHelper.manager.delete(approvalLine);
					} else {

						if (approvalLine.getCompleteTime() == null) {
							approvalLine.setSort(sort);
							if (isAgree) {
								approvalLine.setStartTime(null);
								approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
								approvalLine.setCompleteTime(null);
							} else {
								// 합의 없을경우
								if (sort == 0) {
									approvalLine.setStartTime(startTime);
									approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
									approvalLine.setCompleteTime(null);
									// 합의 없을 경우 메일 보낸다..
								} else {
									approvalLine.setStartTime(null);
									approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
									approvalLine.setCompleteTime(null);
								}
							}
							PersistenceHelper.manager.modify(approvalLine);
						}
					}
				} else {
					String woid = approvalMap.get("woid");
					WTUser wtuser = (WTUser) CommonUtil.getObject(woid);
					// 결재 라인 생성
					ApprovalLine approvalLine = ApprovalLine.newApprovalLine();
					approvalLine.setName(name);
					approvalLine.setOwnership(Ownership.newOwnership(wtuser));
					approvalLine.setMaster(appMaster);
					approvalLine.setReads(false);
					approvalLine.setSort(sort);
					approvalLine.setType(WorkspaceHelper.APPROVAL_LINE);
					approvalLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
					approvalLine.setDescription(null);

					// 합의가 있을 경우
					if (isAgree) {
						approvalLine.setStartTime(null);
						approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
						approvalLine.setCompleteTime(null);
					} else {
						// 합의 없을경우
						if (sort == 0) {
							approvalLine.setStartTime(startTime);
							approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
							approvalLine.setCompleteTime(null);
							// 합의 없을 경우 메일 보낸다..
						} else {
							approvalLine.setStartTime(null);
							approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
							approvalLine.setCompleteTime(null);
						}
					}
					PersistenceHelper.manager.save(approvalLine);
				}
				sort++;
			}

			// 수신라인 가져오기..
			for (Map<String, String> receiveMap : receiveRows) {
				String oid = receiveMap.get("oid");
				String gridState = receiveMap.get("gridState");
				if (StringUtil.checkString(oid)) {
					ApprovalLine receiveLine = (ApprovalLine) CommonUtil.getObject(oid);
					if ("removed".equals(gridState)) {
						PersistenceHelper.manager.delete(receiveLine);
					}
				} else {
					// 완료전에 회수 되어지기떄문에 상태값 변경X
					String woid = receiveMap.get("woid");
					WTUser wtuser = (WTUser) CommonUtil.getObject(woid);
					ApprovalLine receiveLine = ApprovalLine.newApprovalLine();
					receiveLine.setName(name);
					receiveLine.setOwnership(Ownership.newOwnership(wtuser));
					receiveLine.setMaster(appMaster);
					receiveLine.setReads(false);
					receiveLine.setSort(0);
//					receiveLine.setStartTime(startTime);
					receiveLine.setType(WorkspaceHelper.RECEIVE_LINE);
					receiveLine.setRole(WorkspaceHelper.WORKING_RECEIVE);
					receiveLine.setDescription(null);
					receiveLine.setCompleteTime(null);
					receiveLine.setState(WorkspaceHelper.STATE_RECEIVE_READY);
					PersistenceHelper.manager.save(receiveLine);
				}
			}

			afterRegisterAction(appMaster);

			boolean isAgreeApprovalLine = WorkspaceHelper.manager.isAgreeApprovalLine(appMaster);
			// 합의가 모두 끝난 상태..
			if (!isAgreeApprovalLine) {
				ArrayList<ApprovalLine> aList = WorkspaceHelper.manager.getApprovalLines(appMaster);
				for (ApprovalLine line : aList) {

					if (line.getCompleteTime() != null) {
						int idx = line.getSort();
						ApprovalLine nextLine = WorkspaceHelper.manager.getNextAppLine(appMaster, idx + 2);
						System.out.println("nextLine=" + nextLine);
						if (nextLine != null) {
							if (nextLine.getCompleteTime() != null) {
								continue;
							} else {
								nextLine.setStartTime(new Timestamp(new Date().getTime()));
								nextLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
								nextLine = (ApprovalLine) PersistenceHelper.manager.modify(nextLine);
								break;
							}
						}
					}
				}
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
	public void removeLine(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) params.get("data");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> m : data) {
				String oid = (String) m.get("oid");
				ApprovalLine l = (ApprovalLine) CommonUtil.getObject(oid);
				PersistenceHelper.manager.delete(l);
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