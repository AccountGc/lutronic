package com.e3ps.change.ecpr.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.CrToEcprLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.ecpr.dto.EcprDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcprService extends StandardManager implements EcprService {

	public static StandardEcprService newStandardEcprService() throws WTException {
		StandardEcprService instance = new StandardEcprService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EcprDTO dto) throws Exception {
		String name = dto.getName();
		String number = dto.getNumber();
		String writeDate = dto.getWriteDate();
		String approveDate = dto.getApproveDate();
		String createDepart = dto.getCreateDepart();
		String writer_oid = dto.getWriter_oid();
		String proposer_oid = dto.getProposer_oid();
		String eoCommentA = dto.getEoCommentA();
		String eoCommentB = dto.getEoCommentB();
		String eoCommentC = dto.getEoCommentC();
		ArrayList<String> sections = dto.getSections(); // 변경 구분
		ArrayList<Map<String, String>> rows101 = dto.getRows101(); // 관련 CR
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 모델
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

			// 모델 배열 처리
			// US21,MD23,PN21,
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				if(oid != null) {
					NumberCode n = (NumberCode) CommonUtil.getObject(oid);
					if (rows300.size() - 1 == i) {
						model += n.getCode();
					} else {
						model += n.getCode() + ",";
					}					
				}
			}

			String changeSection = "";
			for (int i = 0; i < sections.size(); i++) {
				String value = sections.get(i);
				if (sections.size() - 1 == i) {
					changeSection += value;
				} else {
					changeSection += value + ",";
				}
			}

			ECPRRequest ecpr = ECPRRequest.newECPRRequest();
			ecpr.setEoName(name);
			ecpr.setEoNumber(number);
			ecpr.setCreateDate(writeDate);

			if(!writer_oid.equals("")) {
				long writerOid = CommonUtil.getOIDLongValue(writer_oid);
				ecpr.setWriter(Long.toString(writerOid));
			}
			
			ecpr.setApproveDate(approveDate);
			ecpr.setCreateDepart(createDepart); // 코드 넣엇을듯..
			ecpr.setModel(model);

			if(!proposer_oid.equals("")) {
				long proposerOid = CommonUtil.getOIDLongValue(proposer_oid);
				ecpr.setProposer(Long.toString(proposerOid));	
			}
			
			ecpr.setChangeSection(changeSection);
			ecpr.setEoCommentA(eoCommentA);
			ecpr.setEoCommentB(eoCommentB);
			ecpr.setEoCommentC(eoCommentC);

			String location = "/Default/설계변경/ECPR";
			String lifecycle = "LC_Default";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) ecpr, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(ecpr,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			ecpr = (ECPRRequest) PersistenceHelper.manager.save(ecpr);

			// 첨부 파일 저장
			saveAttach(ecpr, dto);

			// 관련 CR 링크
			saveLink(ecpr, rows101);
			
			// 외부 메일 링크 저장
			MailUserHelper.service.saveLink(ecpr, external);
			
			// 결재 시작
			if (isSelf) {
				// 자가결재시
				WorkspaceHelper.service.self(ecpr);
			} else {
				// 결재시작
				if (approvalRows.size() > 0) {
					WorkspaceHelper.service.register(ecpr, agreeRows, approvalRows, receiveRows);
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
	 * 첨부 파일 저장
	 */
	private void saveAttach(ECPRRequest ecpr, EcprDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(ecpr);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(ecpr, applicationData, vault.getPath());
		}

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(ecpr);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(ecpr, applicationData, vault.getPath());
		}
	}
	
	/**
	 * 관련 CR링크
	 */
	private void saveLink(ECPRRequest ecpr, ArrayList<Map<String, String>> rows101) throws Exception {
		for (Map<String, String> row101 : rows101) {
			String gridState = row101.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row101.get("oid");
				EChangeRequest ref = (EChangeRequest) CommonUtil.getObject(oid);
				CrToEcprLink link = CrToEcprLink.newCrToEcprLink(ref, ecpr);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}
	
	/**
	 * 첨부 파일 삭제
	 */
	private void removeAttach(ECPRRequest ecpr) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(ecpr, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(ecpr, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(ecpr, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(ecpr, item);
		}
	}

	/**
	 * 관련 CR 삭제
	 */
	private void deleteLink(ECPRRequest ecpr) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecpr, "cr", CrToEcprLink.class, false);
		while (result.hasMoreElements()) {
			CrToEcprLink link = (CrToEcprLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	@Override
	public void update(EcprDTO dto) throws Exception {
		String name = dto.getName();
		String number = dto.getNumber();
		String writeDate = dto.getWriteDate();
		String approveDate = dto.getApproveDate();
		String createDepart = dto.getCreateDepart();
		String writer_oid = dto.getWriter_oid();
		String proposer_oid = dto.getProposer_oid();
		String eoCommentA = dto.getEoCommentA();
		String eoCommentB = dto.getEoCommentB();
		String eoCommentC = dto.getEoCommentC();
		ArrayList<String> sections = dto.getSections(); // 변경 구분
		ArrayList<Map<String, String>> rows101 = dto.getRows101(); // 관련 CR
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 모델
		// 외부 메일
		ArrayList<Map<String, String>> external = dto.getExternal();

		Transaction trs = new Transaction();
		try {
			trs.start();
			
			// 모델 배열 처리
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				NumberCode n = (NumberCode) CommonUtil.getObject(oid);
				if(n != null) {
					if (rows300.size() - 1 == i) {
						model += n.getCode();
					} else {
						model += n.getCode() + ",";
					}					
				}
			}
			
			String changeSection = "";
			for (int i = 0; i < sections.size(); i++) {
				String value = sections.get(i);
				if (sections.size() - 1 == i) {
					changeSection += value;
				} else {
					changeSection += value + ",";
				}
			}
			
			ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(dto.getOid());
			ecpr.setEoName(name);
			ecpr.setEoNumber(number);
			ecpr.setCreateDate(writeDate);

			if(!writer_oid.equals("")) {
				long writerOid = CommonUtil.getOIDLongValue(writer_oid);
				ecpr.setWriter(Long.toString(writerOid));
			}
			ecpr.setApproveDate(approveDate);
			ecpr.setCreateDepart(createDepart); // 코드 넣엇을듯..
			ecpr.setModel(model);

			if(!proposer_oid.equals("")) {
				long proposerOid = CommonUtil.getOIDLongValue(proposer_oid);
				ecpr.setProposer(Long.toString(proposerOid));
			}
			ecpr.setChangeSection(changeSection);
			ecpr.setEoCommentA(eoCommentA);
			ecpr.setEoCommentB(eoCommentB);
			ecpr.setEoCommentC(eoCommentC);
			
			ecpr = (ECPRRequest) PersistenceHelper.manager.modify(ecpr);
			
			// 첨부 파일 삭제
			removeAttach(ecpr);
			saveAttach(ecpr, dto);

			// 링크 삭제
			deleteLink(ecpr);
			saveLink(ecpr, rows101);
			
			// 외부 메일 링크 삭제
			MailUserHelper.service.deleteLink(dto.getOid());
			// 외부 메일 링크 추가
			MailUserHelper.service.saveLink(ecpr, external);
			
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
			ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(oid);

			PersistenceHelper.manager.delete(ecpr);

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
