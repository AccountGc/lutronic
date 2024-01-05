<%@page import="java.util.Map"%>
<%@page import="com.e3ps.doc.service.DocumentHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.e3ps.common.code.NumberCode"%>
<%@page import="com.e3ps.change.cr.dto.CrDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<NumberCode> preserationList = (ArrayList<NumberCode>) request.getAttribute("preserationList");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
CrDTO dto = (CrDTO) request.getAttribute("dto");
%>
<style type="text/css">
iframe {
	margin-top: 3px;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CR 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="600">
		<col width="150">
		<col width="600">
	</colgroup>
	<tr>
		<th class="req lb">CR 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300" value="<%=dto.getName()%>">
		</td>
		<th class="req">보존년한</th>
		<td class="indent5">
			<select name="period" id="period" class="width-200">
				<%
				String c = dto.getPeriod_code();
				for (NumberCode preseration : preserationList) {
					String key = preseration.getCode();
				%>
				<option value="<%=key%>" <%if(c.equals(key)) { %> selected="selected" <%} %>><%=preseration.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th class="req lb">제품명</th>
		<td colspan="3" class="indent5 pt5">
			<jsp:include page="/extcore/jsp/admin/code/include/code-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="update" name="mode" />
				<jsp:param value="insert300" name="method" />
				<jsp:param value="MODEL" name="codeType" />
				<jsp:param value="true" name="multi" />
				<jsp:param value="150" name="height" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb req">변경사유</th>
		<td colspan="3">
			&nbsp;
			<%
			String[] ss = new String[]{"영업/마케팅", "원가 절감", "기능/성능 변경", "공정 변경", "자재 변경", "허가/규제 변경", "품질 개선", "라벨링", "기타"};
			for (String s : ss) {
				int include = dto.getChangeSection().indexOf(s);
			%>
			<div class="pretty p-switch">
				<input type="checkbox" name="changeSection" value="<%=s%>" <%if (include > -1) {%> checked="checked" <%}%>>
				<div class="state p-success">
					<label>
						<b><%=s%></b>
					</label>
				</div>
			</div>
			&nbsp;
			<%
			}
			%>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td colspan="5" class="indent7 pb8">
			<textarea name="contents" id="contents" rows="15" style="display: none;"><%=dto.getContents() != null ? dto.getContents() : ""%></textarea>
			<script type="text/javascript">
				new Dext5editor('content');
				const content = document.getElementById("contents").value;
				DEXT5.setBodyValue(content, 'content');
			</script>
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

<!-- 	관련 ECO -->
<jsp:include page="/extcore/jsp/change/eco/include/eco-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<!-- 	관련 CR -->
<jsp:include page="/extcore/jsp/change/cr/include/cr-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="update" name="mode" />
	<jsp:param value="true" name="multi" />
	<jsp:param value="true" name="header" />
</jsp:include>

<table class="button-table">
	<tr>
		<td class="center">
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<script type="text/javascript">
	function modify() {
		const name = document.getElementById("name");
		const period = document.getElementById("period").value;
		const secondarys = toArray("secondarys");
		const oid = document.getElementById("oid").value;
		const primary = document.querySelector("input[name=primary]");
		// 모델
		const rows300 = AUIGrid.getGridDataWithState(myGridID300, "gridState");
		// 관련문서
		const rows90 = AUIGrid.getGridDataWithState(myGridID90, "gridState");
		// 관련CR
		const rows101 = AUIGrid.getGridDataWithState(myGridID101, "gridState");
		// 관련ECO
		const rows105 = AUIGrid.getGridDataWithState(myGridID105, "gridState");
		// 내용
		const content = DEXT5.getBodyValue("content");

		// 변경 구분 배열 처리
		const changeSection = document.querySelectorAll('input[name="changeSection"]:checked');
		const sections = [];
		changeSection.forEach(function(item) {
			sections.push(item.value);
		});

		if (isEmpty(name.value)) {
			alert("CR 제목을 입력해주세요.");
			name.focus();
			return;
		}

		if (isEmpty(period)) {
			alert("보존년한을 선택하세요.");
			return;
		}

		if (rows300.length == 0) {
			alert("제품을 선택해주세요.");
			popup300();
			return;
		}

		if (sections.length === 0) {
			alert("변경사유를 선택하세요.");
			return false;
		}

		if (!confirm("수정하시겠습니까?")) {
			return false;
		}

		const params = {
			oid : oid,
			name : name.value,
			period_code : period,
			contents : content,
			sections : sections, //변경 구분
			secondarys : secondarys,
			rows300 : rows300,
			rows90 : rows90,
			rows101 : rows101,
			rows105 : rows105,
		}

		const url = getCallUrl("/cr/modify");
		logger(params);
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

	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
		selectbox("period");
		createAUIGrid300(columns300);
		createAUIGrid90(columns90);
		createAUIGrid101(columns101);
		createAUIGrid105(columns105);
		AUIGrid.resize(myGridID300);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID105);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID300);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID105);
	});
</script>