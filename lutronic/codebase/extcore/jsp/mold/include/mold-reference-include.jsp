<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 관련 품목 -->
<jsp:include page="/extcore/jsp/part/include_viewPart.jsp" flush="false" >
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="관련 품목" name="title" />
	<jsp:param value="doc" name="moduleType"/>
</jsp:include>

<!-- 관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="true" name="multi" />
</jsp:include>
