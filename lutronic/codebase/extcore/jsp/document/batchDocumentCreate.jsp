<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
	setAttribute();
})


$(function() {
	
	
	<%----------------------------------------------------------
	*                    addCount 입력 중
	----------------------------------------------------------%>
	$('#addCount').keypress(function(event) {
		return common_isNumber(event, this);
	})
	
	<%----------------------------------------------------------
	*                추가
	----------------------------------------------------------%>
	$('#addRow').click(function() {
		var dataMap = auiGridSetting();
		dataMap["addCount"] = $('#addCount').val();
		opener.addRow(dataMap);
	})
	
	<%----------------------------------------------------------
	*                    수정
	----------------------------------------------------------%>
	$('#modifyRow').click(function() {
		var dataMap = auiGridSetting();
		dataMap["auiId"] = $('#auiId').val();
		opener.modifyRow(dataMap);
	})
	<%----------------------------------------------------------
	*                  문서종류
	----------------------------------------------------------%>
	$("input[name=documentName]").keyup(function (event) {
		var charCode = (event.which) ? event.which : event.keyCode;
		if((charCode == 38 || charCode == 40) ) {
			if(!$( "#"+this.id+"Search" ).is( ":hidden" )){
				var isAdd = false;
				if(charCode == 38){
					console.log("documentName keyup 2");
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
	
})

<%----------------------------------------------------------
*                   부포창의 Grid에 Setting
----------------------------------------------------------%>
function auiGridSetting(){
	/*
	if(!validation()){
		return;
	}
	*/
	var dataMap = new Object(); 
	
	dataMap["documentName"] = $('#documentName').val();
	dataMap["docName"] = $('#docName').val();
	dataMap["lifecycle"] = $(":input:radio[name=lifecycle]:checked").val();
	dataMap["lifecycleName"] = $(":input:radio[name=lifecycle]:checked").attr("title");
	dataMap["documentType"] = $('#documentType').val();
	dataMap["documentTypeName"] = checkSelect($("#documentType option:selected").text());
	dataMap["description"] = $('#description').val();
	dataMap["model"] = $('#model').val();
	dataMap["modelName"] =checkSelect($("#model option:selected").text());
	
	dataMap["writer"] = $('#writer').val();
	dataMap["interalnumber"] = $('#interalnumber').val();
	
	dataMap["deptcode"] = $('#deptcode').val();
	dataMap["deptcodeName"] = checkSelect($("#deptcode option:selected").text());
	
	dataMap["preseration"] = $('#preseration').val();
	dataMap["preserationName"] = checkSelect($("#preseration option:selected").text());
	//id_folder//id_location
	dataMap["location"] = $('#id_location').val();
	dataMap["folder"] = $('#id_folder').val();
	
	
	return dataMap;
}

function checkSelect(value){
	if(value == "선택"){
		value = "-"
	}
	return value;
}

<%----------------------------------------------------------
*                      ↑,↓ 입력시
----------------------------------------------------------%>
window.movePartNameFocus = function(id,isAdd) {
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
*                       문서명 입력시 이름 검색
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
	} else{
		if(data.length > 0) {
			$("#" + id + "Search").show();
			for(var i=0; i<data.length; i++) {
				$("#" + id + "UL").append("<li title='" + id + "'>" + data[i].name);
			}
		} else {
			$("#" + id + "Search").hide();
		}
	}
}

<%----------------------------------------------------------
*                      문서명 데이터 마우스 올렸을때
----------------------------------------------------------%>
$(document).on("mouseover", 'div > ul > li', function() {
	var partName = $(this).attr("title");
	
	$("#" + partName + "UL li").each(function() {
		var cls = $(this).attr('class');
		if(cls == 'hover') {
			$(this).removeClass('hover');
		}
	})
	
	$(this).addClass("hover") ;
	$("#" + partName).val($(this).text());
})

<%----------------------------------------------------------
*                      문서명 데이터 마우스 뺄때
----------------------------------------------------------%>
$(document).on("mouseout", 'div > ul > li', function() {
	$(this).removeClass("hover") ;
})

<%----------------------------------------------------------
*                      개벌 ID 수정 Setting
----------------------------------------------------------%>
function setAttribute(){
	if( $("#auiId").val() !=""){
		
		var rowItems = opener.getIDAttribute($("#auiId").val());
		
		var documentName = rowItems[0].documentName;
		var docName = rowItems[0].docName;
		var lifecycle = rowItems[0].lifecycle;
		var documentTypeValue = rowItems[0].documentType;
		var description = rowItems[0].description;
		var model = rowItems[0].model;
		var writer = rowItems[0].writer;
		var interalnumber = rowItems[0].interalnumber;
		var deptcode = rowItems[0].deptcode;
		var preseration = rowItems[0].preseration;
		var location = rowItems[0].location;
		var folder = rowItems[0].folder;
		
		
		$("#documentName").val(documentName);
		$("#docName").val(docName);
		$("#description").val(description);
		$('input:radio[name="lifecycle"][value='+lifecycle+']').prop('checked', true);
		documentType('GENERAL',documentTypeValue);
		numberCodeList('model', '', model);
		$("#writer").val(writer);
		$("#interalnumber").val(interalnumber);
		numberCodeList('deptcode', '', deptcode);
		numberCodeList('preseration', '', preseration);	
		$("#id_location").val(location);
		$("#id_folder").val(folder);
	
	}else{
		
		numberCodeList('model', '', '');
		numberCodeList('deptcode', '', '');
		numberCodeList('preseration', '');
		documentType('GENERAL','');
	}
	
	//console.log("setAttribute seq=" + seq);
	//console.log("setAttribute etc=" + etc);
	
	
	
}

<%----------------------------------------------------------
*                      DocumentType 리스트 가져오기
----------------------------------------------------------%>
window.documentType = function(documentType,value) {
	var data = common_documentType('GENERAL');
	
	addSelectList('documentType', eval(data.responseText),value);
}

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode, value) {
	
	var	type = id.toUpperCase();
	
	var data = common_numberCodeList(type, parentCode, false);
	
	addSelectList(id, eval(data.responseText), value);
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id, data, value){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' >${f:getMessage('선택')}</option>");
	
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "' title='" + data[i].oid + "'";
			
			if(data[i].code == value) {
				html += " selected";
			}
			
			html += " > [" + data[i].code + "] " + data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}
</script>

<body>

<form name="documentCreateForm"  method="post" >
<input type="hidden" name="auiId" 		id="auiId"		value="<c:out value="${auiId}"/>">
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr align="center">
	    <td valign="top" style="padding:0px 0px 0px 0px">
		    <table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
		   		<tr> 
		   			<td height="30" width="93%" align="center"><B><font color="white"><c:out value="${title }" /></font></B></td>
		   		</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"  style="table-layout:fixed">
				<tr height="30">
					<td align="right">
					<c:choose>
						<c:when test="${mode eq 'single' }">
		  					
							<button type="button" name="modifyRow" id="modifyRow" class="btnClose" >
							<span></span>
							${f:getMessage('수정')}
						</button>
						</c:when>
						<c:otherwise>
						<b>*추가 수량 :</b> <input type="text" name="addCount" id="addCount" maxlength="2" size="5">
		  				<button type="button" name="addRow" id="addRow" class="btnClose" >
							<span></span>
							${f:getMessage('적용')}
						</button>
						</c:otherwise>
					</c:choose>
		  				<button type="button" name="" id="" class="btnClose" onclick="self.close()">
							<span></span>
							${f:getMessage('닫기')}
						</button>
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
						<jsp:include page="/eSolution/folder/include_FolderSelect.do">
							<jsp:param value="/Default/Document" name="root"/>
							<jsp:param value="${oLocation }" name="folder"/>
						</jsp:include>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('결재방식')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default" title="기본결재" checked>
							<span></span>
							${f:getMessage('기본결재')}
						<input type="radio" name="lifecycle" id="lifecycle" value="LC_Default_NonWF" title="일괄결재">
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
						문서설명
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<textarea name="description" id="description" cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('description', '4000', '문서설명')"></textarea>
					</td>
				</tr>
				
			</table>
        </td>
    </tr>
</table>
</form>

</body>
</html>