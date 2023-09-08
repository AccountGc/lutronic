<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%-- <%@include file="/extcore/jsp/common/aui/auigrid.jsp"%> --%>
<%-- <input type="hidden" name="location" id="location" value="<%=DocumentHelper.DOCUMENT_ROOT %>"> --%>
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
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png"> 문서 일괄결재
				</div>
			</td>
		</tr>
	</table>
	<table class="button-table">
		<tr>
			<td class="right">
				<input type="button"  value="등록"  title="등록" class="blue"  id="createBtn" name="createBtn">
			</td>
		</tr>
	</table>
	<table class="create-table">
		<colgroup>
			<col width="150">
			<col width="*">
		</colgroup>
		<tr>
			<th class="req lb">일괄결재 제목</th>
			<td class="indent5"><input type="text" name="appName" id="appName" class="width-200"></td>
		</tr>
		<tr>
			<th class="lb">일괄결재 설명</th>
			<td class="indent5"><textarea name="description" id="description" rows="6"></textarea></td>
		</tr>
	</table>
	
	<!-- 	일괄결재 문서 -->
	<jsp:include page="/extcore/jsp/document/include_selectDocument.jsp">
		<jsp:param value="일괄결재" name="title"/>
		<jsp:param value="docOid" name="paramName"/>
		<jsp:param value="" name="searchType"/>
		<jsp:param value="BATCHAPPROVAL" name="state"/>
		<jsp:param value="LC_Default_NonWF" name="lifecycle"/>
	</jsp:include>
	<script type="text/javascript">
		// 등록
		$("#createBtn").click(function() {
			if(isEmpty($("#appName").val())){
				alert("일괄결재 제목을 입력하세요.");
				return;
			}
			var gridList = AUIGrid.getGridData(docGridID);
			if(gridList.length==0){
				alert("일괄결재 대상을 선택하세요.");
				return;
			}
			
			if (!confirm("등록 하시겠습니까?")) {
				return false;
			}
			
			let params = new Object();
			const field = ["appName","description"];
			params = toField(params, field);
			params.searchType = "DOC";
			params.gridList = gridList;
			const url = getCallUrl("/doc/all");
			call(url, params, function(data) {
				if(data.result){
					alert("등록 되었습니다.");
					location.reload();
				}else{
					alert(data.msg);
				}
			});
		});
	
		// jquery 삭제를 해가는 쪽으로 한다..
		document.addEventListener("DOMContentLoaded", function() {
			createAUIGrid4(columnsDoc);
			AUIGrid.resize(docGridID);
		});

		window.addEventListener("resize", function() {
			
		});
	</script>
</body>
</html>