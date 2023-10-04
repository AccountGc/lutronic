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
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-400">
				</td>
				<th class="req lb">EO 구분</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="eoType" value="DEV" checked="checked">
						<div class="state p-success">
							<label>
								<b>개발</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="eoType" value="PRODUCT">
						<div class="state p-success">
							<label>
								<b>양산</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">제품명</th>
				<td colspan="3" class="indent5">
					<input type="button" value="추가" title="추가" class="blue" id="addNumberCode" name="addNumberCode">
					<input type="button" value="삭제" title="삭제" class="red" id="delNumberCode" name="delNumberCode">
				</td>
			</tr>
			<tr>
				<th class="req lb">완제품 품목</th>
				<td colspan="3" class="indent5 pt5">
					<jsp:include page="/extcore/jsp/change/include/include-complete-part.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">제품 설계 개요</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentA" id="eoCommentA" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentB" id="eoCommentB" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentC" id="eoCommentC" rows="6"></textarea>
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
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<!-- 					<input type="button" value="초기화" title="초기화"> -->
					<input type="button" value="이전" title="이전" onclick="javascript:history.back();">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function create() {

				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}

				const name = toId("name");
				const eoCommentA = toId("eoCommentA");
				const eoCommentB = toId("eoCommentB");
				const eoCommentC = toId("eoCommentC");
				const secondarys = toArray("secondarys");
				const eoType = document.querySelector("input[name=eoType]:checked").value;
				const rows104 = AUIGrid.getAddedRowItems(myGridID104);

				const url = getCallUrl("/eo/create");
				const params = {
					name : name,
					eoCommentA : eoCommentA,
					eoCommentB : eoCommentB,
					eoCommentC : eoCommentC,
					eoType : epType,
					secondarys : secondarys,
					rows104 : rows104
				}

				call(url, params, function(data) {
					if (data.result) {
						alert(data.msg);
						location.href = getCallUrl("/changeECO/listEO");
					} else {
						alert(data.msg);
					}
				});
			}

			// 			document.querySelector("#addNumberCode").addEventListener("click", () => {
			// 				const url = getCallUrl("/common/popup_numberCodes?codeType=MODEL&disable=true");
			// 				popup(url, 1500, 700);
			// 			});

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("name");
				createAUIGrid104(columns104);
				AUIGrid.resize(myGridID104);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID104);
			});
		</script>
	</form>
</body>
</html>