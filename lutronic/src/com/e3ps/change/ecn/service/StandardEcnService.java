package com.e3ps.change.ecn.service;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.ecn.dto.EcnDTO;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcnService extends StandardManager implements EcnService {

	public static StandardEcnService newStandardEcnService() throws WTException {
		StandardEcnService instance = new StandardEcnService();
		instance.initialize();
		return instance;
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeNotice ecn = (EChangeNotice) CommonUtil.getObject(oid);
			PersistenceHelper.manager.delete(ecn);

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
	public void create(EcnDTO dto) throws Exception {
		String name = dto.getName();
		String number = dto.getNumber();
		String eoCommentA = dto.getEoCommentA();
		String eoCommentB = dto.getEoCommentB();
		boolean temprary = dto.isTemprary();
		
		// 결재
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		boolean isSelf = dto.isSelf();
		// 외부 메일
		ArrayList<Map<String, String>> external = dto.getExternal();
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeNotice ecn = EChangeNotice.newEChangeNotice();
			ecn.setEoName(name);
			ecn.setEoNumber(number);
			
			ecn.setEoCommentA(eoCommentA);
			ecn.setEoCommentB(eoCommentB);
			
			String location = "/Default/설계변경/ECN";
			String lifecycle = "LC_Default";
			
			// 임시 서장함 이동
			if (temprary) {
				setTemprary(ecn, lifecycle);
			} else {
				Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
				FolderHelper.assignLocation((FolderEntry) ecn, folder);				
			}
			ecn = (EChangeNotice) PersistenceHelper.manager.save(ecn);
			
			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(ecn, state);
			}

			PersistenceHelper.manager.save(ecn);
			
			// 외부 메일 링크 저장
			MailUserHelper.service.saveLink(ecn, external);
			
			// 결재 시작
			if (isSelf) {
				// 자가결재시
				WorkspaceHelper.service.self(ecn);
			} else {
				// 결재시작
				if (approvalRows.size() > 0) {
					WorkspaceHelper.service.register(ecn, agreeRows, approvalRows, receiveRows);
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

	/**
	 * 임시 저장함으로 이동시킬 함수
	 */
	private void setTemprary(EChangeNotice ecn, String lifecycle) throws Exception {
		setTemprary(ecn, lifecycle, "C");
	}

	/**
	 * 임시 저장함으로 이동시킬 함수 C 생성, R, U 개정 및 수정
	 */
	private void setTemprary(EChangeNotice ecn, String lifecycle, String option) throws Exception {
		String location = "/Default/임시저장함";
		if ("C".equals(option)) {
			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) ecn, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(ecn,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
		} else if ("U".equals(option) || "R".equals(option)) {

		}
	}
}
