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
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/smarteditor2/js/HuskyEZCreator.js"></script>
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
					<input type="text" name="number" id="number" class="width-200">
				</td>
				<th class="req">문서템플릿 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-400">
				</td>
				<th class="req">문서양식 유형</th>
				<td class="indent5">
					<select name="formType" id="formType" class="width-200">
						<option value="">선택</option>
						<%
						for (NumberCode n : formType) {
						%>
						<option value="<%=n.getName()%>"><%=n.getName()%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th class="req lb">내용</th>
				<td colspan="5" class="indent5">
					<textarea name="description" id="description" rows="35"></textarea>
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
		// 텍스트 편집기
		const oEditors = [];
		nhn.husky.EZCreator.createInIFrame({
			oAppRef : oEditors,
			elPlaceHolder : "description", //textarea ID 입력
			sSkinURI : "/Windchill/extcore/smarteditor2/SmartEditor2Skin.html", //martEditor2Skin.html 경로 입력
			fCreator : "createSEditor2",
			htParams : {
				// 툴바 사용 여부 (true:사용/ false:사용하지 않음) 
				bUseToolbar : true,
				// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음) 
				bUseVerticalResizer : false,
				// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음) 
				bUseModeChanger : false
			}
		});

		document.addEventListener("DOMContentLoaded", function() {
			selectbox("formType");
		});

		function create() {
			const number = document.getElementById("number");
			const name = document.getElementById("name");
			const formType = document.getElementById("formType");
			oEditors.getById["description"].exec("UPDATE_CONTENTS_FIELD", []);
			const description = document.getElementById("description");

			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}
			const params = {
				name : name.value,
				number : number.value,
				formType : formType.value,
				description : description.value
			}
			const url = getCallUrl("/form/create");
			parent.openLayer();
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