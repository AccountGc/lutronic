<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.BufferedOutputStream"%>
<%@page import="java.io.ObjectOutputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.sql.Blob"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="com.e3ps.groupware.workprocess.WFItemUserLink"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = "com.e3ps.groupware.workprocess.WFItemUserLink:8895751";
	WFItemUserLink itemUserLink = (WFItemUserLink) CommonUtil.getObject(oid);
	out.println(null!=itemUserLink);
	byte buff[] = new byte[1024];

	if(null!=itemUserLink){
		out.println("Comment : "+itemUserLink.getComment());
		String blobComment = String.valueOf(itemUserLink.getComment());
		String newComment ="111111111111111111111111";
		itemUserLink.setComment(newComment);
		itemUserLink=(WFItemUserLink)PersistenceHelper.manager.modify(itemUserLink);
	}
%>
