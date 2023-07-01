<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<html>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<!-- 각 화면 개발시 Tag 복사해서 붙여넣고 사용할것 -->
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<link rel="stylesheet" type="text/css" href="/Windchill/jsp/js/dhtmlx/dhtmlx.css" />
<link rel="stylesheet" href="/Windchill/jsp/css/default.css" type="text/css">
<link rel="stylesheet" href="/Windchill/jsp/css/css.css" type="text/css">
<link rel="stylesheet" href="/Windchill/jsp/css/e3ps.css" type="text/css">

<script type="text/javascript" src="/Windchill/jsp/js/jquery/jquery-1.11.1.min.js" ></script>
<script type="text/javascript" src="/Windchill/dhtmlx/dhtmlx.js" ></script>
<script type="text/javascript" src="/Windchill/dhtmlx/dhtmlxPaging.js" ></script>
<script type="text/javascript" src="/Windchill/jsp/js/jquery/jquery.json-2.4.min.js" ></script>
<script type="text/javascript" src="/Windchill/jsp/js/jquery/pageGrid.js" ></script>
<script type="text/javascript" src="/Windchill/jsp/js/common.js" ></script>
<script type="text/javascript" src="/Windchill/jsp/js/popup.js" ></script>
<script type="text/javascript" src="/Windchill/jsp/js/script.js" ></script>
<script type="text/javascript" src="/Windchill/jsp/js/dtree.js"></script>

<title>
	LUTRONIC PDM
</title>

<style>
#div_root{
width:100%;
}

#div_top{
width:100%;
height:90%
}

#div_body{
width:100%;
margin-left: 2px;
margin-right: 2px;
margin-top : 2px;
border: 1px;
}

#div_menu{
width:15%;
margin: 2px;
float:left;
}

#div_scroll{
width:8px;
float:left;
}

#div_main{
width:80%;
float:left;
margin: 2px;
background-size:100%;
}

.Sub_Right_LT { background:url(/Windchill/jsp/portal/images/base_design/Sub_Right_LT.gif); background-repeat:no-repeat; width:19px; height:29px;}
.Sub_Right_LBG { background:url(/Windchill/jsp/portal/images/base_design/Sub_Right_LBG.gif); background-repeat:repeat-y; vertical-align:top; padding-top:20px;}
.Sub_Right_LB { background:url(/Windchill/jsp/portal/images/base_design/Sub_Right_LB.gif); background-repeat:no-repeat; width:19px; height:4px;}

</style>

<table width="100%" valign=top cellpadding="0" cellspacing="0">
	<tr valign=top>
		<td valign=top>
			<tiles:insertAttribute name="header" />
		</td>
	</tr>
</table>

<table width="100%" valign=top>
	<tr valign=top>
		<td valign=top>
			<table cellpadding="0" cellspacing="0" border=0 height=100% width="100%"  >
				<tr>
					<td class="TLeft_BG"></td>
					<td class="Top_BG"></td>
					<td class="TRight_BG"></td>
				</tr>
			
				<tr>
					<td class="Left_BG"></td>
					<td width="99%" style="padding:3; vertical-align:top">
						<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
							<tr>
								<td valign=top width=100%>
									<tiles:insertAttribute name="body" />
								</td>
							</tr>
						</table>
					</td>
					<td class="Right_BG"></td>
					<td width=10px>&nbsp;&nbsp;</td>
					<td valign=top ></td>
				</tr>
				
				<tr>
					<td class="Left_BG"></td>
					<td align=right ></td>
					<td class="Right_BG"></td>
				</tr>

				<tr>
					<td class="BottomL_BG"></td>
					<td class="Bottom_BG"></td>
					<td class="BottomR_BG"></td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<DIV id="lodingDIV" class='loading_edge' style='display: none;'>
	<img src="/Windchill/jsp/portal/images/loading.gif" />
</DIV>

</html>