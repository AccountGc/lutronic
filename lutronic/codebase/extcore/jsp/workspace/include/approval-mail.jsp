<%@page import="com.e3ps.workspace.service.WorkspaceHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray data = WorkspaceHelper.manager.getExternalMail(oid);
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
				외부 유저 메일
			</div>
		</td>
	</tr>
</table>
<div id="grid10001" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID10001;
	const columns10001 = [ {
		dataField : "name",
		headerText : "이름",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true,
			inline : true
		},
	}, {
		dataField : "email",
		headerText : "이메일",
		dataType : "string",
		style : "aui-left",
		filter : {
			showIcon : true,
			inline : true
		},
	} ]
	function createAUIGrid10001(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			enableSorting : false,
			showAutoNoDataMessage : false,
			autoGridHeight : true
		}
		myGridID10001 = AUIGrid.create("#grid10001", columnLayout, props);
		AUIGrid.setGridData(myGridID10001, <%=data%>);
	}
</script>