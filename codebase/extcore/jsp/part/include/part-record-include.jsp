<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 버전 이력 -->
<jsp:include page="/extcore/jsp/part/include/part-iteration-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
</jsp:include>

<!-- 다운로드 이력 -->
<jsp:include page="/extcore/jsp/common/include/download-history-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
</jsp:include>