<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<script type="text/javascript">
$(document).ready(function() {
})

$(function() {
	<%----------------------------------------------------------
	*                      Activity 수정 버튼
	----------------------------------------------------------%>
	$('#updateActive').click(function() {
		divPageLoad('activeDiv', 'editActiveList', $('#taskOid').val());
	})
})

<%----------------------------------------------------------
*                      Activity 수정후 각각 페이지 재설정
----------------------------------------------------------%>
window.activeAction = function() {
	divPageLoad("devBody", "viewDevelopment", $("#oid").val());
	divPageLoad("TaskInfo", "viewTask", $("#taskOid").val());
	divPageLoad("taskDiv", "viewTaskList", $("#oid").val());
	divPageLoad("activeDiv", "viewActiveList", $('#taskOid').val());
	divPageLoad('userDiv', 'viewUserList', $('#oid').val());
}

</script>

<input type='hidden' id='activeAction' name='activeAction' value='true' />

<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	<tr bgcolor="#ffffff" height=5>
		<td colspan="5">&nbsp;</td>
	</tr>        
	        
    <tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>ACTIVITY</b>
		</td>
		
		<c:if test="${enAbled }">
			<td align="right">
				<button type="button" name="updateActive" id="updateActive" class="btnCustom">
					<span></span>
					${f:getMessage('수정')}
				</button>
			</td>
		</c:if>
	</tr>
	
	<tr>
		<td colspan="2">
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	   			<tr>
	       			<td height="1" width="100%"></td>
    			</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
				<tr>
					
					<td class="tdblueM"  width="40%">
						ACTIVITY${f:getMessage('명')}
					</td>
					
					<td class="tdblueM" width="10%">
						${f:getMessage('수행자')}
					</td>
					
					<td class="tdblueM" width="15%">
						${f:getMessage('완료 요청일')}
					</td>
					
					<td class="tdblueM" width="20%">
						${f:getMessage('완료일')}
					</td>
					
					<td class="tdblueM" width="*">
						${f:getMessage('상태')}
					</td>
				</tr>
				
				<c:if test="${fn:length(list) != 0 }">
					<c:forEach items="${list }" var="DevActiveData">
						<tr align="center" bgcolor="#FFFFFF"> 
							
							<td class="tdwhiteM">
								<a href="javascript:openView('<c:out value="${DevActiveData.oid }" />')">
									<c:out value="${DevActiveData.name }" />
								</a>
							</td>
							
							<td class="tdwhiteM">
								<c:out value="${DevActiveData.workerName }" />
							</td>
							
							<td class="tdwhiteM">
								<c:out value="${DevActiveData.activeDate }" />
							</td>
							
							<td class="tdwhiteM">
								<c:out value="${DevActiveData.finishDate }" />
							</td>
							
							<td class="tdwhiteM">
								<c:out value="${DevActiveData.state }" />
							</td>
						</tr>
					</c:forEach>
				</c:if>
			</table>
		</td>
	</tr>
</table>
