<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 버전 이력 -->
<jsp:include page="/extcore/jsp/mold/include/mold-iteration-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
</jsp:include>

<!-- 결재 이력 -->
<jsp:include page="/extcore/jsp/workspace/include/approval-history.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
</jsp:include>

<!-- 외부 유저 메일 -->
<jsp:include page="/extcore/jsp/workspace/include/approval-mail.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
</jsp:include>

<!-- 다운로드 이력 -->
<jsp:include page="/extcore/jsp/common/include/download-history-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
</jsp:include>