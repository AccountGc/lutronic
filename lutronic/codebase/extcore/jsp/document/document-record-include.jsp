<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 버전 이력 -->
<jsp:include page="/extcore/jsp/document/document-iteration-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="250" name="height" />
</jsp:include>