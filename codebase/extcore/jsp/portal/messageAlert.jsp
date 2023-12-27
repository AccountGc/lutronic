<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"> 
<title>Redirect</title>

<script type="text/javascript" src="/Windchill/jsp/js/jquery/jquery-1.11.1.min.js" ></script>
<script type="text/javascript" src="/Windchill/jsp/js/jquery/jquery.json-2.4.min.js" ></script>

<script>
function gotoPage(){
	<c:if test="${message!=null}">alert("${message}");</c:if>	
}
</script>
</head>
<body onload="gotoPage()">
</body>
</html>
