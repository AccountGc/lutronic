<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.workspace.ApprovalLine"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.doc.dto.DocumentDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
ApprovalLine submitLine = (ApprovalLine) request.getAttribute("submitLine");
ArrayList<ApprovalLine> agreeLines = (ArrayList<ApprovalLine>) request.getAttribute("agreeLiens");
ArrayList<ApprovalLine> approvalLines = (ArrayList<ApprovalLine>) request.getAttribute("approvalLines");
%>
<style type="text/css">
html {
	font-size: 13px;
}

table {
	width: 100%;
	border-top: 1px solid black;
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

<table>
	<tr>
		<th style="max-width: 70px; min-width: 70px;">작성자</th>
		<td class="center"><%=dto.getWriter()%></td>
		<th style="max-width: 70px; min-width: 70px;">작성부서</th>
		<td class="center"><%=dto.getDeptcode_name()%></td>
		<th style="max-width: 70px; min-width: 70px;">문서번호</th>
		<td class="center"><%=dto.getNumber()%></td>
	</tr>
	<tr>
		<th style="max-width: 70px; min-width: 70px;">문서분류</th>
		<td class="center">1</td>
		<th style="max-width: 70px; min-width: 70px;">프로젝트코드</th>
		<td class="center"><%=dto.getModel_name()%></td>
		<th style="max-width: 70px; min-width: 70px;">보존년한</th>
		<td class="center"><%=dto.getPreseration_name()%></td>
	</tr>
</table>

<table style="margin-top: 10px;">
	<colgroup>
		<col width="100">
		<col width="*">
	</colgroup>
	<tr>
		<th style="max-width: 70px; min-width: 70px;">제목</th>
		<td class="indent5"><%=dto.getName()%>
		</td>
	<tr>
</table>