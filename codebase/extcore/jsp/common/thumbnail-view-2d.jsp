<%@page import="com.e3ps.common.util.StringUtil"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="com.e3ps.common.util.ThumbnailUtil"%>
<%@page import="wt.rule.impl.EpmdocumentCopyruleSeq"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="com.e3ps.drawing.service.DrawingHelper"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.part.WTPart"%>
<%@page import="com.ptc.wvs.server.ui.RepHelper"%>
<%@page import="com.ptc.wvs.common.ui.Representer"%>
<%@page import="java.io.File"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.representation.Representation"%>
<%@page import="wt.representation.Representable"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.viewmarkup.Viewable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getParameter("oid");
Persistable per = CommonUtil.getObject(oid);
EPMDocument epm2d = null;
if (per instanceof WTPart) {
	WTPart part = (WTPart) per;
	EPMDocument epm = PartHelper.manager.getEPMDocument(part);
	if (epm != null) {
		epm2d = PartHelper.manager.getEPMDocument2D(epm);
	}
} else if (per instanceof EPMDocument) {
	EPMDocument epm = (EPMDocument) per;
	System.out.println("epm="+epm);
	if (!epm.getDocType().toString().equals("CADDRAWING")) {
		epm2d = PartHelper.manager.getEPMDocument2D(epm);
// 		epm2d = DrawingHelper.manager.latest((EPMDocumentMaster)epm2d.getMaster());
	} else {
		epm2d = epm;
	}
}
System.out.println("epm2d="+epm2d);
String thumb = "";
if (epm2d != null) {
	thumb = ThumbnailUtil.thumbnail(epm2d);
}
%>
<%
	if(StringUtil.checkString(thumb)) {
%>
<img src="<%=thumb%>">
<%
	}
%>