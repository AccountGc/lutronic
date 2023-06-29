package com.e3ps.groupware.workprocess.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.doc.WTDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;



import wt.util.WTException;

import com.e3ps.change.beans.ECRData;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.ObjectComarator;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.common.workflow.E3PSWorkflowHelper;
import com.e3ps.groupware.workprocess.AppPerLink;
import com.e3ps.groupware.workprocess.AsmApproval;
import com.e3ps.groupware.workprocess.beans.AsmData;
import com.e3ps.org.People;

@SuppressWarnings("serial")
public class StandardAsmSearchService extends StandardManager implements AsmSearchService {

	public static StandardAsmSearchService newStandardAsmSearchService() throws Exception {
		final StandardAsmSearchService instance = new StandardAsmSearchService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public Map<String,Object> listAsmAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getAsmQuery(request);
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		}
		
		PageControl control = new PageControl(qr, page, formPage, rows);
	    int totalPage   = control.getTotalPage();
	    int startPage   = control.getStartPage();
	    int endPage     = control.getEndPage();
	    int listCount   = control.getTopListCount();
	    int totalCount  = control.getTotalCount();
	    int currentPage = control.getCurrentPage();
	    String param    = control.getParam();
	    int rowCount    = control.getTopListCount();
		
	    StringBuffer xmlBuf = new StringBuffer();
	    xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	    xmlBuf.append("<rows>");
		
		String select = StringUtil.checkReplaceStr(request.getParameter("select"), "false");
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			AsmApproval ecr = (AsmApproval) o[0];
			AsmData asmData = new AsmData(ecr);
			
