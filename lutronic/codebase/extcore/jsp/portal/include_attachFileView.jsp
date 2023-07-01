<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<c:if test="${fn:length(list) ne 0 }">

	<c:forEach items="${list }" var="list" varStatus="i" >
		<a href="<c:out value='${list.url }' />">
			<c:out value="${list.name }" />
		</a>
		
		<c:if test="${!i.last}">
			<br>
		</c:if>
		
	</c:forEach>

</c:if>