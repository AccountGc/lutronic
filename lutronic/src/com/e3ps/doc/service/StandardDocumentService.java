package com.e3ps.doc.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentECPRLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.groupware.workprocess.AppPerLink;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentHelper;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.enterprise.RevisionControlled;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardDocumentService extends StandardManager implements DocumentService {

	public static StandardDocumentService newStandardDocumentService() throws WTException {
		final StandardDocumentService instance = new StandardDocumentService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> requestDocumentMapping(Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();

		String oid = StringUtil.checkNull((String) params.get("oid"));
		String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));

		String location = StringUtil.checkNull((String) params.get("location"));
		String documentName = StringUtil.checkNull((String) params.get("documentName"));
		String docName = StringUtil.checkNull((String) params.get("docName"));

		String documentType = StringUtil.checkNull((String) params.get("documentType"));
		String model = StringUtil.checkNull((String) params.get("model"));
		String preseration = StringUtil.checkNull((String) params.get("preseration"));
		String interalnumber = StringUtil.checkNull((String) params.get("interalnumber"));
		String deptcode = StringUtil.checkNull((String) params.get("deptcode"));
		String writer = StringUtil.checkNull((String) params.get("writer"));

		String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
		String moldType = StringUtil.checkNull((String) params.get("moldtype"));
		String moldNumber = StringUtil.checkNull((String) params.get("moldnumber"));
		String moldCost = StringUtil.checkNull((String) params.get("moldcost"));

		String description = StringUtil.checkNull((String) params.get("description"));
		String iterationNote = StringUtil.checkNull((String) params.get("iterationNote"));

		String primary = StringUtil.checkNull((String) params.get("PRIMARY"));
		String[] secondary = (String[]) params.get("SECONDARY");
		String[] delocIds = (String[]) params.get("delocIds");

		String[] partOids = (String[]) params.get("partOid");
		String[] activeOids = (String[]) params.get("activeOid");
		String[] docOids = (String[]) params.get("docOid");

		String type = StringUtil.checkNull((String) params.get("type"));
		String parentOid = StringUtil.checkNull((String) params.get("parentOid"));

		map.put("oid", oid);
		map.put("lifecycle", lifecycle);

		map.put("location", location);
		map.put("docName", docName);
		map.put("documentName", documentName);

		map.put("documentType", documentType);
		map.put("model", model);
		map.put("preseration", preseration);
		map.put("interalnumber", interalnumber);
		map.put("deptcode", deptcode);
		map.put("writer", writer);

		map.put("manufacture", manufacture);
		map.put("moldType", moldType);
		map.put("moldNumber", moldNumber);
		map.put("moldCost", moldCost);
		map.put("interalnumber", interalnumber);

		map.put("description", description);
		map.put("iterationNote", iterationNote);

		map.put("primary", primary);
		map.put("secondary", secondary);
		map.put("delocIds", delocIds);

		map.put("partOids", partOids);
		map.put("activeOids", activeOids);
		map.put("docOids", docOids);

		map.put("linkType", type);
		map.put("parentOid", parentOid);

		return map;
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
	public List<DocumentDTO> include_documentLink(String module, String oid) {
		List<DocumentDTO> list = new ArrayList<DocumentDTO>();

		try {
			if (StringUtil.checkString(oid)) {
				if ("active".equals(module)) {
					devActive m = (devActive) CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(m, "output", devOutPutLink.class, false);

					while (qr.hasMoreElements()) {
						devOutPutLink link = (devOutPutLink) qr.nextElement();
						RevisionControlled rc = link.getOutput();
						String linkOid = CommonUtil.getOIDString(link);

						if (rc instanceof WTDocument) {
							DocumentDTO data = new DocumentDTO((WTDocument) rc);
							data.setLinkOid(linkOid);
							list.add(data);
						}

					}
				} else if ("ecaAction".equals(module)) {
					EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(oid);
					List<DocumentActivityLink> linkList = ECAHelper.service.getECADocumentLink(eca);
					for (DocumentActivityLink link : linkList) {
						WTDocumentMaster master = link.getDoc();

						String linkOid = CommonUtil.getOIDString(link);
						WTDocument doc = DocumentHelper.service.getLastDocument(master.getNumber());

						DocumentDTO data = new DocumentDTO(doc);
						data.setLinkOid(linkOid);
						list.add(data);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
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
	public void create(DocumentDTO dto) throws Exception {
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
				DocumentECOLink  link = DocumentECOLink .newDocumentECOLink(doc, eco);
				PersistenceServerHelper.manager.insert(link);
			}
		}
	}

	/**
	 * 문서 IBA 속성값 세팅 함수
	 */
	private void setIBAAttributes(WTDocument doc, DocumentDTO dto) throws Exception {
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

	@Override
	public void register(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String searchType = StringUtil.checkNull((String) params.get("searchType"));
			String description = StringUtil.checkNull((String) params.get("description"));
			String number = "";
			if (searchType.length() > 0) {
				if ("DOC".equals(searchType)) {
					number = "NDBT";
				} else if ("ROHS".equals(searchType)) {
					number = "ROHSBT";
				} else if ("MOLD".equals(searchType)) {
					number = "MMBT";
				}
			}
			String today = DateUtil.getDateString(new Date(), new SimpleDateFormat("yyyyMM"));
			number = number.concat("-").concat(today).concat("-");
			String seqNo = SequenceDao.manager.getSeqNo(number, "000", "WTDocumentMaster", "WTDocumentNumber");
			number = number + seqNo;

			AsmApproval asm = AsmApproval.newAsmApproval();

			String appName = StringUtil.checkNull((String) params.get("appName"));
			asm.setNumber(number);
			asm.setName(appName);
			asm.setDescription(description);

			// 문서 분류쳬게 설정
			Folder folder = FolderTaskLogic.getFolder("/Default/AsmApproval", WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) asm, folder);

			// 문서 lifeCycle 설정
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			LifeCycleHelper.setLifeCycle(asm,
					LifeCycleHelper.service.getLifeCycleTemplate("LC_Default", wtContainerRef)); // Lifecycle

			asm = (AsmApproval) PersistenceHelper.manager.save(asm);

			ArrayList<Map<String, Object>> gridList = (ArrayList<Map<String, Object>>) params.get("gridList");

			for (Map<String, Object> map : gridList) {
				String docOid = (String) map.get("oid");
				WTDocument doc = (WTDocument) CommonUtil.getObject(docOid);

				AppPerLink link = AppPerLink.newAppPerLink(doc, asm);
				E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) doc, asm.getLifeCycleState().toString());
				PersistenceServerHelper.manager.insert(link);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null) {
				trs.rollback();
			}
		}
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
	public void modify(DocumentDTO dto) throws Exception {
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
