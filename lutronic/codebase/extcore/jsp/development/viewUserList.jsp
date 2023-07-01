<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">
$(document).ready(function() {
})

$(function() {
})

</script>

<body>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">

	<tr>
		<td class="tdblueM"  width="10%">
			${f:getMessage('역할')}
		</td>
		
		<td class="tdblueM"  width="30%">
			${f:getMessage('부서명')}
		</td>
		
		<td class="tdblueM"  width="50%">
			${f:getMessage('이름')}
		</td>
		
		<td class="tdblueM0" width="10%">
			${f:getMessage('직위')}
		</td>
	</tr>
	
	<c:if test="${fn:length(list) != 0 }">
		<c:forEach items="${list }" var="user">
			<tr align="center" bgcolor="#FFFFFF"> 
				
				<td class="tdwhiteM">
					<c:out value="${user.Role }" />
				</td>
				
				<td class="tdwhiteM">
					<c:out value="${user.department }" />
				</td>
				
				<td class="tdwhiteM">
					<c:out value="${user.name }" />
				</td>
				
				<td class="tdwhiteM">
					<c:out value="${user.duty }" />
				</td>
				
			</tr>
		</c:forEach>
	</c:if>
				
</table>

</body>
</html>