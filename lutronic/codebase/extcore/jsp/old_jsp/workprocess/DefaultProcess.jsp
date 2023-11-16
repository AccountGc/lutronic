<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fn"		uri="http://java.sun.com/jsp/jstl/functions"	%>
<%@ taglib prefix="f"	uri="/WEB-INF/functions.tld"			%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<body>

<script>
<%----------------------------------------------------------
*                      페이지 초기 설정
----------------------------------------------------------%>
$(document).ready(function () {
	
})

$(function() {
	<%----------------------------------------------------------
	*                      승인 버튼
	----------------------------------------------------------%>
	$("#approve").click(function() {
		
		if($("#isObjection").val() == "true"){
			alert("${f:getMessage('합의자중 이의 제기를 하였습니다')}");
		}
		appSave("승인");
	}),
	<%----------------------------------------------------------
	*                      반려 버튼
	----------------------------------------------------------%>
	$("#return").click(function() {
		appSave("반려");
	}),
	<%----------------------------------------------------------
	*                      합의 버튼
	----------------------------------------------------------%>
	$("#agreement").click(function() {
		appSave("합의");
	}),
	<%----------------------------------------------------------
	*                      이의 버튼
	----------------------------------------------------------%>
	$("#objection").click(function() {
		appSave("이의");
	}),
	<%----------------------------------------------------------
	*                      재작업 버튼
	----------------------------------------------------------%>
	$("#rework").click(function() {
		appSave("재작업");
	}),
	<%----------------------------------------------------------
	*                      결재취소 버튼
	----------------------------------------------------------%>
	$("#appCancel").click(function() {
		appSave("결재취소");
	})
})

<%----------------------------------------------------------
*                      결재 수정
----------------------------------------------------------%>
function appSave(event) {
	if(!confirm("${f:getMessage('"+event+"')}"+ "${f:getMessage('하시겠습니까?')}")) {
		return;
	}
	$("#WfUserEvent").val(event);
	$("#defaultProcess").attr("action", getURLString("groupware", "approveAction", "do")).submit();
}

</script>

<form name="defaultProcess" id="defaultProcess">

<input type="hidden" name="cmd" id="cmd" value="approve">
<input type="hidden" name="WfUserEvent" id="WfUserEvent" value="">
<input type="hidden" name="workOid" id="workOid" value="<c:out value="${workitemoid }" />">
<input type="hidden" name="oid" id="oid" value="<c:out value="${oid }" />">
<input type="hidden" name="actname" id="actname" value="<c:out value="${actname }" />">
<input type="hidden" name="isObjection" id="isObjection" value="<c:out value="${isObjection }" />">
<table border="0" height="100%"  cellspacing="0" cellpadding="0" width="100%" style="table-layout:auto">
	<tr>
		<td align="left" valign=top height=42>
			<table cellspacing="0" cellpadding="0" border=0 width=100% height=29 class="Subinfo_img_bg">
				<tr>
					<td></td>
					<td>
						&nbsp;
						<img src='/Windchill/jsp/portal/images/base_design/Sub_Right_ico.gif' width='10' height='9' />
						&nbsp; ${f:getMessage('나의업무')} > ${f:getMessage('작업함')}
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td>
			<c:if test="${actname ne '재작업' && actname ne '반려확인' }">
				<jsp:include page="/eSolution/groupware/include_Reassign.do">
					<jsp:param value="${workitemoid }" name="workItemOid"/>
				</jsp:include>
			</c:if>
		</td>
	</tr>
	<tr>
		<td class="space5"></td>
	</tr>
	<tr>
		<td align="right" colspan=2>
			<table border="0" cellpadding="0" cellspacing="4" align="right">
               <tr>
	               
	               <c:choose>
	               		<c:when test="${actname eq '결재' }">
							<td>
								<button class="btnCustom" id="approve" name="approve" type="button" >
									<span></span>
									${f:getMessage('승인')}
								</button>
							</td>
							
							<td>
								<button class="btnCustom" id="return" name="return" type="button" >
									<span></span>
									${f:getMessage('반려')}
								</button>
							</td>
	               		</c:when>
	               		<c:when test="${actname eq '반려확인' }">
							<td>
								<button class="btnCustom" id="rework" name="rework" type="button" >
									<span></span>
									${f:getMessage('재작업')}
								</button>
							</td>
							
							<td>
								<button class="btnCustom" id="appCancel" name="appCancel" type="button" >
									<span></span>
									${f:getMessage('결재취소')}
								</button>
							</td>
	               		</c:when>
	               		<c:when test="${actname eq '합의' }">
	               			<td>
								<button class="btnCustom" id="agreement" name="agreement" type="button" >
									<span></span>
									${f:getMessage('합의')}
								</button>
							</td>
							
							<td>
								<button class="btnCustom" id="objection" name="objection" type="button" >
									<span></span>
									${f:getMessage('이의')}
								</button>
							</td>
	               		
	               		</c:when>
	               </c:choose>
               
			   </tr>
           </table>
		</td>
	</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="space5"> </td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="1" cellspacing="0" class="tablehead" align="center">
	<tr><td height="1" width=100%></td></tr>
