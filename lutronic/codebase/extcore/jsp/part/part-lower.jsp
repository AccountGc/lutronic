<%@page import="net.sf.json.JSONArray"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
WTPart part = (WTPart) request.getAttribute("part");
JSONArray lower = (JSONArray) request.getAttribute("lower");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				하위품목(
				<font color="red">
					<b><%=part.getNumber()%></b>
				</font>
				)
			</div>
		</td>
		<td class="right">
			<input type="button" title="닫기" value="닫기" class="gray" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 370px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		headerText : "품목번호",
		dataField : "number",
		dataType : "string",
		width : 140,
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/part/view?oid=" + oid);
				_popup(url, 1600, 800, "n");
			}
		},
	}, {
		headerText : "품목번호",
		dataField : "name",
		dataType : "string",
		style : "aui-left"
	}, {
		headerText : "상태",
		dataField : "state",
		dataType : "string",
		width : 80
	}, {
		headerText : "REV",
		dataField : "version",
		dataType : "string",
		width : 80
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			noDataMessage : "하위품목이 없습니다.",
			showAutoNoDataMessage : true,
			selectionMode : "multipleRows",
			hoverMode : "singleRow",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID,
<%=lower%>
	);
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>