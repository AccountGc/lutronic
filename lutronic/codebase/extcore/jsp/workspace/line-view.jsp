<%@page import="com.e3ps.workspace.dto.ApprovalLineDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ApprovalLineDTO dto = (ApprovalLineDTO) request.getAttribute("dto");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="poid" id="poid" value="<%=dto.getPoid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				결재 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="승인" title="승인" onclick="_approval();">
			<input type="button" value="반려" title="반려" class="red" onclick="_reject();">
			<input type="button" value="검토완료" title="검토완료" onclick="_agree();">
			<input type="button" value="검토반려" title="검토반려" class="red" onclick="_unagree()">
			<input type="button" value="수신확인" title="수신확인" onclick="_receive();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">결재정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="130">
				<col width="400">
				<col width="130">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">결재 제목</th>
				<td class="indent5" colspan="3"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">담당자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">수신일</th>
				<td class="indent5"><%=dto.getReceiveTime()%></td>
			</tr>
			<tr>
				<th class="lb">구분</th>
				<td class="indent5"><%=dto.getType()%></td>
				<th class="lb">역할</th>
				<td class="indent5"><%=dto.getRole()%></td>
			</tr>
			<tr>
				<th class="lb">기안자</th>
				<td class="indent5"><%=dto.getSubmiter()%></td>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">위임</th>
				<td class="indent5" colspan="3">
					<input type="text" name="reassignUser" id="reassignUser">
					<input type="hidden" name="reassignUserOid" id="reassignUserOid">
					<input type="button" title="위임" value="위임" onclick="reassign();">
				</td>
			</tr>
			<tr>
				<th class="lb">결재의견</th>
				<td class="indent5" colspan="3">
					<textarea name="description" id="description" rows="6"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
		</table>

		<!-- 결재 이력 -->
		<jsp:include page="/extcore/jsp/workspace/include/approval-history.jsp">
			<jsp:param value="<%=dto.getPoid()%>" name="oid" />
			<jsp:param value="300" name="height" />
		</jsp:include>

	</div>
</div>

<script type="text/javascript">
	const oid = document.getElementById("oid").value;
	const poid = document.getElementById("poid").value;

	function reassign() {
		const reassignUser = document.getElementById("reassignUser");
		const reassignUserOid = document.getElementById("reassignUserOid").value;
		if (isNull(reassignUser.value)) {
			alert("해당 결재를 위임할 사용자를 선택하세요.");
			return false;
		}

		if (!confirm(reassignUser.value + " 사용자에게 결재를 위임하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/workspace/reassign");
		const params = new Object();
		params.reassignUserOid = reassignUserOid;
		params.oid = oid;
		openLayer();
		call(url, params, function(data) {
			alert(reassignUser.value + "사용자에게 " + data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _receive() {
		if (!confirm("수신확인 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_receive");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _unagree() {
		if (!confirm("검토 반려 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_unagree");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _reject() {
		if (!confirm("결재 반려 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_reject");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _agree() {
		if (!confirm("검토완료 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_agree");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _approval() {

		if (!confirm("승인 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_approval");
		const params = new Object();
		const description = document.getElementById("description").value;
		params.oid = oid;
		params.poid = poid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("description").focus();
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
				}
			}
		});
		createAUIGrid10000(columns10000);
		AUIGrid.resize(myGridID10000);
		// 		createAUIGrid(columns);
		// 		AUIGrid.resize(myGridID);
		finderUser("reassignUser");
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID10000);
	});
</script>