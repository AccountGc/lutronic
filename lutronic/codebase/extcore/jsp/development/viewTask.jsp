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
	*                      Task 수정 버튼
	----------------------------------------------------------%>
	$("#updateTaskBtn").click(function() {
		divPageLoad("TaskInfo", "updateTask", $("#taskOid").val());
	})
	<%----------------------------------------------------------
	*                      Task 삭제 버튼
	----------------------------------------------------------%>
	$("#deleteTaskBtn").click(function() {
		if (confirm("${f:getMessage('삭제하시겠습니까?')}")){
			var form = $("form[name=viewTaskForm]").serialize();
			var url	= getURLString("development", "deleteTaskAction", "do");
			$.ajax({
				type:"POST",
				url: url,
				data: {
					oid : $("#taskOid").val()
				},
				dataType:"json",
				async: true,
				cache: false,
				error: function(data) {
					alert("${f:getMessage('삭제 오류 발생')}");
				},
				success:function(data){
					alert(data.message);
					if(data.result) {
						divPageLoad("taskDiv", "viewTaskList", $('#oid').val());
						divPageLoad("viewTask", '', '');
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
	*                      Task 완료 취소 버튼
	----------------------------------------------------------%>
	$('#cancelTaskBtn').click(function() {
		changeState($('#taskOid').val(), 'PROGRESS', 2);
	})
	<%----------------------------------------------------------
	*                      Task 완료 버튼
	----------------------------------------------------------%>
	$('#completeTaskBtn').click(function() {
		changeState($('#taskOid').val(), 'COMPLETED', 2);
	})
})

</script>

<body>

<form name="viewTaskForm" id="viewTaskForm" method="post" >

<input type="hidden" name="taskOid" id="taskOid" value="<c:out value="${devTaskData.oid }"/>" />

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr>
		<td align="left" style='height: 20px'>
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>
				TASK
				${f:getMessage('상세보기')}
			</b>
		</td>
		
		<td align="right">
		
			<c:if test='${devTaskData.isAdmin() || devTaskData.isDm()}'>
				<c:if test='${devTaskData.isState("COMPLETED")}'>
					<button type="button" name="cancelTaskBtn" id="cancelTaskBtn" class="btnCRUD">
						<span></span>
						${f:getMessage('완료 취소')}
					</button>
				</c:if>
				
				<c:if test='${devTaskData.isState("PROGRESS")}'>
					<button type="button" name="completeTaskBtn" id="completeTaskBtn" class="btnCRUD">
						<span></span>
						${f:getMessage('완료')}
					</button>
		
					<button type="button" name="updateTaskBtn" id="updateTaskBtn" class="btnCRUD">
						<span></span>
						${f:getMessage('수정')}
					</button>
					
					<c:if test="${!devTaskData.isDelete() }">
					
						<button type="button" name="deleteTaskBtn" id="deleteTaskBtn" class="btnCRUD">
							<span></span>
							${f:getMessage('삭제')}
						</button>
					
					</c:if>
			
				</c:if>
			</c:if>
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
					</td>
					
					<td class="tdwhiteL">
						<c:out value="${devTaskData.name }"/>
					</td>
					
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('상태')}
					</td>
					<td class="tdwhiteL">
						<c:out value="${devTaskData.state }"/>
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height="35">
					<td class="tdblueM">
						${f:getMessage('설명')}
					</td>
					<td class="tdwhiteL">
						<c:out value="${devTaskData.getDescription(true) }" escapeXml="false"/>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
</table>

</form>
</body>
</html>