package com.e3ps.mold.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.mold.dto.MoldDTO;
import com.e3ps.workspace.service.WorkspaceHelper;

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
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardMoldService extends StandardManager implements MoldService {

	public static StandardMoldService newStandardMoldService() throws WTException {
		final StandardMoldService instance = new StandardMoldService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(MoldDTO dto) throws Exception {
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String lifecycle = dto.getLifecycle();
		boolean temprary = dto.isTemprary();

		// 결재
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();

		Transaction trs = new Transaction();
		try {
			trs.start();

			DocumentType docType = DocumentType.toDocumentType("$$MMDocument");
			String number = getDocumentNumberSeq(docType.getLongDescription());
			WTDocument doc = WTDocument.newWTDocument();
			doc.setDocType(docType);
			doc.setName(name);
			doc.setDescription(description);
			doc.setNumber(number);

			// 문서 분류쳬게 설정
			Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) doc, folder);

			// 문서 Container 설정
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			doc.setContainer(e3psProduct);

			// 문서 lifeCycle 설정
			LifeCycleHelper.setLifeCycle(doc, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); // Lifecycle

			doc = (WTDocument) PersistenceHelper.manager.save(doc);

			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(doc, state);
			}

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
	private void saveAttach(WTDocument doc, MoldDTO dto) throws Exception {
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

	/**
	 * 문서 IBA 속성값 세팅 함수
	 */
	private void setIBAAttributes(WTDocument doc, MoldDTO dto) throws Exception {
		// 내부 문서 번호
		String interalnumber = dto.getInteralnumber();
		// IBA 키값 어떻게 할지...
		dto.setIBAValue(doc, interalnumber, "INTERALNUMBER");
		// 협력업체
		String manufacture_code = dto.getManufacture_code();
		dto.setIBAValue(doc, manufacture_code, "MANUFACTURE");
		// 금형타입
		String moldtype_code = dto.getMoldtype_code();
		dto.setIBAValue(doc, moldtype_code, "MOLDTYPE");
		// 금형번호
		String moldnumber = dto.getMoldnumber();
		dto.setIBAValue(doc, moldnumber, "MOLDNUMBER");
		// 금형개발비
		String moldcost = dto.getMoldcost();
		dto.setIBAValue(doc, moldcost, "MOLDCOST");
		// 부서
		String deptcode_code = dto.getDeptcode_code();
		dto.setIBAValue(doc, deptcode_code, "DEPTCODE");
		// 결재 유형
		String approvalType_code = dto.getLifecycle().equals("LC_Default") ? "DEFAULT" : "BATCH";
		dto.setIBAValue(doc, approvalType_code, "APPROVALTYPE");
	}

	/**
	 * 문서 관련 객체 저장
	 */
	private void saveLink(WTDocument doc, MoldDTO dto) throws Exception {
		ArrayList<Map<String, String>> partList = dto.getPartList();
		// 관련품목
		for (Map<String, String> map : partList) {
			String oid = map.get("oid");
			WTPart part = (WTPart) CommonUtil.getObject(oid);
			WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
			PersistenceServerHelper.manager.insert(link);
		}

		ArrayList<Map<String, String>> docList = dto.getDocList();
		// 관련문서
		for (Map<String, String> map : docList) {
			String oid = map.get("oid");
			WTDocument ref = (WTDocument) CommonUtil.getObject(oid);
			DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(doc, ref);
			PersistenceServerHelper.manager.insert(link);
		}
	}

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
		QueryResult result2 = PersistenceHelper.manager.navigate(workCopy, "used", DocumentToDocumentLink.class, false);
		while (result2.hasMoreElements()) {
			DocumentToDocumentLink link = (DocumentToDocumentLink) result2.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}

		QueryResult result3 = PersistenceHelper.manager.navigate(workCopy, "useBy", DocumentToDocumentLink.class,
				false);
		while (result3.hasMoreElements()) {
			DocumentToDocumentLink link = (DocumentToDocumentLink) result3.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	@Override
	public void update(MoldDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		String iterationNote = dto.getIterationNote();
		boolean temprary = dto.isTemprary();

		// 결재
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();

		WTDocument doc = null;
		WTDocument olddoc = null;

		Transaction trs = new Transaction();
		try {
			trs.start();
			olddoc = (WTDocument) CommonUtil.getObject(oid);
			// Working Copy
			doc = (WTDocument) getWorkingCopy(olddoc);
			doc.setDescription(description);
			doc = (WTDocument) PersistenceHelper.manager.modify(doc);

			// 첨부 파일 클리어
			removeAttach(doc);
			// 첨부파일 저장
			saveAttach(doc, dto);

			doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

			// CheckedOut
			if (WorkInProgressHelper.isCheckedOut(doc)) {
				doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, iterationNote);
			}

			// 링크 정보 삭제
			deleteLink(doc);
			// 관련 링크 세팅
			saveLink(doc, dto);

			// IBA 설정
			setIBAAttributes(doc, dto);

			doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

			if (location.length() != 0) {
				Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
				doc = (WTDocument) FolderHelper.service.changeFolder((FolderEntry) doc, folder);
			}

			// 임시저장 하겠다 한 경우
			if (temprary) {
				State state = State.toState("TEMPRARY");
				// 상태값 변경해준다 임시저장 <<< StateRB 추가..
				LifeCycleHelper.service.setLifeCycleState(doc, state);
			} else {
				State state = State.toState("INWORK");
				LifeCycleHelper.service.setLifeCycleState(doc, state);
			}

			WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			// 문서 이름 세팅..
			if ("$$MMDocument".equals(doc.getDocType().toString())) {
				identity.setName(name);
			} else {
				if (name.length() > 0) {
					identity.setName(doc.getDocType().getDisplay() + "-" + name);
				} else {
					identity.setName(doc.getDocType().getDisplay());
				}
			}

			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

//			// 결재시작
//			if (approvalRows.size() > 0) {
//				WorkspaceHelper.service.register(doc, agreeRows, approvalRows, receiveRows);
//			}

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

	private WTDocument getWorkingCopy(WTDocument _doc) throws Exception {
		if (!WorkInProgressHelper.isCheckedOut(_doc)) {
			if (!CheckInOutTaskLogic.isCheckedOut(_doc)) {
				CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(_doc,
						CheckInOutTaskLogic.getCheckoutFolder(), "");
			}
			_doc = (WTDocument) WorkInProgressHelper.service.workingCopyOf(_doc);
		} else {
			if (!WorkInProgressHelper.isWorkingCopy(_doc)) {
				_doc = (WTDocument) WorkInProgressHelper.service.workingCopyOf(_doc);
			}
		}
		return _doc;
	}

	@Override
	public void revise(Map<String, Object> params) throws Exception {
		Transaction trx = new Transaction();
		try {
			trx.start();
			String oid = StringUtil.checkNull((String) params.get("oid"));
			if (oid.length() > 0) {
				WTDocument oldDoc = (WTDocument) CommonUtil.getObject(oid);
				String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
				WTDocument doc = (WTDocument) ObjectUtil.revise(oldDoc, lifecycle);

				doc = (WTDocument) PersistenceHelper.manager.save(doc);

				QueryResult qr = PersistenceHelper.manager.navigate(oldDoc, "describes", WTPartDescribeLink.class);
				while (qr.hasMoreElements()) {
					WTPart part = (WTPart) qr.nextElement();
					WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
					PersistenceServerHelper.manager.insert(link);
				}

				List<MoldDTO> useByList = MoldHelper.manager.getDocumentListToLinkRoleName(oldDoc, "useBy");
				for (MoldDTO dto : useByList) {
					WTDocument dtoDoc = (WTDocument) CommonUtil.getObject(dto.getOid());
					DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(doc, dtoDoc);
					PersistenceServerHelper.manager.insert(link);
				}

				List<MoldDTO> usedList = MoldHelper.manager.getDocumentListToLinkRoleName(oldDoc, "used");
				for (MoldDTO dto : usedList) {
					WTDocument dtoDoc = (WTDocument) CommonUtil.getObject(dto.getOid());
					DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(dtoDoc, doc);
					PersistenceServerHelper.manager.insert(link);
				}

				String approvalType = AttributeKey.CommonKey.COMMON_DEFAULT; // 일괄결재 Batch,기본결재 Default
				if ("LC_Default_NonWF".equals(lifecycle)) {
					E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) doc, "BATCHAPPROVAL");
					approvalType = AttributeKey.CommonKey.COMMON_BATCH;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("approvalType", approvalType);
				CommonHelper.service.changeIBAValues(doc, map);
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			trx.rollback();
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

}
