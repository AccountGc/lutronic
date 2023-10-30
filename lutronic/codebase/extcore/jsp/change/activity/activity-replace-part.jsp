<%@page import="com.e3ps.change.EChangeOrder"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
EChangeOrder eco = (EChangeOrder) request.getAttribute("eco");
ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) request.getAttribute("list");
%>

<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECO (<%=eco.getEoNumber() %>) 품목변경
			</div>
		</td>
		<td class="right">
			<input type="button" value="추가" title="추가" onclick="deletwRow();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="insert();">
			<input type="button" value="저장" title="저장" calss="blue" onclick="save();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>