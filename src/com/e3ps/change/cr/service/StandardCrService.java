package com.e3ps.change.cr.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.e3ps.change.CrToDocumentLink;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcrToEcrLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.cr.dto.CrDTO;
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

public class StandardCrService extends StandardManager implements CrService {

	public static StandardCrService newStandardCrService() throws WTException {
		StandardCrService instance = new StandardCrService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(CrDTO dto) throws Exception {
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
			String number = "CR-" + dateFormat.format(currentDate) + "-N";
			number = CrHelper.manager.getNextNumber(number);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			EChangeRequest cr = EChangeRequest.newEChangeRequest();
			cr.setEoName(name);
			cr.setEoNumber(number);
			Timestamp today = new Timestamp(currentDate.getTime());
			cr.setCreateDate(today.toString().substring(0, 10));
			cr.setWriter(sessionUser.getFullName());
			cr.setIsNew(true);
			PeopleDTO data = new PeopleDTO(sessionUser);
			cr.setCreateDepart(data.getDepartment_name());
			cr.setModel(model);
			cr.setPeriod(period);

			cr.setChangeSection(changeSection);
			cr.setContents(contents);

			String location = "/Default/설계변경/ECR";
			String lifecycle = "LC_ECR";

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) cr, folder);
			// lifecycle 설정
			LifeCycleHelper.setLifeCycle(cr,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef()));

			cr = (EChangeRequest) PersistenceHelper.manager.save(cr);
			// 첨부 파일 저장
			saveAttach(cr, dto);
			// 관련 CR 링크
			saveLink(cr, dto);
			WorkDataHelper.service.create(cr);

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
	private void saveLink(EChangeRequest cr, CrDTO dto) throws Exception {
		ArrayList<Map<String, String>> rows101 = dto.getRows101();
		for (Map<String, String> row101 : rows101) {
			String gridState = row101.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row101.get("oid");
				EChangeRequest ref = (EChangeRequest) CommonUtil.getObject(oid);
				EcrToEcrLink link = EcrToEcrLink.newEcrToEcrLink(cr, ref);
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
				RequestOrderLink link = RequestOrderLink.newRequestOrderLink(eco, cr);
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
				CrToDocumentLink link = CrToDocumentLink.newCrToDocumentLink(cr, doc);
				PersistenceServerHelper.manager.insert(link);
			}
		}

	}

	/**
	 * 첨부 파일 저장
	 */
	private void saveAttach(EChangeRequest cr, CrDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(cr);
			applicationData.setRole(ContentRoleType.toContentRoleType("ECR"));
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(cr, applicationData, vault.getPath());
		}

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(cr);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(cr, applicationData, vault.getPath());
		}
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(oid);
			// 관련 링크 삭제
			deleteLink(cr);
			// 결재선 지정 삭제
			WorkData dd = WorkDataHelper.manager.getWorkData(cr);
			if (dd != null) {
				PersistenceHelper.manager.delete(dd);
			}
			// 외부 메일 삭제
			MailUserHelper.service.deleteLink(oid);
			// 모든 결재선 삭제
			WorkspaceHelper.service.deleteAllLines(cr);
			// 데이터 삭제
			PersistenceHelper.manager.delete(cr);

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
	public void modify(CrDTO dto) throws Exception {
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

			EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(dto.getOid());
			cr.setEoName(name);
			cr.setModel(model);
			cr.setChangeSection(changeSection);
			cr.setContents(contents);
			cr.setPeriod(period);

			cr = (EChangeRequest) PersistenceHelper.manager.modify(cr);

			// 첨부 파일 삭제
			removeAttach(cr);
			saveAttach(cr, dto);

			// 링크 삭제
			deleteLink(cr);
			saveLink(cr, dto);

			WorkData wd = WorkDataHelper.manager.getWorkData(cr);
			if (wd != null) {
				System.out.println("기존 결재선 삭제(CR)!");
				PersistenceHelper.manager.delete(wd);
			}

			ApprovalMaster mm = WorkspaceHelper.manager.getMaster(cr);
			if (mm != null) {
				// 모든 결재선 삭제
				WorkspaceHelper.service.deleteAllLines(mm);
			}

			WorkDataHelper.service.create(cr);

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
	 * 첨부 파일 삭제
	 */
	private void removeAttach(EChangeRequest cr) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(cr, ContentRoleType.toContentRoleType("ECR"));
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(cr, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(cr, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(cr, item);
		}
	}

	/**
	 * 관련 CR 삭제
	 */
	private void deleteLink(EChangeRequest cr) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(cr, "useBy", EcrToEcrLink.class, false);
		while (result.hasMoreElements()) {
			EcrToEcrLink link = (EcrToEcrLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}

		result.reset();
		result = PersistenceHelper.manager.navigate(cr, "eco", RequestOrderLink.class, false);
		while (result.hasMoreElements()) {
			RequestOrderLink link = (RequestOrderLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}

		result.reset();
		result = PersistenceHelper.manager.navigate(cr, "doc", CrToDocumentLink.class, false);
		while (result.hasMoreElements()) {
			CrToDocumentLink link = (CrToDocumentLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}
}
