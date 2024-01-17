<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = "wt.part.WTPart:211812696";
WTPart part = (WTPart) CommonUtil.getObject(oid);

ArrayList<WTPart> list = new ArrayList<>();
PartHelper.manager.endRecursive(list, part);

out.println(list.size());

%>