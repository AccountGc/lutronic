package com.e3ps.doc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.Picture;
import com.aspose.cells.Row;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.pdf.Document;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.ecpr.column.EcprColumn;
import com.e3ps.change.eo.column.EoColumn;
import com.e3ps.common.aspose.AsposeUtils;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentClass;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentECPRLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.OrgHelper;
import com.e3ps.part.column.PartColumn;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.service.WorkspaceHelper;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentTypeInfo;
import wt.fc.ObjectReference;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.IteratedFolderMemberLink;
import wt.folder.SubFolder;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

public class DocumentHelper {

	/**
	 * 문서 기본 위치
	 */
	public static final String DOCUMENT_ROOT = "/Default/문서";

	public static final DocumentService service = ServiceFactory.getService(DocumentService.class);
	public static final DocumentHelper manager = new DocumentHelper();

	/**
	 * Word To Pdf 변환
	 */
	private static final String processQueueName = "WordToPdfProcessQueue";
	private static final String className = "com.e3ps.common.aspose.AsposeUtils";
	private static final String wordToPdfMethod = "wordToPdf";
	private static final String genWordAndPdfMethod = "genWordAndPdf";

	/**
	 * 문서 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		long start = System.currentTimeMillis() / 1000;
		System.out.println("쿼리 시작 = " + start);
		Map<String, Object> map = new HashMap<>();
		ArrayList<DocumentColumn> list = new ArrayList<>();

		boolean latest = (boolean) params.get("latest");
		String location = (String) params.get("location");
		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String creatorOid = (String) params.get("creatorOid");
		String state = (String) params.get("state");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String modifiedFrom = (String) params.get("modifiedFrom");
		String modifiedTo = (String) params.get("modifiedTo");
		String documentType = (String) params.get("documentType");
		String preseration = (String) params.get("preseration");
		String model = (String) params.get("modelcode");
		String deptcode = (String) params.get("deptcode");
		String interalnumber = (String) params.get("interalnumber");
		String writer = (String) params.get("writer");
		String description = (String) params.get("description");

		// 분류
		String classType1 = (String) params.get("classType1");
		String classType2 = (String) params.get("classType2");
		String classType3 = (String) params.get("classType3");

		// 정렬
		String sortKey = (String) params.get("sortKey");
		String sortType = (String) params.get("sortType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, false);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		query.appendSelect(new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { idx }, false);

		QuerySpecUtils.toEqualsAnd(query, idx, WTDocument.class, "typeInfoWTDocument.ptc_str_2", classType1);

		if (StringUtil.checkString(classType2)) {
			DocumentClass class2 = (DocumentClass) CommonUtil.getObject(classType2);
			QuerySpecUtils.toEqualsAnd(query, idx, WTDocument.class, "typeInfoWTDocument.ptc_ref_2.key.id", class2);
		}

		if (StringUtil.checkString(classType3)) {
			DocumentClass class3 = (DocumentClass) CommonUtil.getObject(classType3);
			QuerySpecUtils.toEqualsAnd(query, idx, WTDocument.class, "typeInfoWTDocument.ptc_ref_3.key.id", class3);
		}

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		// 상태 임시저장 제외
//		if (query.getConditionCount() > 0) {
//			query.appendAnd();
//		}
//		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL,
//				"TEMPRARY"), new int[] { idx });

		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NUMBER, number);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.DESCRIPTION, description);
		QuerySpecUtils.toState(query, idx, WTDocument.class, state);
		QuerySpecUtils.creatorQuery(query, idx, WTDocument.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, modifiedFrom,
				modifiedTo);

		QuerySpecUtils.toEqualsAnd(query, idx, WTDocument.class, WTDocument.DOC_TYPE, documentType);

		QuerySpecUtils.toIBAEqualsAnd(query, WTDocument.class, idx, "PRESERATION", preseration);
		QuerySpecUtils.toIBAEqualsAnd(query, WTDocument.class, idx, "MODEL", model);
//		QuerySpecUtils.toIBALikeAnd(query, WTDocument.class, idx, "INTERALNUMBER", interalnumber);
		QuerySpecUtils.toIBAEqualsAnd(query, WTDocument.class, idx, "DEPTCODE", deptcode);
		QuerySpecUtils.toIBAEqualsAnd(query, WTDocument.class, idx, "DSGN", writer);

		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
//		int isQuery = DOCUMENT_ROOT.indexOf(location);
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

//		if (isQuery < 0) {
		int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
		ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
		SearchCondition fsc = new SearchCondition(fca, "=",
				new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
		fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
		fsc.setOuterJoin(0);
		query.appendWhere(fsc, new int[] { f_idx, idx });
		query.appendAnd();

//		long fid = folder.getPersistInfo().getObjectIdentifier().getId();
//		query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
//				new int[] { f_idx });
//		}

		query.appendOpenParen();
		long fid = folder.getPersistInfo().getObjectIdentifier().getId();
		query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
				new int[] { f_idx });

		ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
		for (int i = 0; i < folders.size(); i++) {
			Folder sub = (Folder) folders.get(i);
			query.appendOr();
			long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
					new int[] { f_idx });
		}
		query.appendCloseParen();

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, toSortKey(sortKey), sort);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			BigDecimal bd = (BigDecimal) obj[0];
			String oid = "wt.doc.WTDocument:" + bd.longValue();
			DocumentColumn column = new DocumentColumn(oid);
			column.setRowNum(rowNum++);
			list.add(column);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		long end = System.currentTimeMillis() /1000;
		System.out.println("쿼리 종료 = " + end + ", 걸린 시간 = " + (end - start));
		return map;
	}

	/**
	 * 정렬키값
	 */
	private String toSortKey(String sortKey) throws Exception {

		if ("name".equals(sortKey)) {
			return WTDocument.NAME;
		} else if ("number".equals(sortKey)) {
			return WTDocument.NUMBER;
		} else if ("state".equals(sortKey)) {
			return WTDocument.LIFE_CYCLE_STATE;
		} else if ("creator".equals(sortKey)) {
			return (WTDocument.CREATOR + "." + WTAttributeNameIfc.REF_OBJECT_ID);
		} else if ("modifiedDate".equals(sortKey)) {
			return WTDocument.MODIFY_TIMESTAMP;
		} else if ("createdDate".equals(sortKey)) {
			return WTDocument.CREATE_TIMESTAMP;
		}
		return WTDocument.CREATE_TIMESTAMP;
	}

