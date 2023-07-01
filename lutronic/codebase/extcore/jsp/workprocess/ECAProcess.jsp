<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script type="text/javascript">
$(document).ready(function() {
})

$(function() {
	$("#appSave").click(function() {
		if(!validationCheck()){
			return;
		}
		
		appSave("완료");
	})
})

function validationCheck() {
	var activeCode = "<c:out value='${EChangeActivity.activeCode }' />";
	var activeType = "<c:out value='${EChangeActivity.activeType }' />";
	
	if(activeCode == "ORDER_NUMBER") {
		var checkLength = $("input[name='numberCheck']").length;
		for(var i=0; i < checkLength; i++) {
			if($("input[name='numberCheck']").eq(i).val() == "false") {
				alert("${f:getMessage('채번이 안된 부품이  존재합니다.')}")
				return false;
			}
		}
	}else if(activeCode == "ECA_SETTING") {
		var len = $("input[name=activity]").length;
		for(var i=0; i<len; i++) {
			if($("input[name=activity]").eq(i).is(":checked")) {
				var obj = $("input[name=activity]").eq(i);
				
				var activeCode = obj.attr("activeCode");
				
				if($("input[name='ecaCheck']").eq(i).val() == "false") {
					alert("${f:getMessage('ECA 활동을 등록하세요.')}");
					return false;
				}
				
				if($.trim($("#activeUser_"+activeCode).val()) == "") {
					alert(obj.attr("activeName") + "${f:getMessage('활동 담당자를 선택하세요.')}");
					return false;
				}
				
				if($.trim($("#finishDate_"+i).val()) == "") {
					alert(obj.attr("activeName") + "${f:getMessage('활동 요청 완료일을 선택하세요.')}");
					return false;
				}
				
				if($.trim($("#finishDate_"+i).val()) != "") {
					if(!gfn_compareDateToDay($("#finishDate_"+i).val())) {
						alert(obj.attr("activeName") + "${f:getMessage('요청완료일은 오늘 날짜 이전일 수 없습니다.')}");
						return false;
					}
				}
			}
		}
	}
	if(activeType == "DOCUMENT") {	
		if($('#SECONDARY').length > 0 || !($('#fileAttachedCount').val() == $('#fileAttachCount').html())) {
			alert("${f:getMessage('첨부파일을 등록 후 업무 완료를 할수 있습니다.')}");
			return false;
		}
		
		if($("#isDocument").val() == "true" && $("#isDocState").val() == "true"){
			if($("#isState").val() == "false"){
				
				alert("${f:getMessage('산출물 문서가 승인후 업무 완료를 할수 있습니다.')}");
				return false;
			}			
		}
		
		if(activeCode == "ECO_CONFIRM") {
			/*
			if($("input[name='compatible']:checked").size() == 0 ) {
				alert("호환성 여부를 선택해 주세요");
				return false;
			}
			
			if($("input[name='grade']:checked").size() == 0 ) {
				alert("Grade를  선택해 주세요");
				return false;
			}
			
			var partLen = $("input[name='partNumber']").length;
			
			for(var i=0; i<partLen; i++) {
				var partNumber = $("input[name='partNumber']").eq(i).val();
				if($("input[name='apply_" + partNumber + "']:checked").size() == 0 ) {
					alert(partNumber+"의 적용 구분을 선택해 주세요");
					return false;
				}
				
				if($("input[name='serial_" + partNumber + "']:checked").size() == 0 ) {
					alert(partNumber+"의 S/N관리를 선택해 주세요");
					return false;
				}
			}
			*/
			
			if($("#ecaCheck").val() == "false") {
				alert("ECO ${f:getMessage('확정 회의를 입력하세요.')}");
				return false;
			}
			
		}else if(activeCode == "CHECK_METTING") {
			/*
			if($("input[name='checkResult']:checked").length == 0 ) {
				alert("검토 결과를 선택해 주세요");
				return false;
			}
			
			if($.trim($("#checkDate").val()) == "" ) {
				alert("검토 일자를 선택해 주세요");
				return false;
			}
			
			if($.trim($("#tempworker").val()) == "" ) {
				alert("ECO 담당자를 선택해 주세요");
				return false;
			}
			*/
			
			if($("#ecaCheck").val() == "false") {
				alert("${f:getMessage('검토 회의를 입력하세요.')}");
				return false;
			}
		}
		
	}
	if(activeCode == "CHECK_DRAWING"){
		var len = $("input[name=isDwg]").length;
		for(var i=0; i<len; i++) {
			var number = $("input[name=drawingNumber]").eq(i).val();
			if($.trim($("input[name=isControl]").eq(i).val()) == "true") {
				if($.trim($("input[name=isOrCad]").eq(i).val()) == "false") {
					alert(number+" ${f:getMessage('부품에 ORCAD가 없습니다.')}");
					return false;
				}
				if($.trim($("input[name=isOrCadDoc]").eq(i).val()) == "false") {
					alert(number+" ${f:getMessage('부품에 ORCAD 관련 문서가 부족합니다.')}");
					return false;
				}
			}
			if($.trim($("input[name=isDwg]").eq(i).val()) == "true") {
				if($.trim($("input[name=isPDF]").eq(i).val()) == "false") {
					alert(number+"${f:getMessage('부품에 PDF가 없습니다.')}");
					return false;
				}
			}
		}
	}
	return true;
}

