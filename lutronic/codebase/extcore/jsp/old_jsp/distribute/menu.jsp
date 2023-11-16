<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<html>
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	var menu = $("#menu").val();
	
	$("#"+menu).css('background', '#ECECEC');
	
	$("#divMenu").click(function() {
		if ( $( "#distiributeMenuTable" ).is( ":hidden" ) ) {
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_03_s.gif");
		}else{
			$(this).attr("src","/Windchill/jsp/portal/images/Blue_02_s.gif");
		}
		$("#distiributeMenuTable").slideToggle();
	})

});

<%----------------------------------------------------------
*                      페이지 이동
----------------------------------------------------------%>
function gotoMenu(a, createType){
	var url = getURLString("distribute", a, createType);
	document.location = url;
}

<%----------------------------------------------------------
*                      폴더 선택시
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



</script>



<body>
<form name="menuForm" method="post" action="">
<input type="hidden" name="menu" id="menu" value="<c:out value="${menu }" />">
<input type="hidden" name="folderType" id="folderType" value="<c:out value="${folderType }" />">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	
		<tr>
			<td align="center" valign="top">
				<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
					<tr>
						<td align="left" valign=top height=42>
							<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
								<tr>
									<td></td>
									<td>
										<img src="/Windchill/jsp/portal/img/ars_bt_01.gif" width="11" height="11">
										<span id ="menuTitle">
											${f:getMessage('배포')}
											${f:getMessage('관리')}
										</span>
									</td>
									<td align="right">
										<img src="/Windchill/jsp/portal/images/Blue_03_s.gif" style="height: 12px; cursor: pointer;" id="divMenu" >
									</td>
									
								</tr>
							</table>
						</td>
					</tr>
					
					<tr>
						<td align="left" valign="top" width=100% style="padding-right:2;padding-left:2">
						<div id="distiributeMenuTable">
							<table border="0" cellpadding="0" cellspacing="0" width="100%" class=menu >
								<tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
								<tr bgcolor=ffffff>
							        <td height="25" bgcolor="ffffff">
							        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
							        	<b>나의 업무</b>
							        </td>
							    </tr>
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    <!-- 작업함 -->
							    <tr id="menu1" height="20" style="padding-left: 10"   bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listWorkItem','do')">
							        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('작업함')}
							        </td>
							    </tr>
							    
								<!-- 수신함 -->
							    <tr id="menu2" height="20" style="padding-left: 10" bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listItem','do?state=receive','나의업무 > 수신함','menu6');">
							        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">수신함 
							        </td>
							    </tr>
								
								 <!-- 패스워드 변경 -->
							    <tr id="menu3" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('changePassword','do?isPop=false');">
							        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">비밀번호 변경
							        </td>
							    </tr>
							    
							    <tr id="menu4" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('companyTree','do');"><img
							            src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7"    hspace="5">조직도</td>
							    </tr>
							   <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    <tr bgcolor=ffffff>
							        <td height="25" bgcolor="ffffff">
							        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
							        	<b>품목 검색</b>
							        </td>
							    </tr>
							   
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							      <!-- 품목 검색 -->
							    <tr id="menu11" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listPart','do');">
							        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">품목 검색
							        </td>
							    </tr>
							    <!-- 완제품 검색 -->
							    <tr id="menu12" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listProduction','do');"><img
							            src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7"    hspace="5">완제품 검색</td>
							    </tr>
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    <tr bgcolor=ffffff>
							        <td height="25" bgcolor="ffffff">
							        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
							        	<b>설계 변경</b>
							        </td>
							    </tr>
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    <!-- EO 검색 -->
							    <tr id="menu21" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listEO','do');">
							        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">EO 검색
							        </td>
							    </tr>
							    <!-- ECO 검색 -->
							    <tr id="menu22" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listECO','do');"><img
							            src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7"    hspace="5">ECO 검색</td>
							    </tr>
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    <tr bgcolor=ffffff>
							        <td height="25" bgcolor="ffffff">
							        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
							        	<b>문서 검색</b>
							        </td>
							    </tr>
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    <tr id="menu31" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listDocument','do??folderType=DOC');">
							        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">문서 검색
							        </td>
							    </tr>
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    <tr bgcolor=ffffff>
							        <td height="25" bgcolor="ffffff">
							        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
							        	<b>금형 검색</b>
							        </td>
							    </tr>
							    <tr>
							        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							    </tr>
							    
							    <tr id="menu41" height="20" style="padding-left: 10"    bgcolor="white">
							        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listMold','do');">
							        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">금형 검색
							        </td>
							    </tr>
							    <tr>
									<td colspan="2"><hr></td>
								</tr>
							</table>
							</div>
							<c:choose>
								<c:when test="${folderType eq 'PART'}">
								<div style="width:100%;overflow-x:hidden;overflow-y:auto;border:0px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:0px 0px;">
									<div id="scrollBox" style="OVERFLOW-Y:auto;OVERFLOW-X:auto; width:240; height:210" onscroll="true">
									    <jsp:include page="/eSolution/folder/treeFolder.do">
											<jsp:param value="/Default/PART_Drawing" name="folder"/>
										</jsp:include>
									</div>
								</div>
								</c:when>
								<c:when test="${folderType eq 'DOC'}">
								<div style="width:100%;overflow-x:hidden;overflow-y:auto;border:0px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:0px 0px;">
									<div id="scrollBox" style="OVERFLOW-Y:auto;OVERFLOW-X:auto; width:240; height:110" onscroll="true">
									    <jsp:include page="/eSolution/folder/treeFolder.do">
											<jsp:param value="/Default/Document" name="folder"/>
										</jsp:include>
									</div>
								</div>
								</c:when>
							</c:choose>
						</td>
					</tr>
					
				</table>
			</td>
		</tr>
	</table>
</form>
</body>
</html>