			xmlBuf.append("<row id='"+ asmData.oid +"'>");
			if("true".equals(select)) {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + asmData.number + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + asmData.oid + "')>" + asmData.name + "</a>]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + asmData.getApprovalType() + "]]></cell>" );
			
			xmlBuf.append("<cell><![CDATA[" + asmData.getLifecycle() + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + asmData.creator + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + asmData.dateSubString(true) + "]]></cell>" );
			
			
			xmlBuf.append("</row>" );
		}
		xmlBuf.append("</rows>");
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		result.put("formPage"       , formPage);
		result.put("rows"           , rows);
		result.put("totalPage"      , totalPage);
		result.put("startPage"      , startPage);
		result.put("endPage"        , endPage);
		result.put("listCount"      , listCount);
		result.put("totalCount"     , totalCount);
		result.put("currentPage"    , currentPage);
		result.put("param"          , param);
		result.put("sessionId"      , qr.getSessionId()==0 ? "" : qr.getSessionId());
		result.put("xmlString"      , xmlBuf);
		
		return result;	
		
	}
	
	@Override
	public QuerySpec getAsmQuery(HttpServletRequest req) throws Exception{
		
		QuerySpec qs = null; 
		
		try{
			String name = StringUtil.checkNull(req.getParameter("name"));
			String number = StringUtil.checkNull(req.getParameter("number"));
			
			String predate = StringUtil.checkNull(req.getParameter("predate"));
			String postdate = StringUtil.checkNull(req.getParameter("postdate"));
			String creator = StringUtil.checkNull(req.getParameter("creator"));
			String state = StringUtil.checkNull(req.getParameter("state"));
			
			//정렬
			String sortValue = StringUtil.checkNull(req.getParameter("sortValue"));
			String sortCheck = StringUtil.checkNull(req.getParameter("sortCheck"));
			
			/*
			if("1".equals(sortValue) ) {
				sortValue = "master>number";
			} else if("2".equals(sortValue)) {
				sortValue = "master>number";
			}
			*/
			qs = new QuerySpec();
			Class asmClass =  AsmApproval.class;
			int ecoIdx =  qs.appendClassList(asmClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(asmClass, AsmApproval.NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(asmClass, AsmApproval.NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			//등록일
			if(predate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(asmClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
			}
			
			//등록일
			if(postdate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(asmClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
			}
			
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(asmClass, AsmApproval.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}
			
			//등록자//creator.key.id
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(asmClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			
			
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute( AsmApproval.class,sortValue), true), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute( AsmApproval.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute( AsmApproval.class,sortValue), false), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute( AsmApproval.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(asmClass, "thePersistInfo.createStamp"), true), new int[] { ecoIdx }); 
			}
			//System.out.println(qs.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return qs;
	}
	
	@Override
	public List<WTDocument> getObjectForAsmApproval(AsmApproval asm) {
		List<WTDocument> list = new ArrayList<WTDocument>();
		
		try {
			
			List<AppPerLink> linklist = getLinkForAsmApproval(asm);
			for(AppPerLink link : linklist){
				WTDocument doc = (WTDocument)link.getRoleAObject();  
				String vr = CommonUtil.getVROID(doc);
				doc = (WTDocument)CommonUtil.getObject(vr);
				list.add(doc);
			}
			Collections.sort(list,new ObjectComarator());
			/*
			QueryResult result = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class,false);
			
			while(result.hasMoreElements()) {
				AppPerLink link = (AppPerLink)result.nextElement();
				
				list.add((LifeCycleManaged)link.getRoleAObject());
			}
			/*
			System.out.println(CommonUtil.getOIDLongValue(asm));
			QuerySpec query = new QuerySpec();
			
			int idx_wt = query.addClassList(WTDocument.class, true);
			int idx = query.addClassList(AppPerLink.class, false);
			
			ClassAttribute att1 = new ClassAttribute(WTDocument.class, "thePersistInfo.theObjectIdentifier.id");
			ClassAttribute att2 = new ClassAttribute(AppPerLink.class, "roleAObjectRef.key.id");
			
			query.appendWhere(new SearchCondition(att1, SearchCondition.EQUAL, att2),new int[] {idx_wt, idx});
			
			query.appendAnd();
			
			query.appendWhere(new SearchCondition(AppPerLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(asm)), new int[] {idx});
			
			System.out.println(query);
			
			QueryResult result = PersistenceHelper.manager.find(query);
			
			while(result.hasMoreElements()) {
				Object[] o = (Object[])result.nextElement();
				WTDocument doc = (WTDocument)o[0];
				list.add(doc);
			}
			*/
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	@Override
	public List<AppPerLink> getLinkForAsmApproval(AsmApproval asm) throws WTException {
		
		List<AppPerLink> list =new ArrayList<AppPerLink>();
		QueryResult result = PersistenceHelper.manager.navigate(asm, "persistable", AppPerLink.class,false);
		
		while(result.hasMoreElements()) {
			AppPerLink link = (AppPerLink)result.nextElement();
			
			list.add(link);
		}
		return list;
		 
	}
	
	/**
	 * 일괄 결재시 일괄 결재 Return
	 */
	@Override
	public  AsmApproval getAsmApproval(WTDocument doc){
		AsmApproval asm = null;
		try{
			
			if(!CommonUtil.isBatch(doc)){
				return asm;
			}
			
			ClassAttribute classattribute1 = null;
	        ClassAttribute classattribute2 = null;
	        ClassAttribute classattribute3 = null;
			SearchCondition sc = null;
			long vrOid = doc.getBranchIdentifier();
			QuerySpec qs = new QuerySpec();
			Class cls1 = WTDocument.class;
			Class cls2 = AppPerLink.class;
			Class cls3 = AsmApproval.class;
			int idx1 = qs.addClassList(cls1, false);
			int idx2 = qs.addClassList(cls2, false);
			int idx3 = qs.addClassList(cls3, true);
			
			classattribute1 = new ClassAttribute(cls1 ,"thePersistInfo.theObjectIdentifier.id" );
		    classattribute2 = new ClassAttribute(cls2 ,"roleAObjectRef.key.id");
			sc = new SearchCondition(classattribute1, "=", classattribute2);
			sc.setFromIndicies(new int[] {idx1, idx2}, 0);
	        sc.setOuterJoin(0);
	        qs.appendWhere(sc, new int[] {idx1, idx2});
	      
	        qs.appendAnd();
	        classattribute2= new ClassAttribute(cls2 , "roleBObjectRef.key.id" );
		    classattribute3= new ClassAttribute(cls3 , "thePersistInfo.theObjectIdentifier.id");
			sc = new SearchCondition(classattribute2, "=", classattribute3);
			sc.setFromIndicies(new int[] {idx2, idx3}, 0);
	        sc.setOuterJoin(0);
	        qs.appendWhere(sc, new int[] {idx2, idx3});
	        
	        qs.appendAnd();
	        qs.appendWhere(new SearchCondition(cls1,"iterationInfo.branchId",SearchCondition.EQUAL,vrOid),new int[] {idx1});
	        
	        //System.out.println(qs);
	        QueryResult rt = PersistenceHelper.manager.find(qs);
	        
	        while(rt.hasMoreElements()){
	        	Object[] ob=(Object[])rt.nextElement();
	        	asm = (AsmApproval)ob[0];
	        }
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return asm;
	}
	
}
