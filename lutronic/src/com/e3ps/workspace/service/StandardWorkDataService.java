package com.e3ps.workspace.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.common.mail.MailUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.dto.WorkDataDTO;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.lifecycle.LifeCycleManaged;
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
			data.setOwnership(ownership);
			PersistenceHelper.manager.save(data);

			// 메일발송하기!
//			MailUtils.manager.sendWorkDataMail((LifeCycleManaged) per, "결재선지정", "결재선지정");

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

			// 외부 메일 연결
			MailUserHelper.service.saveLink(data.getPer(), external);

			// 결재 필수..
			WorkspaceHelper.service.register(data.getPer(), description, agreeRows, approvalRows, receiveRows);

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
