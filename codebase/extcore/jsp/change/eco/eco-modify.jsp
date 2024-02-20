<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.eco.dto.EcoDTO"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcoDTO dto = (EcoDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
Map<String, Object> contentMap = dto.getContentMap();
String aOid = "";
if (contentMap != null) {
	aOid = contentMap.get("aoid") == null ? "" : contentMap.get("aoid").toString();
}
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECO 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정완료" title="수정완료" class="blue" onclick="update('false');">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="500">
		<col width="130">
		<col width="500">
	</colgroup>
	<tr>
		<th class="req lb">ECO 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-700" value="<%=dto.getName()%>">
		</td>
		<th class="req">ECO 타입</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="sendType" value="ECO" <%if ("ECO".equals(dto.getSendType())) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>ECO</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="sendType" value="SCO" <%if ("SCO".equals(dto.getSendType())) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>SCO</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="sendType" value="ORDER" <%if ("ORDER".equals(dto.getSendType())) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>선구매</b>
					</label>
				</div>
			</div>
		</td>
	</tr>
	<%
		if(isAdmin) {
	%>
	<tr>
		<th class="lb">프로젝트 코드 [명]</th>
		<td colspan="3" class="indent5 pt5">
			<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="update" name="mode" />
				<jsp:param value="insert300" name="method" />
				<jsp:param value="MODEL" name="codeType" />
				<jsp:param value="true" name="multi" />
			</jsp:include>
		</td>
	</tr>	
	<%
		}
	%>
	<tr>
		<th class="lb">변경사유</th>
		<td class="indent5" colspan="3">
			<div class="textarea-auto">
				<textarea id="eoCommentA" name="eoCommentA" rows="10"><%=dto.getEoCommentA()%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">변경사항</th>
		<td class="indent5" colspan="3">
			<div class="textarea-auto">
				<textarea name="eoCommentB" id="eoCommentB" rows="10"><%=dto.getEoCommentB()%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">인허가변경</th>
		<td>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="licensing" id="licensing" value="NONE" <%if ("NONE".equals(dto.getLicensing())) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>N/A</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="licensing" value="LI002" <%if ("LI002".equals(dto.getLicensing())) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>불필요</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="licensing" value="LI001" <%if ("LI001".equals(dto.getLicensing())) {%> checked="checked" <%}%>>
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
				<input type="radio" name="riskType" value="NONE" <%if ("NONE".equals(dto.getRiskType())) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>N/A</b>
					</label>
				</div>
			</div>
			<div class="pretty p-switch">
				<input type="radio" name="riskType" value="0" <%if ("0".equals(dto.getRiskType())) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b>불필요</b>
					</label>
				</div>
			</div>
			&nbsp;
			<div class="pretty p-switch">
				<input type="radio" name="riskType" value="1" <%if ("1".equals(dto.getRiskType())) {%> checked="checked" <%}%>>
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
				<textarea name="eoCommentC" id="eoCommentC" rows="10"><%=dto.getEoCommentC()%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">기타사항</th>
		<td class="indent5" colspan="3">
			<div class="textarea-auto">
				<textarea name="eoCommentD" id="eoCommentD" rows="10"><%=dto.getEoCommentD()%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="modify" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<!-- 설계변경 품목 -->
<%-- <jsp:include page="/extcore/jsp/change/eco/include/eco-part-include.jsp"> --%>
<%--     <jsp:param value="<%=dto.getOid()%>" name="oid" /> --%>
<%--     <jsp:param value="update" name="mode" /> --%>
<%--     <jsp:param value="true" name="multi" /> --%>
<%-- </jsp:include> --%>
<!-- 관련 CR -->
<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="header" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="250" name="height" />
</jsp:include>
<!-- 설변 활동 -->
<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="250" name="height" />
</jsp:include>
<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정완료" title="수정완료" class="blue" onclick="update('false');">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<script type="text/javascript">
	function update(temp) {
		// 임시저장
		const temprary = JSON.parse(temp);
		const oid = document.getElementById("oid").value;
		const name = document.getElementById("name");
		const sendType = document.querySelector("input[name=sendType]:checked").value;

		if (temprary) {
			if (!confirm("임시저장하시겠습니까??")) {
				return false;
			}

		} else {
			if (isEmpty(name.value)) {
				alert("ECO 제목을 입력해주세요.");
				return;
			}

			if (sendType.value === "") {
				alert("ECO 타입을 선택하세요.");
				return false;
			}

			if (!confirm("수정 하시겠습니까?")) {
				return false;
			}
		}

		const eoCommentA = toId("eoCommentA");
		const eoCommentB = toId("eoCommentB");
		const eoCommentC = toId("eoCommentC");
		const eoCommentD = toId("eoCommentD");
		const secondarys = toArray("secondarys");
		const riskType = document.querySelector("input[name=riskType]:checked").value;
		const licensing = document.querySelector("input[name=licensing]:checked").value;
		const rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
		const rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");

		for (let i = 0; i < rows200.length; i++) {
			const dd = rows200[i];
			const activeUser_oid = AUIGrid.getCellValue(myGridID200, i, "activeUser_oid");
			const activity_type = AUIGrid.getCellValue(myGridID200, i, "activity_type");
			const finishDate = AUIGrid.getCellValue(myGridID200, i, "finishDate");
			const step = AUIGrid.getCellValue(myGridID200, i, "step");
			if (step === undefined) {
				// 				AUIGrid.showToastMessage(myGridID200, i, 1, "STEP을 선택하세요.");
				// 				return false;
			}
			if (activity_type === undefined) {
				AUIGrid.showToastMessage(myGridID200, i, 2, "활동구분을 선택하세요.");
				return false;
			}
			if (activeUser_oid === undefined) {
				AUIGrid.showToastMessage(myGridID200, i, 3, "담당자를 선택하세요.");
				return false;
			}
			if (finishDate === undefined) {
				AUIGrid.showToastMessage(myGridID200, i, 4, "완료예정일을 선택하세요.");
				return false;
			}
		}
		
		const rows300 = AUIGrid.getGridDataWithState(myGridID300, "gridState");

		const params = {
			name : name.value,
			riskType : riskType,
			licensing : licensing,
			secondarys : secondarys,
			eoCommentA : eoCommentA,
			eoCommentB : eoCommentB,
			eoCommentC : eoCommentC,
			eoCommentD : eoCommentD,
			rows101 : rows101, // 관련CR
			rows200 : rows200, // 설변활동
			rows300 : rows300,
			temprary : temprary,
			sendType : sendType,
			oid : oid
		};
		const url = getCallUrl("/eco/modify");
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			} else {
				closeLayer();
			}
		});
	}

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
		createAUIGrid101(columns101);
		createAUIGrid200(columns200);
		createAUIGrid300(columns300);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID200);
		AUIGrid.resize(myGridID300);
		autoTextarea();
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID300);
		// 		AUIGrid.resize(myGridID500);
		AUIGrid.resize(myGridID200);
	});
</script>