	/**
	 * 문서 관련 객체
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		if ("doc".equalsIgnoreCase(type)) {
			// 문서
			return JSONArray.fromObject(referenceDoc(doc, list));
		} else if ("part".equalsIgnoreCase(type)) {
			// PART
			return JSONArray.fromObject(referencePart(doc, list));
		} else if ("eo".equalsIgnoreCase(type)) {
			// EO
			return JSONArray.fromObject(referenceEo(doc, list));
		} else if ("eco".equalsIgnoreCase(type)) {
			// ECO
			return JSONArray.fromObject(referenceEco(doc, list));
		} else if ("cr".equalsIgnoreCase(type)) {
			// CR
			return JSONArray.fromObject(referenceCr(doc, list));
		} else if ("ecpr".equalsIgnoreCase(type)) {
			// ECPR
			return JSONArray.fromObject(referenceEcpr(doc, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 문서
	 */
	private Object referenceDoc(WTDocument doc, ArrayList<Map<String, Object>> list) throws Exception {

		QueryResult result = PersistenceHelper.manager.navigate(doc, "useBy", DocumentToDocumentLink.class);
		while (result.hasMoreElements()) {
			WTDocument ref = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(ref);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 ECPR
	 */
	private Object referenceEcpr(WTDocument doc, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(doc, "ecpr", DocumentECPRLink.class);
		while (result.hasMoreElements()) {
			ECPRRequest ecpr = (ECPRRequest) result.nextElement();
			EcprColumn dto = new EcprColumn(ecpr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 CR
	 */
	private Object referenceCr(WTDocument doc, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(doc, "cr", DocumentCRLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest cr = (EChangeRequest) result.nextElement();
			CrColumn dto = new CrColumn(cr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 ECO
	 */
	private Object referenceEco(WTDocument doc, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(doc, "eco", DocumentECOLink.class);
		while (result.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) result.nextElement();
			EcoColumn dto = new EcoColumn(eco);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 EO
	 */
	private Object referenceEo(WTDocument doc, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(doc, "eo", DocumentEOLink.class);
		while (result.hasMoreElements()) {
			EChangeOrder eo = (EChangeOrder) result.nextElement();
			EoColumn dto = new EoColumn(eo);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 품목
	 */
	private ArrayList<Map<String, Object>> referencePart(WTDocument doc, ArrayList<Map<String, Object>> list)
			throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(doc, "describes", WTPartDescribeLink.class);
		while (result.hasMoreElements()) {
			WTPart part = (WTPart) result.nextElement();
			PartColumn dto = new PartColumn(part);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 문서 이력
	 */
	public JSONArray allIterationsOf(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		QueryResult result = VersionControlHelper.service.allIterationsOf(doc.getMaster());
		while (result.hasMoreElements()) {
			WTDocument d = (WTDocument) result.nextElement();
			Map<String, String> map = new HashMap<>();
			DocumentColumn dto = new DocumentColumn(d);
			map.put("oid", dto.getOid());
			map.put("name", dto.getName());
			map.put("interalnumber", dto.getInteralnumber());
			map.put("number", dto.getNumber());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreatedDate_txt());
			map.put("modifier", dto.getModifier());
			map.put("modifiedDate", dto.getModifiedDate_txt());
			map.put("note", d.getIterationNote());
			map.put("primary", dto.getPrimary());
			map.put("secondary", dto.getSecondary());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 문서 링크 관계 확인 ( true : 연결, false : 미연결 )
	 */
	public boolean isConnect(String oid, Class<?> target) throws Exception {
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		return isConnect(doc, target);
	}

	/**
	 * 문서 링크 관계 확인 ( true : 연결, false : 미연결 )
	 */
	public boolean isConnect(WTDocument doc, Class<?> target) throws Exception {
		boolean isConnect = false;

		if (target.equals(DocumentEOLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "eo", DocumentEOLink.class);
			isConnect = qr.size() > 0;
		} else if (target.equals(DocumentECOLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "eco", DocumentECOLink.class);
			isConnect = qr.size() > 0;
		} else if (target.equals(DocumentECPRLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "ecpr", DocumentECPRLink.class);
			isConnect = qr.size() > 0;
		} else if (target.equals(DocumentCRLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "cr", DocumentCRLink.class);
			isConnect = qr.size() > 0;
		} else if (target.equals(WTPartDescribeLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "describes", WTPartDescribeLink.class);
			isConnect = qr.size() > 0;
		}
		return isConnect;
	}

	/**
	 * 최신버전 문서
	 */
	public WTDocument latest(WTDocumentMaster master) throws Exception {
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(master, config);
		if (result.hasMoreElements()) {
			WTDocument latest = (WTDocument) result.nextElement();
			return latest;
		}
		return null;
	}

	/**
	 * 최신버전 문서
	 */
	public WTDocument latest(String oid) throws Exception {
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(doc.getMaster(), config);
		if (result.hasMoreElements()) {
			WTDocument latest = (WTDocument) result.nextElement();
			return latest;
		}
		return null;
	}

	/**
	 * 최신버전 문서인지 확인
	 */
	public boolean isLatest(WTDocument doc) throws Exception {
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(doc.getMaster(), config);
		if (result.hasMoreElements()) {
			WTDocument latest = (WTDocument) result.nextElement();
			if (doc.getPersistInfo().getObjectIdentifier().getId() == latest.getPersistInfo().getObjectIdentifier()
					.getId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 문서 폴더 가져오기
	 */
	public JSONArray recurcive() throws Exception {
		ArrayList<String> list = new ArrayList<>();
		Folder root = FolderTaskLogic.getFolder(DOCUMENT_ROOT, WCUtil.getWTContainerRef());
		Enumeration result = FolderTaskLogic.getSubFolders(root);
		while (result.hasMoreElements()) {
			Folder folder = (Folder) result.nextElement();
			list.add(folder.getFolderPath());
			recurcive(folder, list);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 문서 폴더 가져오기 재귀함수
	 */
	private void recurcive(Folder parent, ArrayList<String> list) throws Exception {
		QueryResult result = FolderHelper.service.findSubFolders(parent);
		while (result.hasMoreElements()) {
			SubFolder folder = (SubFolder) result.nextElement();
			list.add(folder.getFolderPath());
			recurcive(folder, list);
		}
	}

	/**
	 * 문서 타입 JSON - AUI그리드용 - 금형 문서 포함 제외 여부 true, 포함, fasle 제외
	 *
	 */
	public JSONArray toJson() throws Exception {
		return toJson(false);
	}

	/**
	 * 문서 타입 JSON - AUI그리드용 - 금형 문서 포함 제외 여부
	 */
	public JSONArray toJson(boolean include) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		DocumentType[] dlist = DocumentType.getDocumentTypeSet();
		for (DocumentType t : dlist) {
			Map<String, String> map = new HashMap<>();
			if (!include) {
				if (t.toString().equals("$$MMDocument")) {
					continue;
				}
			}
			map.put("key", t.toString());
			map.put("value", t.getDisplay());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 문서 종류 바인더
	 */
	public ArrayList<Map<String, String>> finder(Map<String, String> params) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String value = params.get("value");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "DOCUMENTNAME");
		QuerySpecUtils.toLikeAnd(query, idx, NumberCode.class, NumberCode.NAME, value);
		QuerySpecUtils.toOrderBy(query, idx, NumberCode.class, NumberCode.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode n = (NumberCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("value", n.getName());
			map.put("name", n.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 마지막 번호 받아오기
	 */
	public String lastNumber(String number, String classType1) throws Exception {
		DecimalFormat df = new DecimalFormat("000");
		String rtnNumber = "";
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocumentMaster.class, true);
		SearchCondition sc = new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(WTDocumentMaster.class, WTDocumentMaster.NUMBER);
		OrderBy by = new OrderBy(ca, true);
		query.appendOrderBy(by, new int[] { idx });
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			WTDocumentMaster m = (WTDocumentMaster) obj[0];
			String num = m.getNumber();
			int last = num.lastIndexOf("N");
			String s = num.substring(last + 1);
			int next = Integer.parseInt(s) + 1;
			String prefix = num.substring(0, last + 1);
			rtnNumber = prefix + df.format(next);
//			rtnNumber = getRtnNumberPattern(num, classType1);
		}

		if (qr.size() == 0) {
			rtnNumber = number + firstNumber(classType1);
		}
		return rtnNumber;
	}

	/**
	 * 값이 없을 경우 리턴
	 */
	private String firstNumber(String classType1) throws Exception {
		String rtnNumber = "";
		String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
		// 2023-23-12
		String prefixYear = today.substring(0, 2);
		String suffixYear = today.substring(2, 4);
		String month = today.substring(5, 7);

		if ("DEV".equals(classType1) || "INSTRUCTION".equals(classType1) || "REPORT".equals(classType1)
				|| "VALIDATION".equals(classType1)) {
			rtnNumber = "N001";
		} else if ("CHANGE".equals(classType1) || "MEETING".equals(classType1))

		{
			rtnNumber = suffixYear + "-" + month + "-N001";
		}
		return rtnNumber;
	}

	/**
	 * 옴길 대상 목록
	 */
	public JSONArray getMoveTarget(Folder f) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);
		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);
		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
		ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
		SearchCondition fsc = new SearchCondition(fca, "=",
				new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
		fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
		fsc.setOuterJoin(0);
		query.appendWhere(fsc, new int[] { f_idx, idx });
		query.appendAnd();
		long fid = f.getPersistInfo().getObjectIdentifier().getId();
		query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
				new int[] { f_idx });

		QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("oid", doc.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", doc.getName());
			map.put("number", IBAUtils.getStringValue(doc, "INTERALNUMBER"));
			map.put("location", doc.getLocation());
			map.put("version", doc.getVersionIdentifier().getSeries().getValue() + "."
					+ doc.getIterationIdentifier().getSeries().getValue());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 워드 파일 PDF 생성
	 */
	public void wordToPdfMethod(String oid) throws Exception {

		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", oid);

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, wordToPdfMethod, className, argClasses, argObjects);
	}

	/**
	 * 표지 싸인입력
	 */
	public File stamping(WTDocument d, File excelFile, String classTypeCode) throws Exception {
		String number = d.getNumber();
		ApprovalMaster m = WorkspaceHelper.manager.getMaster(d);
		ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLine(m);

		Workbook workbook = new Workbook(excelFile.getPath());
		Worksheet worksheet = workbook.getWorksheets().get(0);

		ObjectReference ref = d.getTypeInfoWTDocument().getPtc_ref_2();
		DocumentClass classType2 = null;
		if (ref != null) {
			classType2 = (DocumentClass) ref.getObject();
		}

		// 지침서 양식번호
		if ("INSTRUCTION".equals(classTypeCode)) {
			worksheet.getPageSetup().setFooter(0, "P7.3-1-1");
		} else if ("DEV".equals(classTypeCode)) {
			// 개발 문서
			if (classType2 != null) {
				String clazz = classType2.getClazz();
				String n = "";
				// 위험 관리 계획서 QF-701-01
				if ("RMP".equals(clazz.trim())) {
					n = "QF-701-01";
				} else if ("RMR".equals(clazz.trim())) {
					n = "QF-701-02	";
				} else if ("PRS".equals(clazz.trim())) {
					n = "QF-705-12";
				} else if ("DSP".equals(clazz.trim())) {
					n = "QF-705-16";
				} else if ("DSR".equals(clazz.trim())) {
					n = "QF-705-18";
				} else if ("DMR".equals(clazz.trim())) {
					n = "QF-705-19";
				} else if ("CCL".equals(clazz.trim())) {
					n = "QF-701-20";
				} else if ("VR".equals(clazz.trim())) {
					n = "QF-701-29";
				} else {
					n = "P7.3-1-1";
				}
				worksheet.getPageSetup().setFooter(0, n);
			}
		}

		Cell modelCell = worksheet.getCells().get(3, 5);
		modelCell.putValue(IBAUtil.getStringValue(d, "MODEL"));

		Cell nameCell = worksheet.getCells().get(4, 0);
		if (classType2 != null) {
			if (!"INSTRUCTION".equals(classTypeCode)) {
				nameCell.putValue(classType2.getName());
			} else {
				nameCell.putValue(d.getName());
			}
		} else {
			nameCell.putValue("");
		}

		if (classType2 != null) {
			String cc = classType2.getClazz().trim();
			System.out.println("cc=" + cc);
			if ("DMR".equals(cc)) {
				System.out.println("제품명 = " + d.getTypeInfoWTDocument().getPtc_str_1());
				Cell productCell = worksheet.getCells().get(7, 0);
				productCell.putValue(d.getTypeInfoWTDocument().getPtc_str_1());
			}
		}

		Cell numberCell = worksheet.getCells().get(10, 3);
		numberCell.putValue(number);

		Cell versionCell = worksheet.getCells().get(11, 3);
		versionCell.putValue(d.getVersionIdentifier().getSeries().getValue());

		// 최종결재에 만들어지니 당일 날짜로 하면된다..

		String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
		Cell dateCell = worksheet.getCells().get(12, 3);
		dateCell.putValue(today);

		int rowIndex = 15;
		int rowHeight = 30;

		ApprovalLine submitLine = WorkspaceHelper.manager.getSubmitLine(m);
		if (submitLine != null) {
			Row row = worksheet.getCells().getRows().get(rowIndex);
			row.setHeight(rowHeight);

			Cell cell = worksheet.getCells().get(rowIndex, 1); // 결재타입
			cell.putValue("기안");
			setCellStyle(cell);

			WTUser user = (WTUser) submitLine.getOwnership().getOwner().getObject();
			PeopleDTO pdata = new PeopleDTO(user);
			cell = worksheet.getCells().get(rowIndex, 2); // 이름+팀
			cell.putValue(pdata.getName() + "[" + pdata.getDepartment_name() + "]");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 3); // 결재일
			cell.putValue(
					submitLine.getCompleteTime() != null ? submitLine.getCompleteTime().toString().substring(0, 10)
							: "");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 4);
			setCellStyle(cell);
			String signPath = OrgHelper.manager.getSignPath(user.getName());
			if (signPath != null) {
				int picIndex = worksheet.getPictures().add(rowIndex, 4, signPath);
				Picture picture = worksheet.getPictures().get(picIndex);
				picture.setHeightCM(1);
				picture.setWidthCM(2);
			}

			rowIndex++;
		}

		for (ApprovalLine agreeLine : agreeLines) {
			Row row = worksheet.getCells().getRows().get(rowIndex);
			row.setHeight(rowHeight);

			Cell cell = worksheet.getCells().get(rowIndex, 1); // 결재타입
			cell.putValue("합의");
			setCellStyle(cell);

			WTUser user = (WTUser) agreeLine.getOwnership().getOwner().getObject();
			PeopleDTO pdata = new PeopleDTO(user);
			cell = worksheet.getCells().get(rowIndex, 2); // 이름+팀
			cell.putValue(pdata.getName() + "[" + pdata.getDepartment_name() + "]");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 3); // 결재일
			cell.putValue(
					agreeLine.getCompleteTime() != null ? agreeLine.getCompleteTime().toString().substring(0, 10) : "");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 4);
			setCellStyle(cell);

			String signPath = OrgHelper.manager.getSignPath(user.getName());
			if (signPath != null) {
				int picIndex = worksheet.getPictures().add(rowIndex, 4, signPath);
				Picture picture = worksheet.getPictures().get(picIndex);
				picture.setHeightCM(1);
				picture.setWidthCM(2);
			}

			rowIndex++;
		}

		ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(m);
		for (ApprovalLine approvalLine : approvalLines) {
			Row row = worksheet.getCells().getRows().get(rowIndex);
			row.setHeight(rowHeight);

			Cell cell = worksheet.getCells().get(rowIndex, 1); // 결재타입
			cell.putValue("결재");
			setCellStyle(cell);

			WTUser user = (WTUser) approvalLine.getOwnership().getOwner().getObject();
			PeopleDTO pdata = new PeopleDTO(user);
			cell = worksheet.getCells().get(rowIndex, 2); // 이름+팀
			cell.putValue(pdata.getName() + "[" + pdata.getDepartment_name() + "]");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 3); // 결재일
			cell.putValue(
					approvalLine.getCompleteTime() != null ? approvalLine.getCompleteTime().toString().substring(0, 10)
							: "");
			setCellStyle(cell);

			cell = worksheet.getCells().get(rowIndex, 4);
			setCellStyle(cell);
			String signPath = OrgHelper.manager.getSignPath(user.getName());
			if (signPath != null) {
				int picIndex = worksheet.getPictures().add(rowIndex, 4, signPath);
				Picture picture = worksheet.getPictures().get(picIndex);
				picture.setHeightCM(1);
				picture.setWidthCM(2);
			}

			rowIndex++;
		}
		String temp = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "cover";
		File f = new File(temp);
		if (!f.exists()) {
			f.mkdirs();
		}
		String fullPath = temp + File.separator + number + ".xlsx";
		workbook.save(fullPath);
		return new File(fullPath);
	}

	/**
	 * PDF 병합
	 */
	public void mergePdf(WTDocument doc) throws Exception {
		System.out.println("문서 PDF병합 시작!");

		doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

		String temp = WTProperties.getLocalProperties().getProperty("wt.temp");
		String mergePath = temp + File.separator + "merge";
		File mergeFile = new File(mergePath);
		if (!mergeFile.exists()) {
			mergeFile.mkdirs();
		}

		QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.toContentRoleType("COVER"));
		String coverPath = "";
		if (qr.hasMoreElements()) {
			ApplicationData data = (ApplicationData) qr.nextElement();
			byte[] buffer = new byte[10240];
			InputStream is = ContentServerHelper.service.findLocalContentStream(data);
//			String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
			String name = data.getFileName();
			File file = new File(mergePath + File.separator + name);
			coverPath = file.getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buffer, 0, 10240)) > 0) {
				fos.write(buffer, 0, j);
			}
			fos.close();
			is.close();
		}

		qr.reset();
		qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.toContentRoleType("PDF"));
		String wordPath = "";
		String name = "";
		if (qr.hasMoreElements()) {
			ApplicationData data = (ApplicationData) qr.nextElement();
			byte[] buffer = new byte[10240];
			InputStream is = ContentServerHelper.service.findLocalContentStream(data);
//			String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
			name = data.getFileName();
			File file = new File(mergePath + File.separator + name);
			wordPath = file.getAbsolutePath();
			FileOutputStream fos = new FileOutputStream(file);
			int j = 0;
			while ((j = is.read(buffer, 0, 10240)) > 0) {
				fos.write(buffer, 0, j);
			}
			fos.close();
			is.close();
		}

		// 기존 변환된 워드 문서 삭제
		QueryResult result = ContentHelper.service.getContentsByRole(doc, ContentRoleType.toContentRoleType("PDF"));
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			ContentServerHelper.service.deleteContent(doc, data);
		}

		AsposeUtils.setAsposePdfLic();
		Document coverPdf = new Document(coverPath);
		Document wordPdf = new Document(wordPath);
		coverPdf.getPages().add(wordPdf.getPages());
		wordPdf.close();

		String compPath = mergePath + File.separator + "complete";
		File compPdf = new File(compPath);
		if (!compPdf.exists()) {
			compPdf.mkdirs();
		}
		String savePath = compPath + File.separator + name;
		coverPdf.save(savePath);
		coverPdf.close();

		// 기존꺼 제거...
		QueryResult rs = ContentHelper.service.getContentsByRole(doc, ContentRoleType.toContentRoleType("MERGE"));
		if (rs.hasMoreElements()) {
			ApplicationData dd = (ApplicationData) rs.nextElement();
			ContentServerHelper.service.deleteContent(doc, dd);
		}

		System.out.println("savePath=" + savePath);
		ApplicationData applicationData = ApplicationData.newApplicationData(doc);
		applicationData.setRole(ContentRoleType.toContentRoleType("MERGE"));
		PersistenceHelper.manager.save(applicationData);
		ContentServerHelper.service.updateContent(doc, applicationData, savePath);
	}

	/**
	 * 일반 문서 워드 생성 및 PDF변환
	 */
	public void genWordAndPdfMethod(String oid) throws Exception {
		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", oid);

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, genWordAndPdfMethod, className, argClasses, argObjects);
	}

	/**
	 * 엑셀 셀 스타일링
	 */
	private void setCellStyle(Cell cell) throws Exception {
		Style style = cell.getStyle();
		Font font = style.getFont();
		font.setBold(true);
		style.setHorizontalAlignment(TextAlignmentType.CENTER);
		style.setVerticalAlignment(TextAlignmentType.CENTER);
		style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
		cell.setStyle(style);
	}

	/**
	 * 프린터물 문서양식번호
	 * 
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public String getFooterNumber(String oid) throws Exception {
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);

		WTDocumentTypeInfo info = doc.getTypeInfoWTDocument();
		if (info != null) {
			DocumentClass classType2 = (DocumentClass) info.getPtc_ref_2().getObject();
			if (classType2 != null) {
				String name = classType2.getName();
				if ("설계계획검토회의".equals(name)) {
					return "QF-705-17";
				} else if ("회의록".equals(name)) {
					return "P7.3-1-16";
				} else if ("산업디자인검토회의".equals(name)) {
					return "QF-705-27";
				} else if ("설계및검증검토회의".equals(name)) {
					return "QF-705-23";
				} else if ("PP완료여부회의".equals(name)) {
					return "QF-705-25";
				} else if ("테스트보고서".equals(name)) {
					return "QF-705-08";
				} else if ("제3자검증의뢰서".equals(name)) {
					return "P7.3-2-1";
				}
			}
		}
		return "";
	}

	/**
	 * 일반문서 다음 번호
	 */
	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("00");
		String rtn = null;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			WTDocument etc = (WTDocument) obj[0];
			String etcNumber = etc.getNumber();
			int ii = etcNumber.lastIndexOf("-");
			String next = etcNumber.substring(ii + 1); // 00
			int n = Integer.parseInt(next) + 1;
			rtn = number + df.format(n);
		} else {
			rtn = number + "01";
		}
		return rtn;
	}
}