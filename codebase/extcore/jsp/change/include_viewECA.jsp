<%@page import="com.e3ps.change.service.ECAHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray json =  ECAHelper.manager.include_ecaList(oid);
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png"> 활동 현황
			</div>
		</td>
		<td align="right" valign="top">
			<font color=green><img src="/Windchill/jsp/portal/images/tree/task_complete.gif" >&nbsp;완료</font>
			<font color=blue><img src="/Windchill/jsp/portal/images/tree/task_progress.gif">&nbsp;진행</font>
			<font color=red><img src="/Windchill/jsp/portal/images/tree/task_red.gif">&nbsp;지연</font>
			<font color=black><img src="/Windchill/jsp/portal/images/tree/task_ready.gif">&nbsp;예정</font>
		</td>
	</tr>
</table>
<div id="grid_eca" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
<script type="text/javascript">
	let ecaGridID;
	const columnEca = [ {
		dataField : "icon",
		headerText : "현황",
		dataType : "string",
		width : 180,
	}, {
		dataField : "stepName",
		headerText : "단계",
		dataType : "string",
		width : 180,
	}, {
		dataField : "name",
		headerText : "활동명",
		dataType : "string",
		width : 180,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/project/info?oid=" + oid);
				popup(url);
			}
		},
	}, {
		dataField : "activityName",
		headerText : "활동구분",
		dataType : "string",
		width : 180,
	}, {
		dataField : "departmentName",
		headerText : "담당부서",
		dataType : "string",
		width : 180,
	}, {
		dataField : "activeUserName",
		headerText : "담당자",
		dataType : "string",
		width : 180,
	}, {
		dataField : "finishDate",
		headerText : "요청 완료일",
		dataType : "string",
		width : 180,
	}, {
		dataField : "stateName",
		headerText : "상태",
		dataType : "string",
		width : 180,
	}, {
		dataField : "completeDate",
		headerText : "완료일",
		dataType : "string",
		width : 180,
	}, {
		dataField : "oid",
		visible : false
	} ]

	function createAUIGrid1(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			hoverMode : "singleRow",
			rowCheckToRadio : true
		}
		ecaGridID = AUIGrid.create("#grid_eca", columnLayout, props);
		AUIGrid.setGridData(ecaGridID, <%=json%>);
	}

</script>