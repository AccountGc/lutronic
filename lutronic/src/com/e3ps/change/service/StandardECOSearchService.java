package com.e3ps.change.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.ManagedBaseline;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.beans.ECAData;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.EOData;
import com.e3ps.change.beans.EoComparator;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.doc.DocumentToDocumentLink;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.groupware.service.GroupwareHelper;
import com.e3ps.org.People;
import com.e3ps.part.beans.ObjectComarator;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartQueryHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.service.VersionHelper;

@SuppressWarnings("serial")
public class StandardECOSearchService extends StandardManager implements ECOSearchService {

	public static StandardECOSearchService newStandardECOSearchService() throws Exception {
		final StandardECOSearchService instance = new StandardECOSearchService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public Map<String, Object> listECOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		ArrayList<ECOBeans> list = new ArrayList<ECOBeans>();
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getECOQuery(request);
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
			//System.out.println("query="+query);
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
		//System.out.println("qr.size() = " + qr.size());
		
		/*
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) o[0];
			String ecoOid = CommonUtil.getOIDString(eco);
			System.out.println("ecoOid="+ecoOid);
			ECOData ecoData = new ECOData(eco);
			List<Map<String,Object>> appList = null;
			String approverDate = "";
			try {
				appList = GroupwareHelper.service.getApprovalList(ecoOid);
				for (int i = 0; i < appList.size(); i++) {
					Map<String,Object> map = appList.get(i);
					String tmp = StringUtil.checkNull(String.valueOf(map.get("approveDate")));
					if(tmp.length()>0){
						approverDate = tmp;
						approverDate = DateUtil.subString(approverDate, 0, 10);
					}
				}
			} catch(Exception e) {
				appList = new ArrayList<Map<String,Object>>();
			}
			
			//System.out.println("qr.size() = " + ecoData.number );
			
			ECOBeans eBeans = new ECOBeans();
			eBeans.setEcoNumber(ecoData.number);
			eBeans.setEcoName(ecoData.name);
			eBeans.setEcooid(ecoData.oid);
			eBeans.setLicensingDisplay(ecoData.getlicensingDisplay());
			eBeans.setLeifecycle(ecoData.getLifecycle());
			eBeans.setCreator(ecoData.creator);
			eBeans.setApproverDate(approverDate);
			eBeans.setCreateDate(ecoData.dateSubString(true));
			list.add(eBeans);
		}
		*/
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) o[0];
			String ecoOid = CommonUtil.getOIDString(eco);
			//System.out.println("ecoOid="+ecoOid);
			ECOData ecoData = new ECOData(eco);
			/*
			List<Map<String,Object>> appList = null;
			String approverDate = "";
			try {
				appList = GroupwareHelper.service.getApprovalList(ecoOid);
				for (int i = 0; i < appList.size(); i++) {
					Map<String,Object> map = appList.get(i);
					String tmp = StringUtil.checkNull(String.valueOf(map.get("approveDate")));
					if(tmp.length()>0){
						approverDate = tmp;
						approverDate = DateUtil.subString(approverDate, 0, 10);
					}
				}
			} catch(Exception e) {
				appList = new ArrayList<Map<String,Object>>();
			}
			*/
			xmlBuf.append("<row id='"+ ecoData.oid +"'>");
			if("true".equals(select)) {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
			
			//System.out.println("qr.size() = " + ecoData.number );
			
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + ecoData.number + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + ecoData.oid + "')>" + ecoData.name + "</a>]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecoData.getlicensingDisplay(true) + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecoData.riskTypeName + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecoData.getLifecycle() + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecoData.creator + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecoData.eoApproveDate+ "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + ecoData.dateSubString(true)+ "]]></cell>" );
			xmlBuf.append("</row>" );
		}
		
		xmlBuf.append("</rows>" );
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
	public List<Map<String,Object>> listAUIECOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		QuerySpec query = getECOQuery(request);
		QueryResult qr = PersistenceHelper.manager.find(query);
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) o[0];
			String ecoOid = CommonUtil.getOIDString(eco);
			
			ECOData ecoData = new ECOData(eco);
			Map<String,Object> result = new HashMap<String, Object>();
			result.put("oid", ecoData.oid);
			result.put("number", ecoData.number);
			result.put("name", ecoData.name);
			result.put("state", ecoData.getLifecycle());
			
			result.put("creator", ecoData.creator);
			result.put("eoApproveDate", ecoData.eoApproveDate);
			
			result.put("licensingDisplay", ecoData.getlicensingDisplay(false));
			result.put("riskTypeName", ChangeUtil.getRiskTypeName(ecoData.riskType, false));
			result.put("createDate", ecoData.dateSubString(true));
			resultList.add(result);
		}
		
		
		
		return resultList;
	}
	
	@Override
	public Map<String,Object> listPagingAUIECOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		
		int page = StringUtil.getIntParameter((String) request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter((String) request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) request.getParameter("formPage"), 15);
		
		//QuerySpec query = getECOQuery(request);
		
		String sessionId = (String) request.getParameter("sessionId");

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {

			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = getECOQuery(request);
			
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);

		}
		
		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();
		long sessionIdLong = control.getSessionId();
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) o[0];
			String ecoOid = CommonUtil.getOIDString(eco);
			
			ECOData ecoData = new ECOData(eco);
			Map<String,Object> result = new HashMap<String, Object>();
			result.put("oid", ecoData.oid);
			result.put("number", ecoData.number);
			result.put("name", ecoData.name);
			result.put("state", ecoData.getLifecycle());
			
			result.put("creator", ecoData.creator);
			result.put("eoApproveDate", ecoData.eoApproveDate);
			
			result.put("licensingDisplay", ecoData.getlicensingDisplay(false));
			result.put("riskTypeName", ChangeUtil.getRiskTypeName(ecoData.riskType, false));
			result.put("createDate", ecoData.dateSubString(true));
			resultList.add(result);
		}
		
		map.put("list", resultList);
		map.put("totalPage", totalPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("listCount", listCount);
		map.put("totalCount", totalCount);
		map.put("currentPage", currentPage);
		map.put("param", param);
		map.put("rowCount", rowCount);
		map.put("sessionId", sessionIdLong);
		
		return map;
	}
	
	@Override
	public Map<String, Object> listEOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getECOQuery(request);
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
			EChangeOrder eco = (EChangeOrder) o[0];
			EOData eoData = new EOData(eco);
			
			xmlBuf.append("<row id='"+ eoData.oid +"'>");
			if("true".equals(select)) {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + eoData.number + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + eoData.oid + "')>" + eoData.name + "</a>]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.getEoTypeDisplay() + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.getLifecycle() + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.creator + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.eoApproveDate+ "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + eoData.dateSubString(true)+ "]]></cell>" );
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
	public List<Map<String,Object>> listAUIEOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		QuerySpec query = getECOQuery(request);
		QueryResult qr = PersistenceHelper.manager.find(query);
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) o[0];
			EOData eoData = new EOData(eco);
			Map<String, Object> result = new HashMap<String, Object>();
			
			//System.out.println("eoData.number =" + eoData.number);
			result.put("oid", eoData.oid);
			result.put("number", eoData.number);
			result.put("name", eoData.name);
			result.put("eoTypeDisplay", eoData.getEoTypeDisplay());
			result.put("state", eoData.getLifecycle());
			result.put("creator", eoData.creator);
			result.put("eoApproveDate", eoData.eoApproveDate);
			resultList.add(result);
		}
		
		//System.out.println("resultList.size() =" + resultList.size());
		
		return resultList;
	}
	
	@Override
	public Map<String, Object> listPagingAUIEOAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		
		int page = StringUtil.getIntParameter((String) request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter((String) request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter((String) request.getParameter("formPage"), 15);
		
		String sessionId = (String) request.getParameter("sessionId");

		PagingQueryResult qr = null;

		if (StringUtil.checkString(sessionId)) {

			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		} else {
			QuerySpec query = getECOQuery(request);
			
			qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);

		}
		
		PageControl control = new PageControl(qr, page, formPage, rows);
		int totalPage = control.getTotalPage();
		int startPage = control.getStartPage();
		int endPage = control.getEndPage();
		int listCount = control.getTopListCount();
		int totalCount = control.getTotalCount();
		int currentPage = control.getCurrentPage();
		String param = control.getParam();
		int rowCount = control.getTopListCount();
		long sessionIdLong = control.getSessionId();
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) o[0];
			EOData eoData = new EOData(eco);
			Map<String, Object> result = new HashMap<String, Object>();
			
			//System.out.println("eoData.number =" + eoData.number);
			result.put("oid", eoData.oid);
			result.put("number", eoData.number);
			result.put("name", eoData.name);
			result.put("eoTypeDisplay", eoData.getEoTypeDisplay());
			result.put("state", eoData.getLifecycle());
			result.put("creator", eoData.creator);
			result.put("eoApproveDate", eoData.eoApproveDate);
			resultList.add(result);
		}
		
		map.put("list", resultList);
		map.put("totalPage", totalPage);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		map.put("listCount", listCount);
		map.put("totalCount", totalCount);
		map.put("currentPage", currentPage);
		map.put("param", param);
		map.put("rowCount", rowCount);
		map.put("sessionId", sessionIdLong);
		
		return map;
	}
	
	@Override
	public QuerySpec getECOQuery(HttpServletRequest req) throws Exception{
		QuerySpec qs = null; 
		try{
			String name = StringUtil.checkNull(req.getParameter("name"));
			String number = StringUtil.checkNull(req.getParameter("number"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			
			String predate = StringUtil.checkNull(req.getParameter("predate"));
			String postdate = StringUtil.checkNull(req.getParameter("postdate"));
			
			String creator = StringUtil.checkNull(req.getParameter("creator"));
			String state = StringUtil.checkNull(req.getParameter("state"));
			
			String licensing = StringUtil.checkNull(req.getParameter("licensing"));
			
			String[] models = req.getParameterValues("model");
			
			String sortCheck =StringUtil.checkNull(req.getParameter("sortCheck"));
			String sortValue =StringUtil.checkNull(req.getParameter("sortValue"));
			
			String riskType = StringUtil.checkNull(req.getParameter("riskType"));
			String preApproveDate = StringUtil.checkNull(req.getParameter("preApproveDate"));
			String postApproveDate = StringUtil.checkNull(req.getParameter("postApproveDate"));
			
			//System.out.println("Query licensing ====="+licensing);
			//System.out.println("Query riskType ====="+riskType);
			
			qs = new QuerySpec(); 
			Class ecoClass = EChangeOrder.class;
			int ecoIdx = qs.appendClassList(ecoClass, true);
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NAME, SearchCondition.LIKE, "%"+name+"%", false), new int[] {ecoIdx});
			}
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NUMBER, SearchCondition.LIKE, "%"+number+"%", false), new int[] {ecoIdx});
			}
			
			
			//등록일
			if(predate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(predate)), new int[] {ecoIdx});
			}
			//등록일
			if(postdate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postdate)), new int[] {ecoIdx});
			}
			//상태
			if(state.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LIFE_CYCLE_STATE, SearchCondition.EQUAL , state), new int[] {ecoIdx});
			}//creator.key.id
			
			//등록자
			if(creator.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				People pp = (People)CommonUtil.getObject(creator);
				long longOid = CommonUtil.getOIDLongValue(pp.getUser());
				qs.appendWhere(new SearchCondition(ecoClass, "creator.key.id", SearchCondition.EQUAL , longOid), new int[] {ecoIdx});
			}
			
			
			//ECO 구분
			if(eoType.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, eoType, false), new int[] {ecoIdx});
			}else{
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_DEV, false), new int[] {ecoIdx});
				
				qs.appendOr();
				
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, ECOKey.ECO_PRODUCT, false), new int[] {ecoIdx});
				qs.appendCloseParen();
			}
			
			//인허가 구분
			if(licensing.length()>0){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				if(licensing.equals("NONE")){
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
				}else{
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.LICENSING_CHANGE, SearchCondition.EQUAL, licensing, false), new int[] {ecoIdx});
				}
				
			}
			
			//인허가 구분
			if(riskType.length()>0){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				if(riskType.equals("NONE")){
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.IS_NULL,true), new int[] {ecoIdx});
				}else{
					qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.RISK_TYPE, SearchCondition.EQUAL, riskType, false), new int[] {ecoIdx});
				}
				
			}
			
			//승인일
			if(preApproveDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.GREATER_THAN_OR_EQUAL ,preApproveDate), new int[] {ecoIdx});
			}
			//승인일
			if(postApproveDate .length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecoClass, "eoApproveDate", SearchCondition.LESS_THAN_OR_EQUAL , postApproveDate), new int[] {ecoIdx});
			}
			
			
			
			//제품명
			if(models != null){
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendOpenParen();
					for(int i = 0 ;i < models.length ;i++){
						qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.MODEL, SearchCondition.LIKE, "%"+models[i]+"%", false), new int[] {ecoIdx});
						if(i== models.length-1) break;
						qs.appendOr();
						
					}
				qs.appendCloseParen();
			}
			
			String[] completeParts = req.getParameterValues("completeParts");
			if(completeParts != null && completeParts.length > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				
				int idx_partLink = qs.appendClassList(EOCompletePartLink.class, false);
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.theObjectIdentifier.id", EOCompletePartLink.class, EOCompletePartLink.ROLE_BOBJECT_REF + ".key.id"), new int[] {ecoIdx, idx_partLink});

				qs.appendAnd();
				qs.appendOpenParen();
				for(int i=0; i < completeParts.length; i++) {
					
					if(i != 0) {
						qs.appendOr();
					}
					
					String completePart = completeParts[i];
					WTPart part = (WTPart)CommonUtil.getObject(completePart);
					qs.appendWhere(new SearchCondition(EOCompletePartLink.class, EOCompletePartLink.ROLE_AOBJECT_REF+".key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(part.getMaster())),new int[] {idx_partLink});
					
				}
				
				qs.appendCloseParen();
			}
			
			if(sortValue != null && sortValue.length() > 0) {
				//System.out.println("sortCheck="+sortCheck+"\tsortValue="+sortValue);
				if("true".equals(sortCheck)){
					
					if( !"creator.key.id".equals(sortValue)){
						if(!"PROCESSDATE".equals(sortValue)){
							qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), true), new int[] { ecoIdx });
						}
					}else{
						
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					
					if( !"creator.key.id".equals(sortValue)){
						if(!"PROCESSDATE".equals(sortValue)){
							qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeOrder.class,sortValue), false), new int[] { ecoIdx });
						}
					}else{
						
						if(qs.getConditionCount() > 0) qs.appendAnd();
						int idx_user = qs.appendClassList(WTUser.class, false);
						int idx_people = qs.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EChangeOrder.class, "creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
						qs.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{ecoIdx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(qs, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.EO_APPROVE_DATE), true), new int[] { ecoIdx }); 
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, EChangeOrder.CREATE_TIMESTAMP), true), new int[] { ecoIdx });
			}
			 
			//System.out.println(qs);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	return qs;
	}
	/**
	 * 설변 부품 조회
	 * @param eco
	 * @return
	 */
	@Override
	public QueryResult ecoPartLink(EChangeOrder eco){ 
		QueryResult qr = null;
		try{
				//rt=PersistenceHelper.navigate(eco, "part", EcoPartLink.class, false);
				
				
				QuerySpec qs = new QuerySpec();
				
				int idx = qs.appendClassList(EcoPartLink.class, true);
				int idx2 = qs.appendClassList(WTPartMaster.class, false);
				
				ClassAttribute ca	= new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");
				ClassAttribute ca2	= new ClassAttribute(EcoPartLink.class, "roleAObjectRef.key.id");
				
				qs.appendWhere(new SearchCondition(ca2, "=", ca), new int[]{idx, idx2});
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(EcoPartLink.class, "roleBObjectRef.key.id", "=", CommonUtil.getOIDLongValue(eco)), new int[]{idx});
				
				SearchUtil.setOrderBy(qs, WTPartMaster.class, idx2, WTPartMaster.NUMBER, "sort", false);
				
				qr = PersistenceHelper.manager.find(qs);
				
				
				
				
				
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return qr;
	}
	
	/**
	 * 설변 부품 조회
	 * @param eco
	 * @return
	 */
	@Override
	public Vector<WTPart> ecoPartList(EChangeOrder eco){
		QueryResult qr = null;
		Vector<WTPart> partList = new Vector();
		try{
			qr=ecoPartLink(eco);
			while(qr.hasMoreElements()){
				//EcoPartLink link = (EcoPartLink)qr.nextElement();
				
				Object[] o = (Object[])qr.nextElement();
				
				EcoPartLink link = (EcoPartLink)o[0];
				
				String version = link.getVersion();
				WTPartMaster master = (WTPartMaster)link.getPart();
				WTPart part = PartHelper.service.getPart(master.getNumber(),version);
				partList.add(part);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return partList;
	}
	
	/**
	 * eco에서 Part 리스트중에서 개정 리스트
	 * @param eco
	 * @return
	 */
	@Override
	public List<WTPart> ecoPartReviseList(EChangeOrder eco){
		Vector<WTPart> partList = new Vector();
		try{
			QueryResult qr=ecoPartLink(eco);
			while(qr.hasMoreElements()){
				
				//EcoPartLink link = (EcoPartLink)qr.nextElement();
				
				Object[] o = (Object[])qr.nextElement();
				
				EcoPartLink link = (EcoPartLink)o[0];
				
				
				String version = link.getVersion();
				WTPartMaster master = (WTPartMaster)link.getPart();
				WTPart part = PartHelper.service.getPart(master.getNumber(),version);
				if(link.isRevise()){
					WTPart nextPart = (WTPart)com.e3ps.common.obj.ObjectUtil.getNextVersion(part);
					partList.add(nextPart);
				}else{
					partList.add(part);
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return partList;
	}
	
	/**
	 * eco에서 Part Link
	 * @param eco
	 * @return
	 */
	@Override
	public Vector<EcoPartLink> ecoPartLinkList(EChangeOrder eco){
		Vector<EcoPartLink> partList = new Vector();
		try{
			QueryResult qr=ecoPartLink(eco);
			while(qr.hasMoreElements()){
				//EcoPartLink link = (EcoPartLink)qr.nextElement();
				
				Object[] o = (Object[])qr.nextElement();
				
				EcoPartLink link = (EcoPartLink)o[0];
				partList.add(link);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return partList;
	}
	
	public QuerySpec getECOPartList(HttpServletRequest req){
		QuerySpec qs =null;
		
		try{
			String name = StringUtil.checkNull(req.getParameter("name")) ;
			String number = StringUtil.checkNull(req.getParameter("number"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			
			qs = new QuerySpec(); 
			Class ecoClass = EChangeOrder.class;
			Class linkClass = EcoPartLink.class;
			Class partClass = WTPart.class;
			Class masterClass = WTPartMaster.class;
			
			int ecoIdx = qs.appendClassList(ecoClass, false);
			int linkIdx = qs.appendClassList(linkClass, false);
			int partIdx = qs.appendClassList(partClass, true);
			int masterIdx = qs.appendClassList(masterClass, false);
			
			SearchCondition sc1 = new SearchCondition(new ClassAttribute(ecoClass, "thePersistInfo.theObjectIdentifier.id"), SearchCondition.EQUAL, new ClassAttribute(linkClass, "roleBObjectRef.key.id"));
		    sc1.setFromIndicies(new int[]{ecoIdx, linkIdx}, 0);
		    sc1.setOuterJoin(0);
		    qs.appendWhere(sc1, new int[]{ecoIdx, linkIdx});
		    
		    if(qs.getConditionCount() > 0) { qs.appendAnd(); }
		    sc1 = new SearchCondition(new ClassAttribute(masterClass, "thePersistInfo.theObjectIdentifier.id"), SearchCondition.EQUAL, new ClassAttribute(linkClass, "roleAObjectRef.key.id"));
		    sc1.setFromIndicies(new int[]{masterIdx, linkIdx}, 0);
		    sc1.setOuterJoin(0);
		    qs.appendWhere(sc1, new int[]{masterIdx, linkIdx});
		    
		    if(qs.getConditionCount() > 0) { qs.appendAnd(); }
		    sc1 = new SearchCondition(new ClassAttribute(masterClass, "thePersistInfo.theObjectIdentifier.id"), SearchCondition.EQUAL, new ClassAttribute(partClass, "masterReference.key.id"));
		    sc1.setFromIndicies(new int[]{masterIdx, partIdx}, 0);
		    sc1.setOuterJoin(0);
		    qs.appendWhere(sc1, new int[]{masterIdx, partIdx});
		    
		    //최신 이터레이션
			if(qs.getConditionCount() > 0) { qs.appendAnd(); }
			qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[]{partIdx});
			
			//최신 버전 검색
			SearchUtil.addLastVersionCondition(qs, WTPart.class, partIdx);
			
			//승인됨
			if(qs.getConditionCount() > 0) { qs.appendAnd(); }
			qs.appendWhere(new SearchCondition(WTPart.class, "state.state" , SearchCondition.EQUAL, "APPROVED"), new int[]{partIdx});
			
			//제목
			if(name.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NAME, SearchCondition.EQUAL, name, false), new int[] {ecoIdx});
			}
			
			//번호
			if(number.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NUMBER, SearchCondition.EQUAL, number, false), new int[] {ecoIdx});
			}
			
			//EO Type
			if(eoType.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, eoType, false), new int[] {ecoIdx});
			}
			
			//System.out.println(qs);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return qs;
		
	}
	
	@Override
	public QuerySpec getEOPartList(HttpServletRequest req) {
		QuerySpec qs =null;
		
		try{
			
			String name = StringUtil.checkNull(req.getParameter("name")) ;
			String number = StringUtil.checkNull(req.getParameter("number"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			
			qs = new QuerySpec(); 
			qs.setAdvancedQueryEnabled(true);
			int idx = qs.appendClassList(WTPart.class, true);
			QuerySpec subQs=getECOPartSubQuery(req);
			
			
			ClassAttribute attr1 = new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id");
			SearchCondition sc = new SearchCondition(attr1, SearchCondition.IN, new SubSelectExpression(subQs));
			qs.appendWhere(sc, new int[]{0});
			
			//System.out.println(qs);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return qs;
		
	}
	
	public QuerySpec getECOPartSubQuery(HttpServletRequest req){
		QuerySpec subQs =null;
		
		try{
			String name = StringUtil.checkNull(req.getParameter("name")) ;
			String number = StringUtil.checkNull(req.getParameter("number"));
			String eoType = StringUtil.checkNull(req.getParameter("eoType"));
			
			//qs = new QuerySpec(); 
			/*SubQquery*/
			subQs = new QuerySpec(); 
			Class ecoClass = EChangeOrder.class;
			Class linkClass = EcoPartLink.class;
			Class partClass = WTPart.class;
			Class masterClass = WTPartMaster.class;
			
			int ecoIdx = subQs.appendClassList(ecoClass, false);
			int linkIdx = subQs.appendClassList(linkClass, false);
			int partIdx = subQs.appendClassList(partClass, false);
			int masterIdx = subQs.appendClassList(masterClass, false);
			//ClassAttribute ca = new ClassAttribute(partClass, "thePersistInfo.theObjectIdentifier.classname");
			//subQs.appendSelect(ca, new int[] { partIdx }, false);
			ClassAttribute ca= new ClassAttribute(partClass, "thePersistInfo.theObjectIdentifier.id");
			subQs.appendSelect(ca, new int[] { partIdx }, false);
			
			//qs.setAdvancedQueryEnabled(true);
			subQs.setDistinct(true);
			SearchCondition sc1 = new SearchCondition(new ClassAttribute(ecoClass, "thePersistInfo.theObjectIdentifier.id"), SearchCondition.EQUAL, new ClassAttribute(linkClass, "roleBObjectRef.key.id"));
		    sc1.setFromIndicies(new int[]{ecoIdx, linkIdx}, 0);
		    sc1.setOuterJoin(0);
		    subQs.appendWhere(sc1, new int[]{ecoIdx, linkIdx});
		    
		    if(subQs.getConditionCount() > 0) { subQs.appendAnd(); }
		    sc1 = new SearchCondition(new ClassAttribute(masterClass, "thePersistInfo.theObjectIdentifier.id"), SearchCondition.EQUAL, new ClassAttribute(linkClass, "roleAObjectRef.key.id"));
		    sc1.setFromIndicies(new int[]{masterIdx, linkIdx}, 0);
		    sc1.setOuterJoin(0);
		    subQs.appendWhere(sc1, new int[]{masterIdx, linkIdx});
		    
		    if(subQs.getConditionCount() > 0) { subQs.appendAnd(); }
		    sc1 = new SearchCondition(new ClassAttribute(masterClass, "thePersistInfo.theObjectIdentifier.id"), SearchCondition.EQUAL, new ClassAttribute(partClass, "masterReference.key.id"));
		    sc1.setFromIndicies(new int[]{masterIdx, partIdx}, 0);
		    sc1.setOuterJoin(0);
		    subQs.appendWhere(sc1, new int[]{masterIdx, partIdx});
		    
		    //최신 이터레이션
			if(subQs.getConditionCount() > 0) { subQs.appendAnd(); }
			subQs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[]{partIdx});
			
			//최신 버전 검색
			SearchUtil.addLastVersionCondition(subQs, WTPart.class, partIdx);
			
			//승인됨
			if(subQs.getConditionCount() > 0) { subQs.appendAnd(); }
			subQs.appendWhere(new SearchCondition(WTPart.class, "state.state" , SearchCondition.EQUAL, "APPROVED"), new int[]{partIdx});
			
			//제목
			if(name.length() > 0) {
				if( subQs.getConditionCount() > 0 ) {
					subQs.appendAnd();
				}
				subQs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NAME, SearchCondition.EQUAL, name, false), new int[] {ecoIdx});
			}
			
			//번호
			if(number.length() > 0) {
				if( subQs.getConditionCount() > 0 ) {
					subQs.appendAnd();
				}
				subQs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NUMBER, SearchCondition.EQUAL, number, false), new int[] {ecoIdx});
			}
			
			//EO Type
			if(eoType.length() > 0) {
				if( subQs.getConditionCount() > 0 ) {
					subQs.appendAnd();
				}
				subQs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_TYPE, SearchCondition.EQUAL, eoType, false), new int[] {ecoIdx});
			}
			
			//System.out.println(subQs);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return subQs;
		
	}

	/******************* 루트로닉 추가 *******************/
	 
	 /**
	  * 완제품 ECO,PART 에서 링크 정보
	  */
	@Override
	public List<EOCompletePartLink> getCompletePartLink(WTObject obj) throws WTException {
		
		List<EOCompletePartLink> list = new ArrayList<EOCompletePartLink>();
		QueryResult rt = null;
		if(obj instanceof EChangeOrder){
			EChangeOrder eco = (EChangeOrder)obj;
			rt = PersistenceHelper.manager.navigate(eco, "completePart", EOCompletePartLink.class,false);
		}else{
			WTPart part = (WTPart)obj;
			rt = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EOCompletePartLink.class,false);
		}
		
		
		while(rt.hasMoreElements()){
			EOCompletePartLink link = (EOCompletePartLink)rt.nextElement();
			list.add(link);
		}
		return list;
	}
	
	@Override
	public List<WTPart> getCompletePartList(EChangeOrder eco) throws Exception {
		
		List<EOCompletePartLink> list = getCompletePartLink(eco);
		List<WTPart> partList = new ArrayList<WTPart>();
		for(EOCompletePartLink link : list){
			String version = link.getVersion();
			WTPartMaster master = (WTPartMaster)link.getCompletePart();
			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
			partList.add(part);
		}
		
		return partList;
		
	}
	
	@Override
	public List<PartData> getCompletePartDataList(EChangeOrder eco) throws Exception {
		List<PartData> dataList = new ArrayList<PartData>();
		
		if(eco != null){
			List<WTPart> partList = getCompletePartList(eco);
			
			for(WTPart part : partList){
				PartData data = new PartData(part);
				dataList.add(data);
			}
		}
		
		
		return dataList;
		
	}
	/**
	 * 관련 ECR,ECO
	 */
	@Override
	public List<RequestOrderLink> getRequestOrderLink(ECOChange eo) throws WTException {
		
		List<RequestOrderLink> list = new ArrayList<RequestOrderLink>();
		QueryResult rt = null;
		if(eo instanceof EChangeOrder){
			rt=PersistenceHelper.manager.navigate(eo, "ecr", RequestOrderLink.class,false);
		}else if (eo instanceof EChangeRequest){
			rt=PersistenceHelper.manager.navigate(eo, "eco", RequestOrderLink.class,false);
		}else {
			return list;
		}
		
		while(rt.hasMoreElements()){
			RequestOrderLink link = (RequestOrderLink)rt.nextElement();
			list.add(link);
		}
		return list;
	}
	
	@Override
	public List<EChangeOrder> getRequestOrderLinkECO(EChangeRequest ecr) throws Exception {
		
		List<RequestOrderLink> list = getRequestOrderLink(ecr);
		List<EChangeOrder> ecoList = new ArrayList<EChangeOrder>();
		for(RequestOrderLink link : list){
			EChangeOrder eco = (EChangeOrder)link.getRoleAObject();
			ecoList.add(eco);
		}
		
		return ecoList;
		
	}
	
	@Override
	public List<ECOData> getRequestOrderLinkECOData(EChangeRequest ecr) throws Exception {
		List<ECOData> dataList = new ArrayList<ECOData>();
		
		if(ecr != null){
			List<EChangeOrder> ecoList = getRequestOrderLinkECO(ecr);
			
			for(EChangeOrder eco : ecoList){
				ECOData data = new ECOData(eco);
				dataList.add(data);
			}
		}
		
		
		return dataList;
		
	}
	
	/**
	 * ECA 활동중 도면 개정(선택한 리스트)
	 * @param revisableArr
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> batchRevision(String[] revisableArr) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		boolean isBatchRevison = true;
		if (revisableArr != null) {
			for (String linkOid : revisableArr) {
				Map<String, Object> map = new HashMap<String, Object>();

				EcoPartLink link = (EcoPartLink) CommonUtil.getObject(linkOid);
				String oid = link.getPersistInfo().getObjectIdentifier().toString();
				map.put("linkOid", oid);

				/**
				 * 품목 관련 Data
				 * 
				 */
				String version = link.getVersion();
				WTPartMaster master = (WTPartMaster) link.getPart();
				WTPart part = PartHelper.service.getPart(master.getNumber(), version);
				boolean isLatestPart = VersionHelper.service.isLastVersion(part);
				String isLatestPartStyle = "";
				if (isLatestPart) {
					isLatestPartStyle = "<font color='blue'><b>Y</b> </font>";
				} else {
					isBatchRevison = false;
					isLatestPartStyle = "<font color='red'><b>N</b> </font>";
				}
				map.put("partOid", part.getPersistInfo().getObjectIdentifier().toString());
				map.put("partNumber", part.getNumber());
				map.put("partName", part.getName());
				map.put("partState", part.getLifeCycleState().getDisplay(Message.getLocale()));
				map.put("partStyle", isLatestPartStyle);
				map.put("partVersion", part.getVersionIdentifier().getValue());

				/**
				 * 주 도면 Data
				 * 
				 */

				EPMDocument epm3d = null;
				EPMBuildRule buildRule = PartSearchHelper.service.getBuildRule(part);
				String isLatest3DStyle = "";
				String epmNumber = "";
				String epmVersion = "";
				boolean isLatest3D = true;
				Vector<EPMReferenceLink> vec2D = new Vector<EPMReferenceLink>();

				if (buildRule != null) {
					epm3d = (EPMDocument) buildRule.getBuildSource();
					isLatest3D = VersionHelper.service.isLastVersion(epm3d);

					if (isLatest3D) {
						isLatest3DStyle = "<b><font color='blue'>Y </font></b>";
					} else {
						isBatchRevison = false;
						isLatest3DStyle = "<b><font color='red'>N </font></b>";
					}
					epmNumber = epm3d.getNumber();
					epmVersion = "[" + epm3d.getVersionIdentifier().getValue() + "]";

					if (epm3d != null) {
						vec2D = EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster) epm3d.getMaster());
					}
				}
				map.put("epmNumber", epmNumber);
				map.put("epm3DStyle", isLatest3DStyle);
				map.put("epmVersion", epmVersion);

				/**
				 * 참조 항목 Data 2D
				 * 
				 */

				String isLatest2DStyle = "";
				List<Map<String, String>> epm2D = new ArrayList<Map<String, String>>();
				for (EPMReferenceLink epmlink : vec2D) {
					EPMDocument epm2d = epmlink.getReferencedBy();
					boolean isLatest2D = VersionHelper.service.isLastVersion(epm2d);

					if (isLatest2D) {
						isLatest2DStyle = "<font color='blue'><b>Y</b></font>";
					} else {
						isBatchRevison = false;
						isLatest2DStyle = "<font color='red'><b>N</b></font>";
					}

					Map<String, String> map2D = new HashMap<String, String>();
					map2D.put("epm2DNumber", epm2d.getNumber());
					map2D.put("epm2DStyle", isLatest2DStyle);
					map2D.put("epm2DVersion", epm2d.getVersionIdentifier().getValue());

					epm2D.add(map2D);

				}
				map.put("epm2D", epm2D);

				

				String rowSpan = String.valueOf(vec2D.size());
				map.put("rowSpan", rowSpan);

				map.put("isBatchRevison", isBatchRevison);

				list.add(map);
			}
		}

		return list;
	}
	
	/**
	 * 부품의 관련  ECO의 view 용
	 * @param oid
	 * @param moduleType
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<ECOData> include_ChangeECOView(String oid, String moduleType) throws Exception {
		List<ECOData> list = new ArrayList<ECOData>();
		if(StringUtil.checkString(oid)){
    		if("part".equals(moduleType)) {
    			
    			WTPart part = (WTPart)CommonUtil.getObject(oid);
    			
    			List<EChangeOrder> eolist = getPartTOECOList(part);
    			
    			for(EChangeOrder eco : eolist){
    				ECOData data = new ECOData(eco);
    				
    				list.add(data);
    			}
    			
    		}
		}
		return list;
	}
	
	/**
	 * 부품의 관련  ECO의 view 용
	 * @param oid Part Oid
	 * @param moduleType DEV,PRODUCT : EO ,CHANGE: ECO
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<ECOData> include_DistributeEOList(String oid, String moduleType) throws Exception {
		List<ECOData> list = new ArrayList<ECOData>();
		if(StringUtil.checkString(oid)){
    		
			List<Map<String,String>> listDBECO = null;
			try {
				listDBECO = ChangeHelper.service.getGroupingBaseline(oid, "", "");
			} catch(Exception e) {
				listDBECO = new ArrayList<Map<String,String>>();
				e.printStackTrace();
			}
			try {
				int count = 1;
				for (int i = 0; i < listDBECO.size(); i++) {
					Map<String,String> map = listDBECO.get(i);
					String baseName = map.get("baseName");
					WTPart part = (WTPart)CommonUtil.getObject(oid);
					if(null!=baseName){
						//System.out.println("baseName = "+baseName);
						EChangeOrder eco = ECOSearchHelper.service.getEChangeOrder(baseName);
						//System.out.println("eco Check = "+(null!=eco));
						if(null!=eco){
							//System.out.println("eco Check = "+(eco.getEoNumber()));
							ECOData data = new ECOData(eco);
							if(moduleType.equals(data.getEoType())){
								ManagedBaseline baseline = ChangeHelper.service.getEOToPartBaseline(data.number, part.getNumber());
		    					if(baseline != null){
		    						data.setPartToEoBaseline(CommonUtil.getOIDString(baseline));
		    					}
		    					if(count>5){
		    						data.setListInitCount("none");
		    					}else{
		    						data.setListInitCount("");
		    					}
								list.add(data);
								count++;
							}
							
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    			/*WTPart part = (WTPart)CommonUtil.getObject(oid);
    			
    			List<EChangeOrder> eolist = getPartTOECOList(part);
    			int count = 1;
    			for(EChangeOrder eco : eolist){
    				ECOData data = new ECOData(eco);
    				//System.out.println("moduleType ="+moduleType+",data.getEoType()="+data.getEoType() );
    				if(moduleType.equals(data.getEoType())){
    					ManagedBaseline baseline = ChangeHelper.service.getEOToPartBaseline(data.number, part.getNumber());
    					if(baseline != null){
    						data.setPartToEoBaseline(CommonUtil.getOIDString(baseline));
    					}
    					if(count>5){
    						data.setListInitCount("none");
    					}else{
    						data.setListInitCount("");
    					}
    					list.add(data);
    					System.out.println("moduleType ="+moduleType+",eoNumber="+data.number+",ListInitCount =" + data.listInitCount );
    					count++;
    				}
    			}*/
 		}
		return list;
	}
	
	/**
	 * 부품의 관련  ECO
	 * @param oid
	 * @param moduleType
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<EChangeOrder> getPartTOECOList(WTPart part) throws Exception {
		
		List<EChangeOrder> list = new ArrayList<EChangeOrder>();
		QuerySpec qs = new QuerySpec();
		QueryResult eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class,true);
		
		while(eolinkQr.hasMoreElements()){
			EChangeOrder eo = (EChangeOrder)eolinkQr.nextElement();
			list.add(eo);
		}
		
		//ECO Type이 CHANGE 제외
		eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco",EOCompletePartLink.class,true);
		while(eolinkQr.hasMoreElements()){
			EChangeOrder eo = (EChangeOrder)eolinkQr.nextElement();
			if(eo.getEoType().equals(ECOKey.ECO_CHANGE)) continue;
			list.add(eo);
		}
		Collections.sort(list, new EoComparator());
		return list;
	}
	
	@Override
	public EChangeOrder getEChangeOrder(String ecoNumber) throws Exception{
		EChangeOrder eco = null;
		QuerySpec qs = new QuerySpec(); 
		Class ecoClass = EChangeOrder.class;
		int ecoIdx = qs.appendClassList(ecoClass, true);
		qs.appendWhere(new SearchCondition(ecoClass, EChangeOrder.EO_NUMBER, SearchCondition.EQUAL, ecoNumber, false), new int[] {ecoIdx});
		
		QueryResult rt =PersistenceHelper.manager.find(qs);
		
		while(rt.hasMoreElements()){
			
			Object[] obj = (Object[])rt.nextElement();
			eco = (EChangeOrder)obj[0];
			
		}
		return eco;
	}
	
	public static class ECOBeans{
		String ecoNumber;
		String ecooid;
		String ecoName;
		String licensingDisplay;
		String leifecycle;
		String creator;
		Date approverDate;
		Date createDate;
		public ECOBeans() {}
		public String getEcoNumber() {
			return ecoNumber;
		}
		public void setEcoNumber(String ecoNumber) {
			this.ecoNumber = ecoNumber;
		}
		public String getEcooid() {
			return ecooid;
		}
		public void setEcooid(String ecooid) {
			this.ecooid = ecooid;
		}
		public String getEcoName() {
			return ecoName;
		}
		public void setEcoName(String ecoName) {
			this.ecoName = ecoName;
		}
		public String getLicensingDisplay() {
			return licensingDisplay;
		}
		public void setLicensingDisplay(String licensingDisplay) {
			this.licensingDisplay = licensingDisplay;
		}
		public String getLeifecycle() {
			return leifecycle;
		}
		public void setLeifecycle(String leifecycle) {
			this.leifecycle = leifecycle;
		}
		public String getCreator() {
			return creator;
		}
		public void setCreator(String creator) {
			this.creator = creator;
		}
		public String getApproverDate() {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(null!=approverDate)
				return format.format(approverDate);
			else
				return "";
		}
		public void setApproverDate(String approverDate) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				if(approverDate != null && approverDate.length() > 0 ){
					date = format.parse(approverDate);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.leifecycle.equals("승인됨"))
				this.approverDate = date;
		}
		public String getCreateDate() {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(null!=createDate)
				return format.format(createDate);
			else
				return "";
		}
		public void setCreateDate(String createDate) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = format.parse(createDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.createDate = date;
		}
		
		
	}
	 /**
	  * 이름 오름차순
	  * @author falbb
	  *
	  */
	static class ApproveDateAscCompare implements Comparator<ECOBeans> {

		/**
		 * 오름차순(ASC)
		 */

		@Override
		public int compare(ECOBeans o1, ECOBeans o2) {
			// TODO Auto-generated method stub
			return o1.getApproverDate().compareTo(o2.getApproverDate());
		}

	}
	 /**
	  * 이름 오름차순
	  * @author falbb
	  *
	  */
	static class ApproveDateDescCompare implements Comparator<ECOBeans> {

		/**
		 * 오름차순(ASC)
		 */

		@Override
		public int compare(ECOBeans o1, ECOBeans o2) {
			// TODO Auto-generated method stub
			return o2.getApproverDate().compareTo(o1.getApproverDate());
		}

	}
	@Override
	public List<ECOData> getECOListToLinkRoleName(String documentOid,
			String roleName) throws Exception {
		WTDocument document = (WTDocument)CommonUtil.getObject(documentOid);
		return getECOListToLinkRoleName(document, roleName);
	}

	@Override
	public List<ECOData> getECOListToLinkRoleName(WTDocument document,
			String roleName) throws Exception {
		List<ECOData> list = new ArrayList<ECOData>();
		List<String> list_ECONo = new ArrayList<String>();
		//System.out.println(CommonUtil.getOIDString(document));
		QueryResult rt = PersistenceHelper.manager.navigate(document.getMaster(), "activity",DocumentActivityLink.class);
		//System.out.println("getECOListToLinkRoleName ::: Size = "+ rt.size());
		while(rt.hasMoreElements()){
			EChangeActivity eca = (EChangeActivity)rt.nextElement();
			if(eca != null){
				EChangeOrder eco =  (EChangeOrder) eca.getEo();
				ECOData data = new ECOData(eco);
				if(!list_ECONo.contains(data.number)){
					list.add(data);
				}
				list_ECONo.add(data.number);
			}
		}
		return list;
	}
	
}
