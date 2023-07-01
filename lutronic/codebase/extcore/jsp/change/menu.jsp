<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	var menu = $("#menu").val();
	
	$("#"+menu).css('background', '#ECECEC');
});

<%----------------------------------------------------------
*                      메뉴 이동
----------------------------------------------------------%>
function gotoMenu(a,b){
	var url = getURLString(a, b, "do");
	document.location = url;
}
</script>
<body>

<form name="menuForm" method="post" action="">

<input type="hidden" name="menu" id="menu" value="<c:out value="${menu }" />">

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="100%">
	<tr>
		<td align="center" valign="top">
			<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto" >
				<tr>
					<td align="left" valign=top height=42>
						<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
							<tr>
								<td class="Subinfo_img"></td>
								<td>&nbsp;<img src="/Windchill/jsp/portal/img/ars_bt_01.gif" width="11" height="11">&nbsp;<span id ="menuTitle">${f:getMessage('설계변경')}</span></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td align="left" valign="top" width=100% style="padding-right:2;padding-left:2">
						<table border="0" cellpadding="0" cellspacing="0" width="100%" class=menu>
						
							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
					
							<tr height="25">
								<td width="20" ><div align="center"><img src="/Windchill/jsp/portal/img/bt_01.gif" alt="" width="8" height="8"></div></td>
								<td><b>ECO ${f:getMessage('관리')} &nbsp;</b></td>
							</tr>

							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>

							<tr id="menu3" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('changeECO','listECO')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">ECO ${f:getMessage('검색')}</td>
							</tr>
							
							<tr id="menu4" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('changeECO','createECO')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">ECO ${f:getMessage('등록')}</td>
							</tr>
							
							<tr height="10">
								<td width="20" colspan="2"></td>
							</tr>
						
							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
							
							<tr height="25">
								<td width="20" ><div align="center"><img src="/Windchill/jsp/portal/img/bt_01.gif" alt="" width="8" height="8"></div></td>
								<td><b>CR/ECPR ${f:getMessage('관리')} &nbsp;</b></td>
							</tr>
							 
							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
							
							<tr id="menu1" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('changeECR','listECR')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">CR/ECPR ${f:getMessage('검색')}</td>
							</tr>

							<tr id="menu2" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('changeECR','createECR')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">CR/ECPR ${f:getMessage('등록')}</td>
							</tr>

							<tr height="10">
								<td width="20" colspan="2"></td>
							</tr>

							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
					
							<tr height="25">
								<td width="20" ><div align="center"><img src="/Windchill/jsp/portal/img/bt_01.gif" alt="" width="8" height="8"></div></td>
								<td><b>EO ${f:getMessage('관리')} &nbsp;</b></td>
							</tr>

							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>

							<tr id="menu5" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('changeECO','listEO')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">EO ${f:getMessage('검색')}</td>
							</tr>
							<tr id="menu6" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('changeECO','createEO')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">EO ${f:getMessage('등록')}</td>
							</tr>
							<tr height="10">
								<td width="20" colspan="2"></td>
							</tr>
							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
							<%if(CommonUtil.isAdmin()) {%>
							<tr height="25">
								<td width="20" ><div align="center"><img src="/Windchill/jsp/portal/img/bt_01.gif" alt="" width="8" height="8"></div></td>
								<td><b>ERP ${f:getMessage('전송')}&nbsp;${f:getMessage('현황')}&nbsp;</b></td>
							</tr>

							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>

							<tr id="menu7" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('erp','listPARTERP')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">PART ${f:getMessage('전송')}&nbsp;${f:getMessage('현황')}&nbsp;</td>
							</tr>
							<tr id="menu8" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('erp','listECOERP')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">EO&ECO ${f:getMessage('전송')}&nbsp;${f:getMessage('현황')}&nbsp;</td>
							</tr>
							<tr id="menu9" height="20" style="padding-left:10" bgcolor="white">
								<td colspan="2" class=menu_1  style="cursor:pointer" onclick="javascript:gotoMenu('erp','listBOMERP')">
								<img  src="/Windchill/jsp/portal/images/icon/arrow_02.gif" border="0" hspace="5">BOM ${f:getMessage('전송')}&nbsp;${f:getMessage('현황')}&nbsp;</td>
							</tr>
							
							<tr>
								<td colspan="2" height="1" background="/Windchill/jsp/portal/images/icon/dot_dash4.gif"></td>
							</tr>
							<%} %>
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