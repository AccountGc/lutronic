<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>

<script>
$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#update").click(function() {
		if($.trim($("input[name='title']").val()) == "" ) {
			alert('${f:getMessage('제목')}${f:getMessage('을(를) 입력하세요.')}');
			return;
		}
		
		$("#updateNotice").attr("action", getURLString("groupware", "updateNoticeAction", "do")).submit();
	})
})

</script>

<body>
<form name="updateNotice" id="updateNotice"  method=post enctype="multipart/form-data">

<input type="hidden" name="oid"  id="oid"	value="<c:out value="${noticeData.oid }"/>" />

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
	<tr align=center>
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr  align=center>
					<td valign="top" style="padding:0px 0px 0px 0px">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
							<tr><td height=1 width=100%></td>
						</tr>
						</table>
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
						<col width='100'><col><col width='100'><col>
			
							<tr>
								<td class="tdblueM">${f:getMessage('제목')} <span class="style1">*</span></td>
								<td class="tdwhiteL">
									<input type="text" name="title" style="width:90%" maxlength="150" value="<c:out value="${noticeData.title }" />" >
								</td>
							</tr>
							<tr>
								<td class="tdblueM">${f:getMessage('팝업 유무')}</td>
								<td class="tdwhiteL">
									<input type="checkbox" name="isPopup" id="isPopup" value="true" <c:out value="${noticeData.checked }" />>
								</td>
							</tr>
							<tr>
								<td class="tdblueM">${f:getMessage('내용')}</td>
								<td class="tdwhiteL">
									<textarea name="contents" id="contents" cols="80" rows="10" class="fm_area" style="width:90%" onKeyUp="common_CheckStrLength(this, 4000)" onChange="common_CheckStrLength(this, 4000)"><c:out value="${noticeData.contents }" /></textarea>
								</td>
							</tr>
							<tr >
								<td class="tdblueM">${f:getMessage('첨부파일')}</td>
								<td class="tdwhiteL">
									<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
										<jsp:param name="formId" value="updateNotice"/>
										<jsp:param name="type" value="secondary"/>
										<jsp:param name="oid"	value="${noticeData.oid }" />
									</jsp:include>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tr height=35>
					<td align="right">
						<table>
							<tr>
								<td>
									<button type="button" class="btnCRUD" id="update" name="update">
										<span></span>
										${f:getMessage('수정')}
									</button>
								</td>
								
								<td>
									<button type="button" class="btnCustom" onclick="history.go(-1)">
										<span></span>
										${f:getMessage('뒤로')}
									</button>
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