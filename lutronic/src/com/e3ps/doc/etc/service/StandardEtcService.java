package com.e3ps.doc.etc.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.etc.dto.EtcDTO;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardEtcService extends StandardManager implements EtcService {

	public static StandardEtcService newStandardEtcService() throws WTException {
		StandardEtcService instance = new StandardEtcService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(EtcDTO dto) throws Exception {
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String content = dto.getContent();
		String documentType = dto.getDocumentType_code();
		String documentName = dto.getDocumentName();
		String lifecycle = dto.getLifecycle();

		Transaction trs = new Transaction();
		try {
			trs.start();
			DocumentType docType = DocumentType.toDocumentType(documentType);
			String number = getDocumentNumberSeq(docType.getLongDescription());
			WTDocument doc = WTDocument.newWTDocument();
			doc.setDocType(docType);

			// 문서 이름 세팅..
			if (name.length() > 0) {
				doc.setName(documentName + "-" + name);
			} else {
				doc.setName(documentName);
			}
			doc.setNumber(number);
			doc.setDescription(description);
			doc.getTypeInfoWTDocument().setPtc_rht_1(content);

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) doc, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(doc,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			PersistenceHelper.manager.save(doc);

			// 첨부 파일 저장
			saveAttach(doc, dto);

			// 문서 IBA 처리
			setIBAAttributes(doc, dto);

			// 문서 관련 객체 데이터 처리
			saveLink(doc, dto);

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
	
	private String getDocumentNumberSeq(String longDescription) throws Exception {

		String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));

		String number = longDescription.concat("-").concat(today).concat("-");
		String noFormat = "0000";
		String seqNo = SequenceDao.manager.getSeqNo(number, noFormat, "WTDocumentMaster", "WTDocumentNumber");
		number = number + seqNo;

		return number;
	}
	
	/**
	 * 첨부 파일 저장
	 */
	private void saveAttach(WTDocument doc, EtcDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(doc);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(doc, applicationData, vault.getPath());
		}

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(doc);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(doc, applicationData, vault.getPath());
		}
	}
	
	/**
	 * 문서 IBA 속성값 세팅 함수
	 */
	private void setIBAAttributes(WTDocument doc, EtcDTO dto) throws Exception {
		// 내부 문서 번호
		String interalnumber = dto.getInteralnumber();
		// IBA 키값 어떻게 할지...
		dto.setIBAValue(doc, interalnumber, "INTERALNUMBER");
		// 프로젝트 코드
		String model_code = dto.getModel_code();
		dto.setIBAValue(doc, model_code, "MODEL");
		// 부서
		String deptcode_code = dto.getDeptcode_code();
		dto.setIBAValue(doc, deptcode_code, "DEPTCODE");
		// 보존기간
		String preseration_code = dto.getPreseration_code();
		dto.setIBAValue(doc, preseration_code, "PRESERATION");
		// 작성자
		String writer = dto.getWriter();
		dto.setIBAValue(doc, writer, "DSGN");
		// 결재 유형
		String approvalType_code = dto.getLifecycle().equals("LC_Default") ? "DEFAUT" : "BATCH";
		dto.setIBAValue(doc, approvalType_code, "APPROVALTYPE");
	}
	
	/**
	 * 문서 관련 객체 저장
	 */
	private void saveLink(WTDocument doc, EtcDTO dto) throws Exception {
		// 그리드 배열로 받아서 처리한다. 숫자로 그리드 아이디 구분
		ArrayList<Map<String, String>> rows90 = dto.getRows90();
		// 관련문서
		for (Map<String, String> row90 : rows90) {
			String gridState = row90.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row90.get("oid");
				WTDocument ref = (WTDocument) CommonUtil.getObject(oid);
				DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(doc, ref);
				PersistenceServerHelper.manager.insert(link);
			}
		}

		ArrayList<Map<String, String>> rows91 = dto.getRows91();
		// 관련품목
		for (Map<String, String> row91 : rows91) {
			String gridState = row91.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				// 문서 등록시 여기만 호출됨.. 문제 없음, 수정시에도 추가된거 들어올거..
				String oid = row91.get("part_oid");
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}
	
	@Override
	public Map<String, Object> delete(String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

			PersistenceHelper.manager.delete(doc);

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
		return result;
	}

	@Override
	public void revise(EtcDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String content = dto.getContent();
		String documentType = dto.getDocumentType_code();
		String documentName = dto.getDocumentName();
		String lifecycle = dto.getLifecycle();

		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
			WTDocument latest = (WTDocument) VersionControlHelper.service.newVersion(doc);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 문서를 개정 하였습니다.";
			VersionControlHelper.setNote(latest, msg);

			DocumentType docType = DocumentType.toDocumentType(documentType);
			String number = getDocumentNumberSeq(docType.getLongDescription());

			latest.setDescription(description);

			WTDocumentMaster master = (WTDocumentMaster) latest.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();

			// 문서 이름 세팅..
			if ("$$MMDocument".equals(documentType)) {
				identity.setName(name);
			} else {
				if (name.length() > 0) {
					identity.setName(documentName + "-" + name);
				} else {
					identity.setName(documentName);
				}
			}
//			master.setDocType(docType);
			identity.setNumber(number);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			latest.getTypeInfoWTDocument().setPtc_rht_1(content);

			PersistenceHelper.manager.save(latest);

			// 폴더 이동
			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.service.changeFolder((FolderEntry) latest, folder);
			// 라이프사이클 재지정
			LifeCycleHelper.service.reassign(latest,
					LifeCycleHelper.service.getLifeCycleTemplateReference(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			// 첨부 파일 클리어
			removeAttach(latest);
			// 첨부파일 저장
			saveAttach(latest, dto);

			// IBA 삭제
//			deleteIBAAttributes(latest);

			// IBA 설정
			setIBAAttributes(latest, dto);

			// 링크 정보 삭제
			deleteLink(latest);
			// 관련 링크 세팅
			saveLink(latest, dto);

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
	public void modify(EtcDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String content = dto.getContent();
		String documentType = dto.getDocumentType_code();
		String documentName = dto.getDocumentName();
		String lifecycle = dto.getLifecycle();

		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

			DocumentType docType = DocumentType.toDocumentType(documentType);
			String number = getDocumentNumberSeq(docType.getLongDescription());

			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(doc, cFolder, "문서 수정 체크 아웃");
			WTDocument workCopy = (WTDocument) clink.getWorkingCopy();
			workCopy.setDescription(description);
//			workCopy.setDocType(docType);

			WTDocumentMaster master = (WTDocumentMaster) workCopy.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();

			// 문서 이름 세팅..
			if ("$$MMDocument".equals(documentType)) {
				identity.setName(name);
			} else {
				if (name.length() > 0) {
					identity.setName(documentName + "-" + name);
				} else {
					identity.setName(documentName);
				}
			}
//			master.setDocType(docType);
			identity.setNumber(number);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			workCopy.getTypeInfoWTDocument().setPtc_rht_1(content);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 문서를 수정 하였습니다.";
			// 필요하면 수정 사유로 대체
			workCopy = (WTDocument) WorkInProgressHelper.service.checkin(workCopy, msg);

			// 폴더 이동
			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.service.changeFolder((FolderEntry) workCopy, folder);
			// 라이프사이클 재지정
			LifeCycleHelper.service.reassign(workCopy,
					LifeCycleHelper.service.getLifeCycleTemplateReference(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			// 첨부 파일 클리어
			removeAttach(workCopy);
			// 첨부파일 저장
			saveAttach(workCopy, dto);

			// IBA 삭제
//			deleteIBAAttributes(workCopy);

			// IBA 설정
			setIBAAttributes(workCopy, dto);

			// 링크 정보 삭제
			deleteLink(workCopy);
			// 관련 링크 세팅
			saveLink(workCopy, dto);

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
	 * 문서 IBA 속성값 세팅 함수
	 */
	private void setIBAAttributes(WTDocument doc, EtcDTO dto) throws Exception {
		// 내부 문서 번호
		String interalnumber = dto.getInteralnumber();
		// IBA 키값 어떻게 할지...
		dto.setIBAValue(doc, interalnumber, "INTERALNUMBER");
		// 프로젝트 코드
		String model_code = dto.getModel_code();
		dto.setIBAValue(doc, model_code, "MODEL");
		// 부서
		String deptcode_code = dto.getDeptcode_code();
		dto.setIBAValue(doc, deptcode_code, "DEPTCODE");
		// 보존기간
		String preseration_code = dto.getPreseration_code();
		dto.setIBAValue(doc, preseration_code, "PRESERATION");
		// 작성자
		String writer = dto.getWriter();
		dto.setIBAValue(doc, writer, "DSGN");
		// 결재 유형
		String approvalType_code = dto.getLifecycle().equals("LC_Default") ? "DEFAUT" : "BATCH";
		dto.setIBAValue(doc, approvalType_code, "APPROVALTYPE");
	}
	/**
	 * 기타 문서 관련 객체 저장
	 */
	private void saveLink(WTDocument doc, EtcDTO dto) throws Exception {
		// 그리드 배열로 받아서 처리한다. 숫자로 그리드 아이디 구분
		ArrayList<Map<String, String>> rows90 = dto.getRows90();
		// 관련문서
		for (Map<String, String> row90 : rows90) {
			String gridState = row90.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row90.get("oid");
				WTDocument ref = (WTDocument) CommonUtil.getObject(oid);
				DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(doc, ref);
				PersistenceServerHelper.manager.insert(link);
			}
		}

		ArrayList<Map<String, String>> rows91 = dto.getRows91();
		// 관련품목
		for (Map<String, String> row91 : rows91) {
			String gridState = row91.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				// 문서 등록시 여기만 호출됨.. 문제 없음, 수정시에도 추가된거 들어올거..
				String oid = row91.get("part_oid");
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	/**
	 * 기타 문서 링크 데이터 삭제
	 */
	private void deleteLink(WTDocument workCopy) throws Exception {
		// 윈칠 객체의 경우 체크인아웃시 자동 연결 처리
		QueryResult result = PersistenceHelper.manager.navigate(workCopy, "describes", WTPartDescribeLink.class, false);
		while (result.hasMoreElements()) {
			WTPartDescribeLink link = (WTPartDescribeLink) result.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	/**
	 * 첨부 파일 저장
	 */
	private void saveAttach(WTDocument doc, EtcDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(doc);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(doc, applicationData, vault.getPath());
		}

		for (int i = 0; i < secondarys.size(); i++) {
			String cacheId = secondarys.get(i);
			File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
			ApplicationData applicationData = ApplicationData.newApplicationData(doc);
			applicationData.setRole(ContentRoleType.SECONDARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(doc, applicationData, vault.getPath());
		}
	}

	/**
	 * 첨부 파일 삭제
	 */
	private void removeAttach(WTDocument workCopy) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(workCopy, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(workCopy, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(workCopy, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(workCopy, item);
		}
	}
	
}
