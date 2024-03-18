<%@page import="com.e3ps.groupware.workprocess.WFItemUserLink"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="java.util.Base64"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.sql.Blob"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.SQLException"%>
<%@page import="com.e3ps.workspace.AsmApproval"%>
<%@page import="com.e3ps.workspace.AppPerLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="wt.fc.Persistable"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
QuerySpec query = new QuerySpec();
int idx = query.appendClassList(WFItemUserLink.class, true);
QueryResult qr = PersistenceHelper.manager.find(query);
while (qr.hasMoreElements()) {
	Object[] obj = (Object[]) qr.nextElement();
	WFItemUserLink link = (WFItemUserLink) obj[0];
	String comment = (String) link.getComment();
	out.println(comment);
}
%>