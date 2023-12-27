<%@page import="com.e3ps.workspace.service.WorkspaceHelper"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean create = "create".equals(mode);
boolean update = "update".equals(mode);
%>
<style type="text/css">
/** 결재 관련 셀 스타일 **/
.approval {
	background-color: rgb(189, 214, 255);
	font-weight: bold;
}

.submit {
	background-color: #FFFFA1;
	font-weight: bold;
}

.receive {
	background-color: #FFCBCB;
	font-weight: bold;
}

.agree {
	background-color: rgb(200, 255, 203);
	font-weight: bold;
}
</style>
<div class="include">
	<input type="button" value="결재선 추가" title="결재선 추가" class="blue" onclick="popup8();">
	<input type="button" value="결재선 삭제" title="결재선 삭제" class="red" onclick="deleteRow8();">
	<div id="grid8" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
	<script type="text/javascript">
		let myGridID8;
		const columns8 = [ {
			dataField : "sort",
			headerText : "순서",
			dataType : "numeric",
			width : 80
		}, {
			dataField : "type",
			headerText : "결재타입",
			dataType : "string",
			width : 80,
			styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
				if (value === "기안") {
					return "submit";
				} else if (value === "결재") {
					return "approval";
				} else if (value === "수신") {
					return "receive";
				} else if (value === "합의") {
					return "agree";
				}
				return null;
			}
		}, {
			dataField : "name",
			headerText : "이름",
			dataType : "string",
			style : "aui-left"
// 			width : 130
		}, {
			dataField : "id",
			headerText : "아이디",
			dataType : "string",
			width : 130,
		}, {
			dataField : "duty",
			headerText : "직급",
			dataType : "string",
			width : 100
		}, {
			dataField : "department_name",
			headerText : "부서",
			dataType : "string",
			width : 130
		}, {
			dataField : "email",
			headerText : "이메일",
			dataType : "string",
			style : 200,
			style : "aui-left"
		} ]

		function createAUIGrid8(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				showAutoNoDataMessage : false,
				showRowCheckColumn : true,
				showAutoNoDataMessage : false,
				enableRowCheckShiftKey : true,
				enableSorting : false,
				autoGridHeight : true,
			}
			myGridID8 = AUIGrid.create("#grid8", columnLayout, props);
			AUIGrid.setGridData(myGridID8, <%=WorkspaceHelper.manager.loadLines(oid)%>);
		}

		function popup8() {
			const list = AUIGrid.getGridData(myGridID8);
			const approvals = [];
			const agrees = [];
			const receives = [];

			for (let i = 0; i < list.length; i++) {
				const type = list[i].type;
				if ("합의" === type) {
					agrees.push(list[i]);
				} else if ("결재" === type) {
					approvals.push(list[i]);
				} else if ("수신" === type) {
					receives.push(list[i]);
				}
			}
			const url = getCallUrl("/workspace/popup");
			const p = _popup(url, 1400, 900, "n");
			p.approvals = approvals;
			p.agrees = agrees;
			p.receives = receives;
		}

		function deleteRow8() {
			const checkedItems = AUIGrid.getCheckedRowItems(myGridID8);
			if (checkedItems.length === 0) {
				alert("삭제할 행을 선택하세요.");
				return false;
			}

			for (let i = checkedItems.length - 1; i >= 0; i--) {
				var rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID8, rowIndex);
			}
		}

		function setLine(agree, approval, receive) {
			AUIGrid.clearGridData(myGridID8);

			for (let i = receive.length - 1; i >= 0; i--) {
				const item = receive[i];
				item.type = "수신";
				AUIGrid.addRow(myGridID8, item, "first");
			}

			let sort = approval.length;
			for (let i = approval.length - 1; i >= 0; i--) {
				const item = approval[i];
				item.type = "결재";
				item.sort = sort;
				AUIGrid.addRow(myGridID8, item, "first");
				sort--;
			}

			for (let i = agree.length - 1; i >= 0; i--) {
				const item = agree[i];
				item.type = "합의";
				AUIGrid.addRow(myGridID8, item, "first");
			}
		}
	</script>
</div>