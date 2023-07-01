<%@page import="com.e3ps.change.EOCompletePartLink"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="com.e3ps.change.EcoPartLink"%>
<%@page import="java.util.Vector"%>
<%@page import="com.e3ps.change.service.ECOSearchHelper"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="com.e3ps.part.service.PartSearchHelper"%>
<%@page import="com.e3ps.common.iba.AttributeKey.IBAKey"%>
<%@page import="com.e3ps.common.iba.AttributeKey"%>
<%@page import="com.e3ps.common.iba.IBAUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="wt.iba.value.IBAHolder"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.part.util.PartUtil"%>
<%@page import="com.e3ps.part.beans.PartTreeData"%>
<%@page import="com.e3ps.part.beans.ObjectComarator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page import="com.e3ps.common.message.Message"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.part.util.BomBroker"%>
<%@page import="com.e3ps.part.beans.PartData"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<%
	//완제품 목록 중 하나 지우기 ! 
	//해당 ECO OID
	String ecoOid = "com.e3ps.change.EChangeOrder:192511518";
	//삭제할 완제품 품목 OID
	String[] partMasterOids = {
			"wt.part.WTPartMaster:192390713"
			};
	
	EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(ecoOid);
	WTPartMaster partMaster = null;
	//WTPartMaster partMaster = (WTPartMaster) CommonUtil.getObject(partMasterOids[0]);
	//out.println(eco.getEoNumber() + " : " +  partMasterOids[0]);
	for(int i=0; i<partMasterOids.length; i++){
		partMaster = (WTPartMaster) CommonUtil.getObject(partMasterOids[i]);
		out.println("완제품 제거 start >> <br/>  해당 ECO : " + eco.getEoNumber() + " 해당 완제품 이름 : " + partMaster.getName() + "<br/>");
		deletePartLink(eco, partMaster);
		out.println("완제품 제거 end >> <br/>  해당 ECO : " + eco.getEoNumber() + " 해당 완제품 이름 : " + partMaster.getName() + "<br/>");
	}
	
	
	
%>
<%!
private void deletePartLink(EChangeOrder eco, WTPartMaster partMaster) throws Exception{
	
	EOCompletePartLink link = getPartLinkToOne(eco, partMaster);
	
	
	PersistenceHelper.manager.delete(link);
	
}

private EOCompletePartLink getPartLinkToOne(EChangeOrder eco, WTPartMaster partMaster){
	
	EOCompletePartLink link = null;
	QueryResult qr = null;
	try{
			QuerySpec qs = new QuerySpec();
			
			int idx = qs.appendClassList(EOCompletePartLink.class, true);
			
			ClassAttribute ca2	= new ClassAttribute(EOCompletePartLink.class, "roleAObjectRef.key.id");
			
			qs.appendWhere(new SearchCondition(EOCompletePartLink.class, "roleBObjectRef.key.id", "=", CommonUtil.getOIDLongValue(eco)), new int[]{idx});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EOCompletePartLink.class, "roleAObjectRef.key.id", "=", CommonUtil.getOIDLongValue(partMaster)), new int[]{idx});
			
			qr = PersistenceHelper.manager.find(qs);
			
			while(qr.hasMoreElements()){
				
				Object[] o = (Object[])qr.nextElement();
				
				EOCompletePartLink link2 = (EOCompletePartLink)o[0];
				link = link2;
			}
			
	}catch(Exception e){
		e.printStackTrace();
	}
	
	
	return link;
}
%>


