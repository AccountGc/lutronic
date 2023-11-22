<%@page import="com.e3ps.workspace.service.WorkspaceHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray data = WorkspaceHelper.manager.history(oid);
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
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				결재 이력
			</div>
		</td>
	</tr>
</table>
<div id="grid10000" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID10000;
	const columns10000 = [ {
		dataField : "type",
		headerText : "타입",
		dataType : "string",
		width : 80,
		styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
			if (value == "기안") {
				return "submit";
			} else if (value == "결재") {
				return "approval";
			} else if (value == "수신") {
				return "receive";
			} else if (value == "합의") {
				return "agree";
			}
			return null;
		}

	}, {
		dataField : "role",
		headerText : "역할",
		dataType : "string",
		width : 80
	}, {
		dataField : "name",
		headerText : "제목",
		dataType : "string",
		style : "aui-left"
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 80
	}, {
		dataField : "owner",
		headerText : "담당자",
		dataType : "string",
		width : 100
	}, {
		dataField : "receiveDate_txt",
		headerText : "수신일",
		dataType : "string",
		width : 130
	}, {
		dataField : "completeDate_txt",
		headerText : "완료일",
		dataType : "string",
		width : 130
	}, {
		dataField : "description",
		headerText : "결재의견",
		dataType : "string",
		style : "aui-left",
		width : 450,
	}, ]
	function createAUIGrid10000(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			enableSorting : false,
			showAutoNoDataMessage : false,
			autoGridHeight : true
		}
		myGridID10000 = AUIGrid.create("#grid10000", columnLayout, props);
		AUIGrid.setGridData(myGridID10000,
<%=data%>
	);
	}
</script>