<%----------------------------------------------------------
*                      결재 수정
----------------------------------------------------------%>
function appSave(event) {
	if(!confirm(event+ "${f:getMessage('하시겠습니까?')}")) {
		return;
	}
	$("#WfUserEvent").val(event);
	$("#cmd").val("approve");
	
	//alert($("#cmd").val());
	$("#ECAProcess").attr("action", getURLString("groupware", "approveAction", "do")).submit();
}
</script>


<body>

<form name="ECAProcess" id="ECAProcess" method="post" enctype="multipart/form-data">

<input type="hidden" name="cmd" id="cmd" value="">
<input type="hidden" name="WfUserEvent" id="WfUserEvent" value="">
<input type="hidden" name="workOid" id="workOid" value="<c:out value="${workitemoid }" />">
<!--  <input type="hidden" name="oid" id="oid" value="<c:out value="${oid }" />">-->
<input type="hidden" name="actname" id="actname" value="<c:out value="${actname }" />">
<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('작업함')} 
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
			<c:if test="${actname ne '재작업' && actname ne '반려확인' }">
				<jsp:include page="/eSolution/groupware/include_Reassign.do">
					<jsp:param value="${workitemoid }" name="workItemOid"/>
				</jsp:include>
			</c:if>
		</td>
	</tr>

	<tr>
		<td class="space5"></td>
	</tr>
	<tr>
		<td align="right" colspan=2>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
               <tr>
               		<td>
               			<button type="button" class="btnCRUD" id="appSave">
               				<span></span>
               				${f:getMessage('업무완료')}
               			</button>
               		</td>
			   </tr>
           </table>
		</td>
	</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="space5"> </td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	<tr><td height="1" width=100%></td></tr>
</table>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
			<jsp:include page="/eSolution/groupware/processInfo.do">
				<jsp:param value="${workitemoid }" name="workitemoid"/>
			</jsp:include>
		</td>
	</tr>
</table>
<!-- 
<table border="0" cellspacing="0" cellpadding="0" width="100%" >
	<tr>
		<td class="space5"></td>
	</tr>
</table>
 -->
 
<c:choose>
	
	<c:when test="${EChangeActivity.activeType eq 'REVISE_BOM' || EChangeActivity.activeType eq 'BOM_CHANGE' }">
		<jsp:include page="/eSolution/change/wf_CreateEOEul.do">
			<jsp:param value="${EChangeActivity.eoOid }" name="eoOid"/>
		</jsp:include>
	</c:when>

	<c:when test="${EChangeActivity.activeType eq 'DOCUMENT'}">
		<jsp:include page="/eSolution/change/wf_Document.do">
			<jsp:param value="${EChangeActivity.ecaOid }" name="ecaOid"/>
		</jsp:include>
	</c:when>
	
	<c:when test="${EChangeActivity.activeType eq 'ECA_SETTING'}">
		<jsp:include page="/eSolution/change/wf_ECASetting.do">
			<jsp:param value="${EChangeActivity.ecaOid }" name="ecaOid"/>
		</jsp:include>
	</c:when>

	<c:when test="${EChangeActivity.activeType eq 'ORDER_NUMBER'}">
		<jsp:include page="/eSolution/change/wf_OrderNumber.do">
			<jsp:param value="${EChangeActivity.ecaOid }" name="ecaOid"/>
		</jsp:include>
	</c:when>

	<c:when test="${EChangeActivity.activeType eq 'CHECK_DRAWING'}">
		<jsp:include page="/eSolution/change/wf_CheckDrawing.do">
			<jsp:param value="${EChangeActivity.ecaOid }" name="ecaOid"/>
		</jsp:include>
	</c:when>
	
	<c:when test="${EChangeActivity.activeType eq 'CHECK_IN_BOM'}">
		<jsp:include page="/eSolution/change/wf_CheckInEOEul.do">
			<jsp:param value="${EChangeActivity.eoOid }" name="eoOid"/>
		</jsp:include>
	</c:when>
	
</c:choose>

<br>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td width="*"><img src="/Windchill/jsp/portal/img/bt_01.gif" > <b>${f:getMessage('결재의견')}</b></td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	<tr>
		<td height=1 width=100%></td>
	</tr>
</table>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td width=20% class="tdblueM">${f:getMessage('결재의견')}</td>
		<td class="tdwhiteL">
			<TEXTAREA NAME="comment" ROWS="3" COLS="100%" class=fm_area style='width:100%'></TEXTAREA></td>
		</tr>
</table>

</form>

</body>
</html>