<%@page import="com.e3ps.change.eco.dto.EcoDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EcoDTO dto = (EcoDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>



<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				ECO 상세보기
			</div>
		</td>
		<td class="right">
			<input type="button" value="결재회수" title="결재회수">
			<input type="button" value="수정" title="수정" class="blue" onclick="update('modify');">
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
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
			<a href="#tabs-2">설변 활동</a>
		</li>
		<li>
			<a href="#tabs-3">설변 품목</a>
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
				<col width="450">
				<col width="130">
				<col width="450">
			</colgroup>
			<tr>
				<th class="lb">ECO 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>ECO 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th>등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
			</tr>
			<tr>
				<th class="lb">구분</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th>등록일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">인허가 변경</th>
				<td class="indent5"><%=dto.getLicensing_name()%></td>
				<th>위험통제</th>
				<td class="indent5"><%=dto.getRiskType_name()%></td>
			</tr>
			<tr>
				<th class="lb">수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_text()%></td>
				<th>승인일</th>
				<td class="indent5"><%=dto.getApproveDate()%></td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					break;
				case "tabs-2":
					break;
				case "tabs-3":
					break;
				}
			}
		});
	});
</script>