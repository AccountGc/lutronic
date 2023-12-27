<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page session="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><c:set var="loginFailed" scope="request" value="true" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
</head>
<body onload="_page();">
	<form>
		<script type="text/javascript">
			function _page() {
				alert("아이디 또는 패스워드를 확인해주세요.");
				document.location.href = "/Windchill/plm/index";
			}
		</script>
	</form>
</body>
</html>