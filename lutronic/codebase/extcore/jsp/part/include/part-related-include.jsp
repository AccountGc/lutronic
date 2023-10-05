<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 상위 품목 -->
<jsp:include page="/extcore/jsp/part/include/part-up-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="300" name="height" />
</jsp:include>

<!-- 하위 품목 -->
<jsp:include page="/extcore/jsp/part/include/part-down-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="300" name="height" />
</jsp:include>

<!-- END ITEM -->
<jsp:include page="/extcore/jsp/part/include/part-end-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="300" name="height" />
</jsp:include>