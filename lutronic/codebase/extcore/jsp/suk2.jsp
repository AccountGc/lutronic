<%@page import="com.e3ps.drawing.util.EpmPublishUtil"%>
<%@page import="com.e3ps.common.query.SearchUtil"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="com.ptc.wvs.server.util.FileHelper"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="wt.representation.Representation"%>
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

<form name="mainForm" method="post">

<div id="tablehead"></div>
<table>
<tr>
<td style="vertical-align:top;">
<div>
	<div id="list" style="height:150px;width:100%;">
	<%
	out.println("###### test################<br>");
	try{
		String cadType = "CADDRAWING";
		String cadDivision ="PROE";
		
		QuerySpec query = new QuerySpec();
		
		int idx = query.addClassList(EPMDocument.class, true);
		
		if(query.getConditionCount() > 0) { query.appendAnd(); }
		query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[]{idx});
		
		
		
		SearchUtil.addLastVersionCondition(query, EPMDocument.class, idx);;
		
		
		if(cadDivision.length()>0){
			query.appendAnd();
			query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , cadDivision, false), new int[]{idx});
		}
		
		if(cadType.length()>0){
			query.appendAnd();
			query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , cadType, false), new int[]{idx});
		}
		
		out.println("<br>"+query.toString());
		QueryResult rt  = PersistenceHelper.manager.find(query);
		while(rt.hasMoreElements()){
			Object[] obj = (Object[])rt.nextElement();
			
			EPMDocument epm = (EPMDocument)obj[0];
			
			//Representation representation = PublishUtils.getRepresentation(epm);
			QueryResult repResult = PublishUtils.getRepresentations(epm);
			String thum_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm), ContentRoleType.THUMBNAIL);	
			if(thum_mini==null){ 
		       if(repResult.size() > 0){
		    	   EpmPublishUtil.publish(epm);
		       }
		    }
		}
		
		
	}catch(Exception e){
		e.printStackTrace();
		out.print(e.getMessage());
	}
	%>
	</div>

</div>
</td>
</tr>
</table>
</form>
