<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/e3ps.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<script type="text/javascript">
function loginFail() {
	alert("아이디 또는 비밀번호를 확인 해주세요.");
	var url = "/Windchill/eSolution/groupware/main";
	document.location.replace(url);
}
</script>
</head>
<body onload="loginFail();">

</body>
</html>