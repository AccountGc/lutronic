<%@page import="com.e3ps.change.beans.ECNData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
ECNData data = (ECNData) request.getAttribute("data");
%>
<input type="hidden" name="oid" id="oid" value="<%=data.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECN 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="산출물" title="산출물" class="" id="viewECA">
			<input type="button" value="결재이력" title="결재이력" class="" id="approveBtn">
			<input type="button" value="다운로드이력" title="다운로드이력" class="" id="downloadBtn">
			<input type="button" value="닫기" title="닫기" class="gray" id="closeBtn" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본 정보</a>
		</li>
		<li>
			<a href="#tabs-2">활동 현황</a>
		</li>
		<li>
			<a href="#tabs-3">관련 ECO</a>
		</li>
	</ul>
	<div id="tabs-1">
		<!-- 기본 정보 -->
		<table class="view-table">
			<colgroup>
				<col width="13%">
				<col width="37%">
				<col width="13%">
				<col width="37%">
			</colgroup>
			<tr>
				<th>ECN 제목</th>
				<td colspan="3"><%=data.getName()%></td>
			</tr>
			<tr>
				<th>ECN 번호</th>
				<td><%=data.getNumber()%></td>
				<th>상태</th>
				<td><%=data.getStateDisplay()%></td>
			</tr>
			<tr>
				<th>등록자</th>
				<td colspan="3"><%=data.getCreator()%></td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<!-- 활동 현황 -->
		<jsp:include page="/extcore/jsp/change/include_viewECA.jsp" flush="false">
			<jsp:param value="<%=data.getOid() %>" name="oid" />
		</jsp:include>
	</div>
	<div id="tabs-3">
		<!-- 관련 ECO -->
<%-- 		<jsp:include page="/extcore/jsp/change/include_view_ecr_eco.jsp" flush="false"> --%>
<%-- 			<jsp:param value="ecn" name="moduleType"/> --%>
<%-- 			<jsp:param value="<%=data.getOid() %>" name="oid" /> --%>
<%-- 		</jsp:include> --%>
	</div>
</div>

<script type="text/javascript">
	//산출물
	$("#viewECA").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/changeECA/viewECA?oid=" + oid);
		popup(url, 1000, 600);
	})
	
	//다운로드 이력
	$("#downloadBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/common/downloadHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	//결재 이력
	$("#approveBtn").click(function () {
		const oid = document.querySelector("#oid").value;
		const url = getCallUrl("/groupware/workHistory?oid=" + oid);
		popup(url, 830, 600);
	})
	
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-2":
					const isCreated1 = AUIGrid.isCreated(ecaGridID);
					if (isCreated1) {
						AUIGrid.resize(ecaGridID);
					} else {
						createAUIGrid1(columnEca);
					}
					break;
				case "tabs-3":
					const isCreated2 = AUIGrid.isCreated(complePartGridID);
					if (isCreated2) {
						AUIGrid.resize(complePartGridID);
					} else {
						createAUIGrid2(columnComplePart);
					}
					break;
				}
			}
		});
		createAUIGrid1(columnEca);
		AUIGrid.resize(ecaGridID);
		createAUIGrid7(columnEco);
		AUIGrid.resize(ecoGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(ecaGridID);
		AUIGrid.resize(ecoGridID);
	});
</script>
