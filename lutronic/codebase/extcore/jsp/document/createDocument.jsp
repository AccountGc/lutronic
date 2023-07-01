<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	numberCodeList('model', '');
	numberCodeList('deptcode', '');
	numberCodeList('preseration', '');
	documentType('GENERAL');
})

<%----------------------------------------------------------
*                      DocumentType 리스트 가져오기
----------------------------------------------------------%>
window.documentType = function(documentType) {
	var data = common_documentType('GENERAL');
	
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
		if($.trim($("input[name='location']").val()) == '/Default/Document') {
			alert("${f:getMessage('문서분류')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($("input[name='lifecycle']:checked").length == 0 ){
			alert("${f:getMessage('결재방식')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($.trim($("#documentName").val()) == '') {
			alert("${f:getMessage('문서종류')}${f:getMessage('을(를) 입력하세요.')}");
			$("#documentName").focus();
			return;
		}
		if($("#documentType").val() == '') {
			alert("${f:getMessage('문서분류')}${f:getMessage('을(를) 선택하세요.')}");
			$("#documentType").focus();
			return;
		}
		
		if($("#preseration").val() == '') {
			alert("${f:getMessage('보존기간')}${f:getMessage('을(를) 선택하세요.')}");
			$("#preseration").focus();
			return;
		}
		
		if($.trim($("#PRIMARY").val()) == "" ) {
			alert("${f:getMessage('주 첨부파일')}${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("doc", "createDocumentAction", "documentCreateForm", "listDocument");
		}
		
	})
	<%----------------------------------------------------------
	*                      목록버튼
	----------------------------------------------------------%>
	$("#listBtn").click(function() {
		location.href = getURLString("doc", "listDocument", "do");
	})
	
	$("input[name=documentName]").keyup(function (event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if((charCode == 38 || charCode == 40) ) {
			if(!$( "#"+this.id+"Search" ).is( ":hidden" )){
				var isAdd = false;
				if(charCode == 38){
					isAdd = true;
				}
				moveDocumentNameFocus(this.id, isAdd);
			}
		} else if(charCode == 13 || charCode == 27){
			$("#" + this.id + "Search").hide();
		} else {
			autoSearchDocumentName(this.id, this.value);
		}
		
	})
	
	$("input[name=documentName]").focusout(function () {
		$("#" + this.id + "Search").hide();
	})
	
	$('#resetBtn').click(function() {
		resetFunction();
	})
})

<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.moveDocumentNameFocus = function(id,isAdd) {
	var removeCount = 0;
	var addCount = 0;
	var l = $("#" + id + "UL li").length;
	for(var i=0; i<l; i++){
		var cls = $("#" + id + "UL li").eq(i).attr('class');
		if(cls == 'hover') {
			$("#" + id + "UL li").eq(i).removeClass("hover");
			removeCount = i;
			if(isAdd){
				addCount = (i-1);
			}else if(!isAdd) {
				addCount = (i+1);
			}
			break;
		}
	}
	if(addCount == l) {
		addCount = 0;
	}
	$("#" + id + "UL li").eq(addCount).addClass("hover");
	$("#" + id).val($("#" + id + "UL li").eq(addCount).text());
}

<%----------------------------------------------------------
*                      문서명 입력시 이름 검색
----------------------------------------------------------%>
window.autoSearchDocumentName = function(id, value) {
	var codeType = id.toUpperCase();
	var data = common_autoSearchName(codeType, value);
	addSearchList(id, eval(data.responseText), false);
}

<%----------------------------------------------------------
*                      문서명 입력시 데이터 리스트 보여주기
----------------------------------------------------------%>
window.addSearchList = function(id, data, isRemove) {
	$("#" + id + "UL li").remove();
	if(isRemove) {
		$("#" + this.id + "Search").hide();
	}else {
		if(data.length > 0) {
			$("#" + id + "Search").show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL").append("<li title='" + id + "' class=''>" + data[i].name);
			}
		}else {
			$("#" + id + "Search").hide();
		}
	}
}

<%----------------------------------------------------------
*                      문서명 데이터 마우스 올렸을때
----------------------------------------------------------%>
$(document).on("mouseover", 'div > ul > li', function() {
	var partName = $(this).attr("title");
	$(this).addClass("hover");
	$("#" + partName).val($(this).text());
})

<%----------------------------------------------------------
*                      문서명 데이터 마우스 뺄때
----------------------------------------------------------%>
$(document).on("mouseout", 'div > ul > li', function() {
	$(this).removeClass("hover");
})
</script>

<style>
.hover{ 
 	  cursor: default;
      background:#dedede;
}
</style>

<form name="documentCreateForm" id="documentCreateForm" method="post" enctype="multipart/form-data">

<input type="hidden" name="fid" 			id="fid" 					value="">
<input type="hidden" name="location" 		id="location" 				value="/Default/Document">

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' /> 
							${f:getMessage('문서')}
							${f:getMessage('관리')} 
							> 
							${f:getMessage('문서')}
							${f:getMessage('등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<!-- 문서 정보 -->
    <tr>
		<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				${f:getMessage('문서')}
				${f:getMessage('정보')}
			</b>
		</td>
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
						${f:getMessage('문서분류')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<b>
							<span id="locationName">
								/Default/Document
							</span>
						</b>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('결재방식')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default">
							<span></span>
							${f:getMessage('기본결재')}
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default_NonWF">
							<span></span>
							${f:getMessage('일괄결재')}
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('문서종류')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<input name="documentName" id="documentName" class="txt_field" size="85" style="width:90%" maxlength="80"/>
						
						<div id="documentNameSearch" style="display: none; border: 1px solid black ; position: absolute; background-color: white; z-index: 1;">
							<ul id="documentNameUL" style="list-style-type: none; padding-left: 5px; text-align: left; ">
							</ul>
						</div>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('문서명')}
					</td>
					
					<td class="tdwhiteL">
						<input name="docName" id="docName" class="txt_field" size="85" style="width:90%" maxlength="80"/>
					</td>
					
				</tr>
				
				<tr bgcolor="ffffff" height="35">
				
					<td class="tdblueM">
						${f:getMessage('문서유형')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<select id='documentType' name='documentType' style="width: 95%">
						</select>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('보존기간')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<select id='preseration' name='preseration' style="width: 95%">
						</select>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
				
					<td class="tdblueM">
						${f:getMessage('프로젝트코드')}
					</td>
					
					<td class="tdwhiteL">
						<select id='model' name='model' style="width: 95%">
						</select>
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
						${f:getMessage('내부 문서번호')}
					</td>
					
					<td class="tdwhiteL">
						<input name="interalnumber" id="interalnumber" class="txt_field" size="85" style="width:90%" maxlength="80" />
					</td>
					
					<td class="tdblueM">
						${f:getMessage('작성자')}
					</td>
					
					<td class="tdwhiteL">
						<input name="writer" id="writer" class="txt_field" size="85" style="width:90%" maxlength="80" />
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('문서')}${f:getMessage('설명')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<div class="textarea_autoSize">
							<textarea name="description" id="description" cols="10" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('문서')}${f:getMessage('설명')}')"></textarea>
						</div>
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
							<jsp:param name="btnId" value="createBtn" />
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
							<jsp:param name="btnId" value="createBtn" />
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
</jsp:include>

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
						<button type="button" name="" value="" class="btnCRUD" title="등록" id="createBtn" name="createBtn">
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
						<button title="Reset" class="btnCustom" type="reset" name="listBtn" id="listBtn">
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
