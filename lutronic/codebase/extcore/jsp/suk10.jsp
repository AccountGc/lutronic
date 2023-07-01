<%@page import="com.e3ps.doc.service.DocumentQueryHelper"%>
<%@page import="com.e3ps.common.web.PageControl"%>
<%@page import="com.e3ps.common.web.PageQueryBroker"%>
<%@page import="com.e3ps.part.service.PartQueryHelper"%>
<%@page import="wt.fc.PagingSessionHelper"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="com.e3ps.change.EChangeActivity"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="com.e3ps.part.service.PartSearchHelper"%>
<%@page import="wt.epm.build.EPMBuildRule"%>
<%@page import="com.e3ps.drawing.service.EpmSearchHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.e3ps.change.service.ChangeHelper"%>
<%@page import="wt.vc.baseline.ManagedBaseline"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%@page import="com.e3ps.change.service.ChangeWfHelper"%>
<%@page import="wt.vc.views.ViewReference"%>
<%@page import="com.e3ps.common.message.Message"%>
<%@page import="wt.vc.wip.CheckoutLink"%>
<%@page import="wt.clients.vc.CheckInOutTaskLogic"%>
<%@page import="wt.vc.wip.Workable"%>
<%@page import="wt.vc.views.ViewHelper"%>
<%@page import="wt.iba.value.StringValue"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="wt.iba.value.IBAHolder"%>
<%@page import="com.e3ps.change.service.ChangeUtil"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.change.service.ECOSearchHelper"%>
<%@page import="com.e3ps.org.beans.PeopleData"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="com.e3ps.groupware.workprocess.service.WFItemHelper"%>
<%@page import="com.e3ps.part.util.PartUtil"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.e3ps.org.People"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="wt.epm.structure.EPMReferenceType"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="wt.epm.structure.EPMReferenceLink"%>
<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="java.util.List"%>
<%@page import="wt.org.WTPrincipal"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SQLFunction"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.vc.wip.WorkInProgressHelper"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFCell"%>
<%@page import="java.io.File"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFRow"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFSheet"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@page import="org.apache.poi.openxml4j.opc.OPCPackage"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.build.BuildRule"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.epm.util.EPMHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.common.iba.IBAUtil"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.util.WTException"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
public Map<String, Object> listPartAction(HttpServletRequest request, HttpServletResponse response, JspWriter out)
		throws Exception {
	
	long startTime = System.currentTimeMillis();
	long endTime = System.currentTimeMillis();
	long sTime = System.currentTimeMillis();
	long lTime = System.currentTimeMillis();
	long duration;
	
	//out.println("걸린 시간(s) : "+duration);
	
	int page = StringUtil.getIntParameter(request.getParameter("page"), 1);
	int rows = StringUtil.getIntParameter(request.getParameter("rows"), 10);
	int formPage = StringUtil.getIntParameter(request.getParameter("formPage"), 15);

	//String sessionId = "174197101";
	
	//PagingQueryResult qr = null;
	QueryResult qr = null;

	/* if (StringUtil.checkString(sessionId)) {
		startTime = System.currentTimeMillis();
		
		qr = PagingSessionHelper.fetchPagingSession((page - 1) * rows, rows, Long.valueOf(sessionId));
		
		endTime = System.currentTimeMillis();
		duration = (endTime - startTime)/1000;
		out.println(" sess 걸린 시간(s) : "+duration);
	} else { */
		startTime = System.currentTimeMillis();
		
		QuerySpec query = DocumentQueryHelper.service.getListQuery(request, response);
		out.println("<br>query : "+query);
		sTime = System.currentTimeMillis();
		//qr = PageQueryBroker.openPagingSession((page - 1) * rows, rows, query, true);
		//endTime = System.currentTimeMillis();
		//sTime = System.currentTimeMillis();
		qr = PersistenceHelper.manager.find(query);
		lTime = System.currentTimeMillis();
		//duration = (lTime - startTime)/1000;
		out.println("<br>Query get time (s) : "+(sTime - startTime)/1000);
		out.println("<br>Query pagingEnd time : "+(lTime - sTime)/1000);
		//out.println("<br>Query2 : "+(lTime - sTime)/1000);
		//out.println("<br>total 걸린 시간(s) : "+duration);
		out.println("<br>result size : "+qr.size());
		//out.println("<br>result size2 : "+qr2.size());
	//}

	/* PageControl control = new PageControl(qr, page, formPage, rows);
	int totalPage = control.getTotalPage();
	int startPage = control.getStartPage();
	int endPage = control.getEndPage();
	int listCount = control.getTopListCount();
	int totalCount = control.getTotalCount();
	int currentPage = control.getCurrentPage();
	String param = control.getParam();
	int rowCount = control.getTopListCount();

	StringBuffer xmlBuf = new StringBuffer();
	xmlBuf.append("<?xml version='1.0' encoding='UTF-8'?>");
	xmlBuf.append("<rows>");

	Object[] o = null;
	WTPart part = null;
	PartData data = null;
	//startTime = System.currentTimeMillis();
	String select = StringUtil.checkReplaceStr(request.getParameter("select"), "false");
	String remarks = "";
	while (qr.hasMoreElements()) {
		o = (Object[]) qr.nextElement();
		part = (WTPart) o[0];
		data = new PartData(part);
		remarks =  StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) part, AttributeKey.IBAKey.IBA_REMARKS));
		xmlBuf.append("<row id='" + data.oid + "'>");
		if ("true".equals(select)) {
			xmlBuf.append("<cell><![CDATA[]]></cell>");
		}

		String bom = "<button type='button' class='btnCustom' onclick=javascript:auiBom('" + data.oid+ "','')><span></span>BOM</buttom>";
		
		String bom2 = "<button type='button' class='btnCustom' onclick=javascript:auiBom2('" + data.oid+ "','')><span></span>BOM(데이터 요청)</buttom>";

		xmlBuf.append("<cell><![CDATA[" + (rowCount--) + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.icon + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.number + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[<a href=javascript:openView('" + data.oid + "')>" + data.name + "</a>]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.getLocation() + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.version + "." + data.iteration + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + remarks + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.getLifecycle() + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.creator + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.createDate.substring(0, 10) + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + data.modifyDate.substring(0, 10) + "]]></cell>");
		xmlBuf.append("<cell><![CDATA[" + bom+ "]]></cell>");
		
		if("true".equals(select)) {
			String moduleType = request.getParameter("moduleType");
			if("ECO".equals(moduleType) || "EO".equals(moduleType)){
	        	
	        	boolean isSlect = PartSearchHelper.service.isSelectEO(part,moduleType);//data.isSelectEO();
	        	xmlBuf.append("<cell><![CDATA[" + !isSlect + "]]></cell>");
	        }else {
	        	xmlBuf.append("<cell><![CDATA[]]></cell>");
	        }
		}
		
		xmlBuf.append("</row>");
	}
	xmlBuf.append("</rows>");

	endTime = System.currentTimeMillis();
	duration = (endTime - startTime)/1000;
	out.println("<br>while 걸린 시간(s) : "+duration);
	
	Map<String, Object> result = new HashMap<String, Object>();

	result.put("formPage", formPage);
	result.put("rows", rows);
	result.put("totalPage", totalPage);
	result.put("startPage", startPage);
	result.put("endPage", endPage);
	result.put("listCount", listCount);
	result.put("totalCount", totalCount);
	result.put("currentPage", currentPage);
	result.put("param", param);
	result.put("sessionId", qr.getSessionId() == 0 ? "" : qr.getSessionId());
	result.put("xmlString", xmlBuf); */
	
	return null;
}
%>

<%
listPartAction(request, response, out);
%>