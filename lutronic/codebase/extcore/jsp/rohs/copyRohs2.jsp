<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/extcore/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

$(document).ready(function() {
	numberCodeList('manufacture', '');
})

$(function() {
	<%----------------------------------------------------------
	*                      등록 버튼
	----------------------------------------------------------%>
	$('#copyRohs').click(function() {
		if($.trim($("#rohsName").val()) == '') {
			alert("${f:getMessage('물질명')}${f:getMessage('을(를) 입력하세요.')}");
			$("#rohsName").focus();
			return;
		}
		
		if($("input[name='lifecycle']:checked").length == 0 ){
			alert("${f:getMessage('결재방식')} ${f:getMessage('을(를) 선택하세요.')}");
			return;
		}
		
		if($("#manufacture").val() == '') {
			alert("${f:getMessage('협력업체')}${f:getMessage('을(를) 선택하세요.')}");
			$("#manufacture").focus();
			return;
		}
		if(confirm("${f:getMessage('복사하시겠습니까?')}")){
			var form = $("form[name=copyRohsForm]").serialize();
			var url	= getURLString("rohs", "copyRohsAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: form,
				dataType:"json",
				async: true,
				cache: false,

				success:function(data){
					if(data.result) {
						alert('${f:getMessage('복사완료되었습니다.')}');
					}else {
						alert(data.message);
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
	
	$('#dupName').click(function() {
		var rohsName = $('#rohsName').val();
		var url	= getURLString("rohs", "duplicateName", "do");
		$.ajax({
			type:"POST",
			url: url,
			data: {
				rohsName : rohsName
			},
			dataType:"json",
			async: true,
			cache: false,

			success:function(data){
				alert(data.message);
			}
		});
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
})

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

</script>

<body>

<form name="copyRohsForm" id="copyRohsForm" method="post" >

<input type="hidden" name="oid" id="oid" value="<c:out value="${oid }" />" />

<input type="hidden" name="docType"			id="docType"				value="$$ROHS"/>
<input type="hidden" name="location"		id="location"				value="/Default/ROHS" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"><c:out value="${rohsData.number }"/> 복사</font></B></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td>
						<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
						RoHS${f:getMessage('복사')}
					</td>
					
					<td>
					
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
			                
			                	<td>
									<button type="button" name="copyRohs" id="copyRohs" class="btnCRUD">
										<span></span>
										${f:getMessage('복사')}
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
									${f:getMessage('물질번호')} 
									<button id="NumberCheck" class="btnSearch" type="button">
									<span></span>
									${f:getMessage('번호 중복')}
									</button>
								</td>
								
								<td class="tdwhiteL">
									<input name="rohsNumber" id="rohsNumber" class="txt_field" size="85" style="width:90%" maxlength="80"/>
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
									${f:getMessage('물질명')}
									<span class="style1">*</span>
									<!-- 
									<button type='button' name='dupName' id='dupName' class='btnCustom' >
										<span></span>
										${f:getMessage('중복확인')}
									</button>
									 -->
									<button id="NameCheck" class="btnSearch" type="button">
										<span></span>
										${f:getMessage('물질명 중복')}
									</button>
								</td>
								
								<td class="tdwhiteL">
									<input name="rohsName" id="rohsName" class="txt_field" size="85" style="width:90%" maxlength="80" value='<c:out value="${rohsData.name }"/>'/>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('협력업체')} <span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL">
									<select id='manufacture' name='manufacture' style="width: 95%"> 
									</select>
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
</body>
</html>