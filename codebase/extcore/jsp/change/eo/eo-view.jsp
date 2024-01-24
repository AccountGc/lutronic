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
			if (isAdmin) {
			%>
			<select name="state" id="state" class="width-100" onchange="lcm(this);">
				<option value="">선택</option>
				<option value="LINE_REGISTER">결재선 지정</option>
				<option value="INWORK">작업 중</option>
				<option value="APPROVED">승인됨</option>
			</select>
			<%
			}
			%>
			<%
			if (dto.is_withdraw()) {
			%>
			<input type="button" value="회수(결재선 유지)" title="회수(결재선 유지)" class="gray" onclick="withdraw('false');">
			<input type="button" value="회수(결재선 삭제)" title="회수(결재선 삭제)" class="blue" onclick="withdraw('true');">
			<%
			}
			%>
			<%
			if (dto.is_validate()) {
			%>
			<input type="button" value="SAP검증" title="SAP검증" class="gray" onclick="validate();">
			<%
			}
			%>
			<%
			if (dto.is_modify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<%
			}
			if (dto.is_delete()) {
			%>
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
				<th class="lb">등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>등록일</th>
				<td class="indent5"><%=dto.getCreatedDate()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate()%></td>
			</tr>
			<tr>
				<th class="lb">프로젝트 코드 [명]</th>
				<td class="indent5" colspan="5"><%=dto.getModel_name()%></td>
			</tr>
			<tr>
				<th class="lb">제품 설계 개요</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentA()%></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">특기사항</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentB()%></textarea>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">기타사항</th>
				<td colspan="5" class="indent5">
					<div class="textarea-auto">
						<textarea rows="5" readonly="readonly" id="description" rows="5"><%=dto.getEoCommentC()%></textarea>
					</div>
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
				<th class="lb">완제품</th>
				<td colspan="5">
					<!-- 완제품 품목 -->
					<jsp:include page="/extcore/jsp/change/include/complete-part-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
						<jsp:param value="false" name="header" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<!-- 	관련 문서 -->
		<jsp:include page="/extcore/jsp/document/include/document-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="false" name="header" />
		</jsp:include>
		<!-- 설변활동 -->
		<jsp:include page="/extcore/jsp/change/activity/include/activity-view.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-2">
		<jsp:include page="/extcore/jsp/change/include/change-output.jsp">
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

	function sendEoSap() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/eo/sendEoSap?oid=" + oid);
		_popup(url, 1200, 600, "n");
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
		createAUIGrid90(columns90);
		createAUIGrid104(columns104);
		createAUIGrid700(columns700);
		AUIGrid.resize(myGridID90);
		AUIGrid.resize(myGridID104);
		AUIGrid.resize(myGridID700);
		autoTextarea();
		selectbox("state");
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