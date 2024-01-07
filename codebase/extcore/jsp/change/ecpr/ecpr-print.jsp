<%@page import="com.e3ps.change.ecpr.dto.EcprDTO"%>
<%@page import="com.e3ps.org.service.OrgHelper"%>
<%@page import="com.e3ps.org.dto.PeopleDTO"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.workspace.ApprovalLine"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcprDTO dto = (EcprDTO) request.getAttribute("dto");
ApprovalLine submitLine = (ApprovalLine) request.getAttribute("submitLine");
WTUser submitUser = (WTUser) submitLine.getOwnership().getOwner().getPrincipal();
PeopleDTO submit = new PeopleDTO(submitUser);
ArrayList<ApprovalLine> agreeLines = (ArrayList<ApprovalLine>) request.getAttribute("agreeLines");
ArrayList<ApprovalLine> approvalLines = (ArrayList<ApprovalLine>) request.getAttribute("approvalLines");
SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
String submitDate = dateFormat.format(submitLine.getCompleteTime());
%>
<style type="text/css">
@page {
	size: A4;
	margin: 0;
}

html {
	font-size: 13px;
}

table {
	border-spacing: 0;
	border-collapse: collapse;
}

th:first-child {
	border-left: 1px solid black;
}

th {
	height: 24px;
	border-bottom: 1px solid black;
	border-right: 1px solid black;
}

td {
	height: 24px;
	border-bottom: 1px solid black;
	border-right: 1px solid black;
}

.indent5 {
	text-indent: 5px;
}

.center {
	text-align: center;
}
</style>

<div style="float: left; padding-bottom: 10px;">
	<table style="border-top: 1px solid black;">
		<tr>
			<th rowspan="4" style="width: 30px;">
				결
				<br>
				재
			</th>
			<!-- 직급 -->
			<td class="center" style="width: 80px;">기안</td>
			<%
			for (ApprovalLine approvalLine : approvalLines) {
				WTUser approvalUser = (WTUser) approvalLine.getOwnership().getOwner().getPrincipal();
				PeopleDTO approval = new PeopleDTO(approvalUser);
			%>
			<td class="center" style="width: 80px;"><%=approval.getDuty()%></td>
			<%
			}
			%>
		</tr>
		<!-- 싸인 -->
		<tr>
			<td class="center">
				<%
				String id = submitUser.getName();
				String name = OrgHelper.manager.getSignFileName(id.trim());
				if (name != null) {
				%>
				<img src="/Windchill/extcore/jsp/org/sign/<%=name%>" style="height: 50px; width: 60px;">
				<%
				} else {
				%>
				<img src="/Windchill/extcore/jsp/org/sign/empty.png" style="height: 50px; width: 60px;">
				<%
				}
				%>
			</td>
			<%
			for (ApprovalLine approvalLine : approvalLines) {
				String aid = approvalLine.getOwnership().getOwner().getName();
				String aname = OrgHelper.manager.getSignFileName(aid.trim());
			%>
			<td class="center">
				<%
				if (aname != null) {
				%>
				<img src="/Windchill/extcore/jsp/org/sign/<%=aname%>" style="height: 50px; width: 60px;">
				<%
				} else {
				%>
				<img src="/Windchill/extcore/jsp/org/sign/empty.png" style="height: 50px; width: 60px;">
				<%
				}
				%>
			</td>
			<%
			}
			%>
		</tr>
		<!-- 이름 -->
		<tr>
			<td class="center"><%=submitUser.getFullName()%></td>
			<%
			for (ApprovalLine approvalLine : approvalLines) {
				WTUser approvalUser = (WTUser) approvalLine.getOwnership().getOwner().getPrincipal();
			%>
			<td class="center"><%=approvalUser.getFullName()%></td>
			<%
			}
			%>
		</tr>
		<!-- 시간 -->
		<tr>
			<td class="center"><%=submitDate%></td>
			<%
			for (ApprovalLine approvalLine : approvalLines) {
				String approvalDate = dateFormat.format(approvalLine.getCompleteTime());
			%>
			<td class="center"><%=approvalDate%></td>
			<%
			}
			%>
		</tr>
	</table>
