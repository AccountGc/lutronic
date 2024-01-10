<%@page import="com.e3ps.mold.dto.MoldDTO"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> manufactureList = (ArrayList<NumberCode>) request.getAttribute("manufactureList");
ArrayList<NumberCode> moldtypeList = (ArrayList<NumberCode>) request.getAttribute("moldtypeList");
ArrayList<NumberCode> deptcodeList = (ArrayList<NumberCode>) request.getAttribute("deptcodeList");
MoldDTO dto = (MoldDTO) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="location" id="location" value="/Default/금형문서">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				금형 수정
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
		<col width="150">
		<col width="*">
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="req lb">문서명</th>
		<td class="indent5" colspan="3">
			<input type="text" name="name" id="name" class="width-500" value="<%=dto.getName()%>">
		</td>
	</tr>
	<tr>
		<th class="lb">문서설명</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="5"><%=dto.getDescription() == null ? "" : dto.getDescription()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">수정사유</th>
		<td class="indent5" colspan="3">
			<textarea name="iterationNote" id="iterationNote" rows="2"></textarea>
		</td>
	</tr>
	<tr>
		<th class="req lb">주 첨부파일</th>
		<td class="indent5" colspan="5">
			<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="modify" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="5">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="modify" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>

<!-- 속성 -->
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				속성
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
		<th class="lb">MANUFACTURE</th>
		<td class="indent5">
			<select name="manufacture" id="manufacture" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode manufacture : manufactureList) {
				%>
				<option value="<%=manufacture.getCode()%>" <%if (manufacture.getCode().equals(dto.getManufacture_code())) {%> selected="selected" <%}%>><%=manufacture.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th class="req">금형타입</th>
		<td class="indent5">
			<select name="moldtype" id="moldtype" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode moldtype : moldtypeList) {
				%>
				<option value="<%=moldtype.getCode()%>" <%if (moldtype.getCode().equals(dto.getMoldtype_code())) {%> selected="selected" <%}%>><%=moldtype.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th class="lb">업체자체금형번호</th>
		<td class="indent5">
			<input type="text" name="moldnumber" id="moldnumber" class="width-500" value="<%=dto.getMoldnumber()%>">
		</td>
		<th>금형개발비</th>
		<td class="indent5">
			<input type="text" name="moldcost" id="moldcost" class="width-500" value="<%=dto.getMoldcost()%>">
		</td>
	</tr>
	<tr>
		<th class="lb">내부 문서번호</th>
		<td class="indent5">
			<input type="text" name="interalnumber" id="interalnumber" class="width-500" value="<%=dto.getInteralnumber()%>">
		</td>
		<th>부서</th>
		<td class="indent5">
			<select name="deptcode" id="deptcode" class="width-200">
				<option value="">선택</option>
				<%
				for (NumberCode deptcode : deptcodeList) {
				%>
				<option value="<%=deptcode.getCode()%>" <%if (deptcode.getCode().equals(dto.getDeptcode_code())) {%> selected="selected" <%}%>><%=deptcode.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
</table>

<!-- 관련 품목 -->
<jsp:include page="/extcore/jsp/part/include/part-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	관련 문서 -->
<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
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
	document.addEventListener("DOMContentLoaded", function() {
		selectbox("manufacture");
		selectbox("moldtype");
		selectbox("deptcode");
		createAUIGrid91(columns91);
		AUIGrid.resize(myGridID91);
		createAUIGrid90(columns90);
		AUIGrid.resize(myGridID90);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID91);
		AUIGrid.resize(myGridID90);
	});

	function update(temp) {
		const temprary = JSON.parse(temp);
		// 결재선
		// 				const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
		const primary = document.querySelector("input[name=primary]");

		if (temprary) {
			if (!confirm("임시저장하시겠습니까??")) {
				return false;
			}

			// 					if (addRows8.length > 0) {
			// 						alert("결재선 지정을 해지해주세요.")
			// 						return false;
			// 					}

		} else {
			if (isEmpty($("#name").val())) {
				alert("문서명을 입력하세요.");
				return;
			}
			if (isEmpty($("#moldtype").val())) {
				alert("금형타입을 선택하세요.");
				return;
			}
			if (primary == null) {
				alert("주 첨부파일을 첨부해주세요.");
				return;
			}

			if (!confirm("수정 하시겠습니까?")) {
				return false;
			}
		}

		let params = new Object();
		params.oid = $("#oid").val();
		params.location = $("#location").val();
		params.name = $("#name").val();
		params.description = $("#description").val();
		params.iterationNote = $("#iterationNote").val();
		params.primary = primary == null ? '' : primary.value;
		const secondarys = toArray("secondarys");
		params.secondarys = secondarys;
		params.manufacture_code = $("#manufacture").val();
		params.moldtype_code = $("#moldtype").val();
		params.moldnumber = $("#moldnumber").val();
		params.moldcost = $("#moldcost").val();
		params.interalnumber = $("#interalnumber").val();
		params.deptcode_code = $("#deptcode").val();
		var lifecycle = "<%=dto.getApprovaltype_code().equals("DEFAULT") ? "LC_Default" : "LC_Default_NonWF"%>
	";
		params.lifecycle = lifecycle;
		params.partList = AUIGrid.getGridDataWithState(myGridID91, "gridState");
		params.docList = AUIGrid.getGridDataWithState(myGridID90, "gridState");
		params.temprary = temprary;

		var url = getCallUrl("/mold/update");
		// 				toRegister(params, addRows8); // 결재선 세팅
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
</script>