<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function(){
});

function openWfProcessInfo(oid) {
	url = getURLString("groupware", "wfProcessInfo", "do");
}
</script>

<body>

<table width="100%" border="0" cellpadding="1" cellspacing="0" align="center" style="padding-bottom:5px">
	<tr> 
		<td height="30" width="99%" align="center"><B><font color="white"></font></B></td>
	</tr>
</table>
<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
	<tr bgcolor="ffffff" height="5">
		<td>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" >
				<tr>
					<td align="left"><img src="/Windchill/jsp/portal/img/bt_01.gif" > <b>${f:getMessage('활동 현황')}</b></td>
					<td align="right" valign="top">
						<font color=green><img src="/Windchill/jsp/portal/images/tree/task_complete.gif" >&nbsp;${f:getMessage('완료')}</font>
						<font color=blue><img src="/Windchill/jsp/portal/images/tree/task_progress.gif">&nbsp;${f:getMessage('진행')}</font>
						<font color=red><img src="/Windchill/jsp/portal/images/tree/task_red.gif">&nbsp;${f:getMessage('지연')}</font>
						<font color=black><img src="/Windchill/jsp/portal/images/tree/task_ready.gif">&nbsp;${f:getMessage('예정')}</font>
					</td>
				</tr>	
			</table>
 
			<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
				<tr>
		  			<td height="1" width="100%"></td>
				</tr>
			</table>
			
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center style="table-layout:fixed">
				<tr bgcolor="9acd32" height="25">
					<td class="tdblueM" width="5%">${f:getMessage('현황')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('단계')}</td>
					<td class="tdblueM" width="*">${f:getMessage('활동명')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('활동구분')}</td>
					<td class="tdblueM" width="7%">${f:getMessage('담당부서')}</td>
					<td class="tdblueM" width="7%">${f:getMessage('담당자')}</td>
					<td class="tdblueM" width="8%">${f:getMessage('요청 완료일')}</td>
					<td class="tdblueM" width="10%">${f:getMessage('상태')}</td>
					<td class="tdblueM" width="8%">${f:getMessage('완료일')}</td>
					<c:if test="${isAdmin }">
						<td class="tdblueM" width="22%">WF</td>
					</c:if>
				</tr>
				
				<tbody id="ecaTableBody">
						<c:if test="${fn:length(list) != 0 }">
							<c:forEach items="${list}" var="ecaData"  varStatus="status">
								<tr>
									<td class="tdwhiteM">
										<img src='<c:out value="${ecaData.icon }" escapeXml="true"/>'>
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.getStepName() }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.name }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.activityName }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.getDepartmentName() }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.activeUserName }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.finishDate }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.stateName }" />
									</td>
									<td class="tdwhiteM">
										<c:out value="${ecaData.completeDate }" />
									</td>
									<c:if test="${isAdmin }">
										<td class="tdwhiteM" >
											<a href="javascript:openWindow('/Windchill/eSolution/groupware/wfProcessInfo.do?oid=<c:out value="${ecaData.oid }" />','WF',1280,600);">
												<c:out value="${ecaData.oid }" />
											</a>
										</td>
									</c:if>
								</tr>
							</c:forEach>
						</c:if>
				</tbody>
			</table>
		</td>
	</tr>
</table>


</body>
</html>