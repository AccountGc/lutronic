package com.e3ps.change.ecrm.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.e3ps.change.CrToDocumentLink;
import com.e3ps.change.CrToEcprLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoToEcprLink;
import com.e3ps.change.EcprToDocumentLink;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.EcrmToCrLink;
import com.e3ps.change.EcrmToDocumentLink;
import com.e3ps.change.EcrmToEcoLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.ecrm.dto.EcrmDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.MailUserHelper;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.service.WorkDataHelper;
import com.e3ps.workspace.service.WorkspaceHelper;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
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
		String contents = dto.getContents();
		String period = dto.getPeriod_code();
		ArrayList<String> sections = dto.getSections(); // 변경 구분
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 모델
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

			Date currentDate = new Date();

			// 원하는 날짜 형식을 설정합니다.
			SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM");
			String number = "ECRM-" + dateFormat.format(currentDate) + "-N";
			number = EcrmHelper.manager.getNextNumber(number);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			PeopleDTO data = new PeopleDTO(sessionUser);
			ECRMRequest ecrm = ECRMRequest.newECRMRequest();
			ecrm.setEoName(name);
			ecrm.setEoNumber(number);
			ecrm.setPeriod(period);

			Timestamp today = new Timestamp(currentDate.getTime());
			ecrm.setCreateDate(today.toString().substring(0, 10));
			ecrm.setWriter(sessionUser.getFullName());
			ecrm.setCreateDepart(data.getDepartment_name());
			ecrm.setModel(model);
			ecrm.setIsNew(true);

			ecrm.setChangeSection(changeSection);
			ecrm.setContents(contents);

			String location = "/Default/설계변경/ECRM";
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
			saveLink(ecrm, dto);
			WorkDataHelper.service.create(ecrm);
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
	 * 관련 링크
	 */
	private void saveLink(ECRMRequest ecrm, EcrmDTO dto) throws Exception {
		ArrayList<Map<String, String>> rows101 = dto.getRows101();
		for (Map<String, String> row101 : rows101) {
			String gridState = row101.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row101.get("oid");
				EChangeRequest ref = (EChangeRequest) CommonUtil.getObject(oid);
				EcrmToCrLink link = EcrmToCrLink.newEcrmToCrLink(ecrm, ref);
				PersistenceServerHelper.manager.insert(link);
			}
		}
		ArrayList<Map<String, String>> rows105 = dto.getRows105();
		for (Map<String, String> row105 : rows105) {
			String gridState = row105.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row105.get("oid");
				EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
				EcrmToEcoLink link = EcrmToEcoLink.newEcrmToEcoLink(ecrm, eco);
				PersistenceServerHelper.manager.insert(link);
			}
		}

		// 문서
		ArrayList<Map<String, String>> rows90 = dto.getRows90();
		for (Map<String, String> row90 : rows90) {
			String gridState = row90.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row90.get("oid");
				WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
				EcrmToDocumentLink link = EcrmToDocumentLink.newEcrmToDocumentLink(ecrm, doc);
				PersistenceServerHelper.manager.insert(link);
			}
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

	@Override
	public void modify(EcrmDTO dto) throws Exception {
		String name = dto.getName();
		String contents = dto.getContents();
		String period = dto.getPeriod_code();
		ArrayList<String> sections = dto.getSections(); // 변경 구분
		ArrayList<Map<String, String>> rows300 = dto.getRows300(); // 모델
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 모델 배열 처리
			String model = "";
			for (int i = 0; i < rows300.size(); i++) {
				Map<String, String> row300 = rows300.get(i);
				String oid = row300.get("oid");
				NumberCode n = (NumberCode) CommonUtil.getObject(oid);
				if (n != null) {
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

			ECRMRequest ecrm = (ECRMRequest) CommonUtil.getObject(dto.getOid());
			ecrm.setEoName(name);
			ecrm.setModel(model);
			ecrm.setChangeSection(changeSection);
			ecrm.setContents(contents);
			ecrm.setPeriod(period);
			ecrm = (ECRMRequest) PersistenceHelper.manager.modify(ecrm);

			// 첨부 파일 삭제
			removeAttach(ecrm);
			saveAttach(ecrm, dto);

			// 링크 삭제
			deleteLink(ecrm);
			saveLink(ecrm, dto);

			ApprovalMaster mm = WorkspaceHelper.manager.getMaster(ecrm);

			WorkDataHelper.service.create(ecrm);
			// 기존 결재선 복사하기...
			WorkspaceHelper.service.copyLines(ecrm, mm);

			if (mm != null) {
				// 모든 결재선 삭제
				WorkspaceHelper.service.deleteAllLines(mm);
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
	 * 관련링크 삭제
	 */
	private void deleteLink(ECRMRequest ecrm) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(ecrm, "cr", EcrmToCrLink.class, false);
		while (result.hasMoreElements()) {
			EcrmToCrLink link = (EcrmToCrLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}

		result.reset();
		result = PersistenceHelper.manager.navigate(ecrm, "eco", EcrmToEcoLink.class, false);
		while (result.hasMoreElements()) {
			EcrmToEcoLink link = (EcrmToEcoLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}

		result.reset();
		result = PersistenceHelper.manager.navigate(ecrm, "doc", EcrmToDocumentLink.class, false);
		while (result.hasMoreElements()) {
			EcrmToDocumentLink link = (EcrmToDocumentLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	/**
	 * 첨부파일 삭제
	 */
	private void removeAttach(ECRMRequest ecrm) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(ecrm, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(ecrm, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(ecrm, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(ecrm, item);
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ECRMRequest ecrm = (ECRMRequest) CommonUtil.getObject(oid);
			// 관련 링크 삭제
			deleteLink(ecrm);
			// 결재선 지정 삭제
			WorkData dd = WorkDataHelper.manager.getWorkData(ecrm);
			if (dd != null) {
				PersistenceHelper.manager.delete(dd);
			}
			// 외부 메일 삭제
			MailUserHelper.service.deleteLink(oid);
			// 모든 결재선 삭제
			WorkspaceHelper.service.deleteAllLines(ecrm);
			// 데이터 삭제
			PersistenceHelper.manager.delete(ecrm);

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
