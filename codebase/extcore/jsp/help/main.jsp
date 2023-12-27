<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=10">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	var url = "<c:out value='${url}' escapeXml='false'/>";
	initSwitch();
	$("#iframe").attr("src", url);
})
</script>


<body>
<iframe id="iframe" width="100%" height="100%" frameborder="0" padding="0" ></iframe>

</body>
</html>