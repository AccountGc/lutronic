<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="com.e3ps.part.service.PartSearchHelper"%>
<%@page import="com.e3ps.common.iba.AttributeKey.IBAKey"%>
<%@page import="java.util.HashMap"%>
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

<form name="mainForm" method="post">
<%!
public QuerySpec getECOQuery(HttpServletRequest req) throws Exception{
	QuerySpec qs = null; 
	try{
		
		qs = new QuerySpec(); 
		Class ecoClass = EChangeOrder.class;
		int ecoIdx = qs.appendClassList(ecoClass, true);
		 
		//System.out.println(qs);
	}catch(Exception e){
		e.printStackTrace();
	}

return qs;
}
%>
<div id="tablehead"></div>
<table>
<tr>
<td style="vertical-align:top;">
<div>
	<div id="list" style="height:150px;width:100%;">
	<%
	String[] partOids = {"wt.part.WTPart:175901812","wt.part.WTPart:175901336"};
	Map<String, Object> map = new HashMap<String, Object>();
	List<WTPart> completeList =  new ArrayList<WTPart>();
	List<WTPart> changeList = new ArrayList<WTPart>();
	List<String> modelList =new ArrayList<String>();
	String model = "";
	
	for(int i=0; partOids!=null && i<partOids.length; i++){

		WTPart part = (WTPart)CommonUtil.getObject(partOids[i]);
		
		
		
		//제품 수집
		String partModel=IBAUtil.getAttrValue(part, IBAKey.IBA_MODEL);
		//System.out.println("getEODataCollection part = "+part.getNumber()+"partModel = " + partModel +"="+ !modelList.contains(partModel));
		if(!modelList.contains(partModel)){
			model = model +","+partModel;
			modelList.add(partModel);
		}
		
		
		//완제품 수집
		completeList = PartSearchHelper.service.getPartEndItem(part, completeList);
		//System.out.println("getEODataCollection completeList = 완제품수집 완료.");
	}
	
	for (WTPart comPart : completeList) {
		String version =  comPart.getVersionInfo().getIdentifier().getValue();
		String stete = 	comPart.getState().toString();
		if(version.equals("A")&& stete.equals("INWORK")){
		out.print("<br> Version - " + comPart.getVersionInfo().getIdentifier().getValue() + " / state - " +comPart.getState().toString()   + " / number - " + comPart.getNumber() );
			continue;
		}
		String partModel=IBAUtil.getAttrValue(comPart, IBAKey.IBA_MODEL);
		if(!modelList.contains(partModel)){
			model = model +","+partModel;
			modelList.add(partModel);
		}
	}
	
	out.print("<br>" + model);
	%>
	</div>

</div>
</td>
</tr>
</table>
</form>
