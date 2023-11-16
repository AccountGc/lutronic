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

<%----------------------------------------------------------
*                     팝업창 쿠기 생성
----------------------------------------------------------%>
function setCookie(cName, cValue, cDay){
	var cName ="${noticeData.oid }";
	var cValue="${noticeData.oid }";
	var cDay = 7;		//소멸시기 일주일
    var expire = new Date();
    expire.setDate(expire.getDate() + cDay);
    cookies = cName + '=' + escape(cValue) + '; path=/ '; // 한글 깨짐을 막기위해 escape(cValue)를 합니다.
    if(typeof cDay != 'undefined') cookies += ';expires=' + expire.toGMTString() + ';';
    document.cookie = cookies;
    self.close();
}
	
</script>

<body>

<form name="viewNotice" id="viewNotice" method="post" >

<input type="hidden" name="oid" id="oid" value="<c:out value="${noticeData.oid }" />" >

<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center">
		   				<B><font color=white>${f:getMessage('공지사항')}</font></B>
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
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
				<tr><td height=1 width=100%></td>
			</tr>
			</table>
			<table width="100%" border="0" cellpadding="1" cellspacing="0">
				<tr>
					<td align="left">
						<label><input type="checkbox" name="checkId" id="checkId" value="checkbox" onclick="setCookie()">&nbsp;일주일 간 열지 않기</label>
					</td>
					<td align="right"> 
						<button type="button" class="btnCustom" onclick="self.close()">
							<span></span>
							${f:getMessage('닫기')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
		<td >
			
			
		</td>
	</tr>
	
</table>
</form>

</body>
</html>