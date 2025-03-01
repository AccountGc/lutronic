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
					<input type="button" value="등록" title="등록" class="red" onclick="create('false');">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<th class="req lb">ECO 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-700">
				</td>
				<th class="req">ECO 타입</th>
				<td>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="sendType" value="ECO" checked="checked">
						<div class="state p-success">
							<label>
								<b>ECO</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="sendType" value="SCO">
						<div class="state p-success">
							<label>
								<b>SCO</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="sendType" value="ORDER">
						<div class="state p-success">
							<label>
								<b>선구매</b>
							</label>
						</div>
					</div>
				</td>
			</tr>

			<tr>
				<th class="lb">변경사유</th>
				<td class="indent5" colspan="3">
					<div class="textarea-auto">
						<textarea name="eoCommentA" id="eoCommentA" rows="10"></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td class="indent5" colspan="3">
					<div class="textarea-auto">
						<textarea name="eoCommentB" id="eoCommentB" rows="10"></textarea>
					</div>
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
						<input type="radio" name="licensing" value="LI002">
						<div class="state p-success">
							<label>
								<b>불필요</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="licensing" value="LI001">
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
						<input type="radio" name="riskType" value="NONE" checked="checked">
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
					<div class="textarea-auto">
						<textarea name="eoCommentC" id="eoCommentC" rows="10"></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td class="indent5" colspan="3">
					<div class="textarea-auto">
						<textarea name="eoCommentD" id="eoCommentD" rows="10"></textarea>
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

		<!-- 	관련 CR -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
			<jsp:param value="" name="oid" />
			<jsp:param value="create" name="mode" />
			<jsp:param value="true" name="multi" />
			<jsp:param value="true" name="header" />
		</jsp:include>
		
		
		<!-- 	관련 ECPR -->
		<jsp:include page="/extcore/jsp/change/ecpr/include/ecpr-include.jsp">
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
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function create(temp) {
				const name = document.getElementById("name");
				const eoCommentA = toId("eoCommentA");
				const eoCommentB = toId("eoCommentB");
				const eoCommentC = toId("eoCommentC");
				const eoCommentD = toId("eoCommentD");
				const secondarys = toArray("secondarys");
				const riskType = document.querySelector("input[name=riskType]:checked").value;
				const licensing = document.querySelector("input[name=licensing]:checked").value;
				const rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
				const rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");
				const sendType = document.querySelector("input[name=sendType]:checked").value;
				const rows103 = AUIGrid.getGridDataWithState(myGridID103, "gridState");
				for (let i = 0; i < rows200.length; i++) {
					const dd = rows200[i];
					const activeUser_oid = AUIGrid.getCellValue(myGridID200, i, "activeUser_oid");
					const activity_type = AUIGrid.getCellValue(myGridID200, i, "activity_type");
					const finishDate = AUIGrid.getCellValue(myGridID200, i, "finishDate");
					const step = AUIGrid.getCellValue(myGridID200, i, "step");
					if (activity_type === undefined) {
						AUIGrid.showToastMessage(myGridID200, i, 0, "활동구분을 선택하세요.");
						return false;
					}
					if (activeUser_oid === undefined) {
						AUIGrid.showToastMessage(myGridID200, i, 1, "담당자를 선택하세요.");
						return false;
					}
					if (finishDate === undefined) {
						AUIGrid.showToastMessage(myGridID200, i, 2, "완료예정일을 선택하세요.");
						return false;
					}
				}

				if (name.value === "") {
					alert("ECO 제목을 입력해주세요.");
					name.focus();
					return;
				}

				if (sendType.value === "") {
					alert("ECO 타입을 선택하세요.");
					return false;
				}

				if (rows200.length === 0) {
					alert("설계변경 활동을 하나이상 추가하세요.");
					return false;
				}

				if (sendType === "ECO") {
					if (rows101.length === 0) {
						alert("ECO 일 경우 반드시 CR은 하나이상 존재해야합니다.");
						popup101();
						return false;
					}
				}

				if (!confirm("등록하시겠습니까?")) {
					return false;
				}

				const params = {
					name : name.value,
					riskType : riskType,
					licensing : licensing,
					secondarys : secondarys,
					sendType : sendType,
					eoCommentA : eoCommentA,
					eoCommentB : eoCommentB,
					eoCommentC : eoCommentC,
					eoCommentD : eoCommentD,
					rows101 : rows101, // 관련CR
					rows200 : rows200, // 설변활동
					rows103 : rows103
				};
				logger(params);
				const url = getCallUrl("/eco/create");
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						parent.updateHeader();
						document.location.href = getCallUrl("/eco/list");
					} else {
						parent.closeLayer();
					}
				});
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				toFocus("name");
				createAUIGrid101(columns101);
				createAUIGrid200(columns200);
				createAUIGrid103(columns103);
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID200);
				AUIGrid.resize(myGridID103);
				autoTextarea();
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID101);
				AUIGrid.resize(myGridID200);
				AUIGrid.resize(myGridID103);
			});
		</script>
	</form>
</body>
</html>