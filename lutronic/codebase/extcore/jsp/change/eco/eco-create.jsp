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
						ECO 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="이전" title="이전" onclick="javascript:history.back();">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="180">
				<col width="*">
				<col width="180">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">ECO 제목</th>
				<td class="indent5" colspan="3">
					<input type="text" name="name" id="name" class="width-400">
				</td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentA" id="eoCommentA" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentB" id="eoCommentB" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">인허가변경</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" id="licensing" value="NONE" checked="checked">
						<div class="state p-success">
							<label>
								<b>N/A</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="0">
						<div class="state p-success">
							<label>
								<b>불필요</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="1">
						<div class="state p-success">
							<label>
								<b>필요</b>
							</label>
						</div>
					</div>
				</td>
				<th>위험통제</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="">
						<div class="state p-success">
							<label>
								<b>N/A</b>
							</label>
						</div>
					</div>
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="0">
						<div class="state p-success">
							<label>
								<b>불필요</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="riskType" value="1">
						<div class="state p-success">
							<label>
								<b>필요</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentC" id="eoCommentC" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td class="indent5" colspan="3">
					<textarea name="eoCommentD" id="eoCommentD" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">
					설계변경 부품 내역파일
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" style="position: relative; top: 2px;">
				</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
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


		<!-- 설계변경 품목 -->
		<jsp:include page="/extcore/jsp/change/eco/include/eco-part-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
		</jsp:include>

		<!-- 	관련 CR -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>

		<!-- 	설변 활동 -->
		<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="250" name="height" />
		</jsp:include>
		<table class="button-table">
			<tr>
				<td class="center">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="이전" title="이전" onclick="javascript:history.back();">
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function create() {
				const name = document.getElementById("name");
				if (!confirm("등록 하시겠습니까?")) {
					return false;
				}

				const eoCommentA = toId("eoCommentA");
				const eoCommentB = toId("eoCommentB");
				const eoCommentC = toId("eoCommentC");
				const eoCommentD = toId("eoCommentD");
				const secondarys = toArray("secondarys");
				const primary = document.querySelector("input[name=primary]").value;
				const riskType = document.querySelector("input[name=riskType]:checked").value;
				const licensing = document.querySelector("input[name=licensing]:checked").value;
				const rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
				const rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");
				const rows500 = AUIGrid.getGridDataWithState(myGridID500, "gridState");
				const params = {
					name : name.value,
					riskType : riskType,
					licensing : licensing,
					secondarys : secondarys,
					primary : primary,
					eoCommentA : eoCommentA,
					eoCommentB : eoCommentB,
					eoCommentC : eoCommentC,
					eoCommentD : eoCommentD,
					rows101 : rows101, // 관련CR
					rows200 : rows200, // 설변활동
					rows500 : rows500, // 설변품목
				};
				logger(params);
				parent.openLayer();
				const url = getCallUrl("/eco/create");
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/eco/list");
					} else {
						parent.closeLayer();
					}
				});
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid101(columns101);
				createAUIGrid200(columns200);
				createAUIGrid500(columns500);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID500);
				AUIGrid.resize(myGridID200);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID500);
				AUIGrid.resize(myGridID200);
			});
		</script>
	</form>
</body>
</html>