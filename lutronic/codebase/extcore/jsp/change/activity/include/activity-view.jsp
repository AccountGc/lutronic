<%@page import="com.e3ps.change.eo.service.EoHelper"%>
<%@page import="com.e3ps.common.util.AUIGridUtil"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%
String oid = request.getParameter("oid");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				활동 현황
			</div>
		</td>
	</tr>
</table>
<div id="grid700" style="height: 30px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let myGridID700;
	const columns700 = [ {
		dataField : "",
		headerText : "현황",
		dataType : "string",
		width : 100
	}, {
		dataField : "step_name",
		headerText : "단계",
		dataType : "string",
	}, {
		dataField : "name",
		headerText : "활동명",
		dataType : "string",
	}, {
		dataField : "activity_name",
		headerText : "활동구분",
		dataType : "string",
	}, {
		dataField : "department_name",
		headerText : "담당부서",
		dataType : "string",
	}, {
		dataField : "activeUser_name",
		headerText : "담당자",
		dataType : "string",
	}, {
		dataField : "finishDate",
		headerText : "완료 요청일",
		dataType : "string",
	}, {
		dataField : "state",
		headerText : "상턔",
		dataType : "string",
		width : 100,
	}, {
		dataField : "completeDate",
		headerText : "완료일",
		dataType : "string",
	} ]

	function createAUIGrid700(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			selectionMode : "multipleCells",
			autoGridHeight : true
		}
		myGridID700 = AUIGrid.create("#grid700", columnLayout, props);
		AUIGrid.setGridData(myGridID700,
<%=EoHelper.manager.reference(oid, "activity")%>
	);
	}
</script>