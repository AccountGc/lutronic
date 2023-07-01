<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	numberCodeList('model', '', '');
	
	gfn_InitCalendar("developmentStart", "developmentStartBtn");
	gfn_InitCalendar("developmentEnd", "developmentEndBtn");
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode, value) {
	var type = "";
	if(id == 'partType1' || id == 'partType2' || id =='partType3') {
		type = "PARTTYPE";
	}else {
		type = id.toUpperCase();
	}
	
	var data = common_numberCodeList(type, parentCode, false);
	
	addSelectList(id, eval(data.responseText), value);
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id, data, value){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "' title='" + data[i].name + "'";
			
			if(data[i].code == value) {
				html += " selected";
			}
			
			html += " > [" + data[i].code + "] " + data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}

<%----------------------------------------------------------
*                      selectBodx에 옵션 추가
----------------------------------------------------------%>
window.addSelectList = function(id, data, value){
	$("#"+ id + " option").remove();
	$("#"+ id).append("<option value='' title='' > ${f:getMessage('선택')} </option>");
	if(data.length > 0) {
		for(var i=0; i<data.length; i++) {
			var html = "<option value='" + data[i].code + "' title='" + data[i].name + "'";
			
			if(data[i].code == value) {
				html += " selected";
			}
			
			html += " > [" + data[i].code + "] " + data[i].name + "</option>";
			
			$("#"+ id).append(html);
		}
	}
}

$(function() {
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$("#createBtn").click(function() {
		if($("#model").val() == "") {
			alert("${f:getMessage('프로젝트코드')}${f:getMessage('을(를) 선택하세요.')}");
			$("#model").focus();
			return;
		}
		
		if($.trim($('#name').val()) == "") {
			alert("${f:getMessage('프로젝트명')}${f:getMessage('을(를) 입력하세요.')}");
			$("#name").focus();
			return;
		}
		
		if($.trim($("#developmentStart").val()) == "") {
			alert("${f:getMessage('예상 사직일')}${f:getMessage('을(를) 선택하세요.')}");
			$("#developmentStart").focus();
			return;
		}else {
			if(!gfn_compareDateToDay($("#developmentStart").val())){
				alert("${f:getMessage('예상 시작일')}${f:getMessage('을(를) 확인하세요.')} ${f:getMessage('오늘')}${f:getMessage('보다 빠를 수 없습니다.')}");
				$("#developmentStart").focus();
				return;
			}
		}
		
		if($.trim($("#developmentEnd").val()) == "") {
			alert("${f:getMessage('예상 종료일')}${f:getMessage('을(를) 선택하세요.')}");
			$("#developmentEnd").focus();
			return;
		}else {
			if(!gfn_compareDate($("#developmentStart").val(), $("#developmentEnd").val())){
				alert("${f:getMessage('예상 종료일')}${f:getMessage('을(를) 확인하세요.')} ${f:getMessage('예상 시작일')} start${f:getMessage('보다 빠를 수 없습니다.')}");
				$("#developmentEnd").focus();
				return;
			}
		}
		
		if($("#dm").val() == "") {
			alert("DM${f:getMessage('을(를) 선택하세요.')}");
			$("#dm").focus();
			return;
		}
		
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			common_submit("development", "createDevelopmentAction", "developmentCreateForm", "listDevelopment");
		}
	})
	<%----------------------------------------------------------
	*                      목록버튼
	----------------------------------------------------------%>
	$("#listBtn").click(function() {
		location.href = getURLString("development", "listDevelopment", "do");
	})
})

</script>

<form name="developmentCreateForm" id="developmentCreateForm" method="post" >

<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
		<tr>
			<td align="left" valign=top height=42>
				<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
					<tr>
						<td></td>
						<td>
							<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' /> 
							${f:getMessage('개발업무')}
							${f:getMessage('관리')} 
							> 
							${f:getMessage('개발업무')}
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
				${f:getMessage('개발업무')}
				${f:getMessage('등록')}
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
			
				<%-- 
				<tr bgcolor="ffffff" height="35">
				
					<td class="tdblueM">
						${f:getMessage('업무명')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<input type='text' name='name' id='name' class='txt_field' maxlength="80" style='width: 95%'>
					</td>
					
				</tr>
				--%>
				
				<tr bgcolor="ffffff" height="35">
				
					<td class="tdblueM">
						${f:getMessage('프로젝트코드')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<select id='model' name='model' style="width: 95%">
						</select>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('프로젝트명')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<%-- 
							<span id='modelName'></span>
						--%>	
						<input type='text' name='name' id='name' class='txt_field' maxlength="80" style='width: 95%'>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('예상 시작일')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<input name="developmentStart" id="developmentStart" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="developmentStartBtn" id="developmentStartBtn" >
						</a>
						
						<a href="JavaScript:clearText('developmentStart')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
					
					<td class="tdblueM">
						${f:getMessage('예상 종료일')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<input name="developmentEnd" id="developmentEnd" class="txt_field" size="12"  maxlength=15 readonly value=""/>
						
						<a href="javascript:void(0);">
							<img src="/Windchill/jsp/portal/images/calendar_icon.gif" border=0 name="developmentEndBtn" id="developmentEndBtn" >
						</a>
						
						<a href="JavaScript:clearText('developmentEnd')">
							<img src="/Windchill/jsp/portal/images/x.gif" border=0>
						</a>
					</td>
				</tr>
			
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('관리자')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL" colspan="3">
					
						<jsp:include page="/eSolution/common/userSearchForm.do">
							<jsp:param value="single" name="searchMode"/>
							<jsp:param value="dm" name="hiddenParam"/>
							<jsp:param value="dmName" name="textParam"/>
							<jsp:param value="wtuser" name="userType"/>
							<jsp:param value="" name="returnFunction"/>
						</jsp:include>
					
						<%-- 
						<input type='hidden' name='dm' id='dm' style='width:90%'>
						<input type='text' name='dmName' id='dmName' class="txt_field" style='width: 8%' readOnly>
						
						<a href="javascript:searchUser('developmentCreateForm','single','dm','dmName','wtuser')">
							<img src='/Windchill/jsp/portal/images/s_search.gif' border=0>
						</a>
						
						<a href="javascript:clearText('dm');clearText('dmName');">
							<img src='/Windchill/jsp/portal/images/x.gif' border=0>
						</a>
						--%>
					</td>
				</tr>
			
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('설명')}
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<textarea name="description" id="description" cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('설명')}')"></textarea>
					</td>
				</tr>
			</table>
        </td>
    </tr>
</table>

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
