<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
$(document).ready(function() {
	
	divPageLoad('docLink', $('#ecaOid').val(), 'doc', '${f:getMessage('산출물') }', 'include_documentLink', true);

})

$(function() {
	$("#attachFile").click(function() {
		$("#ecaAction").val("attachFile");
		$("#ECAProcess").attr("target", "hiddenFrame");
		$("#ECAProcess").attr("action", getURLString("change", "actionECA", "do")).submit();
	})
	
	$("#inputOutputResult").click(function() {
		var outputOid = $("#ecaOid").val();
		var url = getURLString("doc", "createDocumentPop", "do") + "?outputOid=" + outputOid;
		openOtherName(url,"window","920","550","status=no,scrollbars=yes,resizable=yes");
	})
	
	$("#inputLinkOutput").click(function() {
		var outputOid = $("#ecaOid").val();
		var url = getURLString("doc", "createDocumentLink", "do") + "?outputOid=" + outputOid;
		window.open(url, "CreateDocumentLink" , "status=no,  width=880px, height=520px, resizable=no, scrollbars=no");
	})
	
	$("img[name='docDelete']").click(function() {
		if(!confirm("${f:getMessage('삭제하시겠습니까?')}")){
			return;
		}
		$("#docLinkOid").val(this.id);
		$("#ecaAction").val("docDelete");
		$("#ECAProcess").attr("target", "hiddenFrame");
		$("#ECAProcess").attr("action", getURLString("change", "actionECA", "do")).submit();
	})
	
	$("#eco_confirm").click(function() {
		if($("input[name='compatible']:checked").size() == 0 ) {
			alert("${f:getMessage('호환성 여부')}${f:getMessage('을(를) 선택하세요.')}");
			return false;
		}
		
		if($("input[name='grade']:checked").size() == 0 ) {
			alert("${f:getMessage('등급')}${f:getMessage('을(를) 선택하세요.')}");
			return false;
		}
		
		var partLen = $("input[name='partNumber']").length;
		
		for(var i=0; i<partLen; i++) {
			var partNumber = $("input[name='partNumber']").eq(i).val();
			if($("input[name='apply_" + partNumber + "']:checked").size() == 0 ) {
				alert(partNumber+"${f:getMessage('의 적용 구분을 선택하세요.')}");
				return false;
			}
			
			if($("input[name='serial_" + partNumber + "']:checked").size() == 0 ) {
				alert(partNumber+"${f:getMessage('의 S/N관리를 선택하세요.')}");
				return false;
			}
		}
		$("#ecaAction").val("ecoConfirm");
		$("#ECAProcess").attr("target", "hiddenFrame");
		$("#ECAProcess").attr("action", getURLString("change", "actionECA", "do")).submit();
	})
	
	$("#check_Result").click(function() {
		if($("input[name='checkResult']:checked").length == 0 ) {
			alert("${f:getMessage('합의결과')}${f:getMessage('을(를) 선택하세요.')}");
			return false;
		}
		
		if($.trim($("#checkDate").val()) == "" ) {
			alert("${f:getMessage('합의일자')}${f:getMessage('을(를) 선택하세요.')}");
			return false;
		}
	
		if($("input[name=checkResult]:checked").val() != "CR02"){
			if($.trim($("#tempworker").val()) == "" ) {
				alert("ECO ${f:getMessage('담당자')}${f:getMessage('을(를) 선택하세요.')}");
				return false;
			}
		}
		$("#ecaAction").val("checkResult");
		$("#ECAProcess").attr("target", "hiddenFrame");
		$("#ECAProcess").attr("action", getURLString("change", "actionECA", "do")).submit();
	})
})

