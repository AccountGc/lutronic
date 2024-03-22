<%@page import="com.e3ps.common.history.service.LoginHistoryHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String j_username = request.getParameter("j_username");
String j_password = request.getParameter("j_password");
LoginHistoryHelper.service.create(j_username, request);
// response.sendRedirect("j_security_check");
%>
<form method="post" action="j_security_check">
	<input type="hidden" name="j_username" value="<%=j_username %>">
	<input type="hidden" name="j_password" value="<%=j_password %>">
</form>
<script type="text/javascript">
	document.forms[0].submit();
</script>