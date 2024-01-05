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
table {
	width: 100%;
}

th:first-child {
	border-left: 1px solid black;
}

th {
	border-top: 1px solid black;
}
</style>

<table>
	<colgroup>
		<col width="150">
		<col width="500">
		<col width="150">
		<col width="500">
		<col width="150">
		<col width="500">
	</colgroup>
	<tr>
		<th>작성자</th>
		<td>1</td>
		<th>작성부서</th>
		<td>1</td>
		<th>문서번호</th>
		<td>1</td>
	</tr>
	<tr>
		<th>문서분류</th>
		<td>1</td>
		<th>프로젝트코드</th>
		<td>1</td>
		<th>보존년한</th>
		<td>1</td>
	</tr>
</table>