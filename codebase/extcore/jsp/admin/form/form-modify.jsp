<%@page import="com.e3ps.admin.form.dto.FormTemplateDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> formType = (ArrayList<NumberCode>) request.getAttribute("formType");
FormTemplateDTO dto = (FormTemplateDTO) request.getAttribute("dto");
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
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서 템플릿 수정
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">문서템플릿 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-400" value="<%=dto.getName()%>">
				</td>
			</tr>
			<tr>
				<th class="req lb">내용</th>
				<td class="indent5">
					<textarea name="contents" id="contents" rows="15" style="display:none;"><%=dto.getDescription() != null ? dto.getDescription() : "" %></textarea>
					<script type="text/javascript">
						new Dext5editor('description');
						var description = document.getElementById("contents").value;
						DEXT5.setBodyValue(description, 'description');
					</script>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
					<input type="button" value="뒤로" title="뒤로" onclick="history.go(-1);">
				</td>
			</tr>
		</table>
	</form>

	<script type="text/javascript">
		document.addEventListener("DOMContentLoaded", function() {
			toFocus("name");
		});

		function modify() {
			const oid = document.getElementById("oid").value;
			const name = document.getElementById("name");
			const description = DEXT5.getBodyValue("description");

			if (!confirm("수정 하시겠습니까?")) {
				return false;
			}
			
			const params = {
				oid : oid,
				name : name.value,
				description : description
			}
			const url = getCallUrl("/form/modify");
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