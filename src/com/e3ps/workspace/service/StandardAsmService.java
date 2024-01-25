package com.e3ps.workspace.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.WCUtil;
import com.e3ps.workspace.AppPerLink;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.WorkData;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardAsmService extends StandardManager implements AsmService {

	public static StandardAsmService newStandardAsmService() throws WTException {
		StandardAsmService instance = new StandardAsmService();
		instance.initialize();
		return instance;
	}

	@Override
	public void register(Map<String, Object> params) throws Exception {
		String appName = (String) params.get("appName");
		String description = (String) params.get("description");
		String type = (String) params.get("type");
		Transaction trs = new Transaction();
		try {
			trs.start();

			String number = "";
			if ("DOC".equals(type)) {
				number = "NDBT";
			} else if ("ROHS".equals(type)) {
				number = "ROHSBT";
			} else if ("MOLD".equals(type)) {
				number = "MMBT";
			} else if ("PATHOLOGICAL".equals(type)) {
				number = "AMBT";
			} else if ("CLINICAL".equals(type)) {
				number = "BMBT";
			} else if ("COSMETIC".equals(type)) {
				number = "CMBT";
			}
			String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));
			number = number.concat("-").concat(today).concat("-");
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "AsmApproval", "AsmNumber");
			number = number + seqNo;

			AsmApproval asm = AsmApproval.newAsmApproval();

			asm.setNumber(number);
			asm.setName(appName);
			asm.setDescription(description);

			Folder folder = FolderTaskLogic.getFolder("/Default/AsmApproval", WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) asm, folder);

			LifeCycleHelper.setLifeCycle(asm,
					LifeCycleHelper.service.getLifeCycleTemplate("LC_Default", WCUtil.getWTContainerRef())); // Lifecycle

			asm = (AsmApproval) PersistenceHelper.manager.save(asm);

			ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) params.get("list");

			for (Map<String, Object> map : list) {
				String oid = (String) map.get("oid");
				WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
				AppPerLink link = AppPerLink.newAppPerLink(doc, asm);
				LifeCycleHelper.service.setLifeCycleState(doc, State.toState(asm.getLifeCycleState().toString()));
				PersistenceHelper.manager.save(link);
			}

			// 결제선 지정 만들기
			WorkDataHelper.service.create(asm);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			AsmApproval asm = (AsmApproval) CommonUtil.getObject(oid);

			WorkData wd = WorkDataHelper.manager.getWorkData(asm);
			if (wd != null) {
				PersistenceHelper.manager.delete(wd);
			}

			QueryResult qr = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class);
			while (qr.hasMoreElements()) {
				Persistable per = (Persistable) qr.nextElement();
				if (per instanceof LifeCycleManaged) {
					LifeCycleManaged lcm = (LifeCycleManaged) per;
					LifeCycleHelper.service.setLifeCycleState(lcm, State.toState("BATCHAPPROVAL"));
				}
			}

			ApprovalMaster mm = WorkspaceHelper.manager.getMaster(asm);
			if (mm != null) {
				ArrayList<ApprovalLine> lines = WorkspaceHelper.manager.getAllLines(mm);
				for (ApprovalLine line : lines) {
					PersistenceHelper.manager.delete(line);
				}
				PersistenceHelper.manager.delete(mm);
			}

			PersistenceHelper.manager.delete(asm);

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
	public void modify(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String name = (String) params.get("name");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			AsmApproval asm = (AsmApproval) CommonUtil.getObject(oid);

			WorkData wd = WorkDataHelper.manager.getWorkData(asm);
			if (wd != null) {
				PersistenceHelper.manager.delete(wd);
			}

			ApprovalMaster mm = WorkspaceHelper.manager.getMaster(asm);
			if (mm != null) {
				ArrayList<ApprovalLine> lines = WorkspaceHelper.manager.getAllLines(mm);
				for (ApprovalLine line : lines) {
					PersistenceHelper.manager.delete(line);
				}
				PersistenceHelper.manager.delete(mm);
			}

			asm.setName(name);
			asm.setDescription(description);
			asm = (AsmApproval) PersistenceHelper.manager.modify(asm);

			ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) params.get("list");

			// 기존꺼 삭제
			QueryResult qr = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class, false);
			while (qr.hasMoreElements()) {
				AppPerLink link = (AppPerLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			for (Map<String, Object> map : list) {
				String s = (String) map.get("oid");
				WTDocument doc = (WTDocument) CommonUtil.getObject(s);
				AppPerLink link = AppPerLink.newAppPerLink(doc, asm);
				LifeCycleHelper.service.setLifeCycleState(doc, State.toState(asm.getLifeCycleState().toString()));
				PersistenceHelper.manager.save(link);
			}

			// 결제선 지정 만들기
			WorkDataHelper.service.create(asm);

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
