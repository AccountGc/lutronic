<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function() {
})

$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#update").click(function() {
		document.location = getURLString("groupware", "updateNotice", "do") + "?oid="+$("#oid").val();
	}),
	<%----------------------------------------------------------
	*                      삭제 버튼
	----------------------------------------------------------%>
	$("#delete").click(function () {
		if (!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			return;
		}
		var form = $("form[name=viewNotice]").serialize();
		var url	= getURLString("groupware", "deleteNoticeAction", "do");
		$.ajax({
			type:"POST",
			url: url,
			data:form,
			dataType:"json",
			async: true,
			cache: false,
			error: function(data) {
				alert("${f:getMessage('삭제 오류 발생')}");
			},
			success:function(data){
				alert(data);
				document.location = getURLString("groupware", "listNotice", "do");
			}
		});
	})
})

</script>

<body>

<form name="viewNotice" id="viewNotice" method="post" >

<input type="hidden" name="oid" id="oid" value="<c:out value="${noticeData.oid }" />" >

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('공지사항')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="10" cellspacing="10" > <!--//여백 테이블-->
	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
				<tr><td height=1 width=100%></td>
			</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0"  align=center >
			<col width='10%' width='40%'><col><col width='10%'><col width='40%'>

				<tr >
					<TD class="tdblueM" >&nbsp;${f:getMessage('제목')}</TD>
					<TD class="tdwhiteL" colspan="3">
						<c:out value="${noticeData.title }" />
					</TD>
				</TR>
				<tr >
					<TD class="tdblueM">&nbsp;${f:getMessage('등록자')}</TD>
					<TD class="tdwhiteL">
						<c:out value="${noticeData.creator }" />
					</TD>
					<TD class="tdblueM">&nbsp;${f:getMessage('등록일')}</TD>
					<TD class="tdwhiteL">
						<c:out value="${noticeData.createDate }" />
					</TD>
				</TR>
				<tr >
					<TD class="tdblueM">&nbsp;${f:getMessage('조회수')}</TD>
					<TD class="tdwhiteL">
						<c:out value="${noticeData.count }" />
					</TD>
					<TD class="tdblueM">&nbsp;${f:getMessage('팝업')}</TD>
					<TD class="tdwhiteL">
						<c:out value="${noticeData.isPopup() }" />
					</TD>
				</TR>
				<tr >
					<TD class="tdblueM">&nbsp;${f:getMessage('내용')}</TD>
					<TD class="tdwhiteL" style="height:200px" colspan=3 valign=top height=200px>
						<c:out value="${noticeData.vContents }" escapeXml="false" />
					</TD>
				</TR>
				<tr >
					<td class="tdblueM">&nbsp;${f:getMessage('첨부파일')}</td>					
					<td class="tdwhiteL" colspan="3" >
						<jsp:include page="/eSolution/content/includeAttachFileView">
							<jsp:param value="${noticeData.oid }" name="oid"/>
						</jsp:include>
					</td>
				</tr>				
			</table>
		</td>
	</tr>
	
	<tr>
		<td align="right">
			<table>
				<tr>
					<td>
						<button type="button" class="btnCustom" onclick="history.back()">
							<span></span>
							${f:getMessage('뒤로')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
</table>
</form>

</body>
</html>