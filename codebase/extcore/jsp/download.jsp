<%@page import="wt.epm.EPMDocument"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.part.bom.service.BomHelper"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "wt.part.WTPart:239201538";
WTPart part = (WTPart) CommonUtil.getObject(oid);

ArrayList<WTPart> list = PartHelper.manager.descendants(part);
for (WTPart node : list) {
	// 	out.println("번호 = " + node.getNumber() + "<br>");

	EPMDocument epm = PartHelper.manager.getEPMDocument(node);
	if (epm != null) {
		EPMDocument d = PartHelper.manager.getEPMDocument2D(epm);
		if (d != null) {
	out.println("드로잉 = " + d.getNumber() + " 부품 = " + node.getNumber() + "<br>");
		}
	}

}
%>