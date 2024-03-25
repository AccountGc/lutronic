<%@page import="wt.epm.EPMDocumentHelper"%>
<%@page import="wt.fc.IdentityHelper"%>
<%@page import="wt.epm.EPMDocumentMasterIdentity"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="com.ptc.wpcfg.deliverables.library.EPMDocumentMaker"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="com.e3ps.common.util.ZipUtil"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="wt.content.ContentServerHelper"%>
<%@page import="java.io.InputStream"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.util.WTProperties"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="com.e3ps.common.util.DateUtil"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "wt.epm.EPMDocument:239860181";
EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
EPMDocumentMaster m = (EPMDocumentMaster) epm.getMaster();

EPMDocumentHelper.service.changeCADName(m, "3012229912_EXT PORT WIRE 1.pdf");

//3012229912_EXT PORT WIRE 1.pdf.PORT WIRE 1

// EPMDocumentMasterIdentity identity = (EPMDocumentMasterIdentity)m.getIdentificationObject();
// identity.setName("EXT PORT WIRE 1");
// IdentityHelper.service.changeIdentity(m, identity);

%>