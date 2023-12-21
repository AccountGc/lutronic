package com.e3ps.doc.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.ecpr.column.EcprColumn;
import com.e3ps.change.eo.column.EoColumn;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
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
import com.e3ps.part.column.PartColumn;
import com.ibm.icu.text.DecimalFormat;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.IteratedFolderMemberLink;
import wt.folder.SubFolder;
import wt.org.WTPrincipal;
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
	private static final String methodName = "wordToPdf";

	/**
	 * 문서 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
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
		String model = (String) params.get("model");
		String deptcode = (String) params.get("deptcode");
		String interalnumber = (String) params.get("interalnumber");
		String writer = (String) params.get("writerOid");
		String description = (String) params.get("description");

		// 분류
		String classType1 = (String) params.get("classType1");
		String classType2 = (String) params.get("classType2");
		String classType3 = (String) params.get("classType3");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

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
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL,
				"TEMPRARY"), new int[] { idx });

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
		QuerySpecUtils.toIBALikeAnd(query, WTDocument.class, idx, "MODEL", model);
		QuerySpecUtils.toIBALikeAnd(query, WTDocument.class, idx, "INTERALNUMBER", interalnumber);
		QuerySpecUtils.toIBAEqualsAnd(query, WTDocument.class, idx, "DEPTCODE", deptcode);
		QuerySpecUtils.toIBALikeAnd(query, WTDocument.class, idx, "DSGN", writer);

		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
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

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DocumentColumn data = new DocumentColumn(obj);
			list.add(data);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
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
	public void genWordToPdf(String oid) throws Exception {
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", oid);

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}

	/**
	 * 표지 싸인입력
	 */
	public File stamping(WTDocument doc, String key, File excelFile) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * PDF 병합
	 */
	public void mergePdf(WTDocument doc) throws Exception {

		QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.toContentRoleType("COVER"));)
		


//String first = list.get(0);
//Document firstPdf = new Document(first);
//list.remove(0);
//
//for (String path : list) {
//	Document pdf = new Document(path);
//	firstPdf.getPages().add(pdf.getPages());
//	pdf.close();
//}
//
//String mergePdfPath = mergePath + num + ".pdf";
//firstPdf.save(mergePdfPath);

		
	}
}