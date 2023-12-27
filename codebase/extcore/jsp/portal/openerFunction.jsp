<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Close</title>
<script type="text/javascript">
function gotoPage(){
	
	<c:if test="${message!=null}">
	alert("${message}");
	</c:if>

	<c:choose>
		<c:when test="${functionName != null }">
			opener.parent.${functionName}(${list});
		</c:when>
		
		<c:otherwise>
			opener.parent.reload();
		</c:otherwise>

	</c:choose>

	self.close();
}
</script>
</head>
<body onload="javascript:gotoPage()">


</body>
</html>
