<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><tiles:insertAttribute name="title" ignore="true" /></title>
<link rel="stylesheet" href="/Windchill/extcore/css/layout.css">
<link rel="stylesheet" href="/Windchill/extcore/css/bootstrap.css">

<link rel="stylesheet" href="/Windchill/extcore/css/fonts/font-awesome.css">
<link rel="stylesheet" href="/Windchill/extcore/css/jquery.gritter.css">
<link rel="stylesheet" href="/Windchill/extcore/css/animate.css">
<link rel="stylesheet" href="/Windchill/extcore/component/ax5ui-mask/dist/ax5mask.css">


<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
</head>


<body>
	<div>
		<tiles:insertAttribute name="body" />
	</div>

<DIV id="lodingDIV" class='loading_edge' style='display: none;'>
	<img src="/Windchill/extcore/jsp/portal/images/loading.gif" />
</DIV>

<form name='hiddenForm' id='hiddenForm'>
</form>

</body>


</html>


