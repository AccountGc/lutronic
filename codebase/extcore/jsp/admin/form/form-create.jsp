<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> formType = (ArrayList<NumberCode>) request.getAttribute("formType");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<style type="text/css">
iframe {
	margin-top: 3px;
}
</style>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/dext5editor/js/dext5editor.js"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서 템플릿 등록
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">문서템플릿 번호</th>
				<td class="indent5">
					<input type="text" name="number" id="number" class="width-300">
				</td>
				<th class="req">문서템플릿 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th class="req">코드</th>
				<td class="indent5">
					<input type="text" name="formType" id="formType" class="width-300">
				</td>
			</tr>
			<tr>
				<th class="req lb">내용</th>
				<td colspan="5" class="indent7 pb8">
					<script type="text/javascript">
						new Dext5editor('description');
					</script>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="뒤로" title="뒤로" onclick="history.go(-1);">
				</td>
			</tr>
		</table>
	</form>

	<script type="text/javascript">
		document.addEventListener("DOMContentLoaded", function() {
			toFocus("name");
// 			selectbox("formType");
		});

		function create() {
			const number = document.getElementById("number");
			const name = document.getElementById("name");
			const formType = document.getElementById("formType");
			// 내용
			const description = DEXT5.getBodyValue("description");

			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}
			const params = {
				name : name.value,
				number : number.value,
				description : description,
				formType : formType.value
			}
			const url = getCallUrl("/form/create");
			parent.openLayer();
			logger(params);
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					document.location.href = getCallUrl("/form/list");
				}
				parent.closeLayer();
			})
		}
	</script>
</body>
</html>