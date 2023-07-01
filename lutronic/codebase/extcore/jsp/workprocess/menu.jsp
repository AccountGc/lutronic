<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	var menu = $("#menu").val();
	$("#"+menu).css('background', '#ECECEC');
	isAdmin();
});

<%----------------------------------------------------------
*                      관리자 정보 설정
----------------------------------------------------------%>
function isAdmin() {
	var url	= getURLString("common", "isAdmin", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			if(data) {
				$("#adminMenu").css('display', '');
			}
		}
	});
}

window.gotoMenu = function(a,b) {
	var url = getURLString("groupware", a, b);
	document.location = url;
}


function gotoBatchMenu(a){
	var url = getURLString("asmApproval", a, "do");
	document.location = url;
}
</script>


<body>

<form name=menuForm method=get>

<input type="hidden" name="menu" id="menu" value="<c:out value="${menu }" />">
<!-- NEW Start -->


<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	<tr>
		<td align="left" valign="top">
			<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
				<tr>
					<td  valign=top height=42>
						<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
							<tr>
								<td></td>
								<td>&nbsp;<img src="/Windchill/jsp/portal/images/ars_bt_01.gif" width="11" height="11">&nbsp;<span id ="menuTitle">${f:getMessage('나의업무')}</span></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td valign="top" width=100% style="padding-right:2;padding-left:2">
						<table border="0" cellpadding="0" cellspacing="0" width="100%" class="menu">
						    <tr id="menu1" height="20" style="padding-left: 10"    bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listNotice','do')" >
						        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('공지사항')}
						        </td>
						    </tr>
						    
							<tr>
						        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
						    </tr>
						    
						    <tr bgcolor=ffffff>
						        <td height="25" bgcolor="ffffff">
						        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
						        	<b>${f:getMessage('작업공간')}</b>
						        </td>
						    </tr>
						    
						    <tr>
						        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
						    </tr>
						    
						    <!-- 작업함 -->
						    <tr id="menu3" height="20" style="padding-left: 10"   bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listWorkItem','do')">
						        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('작업함')}
						        </td>
						    </tr>
						    
							<!-- 진행함 -->
							<tr id="menu4" height="20" style="padding-left: 10"    bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listItem','do?state=ing')">
						        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('진행함')}
						        </td>
						    </tr>
						    
							<!-- 완료함 -->
						    <tr id="menu5" height="20" style="padding-left: 10"    bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listItem','do?state=complete','${f:getMessage('나의업무')} > ${f:getMessage('완료함')} ','menu5');">
						        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('완료함')} 
						        </td>
						    </tr>
						    
							<!-- 수신함 -->
						    <tr id="menu6" height="20" style="padding-left: 10" bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('listItem','do?state=receive','${f:getMessage('나의업무')} > ${f:getMessage('수신함')}','menu6');">
						        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('수신함')} 
						        </td>
						    </tr>
						    
						    <!-- 일괄결재 -->
						    <tr>
						    	<td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
						    </tr>
						    
						    <tr bgcolor=ffffff>
						        <td height="25" bgcolor="ffffff">
						        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
						        	<b>${f:getMessage('일괄결재')}</b>
						        </td>
						    </tr>
						    <tr>
						    	<td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
						    </tr>
						    <!-- 일괄결재 -->
						    <tr id="menu15" height="20" style="padding-left: 10" bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoBatchMenu('listAsm');">
						        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('일괄결재 검색')} 
						        </td>
						    </tr>
						    
						    <!-- 일반메뉴 -->
						    <tr>
						    	<td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
						    </tr>
						    
						    <tr bgcolor=ffffff>
						        <td height="25" bgcolor="ffffff">
						        	<img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
						        	<b>${f:getMessage('일반메뉴')}</b>
						        </td>
						    </tr>
						    
						    <tr>
						    	<td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
						    </tr>
						    
						    <!-- 패스워드 변경 -->
						    <tr id="menu11" height="20" style="padding-left: 10"    bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('changePassword','do?isPop=false');">
						        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7" hspace="5">${f:getMessage('비밀번호 변경')}
						        </td>
						    </tr>
						    
						    <tr id="menu12" height="20" style="padding-left: 10"    bgcolor="white">
						        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('companyTree','do');"><img
						            src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7"    hspace="5">${f:getMessage('조직도')}</td>
						    </tr>
						    
						    <!-- 관리자메뉴 -->
						    <tr>
						    	<td>
						    		<table id="adminMenu" border="0" cellpadding="0" cellspacing="0" width="100%" class="menu" style="display: none;">
									    <tr>
									    	<td height="1"     background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
									    </tr>
									    
									    <tr bgcolor=ffffff>
									        <td height="25" bgcolor="ffffff"><img src="/Windchill/jsp/portal/images/bt_01.gif" alt="" width="8" height="8" border="0">
									        <b>${f:getMessage('관리자메뉴')}</b></td>
									    </tr>
									    
									    <tr>
									    	<td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
									    </tr>
									    
										<tr>
									    </tr>
									    
									    <tr id="menu10" height="20" style="padding-left: 10" bgcolor="white">
									        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('wfProcessInfo','do');">
									        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7"    hspace="5">
									        		${f:getMessage('관리자메뉴')}
									        </td>
									    </tr>
									    
									    <tr id="menu13" height="20" style="padding-left: 10"    bgcolor="white">
									        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('changeIBA','do');">
									        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7"    hspace="5">
									        	${f:getMessage('속성값 변경')}
									        </td>
									    </tr>
									    
									    <tr id="menu14" height="20" style="padding-left: 10"    bgcolor="white">
									        <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="javascript:gotoMenu('multiPublishing','do');">
									        	<img src="/Windchill/jsp/portal/images/orange_point.gif" alt="" width="5" height="7"    hspace="5">
									        	${f:getMessage('도면 재변환')}
									        </td>
									    </tr>
							   		</table>
							  	</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
</body>
</html>