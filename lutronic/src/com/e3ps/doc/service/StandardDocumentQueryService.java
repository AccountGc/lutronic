package com.e3ps.doc.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
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
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.StringSearch;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.development.devOutPutLink;
import com.e3ps.doc.DocLocation;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.org.People;

@SuppressWarnings("serial")
public class StandardDocumentQueryService extends StandardManager implements DocumentQueryService {

	public static final String ROOTLOCATION = "/Default/Document";
	
	public static StandardDocumentQueryService newStandardDocumentQueryService() throws WTException {
		final StandardDocumentQueryService instance = new StandardDocumentQueryService();
		instance.initialize();
		return instance;
	}
	
	@Override
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
	
	@Override
	public QuerySpec devActiveLinkDocument(String activeOid, String docOid) {
		QuerySpec spec = null;
		
		try {
			spec = new QuerySpec();
			
			int idx = spec.appendClassList(devOutPutLink.class, true);
			
			spec.appendWhere(new SearchCondition(devOutPutLink.class, devOutPutLink.ROLE_AOBJECT_REF + ".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(activeOid)), new int[] {idx});
			
			if(spec.getConditionCount() > 0) {
				spec.appendAnd();
			}
			spec.appendWhere(new SearchCondition(devOutPutLink.class, devOutPutLink.ROLE_BOBJECT_REF + ".key.branchId", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(CommonUtil.getVROID(docOid))), new int[] {idx});
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return spec;
	}
	
	@Override
	public QuerySpec listCreateDocumentLinkAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		
		//String foid = StringUtil.checkNull( request.getParameter("folder"));
		String number = request.getParameter("number");
		String name = request.getParameter("name");
		String islastversion = (String) request.getParameter("islastversion");
		
		String location 	= StringUtil.checkNull((String) request.getParameter("location"));
		
		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
		
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTDocument.class, true);

		//최신 이터레이션
		query.appendWhere(VersionControlHelper.getSearchCondition(WTDocument.class, true), new int[] { idx });

		if (number != null && number.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			
			StringSearch stringsearch = new StringSearch("number");
			stringsearch.setValue("%" + number.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(WTDocument.class),new int[] { idx });
		}else{
			number = "";
		}
		
		if (name != null && name.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			StringSearch stringsearch = new StringSearch("name");
			stringsearch.setValue("%" + name.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(WTDocument.class),new int[] { idx });
		} else {
			name = "";
		}

		//folder search
		if (folder != null) {
			if (query.getConditionCount() > 0)
				query.appendAnd();

			int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
			SearchCondition sc1 = new SearchCondition(new ClassAttribute(IteratedFolderMemberLink.class,"roleBObjectRef.key.branchId"), "=",new ClassAttribute(WTDocument.class,"iterationInfo.branchId"));
			sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
			sc1.setOuterJoin(0);
			query.appendWhere(sc1, new int[] { folder_idx, idx });

			query.appendAnd();
			ArrayList folders = CommonFolderHelper.service.getFolderTree(folder);
			
			query.appendOpenParen();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class,
			"roleAObjectRef.key.id", SearchCondition.EQUAL,	folder.getPersistInfo().getObjectIdentifier()
			.getId()), new int[] { folder_idx });

			for (int fi = 0; fi < folders.size(); fi++) {
				String[] s = (String[]) folders.get(fi);
				Folder sf = (Folder) rf.getReference(s[2]).getObject();

				query.appendOr();
				query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()), new int[] { folder_idx });
			}
			query.appendCloseParen();
		}

		if (islastversion==null || islastversion.length()==0 || "true".equals(islastversion)) {
			SearchUtil.addLastVersionCondition(query, WTDocument.class, idx);
		}

		ClassAttribute classattribute = new ClassAttribute(WTDocument.class, "thePersistInfo.createStamp");
		classattribute.setColumnAlias("sort0");
		int[] fieldNoArr = { idx };
		query.appendSelect(classattribute, fieldNoArr, false);
		OrderBy orderby = new OrderBy(classattribute, true);
		query.appendOrderBy(orderby, fieldNoArr);
		
		return query;
	}
	
	
	@Override
	public List<DocumentToDocumentLink> getDocumentToDocumentLinks(String documentOid, String roleName) throws Exception {
		WTDocument document = (WTDocument)CommonUtil.getObject(documentOid);
		return getDocumentToDocumentLinks(document, roleName);
	}
	
	@Override
	public List<DocumentToDocumentLink> getDocumentToDocumentLinks(WTDocument document, String roleName) throws Exception {
		List<DocumentToDocumentLink> list = new ArrayList<DocumentToDocumentLink>();
		
		String vrOid = CommonUtil.getVROID(document);
		document = (WTDocument)CommonUtil.getObject(vrOid);
		QueryResult qr = PersistenceHelper.manager.navigate(document, roleName, DocumentToDocumentLink.class, false);
		while(qr.hasMoreElements()){ 
			DocumentToDocumentLink link = (DocumentToDocumentLink)qr.nextElement();
			list.add(link);
    	}
		return list;
	}
	
	@Override
	public List<DocumentData> getDocumentListToLinkRoleName(String documentOid, String roleName) throws Exception {
		WTDocument document = (WTDocument)CommonUtil.getObject(documentOid);
		return getDocumentListToLinkRoleName(document, roleName);
	}
	
	@Override
	public List<DocumentData> getDocumentListToLinkRoleName(WTDocument document, String roleName) throws Exception {
		List<DocumentData> list = new ArrayList<DocumentData>();
		
		List<DocumentToDocumentLink> linkList = getDocumentToDocumentLinks(document, roleName);
		for(DocumentToDocumentLink link : linkList) {
			WTDocument doc = null;
			if("used".equals(roleName)){
				doc = link.getUsed();
			}else if("useBy".equals(roleName)){
				doc = link.getUseBy();
			}
			
			DocumentData data = new DocumentData(doc);
			list.add(data);
		}
		return list;
	}
	
}
