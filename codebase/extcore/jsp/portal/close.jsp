<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"> 
<title>Close</title>
<script>
function gotoPage(){
	<c:if test="${message!=null}">alert("${message}");</c:if>	
	self.close();
}
</script>
</head>
<body onload="gotoPage()">
</body>
</html>
