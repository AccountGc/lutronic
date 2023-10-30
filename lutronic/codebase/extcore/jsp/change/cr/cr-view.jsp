<%@page import="com.e3ps.common.code.service.NumberCodeHelper"%>
<%@page import="com.e3ps.change.cr.dto.CrDTO"%>
<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
CrDTO dto = (CrDTO) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CR 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if (dto.is_modify()) {
			%>
			<input type="button" value="수정" title="수정" class="blue" onclick="modify();">
			<%
			}
			%>
			<%
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
			<a href="#tabs-2">관련 객체</a>
		</li>
		<li>
			<a href="#tabs-3">이력 관리</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="450">
				<col width="130">
				<col width="450">
				<col width="130">
				<col width="450">
			</colgroup>
			<tr>
				<th class="lb">CR 번호</th>
				<td><%=dto.getNumber()%></td>
				<th>CR 제목</th>
				<td><%=dto.getName()%></td>
				<th>상태</th>
				<td><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td><%=dto.getCreator()%></td>
				<th>등록일</th>
				<td><%=dto.getCreatedDate_text()%></td>
				<th>수정일</th>
				<td><%=dto.getModifiedDate_text()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td><%=dto.getWriter_name()%></td>
				<th>작성부서</th>
				<td><%=dto.getCreateDepart_name()%></td>
				<th>작성일</th>
				<td><%=dto.getWriteDate()%></td>
			</tr>
			<tr>
				<th class="lb">제안자</th>
				<td><%=dto.getProposer_name()%></td>
				<th>변경부분</th>
				<td><%=dto.getChangeSection()%></td>
				<th>승인일</th>
				<td><%=dto.getApproveDate()%></td>
			</tr>
			<tr>
				<th class="lb">제품명</th>
				<td colspan="5"><%=dto.getModel()%></td>
			</tr>
			<tr>
				<th class="lb">변경사유</th>
				<td colspan="5">
					<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentA()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">변경사항</th>
				<td colspan="5">
					<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentB()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">참고사항</th>
				<td colspan="5">
					<textarea rows="10" readonly="readonly" id="description"><%=dto.getEoCommentC()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td colspan="5">
					<%
					Map<String, Object> contentMap = dto.getContentMap();
					if (contentMap != null) {
					%>
					<div>
						<a href="<%=contentMap.get("url")%>">
							<span style="position: relative; bottom: 2px;"><%=contentMap.get("name")%></span>
							<img src="<%=contentMap.get("fileIcon")%>" style="position: relative; top: 1px;">
						</a>
					</div>
					<%
					} else {
					%>
					<font color="red">
						<b>등록된 계변경 부품 내역파일이 없습니다.</b>
					</font>
					<%
					}
					%>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td colspan="5">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%= dto.getOid() %>" name="oid"/>
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<!-- 관련 객체 -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-reference-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 이력관리 -->
		<jsp:include page="/extcore/jsp/change/cr/include/cr-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/cr/update?oid=" + oid);
		document.location.href = url;
	}

	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/cr/delete?oid=" + oid);
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
	
	
// 	$("#approveBtn").click(function () {
// 		var oid = $("#oid").val();
// 		var url = getURLString("groupware", "historyWork", "do") + "?oid=" + oid;
// 		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
// 	})
	
// 	$("#downloadBtn").click(function () {
// 		var oid = $("#oid").val();
// 		var url = getURLString("common", "downloadHistory", "do") + "?oid=" + oid;
// 		openOtherName(url,"window","830","600","status=no,scrollbars=yes,resizable=yes");
// 	})
	
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-2":
					const isCreated300 = AUIGrid.isCreated(myGridID300); // ECO
					if (isCreated300) {
						AUIGrid.resize(myGridID300);
					} else {
						createAUIGrid300(columns300);
					}
					const isCreated101 = AUIGrid.isCreated(myGridID101); // CR
					if (isCreated101) {
						AUIGrid.resize(myGridID101);
					} else {
						createAUIGrid101(columns101);
					}
					break;
				case "tabs-3":
					const isCreated51 = AUIGrid.isCreated(myGridID51); // 다운로드이력
					if (isCreated51) {
						AUIGrid.resize(myGridID51);
					} else {
						createAUIGrid51(columns51);
					}
					const isCreated10000 = AUIGrid.isCreated(myGridID10000); // 결재이력
					if (isCreated10000) {
						AUIGrid.resize(myGridID10000);
					} else {
						createAUIGrid10000(columns10000);
					}
					const isCreated10001 = AUIGrid.isCreated(myGridID10001); // 외부 메일
					if (isCreated10001) {
						AUIGrid.resize(myGridID10001);
					} else {
						createAUIGrid10001(columns10001);
					}
					break;
				}
			}
		});
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID101);
		AUIGrid.resize(myGridID300);
	});
</script>
