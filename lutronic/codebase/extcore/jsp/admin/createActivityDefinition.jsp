<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
$(document).ready(function () {
	numberCodeList('eoStep', ''); //eoStep
	getActivityType();
	
})

<%----------------------------------------------------------
*                      NumberCode 리스트 가져오기
----------------------------------------------------------%>
window.numberCodeList = function(id, parentCode1) {
	var type = id;
	
	type = id.toUpperCase();
	var data = common_numberCodeList(type, parentCode1,false);
	
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
			
			$("#"+ id).append("<option value='" + data[i].code + "' title='" + data[i].oid + "' > [" + data[i].code + "] " + data[i].name + "</option>");
		}
	}
}
<%----------------------------------------------------------
*                  활동 타입    selectBodx에 옵션 추가
----------------------------------------------------------%>
window.getActivityType = function(){
	
	var url	= getURLString("changeECA", "getActivityTypeList", "do");
	$.ajax({
		type:"POST",
		url: url,
		dataType:"json",
		async: false,
		cache: false,
		error:function(data){
			var msg = "${f:getMessage('Root 에러')}";
			alert(msg);
		},
		
		success:function(data){
			$("#activeType").append("<option value=''> ${f:getMessage('선택')} </option>");
			for(var i=0; i<data.length; i++) {
				$("#activeType").append("<option value='" + data[i].code + "'>" + data[i].name + "</option>");
			}
		}
		
	});
	
}
$(function() {
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$("#createBtn").click(function() {
		
		if($.trim($("#name").val()) == "" ) {
			alert("Name ${f:getMessage('을(를) 입력하세요.')}");
			$('#name').focus();
			return;		
		}
		/*
		if($.trim($("#name_eng").val()) == "" ) {
			alert("${f:getMessage('영문명')} ${f:getMessage('을(를) 입력하세요.')}");
			$('#name_eng').focus();
			return;		
		}
		*/
		if($.trim($("#eoStep").val()) == "" ){
			alert(" eoStep ${f:getMessage('을(를) 선택하세요.')}");
			$('#eoStep').focus();
			return;
		}
		
		if($.trim($("#activeType").val()) == "" ){
			alert(" ${f:getMessage('활동구분')} ${f:getMessage('을(를) 선택하세요.')}");
			$('#activeType').focus();
			return;
		}
		
		if($.trim($("#sortNumber").val()) == "" ) {
			alert("sortNumber ${f:getMessage('을(를) 입력하세요.')}");
			$('#sortNumber').focus();
			return;		
		}
		
		if(!textAreaLengthCheckId('description','2000','${f:getMessage("설명")}')){
			return;
		}
		
		/*
		gfn_StartShowProcessing();
		$("#changeCreateForm").attr("action", getURLString("changeECR", "createECRtAction", "do")).submit()
		*/
		if(confirm("${f:getMessage('등록하시겠습니까?')}")){
			//common_submit("admin", "createActivityDefinitionAction", "createActivityDefinition", "admin_listChangeActivity").submit();
			var form = $("form[name=createActivityDefinition]").serialize();
			var url	= getURLString("admin", "createActivityDefinitionAction", "do");
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
						alert("${f:getMessage('등록 성공하였습니다.')}");
						opener.location.href = getURLString("admin", "admin_listChangeActivity", "do") + "?oid="+$("#oid").val();
						
						self.close();
						//document.location = getURLString(module, movePage, "do");
					}else {
						alert("${f:getMessage('등록 실패하였습니다.')} \n" + data.message);
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
	}),
	<%----------------------------------------------------------
	*                      SEQ 입력 중
	----------------------------------------------------------%>
	$("#sortNumber").keypress(function (event) {
		
			return common_isNumber(event, this);
		
    })
	
})
</script>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="11" topmargin="0" marginwidth="0" marginheight="0">

<form name=createActivityDefinition>
<input type="hidden"	name="oid"		id="oid"		value="<c:out value="${oid }" />">
<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align=center style="padding-bottom:10px">
				<tr> 
					<td height=30 width=99% align=center><B><font color=white></font></B></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td>
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp;${f:getMessage('설계변경')}&nbsp;${f:getMessage('활동')}&nbsp;${f:getMessage('등록')}
					</td>
					<td>
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                	<td>
									<button type="button" name="createBtn" id="createBtn" class="btnCRUD" >
			               				<span></span>
			               				${f:getMessage('등록')}
			               			</button>
								</td>
								<td>
									<button type="button" name="closeBtn" id="closeBtn" class="btnClose" onclick="self.close();">
			               				<span></span>
			               				${f:getMessage('닫기')}
			               			</button>
								</td>
							</tr>
			            </table>
					</td>
				</tr>
				<tr align="center">
					<td valign="top" style="padding:0px 0px 0px 0px" colspan="2">
						<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
							<tr>
								<td height=1 width=100%></td>
							</tr>
						</table>
						<!-- Create Table start -->
						<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
				            <tr>
				            	<td width="30%"></td>
								<td width="70%"></td>
								
				            </tr>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">Name<span class="style1">*</span></td>
								<td class="tdwhiteL" colspan="4">
									<input name="name" id="name" class="txt_field" size="85" style="width:98%"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('영문명')}</td>
								<td class="tdwhiteL" colspan="4">
									<input name="name_eng" id="name_eng" class="txt_field" size="85" style="width:98%"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">Step<span class="style1">*</span></td>
								<td class="tdwhiteL" colspan="4">
									<select id="eoStep" name="eoStep" style="width:40%">
									</select>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('활동구분')}<span class="style1">*</span></td>
								<td class="tdwhiteL" colspan="4">
									<select id="activeType" name="activeType" style="width:40%">
										<!-- 
										<option value="ORDER_NUMBER">진체번</option>
										<option value="REVISE_BOM">도면/BOM 변경</option>
										<option value="BOM_CHANGE">BOM 변경</option>
										<option value="DOCUMENT">산출물등록</option>
										 -->
									</select>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">${f:getMessage('담당자')}<span class="style1">*</span></td>
								<td class="tdwhiteL" colspan="4">
									<input type="hidden" id="activeUser" name="activeUser" value="" />
									<input type="text" id="activeUserName" name="activeUserName" size="30" style="width:30%" value="" class="txt_field" readOnly/>
									
									<a href="JavaScript:searchUser('documentForm','single','activeUser','activeUserName','wtuser')">
										<img src="/Windchill/jsp/portal/images/s_search.gif" border="0" />
									</a>
									
									<a href="JavaScript:clearText('activeUser');clearText('activeUserName');">
										<img src="/Windchill/jsp/portal/images/x.gif" border="0" />
									</a>
								</td>
							</tr>
							<tr bgcolor="ffffff" height=35>
								<td class="tdblueM">sort<span class="style1">*</span>
								<br>(${f:getMessage('숫자만 입력가능')})</td>
								<td class="tdwhiteL" colspan="4">
									<input name="sortNumber" id="sortNumber" class="txt_field" size="85" style="width:98%"/>
								</td>
							</tr>
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">${f:getMessage('설명')}</td>
								<td class="tdwhiteL" >
									<textarea name="description" id="description" rows="5" class="fm_area" style="width:98%" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('설명')}')"></textarea>
								</td>
							</tr>
						</table>
						<!-- Create Table End -->
					</td>
				</tr>
		</td>
	</tr>
</table>

</form>

</body>
</html>