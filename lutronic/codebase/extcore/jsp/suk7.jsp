<%@page import="com.e3ps.change.service.ECOHelper"%>
<%@page import="com.e3ps.common.iba.AttributeKey.IBAKey"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="com.e3ps.part.service.StandardPartQueryservice"%>
<%@page import="com.e3ps.part.service.PartQueryHelper"%>
<%@page import="wt.introspection.ClassInfo"%>
<%@page import="wt.introspection.WTIntrospector"%>
<%@page import="wt.pds.DatabaseInfoUtilities"%>
<%@page import="wt.query.KeywordExpression"%>
<%@page import="wt.query.RelationalExpression"%>
<%@page import="wt.iba.definition.litedefinition.AttributeDefDefaultView"%>
<%@page import="wt.iba.definition.service.IBADefinitionHelper"%>
<%@page import="com.e3ps.common.folder.beans.CommonFolderHelper"%>
<%@page import="wt.folder.IteratedFolderMemberLink"%>
<%@page import="com.e3ps.common.query.SearchUtil"%>
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
<%@page import="com.e3ps.org.dto.PeopleDTO"%>
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

public List<WTPart> ecoPartReviseList(EChangeOrder eco){
	Vector<WTPart> partList = new Vector();
	try{
		QueryResult qr= ECOSearchHelper.service.ecoPartLink(eco);
		while(qr.hasMoreElements()){
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



%>

<%
String ecoOid = "com.e3ps.change.EChangeOrder:182131164";
EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(ecoOid);
ECOHelper.service.completeECO(eco);

%>