<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td class="tab_btm2"></td>
	</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td  class="tab_btm1"></td>
	</tr>
</table>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<colgroup>
		<col width="15%">
		<col width="35%">
		<col width="15%">
		<col width="35%">
	</colgroup>
	<tr>
		<td class="tdblueM">&nbsp;${f:getMessage('번호')}</td>
		<td class="tdwhiteL0" colspan=3 >
			<c:choose>
	              <c:when test= "${distributeType eq '1'}">
	              	<a href="javascript:openDistributeView('${infoViewOid }')">
						<c:out value="${processTarget[0] }" />]<c:out value="${processTarget[1] }" />
					</a>
	              </c:when>
	              <c:otherwise>
	              	<a href="javascript:openView('${infoViewOid }')">
						[<c:out value="${processTarget[0] }" />]<c:out value="${processTarget[1] }" />
					</a>
	              </c:otherwise>
	              
	         </c:choose>
		</td>
	</tr>
	
	<tr>
		<td class="tdblueM">&nbsp;${f:getMessage('제목')}</td>
		<td class="tdwhiteL0" colspan=3 >
			<c:out value="${processTarget[2] }" />
		</td>
	</tr>
	
	<tr>
		<td class="tdblueM">&nbsp;${f:getMessage('업무명')}</td>
		<td class="tdwhiteL">
			<c:out value="${workname }" />
		</td>
		<td class="tdblueM">&nbsp;${f:getMessage('작업자')}</td>
		<td class="tdwhiteL0">
			<c:out value="${fullName }" />
		</td>
	</tr>
	
	<tr>
		<td class="tdblueM">&nbsp;${f:getMessage('도착일')}</td>
		<td class="tdwhiteL" colspan=3 >
			<c:out value="${createStamp }" />
		</td>
		<%-- 
		<td class="tdblueM">&nbsp;${f:getMessage('기한일')}</td>
		<td class="tdwhiteL0">
			<c:out value="${deadline }" />
		</td>
		--%>
	</tr>
	
	<c:if test="${isAdmin }">
	
		<tr>
			<td class="tdblueM">&nbsp;Role</td>
			<td class="tdwhiteL0" colspan=3 >
				<c:out value="${role }" escapeXml="false" />
			</td>
		</tr>
		
		<tr>
			<td class="tdblueM">&nbsp;Process</td>
			<td class="tdwhiteL0" colspan=3 >
				<table>
					<c:forEach items="${plist }" var="plist">
						<tr>
							<td class="tdwhiteL0" colspan="3">
								<a href="javascript:void(0);" onclick="javascript:processHistory('/Windchill/ptc1/process/info?oid=<c:out value="${plist.wfProcessOid }" />&action=ProcessManager')">
									<c:out value="${plist.wfProcessName }" /> 
									| <c:out value="${plist.wfProcessState }" /> 
									| <c:out value="${plist.wfProcessCreate }" /> 
									| <c:out value="${plist.wfProcessOid }" />
								</a>
							</td>
						</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	
	</c:if>
	
	<c:if test="${isDelay }">
		<tr>
			<td class="tdblueM">&nbsp;${f:getMessage('지연사유')}</td>
			<td class="tdwhiteL0" colspan=3><TEXTAREA NAME="delay" ROWS="3" id=i style='width:100%'></TEXTAREA></td>
		</tr>
	</c:if>
</table>

