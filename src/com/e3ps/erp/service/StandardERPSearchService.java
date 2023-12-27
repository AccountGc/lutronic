package com.e3ps.erp.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.PageControl;
import com.e3ps.common.web.PageQueryBroker;
import com.e3ps.erp.BOMERP;
import com.e3ps.erp.ECOERP;
import com.e3ps.erp.PARTERP;
import com.e3ps.erp.beans.BOMERPData;
import com.e3ps.erp.beans.ECOERPData;
import com.e3ps.erp.beans.PARTERPData;
import com.e3ps.org.People;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.service.BomSearchHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.util.BomBroker;
import com.sap.conn.jco.JCoTable;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.ManagedBaseline;

@SuppressWarnings("serial")
public class StandardERPSearchService extends StandardManager implements ERPSearchService{
	
	public static StandardERPSearchService newStandardERPSearchService() throws Exception {
		final StandardERPSearchService instance = new StandardERPSearchService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public QuerySpec getPARTERPQuery(HttpServletRequest req) throws Exception{
		QuerySpec qs = null; 
		try{
			String zifno = StringUtil.checkNull(req.getParameter("zifno"));  //인터페이스번호
			String matnr = StringUtil.checkNull(req.getParameter("matnr"));  //자재번호
			String maktx = StringUtil.checkNull(req.getParameter("maktx"));  //자재명
			String aennr = StringUtil.checkNull(req.getParameter("aennr")); //변경번호
			String zifsta = StringUtil.checkNull(req.getParameter("zifsta")); //인터페이스상태
			String returnZifsta = StringUtil.checkNull(req.getParameter("returnZifsta")); //ERP 적용 상태
			String preCreateDate = StringUtil.checkNull(req.getParameter("preCreateDate"));
			String postCreateDate = StringUtil.checkNull(req.getParameter("postCreateDate"));
			
			String sortValue = StringUtil.checkNull(req.getParameter("sortValue"));
			String sortCheck = StringUtil.checkNull(req.getParameter("sortCheck"));
			qs = new QuerySpec(); 
			Class partClass = PARTERP.class;
			int partIdx = qs.appendClassList(partClass, true);
			
			//인터페이스번호
			if(zifno.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(partClass, PARTERP.ZIFNO, SearchCondition.LIKE, "%"+zifno+"%", false), new int[] {partIdx});
			}
			//자재번호
			if(matnr.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(partClass, PARTERP.MATNR, SearchCondition.LIKE, "%"+matnr+"%", false), new int[] {partIdx});
			}
			//자재명
			if(maktx.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(partClass, PARTERP.MAKTX, SearchCondition.LIKE, "%"+maktx+"%", false), new int[] {partIdx});
			}
			//변경번호
			if(aennr.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(partClass, PARTERP.AENNR, SearchCondition.LIKE, "%"+aennr+"%", false), new int[] {partIdx});
			}
			
			//인터페이스상태
			if(zifsta.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(partClass, PARTERP.ZIFSTA, SearchCondition.LIKE, "%"+zifsta+"%", false), new int[] {partIdx});
			}
			
			//ERP 적용 상태
			if(returnZifsta.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(partClass, PARTERP.RETURN_ZIFSTA, SearchCondition.LIKE, "%"+returnZifsta+"%", false), new int[] {partIdx});
			}
			
			//등록일
			if(preCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(partClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(preCreateDate)), new int[] {partIdx});
			}
			//등록일
			if(postCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(partClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postCreateDate)), new int[] {partIdx});
			}
			
			
			
			//정렬
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					qs.appendOrderBy(new OrderBy(new ClassAttribute(partClass,sortValue), true), new int[] { partIdx });
				}else{
					qs.appendOrderBy(new OrderBy(new ClassAttribute(partClass,sortValue), false), new int[] { partIdx });
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(partClass, PARTERP.ZIFNO), true), new int[] { partIdx });
			}
			
			
			//System.out.println(qs);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	return qs;
	}
	
	@Override
	public Map<String, Object> listPARTERPAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getPARTERPQuery(request);
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
		//System.out.println("qr.size() = " + qr.size());
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			PARTERP part = (PARTERP) o[0];
			PARTERPData data = new PARTERPData(part);
			
			
			xmlBuf.append("<row id='"+ data.oid +"'>");
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");//1
			xmlBuf.append("<cell><![CDATA[" + data.zifno + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.matnr + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.maktx + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.meins + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.matkl + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.ntgew + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.gewei + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.wrkst + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zspec + "]]></cell>" );//10
			xmlBuf.append("<cell><![CDATA[" + data.zfinsh + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.zmodel + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.zprodm + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.zdept + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zmat1 + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zmat2 + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zmat3 + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.aennr + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zeivr + "]]></cell>" );//19
			xmlBuf.append("<cell><![CDATA[" + data.zifsta + "]]></cell>");//인터페이스 상태
			xmlBuf.append("<cell><![CDATA[" + data.zifmsg + "]]></cell>");//인터페이스 결과
			xmlBuf.append("<cell><![CDATA[" + data.createDate + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.returnZifsta + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.returnZifmsg + "]]></cell>");
			
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
	public QuerySpec getECOERPQuery(HttpServletRequest req) throws Exception{
		QuerySpec qs = null; 
		try{
			String zifno = StringUtil.checkNull(req.getParameter("zifno"));  //인터페이스번호
			String aennr = StringUtil.checkNull(req.getParameter("aennr"));  //변경번호
			String aetxt = StringUtil.checkNull(req.getParameter("aetxt"));  //변경내역(제목)
			
			String zifsta = StringUtil.checkNull(req.getParameter("zifsta")); //인터페이스상태
			String returnZifsta = StringUtil.checkNull(req.getParameter("returnZifsta")); //ERP 적용 상태
			String preCreateDate = StringUtil.checkNull(req.getParameter("preCreateDate"));
			String postCreateDate = StringUtil.checkNull(req.getParameter("postCreateDate"));
			
			String sortValue = StringUtil.checkNull(req.getParameter("sortValue"));
			String sortCheck = StringUtil.checkNull(req.getParameter("sortCheck"));
			qs = new QuerySpec(); 
			Class ecoClass = ECOERP.class;
			int ecoIdx = qs.appendClassList(ecoClass, true);
			
			//인터페이스번호
			if(zifno.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, ECOERP.ZIFNO, SearchCondition.LIKE, "%"+zifno+"%", false), new int[] {ecoIdx});
			}
			//변경번호
			if(aennr.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, ECOERP.AENNR, SearchCondition.LIKE, "%"+aennr+"%", false), new int[] {ecoIdx});
			}
			//변경내역(제목)
			if(aetxt.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, ECOERP.AETXT, SearchCondition.LIKE, "%"+aetxt+"%", false), new int[] {ecoIdx});
			}
			//인터페이스상태
			if(zifsta.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, ECOERP.ZIFSTA, SearchCondition.LIKE, "%"+zifsta+"%", false), new int[] {ecoIdx});
			}
			
			//ERP 적용 상태
			if(returnZifsta.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(ecoClass, PARTERP.RETURN_ZIFSTA, SearchCondition.LIKE, "%"+returnZifsta+"%", false), new int[] {ecoIdx});
			}
			
			
			//등록일
			if(preCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(preCreateDate)), new int[] {ecoIdx});
			}
			//등록일
			if(postCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(ecoClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postCreateDate)), new int[] {ecoIdx});
			}
			
			
			
			//정렬
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass,sortValue), true), new int[] { ecoIdx });
				}else{
					qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass,sortValue), false), new int[] { ecoIdx });
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(ecoClass, ECOERP.ZIFNO), true), new int[] { ecoIdx });
			}
			
			
			//System.out.println(qs);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	return qs;
	}
	
	@Override
	public Map<String, Object> listECOERPAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getECOERPQuery(request);
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
		//System.out.println("qr.size() = " + qr.size());
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			ECOERP eco = (ECOERP) o[0];
			ECOERPData data = new ECOERPData(eco);
			
			
			xmlBuf.append("<row id='"+ data.oid +"'>");
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");//1
			xmlBuf.append("<cell><![CDATA[" + data.zifno + "]]></cell>" );  //인터페이스번호
			xmlBuf.append("<cell><![CDATA[" + data.aennr + "]]></cell>" );  //변경번호_000
			xmlBuf.append("<cell><![CDATA[" + data.aetxt + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.datuv + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.aegru + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zecmid + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zifsta + "]]></cell>");//인터페이스 상태
			xmlBuf.append("<cell><![CDATA[" + data.zifmsg + "]]></cell>");//인터페이스 결과
			xmlBuf.append("<cell><![CDATA[" + data.createDate + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.returnZifsta + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.returnZifmsg + "]]></cell>");
			
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
	public QuerySpec getBOMERPQuery(HttpServletRequest req) throws Exception{
		QuerySpec qs = null; 
		try{
			String zifno = StringUtil.checkNull(req.getParameter("zifno"));  //인터페이스번호
			String zitmsta = StringUtil.checkNull(req.getParameter("zitmsta"));  //설계변경 CUD
			String matnr = StringUtil.checkNull(req.getParameter("matnr"));  //모품번
			String posnr = StringUtil.checkNull(req.getParameter("posnr"));  //PREFIX
			String idnrk = StringUtil.checkNull(req.getParameter("idnrk"));  //자품번
			String aennr = StringUtil.checkNull(req.getParameter("aennr"));  //변경번호
			
			String zifsta = StringUtil.checkNull(req.getParameter("zifsta")); //인터페이스상태
			String returnZifsta = StringUtil.checkNull(req.getParameter("returnZifsta")); //ERP 적용 상태
			String preCreateDate = StringUtil.checkNull(req.getParameter("preCreateDate"));
			String postCreateDate = StringUtil.checkNull(req.getParameter("postCreateDate"));
			
			String sortValue = StringUtil.checkNull(req.getParameter("sortValue"));
			String sortCheck = StringUtil.checkNull(req.getParameter("sortCheck"));
			qs = new QuerySpec(); 
			Class bomClass = BOMERP.class;
			int bomIdx = qs.appendClassList(bomClass, true);
			
			//인터페이스번호
			if(zifno.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(bomClass, BOMERP.ZIFNO, SearchCondition.LIKE, "%"+zifno+"%", false), new int[] {bomIdx});
			}
			
			//설계변경 CUD
			if(zitmsta.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(bomClass, BOMERP.ZITMSTA, SearchCondition.LIKE, "%"+zitmsta+"%", false), new int[] {bomIdx});
			}
			
			//모품번
			if(matnr.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(bomClass, BOMERP.MATNR, SearchCondition.LIKE, "%"+matnr+"%", false), new int[] {bomIdx});
			}
			
			//자품번
			if(idnrk.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(bomClass, BOMERP.IDNRK, SearchCondition.LIKE, "%"+idnrk+"%", false), new int[] {bomIdx});
			}
			
			//변경번호
			if(aennr.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(bomClass, BOMERP.AENNR, SearchCondition.LIKE, "%"+aennr+"%", false), new int[] {bomIdx});
			}
			
			//인터페이스상태
			if(zifsta.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(bomClass, BOMERP.ZIFSTA, SearchCondition.LIKE, "%"+zifsta+"%", false), new int[] {bomIdx});
			}
			
			//ERP 적용 상태
			if(returnZifsta.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}
				qs.appendWhere(new SearchCondition(bomClass, PARTERP.RETURN_ZIFSTA, SearchCondition.LIKE, "%"+returnZifsta+"%", false), new int[] {bomIdx});
			}
			
			//등록일
			if(preCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//>=
				qs.appendWhere(new SearchCondition(bomClass, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN_OR_EQUAL , DateUtil.convertStartDate(preCreateDate)), new int[] {bomIdx});
			}
			//등록일
			if(postCreateDate.length() > 0) {
				if( qs.getConditionCount() > 0 ) {
					qs.appendAnd();
				}//<=
				qs.appendWhere(new SearchCondition(bomClass, "thePersistInfo.createStamp", SearchCondition.LESS_THAN_OR_EQUAL , DateUtil.convertEndDate(postCreateDate)), new int[] {bomIdx});
			}
			
			
			
			//정렬
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					qs.appendOrderBy(new OrderBy(new ClassAttribute(bomClass,sortValue), true), new int[] { bomIdx });
				}else{
					qs.appendOrderBy(new OrderBy(new ClassAttribute(bomClass,sortValue), false), new int[] { bomIdx });
				}
			}else{
				qs.appendOrderBy(new OrderBy(new ClassAttribute(bomClass, BOMERP.ZIFNO), true), new int[] { bomIdx });
			}
			
			
			//System.out.println(qs);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	return qs;
	}
	
	@Override
	public Map<String, Object> listBOMERPAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
		int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
		int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);
		
		String sessionId = request.getParameter("sessionId");
		
		PagingQueryResult qr = null;
		
		if(StringUtil.checkString(sessionId)) {
			qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		}else {
			QuerySpec query = getBOMERPQuery(request);
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
		//System.out.println("qr.size() = " + qr.size());
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			BOMERP bom = (BOMERP) o[0];
			BOMERPData data = new BOMERPData(bom);
			
			//System.out.println("PARTERPData = " + data.zifno );
			xmlBuf.append("<row id='"+ data.oid +"'>");
			xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");//1
			xmlBuf.append("<cell><![CDATA[" + data.zifno + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zitmsta + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.matnr + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.posnr + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.idnrk + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.menge + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.meins + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.aennr + "]]></cell>" );
			xmlBuf.append("<cell><![CDATA[" + data.zifsta + "]]></cell>");//인터페이스 상태
			xmlBuf.append("<cell><![CDATA[" + data.zifmsg + "]]></cell>");//인터페이스 결과
			xmlBuf.append("<cell><![CDATA[" + data.createDate + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.returnZifsta + "]]></cell>");
			xmlBuf.append("<cell><![CDATA[" + data.returnZifmsg + "]]></cell>");
			
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
	
	/**
	 * 최종 Basleline 
	 * @param partOid
	 * @return
	 * @throws Exception
	 */
	@Override
	public ManagedBaseline getLastManagedBaseline(String partOid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart)rf.getReference(partOid).getObject();
		
		QuerySpec qs = new QuerySpec();
		int ii = qs.appendClassList(ManagedBaseline.class, true);
		int jj = qs.appendClassList(BaselineMember.class,false);
		int kk = qs.appendClassList(WTPart.class,true);
		
		qs.appendWhere(new SearchCondition(ManagedBaseline.class,"thePersistInfo.theObjectIdentifier.id",BaselineMember.class,"roleAObjectRef.key.id"),new int[]{ii,jj});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(BaselineMember.class,"roleBObjectRef.key.id",WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{jj,kk});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class,"masterReference.key.id","=",part.getMaster().getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
		
		qs.appendOrderBy(new OrderBy(new ClassAttribute(ManagedBaseline.class,"thePersistInfo.createStamp"),true),new int[]{ii});
		//System.out.println(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		ManagedBaseline bl = null;
		while(qr.hasMoreElements()){
			Object[] o = (Object[])qr.nextElement();
			bl = (ManagedBaseline)o[0];
			WTPart pp = (WTPart)o[1];
			return bl;
			
		}
		return bl;
	}
	
	/*
	 * Baseline 하고 비교 
	 */
	@Override
	public ArrayList<PartTreeData[]> getBaseLineCompare(String oid) throws Exception {
		
		WTPart part = (WTPart)CommonUtil.getObject(oid);
		
		//최신 베이스 라인 가져옴.(본인이 포함된 base라인중에.
				
		ManagedBaseline baseline = getLastManagedBaseline(oid);
		
		List<PartTreeData> list = BomSearchHelper.service.getBOM(part, true, null,false);
		
		
		List<PartTreeData> baselinelist = new ArrayList<PartTreeData>();
		
		
		if(baseline != null){
			baselinelist = BomSearchHelper.service.getBOM(part, true, baseline,false);
		}
		
		
		BomBroker broker = new BomBroker();
		
		PartTreeData root = broker.getOneleveTree(part , null);
		PartTreeData root2 = null;
		if(baseline == null){
			root2 = new PartTreeData(part, null, 0,"");
		}else{
			WTPart part2 = getBaselinePart(oid, baseline);
			root2 = broker.getOneleveTree(part2 ,baseline);
		}
		
		//System.out.println(" baseline ="+baseline);
		ArrayList<PartTreeData[]> result = new ArrayList<PartTreeData[]>();
		broker.compareBom(root, root2, result);
		
		//System.out.println("result.size =" + result.size());
		
		
		return result;
	}
	
	public WTPart getBaselinePart(String oid, ManagedBaseline bsobj) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();

		WTPart part = (WTPart)rf.getReference(oid).getObject();

		if(bsobj!=null){
		    QuerySpec qs = new QuerySpec();
		    int ii = qs.addClassList(WTPart.class,true);
		    int jj = qs.addClassList(BaselineMember.class,false);
		    qs.appendWhere(new SearchCondition(BaselineMember.class,"roleBObjectRef.key.id",WTPart.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{jj,ii});
		    qs.appendAnd();
		    qs.appendWhere(new SearchCondition(BaselineMember.class,"roleAObjectRef.key.id","=",bsobj.getPersistInfo().getObjectIdentifier().getId()),new int[]{jj});
		    qs.appendAnd();
		    qs.appendWhere(new SearchCondition(WTPart.class,"masterReference.key.id","=",part.getMaster().getPersistInfo().getObjectIdentifier().getId()),new int[]{ii});
		    QueryResult qr = PersistenceHelper.manager.find(qs);
		    if(qr.hasMoreElements()){
		        Object[] o = (Object[])qr.nextElement();
		        part = (WTPart)o[0];
		    }
		}
		
		return part;
	}
	
	@Override
	public HashMap<String,Object> getZINFOData(Class cls,String startZinfo,String endZinfo) throws Exception {
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		QuerySpec qs = new QuerySpec();
		int idx = qs.addClassList(cls, true);
		startZinfo = StringUtil.checkNull(startZinfo);
		endZinfo = StringUtil.checkNull(endZinfo);
		if(startZinfo.length()>0 && endZinfo.length()>0){
			qs.appendWhere(new SearchCondition(cls, "zifno", SearchCondition.GREATER_THAN_OR_EQUAL, startZinfo, false), new int[] {idx});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, "zifno", SearchCondition.LESS_THAN_OR_EQUAL, endZinfo, false), new int[] {idx});
		}else if (startZinfo.length()>0 && endZinfo.length()==0) {
			qs.appendWhere(new SearchCondition(cls, "zifno", SearchCondition.EQUAL, startZinfo, false), new int[] {idx});
		}
		
		//System.out.println(qs.toString());
		QueryResult rt = PersistenceHelper.manager.find(qs);
		
		while(rt.hasMoreElements()){
			Object[] obj = (Object[])rt.nextElement();
			Object ob = (Object)obj[0];
			
			if(ob instanceof PARTERP){
				PARTERP part = (PARTERP)ob;
				map.put(part.getZifno(), part);
			}else if(ob instanceof ECOERP){
				ECOERP eco = (ECOERP)ob;
				map.put(eco.getZifno(), eco);
			}else{
				BOMERP bom = (BOMERP)ob;
				map.put(bom.getZifno(), bom);
			}
			
		}
		
		return map;
	}
	
	/**
	 * 배포시스템에서 완제품 상세 보기에서 ECO 별 ERP 전송 PART
	 * number ECO number
	 */
	@Override
	public List<PARTERPData> listPARTERPAction(String ecoNumber) throws Exception {
		
		List<PARTERPData> list = new ArrayList<PARTERPData>();
		QuerySpec qs = new QuerySpec(); 
		Class partClass = PARTERP.class;
		int partIdx = qs.appendClassList(partClass, true);
		
		
		qs.appendWhere(new SearchCondition(partClass, PARTERP.AENNR, SearchCondition.EQUAL, ecoNumber, false), new int[] {partIdx});
		qs.appendOrderBy(new OrderBy(new ClassAttribute(partClass, PARTERP.MATNR), false), new int[] { partIdx });
		
		//System.out.println(qs.toString());
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		EChangeOrder eco =ECOSearchHelper.service.getEChangeOrder(ecoNumber);
		HashMap<String, Object> partMap = new  HashMap<String, Object>();
		if(eco != null){
			Vector<EcoPartLink> link = ECOSearchHelper.service.ecoPartLinkList(eco);
			for(EcoPartLink linkPart : link){
				partMap.put(linkPart.getPart().getNumber(), linkPart.isRevise());
			}
		}
		
		
		
		//System.out.println(" listPARTERPAction qr.size() = " + qr.size());
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			PARTERP part = (PARTERP) o[0];
			
			WTPart wtpart = PartHelper.service.getPart(part.getMatnr(), part.getZeivr());
			String partOid = CommonUtil.getOIDString(wtpart);
			String manufacture = IBAUtil.getAttrValue(wtpart, AttributeKey.IBAKey.IBA_MANUFACTURE);
			PARTERPData data = new PARTERPData(part);
			boolean revise = (boolean)partMap.get(part.getMatnr());
			String reviseImg = (revise) ? "O" : "X";
			data.setRevise(reviseImg);
			data.setPartOid(partOid);
			data.setManufacture(manufacture);
			list.add(data);
		}
		return list;
	}
	
	/**
	 * 배포시스템에서 완제품 상세 보기에서 ECO 별 ERP 전송 PART
	 * number ECO number
	 */
	@Override
	public List<BOMERPData> listBOMERPAction(String ecoNumber) throws Exception {
		
		List<BOMERPData> list = new ArrayList<BOMERPData>();
		QuerySpec qs = new QuerySpec(); 
		Class bomClass = BOMERP.class;
		int bomIdx = qs.appendClassList(bomClass, true);
		
		
		qs.appendWhere(new SearchCondition(bomClass, PARTERP.AENNR, SearchCondition.LIKE, ecoNumber+"%", false), new int[] {bomIdx});
		qs.appendOrderBy(new OrderBy(new ClassAttribute(bomClass, PARTERP.MATNR), false), new int[] { bomIdx });
		
		//System.out.println(qs.toString());
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		
		//System.out.println("listBOMERPAction qr.size() = " + qr.size());
		while(qr.hasMoreElements()){	
			Object[] o = (Object[]) qr.nextElement();
			BOMERP bom = (BOMERP) o[0];
			
			BOMERPData data = new BOMERPData(bom);
			
			String pNumber = data.getMatnr();
			WTPart pPart = null;
			if(data.pVer.length()==0){
				pPart = PartHelper.service.getPart(pNumber);
				data.setpVer(pPart.getVersionIdentifier().getValue());
			}else{
				pPart = PartHelper.service.getPart(pNumber,data.pVer);
			}
			data.setpName(pPart.getName());
			data.setpOid(CommonUtil.getOIDString(pPart));
			
			String cNumber = data.getIdnrk();
			
			WTPart cPart = null;
			if(data.cVer.length()==0 ){
				cPart = PartHelper.service.getPart(cNumber);
				data.setcVer(cPart.getVersionIdentifier().getValue());
			}else{
				cPart = PartHelper.service.getPart(cNumber,data.cVer);
			}
			
			data.setcName(cPart.getName());
			data.setcOid(CommonUtil.getOIDString(cPart));
			list.add(data);
		}
		return list;
	}
	
	@Override
	public boolean checkSendEO(String eoNumber){
		boolean isSend = false;
		try{
			QuerySpec qs = new QuerySpec();
			Class partClass = PARTERP.class;
			int ecoIdx = qs.appendClassList(partClass, true);
			qs.appendWhere(new SearchCondition(partClass, PARTERP.AENNR, SearchCondition.EQUAL, eoNumber, false), new int[] {ecoIdx});
			
			QueryResult rt = PersistenceHelper.manager.find(qs);
			
			if(rt.size()>0){
				isSend = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isSend;
	}
	
}