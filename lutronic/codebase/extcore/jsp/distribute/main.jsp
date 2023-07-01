<%@page import="wt.session.SessionHelper"%>
<%@page import="java.rmi.server.RemoteServer"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.e3ps.common.service.CommonHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
	checkPopUP();
})
<%----------------------------------------------------------
*                      더보기 선택시
----------------------------------------------------------%>
function gotoMoreMenu(select) {
	var location = "";
	if( select == 1 ) {
		location = getURLString("distribute", "listNotice", "do");
	} else if ( select == 2) {
		location = getURLString("distribute", "listWorkItem", "do");
	} 
	document.location = location;
		
}

<%----------------------------------------------------------
*                      공지사항 상세보기
----------------------------------------------------------%>
function gotoNotice(oid) {
	document.location = getURLString("distribute", "viewNotice", "do") + "?oid="+oid;
}

<%----------------------------------------------------------
*                      결재 상세보기
----------------------------------------------------------%>
function gotoWorkItem(url, oid, viewOid){
	var params = url.split("&");
    var urls = params[params.length - 1].split("=");
    var url = getURLString("distribute", "approval", "do") + "?action=" + urls[urls.length - 1] + "&workoid=" + oid + "&pboOid=" + viewOid;
    location.href = url;
}
</script>

<body>
<form>
<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp; ${f:getMessage('메인포탈')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="699" border="0" cellspacing="0" cellpadding="0">
	<tr height=20>
		<td></td>
	</tr>
	
	<tr>
		<td>
			<!-- 공지 사항 -->
			<jsp:include page="/eSolution/common/include_Notice.do" flush="true"/>
			
		</td>
		
		<td>
			<!-- 결재&작업 -->
			<jsp:include page="/eSolution/common/include_Approve.do" flush="true" />
		</td>
	</tr>
	
	
</table>

</form>

</body>
</html>