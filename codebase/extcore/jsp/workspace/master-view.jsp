<%@page import="com.e3ps.workspace.dto.ApprovalLineDTO"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ApprovalLineDTO dto = (ApprovalLineDTO) request.getAttribute("dto");
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
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
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
		<td class="indent5" colspan="3">
			<a href="javascript:dataView();"><%=dto.getName()%></a>
		</td>
	</tr>
	<tr>
		<th class="lb">수신일</th>
		<td class="indent5"><%=dto.getReceiveTime()%></td>
		<th>상태</th>
		<td class="indent5"><%=dto.getState()%></td>
	</tr>
	<tr>
		<th class="lb">결재의견</th>
		<td class="indent5" colspan="5">
			<div class="textarea-auto">
				<textarea name="description" id="description" rows="6" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
			</div>
		</td>
	</tr>
</table>

<!-- 결재이력 -->
<jsp:include page="/extcore/jsp/workspace/include/approval-history.jsp">
	<jsp:param value="<%=dto.getPoid()%>" name="oid" />
</jsp:include>

<!-- 외부 유저 메일 -->
<jsp:include page="/extcore/jsp/workspace/include/approval-mail.jsp">
	<jsp:param value="<%=dto.getPoid()%>" name="oid" />
</jsp:include>

<script type="text/javascript">
	function dataView() {
		const url = "<%=dto.getViewUrl()%>";
		_popup(url, 1600, 800, "n");
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("description");
		createAUIGrid10000(columns10000);
		AUIGrid.resize(myGridID10000);
		finderUser("reassignUser");
		autoTextarea();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID10000);
	});
</script>