<%@page import="wt.vc.wip.WorkInProgressHelper"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.lifecycle.LifeCycleTemplate"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="com.e3ps.common.util.WCUtil"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.change.EChangeOrder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "wt.epm.EPMDocument:222937086";
EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);

WorkInProgressHelper.service.undoCheckout(epm);

%>