</table>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
			<jsp:include page="/eSolution/groupware/processInfo.do">
				<jsp:param value="${workitemoid }" name="workitemoid"/>
			</jsp:include>
		</td>
	</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%" >
	<tr>
		<td class="space5"> </td>
	</tr>
</table>
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
	<tr>
		<td width=20% class="tdblueM">${f:getMessage('결재의견')}</td>
		<td class="tdwhiteL">
			<TEXTAREA NAME="comment" ROWS="3" COLS="100%" class=fm_area style='width:100%'></TEXTAREA></td>
		</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="space20"> </td>
	</tr>
</table>

<c:choose>
	<c:when test="${fn:length(appline) != 0 }">
		<table name='approverInfo' id='approverInfo' border="0" cellpadding="0" cellspacing="0" width="100%"> <!--//여백 테이블-->
			 <tr> 
			   <td class="tdblueM" width="5%">${f:getMessage('순서')}</td>
			   <td class="tdblueM" width="5%">${f:getMessage('구분')}</td>
			   <td class="tdblueM" width="10%">${f:getMessage('부서')}</td>
			   <td class="tdblueM" width="10%">${f:getMessage('이름')}</td>
			   <td class="tdblueM" width="12%">ID</td>
			   <td class="tdblueM" width="7%">${f:getMessage('결재')}</td>
			   <td class="tdblueM" width="20%">${f:getMessage('결재일')}</td>
			   <td class="tdblueM" width="35%">${f:getMessage('결재의견')}</td>
			 </tr>
			 
			 <c:forEach items="${appline }" var="appline">
			 
			
			 
			 <tr>
			 	<c:choose>
					<c:when test="${appline.ActivityName eq '합의' }">
						<td class="tdwhiteM0" style="background-color:#79ABFF" >						
					</c:when>
					<c:when test="${appline.ActivityName eq '결재' }">
						<td class="tdwhiteM0" style="background-color:#C3ED60" >						
					</c:when>
					<c:when test="${appline.ActivityName eq '기안' }">
						<td class="tdwhiteM0" style="background-color:#FFFFA1" >						
					</c:when>
					<c:when test="${appline.ActivityName eq '수신' }">
						<td class="tdwhiteM0" style="background-color:#FFCBCB" >						
					</c:when>
					<c:otherwise>
						<td class="tdwhiteM0">
					</c:otherwise>
				</c:choose>
			 
				 	
				 		<c:out value="${appline.ProcessOrder }" />
				 	</td>
				 	<td class="tdwhiteM0" >
				 		<c:out value="${appline.ActivityName }" />
				 	</td>
				 	<td class="tdwhiteM0" >
				 		<c:out value="${appline.DepartmentName }" />
				 	</td>
				 	<td class="tdwhiteM0" >
				 		<c:out value="${appline.FullName }" />
				 	</td>
				 	<td class="tdwhiteM0" >
				 		<c:out value="${appline.ID }" />
				 	</td>
				 	<td class="tdwhiteM0" >
				 		<c:out value="${appline.State }" />
				 	</td>
				 	<td class="tdwhiteL0" >
				 		<c:out value="${appline.ProcessDate }" />
				 	</td>
				 	<td class="tdwhiteL0" >
				 		<p><c:out value="${appline.Comment }" /></p>
				 	</td>
				 </tr>
			 </c:forEach>
		</table>
	</c:when>
</c:choose>

</form>

</body>
</html>