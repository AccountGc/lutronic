<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	numberCodeList('model', '');
	numberCodeList('manufacture', '');
	numberCodeList('deptcode', '');
	numberCodeList('moldtype', '');
	documentType('GENERAL');
})

<%----------------------------------------------------------
*                      DocumentType 리스트 가져오기
----------------------------------------------------------%>
window.documentType = function(documentType) {
	var data = common_documentType('moldtype');
	
	addSelectList('documentType', eval(data.responseText));
}

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode1) {
	var type = id.toUpperCase();
	var data = common_numberCodeList(type, parentCode1, false);
	
	addSelectList(id, eval(data.responseText));
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id,data){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "'>";
			
			if(id != 'documentType') {
				html += " [" + data[i].code + "] ";
			} 
			html += data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}

$(function() {
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$("#createBtn").click(function() {
		
		if($("input[name='lifecycle']:checked").length == 0 ){
			alert("${f:getMessage('결재방식')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($.trim($("#docName").val()) == "" ) {
			alert("${f:getMessage('문서명')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		
		if($.trim($("#moldtype").val()) == "" ) {
			alert("${f:getMessage('금형타입')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($.trim($("#PRIMARY").val()) == "" ) {
			alert("${f:getMessage('주 첨부파일')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("doc", "createDocumentAction", "documentCreateForm", "listMold");
		}
		
	})
	<%----------------------------------------------------------
	*                      목록버튼
	----------------------------------------------------------%>
	$("#listBtn").click(function() {
		location.href = getURLString("doc", "listMold", "do");
	})
	$('#resetBtn').click(function() {
		resetFunction();
	})
})

</script>

<form name="documentCreateForm" id="documentCreateForm" method="post" enctype="multipart/form-data">

<input type="hidden" name="fid" 			id="fid" 					value="">
<input type="hidden" name="location" 		id="location" 				value="/Default/금형문서">
<input type="hidden" name="documentType" 	id="documentType" 			value="$$MMDocument">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' /> 
							&nbsp; ${f:getMessage('금형')}${f:getMessage('관리')} > ${f:getMessage('금형')}${f:getMessage('등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<!-- 금형 정보 -->
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" ><b>${f:getMessage('금형')}${f:getMessage('정보')}</b></td>
	</tr>
	<tr>
        <td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
			    <tr>
			        <td height="1" width="100%"></td>
			    </tr>
			</table>
		
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			
				<colgroup>
					<col width="150">
					<col width="350">
					<col width="150">
					<col width="350">
				</colgroup>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('결재방식')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default" >
							<span></span>
							${f:getMessage('기본결재')}
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default_NonWF">
							<span></span>
							${f:getMessage('일괄결재')}
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('문서명')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<input name="docName" id="docName" class="txt_field" size="85" style="width:90%" maxlength="80" onchange="textAreaLengthCheckName('docName', '600', '제목')"/>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						Manufacturer
					</td>
					
					<td class="tdwhiteL">
						<select id='manufacture' name='manufacture' style="width: 95%">
						</select>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('금형타입')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<select id='moldtype' name='moldtype' style="width: 95%">
						</select>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('업체자제금형번호')}
					</td>
					
					<td class="tdwhiteL">
						<input name="moldnumber" id="moldnumber" class="txt_field" size="85" style="width:90%" maxlength="80" onchange="textAreaLengthCheckName('moldnumber', '600', '업체자제금형번호')"/>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('금형개발비')}
					</td>
					
					<td class="tdwhiteL">
						<input name="moldcost" id="moldcost" class="txt_field" size="85" style="width:90%" maxlength="80" onchange="textAreaLengthCheckName('moldcost', '600', '금형개발비')"/>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('내부 문서번호')}<br/>
						(${f:getMessage('자산등록번호')})
					</td>
					
					<td class="tdwhiteL">
						<input name="interalnumber" id="interalnumber" class="txt_field" size="85" style="width:90%" maxlength="80" onchange="textAreaLengthCheckName('insideNumber', '600', '내부 문서번호')"/>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('부서')}
					</td>
					
					<td class="tdwhiteL">
						<select id='deptcode' name='deptcode' style="width: 95%">
						</select>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('문서')}${f:getMessage('설명')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<textarea name="description" id="description" cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('금형')}${f:getMessage('설명')}')"></textarea>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('주 첨부파일')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="documentCreateForm"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="type" value="PRIMARY"/>
						</jsp:include>
					</td>
				</tr>
				
				<tr bgcolor="ffffff">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
							<jsp:param name="formId" value="documentCreateForm"/>
							<jsp:param name="command" value="insert"/>
						</jsp:include>
					</td>
				</tr>
			</table>
        </td>
    </tr>
</table>

<!-- 관련 품목 -->
<jsp:include page="/eSolution/part/include_PartSelect.do" flush="false" >
	<jsp:param name="moduleType" value="doc"/>
	<jsp:param name="title" value="${f:getMessage('관련 품목')}"/>
	<jsp:param name="paramName" value="partOid"/>
	<jsp:param name="state" value="APPROVED"/>
	
</jsp:include>

<!-- 관련 ECO 
<jsp:include page="" flush="false">
	<jsp:param value="" name=""/>
</jsp:include>	
-->

<!-- 관련 문서 -->
<jsp:include page="/eSolution/doc/include_DocumentSelect.do">
	<jsp:param value="${f:getMessage('관련 문서')}" name="title"/>
	<jsp:param value="docOid" name="paramName"/>
</jsp:include>

<!-- 버튼 -->
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr>
		<td align="center" colspan="2">
			<table border="0" cellpadding="0" cellspacing="4" align="center">
				<tr>
					<td>
						<button type="button" class="btnCRUD" title="${f:getMessage('등록')}" id="createBtn" name="createBtn">
		                  	<span></span>
		                  	${f:getMessage('등록')}
	                  	</button>
					</td>
					<td>
						<button title="Reset" class="btnCustom" type="reset" name="resetBtn" id="resetBtn">
							<span></span>
							${f:getMessage('초기화')}
						</button>
					</td>
					<td>
						<button title="${f:getMessage('목록')}" class="btnCustom" type="button" name="listBtn" id="listBtn">
							<span></span>
							${f:getMessage('목록')}
						</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>
