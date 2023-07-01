<%@page import="wt.audit.auditResource"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="wt.audit.AuditRecord"%>
<%@page import="wt.audit.AuditEvent"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="wt.lifecycle.LifeCycleTemplate"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="wt.util.WTPropertyVetoException"%>
<%@page import="wt.vc.wip.WorkInProgressException"%>
<%@page import="wt.vc.wip.CheckoutLink"%>
<%@page import="wt.clients.vc.CheckInOutTaskLogic"%>
<%@page import="wt.vc.wip.WorkInProgressHelper"%>
<%@page import="wt.vc.wip.Workable"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.OutputStreamWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.iba.definition.service.IBADefinitionHelper"%>
<%@page import="wt.iba.value.StringValue"%>
<%@page import="wt.iba.definition.litedefinition.AttributeDefDefaultView"%>
<%@page import="com.e3ps.common.query.SearchUtil"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.org.People"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.QuerySpec"%>
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
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.epm.util.EPMHelper"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.common.iba.IBAUtil"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.e3ps.migration.beans.MigrationPartHelper"%>
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
	out.println("###### test22<br>");
	try{
		
		String predate = "2019-01-01";
		String postdate = "";
		
		HashMap<String, AuditRecord> map = new HashMap<String, AuditRecord>();
		
		QuerySpec qs = new QuerySpec();
		
		int idx = qs.appendClassList(WTUser.class, true);
		
	
		qs.appendWhere( new SearchCondition(WTUser.class, "disabled", SearchCondition.IS_TRUE), new int[] { idx });
		
		
		//SearchUtil.appendOrderBy(qs, WTUser.class, "thePersistInfo.createStamp", idx, true);
		
		
		QueryResult qr = PersistenceHelper.manager.find(qs);
		
		while(qr.hasMoreElements()){
			Object[] o = (Object[])qr.nextElement();
			WTUser user = (WTUser)o[0];
			
			out.println("<br>"+user.getName()+"--"+user.getFullName()+"--"+user.isDisabled()+"--"+DateUtil.getDateString(user.getModifyTimestamp(),"d"));
			
			
			//map.put(recode.getUserName(), recode);
			
		}
		out.println("<br>======================================");
		
		Set<String> keys = map.keySet(); // 해시맵 h에 있는 모든 키를 Set 컬렉션으로 리턴
		Iterator<String> it = keys.iterator(); // Set의 각 문자열을 순차 검색하는 Iterator 리턴
		out.println("<br>======================================"+keys.size());
		while(it.hasNext())  {
		    String key = it.next(); // 키
		    AuditRecord value = map.get(key); // 값
		    out.println("<br>(" + key + "," + value + ")"); // 키와 값의 쌍 출력
		}
		out.println("<br>============END==============111222=========");
	
	}catch(Exception e){
		e.printStackTrace();
		out.print("<br>"+e.getMessage());
	}
	%>
	</div>

</div>
</td>
</tr>
</table>
</form>
