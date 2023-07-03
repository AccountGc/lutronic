<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">
$(document).ready(function() {
	divPageLoad("TaskInfo", "viewTask", $("#taskOid").val());
	divPageLoad("activeDiv", "viewActiveList", $('#taskOid').val());
})

</script>

<body>

<input type="hidden" name="taskOid" id="taskOid" value="<c:out value="${oid }"/>" />

<!-- Task 보기 -->
<div id="TaskInfo">
</div>

<!-- Active 보기 -->
<div id="activeDiv">
</div>

</body>
</html>