<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 	관련 CR -->
<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="200" name="height" />
</jsp:include>

<!-- 	관련 MODEL -->
<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
	<jsp:param value="<%= oid %>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert300" name="method" />
	<jsp:param value="MODEL" name="codeType" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="150" name="height" />
	<jsp:param value="true" name="header" />
</jsp:include>