package com.e3ps.change.ecrm.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.CrToEcprLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.ecrm.dto.EcrmDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.workspace.service.WorkDataHelper;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardEcrmService extends StandardManager implements EcrmService {

	public static StandardEcrmService newStandardEcrmService() throws WTException {
		StandardEcrmService instance = new StandardEcrmService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EcrmDTO dto) throws Exception {
		String name = dto.getName();
		String number = dto.getNumber();
		String writeDate = dto.getWriteDate();
		String approveDate = dto.getApproveDate();
		String createDepart = dto.getCreateDepart();
		String writer = dto.getWriter();
		String contents = dto.getContents();
		ArrayList<String> sections = dto.getSections(); // 변경 구분
		ArrayList<Map<String, String>> rows101 = dto.getRows101(); // 관련 CR
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 모델
		boolean temprary = dto.isTemprary();
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 모델 배열 처리
			// US21,MD23,PN21,
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				if (oid != null) {
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

			ECRMRequest ecrm = ECRMRequest.newECRMRequest();
			ecrm.setEoName(name);
			ecrm.setEoNumber(number);
			ecrm.setCreateDate(writeDate);
			ecrm.setWriter(writer);

			ecrm.setApproveDate(approveDate);
			ecrm.setCreateDepart(createDepart);
			ecrm.setModel(model);
			ecrm.setIsNew(true);
			
			ecrm.setChangeSection(changeSection);
			ecrm.setContents(contents);
			
			String location = "/Default/설계변경/ECPR";
			String lifecycle = "LC_Default";
			
			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) ecrm, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(ecrm,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle
			ecrm = (ECRMRequest) PersistenceHelper.manager.save(ecrm);
			
			// 첨부 파일 저장
			saveAttach(ecrm, dto);
			
			// 관련 CR 링크
//			saveLink(ecrm, rows101);
						
			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(ecrm, state);
			} else {
				WorkDataHelper.service.create(ecrm);
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
	private void saveAttach(ECRMRequest ecrm, EcrmDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(ecrm);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(ecrm, applicationData, vault.getPath());
		}

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(ecrm);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(ecrm, applicationData, vault.getPath());
		}
	}
	
	/**
	 * 관련 CR링크
	 */
//	private void saveLink(ECRMRequest ecrm, ArrayList<Map<String, String>> rows101) throws Exception {
//		for (Map<String, String> row101 : rows101) {
//			String gridState = row101.get("gridState");
//			// 신규 혹은 삭제만 있다. (added, removed
//			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
//				String oid = row101.get("oid");
//				EChangeRequest ref = (EChangeRequest) CommonUtil.getObject(oid);
//				CrToEcprLink link = CrToEcprLink.newCrToEcprLink(ref, ecrm);
//				PersistenceServerHelper.manager.insert(link);
//			}
//		}
	
}
