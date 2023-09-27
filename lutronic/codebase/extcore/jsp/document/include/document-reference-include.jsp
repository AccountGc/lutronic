<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 관련 품목 -->
<jsp:include page="/extcore/jsp/part/part-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert91" name="method" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="200" name="height" />
</jsp:include>

<!-- 	관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert90" name="method" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="200" name="height" />
</jsp:include>

<!-- 	관련 EO -->
<jsp:include page="/extcore/jsp/change/include/eo-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert100" name="method" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="200" name="height" />
</jsp:include>

<!-- 	관련 CR -->
<jsp:include page="/extcore/jsp/change/include/cr-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert101" name="method" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="200" name="height" />
</jsp:include>

<!-- 	관련 ECO -->
<jsp:include page="/extcore/jsp/change/include/eco-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert102" name="method" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="200" name="height" />
</jsp:include>

<!-- 	관련 ECPR -->
<jsp:include page="/extcore/jsp/change/include/ecpr-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="insert103" name="method" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="200" name="height" />
</jsp:include>