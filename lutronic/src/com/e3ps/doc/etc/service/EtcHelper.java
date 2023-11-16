package com.e3ps.doc.etc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.part.column.PartColumn;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;

public class EtcHelper {

	public static final EtcService service = ServiceFactory.getService(EtcService.class);
	public static final EtcHelper manager = new EtcHelper();

	public static final String RA = "/Default/RA팀 문서관리/RA팀";
	public static final String PRODUCTION = "/Default/생산본부 문서관리/생산본부";
	public static final String PATHOLOGICAL = "/Default/병리연구 문서관리/병리연구";
	public static final String CLINICAL = "/Default/임상개발 문서관리/임상개발";
	public static final String COSMETIC = "/Default/화장품 문서관리/화장품";

	/**
	 * 기타 문서 타입별 위치
	 */
	public String toLocation(String type) throws Exception {
		if ("ra".equalsIgnoreCase(type)) {
			return RA;
		} else if ("production".equalsIgnoreCase(type)) {
			return PRODUCTION;
		} else if ("pathological".equalsIgnoreCase(type)) {
			return PATHOLOGICAL;
		} else if ("clinical".equalsIgnoreCase(type)) {
			return CLINICAL;
		} else if ("cosmetic".equalsIgnoreCase(type)) {
			return COSMETIC;
		}
		return null;
	}
	
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

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NUMBER, number);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.DESCRIPTION, description);
		QuerySpecUtils.toState(query, idx, WTDocument.class, state);
		QuerySpecUtils.creatorQuery(query, idx, WTDocument.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, modifiedFrom,
				modifiedTo);
		
		QuerySpecUtils.toEqualsAnd(query, idx, WTDocument.class, WTDocument.DOC_TYPE, documentType);

		// 보존 기간
    	if(preseration.length() > 0) {
    		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_PRESERATION);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + preseration + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			preseration = "";
		}
    	
    	// 프로젝트 코드
		if (model.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + model + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			model = "";
		}
		
		// 내부 문서번호
		if (interalnumber.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_INTERALNUMBER);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + interalnumber + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			interalnumber = "";
		}
		
		// 부서
		if (deptcode.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DEPTCODE);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			deptcode = "";
		}
		
		// 작성자
		if (writer.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DSGN);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.EQUAL, Long.toString(CommonUtil.getOIDLongValue(writer)), false), new int[] { _idx });
			}
		} else {
			deptcode = "";
		}
		

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
			Map<String, Object> map = new HashMap<>();
			DocumentColumn dto = new DocumentColumn(ref);
			map.put("oid", dto.getOid());
			map.put("number", dto.getNumber());
			map.put("interalnumber", dto.getInteralnumber());
			map.put("model", dto.getModel());
			map.put("name", dto.getName());
			map.put("location", dto.getLocation());
			map.put("version", dto.getVersion());
			map.put("state", dto.getState());
			map.put("writer", dto.getWriter());
			map.put("creator", dto.getCreator());
			map.put("createdDate_txt", dto.getCreatedDate_txt());
			map.put("modifier", dto.getModifier());
			map.put("modifiedDate_txt", dto.getModifiedDate_txt());
			map.put("primary", dto.getPrimary());
			map.put("secondary", dto.getSecondary());
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 ECPR
	 */
	private Object referenceEcpr(WTDocument doc, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(doc, "describes", WTPartDescribeLink.class);
		while (result.hasMoreElements()) {
			WTPart part = (WTPart) result.nextElement();
			Map<String, Object> map = new HashMap<>();
			PartColumn dto = new PartColumn(part);
			map.put("part_oid", dto.getPart_oid());
			map.put("_3d", dto.get_3d());
			map.put("_2d", dto.get_2d());
			map.put("number", dto.getNumber());
			map.put("name", dto.getName());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreatedDate().toString().substring(0, 10));
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
			WTPart part = (WTPart) result.nextElement();
			Map<String, Object> map = new HashMap<>();
			PartColumn dto = new PartColumn(part);
			map.put("part_oid", dto.getPart_oid());
			map.put("_3d", dto.get_3d());
			map.put("_2d", dto.get_2d());
			map.put("number", dto.getNumber());
			map.put("name", dto.getName());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreatedDate().toString().substring(0, 10));
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
			WTPart part = (WTPart) result.nextElement();
			Map<String, Object> map = new HashMap<>();
			PartColumn dto = new PartColumn(part);
			map.put("part_oid", dto.getPart_oid());
			map.put("_3d", dto.get_3d());
			map.put("_2d", dto.get_2d());
			map.put("number", dto.getNumber());
			map.put("name", dto.getName());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreatedDate().toString().substring(0, 10));
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
			WTPart part = (WTPart) result.nextElement();
			Map<String, Object> map = new HashMap<>();
			PartColumn dto = new PartColumn(part);
			map.put("part_oid", dto.getPart_oid());
			map.put("_3d", dto.get_3d());
			map.put("_2d", dto.get_2d());
			map.put("number", dto.getNumber());
			map.put("name", dto.getName());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreatedDate().toString().substring(0, 10));
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
			Map<String, Object> map = new HashMap<>();
			PartColumn dto = new PartColumn(part);
//			map.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("part_oid", dto.getPart_oid());
			map.put("_3d", dto.get_3d());
			map.put("_2d", dto.get_2d());
			map.put("number", dto.getNumber());
			map.put("name", dto.getName());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreatedDate().toString().substring(0, 10));
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 기타 문서 이력
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
//		} else if (target.equals(DocumentECPRLink.class)) {
//			QueryResult qr = PersistenceHelper.manager.navigate(doc, "ecpr", DocumentECPRLink.class);
//			isConnect = qr.size() > 0;
		} else if (target.equals(DocumentCRLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "cr", DocumentCRLink.class);
			isConnect = qr.size() > 0;
		} else if (target.equals(WTPartDescribeLink.class)) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "describes", WTPartDescribeLink.class);
			isConnect = qr.size() > 0;
		}
		return isConnect;
	}

}
