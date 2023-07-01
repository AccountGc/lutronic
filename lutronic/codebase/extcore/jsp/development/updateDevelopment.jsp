<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/css/e3ps.css">

<script type="text/javascript">

$(document).ready(function() {
	numberCodeList('model', '', '<c:out value="${masterData.model }"/>');
	
	gfn_InitCalendar("developmentStart", "developmentStartBtn");
	gfn_InitCalendar("developmentEnd", "developmentEndBtn");
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
function numberCodeList(id, parentCode, value) {
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
function addSelectList(id, data, value){
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
	*                      수정버튼
	----------------------------------------------------------%>
	$("#updateDevBtn").click(function() {
		if($("#model").val() == "") {
			alert("${f:getMessage('프로젝트코드')}${f:getMessage('을(를) 선택하세요.')}");
			$("#model").focus();
			return;
		}
		
		if($.trim($("#name").val()) == "") {
			alert("${f:getMessage('프로젝트명')}${f:getMessage('을(를) 입력하세요.')}");
			$("#name").focus();
			return;
		}
		
		if($.trim($("#developmentStart").val()) == "") {
			alert("${f:getMessage('예상 시작일')} start${f:getMessage('을(를) 선택하세요.')}");
			$("#developmentStart").focus();
			return;
		}else {
			if(!gfn_compareDateToDay($("#developmentStart").val())){
				alert("${f:getMessage('예상 시작일')} start${f:getMessage('을(를) 확인하세요.')} ${f:getMessage('오늘')}${f:getMessage('보다 빠를 수 없습니다.')}");
				$("#developmentStart").focus();
				return;
			}
		}
		
		if($.trim($("#developmentEnd").val()) == "") {
			alert("${f:getMessage('예상 종료일')} end${f:getMessage('을(를) 선택하세요.')}");
			$("#developmentEnd").focus();
			return;
		}else {
			if(!gfn_compareDate($("#developmentStart").val(), $("#developmentEnd").val())){
				alert("${f:getMessage('예상 종료일')} end${f:getMessage('을(를) 확인하세요.')} ${f:getMessage('예상 시작일')} start${f:getMessage('보다 빠를 수 없습니다.')}");
				$("#developmentEnd").focus();
				return;
			}
		}
		
		if($("#dm").val() == "") {
			alert("${f:getMessage('관리자')}${f:getMessage('을(를) 선택하세요.')}");
			$("#dm").focus();
			return;
		}
		
		if (confirm("${f:getMessage('수정하시겠습니까?')}")){
			var form = $("form[name=updateDevelopmentForm]").serialize();
			var url	= getURLString("development", "updateDevelopmentAction", "do");
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
						divPageLoad("devBody", "viewDevelopment", $('#oid').val());
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
	})<%----------------------------------------------------------
	*                      이전 페이지 버튼
	----------------------------------------------------------%>
	$("#backBtn").click(function() {
		divPageLoad("devBody", "viewDevelopment", $("#oid").val());
	})
})

</script>

<form name="updateDevelopmentForm" id="updateDevelopmentForm" method="post" >

<input type="hidden" name="oid" id="oid" value="<c:out value="${masterData.oid }"/>" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td>
						<img src="/Windchill/jsp/portal/img/bt_01.gif" >
						<b>
							${f:getMessage('개발업무')}
							${f:getMessage('수정')}
						</b>
					</td>
					
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
								<td>
									<button type="button" name="updateDevBtn" id="updateDevBtn" class="btnCRUD">
										<span></span>
										${f:getMessage('수정')}
									</button>
								</td>
								
								<td>
									<button type="button" name="backBtn" id="backBtn" class="btnCustom">
										<span></span>
										${f:getMessage('이전페이지')}
									</button>
								</td>
								
							</tr>
			            </table>
			            <!-- 버튼 테이블 끝 -->
			            
					</td>
				</tr>
				
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
								<tr><td height="1" width="100%"></td></tr>
						</table>
						
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
							<col width="150">
							<col width="350">
							<col width="150">
							<col width="350">
							
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
								</TD>
								
								<td class="tdwhiteL">
									<input type='text' name='name' id='name' class='txt_field' value='<c:out value="${masterData.name}"/>' maxlength="80" style='width: 95%'>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('예상 시작일')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL">
									<input name="developmentStart" id="developmentStart" class="txt_field" size="12"  maxlength=15 readonly value="<c:out value="${masterData.developmentStart }"/>"/>
						
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
									<input name="developmentEnd" id="developmentEnd" class="txt_field" size="12"  maxlength=15 readonly value="<c:out value="${masterData.developmentEnd}"/>"/>
						
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
									<input type='hidden' name='dm' id='dm' style='width:90%' value='<c:out value="${masterData.dmOid }"/>'>
									<input type='text' name='dmName' id='dmName' value='<c:out value="${masterData.dmName }"/>' class="txt_field" style='width: 8%' readOnly>
									
									<a href="javascript:searchUser('updateDevelopmentForm','single','dm','dmName','wtuser')">
										<img src='/Windchill/jsp/portal/images/s_search.gif' border=0>
									</a>
									
									<a href="javascript:clearText('dm');clearText('dmName');">
										<img src='/Windchill/jsp/portal/images/x.gif' border=0>
									</a>
								</td>
							</tr>

							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('설명')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<textarea name="description" id="description" cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('설명')}')"><c:out value="${masterData.getDescription(false) }"/></textarea>
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