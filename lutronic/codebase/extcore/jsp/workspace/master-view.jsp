<%@page import="com.e3ps.workspace.dto.ApprovalLineDTO"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ApprovalLineDTO dto = (ApprovalLineDTO) request.getAttribute("dto");
String jsp = dto.getPersist().getClass().getName();

int idx = jsp.indexOf(".");

jsp = jsp.substring(jsp.lastIndexOf(".") + 1);
String url = "/extcore/jsp/workspace/" + jsp + ".jsp";
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				결재 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">결재정보</a>
		</li>
		<li>
			<a href="#tabs-2">결재대상정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="10%">
				<col width="300">
				<col width="130">
				<col width="300">
				<col width="130">
				<col width="300">
			</colgroup>
			<tr>
				<th class="lb">결재 제목</th>
				<td class="indent5" colspan="5"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">수신일</th>
				<td class="indent5"><%=dto.getReceiveTime()%></td>
				<th class="lb">기안자</th>
				<td class="indent5"><%=dto.getSubmiter()%></td>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">결재의견</th>
				<td class="indent5" colspan="5">
					<textarea name="description" id="description" rows="6" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
		</table>

		<!-- 결재이력 -->
		<jsp:include page="/extcore/jsp/workspace/include/approval-history.jsp">
			<jsp:param value="<%=dto.getPoid()%>" name="oid" />
		</jsp:include>
	</div>
	
	<div id="tabs-2">
		<jsp:include page="<%=url%>">
			<jsp:param value="<%=dto.getPoid()%>" name="tapOid" />
		</jsp:include>
		
	</div>
</div>

<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		toFocus("description");
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated10000 = AUIGrid.isCreated(columns10000);
					if (isCreated10000) {
						AUIGrid.resize(myGridID10000);
					} else {
						createAUIGrid10000(columns10000);
					}
					break;
				case "tabs-2":
					break;
				}
			}
		});
		createAUIGrid10000(columns10000);
		AUIGrid.resize(myGridID10000);
		createAUIGrid10001(columns10001);
		AUIGrid.resize(myGridID10001);
		// 		createAUIGrid(columns);
		// 		AUIGrid.resize(myGridID);
		finderUser("reassignUser");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID10000);
		AUIGrid.resize(myGridID10001);
	});
</script>