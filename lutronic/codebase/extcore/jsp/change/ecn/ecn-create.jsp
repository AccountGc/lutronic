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
				<td class="right">
					<input type="button" value="기안" title="기안" class="red" onclick="create('false');">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="create('true');">
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
			<tr>
				<th class="lb">결재</th>
				<td colspan="7">
					<jsp:include page="/extcore/jsp/workspace/include/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="기안" title="기안" class="red" onclick="create('false');">
					<input type="button" value="임시저장" title="임시저장" class="" onclick="create('true');">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function create(temp) {
				const name = document.getElementById("name").value;
				const eoCommentA = document.getElementById("eoCommentA").value;
				const eoCommentB = document.getElementById("eoCommentB").value;
				
				const temprary = JSON.parse(temp);
				const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
				
				if(isEmpty($("#name").val())) {
					alert("제목을 입력하세요.");
					return;
				}
				
				if (temprary) {
					if (!confirm("임시저장하시겠습니까??")) {
						return false;
					}
					
					if (addRows8.length > 0) {
						alert("결재선 지정을 해지해주세요.")
						return false;
					}
					
				} else {
					if (!confirm("등록하시겠습니까?")) {
						return false;
					}
				}
				
				const params = {
					name : name,
					eoCommentA : eoCommentA,
					eoCommentB : eoCommentB,
					temprary : temprary,
				}
				toRegister(params, addRows8); // 결재선 세팅
				var url = getCallUrl("/ecn/create");
				call(url, params, function(data) {
					if(data.result){
						alert(data.msg);
						location.href = getCallUrl("/ecn/list");
					}else{
						alert(data.msg);
					}
				});
			}
			
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid1(columnsEco);
				createAUIGrid2(columnsPart);
				createAUIGrid8(columns8);
				createAUIGrid9(columns9);
				AUIGrid.resize(partGridID);
				AUIGrid.resize(ecoGridID);
				AUIGrid.resize(myGridID8);
				AUIGrid.resize(myGridID9);
			});
			
			window.addEventListener("resize", function() {
				AUIGrid.resize(partGridID);
				AUIGrid.resize(ecoGridID);
				AUIGrid.resize(myGridID8);
				AUIGrid.resize(myGridID9);
			});
	
		</script>
	</form>	
</body>
</html>