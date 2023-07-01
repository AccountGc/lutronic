<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
	var menu = "${menu}";
	$("#"+menu).css('background', '#ECECEC');
	isAdmin();
})

window.gotoMenu = function(a,menu,title) {
	var url = getURLString("help", a, "do") + "?menu=" + menu + "&title="+title;
	//url = url + "?menu=" + menu + "&title="+title;
	document.location = url;
}

<%----------------------------------------------------------
*                      관리자 정보 설정
----------------------------------------------------------%>
window.isAdmin = function() {
	var url	= getURLString("common", "isAdmin", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('검색오류')}";
			alert(msg);
		},

		success:function(data){
			if(data) {
				$("#adminMenu").css('display', '');
			}
		}
	});
}
</script>

<body>

<form>


<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	<tr>
		<td align="center" valign="top">
			<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto" class="">
				<tr>
					<td align="left" valign=top height=42>
						<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
							<tr>
								<td class=""></td>
								<td>
									&nbsp;
									<img src="/Windchill/jsp/portal/images/ars_bt_01.gif" width="11" height="11">&nbsp;<span id ="menuTitle">${f:getMessage('프로젝트')} ${f:getMessage('관리')}</span>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr>
					<td align="left" valign="top" width=100% style="padding-right:2;padding-left:2">
						<table border="0" cellpadding="0" cellspacing="0" width="100%" class=menu>
							<tr bgcolor=ffffff>
							    <td height="25" bgcolor="ffffff"><img src="/Windchill/jsp/portal/img/bt_01.gif" alt="" width="8" height="8" border="0">
							    <b>HELP DESK</b> </td>
							</tr>
							
							<tr>
							    <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
							
							<!-- 메뉴얼 -->
							<tr id="menual" height="20" style="padding-left: 10" bgcolor="white">
							    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','menual','${f:getMessage('메뉴얼')}')">
								    <img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
									${f:getMessage('메뉴얼')}
							    </td>
							</tr>
							
							<tr id="process" height="20" style="padding-left: 10" bgcolor="white">
							    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','process','${f:getMessage('프로세스')}')">
							    	<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
							    	${f:getMessage('프로세스')}
							    </td>
							</tr>
							
							<tr id="etc" height="20" style="padding-left: 10" bgcolor="white">
							    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','etc','${f:getMessage('기타')}')">
							   		<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
							    	${f:getMessage('기타')}
							    </td>
							</tr>
							
							<tr id="workdoc" height="20" style="padding-left: 10" bgcolor="white">
							    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','workdoc','${f:getMessage('업무표준서')}')">
							    	<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
							    	${f:getMessage('업무표준서')}
							    </td>
							</tr>
							
							<tr id="improvement" height="20" style="padding-left: 10" bgcolor="white">
							    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','TEST1','${f:getMessage('단위테스트')}')">
							    	<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
							    	${f:getMessage('단위테스트')}
							    </td>
							</tr>
							<tr id="improvement" height="20" style="padding-left: 10" bgcolor="white">
							    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','TEST2','${f:getMessage('통합테스트')}')">
							    	<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
							    	${f:getMessage('통합테스트')}
							    </td>
							</tr>
							
							<tr>
						    	<td>
						    		<table id="adminMenu" border="0" cellpadding="0" cellspacing="0" width="100%" class="menu" style="display: none;">
							
										<tr>
										    <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
										</tr>
										
										<tr id="tr50" height="20" style="padding-left: 10">
										    <td width="100%" class=menu_1></td>
										</tr>
										
										<tr bgcolor=ffffff>
										    <td height="25" bgcolor="ffffff"><img src="/Windchill/jsp/portal/img/bt_01.gif" alt="" width="8" height="8" border="0">
										    <b>${f:getMessage('관리자')}</b> </td>
										</tr>
										
										<tr>
										    <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
										</tr>
										
										<tr id="admin" height="20" style="padding-left: 10;" bgcolor="white">
										    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','admin' ,'${f:getMessage('관리자 전용')}')">
										    	<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
										    	${f:getMessage('관리자 전용')}
										    </td>
										</tr>
									</table>
								</td>
							</tr>
							
							<tr>
							    <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
							
							<tr id="tr50" height="20" style="padding-left: 10">
							    <td width="100%" class=menu_1></td>
							</tr>
							
							<tr bgcolor=ffffff>
							    <td height="25" bgcolor="ffffff"><img src="/Windchill/jsp/portal/img/bt_01.gif" alt="" width="8" height="8" border="0">
							    <b>SW ${f:getMessage('설치')}</b> </td>
							</tr>
							
							<tr>
							    <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
							
							<tr id="creoView" height="20" style="padding-left: 10" bgcolor="white">
							    <td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','creoView','Creo View ${f:getMessage('설치')}')">
							    	<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
							    	Creo View ${f:getMessage('설치')}
							    </td>
							</tr>
							
							<tr id="wgm" height="20" style="padding-left: 10" bgcolor="white">
						    	<td width="100%" class=menu_1 STYLE="cursor: pointer" onclick="gotoMenu('index','wgm','WorkGroupManager')">
						        	<img src="/Windchill/jsp/portal/img/orange_point.gif" alt="" width="5" height="7" hspace="5">
						        	WorkGroupManager
						        </td>
						    </tr>
						    
						    <tr>
						        <td height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
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