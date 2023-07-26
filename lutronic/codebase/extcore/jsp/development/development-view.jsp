<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="com.e3ps.development.beans.MasterData"%>
<%-- <%@page import="e3ps.project.dto.ProjectDTO"%> --%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%@page import="net.sf.json.JSONArray"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
MasterData data = (MasterData) request.getAttribute("masterData");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				개발업무 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="개정" title="개정" class="blue" onclick="update('revise');">
			<input type="button" value="수정" title="수정" onclick="update('modify');">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
<!-- 	<ul> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-1">개발업무 상세보기</a> -->
<!-- 		</li> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-2">구성원</a> -->
<!-- 		</li> -->
<!-- 		<li> -->
<!-- 			<a href="#tabs-3">TASK</a> -->
<!-- 		</li> -->
<!-- 	</ul> -->
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<th class="lb">프로젝트 코드</th>
				<td class="indent5"><%=data.getName()%></td>
				<th>프로젝트 명</th>
				<td class="indent5"><%=data.getName()%></td>
			</tr>
			<tr>
				<th class="lb">예상 시작일</th>
				<td class="indent5"><%=data.getDevelopmentStart()%></td>
				<th>예상 종료일</th>
				<td class="indent5"><%=data.getDevelopmentEnd()%></td>
			</tr>
			<tr>
				<th class="lb">관리자</th>
				<td class="indent5"><%=data.getDm().getFullName()%></td>
				<th>등록자</th>
				<td class="indent5"><%=data.getCreator()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5" colspan="3"><%=data.getState()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly"><%=data.getDescription() != null ? data.getDescription() : ""%></textarea>
				</td>
			</tr>
		</table>
	</div>
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					구성원
				</div>
			</td>
		</tr>
	</table>
	<div id="tabs-2">
		<jsp:include page="/extcore/jsp/development/userList.jsp">
			<jsp:param value="<%=data.getOid()%>" name="oid" />
		</jsp:include>
	</div>
	<table class="button-table">
		<tr>
			<td class="left">
				<div class="header">
					<img src="/Windchill/extcore/images/header.png">
					TASK
				</div>
			</td>
		</tr>
	</table>
	<div id="tabs-3">
		<jsp:include page="/extcore/jsp/development/taskList.jsp">
			<jsp:param value="<%=data.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
// 	function update(mode) {
// 		const oid = document.getElementById("oid").value;
// 		const url = getCallUrl("/doc/update?oid=" + oid + "&mode=" + mode);
// 		openLayer();
// 		document.location.href = url;
// 	}

// 	function _delete() {

// 		if (!confirm("삭제 하시겠습니까?")) {
// 			return false;
// 		}

// 		const oid = document.getElementById("oid").value;
// 		const url = getCallUrl("/doc/delete?oid=" + oid);
// 		openLayer();
// 		call(url, null, function(data) {
// 			alert(data.msg);
// 			if (data.result) {
// // 				opener.loadGridData();
// 				self.close();
// 			} else {
// 				closeLayer();
// 			}
// 		}, "GET");
// 	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				case "tabs-3":
					const isCreated2 = AUIGrid.isCreated(myGridID2);
					if (isCreated2) {
						AUIGrid.resize(myGridID2);
					} else {
						createAUIGrid2(columns2);
					}
					break;
				}
			},
		});
		createAUIGrid(columns);
		createAUIGrid2(columns2);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID2);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID2);
	});
</script>