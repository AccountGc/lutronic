<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.change.eo.dto.EoDTO"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EoDTO dto = (EoDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="eoType" id="eoType" value="<%=dto.getEoType()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				EO 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정완료" title="수정완료" class="blue" onclick="update();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
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
			<input type="text" name="name" id="name" class="width-500" value="<%=dto.getName()%>">
		</td>
	</tr>
	<tr>
		<th class="req lb">프로젝트 코드 [명]</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="update" name="mode" />
				<jsp:param value="insert300" name="method" />
				<jsp:param value="MODEL" name="codeType" />
				<jsp:param value="true" name="multi" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">완제품 품목</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/change/include/eo-complete-part-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="update" name="mode" />
				<jsp:param value="true" name="multi" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">제품 설계 개요</th>
		<td class="indent5" colspan="3">
			<div class="textarea-auto">
				<textarea name="eoCommentA" id="eoCommentA" rows="6"><%=dto.getEoCommentA()%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">특기사항</th>
		<td class="indent5" colspan="3">
			<div class="textarea-auto">
				<textarea name="eoCommentB" id="eoCommentB" rows="6"><%=dto.getEoCommentB()%></textarea>
			</div>
		</td>
	</tr>
	<tr>
		<th class="lb">기타사항</th>
		<td class="indent5" colspan="3">
			<div class="textarea-auto">
				<textarea name="eoCommentC" id="eoCommentC" rows="6"><%=dto.getEoCommentC()%></textarea>
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

<!-- 	관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	설변 활동 -->
<jsp:include page="/extcore/jsp/change/activity/include/activity-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
</jsp:include>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정완료" title="수정완료" class="blue" onclick="update();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<script type="text/javascript">
	function update() {
		const name = document.getElementById("name");
		const eoCommentA = toId("eoCommentA");
		const eoCommentB = toId("eoCommentB");
		const eoCommentC = toId("eoCommentC");
		const secondarys = toArray("secondarys");
		const eoType = toId("eoType");
		const oid = document.getElementById("oid").value;
		// 완제품
		const rows104 = AUIGrid.getGridDataWithState(myGridID104, "gridState");
		// 관련문서
		const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
		// ECA
		const rows200 = AUIGrid.getGridDataWithState(myGridID200, "gridState");
		// 제품
		const rows300 = AUIGrid.getGridDataWithState(myGridID300, "gridState");
		const url = getCallUrl("/eo/modify");
		const params = {
			name : name.value,
			eoCommentA : eoCommentA,
			eoCommentB : eoCommentB,
			eoCommentC : eoCommentC,
			eoType : eoType,
			secondarys : secondarys,
			rows104 : rows104,
			rows90 : rows90,
			rows200 : rows200,
			rows300 : rows300,
			oid : oid
		};

		logger(rows200);

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

		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}

		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
		createAUIGrid300(columns300);
		createAUIGrid104(columns104);
		createAUIGrid90(columns90);
		createAUIGrid200(columns200);
		AUIGrid.resize(myGridID300);
		AUIGrid.resize(myGridID104);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID200);
		autoTextarea();
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID300);
		AUIGrid.resize(myGridID104);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID200);
	});
</script>