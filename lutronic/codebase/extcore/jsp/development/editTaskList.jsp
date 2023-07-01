<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<script type="text/javascript" src="/Windchill/jsp/js/common.js"></script>
<link rel="stylesheet" type="text/css" href="/Windchill/jsp/css/e3ps.css">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<script type="text/javascript">

var count=$("#taskRows tr").length;

$(document).ready(function() {
})

$(function() {
	<%----------------------------------------------------------
	*                      + 버튼
	----------------------------------------------------------%>
	$("#addRows").click(function() {
		var length = $("#taskRows tr").length;
		
		var html = "";
		html += "<tr>";
		html += "	<td class='tdwhiteM' width='3%'>";
		html += "		<input type='checkbox' name='delRow' value=''>";
		html += "	</td>";
		html += "	<td class='tdwhiteL' width='30%'>";
		html += "		<input type='text' name='taskNames' id='taskName_" + count + "' style='width:90%' value='' class='taskNames'>";
		html += "	</td>";
		html += "	<td class='tdwhiteL' width='*'>";
		html += "		<input type='text' name='taskDescriptions' id='taskDescription_" + count + "' style='width:90%'>";
		html += "	</td>";
		html += "</tr>";
		
		$("#taskRows").append(html);
		
		count = count + 1;
	})
	<%----------------------------------------------------------
	*                      - 버튼
	----------------------------------------------------------%>
	$("#delRows").click(function() {
		$("input[name=delRow]").each(function() {
			if(this.checked) {
				if(this.value != '') {
					$('#deleteTaskOids').val($('#deleteTaskOids').val() + this.value + ",");
				}
				$(this).parent().parent().remove();
			}
		})
		$('#checkAll').prop('checked', '');
	})
	<%----------------------------------------------------------
	*                      모두 체크 체크박스
	----------------------------------------------------------%>
	$('#checkAll').click(function() {
		if(this.checked) {
			$("input[name='delRow']").prop("checked", "checked");
		}else {
			$("input[name='delRow']").prop("checked", "");
		}
	})
	<%----------------------------------------------------------
	*                      취소 버튼
	----------------------------------------------------------%>
	$("#cancelTask").click(function() {
		divPageLoad("taskDiv", "viewTaskList", $('#oid').val());
	})
	<%----------------------------------------------------------
	*                      저장 버튼
	----------------------------------------------------------%>
	$('#saveTask').click(function() {
		
		var length = $("#taskRows tr").length;
		
		if(length == 0) {
			alert("TASK${f:getMessage('을(를) 추가하세요.')}");
			return;
		}
		
		for(var i=0; i<length; i++){
			if($.trim($('.taskNames').eq(i).val()) == '') {
				alert("TASK${f:getMessage('명')}${f:getMessage('을(를) 입력하세요.')}");
				$('.taskNames').eq(i).focus();
				return;
			}
		}
		
		if (confirm("${f:getMessage('저장하시겠습니까?')}")){
			var form = $("form[name=editTaskForm]").serialize();
			var url	= getURLString("development", "editTaskAction", "do");
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
				}
				,success:function(data){
					alert(data.message);
					if(data.result) {
						divPageLoad('taskDiv', 'viewTaskList', $('#oid').val());
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
})

</script>

<form id="editTaskForm" name="editTaskForm" >

<input type='hidden' name='deleteTaskOids' id='deleteTaskOids' value='' />
<input type="hidden" name="oid" id="oid"	value="${oid}" />

<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	        
    <tr>
		<td align="left" style="width: 8%">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>TASK</b>
		</td>
		
		<td>
			<img alt="" src="/Windchill/jsp/portal/images/icon/shu_plus.gif" id="addRows" style="width: 15px; height: 15px; cursor: pointer;">
			<img alt="" src="/Windchill/jsp/portal/images/icon/shu_minus.gif" id="delRows" style="width: 15px; height: 15px; cursor: pointer;">
		</td>
		
		<td align="right">
			<button type="button" name="saveTask" id="saveTask" class="btnCRUD">
				<span></span>
				${f:getMessage('저장')}
			</button>
			<button type="button" name="cancelTask" id="cancelTask" class="btnCustom">
				<span></span>
				${f:getMessage('취소')}
			</button>
		</td>
		
	</tr>
	
	<tr>
		<td colspan="3">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
			
				<tr>
					<td class="tdblueM"  width="3%">
						<input type='checkbox' id="checkAll" value=''>
					</td>
					
					<td class="tdblueM"  width="30%">
						TASK${f:getMessage('명')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdblueM0" width="*">
						${f:getMessage('설명')}
					</td>
				</tr>
				
				<tbody id="taskRows">
					<c:if test="${fn:length(list) != 0 }">
						<c:forEach items="${list }" var="DevTaskData">
							<tr align="center" bgcolor="#FFFFFF"> 
								<td class="tdwhiteM">
									
									<c:if test="${!DevTaskData.isDelete() }">
										<input type='checkbox' name='delRow' value='<c:out value="${DevTaskData.oid }" />'>
									</c:if>
									<input type='hidden' name='taskOids' id='taskOids' value='<c:out value="${DevTaskData.oid }" />' />
								</td>
								
								<td class="tdwhiteL"  width="35%">
									<input type='text' name='<c:out value="${DevTaskData.oid }" />_taskNames' id='<c:out value="${DevTaskData.oid }" />_taskNames' 
									style='width:90%' value='<c:out value="${DevTaskData.name }" />' class='taskNames'>
								</td>
								
								<td class="tdwhiteL"  width="50%">
									<input type='text' name='<c:out value="${DevTaskData.oid }" />_taskDescriptions' id='<c:out value="${DevTaskData.oid }" />_taskDescriptions' 
									style='width:90%' value='<c:out value="${DevTaskData.description }" />'>
								</td>
								
							</tr>
						</c:forEach>
					</c:if>
				
				</tbody>
							
			</table>
		</td>
	</tr>
</table>

</form>

</html>