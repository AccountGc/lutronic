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
	gfn_InitCalendar("activeDate", "activeDateBtn");
})


$(function() {
	<%----------------------------------------------------------
	*                      Activity 수정 버튼
	----------------------------------------------------------%>
	$('#updateBtn').click(function() {
		if($.trim($('#activeName').val()) == '') {
			alert("ACTIVITY${f:getMessage('명')}${f:getMessage('을(를) 입력하세요.')}");
			$('#activeName').focus();
			return;
		}
		if($.trim($('#activeDate').val()) == '') {
			alert("${f:getMessage('완료요청 완료일')}${f:getMessage('을(를) 선택하세요.')}");
			$('#activeDate').focus();
			return;
		}
		/*
		else {
			if(!gfn_compareDateToDay($('#activeDate').val())){
				alert("${f:getMessage('요청 완료일')} ${f:getMessage('을(를) 확인하세요.')} ${f:getMessage('오늘')}${f:getMessage('보다 빠를 수 없습니다.')}");
				$('#activeDate').focus();
				return;
			}
		}
		*/
		if($.trim($('#activeWorker').val()) == '') {
			alert("${f:getMessage('수행자 ')}${f:getMessage('을(를) 선택하세요.')}");
			$('#activeWorker').focus();
			return;
		}
		
		if (confirm("${f:getMessage('수정하시겠습니까?')}")){
			var form = $("form[name=updateActiveForm]").serialize();
			var url	= getURLString("development", "updateActiveAction", "do");
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
						location.href = getURLString("development", "viewActive", "do") + "?oid="+data.oid;
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
	})
})

</script>

<body>

<form name="updateActiveForm" id="updateActiveForm" >

<input type="hidden" name="oid" id="oid" value="<c:out value="${devActiveData.oid }"/>" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
	<tr height="5">
		<td>
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center" style="padding-bottom:10px">
				<tr> 
					<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
				</tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="3">
				<tr>
					<td >
						<img src="/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif" width="10" height="9" />
						ACTIVITY
						${f:getMessage('수정')}
					</td>
					
					<td>
						<!-- 버튼 테이블 시작 -->
						<table border="0" cellpadding="0" cellspacing="4" align="right">
			                <tr>
								<td>
									<button type="button" name="updateBtn" id="updateBtn" class="btnCRUD">
										<span></span>
										${f:getMessage('수정')}
									</button>
								</td>
							
								<td>
									<button type="button" name="" id="" class="btnCustom" onclick="javascript:history.back();">
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
									ACTIVITY${f:getMessage('명')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL">
									<input type='text' name='activeName' id='activeName' class='txt_field' style='width: 95%' value='<c:out value="${devActiveData.name }"/>' />
								</td>
								
								<td class="tdblueM">
									${f:getMessage('수행자')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL">
									<input type='hidden' name='activeWorker' id='activeWorker' value='<c:out value="${devActiveData.workerOid }" />' style='width:90%'>
									<input type='text' name='activeWorkerName' id='activeWorkerName' value='<c:out value="${devActiveData.workerName }" />' class="txt_field" style='width: 17%' readOnly>
									<a href="javascript:searchUser('updateActiveForm','single','activeWorker','activeWorkerName','wtuser')">
										<img src='/Windchill/jsp/portal/images/s_search.gif' border=0>
									</a>
									<a href="javascript:clearText('activeWorker');clearText('activeWorkerName');">
										<img src='/Windchill/jsp/portal/images/x.gif' border=0>
									</a>
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('완료 요청일')}
									<span class="style1">*</span>
								</td>
								
								<td class="tdwhiteL">
									<input type='text' name='activeDate' id='activeDate' value='<c:out value="${devActiveData.activeDate }" />' size='12' maxlength='15' readonly>
									
									<a href='javascript:void(0);'>
										<img src='/Windchill/jsp/portal/images/calendar_icon.gif' border=0 name='activeDateBtn' id='activeDateBtn' >
									</a>
									<a href="javascript:clearText('activeDate');">
										<img src='/Windchill/jsp/portal/images/x.gif' border=0>
									</a>
								</td>
								
								<td class="tdblueM">
									${f:getMessage('완료일')}
								</td>
								
								<td class="tdwhiteL">
									<c:out value="${devActiveData.finishDate }" />
								</td>
							</tr>
							
							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('설명')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<textarea name="description" id="description" cols="88px" rows="5" class="fm_area" style="width:90%" style="overflow:auto" wrap="hard" onchange="textAreaLengthCheckName('description', '4000', '${f:getMessage('설명')}')"><c:out value="${devActiveData.getDescription(false) }" escapeXml="false"/></textarea>
								</td>
							</tr>

							<tr bgcolor="ffffff" height="35">
								<td class="tdblueM">
									${f:getMessage('첨부파일')}
								</td>
								
								<td class="tdwhiteL" colspan="3">
									<jsp:include page="/eSolution/content/includeAttachFiles.do" flush="true">
										<jsp:param name="formId" value="updateActiveForm"/>
										<jsp:param name="btnId" value="updateBtn" />
									</jsp:include>
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