</div>

<%
	if(agreeLines.size() > 0) {
%>
<div style="float: right; padding-bottom: 10px;">
	<table style="border-top: 1px solid black;">
		<tr>
			<th rowspan="4">합의</th>
			<%
			for (ApprovalLine agreeLine : agreeLines) {
				WTUser agreeUser = (WTUser) agreeLine.getOwnership().getOwner().getPrincipal();
				PeopleDTO agree = new PeopleDTO(agreeUser);
			%>
			<td class="center" style="width: 80px;"><%=agree.getDuty()%></td>
			<%
			}
			%>
		</tr>
		<!-- 싸인 -->
		<tr>
			<%
			for (ApprovalLine agreeLine : agreeLines) {
				String aid = agreeLine.getOwnership().getOwner().getName();
				String aname = OrgHelper.manager.getSignFileName(aid.trim());
			%>
			<td class="center">
				<%
				if (aname != null) {
				%>
				<img src="/Windchill/extcore/jsp/org/sign/<%=aname%>" style="height: 50px; width: 60px;">
				<%
				} else {
				%>
				<img src="/Windchill/extcore/jsp/org/sign/empty.png" style="height: 50px; width: 60px;">
				<%
				}
				%>
			</td>
			<%
			}
			%>
		</tr>
		<!-- 이름 -->
		<tr>
			<%
			for (ApprovalLine agreeLine : agreeLines) {
				WTUser agreeUser = (WTUser) agreeLine.getOwnership().getOwner().getPrincipal();
			%>
			<td class="center"><%=agreeUser.getFullName()%></td>
			<%
			}
			%>
		</tr>
		<!-- 시간 -->
		<tr>
			<%
			for (ApprovalLine agreeLine : agreeLines) {
				String agreeDate = dateFormat.format(agreeLine.getCompleteTime());
			%>
			<td class="center"><%=agreeDate%></td>
			<%
			}
			%>
		</tr>
	</table>
</div>
<%
	}
%>
<!-- <table style="width: 100%; border-top: 1px solid black;"> -->
<!-- 	<colgroup> -->
<!-- 		<col style="width: 13%;" /> -->
<!-- 		<col style="width: 20%" /> -->
<!-- 		<col style="width: 13%" /> -->
<!-- 		<col style="width: 20%" /> -->
<!-- 		<col style="width: 13%" /> -->
<!-- 		<col style="width: 21%" /> -->
<!-- 	</colgroup> -->
<!-- 	<tr> -->
<!-- 		<th style="max-width: 100px; min-width: 100px;">작성자</th> -->
<%-- 		<td class="center"><%=dto.getWriter()%></td> --%>
<!-- 		<th style="max-width: 100px; min-width: 100px;">작성부서</th> -->
<%-- 		<td class="center"><%=dto.getDeptcode_name()%></td> --%>
<!-- 		<th style="max-width: 100px; min-width: 100px;">문서번호</th> -->
<%-- 		<td class="center"><%=dto.getNumber()%></td> --%>
<!-- 	</tr> -->
<!-- 	<tr> -->
<!-- 		<th style="max-width: 100px; min-width: 100px;">문서분류</th> -->
<%-- 		<td class="center"><%=classType1%></td> --%>
<!-- 		<th style="max-width: 100px; min-width: 100px;">프로젝트코드</th> -->
<%-- 		<td class="center"><%=dto.getModel_name()%></td> --%>
<!-- 		<th style="max-width: 100px; min-width: 100px;">보존년한</th> -->
<%-- 		<td class="center"><%=dto.getPreseration_name()%></td> --%>
<!-- 	</tr> -->
<!-- </table> -->

<table style="margin-top: 10px; width: 100%; border-top: 1px solid black;">
	<colgroup>
		<col width="100">
		<col width="*">
	</colgroup>
	<tr>
		<th style="max-width: 100px; min-width: 100px;">제목</th>
		<td class="indent5"><%=dto.getName()%>
		</td>
	<tr>
</table>

<div id="content"></div>

<script type="text/javascript">
	const data = window.data;
	const content = document.getElementById("content");
	content.innerHTML = data;
	window.opener._print(content);
</script>