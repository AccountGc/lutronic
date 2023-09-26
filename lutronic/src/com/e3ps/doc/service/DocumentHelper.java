package com.e3ps.doc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e3ps.admin.form.FormTemplate;
import com.e3ps.common.comments.Comments;
import com.e3ps.common.comments.CommentsData;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.FolderUtils;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devOutPutLink;
import com.e3ps.doc.DocumentCRLink;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.DocumentECPRLink;
import com.e3ps.doc.DocumentEOLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.doc.dto.DocumentDTO;
import com.e3ps.doc.template.DocumentTemplateData;
import com.e3ps.download.DownloadHistory;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.groupware.workprocess.service.AsmSearchHelper;
import com.e3ps.org.People;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.part.column.PartColumn;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class DocumentHelper {

	/**
	 * 문서 기본 위치
	 */
	public static final String DOCUMENT_ROOT = "/Default/문서";

	public static final DocumentService service = ServiceFactory.getService(DocumentService.class);
	public static final DocumentHelper manager = new DocumentHelper();

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

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NUMBER, number);
//		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.DESCRIPTION, description);
		QuerySpecUtils.toState(query, idx, WTDocument.class, state);
		QuerySpecUtils.creatorQuery(query, idx, WTDocument.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, modifiedFrom,
				modifiedTo);

		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
		if (folder != null) {
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
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
						new int[] { f_idx });
			}
			query.appendCloseParen();
		}

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];
			DocumentColumn data = new DocumentColumn(document);
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

		if ("part".equalsIgnoreCase(type)) {
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
				map.put("createdDate", dto.getCreateDate());
				list.add(map);
			}

		} else if ("doc".equalsIgnoreCase(type)) {

		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 문서 결재 이력
	 */
	public JSONArray allIterationsOf(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		QueryResult result = VersionControlHelper.service.allIterationsOf(doc.getMaster());
		while (result.hasMoreElements()) {
			WTDocument d = (WTDocument) result.nextElement();
			Map<String, Object> map = new HashMap<>();
			DocumentColumn dto = new DocumentColumn(d);
			map.put("oid", dto.getOid());
			map.put("name", dto.getName());
			map.put("number", dto.getNumber());
			map.put("version", dto.getVersion());
			map.put("creator", dto.getCreator());
			map.put("createdDate", dto.getCreatedDate());
			map.put("note", d.getIterationNote());
			map.put("primary", dto.getPrimary());
			map.put("secondary", dto.getSecondary());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 다운로드 이력
	 */
	public JSONArray a(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(DownloadHistory.class, true);
		QuerySpecUtils.toEquals(query, idx, DownloadHistory.class, DownloadHistory.D_OID, oid);
		QuerySpecUtils.toOrderBy(query, idx, DownloadHistory.class, "thePersistInfo.createStamp", false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			DownloadHistory history = (DownloadHistory) obj[0];
			Map<String, Object> map = new HashMap<>();
			WTUser user = history.getUser();
			PeopleDTO dto = new PeopleDTO(user);
			map.put("oid", history.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("count", history.getDCount());
			map.put("name", dto.getName());
			map.put("duty", dto.getDuty());
			map.put("time", history.getPersistInfo().getCreateStamp());
			map.put("departmentName", dto.getDepartment_name());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	public Map<String, Object> listMoldAction(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<DocumentDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
//    	int idx = query.addClassList(WTDocument.class, true);
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		ReferenceFactory rf = new ReferenceFactory();

		try {
			String location = StringUtil.checkNull((String) params.get("location"));
			String foid = StringUtil.checkNull((String) params.get("fid"));
			if (foid.length() == 0) {
				foid = StringUtil.checkNull((String) params.get("folder"));
			}
			if (location.length() == 0) {
				location = "/Default/Document";
			}
			// System.out.println("location =" + location);
			String islastversion = StringUtil.checkNull((String) params.get("islastversion"));
			String docNumber = StringUtil.checkNull((String) params.get("docNumber"));
			String docName = StringUtil.checkNull((String) params.get("docName"));
			String predate = StringUtil.checkNull((String) params.get("predate"));
			String postdate = StringUtil.checkNull((String) params.get("postdate"));
			String predate_modify = StringUtil.checkNull((String) params.get("predate_modify"));
			String postdate_modify = StringUtil.checkNull((String) params.get("postdate_modify"));
			String creator = StringUtil.checkNull((String) params.get("creator"));
			String state = StringUtil.checkNull((String) params.get("state"));

			String documentType = StringUtil.checkNull((String) params.get("documentType"));
			String preseration = StringUtil.checkNull((String) params.get("preseration"));
			String model = StringUtil.checkNull((String) params.get("model"));
			String interalnumber = StringUtil.checkNull((String) params.get("interalnumber"));
			String deptcode = StringUtil.checkNull((String) params.get("deptcode"));
			String writer = StringUtil.checkNull((String) params.get("writer"));

			String description = StringUtil.checkNull((String) params.get("description"));

			String sortValue = StringUtil.checkNull((String) params.get("sortValue"));
			String sortCheck = StringUtil.checkNull((String) params.get("sortCheck"));

			String searchType = StringUtil.checkNull((String) params.get("searchType"));

			String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
			String moldtype = StringUtil.checkNull((String) params.get("moldtype"));
			String moldNumber = StringUtil.checkNull((String) params.get("moldnumber"));
			String moldCost = StringUtil.checkNull((String) params.get("moldcost"));

			// 최신 이터레이션
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(VersionControlHelper.getSearchCondition(WTDocument.class, true), new int[] { idx });

			// 버전 검색
			if (!StringUtil.checkString(islastversion)) {
				islastversion = "true";
			}

			if ("true".equals(islastversion)) {
				SearchUtil.addLastVersionCondition(query, WTDocument.class, idx);
			}

			// Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(WTDocument.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL,
						"wrk", false), new int[] { idx });
			}

			// 일괄 결재시 타입에 따른 LC 상태 검색
			if (searchType.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				if ("document".equals(searchType) || "DOC".equals(searchType)) {
					query.appendOpenParen();
					query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE,
							SearchCondition.NOT_EQUAL, "$$MMDocument"), new int[] { idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE,
							SearchCondition.NOT_EQUAL, "$$ROHS"), new int[] { idx });
					query.appendCloseParen();
				} else if ("MOLD".equals(searchType)) {
					query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL,
							"$$MMDocument"), new int[] { idx });
				} else if ("ROHS".equals(searchType)) {
					query.appendWhere(
							new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, "$$ROHS"),
							new int[] { idx });
				}
			}

			// 문서번호
			if (docNumber.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE,
						"%" + docNumber + "%", false), new int[] { idx });
			}

			// 문서명
			if (docName.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE,
						"%" + docName + "%", false), new int[] { idx });
			}

			// 등록일
			if (predate.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[] { idx });
			}

			if (postdate.length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[] { idx });
			}

			// 수정일
			if (predate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.MODIFY_TIMESTAMP,
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[] { idx });
			}

			if (postdate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.MODIFY_TIMESTAMP,
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[] { idx });
			}

			// 등록자
			if (creator.length() > 0) {
				People people = (People) rf.getReference(creator).getObject();
				WTUser user = people.getUser();
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id",
						SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[] { idx });
			}

			// 상태
			if (state.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE,
						SearchCondition.EQUAL, state), new int[] { idx });
			}

			// 문서 종류
			if (documentType.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(
						new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, documentType),
						new int[] { idx });
			}

			// 보존 기간
			if (preseration.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_PRESERATION);
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
							("%" + preseration + "%").toUpperCase(), false), new int[] { _idx });
				}
			} else {
				preseration = "";
			}

			// 프로젝트 코드
			if (model.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
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
							("%" + model + "%").toUpperCase(), false), new int[] { _idx });
				}
			} else {
				model = "";
			}

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

			// 등록자
			if (writer.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DSGN);
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
							("%" + writer + "%").toUpperCase(), false), new int[] { _idx });
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

			// 금형번호
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

			// 설명
			if (description.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DESCRIPTION, SearchCondition.LIKE,
						"%" + description + "%", false), new int[] { idx });
			}

			// folder search
