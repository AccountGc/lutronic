<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<title>Insert title here</title>
</head>

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
})

$(function() {
	$("#updateBtn").click(function() {
		if($("#appName").val() == '') {
			alert("${f:getMessage('일괄결재')} ${f:getMessage('제목')} ${f:getMessage('을(를) 입력하세요.')}");
			$("#appName").focus();
			return;
		}
		if($("#docTable tr").length == 1) {
			//alert("${f:getMessage('일괄결재')} ${f:getMessage('문서')} ${f:getMessage('을(를) 선택하세요.')}");
			//return;
		}
		
		if(confirm("${f:getMessage('수정하시겠습니까?')}")){
			//common_submit("asmApproval", "updateAsmAction", "updateAsm", "listAsm");
			var form = $("form[name=updateAsm]").serialize();
			var url	= getURLString("asmApproval", "updateAsmAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data:form,
				dataType:"json",
				async: true,
				cache: false,
				error:function(data){
					var msg = "${f:getMessage('등록 오류')}";
					alert(msg);
				},

				success:function(data){
					if(data.result) {
						alert("${f:getMessage('수정 성공하였습니다.')}");
						location.href = getURLString("asmApproval", "viewAsm", "do") + "?oid="+data.oid;
					}else {
						alert("${f:getMessage('수정 실패하였습니다.')} \n" + data.message);
					}
				}
				,beforeSend: function() {
					gfn_StartShowProcessing();
		        }
				,complete: function() {
					gfn_EndShowProcessing();
		        }
			});
		}
	})
})

</script>

<body>

<form name="updateAsm" id="updateAsm" method="post" enctype="multipart/form-data">
<input type="hidden" name="oid" id="oid" value="<c:out value="${asmData.oid }" />" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		
		<tr>
			<td align="left" valign=top height=42>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
				</tr>
			</table>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
							&nbsp;  ${f:getMessage('일괄결재')} ${f:getMessage('수정')}
						</td>
						<td>
							<table border="0" cellpadding="0" cellspacing="4" align="right">
								<tr>
									<td>
										<button type="button" name="" value="" class="btnCRUD" title="등록" id="updateBtn" name="updateBtn">
								           	<span></span>
								           	${f:getMessage('수정')}
							          	</button>
									</td>
									<td>
									<button type="button" name="approveBtn" id="approveBtn" class="btnCustom" onclick="javascript:history.back();">
										<span></span>
										${f:getMessage('이전페이지')}
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

        	
<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center>
    <tr><td height=1 width=100%></td></tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
	<col width='15%'>
	<col width='35%'>
 
	<tr bgcolor="ffffff">
 		<td class="tdblueM">
 			${f:getMessage('일괄결재')}${f:getMessage('제목')}
    	</td>
    
	   	<td class="tdwhiteL">
	   		<input type="text" name="appName" id="appName" class="txt_field" style="width: 90%" value="<c:out value="${asmData.name }" />"/>
	   	</td>
    </tr>
    <tr bgcolor="ffffff">
 		<td class="tdblueM">
 			${f:getMessage('일괄결재')}${f:getMessage('설명')}
    	</td>
    	
	   	<td class="tdwhiteL">
	   		<textarea name="description" id="description" value="<c:out value="${asmData.description }" /> cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('description', '300', '${f:getMessage('설명')}')"></textarea>
	   	</td>
    </tr>
</table>


<c:choose>
	<c:when test="${asmData.getApprovalKey()eq 'NDBT' }">
		<!-- 관련 문서 -->
		<jsp:include page="/eSolution/doc/include_DocumentSelect.do">
			<jsp:param value="asm" name="moduleType"/>
			<jsp:param value="${f:getMessage('일괄결재')} ${f:getMessage('문서')}" name="title"/>
			<jsp:param value="docOid" name="paramName"/>
			<jsp:param value="${asmData.getSearchType()}" name="searchType"/>
			<jsp:param value="BATCHAPPROVAL" name="state"/>
			<jsp:param value="LC_Default_NonWF" name="lifecycle"/>
			<jsp:param value="${asmData.oid }" name="oid"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<!-- 관련 문서 -->
		<jsp:include page="/eSolution/doc/include_OtherDocumentSelect.do">
			<jsp:param value="asm" name="moduleType"/>
			<jsp:param value="${f:getMessage('일괄결재')}" name="title"/>
			<jsp:param value="docOid" name="paramName"/>
			<jsp:param value="${asmData.getSearchType()}" name="searchType"/>
			<jsp:param value="BATCHAPPROVAL" name="state"/>
			<jsp:param value="LC_Default_NonWF" name="lifecycle"/>
			<jsp:param value="${asmData.oid }" name="oid"/>
		</jsp:include>
	</c:otherwise>
</c:choose>


</form>

</body>
</html>