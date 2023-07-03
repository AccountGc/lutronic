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
})


$(function() {
	<%----------------------------------------------------------
	*                      TASK 수정 버튼
	----------------------------------------------------------%>
	$("#updateTaskBtn").click(function() {
		if($.trim($('#taskName').val()) == '') {
			alert("TASK${f:getMessage('명')}${f:getMessage('을(를) 입력하세요.')}");
			$('#taskName').focus();
			return;
		}
		if (confirm("${f:getMessage('수정하시겠습니까?')}")){
			var form = $("form[name=updateTaskForm]").serialize();
			var url	= getURLString("development", "updateTaskAction", "do");
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
						divPageLoad("TaskInfo", "viewTask", $("#taskOid").val());
						divPageLoad("taskDiv", "viewTaskList", $("#oid").val());
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
	<%----------------------------------------------------------
	*                      이전 페이지 버튼
	----------------------------------------------------------%>
	$('#backTaskBtn').click(function() {
		divPageLoad("TaskInfo", "viewTask", $("#taskOid").val());
	})
})

</script>

<body>

<form name="updateTaskForm" id="updateTaskForm">

<input type="hidden" name="taskOid" id="taskOid" value="<c:out value="${devTaskData.oid }"/>" />

<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr>
		<td>
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				TASK
				${f:getMessage('수정')}
			</b>
		</td>
		
		<td align="right">
			<button type="button" name="updateTaskBtn" id="updateTaskBtn" class="btnCRUD">
				<span></span>
				${f:getMessage('수정')}
			</button>
			<button type="button" name="backTaskBtn" id="backTaskBtn" class="btnCustom">
				<span></span>
				${f:getMessage('이전페이지')}
			</button>
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
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						TASK${f:getMessage('명')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdwhiteL">
						<input type='text' name='taskName' id='taskName' value='<c:out value="${devTaskData.name }"/>' style='width: 95%' />
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('설명')}
					</td>
					
					<td class="tdwhiteL">
						<textarea name="taskDescription" id="taskDescription" cols="10" rows="5" class="fm_area" style="width:90%" onchange="textAreaLengthCheckName('taskDescription', '4000', '${f:getMessage('설명')}')"><c:out value="${devTaskData.getDescription(false) }"/></textarea>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</form>

</body>
</html>