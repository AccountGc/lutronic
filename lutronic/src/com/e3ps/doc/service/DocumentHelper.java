package com.e3ps.doc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocLocation;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.org.People;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;

public class DocumentHelper {

	/**
	 * 문서 기본 위치
	 */
	public static final String DOCUMENT_ROOT = "/Default/문서";

	/**
	 * 신규 제작 사양서 위치
	 */
	public static final String SPEC_NEW_ROOT = "/Default/프로젝트/제작사양서";

	/**
	 * PDM업그레이드 제작사양서 위치
	 */
	public static final String SPEC_OLD_ROOT = "/Default/문서/프로젝트/제작사양서";

	public static final DocumentService service = ServiceFactory.getService(DocumentService.class);
	public static final DocumentHelper manager = new DocumentHelper();

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<DocumentData> list = new ArrayList<>();

		boolean latest = (boolean) params.get("latest");
		String oid = (String) params.get("oid");
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

		Folder folder = null;
//		if (!StringUtil.isNull(oid)) {
//			folder = (Folder) CommonUtil.getObject(oid);
//		} else {
//			folder = FolderTaskLogic.getFolder(DOCUMENT_ROOT, CommonUtil.getPDMLinkProductContainer());
//		}
//
//		if (folder != null) {
//			if (query.getConditionCount() > 0) {
//				query.appendAnd();
//			}
//			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
//			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
//			SearchCondition fsc = new SearchCondition(fca, "=",
//					new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
//			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
//			fsc.setOuterJoin(0);
//			query.appendWhere(fsc, new int[] { f_idx, idx });
//			query.appendAnd();
//			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
//			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
//					new int[] { f_idx });
//		}

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

//		PageQueryUtils pager = new PageQueryUtils(params, query);
//		PagingQueryResult result = pager.find();
		
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];
			DocumentData data = new DocumentData(document);
			list.add(data);
		}

		map.put("list", list);
//		map.put("sessionid", pager.getSessionId());
//		map.put("curPage", pager.getCpage());
		return map;
	}
	
	public List<Map<String,Object>> listMoldAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);
		QueryResult qr = PersistenceHelper.manager.find(query);
	
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		HashMap<String , String> verMap = new HashMap<String, String>();
		while(qr.hasMoreElements()){	
			Map<String, Object> result = new HashMap<String, Object>();
			Object[] o = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) o[0];
			DocumentData data = new DocumentData(doc);

