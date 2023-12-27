<%@page import="com.e3ps.admin.form.dto.FormTemplateDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
FormTemplateDTO dto = (FormTemplateDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
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
						문서 템플릿 정보
					</div>
				</td>
			</tr>
		</table>
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">문서템플릿 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>문서템플릿 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>문서양식 유형</th>
				<td class="indent5"><%=dto.getFormType()%></td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td colspan="5" class="indent5">
					<textarea name="contents" id="contents" rows="15" style="display:none;"><%=dto.getDescription() != null ? dto.getDescription() : "" %></textarea>
					<script type="text/javascript">
						// 에디터를 view 모드로 설정합니다.
						DEXT5.config.Mode = "view";
						
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
					<%
					if (isAdmin) {
					%>
					<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
					<%
					}
					%>
					<input type="button" value="뒤로" title="뒤로" onclick="history.go(-1);">
				</td>
			</tr>
		</table>
	</form>

	<script type="text/javascript">
		function _delete() {
			const oid = document.getElementById("oid").value;

			if (!confirm("삭제 하시겠습니까?")) {
				return false;
			}
			const url = getCallUrl("/form/delete?oid=" + oid);
			parent.openLayer();
			call(url, null, function(data) {
				alert(data.msg);
				if (data.result) {
					document.location.href = getCallUrl("/form/list");
				}
				parent.closeLayer();
			})
		}

		function modify() {
			const oid = document.getElementById("oid").value;
			const url = getCallUrl("/form/modify?oid=" + oid);
			document.location.href = url;
		}
	</script>
</body>
</html>