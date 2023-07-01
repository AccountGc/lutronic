<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
var activeOid = "<c:out value='${paramName}' />";
$(document).ready(function () {
	
})

$(function() {
	$('#addActivity').click(function() {
		var url = getURLString("development", "selectDevelopmentPopup", "do");
		openOtherName(url,"selectDevelopmentPopup","1180","600","status=no,scrollbars=yes,resizable=yes");
	})
	<%----------------------------------------------------------
	*                      관련 문서 삭제
	----------------------------------------------------------%>
	$("#delActivity").click(function() {
		var obj = $("input[name='activityDelete']");
		var checkCount = obj.size();
		for (var i = 0; i < checkCount; i++) {
			if (obj.eq(i).is(":checked")) {
				obj.eq(i).parent().parent().remove();
			}
		}
		$("#allActivityCheck").prop("checked", "");
	})
	
	$('#allActivityCheck').click(function() {
		if(this.checked) {
			$("input[name='activityDelete']").prop("checked", "checked");
		}else {
			$("input[name='activityDelete']").prop("checked", "");
		}
	})
})

<%----------------------------------------------------------
*                      개별업무 추가
----------------------------------------------------------%>
function addDevelopment(obj) {
	for (var i = 0; i < obj.length; i++) {
		if(activeDuplicateCheck(obj[i][0])) {
			var html = ""
			html += "<tr id='" + obj[i][0] + "'>";
			html += "	<td class='tdwhiteM'>";
			html += "		<input type='checkbox' name='activityDelete'>";
			html += "		<input type='hidden' name='" + activeOid +"' value='" + obj[i][0] + "' />";
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][2];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][4];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][5];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][7];
			html += "	</td>";
			html += "	<td class='tdwhiteM'>";
			html += obj[i][10];
			html += "	</td>";
			html += "</tr>";
	
			$("#activeTable").append(html);
		}
	}
}

<%----------------------------------------------------------
*                      개별업무 중복 검색
----------------------------------------------------------%>
function activeDuplicateCheck(oid) {
	var obj = $("input[name=" + activeOid + "]");
	var checkCount = obj.size();
	for (var i = 0; i < checkCount; i++) {
		if(obj.eq(i).val() == oid) {
			return false;
		}
	}
	return true;
}
</script>

<body>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr bgcolor="ffffff" height="5">
		<td colspan="5">&nbsp;</td>
	</tr> 
	
	<tr>
		<td align="left">
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b><c:out value="${title }" /></b>
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
			
				<tr>
					<td width="150"></td>
					<td width="350"></td>
					<td width="150"></td>
					<td width="350"></td>
				</tr>
			
				<tr>
					<td class="tdblueM">
						<c:out value="${title }" />
					</td>
					
					<td class="tdwhiteL" colspan="3">
						<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="left">
									<table border="0" cellpadding="0" cellspacing="2">
										<tr>
											<td>
												<button type="button" name="addActivity" id="addActivity" class="btnCustom">
													<span></span>
													${f:getMessage('추가')}
												</button>
											</td>
											
											<td>
												<button type="button" name="delActivity" id="delActivity" class="btnCustom">
													<span></span>
													${f:getMessage('삭제')}
												</button>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td>
									<div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
										<table width="100%" cellspacing="0" cellpadding="1" border="0" id="activeTable" align="center">
											<tbody>
												<tr>
													<td class="tdblueM" width="5%">
														<input type="checkbox" name="allActivityCheck" id="allActivityCheck">
								                    </td>
								                    
													<td class="tdblueM" width="20%">
									                    ${f:getMessage('프로젝트명')}
								                    </td>
								                    
													<td class="tdblueM" width="35%">
														TASK
													</td>
													
													<td class="tdblueM" width="10%">
														ACTIVITY
													</td>
													
													<td class="tdblueM" width="10%">
														${f:getMessage('담당자')}
													</td>
													
													<td class="tdblueM" width="10%">
														${f:getMessage('상태')}
													</td>
												</tr>
											
												<c:if test="${fn:length(list) != 0 }">
													<c:forEach items="${list }" var="activeData">
														<tr>
															<td class="tdwhiteM">
																<input type="checkbox" name="activityDelete">
																<input type="hidden" name="<c:out value="${paramName}" />" value="<c:out value="${activeData.oid }" />">
															</td>
															
							                    			<td class="tdwhiteL">
							                   					<c:out value="${activeData.masterName }" />
							                    			</td>
							                    			
							                    			<td class="tdwhiteL">
							                    				<c:out value="${activeData.taskName }" />
							                    			</td>
							                    			
							                    			<td class="tdwhiteM" align="center">
							                    				<a href="javascript:openView('<c:out value="${activeData.oid }" />')">
							                    					<c:out value="${activeData.name }" />
							                    				</a>
							                    			</td>
							                    			
							                    			<td class="tdwhiteM" align="center">
							                    				<c:out value="${activeData.workerName }" />
							                    			</td>
							                    			
							                    			<td class="tdwhiteM">
							                    				<c:out value="${activeData.state }" />
							                    			</td>
							                    		</tr>
													</c:forEach>
												</c:if>
												
											</tbody>
										</table>
									</div>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

</body>
</html>