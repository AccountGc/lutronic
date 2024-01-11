<%@page import="com.e3ps.workspace.dto.ApprovalLineDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ApprovalLineDTO dto = (ApprovalLineDTO) request.getAttribute("dto");
String jsp = dto.getPersist().getClass().getName();

int idx = jsp.indexOf(".");

jsp = jsp.substring(jsp.lastIndexOf(".") + 1);
String url = "/extcore/jsp/workspace/" + jsp + ".jsp";
String tapOid = dto.getPersist().getPersistInfo().getObjectIdentifier().getStringValue();
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
			<%
			if (dto.isApprovalLine()) {
			%>
			<input type="button" value="승인" title="승인" onclick="_approval();">
			<input type="button" value="반려" title="반려" class="red" onclick="_reject();">
			<%
			}
			%>
			<%
			if (dto.isAgreeLine()) {
			%>
			<input type="button" value="합의완료" title="합의완료" onclick="_agree();">
			<input type="button" value="합의반려" title="합의반려" class="red" onclick="_unagree()">
			<%
			}
			%>
			<%
			if (dto.isReceiveLine()) {
			%>
			<input type="button" value="수신확인" title="수신확인" onclick="_receive();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="view-table">
	<colgroup>
		<col width="130">
		<col width="500">
		<col width="130">
		<col width="500">
	</colgroup>
	<tr>
		<th class="lb">결재 제목</th>
		<td class="indent5" colspan="3">
			<a href="javascript:dataView();"><%=dto.getName()%></a>
		</td>
	</tr>
	<tr>
		<th class="lb">담당자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th>수신일</th>
		<td class="indent5"><%=dto.getReceiveTime()%></td>
	</tr>
	<tr>
		<th class="lb">구분</th>
		<td class="indent5"><%=dto.getType()%></td>
		<th>역할</th>
		<td class="indent5"><%=dto.getRole()%></td>
	</tr>
	<tr>
		<th class="lb">기안자</th>
		<td class="indent5"><%=dto.getSubmiter()%></td>
		<th>상태</th>
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
			<div class="textarea-auto">
				<textarea name="description" id="description" rows="6"></textarea>
			</div>
		</td>
	</tr>
</table>

<!-- 결재 이력 -->
<jsp:include page="/extcore/jsp/workspace/include/approval-history.jsp">
	<jsp:param value="<%=dto.getPoid()%>" name="oid" />
</jsp:include>

<!-- 외부 유저 메일 -->
<jsp:include page="/extcore/jsp/workspace/include/approval-mail.jsp">
	<jsp:param value="<%=dto.getPoid()%>" name="oid" />
</jsp:include>

<script type="text/javascript">
	function reassign() {
		const oid = document.getElementById("oid").value;
		const reassignUser = document.getElementById("reassignUser");
		const reassignUserOid = document.getElementById("reassignUserOid").value;
		if (isNull(reassignUser.value)) {
			alert("해당 결재를 위임할 사용자를 선택하세요.");
			return false;
		}

		if (!confirm(reassignUser.value + " 사용자에게 결재를 위임하시겠습니까?")) {
			return false;
		}

		const url = getCallUrl("/workspace/delegate");
		const params = {
			tapOid : "<%=tapOid%>",
			reassignUserOid : reassignUserOid,
			oid : oid
		}
		openLayer();
		call(url, params, function(data) {
			alert(reassignUser.value + "사용자에게 " + data.msg);
			if (data.result) {
				opener.loadGridData();
				opener.parent.updateWorkspace();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _receive() {
		const oid = document.getElementById("oid").value;
		if (!confirm("수신확인 하시겠습니까?")) {
			return false;
		}
		const description = document.getElementById("description").value;
		const url = getCallUrl("/workspace/_receive");
		const params = {
			oid : oid,
			description : description
		}
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				opener.parent.updateWorkspace();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _unagree() {
		const oid = document.getElementById("oid").value;
		const description = document.getElementById("description").value;
		if (!confirm("합의 반려 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_unagree");
		const params = new Object();
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				opener.parent.updateWorkspace();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _reject() {
		const oid = document.getElementById("oid").value;
		const description = document.getElementById("description").value;
		if (!confirm("반려 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_reject");
		const params = {
			oid : oid,
			description : description
		}
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				opener.parent.updateWorkspace();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _agree() {
		const oid = document.getElementById("oid").value;
		const description = document.getElementById("description").value;
		if (!confirm("합의완료 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_agree");
		const params = new Object();
		params.oid = oid;
		params.description = description;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				opener.parent.updateWorkspace();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function _approval() {
		const oid = document.getElementById("oid").value;
		const description = document.getElementById("description").value;
		if (!confirm("승인 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/workspace/_approval");
		const params = {
			oid : oid,
			description : description,
			tapOid : "<%=tapOid%>"
		}
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				opener.parent.updateWorkspace();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	function read() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/workspace/read?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			if (data.result) {
				opener.loadGridData();
			} else {
				alert(data.msg);
			}
			closeLayer();
		}, "GET");
	}
	
	function dataView() {
		const url = "<%=dto.getViewUrl()%>";
		_popup(url, 1600, 800, "n");
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("description");
		createAUIGrid10000(columns10000);
		AUIGrid.resize(myGridID10000);
		createAUIGrid9(columns10001);
		AUIGrid.resize(myGridID10001);
		finderUser("reassignUser");
		read();
		autoTextarea();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID10000);
		AUIGrid.resize(myGridID10001);
	});
</script>