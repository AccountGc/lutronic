<%@page import="com.e3ps.common.service.CommonHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = "com.e3ps.change.EChangeOrder:13886023";
	boolean isInit = true;
	try{
		CommonHelper.service.withDrawAction(oid,isInit);
	}catch(Exception e){
		e.printStackTrace();
	}
	
%>