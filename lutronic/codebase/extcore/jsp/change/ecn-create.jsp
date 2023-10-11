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
	<form>
		<input type="hidden" name="cmd" id="cmd" value="save" />
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png"> ECN 등록
					</div>
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="*">
				<col width="*">
				<col width="*">
				<col width="*">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">ECN 제목</th>
				<td class="indent5" colspan="7"><input type="text" name="name" id="name" class="width-200"></td>
			</tr>
			<tr>
				<th class="lb">관련 ECO</th>
				<td colspan="7">
					<jsp:include page="/extcore/jsp/change/include_selectEco.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5" colspan="7"><textarea name="eoCommentA" id="eoCommentA" rows="10"></textarea></td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td class="indent5" colspan="7"><textarea name="eoCommentB" id="eoCommentB" rows="10"></textarea></td>
			</tr>
			<tr>
				<th class="lb">설계변경 부품</th>
				<td colspan="7">
					<jsp:include page="/extcore/jsp/change/include_selectEcoPart.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="7">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button"  value="기안"  title="기안"  class="blue"  id="createBtn">
					<input type="button" value="초기화" title="초기화" id="resetBtn">
					<input type="button" value="이전" title="이전" onclick="javascript:history.back();">
					<input type="button"  value="임시저장"  title="임시저장"  id="exBtn">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			$("#createBtn").click(function() {
				const name = document.getElementById("name").value;
				const eoCommentA = document.getElementById("eoCommentA").value;
				const eoCommentB = document.getElementById("eoCommentB").value;
				
				if(isEmpty($("#name").val())) {
					alert("제목을 입력하세요.");
					return;
				}
				
				if (!confirm("등록 하시겠습니까?")) {
					return;
				}
				
				const params = new Object();
				params.name = name;
				params.eoCommentA = eoCommentA;
				params.eoCommentB = eoCommentB;
				
				var url = getCallUrl("/changeECN/create");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.href = getCallUrl("/changeECN/list");
					}else{
						alert(data.msg);
					}
				});
			})
			
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid1(columnsEco);
				createAUIGrid2(columnsPart);
				AUIGrid.resize(partGridID);
				AUIGrid.resize(ecoGridID);
			});
	
		</script>
	</form>	
</body>
</html>