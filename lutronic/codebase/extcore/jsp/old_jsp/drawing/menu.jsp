<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	var menu = $("#menu").val();
	
	$("#"+menu).css('background', '#ECECEC');
});

<%----------------------------------------------------------
*                      폴더 선택시 데이터 설정
----------------------------------------------------------%>
function setLocationDocument(foid,loc,isLast){
	$("#locationName").html(loc);
	$("#location").val(loc);
	$("#fid").val(foid);
	if($("#search").val() == "true") {
		$("#sortValue").val("");
		$("#sortCheck").val("");
		$("#sessionId").val("");
		$("#page").val(1);
		lfn_Search();
	}
}

<%----------------------------------------------------------
*                      메뉴 이동
----------------------------------------------------------%>
function gotoMenu(a){
	var url = getURLString("drawing", a, "do");
	document.location = url;
}
</script>
<body>

<form name=menuForm method=get>
<input type="hidden" name="menu" id="menu" value="<c:out value="${menu }" />">

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	<tr>
		<td align="center" valign="top">
			<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto" class="">
				<tr>
					<td align="left" valign=top height=42>
						<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
							<tr>
								<td class=" "></td>
								<td>
									<img src="/Windchill/jsp/portal/img/ars_bt_01.gif" width="11" height="11">
									<span id ="menuTitle">
										${f:getMessage('도면')}
										${f:getMessage('관리')}
									</span>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr>
					<td align="left" valign="top" width=100% style="padding-right:2;padding-left:2">
						<table border="0" cellpadding="0" cellspacing="0" width="100%" class=menu>
										
							<tr id="menu1" height="20" style="padding-left:10" bgcolor="white">
								<td width="100%" class=menu_1  STYLE="cursor:pointer" onclick="javascript:gotoMenu('listDrawing')">
									<img src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">
									${f:getMessage('도면')}
									${f:getMessage('검색')}
								</td>
							</tr>
							
							<tr id="menu2" height="20" style="padding-left:10" bgcolor="white">
								<td width="100%" class=menu_1  STYLE="cursor:pointer" onclick="javascript:gotoMenu('createDrawing')">
									<img src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">
									${f:getMessage('도면')}
									${f:getMessage('등록')}
								</td>
							</tr>
							<tr id="menu3" height="20" style="padding-left:10" bgcolor="white">
								<td width="100%" class=menu_1  STYLE="cursor:pointer" onclick="javascript:gotoMenu('createPackageDrawing')">
									<img src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">
									${f:getMessage('주 도면')}
									${f:getMessage('일괄등록')}
								</td>
							</tr>
						
							<tr>
								<td><hr></td>
							</tr>
						</table>
						<div style="width:100%;overflow-x:hidden;overflow-y:auto;border:0px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:0px 0px;">
						<div id="scrollBox" style="OVERFLOW-Y:auto;OVERFLOW-X:auto; width:240; height:510" onscroll="true">
						<table width="100%" height="40" border="0" cellpadding="0" cellspacing="0" bgcolor="ffffff" style="margin:0px 0px 10px 0px">
							<tr>
								<td >
									<jsp:include page="/eSolution/folder/treeFolder.do">
										<jsp:param value="/Default/PART_Drawing" name="folder"/>
									</jsp:include>
								</td>
							</tr>
						</table>
						</div>
						</div>
					</td>
				</tr>
			
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>