function set_Document(docOid,outputOid) {
	$("#docOid").val(docOid);
	$("#ecaAction").val("activeDoc");
	$("#ECAProcess").attr("target", "hiddenFrame");
	$("#ECAProcess").attr("action", getURLString("change", "actionECA", "do")).submit();
}

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
function numberCodeList(type,value) {
	var url	= getURLString("common", "numberCodeList", "do");
	$.ajax({
		type:"POST",
		url: url,
		data:{type:type},
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = type + " ${f:getMessage('코드 목록 오류')}";
			alert(msg);
		},

		success:function(data){
			addSelectList(type,data,value);
		}
	});
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
function addSelectList(type,data,value){
	if (type == "ECRRESULT"){
		id = "check";
		for(var i=0; i<data.length; i++) {
			
			var html = "<input type=radio name='checkResult' value='" + data[i].code + "'"
			if(data[i].code ==  value) {
				html += "checked";
				if(data[i].code == 'CR02'){
					$("#dropCheck1").hide();
					$("#dropCheck2").hide();
				}
			}
			html += "> " + data[i].name;
			
			$("#"+ id).append(html);
		}
	}else if(type == "GRADE") {
		for(var i=0; i<data.length; i++) {
			$("#gradeList").append("<input type='checkbox' name='grade' value='" + data[i].code + "'> " + data[i].name);
		}
		setSelectList("grade", value);
	}
}

<%----------------------------------------------------------
*               기존 데이터 selectBodx에 옵션 추가
----------------------------------------------------------%>
function setSelectList(name, value){
	var valueArr = value.split(',');
	
	for(var i=0; i<valueArr.length; i++) {
		$("input:checkbox[name='" + name +"']").each(function() {
			if(this.value == valueArr[i]) {
				this.checked = true;
			}
		})
	}
}

function checkDrop(){
	$("input[name=checkResult]").change(function() {
		var radioValue = $(this).val();
		if(radioValue == 'CR02'){
			$("#dropCheck1").hide();
			$("#dropCheck2").hide();
			$("#worker").val("");
			$("#tempworker").val("");
		}else{
			$("#dropCheck1").show();
			$("#dropCheck2").show();
		}
	})
}

<%----------------------------------------------------------
*                      DIV 페이지 로딩 설정
----------------------------------------------------------%>
window.divPageLoad = function(divId, oid, moduleName, title, url, enabled) {
	if(url.length > 0) {
		var url	= getURLString(moduleName, url, "do");
		$.ajax({
			type:"POST",
			url: url,
			data: {
				oid : oid,
				title : title,
				module : 'ecaAction',
				enabled : enabled
			},
			success:function(data){
				$('#' + divId).html(data);
			}
		});
	} else {
		$('#' + divId).html('');
	}
}

</script>

<body>
<input type="hidden" name="ecaAction" id="ecaAction" >
<input type="hidden" name="docOid" id="docOid" >
<input type="hidden" name="docLinkOid" id="docLinkOid">
<input type="hidden" name="ecaOid" id="ecaOid" value="<c:out value="${ecaOid }"/>">

<input type="hidden"	name="ecaCheck" id="ecaCheck" value="<c:out value="${ecaCheck }"/>" />
<input type="hidden"	name="isDocument" id="isDocument" value="<c:out value="${isDocument }"/>" />
<input type="hidden"	name="isDocState" id="isDocState" value="<c:out value="${isDocState }"/>" />
<input type="hidden"	name="isState" id="isState" value="<c:out value="${isState }"/>" />

<iframe src="" name="hiddenFrame" id="hiddenFrame" scrolling=no frameborder=no marginwidth=0 marginheight=0 style="display:none"></iframe>

<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td>
			<!-- 문서 링크 -->
			<div id='docLink'></div>
		
		</td>
	</tr>
<!------------------------------------------------------------ 첨부파일 END    ------------------------------------------------------------------------------------->

	<c:if test="${isAttach }">

	<tr><td height="10">&nbsp;</td></tr>
	
	<tr>
		<td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td width="*">
						<img src="/Windchill/jsp/portal/img/bt_01.gif" >
						<b>첨부파일 </b>
					</td>
					
					<td align="right" width="80">
						<button type="button" class="btnCRUD" id="attachFile">
							<span></span>
							첨부파일
						</button>
					</td>		
				</tr>
			</table>
			
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td class="tab_btm2"></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr><td height=1 width=100% ></td></tr>
			</table>
			
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="tdblueM">
						첨부파일
					</td>
					
					<td class="tdwhiteL" colspan=3>
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="ECAProcess"/>
							<jsp:param name="oid" value="${ecaOid}"/>
						</jsp:include>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	</c:if>
</table>


</body>
</html>