package com.e3ps.change.service;

import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.beans.EOData;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.org.People;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SearchCondition;
import wt.services.StandardManager;


@SuppressWarnings("serial")
public class StandardECNSearchService extends StandardManager implements ECNSearchService{
	
	public static StandardECNSearchService newStandardECNSearchService() throws Exception {
		final StandardECNSearchService instance = new StandardECNSearchService();
		instance.initialize();
		return instance;
	}

	/**
	 * ECO 관련 ECN Search
	 * @param eco
	 * @return
	 */
	@Override
	public EChangeNotice getECN(EChangeOrder eco){
		EChangeNotice ecn= null;
		try{
			
			QuerySpec qs = new QuerySpec(EChangeNotice.class);
			
			qs.appendWhere(new SearchCondition
					(EChangeNotice.class,"ecoReference.key.id",SearchCondition.EQUAL,CommonUtil.getOIDLongValue(eco)), new int[] {0});
			
			QueryResult rt = PersistenceHelper.manager.find(qs);
			while(rt.hasMoreElements()){
				return  ecn =(EChangeNotice)rt.nextElement(); 
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return ecn;
	}
	
	@Override
	public Map<String, Object> listECNAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getECNQuery(request);
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
			EChangeNotice ecr = (EChangeNotice) o[0];
			EOData eoData = new EOData(ecr);
			
			xmlBuf.append("<row id='"+ eoData.oid +"'>");
			if("true".equals(select)) {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + eoData.number + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + eoData.oid + "')>" + eoData.name + "</a>]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.getLifecycle() + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.creator + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.getCreateDate() + "]]></cell>" );
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
	public QuerySpec getECNQuery(HttpServletRequest req) throws Exception{
		
		QuerySpec qs = null; 
		try{
			
			String name = StringUtil.checkNull((String)req.getParameter("name"));
			String number = StringUtil.checkNull((String)req.getParameter("number"));
			String predate_applyDate = StringUtil.checkNull((String)req.getParameter("predate_applyDate"));
			String postdate_applyDate = StringUtil.checkNull((String)req.getParameter("postdate_applyDate"));
			String predate = StringUtil.checkNull((String)req.getParameter("predate"));
			String postdate = StringUtil.checkNull((String)req.getParameter("postdate"));
			String worker = StringUtil.checkNull((String)req.getParameter("creator"));
			String state = StringUtil.checkNull((String)req.getParameter("state"));
			String sortCheck =StringUtil.checkNull((String)req.getParameter("sortCheck"));
			String sortValue =StringUtil.checkNull((String)req.getParameter("sortValue"));
			
			
			qs = new QuerySpec(); 
			Class ecnClass = EChangeNotice.class;
			int ecoIdx = qs.appendClassList(ecnClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecnClass, EChangeNotice.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecnClass, EChangeNotice.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			//발행일
			if(predate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecnClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
			}
			//발생일
			if(postdate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecnClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
			}
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecnClass, EChangeNotice.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}//creator.key.id
			
			//등록자
			if(worker.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(worker);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(ecnClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			
			if(sortValue.length() > 0) {
				
				if("true".equals(sortCheck)){
					
					
					if( !"creator.key.id".equals(sortValue)){
						qs.appendOrderBy(new OrderBy(new ClassAttribute(ecnClass,sortValue), true), new int[] { ecoIdx });
					}else{
						
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(ecnClass, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					
					if( !"creator.key.id".equals(sortValue)){
						
						qs.appendOrderBy(new OrderBy(new ClassAttribute(ecnClass,sortValue), false), new int[] { ecoIdx });
					}else{
						
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(ecnClass, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecnClass, "thePersistInfo.createStamp"), true), new int[] { ecoIdx }); 
			}
			 
			//System.out.println(qs);
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return qs;
	
	}
}