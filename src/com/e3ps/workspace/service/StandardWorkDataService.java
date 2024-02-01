package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.mail.MailUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.workspace.AppPerLink;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.dto.WorkDataDTO;
import com.ptc.wvs.service.VisualizationService;

import wt.doc.StandardWTDocumentService;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardWorkDataService extends StandardManager implements WorkDataService {

	public static StandardWorkDataService newStandardWorkDataService() throws WTException {
		StandardWorkDataService instance = new StandardWorkDataService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Persistable per) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = CommonUtil.sessionOwner();

			WorkData data = WorkData.newWorkData();
			data.setPer(per);
			if (per instanceof EChangeOrder) {
				EChangeOrder e = (EChangeOrder) per;
				Ownership owner = Ownership.newOwnership(e.getCreator());
				data.setOwnership(owner);
			} else {
				data.setOwnership(ownership);
			}

			PersistenceHelper.manager.save(data);

			if (per instanceof LifeCycleManaged) {
				LifeCycleManaged lcm = (LifeCycleManaged) per;

				LifeCycleTemplate lct = (LifeCycleTemplate) lcm.getLifeCycleTemplate().getObject();
				if (!lct.isLatestIteration()) {
					lcm = (LifeCycleManaged) LifeCycleHelper.service.reassign(lcm, LifeCycleHelper.service
							.getLifeCycleTemplateReference(lcm.getLifeCycleName(), WCUtil.getWTContainerRef())); // Lifecycle
					lcm = (LifeCycleManaged) PersistenceHelper.manager.refresh(lcm);
				}
				LifeCycleHelper.service.setLifeCycleState(lcm, State.toState("LINE_REGISTER"));
			}

			// 일괄겨재일 경우 대상들 결재선 지정상태로만 변경한다
			if (per instanceof AsmApproval) {
				AsmApproval asm = (AsmApproval) per;
				QueryResult rs = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class);
				while (rs.hasMoreElements()) {
					Persistable persistable = (Persistable) rs.nextElement();
					LifeCycleManaged lcm = (LifeCycleManaged) persistable;
					LifeCycleTemplate lct = (LifeCycleTemplate) lcm.getLifeCycleTemplate().getObject();
					if (!lct.isLatestIteration()) {
						lcm = (LifeCycleManaged) LifeCycleHelper.service.reassign(lcm, LifeCycleHelper.service
								.getLifeCycleTemplateReference(lcm.getLifeCycleName(), WCUtil.getWTContainerRef())); // Lifecycle
						lcm = (LifeCycleManaged) PersistenceHelper.manager.refresh(lcm);
					}
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) persistable,
							State.toState("LINE_REGISTER"));
				}
			}

			// 메일발송하기!
			MailUtils.manager.sendWorkDataMailMethod(per);

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
	public void _submit(WorkDataDTO dto) throws Exception {
		String oid = dto.getOid();
		String description = dto.getDescription();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<Map<String, String>> external = dto.getExternal();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkData data = (WorkData) CommonUtil.getObject(oid);
			data.setProcess(true);
			PersistenceHelper.manager.modify(data);

			// 기존 연결된 .. 결재랑 외부 메일 있는지 확인한다
			// 외부 메일 연결
//			MailUserHelper.service.saveLink(data, data.getPer(), external);

			// 결재 필수..
			// 결재라인 + 워크데이터 연결

			ApprovalMaster appMaster = data.getAppMaster();
			if (appMaster == null) {
				// 없을시 결재라인 생성
				WorkspaceHelper.service.register(data, data.getPer(), description, agreeRows, approvalRows,
						receiveRows);
			} else {
				// 상태값 및 날짜 변경들..
				ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAllLines(appMaster);
				for (ApprovalLine line : list) {
					PersistenceHelper.manager.delete(line);
				}
				PersistenceHelper.manager.delete(appMaster);
//				WorkspaceHelper.service.register(data, data.getPer(), description, agreeRows, approvalRows,
//						receiveRows);
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
	public void read(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkData data = (WorkData) CommonUtil.getObject(oid);
			data.setReads(true);
			PersistenceHelper.manager.modify(data);

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
