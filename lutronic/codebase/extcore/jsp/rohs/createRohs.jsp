<%@page import="com.e3ps.rohs.service.RohsUtil"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<style type="text/css">

</style>
<script type="text/javascript">
$(document).ready(function() {
	numberCodeList('manufacture', '');
	
});
function date_mask(formd, textid) {
	

	/*
	input onkeyup에서
	formd == this.form.name
	textid == this.name
	*/

	var form = eval("document."+formd);
	var text = eval("form."+textid);

	var textlength = text.value.length;

	if (textlength == 4) {
	text.value = text.value + "-";
	} else if (textlength == 7) {
	text.value = text.value + "-";
	} else if (textlength > 9) {
	//날짜 수동 입력 Validation 체크
	var chk_date = checkdate(text);

	if (chk_date == false) {
	return;
	}
	}
	}

	 

	function checkdate(input) {
	   var validformat = /^\d{4}\-\d{2}\-\d{2}$/; //Basic check for format validity 
	   var returnval = false;
	   if (!validformat.test(input.value)) {
	    alert("날짜 형식이 올바르지 않습니다. YYYY-MM-DD");
	   } else { //Detailed check for valid date ranges 
	    var yearfield = input.value.split("-")[0];
	    var monthfield = input.value.split("-")[1];
	    var dayfield = input.value.split("-")[2];
	    var dayobj = new Date(yearfield, monthfield - 1, dayfield);
	   }
	   if ((dayobj.getMonth() + 1 != monthfield)
	     || (dayobj.getDate() != dayfield)
	     || (dayobj.getFullYear() != yearfield)) {
	    alert("날짜 형식이 올바르지 않습니다. YYYY-MM-DD");
	   } else {
	    //alert ('Correct date'); 
	    returnval = true;
	   }
	   if (returnval == false) {
	    input.select();
	   }
	   return returnval;
	  }
<%----------------------------------------------------------
*                      페이지 이동 function
----------------------------------------------------------%>
window.divPageLoad = function(divId,roleType){
	var btnId = $('.btnCRUD').attr('id');
	var url	= getURLString("content", "includeAttachFiles", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			formId : 'rohsCreateForm',
			type : roleType,
			btnId : btnId
		},
		success:function(data){
			$('#' + divId).html(data);
		}
	});
}

window.rohsFileType = function(id) {
	var url	= getURLString("rohs", "rohsFileType", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: true,
		cache: false,

		error:function(data){
			var msg = "${f:getMessage('데이터 검색오류')}";
			alert(msg);
		},

		success:function(data){
			addSelectList(id, data, '');
		}
	});
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
	
	$("#" + id + " option").remove();
	$("#" + id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "'>";
			html += " [" + data[i].code + "] ";
			html += data[i].name + "</option>";
			$("#" + id).append(html);
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
		
		if($.trim($("#rohsName").val()) == '') {
			alert("${f:getMessage('물질명')}${f:getMessage('을(를) 입력하세요.')}");
			$("#rohsName").focus();
			return;
		}
		if($("#manufacture").val() == '') {
			alert("${f:getMessage('협력업체')}${f:getMessage('을(를) 선택하세요.')}");
			$("#manufacture").focus();
			return;
		}
		
		var length = $('input[name=roleType]').length;
		for(var i=0; i<length; i++) {
			
			var fileValue = $('input[name=roleType]').eq(i).val();
			if($('#' + fileValue).length == 0) {
				alert("${f:getMessage('파일')}${f:getMessage('을(를) 첨부하세요.')}");
				return;
			}
			if($('.fileType').eq(i).val() == '') {
				alert("${f:getMessage('파일구분')}${f:getMessage('을(를) 선택하세요.')}");
				$('.fileType').eq(i).focus();
				return;
			}
			if($('.fileType').eq(i).val() == 'TR' && $('.date').eq(i).val() == '') {
				alert("${f:getMessage('발행일')}${f:getMessage('을(를) 선택하세요.')}");
				$('.date').eq(i).focus();
				return;
			}
			
		}
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("rohs", "createRohsAction", "rohsCreateForm", "listRohs");
		}
		
	})
	<%----------------------------------------------------------
	*                      목록버튼
	----------------------------------------------------------%>
	$("#listBtn").click(function() {
		location.href = getURLString("rohs", "listRohs", "do");
	})
	
	<%----------------------------------------------------------
	*                      첨부파일 추가
	----------------------------------------------------------%>
	$("#addFile").click(function() {
		var id = getRoleTypeValue();
		var length = $('#filesBody tr').length;
		if(id < 21) {

			var html = ""
			html += "<tr>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='fileDelete' id='fileDelete' value='ROHS" + id + "' >";
			html += "		<input type='hidden' name='roleType' value='ROHS" + id + "' />"
			html += "	</td>";
			html += "	<td class='tdwhiteL'>";
			html += "		<div id='ROHS" + id + "_div'>";
			html += "		</div>";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += "		<select id='ROHS" + id + "_fileType' name='ROHS" + id + "_fileType' class='fileType' style='width: 90%'>"; 
			html += "		</select>";
			html += "	</td>";
			html += "	<td class='tdwhiteL'>";
			html += "		<input name='ROHS" + id + "_date' id='ROHS" + id + "_date' class='txt_field date' onkeyup='javascript:date_mask(this.form.name, this.name);'  onblur =\"checkDate('ROHS" + id + "_date',this.value)\" size='12' maxlength='15'/>";
			html += "		<a href='javascript:void(0);'>";
			html += "			<img src='/Windchill/jsp/portal/images/calendar_icon.gif' border='0' id='ROHS" + id + "_date_btn' >";
			html += "		</a>";
			html += "		<a href=JavaScript:clearText('ROHS" + id + "_date');>";
			html += "			<img src='/Windchill/jsp/portal/images/x.gif' border=0>";
			html += "		</a>";
			html += "	</td>";
			html += "</tr>";
				
			$("#filesBody").append(html);
			
			divPageLoad('ROHS'+ id + '_div', 'ROHS' + id);
			gfn_InitCalendar("ROHS" + id + "_date", "ROHS" + id + "_date_btn");
			rohsFileType('ROHS' + id + '_fileType');
		}else {
			//20161108 PJT EDIT
			//alert("{f:message('20개를 초과하여 추가할 수 없습니다.')}"); 
			alert("${f:getMessage('20개를 초과하여 추가할 수 없습니다.')}");
			
		}
	})
	$('#delFile').click(function() {
		var obj = $("input[name='fileDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				var fileId = obj.eq(i).val();
				$('#' + fileId).remove();
				obj.eq(i).parent().parent().remove();
			}
		}
	})
	
	<%----------------------------------------------------------
	*                      번호 중복
	----------------------------------------------------------%>
	$("#NumberCheck").click(function() {
		var url = getURLString("rohs", "checkList", "do") + "?rohsNumber="+$("#rohsNumber").val();
		openOtherName(url,"numberCheck","900","450","status=no,scrollbars=yes,resizable=yes");
	})
	
	<%----------------------------------------------------------
	*                      물질명 중복
	----------------------------------------------------------%>
	$("#NameCheck").click(function() {
		var url = getURLString("rohs", "checkList", "do") + "?rohsName="+$("#rohsName").val();
		openOtherName(url,"checkList","900","450","status=no,scrollbars=yes,resizable=yes");
	})
	$('#resetBtn').click(function() {
		resetFunctionRoHS();
	})
})

