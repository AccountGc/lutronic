
package com.e3ps.doc.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.Workbook;
import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.common.aspose.AsposeUtils;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentClass;
import com.e3ps.doc.DocumentClassType;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentECPRLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.workspace.AppPerLink;
import com.e3ps.workspace.AsmApproval;
import com.e3ps.workspace.WorkData;
import com.e3ps.workspace.service.WorkDataHelper;
import com.e3ps.workspace.service.WorkspaceHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.doc.WTDocumentTypeInfo;
import wt.fc.IdentityHelper;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardDocumentService extends StandardManager implements DocumentService {

	public static StandardDocumentService newStandardDocumentService() throws WTException {
		final StandardDocumentService instance = new StandardDocumentService();
		instance.initialize();
		return instance;
	}

	private String getDocumentNumberSeq(String longDescription) throws Exception {

		String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));

		String number = longDescription.concat("-").concat(today).concat("-");
		String noFormat = "0000";
		String seqNo = SequenceDao.manager.getSeqNo(number, noFormat, "WTDocumentMaster", "WTDocumentNumber");
		number = number + seqNo;

		return number;
	}

	@Override
	public ResultData approvalPackageDocumentAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();
		try {
			trx.start();

			String searchType = StringUtil.checkNull(request.getParameter("searchType"));
			String number = "";
			if (searchType.length() > 0) {
				if ("document".equals(searchType)) {
					number = "NDBT";
				} else if ("ROHS".equals(searchType)) {
					number = "ROHSBT";
				} else if ("MMDocument".equals(searchType)) {
					number = "MMBT";
				}
			}
			String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));
			number = number.concat("-").concat(today).concat("-");
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "WTDocumentMaster", "WTDocumentNumber");
			number = number + seqNo;

			AsmApproval asm = AsmApproval.newAsmApproval();

			String appName = StringUtil.checkNull(request.getParameter("appName"));
			asm.setNumber(number);
			asm.setName(appName);

			// 문서 분류쳬게 설정
			Folder folder = FolderTaskLogic.getFolder("/Default/Document", WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) asm, folder);

			// 문서 lifeCycle 설정
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			LifeCycleHelper.setLifeCycle(asm,
					LifeCycleHelper.service.getLifeCycleTemplate("LC_Default", wtContainerRef)); // Lifecycle

			asm = (AsmApproval) PersistenceHelper.manager.save(asm);

			String[] docOids = request.getParameterValues("docOid");
			for (String docOid : docOids) {
				WTDocument doc = (WTDocument) CommonUtil.getObject(docOid);

				AppPerLink link = AppPerLink.newAppPerLink(doc, asm);
				PersistenceServerHelper.manager.insert(link);
			}

			trx.commit();
			trx = null;
			data.setResult(true);
		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return data;
	}

	@Override
	public Map<String, Object> delete(String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

			WorkData dd = WorkDataHelper.manager.getWorkData(doc);
			if (dd != null) {
				PersistenceHelper.manager.delete(dd);
			}

			WorkspaceHelper.service.deleteAllLines(doc);

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
	public void create(DocumentDTO dto) throws Exception {
		String product = dto.getProduct();
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String content = dto.getContent();
		String lifecycle = dto.getLifecycle();
		String oid = dto.getOid();
		String classType1_code = dto.getClassType1_code();
		String classType2_oid = dto.getClassType2_oid();
		String classType3_oid = dto.getClassType3_oid();
		String oldNumber = dto.getOldNumber();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = WTDocument.newWTDocument();

			// 분류 속성
			WTDocumentTypeInfo info = WTDocumentTypeInfo.newWTDocumentTypeInfo();
			DocumentClassType docClassType = DocumentClassType.toDocumentClassType(classType1_code);
			info.setPtc_str_2(docClassType.toString());

			if (StringUtil.checkString(oldNumber)) {
				info.setPtc_str_3(oldNumber);
			}

			if (StringUtil.checkString(product)) {
				info.setPtc_str_1(product);
			}

			if (StringUtil.checkString(classType2_oid)) {
				DocumentClass classType2 = (DocumentClass) CommonUtil.getObject(classType2_oid);
				ObjectReference ref = ObjectReference.newObjectReference(classType2);
				info.setPtc_ref_2(ref);
			}

			if (StringUtil.checkString(classType3_oid)) {
				DocumentClass classType3 = (DocumentClass) CommonUtil.getObject(classType3_oid);
				ObjectReference ref = ObjectReference.newObjectReference(classType3);
				info.setPtc_ref_3(ref);
			}

			doc.setTypeInfoWTDocument(info);
			String interalnumber = dto.getInteralnumber();
			doc.setName(name); // 하나의 번호로 세팅합니다.

			if ("SOURCE".equals(classType1_code)) {
				String number = "SOURCE" + DateUtil.getCurrentDateString("ym");
				number = DocumentHelper.manager.getNextNumber(number + "-");
				doc.setNumber(number);
			} else {
				doc.setNumber(interalnumber);
			}
			doc.setDescription(description);
			doc.getTypeInfoWTDocument().setPtc_rht_1(content);

			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) doc, folder);
			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(doc,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			doc = (WTDocument) PersistenceHelper.manager.save(doc);

			// 상태값 변경
			if ("LC_Default_NonWF".equals(lifecycle)) {
				doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
				LifeCycleHelper.service.setLifeCycleState(doc, State.toState("BATCHAPPROVAL"), false);
			}

			// 첨부 파일 저장
			saveAttach(doc, dto);

			// 문서 IBA 처리
			setIBAAttributes(doc, dto);

			// 문서 관련 객체 데이터 처리
			saveLink(doc, dto);

			// 설변활동 링크
			if (StringUtil.checkString(oid)) {
				EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
				DocumentActivityLink link = DocumentActivityLink
						.newDocumentActivityLink((WTDocumentMaster) doc.getMaster(), eca);
				PersistenceHelper.manager.save(link);
			}

			doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

			// 작업함으로 이동 시킨다
			// 일괄 결재가 아닐 경우에만 시작한다
			if (!"LC_Default_NonWF".equals(lifecycle)) {
				WorkDataHelper.service.create(doc);
			}

			// 개발 구분과 지침서
			if (classType1_code.equals("DEV") || classType1_code.equals("INSTRUCTION")) {
				DocumentHelper.manager.wordToPdfMethod(doc.getPersistInfo().getObjectIdentifier().getStringValue());
			} else {
//				DocumentHelper.manager.genWordAndPdfMethod(doc.getPersistInfo().getObjectIdentifier().getStringValue());
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
	 * 문서 관련 객체 저장
	 */
	private void saveLink(WTDocument doc, DocumentDTO dto) throws Exception {
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

		ArrayList<Map<String, String>> rows100 = dto.getRows100();
		// 관련EO
		for (Map<String, String> row100 : rows100) {
			String gridState = row100.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row100.get("oid");
				EChangeOrder eo = (EChangeOrder) CommonUtil.getObject(oid);
				DocumentEOLink link = DocumentEOLink.newDocumentEOLink(doc, eo);
				PersistenceServerHelper.manager.insert(link);
			}
		}

		ArrayList<Map<String, String>> rows105 = dto.getRows105();
		// 관련ECO
		for (Map<String, String> row105 : rows105) {
			String gridState = row105.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row105.get("oid");
				EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
				DocumentECOLink link = DocumentECOLink.newDocumentECOLink(doc, eco);
				PersistenceServerHelper.manager.insert(link);
			}
		}

		ArrayList<Map<String, String>> rows101 = dto.getRows101();
		// 관련CR
		for (Map<String, String> row101 : rows101) {
			String gridState = row101.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row101.get("oid");
				EChangeRequest cr = (EChangeRequest) CommonUtil.getObject(oid);
				DocumentCRLink link = DocumentCRLink.newDocumentCRLink(doc, cr);
				PersistenceServerHelper.manager.insert(link);
			}
		}

		ArrayList<Map<String, String>> rows103 = dto.getRows103();
		// 관련ECPR
		for (Map<String, String> row103 : rows103) {
			String gridState = row103.get("gridState");
			// 신규 혹은 삭제만 있다. (added, removed
			if ("added".equals(gridState) || !StringUtil.checkString(gridState)) {
				String oid = row103.get("oid");
				ECPRRequest ecpr = (ECPRRequest) CommonUtil.getObject(oid);
				DocumentECPRLink link = DocumentECPRLink.newDocumentECPRLink(doc, ecpr);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	/**
	 * 문서 IBA 속성값 세팅 함수
	 */
	private void setIBAAttributes(WTDocument doc, DocumentDTO dto) throws Exception {
		// 내부 문서 번호
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		PeopleDTO data = new PeopleDTO(user);
		String interalnumber = dto.getInteralnumber();
		// IBA 키값 어떻게 할지...
		dto.setIBAValue(doc, interalnumber, "INTERALNUMBER");
		// 프로젝트 코드
		String model_code = dto.getModel_code();
		dto.setIBAValue(doc, model_code, "MODEL");
		// 부서
		String deptcode_code = dto.getDeptcode_code();
		dto.setIBAValue(doc, data.getDepartment_name(), "DEPTCODE"); // 부서코드...
		// 보존기간
		String preseration_code = dto.getPreseration_code();
		dto.setIBAValue(doc, preseration_code, "PRESERATION");
		// 작성자
//		String writer = "";
//		if (!dto.getWriter_oid().equals("")) {
//			writer = Long.toString(CommonUtil.getOIDLongValue(dto.getWriter_oid()));
//		}

//		dto.setIBAValue(doc, user.getFullName(), "DSGN");
		// 결재 유형
		String approvalType_code = dto.getLifecycle().equals("LC_Default") ? "DEFAUT" : "BATCH";
		dto.setIBAValue(doc, approvalType_code, "APPROVALTYPE");
	}

	/**
	 * 문서 일괄등록 메서드
	 */
	@Override
	public void batch(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ArrayList<Map<String, Object>> gridData = (ArrayList<Map<String, Object>>) params.get("gridData");

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			// 화면에서 모든 검증 처리 진행
			for (Map<String, Object> data : gridData) {
				DocumentDTO dto = mapper.convertValue(data, DocumentDTO.class);
				create(dto);
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
	public void revise(DocumentDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String content = dto.getContent();
		String lifecycle = dto.getLifecycle();
		String iterationNote = dto.getIterationNote();

		Transaction trs = new Transaction();
		try {
			trs.start();

			// 승인됨 상태이니 기존 결재선 지정이 없는 상태..

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
			WTDocument latest = (WTDocument) VersionControlHelper.service.newVersion(doc);
			VersionControlHelper.setNote(latest, iterationNote);

			latest.setDescription(description);

			WTDocumentMaster master = (WTDocumentMaster) latest.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();

			// 문서 이름 세팅..
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);
			latest.getTypeInfoWTDocument().setPtc_rht_1(content);

			PersistenceHelper.manager.save(latest);

			// 폴더 이동
			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.service.changeFolder((FolderEntry) latest, folder);
			// 라이프사이클 재지정
			LifeCycleHelper.service.reassign(latest,
					LifeCycleHelper.service.getLifeCycleTemplateReference(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			if ("LC_Default_NonWF".equals(lifecycle)) {
				doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
				LifeCycleHelper.service.setLifeCycleState(doc, State.toState("BATCHAPPROVAL"), false);
			} else {
				// 작업함으로 이동 시킨다
				WorkDataHelper.service.create(latest);
			}

			// 첨부 파일 클리어
			removeAttach(latest);
			// 첨부파일 저장
			saveAttach(latest, dto);

			// IBA 설정
			setIBAAttributes(latest, dto);

			// 링크 정보 삭제
			deleteLink(latest);
			// 관련 링크 세팅
			saveLink(latest, dto);

			String classType1_code = latest.getTypeInfoWTDocument().getPtc_str_2();
			if (StringUtil.checkString(classType1_code)) {
				if (classType1_code.equals("DEV") || classType1_code.equals("INSTRUCTION")) {
					DocumentHelper.manager
							.wordToPdfMethod(latest.getPersistInfo().getObjectIdentifier().getStringValue());
				} else {
//					DocumentHelper.manager
//							.genWordAndPdfMethod(latest.getPersistInfo().getObjectIdentifier().getStringValue());
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

	@Override
	public void modify(DocumentDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String content = dto.getContent();
		String lifecycle = dto.getLifecycle();
		String iterationNote = dto.getIterationNote();

		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

			// 기존 결재선 지정 삭제..
			WorkData wd = WorkDataHelper.manager.getWorkData(doc);
			if (wd != null) {
				System.out.println("기존 결재선 삭제!");
				PersistenceHelper.manager.delete(wd);
			}

			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(doc, cFolder, "문서 수정 체크 아웃");
			WTDocument workCopy = (WTDocument) clink.getWorkingCopy();
			workCopy.setDescription(description);

			WTDocumentMaster master = (WTDocumentMaster) workCopy.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();

			// 문서 이름 세팅..
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			workCopy.getTypeInfoWTDocument().setPtc_rht_1(content);
			workCopy = (WTDocument) WorkInProgressHelper.service.checkin(workCopy, iterationNote);

			// 폴더 이동
			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.service.changeFolder((FolderEntry) workCopy, folder);
			// 라이프사이클 재지정
			LifeCycleHelper.service.reassign(workCopy,
					LifeCycleHelper.service.getLifeCycleTemplateReference(lifecycle, WCUtil.getWTContainerRef())); // Lifecycle

			// 일괄결재 일경우 결재선 지정을 안만든다..
			if ("LC_Default_NonWF".equals(lifecycle)) {
				workCopy = (WTDocument) PersistenceHelper.manager.refresh(workCopy);
				LifeCycleHelper.service.setLifeCycleState(workCopy, State.toState("BATCHAPPROVAL"));
			} else {
				WorkDataHelper.service.create(workCopy);
			}

			// 첨부 파일 클리어
			removeAttach(workCopy);
			// 첨부파일 저장
			saveAttach(workCopy, dto);

			// IBA 설정
			setIBAAttributes(workCopy, dto);

			// 링크 정보 삭제
			deleteLink(workCopy);
			// 관련 링크 세팅
			saveLink(workCopy, dto);

			String classType1_code = workCopy.getTypeInfoWTDocument().getPtc_str_2();
			if (StringUtil.checkString(classType1_code)) {
				if (classType1_code.equals("DEV") || classType1_code.equals("INSTRUCTION")) {
					DocumentHelper.manager
							.wordToPdfMethod(workCopy.getPersistInfo().getObjectIdentifier().getStringValue());
				} else {
//					DocumentHelper.manager
//							.genWordAndPdfMethod(workCopy.getPersistInfo().getObjectIdentifier().getStringValue());
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

//	/**
//	 * IBA 속성 삭제
//	 */
//	private void deleteIBAAttributes(WTDocument doc) throws Exception {
//		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(doc, null, null, null);
//		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
//		AbstractValueView[] avv = container.getAttributeValues();
//		ReferenceFactory rf = new ReferenceFactory();
//		for (int i = 0; i < avv.length; i++) {
//			StringValue sv = (StringValue) rf.getReference(avv[i].getObjectID().getStringValue()).getObject();
//			PersistenceHelper.manager.delete(sv);
//		}
//	}

	/**
	 * 문서 링크 데이터 삭제
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
	private void saveAttach(WTDocument doc, DocumentDTO dto) throws Exception {
		String primary = dto.getPrimary();
		ArrayList<String> secondarys = dto.getSecondarys();

		if (StringUtil.checkString(primary)) {
			File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
			ApplicationData applicationData = ApplicationData.newApplicationData(doc);
			applicationData.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(applicationData);
			ContentServerHelper.service.updateContent(doc, applicationData, vault.getPath());
		}

		for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
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
	private void removeAttach(WTDocument doc) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(doc, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(doc, item);
		}
	}

	@Override
	public void force(DocumentDTO dto) throws Exception {
		String oid = dto.getOid();
		String content = dto.getContent();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
			doc.getTypeInfoWTDocument().setPtc_rht_1(content);
			PersistenceServerHelper.manager.update(doc);

			doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
			removeAttach(doc);

			saveAttach(doc, dto);

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
	public void move(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) params.get("data");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, String> map : data) {
				String oid = map.get("oid");
				String foid = map.get("foid");

				WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
				Folder folder = (Folder) CommonUtil.getObject(foid);
				FolderHelper.service.changeFolder((FolderEntry) doc, folder);
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
	public void createCover(WTDocument doc) throws Exception {
		System.out.println("커버 생성");
		File excelFile = null;
		File rtnFile = null;
		String codebase = WTProperties.getLocalProperties().getProperty("wt.codebase.location");
		String preFixPath = codebase + File.separator + "extcore" + File.separator + "jsp" + File.separator + "document"
				+ File.separator + "cover";
		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean checker = true;

			QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
			if (qr.hasMoreElements()) {
				ApplicationData dd = (ApplicationData) qr.nextElement();
				String n = dd.getFileName();
				String ext = FileUtil.getExtension(n);
				System.out.println("ext=" + ext);
				if (!"docx".equalsIgnoreCase(ext) && !"doc".equalsIgnoreCase(ext)) {
					System.out.println("대상파일이 아닙니다.");
					checker = false;
				}
			}

			// 없을 경우도 패스
			if (qr.size() == 0) {
				checker = false;
			}

			if (checker) {
				WTDocumentTypeInfo info = doc.getTypeInfoWTDocument();
				if (info != null) {
					String classType1Code = info.getPtc_str_2();

					// 과거 데이터는 없음
					if (StringUtil.checkString(classType1Code)) {
						// 개발문서 일 경우
						if ("DEV".equals(classType1Code) || "INSTRUCTION".equals(classType1Code)) {
							excelFile = new File(preFixPath + File.separator + "DMR.xlsx");
							rtnFile = DocumentHelper.manager.stamping(doc, excelFile, classType1Code);
						}
					}

					String temp = WTProperties.getLocalProperties().getProperty("wt.temp");
					String pdfPath = temp + File.separator + "pdf";
					File f = new File(pdfPath);
					if (!f.exists()) {
						f.mkdirs();
					}
					String output = pdfPath + File.separator + doc.getNumber() + "_COVER.pdf";
					// excel to pdf
					Workbook wb = new Workbook(rtnFile.getAbsolutePath());
					wb.save(output, FileFormatType.PDF);

					// 기존 커버 삭제
					QueryResult result = ContentHelper.service.getContentsByRole(doc,
							ContentRoleType.toContentRoleType("COVER"));
					if (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						ContentServerHelper.service.deleteContent(doc, data);
					}

					// save
					File pdfFile = new File(output);
					ApplicationData applicationData = ApplicationData.newApplicationData(doc);
					applicationData.setRole(ContentRoleType.toContentRoleType("COVER"));
					PersistenceHelper.manager.save(applicationData);
					ContentServerHelper.service.updateContent(doc, applicationData, pdfFile.getPath());

					// pdf merge
					DocumentHelper.manager.mergePdf(doc);
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

	@Override
	public void publish(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

			// 기존에 첨부된 PDF삭제
			QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.toContentRoleType("PDF"));
			if (qr.hasMoreElements()) {
				ApplicationData dd = (ApplicationData) qr.nextElement();
				ContentServerHelper.service.deleteContent(doc, dd);
			}

			Hashtable<String, String> hash = new Hashtable<>();
			hash.put("oid", oid);
			AsposeUtils.wordToPdf(hash);

			// 재생성 시작
			createCover(doc);

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
	public void forceWorkData(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

			WorkData wd = WorkDataHelper.manager.getWorkData(doc);
			if (wd != null) {
				throw new Exception("이미 결재가 진행 중입니다.");
			}

			WorkDataHelper.service.create(doc);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		}
	}
}
