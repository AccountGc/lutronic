<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						EO 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
					<input type="button" value="이전" title="이전" class="gray" onclick="history.back();">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">EO 제목</th>
				<td class="indent5" colspan="3">
					<input type="text" name="name" id="name" class="width-500">
				</td>
			</tr>
			<tr>
				<th class="req lb">프로젝트 코드 [명]</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="MODEL" name="codeType" />
						<jsp:param value="true" name="multi" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">완제품 품목</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/change/include/eo-complete-part-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="true" name="multi" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">제품 설계 개요</th>
				<td class="indent5" colspan="3">
					<div class="textarea-auto">
						<textarea name="eoCommentA" id="eoCommentA" rows="10"></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td class="indent5" colspan="3">
					<div class="textarea-auto">
						<textarea name="eoCommentB" id="eoCommentB" rows="10"></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td class="indent5" colspan="3">
					<div class="textarea-auto">
						<textarea name="eoCommentC" id="eoCommentC" rows="10"></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>

		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>

		<!-- 	설변 활동 -->
		<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
		</jsp:include>

		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
					<input type="button" value="이전" title="이전" class="gray" onclick="history.back();">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function create() {
				const name = document.getElementById("name");
				const eoCommentA = toId("eoCommentA");
				const eoCommentB = toId("eoCommentB");
				const eoCommentC = toId("eoCommentC");
				const secondarys = toArray("secondarys");
				// 완제품
				const rows104 = AUIGrid.getGridDataWithState(myGridID104, "gridState");
				// 관련문서
				const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
				// ECA
				const rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");
				// 제품
				const rows300 = AUIGrid.getGridDataWithState(myGridID300, "gridState");
				const params = {
					name : name.value,
					eoCommentA : eoCommentA,
					eoCommentB : eoCommentB,
					eoCommentC : eoCommentC,
					secondarys : secondarys,
					rows104 : rows104,
					rows90 : rows90,
					rows200 : rows200,
					rows300 : rows300,
				}

				if (name.value === "") {
					alert("EO 제목을 입력하세요.");
					name.focus();
					return false;
				}

				if (rows300.length === 0) {
					alert("제품명을 선택하세요.");
					popup300();
					return false;
				}

				if (rows104.length === 0) {
					alert("완제품을 선택하세요.");
					popup104();
					return false;
				}

				if (!confirm("등록하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/eo/create");
				logger(params);
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/eo/list");
					} else {
						parent.closeLayer();
					}
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("name");
				createAUIGrid104(columns104);
				createAUIGrid90(columns90);
				createAUIGrid300(columns300);
				createAUIGrid200(columns200);
				AUIGrid.resize(myGridID104);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID200);
				autoTextarea();
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID104);
				AUIGrid.resize(myGridID90);
				AUIGrid.resize(myGridID300);
				AUIGrid.resize(myGridID200);
			});
		</script>
	</form>
</body>
</html>