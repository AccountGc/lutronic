<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.e3ps.common.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.e3ps.part.service.PartHelper"%>
<%
String oid = request.getParameter("oid");
Map<String, Object> params = new HashMap<String, Object>();
params.put("oid", oid);
params.put("bomType", "down");
Map<String, Object> result = PartHelper.manager.bomPartList(params);
List<Map<String, Object>> list = (List<Map<String, Object>>)result.get("list");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				하위 품목
			</div>
		</td>
	</tr>
</table>
<table class="create-table">
	<tr>
		<td class="lb">
			<div id="grid81" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID81;
	const columns81 = [ {
		dataField : "number",
		headerText : "품목번호.",
		dataType : "string",
		width : 120,
	}, {
		dataField : "name",
		headerText : "품명",
		dataType : "string",
		width : 120,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 120,
	}, {
		dataField : "version",
		headerText : "Rev.",
		dataType : "string",
		width : 350,
	}];

	function createAUIGrid81(columnLayout) {
		const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				fillColumnSizeMode: true,
				showAutoNoDataMessage : false,
				selectionMode : "multipleCells",
				enableMovingColumn : true,
				enableFilter : true,
				showInlineFilter : false,
				useContextMenu : true,
				enableRightDownFocus : true,
				filterLayerWidth : 320,
				filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
		};
		
		let dataList = [];
		<% for(Map<String, Object> part : list) { %>
			var data = new Object();
			data.number = "<%= part.get("number") %>";
			data.name = "<%= part.get("name") %>";
			data.state = "<%= part.get("state")==null ? "":part.get("state") %>";
			data.version = "<%= part.get("version") %>";
			dataList.push(data);
		<% } %>
		
		myGridID81 = AUIGrid.create("#grid81", columnLayout, props);
		AUIGrid.setGridData(myGridID81, dataList);
		
// 			loadGridData();
// 			AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
// 			AUIGrid.bind(myGridID, "vScrollChange", function(event) {
// 				hideContextMenu();
// 				vScrollChangeHandler(event);
// 			});
// 			AUIGrid.bind(myGridID, "hScrollChange", function(event) {
// 				hideContextMenu();
// 			});
		
		
		
	}
</script>