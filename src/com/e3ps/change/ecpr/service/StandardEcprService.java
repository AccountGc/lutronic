package com.e3ps.change.ecpr.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.e3ps.change.CrToDocumentLink;
import com.e3ps.change.CrToEcprLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoToEcprLink;
import com.e3ps.change.EcprToDocumentLink;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.ecpr.dto.EcprDTO;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.MailUserHelper;
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

public class StandardEcprService extends StandardManager implements EcprService {

	public static StandardEcprService newStandardEcprService() throws WTException {
		StandardEcprService instance = new StandardEcprService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EcprDTO dto) throws Exception {
		String name = dto.getName();
		String period = dto.getPeriod_code();
		String contents = dto.getContents();
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

			Timestamp currentDate = new Timestamp(new Date().getTime());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM");
			String number = "ECPR-" + dateFormat.format(currentDate) + "-N";
			number = EcprHelper.manager.getNextNumber(number);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			ECPRRequest ecpr = ECPRRequest.newECPRRequest();
			ecpr.setEoName(name);
			ecpr.setEoNumber(number);
			ecpr.setPeriod(period);
			
			Timestamp today = new Timestamp(currentDate.getTime());
			ecpr.setCreateDate(today.toString().substring(0, 10));
			ecpr.setWriter(sessionUser.getFullName());

			PeopleDTO data = new PeopleDTO(sessionUser);
			ecpr.setCreateDepart(data.getDepartment_name());
			ecpr.setModel(model);
			ecpr.setIsNew(true);

			ecpr.setChangeSection(changeSection);
			ecpr.setContents(contents);

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
			saveLink(ecpr, dto);
			WorkDataHelper.service.create(ecpr);
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
	private void saveLink(ECPRRequest ecpr, EcprDTO dto) throws Exception {
		ArrayList<Map<String, String>> rows101 = dto.getRows101(); // 관련 CR
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

		ArrayList<Map<String, String>> rows105 = dto.getRows105(); // 관련 ECO
		for (Map<String, String> row105 : rows105) {
			String gridState = row105.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row105.get("oid");
				EChangeOrder ref = (EChangeOrder) CommonUtil.getObject(oid);
				EcoToEcprLink link = EcoToEcprLink.newEcoToEcprLink(ref, ecpr);
				PersistenceServerHelper.manager.insert(link);
			}
		}

		ArrayList<Map<String, String>> rows90 = dto.getRows90();
		for (Map<String, String> row90 : rows90) {
			String gridState = row90.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row90.get("oid");
				WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
				EcprToDocumentLink link = EcprToDocumentLink.newEcprToDocumentLink(ecpr, doc);
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

		result.reset();
		result = PersistenceHelper.manager.navigate(ecpr, "eco", EcoToEcprLink.class, false);
		while (result.hasMoreElements()) {
			EcoToEcprLink link = (EcoToEcprLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}

		result.reset();
		result = PersistenceHelper.manager.navigate(ecpr, "doc", EcprToDocumentLink.class, false);
		while (result.hasMoreElements()) {
			EcprToDocumentLink link = (EcprToDocumentLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	@Override
	public void modify(EcprDTO dto) throws Exception {
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

			ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(dto.getOid());
			ecpr.setEoName(name);
			ecpr.setModel(model);
			ecpr.setChangeSection(changeSection);
			ecpr.setContents(contents);
			ecpr.setPeriod(period);
			ecpr = (ECPRRequest) PersistenceHelper.manager.modify(ecpr);

			// 첨부 파일 삭제
			removeAttach(ecpr);
			saveAttach(ecpr, dto);

			// 링크 삭제
			deleteLink(ecpr);
			saveLink(ecpr, dto);

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
			// 관련 링크 삭제
			deleteLink(ecpr);
			// 결재선 지정 삭제
			WorkData dd = WorkDataHelper.manager.getWorkData(ecpr);
			if (dd != null) {
				PersistenceHelper.manager.delete(dd);
			}
			// 외부 메일 삭제
			MailUserHelper.service.deleteLink(oid);
			// 모든 결재선 삭제
			WorkspaceHelper.service.deleteAllLines(ecpr);
			// 데이터 삭제
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
