<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>

<script type="text/javascript">
$(function() {
	<%----------------------------------------------------------
	*                      수정 버튼
	----------------------------------------------------------%>
	$("#updateTask").click(function() {
		divPageLoad("taskDiv", "editTaskList", $("#oid").val());
	})
})
</script>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >	
	        
    <tr>
		<td align="left" style='height: 20px'>
			<img src="/Windchill/jsp/portal/img/bt_01.gif" >
			<b>TASK</b>
		</td>
		
		<c:if test="${enAbled }">
			<td align="right">
				<button type="button" name="updateTask" id="updateTask" class="btnCustom">
					<span></span>
					${f:getMessage('수정')} <c:out value="${masterData.isAdmin() }" />
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
					<td class="tdblueM"  width="30%">
						TASK${f:getMessage('명')}
					</td>
					
					<td class="tdblueM" width="10%">
						${f:getMessage('상태')}
					</td>
					
					<td class="tdblueM" width="*">
						${f:getMessage('설명')}
					</td>
				</tr>
				
				<c:if test="${fn:length(list) != 0 }">
					<c:forEach items="${list }" var="DevTaskData">
						<tr align="center" bgcolor="#FFFFFF"> 
							<td class="tdwhiteM"  width="30%">
								<a href="javascript:viewTask('<c:out value="${DevTaskData.oid }" />')">
									<c:out value="${DevTaskData.name }" />
								</a>
							</td>
							
							<td class="tdwhiteM" width="10%">
								<c:out value="${DevTaskData.state }" />
							</td>
							
							<td class="tdwhiteL"  width="*">
								<c:out value="${DevTaskData.getDescription(true) }" escapeXml="false"/>
							</td>
							
						</tr>
					</c:forEach>
				</c:if>
							
			</table>
		</td>
	</tr>
</table>
