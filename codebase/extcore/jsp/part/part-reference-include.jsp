<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
%>
<!-- 관련 도면 -->
<%-- 		<jsp:include page="/extcore/jsp/drawing/drawingView_include.jsp"> --%>
<%-- 			<jsp:param value="part" name="moduleType"/> --%>
<%-- 			<jsp:param value="<%=data.getOid() %>" name="oid"/> --%>
<%-- 			<jsp:param value="관련 도면" name="title"/> --%>
<%-- 			<jsp:param value="epmOid" name="paramName"/> --%>
<%-- 		</jsp:include> --%>

<!-- 	관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
</jsp:include>

<!-- 관련 물질 -->
<jsp:include page="/extcore/jsp/rohs/include/rohs-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
</jsp:include>

<!-- 관련 EO -->
<%-- <jsp:include page="/extcore/jsp/change/eo/include/eo-include.jsp"> --%>
<%-- 	<jsp:param value="<%=oid%>" name="oid" /> --%>
<%-- 	<jsp:param value="view" name="mode" /> --%>
<%-- </jsp:include> --%>

<!-- 관련 ECO -->
<jsp:include page="/extcore/jsp/change/eco/include/eco-include.jsp">
	<jsp:param value="<%=oid%>" name="oid" />
	<jsp:param value="view" name="mode" />
</jsp:include>