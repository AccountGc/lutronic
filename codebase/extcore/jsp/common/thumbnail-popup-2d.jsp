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
EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
String thumb = "";
if (epm != null) {
	thumb = ThumbnailUtil.thumbnail(epm);
}
%>
<%
if (StringUtil.checkString(thumb)) {
	// 288 192
%>
<img src="<%=thumb%>" style="width: 864px; height: 576px;">
<%
}
%>