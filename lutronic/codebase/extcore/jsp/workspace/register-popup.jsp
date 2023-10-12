<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<link rel="stylesheet" href="/Windchill/extcore/css/approval.css?v=1">
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				결재선 지정
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="register();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="350">
		<col width="5">
		<col width="150">
		<col width="5">
		<col width="150">
		<col width="5">
		<col width="150">
		<col width="5">
		<col width="*">
	</colgroup>
	<tr>
		<!-- 조직도 -->
		<td></td>

		<!-- 공란 -->
		<td>&nbsp;</td>

		<!-- 유저검색 -->
		<td></td>
		<!-- 공란 -->
		<td>&nbsp;</td>

		<!-- 개인결재선 -->
		<td></td>

		<!-- 공란 -->
		<td>&nbsp;</td>

		<!-- 결재선 지정 버튼 구역 -->
		<td>버튼</td>

		<!-- 공란 -->
		<td>&nbsp;</td>

		<!-- 결재선 지정 -->
		<td></td>
	</tr>
</table>

<script type="text/javascript">
	
</script>