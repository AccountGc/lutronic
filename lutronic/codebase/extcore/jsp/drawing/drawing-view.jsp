<%@page import="com.e3ps.drawing.beans.EpmData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/auigrid.jsp"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
// WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
EpmData dto = (EpmData) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면 상세보기
			</div>
		</td>
		<td class="right">
			<%
			if(dto.isNameSyschronization){
			%>
				<input type="button" value="Name 동기화" title="Name 동기화">
			<%	
			}
			%>
			<%
			if(dto.isUpdate){
			%>
				<input type="button" value="수정" title="수정" class="blue" onclick="updateBtn();">
				<input type="button" value="삭제" title="삭제" class="red" onclick="deleteBtn();">
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
			<a href="#tabs-2">참조</a>
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
				<col width="500">
				<col width="130">
				<col width="500">
			</colgroup>
			<tr>
				<th class="lb">도면번호</th>
				<td class="indent5" colspan="3"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">도면명</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th class="lb">도면분류</th>
				<td class="indent5"><%=dto.getLocation()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getStateDisplay()%></td>
				<th class="lb">Rev.</th>
				<td class="indent5">
					<%=dto.getVersion()%>
					<%
					if (!dto.isLatest()) {
					%>
					&nbsp;
					<b>
						<a href="javascript:latest();">(최신버전으로)</a>
					</b>
					<%
					}
					%>
				</td>
			</tr>
			<tr>
				<th class="lb">등록자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
			</tr>
			<tr>
				<th class="lb">등록일</th>
				<td class="indent5"><%=dto.getCreateDate()%></td>
				<th class="lb">수정일</th>
				<td class="indent5"><%=dto.getModifyDate()%></td>
			</tr>
			<tr>
				<th class="lb">도면구분</th>
				<td class="indent5"><%=dto.getCadType()%></td>
				<th class="lb">도면파일</th>
				<td class="indent5"><%=dto.getCadName()%></td>
			</tr>
			<tr>
				<th class="lb">주 부품</th>
				<td class="indent5"><%=dto.getPNum()%></td>
				<th class="lb">ApplicationType</th>
				<td class="indent5"><%=dto.getApplicationType()%></td>
			</tr>
			<tr>
				<th class="lb">dxf</th>
				<td class="indent5" colspan="3"></td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<!-- 참조 -->
		<jsp:include page="/extcore/jsp/drawing/include_viewReference.jsp">
			<jsp:param value="<%=dto.getOid() %>" name="oid" />
			<jsp:param value="drawing"  name="moduleType"/>
		</jsp:include>
	</div>

	<div id="tabs-4">
		<!-- 이력관리 -->
		<jsp:include page="/extcore/jsp/drawing/drawing-record-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>

<script type="text/javascript">
	//수정
	function updateBtn(){
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/drawing/update?oid=" + oid);
		document.location.href = url;
	}
	
	//삭제
	function deleteBtn(){
	
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
	
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/drawing/delete?oid=" + oid);
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				if(parent.opener.$("#sessionId").val() == "undefined" || parent.opener.$("#sessionId").val() == null){
					parent.opener.location.reload();
				}else {
					parent.opener.$("#sessionId").val("");
					parent.opener.lfn_Search();
				}
				self.close();
			}
		}, "DELETE");
	}
			
	//최신버전
	function latest() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/drawing/latest?oid=" + oid);
		_popup(url, 1600, 550, "n");
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-2":
					const isCreated1 = AUIGrid.isCreated(refGridID);
					if (isCreated1) {
						AUIGrid.resize(refGridID);
					} else {
						createAUIGrid2(columnRef);
					}
					const isCreated2 = AUIGrid.isCreated(refbyGridID);
					if (isCreated2) {
						AUIGrid.resize(refbyGridID);
					} else {
						createAUIGrid3(columnRefby);
					}
					const isCreated3 = AUIGrid.isCreated(partGridID);
					if (isCreated3) {
						AUIGrid.resize(partGridID);
					} else {
						createAUIGrid1(columnPart);
					}
					break;
				case "tabs-4":
					const isCreated50 = AUIGrid.isCreated(myGridID50); // 버전이력
					if (isCreated50) {
						AUIGrid.resize(myGridID50);
					} else {
						createAUIGrid50(columns50);
					}
					const isCreated10000 = AUIGrid.isCreated(myGridID10000); // 결재이력
					if (isCreated10000) {
						AUIGrid.resize(myGridID10000);
					} else {
						createAUIGrid10000(columns10000);
					}
					const isCreated51 = AUIGrid.isCreated(myGridID51); // 다운로드이력
					if (isCreated51) {
						AUIGrid.resize(myGridID51);
					} else {
						createAUIGrid51(columns51);
					}
					break;
				}
			}
		});
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(refGridID);
		AUIGrid.resize(refbyGridID);
		AUIGrid.resize(partGridID);
		AUIGrid.resize(myGridID50);
		AUIGrid.resize(myGridID10000);
		AUIGrid.resize(myGridID51);
	});
</script>
