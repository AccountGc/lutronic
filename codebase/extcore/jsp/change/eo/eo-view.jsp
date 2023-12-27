<%@page import="com.e3ps.change.eo.dto.EoDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EoDTO dto = (EoDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				EO 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if (isAdmin || dto.isModify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-2">산출물</a>
		</li>
		<li>
			<a href="#tabs-3">관련 객체</a>
		</li>
		<li>
			<a href="#tabs-4">이력 관리</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="350">
				<col width="130">
				<col width="350">
				<col width="130">
				<col width="350">
			</colgroup>
			<tr>
				<th class="lb">EO 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
				<th>EO 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">제품명</th>
				<td class="indent5"><%=dto.getModel_name()%></td>
				<th>구분</th>
				<td class="indent5" colspan="3"><%=dto.getEoType_name()%></td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>등록일</th>
				<td class="indent5"><%=dto.getCreatedDate()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate()%></td>
			</tr>
			<tr>
				<th class="lb">제품 설계 개요</th>
				<td colspan="5" class="indent5">
					<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentA()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td colspan="5" class="indent5">
					<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentB()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td colspan="5" class="indent5">
					<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentC()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">황제품</th>
				<td colspan="5">
					<!-- 완제품 품목 -->
					<jsp:include page="/extcore/jsp/change/include/complete-part-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
						<jsp:param value="300" name="height" />
						<jsp:param value="false" name="header" />
					</jsp:include>
				</td>
			</tr>
		</table>


		<jsp:include page="/extcore/jsp/change/activity/include/activity-view.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-2">
		<jsp:include page="/extcore/jsp/change/include/change-output.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 관련 객체 -->
		<jsp:include page="/extcore/jsp/change/eo/include/eo-reference-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-4">
		<!-- 이력관리 -->
		<jsp:include page="/extcore/jsp/change/eo/include/eo-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>

<script type="text/javascript">
	const oid = document.getElementById("oid").value;

	function modify() {
		document.location.href = getCallUrl("/eo/modify?oid=" + oid);
	}

	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/eo/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				clsoeLayer();
			}
		}, "DELETE");
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated104 = AUIGrid.isCreated(myGridID104); // 완제품
					if (isCreated104) {
						AUIGrid.resize(myGridID104);
					} else {
						createAUIGrid104(columns104);
					}
					const isCreated700 = AUIGrid.isCreated(myGridID700);
					if (isCreated700) {
						AUIGrid.resize(myGridID700);
					} else {
						createAUIGrid700(columns700);
					}
					break;
				case "tabs-2":
					break;
				case "tabs-3":
					const isCreated90 = AUIGrid.isCreated(myGridID90); // 문서
					if (isCreated90) {
						AUIGrid.resize(myGridID90);
					} else {
						createAUIGrid90(columns90);
					}
					const isCreated300 = AUIGrid.isCreated(myGridID300); // MODEL
					if (isCreated300) {
						AUIGrid.resize(myGridID300);
					} else {
						createAUIGrid300(columns300);
					}
					break;
				case "tabs-4":
					const isCreated51 = AUIGrid.isCreated(myGridID51); // 다운로드이력
					if (isCreated51) {
						AUIGrid.resize(myGridID51);
					} else {
						createAUIGrid51(columns51);
					}
					const isCreated10000 = AUIGrid.isCreated(myGridID10000); // 다운로드이력
					if (isCreated10000) {
						AUIGrid.resize(myGridID10000);
					} else {
						createAUIGrid10000(columns10000);
					}
					const isCreated10001 = AUIGrid.isCreated(myGridID10001); // 외부 유저 메일
					if (isCreated10001) {
						AUIGrid.resize(myGridID10001);
					} else {
						createAUIGrid10001(columns10001);
					}
					break;
				}
			}
		});
		createAUIGrid104(columns104);
		AUIGrid.resize(myGridID104);
		createAUIGrid700(columns700);
		AUIGrid.resize(myGridID700);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID104);
		AUIGrid.resize(myGridID700);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID300);
		AUIGrid.resize(myGridID51);
		AUIGrid.resize(myGridID10000);
		AUIGrid.resize(myGridID10001);
	});
</script>