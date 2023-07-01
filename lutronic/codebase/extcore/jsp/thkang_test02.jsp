<%@page import="wt.pom.Transaction"%>
<%@page import="wt.fc.collections.WTHashSet"%>
<%@page import="wt.fc.collections.WTSet"%>
<%@page import="wt.part.WTPartDescribeLink"%>
<%@page import="java.io.IOException"%>
<%@page import="com.e3ps.common.obj.ObjectUtil"%>
<%@page import="wt.vc.baseline.BaselineMember"%>
<%@page import="com.e3ps.part.util.BomBroker"%>
<%@page import="com.e3ps.part.service.BomSearchHelper"%>
<%@page import="com.e3ps.erp.service.ERPSearchHelper"%>
<%@page import="com.e3ps.part.beans.PartTreeData"%>
<%@page import="com.e3ps.erp.beans.BomData"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="com.e3ps.change.service.ECOHelper"%>
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
<%
	out.println("start"+"</br>");
	/* delete doc to partLink */
	//wt.doc.WTDocument:194123655
	//Transaction trx = new Transaction();

	/* String[] docOidAry = {"wt.doc.WTDocument:194123729",
						  }; */
	//String docOid = "wt.doc.WTDocument:194123499";
	//for(String docOid : docOidAry){
		/* WTDocument doc = (WTDocument) CommonUtil.getObject(docOid);
		
		try {
			trx.start();
			
			QueryResult rs = PersistenceHelper.manager.navigate(doc, "describes", WTPartDescribeLink.class, false);
			
			WTSet ws = new WTHashSet(rs);
			
			PersistenceServerHelper.manager.remove(ws);
			
			trx.commit();
			trx = null;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(trx != null){
				trx.rollback();
			}
		} */
	//}
	
	out.println("end"+"</br>");
	
%>