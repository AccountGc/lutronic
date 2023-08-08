<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
</head>
<body>
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">

		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>일괄결재 제목 <span style="color:red;">*</span></th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500">
				</td>
			</tr>
			<tr>
				<th>일괄결재 설명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500">
				</td>
			</tr>
		</table>	
		<br>
		
		<!-- 		일괄 결재 -->
		<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
			<jsp:param value="일괄결재" name="title"/>
			<jsp:param value="docOid" name="paramName"/>
			<jsp:param value="mold" name="searchType"/>
			<jsp:param value="BATCHAPPROVAL" name="state"/>
			<jsp:param value="LC_Default_NonWF" name="lifecycle"/>
		</jsp:include>
		
		<table class="button-table">
			<tr>
				<td class="right">
					<input type="button" value="등록" title="등록" class="blue" id="createBtn">
				</td>
			</tr>
		</table>

		<script type="text/javascript">

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid4(columnsDoc);
				AUIGrid.resize(docGridID);
				selectbox("state");
				selectbox("type");
				selectbox("depart");
			});
		</script>
	</form>
</body>
</html>