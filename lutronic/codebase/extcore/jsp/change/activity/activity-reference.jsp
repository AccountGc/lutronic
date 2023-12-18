<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Map<String, ArrayList<Map<String, Object>>> nextData = (Map<String, ArrayList<Map<String, Object>>>) request
		.getAttribute("nextData");
Map<String, ArrayList<Map<String, Object>>> preData = (Map<String, ArrayList<Map<String, Object>>>) request
		.getAttribute("preData");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				참조항목
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>


<table>
	<colgroup>
		<col width="49%">
		<col width="10px;">
		<col width="49%">
	</colgroup>
	<tr>
		<td>
			<div id="pre_wrap" style="height: 60px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>11</td>
		<td>
			<div id="next_wrap" style="height: 60px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	const list = window.list;
	const link = window.link;
	let myGridiD;
	const columns = [ {
		headerText : "부품번호",
		dataField : "number",
		dataType : "string",
		width : 140
	}, {
		headerText : "부품명",
		dataField : "name",
		dataType : "string",
		style : "aui-left",
	}, {
		headerText : "REV",
		dataField : "version",
		dataType : "string",
		width : 140
	}, {
		headerText : "상태",
		dataField : "state",
		dataType : "string",
		width : 140
	} ];
	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			fillColumnSizeMode : false,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			enableSorting : false,
			softRemoveRowMode : false,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
			autoGridHeight : true,
		};
		preGridID = AUIGrid.create("#pre_wrap", columnLayout, props);
		nextGridID = AUIGrid.create("#next_wrap", columnLayout, props);

		AUIGrid.setGridData(preGridID,
<%=JSONArray.fromObject(preData.get("list3d"))%>
	);
		AUIGrid.setGridData(nextGridID,
<%=JSONArray.fromObject(nextData.get("list3d"))%>
	);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(preGridID);
		AUIGrid.resize(nextGridID);
	})
	window.addEventListener("resize", function() {
		AUIGrid.resize(preGridID);
		AUIGrid.resize(nextGridID);
	});
</script>