window.getRoleTypeValue = function() {
	for(var i=0; i<20; i++) {
		var obj = $('[value^=ROHS' + (i+1) + ']').val();
		if(obj == 'undefined' || obj == null) {
			return (i+1);
		}
	}
}

</script>

<form name="rohsCreateForm" id="rohsCreateForm" method="post" >

<input type="hidden" name="docType"			id="docType"				value="$$ROHS"/>
<input type="hidden" name="location"		id="location"				value="/Default/ROHS" />

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							&nbsp;
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' /> 
							&nbsp; RoHS${f:getMessage('관리')} > ${f:getMessage('물질등록')}
						</td>
					</tr>
				</table>
			</td>
		</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" > <!--//여백 테이블-->
	<!-- 문서 정보 -->
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>RoHS${f:getMessage('정보')}</b>
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
						${f:getMessage('물질번호')} 
					</td>
					
					<td class="tdwhiteL">
						<input name="rohsNumber" id="rohsNumber" class="txt_field" size="85" style="width:70%" maxlength="80" onchange="textAreaLengthCheckName('rohsNumber', '100', '물질번호')"/>
							&nbsp;<button id="NumberCheck" class="btnSearch" type="button">
							<span></span>
							${f:getMessage('번호 중복')}
							</button>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('결재방식')} <span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						
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
						${f:getMessage('물질명')} <span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<input name="rohsName" id="rohsName" class="txt_field" size="85" style="width:70%" maxlength="80" onchange="textAreaLengthCheckName('rohsName', '200', '물질명')"/>
						&nbsp;<button id="NameCheck" class="btnSearch" type="button">
							<span></span>
							${f:getMessage('물질명 중복')}
							</button>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('협력업체')} <span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<select id='manufacture' name='manufacture' style="width: 95%"> 
						</select>
					</td>
					
				</tr>
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('설명')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
					 <div class="textarea_autoSize">
						<textarea name="description" id="description" cols="10" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('문서')}${f:getMessage('설명')}')"></textarea>
					</div>
					</td>
				</tr>
				
				<tr bgcolor="ffffff">
					<td class="tdblueM">
						${f:getMessage('첨부파일')}
					</td>
					<td class="tdwhiteL" colspan="3">
					<!-- ROHS 파일 START -->
						<table border="0" cellpadding="0" cellspacing="0" style="width: 100%">
							<tr>
								<td>
									<table border="0" cellpadding="0" cellspacing="2">
									    <tr>
									        <td>
									        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('추가')}" id="addFile" name="addFile">
									            	<span></span>
									            	${f:getMessage('추가')}
									           	</button>
									        </td>
									        <td>
									        	<button type="button" name="" value="" class="btnCustom" title="${f:getMessage('삭제')}" id="delFile" name="delFile">
									            	<span></span>
									            	${f:getMessage('삭제')}
									           	</button>
									        </td>
									    </tr>
									</table>
								</td>
							</tr>
							
							<tr>
								<td>
									<table width="100%" cellspacing="0" cellpadding="1" border="0" id="fileTable" align="center">
										<tr>
											<td class="tdblueM" width="5%"></td>
										    <td class="tdblueM" width="*">${f:getMessage('첨부파일')}</td>
										    <td class="tdblueM" width="25%">${f:getMessage('파일구분')} </td>
										  	<td class="tdblueM" width="20%">${f:getMessage('발행일')}</td>
										</tr>
										
										<tbody id='filesBody'>
										
										</tbody>
										
									</table>
								</td>
							</tr>
						</table>
					<!-- ROHS 파일 END-->
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

<!-- 관련 rohs -->
<jsp:include page="/eSolution/rohs/include_RohsSelect.do">
	<jsp:param value="${f:getMessage('관련')} RoHS" name="title"/>
	<jsp:param name="paramName" value="rohsOid"/>
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
