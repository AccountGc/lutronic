<!DOCTYPE html>
<html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<head>

<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<!-- 각 화면 개발시 Tag 복사해서 붙여넣고 사용할것 -->
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/js/dhtmlx/dhtmlx.css" />
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/default.css" type="text/css">
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/e3ps.css" type="text/css">
<link rel="stylesheet" href="/Windchill/extcore/jsp/css/css.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/css/dtree.css" />
<!-- AUIGrid -->
<link rel="stylesheet" href="/Windchill/extcore/AUIGrid/AUIGrid_style.css" type="text/css">

<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/jquery-1.11.1.min.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/dhtmlx/dhtmlx.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/dhtmlx/dhtmlxPaging.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/jquery.json-2.4.min.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/jquery/pageGrid.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/common.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/popup.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/jsp/js/dtree.js"></script>

<!-- AUIGrid -->
<script type="text/javascript" src="/Windchill/extcore/AUIGrid/AUIGrid.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/AUIGrid/AUIGridLicense.js" ></script>
<script type="text/javascript" src="/Windchill/extcore/AUIGrid/AUIGrid_paging.js" ></script>
<script>

$(document).ready(function() {
	<%----------------------------------------------------------
	*                    textarea  자동 조절
	----------------------------------------------------------%>
	$('.textarea_autoSize').on( 'keyup', 'textarea', function (e){
	$(this).css('height', 'auto' );
	$(this).height( this.scrollHeight );
	});
	$('.textarea_autoSize').find( 'textarea' ).keyup();
	
})

$(window).resize(function() {
	
	var width = "";
	width = $(window).width()-10;
	var height = $(window).height()-20;
	
	if (window.myGridID){
		AUIGrid.resize(window.myGridID, width);
	}
	
	if (window.myGridID2){
		AUIGrid.resize(window.myGridID2, width);
	}
	if (window.myBOMGridID){
		AUIGrid.resize(window.myBOMGridID, width,height-100);
	}
	
})
</script>
<title>
	<tiles:insertAttribute name="title" ignore="true" />
</title>
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