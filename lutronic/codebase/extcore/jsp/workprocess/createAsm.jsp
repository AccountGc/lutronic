<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
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
})

$(function() {
	$("#createBtn").click(function() {
		if($("#appName").val() == '') {
			alert("${f:getMessage('일괄결재')} ${f:getMessage('제목')} ${f:getMessage('을(를) 입력하세요.')}");
			$("#appName").focus();
			return;
		}
		if($("#docTable tr").length == 1) {
			alert("${f:getMessage('일괄결재')} ${f:getMessage('대상')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("asmApproval", "createAsmAction", "createAsm", "listAsm");
		}
	})
})

</script>

<body>

<form name="createAsm" id="createAsm" method="post" enctype="multipart/form-data">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							<c:choose>
								<c:when test="${searchType eq 'MOLD' }">
									&nbsp; ${f:getMessage('금형')}
								</c:when>
								<c:when test="${searchType eq 'ROHS' }">
									&nbsp; RoHS
								</c:when>
								<c:when test="${searchType eq 'DOC' }">
									&nbsp; ${f:getMessage('문서')} 
								</c:when>
							</c:choose>
								${f:getMessage('일괄결재')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>
<table border="0" cellpadding="0" cellspacing="4" align="right">
	<tr>
		<td>
			<button type="button" name="" value="" class="btnCRUD" title="등록" id="createBtn" name="createBtn">
	           	<span></span>
	           	${f:getMessage('등록')}
          	</button>
		</td>
	</tr>
</table>
        	
<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
    <tr><td height=1 width=100%></td></tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
	<col width='15%'>
	<col width='35%'>
 
	<tr bgcolor="ffffff">
 		<td class="tdblueM">
 			${f:getMessage('일괄결재')}
 			${f:getMessage('제목')} <span class="style1">*</span>
    	</td>
    
	   	<td class="tdwhiteL">
	   		<input type="text" name="appName" id="appName" class="txt_field" style="width: 90%" />
	   	</td>
    </tr>
    <tr bgcolor="ffffff">
 		<td class="tdblueM">
 			${f:getMessage('일괄결재')}
 			${f:getMessage('설명')} 
    	</td>
    
	   	<td class="tdwhiteL">
	   		<textarea name="description" id="description" cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('description', '300', '${f:getMessage('설명')}')"></textarea>
	   	</td>
    </tr>
</table>

<!-- 관련 문서 -->
<c:choose>
	<c:when test="${searchType eq 'DOC' }">
		<jsp:include page="/eSolution/doc/include_DocumentSelect.do">
			<jsp:param value="${f:getMessage('일괄결재')} ${f:getMessage('문서')}" name="title"/>
			<jsp:param value="docOid" name="paramName"/>
			<jsp:param value="${searchType }" name="searchType"/>
			<jsp:param value="BATCHAPPROVAL" name="state"/>
			<jsp:param value="LC_Default_NonWF" name="lifecycle"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<jsp:include page="/eSolution/doc/include_OtherDocumentSelect.do">
			<jsp:param value="${f:getMessage('일괄결재')}" name="title"/>
			<jsp:param value="docOid" name="paramName"/>
			<jsp:param value="${searchType }" name="searchType"/>
			<jsp:param value="BATCHAPPROVAL" name="state"/>
			<jsp:param value="LC_Default_NonWF" name="lifecycle"/>
		</jsp:include>
	</c:otherwise>
</c:choose>
</form>

</body>
</html>