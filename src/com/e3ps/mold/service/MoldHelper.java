package com.e3ps.mold.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.mold.dto.MoldDTO;

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
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;

public class MoldHelper {

	public static final MoldService service = ServiceFactory.getService(MoldService.class);
	public static final MoldHelper manager = new MoldHelper();

	/**
	 * 금형 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<MoldDTO> list = new ArrayList<>();

		String location = StringUtil.checkNull((String) params.get("location"));
		String islastversion = StringUtil.checkNull((String) params.get("islastversion"));
		String docNumber = StringUtil.checkNull((String) params.get("number"));
		String docName = StringUtil.checkNull((String) params.get("name"));
		String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
		String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
		String modifiedFrom = StringUtil.checkNull((String) params.get("modifiedFrom"));
		String modifiedTo = StringUtil.checkNull((String) params.get("modifiedTo"));
		String creator = StringUtil.checkNull((String) params.get("creatorOid"));
		String state = StringUtil.checkNull((String) params.get("state"));
		String interalnumber = StringUtil.checkNull((String) params.get("interalnumber"));
		String deptcode = StringUtil.checkNull((String) params.get("deptcode"));
		String description = StringUtil.checkNull((String) params.get("description"));
		String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
		String moldtype = StringUtil.checkNull((String) params.get("moldtype"));
		String moldCost = StringUtil.checkNull((String) params.get("moldcost"));
		String searchType = StringUtil.checkNull((String) params.get("searchType"));
		String moldNumber = StringUtil.checkNull((String) params.get("moldnumber"));

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		// 최신 이터레이션
		if ("true".equals(islastversion)) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		// 일괄 결재시 타입에 따른 LC 상태 검색
		if (searchType.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			if ("MOLD".equals(searchType)) {
				QuerySpecUtils.toEquals(query, idx, WTDocument.class, WTDocument.DOC_TYPE, "$$MMDocument");
			}
		}

		// 상태 임시저장 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL,
				"TEMPRARY"), new int[] { idx });

		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NUMBER, docNumber);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NAME, docName);
		QuerySpecUtils.creatorQuery(query, idx, WTDocument.class, creator);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, modifiedFrom,
				modifiedTo);
		QuerySpecUtils.toState(query, idx, WTDocument.class, state);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.DESCRIPTION, description);

		// 내부 문서번호
		if (interalnumber.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_INTERALNUMBER);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + interalnumber + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			interalnumber = "";
		}

		// 부서
		if (deptcode.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DEPTCODE);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
			}
		} else {
			deptcode = "";
		}

		// manufacture
		if (manufacture.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + manufacture + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			manufacture = "";
		}

		// 금형타입
		if (moldtype.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MOLDTYPE);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + moldtype + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			moldtype = "";
		}

		// 금형번호
		if (moldNumber.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MOLDNUMBER);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + moldNumber + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			moldNumber = "";
		}

		// 금형개발비
		if (moldCost.length() > 0) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MOLDCOST);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
						WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + moldCost + "%").toUpperCase(), false), new int[] { _idx });
			}
		} else {
			moldCost = "";
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

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = pager.getTotal();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];
			MoldDTO data = new MoldDTO(document);
			data.setRowNum(rowNum--);
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

	public List<MoldDTO> getDocumentListToLinkRoleName(WTDocument document, String roleName) throws Exception {
		List<MoldDTO> list = new ArrayList<MoldDTO>();

		List<DocumentToDocumentLink> linkList = getDocumentToDocumentLinks(document, roleName);
		for (DocumentToDocumentLink link : linkList) {
			WTDocument doc = null;
			if ("used".equals(roleName)) {
				doc = link.getUsed();
			} else if ("useBy".equals(roleName)) {
				doc = link.getUseBy();
			}

			MoldDTO data = new MoldDTO(doc);
			list.add(data);
		}
		return list;
	}

	/**
	 * 관련 문서 링크 가져오기
	 */
	public List<DocumentToDocumentLink> getDocumentToDocumentLinks(WTDocument document, String roleName)
			throws Exception {
		List<DocumentToDocumentLink> list = new ArrayList<DocumentToDocumentLink>();

		String vrOid = CommonUtil.getVROID(document);
		document = (WTDocument) CommonUtil.getObject(vrOid);
		QueryResult qr = PersistenceHelper.manager.navigate(document, roleName, DocumentToDocumentLink.class, false);
		while (qr.hasMoreElements()) {
			DocumentToDocumentLink link = (DocumentToDocumentLink) qr.nextElement();
			list.add(link);
		}
		return list;
	}

	/**
	 * 관련 문서 링크 관계 확인 ( true : 연결, false : 미연결 )
	 */
	public boolean isConnect(String oid) throws Exception {
		boolean isConnect = false;
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		List<DocumentToDocumentLink> used = getDocumentToDocumentLinks(doc, "used");
		List<DocumentToDocumentLink> useBy = getDocumentToDocumentLinks(doc, "useBy");
		if (used.size() > 0 || useBy.size() > 0) {
			isConnect = true;
		}
		return isConnect;
	}

	/**
	 * 금형 이력
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
}
