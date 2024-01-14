<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="com.e3ps.change.EChangeRequest"%>
<%@page import="wt.query.QuerySpec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
QuerySpec query = new QuerySpec();
int idx = query.appendClassList(EChangeRequest.class, true);
SearchCondition sc = new SearchCondition(EChangeRequest.class, EChangeRequest.EO_NUMBER, "LIKE", "ECPR%");
query.appendWhere(sc, new int[] { idx });
QueryResult qr = PersistenceHelper.manager.find(query);
while (qr.hasMoreElements()) {
	Object[] obj = (Object[]) qr.nextElement();
	EChangeRequest e = (EChangeRequest) obj[0];
	out.println(e.getEoNumber() + "<br>");
}
%>