//			int kk = 0 ;
//			if(verMap.containsKey(data.number)){
//				
//				kk = verMap.get(data.number).compareTo(data.version);
//				if(kk>0){
//					continue;
//				}else{
//					verMap.put(data.number, data.version);
//				}
//			}else{
//				verMap.put(data.number, data.version);
//			}
			
			
			result.put("number", data.getNumber());
			result.put("interNumber", data.getIBAValue(AttributeKey.IBAKey.IBA_INTERALNUMBER));
			result.put("model", data.getIBAValue(AttributeKey.IBAKey.IBA_MODEL));
			result.put("name", data.getName());
			result.put("oid", data.getOid());
			result.put("location", data.getLocation());
			result.put("version", data.getVersion());
			result.put("rev", data.getVersion()+"." + data.getIteration());
			result.put("state", data.getLifecycle());
			result.put("creator", data.getCreator());
			result.put("createDate", data.getCreateDate().substring(0, 10));
			result.put("modifyDate", data.getModifyDate().substring(0, 10));
			
			resultList.add(result);
		}
		
		return resultList;
		
	}
	
	public QuerySpec getListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	QuerySpec query = new QuerySpec();
    	int idx = query.addClassList(WTDocument.class, true);
    	ReferenceFactory rf = new ReferenceFactory();
    	
    	try {
    		
    		
    		String location 	= StringUtil.checkNull(request.getParameter("location"));
			String foid 		= StringUtil.checkNull(request.getParameter("fid"));
			if(foid.length() == 0) {
				foid = StringUtil.checkNull(request.getParameter("folder"));
			}
			if(location.length() ==0 ) {
				location = "/Default/Document";
			}
			//System.out.println("location =" + location);
			String islastversion 	= StringUtil.checkNull(request.getParameter("islastversion"));
			String docNumber 		= StringUtil.checkNull(request.getParameter("docNumber"));
			String docName 			= StringUtil.checkNull(request.getParameter("docName"));
			String predate 			= StringUtil.checkNull(request.getParameter("predate"));
			String postdate 		= StringUtil.checkNull(request.getParameter("postdate"));
			String predate_modify	= StringUtil.checkNull(request.getParameter("predate_modify"));
			String postdate_modify	= StringUtil.checkNull(request.getParameter("postdate_modify"));
			String creator 			= StringUtil.checkNull(request.getParameter("creator"));
			String state 			= StringUtil.checkNull(request.getParameter("state"));
			
			String documentType     = StringUtil.checkNull(request.getParameter("documentType"));
			String preseration      = StringUtil.checkNull(request.getParameter("preseration"));
			String model 			= StringUtil.checkNull(request.getParameter("model"));
			String interalnumber	= StringUtil.checkNull(request.getParameter("interalnumber"));
			String deptcode			= StringUtil.checkNull(request.getParameter("deptcode"));
			String writer			= StringUtil.checkNull(request.getParameter("writer"));
			
			String description		= StringUtil.checkNull(request.getParameter("description"));
			
			String sortValue 		= StringUtil.checkNull(request.getParameter("sortValue"));
			String sortCheck 		= StringUtil.checkNull(request.getParameter("sortCheck"));
			
			String searchType		= StringUtil.checkNull(request.getParameter("searchType"));
			
			String manufacture      = StringUtil.checkNull(request.getParameter("manufacture"));
			String moldtype         = StringUtil.checkNull(request.getParameter("moldtype"));
			String moldNumber 		= StringUtil.checkNull(request.getParameter("moldnumber"));
			String moldCost 		= StringUtil.checkNull(request.getParameter("moldcost"));
			
			
			
			// 최신 이터레이션
	    	if(query.getConditionCount() > 0) { query.appendAnd(); }
	    	query.appendWhere(VersionControlHelper.getSearchCondition(WTDocument.class, true), new int[]{idx});
	    	
	    	// 버전 검색
	    	if(!StringUtil.checkString(islastversion)) {
	    		islastversion = "true";
	    	}
	    	
	    	if("true".equals(islastversion)) {
				 SearchUtil.addLastVersionCondition(query, WTDocument.class, idx);
			}
			
	    	//Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(WTDocument.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false), new int[] { idx });

			
			// 일괄 결재시 타입에 따른 LC 상태 검색
			if(searchType.length() > 0){
				if(query.getConditionCount() > 0) {
					query.appendAnd();
				}
				if("document".equals(searchType) || "DOC".equals(searchType)) {
					query.appendOpenParen();
					query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.NOT_EQUAL, "$$MMDocument"), new int[] {idx});
					query.appendAnd();
					query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.NOT_EQUAL, "$$ROHS"), new int[] {idx});
					query.appendCloseParen();
				}else if("MOLD".equals(searchType)){
					query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, "$$MMDocument"), new int[] {idx});
				}else if("ROHS".equals(searchType)){
					query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, "$$ROHS"), new int[] {idx});
				}
			}
			
	    	//문서번호
	    	if(docNumber.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%" + docNumber + "%", false), new int[]{idx});
	    	}
	    	
	    	//문서명
	    	if(docName.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + docName + "%", false), new int[]{idx});
	    	}
	    	
	    	//등록일
	    	if(predate.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP ,SearchCondition.GREATER_THAN,DateUtil.convertStartDate(predate)), new int[]{idx});
	    	}
	    	
	    	if(postdate.length() > 0){
	    		if(query.getConditionCount() > 0)query.appendAnd();
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP,SearchCondition.LESS_THAN,DateUtil.convertEndDate(postdate)), new int[]{idx});
	    	}
	    	
	    	//수정일
	    	if(predate_modify.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.MODIFY_TIMESTAMP, SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[]{idx});
	    	}
	    	
	    	if(postdate_modify.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.MODIFY_TIMESTAMP, SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[]{idx});
	    	}
	    	
	    	//등록자
	    	if(creator.length() > 0){
	    		People people = (People)rf.getReference(creator).getObject();
	    		WTUser user = people.getUser();
	    		if(query.getConditionCount() > 0) { query.appendAnd(); } 
	    		query.appendWhere(new SearchCondition(WTDocument.class,"iterationInfo.creator.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[]{idx});
	    	}
	    	
	    	//상태
	    	if(state.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, state), new int[]{idx});
	    	}
	    	
	    	// 문서 종류
	    	if(documentType.length() > 0){
	    		if(query.getConditionCount() > 0) { query.appendAnd(); }
	    		query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, documentType), new int[] {idx});
	    	}
	    	
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
			
			// 등록자
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
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + writer + "%").toUpperCase(), false), new int[] { _idx });
				}
			} else {
				deptcode = "";
			}
			
			// manufacture
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + manufacture + "%").toUpperCase(), false), new int[] { _idx });
				}
			} else {
				manufacture = "";
			}
			
			// 금형타입
			if (moldtype.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MOLDTYPE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + moldtype + "%").toUpperCase(), false), new int[] { _idx });
				}
			} else {
				moldtype = "";
			}
			
			// 금형번호
			if (moldNumber.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MOLDNUMBER);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + moldNumber + "%").toUpperCase(), false), new int[] { _idx });
				}
			} else {
				moldNumber = "";
			}
						
			// 금형번호
			if (moldCost.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MOLDCOST);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + moldCost + "%").toUpperCase(), false), new int[] { _idx });
				}
			} else {
				moldCost = "";
			}
			
			// 설명
			if(description.length() > 0) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DESCRIPTION, SearchCondition.LIKE, "%"+description+"%", false), new int[] {idx} );
			}
			
	    	//folder search
	    	if (location.length() > 0) {
				int l = location.indexOf(ROOTLOCATION);
				if (l >= 0) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					location = location.substring((l + ROOTLOCATION.length()));
					// Folder Search
					int folder_idx = query.addClassList(DocLocation.class, false);
					query.appendWhere(new SearchCondition(DocLocation.class, DocLocation.DOC, WTDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { folder_idx, idx });
					query.appendAnd();

					query.appendWhere(new SearchCondition(DocLocation.class, "loc", SearchCondition.LIKE, location + "%"), new int[] { folder_idx });
				}
			}
	    	
	    	
	    	
	    	//소팅
	    	if (sortCheck == null) sortCheck = "true";
	    	boolean sort = "true".equals(sortCheck);
			if(sortValue != null && sortValue.length() > 0) {
				
				if( !"creator".equals(sortValue)){
					//query.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,sortValue), true), new int[] { idx });
					SearchUtil.setOrderBy(query, WTDocument.class, idx, sortValue, "sort", sort);
				}else{
					if(query.getConditionCount() > 0) query.appendAnd();
					int idx_user = query.appendClassList(WTUser.class, false);
					int idx_people = query.appendClassList(People.class, false);
					
					ClassAttribute ca = new ClassAttribute(WTDocument.class, "creator.key.id");
		            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
		            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
					query.appendAnd();
					query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
					SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , sort);
				}
				
				/*
				if("true".equals(sortCheck)){
					if( !"creator".equals(sortValue)){
						//query.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,sortValue), true), new int[] { idx });
						SearchUtil.setOrderBy(query, WTDocument.class, idx, sortValue, "sort", true);
					}else{
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
						ClassAttribute ca = new ClassAttribute(WTDocument.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , true);
					}
				}else{
					if( !"creator".equals(sortValue)){
						//query.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,sortValue), false), new int[] { idx });
						SearchUtil.setOrderBy(query, WTDocument.class, idx, sortValue, "sort", false);
					}else{
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
						ClassAttribute ca = new ClassAttribute(WTDocument.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
				 */
			}else{
				//query.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,"thePersistInfo.createStamp"), true), new int[] { idx }); 
				SearchUtil.setOrderBy(query, WTDocument.class, idx, WTDocument.MODIFY_TIMESTAMP, "sort", true);
			}
	    }catch (Exception e) {
			e.printStackTrace();
		}
    	//System.out.println(query.toString());
		return query;
    }

}
