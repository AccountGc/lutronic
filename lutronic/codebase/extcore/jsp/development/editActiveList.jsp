<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<script type="text/javascript">
var count=$("#activeRows tr").length;

$(document).ready(function() {
	var length = $("#activeRows tr").length;
	for(var i=0; i < length; i++) {
		gfn_InitCalendar("activeDates_" + i, "activeDatesBtn_" + i);
	}
})

$(function() {
	<%----------------------------------------------------------
	*                      + 버튼
	----------------------------------------------------------%>
	$("#addRows").click(function() {
		
		var html = "";
		html += "<tr>";
		html += "	<td class='tdwhiteM' width='3%'>";
		html += "		<input type='checkbox' name='delRow' value=''>";
		html += "	</td>";
		html += "	<td class='tdwhiteL' width='30%'>";
		html += "		<input type='text' name='activeNames' id='activeNames_" + count + "' style='width:90%' class='activeNames' >";
		html += "	</td>";
		html += "	<td class='tdwhiteL' width='*'>";
		html += "		<input type='text' name='activeDates' id='activeDates_" + count + "' size='12' maxlength='15' class='activeDates' readonly>";
		html += "		<a href='javascript:void(0);'>";
		html += "			<img src='/Windchill/jsp/portal/images/calendar_icon.gif' border=0 name='activeDatesBtn' id='activeDatesBtn_" + count + "' >";
		html += "		</a>";
		html += "		<a href=javascript:clearText('activeDates_" + count + "');>";
		html += "			<img src='/Windchill/jsp/portal/images/x.gif' border=0>";
		html += "		</a>";
		html += "	</td>";
		html += "	<td class='tdwhiteL' width='45%'>";
		
		/*
		html += "		<div id='userDIV_" + count +"'></div>";
		*/
		
		html += "		<input type='hidden' name='activeWorkers' id='activeWorkers_" + count + "' style='width:90%' class='activeWorkers' >";
		html += "		<input type='text' name='activeWorkersName' id='activeWorkersName_" + count + "' style='width: 80%' readOnly disabled >";
		html += "		<a href=javascript:searchUser('editActiveForm','single','activeWorkers_" + count + "','activeWorkersName_" + count + "','wtuser')>";
		html += "			<img src='/Windchill/jsp/portal/images/s_search.gif' border=0>";
		html += "		</a>";
		html += "		<a href=javascript:clearText('activeWorkers_" + count + "');clearText('activeWorkersName_" + count + "');>";
		html += "			<img src='/Windchill/jsp/portal/images/x.gif' border=0>";
		html += "		</a>";
		html += "	</td>";
		html += "</tr>";
		
		$("#activeRows").append(html);
		
		//userDivLoad(count);
		gfn_InitCalendar("activeDates_" + count, "activeDatesBtn_" + count);
		
		count = count + 1;
		
	})
	<%----------------------------------------------------------
	*                      - 버튼
	----------------------------------------------------------%>
	$("#delRows").click(function() {
		$("input[name=delRow]").each(function() {
			if(this.checked) {
				if(this.value != '') {
					$('#deleteActiveOids').val($('#deleteActiveOids').val() + this.value + ",");
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
	$('#cancelActive').click(function() {
		divPageLoad("activeDiv", "viewActiveList", $('#taskOid').val());
	})
	<%----------------------------------------------------------
	*                      저장 버튼
	----------------------------------------------------------%>
	$('#saveActive').click(function() {
		
		var length = $("#activeRows tr").length;
		
		if(length == 0) {
			alert("ACTIVITY${f:getMessage('을(를) 추가하세요.')}");
			return;
		}
		
		for(var i=0; i<length; i++){
			if($.trim($('.activeNames').eq(i).val()) == '') {
				alert("ACTIVITY${f:getMessage('명')}${f:getMessage('을(를) 입력하세요.')}");
				$('.activeNames').eq(i).focus();
				return;
			}
			if($.trim($('.activeDates').eq(i).val()) == '') {
				alert("${f:getMessage('요청 완료일')}${f:getMessage('을(를) 선택하세요.')}");
				$('.activeDates').eq(i).focus();
				return;
			}
			/*
			else {
				if(!gfn_compareDateToDay($('.activeDates').eq(i).val())){
					alert("${f:getMessage('요청 완료일')} ${f:getMessage('을(를) 확인하세요.')} ${f:getMessage('오늘')}${f:getMessage('보다 빠를 수 없습니다.')}");
					$('.activeDates').eq(i).focus();
					return;
				}
			}
			*/
			if($.trim($('input[name=activeWorkersName]').eq(i).val()) == '') {
				alert("${f:getMessage('수행자 ')}${f:getMessage('을(를) 선택하세요.')}");
				$('input[name=activeWorkersName]').eq(i).focus();
				return;
			}
		}
		
		if (confirm("${f:getMessage('저장하시겠습니까?')}")){
			var form = $("form[name=editActiveForm]").serialize();
			var url	= getURLString("development", "editActiveAction", "do");
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
						divPageLoad("TaskInfo", "viewTask", $("#taskOid").val());
						divPageLoad("activeDiv", "viewActiveList", $('#taskOid').val());
						divPageLoad('userDiv', 'viewUserList', $('#oid').val());
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

window.userDivLoad = function(count) {
	var url	= getURLString("common", "userSearchForm", "do");
	$.ajax({
		type:"POST",
		url: url,
		data: {
			searchMode : 'single'
			,hiddenParam : 'activeWorkers'
			,textParam : 'activeWorkersName'
			,userType : 'wtuser'
			,index : count
		},
		success:function(data){
			$('#userDIV_' + count).html(data);
		}
	});
}

</script>

<form id="editActiveForm" name="editActiveForm" >

<input type='hidden' name='deleteActiveOids' id='deleteActiveOids' value='' />
<input type="hidden" name="oid" id="oid"	value="${oid}" />

<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	<tr bgcolor="#ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>        
	        
    <tr>
		<td align="left" style="width: 10%">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>ACTIVITY</b>
		</td>
		
		<td align="left">
			<img alt="" src="/Windchill/jsp/portal/images/icon/shu_plus.gif" id="addRows" style="width: 15px; height: 15px; cursor: pointer;">
			<img alt="" src="/Windchill/jsp/portal/images/icon/shu_minus.gif" id="delRows" style="width: 15px; height: 15px; cursor: pointer;">
		</td>
		
		<td align="right">
			<button type="button" name="saveActive" id="saveActive" class="btnCRUD">
				<span></span>
				${f:getMessage('저장')}
			</button>
			<button type="button" name="cancelActive" id="cancelActive" class="btnCustom">
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
						ACTIVITY${f:getMessage('명')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdblueM" width="*">
						${f:getMessage('완료 요청일')}
						<span class="style1">*</span>
					</td>
					
					<td class="tdblueM" width="45%">
						${f:getMessage('수행자')}
						<span class="style1">*</span>
					</td>
					
				</tr>
				
				<tbody id="activeRows">
				
				<c:if test="${fn:length(list) != 0 }">
					<c:forEach items="${list }" var="DevActiveData" varStatus="i">
						<tr align="center" bgcolor="#FFFFFF"> 
							<td class="tdwhiteM" width="3%">
								<c:if test="${!DevActiveData.isDelete() }">
									<input type='checkbox' name='delRow' value='<c:out value="${DevActiveData.oid }" />'>
								</c:if>
								<input type='hidden' name='activeOids' id='activeOids' value='<c:out value="${DevActiveData.oid }" />' />
							</td>
							
							<td class="tdwhiteL"  width="30%">
								<input type='text' name='<c:out value="${DevActiveData.oid }" />_activeNames' value="<c:out value="${DevActiveData.name }" />" 
								style='width:90%' class='activeNames' />
							</td>
							
							<td class="tdwhiteL"  width="*">
								<input type='text' name='<c:out value="${DevActiveData.oid }" />_activeDates' id="activeDates_${i.index }" 
								value='<c:out value="${DevActiveData.activeDate }" />' 
								size='12' maxlength='15' readonly class='activeDates' >
								<a href='javascript:void(0);'>
									<img src='/Windchill/jsp/portal/images/calendar_icon.gif' border=0 name='activeDatesBtn' id='activeDatesBtn_${i.index }' >
								</a>
								<a href="javascript:clearText('activeDates_${i.index }');">
									<img src='/Windchill/jsp/portal/images/x.gif' border=0>
								</a>
							</td>
							
							<td class="tdwhiteL"  width="45%">
							
								<%-- 
								<jsp:include page="/eSolution/common/userSearchForm.do">
									<jsp:param value="single" name="searchMode"/>
									<jsp:param value="${DevActiveData.oid }_activeWorkers" name="hiddenParam"/>
									<jsp:param value="${DevActiveData.workerOid }" name="hiddenValue"/>
									<jsp:param value="activeWorkersName" name="textParam"/>
									<jsp:param value="${DevActiveData.workerName }" name="textValue"/>
									<jsp:param value="wtuser" name="userType"/>
									<jsp:param value="" name="returnFunction"/>
									<jsp:param value="${i.index }" name="index"/>
								</jsp:include>
								--%>
								
								<input type='hidden' name='<c:out value="${DevActiveData.oid }" />_activeWorkers' id='activeWorkers_${i.index }' value="<c:out value="${DevActiveData.workerOid }" />" 
								style='width:90%' class='activeWorkers' />
								<input type='text' name='activeWorkersName' id='activeWorkersNames_${i.index }' value="<c:out value="${DevActiveData.workerName }" />" 
								style='width: 80%' readOnly disabled />
								
								<a href="javascript:searchUser('editActiveForm', 'single', 'activeWorkers_${i.index }', 'activeWorkersNames_${i.index }', 'wtuser')">
									<img src='/Windchill/jsp/portal/images/s_search.gif' border=0>
								</a>
								<a href="javascript:clearText('activeWorkers_${i.index }');clearText('activeWorkersNames_${i.index }');">
									<img src='/Windchill/jsp/portal/images/x.gif' border=0>
								</a>
								
								
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