//	    	if (location.length() > 0) {
//				int l = location.indexOf(ROOTLOCATION);
//				if (l >= 0) {
//					if (query.getConditionCount() > 0) {
//						query.appendAnd();
//					}
//					location = location.substring((l + ROOTLOCATION.length()));
//					// Folder Search
//					int folder_idx = query.addClassList(DocLocation.class, false);
//					query.appendWhere(new SearchCondition(DocLocation.class, DocLocation.DOC, WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { folder_idx, idx });
//					query.appendAnd();
//
//					query.appendWhere(new SearchCondition(DocLocation.class, "loc", SearchCondition.LIKE, location + "%"), new int[] { folder_idx });
//				}
//			}

			// 소팅
			if (sortCheck == null)
				sortCheck = "true";
			boolean sort = "true".equals(sortCheck);
			if (sortValue != null && sortValue.length() > 0) {

				if (!"creator".equals(sortValue)) {
					// query.appendOrderBy(new OrderBy(new
					// ClassAttribute(WTDocument.class,sortValue), true), new int[] { idx });
					SearchUtil.setOrderBy(query, WTDocument.class, idx, sortValue, "sort", sort);
				} else {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					int idx_user = query.appendClassList(WTUser.class, false);
					int idx_people = query.appendClassList(People.class, false);

					ClassAttribute ca = new ClassAttribute(WTDocument.class, "creator.key.id");
					ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
					query.appendWhere(new SearchCondition(ca, "=", ca2), new int[] { idx, idx_user });
					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
					query.appendAnd();
					query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
					SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", sort);
				}

			} else {
				// query.appendOrderBy(new OrderBy(new
				// ClassAttribute(WTDocument.class,"thePersistInfo.createStamp"), true), new
				// int[] { idx });
				SearchUtil.setOrderBy(query, WTDocument.class, idx, WTDocument.MODIFY_TIMESTAMP, "sort", true);
			}

			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTDocument document = (WTDocument) obj[0];
				DocumentDTO data = new DocumentDTO(document);
				list.add(data);
			}

			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	public JSONArray include_DocumentList(String oid, String moduleType) throws Exception {
		List<DocumentDTO> list = new ArrayList<DocumentDTO>();
		try {
			if (StringUtil.checkString(oid)) {
				if ("part".equals(moduleType)) {
					WTPart part = (WTPart) CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class);
					while (qr.hasMoreElements()) {
						WTDocument doc = (WTDocument) qr.nextElement();
						DocumentDTO data = new DocumentDTO(doc);
						// Part가 최신 버전이면 관련 문서가 최신 버전만 ,Part가 최신 버전이 아니면 모든 버전
						if (CommonUtil.isLatestVersion(part)) {
							if (data.isLatest()) {
								list.add(data);
							}
						} else {
							list.add(data);
						}
					}
				} else if ("doc".equals(moduleType)) {
					List<DocumentDTO> dataList = DocumentQueryHelper.service.getDocumentListToLinkRoleName(oid, "used");
					for (DocumentDTO data : dataList) {
						list.add(data);
					}

					dataList = DocumentQueryHelper.service.getDocumentListToLinkRoleName(oid, "useBy");
					for (DocumentDTO data : dataList) {
						list.add(data);
					}

				} else if ("active".equals(moduleType)) {
					devActive m = (devActive) CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(m, "output", devOutPutLink.class);

					while (qr.hasMoreElements()) {
						Object p = (Object) qr.nextElement();
						if (p instanceof WTDocument) {
							DocumentDTO data = new DocumentDTO((WTDocument) p);
							list.add(data);
						}
					}
				} else if ("asm".equals(moduleType)) {
					AsmApproval asm = (AsmApproval) CommonUtil.getObject(oid);
					List<WTDocument> aList = AsmSearchHelper.service.getObjectForAsmApproval(asm);
					for (WTDocument doc : aList) {
						DocumentDTO data = new DocumentDTO(doc);
						list.add(data);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONArray.fromObject(list);
	}

	public Map<String, Object> docTemplateList(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		List<DocumentTemplateData> docTemplateList = new ArrayList<DocumentTemplateData>();

		String number = (String) params.get("number");
		String name = (String) params.get("name");
		String dcoTemplateType = (String) params.get("dcoTemplateType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FormTemplate.class, true);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult qr = pager.find();

//		QueryResult result = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			FormTemplate template = (FormTemplate) obj[0];
			DocumentTemplateData data = new DocumentTemplateData(template);
			docTemplateList.add(data);
		}

		result.put("list", docTemplateList);
		result.put("topListCount", pager.getTotal());
		result.put("pageSize", pager.getPsize());
		result.put("total", pager.getTotalSize());
		result.put("sessionid", pager.getSessionId());
		result.put("curPage", pager.getCpage());
		return result;
	}

	public List<CommentsData> commentsList(String oid) throws Exception {
		List<CommentsData> comList = new ArrayList<CommentsData>();
		WTDocument doc = (WTDocument) CommonUtil.getObject(oid);
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(Comments.class, true);

		qs.appendWhere(new SearchCondition(Comments.class, "wtdocumentReference.key.id", "=",
				doc.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "cNum"), false), new int[] { idx });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "thePersistInfo.createStamp"), false),
				new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(qs);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommentsData data = new CommentsData((Comments) obj[0]);
			comList.add(data);
		}
		return comList;
	}

	public String getCnum(List<CommentsData> list) throws Exception {
		String p_num = "";
		if (list.size() > 0) {
			int c_num = 0;
			for (CommentsData c : list) {
				if (c_num < c.getCNum()) {
					c_num = c.getCNum();
				}
			}
			p_num = Integer.toString(c_num);
		}

		return p_num;
	}

	public int getCommentsChild(Comments com) throws Exception {
		WTDocument doc = com.getWtdocument();
		int count = 0;
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(Comments.class, true);
		qs.appendWhere(new SearchCondition(Comments.class, "wtdocumentReference.key.id", "=",
				doc.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "oPerson", "=", com.getOwner().getFullName()),
				new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "cNum", "=", com.getCNum()), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "cStep", ">", com.getCStep()), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "deleteYN", "=", "N"), new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(qs);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			count++;
		}
		return count;
	}

	/**
	 * 문서 링크 관계 확인 ( true : 연결, false : 미연결 )
	 */
	public boolean connect(WTDocument doc, Class<?> target) throws Exception {
		boolean isConnect = false;

		Class<?> clz = target.getClass();
		if (clz == DocumentEOLink.class) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "eo", DocumentECOLink.class);
			isConnect = qr.size() > 0;
		} else if (clz == DocumentECOLink.class) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "eco", DocumentECOLink.class);
			isConnect = qr.size() > 0;
		} else if (clz == DocumentECPRLink.class) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "ecpr", DocumentECPRLink.class);
			isConnect = qr.size() > 0;
		} else if (clz == DocumentCRLink.class) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "cr", DocumentCRLink.class);
			isConnect = qr.size() > 0;
		} else if (clz == WTPartDescribeLink.class) {
			QueryResult qr = PersistenceHelper.manager.navigate(doc, "describes", WTPartDescribeLink.class);
			isConnect = qr.size() > 0;
		}
		return isConnect;
	}

	/**
	 * 문서 관련 객체들 가져올 함수 AUI용
	 */
	public Map<String, ArrayList<Map<String, String>>> a(WTDocument doc) throws Exception {
		Map<String, ArrayList<Map<String, String>>> result = new HashMap<>();

		// 그리드 아이디에 맞게 배열 생성 한다.

		return result;
	}
}
