<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>


<table>
	<tr>
			<c:forEach items="${list }" var="list">
		<td class="tdblueM">
				<c:out value="${list }" />
		</td>
			</c:forEach>
	</tr>
</table>