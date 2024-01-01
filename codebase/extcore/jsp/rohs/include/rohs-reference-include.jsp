<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 관련 품목 -->
<jsp:include page="/extcore/jsp/part/include/part-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="false" name="header" />
</jsp:include>


<!-- 관련 대표 물질 -->
<jsp:include page="/extcore/jsp/rohs/include_viewRohs.jsp" flush="false">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="represent" name="roleType"/>
	<jsp:param value="관련 대표 물질" name="title"/>
</jsp:include>

<!-- 관련 물질 -->
<jsp:include page="/extcore/jsp/rohs/include_viewRohs.jsp" flush="false">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
	<jsp:param value="composition" name="roleType"/>
	<jsp:param value="관련 물질" name="title"/>
</jsp:include>

