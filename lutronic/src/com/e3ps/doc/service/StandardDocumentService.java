package com.e3ps.doc.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.service.ECAHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.comments.Comments;
import com.e3ps.common.content.FileRequest;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.POIUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.web.WebUtil;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.development.service.DevelopmentHelper;
import com.e3ps.development.service.DevelopmentQueryHelper;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.template.DocumentTemplate;
import com.e3ps.groupware.workprocess.AppPerLink;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.groupware.workprocess.service.AsmSearchHelper;
import com.e3ps.groupware.workprocess.service.WFItemHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.VersionHelper;

import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.DBProperties;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardDocumentService extends StandardManager implements DocumentService {

	public static final String DOC_LifeCycle = "LC_Default";

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

	public WTDocument createAction(Map<String, Object> map) throws Exception {
		String location = StringUtil.checkNull((String) map.get("location"));
		String documentName = StringUtil.checkNull((String) map.get("documentName"));
		String docName = StringUtil.checkNull((String) map.get("docName"));
		String description = StringUtil.checkNull((String) map.get("description"));

		String documentType = StringUtil.checkNull((String) map.get("documentType"));

		DocumentType docType = DocumentType.toDocumentType(documentType);
		String number = getDocumentNumberSeq(docType.getLongDescription());

		// 문서 기본 정보 설정
		WTDocument doc = WTDocument.newWTDocument();
		if ("$$MMDocument".equals(documentType)) {
			doc.setName(docName);
		} else {
			if (docName.length() > 0) {
				doc.setName(documentName + "-" + docName);
			} else {
				doc.setName(documentName);
			}
		}
		doc.setDescription(description);
		doc.setNumber(number);
		doc.setDocType(docType);

		// 문서 분류쳬게 설정
		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
		FolderHelper.assignLocation((FolderEntry) doc, folder);

		// 문서 Container 설정
		PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
		WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
		doc.setContainer(e3psProduct);

		// 문서 lifeCycle 설정
		String lifecycle = StringUtil.checkNull((String) map.get("lifecycle"));
		LifeCycleHelper.setLifeCycle(doc, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); // Lifecycle

		doc = (WTDocument) PersistenceHelper.manager.save(doc);

		String primary = StringUtil.checkNull((String) map.get("primary"));
		String[] secondary = (String[]) map.get("secondary");

		CommonContentHelper.service.attach((ContentHolder) doc, primary, secondary);

		// 관련 부품
		String[] partOids = (String[]) map.get("partOids");
		updateDocumentToPartLink(doc, partOids, false);

		// 관련 문서
		String[] docOids = (String[]) map.get("docOids");
		updateDocumentToDocumentLink(doc, docOids, false);

		String approvalType = AttributeKey.CommonKey.COMMON_DEFAULT; // 일괄결재 Batch,기본결재 Default
		if ("LC_Default_NonWF".equals(lifecycle)) {
			E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) doc, "BATCHAPPROVAL");
			approvalType = AttributeKey.CommonKey.COMMON_BATCH;
		}
		map.put("approvalType", approvalType);
		CommonHelper.service.changeIBAValues(doc, map);

		// 산출물 직접 등록(개발업무 관리,설계 변경 관리) 문서 직접등록 시 링크 생성
		String linkType = (String) map.get("linkType");
		String parentOid = (String) map.get("parentOid");
		createLinkDocument(doc, linkType, parentOid);

		/*
		 * if(linkType.length() > 0){ if("active".equals(linkType)) {
		 * 
		 * DevelopmentHelper.service.createDocumentToDevelopmentLink(doc, parentOid); }
		 * }
		 */

		return doc;
	}

	@Override
	public ResultData createDocumentAction(Map<String, Object> map) {
		ResultData result = new ResultData();

		Transaction trx = new Transaction();
		WTDocument doc = null;

		try {
			trx.start();

			doc = createAction(map);

			trx.commit();
			trx = null;
			result.setResult(true);
			result.setOid(CommonUtil.getOIDString(doc));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResult(false);
			result.setMessage(e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return result;
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
	public Map<String, Object> listDocumentAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);

		String sessionId = request.getParameter("sessionId");

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		String select = StringUtil.checkReplaceStr(request.getParameter("select"), "false");

		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) o[0];
			DocumentData data = new DocumentData(doc);

			xmlBuf.append("<row id='" + data.oid + "'>");
			if ("true".equals(select)) {
				xmlBuf.append("<cell><![CDATA[]]></cell>");
			}

			if (data.getDocumentType().equals("금형문서")) {
				xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + data.oid + "')>" + data.name
						+ "</a>]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.version + "." + data.iteration + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.getLifecycle() + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.createDate.substring(0, 10) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.modifyDate.substring(0, 10) + "]]></cell>");
			} else {
				xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>");
				xmlBuf.append(
						"<cell><![CDATA[" + data.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.getIBAValue(AttributeKey.IBAKey.IBA_MODEL) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + data.oid + "')>" + data.name
						+ "</a>]]></cell>");
				xmlBuf.append(
						"<cell><![CDATA[" + data.location.substring(("/Default/Document").length()) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.version + "." + data.iteration + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.getLifecycle() + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.getIBAValue(AttributeKey.IBAKey.IBA_DSGN) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.createDate.substring(0, 10) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + data.modifyDate.substring(0, 10) + "]]></cell>");
			}

			xmlBuf.append("</row>");

		}
		xmlBuf.append("</rows>");

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("formPage", formPage);
		result.put("rows", rows);
		result.put("totalPage", totalPage);
		result.put("startPage", startPage);
		result.put("endPage", endPage);
		result.put("listCount", listCount);
		result.put("totalCount", totalCount);
		result.put("currentPage", currentPage);
		result.put("param", param);
		result.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
		result.put("xmlString", xmlBuf);

		return result;

	}

	/**
	 * AUI 리스트
	 */
	@Override
	public List<Map<String, Object>> listAUIDocumentAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);
		QueryResult qr = PersistenceHelper.manager.find(query);

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		HashMap<String, String> verMap = new HashMap<String, String>();
		while (qr.hasMoreElements()) {
			Map<String, Object> result = new HashMap<String, Object>();
			Object[] o = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) o[0];
			DocumentData data = new DocumentData(doc);

			int kk = 0;
			if (verMap.containsKey(data.number)) {

				kk = verMap.get(data.number).compareTo(data.version);
				if (kk > 0) {
					continue;
				} else {
					verMap.put(data.number, data.version);
				}
			} else {
				verMap.put(data.number, data.version);
			}

			result.put("number", data.number);
			result.put("interNumber", data.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER));
			result.put("model", data.getIBAValue(AttributeKey.IBAKey.IBA_MODEL));
			result.put("name", data.name);
			result.put("oid", data.oid);
			result.put("location", data.getLocation());
			result.put("version", data.version);
			result.put("rev", data.version + "." + data.iteration);
			result.put("state", data.getLifecycle());
			result.put("creator", data.creator);
			result.put("createDate", data.createDate.substring(0, 10));
			result.put("modifyDate", data.modifyDate.substring(0, 10));

			resultList.add(result);
		}

		return resultList;

	}

	@Override
	public Map<String, Object> listPagingAUIDocumentAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> map = new HashMap<>();

		int page = StringUtil.getIntParameter((String) request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter((String) request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) request.getParameter("formPage"), 15);

		String sessionId = (String) request.getParameter("sessionId");

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {

			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);

			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);

		}

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();
		long sessionIdLong = control.getSessionId();

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		HashMap<String, String> verMap = new HashMap<String, String>();
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) o[0];
			DocumentData data = new DocumentData(doc);
			Map<String, Object> result = new HashMap<String, Object>();

			int kk = 0;
			if (verMap.containsKey(data.number)) {

				kk = verMap.get(data.number).compareTo(data.version);
				if (kk > 0) {
					continue;
				} else {
					verMap.put(data.number, data.version);
				}
			} else {
				verMap.put(data.number, data.version);
			}

			result.put("number", data.number);
			result.put("interNumber", data.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER));
			result.put("model", data.getIBAValue(AttributeKey.IBAKey.IBA_MODEL));
			result.put("name", data.name);
			result.put("oid", data.oid);
			result.put("location", data.getLocation());
			result.put("version", data.version);
			result.put("rev", data.version + "." + data.iteration);
			result.put("state", data.getLifecycle());
			result.put("creator", data.creator);
			result.put("createDate", data.createDate.substring(0, 10));
			result.put("modifyDate", data.modifyDate.substring(0, 10));

			resultList.add(result);
		}

		map.put("list", resultList);
		map.put("totalPage", totalPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("listCount", listCount);
		map.put("totalCount", totalCount);
		map.put("currentPage", currentPage);
		map.put("param", param);
		map.put("rowCount", rowCount);
		map.put("sessionId", sessionIdLong);

		return map;

	}

	@Override
	public Map<String, Object> deleteDocumentAction(Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");

		try {
			ReferenceFactory f = new ReferenceFactory();
			WTDocument wtdoc = (WTDocument) CommonUtil.getObject(oid);

			if (WorkInProgressHelper.isCheckedOut(wtdoc)) {
				result.put("msg", Message.get("체크아웃되어 있어서 삭제하실 수 없습니다."));
				result.put("result", false);
			} else {
				result.put("msg", DocumentHelper.service.delete(oid));
				result.put("result", true);
				// data.setMessage(Message.get("삭제 되었습니다."));
			}
		} catch (Exception e) {
			result.put("result", false);
			result.put("msg", e.getLocalizedMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Map<String, Object> updateDocumentAction(Map<String, Object> map) {
		Map<String, Object> result = new HashMap<String, Object>();

		Transaction trx = new Transaction();

		WTDocument doc = null;
		WTDocument olddoc = null;

		try {
			String oid = StringUtil.checkNull((String) map.get("oid"));
			String location = StringUtil.checkNull((String) map.get("location"));

			String documentName = StringUtil.checkNull((String) map.get("documentName"));
			String docName = StringUtil.checkNull((String) map.get("docName"));

			String description = StringUtil.checkNull((String) map.get("description"));
			String iterationNote = StringUtil.checkNull((String) map.get("iterationNote"));

			String primary = StringUtil.checkNull((String) map.get("primary"));
			String[] secondary = (String[]) map.get("secondary");
			String[] delocIds = (String[]) map.get("delocIds");

			ReferenceFactory f = new ReferenceFactory();
			if (oid != null) {
				olddoc = (WTDocument) CommonUtil.getObject(oid);
				// Working Copy
				doc = (WTDocument) getWorkingCopy(olddoc);
				doc.setDescription(description);

				doc = (WTDocument) PersistenceHelper.manager.modify(doc);

				CommonContentHelper.service.attach((ContentHolder) doc, primary, secondary, delocIds);

				doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

				// CheckedOut
				if (WorkInProgressHelper.isCheckedOut(doc)) {
					doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, iterationNote);
				}

				// 관련부품 링크 수정
				String[] partOids = (String[]) map.get("partOids");
				updateDocumentToPartLink(doc, partOids, true);

				// 관련 개별업무 관리 링크 수정
				// String[] activeOids = (String[]) map.get("activeOids");
				// updateDocumentToDevelopmentLink(doc, activeOids, true);

				// 관련문서 링크 수정
				String[] docOids = (String[]) map.get("docOids");
				updateDocumentToDocumentLink(doc, docOids, true);

				CommonHelper.service.changeIBAValues((IBAHolder) doc, map);

				doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
				if (location.length() != 0) {
					Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
					doc = (WTDocument) FolderHelper.service.changeFolder((FolderEntry) doc, folder);
				}
				if (StringUtil.checkString(docName)) {
					documentName = documentName + "-" + docName;
				} else {
					documentName = documentName;
				}
				if (!doc.getName().equals(documentName)) {
					if ("$$MMDocument".equals(doc.getDocType().toString())) {
						documentName = docName;
					}
					// System.out.println("documentName =" + documentName);

					docReName(doc, documentName);
					/*
					 * WTDocumentMaster docMaster = (WTDocumentMaster)(doc.getMaster());
					 * WTDocumentMasterIdentity identity =
					 * (WTDocumentMasterIdentity)docMaster.getIdentificationObject();
					 * identity.setNumber(doc.getNumber());
					 * if("$$MMDocument".equals(doc.getDocType().toString())) {
					 * identity.setName(docName); }else { if(StringUtil.checkString(docName)) {
					 * identity.setName(documentName + "-" + docName); }else {
					 * identity.setName(documentName); } } docMaster =
					 * (WTDocumentMaster)IdentityHelper.service.changeIdentity(docMaster, identity);
					 */
				}
			}
			trx.commit();
			trx = null;
			result.put("oid", CommonUtil.getOIDString(doc));
			result.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", e.getLocalizedMessage());
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return result;

	}

	@Override
	public String createPackageDocumentAction(HttpServletRequest request, HttpServletResponse response) {

		Transaction trx = new Transaction();
		boolean validation = true;
		int failCount = 0;

		Map<String, String> fileMap = new HashMap<String, String>();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		try {
			trx.start();

			FileRequest req = new FileRequest(request);
			String excelFile = req.getFileLocation("excelFile");

			File file = new File(excelFile);
			XSSFWorkbook workbook = POIUtil.getWorkBook(file);
			XSSFSheet sheet = POIUtil.getSheet(workbook, 0);

			String[] secondary = req.getParameterValues("SECONDARY");

			if (secondary != null) {
				for (String attachFile : secondary) {
					String fileName = attachFile.split("/")[1].toUpperCase();
					if (fileMap.get(fileName) == null) {
						// System.out.println("attachFile add secondary ="+attachFile);

						fileMap.put(fileName, attachFile);
					} else {
						fileMap.remove(fileName);
					}
				}
			}

			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

			for (int i = 1; i < POIUtil.getSheetRow(sheet); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				XSSFRow row = sheet.getRow(i);

				validation = true;
				String fail = "";

				// NO
				String no = StringUtil.checkNull(POIUtil.getRowStringValue(row, 0));

				if (no.length() > 0) {

					map.put("index", (i + 1));

					// 문서 종류
					String documentName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 1));
					if (documentName.length() == 0) {
						validation = false;
						fail = Message.get("문서 종류가 없습니다.");
					}
					map.put("documentName", documentName);

					// 문서명
					String docName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 2));
					map.put("docName", docName);

					// 결재 방식
					String lifecycleName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 3));
					String lifecycle = "";
					if (lifecycleName.length() == 0) {
						validation = false;
						fail = Message.get("결재방식이 없습니다.");
					} else {
						if ("기본결재".equals(lifecycleName)) {
							lifecycle = "LC_Default";
						} else if ("일괄결재".equals(lifecycleName)) {
							lifecycle = "LC_Default_NonWF";
						}
					}
					map.put("lifecycleName", lifecycleName);
					map.put("lifecycle", lifecycle);

					// 문서분류, 문서 코드
					String documentTypeName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 4));
					String documentType = StringUtil.checkNull(POIUtil.getRowStringValue(row, 5));
					if (documentType.length() == 0) {
						validation = false;
						fail = Message.get("문서분류가 없습니다.");
					} else {
						DocumentType type = DocumentType.toDocumentType(documentType);
						if (type == null) {
							validation = false;
							fail = Message.get("등록되지 않은 문서 분류입니다.");
						}
					}
					map.put("documentTypeName", documentTypeName);
					map.put("documentType", documentType);

					// 프로젝트명, 프로젝트코드
					String modelName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 6));
					String model = StringUtil.checkNull(POIUtil.getRowStringValue(row, 7));
					if (model.length() > 0) {
						NumberCode code = NumberCodeHelper.service.getNumberCode("MODEL", model);
						if (code == null) {
							validation = false;
							fail = Message.get("등록되지 않은 프로젝트 코드입니다.");
						}
					}
					map.put("modelName", modelName);
					map.put("model", model);

					// 작성자
					String writer = StringUtil.checkNull(POIUtil.getRowStringValue(row, 8));
					map.put("writer", writer);

					// 내부 문서 번호
					String interalnumber = StringUtil.checkNull(POIUtil.getRowStringValue(row, 9));
					map.put("interalnumber", interalnumber);

					// 부서명, 부서코드
					String deptcodeName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 10));
					String deptcode = StringUtil.checkNull(POIUtil.getRowStringValue(row, 11));
					if (deptcode.length() > 0) {
						NumberCode code = NumberCodeHelper.service.getNumberCode("DEPTCODE", deptcode);
						if (code == null) {
							validation = false;
							fail = Message.get("등록되지 않은 부서 코드입니다.");
						}
					}
					map.put("deptcodeName", deptcodeName);
					map.put("deptcode", deptcode);

					// 보존기간
					String preserationName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 12));
					String preseration = StringUtil.checkNull(POIUtil.getRowStringValue(row, 13));
					if (preseration.length() == 0) {
						validation = false;
						fail = Message.get("보존기간이 없습니다.");
					} else {
						NumberCode code = NumberCodeHelper.service.getNumberCode("PRESERATION", preseration);
						if (code == null) {
							validation = false;
							fail = Message.get("등록되지 않은 보존기간 코드입니다.");
						}
					}
					map.put("preserationName", preserationName);
					map.put("preseration", preseration);

					// 분류체계
					String location = StringUtil.checkNull(POIUtil.getRowStringValue(row, 14));
					if (location.length() == 0) {
						validation = false;
						fail = Message.get("문서분류가 없습니다.");
					} else {
						if ("/Default/Document".equals(location.trim())) {
							validation = false;
							fail = Message.get("최상위 문서분류에는 등록할 수 없습니다.");
						} else {
							Folder folder = FolderTaskLogic.getFolder(location.trim(), WCUtil.getWTContainerRef());
							if (folder == null) {
								validation = false;
								fail = Message.get("등록되지 않은 문서분류입니다.");
							}
						}
					}
					map.put("location", location);

					// 파일명
					String fileName = StringUtil.checkNull(POIUtil.getRowStringValue(row, 15));
					String primary = "";
					if (fileName.length() == 0) {
						validation = false;
						fail = Message.get("파일이 입력되지 않았습니다.");
					} else {
						if (fileMap.get(fileName.toUpperCase()) == null) {
							validation = false;
							map.put("validation", false);
							fail = Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
						} else {
							primary = fileMap.get(fileName.toUpperCase());
							fileMap.remove(fileName.toUpperCase());
						}
					}
					map.put("fileName", fileName);
					map.put("primary", primary);

					// 관련부품
					String partNumber = StringUtil.checkNull(POIUtil.getRowStringValue(row, 16));
					String[] partOids = null;
					if (partNumber.length() > 0) {
						String[] partNumbers = partNumber.split(",");
						partOids = new String[partNumbers.length];
						for (int j = 0; j < partNumbers.length; j++) {
							WTPart part = PartHelper.service.getPart(partNumbers[j]);
							if (part == null) {
								validation = false;
								fail = Message.get("해당 부품이 존재하지 않습니다.");
							} else {
								partOids[j] = CommonUtil.getOIDString(part);
							}
						}
					}
					map.put("partNumber", partNumber);
					map.put("partOids", partOids);

					map.put("validation", true);
					map.put("linkType", "");

					String oid = "";
					String fontColor = "";
					String lineValidation = "";

					if (validation) {
						try {
							WTDocument document = createAction(map);
							oid = CommonUtil.getOIDString(document);
							fontColor = "black";
							lineValidation = "true";
							documentName = document.getName();
						} catch (Exception e) {
							e.printStackTrace();
							fontColor = "red";
							lineValidation = "fail";
							fail = e.getLocalizedMessage();
							failCount = failCount + 1;
							documentName = documentName + (docName != "" ? "-" + docName : "");
						}

					} else {
						fontColor = "red";
						lineValidation = "fail";
						failCount = failCount + 1;
						documentName = documentName + (docName != "" ? "-" + docName : "");
					}

					map.put("oid", oid);
					map.put("fontColor", fontColor);
					map.put("lineValidation", lineValidation);
					map.put("fail", fail);

					resultList.add(map);
				}
			}

			for (Map<String, Object> map : resultList) {

				int index = (Integer) map.get("index");
				String documentName = (String) map.get("documentName");
				String docName = (String) map.get("docName");
				String documentTypeName = (String) map.get("documentTypeName");
				String lifecycleName = (String) map.get("lifecycleName");
				String documentType = (String) map.get("documentType");
				String modelName = (String) map.get("modelName");
				String model = (String) map.get("model");
				String interalnumber = (String) map.get("interalnumber");
				String deptcodeName = (String) map.get("deptcodeName");
				String deptcode = (String) map.get("deptcode");
				String preserationName = (String) map.get("preserationName");
				String preseration = (String) map.get("preseration");
				String location = (String) map.get("location");
				String fileName = (String) map.get("fileName");
				String partNumber = (String) map.get("partNumber");
				String fontColor = (String) map.get("fontColor");
				String lineValidation = (String) map.get("lineValidation");
				String fail = (String) map.get("fail");

				if (failCount == 0) {
					String oid = (String) map.get("oid");
					documentName = "<a href=javascript:openView('" + oid + "')>" + documentName + "</a>";
				}

				xmlBuf.append("<row id='" + index + "'>");
				xmlBuf.append("<cell><![CDATA[" + index + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + documentName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + WebUtil.getHtml(docName) + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + lifecycleName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + documentTypeName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + documentType + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + modelName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + model + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + interalnumber + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + deptcodeName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + deptcode + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + preserationName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + preseration + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + location.replaceAll("/Default/Document", "") + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + fileName + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[" + partNumber + "]]></cell>");
				xmlBuf.append("<cell><![CDATA[<font color='" + fontColor + "'>" + lineValidation + "</font>]]></cell>");
				xmlBuf.append("<cell><![CDATA[<font color='" + fontColor + "'>" + fail + "</font>]]></cell>");
				xmlBuf.append("</row>");
			}

			if (failCount == 0) {
				trx.commit();
				trx = null;
			} else {
				trx.rollback();
			}

		} catch (Exception e) {
			e.printStackTrace();
			xmlBuf.append("<row id='error'>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[<font color='red'>fail</font>]]></cell>");
			xmlBuf.append("<cell><![CDATA[<font color='red'>" + e.getLocalizedMessage() + "</font>]]></cell>");
			xmlBuf.append("</row>");
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		xmlBuf.append("</rows>");
		return xmlBuf.toString();
	}

	@Override
	public ResultData createAUIPackageDocumentAction(HttpServletRequest request, HttpServletResponse response) {

		Transaction trx = new Transaction();
		ResultData data = new ResultData();
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();

		try {
			trx.start();
			boolean totalValidation = true;
			FileRequest req = new FileRequest(request);

			Map<String, String> fileMap = new HashMap<String, String>();
			String[] secondary = req.getParameterValues("SECONDARY");
			// System.out.println("SECONDARY =" + secondary);
			if (secondary != null) {
				for (String attachFile : secondary) {
					String fileName = attachFile.split("/")[1].toUpperCase();
					// System.out.println("fileName = " + fileName);
					if (fileMap.get(fileName) == null) {
						fileMap.put(fileName, attachFile);
					} else {
						fileMap.remove(fileName);
					}
				}
			}

			JSONArray item_json = new JSONArray(req.getParameter("rowList"));

			// System.out.println("item_json.length() = " + item_json.length());

			for (int i = 0; i < item_json.length(); i++) {

				Map<String, String> returnMap = new HashMap<String, String>();
				// 문서 생성
				Map<String, Object> mapDoc = new HashMap<String, Object>();
				boolean validation = true;
				String msg = "";
				JSONObject item = item_json.getJSONObject(i);
				// System.out.println("item =" + item);
				String id = (String) item.get("id");
				String state = (String) item.get("state");

				if (state.equals("S")) {
					continue;
				}

				String documentName = (String) item.get("documentName");
				String docName = (String) item.get("docName");
				String lifecycle = (String) item.get("lifecycle");
				String documentType = (String) item.get("documentType");
				String model = (String) item.get("model");
				String writer = (String) item.get("writer");
				String interalnumber = (String) item.get("interalnumber");
				String deptcode = (String) item.get("deptcode");
				String preseration = (String) item.get("preseration");
				String location = (String) item.get("location");
				String folder = (String) item.get("folder");
				String fileName = (String) item.get("fileName");
				String attachmentFileName1 = StringUtil.checkNull((String) item.get("attachmentFileName1"));
				String attachmentFileName2 = StringUtil.checkNull((String) item.get("attachmentFileName2"));
				String attachmentFileName3 = StringUtil.checkNull((String) item.get("attachmentFileName3"));
				String attachmentFileName4 = StringUtil.checkNull((String) item.get("attachmentFileName4"));
				String attachmentFileName5 = StringUtil.checkNull((String) item.get("attachmentFileName5"));

				String partNumber = (String) item.get("partNumber");
				String description = (String) item.get("description");

				mapDoc.put("documentName", documentName);
				mapDoc.put("docName", docName);
				mapDoc.put("lifecycle", lifecycle);
				mapDoc.put("documentType", documentType);
				mapDoc.put("description", description);
				mapDoc.put("model", model);
				mapDoc.put("writer", writer);
				mapDoc.put("interalnumber", interalnumber);
				mapDoc.put("deptcode", deptcode);
				mapDoc.put("preseration", preseration);
				mapDoc.put("location", location);
				mapDoc.put("documentName", documentName);
				mapDoc.put("location", location);
				mapDoc.put("linkType", "");

				// 파일명
				String primary = "";
				if (fileName.length() == 0) {
					msg = Message.get("파일이 입력되지 않았습니다.");
					validation = false;
					totalValidation = false;
					returnMap.put("state", "F");
					returnMap.put("id", id);
					returnMap.put("msg", msg);
					returnList.add(returnMap);
					continue;
				} else {
					if (fileMap.get(fileName.toUpperCase()) == null) {
						msg = Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
						validation = false;
						totalValidation = false;
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					} else {
						primary = fileMap.get(fileName.toUpperCase());
						fileMap.remove(fileName.toUpperCase());
					}
				}
				mapDoc.put("fileName", fileName);
				mapDoc.put("primary", primary);
				StringBuffer sendarys = new StringBuffer();
				// 파일명
				if (!attachmentFileName1.isEmpty()) {
					if (attachmentFileName1.length() == 0) {
						msg = Message.get("파일이 입력되지 않았습니다.");
						validation = false;
						totalValidation = false;
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					} else {
						if (fileMap.get(attachmentFileName1.toUpperCase()) == null) {
							msg = Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
							validation = false;
							totalValidation = false;
							returnMap.put("state", "F");
							returnMap.put("id", id);
							returnMap.put("msg", msg);
							returnList.add(returnMap);
							continue;
						} else {
							sendarys.append(fileMap.get(attachmentFileName1.toUpperCase()) + "@");
							fileMap.remove(attachmentFileName1.toUpperCase());
						}
					}
					mapDoc.put("attachmentFileName1", attachmentFileName1);
				}
				// 파일명
				if (!attachmentFileName2.isEmpty()) {
					if (attachmentFileName2.length() == 0) {
						msg = Message.get("파일이 입력되지 않았습니다.");
						validation = false;
						totalValidation = false;
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					} else {
						if (fileMap.get(attachmentFileName2.toUpperCase()) == null) {
							msg = Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
							validation = false;
							totalValidation = false;
							returnMap.put("state", "F");
							returnMap.put("id", id);
							returnMap.put("msg", msg);
							returnList.add(returnMap);
							continue;
						} else {
							sendarys.append(fileMap.get(attachmentFileName2.toUpperCase()) + "@");
							fileMap.remove(attachmentFileName2.toUpperCase());
						}
					}
					mapDoc.put("attachmentFileName2", attachmentFileName2);
				}
				// 파일명
				if (!attachmentFileName3.isEmpty()) {
					if (attachmentFileName3.length() == 0) {
						msg = Message.get("파일이 입력되지 않았습니다.");
						validation = false;
						totalValidation = false;
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					} else {
						if (fileMap.get(attachmentFileName3.toUpperCase()) == null) {
							msg = Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
							validation = false;
							totalValidation = false;
							returnMap.put("state", "F");
							returnMap.put("id", id);
							returnMap.put("msg", msg);
							returnList.add(returnMap);
							continue;
						} else {
							sendarys.append(fileMap.get(attachmentFileName3.toUpperCase()) + "@");
							fileMap.remove(attachmentFileName3.toUpperCase());
						}
					}
					mapDoc.put("attachmentFileName3", attachmentFileName3);
				}
				// 파일명
				if (!attachmentFileName4.isEmpty()) {
					if (attachmentFileName4.length() == 0) {
						msg = Message.get("파일이 입력되지 않았습니다.");
						validation = false;
						totalValidation = false;
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					} else {
						if (fileMap.get(attachmentFileName4.toUpperCase()) == null) {
							msg = Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
							validation = false;
							totalValidation = false;
							returnMap.put("state", "F");
							returnMap.put("id", id);
							returnMap.put("msg", msg);
							returnList.add(returnMap);
							continue;
						} else {
							sendarys.append(fileMap.get(attachmentFileName4.toUpperCase()) + "@");
							fileMap.remove(attachmentFileName4.toUpperCase());
						}
					}
					mapDoc.put("attachmentFileName4", attachmentFileName4);
				}
				// 파일명
				if (!attachmentFileName5.isEmpty()) {
					if (attachmentFileName5.length() == 0) {
						msg = Message.get("파일이 입력되지 않았습니다.");
						validation = false;
						totalValidation = false;
						returnMap.put("state", "F");
						returnMap.put("id", id);
						returnMap.put("msg", msg);
						returnList.add(returnMap);
						continue;
					} else {
						if (fileMap.get(attachmentFileName5.toUpperCase()) == null) {
							msg = Message.get("파일이 첨부되지 않았거나, 파일명이 중복되었습니다.");
							validation = false;
							totalValidation = false;
							returnMap.put("state", "F");
							returnMap.put("id", id);
							returnMap.put("msg", msg);
							returnList.add(returnMap);
							continue;
						} else {
							sendarys.append(fileMap.get(attachmentFileName5.toUpperCase()) + "@");
							fileMap.remove(attachmentFileName5.toUpperCase());
						}
					}
					mapDoc.put("attachmentFileName5", attachmentFileName5);
				}
				String sendarsStr = sendarys.toString();
				if (sendarsStr.endsWith("|")) {
					sendarsStr = sendarsStr.substring(0, sendarsStr.length() - 1);
				}
				// System.out.println("sendarsStr="+sendarsStr);
				if (null != sendarsStr && sendarsStr.length() > 0) {
					String[] sendarsArray = sendarsStr.split("@");
					mapDoc.put("secondary", sendarsArray);
				}
				// System.out.println("fileName =" + fileName +",primary =" + primary);
				// 관련부품
				boolean checkPart = true;
				String[] partOids = null;
				if (partNumber.length() > 0) {
					String[] partNumbers = partNumber.split(",");
					partOids = new String[partNumbers.length];

					for (int j = 0; j < partNumbers.length; j++) {
						WTPart part = PartHelper.service.getPart(partNumbers[j]);
						// System.out.println("partNumbers+["+j+"]" + partNumbers[j] +","+part);
						if (part == null) {
							checkPart = false;
							// System.out.println("Part is Null partNumbers+["+j+"]" + partNumbers[j]
							// +","+part);
							msg = Message.get(partNumbers[j] + "의 부품이 존재하지 않습니다.");
							validation = false;
							totalValidation = false;
							returnMap.put("state", "F");
							returnMap.put("id", id);
							returnMap.put("msg", msg);
							returnList.add(returnMap);
							break;
						} else {
							partOids[j] = CommonUtil.getOIDString(part);
						}
					}
				}

				if (!checkPart) {
					continue;
				}

				mapDoc.put("partNumber", partNumber);
				mapDoc.put("partOids", partOids);

				try {
					WTDocument newDoc = createAction(mapDoc);
					// System.out.println(id+" newDoc =" + newDoc);
					returnMap.put("number", newDoc.getNumber());
					returnMap.put("oid", CommonUtil.getOIDString(newDoc));
					returnMap.put("msg", "등록 성공");
					returnMap.put("state", "S");
					returnMap.put("id", id);
					returnList.add(returnMap);
				} catch (Exception e) {
					e.printStackTrace();
					// System.out.println("ERRROR : " + e.getMessage() +"::::"+
					// e.getLocalizedMessage());
					totalValidation = false;
					e.printStackTrace();
					returnMap.put("number", "");
					returnMap.put("oid", "");
					returnMap.put("msg", e.getMessage());
					returnMap.put("state", "F");
					returnMap.put("id", id);
					returnList.add(returnMap);
				}

			}

			data.setReturnList(returnList);

			if (totalValidation) {
				trx.commit();
				trx = null;
				data.setResult(true);
			} else {
				data.setResult(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
				trx = null;
			}
		}
		return data;
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
	public List<DocumentData> include_documentLink(String module, String oid) {
		List<DocumentData> list = new ArrayList<DocumentData>();

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
							DocumentData data = new DocumentData((WTDocument) rc);
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

						DocumentData data = new DocumentData(doc);
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

	/**
	 * 산출물 링크 등록
	 * 
	 * @param doc
	 * @param hash
	 * @throws Exception
	 */
	@Override
	public void createOutPutLink(WTDocument doc, Hashtable hash) throws Exception {

		try {
			String outputOid = StringUtil.checkNull((String) hash.get("outputOid"));

			// System.out.println("[DocumentHelper][createOutPutLink][output]"+outputOid);
			if (outputOid.length() == 0)
				return;
			Object obj = CommonUtil.getObject(outputOid);

			if (obj instanceof EChangeActivity) { /* ECA 산출물 */
				EChangeActivity eca = (EChangeActivity) obj;
				DocumentActivityLink link = DocumentActivityLink
						.newDocumentActivityLink((WTDocumentMaster) doc.getMaster(), eca);
				PersistenceHelper.manager.save(link);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Working Copy
	 * 
	 * @param _doc
	 * @return
	 */
	private WTDocument getWorkingCopy(WTDocument _doc) throws Exception {
		/*
		 * try {
		 */
		if (!WorkInProgressHelper.isCheckedOut(_doc)) {
			if (!CheckInOutTaskLogic.isCheckedOut(_doc)) {
				CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(_doc,
						CheckInOutTaskLogic.getCheckoutFolder(), "");
			}
			_doc = (WTDocument) WorkInProgressHelper.service.workingCopyOf(_doc);
		} else {
			if (!WorkInProgressHelper.isWorkingCopy(_doc))
				_doc = (WTDocument) WorkInProgressHelper.service.workingCopyOf(_doc);
		}
		/*
		 * }catch (WorkInProgressException e){ e.printStackTrace(); throw new
		 * Exception(e.getLocalizedMessage()); }catch (WTException e){
		 * e.printStackTrace(); throw new Exception(e.getLocalizedMessage()); }catch
		 * (WTPropertyVetoException e){ e.printStackTrace(); throw new
		 * Exception(e.getLocalizedMessage()); }
		 */
		return _doc;
	}

	@Override
	public synchronized String delete(String oid) throws Exception {

		Transaction trx = new Transaction();
		try {
			trx.start();
			if (oid != null) {
				ReferenceFactory f = new ReferenceFactory();
				WTDocument wtdoc = (WTDocument) f.getReference(oid).getObject();

				/*
				 * // DescribeLink를 삭제한다. QuerySpec spec = new QuerySpec(); int index0 =
				 * spec.addClassList(WTPartDescribeLink.class, true); int index1 =
				 * spec.addClassList(WTDocument.class, false);
				 * 
				 * ClassAttribute ca0 = new ClassAttribute(WTPartDescribeLink.class,
				 * "roleBObjectRef.key.id"); ClassAttribute ca1 = new
				 * ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
				 * SearchCondition sc = new SearchCondition(ca0, "=", ca1); spec.appendWhere(sc,
				 * new int[]{index0, index1}); spec.appendAnd(); spec.appendWhere(new
				 * SearchCondition(WTDocument.class, "master>number", "=", wtdoc.getNumber()),
				 * new int[]{index1});
				 * 
				 * QueryResult qr = PersistenceHelper.manager.find(spec); while
				 * (qr.hasMoreElements()) { Object[] o = (Object[]) qr.nextElement();
				 * WTPartDescribeLink link = (WTPartDescribeLink) o[0];
				 * PersistenceServerHelper.manager.remove(link); }
				 * 
				 * // DocumentUnitLink를 삭제한다.
				 */

				QueryResult results = PersistenceHelper.manager.navigate(wtdoc, "describes", WTPartDescribeLink.class,
						false);
				if (results.size() > 0) {
					throw new Exception(Message.get("관련 품목") + Message.get("이(가) 있을 때 삭제할 수 없습니다."));
				}

				List<DocumentToDocumentLink> used = DocumentQueryHelper.service.getDocumentToDocumentLinks(wtdoc,
						"used");
				List<DocumentToDocumentLink> useBy = DocumentQueryHelper.service.getDocumentToDocumentLinks(wtdoc,
						"useBy");

				if (used.size() > 0 || useBy.size() > 0) {
					throw new Exception(Message.get("관련 문서") + Message.get("이(가) 있을 때 삭제할 수 없습니다."));
				}

				WFItemHelper.service.deleteWFItem(wtdoc);
				PersistenceHelper.manager.delete(wtdoc);

				trx.commit();
			}
			trx = null;
		} finally {
			if (trx != null)
				trx.rollback();
		}

		return Message.get("삭제 되었습니다");
	}

	@Override
	public ResultData reviseUpdate(Map<String, Object> params) throws Exception {
		ResultData data = new ResultData();
		String reOid = "";
		Transaction trx = new Transaction();
		try {
			trx.start();

			ReferenceFactory f = new ReferenceFactory();

			String oid = StringUtil.checkNull((String) params.get("oid"));
			if (oid.length() > 0) {

				WTDocument oldDoc = (WTDocument) f.getReference(oid).getObject();
				// WTDocument doc = (WTDocument) ObjectUtil.reviseNote(oldDoc, Message.get("문서가
				// 개정되었습니다."));
				String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
				WTDocument doc = (WTDocument) ObjectUtil.revise(oldDoc, lifecycle);

				doc = (WTDocument) PersistenceHelper.manager.save(doc);

				QueryResult qr = PersistenceHelper.manager.navigate(oldDoc, "describes", WTPartDescribeLink.class);
				while (qr.hasMoreElements()) {
					WTPart part = (WTPart) qr.nextElement();
					WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
					PersistenceServerHelper.manager.insert(link);
				}

				List<DocumentData> useByList = DocumentQueryHelper.service.getDocumentListToLinkRoleName(oldDoc,
						"useBy");
				for (DocumentData docData : useByList) {
					DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(doc,
							docData.getDoc());
					PersistenceServerHelper.manager.insert(link);
				}

				List<DocumentData> usedList = DocumentQueryHelper.service.getDocumentListToLinkRoleName(oldDoc, "used");
				for (DocumentData docData : usedList) {
					DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(docData.getDoc(),
							doc);
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

				reOid = doc.getPersistInfo().getObjectIdentifier().toString();
			}

			trx.commit();
			trx = null;
			data.setResult(true);
			data.setOid(reOid);
		} catch (Exception e) {
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return data;
	}

	@Override
	public List<DocumentData> include_DocumentList(String oid, String moduleType) throws Exception {
		List<DocumentData> list = new ArrayList<DocumentData>();
		if (StringUtil.checkString(oid)) {
			if ("part".equals(moduleType)) {
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				QueryResult qr = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class);
				while (qr.hasMoreElements()) {
					WTDocument doc = (WTDocument) qr.nextElement();
					DocumentData data = new DocumentData(doc);
					// Part가 최신 버전이면 관련 문서가 최신 버전만 ,Part가 최신 버전이 아니면 모든 버전
					if (VersionHelper.service.isLastVersion(part)) {
						if (data.isLatest()) {
							list.add(data);
						}
					} else {
						list.add(data);
					}
				}
			} else if ("doc".equals(moduleType)) {
				List<DocumentData> dataList = DocumentQueryHelper.service.getDocumentListToLinkRoleName(oid, "used");
				for (DocumentData data : dataList) {
					list.add(data);
				}

				dataList = DocumentQueryHelper.service.getDocumentListToLinkRoleName(oid, "useBy");
				for (DocumentData data : dataList) {
					list.add(data);
				}

			} else if ("active".equals(moduleType)) {
				devActive m = (devActive) CommonUtil.getObject(oid);
				QueryResult qr = PersistenceHelper.manager.navigate(m, "output", devOutPutLink.class);

				while (qr.hasMoreElements()) {
					Object p = (Object) qr.nextElement();
					if (p instanceof WTDocument) {
						DocumentData data = new DocumentData((WTDocument) p);
						list.add(data);
					}
				}
			} else if ("asm".equals(moduleType)) {
				AsmApproval asm = (AsmApproval) CommonUtil.getObject(oid);
				List<WTDocument> aList = AsmSearchHelper.service.getObjectForAsmApproval(asm);
				for (WTDocument doc : aList) {
					DocumentData data = new DocumentData(doc);
					list.add(data);
				}
			}
		}
		return list;
	}

	@Override
	public WTDocument getLastDocument(String number) throws Exception {
		WTDocument doc = null;
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(WTDocument.class, true);

			// 최신 이터레이션
			query.appendWhere(VersionControlHelper.getSearchCondition(WTDocument.class, true), new int[] { idx });
			// 최신 버전
			SearchUtil.addLastVersionCondition(query, WTDocument.class, idx);
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(WTDocument.class, "master>number", SearchCondition.EQUAL, number, false),
					new int[] { idx });

			QueryResult rt = PersistenceHelper.manager.find(query);
			while (rt.hasMoreElements()) {
				Object[] obj = (Object[]) rt.nextElement();
				doc = (WTDocument) obj[0];
				return doc;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	@Override
	public Map<String, Object> createDocumentLinkAction(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);

		String sessionId = request.getParameter("sessionId");
		Hashtable<String, String> hash = null;

		PagingQueryResult qr = null;
		ReferenceFactory rf = new ReferenceFactory();
		if (StringUtil.checkString(sessionId)) {

			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {

			// QuerySpec query =
			// DocumentQueryHelper.service.listCreateDocumentLinkAction(request, response);
			QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		}

		// PageQueryBroker broker = new PageQueryBroker(request,query);

		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();

		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
		xmlBuf.append("<rows>");

		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) o[0];
			DocumentData data = new DocumentData(doc);
			xmlBuf.append("<row id='" + data.oid + "'>");
			xmlBuf.append("<cell><![CDATA[]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getIBAValue(AttributeKey.IBAKey.IBA_MODEL) + "]]></cell>");
			xmlBuf.append(
					"<cell><![CDATA[<a href=javascript:openView('" + data.oid + "')>" + data.name + "</a>]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.getLifecycle() + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.version + "." + data.iteration + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.createDate + "]]></cell>");
			// xmlBuf.append("<cell><![CDATA[<a href=" + data.pUrl + ">" + data.icon +
			// "]]></cell>" );
			xmlBuf.append("</row>");

		}
		xmlBuf.append("</rows>");

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("formPage", formPage);
		result.put("rows", rows);
		result.put("totalPage", totalPage);
		result.put("startPage", startPage);
		result.put("endPage", endPage);
		result.put("listCount", listCount);
		result.put("totalCount", totalCount);
		result.put("currentPage", currentPage);
		result.put("param", param);
		result.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
		result.put("xmlString", xmlBuf);

		return result;
	}

	/**
	 * 부품과 연관되 문서 ,문서와 연관된 부품
	 * 
	 * @param rc
	 * @param isLast
	 * @return
	 */
	@Override
	public Vector<WTPartDescribeLink> getWTPartDescribeLink(RevisionControlled rc, boolean isLast) {

		Vector<WTPartDescribeLink> vec = new Vector<WTPartDescribeLink>();
		try {

			long logOid = CommonUtil.getOIDLongValue(rc);
			Class cls = null;

			String columnName = "";

			QuerySpec qs = new QuerySpec();
			int idxA = qs.addClassList(WTPartDescribeLink.class, true);
			int idxB = qs.addClassList(WTDocument.class, false);
			int idxC = qs.addClassList(WTPart.class, false);
			int idx = 0;
			if (rc instanceof WTDocument) {
				cls = WTPart.class;
				columnName = "roleBObjectRef.key.id";
				idx = idxC;
			} else if (rc instanceof WTPart) {
				cls = WTDocument.class;
				columnName = "roleAObjectRef.key.id";
				idx = idxB;
			}
			// Join
			qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id", WTDocument.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { idxA, idxB });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, "roleAObjectRef.key.id", WTPart.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { idxA, idxC });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPartDescribeLink.class, columnName, SearchCondition.EQUAL, logOid),
					new int[] { idxA });

			if (isLast) {
				// 최신 이터레이션
				qs.appendAnd();
				qs.appendWhere(VersionControlHelper.getSearchCondition(cls, true), new int[] { idx });

				// 최신 버전
				SearchUtil.addLastVersionCondition(qs, cls, idx);
			}

			QueryResult rt = PersistenceHelper.manager.find(qs);
			while (rt.hasMoreElements()) {
				Object[] oo = (Object[]) rt.nextElement();
				WTPartDescribeLink link = (WTPartDescribeLink) oo[0];
				vec.add(link);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return vec;
	}

	@Override
	public ResultData linkDocumentAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();

		String parentOid = StringUtil.checkNull(request.getParameter("parentOid"));
		String docOid = StringUtil.checkNull(request.getParameter("docOid"));
		String type = StringUtil.checkNull(request.getParameter("type"));

		try {
			trx.start();
			WTDocument doc = (WTDocument) CommonUtil.getObject(docOid);

			// System.out.println("parentOid =" + parentOid);
			// System.out.println("docOid =" + docOid);
			// System.out.println("type =" + type);
			if ("active".equals(type)) {
				devActive active = (devActive) CommonUtil.getObject(parentOid);
				devOutPutLink link = devOutPutLink.newdevOutPutLink(active, doc);
				PersistenceServerHelper.manager.insert(link);
			} else if ("ecaAction".equals(type)) {
				EChangeActivity eca = (EChangeActivity) CommonUtil.getObject(parentOid);
				DocumentActivityLink link = DocumentActivityLink
						.newDocumentActivityLink((WTDocumentMaster) doc.getMaster(), eca);
				PersistenceHelper.manager.save(link);
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
	public ResultData deleteDocumentLinkAction(HttpServletRequest request, HttpServletResponse response) {
		ResultData data = new ResultData();
		Transaction trx = new Transaction();

		String parentOid = StringUtil.checkNull(request.getParameter("parentOid"));
		String linkOid = StringUtil.checkNull(request.getParameter("linkOid"));
		String type = StringUtil.checkNull(request.getParameter("type"));

		try {
			// System.out.println("linkOid = " + linkOid);
			trx.start();
			WTObject objLink = (WTObject) CommonUtil.getObject(linkOid);
			PersistenceHelper.manager.delete(objLink);
			/*
			 * if("active".equals(type)) { QuerySpec spec =
			 * DocumentQueryHelper.service.devActiveLinkDocument(parentOid, docOid);
			 * QueryResult result = PersistenceHelper.manager.find(spec);
			 * if(result.hasMoreElements()) { Object[] o = (Object[]) result.nextElement();
			 * devOutPutLink link = (devOutPutLink)o[0];
			 * PersistenceHelper.manager.delete(link); } }
			 */
			trx.commit();
			trx = null;
			data.setResult(true);

		} catch (Exception e) {
			e.printStackTrace();
			data.setResult(false);
			data.setMessage(e.getLocalizedMessage());
		}

		return data;
	}

	/**
	 * WTDocument -- WTDocument 링크 수정(등록, 삭제)
	 * 
	 * @param document
	 * @param docOids
	 * @param isDelete
	 * @throws Exception
	 */
	public void updateDocumentToDocumentLink(WTDocument document, String[] docOids, boolean isDelete) throws Exception {
		if (isDelete) {
			deleteDocumentToDocumentLink(document);
		}

		// 관련 문서
		for (int i = 0; docOids != null && i < docOids.length; i++) {
			WTDocument linkDocument = (WTDocument) CommonUtil.getObject(docOids[i]);
			DocumentToDocumentLink link = DocumentToDocumentLink.newDocumentToDocumentLink(document, linkDocument);
			PersistenceServerHelper.manager.insert(link);
		}
	}

	/**
	 * WTDocument -- WTDocument 링크 삭제
	 * 
	 * @param document
	 * @throws Exception
	 */
	public void deleteDocumentToDocumentLink(WTDocument document) throws Exception {
		List<DocumentToDocumentLink> list = DocumentQueryHelper.service.getDocumentToDocumentLinks(document, "useBy");
		for (DocumentToDocumentLink link : list) {
			PersistenceServerHelper.manager.remove(link);
		}
		list = DocumentQueryHelper.service.getDocumentToDocumentLinks(document, "used");
		for (DocumentToDocumentLink link : list) {
			PersistenceServerHelper.manager.remove(link);
		}
	}

	/**
	 * WTDocument -- WTPart 링크 수정(등록, 삭제)
	 * 
	 * @param document
	 * @param partOids
	 * @param isDelete
	 * @throws Exception
	 */
	public void updateDocumentToPartLink(WTDocument document, String[] partOids, boolean isDelete) throws Exception {
		if (isDelete) {
			deleteDocumentToPartLink(document);
		}

		for (int i = 0; partOids != null && i < partOids.length; i++) {
			WTPart part = (WTPart) CommonUtil.getObject(partOids[i]);
			WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, document);
			PersistenceServerHelper.manager.insert(link);
		}
	}

	/**
	 * WTDocument -- WTPart 링크 삭제
	 * 
	 * @param document
	 * @throws Exception
	 */
	public void deleteDocumentToPartLink(WTDocument document) throws Exception {
		QueryResult results = PersistenceHelper.manager.navigate(document, "describes", WTPartDescribeLink.class,
				false);
		while (results.hasMoreElements()) {
			WTPartDescribeLink link = (WTPartDescribeLink) results.nextElement();
			PersistenceServerHelper.manager.remove(link);
		}
	}

	/**
	 * WTDocument -- devActive 링크 수정(등록, 삭제)
	 * 
	 * @param document
	 * @param partOids
	 * @param isDelete
	 * @throws Exception
	 */
	public void updateDocumentToDevelopmentLink(WTDocument document, String[] activeOids, boolean isDelete)
			throws Exception {
		if (isDelete) {
			deleteDocumentToDevelopmentLink(document);
		}

		for (int i = 0; activeOids != null && i < activeOids.length; i++) {
			devActive active = (devActive) CommonUtil.getObject(activeOids[i]);
			devOutPutLink link = devOutPutLink.newdevOutPutLink(active, document);
			PersistenceServerHelper.manager.insert(link);
		}
	}

	/**
	 * WTDocument -- devActive 링크 삭제
	 * 
	 * @param document
	 * @throws Exception
	 */
	public void deleteDocumentToDevelopmentLink(WTDocument document) throws Exception {
		List<devOutPutLink> list = DevelopmentQueryHelper.service.getDevOutPutLinkFromRevisionControlled(document);
		for (devOutPutLink link : list) {
			PersistenceServerHelper.manager.remove(link);
		}
	}

	/**
	 * 산출물 등록(EO 산출물,개발 산출물)
	 * 
	 * @param doc
	 * @param linkType
	 * @param parentOid
	 * @throws Exception
	 */
	public void createLinkDocument(WTDocument doc, String linkType, String parentOid) throws Exception {

		if (linkType.equals("active")) {
			DevelopmentHelper.service.createDocumentToDevelopmentLink(doc, parentOid);
		} else if (linkType.equals("ecaAction")) {
			ECAHelper.service.createDocumentActivityLink(doc, parentOid);
		}

	}

	public void docReName(WTDocument doc, String changeName) throws Exception {

		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

		PreparedStatement st = null;
		ResultSet rs = null;
		// System.out.println("============docReName =================");
		try {
			if (!doc.getName().equals(changeName)) {
				methodcontext = MethodContext.getContext();
				wtconnection = (WTConnection) methodcontext.getConnection();
				Connection con = wtconnection.getConnection();

				WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
				long longOid = CommonUtil.getOIDLongValue(master);

				StringBuffer sql = new StringBuffer();

				sql.append("UPDATE WTDocumentMaster set name= ? where ida2a2 = ? ");
				// System.out.println(sql.toString());
				// System.out.println(changeName + ","+longOid);
				st = con.prepareStatement(sql.toString());
				st.setString(1, changeName);
				st.setLong(2, longOid);
				st.executeQuery();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new WTException(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (Exception e) {
				throw new WTException(e);
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

//	@Override
//	public void create(Map<String, Object> params) throws Exception {
//		Transaction trs = new Transaction();
//		Map<String,String> fileMap = new HashMap<String,String>();
//		try {
//			trs.start();
//			
//			String[] secondary = (String[]) params.get("SECONDARY");
//			if(secondary != null) {
//				for(String attachFile : secondary) {
//			        String fileName = attachFile.split("/")[1].toUpperCase();
//			        if(fileMap.get(fileName) == null){
//			        	fileMap.put(fileName, attachFile);
//			        }else {
//			        	fileMap.remove(fileName);
//			        }
//				}
//			}
//			
//			WTDocument doc = WTDocument.newWTDocument();
//			// 문서명
//			String docName = StringUtil.checkNull((String) params.get("docName"));
//			doc.setName(docName);
//			
//			// 결재 방식
//			String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
//			
//			String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
//			String description = StringUtil.checkNull((String) params.get("description"));
//			doc.setDescription(description);
//			doc.setOwnership(Ownership.newOwnership(SessionHelper.manager.getPrincipalReference()));
//			PersistenceHelper.manager.save(doc);
//			
//			String approvalType =AttributeKey.CommonKey.COMMON_DEFAULT; //일괄결재 Batch,기본결재 Default
//            if("LC_Default_NonWF".equals(lifecycle)){
//            	E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) doc, "BATCHAPPROVAL");
//            	approvalType = AttributeKey.CommonKey.COMMON_BATCH;
//            }
//            
//            Map<String,Object> map = new HashMap<String,Object>();
//            
//            
//            map.put("approvalType", approvalType);
//            map.put("manufacture", manufacture);
//            CommonHelper.service.changeIBAValues(doc, map);
//			
//            trs.commit();
//			trs = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			trs.rollback();
//			throw e;
//        } finally {
//        	if (trs != null) {
//				trs.rollback();
//			}
//        }
//	}

	@Override
	public void create(Map<String, Object> params) throws Exception {

		String location = StringUtil.checkNull((String) params.get("location"));
		String documentName = StringUtil.checkNull((String) params.get("documentName"));
		String name = StringUtil.checkNull((String) params.get("docName"));
		String description = StringUtil.checkNull((String) params.get("description"));

		String documentType = StringUtil.checkNull((String) params.get("documentType"));

		DocumentType docType = DocumentType.toDocumentType(documentType);
		String number = getDocumentNumberSeq(docType.getLongDescription());

		String primary = (String) params.get("primary");
		ArrayList<String> secondarys = (ArrayList<String>) params.get("secondarys");
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 문서 기본 정보 설정
			WTDocument document = WTDocument.newWTDocument();
			if ("$$MMDocument".equals(documentType)) {
				document.setName(name);
			} else {
				if (name.length() > 0) {
					document.setName(documentName + "-" + name);
				} else {
					document.setName(documentName);
				}
			}
			document.setNumber(number);
			document.setDescription(description);

			// 문서 분류체계 설정
			Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) document, folder);

			// 문서 Container 설정
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			document.setContainer(e3psProduct);

			// 문서 lifeCycle 설정
			String lifecycle = StringUtil.checkNull((String) params.get("lifecycle"));
			LifeCycleHelper.setLifeCycle(document,
					LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); // Lifecycle

			document = (WTDocument) PersistenceHelper.manager.save(document);

			if (StringUtil.checkString(primary)) {
				File vault = CommonContentHelper.manager.getFileFromCacheId(primary);
				ApplicationData applicationData = ApplicationData.newApplicationData(document);
				applicationData.setRole(ContentRoleType.PRIMARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(document, applicationData, vault.getPath());
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(document);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(document, applicationData, vault.getPath());
			}

			// 관련 부품
			String[] partOids = (String[]) params.get("partOids");
			updateDocumentToPartLink(document, partOids, false);

			// 관련 문서
			String[] docOids = (String[]) params.get("docOids");
			updateDocumentToDocumentLink(document, docOids, false);

			String approvalType = AttributeKey.CommonKey.COMMON_DEFAULT; // 일괄결재 Batch,기본결재 Default
			if ("LC_Default_NonWF".equals(lifecycle)) {
				E3PSWorkflowHelper.service.changeLCState((LifeCycleManaged) document, "BATCHAPPROVAL");
				approvalType = AttributeKey.CommonKey.COMMON_BATCH;
			}
			params.put("approvalType", approvalType);
			CommonHelper.service.changeIBAValues(document, params);

			// 산출물 직접 등록(개발업무 관리,설계 변경 관리) 문서 직접등록 시 링크 생성
//	        String linkType = (String)params.get("linkType");
//	        String parentOid = (String)params.get("parentOid");
//	        createLinkDocument(document,linkType,parentOid);

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
	public void createComments(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = StringUtil.checkNull((String) params.get("oid"));
			String comments = StringUtil.checkNull((String) params.get("comments"));
			int num = (int) params.get("num");
			int step = (int) params.get("step");
			String oPerson = StringUtil.checkNull((String) params.get("person"));

			WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

			Comments com = Comments.newComments();
			com.setWtdocument(doc);
			com.setComments(comments);
			com.setCNum(num);
			com.setCStep(step);
			com.setOPerson(oPerson);
			com.setDeleteYN("N");
			com.setOwner(SessionHelper.manager.getPrincipalReference());

			PersistenceHelper.manager.save(com);

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

	@Override
	public void updateComments(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = StringUtil.checkNull((String) params.get("oid"));
			String comments = StringUtil.checkNull((String) params.get("comments"));

			Comments com = (Comments) CommonUtil.getObject(oid);
			com.setComments(comments);

			PersistenceHelper.manager.modify(com);

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

	@Override
	public void deleteComments(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Comments com = (Comments) CommonUtil.getObject(oid);

			int child = DocumentHelper.manager.getCommentsChild(com);
			if (child > 0) {
				com.setDeleteYN("Y");
				PersistenceHelper.manager.modify(com);
			} else {
				PersistenceHelper.manager.delete(com);
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
	 * 문서 양식 저장 메서드
	 */
	@Override
	public void createTemplate(Map<String, Object> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String docTemplateNumber = StringUtil.checkNull((String) params.get("number"));
			String name = StringUtil.checkNull((String) params.get("name"));
			String _docTemplateType = (String) params.get("documentTemplateType");
			String description = (String) params.get("description");

			DocumentTemplate docTemp = DocumentTemplate.newDocumentTemplate();

			docTemp.setNumber(docTemplateNumber);
			docTemp.setName(name);
			if (!StringUtil.isNull(_docTemplateType)) {
//	    		NumberCode docTemplateType = NumberCodeHelper.service.getNumberCode(_docTemplateType, "DOCFORMTYPE");
				NumberCode docTemplateType = (NumberCode) CommonUtil.getObject(_docTemplateType);
				docTemp.setTemplateType(docTemplateType);
			}

			docTemp.setDescription(description);
			System.out.println("===========================>" + description);
			System.out.println("===========================>" + docTemp.getDescription());

			PersistenceHelper.manager.save(docTemp);

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
}
