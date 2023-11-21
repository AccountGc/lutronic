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
</head>
<body>
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="lastNum" id="lastNum">
		<input type="hidden" name="curPage" id="curPage">
		<input type="hidden" name="oid" id="oid">
		
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> 속성값 변경
					</div>
				</td>
			</tr>
		</table>
		<table class="search-table">
			<colgroup>
				<col width="174">
				<col width="*">
			</colgroup>
			<tr>
				<th>OID</th>
				<td class="indent5">
					<input type="text" name="oid" id="oid" class="width-300">
				</td>
			</tr>
			<tr>
				<th>속성명</th>
				<td class="indent5">
					<select name="attrName" id="attrName" class="width-200">
						<option value="">선택</option>
						<option value="INWORK">작업 중</option>
						<option value="UNDERAPPROVAL">승인 중</option>
						<option value="APPROVED">승인됨</option>
						<option value="RETURN">반려됨</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>속성값</th>
				<td class="indent5">
					<input type="text" name="attrValue" id="attrValue" class="width-300">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="right">
					<input type="button" value="변경" title="변경" class="blue">
					<input type="button" value="초기화" title="초기화">
					<input type="button" value="메인화면으로" title="메인화면으로">
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			selectbox("attrName");
			
			function loadGridData() {
				// 				let params = new Object();
				// 				const url = getCallUrl("/doc/list");
				// 				const field = ["_psize","oid","name","number","description","state","creatorOid","createdFrom","createdTo"];
				// 				const latest = !!document.querySelector("input[name=latest]:checked").value;
				// 				params = toField(params, field);
				// 				params.latest = latest;
				// 				AUIGrid.showAjaxLoader(myGridID);
				// 				parent.openLayer();
				// 				call(url, params, function(data) {
				// 					AUIGrid.removeAjaxLoader(myGridID);
				// 					AUIGrid.setGridData(myGridID, data.list);
				// 					document.getElementById("sessionid").value = data.sessionid;
				// 					document.getElementById("curPage").value = data.curPage;document.getElementById("lastNum").value = data.list.length;
				// 					parent.closeLayer();
				// 				});
			}

			function exportExcel() {
// 				const exceptColumnFields = [ "primary" ];
// 				const sessionName = document.getElementById("sessionName").value;
// 				exportToExcel("문서 리스트", "문서", "문서 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

		</script>
	</form>
</body>
</html>