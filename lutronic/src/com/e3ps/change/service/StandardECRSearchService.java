package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.beans.EOData;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.org.People;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;

@SuppressWarnings("serial")
public class StandardECRSearchService extends StandardManager implements ECRSearchService {

	public static StandardECRSearchService newStandardECRSearchService() throws Exception {
		final StandardECRSearchService instance = new StandardECRSearchService();
		instance.initialize();
		return instance;
	}
	
	
	@Override
	public Map<String,Object> listECRAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getECRQuery(request);
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
			EChangeRequest ecr = (EChangeRequest) o[0];
			ECRData ecrData = new ECRData(ecr);
			
			xmlBuf.append("<row id='"+ ecrData.oid +"'>");
			if("true".equals(select)) {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + ecrData.number + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + ecrData.oid + "')>" + ecrData.name + "</a>]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecrData.getChangeDisplay() + "]]></cell>" );
			
			xmlBuf.append("<cell><![CDATA[" + ecrData.writer + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecrData.createDepart + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecrData.writeDate + "]]></cell>" );
			
			xmlBuf.append("<cell><![CDATA[" + ecrData.getLifecycle() + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecrData.creator + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecrData.dateSubString(true) + "]]></cell>" );
			
			
			if("true".equals(select)) {
				xmlBuf.append("<cell><![CDATA[" + ecrData.name  + "]]></cell>" );
			}
			
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
	public QuerySpec getECRQuery(HttpServletRequest req) throws Exception{
		
		QuerySpec qs = null; 
		
		try{
			String name = StringUtil.checkNull(req.getParameter("name"));
			String number = StringUtil.checkNull(req.getParameter("number"));
			
			String predate = StringUtil.checkNull(req.getParameter("predate"));
			String postdate = StringUtil.checkNull(req.getParameter("postdate"));
			String creator = StringUtil.checkNull(req.getParameter("creator"));
			String state = StringUtil.checkNull(req.getParameter("state"));
			
			//ECR 속성
			String preCreateDate = StringUtil.checkNull(req.getParameter("preCreateDate"));
			String postCreateDate = StringUtil.checkNull(req.getParameter("postCreateDate"));
			String preApproveDate = StringUtil.checkNull(req.getParameter("preApproveDate"));
			String postApproveDate = StringUtil.checkNull(req.getParameter("postApproveDate"));
			
			String createDepart = StringUtil.checkNull(req.getParameter("createDepart"));
			String writer = StringUtil.checkNull(req.getParameter("writer"));
			
			String proposer = StringUtil.checkNull(req.getParameter("proposer"));
			
			String[] models = req.getParameterValues("model");
			String[] changeSections = req.getParameterValues("changeSection");
			
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
			Class ecrClass = EChangeRequest.class;
			int ecoIdx = qs.appendClassList(ecrClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			//등록일
			if(predate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
			}
			
			//등록일
			if(postdate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
			}
			
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}
			
			//등록자//creator.key.id
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(ecrClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			//작성일
			if(preCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CREATE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL , preCreateDate), new int[] {ecoIdx});
			}
			
		
			if(postCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CREATE_DATE, SearchCondition.LESS_THAN_OR_EQUAL , postCreateDate), new int[] {ecoIdx});
			}
			
			//승인일
			if(preApproveDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.APPROVE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL , preApproveDate), new int[] {ecoIdx});
			}
			
		
			if(postApproveDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.APPROVE_DATE, SearchCondition.LESS_THAN_OR_EQUAL , postApproveDate), new int[] {ecoIdx});
			}
			
			//작성부서
			if(createDepart.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CREATE_DEPART, SearchCondition.LIKE , "%"+createDepart+"%", false), new int[] {ecoIdx});
			}
			
			//작성자
			if(writer.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.WRITER, SearchCondition.LIKE , "%"+writer+"%", false), new int[] {ecoIdx});
			}
			
			//제안자
			if(proposer.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.PROPOSER, SearchCondition.LIKE , "%"+proposer+"%", false), new int[] {ecoIdx});
			}
			
			
			//제품명
			if(models != null){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
					for(int i = 0 ;i < models.length ;i++){
						qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.MODEL, SearchCondition.LIKE, "%"+models[i]+"%", false), new int[] {ecoIdx});
						if(i== models.length-1) break;
						qs.appendOr();
						
					}
				qs.appendCloseParen();
			}
			
			//변경구분
			if(changeSections != null){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
					for(int i = 0 ;i < changeSections.length ;i++){
						qs.appendWhere(new SearchCondition(ecrClass, EChangeRequest.CHANGE_SECTION, SearchCondition.LIKE, "%"+changeSections[i]+"%", false), new int[] {ecoIdx});
						if(i== changeSections.length-1) break;
						qs.appendOr();
						
					}
				qs.appendCloseParen();
			}
			
			
			
			
			
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeRequest.class,sortValue), true), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeRequest.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					if( !"creator".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeRequest.class,sortValue), false), new int[] { ecoIdx });
					}else{
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeRequest.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecrClass, "thePersistInfo.modifyStamp"), true), new int[] { ecoIdx }); 
			}
			//System.out.println(qs.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return qs;
	}
	
	/**
	 * 관련 ECR
	 */
	@Override
	public List<EChangeRequest> getRequestOrderLinkECR(EChangeOrder eco) throws Exception {
		
		List<RequestOrderLink> list = ECOSearchHelper.service.getRequestOrderLink(eco);
		List<EChangeRequest> ecoList = new ArrayList<EChangeRequest>();
		for(RequestOrderLink link : list){
			EChangeRequest ecr = (EChangeRequest)link.getRoleBObject();
			ecoList.add(ecr);
		}
		
		return ecoList;
		
	}
	
	/**
	 * 관련  ECO
	 */
	@Override
	public List<ECRData> getRequestOrderLinkECRData(EChangeOrder eco) throws Exception {
		List<ECRData> dataList = new ArrayList<ECRData>();
		
		if(eco != null){
			List<EChangeRequest> ecrList = getRequestOrderLinkECR(eco);
			
			for(EChangeRequest ecr : ecrList){
				ECRData data = new ECRData(ecr);
				dataList.add(data);
			}
		}
		
		
		return dataList;
		
	}
	
	
	
}
